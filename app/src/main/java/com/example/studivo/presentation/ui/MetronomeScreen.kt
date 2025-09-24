package com.example.studivo.presentation.ui


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.studivo.presentation.ui.commons.BottomNavigationBar


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MetronomeScreen(
	navController: NavController,
) {
	MetronomeScreenContent(navController = navController)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MetronomeScreenContent(
	navController: NavController,
) {
	var bpm by remember { mutableStateOf(120f) }
	var selectedSignature by remember { mutableStateOf("4/4") }
	var isPlaying by remember { mutableStateOf(false) }
	var currentBeat by remember { mutableStateOf(1) }
	
	Scaffold(
		topBar = {
			CenterAlignedTopAppBar(
				title = {
					Text(
						text = "Metronome",
						style = MaterialTheme.typography.titleMedium
					)
				}
			)
		},
		bottomBar = {
			BottomNavigationBar(navController)
		}
	) { innerPadding ->
		Box(modifier = Modifier.padding(innerPadding)) {
			
			Text(text = "BPM: ${bpm.toInt()}")
		}
	}
}