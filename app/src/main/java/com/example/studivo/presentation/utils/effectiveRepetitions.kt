package com.example.studivo.presentation.utils

import com.example.studivo.domain.model.Phase
import kotlin.math.ceil

// devuelve cuántas repeticiones efectivas tendrá esta fase según su modo
fun Phase.effectiveRepetitions(): Int {
	return when (mode) {
		"BY_REPS" -> repetitions.coerceAtLeast(1)
		"UNTIL_BPM_MAX" -> {
			if (bpmIncrement > 0 && bpmMax > bpm) {
				val diff = bpmMax - bpm
				val incrementsNeeded = ceil(diff.toDouble() / bpmIncrement.toDouble()).toInt()
				(incrementsNeeded + 1).coerceAtLeast(1)
			} else 1
		}
		else -> repetitions.coerceAtLeast(1)
	}
}

// duración total en minutos que ocupa la fase considerando repeticiones
fun Phase.getTotalDurationMinutes(): Int {
	val reps = effectiveRepetitions()
	return duration * reps
}

