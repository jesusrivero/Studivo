
package com.example.studivo.domain.services

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.work.*
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Calendar
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationScheduler @Inject constructor(
	@ApplicationContext private val context: Context,
	private val notificationPreferences: NotificationPreferences
) {
	
	companion object {
		const val WORKER_TAG = "studivo_daily_reminder"
		const val ALARM_REQUEST_CODE = 1001
	}
	
	/**
	 * Inicializa TODOS los mecanismos de notificaciones para máxima persistencia
	 */
	fun scheduleAllNotifications() {
		scheduleWorkManager()
		scheduleAlarmManager()
		schedulePeriodicCheck()
	}
	
	/**
	 * WorkManager - Principal mecanismo (Android 6.0+)
	 * Se ejecuta incluso si la app está cerrada
	 */
	private fun scheduleWorkManager() {
		val constraints = Constraints.Builder()
			.setRequiresBatteryNotLow(false) // ⚡ NO requiere batería alta
			.setRequiresCharging(false)
			.build()
		
		val dailyWorkRequest = PeriodicWorkRequestBuilder<DailyReminderWorker>(
			repeatInterval = 1,
			repeatIntervalTimeUnit = TimeUnit.HOURS
		)
			.setConstraints(constraints)
			.setBackoffCriteria(
				BackoffPolicy.LINEAR,
				WorkRequest.MIN_BACKOFF_MILLIS,  // ✅ Cambiar aquí
				TimeUnit.MILLISECONDS
			)
			.addTag(WORKER_TAG)
			.build()
		
		WorkManager.getInstance(context)
			.enqueueUniquePeriodicWork(
				WORKER_TAG,
				ExistingPeriodicWorkPolicy.KEEP,
				dailyWorkRequest
			)
	}
	
	/**
	 * AlarmManager - Mecanismo de respaldo CRÍTICO
	 * Más persistente que WorkManager en baterías bajas
	 */
	internal fun scheduleAlarmManager() {
		val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
		val intent = Intent(context, NotificationAlarmReceiver::class.java)
		val pendingIntent = PendingIntent.getBroadcast(
			context,
			ALARM_REQUEST_CODE,
			intent,
			PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
		)
		
		// Programar para las horas preferidas del usuario
		val notificationTimes = notificationPreferences.getNotificationTimes()
		
		notificationTimes.forEachIndexed { index, time ->
			val calendar = Calendar.getInstance().apply {
				timeInMillis = System.currentTimeMillis()
				set(Calendar.HOUR_OF_DAY, time.first)
				set(Calendar.MINUTE, time.second)
				set(Calendar.SECOND, 0)
				
				// Si la hora ya pasó hoy, programar para mañana
				if (timeInMillis <= System.currentTimeMillis()) {
					add(Calendar.DAY_OF_YEAR, 1)
				}
			}
			
			val alarmPendingIntent = PendingIntent.getBroadcast(
				context,
				ALARM_REQUEST_CODE + index,
				intent,
				PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
			)
			
			// 🔥 setExactAndAllowWhileIdle - SE EJECUTA INCLUSO CON BATERÍA BAJA
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
				alarmManager.setExactAndAllowWhileIdle(
					AlarmManager.RTC_WAKEUP,
					calendar.timeInMillis,
					alarmPendingIntent
				)
			} else {
				alarmManager.setExact(
					AlarmManager.RTC_WAKEUP,
					calendar.timeInMillis,
					alarmPendingIntent
				)
			}
		}
	}
	
	/**
	 * Chequeo periódico adicional cada 6 horas
	 */
	private fun schedulePeriodicCheck() {
		val checkWorkRequest = PeriodicWorkRequestBuilder<PeriodicCheckWorker>(
			6, TimeUnit.HOURS
		)
			.setConstraints(
				Constraints.Builder()
					.setRequiresBatteryNotLow(false)
					.build()
			)
			.build()
		
		WorkManager.getInstance(context)
			.enqueueUniquePeriodicWork(
				"periodic_check",
				ExistingPeriodicWorkPolicy.KEEP,
				checkWorkRequest
			)
	}
	
	fun cancelAllNotifications() {
		// Cancelar WorkManager
		WorkManager.getInstance(context).cancelAllWorkByTag(WORKER_TAG)
		
		// Cancelar AlarmManager
		val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
		val intent = Intent(context, NotificationAlarmReceiver::class.java)
		val pendingIntent = PendingIntent.getBroadcast(
			context,
			ALARM_REQUEST_CODE,
			intent,
			PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
		)
		alarmManager.cancel(pendingIntent)
	}
}