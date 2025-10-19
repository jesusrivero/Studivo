package com.example.studivo.domain.services



import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.studivo.domain.model.helpers.NotificationHelper
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject



@HiltWorker
class DailyReminderWorker @AssistedInject constructor(
	@Assisted context: Context,
	@Assisted params: WorkerParameters,
	private val notificationHelper: NotificationHelper,
	private val notificationScheduler: NotificationScheduler
) : CoroutineWorker(context, params) {
	
	override suspend fun doWork(): Result {
		return try {
			notificationHelper.checkAndSendReminder()
			
			// Re-programar AlarmManager para el próximo día
			notificationScheduler.scheduleAlarmManager()
			
			Result.success()
		} catch (e: Exception) {
			Result.retry()
		}
	}
}

