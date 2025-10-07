package com.example.studivo.domain.usecase

import com.example.studivo.domain.model.RoutinePlaybackState
import com.example.studivo.domain.repository.RoutineRepository


// --- Use Cases ---

class StartRoutinePlaybackUseCase(
	private val repository: RoutineRepository
) {
	suspend operator fun invoke(routineId: String): Result<RoutinePlaybackState> {
		return try {
			val routine = repository.getRoutineById(routineId)
				?: return Result.failure(Exception("Rutina no encontrada"))
			
			val phases = repository.getPhasesByRoutine(routineId)
				.sortedBy { it.order }
			
			if (phases.isEmpty()) {
				return Result.failure(Exception("La rutina no tiene fases"))
			}
			
			val initialState = RoutinePlaybackState(
				routine = routine,
				phases = phases,
				currentPhaseIndex = 0,
				currentRepetition = 1,
				timeRemaining = phases.first().duration * 60,
				isPaused = false,
				isCompleted = false,
				totalElapsedTime = 0
			)
			
			Result.success(initialState)
		} catch (e: Exception) {
			Result.failure(e)
		}
	}
}