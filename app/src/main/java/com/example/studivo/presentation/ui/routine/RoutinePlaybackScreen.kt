package com.example.studivo.presentation.ui.routine


import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.studivo.domain.model.Phase
import com.example.studivo.domain.model.PlaybackEvent
import com.example.studivo.domain.model.PlaybackUiState
import com.example.studivo.domain.model.RoutinePlaybackState
import com.example.studivo.domain.model.calculateTotalRepetitions
import com.example.studivo.domain.viewmodels.RoutinePlaybackViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoutinePlaybackScreen(
	navController: NavController,
	routineId: String,
	viewModel: RoutinePlaybackViewModel = hiltViewModel()
) {
	val uiState by viewModel.uiState.collectAsState()
	
	Box(modifier = Modifier.fillMaxSize()) {
		
		// 🔹 UI normal de reproducción (siempre debajo)
		if (uiState is PlaybackUiState.Playing) {
			val playbackState = (uiState as PlaybackUiState.Playing).state
			RoutinePlaybackContent(
				playbackState = playbackState,
				onPlayPause = { viewModel.onEvent(PlaybackEvent.PlayPause) },
				onNextPhase = { viewModel.onEvent(PlaybackEvent.NextPhase) },
				onNextRepetition = { viewModel.onEvent(PlaybackEvent.NextRepetition) },
				onRepeatPhase = { viewModel.onEvent(PlaybackEvent.RepeatPhase) },
				onClose = { navController.popBackStack() }
			)
		} else if (uiState is PlaybackUiState.Loading) {
			Box(
				modifier = Modifier.fillMaxSize(),
				contentAlignment = Alignment.Center
			) {
				CircularProgressIndicator()
			}
		} else if (uiState is PlaybackUiState.Error) {
			val state = uiState as PlaybackUiState.Error
			Box(
				modifier = Modifier
					.fillMaxSize()
					.padding(24.dp),
				contentAlignment = Alignment.Center
			) {
				Column(
					horizontalAlignment = Alignment.CenterHorizontally,
					verticalArrangement = Arrangement.spacedBy(16.dp)
				) {
					Text(
						text = state.message,
						style = MaterialTheme.typography.bodyLarge,
						color = MaterialTheme.colorScheme.error,
						textAlign = TextAlign.Center
					)
					Button(onClick = { navController.popBackStack() }) {
						Text("Volver")
					}
				}
			}
		}
		
		// 🔹 Cuenta regresiva superpuesta (si está activa)
		if (uiState is PlaybackUiState.Playing) {
			val playbackState = (uiState as PlaybackUiState.Playing).state
			if (playbackState.isCountingDown) {
				
				// Animación de escala que se reinicia cada vez que cambia el número
				val scale = remember(playbackState.countdown) { Animatable(0f) }
				LaunchedEffect(playbackState.countdown) {
					scale.snapTo(0f) // Comienza pequeño
					scale.animateTo(
						targetValue = 1f, // Crece a tamaño normal
						animationSpec = tween(
							durationMillis = 800,
							easing = FastOutSlowInEasing
						)
					)
				}
				
				Box(
					modifier = Modifier.fillMaxSize(),
					contentAlignment = Alignment.Center
				) {
					Text(
						text = playbackState.countdown.toString(),
						fontSize = 200.sp,
						fontWeight = FontWeight.Bold,
						color = Color.Black,
						modifier = Modifier
							.scale(scale.value) // 🔹 Aplica el crecimiento
							.zIndex(1f) // 🔹 Se asegura de estar arriba
					)
				}
			}
		}
	}
	
	// ✅ Diálogo de completitud
	if (uiState is PlaybackUiState.Playing &&
		(uiState as PlaybackUiState.Playing).state.isCompleted
	) {
		CompletionDialog(
			onDismiss = { navController.popBackStack() },
			routineName = (uiState as PlaybackUiState.Playing).state.routine.name,
			totalTime = (uiState as PlaybackUiState.Playing).state.totalElapsedTime
		)
	}
}



