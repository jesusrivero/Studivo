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
	data object StatisticsScreen
	
	
}