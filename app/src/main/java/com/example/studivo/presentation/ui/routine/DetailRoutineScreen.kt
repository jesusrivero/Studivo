package com.example.studivo.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.studivo.presentation.ui.commons.BottomNavigationBar
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.sp
import androidx.compose.material3.LinearProgressIndicator


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailRoutineScreen(
	navController: NavController,
) {
	DetailRoutineScreenContent(navController = navController)
}

// --- FocusModeScreenContent ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailRoutineScreenContent(
	navController: NavController,
	routine: Routine = sampleRoutine()
) {
	// Estados para el control de la rutina
	var currentPhaseIndex by remember { mutableStateOf(0) }
	var timeRemaining by remember { mutableStateOf(300) } // 5:00 en segundos
	var isPaused by remember { mutableStateOf(false) }
	
	val currentPhase = routine.phases.getOrNull(currentPhaseIndex)
	
	// Calcular progreso total
	val totalPhases = routine.phases.size
	val progress = (currentPhaseIndex + 1).toFloat() / totalPhases.toFloat()
	
	// Función para avanzar a la siguiente fase (retorna Unit no-nulo)
	val nextPhase: () -> Unit = {
		if (currentPhaseIndex < routine.phases.size - 1) {
			currentPhaseIndex++
			timeRemaining = routine.phases[currentPhaseIndex].duration * 60
		}
	}

// Función para reiniciar la fase actual (aseguramos Unit no-nulo usando ?: Unit)
	val repeatPhase: () -> Unit = {
		currentPhase?.let {
			timeRemaining = it.duration * 60
		} ?: Unit
	}
	
	Scaffold { innerPadding ->
		Box(
			modifier = Modifier
				.fillMaxSize()
				.background(
					brush = Brush.radialGradient(
						colors = listOf(
							currentPhase?.color?.copy(alpha = 0.3f) ?: Color(0xFF2196F3).copy(alpha = 0.3f),
							Color.Transparent
						),
						radius = 800f
					)
				)
				.padding(innerPadding)
		) {
			Column(
				modifier = Modifier
					.fillMaxSize()
					.padding(24.dp),
				verticalArrangement = Arrangement.SpaceBetween
			) {
				// Header con botón cerrar
				Row(
					modifier = Modifier.fillMaxWidth(),
					horizontalArrangement = Arrangement.SpaceBetween,
					verticalAlignment = Alignment.CenterVertically
				) {
					IconButton(
						onClick = { navController.popBackStack() },
						modifier = Modifier.size(48.dp)
					) {
						Icon(
							Icons.Default.Close,
							contentDescription = "Cerrar",
							tint = MaterialTheme.colorScheme.onSurface,
							modifier = Modifier.size(24.dp)
						)
					}
					
					Text(
						text = "Focus Mode",
						style = MaterialTheme.typography.titleLarge,
						fontWeight = FontWeight.SemiBold,
						color = MaterialTheme.colorScheme.onSurface
					)
					
					// Espaciador para centrar el título
					Spacer(modifier = Modifier.size(48.dp))
				}
				
				// Contenido principal
				Column(
					modifier = Modifier.weight(1f),
					horizontalAlignment = Alignment.CenterHorizontally,
					verticalArrangement = Arrangement.Center
				) {
					// Barra de progreso
					ProgressSection(
						progress = progress,
						currentPhase = currentPhaseIndex + 1,
						totalPhases = totalPhases
					)
					
					Spacer(modifier = Modifier.height(48.dp))
					
					// Información de la fase actual
					currentPhase?.let { phase ->
						PhaseInfoSection(
							phaseName = phase.name,
							phaseNumber = currentPhaseIndex + 1
						)
						
						Spacer(modifier = Modifier.height(32.dp))
						
						// Timer principal
						TimerDisplay(
							timeRemaining = timeRemaining,
							isPaused = isPaused
						)
						
						Spacer(modifier = Modifier.height(48.dp))
						
						// Metrónomo visual (si está activado)
						if (phase.bpm > 0) {
							MetronomeSection(
								bpm = phase.bpm,
								isActive = !isPaused
							)
							
							Spacer(modifier = Modifier.height(32.dp))
						}
					}
				}
				
				// Botones de control
				ControlButtonsSection(
					isPaused = isPaused,
					onPausePlay = { isPaused = !isPaused },
					onNextPhase = nextPhase,
					onRepeatPhase = repeatPhase,
					hasNextPhase = currentPhaseIndex < routine.phases.size - 1
				)
			}
		}
	}
}

@Composable
private fun ProgressSection(
	progress: Float,
	currentPhase: Int,
	totalPhases: Int
) {
	Column(
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.spacedBy(8.dp)
	) {
		LinearProgressIndicator(
			progress = progress,
			modifier = Modifier
				.fillMaxWidth()
				.height(6.dp)
				.clip(RoundedCornerShape(3.dp)),
			color = MaterialTheme.colorScheme.primary,
			trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
		)
	}
}

@Composable
private fun PhaseInfoSection(
	phaseName: String,
	phaseNumber: Int
) {
	Column(
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.spacedBy(8.dp)
	) {
		Text(
			text = "Phase $phaseNumber: $phaseName",
			style = MaterialTheme.typography.headlineMedium,
			fontWeight = FontWeight.SemiBold,
			color = MaterialTheme.colorScheme.onSurface,
			textAlign = TextAlign.Center
		)
	}
}

