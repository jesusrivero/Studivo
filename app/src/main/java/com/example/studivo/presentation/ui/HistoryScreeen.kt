package com.example.studivo.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.studivo.domain.model.RoutineProgress
import com.example.studivo.domain.viewmodels.HistoryViewModel
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun HistoryScreen(navController: NavController) {
	HistoryScreenContent(navController = navController)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreenContent(
	navController: NavController,
	viewModel: HistoryViewModel = hiltViewModel(),
) {
	val history by viewModel.history.collectAsState()
	
	Scaffold(
		topBar = {
			CenterAlignedTopAppBar(
				title = {
					Text(
						"Historial",
						style = MaterialTheme.typography.titleLarge,
						fontWeight = FontWeight.SemiBold
					)
				},
				navigationIcon = {
					IconButton(onClick = { navController.navigateUp() }) {
						Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
					}
				},
				colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
					containerColor = MaterialTheme.colorScheme.surface
				)
			)
		},
		containerColor = MaterialTheme.colorScheme.surface
	) { innerPadding ->
		if (history.isEmpty()) {
			EmptyHistoryState(modifier = Modifier.padding(innerPadding))
		} else {
			LazyColumn(
				modifier = Modifier
					.fillMaxSize()
					.padding(innerPadding)
			) {
				// Resumen general
				item {
					OverallSummary(history)
					Spacer(modifier = Modifier.height(16.dp))
					Divider(
						modifier = Modifier.padding(horizontal = 24.dp),
						color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
					)
					Spacer(modifier = Modifier.height(8.dp))
				}
				
				// Timeline de días
				history.entries.forEachIndexed { index, (date, routines) ->
					item {
						DayTimelineEntry(
							date = date,
							routines = routines,
							isLast = index == history.size - 1
						)
					}
				}
				
				item {
					Spacer(modifier = Modifier.height(32.dp))
				}
			}
		}
	}
}

@Composable
fun EmptyHistoryState(modifier: Modifier = Modifier) {
	Column(
		modifier = modifier
			.fillMaxSize()
			.padding(32.dp),
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.Center
	) {
		Text(
			text = "📚",
			fontSize = 64.sp
		)
		Spacer(modifier = Modifier.height(16.dp))
		Text(
			text = "Sin historial aún",
			style = MaterialTheme.typography.titleLarge,
			fontWeight = FontWeight.SemiBold
		)
		Spacer(modifier = Modifier.height(8.dp))
		Text(
			text = "Completa tu primera rutina de estudio\npara ver tu progreso aquí",
			style = MaterialTheme.typography.bodyMedium,
			color = MaterialTheme.colorScheme.onSurfaceVariant,
			textAlign = TextAlign.Center
		)
	}
}

@Composable
fun OverallSummary(history: Map<String, List<RoutineProgress>>) {
	val totalSessions = history.values.sumOf { it.size }
	val totalCompleted = history.values.sumOf { routines -> routines.count { it.isCompleted } }
	val totalMinutes = history.values.sumOf { routines -> routines.sumOf { it.totalElapsedTime } } / 60
	
	Column(
		modifier = Modifier
			.fillMaxWidth()
			.padding(horizontal = 24.dp, vertical = 16.dp)
	) {
		Text(
			text = "Resumen total",
			style = MaterialTheme.typography.labelLarge,
			color = MaterialTheme.colorScheme.onSurfaceVariant,
			letterSpacing = 0.5.sp
		)
		
		Spacer(modifier = Modifier.height(12.dp))
		
		Row(
			modifier = Modifier.fillMaxWidth(),
			horizontalArrangement = Arrangement.spacedBy(24.dp)
		) {
			SummaryItem(
				value = totalSessions.toString(),
				label = "Sesiones",
				modifier = Modifier.weight(1f)
			)
			SummaryItem(
				value = totalCompleted.toString(),
				label = "Completadas",
				modifier = Modifier.weight(1f)
			)
			SummaryItem(
				value = "$totalMinutes min",
				label = "Total",
				modifier = Modifier.weight(1f)
			)
		}
	}
}

@Composable
fun SummaryItem(
	value: String,
	label: String,
	modifier: Modifier = Modifier
) {
	Column(
		modifier = modifier,
		horizontalAlignment = Alignment.Start
	) {
		Text(
			text = value,
			style = MaterialTheme.typography.headlineSmall,
			fontWeight = FontWeight.Bold,
			color = MaterialTheme.colorScheme.primary
		)
		Text(
			text = label,
			style = MaterialTheme.typography.bodySmall,
			color = MaterialTheme.colorScheme.onSurfaceVariant
		)
	}
}

