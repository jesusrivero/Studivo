package com.example.studivo.data.mapper

import android.R.attr.order
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.example.studivo.domain.model.Phase
import com.example.studivo.domain.model.entity.PhaseEntity
import com.example.studivo.domain.model.entity.RoutineEntity
import com.example.studivo.presentation.ui.routine.Routine


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
	color = Color(0xFF2196F3).toArgb(),
	createdAt = createdAt
)

fun Phase.toEntity() = PhaseEntity(
	id = id,
	routineId = routineId,
	name = name,
	duration = duration,
	bpm = bpm,
	timeSignature = timeSignature,
	color = color.toArgb(),
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
	color = if (color != 0) Color(color) else Color(0xFF2196F3),
	repetitions = repetitions,
	bpmIncrement = bpmIncrement,
	bpmMax = bpmMax,
	mode = mode,
	order = order
)


