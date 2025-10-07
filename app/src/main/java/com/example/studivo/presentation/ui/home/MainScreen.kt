package com.example.studivo.presentation.ui.home


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.studivo.R
import com.example.studivo.domain.model.RoutineSummary
import com.example.studivo.domain.viewmodels.RoutineViewModel
import com.example.studivo.presentation.navegacion.AppRoutes


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
	navController: NavController,
) {
	MainScreenContent(navController = navController)
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreenContent(
	navController: NavController,
	viewModel: RoutineViewModel = hiltViewModel()
) {
	
	var selectedRoutine by remember { mutableStateOf<RoutineSummary?>(null) }
	var showBottomSheet by remember { mutableStateOf(false) }
	val isLoading by viewModel.isLoading.collectAsState()
	val routines = viewModel.routineSummaries
	LaunchedEffect(Unit) {
		viewModel.loadRoutines()
	}
	
	Scaffold(
		floatingActionButton = {
			FloatingActionButton(
				onClick = { navController.navigate(AppRoutes.CreateRoutineScreen) },
				containerColor =  MaterialTheme.colorScheme.onSecondaryContainer,
				contentColor = Color.White,
				elevation = FloatingActionButtonDefaults.elevation(8.dp)
			) {
				Icon(
					imageVector = Icons.Default.Add,
					contentDescription = "Nueva rutina",
					modifier = Modifier.size(28.dp)
				)
			}
		}
	) { innerPadding ->
		LazyColumn(
			modifier = Modifier
				.fillMaxSize()
				.padding(innerPadding),
			contentPadding = PaddingValues(bottom = 100.dp)
		) {
	
			// Header con saludo y acciones rápidas
			item {
				Surface(
					modifier = Modifier.fillMaxWidth(),
					color = MaterialTheme.colorScheme.surface,
					shadowElevation = 2.dp
				) {
					Column(modifier = Modifier.padding(20.dp)) {
						Row(
							modifier = Modifier.fillMaxWidth(),
							horizontalArrangement = Arrangement.SpaceBetween,
							verticalAlignment = Alignment.Top
						) {
							// Saludo
							Column(modifier = Modifier.weight(1f)) {
								Text(
									text = "Hola, Músico",
									style = MaterialTheme.typography.titleLarge.copy(
										fontWeight = FontWeight.Bold
									)
								)
								Text(
									text = "Listo para practicar?",
									style = MaterialTheme.typography.bodyMedium,
									color = MaterialTheme.colorScheme.onSurfaceVariant
								)
							}
							
							// Acciones rápidas (Metrónomo y Settings)
							Row(
								horizontalArrangement = Arrangement.spacedBy(8.dp),
								verticalAlignment = Alignment.CenterVertically
							) {
								// Botón Metrónomo
								IconButton(
									onClick = { navController.navigate(AppRoutes.MetronomeScreen) },
									modifier = Modifier
										.size(48.dp)
										.background(
											color = MaterialTheme.colorScheme.secondaryContainer,
											shape = RoundedCornerShape(12.dp)
										)
								) {
									Icon(
										painterResource(id = R.drawable.ic_metronomo),
										contentDescription = "Metrónomo",
										tint = MaterialTheme.colorScheme.onSecondaryContainer,
										modifier = Modifier.size(24.dp)
									)
								}
								
								// Botón Settings
								IconButton(
									onClick = { navController.navigate(AppRoutes.SettingsScreen) },
									modifier = Modifier
										.size(48.dp)
										.background(
											color = MaterialTheme.colorScheme.secondaryContainer,
											shape = RoundedCornerShape(12.dp)
										)
								) {
									Icon(
										Icons.Default.Settings,
										contentDescription = "Configuración",
										tint = MaterialTheme.colorScheme.onSecondaryContainer,
										modifier = Modifier.size(24.dp)
									)
								}
							}
						}
					}
				}
			}
			
			// Stats compactos y visuales
//			item {
//				Spacer(modifier = Modifier.height(16.dp))
//				WeeklyProgressCard()
//				Spacer(modifier = Modifier.height(24.dp))
//			}
			
			// Header de sección con contador
			item {
				Row(
					modifier = Modifier
						.fillMaxWidth()
						.padding(horizontal = 16.dp, vertical = 8.dp),
					horizontalArrangement = Arrangement.SpaceBetween,
					verticalAlignment = Alignment.CenterVertically
				) {
					Text(
						text = "Mis Rutinas",
						style = MaterialTheme.typography.titleLarge.copy(
							fontWeight = FontWeight.Bold
						)
					)
					Surface(
						shape = RoundedCornerShape(12.dp),
						color = MaterialTheme.colorScheme.secondaryContainer
					) {
						Text(
							text = "${routines.size}",
							modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
							style = MaterialTheme.typography.labelLarge,
							fontWeight = FontWeight.Bold,
							color = MaterialTheme.colorScheme.onSecondaryContainer
						)
					}
				}
			}
			
			// Lista de rutinas mejorada
			if (isLoading) {
				item {
					Box(
						modifier = Modifier
							.fillMaxWidth()
							.padding(vertical = 32.dp),
						contentAlignment = Alignment.Center
					) {
						CircularProgressIndicator()
					}
				}
			} else if (routines.isEmpty()) {
				item {
					EmptyStateRoutines(
						onCreateFirst = { navController.navigate(AppRoutes.CreateRoutineScreen) }
					)
				}
			} else {
				items(routines) { routine ->
					EnhancedRoutineCard(
						routine = routine,
						onClick = {
							selectedRoutine = routine
							showBottomSheet = true
						},
						modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
					)
				}
			}
		}
		
		// Bottom Sheet mejorado
		if (showBottomSheet && selectedRoutine != null) {
			RoutineActionsBottomSheet(
				routine = selectedRoutine!!,
				onDismiss = { showBottomSheet = false },
				onStart = {
					showBottomSheet = false
					
					navController.navigate("${AppRoutes.RoutinePlaybackScreen}/${selectedRoutine!!.id}")
				},
				onEdit = {
					showBottomSheet = false
					navController.navigate("${AppRoutes.EditedRoutineScreen}/${selectedRoutine!!.id}")
				},
				onDelete = {
					viewModel.deleteRoutineWithPhases(
						selectedRoutine!!.id,
						onSuccess = { viewModel.loadRoutines() },
						onError = {}
					)
				}
			)
		}
	}
}

@Composable
fun EnhancedRoutineCard(
	routine: RoutineSummary,
	onClick: () -> Unit,
	modifier: Modifier = Modifier
) {
	val randomColor = remember {
		listOf(
			Color(0xFF2196F3),
			Color(0xFF4CAF50),
			Color(0xFFFF9800),
			Color(0xFF9C27B0),
			Color(0xFFE91E63)
		).random()
	}
	
	// 🔹 Calculamos aquí los valores reales
	val totalDuration = routine.totalDuration
	val totalPhases = routine.totalPhases
	
	Card(
		modifier = modifier.fillMaxWidth(),
		onClick = onClick,
		colors = CardDefaults.cardColors(
			containerColor = MaterialTheme.colorScheme.surface
		),
		shape = RoundedCornerShape(16.dp),
		elevation = CardDefaults.cardElevation(2.dp)
	) {
		Row(
			modifier = Modifier
				.fillMaxWidth()
				.padding(16.dp),
			verticalAlignment = Alignment.CenterVertically,
		) {
			// Icono de color
			Box(
				modifier = Modifier
					.size(56.dp)
					.background(
						color = randomColor.copy(alpha = 0.15f),
						shape = RoundedCornerShape(14.dp)
					),
				contentAlignment = Alignment.Center
			) {
				Icon(
					Icons.Default.MusicNote,
					contentDescription = null,
					tint = randomColor,
					modifier = Modifier.size(28.dp)
				)
			}
			
			Spacer(modifier = Modifier.width(16.dp))
			
			// Info
			Column(modifier = Modifier.weight(1f)) {
				Text(
					text = routine.name,
					style = MaterialTheme.typography.titleMedium.copy(
						fontWeight = FontWeight.SemiBold
					),
					maxLines = 1,
					overflow = TextOverflow.Ellipsis
				)
				
				Spacer(modifier = Modifier.height(4.dp))
				
				Row(
					horizontalArrangement = Arrangement.spacedBy(16.dp),
					verticalAlignment = Alignment.CenterVertically
				) {
					Row(verticalAlignment = Alignment.CenterVertically) {
						Icon(
							Icons.Default.Timer,
							contentDescription = null,
							modifier = Modifier.size(16.dp),
							tint = MaterialTheme.colorScheme.onSurfaceVariant
						)
						Spacer(modifier = Modifier.width(4.dp))
						Text(
							text = routine.getFormattedDuration(), // 🔹 aquí se muestra la duración total
							style = MaterialTheme.typography.bodySmall,
							color = MaterialTheme.colorScheme.onSurfaceVariant
						)
					}
					
					Row(verticalAlignment = Alignment.CenterVertically) {
						Icon(
							Icons.Default.Layers,
							contentDescription = null,
							modifier = Modifier.size(16.dp),
							tint = MaterialTheme.colorScheme.onSurfaceVariant
						)
						Spacer(modifier = Modifier.width(4.dp))
						Text(
							text = "$totalPhases fases",
							style = MaterialTheme.typography.bodySmall,
							color = MaterialTheme.colorScheme.onSurfaceVariant
						)
					}
				}
			}
			
			// Botón de acción rápida
			IconButton(
				onClick = onClick,
				modifier = Modifier
					.size(40.dp)
					.background(
						color = randomColor.copy(alpha = 0.1f),
						shape = CircleShape
					)
			) {
				Icon(
					Icons.Default.PlayArrow,
					contentDescription = "Iniciar",
					tint = randomColor
				)
			}
		}
	}
}




@Composable
fun ProgressItem(
	label: String,
	value: String,
	maxValue: String,
	progress: Float,
	color: Color
) {
	Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
		Row(
			modifier = Modifier.fillMaxWidth(),
			horizontalArrangement = Arrangement.SpaceBetween
		) {
			Text(
				text = label,
				style = MaterialTheme.typography.bodyMedium,
				color = MaterialTheme.colorScheme.onSurfaceVariant
			)
			Text(
				text = "$value / $maxValue",
				style = MaterialTheme.typography.bodyMedium,
				fontWeight = FontWeight.SemiBold,
				color = color
			)
		}
		
		androidx.compose.material3.LinearProgressIndicator(
			progress = progress,
			modifier = Modifier
				.fillMaxWidth()
				.height(8.dp)
				.clip(RoundedCornerShape(4.dp)),
			color = color,
			trackColor = MaterialTheme.colorScheme.surfaceVariant
		)
	}
}



