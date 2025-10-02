package com.example.studivo.presentation.ui.routine

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.glance.appwidget.Switch
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.studivo.domain.model.TempPhaseItem
import com.example.studivo.domain.viewmodels.RoutineViewModel
import com.example.studivo.presentation.navegacion.AppRoutes
import java.util.UUID
import kotlin.collections.isNotEmpty
import kotlin.math.ceil



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditedRoutineScreen(
	navController: NavController,
	routineId: String
) {
	EditedRoutineScreenContent(
		navController = navController,
		routineId = routineId
	)
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun EditedRoutineScreenContent(
	navController: NavController,
	routineId: String
) {
	val viewModel: RoutineViewModel = hiltViewModel()
	
	// Nombre de la rutina
	var routineName by remember { mutableStateOf("") }
	
	// Lista de fases temporal editable
	val phases by remember { derivedStateOf { viewModel.tempPhases } }
	
	// Validaciones
	var showNameError by remember { mutableStateOf(false) }
	var showPhasesError by remember { mutableStateOf(false) }
	
	// Estado para el diálogo de fase
	var showPhaseDialog by remember { mutableStateOf(false) }
	var editingPhaseId by remember { mutableStateOf<String?>(null) }
	
	// Cargar rutina al iniciar la pantalla
	LaunchedEffect(routineId) {
		Log.d("EditRoutineUnified", "Cargando rutina con ID: $routineId")
		viewModel.loadRoutineForEditing(routineId)
	}
	
	// Sincronizar el nombre cargado desde ViewModel
	LaunchedEffect(viewModel.routineName) {
		routineName = viewModel.routineName
		Log.d("EditRoutineUnified", "Nombre de rutina cargado: $routineName" )
	}
	
	LaunchedEffect(phases) {
		phases.forEach { phase ->
			Log.d(
				"EditRoutineUnified",
				"""
            ---- Fase ----
            id: ${phase.tempId}
            nombre: ${phase.name}
            duración: ${phase.duration}
            bpmInitial: ${phase.bpmInitial}
            timeSignature: ${phase.timeSignature}
            color (Int): ${phase.color}
            color (hex): #${Integer.toHexString(phase.color)}
            modo: ${phase.mode}
            repeticiones: ${phase.repetitions}
            bpmIncrement: ${phase.bpmIncrement}
            bpmMax: ${phase.bpmMax}
            routineId: ${phase.routineId}
            ----------------
            """.trimIndent()
			)
		}
	}
	
	Scaffold(
		topBar = {
			CenterAlignedTopAppBar(
				title = {
					Text(
						"Editar rutina",
						style = MaterialTheme.typography.titleLarge,
						fontWeight = FontWeight.SemiBold
					)
				},
				navigationIcon = {
					IconButton(onClick = { navController.popBackStack() }) {
						Icon(
							Icons.Default.ArrowBack,
							contentDescription = "Volver",
							tint = MaterialTheme.colorScheme.primary
						)
					}
				},
				colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
					containerColor = MaterialTheme.colorScheme.surface
				)
			)
		},
		bottomBar = {
			SaveButton(
				onSave = {
					showNameError = routineName.isBlank()
					showPhasesError = phases.isEmpty()
					if (!showNameError && !showPhasesError) {
						viewModel.updateRoutineWithTempPhases(
							routineId = routineId,
							name = routineName.trim(),
							onSuccess = { navController.popBackStack() },
							onError = { error ->
								Log.e("EditRoutineUnified", "Error al guardar: $error")
							}
						)
					}
				},
				isEnabled = routineName.isNotBlank() && phases.isNotEmpty()
			)
		}
	) { innerPadding ->
		Column(
			modifier = Modifier
				.fillMaxSize()
				.padding(innerPadding)
				.padding(horizontal = 16.dp)
		) {
			Spacer(modifier = Modifier.height(16.dp))
			
			// Campo nombre rutina
			RoutineNameField(
				value = routineName,
				onValueChange = {
					routineName = it
					showNameError = false
				},
				isError = showNameError,
				errorMessage = "El nombre de la rutina es obligatorio"
			)
			
			Spacer(modifier = Modifier.height(24.dp))
			
			// Header fases
			PhasesHeader(
				onClick = {
					editingPhaseId = null
					showPhaseDialog = true
				}
			)
			
			if (showPhasesError) {
				Text(
					text = "Debes agregar al menos una fase",
					color = MaterialTheme.colorScheme.error,
					style = MaterialTheme.typography.bodySmall,
					modifier = Modifier.padding(start = 16.dp, top = 4.dp)
				)
			}
			
			Spacer(modifier = Modifier.height(16.dp))
			
			// Lista fases
			if (phases.isNotEmpty()) {
				EnhancedPhasesReorderableList(
					phases = phases,
					onReordered = { newList -> viewModel.reorderTempPhases(newList) },
					onItemClick = { phase ->
						Log.d("EditRoutineUnified", "Clicked fase: ${phase.tempId} - ${phase.name}")
						editingPhaseId = phase.tempId
						showPhaseDialog = true
					},
					onDeletePhase = { phase -> viewModel.removeTempPhase(phase.tempId) }
				)
			} else {
				EmptyPhasesState(
					onAddPhase = {
						editingPhaseId = null
						showPhaseDialog = true
					}
				)
			}
		}
	}
	
	// Diálogo de fase (bottom sheet)
	if (showPhaseDialog) {
		EditPhaseBottomSheet(
			viewModel = viewModel,
			editingPhaseId = editingPhaseId,
			onDismiss = {
				showPhaseDialog = false
				editingPhaseId = null
			},
			onSave = { phase ->
				if (editingPhaseId != null) {
					viewModel.updateTempPhase(phase)
				} else {
					viewModel.addTempPhase(phase)
				}
				showPhaseDialog = false
				editingPhaseId = null
			}
		)
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPhaseBottomSheet(
	viewModel: RoutineViewModel,
	editingPhaseId: String?,
	onDismiss: () -> Unit,
	onSave: (TempPhaseItem) -> Unit
) {
	val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
	val scope = rememberCoroutineScope()
	
	// 🔹 Fase reactiva desde tempPhases
	val currentPhase = remember(viewModel.tempPhases, editingPhaseId) {
		derivedStateOf {
			editingPhaseId?.let { id ->
				viewModel.tempPhases.find { it.tempId == id }
			}
		}
	}.value
	
	// Estados del formulario
	var phaseName by remember { mutableStateOf("") }
	var duration by remember { mutableStateOf("") }
	var bpm by remember { mutableStateOf("") }
	var selectedTimeSignature by remember { mutableStateOf("4/4") }
	var showAdvancedOptions by remember { mutableStateOf(false) }
	var repetitions by remember { mutableStateOf("") }
	var bpmIncrement by remember { mutableStateOf("") }
	var bpmMax by remember { mutableStateOf("") }
	var selectedColor by remember { mutableStateOf(Color(0xFF2196F3)) }
	var selectedMode by remember { mutableStateOf("BY_REPS") }
	
	// 🔹 Cargar campos locales cuando la fase cambie
	LaunchedEffect(currentPhase) {
		currentPhase?.let {
			phaseName = it.name
			duration = it.duration.toString()
			bpm = it.bpmInitial.toString()
			selectedTimeSignature = it.timeSignature
			repetitions = if (it.repetitions > 0) it.repetitions.toString() else ""
			bpmIncrement = if (it.bpmIncrement > 0) it.bpmIncrement.toString() else ""
			bpmMax = if (it.bpmMax > 0) it.bpmMax.toString() else ""
			selectedColor = Color(it.color)
			selectedMode = it.mode
			showAdvancedOptions = it.repetitions > 0 || it.bpmMax > it.bpmInitial
		} ?: run {
			// Si no existe en tempPhases, cargar desde Room
			editingPhaseId?.let { id ->
				viewModel.getPhaseById(id) { dbPhase ->
					dbPhase?.let {
						val mapped = TempPhaseItem(
							tempId = it.id,
							routineId = it.routineId,
							name = it.name,
							duration = it.duration,
							bpmInitial = it.bpm,
							timeSignature = it.timeSignature,
							repetitions = it.repetitions,
							bpmIncrement = it.bpmIncrement,
							bpmMax = it.bpmMax,
							color = it.color.toArgb(), // <-- convertir Color -> Int ARGB
							mode = it.mode
						)
						viewModel.addTempPhase(mapped)
					}
				}
			}
		}
	}
	
	val timeSignatures = listOf("4/4", "3/4", "2/4", "6/8")
	val availableColors = listOf(
		Color(0xFF2196F3),
		Color(0xFF4CAF50),
		Color(0xFFFFC107),
		Color(0xFFF44336),
		Color(0xFF9C27B0),
		Color(0xFFE91E63),
		Color(0xFF3F51B5)
	)
	
	val isFormValid = phaseName.isNotBlank() && duration.isNotBlank()
	
	ModalBottomSheet(
		onDismissRequest = onDismiss,
		sheetState = sheetState,
		dragHandle = { BottomSheetDefaults.DragHandle() },
		containerColor = MaterialTheme.colorScheme.surface,
		modifier = Modifier.fillMaxHeight(0.95f)
	) {
		Column(
			modifier = Modifier
				.fillMaxSize()
				.padding(horizontal = 16.dp)
		) {
			// Header
			Row(
				modifier = Modifier
					.fillMaxWidth()
					.padding(bottom = 16.dp),
				horizontalArrangement = Arrangement.SpaceBetween,
				verticalAlignment = Alignment.CenterVertically
			) {
				Text(
					text = if (editingPhaseId != null) "Editar Fase" else "Nueva Fase",
					style = MaterialTheme.typography.titleLarge,
					fontWeight = FontWeight.SemiBold
				)
				
				TextButton(
					enabled = isFormValid,
					onClick = {
						val phase = TempPhaseItem(
							tempId = editingPhaseId ?: UUID.randomUUID().toString(),
							routineId = currentPhase?.routineId ?: "",
							name = phaseName,
							duration = duration.toIntOrNull() ?: 0,
							bpmInitial = bpm.toIntOrNull() ?: 0,
							timeSignature = selectedTimeSignature,
							repetitions = if (showAdvancedOptions && selectedMode == "BY_REPS")
								repetitions.toIntOrNull() ?: 0 else 0,
							bpmIncrement = if (showAdvancedOptions)
								bpmIncrement.toIntOrNull() ?: 0 else 0,
							bpmMax = if (showAdvancedOptions && selectedMode == "UNTIL_BPM_MAX")
								bpmMax.toIntOrNull() ?: 0 else 0,
							color = selectedColor.toArgb(),
							mode = if (showAdvancedOptions) selectedMode else "BY_REPS"
						)
						onSave(phase)
					}
				) {
					Text(
						"GUARDAR",
						style = MaterialTheme.typography.labelLarge,
						fontWeight = FontWeight.Bold,
						color = if (isFormValid) MaterialTheme.colorScheme.primary
						else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
					)
				}
			}
			
			// Contenido scrolleable
			LazyColumn(
				verticalArrangement = Arrangement.spacedBy(24.dp),
				contentPadding = PaddingValues(bottom = 32.dp)
			) {
				// Datos básicos
				item {
					BasicDataSection(
						phaseName = phaseName,
						onPhaseNameChange = { phaseName = it },
						duration = duration,
						onDurationChange = { duration = it },
						bpm = bpm,
						onBpmChange = { bpm = it },
						selectedTimeSignature = selectedTimeSignature,
						onTimeSignatureChange = { selectedTimeSignature = it },
						timeSignatures = timeSignatures,
						selectedColor = selectedColor,
						onColorChange = { selectedColor = it },
						availableColors = availableColors
					)
				}
				
				// Switch Opciones avanzadas
				item {
					AdvancedOptionsSwitch(
						isEnabled = showAdvancedOptions,
						onToggle = { showAdvancedOptions = it },
						switchEnabled = bpm.isNotBlank()
					)
				}
				
				// Opciones avanzadas
				if (showAdvancedOptions) {
					item {
						AdvancedOptionsSection(
							selectedMode = selectedMode,
							onModeChange = { selectedMode = it },
							bpmInitial = bpm,
							duration = duration,
							repetitions = repetitions,
							onRepetitionsChange = { repetitions = it },
							bpmIncrement = bpmIncrement,
							onBpmIncrementChange = { bpmIncrement = it },
							bpmMax = bpmMax,
							onBpmMaxChange = { bpmMax = it }
						)
					}
				}
			}
		}
	}
}

@Composable
private fun BasicDataSection(
	phaseName: String,
	onPhaseNameChange: (String) -> Unit,
	duration: String,
	onDurationChange: (String) -> Unit,
	bpm: String,
	onBpmChange: (String) -> Unit,
	selectedTimeSignature: String,
	onTimeSignatureChange: (String) -> Unit,
	timeSignatures: List<String>,
	selectedColor: Color,
	onColorChange: (Color) -> Unit,
	availableColors: List<Color>,
) {
	Card(
		modifier = Modifier.fillMaxWidth(),
		shape = RoundedCornerShape(16.dp),
		colors = CardDefaults.cardColors(
			containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
		)
	) {
		Column(
			modifier = Modifier.padding(20.dp),
			verticalArrangement = Arrangement.spacedBy(16.dp)
		) {
			Text(
				text = "Datos básicos",
				style = MaterialTheme.typography.titleMedium,
				fontWeight = FontWeight.SemiBold,
				color = MaterialTheme.colorScheme.onSurface
			)
			
			OutlinedTextField(
				value = phaseName,
				onValueChange = onPhaseNameChange,
				modifier = Modifier.fillMaxWidth(),
				label = { Text("Nombre de la fase") },
				placeholder = { Text("Ej: Calentamiento") },
				shape = RoundedCornerShape(12.dp)
			)
			
			Row(
				modifier = Modifier.fillMaxWidth(),
				horizontalArrangement = Arrangement.spacedBy(16.dp)
			) {
				OutlinedTextField(
					value = duration,
					onValueChange = onDurationChange,
					modifier = Modifier.weight(1f),
					label = { Text("Duración (min)") },
					placeholder = { Text("Ej: 10") },
					keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
					shape = RoundedCornerShape(12.dp)
				)
				OutlinedTextField(
					value = bpm,
					onValueChange = onBpmChange,
					modifier = Modifier.weight(1f),
					label = { Text("BPM inicial") },
					placeholder = { Text("Ej: 60") },
					keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
					shape = RoundedCornerShape(12.dp)
				)
			}
			
			LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
				items(timeSignatures) { timeSignature ->
					TimeSignatureChip(
						timeSignature = timeSignature,
						isSelected = selectedTimeSignature == timeSignature,
						onSelect = { onTimeSignatureChange(timeSignature) }
					)
				}
			}
			
			LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
				items(availableColors) { color ->
					ColorOption(
						color = color,
						isSelected = selectedColor == color,
						onSelect = { onColorChange(color) }
					)
				}
			}
		}
	}
}

