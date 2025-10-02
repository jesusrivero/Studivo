package com.example.studivo.presentation.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.studivo.R
import com.example.studivo.presentation.navegacion.AppRoutes
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {
	Box(
		modifier = Modifier
			.fillMaxSize()
			.background(MaterialTheme.colorScheme.surface),
		contentAlignment = Alignment.Center
	) {
		Image(
			painter = painterResource(id = R.drawable.ic_studivo_logo_2),
			contentDescription = "Logo",
			modifier = Modifier
				.size(200.dp)
		)
	}
	
	LaunchedEffect(Unit) {
		delay(1000)
		navController.navigate(AppRoutes.MainScreen) {
			popUpTo(AppRoutes.SplashScreen) { inclusive = true }
		}
	}
}

