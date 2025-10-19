package com.example.studivo.data.repository

import com.example.studivo.data.local.RoutineProgressDao
import com.example.studivo.domain.model.RoutineProgress
import com.example.studivo.domain.model.toDomain
import com.example.studivo.domain.model.toEntity
import com.example.studivo.domain.repository.RoutineProgressRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class RoutineProgressRepositoryImpl @Inject constructor(
	private val progressDao: RoutineProgressDao
) : RoutineProgressRepository {
	
	override suspend fun saveProgress(progress: RoutineProgress) {
		progressDao.insertProgress(progress.toEntity())
	}
	
	override suspend fun getProgressByRoutineAndDate(
		routineId: String,
		date: String
	): RoutineProgress? {
		return progressDao.getProgressByRoutineAndDate(routineId, date)?.toDomain()
	}
	
	override fun getProgressByRoutineAndDateFlow(
		routineId: String,
		date: String
	): Flow<RoutineProgress?> {
		return progressDao.getProgressByRoutineAndDateFlow(routineId, date)
			.map { it?.toDomain() }
	}
	
	override suspend fun getLastProgressByRoutine(routineId: String): RoutineProgress? {
		return progressDao.getLastProgressByRoutine(routineId)?.toDomain()
	}
	
	override suspend fun getProgressByDate(date: String): List<RoutineProgress> {
		return progressDao.getProgressByDate(date).map { it.toDomain() }
	}
	
	override suspend fun getProgressHistoryByRoutine(routineId: String): List<RoutineProgress> {
		return progressDao.getProgressHistoryByRoutine(routineId).map { it.toDomain() }
	}
	
	override suspend fun deleteProgress(routineId: String, date: String) {
		progressDao.deleteProgress(routineId, date)
	}
	
	override suspend fun getCompletedRoutinesToday(): Int {
		val today = getCurrentDateString()
		return progressDao.getCompletedRoutinesCountByDate(today)
	}
	
	override suspend fun isRoutineCompletedToday(
		routineId: String,
		date: String
	): Boolean {
		val progress = progressDao.getProgressByRoutineAndDate(routineId, date)
		return progress?.isCompleted ?: false
	}
	
	private fun getCurrentDateString(): String {
		val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
		return sdf.format(java.util.Date())
	}
	
	

	override suspend fun getAllProgressOrderedByDate(): List<RoutineProgress> {
		return progressDao.getAllProgressOrderedByDate().map { it.toDomain() }
	}
}