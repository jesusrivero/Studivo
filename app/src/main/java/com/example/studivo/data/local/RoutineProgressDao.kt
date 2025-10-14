package com.example.studivo.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.studivo.domain.model.entity.RoutineProgressEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RoutineProgressDao {
	
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insertProgress(progress: RoutineProgressEntity)
	
	@Update
	suspend fun updateProgress(progress: RoutineProgressEntity)
	
	// ✅ Estas queries siguen funcionando con routineId nullable
	@Query("SELECT * FROM routine_progress WHERE routineId = :routineId AND date = :date ORDER BY lastUpdated DESC LIMIT 1")
	suspend fun getProgressByRoutineAndDate(routineId: String, date: String): RoutineProgressEntity?
	
	@Query("SELECT * FROM routine_progress WHERE routineId = :routineId AND date = :date ORDER BY lastUpdated DESC LIMIT 1")
	fun getProgressByRoutineAndDateFlow(routineId: String, date: String): Flow<RoutineProgressEntity?>
	
	@Query("SELECT * FROM routine_progress WHERE routineId = :routineId ORDER BY date DESC LIMIT 1")
	suspend fun getLastProgressByRoutine(routineId: String): RoutineProgressEntity?
	
	// ✅ Sin cambios - trae todo el progreso de un día
	@Query("SELECT * FROM routine_progress WHERE date = :date ORDER BY lastUpdated DESC")
	suspend fun getProgressByDate(date: String): List<RoutineProgressEntity>
	
	@Query("SELECT * FROM routine_progress WHERE routineId = :routineId ORDER BY date DESC")
	suspend fun getProgressHistoryByRoutine(routineId: String): List<RoutineProgressEntity>
	
	@Query("DELETE FROM routine_progress WHERE date = :date")
	suspend fun deleteProgressByDate(date: String)
	
	@Query("DELETE FROM routine_progress WHERE routineId = :routineId AND date = :date")
	suspend fun deleteProgress(routineId: String, date: String)
	
	@Delete
	suspend fun deleteProgress(progress: RoutineProgressEntity)
	
	// ✅ CAMBIO IMPORTANTE: Contar rutinas únicas completadas (incluso eliminadas)
	@Query("SELECT COUNT(DISTINCT routineId) FROM routine_progress WHERE date = :date AND isCompleted = 1")
	suspend fun getCompletedRoutinesCountByDate(date: String): Int
	
	// ✅ Sin cambios - para la racha histórica
	@Query("SELECT * FROM routine_progress ORDER BY date DESC, lastUpdated DESC")
	suspend fun getAllProgressOrderedByDate(): List<RoutineProgressEntity>
}