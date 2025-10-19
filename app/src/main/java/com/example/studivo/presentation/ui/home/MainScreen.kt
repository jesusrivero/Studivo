package com.example.studivo.presentation.ui.home


import android.graphics.Bitmap
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.studivo.R
import com.example.studivo.domain.model.DayProgress
import com.example.studivo.domain.model.RoutineSummary
import com.example.studivo.domain.viewmodels.RoutineProgressViewModel
import com.example.studivo.domain.viewmodels.RoutineShareViewModel
import com.example.studivo.domain.viewmodels.RoutineViewModel
import com.example.studivo.domain.viewmodels.ShareUiState
import com.example.studivo.presentation.navegacion.AppRoutes
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


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
	viewModel: RoutineViewModel = hiltViewModel(),
	progressViewModel: RoutineProgressViewModel = hiltViewModel(),
	shareViewModel: RoutineShareViewModel = hiltViewModel(),
) {
	var selectedRoutine by remember { mutableStateOf<RoutineSummary?>(null) }
	var showBottomSheet by remember { mutableStateOf(false) }
	var showShareDialog by remember { mutableStateOf(false) }
	val isLoading by viewModel.isLoading.collectAsState()
	val routines = viewModel.routineSummaries
	val currentStreak by progressViewModel.currentStreak.collectAsState()
	val progressCalendar by progressViewModel.progressCalendar.collectAsState()
	val shareUiState by shareViewModel.uiState.collectAsState()
	val qrBitmap by shareViewModel.qrBitmap.collectAsState()
	
	LaunchedEffect(Unit) {
		viewModel.loadRoutines()
		progressViewModel.initializeCalendarIfNeeded()
		progressViewModel.loadCurrentStreak()
	}
	
	Scaffold(
		floatingActionButton = {
			FloatingActionButton(
				onClick = { navController.navigate(AppRoutes.CreateRoutineScreen) },
				containerColor = MaterialTheme.colorScheme.primary,
				contentColor = MaterialTheme.colorScheme.onPrimary,
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
		Column(
			modifier = Modifier
				.fillMaxSize()
				.padding(innerPadding)
		) {
			Surface(
				modifier = Modifier.fillMaxWidth(),
				color = MaterialTheme.colorScheme.background,
			) {
				Column(modifier = Modifier.padding(20.dp)) {
					Row(
						modifier = Modifier.fillMaxWidth(),
						horizontalArrangement = Arrangement.SpaceBetween,
						verticalAlignment = Alignment.CenterVertically
					) {
						
						Row(
							modifier = Modifier
								.background(
									color = if (currentStreak > 0)
										MaterialTheme.colorScheme.tertiaryContainer
									else
										MaterialTheme.colorScheme.surfaceVariant,
									shape = RoundedCornerShape(12.dp),
								)
								.height(40.dp)
								.padding(horizontal = 10.dp),
							verticalAlignment = Alignment.CenterVertically,
							horizontalArrangement = Arrangement.spacedBy(6.dp)
						) {
							Icon(
								painter = painterResource(id = R.drawable.ic_fire),
								contentDescription = "Racha",
								tint = if (currentStreak > 0)
									Color(0xFFFF9800)
								else
									MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
								modifier = Modifier.size(20.dp)
							)
							Text(
								text = "$currentStreak ${if (currentStreak == 1) "día" else "días"}",
								style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
								color = if (currentStreak > 0)
									MaterialTheme.colorScheme.onTertiaryContainer
								else
									MaterialTheme.colorScheme.onSurfaceVariant
							)
						}
						
						Row(
							horizontalArrangement = Arrangement.spacedBy(8.dp),
							verticalAlignment = Alignment.CenterVertically
						) {
							IconButton(
								onClick = { navController.navigate(AppRoutes.QRScannerScreen) },
								modifier = Modifier
									.size(48.dp)
									.background(
										color = MaterialTheme.colorScheme.secondaryContainer,
										shape = RoundedCornerShape(12.dp)
									)
							) {
								Icon(
									painter = painterResource(id = R.drawable.ic_qr_scan),
									contentDescription = "Escanear QR",
									tint = MaterialTheme.colorScheme.onSecondaryContainer,
									modifier = Modifier.size(24.dp)
								)
							}
							
							IconButton(
								onClick = { navController.navigate(AppRoutes.HistoryScreen) },
								modifier = Modifier
									.size(48.dp)
									.background(
										color = MaterialTheme.colorScheme.secondaryContainer,
										shape = RoundedCornerShape(12.dp)
									)
							) {
								Icon(
									painterResource(id = R.drawable.ic_history),
									contentDescription = "Historial",
									tint = MaterialTheme.colorScheme.onSecondaryContainer,
									modifier = Modifier.size(24.dp)
								)
							}
							
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
			
			
			ProgressCalendarGrid(
				progressDays = progressCalendar,
				modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
			)
			
			Spacer(modifier = Modifier.height(8.dp))
			
			
			Row(
				modifier = Modifier
					.fillMaxWidth()
					.padding(horizontal = 20.dp, vertical = 8.dp),
				horizontalArrangement = Arrangement.SpaceBetween,
				verticalAlignment = Alignment.CenterVertically
			) {
				Text(
					text = "Mis Rutinas",
					style = MaterialTheme.typography.titleLarge,
					fontWeight = FontWeight.Bold
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
			
			
			LazyColumn(
				modifier = Modifier
					.fillMaxSize()
					.padding(horizontal = 20.dp)
			) {
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
						EmptyStateRoutines()
					}
				} else {
					items(routines) { routine ->
						MinimalRoutineItem(
							routine = routine,
							progressViewModel = progressViewModel,
							onClick = {
								selectedRoutine = routine
								showBottomSheet = true
							},
							onPlayClick = {
								navController.navigate("${AppRoutes.RoutinePlaybackScreen}/${routine.id}")
							},
							modifier = Modifier.padding(vertical = 6.dp)
						)
					}
					item {
						Spacer(modifier = Modifier.height(80.dp))
					}
				}
			}
		}
		
		
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
				},
				onShare = {
					shareViewModel.generateQRForRoutine(selectedRoutine!!.id)
					showShareDialog = true
				}
			)
		}
		
		
		if (showShareDialog && selectedRoutine != null) {
			val errorMessage = when (val state = shareUiState) {
				is ShareUiState.Error -> state.message
				else -> null
			}
			
			ShareRoutineDialog(
				routineName = selectedRoutine!!.name,
				qrBitmap = qrBitmap,
				isLoading = shareUiState is ShareUiState.Loading,
				errorMessage = errorMessage,
				onDismiss = {
					showShareDialog = false
					shareViewModel.resetShareState()
				},
				onShare = {
					shareViewModel.shareQRCode()
				}
			)
		}
	}
}

@Composable
fun MinimalRoutineItem(
	routine: RoutineSummary,
	onClick: () -> Unit,
	onPlayClick: () -> Unit,
	modifier: Modifier = Modifier,
	progressViewModel: RoutineProgressViewModel,
) {
	val progress by progressViewModel.getProgressForRoutine(routine.id).collectAsState()
	val progressPercentage = progress?.progressPercentage ?: 0
	
	
	Surface(
		modifier = modifier.fillMaxWidth(),
		onClick = onClick,
		color = MaterialTheme.colorScheme.surface,
		shape = RoundedCornerShape(16.dp),
		tonalElevation = 1.dp
	) {
		Row(
			modifier = Modifier
				.fillMaxWidth()
				.padding(16.dp),
			horizontalArrangement = Arrangement.SpaceBetween,
			verticalAlignment = Alignment.CenterVertically
		) {
			
			Row(
				modifier = Modifier.weight(1f),
				verticalAlignment = Alignment.CenterVertically,
				horizontalArrangement = Arrangement.spacedBy(12.dp)
			) {
				
				Box(
					modifier = Modifier
						.size(48.dp)
						.background(
							color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
							shape = RoundedCornerShape(12.dp)
						),
					contentAlignment = Alignment.Center
				) {
					Icon(
						Icons.Default.MusicNote,
						contentDescription = null,
						tint = MaterialTheme.colorScheme.primary,
						modifier = Modifier.size(24.dp)
					)
				}
				
				
				Column(
					modifier = Modifier.weight(1f),
					verticalArrangement = Arrangement.spacedBy(4.dp)
				) {
					Text(
						text = routine.name,
						style = MaterialTheme.typography.titleMedium,
						fontWeight = FontWeight.SemiBold,
						maxLines = 1,
						overflow = TextOverflow.Ellipsis
					)
					
					Row(
						horizontalArrangement = Arrangement.spacedBy(12.dp),
						verticalAlignment = Alignment.CenterVertically
					) {
						Text(
							text = routine.getFormattedDuration(),
							style = MaterialTheme.typography.bodySmall,
							color = MaterialTheme.colorScheme.onSurfaceVariant
						)
						Text(
							text = "·",
							style = MaterialTheme.typography.bodySmall,
							color = MaterialTheme.colorScheme.onSurfaceVariant
						)
						Text(
							text = "${routine.totalPhases} fases",
							style = MaterialTheme.typography.bodySmall,
							color = MaterialTheme.colorScheme.onSurfaceVariant
						)
						
						if (progressPercentage > 0) {
							Text(
								text = "·",
								style = MaterialTheme.typography.bodySmall,
								color = MaterialTheme.colorScheme.onSurfaceVariant
							)
							Text(
								text = "$progressPercentage%",
								style = MaterialTheme.typography.bodySmall,
								color = MaterialTheme.colorScheme.primary,
								fontWeight = FontWeight.Medium
							)
						}
					}
				}
			}
			
			
			IconButton(
				onClick = {
					progressViewModel.updateProgress(
						routineId = routine.id,
						currentPhaseIndex = 0,
						progressPercentage = progressPercentage
					)
					onPlayClick()
				},
				modifier = Modifier
					.size(40.dp)
					.background(
						color = MaterialTheme.colorScheme.primary,
						shape = CircleShape
					)
			) {
				Icon(
					Icons.Default.PlayArrow,
					contentDescription = "Iniciar",
					tint = MaterialTheme.colorScheme.onPrimary,
					modifier = Modifier.size(20.dp)
				)
			}
		}
	}
}


@Composable
fun EmptyStateRoutines() {
	Column(
		modifier = Modifier
			.fillMaxWidth()
			.padding(horizontal = 32.dp, vertical = 10.dp),
		horizontalAlignment = Alignment.CenterHorizontally
	) {
		// Logo circular
		Box(
			contentAlignment = Alignment.Center
		) {
			Box(
				modifier = Modifier
					.size(140.dp)
					.background(
						color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
						shape = CircleShape
					)
			)
			
			Box(
				modifier = Modifier
					.size(150.dp)
					.background(
						color = MaterialTheme.colorScheme.primaryContainer,
						shape = CircleShape
					),
				contentAlignment = Alignment.Center
			) {
				Icon(
					painter = painterResource(id = R.drawable.ic_studivo_logo_2),
					contentDescription = "Studivo Logo",
					modifier = Modifier.size(100.dp),
					tint = MaterialTheme.colorScheme.primary
				)
			}
		}
		
		Spacer(modifier = Modifier.height(10.dp))
		
		// Título
		Text(
			text = "Comienza tu viaje musical",
			style = MaterialTheme.typography.headlineSmall,
			fontWeight = FontWeight.Bold,
			textAlign = TextAlign.Center,
			color = MaterialTheme.colorScheme.onSurface
		)
		
		Spacer(modifier = Modifier.height(12.dp))
		
		// Descripción
		Text(
			text = "Crea tu primera rutina de práctica personalizada y alcanza tus metas musicales paso a paso",
			style = MaterialTheme.typography.bodyLarge,
			color = MaterialTheme.colorScheme.onSurfaceVariant,
			textAlign = TextAlign.Center,
			lineHeight = 24.sp
		)
		
		Spacer(modifier = Modifier.height(32.dp))
		
		// Features - Centradas horizontalmente
		Column(
			verticalArrangement = Arrangement.spacedBy(16.dp),
			horizontalAlignment = Alignment.CenterHorizontally,
			modifier = Modifier.fillMaxWidth()
		) {
			// Feature 1
			Row(
				modifier = Modifier.wrapContentWidth(),
				verticalAlignment = Alignment.Top,
				horizontalArrangement = Arrangement.spacedBy(12.dp)
			) {
				Box(
					modifier = Modifier
						.size(40.dp)
						.background(
							color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f),
							shape = RoundedCornerShape(10.dp)
						),
					contentAlignment = Alignment.Center
				) {
					Icon(
						imageVector = Icons.Default.Timer,
						contentDescription = null,
						modifier = Modifier.size(20.dp),
						tint = MaterialTheme.colorScheme.primary
					)
				}
				
				Column(
					modifier = Modifier.widthIn(max = 300.dp)
				) {
					Text(
						text = "Sesiones cronometradas",
						style = MaterialTheme.typography.titleSmall,
						fontWeight = FontWeight.SemiBold,
						color = MaterialTheme.colorScheme.onSurface
					)
					Text(
						text = "Organiza tu práctica con tiempos precisos",
						style = MaterialTheme.typography.bodySmall,
						color = MaterialTheme.colorScheme.onSurfaceVariant
					)
				}
			}
			
			// Feature 2
			Row(
				modifier = Modifier.wrapContentWidth(),
				verticalAlignment = Alignment.Top,
				horizontalArrangement = Arrangement.spacedBy(12.dp)
			) {
				Box(
					modifier = Modifier
						.size(40.dp)
						.background(
							color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f),
							shape = RoundedCornerShape(10.dp)
						),
					contentAlignment = Alignment.Center
				) {
					Icon(
						imageVector = Icons.Default.Layers,
						contentDescription = null,
						modifier = Modifier.size(20.dp),
						tint = MaterialTheme.colorScheme.primary
					)
				}
				
				Column(
					modifier = Modifier.widthIn(max = 300.dp)
				) {
					Text(
						text = "Fases personalizadas",
						style = MaterialTheme.typography.titleSmall,
						fontWeight = FontWeight.SemiBold,
						color = MaterialTheme.colorScheme.onSurface
					)
					Text(
						text = "Divide tu rutina en secciones enfocadas",
						style = MaterialTheme.typography.bodySmall,
						color = MaterialTheme.colorScheme.onSurfaceVariant
					)
				}
			}
			
			// Feature 3
			Row(
				modifier = Modifier.wrapContentWidth(),
				verticalAlignment = Alignment.Top,
				horizontalArrangement = Arrangement.spacedBy(12.dp)
			) {
				Box(
					modifier = Modifier
						.size(40.dp)
						.background(
							color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f),
							shape = RoundedCornerShape(10.dp)
						),
					contentAlignment = Alignment.Center
				) {
					Icon(
						imageVector = Icons.Default.PlayArrow,
						contentDescription = null,
						modifier = Modifier.size(20.dp),
						tint = MaterialTheme.colorScheme.primary
					)
				}
				
				Column(
					modifier = Modifier.widthIn(max = 300.dp)
				) {
					Text(
						text = "Practica sin interrupciones",
						style = MaterialTheme.typography.titleSmall,
						fontWeight = FontWeight.SemiBold,
						color = MaterialTheme.colorScheme.onSurface
					)
					Text(
						text = "Modo de reproducción automático",
						style = MaterialTheme.typography.bodySmall,
						color = MaterialTheme.colorScheme.onSurfaceVariant
					)
				}
			}
		}
	}
}


