package com.example.studivo.domain.viewmodels


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studivo.domain.model.helpers.NotificationHelper
import com.example.studivo.domain.services.NotificationPreferences
import com.example.studivo.domain.services.NotificationScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationSettingsViewModel @Inject constructor(
	private val notificationPreferences: NotificationPreferences,
	private val notificationScheduler: NotificationScheduler,
	private val notificationHelper: NotificationHelper
) : ViewModel() {
	
	private val _notificationsEnabled = MutableStateFlow(false)
	val notificationsEnabled: StateFlow<Boolean> = _notificationsEnabled.asStateFlow()
	
	private val _notificationTimes = MutableStateFlow<List<Pair<Int, Int>>>(emptyList())
	val notificationTimes: StateFlow<List<Pair<Int, Int>>> = _notificationTimes.asStateFlow()
	
	init {
		loadSettings()
	}
	
	private fun loadSettings() {
		_notificationsEnabled.value = notificationPreferences.areNotificationsEnabled()
		_notificationTimes.value = notificationPreferences.getNotificationTimes()
	}
	
	fun setNotificationsEnabled(enabled: Boolean) {
		_notificationsEnabled.value = enabled
		notificationPreferences.setNotificationsEnabled(enabled)
		
		if (enabled) {
			notificationScheduler.scheduleAllNotifications()
		} else {
			notificationScheduler.cancelAllNotifications()
		}
	}
	
	fun addNotificationTime(hour: Int, minute: Int) {
		val currentTimes = _notificationTimes.value.toMutableList()
		
		// Evitar duplicados
		if (currentTimes.none { it.first == hour && it.second == minute }) {
			currentTimes.add(Pair(hour, minute))
			currentTimes.sortBy { it.first * 60 + it.second }
			
			_notificationTimes.value = currentTimes
			notificationPreferences.setNotificationTimes(currentTimes)
			
			// Re-programar notificaciones
			notificationScheduler.scheduleAllNotifications()
		}
	}
	
	fun removeNotificationTime(index: Int) {
		val currentTimes = _notificationTimes.value.toMutableList()
		if (index in currentTimes.indices) {
			currentTimes.removeAt(index)
			_notificationTimes.value = currentTimes
			notificationPreferences.setNotificationTimes(currentTimes)
			
			// Re-programar notificaciones
			notificationScheduler.scheduleAllNotifications()
		}
	}
	
	fun sendTestNotification() {
		viewModelScope.launch {
			notificationHelper.sendTestNotification()
//			notificationHelper.checkAndSendReminder()
		}
	}
}