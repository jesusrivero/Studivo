package com.example.studivo.domain.model.helpers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.studivo.R
import com.example.studivo.domain.repository.RoutineProgressRepository
import com.example.studivo.domain.services.NotificationMessages
import com.example.studivo.domain.services.NotificationMessages.NotificationMessage
import com.example.studivo.domain.services.NotificationPreferences
import com.example.studivo.infraestructure.MainActivity
import com.example.studivo.presentation.utils.calculateStreakFromDates
import com.example.studivo.presentation.utils.getCurrentDateString
import dagger.hilt.android.qualifiers.ApplicationContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject


class NotificationHelper @Inject constructor(
	@ApplicationContext private val context: Context,
	private val progressRepository: RoutineProgressRepository,
	private val preferences: NotificationPreferences
) {
	
	companion object {
		const val CHANNEL_ID = "studivo_reminders"
		const val NOTIFICATION_ID = 2001
	}
	
	suspend fun checkAndSendReminder() {
		if (!preferences.areNotificationsEnabled()) return
		
		val today = getCurrentDateString()
		val todayProgress = progressRepository.getProgressByDate(today)
		val hasCompletedToday = todayProgress.any { it.isCompleted }
		
		if (hasCompletedToday) {
			// Ya completó una rutina hoy, no enviar notificación
			return
		}
		
		// Calcular días sin estudiar
		val daysWithoutStudy = calculateDaysWithoutStudy()
		
		// Obtener mensaje apropiado según la situación
		val message = NotificationMessages.getMessage(
			daysWithoutStudy = daysWithoutStudy,
			hasStreak = getCurrentStreak() > 0
		)
		
		sendNotification(message)
	}
	
	private suspend fun calculateDaysWithoutStudy(): Int {
		val allProgress = progressRepository.getAllProgressOrderedByDate()
		val lastCompletedDate = allProgress
			.firstOrNull { it.isCompleted }
			?.date
			?: return Int.MAX_VALUE // Nunca ha completado nada
		
		val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
		val lastDate = sdf.parse(lastCompletedDate) ?: return 0
		val today = Date()
		
		val diffInMillis = today.time - lastDate.time
		return (diffInMillis / (1000 * 60 * 60 * 24)).toInt()
	}
	
	private suspend fun getCurrentStreak(): Int {
		// Reutilizar lógica del ViewModel
		val calendar = progressRepository.getAllProgressOrderedByDate()
		val completedDays = calendar
			.filter { it.isCompleted }
			.map { it.date }
			.distinct()
		
		return calculateStreakFromDates(completedDays)
	}
	
	private fun sendNotification(message: NotificationMessage) {
		createNotificationChannel()
		
		val intent = Intent(context, MainActivity::class.java).apply {
			flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
		}
		
		val pendingIntent = PendingIntent.getActivity(
			context,
			0,
			intent,
			PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
		)
		
		val notification = NotificationCompat.Builder(context, CHANNEL_ID)
			.setSmallIcon(R.drawable.ic_studivo_logo_2)
			.setContentTitle(message.title)
			.setContentText(message.body)
			.setStyle(NotificationCompat.BigTextStyle().bigText(message.body))
			.setPriority(NotificationCompat.PRIORITY_HIGH)
			.setContentIntent(pendingIntent)
			.setAutoCancel(true)
			.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
			.setVibrate(longArrayOf(0, 500, 200, 500))
			.build()
		
		val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE)
				as NotificationManager
		notificationManager.notify(NOTIFICATION_ID, notification)
	}
	
	private fun createNotificationChannel() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			val channel = NotificationChannel(
				CHANNEL_ID,
				"Recordatorios de Estudio",
				NotificationManager.IMPORTANCE_HIGH
			).apply {
				description = "Notificaciones para recordarte estudiar"
				enableVibration(true)
				enableLights(true)
			}
			
			val notificationManager = context.getSystemService(NotificationManager::class.java)
			notificationManager.createNotificationChannel(channel)
		}
	}
	
	//Para pruebas
	fun sendTestNotification() {
		createNotificationChannel()
		
		val intent = Intent(context, MainActivity::class.java).apply {
			flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
		}
		
		val pendingIntent = PendingIntent.getActivity(
			context,
			0,
			intent,
			PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
		)
		
		val notification = NotificationCompat.Builder(context, CHANNEL_ID)
			.setSmallIcon(R.drawable.ic_studivo_logo_2) // reemplaza con tu ícono
			.setContentTitle("Notificación de prueba")
			.setContentText("¡Esta es una notificación de prueba de Studivo!")
			.setStyle(NotificationCompat.BigTextStyle().bigText("¡Esta es una notificación de prueba de Studivo!"))
			.setPriority(NotificationCompat.PRIORITY_HIGH)
			.setContentIntent(pendingIntent)
			.setAutoCancel(true)
			.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
			.setVibrate(longArrayOf(0, 500, 200, 500))
			.build()
		
		val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
		notificationManager.notify(9999, notification) // id diferente al de recordatorios reales
	}
}