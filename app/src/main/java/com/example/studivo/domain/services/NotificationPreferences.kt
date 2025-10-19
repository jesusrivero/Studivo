package com.example.studivo.domain.services

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class NotificationPreferences @Inject constructor(
	@ApplicationContext private val context: Context
) {
	private val prefs = context.getSharedPreferences("notification_prefs", Context.MODE_PRIVATE)
	
	fun areNotificationsEnabled(): Boolean {
		return prefs.getBoolean("notifications_enabled", true)
	}
	
	fun setNotificationsEnabled(enabled: Boolean) {
		prefs.edit().putBoolean("notifications_enabled", enabled).apply()
	}
	
	/**
	 * Retorna las horas del día en que se deben enviar notificaciones
	 * Por defecto: 10:00, 16:00, 20:00
	 */
	fun getNotificationTimes(): List<Pair<Int, Int>> {
		// Formato: "hour:minute,hour:minute"
		val defaultTimes = listOf(
//			Pair(6,0),
			Pair(9,0),
//			Pair(14,0),
			Pair(18,0),
			Pair(21,0))
		val timesString = prefs.getString(
			"notification_times",
			defaultTimes.joinToString(",") { "${it.first}:${it.second}" }
		) ?: defaultTimes.joinToString(",") { "${it.first}:${it.second}" }
		
		return timesString.split(",").mapNotNull { timeStr ->
			val parts = timeStr.split(":")
			if (parts.size == 2) {
				val hour = parts[0].toIntOrNull()
				val minute = parts[1].toIntOrNull()
				if (hour != null && minute != null) Pair(hour, minute) else null
			} else null
		}
	}
	
	fun setNotificationTimes(times: List<Pair<Int, Int>>) {
		val timesString = times.joinToString(",") { "${it.first}:${it.second}" }
		prefs.edit().putString("notification_times", timesString).apply()
	}
}