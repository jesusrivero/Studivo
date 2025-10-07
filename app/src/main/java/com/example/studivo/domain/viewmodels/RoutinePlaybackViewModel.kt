package com.example.studivo.domain.viewmodels

import android.content.Context
import android.os.SystemClock
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studivo.domain.model.PlaybackEvent
import com.example.studivo.domain.model.PlaybackUiState
import com.example.studivo.domain.model.RoutinePlaybackState
import com.example.studivo.domain.model.calculateCurrentBPM
import com.example.studivo.domain.services.Metronome
import com.example.studivo.domain.usecase.StartRoutinePlaybackUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RoutinePlaybackViewModel @Inject constructor(
	private val startRoutinePlayback: StartRoutinePlaybackUseCase,
	savedStateHandle: SavedStateHandle,
	@ApplicationContext private val context: Context
) : ViewModel() {
	
	private val routineId: String = savedStateHandle.get<String>("routineId")
		?: throw IllegalArgumentException("routineId is required")
	
	private val _uiState = MutableStateFlow<PlaybackUiState>(PlaybackUiState.Loading)
	val uiState: StateFlow<PlaybackUiState> = _uiState.asStateFlow()
	
	private var timerJob: Job? = null
	private var countdownJob: Job? = null
	private var metronome: Metronome? = null
	
	init {
		metronome = Metronome(context)
		loadRoutine()
	}
	
	private fun loadRoutine() {
		viewModelScope.launch {
			_uiState.value = PlaybackUiState.Loading
			
			startRoutinePlayback(routineId)
				.onSuccess { state ->
					// 🔹 Empieza con cuenta regresiva
					val countdownState = state.copy(
						isCountingDown = true,
						countdown = 5,
						isPaused = true
					)
					_uiState.value = PlaybackUiState.Playing(countdownState)
					startCountdown()
				}
				.onFailure { error ->
					_uiState.value = PlaybackUiState.Error(
						error.message ?: "Error al cargar la rutina"
					)
				}
		}
	}
	
	// 🔹 Cuenta regresiva antes de iniciar (3, 2, 1)
	private fun startCountdown() {
		countdownJob?.cancel()
		
		// Inicializa la cuenta regresiva en 3
		val initialCountdown = 4
		(_uiState.value as? PlaybackUiState.Playing)?.state?.let { currentState ->
			val newState = currentState.copy(
				countdown = initialCountdown,
				isCountingDown = true,
				isPaused = true // pausamos la rutina mientras contamos
			)
			_uiState.value = PlaybackUiState.Playing(newState)
		}
		
		countdownJob = viewModelScope.launch {
			while (isActive) {
				val currentState = (_uiState.value as? PlaybackUiState.Playing)?.state ?: break
				val remaining = currentState.countdown - 1
				
				if (remaining > 0) {
					val newState = currentState.copy(countdown = remaining)
					_uiState.value = PlaybackUiState.Playing(newState)
					delay(1000)
				} else {
					// ⏰ Termina la cuenta regresiva → inicia rutina real y metrónomo
					val newState = currentState.copy(
						countdown = 0,
						isCountingDown = false,
						isPaused = false
					)
					_uiState.value = PlaybackUiState.Playing(newState)
					startTimer()         // ✅ inicia el temporizador
					startMetronome(newState) // ✅ inicia el metrónomo
					break
				}
			}
		}
	}

	
	
	fun onEvent(event: PlaybackEvent) {
		val currentState = (_uiState.value as? PlaybackUiState.Playing)?.state ?: return
		
		when (event) {
			PlaybackEvent.PlayPause -> handlePlayPause(currentState)
			PlaybackEvent.NextPhase -> handleNextPhase(currentState)
			PlaybackEvent.NextRepetition -> handleNextRepetition(currentState)
			PlaybackEvent.RepeatPhase -> handleRepeatPhase(currentState)
			PlaybackEvent.CompleteRoutine -> handleCompleteRoutine(currentState)
		}
	}
	
	private fun handlePlayPause(state: RoutinePlaybackState) {
		val newState = state.copy(isPaused = !state.isPaused)
		_uiState.value = PlaybackUiState.Playing(newState)
		
		if (newState.isPaused) {
			stopTimer()
			metronome?.stop()
		} else {
			startTimer()
			startMetronome(newState)
		}
	}
	
	
	private fun handleNextPhase(state: RoutinePlaybackState) {
		if (!state.hasNextPhase) return
		val nextPhaseIndex = state.currentPhaseIndex + 1
		val nextPhase = state.phases[nextPhaseIndex]
		
		val newState = state.copy(
			currentPhaseIndex = nextPhaseIndex,
			currentRepetition = 1,
			timeRemaining = nextPhase.duration * 60
		)
		
		_uiState.value = PlaybackUiState.Playing(newState)
		
		startTimer()
		startMetronome(newState) // ✅ ahora respeta fases sin BPM
	}
	
	
	private fun handleNextRepetition(state: RoutinePlaybackState) {
		if (!state.hasNextRepetition) return
		val currentPhase = state.currentPhase ?: return
		val nextRepetition = state.currentRepetition + 1
		
		val canProceed = when (currentPhase.mode) {
			"BY_REPS" -> nextRepetition <= currentPhase.repetitions
			"UNTIL_BPM_MAX" -> {  // ✅ CAMBIO: "BY_BPM_MAX" → "UNTIL_BPM_MAX"
				val nextBPM = currentPhase.calculateCurrentBPM(nextRepetition)
				nextBPM <= currentPhase.bpmMax && currentPhase.bpmMax > 0
			}
			else -> false
		}
		
		if (!canProceed) return
		
		val newState = state.copy(
			currentRepetition = nextRepetition,
			timeRemaining = currentPhase.duration * 60
		)
		
		_uiState.value = PlaybackUiState.Playing(newState)
		
		startTimer()
		startMetronome(newState)
	}
	
//	private fun handleNextRepetition(state: RoutinePlaybackState) {
//		if (!state.hasNextRepetition) return
//		val currentPhase = state.currentPhase ?: return
//		val nextRepetition = state.currentRepetition + 1
//
//		val canProceed = when (currentPhase.mode) {
//			"BY_REPS" -> nextRepetition <= currentPhase.repetitions
//			"BY_BPM_MAX" -> {
//				val nextBPM = currentPhase.calculateCurrentBPM(nextRepetition)
//				nextBPM <= currentPhase.bpmMax && currentPhase.bpmMax > 0
//			}
//			else -> false
//		}
//
//		if (!canProceed) return
//
//		val newState = state.copy(
//			currentRepetition = nextRepetition,
//			timeRemaining = currentPhase.duration * 60
//		)
//
//		_uiState.value = PlaybackUiState.Playing(newState)
//
//		startTimer()
//		startMetronome(newState) // ✅ ahora respeta fases sin BPM
//	}


	
	private fun handleRepeatPhase(state: RoutinePlaybackState) {
		val currentPhase = state.currentPhase ?: return
		val newState = state.copy(timeRemaining = currentPhase.duration * 60)
		_uiState.value = PlaybackUiState.Playing(newState)
		
		startTimer()
		startMetronome(newState) // ✅ ahora respeta fases sin BPM
	}
	

	
	private fun handlePhaseComplete(state: RoutinePlaybackState) {
		if (state.hasNextRepetition) {
			handleNextRepetition(state)
		} else if (state.hasNextPhase) {
			handleNextPhase(state)
		} else {
			handleCompleteRoutine(state)
		}
	}
	
	private fun handleCompleteRoutine(state: RoutinePlaybackState) {
		stopTimer()
		metronome?.stop()
		
		val newState = state.copy(
			isCompleted = true,
			isPaused = true,
			timeRemaining = 0
		)
		_uiState.value = PlaybackUiState.Playing(newState)
	}
	
	
	// 🎵 Metronome controls robustos
	private fun startMetronome(state: RoutinePlaybackState) {
		val phase = state.currentPhase ?: return
		
		if (state.isPaused || phase.bpm <= 0) {
			// 🔹 Si está pausado o fase sin BPM, detenemos el metrónomo
			metronome?.stop()
			return
		}
		
		// 🔹 Inicia metrónomo con BPM, compás y subdivisión
		metronome?.start(
			bpm = state.currentBPM,
			timeSignature = phase.timeSignature,
			subdivision = phase.subdivision,
			coroutineScope = viewModelScope
		)
	}

	
	private fun updateMetronome(state: RoutinePlaybackState) {
		val phase = state.currentPhase ?: return
		
		if (state.isPaused || phase.bpm <= 0) {
			// 🔹 Detener metrónomo si no hay BPM o la rutina está pausada
			metronome?.stop()
			return
		}
		
		// 🔹 Actualiza solo si la fase tiene BPM y no está pausada
		metronome?.updateTempo(
			bpm = state.currentBPM,
			timeSignature = phase.timeSignature,
			subdivision = phase.subdivision,
			coroutineScope = viewModelScope
		)
	}
	
	// ⏱ Timer preciso con SystemClock
	private fun startTimer() {
		stopTimer()
		
		val startTime = SystemClock.elapsedRealtime()
		val initialState = (_uiState.value as? PlaybackUiState.Playing)?.state ?: return
		val initialRemaining = initialState.timeRemaining
		val initialElapsed = initialState.totalElapsedTime
		
		timerJob = viewModelScope.launch {
			while (isActive) {
				val now = SystemClock.elapsedRealtime()
				val elapsedSinceStart = ((now - startTime) / 1000).toInt()
				val totalElapsed = initialElapsed + elapsedSinceStart
				val newRemaining = (initialRemaining - elapsedSinceStart).coerceAtLeast(0)
				
				val currentState = (_uiState.value as? PlaybackUiState.Playing)?.state ?: continue
				if (!currentState.isPaused) {
					// 🔹 Calculamos minutos redondeados automáticamente
					val roundedMinutes = if (totalElapsed % 60 >= 30) {
						(totalElapsed / 60) + 1
					} else {
						totalElapsed / 60
					}
					val newState = currentState.copy(
						timeRemaining = newRemaining,
						totalElapsedTime = roundedMinutes * 60 // ⚠️ guardamos en segundos, pero UI solo muestra minutos
					)
					_uiState.value = PlaybackUiState.Playing(newState)
					
					if (newRemaining <= 0) {
						handlePhaseComplete(currentState)
						break
					}
				}
				
				val nextTick = 1000 - (now - startTime) % 1000
				delay(nextTick)
			}
		}
	}
	
	
	private fun stopTimer() {
		timerJob?.cancel()
		timerJob = null
	}
	
	override fun onCleared() {
		super.onCleared()
		stopTimer()
		countdownJob?.cancel()
		metronome?.release()
	}
}
