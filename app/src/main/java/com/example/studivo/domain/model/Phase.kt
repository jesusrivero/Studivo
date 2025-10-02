package com.example.studivo.domain.model

import androidx.compose.ui.graphics.Color

data class Phase(
	val id: String,
	val routineId: String,
	val name: String,
	val duration: Int,
	val bpm: Int = 0,
	val timeSignature: String = "4/4",
	val color: Color = Color(0xFF2196F3),
	val repetitions: Int = 1,
	val bpmIncrement: Int = 0,
	val bpmMax: Int = 0,
	val mode: String = "BY_REPS",
	val order: Int = 0
)