@Composable
private fun RoutinePlaybackContent(
	playbackState: RoutinePlaybackState,
	onPlayPause: () -> Unit,
	onNextPhase: () -> Unit,
	onNextRepetition: () -> Unit,
	onRepeatPhase: () -> Unit,
	onClose: () -> Unit
) {
	val currentPhase = playbackState.currentPhase
	
	Scaffold { innerPadding ->
		Box(
			modifier = Modifier
				.fillMaxSize()
				.background(
					brush = Brush.radialGradient(
						colors = listOf(
							currentPhase?.color?.copy(alpha = 0.2f)
								?: Color(0xFF2196F3).copy(alpha = 0.2f),
							Color.Transparent
						),
						center = Offset(500f, 500f),
						radius = 1000f
					)
				)
				.padding(innerPadding)
		) {
			Column(
				modifier = Modifier
					.fillMaxSize()
					.padding(24.dp),
				verticalArrangement = Arrangement.SpaceBetween,
			) {
				// Header
				HeaderSection(
					routineName = playbackState.routine.name,
					onClose = onClose
				)
				
				// Progress Bar
				ProgressSection(
					progress = playbackState.progress,
					currentPhase = playbackState.currentPhaseIndex + 1,
					totalPhases = playbackState.phases.size,
					accentColor = currentPhase?.color ?: Color(0xFF2196F3)
				)
				
				Spacer(modifier = Modifier.height(32.dp))
				
				// Main Content
				Column(
					modifier = Modifier.weight(1f),
					horizontalAlignment = Alignment.CenterHorizontally,
					verticalArrangement = Arrangement.Center
				) {
					currentPhase?.let { phase ->
						PhaseInfoCard(
							phaseName = phase.name,
							phaseNumber = playbackState.currentPhaseIndex + 1,
							phaseMode = phase.mode,
							currentRepetition = playbackState.currentRepetition,
							totalRepetitions = phase.calculateTotalRepetitions(),
							currentBPM = playbackState.currentBPM,
							timeSignature = phase.timeSignature, // 👈 NUEVO
							color = phase.color
						)
						Spacer(modifier = Modifier.height(40.dp))
						
						TimerCircle(
							timeRemaining = playbackState.timeRemaining,
							totalTime = phase.duration * 60,
							isPaused = playbackState.isPaused,
							color = phase.color
						)
						
						Spacer(modifier = Modifier.height(40.dp))
						
						if (phase.bpm > 0) {
							MetronomeDisplay(
								bpm = playbackState.currentBPM,
								timeSignature = phase.timeSignature,
								isActive = !playbackState.isPaused,
								color = phase.color,
								showIncrement = phase.bpmIncrement > 0
							)
						}
					}
				}
				
				Spacer(modifier = Modifier.height(32.dp))
				
				ControlButtonsSection(
					isPaused = playbackState.isPaused,
					onPausePlay = onPlayPause,
					onNextPhase = onNextPhase,
					onRepeatPhase = onRepeatPhase,
					onNextRepetition = onNextRepetition,
					hasNextPhase = playbackState.hasNextPhase,
					showNextRepetition = playbackState.hasNextRepetition
				)
			}
		}
	}
}

