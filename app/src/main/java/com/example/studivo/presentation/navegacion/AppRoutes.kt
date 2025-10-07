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
	
//	@Serializable
//	data object StatisticsScreen
	
	// 👇 pantalla única para crear/editar rutina
	@Serializable
	data object CreateRoutineScreen
	
	
	@Serializable
	data class EditedRoutineScreen(val routineId: String)
	
	@Serializable
	data object CreateFaseScreen
	
	@Serializable
	data class EditedFaseScreen(val faseId: String)
	
	@Serializable
	data class RoutinePlaybackScreen(val routineId: String)
	

}