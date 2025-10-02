package com.example.studivo.domain.repository

import com.example.studivo.domain.model.Phase

import com.example.studivo.presentation.ui.routine.Routine

interface RoutineRepository {
	suspend fun insertRoutine(routine: Routine): String
	suspend fun insertPhase(phase: Phase): String
	suspend fun getAllRoutines(): List<Routine>
	suspend fun getPhasesByRoutine(routineId: String): List<Phase>
	suspend fun deleteRoutine(routineId: String)
	suspend fun deletePhase(phaseId: String)
	suspend fun updatePhasesOrder(phases: List<Phase>)
	suspend fun getRoutineById(routineId: String): Routine?   // nuevo
	suspend fun updateRoutine(routine: Routine)              // nuevo
	suspend fun updatePhase(phase: Phase)
	suspend fun getPhaseById(phaseId: String): Phase?   // útil para cargar datos de edición
}