@Composable
fun DayTimelineEntry(
	date: String,
	routines: List<RoutineProgress>,
	isLast: Boolean
) {
	val formattedDate = formatDateReadable(date)
	val totalMinutes = routines.sumOf { it.totalElapsedTime } / 60
	val completedCount = routines.count { it.isCompleted }
	
	Row(
		modifier = Modifier
			.fillMaxWidth()
			.padding(start = 24.dp, end = 24.dp, top = 12.dp, bottom = 8.dp)
	) {
		// Timeline indicator
		Column(
			horizontalAlignment = Alignment.CenterHorizontally,
			modifier = Modifier.padding(end = 16.dp)
		) {
			Box(
				modifier = Modifier
					.size(12.dp)
					.clip(CircleShape)
					.background(
						if (completedCount > 0)
							MaterialTheme.colorScheme.primary
						else
							MaterialTheme.colorScheme.outlineVariant
					)
			)
			if (!isLast) {
				Box(
					modifier = Modifier
						.width(2.dp)
						.height(120.dp)
						.background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
				)
			}
		}
		
		// Content
		Column(modifier = Modifier.weight(1f)) {
			// Header del día
			Row(
				modifier = Modifier.fillMaxWidth(),
				horizontalArrangement = Arrangement.SpaceBetween,
				verticalAlignment = Alignment.CenterVertically
			) {
				Text(
					text = formattedDate,
					style = MaterialTheme.typography.titleMedium,
					fontWeight = FontWeight.SemiBold
				)
				
				Text(
					text = "$totalMinutes min",
					style = MaterialTheme.typography.bodyMedium,
					color = MaterialTheme.colorScheme.onSurfaceVariant,
					fontWeight = FontWeight.Medium
				)
			}
			
			Spacer(modifier = Modifier.height(4.dp))
			
			Text(
				text = "$completedCount de ${routines.size} completadas",
				style = MaterialTheme.typography.bodySmall,
				color = MaterialTheme.colorScheme.onSurfaceVariant
			)
			
			Spacer(modifier = Modifier.height(12.dp))
			
			// Lista de rutinas
			routines.forEach { progress ->
				RoutineListItem(progress)
				Spacer(modifier = Modifier.height(8.dp))
			}
		}
	}
}

@Composable
fun RoutineListItem(progress: RoutineProgress) {
	Row(
		modifier = Modifier
			.fillMaxWidth()
			.padding(vertical = 8.dp),
		horizontalArrangement = Arrangement.SpaceBetween,
		verticalAlignment = Alignment.CenterVertically
	) {
		Row(
			modifier = Modifier.weight(1f),
			verticalAlignment = Alignment.CenterVertically
		) {
			// Indicador de estado
			Icon(
				imageVector = if (progress.isCompleted) Icons.Default.CheckCircle else Icons.Outlined.Circle,
				contentDescription = null,
				modifier = Modifier.size(20.dp),
				tint = if (progress.isCompleted)
					MaterialTheme.colorScheme.primary
				else
					MaterialTheme.colorScheme.outlineVariant
			)
			
			Spacer(modifier = Modifier.width(12.dp))
			
			Column {
				Text(
					text = progress.routineName.ifBlank { "Sin nombre" },
					style = MaterialTheme.typography.bodyLarge,
					fontWeight = FontWeight.Normal
				)
				
				Row(
					horizontalArrangement = Arrangement.spacedBy(8.dp),
					verticalAlignment = Alignment.CenterVertically
				) {
					Text(
						text = "${progress.totalElapsedTime / 60} min",
						style = MaterialTheme.typography.bodySmall,
						color = MaterialTheme.colorScheme.onSurfaceVariant
					)
					
					Text(
						text = "·",
						style = MaterialTheme.typography.bodySmall,
						color = MaterialTheme.colorScheme.onSurfaceVariant
					)
					
					Text(
						text = "${progress.progressPercentage.toInt()}%",
						style = MaterialTheme.typography.bodySmall,
						color = MaterialTheme.colorScheme.onSurfaceVariant
					)
				}
			}
		}
	}
}

fun formatDateReadable(date: String): String {
	return try {
		val parser = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
		val parsed = parser.parse(date)
		val formatter = SimpleDateFormat("EEE, dd MMM", Locale.getDefault())
		formatter.format(parsed!!).replaceFirstChar { it.uppercase() }
	} catch (e: Exception) {
		date
	}
}