@Composable
private fun AdvancedOptionsSwitch(
	isEnabled: Boolean,
	onToggle: (Boolean) -> Unit,
	switchEnabled: Boolean,
) {
	Row(
		modifier = Modifier.fillMaxWidth(),
		horizontalArrangement = Arrangement.SpaceBetween,
		verticalAlignment = Alignment.CenterVertically
	) {
		Text(
			text = "Opciones avanzadas",
			style = MaterialTheme.typography.bodyLarge,
			color = MaterialTheme.colorScheme.onSurface
		)
		androidx.compose.material3.Switch(
			checked = isEnabled,
			onCheckedChange = { if (switchEnabled) onToggle(it) },
			enabled = switchEnabled,
			colors = SwitchDefaults.colors(
				checkedThumbColor = MaterialTheme.colorScheme.primary,
				checkedTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
			)
		)
	}
}

@Composable
private fun AdvancedOptionsSection(
	selectedMode: String,
	onModeChange: (String) -> Unit,
	bpmInitial: String,
	duration: String,
	repetitions: String,
	onRepetitionsChange: (String) -> Unit,
	bpmIncrement: String,
	onBpmIncrementChange: (String) -> Unit,
	bpmMax: String,
	onBpmMaxChange: (String) -> Unit
) {
	Card(
		modifier = Modifier.fillMaxWidth(),
		shape = RoundedCornerShape(16.dp),
		colors = CardDefaults.cardColors(
			containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
		)
	) {
		Column(
			modifier = Modifier.padding(20.dp),
			verticalArrangement = Arrangement.spacedBy(16.dp)
		) {
			Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
				Text(
					"Modo:",
					style = MaterialTheme.typography.bodyMedium,
					fontWeight = FontWeight.Medium
				)
				Row(
					horizontalArrangement = Arrangement.spacedBy(16.dp),
					verticalAlignment = Alignment.CenterVertically
				) {
					FilterChip(
						selected = selectedMode == "BY_REPS",
						onClick = { onModeChange("BY_REPS") },
						label = { Text("Por repeticiones") }
					)
					FilterChip(
						selected = selectedMode == "UNTIL_BPM_MAX",
						onClick = { onModeChange("UNTIL_BPM_MAX") },
						label = { Text("Hasta BPM máximo") }
					)
				}
			}
			
			OutlinedTextField(
				value = bpmInitial,
				onValueChange = {},
				modifier = Modifier.fillMaxWidth(),
				readOnly = true,
				label = { Text("BPM inicial") },
				shape = RoundedCornerShape(12.dp)
			)
			
			if (selectedMode == "BY_REPS") {
				AdvancedOptionsByReps(
					bpmInitial = bpmInitial,
					duration = duration,
					repetitions = repetitions,
					onRepetitionsChange = onRepetitionsChange,
					bpmIncrement = bpmIncrement,
					onBpmIncrementChange = onBpmIncrementChange
				)
			} else {
				AdvancedOptionsUntilBpmMax(
					bpmInitial = bpmInitial,
					duration = duration,
					bpmIncrement = bpmIncrement,
					onBpmIncrementChange = onBpmIncrementChange,
					bpmMax = bpmMax,
					onBpmMaxChange = onBpmMaxChange
				)
			}
		}
	}
}