// Estado vacío mejorado
@Composable
fun EmptyStateRoutines(
	onCreateFirst: () -> Unit
) {
	Column(
		modifier = Modifier
			.fillMaxWidth()
			.padding(horizontal = 32.dp, vertical = 48.dp),
		horizontalAlignment = Alignment.CenterHorizontally
	) {
		// Ilustración simple
		Box(
			modifier = Modifier
				.size(120.dp)
				.background(
					color = MaterialTheme.colorScheme.secondaryContainer,
					shape = CircleShape
				),
			contentAlignment = Alignment.Center
		) {
			Icon(
				Icons.Default.MusicNote,
				contentDescription = null,
				modifier = Modifier.size(60.dp),
				tint = MaterialTheme.colorScheme.onSecondaryContainer
			)
		}
		
		Spacer(modifier = Modifier.height(24.dp))
		
		Text(
			text = "Comienza tu viaje musical",
			style = MaterialTheme.typography.titleLarge.copy(
				fontWeight = FontWeight.Bold
			),
			textAlign = TextAlign.Center
		)
		
		Spacer(modifier = Modifier.height(8.dp))
		
		Text(
			text = "Crea tu primera rutina de práctica y alcanza tus metas musicales",
			style = MaterialTheme.typography.bodyMedium,
			color = MaterialTheme.colorScheme.onSurfaceVariant,
			textAlign = TextAlign.Center
		)
		
	}
}