@Composable
private fun PhaseInfoCard(
	phaseName: String,
	phaseNumber: Int,
	phaseMode: String,
	currentRepetition: Int,
	totalRepetitions: Int,
	currentBPM: Int,
	timeSignature: String, // 👈 NUEVO parámetro
	color: Color
) {
	Column(
		modifier = Modifier
			.fillMaxWidth()
			.wrapContentHeight(),
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.spacedBy(12.dp)
	) {
		// Badge del modo
		Surface(
			shape = RoundedCornerShape(20.dp),
			color = color.copy(alpha = 0.15f)
		) {
			Text(
				text = when (phaseMode) {
					"BY_REPS" -> "POR REPETICIONES"
					"BY_BPM_MAX" -> "HASTA BPM MÁXIMO"
					else -> "POR TIEMPO"
				},
				style = MaterialTheme.typography.labelSmall,
				fontWeight = FontWeight.Bold,
				color = color,
				modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
			)
		}
		
		Text(
			text = phaseName,
			style = MaterialTheme.typography.headlineMedium,
			fontWeight = FontWeight.Bold,
			color = MaterialTheme.colorScheme.onSurface,
			textAlign = TextAlign.Center
		)
		
		// Información según el modo
		if (phaseMode == "BY_REPS" || phaseMode == "BY_BPM_MAX") {
			Column(
				horizontalAlignment = Alignment.CenterHorizontally,
				verticalArrangement = Arrangement.spacedBy(8.dp)
			) {
				// Contador de repeticiones
				Text(
					text = "Rep. $currentRepetition/$totalRepetitions",
					style = MaterialTheme.typography.bodyLarge,
					color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
					fontWeight = FontWeight.Medium
				)
				
				// Row con BPM y Compás
				Row(
					horizontalArrangement = Arrangement.spacedBy(12.dp),
					verticalAlignment = Alignment.CenterVertically
				) {
					// BPM Badge
					Surface(
						shape = RoundedCornerShape(12.dp),
						color = color.copy(alpha = 0.2f)
					) {
						Row(
							modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
							horizontalArrangement = Arrangement.spacedBy(4.dp),
							verticalAlignment = Alignment.CenterVertically
						) {
							Text(
								text = "$currentBPM",
								style = MaterialTheme.typography.titleMedium,
								fontWeight = FontWeight.Bold,
								color = color
							)
							Text(
								text = "BPM",
								style = MaterialTheme.typography.bodySmall,
								color = color.copy(alpha = 0.8f)
							)
						}
					}
					
					// Time Signature Badge
					Surface(
						shape = RoundedCornerShape(12.dp),
						color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
					) {
						Text(
							text = timeSignature,
							style = MaterialTheme.typography.titleMedium,
							fontWeight = FontWeight.Bold,
							color = MaterialTheme.colorScheme.onSurface,
							modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
						)
					}
				}
			}
		} else {
			// Para modo BY_TIME
			Row(
				horizontalArrangement = Arrangement.spacedBy(12.dp),
				verticalAlignment = Alignment.CenterVertically
			) {
				// Mostrar BPM si existe
				if (currentBPM > 0) {
					Surface(
						shape = RoundedCornerShape(12.dp),
						color = color.copy(alpha = 0.2f)
					) {
						Row(
							modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
							horizontalArrangement = Arrangement.spacedBy(4.dp),
							verticalAlignment = Alignment.CenterVertically
						) {
							Text(
								text = "$currentBPM",
								style = MaterialTheme.typography.titleMedium,
								fontWeight = FontWeight.Bold,
								color = color
							)
							Text(
								text = "BPM",
								style = MaterialTheme.typography.bodySmall,
								color = color.copy(alpha = 0.8f)
							)
						}
					}
					
					// Time Signature Badge
					Surface(
						shape = RoundedCornerShape(12.dp),
						color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
					) {
						Text(
							text = timeSignature,
							style = MaterialTheme.typography.titleMedium,
							fontWeight = FontWeight.Bold,
							color = MaterialTheme.colorScheme.onSurface,
							modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
						)
					}
				}
			}
		}
	}
}

@Composable
private fun HeaderSection(
	routineName: String,
	onClose: () -> Unit
) {
	Row(
		modifier = Modifier.fillMaxWidth(),
		horizontalArrangement = Arrangement.SpaceBetween,
		verticalAlignment = Alignment.CenterVertically
	) {
		IconButton(
			onClick = onClose,
			modifier = Modifier.size(48.dp)
		) {
			Icon(
				Icons.Default.Close,
				contentDescription = "Cerrar",
				tint = MaterialTheme.colorScheme.onSurface,
				modifier = Modifier.size(28.dp)
			)
		}
		
		Text(
			text = routineName,
			style = MaterialTheme.typography.titleLarge,
			fontWeight = FontWeight.Bold,
			color = MaterialTheme.colorScheme.onSurface,
			textAlign = TextAlign.Center,
			modifier = Modifier.weight(1f)
		)
		
		Spacer(modifier = Modifier.size(48.dp))
	}
}