@Composable
private fun AdvancedOptionsByReps(
	bpmInitial: String,
	duration: String,
	repetitions: String,
	onRepetitionsChange: (String) -> Unit,
	bpmIncrement: String,
	onBpmIncrementChange: (String) -> Unit
) {
	val bpmInt = bpmInitial.toIntOrNull() ?: 0
	val incrementInt = bpmIncrement.toIntOrNull() ?: 0
	val repsInt = repetitions.toIntOrNull() ?: 0
	val durationInt = duration.toIntOrNull() ?: 0
	val calculatedBpmMax = if (bpmInt > 0 && repsInt > 0) bpmInt + (repsInt - 1) * incrementInt else bpmInt
	val totalDuration = durationInt * repsInt
	
	Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
		OutlinedTextField(
			value = repetitions,
			onValueChange = onRepetitionsChange,
			modifier = Modifier.fillMaxWidth(),
			label = { Text("Número de repeticiones") },
			placeholder = { Text("Ej: 5") },
			keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
			shape = RoundedCornerShape(12.dp)
		)
		
		OutlinedTextField(
			value = bpmIncrement,
			onValueChange = onBpmIncrementChange,
			modifier = Modifier.fillMaxWidth(),
			label = { Text("Incremento de BPM por repetición") },
			placeholder = { Text("Ej: 5") },
			keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
			shape = RoundedCornerShape(12.dp)
		)
		
		Card(
			modifier = Modifier.fillMaxWidth(),
			shape = RoundedCornerShape(12.dp),
			colors = CardDefaults.cardColors(
				containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
			)
		) {
			Column(
				modifier = Modifier.padding(16.dp),
				verticalArrangement = Arrangement.spacedBy(8.dp)
			) {
				ResultRow(label = "BPM máximo", value = calculatedBpmMax.toString())
				ResultRow(label = "Duración total aproximada", value = "$totalDuration min")
			}
		}
	}
}

