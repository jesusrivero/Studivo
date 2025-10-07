package com.example.studivo.presentation.ui



import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.studivo.domain.viewmodels.MetronomeViewModel
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
	viewModel: MetronomeViewModel = hiltViewModel()
) {
	val uiState by viewModel.uiState.collectAsState()
	
	Scaffold(
		topBar = {
			CenterAlignedTopAppBar(
				title = {
					Text(
						text = "Metronome",
						style = MaterialTheme.typography.titleMedium
					)
				},
				navigationIcon = {
					IconButton(onClick = { navController.navigateUp() }) {
						Icon(
							imageVector = Icons.Default.ArrowBack,
							contentDescription = "Back"
						)
					}
				}
			)
		}
	) { innerPadding ->
		Box(
			modifier = Modifier
				.fillMaxSize()
				.padding(innerPadding)
		) {
			Column(
				modifier = Modifier
					.fillMaxSize()
					.padding(horizontal = 24.dp, vertical = 16.dp),
				horizontalAlignment = Alignment.CenterHorizontally
			) {
				
				// Visualizador de beats circular
				Box(
					contentAlignment = Alignment.Center,
					modifier = Modifier
						.size(250.dp)
						.background(
							color = Color(0xFF1976D2).copy(alpha = 0.05f),
							shape = CircleShape
						)
						.border(
							width = 3.dp,
							color = if (uiState.isPlaying)
								Color(0xFF1976D2)
							else
								Color(0xFF1976D2).copy(alpha = 0.3f),
							shape = CircleShape
						)
				) {
					Column(
						horizontalAlignment = Alignment.CenterHorizontally
					) {
						// BPM principal
						Text(
							text = uiState.bpm.toString(),
							style = MaterialTheme.typography.displayLarge.copy(
								fontWeight = FontWeight.ExtraBold,
								fontSize = 60.sp,
								color = Color(0xFF1976D2)
							)
						)
						Text(
							text = "BPM",
							style = MaterialTheme.typography.bodyLarge.copy(
								fontWeight = FontWeight.Medium,
								letterSpacing = 2.sp
							),
							color = Color.Gray
						)
						
						Spacer(modifier = Modifier.height(8.dp))
						
						// Indicador de beat actual
						Row(
							horizontalArrangement = Arrangement.spacedBy(6.dp)
						) {
							val beats = parseTimeSignature(uiState.timeSignature)
							repeat(beats) { index ->
								Box(
									modifier = Modifier
										.size(10.dp)
										.clip(CircleShape)
										.background(
											if (uiState.isPlaying && uiState.currentBeat == index + 1)
												Color(0xFF4CAF50)
											else
												Color.LightGray.copy(alpha = 0.5f)
										)
								)
							}
						}
					}
				}
				
				Spacer(modifier = Modifier.height(150.dp))
				
				// Controles de BPM
				Row(
					horizontalArrangement = Arrangement.SpaceBetween,
					verticalAlignment = Alignment.CenterVertically,
					modifier = Modifier.fillMaxWidth()
				) {
					OutlinedButton(
						onClick = { viewModel.updateBpm((uiState.bpm - 10).coerceAtLeast(40)) },
						modifier = Modifier
							.height(48.dp)
							.width(56.dp), // Cambiado de size a width/height
						shape = CircleShape,
						contentPadding = PaddingValues(0.dp) // Remueve padding interno
					) {
						Text(
							text = "-10",
							style = MaterialTheme.typography.labelSmall,
							fontSize = 15.sp // Asegura que el texto sea visible
						)
					}
					
					OutlinedButton(
						onClick = { viewModel.updateBpm((uiState.bpm - 1).coerceAtLeast(40)) },
						modifier = Modifier.size(44.dp),
						shape = CircleShape,
						contentPadding = PaddingValues(0.dp)
					) {
						Text(
							text = "-1",
							style = MaterialTheme.typography.labelMedium,
							fontSize = 15.sp // Asegura que el texto sea visible
						)
					}
					
					Slider(
						value = uiState.bpm.toFloat(),
						onValueChange = { viewModel.updateBpm(it.toInt()) },
						valueRange = 40f..240f,
						modifier = Modifier.weight(1f).padding(horizontal = 16.dp),
						colors = SliderDefaults.colors(
							thumbColor = Color(0xFF1976D2),
							activeTrackColor = Color(0xFF1976D2)
						)
					)
					
					OutlinedButton(
						onClick = { viewModel.updateBpm((uiState.bpm + 1).coerceAtMost(240)) },
						modifier = Modifier.size(44.dp),
						shape = CircleShape,
						contentPadding = PaddingValues(0.dp)
					) {
						Text(
							text = "+1",
							style = MaterialTheme.typography.labelMedium,
							fontSize = 15.sp // Asegura que el texto sea visible
						)
					}
					
					OutlinedButton(
						onClick = { viewModel.updateBpm((uiState.bpm + 10).coerceAtMost(240)) },
						modifier = Modifier
							.height(48.dp)
							.width(56.dp),
						shape = CircleShape,
						contentPadding = PaddingValues(0.dp)
					) {
						Text(
							text = "+10",
							style = MaterialTheme.typography.labelSmall,
							fontSize = 15.sp // Asegura que el texto sea visible
						)
					}
				}
				
				Spacer(modifier = Modifier.height(16.dp))
				
				// Time Signature
				Text(
					text = "Time Signature",
					style = MaterialTheme.typography.bodyLarge.copy(
						fontWeight = FontWeight.SemiBold
					),
					modifier = Modifier.align(Alignment.Start)
				)
				
				Spacer(modifier = Modifier.height(8.dp))
				
				// Chips de compases
				Row(
					horizontalArrangement = Arrangement.spacedBy(8.dp),
					modifier = Modifier.fillMaxWidth()
				) {
					listOf("2/4", "3/4", "4/4", "6/8", "7/8").forEach { sig ->
						FilterChip(
							onClick = { viewModel.updateTimeSignature(sig) },
							label = { Text(sig) },
							selected = uiState.timeSignature == sig,
							modifier = Modifier.weight(1f),
							colors = FilterChipDefaults.filterChipColors(
								selectedContainerColor = Color(0xFF1976D2).copy(alpha = 0.15f),
								selectedLabelColor = Color(0xFF1976D2)
							)
						)
					}
				}
				
				Spacer(modifier = Modifier.weight(1f))
			}
			
			// Botón Start/Stop
			Button(
				onClick = { viewModel.togglePlayPause() },
				modifier = Modifier
					.align(Alignment.BottomCenter)
					.padding(24.dp)
					.fillMaxWidth()
					.height(56.dp),
				shape = RoundedCornerShape(28.dp),
				colors = ButtonDefaults.buttonColors(
					containerColor = if (uiState.isPlaying)
						Color(0xFFE53935)
					else
						Color(0xFF1976D2)
				)
			) {
				Row(
					horizontalArrangement = Arrangement.Center,
					verticalAlignment = Alignment.CenterVertically
				) {
					Icon(
						imageVector = if (uiState.isPlaying) Icons.Default.Stop else Icons.Default.PlayArrow,
						contentDescription = null,
						modifier = Modifier.size(24.dp)
					)
					Spacer(modifier = Modifier.width(8.dp))
					Text(
						text = if (uiState.isPlaying) "Stop" else "Start",
						style = MaterialTheme.typography.bodyLarge.copy(
							fontWeight = FontWeight.Bold
						)
					)
				}
			}
		}
	}
}

// Helper para parsear compás
private fun parseTimeSignature(timeSignature: String): Int {
	return timeSignature.split("/").firstOrNull()?.toIntOrNull() ?: 4
}