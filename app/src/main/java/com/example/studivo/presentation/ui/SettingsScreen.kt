package com.example.studivo.presentation.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.studivo.presentation.ui.commons.BottomNavigationBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
	navController: NavController,
) {
	SettingsScreenContent(navController = navController)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreenContent(
	navController: NavController,
) {
	
	Scaffold(
		topBar = {
			CenterAlignedTopAppBar(
				title = {
					Text("Ajustes", style = MaterialTheme.typography.titleMedium)
				}
			)
		},
		bottomBar = {
			BottomNavigationBar(navController)
		}
	) { innerPadding ->
		Box(modifier = Modifier.padding(innerPadding)){
			Text(text = "hola")
		}
	}
}