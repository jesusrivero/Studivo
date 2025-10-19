package com.example.studivo.infraestructure


import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.example.studivo.domain.services.NotificationScheduler
import com.example.studivo.domain.viewmodels.ThemeViewModel
import com.example.studivo.presentation.navegacion.NavigationHost
import com.example.studivo.presentation.theme.StudivoTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
	
	@Inject
	lateinit var notificationScheduler: NotificationScheduler
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		WindowCompat.setDecorFitsSystemWindows(window, false)
		
		// ✅ Inicializar notificaciones al abrir la app
		notificationScheduler.scheduleAllNotifications()
		
		// ✅ Solicitar permisos en Android 13+
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
			requestNotificationPermission()
		}
		
		// ✅ Solicitar desactivar optimización de batería
		requestIgnoreBatteryOptimization()
		
		// ✅ UI principal
		setContent {
			val themeViewModel: ThemeViewModel = hiltViewModel()
			val isDarkMode by themeViewModel.isDarkMode.collectAsState()
			
			StudivoTheme(isDarkTheme = isDarkMode) {
				val navController = rememberNavController()
				NavigationHost(navController = navController)
			}
		}
	}
	
	@RequiresApi(Build.VERSION_CODES.TIRAMISU)
	private fun requestNotificationPermission() {
		if (ContextCompat.checkSelfPermission(
				this,
				Manifest.permission.POST_NOTIFICATIONS
			) != PackageManager.PERMISSION_GRANTED
		) {
			ActivityCompat.requestPermissions(
				this,
				arrayOf(Manifest.permission.POST_NOTIFICATIONS),
				1001
			)
		}
	}
	
	private fun requestIgnoreBatteryOptimization() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
			val packageName = packageName
			
			if (!powerManager.isIgnoringBatteryOptimizations(packageName)) {
				val intent =
					Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
						data = Uri.parse("package:$packageName")
					}
				try {
					startActivity(intent)
				} catch (e: Exception) {
					// El usuario puede cancelar o el dispositivo puede no permitirlo
				}
			}
		}
	}
}