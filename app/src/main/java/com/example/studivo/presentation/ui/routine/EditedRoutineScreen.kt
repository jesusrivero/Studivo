package com.example.studivo.presentation.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.studivo.presentation.ui.commons.BottomNavigationBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditedRoutineScreen(
	navController: NavController,
) {
	EditedRoutineScreenContent(navController = navController)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditedRoutineScreenContent(
	navController: NavController,
) {
	
	Scaffold(
		topBar = {
			CenterAlignedTopAppBar(
				title = {
					Text("Editar rutina", style = MaterialTheme.typography.titleMedium)
				},
				navigationIcon = {
					IconButton(onClick = { navController.popBackStack() }) {
						Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
					}
				},
			)
		}
	) { innerPadding ->
		Box(modifier = Modifier.padding(innerPadding)){
			Text(text = "hola")
		}
	}
}
