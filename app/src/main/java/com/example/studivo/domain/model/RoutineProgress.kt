package com.example.studivo.domain.model

import com.example.studivo.domain.model.entity.RoutineProgressEntity

data class RoutineProgress(
	val id: String,
	val routineId: String?,
	val routineName: String = "",
	val date: String,
	val progressPercentage: Int,
	val currentPhaseIndex: Int,
	val currentRepetition: Int,
	val totalElapsedTime: Int,
	val isCompleted: Boolean,
	val lastUpdated: Long,
	val createdAt: Long
)

// Mapper extension: Entity to Domain
fun RoutineProgressEntity.toDomain(): RoutineProgress {
	return RoutineProgress(
		id = id,
		routineId = routineId,
		routineName = routineName,
		date = date,
		progressPercentage = progressPercentage,
		currentPhaseIndex = currentPhaseIndex,
		currentRepetition = currentRepetition,
		totalElapsedTime = totalElapsedTime,
		isCompleted = isCompleted,
		lastUpdated = lastUpdated,
		createdAt = createdAt
	)
}

// Mapper extension: Domain to Entity
fun RoutineProgress.toEntity(): RoutineProgressEntity {
	return RoutineProgressEntity(
		id = id,
		routineId = routineId,
		routineName = routineName,
		date = date,
		progressPercentage = progressPercentage,
		currentPhaseIndex = currentPhaseIndex,
		currentRepetition = currentRepetition,
		totalElapsedTime = totalElapsedTime,
		isCompleted = isCompleted,
		lastUpdated = lastUpdated,
		createdAt = createdAt
	)
}