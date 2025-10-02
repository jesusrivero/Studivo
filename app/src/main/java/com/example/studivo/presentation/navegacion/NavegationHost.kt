package com.example.studivo.presentation.navegacion

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.toRoute
import com.example.studivo.presentation.splash.SplashScreen
import com.example.studivo.presentation.ui.MetronomeScreen
import com.example.studivo.presentation.ui.SettingsScreen
import com.example.studivo.presentation.ui.StatisticsScreen
import com.example.studivo.presentation.ui.home.MainScreen
//import com.example.studivo.presentation.ui.phases.CreateFaseScreen
//import com.example.studivo.presentation.ui.phases.EditedFaseScreen
import com.example.studivo.presentation.ui.routine.CreateRoutineScreen
import com.example.studivo.presentation.ui.routine.DetailRoutineScreen
import com.example.studivo.presentation.ui.routine.EditedRoutineScreen

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
		
		composable<AppRoutes.StatisticsScreen> {
			StatisticsScreen(navController = navController)
		}
		
		
		// 👇 unificada: crear o editar según routineId
		composable<AppRoutes.CreateRoutineScreen> {
			CreateRoutineScreen(navController = navController,
				)
		}
		
		composable(
			route = "${AppRoutes.EditedRoutineScreen}/{routineId}",
			arguments = listOf(navArgument("routineId") { type = NavType.StringType })
		) { backStackEntry ->
			val routineId = backStackEntry.arguments?.getString("routineId") ?: ""
			EditedRoutineScreen(navController = navController, routineId = routineId)
		}
		
//
//		composable<AppRoutes.CreateFaseScreen> {
//			CreateFaseScreen(navController = navController)
//		}
		
		
		
//		composable(
//			route = "${AppRoutes.EditedFaseScreen}/{faseId}",
//			arguments = listOf(navArgument("faseId") { type = NavType.StringType })
//		) { backStackEntry ->
//			val faseId = backStackEntry.arguments?.getString("faseId") ?: ""
//			EditedFaseScreen(navController = navController, faseId = faseId)
//		}

		
		
		composable<AppRoutes.DetailRoutineScreen> { backStackEntry ->
			val args = backStackEntry.toRoute<AppRoutes.DetailRoutineScreen>()
			DetailRoutineScreen(
				navController = navController,
				routineId = args.routineId
			)
		}
		
	}
}