@Composable
private fun TimerDisplay(
	timeRemaining: Int,
	isPaused: Boolean
) {
	val minutes = timeRemaining / 60
	val seconds = timeRemaining % 60
	
	Text(
		text = String.format("%02d:%02d", minutes, seconds),
		style = MaterialTheme.typography.displayLarge.copy(
			fontSize = 88.sp,
			fontWeight = FontWeight.Bold
		),
		color = MaterialTheme.colorScheme.onSurface,
		textAlign = TextAlign.Center
	)
}

@Composable
private fun MetronomeSection(
	bpm: Int,
	isActive: Boolean
) {
	Column(
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.spacedBy(16.dp)
	) {
		// Indicador visual del metrónomo
		Box(
			modifier = Modifier
				.size(80.dp)
				.clip(CircleShape)
				.background(
					if (isActive)
						MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
					else
						MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)
				),
			contentAlignment = Alignment.Center
		) {
			Box(
				modifier = Modifier
					.size(40.dp)
					.clip(CircleShape)
					.background(
						if (isActive)
							MaterialTheme.colorScheme.primary
						else
							MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
					)
			)
		}
		
		Text(
			text = "Metronome: $bpm BPM",
			style = MaterialTheme.typography.bodyLarge,
			color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
			textAlign = TextAlign.Center
		)
	}
}

@Composable
private fun ControlButtonsSection(
	isPaused: Boolean,
	onPausePlay: () -> Unit,
	onNextPhase: () -> Unit,
	onRepeatPhase: () -> Unit,
	hasNextPhase: Boolean
) {
	Column(
		verticalArrangement = Arrangement.spacedBy(16.dp)
	) {
		// Botones principales (Pause/Play y Next Phase)
		Row(
			modifier = Modifier.fillMaxWidth(),
			horizontalArrangement = Arrangement.spacedBy(12.dp)
		) {
			// Botón Pause/Resume
			Button(
				onClick = onPausePlay,
				modifier = Modifier.weight(1f),
				shape = RoundedCornerShape(25.dp),
				colors = ButtonDefaults.buttonColors(
					containerColor = MaterialTheme.colorScheme.surfaceVariant,
					contentColor = MaterialTheme.colorScheme.onSurfaceVariant
				),
				contentPadding = PaddingValues(vertical = 16.dp)
			) {
				Text(
					text = if (isPaused) "Resume" else "Pause",
					style = MaterialTheme.typography.labelLarge,
					fontWeight = FontWeight.Medium
				)
			}
			
			// Botón Next Phase
			Button(
				onClick = onNextPhase,
				modifier = Modifier.weight(1f),
				enabled = hasNextPhase,
				shape = RoundedCornerShape(25.dp),
				colors = ButtonDefaults.buttonColors(
					containerColor = MaterialTheme.colorScheme.primary,
					contentColor = MaterialTheme.colorScheme.onPrimary,
					disabledContainerColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
					disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
				),
				contentPadding = PaddingValues(vertical = 16.dp)
			) {
				Text(
					text = "Next Phase",
					style = MaterialTheme.typography.labelLarge,
					fontWeight = FontWeight.Medium
				)
			}
		}
		
		// Botón Repeat Phase
		TextButton(
			onClick = onRepeatPhase,
			modifier = Modifier.fillMaxWidth(),
			shape = RoundedCornerShape(25.dp)
		) {
			Text(
				text = "Repeat Phase",
				style = MaterialTheme.typography.labelLarge,
				fontWeight = FontWeight.Medium,
				color = MaterialTheme.colorScheme.primary
			)
		}
	}
}

// --- Data Classes (para ejemplo) ---
data class Routine(
	val id: String,
	val name: String,
	val description: String = "",
	val phases: List<Phase> = emptyList(),
	val createdAt: Long = System.currentTimeMillis()
) {
	fun calculateTotalDuration(): Int = phases.sumOf { it.duration }
	fun calculateAverageBpm(): Int = if (phases.isNotEmpty()) {
		phases.filter { it.bpm > 0 }.map { it.bpm }.average().toInt()
	} else 0
}

data class Phase(
	val id: String,
	val name: String,
	val duration: Int,
	val bpm: Int = 0,
	val timeSignature: String = "4/4",
	val color: Color = Color(0xFF2196F3),
	val repetitions: Int = 1,
	val bpmIncrement: Int = 0,
	val bpmMax: Int = 0
)

// --- Sample Data (para preview) ---
private fun sampleRoutine() = Routine(
	id = "1",
	name = "Mi rutina de práctica",
	description = "Rutina completa para mejorar técnica y velocidad",
	phases = listOf(
		Phase(
			id = "1",
			name = "Warm-up",
			duration = 5,
			bpm = 120,
			timeSignature = "4/4",
			color = Color(0xFF4CAF50)
		),
		Phase(
			id = "2",
			name = "Ejercicios técnicos",
			duration = 15,
			bpm = 80,
			timeSignature = "4/4",
			color = Color(0xFF2196F3)
		),
		Phase(
			id = "3",
			name = "Repertorio",
			duration = 20,
			bpm = 140,
			timeSignature = "3/4",
			color = Color(0xFFF44336)
		)
	)
)