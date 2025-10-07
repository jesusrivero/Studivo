package com.example.studivo.domain.model

// --- Data Classes ---
data class Routine(
	val id: String,
	val name: String,
	val description: String = "",
	val phases: List<Phase> = emptyList(),
	val createdAt: Long = System.currentTimeMillis()
)