@Composable
private fun ProgressSection(
	progress: Float,
	currentPhase: Int,
	totalPhases: Int,
	accentColor: Color
) {
	Column(
		modifier = Modifier
			.fillMaxWidth()
			.wrapContentHeight(),
		verticalArrangement = Arrangement.spacedBy(8.dp),
		horizontalAlignment = Alignment.CenterHorizontally
	) {
		// Centrar textos
		Row(
			horizontalArrangement = Arrangement.spacedBy(12.dp),
			verticalAlignment = Alignment.CenterVertically
		) {
			Text(
				text = "Fase $currentPhase de $totalPhases",
				style = MaterialTheme.typography.bodyMedium,
				color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
			)
			Text(
				text = "${(progress * 100).toInt()}%",
				style = MaterialTheme.typography.bodyMedium,
				fontWeight = FontWeight.SemiBold,
				color = accentColor
			)
		}
		
		// Barra centrada con ancho limitado
		LinearProgressIndicator(
			progress = { progress },
			modifier = Modifier
				.width(220.dp)
				.height(8.dp)
				.clip(RoundedCornerShape(4.dp)),
			color = accentColor,
			trackColor = accentColor.copy(alpha = 0.2f)
		)
	}
}


@Composable
private fun TimerCircle(
	timeRemaining: Int,
	totalTime: Int,
	isPaused: Boolean,
	color: Color
) {
	val minutes = timeRemaining / 60
	val seconds = timeRemaining % 60
	val progressValue = if (totalTime > 0) timeRemaining.toFloat() / totalTime.toFloat() else 1f
	
	Box(
		modifier = Modifier
			.fillMaxWidth()
			.padding(vertical = 16.dp),
		contentAlignment = Alignment.Center
	) {
		Canvas(modifier = Modifier.size(280.dp)) {
			val strokeWidth = 16.dp.toPx()
			val diameter = size.minDimension - strokeWidth
			val radius = diameter / 2
			val topLeft = Offset(
				x = (size.width - diameter) / 2,
				y = (size.height - diameter) / 2
			)
			
			// Círculo de fondo
			drawArc(
				color = color.copy(alpha = 0.1f),
				startAngle = -90f,
				sweepAngle = 360f,
				useCenter = false,
				topLeft = topLeft,
				size = Size(diameter, diameter),
				style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
			)
			
			// Círculo de progreso
			drawArc(
				color = color,
				startAngle = -90f,
				sweepAngle = 360f * progressValue,
				useCenter = false,
				topLeft = topLeft,
				size = Size(diameter, diameter),
				style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
			)
		}
		
		// Contenido central (centrado por defecto)
		Column(
			horizontalAlignment = Alignment.CenterHorizontally,
			verticalArrangement = Arrangement.spacedBy(4.dp)
		) {
			Text(
				text = String.format("%02d:%02d", minutes, seconds),
				style = MaterialTheme.typography.displayLarge.copy(
					fontSize = 72.sp,
					fontWeight = FontWeight.Bold
				),
				color = MaterialTheme.colorScheme.onSurface
			)
			Text(
				text = if (isPaused) "PAUSADO" else "EN PROGRESO",
				style = MaterialTheme.typography.labelMedium,
				color = if (isPaused)
					MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
				else color,
				fontWeight = FontWeight.Medium
			)
		}
	}
}


@Composable
private fun MetronomeDisplay(
	bpm: Int,
	timeSignature: String,
	isActive: Boolean,
	color: Color,
	showIncrement: Boolean
) {
	val infiniteTransition = rememberInfiniteTransition(label = "metronome")
	val scale by infiniteTransition.animateFloat(
		initialValue = 1f,
		targetValue = 1.2f,
		animationSpec = infiniteRepeatable(
			animation = tween(60000 / bpm, easing = LinearEasing),
			repeatMode = RepeatMode.Reverse
		),
		label = "pulse"
	)
	
	Surface(
		shape = RoundedCornerShape(24.dp),
		color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
		modifier = Modifier.padding(horizontal = 32.dp)
	) {
		Row(
			modifier = Modifier.padding(20.dp),
			horizontalArrangement = Arrangement.spacedBy(24.dp),
			verticalAlignment = Alignment.CenterVertically
		) {
			Box(
				modifier = Modifier.size(56.dp),
				contentAlignment = Alignment.Center
			) {
				Box(
					modifier = Modifier
						.size(if (isActive) 56.dp * scale else 56.dp)
						.clip(CircleShape)
						.background(color.copy(alpha = 0.2f))
				)
				Box(
					modifier = Modifier
						.size(if (isActive) 28.dp * scale else 28.dp)
						.clip(CircleShape)
						.background(if (isActive) color else color.copy(alpha = 0.5f))
				)
			}
			
			Column(
				verticalArrangement = Arrangement.spacedBy(4.dp)
			) {
				Row(
					horizontalArrangement = Arrangement.spacedBy(8.dp),
					verticalAlignment = Alignment.Bottom
				) {
					Text(
						text = "$bpm",
						style = MaterialTheme.typography.headlineLarge,
						fontWeight = FontWeight.Bold,
						color = MaterialTheme.colorScheme.onSurface
					)
					Text(
						text = "BPM",
						style = MaterialTheme.typography.bodyMedium,
						color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
						modifier = Modifier.padding(bottom = 4.dp)
					)
				}
				
				Row(
					horizontalArrangement = Arrangement.spacedBy(8.dp)
				) {
					Text(
						text = timeSignature,
						style = MaterialTheme.typography.bodyMedium,
						color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
					)
					if (showIncrement) {
						Text(
							text = "• Se incrementa",
							style = MaterialTheme.typography.bodySmall,
							color = color
						)
					}
				}
			}
		}
	}
}

