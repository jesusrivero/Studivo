package com.example.studivo.domain.model

data class DayProgress(
	val date: String, // "yyyy-MM-dd"
	val dayOfMonth: Int,
	val hasProgress: Boolean,
	val hasCompletedRoutine: Boolean,
	val totalProgressPercentage: Int,
	val routinesCount: Int,
	val dayOfWeekInitial: String
)
