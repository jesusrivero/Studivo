package com.example.studivo.domain.model

import com.example.studivo.presentation.ui.routine.Routine


// --- Models ---
data class RoutinePlaybackState(
	val routine: Routine,
	val phases: List<Phase>,
	val currentPhaseIndex: Int = 0,
	val currentRepetition: Int = 1,
	val timeRemaining: Int = 0, // en segundos
	val isPaused: Boolean = false,
	val isCompleted: Boolean = false,
	val subdivision: NoteSubdivision = NoteSubdivision.QUARTER,
	val totalElapsedTime: Int = 0, // tiempo total transcurrido en segundos
	val countdown: Int = 0,           // 🔹 Valor actual de la cuenta regresiva
	val isCountingDown: Boolean = false // 🔹 Si estamos en cuenta regresiva
) {
	val currentPhase: Phase?
		get() = phases.getOrNull(currentPhaseIndex)
	
	val currentBPM: Int
		get() = currentPhase?.calculateCurrentBPM(currentRepetition) ?: 0
	
	val progress: Float
		get() = if (phases.isNotEmpty()) {
			(currentPhaseIndex + 1).toFloat() / phases.size.toFloat()
		} else 0f
	
	val hasNextPhase: Boolean
		get() = currentPhaseIndex < phases.size - 1
	
	val hasNextRepetition: Boolean
		get() = currentPhase?.let { phase ->
			when (phase.mode) {
				"BY_REPS" -> currentRepetition < phase.repetitions
				"UNTIL_BPM_MAX" -> {  // ✅ CAMBIO: "BY_BPM_MAX" → "UNTIL_BPM_MAX"
					val nextBPM = phase.calculateCurrentBPM(currentRepetition + 1)
					nextBPM <= phase.bpmMax
				}
				else -> false
			}
		} ?: false
}


// Extension para calcular BPM actual según repetición
fun Phase.calculateCurrentBPM(repetition: Int): Int {
	if (bpmIncrement == 0) return bpm
	
	val incrementedBPM = bpm + ((repetition - 1) * bpmIncrement)
	return if (bpmMax > 0) minOf(incrementedBPM, bpmMax) else incrementedBPM
}

// Extension para calcular total de repeticiones en modo BY_BPM_MAX
fun Phase.calculateTotalRepetitions(): Int {
	return when (mode) {
		"BY_REPS" -> repetitions
		"BY_BPM_MAX" -> {
			if (bpmIncrement <= 0 || bpmMax <= bpm) return 1
			val totalIncrements = (bpmMax - bpm) / bpmIncrement
			totalIncrements + 1
		}
		else -> 1
	}
}

// Extension para obtener duración total de la fase considerando repeticiones
fun Phase.getTotalDurationMinutes(): Int {
	return duration * calculateTotalRepetitions()
}


// ==================== PRESENTATION LAYER ====================

sealed class PlaybackEvent {
	object PlayPause : PlaybackEvent()
	object NextPhase : PlaybackEvent()
	object NextRepetition : PlaybackEvent()
	object RepeatPhase : PlaybackEvent()
//	object TickSecond : PlaybackEvent() // Para el countdown del timer
	object CompleteRoutine : PlaybackEvent()
}

sealed class PlaybackUiState {
	object Loading : PlaybackUiState()
	data class Error(val message: String) : PlaybackUiState()
	data class Playing(val state: RoutinePlaybackState) : PlaybackUiState()
}



