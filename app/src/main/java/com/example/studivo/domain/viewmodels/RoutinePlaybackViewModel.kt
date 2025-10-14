package com.example.studivo.domain.viewmodels


import PlaybackEvent
import PlaybackUiState
import RoutinePlaybackState
import android.content.Context
import android.os.SystemClock
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import calculateCurrentBPM
import com.example.studivo.domain.model.RoutineProgress
import com.example.studivo.domain.services.Metronome
import com.example.studivo.domain.usecase.GetRoutineProgressUseCase
import com.example.studivo.domain.usecase.SaveRoutineProgressUseCase
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
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class RoutinePlaybackViewModel @Inject constructor(
	private val startRoutinePlayback: StartRoutinePlaybackUseCase,
	private val saveProgressUseCase: SaveRoutineProgressUseCase,
	private val getProgressUseCase: GetRoutineProgressUseCase,
	savedStateHandle: SavedStateHandle,
	@ApplicationContext private val context: Context,
) : ViewModel() {
	
	private var lastSavedProgress: Int? = null
	private var lastSavedPhase: Int? = null
	private var lastSavedRepetition: Int? = null
	
	private val _currentProgress = MutableStateFlow<RoutineProgress?>(null)
	val currentProgress: StateFlow<RoutineProgress?> = _currentProgress.asStateFlow()
	
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
				.onSuccess { initialState ->
					
					val date = getCurrentDateString()
					val savedProgress = getProgressUseCase(routineId, date)
					
					val shouldResume = savedProgress != null &&
							savedProgress.progressPercentage in 1..99 &&
							!savedProgress.isCompleted
					
					val state = if (shouldResume && savedProgress != null) {
						Log.d(
							TAG,
							"📥 Restaurando progreso: ${savedProgress.progressPercentage}% | Fase: ${savedProgress.currentPhaseIndex} | Rep: ${savedProgress.currentRepetition}"
						)
						
						val restoredPhase = initialState.phases.getOrNull(savedProgress.currentPhaseIndex)
						initialState.copy(
							currentPhaseIndex = savedProgress.currentPhaseIndex,
							currentRepetition = savedProgress.currentRepetition,
							timeRemaining = restoredPhase?.duration?.times(60) ?: 0,
							totalElapsedTime = savedProgress.totalElapsedTime,
							resumedFromProgress = true
						)
					} else {
						Log.d(TAG, "🆕 Iniciando rutina desde el principio")
						initialState
					}
					
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
	
	private fun startCountdown() {
		countdownJob?.cancel()
		
		val initialCountdown = 4
		(_uiState.value as? PlaybackUiState.Playing)?.state?.let { currentState ->
			val newState = currentState.copy(
				countdown = initialCountdown,
				isCountingDown = true,
				isPaused = true
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
					val newState = currentState.copy(
						countdown = 0,
						isCountingDown = false,
						isPaused = false
					)
					_uiState.value = PlaybackUiState.Playing(newState)
					startTimer()
					startMetronome(newState)
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
		startMetronome(newState)
	}
	
	
	
	private fun handleNextRepetition(state: RoutinePlaybackState) {
		if (!state.hasNextRepetition) return
		val currentPhase = state.currentPhase ?: return
		val nextRepetition = state.currentRepetition + 1
		
		// ✅ CORRECCIÓN: La lógica de canProceed debe verificar si YA alcanzamos el máximo
		val canProceed = when (currentPhase.mode) {
			"BY_REPS" -> nextRepetition <= currentPhase.repetitions
			"UNTIL_BPM_MAX" -> {
				// ✅ CAMBIO: Verificar el BPM de la repetición ACTUAL, no la siguiente
				val currentBPM = currentPhase.calculateCurrentBPM(state.currentRepetition)
				// Si el BPM actual ya es el máximo, NO procedemos (pasamos a siguiente fase)
				currentBPM < currentPhase.bpmMax
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
	
	
	private fun handleRepeatPhase(state: RoutinePlaybackState) {
		val currentPhase = state.currentPhase ?: return
		val newState = state.copy(timeRemaining = currentPhase.duration * 60)
		_uiState.value = PlaybackUiState.Playing(newState)
		
		startTimer()
		startMetronome(newState)
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
			timeRemaining = 0,
			totalElapsedTime = state.accurateTotalTime // ✅ CAMBIO CLAVE
		)
		_uiState.value = PlaybackUiState.Playing(newState)
		
		updateProgressDuringPlayback(newState)
	}
	
	private fun startMetronome(state: RoutinePlaybackState) {
		val phase = state.currentPhase ?: return
		
		if (state.isPaused || phase.bpm <= 0) {
			metronome?.stop()
			return
		}
		
		metronome?.start(
			bpm = state.currentBPM,
			timeSignature = phase.timeSignature,
			subdivision = phase.subdivision,
			coroutineScope = viewModelScope
		)
	}
	
	
	
	
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
					val newState = currentState.copy(
						timeRemaining = newRemaining,
						totalElapsedTime = totalElapsed
					)
					_uiState.value = PlaybackUiState.Playing(newState)
					updateProgressDuringPlayback(newState)
					
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
	
	companion object {
		private const val TAG = "RoutinePlayback"
	}
	
	
	private fun updateProgressDuringPlayback(state: RoutinePlaybackState) {
		viewModelScope.launch {
			try {
				val currentProgress = (state.progress * 100).toInt()
				
				val shouldSave = currentProgress != lastSavedProgress ||
						state.currentPhaseIndex != lastSavedPhase ||
						state.currentRepetition != lastSavedRepetition ||
						state.isCompleted
				
				if (!shouldSave) return@launch
				
				Log.d(
					TAG,
					"💾 Guardando progreso: $currentProgress% | Tiempo preciso: ${state.accurateTotalTime}s | Completado: ${state.isCompleted}"
				)
				
				val date = getCurrentDateString()
				val existingProgress = getProgressUseCase(routineId, date)
				
				val progress = RoutineProgress(
					id = existingProgress?.id ?: UUID.randomUUID().toString(),
					routineId = routineId,
					routineName = state.routine.name,
					date = date,
					progressPercentage = currentProgress.coerceIn(0, 100),
					currentPhaseIndex = state.currentPhaseIndex,
					currentRepetition = state.currentRepetition,
					totalElapsedTime = state.accurateTotalTime, // ✅ USAR TIEMPO PRECISO
					isCompleted = state.isCompleted,
					lastUpdated = System.currentTimeMillis(),
					createdAt = existingProgress?.createdAt ?: System.currentTimeMillis()
				)
				
				saveProgressUseCase(progress)
				
				lastSavedProgress = currentProgress
				lastSavedPhase = state.currentPhaseIndex
				lastSavedRepetition = state.currentRepetition
				
				Log.d(TAG, "✅ Progreso guardado: ${state.formattedTotalTime}")
				
			} catch (e: Exception) {
				Log.e(TAG, "❌ Error actualizando progreso: ${e.message}", e)
			}
		}
	}
	
	private fun getCurrentDateString(): String {
		val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
		return sdf.format(java.util.Date())
	}
}

