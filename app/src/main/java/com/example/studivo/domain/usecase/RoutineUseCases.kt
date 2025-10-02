package com.example.studivo.domain.usecase

import com.example.studivo.domain.model.entity.PhaseEntity
import com.example.studivo.domain.repository.RoutineRepository
import com.example.studivo.presentation.ui.routine.Phase
import com.example.studivo.presentation.ui.routine.Routine
import javax.inject.Inject


class InsertRoutineUseCase(private val repository: RoutineRepository) {
	suspend operator fun invoke(routine: Routine): Long {
		return repository.insertRoutine(routine)
	}
}

class InsertPhaseUseCase(private val repository: RoutineRepository) {
	suspend operator fun invoke(phase: Phase): Long {
		return repository.insertPhase(phase)
	}
}

class GetAllRoutinesUseCase(private val repository: RoutineRepository) {
	suspend operator fun invoke(): List<Routine> {
		return repository.getAllRoutines()
	}
}

class GetPhasesByRoutineUseCase(private val repository: RoutineRepository) {
	suspend operator fun invoke(routineId: Long): List<Phase> {
		return repository.getPhasesByRoutine(routineId)
	}
}

class DeleteRoutineUseCase(private val repository: RoutineRepository) {
	suspend operator fun invoke(routineId: Long) {
		repository.deleteRoutine(routineId)
	}
}

class DeletePhaseUseCase(private val repository: RoutineRepository) {
	suspend operator fun invoke(phaseId: Long) {
		repository.deletePhase(phaseId)
	}
}

class UpdatePhasesOrderUseCase(private val repository: RoutineRepository) {
	suspend operator fun invoke(phases: List<Phase>) {
		repository.updatePhasesOrder(phases)
	}
}

// Contenedor de todos los casos de uso
data class RoutineUseCases(
	val insertRoutine: InsertRoutineUseCase,
	val insertPhase: InsertPhaseUseCase,
	val getAllRoutines: GetAllRoutinesUseCase,
	val getPhasesByRoutine: GetPhasesByRoutineUseCase,
	val deleteRoutine: DeleteRoutineUseCase,
	val deletePhase: DeletePhaseUseCase,
	val updatePhasesOrder: UpdatePhasesOrderUseCase
)
