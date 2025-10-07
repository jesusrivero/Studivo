package com.example.studivo.domain.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studivo.domain.model.MetronomeState
import com.example.studivo.domain.services.Metronome
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class MetronomeViewModel @Inject constructor(
	@ApplicationContext private val context: Context
) : ViewModel() {
	
	private val _uiState = MutableStateFlow(MetronomeState())
	val uiState: StateFlow<MetronomeState> = _uiState.asStateFlow()
	
	private var metronome: Metronome? = null
	// ❌ ELIMINAR: private var beatCounterJob: Job? = null
	
	init {
		metronome = Metronome(context)
		
		// ✨ AGREGAR: Registrar el callback para recibir notificaciones de beats
		metronome?.setOnBeatListener { currentBeat, _ ->
			_uiState.value = _uiState.value.copy(currentBeat = currentBeat)
		}
	}
	
	fun updateBpm(newBpm: Int) {
		val currentState = _uiState.value
		_uiState.value = currentState.copy(bpm = newBpm)
		
		// Si está sonando, actualizar en tiempo real
		if (currentState.isPlaying) {
			metronome?.updateTempo(
				newBpm,
				currentState.timeSignature,
				currentState.subdivision,
				viewModelScope
			)
		}
	}
	
	fun updateTimeSignature(newSignature: String) {
		val currentState = _uiState.value
		_uiState.value = currentState.copy(
			timeSignature = newSignature,
			currentBeat = 1 // Reset al cambiar compás
		)
		
		// Si está sonando, actualizar en tiempo real
		if (currentState.isPlaying) {
			metronome?.updateTempo(
				bpm = currentState.bpm,
				timeSignature = newSignature,
				subdivision = currentState.subdivision, // 👈 agrega esto
				coroutineScope = viewModelScope
			)
		}
	}
	
	fun togglePlayPause() {
		val currentState = _uiState.value
		
		if (currentState.isPlaying) {
			// Detener
			metronome?.stop()
			_uiState.value = currentState.copy(
				isPlaying = false,
				currentBeat = 1
			)
		} else {
			// Iniciar
			metronome?.start(
				bpm = currentState.bpm,
				timeSignature = currentState.timeSignature,
				subdivision = currentState.subdivision, // ✨ NUEVO
				coroutineScope = viewModelScope
			)
			_uiState.value = currentState.copy(isPlaying = true)
		}
	}
	
	override fun onCleared() {
		super.onCleared()
		metronome?.release()
	}
}