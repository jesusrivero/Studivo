package com.example.studivo.presentation.ui



import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
	navController: NavController
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
				// Indicador de páginas mejorado
				Row(
					horizontalArrangement = Arrangement.Center,
					modifier = Modifier.fillMaxWidth()
				) {
					repeat(4) { index ->
						Box(
							modifier = Modifier
								.size(if (index == 0) 12.dp else 8.dp)
								.clip(CircleShape)
								.background(
									if (index == 0) Color(0xFF1976D2)
									else Color.LightGray.copy(alpha = 0.4f)
								)
						)
						if (index < 3) Spacer(modifier = Modifier.width(8.dp))
					}
				}
				
				Spacer(modifier = Modifier.height(24.dp))
				
				// Visualizador de beats circular mejorado
				Box(
					contentAlignment = Alignment.Center,
					modifier = Modifier
						.size(160.dp)
						.background(
							color = Color(0xFF1976D2).copy(alpha = 0.05f),
							shape = CircleShape
						)
						.border(
							width = 3.dp,
							color = if (isPlaying) Color(0xFF1976D2) else Color(0xFF1976D2).copy(alpha = 0.3f),
							shape = CircleShape
						)
				) {
					Column(
						horizontalAlignment = Alignment.CenterHorizontally
					) {
						// BPM principal
						Text(
							text = bpm.toInt().toString(),
							style = MaterialTheme.typography.displayLarge.copy(
								fontWeight = FontWeight.ExtraBold,
								fontSize = 44.sp,
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
							val beats = when (selectedSignature) {
								"2/4" -> 2
								"3/4" -> 3
								else -> 4
							}
							repeat(beats) { index ->
								Box(
									modifier = Modifier
										.size(10.dp)
										.clip(CircleShape)
										.background(
											if (isPlaying && currentBeat == index + 1)
												Color(0xFF4CAF50)
											else Color.LightGray.copy(alpha = 0.5f)
										)
								)
							}
						}
					}
				}
				
				Spacer(modifier = Modifier.height(24.dp))
				
				// Controles de BPM mejorados
				Row(
					horizontalArrangement = Arrangement.SpaceBetween,
					verticalAlignment = Alignment.CenterVertically,
					modifier = Modifier.fillMaxWidth()
				) {
					// Botón -10
					OutlinedButton(
						onClick = {
							bpm = (bpm - 10f).coerceAtLeast(40f)
						},
						modifier = Modifier.size(48.dp),
						shape = CircleShape,
						colors = ButtonDefaults.outlinedButtonColors(
							contentColor = Color(0xFF1976D2)
						)
					) {
						Text("-10", style = MaterialTheme.typography.labelSmall)
					}
					
					// Botón -1
					OutlinedButton(
						onClick = {
							bpm = (bpm - 1f).coerceAtLeast(40f)
						},
						modifier = Modifier.size(40.dp),
						shape = CircleShape,
						colors = ButtonDefaults.outlinedButtonColors(
							contentColor = Color(0xFF1976D2)
						)
					) {
						Text("-1", style = MaterialTheme.typography.labelSmall)
					}
					
					// Slider
					Slider(
						value = bpm,
						onValueChange = { bpm = it },
						valueRange = 40f..240f,
						modifier = Modifier.weight(1f).padding(horizontal = 16.dp),
						colors = SliderDefaults.colors(
							thumbColor = Color(0xFF1976D2),
							activeTrackColor = Color(0xFF1976D2),
							inactiveTrackColor = Color.LightGray.copy(alpha = 0.3f)
						)
					)
					
					// Botón +1
					OutlinedButton(
						onClick = {
							bpm = (bpm + 1f).coerceAtMost(240f)
						},
						modifier = Modifier.size(40.dp),
						shape = CircleShape,
						colors = ButtonDefaults.outlinedButtonColors(
							contentColor = Color(0xFF1976D2)
						)
					) {
						Text("+1", style = MaterialTheme.typography.labelSmall)
					}
					
					// Botón +10
					OutlinedButton(
						onClick = {
							bpm = (bpm + 10f).coerceAtMost(240f)
						},
						modifier = Modifier.size(48.dp),
						shape = CircleShape,
						colors = ButtonDefaults.outlinedButtonColors(
							contentColor = Color(0xFF1976D2)
						)
					) {
						Text("+10", style = MaterialTheme.typography.labelSmall)
					}
				}
				
				Spacer(modifier = Modifier.height(16.dp))
				
				// Time Signature mejorado
				Text(
					text = "Time Signature",
					style = MaterialTheme.typography.bodyLarge.copy(
						fontWeight = FontWeight.SemiBold
					),
					color = Color.Black,
					modifier = Modifier.align(Alignment.Start)
				)
				
				Spacer(modifier = Modifier.height(8.dp))
				
				Row(
					horizontalArrangement = Arrangement.spacedBy(12.dp),
					modifier = Modifier.fillMaxWidth()
				) {
					listOf("2/4", "3/4", "4/4").forEach { sig ->
						FilterChip(
							onClick = {
								selectedSignature = sig
								currentBeat = 1 // Reset beat counter
							},
							label = {
								Text(
									text = sig,
									style = MaterialTheme.typography.bodyLarge.copy(
										fontWeight = FontWeight.SemiBold
									)
								)
							},
							selected = selectedSignature == sig,
							modifier = Modifier.weight(1f),
							colors = FilterChipDefaults.filterChipColors(
								selectedContainerColor = Color(0xFF1976D2).copy(alpha = 0.15f),
								selectedLabelColor = Color(0xFF1976D2),
								containerColor = Color(0xFFF5F5F5)
							)
						)
					}
				}
				
				Spacer(modifier = Modifier.weight(1f))
			}
			
			// Botón Start/Stop original mejorado
			Button(
				onClick = {
					isPlaying = !isPlaying
					if (!isPlaying) currentBeat = 1
				},
				modifier = Modifier
					.align(Alignment.BottomCenter)
					.padding(24.dp)
					.fillMaxWidth()
					.height(56.dp),
				shape = RoundedCornerShape(28.dp),
				colors = ButtonDefaults.buttonColors(
					containerColor = if (isPlaying) Color(0xFFE53935) else Color(0xFF1976D2),
					contentColor = Color.White
				)
			) {
				Row(
					horizontalArrangement = Arrangement.Center,
					verticalAlignment = Alignment.CenterVertically
				) {
					Icon(
						imageVector = if (isPlaying) Icons.Default.Stop else Icons.Default.PlayArrow,
						contentDescription = if (isPlaying) "Stop" else "Start",
						modifier = Modifier.size(24.dp)
					)
					Spacer(modifier = Modifier.width(8.dp))
					Text(
						text = if (isPlaying) "Stop" else "Start",
						style = MaterialTheme.typography.bodyLarge.copy(
							fontWeight = FontWeight.Bold
						)
					)
				}
			}
		}
	}
}