@Composable
private fun ControlButtonsSection(
	isPaused: Boolean,
	onPausePlay: () -> Unit,
	onNextPhase: () -> Unit,
	onRepeatPhase: () -> Unit,
	onNextRepetition: () -> Unit,
	hasNextPhase: Boolean,
	showNextRepetition: Boolean
) {
	Column(
		verticalArrangement = Arrangement.spacedBy(8.dp) // 🔹 Menos espacio vertical
	) {
		Row(
			modifier = Modifier.fillMaxWidth(),
			horizontalArrangement = Arrangement.spacedBy(8.dp) // 🔹 Menos espacio horizontal
		) {
			Button(
				onClick = onPausePlay,
				modifier = Modifier
					.weight(1f)
					.height(52.dp), // 🔹 Más compacto
				shape = RoundedCornerShape(24.dp), // 🔹 Curvas más suaves
				colors = ButtonDefaults.buttonColors(
					containerColor = MaterialTheme.colorScheme.primary,
					contentColor = MaterialTheme.colorScheme.onPrimary
				)
			) {
				Icon(
					imageVector = if (isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
					contentDescription = if (isPaused) "Reanudar" else "Pausar",
					modifier = Modifier.size(22.dp)
				)
				Spacer(modifier = Modifier.width(6.dp))
				Text(
					text = if (isPaused) "Reanudar" else "Pausar",
					style = MaterialTheme.typography.bodyMedium, // 🔹 Más pequeño
					fontWeight = FontWeight.SemiBold
				)
			}
			
			Button(
				onClick = onNextPhase,
				enabled = hasNextPhase,
				modifier = Modifier
					.weight(1f)
					.height(52.dp),
				shape = RoundedCornerShape(24.dp),
				colors = ButtonDefaults.buttonColors(
					containerColor = MaterialTheme.colorScheme.secondaryContainer,
					contentColor = MaterialTheme.colorScheme.onSecondaryContainer
				)
			) {
				Text(
					text = "Siguiente",
					style = MaterialTheme.typography.bodyMedium,
					fontWeight = FontWeight.SemiBold
				)
				Spacer(modifier = Modifier.width(6.dp))
				Icon(
					imageVector = Icons.Default.SkipNext,
					contentDescription = "Siguiente fase",
					modifier = Modifier.size(22.dp)
				)
			}
		}
		
		Row(
			modifier = Modifier.fillMaxWidth(),
			horizontalArrangement = Arrangement.spacedBy(8.dp)
		) {
			OutlinedButton(
				onClick = onRepeatPhase,
				modifier = Modifier
					.weight(1f)
					.height(46.dp), // 🔹 Un poco más pequeño aún
				shape = RoundedCornerShape(20.dp)
			) {
				Icon(
					imageVector = Icons.Default.Refresh,
					contentDescription = "Repetir fase",
					modifier = Modifier.size(18.dp)
				)
				Spacer(modifier = Modifier.width(6.dp))
				Text(
					text = "Repetir",
					style = MaterialTheme.typography.bodySmall,
					fontWeight = FontWeight.Medium
				)
			}
			
			if (showNextRepetition) {
				OutlinedButton(
					onClick = onNextRepetition,
					modifier = Modifier
						.weight(1f)
						.height(46.dp),
					shape = RoundedCornerShape(20.dp)
				) {
					Text(
						text = "Siguiente Rep.",
						style = MaterialTheme.typography.bodySmall,
						fontWeight = FontWeight.Medium
					)
				}
			}
		}
	}
}


@Composable
private fun CompletionDialog(
	onDismiss: () -> Unit,
	routineName: String,
	totalTime: Int
) {
	val hours = totalTime / 3600
	val minutes = (totalTime % 3600) / 60
	val seconds = totalTime % 60
	
	AlertDialog(
		onDismissRequest = onDismiss,
		title = null,
		text = {
			Column(
				modifier = Modifier
					.fillMaxWidth()
					.padding(vertical = 8.dp),
				horizontalAlignment = Alignment.CenterHorizontally,
				verticalArrangement = Arrangement.spacedBy(16.dp)
			) {
				// Icono de éxito
				Surface(
					shape = CircleShape,
					color = MaterialTheme.colorScheme.primaryContainer,
					modifier = Modifier.size(72.dp)
				) {
					Box(
						contentAlignment = Alignment.Center,
						modifier = Modifier.fillMaxSize()
					) {
						Icon(
							imageVector = Icons.Default.Check,
							contentDescription = null,
							tint = MaterialTheme.colorScheme.onPrimaryContainer,
							modifier = Modifier.size(40.dp)
						)
					}
				}
				
				// Título
				Text(
					text = "¡Rutina Completada!",
					style = MaterialTheme.typography.headlineMedium,
					fontWeight = FontWeight.Bold,
					color = MaterialTheme.colorScheme.onSurface,
					textAlign = TextAlign.Center
				)
				
				// Nombre de la rutina
				Surface(
					shape = RoundedCornerShape(12.dp),
					color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
				) {
					Text(
						text = routineName,
						style = MaterialTheme.typography.titleMedium,
						fontWeight = FontWeight.SemiBold,
						color = MaterialTheme.colorScheme.primary,
						textAlign = TextAlign.Center,
						modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
					)
				}
				
				// Tiempo total
				Column(
					horizontalAlignment = Alignment.CenterHorizontally,
					verticalArrangement = Arrangement.spacedBy(4.dp)
				) {
					Text(
						text = "Tiempo total de práctica",
						style = MaterialTheme.typography.bodySmall,
						color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
					)
					
					Row(
						horizontalArrangement = Arrangement.spacedBy(8.dp),
						verticalAlignment = Alignment.CenterVertically
					) {
						Icon(
							imageVector = Icons.Default.Timer,
							contentDescription = null,
							tint = MaterialTheme.colorScheme.primary,
							modifier = Modifier.size(20.dp)
						)
						Text(
							text = buildString {
								if (hours > 0) append("$hours h ")
								append("$minutes min ")
							},
							style = MaterialTheme.typography.titleLarge,
							fontWeight = FontWeight.Bold,
							color = MaterialTheme.colorScheme.onSurface
						)
					}
				}
				
				// Mensaje motivacional
				Text(
					text = "¡Excelente trabajo! Sigue así para alcanzar tus metas musicales.",
					style = MaterialTheme.typography.bodyMedium,
					color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
					textAlign = TextAlign.Center
				)
			}
		},
		confirmButton = {
			Button(
				onClick = onDismiss,
				modifier = Modifier
					.fillMaxWidth()
					.height(48.dp),
				shape = RoundedCornerShape(24.dp)
			) {
				Text(
					text = "Finalizar",
					style = MaterialTheme.typography.bodyLarge,
					fontWeight = FontWeight.SemiBold
				)
			}
		},
		shape = RoundedCornerShape(24.dp),
		containerColor = MaterialTheme.colorScheme.surface,
		tonalElevation = 0.dp
	)
}


// --- Data Classes ---
data class Routine(
	val id: String,
	val name: String,
	val description: String = "",
	val phases: List<Phase> = emptyList(),
	val createdAt: Long = System.currentTimeMillis()
)