// Bottom Sheet mejorado con opción de eliminar
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoutineActionsBottomSheet(
	routine: RoutineSummary,
	onDismiss: () -> Unit,
	onStart: () -> Unit,
	onEdit: () -> Unit,
	onDelete: () -> Unit
) {
	val sheetState = rememberModalBottomSheetState()
	var showDeleteDialog by remember { mutableStateOf(false) }
	
	ModalBottomSheet(
		onDismissRequest = onDismiss,
		sheetState = sheetState,
		dragHandle = { BottomSheetDefaults.DragHandle() }
	) {
		Column(
			modifier = Modifier
				.fillMaxWidth()
				.padding(horizontal = 24.dp, vertical = 16.dp),
			horizontalAlignment = Alignment.CenterHorizontally
		) {
			// Header
			Box(
				modifier = Modifier
					.size(80.dp)
					.background(
						color = MaterialTheme.colorScheme.primaryContainer,
						shape = CircleShape
					),
				contentAlignment = Alignment.Center
			) {
				Icon(
					Icons.Default.MusicNote,
					contentDescription = null,
					modifier = Modifier.size(40.dp),
					tint = MaterialTheme.colorScheme.onPrimaryContainer
				)
			}
			
			Spacer(modifier = Modifier.height(16.dp))
			
			Text(
				text = routine.name,
				style = MaterialTheme.typography.titleLarge.copy(
					fontWeight = FontWeight.Bold
				),
				textAlign = TextAlign.Center
			)
			
			Spacer(modifier = Modifier.height(8.dp))
			
			Row(
				horizontalArrangement = Arrangement.spacedBy(16.dp),
				verticalAlignment = Alignment.CenterVertically
			) {
				Row(verticalAlignment = Alignment.CenterVertically) {
					Icon(
						Icons.Default.Timer,
						contentDescription = null,
						modifier = Modifier.size(18.dp),
						tint = MaterialTheme.colorScheme.onSurfaceVariant
					)
					Spacer(modifier = Modifier.width(4.dp))
					Text(
						text = routine.getFormattedDuration(), // 🔹 aquí se muestra la duración total
						style = MaterialTheme.typography.bodyMedium,
						color = MaterialTheme.colorScheme.onSurfaceVariant
					)
				}
				
				Row(verticalAlignment = Alignment.CenterVertically) {
					Icon(
						Icons.Default.Layers,
						contentDescription = null,
						modifier = Modifier.size(18.dp),
						tint = MaterialTheme.colorScheme.onSurfaceVariant
					)
					Spacer(modifier = Modifier.width(4.dp))
					Text(
						text = "${routine.totalPhases} fases",
						style = MaterialTheme.typography.bodyMedium,
						color = MaterialTheme.colorScheme.onSurfaceVariant
					)
				}
			}
			
			Spacer(modifier = Modifier.height(32.dp))
			
			// Botón Principal - Iniciar (más prominente)
			Button(
				onClick = onStart,
				modifier = Modifier
					.fillMaxWidth()
					.height(56.dp),
				contentPadding = PaddingValues(vertical = 16.dp),
				shape = RoundedCornerShape(16.dp),
				colors = ButtonDefaults.buttonColors(
					containerColor = Color(0xFF4CAF50)
				),
				elevation = ButtonDefaults.buttonElevation(
					defaultElevation = 4.dp,
					pressedElevation = 8.dp
				)
			) {
				Icon(
					Icons.Default.PlayArrow,
					contentDescription = null,
					modifier = Modifier.size(24.dp)
				)
				Spacer(modifier = Modifier.width(8.dp))
				Text(
					"Iniciar Práctica",
					style = MaterialTheme.typography.titleMedium,
					fontWeight = FontWeight.Bold
				)
			}
			
			Spacer(modifier = Modifier.height(12.dp))
			
			// Botón Secundario - Editar
			OutlinedButton(
				onClick = onEdit,
				modifier = Modifier
					.fillMaxWidth()
					.height(48.dp),
				contentPadding = PaddingValues(vertical = 12.dp),
				shape = RoundedCornerShape(16.dp),
				border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.outline)
			) {
				Icon(
					Icons.Default.Edit,
					contentDescription = null,
					modifier = Modifier.size(20.dp)
				)
				Spacer(modifier = Modifier.width(8.dp))
				Text(
					"Editar Rutina",
					style = MaterialTheme.typography.bodyLarge,
					fontWeight = FontWeight.Medium
				)
			}
			
			Spacer(modifier = Modifier.height(8.dp))
			
			// Botón de Eliminar - Discreto pero accesible
			TextButton(
				onClick = { showDeleteDialog = true },
				modifier = Modifier.fillMaxWidth(),
				shape = RoundedCornerShape(12.dp)
			) {
				Icon(
					Icons.Default.Delete,
					contentDescription = null,
					modifier = Modifier.size(18.dp),
					tint = Color(0xFFE53935)
				)
				Spacer(modifier = Modifier.width(6.dp))
				Text(
					"Eliminar rutina",
					style = MaterialTheme.typography.bodyMedium,
					color = Color(0xFFE53935)
				)
			}
			
			Spacer(modifier = Modifier.height(16.dp))
		}
	}
	
	// Diálogo de confirmación para eliminar
	if (showDeleteDialog) {
		AlertDialog(
			onDismissRequest = { showDeleteDialog = false },
			icon = {
				Icon(
					Icons.Default.Delete,
					contentDescription = null,
					tint = Color(0xFFE53935),
					modifier = Modifier.size(32.dp)
				)
			},
			title = {
				Text(
					text = "¿Eliminar rutina?",
					fontWeight = FontWeight.Bold
				)
			},
			text = {
				Text(
					text = "Esta acción eliminará permanentemente \"${routine.name}\" y todas sus fases. No se puede deshacer.",
					textAlign = TextAlign.Center
				)
			},
			confirmButton = {
				Button(
					onClick = {
						showDeleteDialog = false
						onDelete()
						onDismiss()
					},
					colors = ButtonDefaults.buttonColors(
						containerColor = Color(0xFFE53935)
					)
				) {
					Text("Eliminar")
				}
			},
			dismissButton = {
				TextButton(onClick = { showDeleteDialog = false }) {
					Text("Cancelar")
				}
			},
			shape = RoundedCornerShape(24.dp)
		)
	}
}


