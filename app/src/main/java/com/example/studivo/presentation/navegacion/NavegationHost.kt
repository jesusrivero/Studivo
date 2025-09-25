package com.example.studivo.presentation.navegacion

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.studivo.presentation.splash.SplashScreen
import com.example.studivo.presentation.ui.CreateFaseScreen
import com.example.studivo.presentation.ui.CreateRoutineScreen
import com.example.studivo.presentation.ui.EditedFaseScreen
import com.example.studivo.presentation.ui.EditedRoutineScreen
import com.example.studivo.presentation.ui.MainScreen
import com.example.studivo.presentation.ui.MetronomeScreen
import com.example.studivo.presentation.ui.StatisticsScreen
import com.example.studivo.presentation.ui.SettingsScreen

@Composable
fun NavigationHost(
	navController: NavHostController = rememberNavController()
) {
	NavHost(
		navController = navController,
		startDestination = AppRoutes.SplashScreen
	) {
		composable<AppRoutes.MainScreen> {
			MainScreen(navController = navController)
		}
		
		composable<AppRoutes.MetronomeScreen> {
			MetronomeScreen(navController = navController)
		}
		
		composable<AppRoutes.SplashScreen> {
			SplashScreen(navController = navController)
		}
		composable<AppRoutes.SettingsScreen> {
			 SettingsScreen(navController = navController)
		}
		
		composable<AppRoutes.StatisticsScreen> {
			 StatisticsScreen(navController = navController)
		}
		
		composable<AppRoutes.CreateRoutineScreen> {
			CreateRoutineScreen(navController = navController)
		}
		composable<AppRoutes.EditedRoutineScreen> {
			EditedRoutineScreen(navController= navController)
		}
		
		composable<AppRoutes.CreateFaseScreen>{
			CreateFaseScreen(navController = navController)
		}
		composable<AppRoutes.EditedFaseScreen>{
			EditedFaseScreen(navController=navController)
		}
	}
}
