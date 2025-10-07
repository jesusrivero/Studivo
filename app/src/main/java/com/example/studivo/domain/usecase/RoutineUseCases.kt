package com.example.studivo.domain.usecase

import com.example.studivo.domain.model.Phase
import com.example.studivo.domain.repository.RoutineRepository
import com.example.studivo.presentation.ui.routine.Routine

class InsertRoutineUseCase(private val repository: RoutineRepository) {
	suspend operator fun invoke(routine: Routine): String = repository.insertRoutine(routine)
}

class InsertPhaseUseCase(private val repository: RoutineRepository) {
	suspend operator fun invoke(phase: Phase): String = repository.insertPhase(phase)
}

class GetPhasesByRoutineUseCase(private val repository: RoutineRepository) {
	suspend operator fun invoke(routineId: String): List<Phase> = repository.getPhasesByRoutine(routineId)
}

class DeleteRoutineUseCase(private val repository: RoutineRepository) {
	suspend operator fun invoke(routineId: String) = repository.deleteRoutine(routineId)
}

class DeletePhaseUseCase(private val repository: RoutineRepository) {
	suspend operator fun invoke(phaseId: String) = repository.deletePhase(phaseId)
}

class UpdatePhasesOrderUseCase(private val repository: RoutineRepository) {
	suspend operator fun invoke(phases: List<Phase>) = repository.updatePhasesOrder(phases)
}


class GetRoutineByIdUseCase(private val repository: RoutineRepository) {
	suspend operator fun invoke(routineId: String): Routine? = repository.getRoutineById(routineId)
}

class UpdateRoutineUseCase(private val repository: RoutineRepository) {
	suspend operator fun invoke(routine: Routine) = repository.updateRoutine(routine)
}

class UpdatePhaseUseCase(private val repository: RoutineRepository) {
	suspend operator fun invoke(phase: Phase) = repository.updatePhase(phase)
}

class GetPhaseByIdUseCase(private val repository: RoutineRepository) {
	suspend operator fun invoke(phaseId: String): Phase? = repository.getPhaseById(phaseId)
}


class DeleteRoutineWithPhasesUseCase(private val repository: RoutineRepository) {
	suspend operator fun invoke(routineId: String) {
		repository.deleteRoutineWithPhases(routineId)
	}
}

//class GetRoutineSummariesUseCase(private val repository: RoutineRepository) {
//	suspend operator fun invoke(): List<RoutineSummary> {
//		val routines = repository.getAllRoutines()
//		return routines.map { routine ->
//			val phases = repository.getPhasesByRoutine(routine.id)
//			RoutineSummary(
//				id = routine.id,
//				name = routine.name,
//				description = routine.description,
//				totalPhases = phases.size,
//				totalDuration = phases.sumOf { it.duration },
//				createdAt = routine.createdAt
//			)
//		}
//	}
	
	class GetAllRoutinesUseCase(private val repository: RoutineRepository) {
		suspend operator fun invoke(): List<Routine> = repository.getAllRoutines()
	}


data class RoutineUseCases(
	val insertRoutine: InsertRoutineUseCase,
	val insertPhase: InsertPhaseUseCase,
	val getPhasesByRoutine: GetPhasesByRoutineUseCase,
	val deleteRoutine: DeleteRoutineUseCase,
	val deletePhase: DeletePhaseUseCase,
	val updatePhasesOrder: UpdatePhasesOrderUseCase,
	val getRoutineById: GetRoutineByIdUseCase,
	val updateRoutine: UpdateRoutineUseCase,
	val updatePhase: UpdatePhaseUseCase,
	val getPhaseById: GetPhaseByIdUseCase,
	val deleteRoutineWithPhases: DeleteRoutineWithPhasesUseCase,
//	val getRoutineSummaries: GetRoutineSummariesUseCase,
	val getAllRoutines: GetAllRoutinesUseCase
)