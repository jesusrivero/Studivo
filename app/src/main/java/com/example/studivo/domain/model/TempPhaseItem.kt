package com.example.studivo.domain.model

import com.example.studivo.domain.model.entity.PhaseEntity
import com.example.studivo.presentation.ui.routine.PhaseIcon
import java.util.UUID

data class TempPhaseItem(
	val tempId: String = UUID.randomUUID().toString(),
	val name: String,
	val duration: Int,
	val bpmInitial: Int,
	val timeSignature: String,
	val color: String, // ✅ string HEX
	val mode: String,
	val repetitions: Int,
	val bpmIncrement: Int,
	val bpmMax: Int,
	val routineId: String = ""
) {
	fun toPhaseEntity(routineId: String): PhaseEntity {
		return PhaseEntity(
			id = UUID.randomUUID().toString(),
			routineId = routineId,
			name = name,
			duration = duration,
			bpm = bpmInitial,
			timeSignature = timeSignature,
			color = color, // ya string
			mode = mode,
			repetitions = repetitions,
			bpmIncrement = bpmIncrement,
			bpmMax = bpmMax
		)
	}
}