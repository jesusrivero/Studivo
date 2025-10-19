package com.example.studivo.domain.services


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.studivo.domain.model.helpers.NotificationHelper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class NotificationAlarmReceiver : BroadcastReceiver() {
	
	@Inject
	lateinit var notificationHelper: NotificationHelper
	
	@Inject
	lateinit var notificationScheduler: NotificationScheduler
	
	override fun onReceive(context: Context, intent: Intent?) {
		// Usar coroutine para operaciones asíncronas
		CoroutineScope(Dispatchers.IO).launch {
			notificationHelper.checkAndSendReminder()
			
			// Re-programar la siguiente alarma
			notificationScheduler.scheduleAlarmManager()
		}
	}
}
