package com.example.studivo.domain.model.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

//@Entity(
//	tableName = "phases",
//	foreignKeys = [ForeignKey(
//		entity = RoutineEntity::class,
//		parentColumns = ["id"],
//		childColumns = ["routineId"],
//		onDelete = ForeignKey.CASCADE
//	)],
//	indices = [Index("routineId")]
//)
//data class PhaseEntity(
//	@PrimaryKey val id: String = java.util.UUID.randomUUID().toString(), // UUID en lugar de Long
//	val routineId: String,
//	val name: String,
//	val duration: Int,
//	val bpm: Int = 0, // reemplaza bpmInitial
//	val timeSignature: String = "4/4",
//	val color: Int = 0xFF2196F3.toInt(),
//	val mode: String = "BY_REPS", // "BY_REPS" | "UNTIL_BPM_MAX"
//	val repetitions: Int = 1,
//	val bpmIncrement: Int = 0,
//	val bpmMax: Int = 0
//)
