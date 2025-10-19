package com.example.studivo.presentation.navegacion


import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.studivo.presentation.splash.SplashScreen
import com.example.studivo.presentation.ui.HistoryScreen
import com.example.studivo.presentation.ui.MetronomeScreen
import com.example.studivo.presentation.ui.SettingsScreen
import com.example.studivo.presentation.ui.home.MainScreen
import com.example.studivo.presentation.ui.home.QRScannerScreen
import com.example.studivo.presentation.ui.routine.CreateRoutineScreen
import com.example.studivo.presentation.ui.routine.EditedRoutineScreen
import com.example.studivo.presentation.ui.routine.RoutinePlaybackScreen
import com.example.studivo.presentation.ui.settings.NotificationSettingsScreen

@Composable
fun NavigationHost(
	navController: NavHostController = rememberNavController(),
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
		
		
		composable<AppRoutes.CreateRoutineScreen> {
			CreateRoutineScreen(
				navController = navController,
			)
		}
		
		composable(
			route = "${AppRoutes.EditedRoutineScreen}/{routineId}",
			arguments = listOf(navArgument("routineId") { type = NavType.StringType })
		) { backStackEntry ->
			val routineId = backStackEntry.arguments?.getString("routineId") ?: ""
			EditedRoutineScreen(navController = navController, routineId = routineId)
		}
		
		
		composable(
			route = "${AppRoutes.RoutinePlaybackScreen}/{routineId}",
			arguments = listOf(navArgument("routineId") { type = NavType.StringType })
		) { backStackEntry ->
			val routineId = backStackEntry.arguments?.getString("routineId") ?: ""
			RoutinePlaybackScreen(navController = navController, routineId = routineId)
		}
		
		composable<AppRoutes.QRScannerScreen> {
			QRScannerScreen(navController = navController)
		}
		composable<AppRoutes.HistoryScreen> {
			HistoryScreen(navController = navController)
		}
		composable<AppRoutes.NotificationSettingsScreen> {
			NotificationSettingsScreen(navController = navController,onNavigateBack = { navController.popBackStack()})
		}
		
	}
	
}
