package com.example.studivo.domain.model.entity

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID


@Entity(tableName = "routines")
data class RoutineEntity(
	@PrimaryKey val id: String = UUID.randomUUID().toString(),
	val name: String,
	val description: String = "",
	val color: String = "#FF2196F3", // ✅ color como string hex
	val createdAt: Long = System.currentTimeMillis()
)
@Entity(
	tableName = "phases",
	foreignKeys = [ForeignKey(
		entity = RoutineEntity::class,
		parentColumns = ["id"],
		childColumns = ["routineId"],
		onDelete = ForeignKey.CASCADE
	)],
	indices = [Index("routineId")]
)
data class PhaseEntity(
	@PrimaryKey val id: String = UUID.randomUUID().toString(),
	val routineId: String,
	val name: String,
	val duration: Int,
	val bpm: Int = 0,
	val timeSignature: String = "4/4",
	val subdivision: String = "QUARTER", // ✨ NUEVO
	val color: String = "#FF2196F3",
	val mode: String = "BY_REPS",
	val repetitions: Int = 1,
	val bpmIncrement: Int = 0,
	val bpmMax: Int = 0,
	val order: Int = 0
)