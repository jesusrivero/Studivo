package com.example.studivo.domain.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class BootReceiver : BroadcastReceiver() {
	
	@Inject
	lateinit var notificationScheduler: NotificationScheduler
	
	override fun onReceive(context: Context, intent: Intent?) {
		if (intent?.action == Intent.ACTION_BOOT_COMPLETED ||
			intent?.action == Intent.ACTION_MY_PACKAGE_REPLACED) {
			
			// Re-programar notificaciones tras reinicio del dispositivo
			notificationScheduler.scheduleAllNotifications()
		}
	}
}