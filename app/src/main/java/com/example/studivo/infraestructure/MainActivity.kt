package com.example.studivo.infraestructure

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.example.studivo.domain.viewmodels.ThemeViewModel
import com.example.studivo.presentation.navegacion.NavigationHost
import com.example.studivo.presentation.theme.StudivoTheme

import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		enableEdgeToEdge()
		setContent {
			val themeViewModel: ThemeViewModel = hiltViewModel()
			val isDarkMode by themeViewModel.isDarkMode.collectAsState()
			
			StudivoTheme() {
				val navController = rememberNavController()
				NavigationHost(navController = navController)
			}
		}
	}
}
