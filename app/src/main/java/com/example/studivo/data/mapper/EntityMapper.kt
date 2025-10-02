package com.example.studivo.data.mapper

import android.R.attr.mode
import com.example.studivo.domain.model.entity.PhaseEntity
import com.example.studivo.domain.model.entity.RoutineEntity
import com.example.studivo.presentation.ui.routine.Phase
import com.example.studivo.presentation.ui.routine.Routine

fun RoutineEntity.toDomain() = Routine(id, name, color)
fun Routine.toEntity() = RoutineEntity(id, name, color)



fun Phase.toEntity() = PhaseEntity(
	id, routineId, name, duration, bpmInitial, timeSignature, color,
	mode, repetitions, bpmIncrement, bpmMax, totalDuration
)