// Progreso semanal más visual
@Composable
fun WeeklyProgressCard() {
	Card(
		modifier = Modifier
			.fillMaxWidth()
			.padding(horizontal = 16.dp),
		colors = CardDefaults.cardColors(
			containerColor = MaterialTheme.colorScheme.surface
		),
		shape = RoundedCornerShape(20.dp),
		elevation = CardDefaults.cardElevation(2.dp)
	) {
		Column(modifier = Modifier.padding(20.dp)) {
			Row(
				modifier = Modifier.fillMaxWidth(),
				horizontalArrangement = Arrangement.SpaceBetween,
				verticalAlignment = Alignment.CenterVertically
			) {
				Text(
					text = "Esta Semana",
					style = MaterialTheme.typography.titleMedium.copy(
						fontWeight = FontWeight.Bold
					)
				)
				Icon(
					Icons.Default.TrendingUp,
					contentDescription = null,
					tint = Color(0xFF4CAF50),
					modifier = Modifier.size(24.dp)
				)
			}
			
			Spacer(modifier = Modifier.height(20.dp))
			
			// Barra de progreso visual
			Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
				ProgressItem(
					label = "Tiempo Practicado",
					value = "2.5h",
					maxValue = "5h",
					progress = 0.5f,
					color = Color(0xFF2196F3)
				)
				ProgressItem(
					label = "Rutinas Completadas",
					value = "8",
					maxValue = "15",
					progress = 0.53f,
					color = Color(0xFF4CAF50)
				)
				ProgressItem(
					label = "Racha Actual",
					value = "5 días",
					maxValue = "7 días",
					progress = 0.71f,
					color = Color(0xFFFF9800)
				)
			}
		}
	}
}