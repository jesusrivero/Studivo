package com.example.studivo.presentation.navegacion

import kotlinx.serialization.Serializable

object AppRoutes {
	
	@Serializable
	data object MetronomeScreen
	
	@Serializable
	data object MainScreen
	
	@Serializable
	data object SplashScreen
	
	@Serializable
	data object SettingsScreen
	
	@Serializable
	data object CreateRoutineScreen
	
	@Serializable
	data class EditedRoutineScreen(val routineId: String)
	
	@Serializable
	data class RoutinePlaybackScreen(val routineId: String)
	
	@Serializable
	data object QRScannerScreen
	
	@Serializable
	data object HistoryScreen
	
	@Serializable
	data object NotificationSettingsScreen
}