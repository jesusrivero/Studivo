package com.example.studivo.domain.repository

import com.example.studivo.domain.model.RoutineProgress
import kotlinx.coroutines.flow.Flow

interface RoutineProgressRepository {
	

	suspend fun saveProgress(progress: RoutineProgress)
	
	// Obtener progreso por rutina y fecha
	suspend fun getProgressByRoutineAndDate(routineId: String, date: String): RoutineProgress?
	
	// Flow para observar cambios en tiempo real
	fun getProgressByRoutineAndDateFlow(routineId: String, date: String): Flow<RoutineProgress?>
	
	// Obtener último progreso de una rutina
	suspend fun getLastProgressByRoutine(routineId: String): RoutineProgress?
	
	// Obtener todos los progresos de un día
	suspend fun getProgressByDate(date: String): List<RoutineProgress>
	
	// Historial de progreso de una rutina
	suspend fun getProgressHistoryByRoutine(routineId: String): List<RoutineProgress>
	
	// Eliminar progreso
	suspend fun deleteProgress(routineId: String, date: String)
	
	// Obtener rutinas completadas hoy
	suspend fun getCompletedRoutinesToday(): Int
	
	// Verificar si la rutina ya está completada hoy
	suspend fun isRoutineCompletedToday(routineId: String, date: String): Boolean
	
	// Obtener todos los progresos ordenados por fecha
	suspend fun getAllProgressOrderedByDate(): List<RoutineProgress>
}