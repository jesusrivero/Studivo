package com.example.studivo.domain.model

import com.example.studivo.domain.model.entity.PhaseEntity
import com.example.studivo.presentation.ui.routine.PhaseIcon
import java.util.UUID

// Modelo temporal para las fases durante la creación
data class TempPhaseItem(
	val tempId: String = UUID.randomUUID().toString(),
	val name: String,
	val duration: Int,
	val bpmInitial: Int,
	val timeSignature: String,
	val color: Int,
	val mode: String,
	val repetitions: Int,
	val bpmIncrement: Int,
	val bpmMax: Int,
	val routineId: String = ""
) {
	fun toPhaseEntity(routineId: String): PhaseEntity {
		return PhaseEntity(
			routineId = routineId,
			name = name,
			duration = duration,
			bpm = bpmInitial,
			timeSignature = timeSignature,
			color = color,
			mode = mode,
			repetitions = repetitions,
			bpmIncrement = bpmIncrement,
			bpmMax = bpmMax,

		)
	}
}