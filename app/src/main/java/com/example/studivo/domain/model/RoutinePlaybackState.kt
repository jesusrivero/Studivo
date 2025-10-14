import com.example.studivo.domain.model.NoteSubdivision
import com.example.studivo.domain.model.Phase
import com.example.studivo.presentation.ui.routine.Routine

data class RoutinePlaybackState(
	val routine: Routine,
	val phases: List<Phase>,
	val currentPhaseIndex: Int = 0,
	val currentRepetition: Int = 1,
	val timeRemaining: Int = 0,
	val isPaused: Boolean = false,
	val isCompleted: Boolean = false,
	val subdivision: NoteSubdivision = NoteSubdivision.QUARTER,
	val totalElapsedTime: Int = 0,
	val countdown: Int = 0,
	val isCountingDown: Boolean = false,
	val resumedFromProgress: Boolean = false
) {
	val currentPhase: Phase?
		get() = phases.getOrNull(currentPhaseIndex)
	
	val currentBPM: Int
		get() = currentPhase?.calculateCurrentBPM(currentRepetition) ?: 0
	
	// ✅ NUEVO: Calcular tiempo total transcurrido de forma precisa
	val accurateTotalTime: Int
		get() {
			if (phases.isEmpty()) return 0
			
			var totalSeconds = 0
			
			// Sumar tiempo de fases completadas
			for (i in 0 until currentPhaseIndex) {
				val phase = phases[i]
				val phaseReps = phase.calculateTotalRepetitions()
				totalSeconds += phase.duration * 60 * phaseReps
			}
			
			// Sumar tiempo de la fase actual
			val currentPhase = phases.getOrNull(currentPhaseIndex)
			if (currentPhase != null) {
				// Repeticiones completadas en esta fase
				val completedReps = (currentRepetition - 1).coerceAtLeast(0)
				totalSeconds += completedReps * (currentPhase.duration * 60)
				
				// Tiempo transcurrido en la repetición actual
				val currentRepTime = currentPhase.duration * 60 - timeRemaining
				totalSeconds += currentRepTime.coerceAtLeast(0)
			}
			
			return totalSeconds
		}
	
	// ✅ Formatear tiempo en formato legible (para mostrar en UI)
	val formattedTotalTime: String
		get() {
			val totalMinutes = (accurateTotalTime / 60.0)
			val hours = (totalMinutes / 60).toInt()
			val minutes = (totalMinutes % 60).toInt()
			val seconds = accurateTotalTime % 60
			
			return when {
				hours > 0 -> {
					// Si hay horas, redondear minutos hacia arriba si hay segundos
					val displayMinutes = if (seconds > 0) minutes + 1 else minutes
					if (displayMinutes >= 60) {
						"${hours + 1} h"
					} else {
						"$hours h $displayMinutes min"
					}
				}
				minutes > 0 -> {
					// Si solo hay minutos, redondear hacia arriba si hay segundos
					val displayMinutes = if (seconds > 0) minutes + 1 else minutes
					"$displayMinutes min"
				}
				else -> {
					// Menos de un minuto
					"$seconds seg"
				}
			}
		}
	
	val progress: Float
		get() {
			if (phases.isEmpty()) return 0f
			
			val totalUnits = phases.sumOf { it.calculateTotalRepetitions() }
			if (totalUnits == 0) return 0f
			
			var completedUnits = 0f
			for (i in 0 until currentPhaseIndex) {
				completedUnits += phases[i].calculateTotalRepetitions().toFloat()
			}
			
			val currentPhase = phases.getOrNull(currentPhaseIndex)
			if (currentPhase != null) {
				if (timeRemaining <= 0 && !isCompleted) {
					completedUnits += currentRepetition
				} else {
					completedUnits += (currentRepetition - 1).toFloat()
					val totalTime = currentPhase.duration * 60
					if (totalTime > 0) {
						val currentProgress = 1f - (timeRemaining.toFloat() / totalTime.toFloat())
						completedUnits += currentProgress.coerceIn(0f, 1f)
					}
				}
			}
			
			return (completedUnits.toFloat() / totalUnits.toFloat()).coerceIn(0f, 1f)
		}
	
	val hasNextPhase: Boolean
		get() = currentPhaseIndex < phases.size - 1
	
	val hasNextRepetition: Boolean
		get() = currentPhase?.let { phase ->
			when (phase.mode) {
				"BY_REPS" -> currentRepetition < phase.repetitions
				"UNTIL_BPM_MAX" -> {
					val currentBPM = phase.calculateCurrentBPM(currentRepetition)
					currentBPM < phase.bpmMax
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

// Extension para calcular total de repeticiones
fun Phase.calculateTotalRepetitions(): Int {
	return when (mode) {
		"BY_REPS" -> repetitions.coerceAtLeast(1)
		"UNTIL_BPM_MAX" -> {
			if (bpmIncrement <= 0 || bpmMax <= bpm) return 1
			val totalIncrements = (bpmMax - bpm) / bpmIncrement
			(totalIncrements + 1).coerceAtLeast(1)
		}
		else -> 1
	}
}

// Extension para obtener duración total de la fase considerando repeticiones
fun Phase.getTotalDurationMinutes(): Int {
	return duration * calculateTotalRepetitions()
}

sealed class PlaybackEvent {
	object PlayPause : PlaybackEvent()
	object NextPhase : PlaybackEvent()
	object NextRepetition : PlaybackEvent()
	object RepeatPhase : PlaybackEvent()
	object CompleteRoutine : PlaybackEvent()
}

sealed class PlaybackUiState {
	object Loading : PlaybackUiState()
	data class Error(val message: String) : PlaybackUiState()
	data class Playing(val state: RoutinePlaybackState) : PlaybackUiState()
}