@Composable
private fun AdvancedOptionsUntilBpmMax(
	bpmInitial: String,
	duration: String,
	bpmIncrement: String,
	onBpmIncrementChange: (String) -> Unit,
	bpmMax: String,
	onBpmMaxChange: (String) -> Unit
) {
	val bpmInt = bpmInitial.toIntOrNull() ?: 0
	val incrementInt = bpmIncrement.toIntOrNull() ?: 0
	val bpmMaxInt = bpmMax.toIntOrNull() ?: 0
	val durationInt = duration.toIntOrNull() ?: 0
	
	val calculatedReps = if (bpmInt > 0 && incrementInt > 0 && bpmMaxInt > bpmInt) {
		ceil((bpmMaxInt - bpmInt).toDouble() / incrementInt).toInt() + 1
	} else 0
	
	val totalDuration = durationInt * calculatedReps
	
	Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
		OutlinedTextField(
			value = bpmIncrement,
			onValueChange = onBpmIncrementChange,
			modifier = Modifier.fillMaxWidth(),
			label = { Text("Incremento de BPM por repetición") },
			placeholder = { Text("Ej: 5") },
			keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
			shape = RoundedCornerShape(12.dp)
		)
		
		OutlinedTextField(
			value = bpmMax,
			onValueChange = onBpmMaxChange,
			modifier = Modifier.fillMaxWidth(),
			label = { Text("BPM máximo") },
			placeholder = { Text("Ej: 100") },
			keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
			shape = RoundedCornerShape(12.dp)
		)
		
		Card(
			modifier = Modifier.fillMaxWidth(),
			shape = RoundedCornerShape(12.dp),
			colors = CardDefaults.cardColors(
				containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
			)
		) {
			Column(
				modifier = Modifier.padding(16.dp),
				verticalArrangement = Arrangement.spacedBy(8.dp)
			) {
				ResultRow(label = "Repeticiones necesarias", value = calculatedReps.toString())
				ResultRow(label = "Duración total aproximada", value = "$totalDuration min")
			}
		}
	}
}