@Composable
fun ProgressCalendarGrid(
	progressDays: List<DayProgress>,
	modifier: Modifier = Modifier,
) {
	val listState = rememberLazyListState()
	val scrolled = remember { mutableStateOf(false) }
	// Obtener fecha actual
	val sdf = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }
	val today = remember { sdf.format(Date()) }
	// Organizar días por semanas (Lunes a Domingo)
	val weekGroups = remember(progressDays) { organizeDaysByWeeks(progressDays) }
	
	// Scroll automático a la semana actual
	LaunchedEffect(weekGroups.size) {
		if (weekGroups.isNotEmpty() && !scrolled.value) {
			listState.scrollToItem(weekGroups.lastIndex)
			scrolled.value = true
		}
	}
	
	LazyRow(
		state = listState,
		horizontalArrangement = Arrangement.spacedBy(16.dp),
		modifier = modifier.fillMaxWidth(),
		contentPadding = PaddingValues(horizontal = 4.dp)
	) {
		items(weekGroups.size) { weekIndex ->
			WeekGroup(
				days = weekGroups[weekIndex],
				today = today
			)
		}
	}
}

@Composable
fun WeekGroup(
	days: List<DayProgress>,
	today: String,
) {
	Row(
		horizontalArrangement = Arrangement.spacedBy(6.dp)
	) {
		days.forEach { day ->
			val isToday = day.date == today
			val backgroundColor = when {
				day.hasCompletedRoutine -> Color(0xFF4CAF50)
				day.hasProgress -> Color(0xFF4CAF50).copy(alpha = 0.3f)
				else -> Color.LightGray.copy(alpha = 0.3f)
			}
			
			Box(
				modifier = Modifier
					.size(45.dp)
					.background(
						color = backgroundColor,
						shape = RoundedCornerShape(8.dp)
					)
					.then(
						if (isToday) {
							Modifier.border(
								width = 2.dp,
								color = MaterialTheme.colorScheme.primary,
								shape = RoundedCornerShape(8.dp)
							)
						} else Modifier
					),
				contentAlignment = Alignment.Center
			) {
				Column(
					horizontalAlignment = Alignment.CenterHorizontally,
					verticalArrangement = Arrangement.Center
				) {
					Text(
						text = day.dayOfMonth.toString(),
						style = MaterialTheme.typography.labelSmall,
						fontSize = 18.sp,
						color = if (day.hasCompletedRoutine)
							Color.White
						else
							MaterialTheme.colorScheme.onSurface,
						fontWeight = if (isToday)
							FontWeight.Bold
						else if (day.hasCompletedRoutine)
							FontWeight.SemiBold
						else
							FontWeight.Normal
					)
					Text(
						text = day.dayOfWeekInitial,
						style = MaterialTheme.typography.labelSmall,
						fontSize = 11.sp,
						color = if (day.hasCompletedRoutine)
							Color.White.copy(alpha = 0.8f)
						else
							MaterialTheme.colorScheme.onSurfaceVariant
					)
				}
			}
		}
	}
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoutineActionsBottomSheet(
	routine: RoutineSummary,
	onDismiss: () -> Unit,
	onStart: () -> Unit,
	onEdit: () -> Unit,
	onDelete: () -> Unit,
	onShare: () -> Unit,
) {
	val sheetState = rememberModalBottomSheetState()
	var showDeleteDialog by remember { mutableStateOf(false) }
	
	ModalBottomSheet(
		onDismissRequest = onDismiss,
		sheetState = sheetState,
		containerColor = MaterialTheme.colorScheme.surface,
		dragHandle = { BottomSheetDefaults.DragHandle() }
	) {
		Column(
			modifier = Modifier
				.fillMaxWidth()
				.padding(horizontal = 24.dp, vertical = 16.dp),
			horizontalAlignment = Alignment.CenterHorizontally
		) {
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
						text = routine.getFormattedDuration(),
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
			
			Button(
				onClick = onStart,
				modifier = Modifier
					.fillMaxWidth()
					.height(56.dp),
				contentPadding = PaddingValues(vertical = 16.dp),
				shape = RoundedCornerShape(16.dp),
				colors = ButtonDefaults.buttonColors(
					containerColor = MaterialTheme.colorScheme.primary
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
			
			OutlinedButton(
				onClick = {
					onShare()
					onDismiss()
				},
				modifier = Modifier
					.fillMaxWidth()
					.height(48.dp),
				contentPadding = PaddingValues(vertical = 12.dp),
				shape = RoundedCornerShape(16.dp),
				border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary)
			) {
				Icon(
					Icons.Default.Share,
					contentDescription = null,
					modifier = Modifier.size(20.dp),
					tint = MaterialTheme.colorScheme.primary
				)
				Spacer(modifier = Modifier.width(8.dp))
				Text(
					"Compartir Rutina",
					style = MaterialTheme.typography.bodyLarge,
					fontWeight = FontWeight.Medium,
					color = MaterialTheme.colorScheme.primary
				)
			}
			
			Spacer(modifier = Modifier.height(8.dp))
			
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
			
			TextButton(
				onClick = { showDeleteDialog = true },
				modifier = Modifier.fillMaxWidth(),
				shape = RoundedCornerShape(12.dp)
			) {
				Icon(
					Icons.Default.Delete,
					contentDescription = null,
					modifier = Modifier.size(18.dp),
					tint = MaterialTheme.colorScheme.error
				)
				Spacer(modifier = Modifier.width(6.dp))
				Text(
					"Eliminar rutina",
					style = MaterialTheme.typography.bodyMedium,
					color = MaterialTheme.colorScheme.error
				)
			}
			
			Spacer(modifier = Modifier.height(16.dp))
		}
	}
	
	if (showDeleteDialog) {
		AlertDialog(
			onDismissRequest = { showDeleteDialog = false },
			icon = {
				Icon(
					Icons.Default.Delete,
					contentDescription = null,
					tint = MaterialTheme.colorScheme.error,
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
						containerColor = MaterialTheme.colorScheme.error
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
			containerColor = MaterialTheme.colorScheme.surface,
			shape = RoundedCornerShape(24.dp)
		)
	}
}


@Composable
fun ShareRoutineDialog(
	routineName: String,
	qrBitmap: Bitmap?,
	isLoading: Boolean,
	errorMessage: String?,
	onDismiss: () -> Unit,
	onShare: () -> Unit,
) {
	Dialog(onDismissRequest = onDismiss) {
		Surface(
			shape = RoundedCornerShape(24.dp),
			color = MaterialTheme.colorScheme.surface,
			tonalElevation = 8.dp
		) {
			Column(
				modifier = Modifier
					.fillMaxWidth()
					.padding(24.dp),
				horizontalAlignment = Alignment.CenterHorizontally
			) {
				
				Row(
					modifier = Modifier.fillMaxWidth(),
					horizontalArrangement = Arrangement.SpaceBetween,
					verticalAlignment = Alignment.CenterVertically,
					
					) {
					Text(
						text = "Compartir Rutina",
						style = MaterialTheme.typography.headlineSmall,
						fontWeight = FontWeight.Bold
					)
					IconButton(onClick = onDismiss) {
						Icon(Icons.Default.Close, contentDescription = "Cerrar")
					}
				}
				
				Spacer(modifier = Modifier.height(16.dp))
				
				
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
				
				Spacer(modifier = Modifier.height(24.dp))
				
				
				Box(
					modifier = Modifier
						.size(300.dp)
						.background(
							color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
							shape = RoundedCornerShape(16.dp)
						),
					contentAlignment = Alignment.Center
				) {
					when {
						isLoading -> {
							CircularProgressIndicator()
						}
						
						errorMessage != null -> {
							Column(
								horizontalAlignment = Alignment.CenterHorizontally,
								verticalArrangement = Arrangement.spacedBy(8.dp),
								modifier = Modifier.padding(16.dp)
							) {
								Text(
									text = "Error",
									style = MaterialTheme.typography.titleMedium,
									fontWeight = FontWeight.Bold,
									color = MaterialTheme.colorScheme.error
								)
								Text(
									text = errorMessage,
									style = MaterialTheme.typography.bodyMedium,
									color = MaterialTheme.colorScheme.onSurfaceVariant,
									textAlign = TextAlign.Center
								)
							}
						}
						
						qrBitmap != null -> {
							Surface(
								shape = RoundedCornerShape(12.dp),
								color = androidx.compose.ui.graphics.Color.White,
								modifier = Modifier
									.size(280.dp)
									.padding(8.dp)
							) {
								Image(
									bitmap = qrBitmap.asImageBitmap(),
									contentDescription = "Código QR",
									modifier = Modifier
										.fillMaxSize()
										.padding(12.dp)
								)
							}
						}
						
						else -> {
							Text(
								text = "Generando código...",
								style = MaterialTheme.typography.bodyMedium,
								color = MaterialTheme.colorScheme.onSurfaceVariant
							)
						}
					}
				}
				
				Spacer(modifier = Modifier.height(24.dp))
				
				Text(
					text = if (qrBitmap != null)
						"Escanea este código QR para importar la rutina en otro dispositivo"
					else
						"El código QR se está generando...",
					style = MaterialTheme.typography.bodyMedium,
					color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
					textAlign = TextAlign.Center
				)
				
				Spacer(modifier = Modifier.height(24.dp))
				
				Button(
					onClick = onShare,
					modifier = Modifier
						.fillMaxWidth()
						.height(48.dp),
					shape = RoundedCornerShape(16.dp),
					enabled = qrBitmap != null && !isLoading
				) {
					Icon(
						Icons.Default.Share,
						contentDescription = null,
						modifier = Modifier.size(20.dp)
					)
					Spacer(modifier = Modifier.width(8.dp))
					Text(
						"Compartir QR",
						style = MaterialTheme.typography.bodyLarge,
						fontWeight = FontWeight.SemiBold
					)
				}
			}
		}
	}
}


// Función para organizar días en semanas (Lunes a Domingo)
private fun organizeDaysByWeeks(days: List<DayProgress>): List<List<DayProgress>> {
	if (days.isEmpty()) return emptyList()
	
	val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
	val calendar = Calendar.getInstance()
	
	// Encontrar el primer lunes desde el primer día con progreso
	val firstDate = sdf.parse(days.first().date)!!
	calendar.time = firstDate
	
	// Retroceder al lunes de esa semana
	while (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
		calendar.add(Calendar.DAY_OF_MONTH, -1)
	}
	
	val startOfFirstWeek = calendar.clone() as Calendar
	
	// Encontrar la última fecha
	val lastDate = sdf.parse(days.last().date)!!
	calendar.time = lastDate
	
	// Avanzar al domingo de esa semana
	while (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
		calendar.add(Calendar.DAY_OF_MONTH, 1)
	}
	
	val endOfLastWeek = calendar.clone() as Calendar
	
	// Crear mapa de días por fecha para búsqueda rápida
	val daysMap = days.associateBy { it.date }
	
	// Generar todas las semanas
	val weeks = mutableListOf<List<DayProgress>>()
	calendar.time = startOfFirstWeek.time
	
	while (calendar.timeInMillis <= endOfLastWeek.timeInMillis) {
		val week = mutableListOf<DayProgress>()
		
		// Generar 7 días (Lunes a Domingo)
		for (i in 0..6) {
			val dateString = sdf.format(calendar.time)
			val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
			val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
			
			val dayInitial = when (dayOfWeek) {
				Calendar.MONDAY -> "L"
				Calendar.TUESDAY -> "M"
				Calendar.WEDNESDAY -> "M"
				Calendar.THURSDAY -> "J"
				Calendar.FRIDAY -> "V"
				Calendar.SATURDAY -> "S"
				Calendar.SUNDAY -> "D"
				else -> ""
			}
			
			// Buscar si existe progreso para este día
			val existingDay = daysMap[dateString]
			
			week.add(
				existingDay ?: DayProgress(
					date = dateString,
					dayOfMonth = dayOfMonth,
					dayOfWeekInitial = dayInitial,
					hasProgress = false,
					hasCompletedRoutine = false,
					totalProgressPercentage = 0,
					routinesCount = 0
				)
			)
			
			calendar.add(Calendar.DAY_OF_MONTH, 1)
		}
		
		weeks.add(week)
	}
	
	return weeks
}