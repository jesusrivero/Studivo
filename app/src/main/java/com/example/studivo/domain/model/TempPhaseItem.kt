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
	val subdivision: String = "QUARTER", // ✨ NUEVO
	val color: String,
	val mode: String,
	val repetitions: Int,
	val bpmIncrement: Int,
	val bpmMax: Int,
	val routineId: String = ""
) {
	fun getSubdivisionEnum(): NoteSubdivision {
		return NoteSubdivision.fromName(subdivision)
	}
}