package com.example.studivo.presentation.ui.routine

import android.widget.Switch
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.glance.appwidget.Switch
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.studivo.data.mapper.fromHex
import com.example.studivo.data.mapper.toHexString
import com.example.studivo.domain.model.TempPhaseItem
import com.example.studivo.domain.viewmodels.RoutineViewModel
import com.example.studivo.presentation.navegacion.AppRoutes
import kotlinx.coroutines.delay
import kotlin.collections.isNotEmpty
import com.example.studivo.presentation.utils.move
import java.util.UUID
import kotlin.math.ceil


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateRoutineScreen(
	navController: NavController,
) {
		CreateRoutineScreenContent(
			navController = navController
		)
}
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun CreateRoutineScreenContent(
	navController: NavController,
) {
	val viewModel: RoutineViewModel = hiltViewModel()
	
	// Estado de nombre de rutina
	var routineName by remember { mutableStateOf("") }
	
	val phases by remember { derivedStateOf { viewModel.tempPhases } }
	
	// Validaciones
	var showNameError by remember { mutableStateOf(false) }
	var showPhasesError by remember { mutableStateOf(false) }
	
	// Estado para el diálogo de fase
	var showPhaseDialog by remember { mutableStateOf(false) }
	var editingPhaseId by remember { mutableStateOf<String?>(null) }
	
	Scaffold(
		topBar = {
			CenterAlignedTopAppBar(
				title = {
					Text(
						"Crear Rutina",
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
						viewModel.createRoutineWithTempPhases(
							name = routineName.trim(),
							onSuccess = { navController.popBackStack() }
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
		PhaseBottomSheet(
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
fun PhaseBottomSheet(
	viewModel: RoutineViewModel,
	editingPhaseId: String?,
	onDismiss: () -> Unit,
	onSave: (TempPhaseItem) -> Unit
) {
	val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
	val scope = rememberCoroutineScope()
	
	val existingPhase = editingPhaseId?.let { id ->
		viewModel.tempPhases.find { it.tempId == id }
	}
	
	
	var phaseName by remember { mutableStateOf(existingPhase?.name ?: "") }
	var duration by remember { mutableStateOf(existingPhase?.duration?.toString() ?: "") }
	var bpm by remember { mutableStateOf(existingPhase?.bpmInitial?.toString() ?: "") }
	var selectedTimeSignature by remember { mutableStateOf(existingPhase?.timeSignature ?: "") }
	var showAdvancedOptions by remember { mutableStateOf(existingPhase?.let { it.repetitions > 0 || it.bpmMax > it.bpmInitial } ?: false)}
	var repetitions by remember { mutableStateOf(existingPhase?.repetitions?.toString() ?: "") }
	var bpmIncrement by remember { mutableStateOf(existingPhase?.bpmIncrement?.toString() ?: "") }
	var bpmMax by remember { mutableStateOf(existingPhase?.bpmMax?.toString() ?: "") }
	var selectedColor by remember {
		mutableStateOf(existingPhase?.let { it.color.fromHex() } ?: Color(0xFF2196F3))
	}
	var selectedMode by remember { mutableStateOf(existingPhase?.mode ?: "BY_REPS") }
	val isFormValid = phaseName.isNotBlank() && duration.isNotBlank()
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
							routineId = "",
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
							color = selectedColor.toHexString(), // ✅ Color -> String
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
			
			
			LazyColumn(
				verticalArrangement = Arrangement.spacedBy(24.dp),
				contentPadding = PaddingValues(bottom = 32.dp)
			) {
			
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
				
			
				item {
					AdvancedOptionsSwitch(
						isEnabled = showAdvancedOptions,
						onToggle = { showAdvancedOptions = it },
						switchEnabled = bpm.isNotBlank()
					)
				}
				
				
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

// Mantener los componentes de la lista reutilizables
@Composable
internal fun RoutineNameField(
	value: String,
	onValueChange: (String) -> Unit,
	isError: Boolean = false,
	errorMessage: String = ""
) {
	Column {
		OutlinedTextField(
			value = value,
			onValueChange = onValueChange,
			label = { Text("Nombre de la rutina") },
			placeholder = { Text("Ej: Rutina de Lunes") },
			modifier = Modifier.fillMaxWidth(),
			shape = RoundedCornerShape(12.dp),
			isError = isError,
			colors = OutlinedTextFieldDefaults.colors(
				focusedBorderColor = if (isError) MaterialTheme.colorScheme.error
				else MaterialTheme.colorScheme.primary,
				unfocusedBorderColor = if (isError) MaterialTheme.colorScheme.error
				else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
				focusedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
				unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
			),
			textStyle = MaterialTheme.typography.bodyLarge,
			singleLine = true
		)
		
		if (isError && errorMessage.isNotEmpty()) {
			Text(
				text = errorMessage,
				color = MaterialTheme.colorScheme.error,
				style = MaterialTheme.typography.bodySmall,
				modifier = Modifier.padding(start = 16.dp, top = 4.dp)
			)
		}
	}
}

@Composable
internal fun PhasesHeader(
	onClick: () -> Unit
) {
	Row(
		modifier = Modifier.fillMaxWidth(),
		horizontalArrangement = Arrangement.SpaceBetween,
		verticalAlignment = Alignment.CenterVertically
	) {
		Text(
			text = "Fases",
			style = MaterialTheme.typography.headlineSmall,
			fontWeight = FontWeight.SemiBold,
			color = MaterialTheme.colorScheme.onSurface
		)
		
		TextButton(
			onClick = onClick,
			colors = ButtonDefaults.textButtonColors(
				contentColor = MaterialTheme.colorScheme.primary
			)
		) {
			Icon(
				Icons.Default.Add,
				contentDescription = null,
				modifier = Modifier.size(18.dp)
			)
			Spacer(modifier = Modifier.width(4.dp))
			Text(
				"Añadir fase",
				style = MaterialTheme.typography.labelLarge,
				fontWeight = FontWeight.Medium
			)
		}
	}
}

@Composable
internal fun EmptyPhasesState(
	onAddPhase: () -> Unit
) {
	Column(
		modifier = Modifier
			.fillMaxWidth()
			.padding(vertical = 48.dp),
		horizontalAlignment = Alignment.CenterHorizontally
	) {
		Icon(
			Icons.Default.Add,
			contentDescription = null,
			modifier = Modifier.size(64.dp),
			tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)
		)
		
		Spacer(modifier = Modifier.height(16.dp))
		
		Text(
			text = "No hay fases agregadas",
			style = MaterialTheme.typography.titleMedium,
			color = MaterialTheme.colorScheme.onSurfaceVariant
		)
		
		Spacer(modifier = Modifier.height(8.dp))
		
		Text(
			text = "Añade al menos una fase para completar tu rutina",
			style = MaterialTheme.typography.bodyMedium,
			color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
			textAlign = TextAlign.Center
		)
		
		Spacer(modifier = Modifier.height(24.dp))
		
		Button(
			onClick = onAddPhase,
			colors = ButtonDefaults.buttonColors(
				containerColor = MaterialTheme.colorScheme.primary
			)
		) {
			Icon(
				Icons.Default.Add,
				contentDescription = null,
				modifier = Modifier.size(18.dp)
			)
			Spacer(modifier = Modifier.width(8.dp))
			Text("Añadir primera fase")
		}
	}
}

@Composable
internal fun SaveButton(
	onSave: () -> Unit,
	isEnabled: Boolean = true
) {
	Surface(
		modifier = Modifier
			.fillMaxWidth()
			.navigationBarsPadding(),
		color = MaterialTheme.colorScheme.surface,
		shadowElevation = 8.dp
	) {
		Button(
			onClick = onSave,
			enabled = isEnabled,
			modifier = Modifier
				.fillMaxWidth()
				.padding(16.dp)
				.height(56.dp),
			shape = RoundedCornerShape(28.dp),
			colors = ButtonDefaults.buttonColors(
				containerColor = MaterialTheme.colorScheme.primary,
				disabledContainerColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
			)
		) {
			Text(
				"Guardar rutina",
				style = MaterialTheme.typography.titleMedium,
				fontWeight = FontWeight.SemiBold
			)
		}
	}
}

// Componentes reutilizables de la lista
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun EnhancedPhasesReorderableList(
	phases: List<TempPhaseItem>,
	onReordered: (List<TempPhaseItem>) -> Unit,
	onItemClick: (TempPhaseItem) -> Unit,
	onDeletePhase: (TempPhaseItem) -> Unit
) {
	val dragDropState = rememberEnhancedDragDropState()
	val listState = rememberLazyListState()
	val haptic = LocalHapticFeedback.current
	
	LaunchedEffect(dragDropState.isDragging, dragDropState.targetIndex) {
		if (dragDropState.isDragging) {
			while (dragDropState.isDragging) {
				val layoutInfo = listState.layoutInfo
				val visibleItems = layoutInfo.visibleItemsInfo
				
				dragDropState.targetIndex?.let { target ->
					val firstVisibleIndex = visibleItems.firstOrNull()?.index ?: 0
					val lastVisibleIndex = visibleItems.lastOrNull()?.index ?: 0
					
					when {
						target < firstVisibleIndex -> {
							listState.animateScrollToItem(target)
						}
						target > lastVisibleIndex -> {
							listState.animateScrollToItem(target)
						}
					}
				}
				
				delay(16)
			}
		}
	}
	
	LazyColumn(
		state = listState,
		verticalArrangement = Arrangement.spacedBy(8.dp),
		contentPadding = PaddingValues(bottom = 16.dp)
	) {
		itemsIndexed(
			phases,
			key = { _, item -> item.tempId }
		) { index, item ->
			EnhancedDraggablePhaseItem(
				phase = item,
				index = index,
				dragDropState = dragDropState,
				modifier = Modifier
					.animateItemPlacement(
						animationSpec = tween(300, easing = EaseOutCubic)
					)
					.pointerInput(phases, dragDropState) {
						var itemHeight = 100f
						var accumulatedDrag = 0f
						var isDragging = false
						var longPressStartTime = 0L
						
						awaitPointerEventScope {
							while (true) {
								val event = awaitPointerEvent()
								
								when (event.type) {
									PointerEventType.Press -> {
										longPressStartTime = System.currentTimeMillis()
										isDragging = false
										accumulatedDrag = 0f
										
										val layoutInfo = listState.layoutInfo
										val currentItemInfo = layoutInfo.visibleItemsInfo.find { it.index == index }
										itemHeight = currentItemInfo?.size?.toFloat() ?: 100f
									}
									
									PointerEventType.Move -> {
										if (!isDragging) {
											val holdTime = System.currentTimeMillis() - longPressStartTime
											if (holdTime > 500) {
												haptic.performHapticFeedback(HapticFeedbackType.LongPress)
												dragDropState.startDrag(index)
												isDragging = true
											}
										}
										
										if (isDragging) {
											val change = event.changes.first()
											val dragAmount = change.position.y - change.previousPosition.y
											accumulatedDrag += dragAmount
											
											val threshold = itemHeight * 0.6f
											val positions = (accumulatedDrag / threshold).toInt()
											val newTargetIndex = (index + positions).coerceIn(0, phases.size - 1)
											
											if (newTargetIndex != dragDropState.targetIndex) {
												haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
												dragDropState.updateTarget(newTargetIndex)
											}
											
											change.consume()
										}
									}
									
									PointerEventType.Release -> {
										if (isDragging) {
											val (from, to) = dragDropState.endDrag()
											if (from != null && to != null && from != to) {
												haptic.performHapticFeedback(HapticFeedbackType.LongPress)
												val newList = phases.toMutableList()
												newList.move(from, to)
												onReordered(newList)
											}
											isDragging = false
										}
									}
								}
							}
						}
					},
				onClick = { onItemClick(item) },
				onDelete = { onDeletePhase(item) }
			)
		}
	}
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun EnhancedDraggablePhaseItem(
	phase: TempPhaseItem,
	index: Int,
	dragDropState: EnhancedDragDropState,
	modifier: Modifier = Modifier,
	onClick: () -> Unit,
	onDelete: () -> Unit
) {
	val isDragging = dragDropState.draggedIndex == index
	val isTarget = dragDropState.targetIndex == index && dragDropState.draggedIndex != index
	val shouldShowTopLine = dragDropState.targetIndex == index &&
			dragDropState.draggedIndex?.let { it > index } == true
	val shouldShowBottomLine = dragDropState.targetIndex == index &&
			dragDropState.draggedIndex?.let { it < index } == true
	
	var showOptionsMenu by remember { mutableStateOf(false) }
	
	val elevation by animateDpAsState(
		targetValue = when {
			isDragging -> 12.dp
			isTarget -> 6.dp
			else -> 2.dp
		},
		animationSpec = tween(200),
		label = "elevation"
	)
	
	val scale by animateFloatAsState(
		targetValue = if (isDragging) 1.02f else 1f,
		animationSpec = tween(200),
		label = "scale"
	)
	
	val alpha by animateFloatAsState(
		targetValue = if (isDragging) 0.85f else 1f,
		animationSpec = tween(200),
		label = "alpha"
	)
	
	Column(modifier = modifier) {
		AnimatedVisibility(
			visible = shouldShowTopLine,
			enter = fadeIn() + expandVertically(),
			exit = fadeOut() + shrinkVertically()
		) {
			Box(
				modifier = Modifier
					.fillMaxWidth()
					.height(3.dp)
					.background(
						MaterialTheme.colorScheme.primary,
						RoundedCornerShape(2.dp)
					)
					.padding(horizontal = 8.dp)
			)
		}
		
		Card(
			modifier = Modifier
				.fillMaxWidth()
				.graphicsLayer {
					scaleX = scale
					scaleY = scale
					this.alpha = alpha
				}
				.combinedClickable(
					onClick = onClick,
					onLongClick = { }
				),
			elevation = CardDefaults.cardElevation(defaultElevation = elevation),
			shape = RoundedCornerShape(16.dp),
			colors = CardDefaults.cardColors(
				containerColor = when {
					isDragging -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
					isTarget -> MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)
					else -> MaterialTheme.colorScheme.surface
				}
			),
			border = if (isTarget) BorderStroke(
				2.dp,
				MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
			) else null
		) {
			Row(
				modifier = Modifier
					.fillMaxWidth()
					.padding(16.dp),
				verticalAlignment = Alignment.CenterVertically
			) {
				Icon(
					Icons.Default.DragHandle,
					contentDescription = "Arrastrar",
					tint = if (isDragging)
						MaterialTheme.colorScheme.primary
					else
						MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
					modifier = Modifier.size(20.dp)
				)
				
				Spacer(modifier = Modifier.width(12.dp))
				
				// Color indicator
				Box(
					modifier = Modifier
						.size(48.dp)
						.clip(RoundedCornerShape(12.dp))
						.background(Color(android.graphics.Color.parseColor(phase.color)))
				)
				
				Spacer(modifier = Modifier.width(16.dp))
				
				Column(modifier = Modifier.weight(1f)) {
					Text(
						text = phase.name,
						style = MaterialTheme.typography.titleMedium,
						fontWeight = FontWeight.SemiBold,
						color = MaterialTheme.colorScheme.onSurface
					)
					
					Spacer(modifier = Modifier.height(4.dp))
					
					Row {
						Text(
							text = "${phase.duration} min",
							style = MaterialTheme.typography.bodyMedium,
							color = MaterialTheme.colorScheme.primary
						)
						
						Text(
							text = " • ",
							style = MaterialTheme.typography.bodyMedium,
							color = MaterialTheme.colorScheme.onSurfaceVariant
						)
						
						Text(
							text = "${phase.bpmInitial} BPM",
							style = MaterialTheme.typography.bodyMedium,
							color = MaterialTheme.colorScheme.primary
						)
						
						if (phase.mode == "UNTIL_BPM_MAX" && phase.bpmMax > phase.bpmInitial) {
							Text(
								text = " → ${phase.bpmMax}",
								style = MaterialTheme.typography.bodyMedium,
								color = MaterialTheme.colorScheme.secondary
							)
						}
					}
					
					if (phase.timeSignature != "4/4") {
						Text(
							text = "Compás: ${phase.timeSignature}",
							style = MaterialTheme.typography.bodySmall,
							color = MaterialTheme.colorScheme.onSurfaceVariant
						)
					}
				}
				
				AnimatedVisibility(
					visible = dragDropState.isDragging,
					enter = fadeIn() + scaleIn(),
					exit = fadeOut() + scaleOut()
				) {
					Surface(
						shape = CircleShape,
						color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
						modifier = Modifier.size(32.dp)
					) {
						Box(contentAlignment = Alignment.Center) {
							Text(
								text = "${index + 1}",
								style = MaterialTheme.typography.labelSmall,
								fontWeight = FontWeight.Bold,
								color = MaterialTheme.colorScheme.primary
							)
						}
					}
				}
				
				AnimatedVisibility(
					visible = !dragDropState.isDragging,
					enter = fadeIn(),
					exit = fadeOut()
				) {
					Box {
						IconButton(
							onClick = { showOptionsMenu = true },
							modifier = Modifier.size(40.dp)
						) {
							Icon(
								Icons.Default.MoreVert,
								contentDescription = "Opciones",
								tint = MaterialTheme.colorScheme.onSurfaceVariant,
								modifier = Modifier.size(20.dp)
							)
						}
						
						DropdownMenu(
							expanded = showOptionsMenu,
							onDismissRequest = { showOptionsMenu = false }
						) {
							DropdownMenuItem(
								text = { Text("Editar") },
								onClick = {
									showOptionsMenu = false
									onClick()
								},
								leadingIcon = {
									Icon(Icons.Default.Edit, contentDescription = null)
								}
							)
							
							DropdownMenuItem(
								text = {
									Text(
										"Eliminar",
										color = MaterialTheme.colorScheme.error
									)
								},
								onClick = {
									showOptionsMenu = false
									onDelete()
								},
								leadingIcon = {
									Icon(
										Icons.Default.Delete,
										contentDescription = null,
										tint = MaterialTheme.colorScheme.error
									)
								}
							)
						}
					}
				}
			}
		}
		
		AnimatedVisibility(
			visible = shouldShowBottomLine,
			enter = fadeIn() + expandVertically(),
			exit = fadeOut() + shrinkVertically()
		) {
			Box(
				modifier = Modifier
					.fillMaxWidth()
					.height(3.dp)
					.background(
						MaterialTheme.colorScheme.primary,
						RoundedCornerShape(2.dp)
					)
					.padding(horizontal = 8.dp)
			)
		}
	}
}

@Composable
fun rememberEnhancedDragDropState(): EnhancedDragDropState {
	return remember { EnhancedDragDropState() }
}

class EnhancedDragDropState {
	var isDragging by mutableStateOf(false)
		private set
	
	var draggedIndex by mutableStateOf<Int?>(null)
		private set
	
	var targetIndex by mutableStateOf<Int?>(null)
		private set
	
	fun startDrag(index: Int) {
		isDragging = true
		draggedIndex = index
		targetIndex = index
	}
	
	fun updateTarget(index: Int?) {
		targetIndex = index
	}
	
	fun endDrag(): Pair<Int?, Int?> {
		val from = draggedIndex
		val to = targetIndex
		isDragging = false
		draggedIndex = null
		targetIndex = null
		return Pair(from, to)
	}
}

// Extension function para mover elementos en la lista
fun <T> MutableList<T>.move(fromIndex: Int, toIndex: Int) {
	if (fromIndex == toIndex) return
	val element = removeAt(fromIndex)
	add(toIndex, element)
}


// Enum para iconos de fases
enum class PhaseIcon {
	WARMUP,
	MAIN,
	COOLDOWN
}