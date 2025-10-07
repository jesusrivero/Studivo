package com.example.studivo.data.repository

import com.example.studivo.data.local.PhaseDao
import com.example.studivo.data.local.RoutineDao
import com.example.studivo.data.mapper.toDomain

import com.example.studivo.data.mapper.toEntity
import com.example.studivo.domain.model.Phase
import com.example.studivo.domain.repository.RoutineRepository
import com.example.studivo.presentation.ui.routine.Routine

class RoutineRepositoryImpl(
	private val routineDao: RoutineDao,
	private val phaseDao: PhaseDao
) : RoutineRepository {
	
	override suspend fun insertRoutine(routine: Routine): String {
		routineDao.insert(routine.toEntity())
		return routine.id
	}
	
	override suspend fun insertPhase(phase: Phase): String {
		phaseDao.insert(phase.toEntity())
		return phase.id
	}
	
	override suspend fun getAllRoutines(): List<Routine> {
		return routineDao.getAll().map { it.toDomain() }
	}
	
	override suspend fun getPhasesByRoutine(routineId: String): List<Phase> {
		return phaseDao.getPhasesByRoutine(routineId).map { it.toDomain() }
	}
	
	override suspend fun deleteRoutine(routineId: String) {
		routineDao.deleteById(routineId)
	}
	
	override suspend fun deletePhase(phaseId: String) {
		phaseDao.deleteById(phaseId)
	}
	
	override suspend fun updatePhasesOrder(phases: List<Phase>) {
		phaseDao.updatePhases(phases.map { it.toEntity() })
	}
	
	override suspend fun getRoutineById(routineId: String): Routine? =
		routineDao.getById(routineId)?.toDomain()
	
	override suspend fun updateRoutine(routine: Routine) =
		routineDao.update(routine.toEntity())
	
	
	
	override suspend fun updatePhase(phase: Phase) {
		phaseDao.updatePhase(phase.toEntity())
	}
	
	override suspend fun getPhaseById(phaseId: String): Phase? {
		return phaseDao.getById(phaseId)?.toDomain()
	}
	
	override suspend fun deleteRoutineWithPhases(routineId: String) {
		val phases = phaseDao.getPhasesByRoutine(routineId)
		phases.forEach { phaseDao.deleteById(it.id) }
		routineDao.deleteById(routineId)
	}
}