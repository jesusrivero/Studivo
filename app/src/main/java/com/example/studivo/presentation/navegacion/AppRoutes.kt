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
	
	@Serializable
	data object CreateRoutineScreen
	
	@Serializable
	data object EditedRoutineScreen
	
	@Serializable
	data object CreateFaseScreen
	
	@Serializable
	data object EditedFaseScreen
}