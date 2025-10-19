package com.example.studivo.presentation.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

fun calculateStreakFromDates(completedDates: List<String>): Int {
	if (completedDates.isEmpty()) return 0
	
	val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
	val today = sdf.format(Date())
	
	val completed = completedDates
		.mapNotNull { dateStr -> sdf.parse(dateStr) }
		.sortedDescending()
	
	val completedSet = completedDates.toSet()
	
	var streak = 0
	val current = Calendar.getInstance()
	
	// Si no completó hoy, empezar desde ayer
	if (!completedSet.contains(today)) {
		current.add(Calendar.DAY_OF_YEAR, -1)
	}
	
	for (date in completed) {
		val day = Calendar.getInstance().apply { time = date }
		
		if (day.get(Calendar.YEAR) == current.get(Calendar.YEAR) &&
			day.get(Calendar.DAY_OF_YEAR) == current.get(Calendar.DAY_OF_YEAR)
		) {
			streak++
			current.add(Calendar.DAY_OF_YEAR, -1)
		} else {
			// Si la fecha es anterior al día que buscamos, romper el streak
			if (day.before(current)) {
				break
			}
		}
	}
	
	return streak
}

fun getCurrentDateString(): String {
	val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
	return sdf.format(Date())
}