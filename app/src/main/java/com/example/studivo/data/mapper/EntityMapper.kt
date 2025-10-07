package com.example.studivo.data.mapper


import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.example.studivo.domain.model.NoteSubdivision
import com.example.studivo.domain.model.Phase
import com.example.studivo.domain.model.entity.PhaseEntity
import com.example.studivo.domain.model.entity.RoutineEntity
import com.example.studivo.presentation.ui.routine.Routine
import com.example.studivo.presentation.utils.fromHex
import com.example.studivo.presentation.utils.toHexString


fun RoutineEntity.toDomain(phases: List<Phase> = emptyList()) = Routine(
	id = id,
	name = name,
	description = description,
	phases = phases,
	createdAt = createdAt
)

fun Routine.toEntity() = RoutineEntity(
	id = id,
	name = name,
	description = description,
	color = "#FF2196F3", // string HEX por defecto
	createdAt = createdAt
)

fun Phase.toEntity() = PhaseEntity(
	id = id,
	routineId = routineId,
	name = name,
	duration = duration,
	bpm = bpm,
	timeSignature = timeSignature,
	subdivision = subdivision.name, // ✨ Enum -> String
	color = color.toHexString(), // Color -> String
	repetitions = repetitions,
	bpmIncrement = bpmIncrement,
	bpmMax = bpmMax,
	mode = mode,
	order = order
)

fun PhaseEntity.toDomain(): Phase = Phase(
	id = id,
	routineId = routineId,
	name = name,
	duration = duration,
	bpm = bpm,
	timeSignature = timeSignature,
	subdivision = NoteSubdivision.fromName(subdivision), // ✨ String -> Enum
	color = color.fromHex(), // String -> Color
	repetitions = repetitions,
	bpmIncrement = bpmIncrement,
	bpmMax = bpmMax,
	mode = mode,
	order = order
)

