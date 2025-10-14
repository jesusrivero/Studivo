package com.example.studivo.domain.model.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID
@Entity(
	tableName = "routine_progress",
	foreignKeys = [
		ForeignKey(
			entity = RoutineEntity::class,
			parentColumns = ["id"],
			childColumns = ["routineId"],
			onDelete = ForeignKey.SET_NULL
		)
	],
	indices = [
		Index(value = ["routineId", "date"], unique = false),
		Index(value = ["routineId"], unique = false)
	]
)
data class RoutineProgressEntity(
	@PrimaryKey val id: String = UUID.randomUUID().toString(),
	val routineId: String?, // ✅
	val routineName: String = "",
	val date: String, //
	val progressPercentage: Int = 0,
	val currentPhaseIndex: Int = 0,
	val currentRepetition: Int = 1,
	val totalElapsedTime: Int = 0,
	val isCompleted: Boolean = false,
	val lastUpdated: Long = System.currentTimeMillis(),
	val createdAt: Long = System.currentTimeMillis()
)