@Composable
private fun ResultRow(label: String, value: String) {
	Row(
		modifier = Modifier.fillMaxWidth(),
		horizontalArrangement = Arrangement.SpaceBetween,
		verticalAlignment = Alignment.CenterVertically
	) {
		Text(label, style = MaterialTheme.typography.bodyMedium)
		Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimeSignatureChip(
	timeSignature: String,
	isSelected: Boolean,
	onSelect: () -> Unit,
) {
	FilterChip(
		selected = isSelected,
		onClick = onSelect,
		label = {
			Text(
				text = timeSignature,
				style = MaterialTheme.typography.labelLarge,
				fontWeight = FontWeight.Medium
			)
		},
		shape = RoundedCornerShape(50.dp),
		colors = FilterChipDefaults.filterChipColors(
			selectedContainerColor = MaterialTheme.colorScheme.primary,
			selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
			containerColor = MaterialTheme.colorScheme.surface,
			labelColor = MaterialTheme.colorScheme.onSurface
		),
		border = FilterChipDefaults.filterChipBorder(
			enabled = true,
			selected = isSelected,
			borderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
			selectedBorderColor = Color.Transparent
		)
	)
}

@Composable
private fun ColorOption(
	color: Color,
	isSelected: Boolean,
	onSelect: () -> Unit,
) {
	Box(
		modifier = Modifier
			.size(40.dp)
			.clip(CircleShape)
			.background(color)
			.border(
				width = if (isSelected) 3.dp else 0.dp,
				color = MaterialTheme.colorScheme.onSurface,
				shape = CircleShape
			)
			.clickable { onSelect() }
	)
}