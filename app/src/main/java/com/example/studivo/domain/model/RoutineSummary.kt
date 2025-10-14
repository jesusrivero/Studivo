package com.example.studivo.domain.model

data class RoutineSummary(
	val id: String,
	val name: String,
	val description: String,
	val totalPhases: Int,
	val totalDuration: Int,
	val createdAt: Long
) {
	fun getFormattedDuration(): String {
		val hours = totalDuration / 60
		val minutes = totalDuration % 60
		return if (hours > 0) "$hours h $minutes min" else "$minutes min"
	}
}