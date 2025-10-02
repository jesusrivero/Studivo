//package com.example.studivo.presentation.ui.phases
//
//import androidx.compose.foundation.background
//import androidx.compose.foundation.border
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.PaddingValues
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.size
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.LazyRow
//import androidx.compose.foundation.lazy.items
//import androidx.compose.foundation.shape.CircleShape
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.foundation.text.KeyboardOptions
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.Close
//import androidx.compose.material3.Card
//import androidx.compose.material3.CardDefaults
//import androidx.compose.material3.CenterAlignedTopAppBar
//import androidx.compose.material3.ExperimentalMaterial3Api
//import androidx.compose.material3.FilterChip
//import androidx.compose.material3.FilterChipDefaults
//import androidx.compose.material3.Icon
//import androidx.compose.material3.IconButton
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.OutlinedTextField
//import androidx.compose.material3.Scaffold
//import androidx.compose.material3.SnackbarHost
//import androidx.compose.material3.SnackbarHostState
//import androidx.compose.material3.Switch
//import androidx.compose.material3.SwitchDefaults
//import androidx.compose.material3.Text
//import androidx.compose.material3.TextButton
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.rememberCoroutineScope
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.graphics.toArgb
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.text.input.KeyboardType
//import androidx.compose.ui.unit.dp
//import androidx.hilt.navigation.compose.hiltViewModel
//import androidx.navigation.NavController
//import com.example.studivo.domain.model.TempPhaseItem
//import com.example.studivo.domain.viewmodels.RoutineViewModel
//import com.example.studivo.presentation.navegacion.AppRoutes
//import com.example.studivo.presentation.ui.routine.PhaseIcon
////import com.example.studivo.presentation.ui.routine.PhaseIcon
//import kotlinx.coroutines.launch
//import java.util.UUID
//import kotlin.math.ceil
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun CreateFaseScreen(
//	navController: NavController,
//) {
//	CreateFaseScreenContent(navController = navController)
//}
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun CreateFaseScreenContent(
//	navController: NavController,
//) {
//	val parentEntry = remember {
//		navController.getBackStackEntry(AppRoutes.CreateRoutineScreen)
//	}
//	val viewModel: RoutineViewModel = hiltViewModel(parentEntry)
//	// Estados locales
//	var phaseName by remember { mutableStateOf("") }
//	var duration by remember { mutableStateOf("") }
//	var bpm by remember { mutableStateOf("") }
//	var selectedTimeSignature by remember { mutableStateOf("4/4") }
//	var showAdvancedOptions by remember { mutableStateOf(false) }
//	var repetitions by remember { mutableStateOf("") }
//	var bpmIncrement by remember { mutableStateOf("") }
//	var bpmMax by remember { mutableStateOf("") }
//	var selectedColor by remember { mutableStateOf(Color(0xFF2196F3)) }
//	var selectedMode by remember { mutableStateOf("BY_REPS") }
//	var selectedIcon by remember { mutableStateOf<PhaseIcon>(PhaseIcon.WARMUP) }
//
//
//	val timeSignatures = listOf("4/4", "3/4", "2/4", "6/8")
//	val availableColors = listOf(
//		Color(0xFF2196F3),
//		Color(0xFF4CAF50),
//		Color(0xFFFFC107),
//		Color(0xFFF44336),
//		Color(0xFF9C27B0),
//		Color(0xFFE91E63),
//		Color(0xFF3F51B5)
//	)
//
//	val bpmInitial = bpm
//
//	// Validación de campos obligatorios
//	val isFormValid = phaseName.isNotBlank() && duration.isNotBlank()
//
//	// Snackbar state
//	val snackbarHostState = remember { SnackbarHostState() }
//	val scope = rememberCoroutineScope()
//
//	Scaffold(
//		topBar = {
//			CenterAlignedTopAppBar(
//				title = { Text("Crear Fase", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold) },
//				navigationIcon = {
//					IconButton(onClick = { navController.popBackStack() }) {
//						Icon(Icons.Default.Close, contentDescription = "Cerrar")
//					}
//				},
//				actions = {
//					TextButton(
//						enabled = isFormValid,
//						onClick = {
//							// Construir TempPhaseItem
//							val phase = TempPhaseItem(
//								tempId = UUID.randomUUID().toString(), // 🔹 id temporal
//								routineId = "",
//								name = phaseName,
//								duration = duration.toIntOrNull() ?: 0,
//								bpmInitial = bpm.toIntOrNull() ?: 0,
//								timeSignature = selectedTimeSignature,
//								repetitions = repetitions.toIntOrNull() ?: 0,
//								bpmIncrement = bpmIncrement.toIntOrNull() ?: 0,
//								bpmMax = bpmMax.toIntOrNull() ?: 0,
////								totalDuration = calculateTotalDuration(
////									duration.toIntOrNull(),
////									repetitions.toIntOrNull()
////								),
//								color = selectedColor.toArgb(),
//								mode = selectedMode,
////								icon = selectedIcon
//							)
//
//							viewModel.addTempPhase(phase)
//
//							// Mostrar Snackbar de éxito
//							scope.launch {
//								snackbarHostState.showSnackbar("Fase creada exitosamente")
//							}
//
//							navController.popBackStack()
//						}
//					) {
//						Text(
//							"GUARDAR",
//							style = MaterialTheme.typography.labelLarge,
//							fontWeight = FontWeight.Bold,
//							color = if (isFormValid) MaterialTheme.colorScheme.primary
//							else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
//						)
//					}
//				}
//			)
//		},
//		snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
//	) { innerPadding ->
//		LazyColumn(
//			modifier = Modifier
//				.fillMaxSize()
//				.padding(innerPadding)
//				.padding(horizontal = 16.dp),
//			verticalArrangement = Arrangement.spacedBy(24.dp),
//			contentPadding = PaddingValues(vertical = 16.dp)
//		) {
//			// Datos básicos
//			item {
//				BasicDataSection(
//					phaseName = phaseName,
//					onPhaseNameChange = { phaseName = it },
//					duration = duration,
//					onDurationChange = { duration = it },
//					bpm = bpm,
//					onBpmChange = { bpm = it },
//					selectedTimeSignature = selectedTimeSignature,
//					onTimeSignatureChange = { selectedTimeSignature = it },
//					timeSignatures = timeSignatures,
//					selectedColor = selectedColor,
//					onColorChange = { selectedColor = it },
//					availableColors = availableColors
//				)
//			}
//
//			// Switch Opciones avanzadas
//			item {
//				AdvancedOptionsSwitch(
//					isEnabled = showAdvancedOptions,
//					onToggle = { showAdvancedOptions = it },
//					switchEnabled = bpm.isNotBlank()
//				)
//			}
//
//			// Opciones avanzadas
//			if (showAdvancedOptions) {
//				item {
//					AdvancedOptionsSection(
//						bpmInitial = bpmInitial,
//						duration = duration
//					)
//				}
//			}
//		}
//	}
//}
//
//// Calcula la duración total aproximada
//private fun calculateTotalDuration(
//	duration: Int?,
//	repetitions: Int?,
//): Int {
//	val dur = duration ?: 0
//	val reps = repetitions ?: 0
//	return dur * if (reps > 0) reps else 1
//}
//
//// --- Switch ---
//@Composable
//private fun AdvancedOptionsSwitch(
//	isEnabled: Boolean,
//	onToggle: (Boolean) -> Unit,
//	switchEnabled: Boolean, // ✅ nuevo parámetro para habilitar/deshabilitar
//) {
//	Row(
//		modifier = Modifier.fillMaxWidth(),
//		horizontalArrangement = Arrangement.SpaceBetween,
//		verticalAlignment = Alignment.CenterVertically
//	) {
//		Text(
//			text = "Opciones avanzadas",
//			style = MaterialTheme.typography.bodyLarge,
//			color = MaterialTheme.colorScheme.onSurface
//		)
//		Switch(
//			checked = isEnabled,
//			onCheckedChange = { if (switchEnabled) onToggle(it) },
//			enabled = switchEnabled, // ✅ deshabilitado si no hay BPM inicial
//			colors = SwitchDefaults.colors(
//				checkedThumbColor = MaterialTheme.colorScheme.primary,
//				checkedTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
//			)
//		)
//	}
//}
//
//
//// --- BasicDataSection con labels ---
//@Composable
//private fun BasicDataSection(
//	phaseName: String,
//	onPhaseNameChange: (String) -> Unit,
//	duration: String,
//	onDurationChange: (String) -> Unit,
//	bpm: String,
//	onBpmChange: (String) -> Unit,
//	selectedTimeSignature: String,
//	onTimeSignatureChange: (String) -> Unit,
//	timeSignatures: List<String>,
//	selectedColor: Color,
//	onColorChange: (Color) -> Unit,
//	availableColors: List<Color>,
//) {
//	Card(
//		modifier = Modifier.fillMaxWidth(),
//		shape = RoundedCornerShape(16.dp),
//		colors = CardDefaults.cardColors(
//			containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
//		)
//	) {
//		Column(
//			modifier = Modifier.padding(20.dp),
//			verticalArrangement = Arrangement.spacedBy(16.dp)
//		) {
//			Text(
//				text = "Datos básicos",
//				style = MaterialTheme.typography.titleMedium,
//				fontWeight = FontWeight.SemiBold,
//				color = MaterialTheme.colorScheme.onSurface
//			)
//
//			OutlinedTextField(
//				value = phaseName,
//				onValueChange = onPhaseNameChange,
//				modifier = Modifier.fillMaxWidth(),
//				label = { Text("Nombre de la fase") },
//				placeholder = { Text("Ej: Calentamiento") },
//				shape = RoundedCornerShape(12.dp)
//			)
//
//			Row(
//				modifier = Modifier.fillMaxWidth(),
//				horizontalArrangement = Arrangement.spacedBy(16.dp)
//			) {
//				OutlinedTextField(
//					value = duration,
//					onValueChange = onDurationChange,
//					modifier = Modifier.weight(1f),
//					label = { Text("Duración (min)") },
//					placeholder = { Text("Ej: 10") },
//					keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
//					shape = RoundedCornerShape(12.dp)
//				)
//				OutlinedTextField(
//					value = bpm,
//					onValueChange = onBpmChange,
//					modifier = Modifier.weight(1f),
//					label = { Text("BPM inicial") },
//					placeholder = { Text("Ej: 60") },
//					keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
//					shape = RoundedCornerShape(12.dp)
//				)
//			}
//
//			LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
//				items(timeSignatures) { timeSignature ->
//					TimeSignatureChip(
//						timeSignature = timeSignature,
//						isSelected = selectedTimeSignature == timeSignature,
//						onSelect = { onTimeSignatureChange(timeSignature) }
//					)
//				}
//			}
//
//			LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
//				items(availableColors) { color ->
//					ColorOption(
//						color = color,
//						isSelected = selectedColor == color,
//						onSelect = { onColorChange(color) }
//					)
//				}
//			}
//		}
//	}
//}
//
//@Composable
//private fun AdvancedOptionsSection(
//	bpmInitial: String,
//	duration: String
//) {
//	var modeByReps by remember { mutableStateOf(true) } // true = por repes, false = hasta BPM max
//
//	Card(
//		modifier = Modifier.fillMaxWidth(),
//		shape = RoundedCornerShape(16.dp),
//		colors = CardDefaults.cardColors(
//			containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
//		)
//	) {
//		Column(
//			modifier = Modifier.padding(20.dp),
//			verticalArrangement = Arrangement.spacedBy(16.dp)
//		) {
//			// Texto Modo arriba de los chips
//			Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
//				Text(
//					"Modo:",
//					style = MaterialTheme.typography.bodyMedium,
//					fontWeight = FontWeight.Medium
//				)
//				Row(
//					horizontalArrangement = Arrangement.spacedBy(16.dp),
//					verticalAlignment = Alignment.CenterVertically
//				) {
//					FilterChip(
//						selected = modeByReps,
//						onClick = { modeByReps = true },
//						label = { Text("Por repeticiones") }
//					)
//					FilterChip(
//						selected = !modeByReps,
//						onClick = { modeByReps = false },
//						label = { Text("Hasta BPM máximo") }
//					)
//				}
//			}
//
//			// BPM inicial siempre visible
//			OutlinedTextField(
//				value = bpmInitial,
//				onValueChange = {},
//				modifier = Modifier.fillMaxWidth(),
//				readOnly = true,
//				placeholder = { Text("BPM inicial") },
//				shape = RoundedCornerShape(12.dp)
//			)
//
//			// Mostrar modo correspondiente
//			if (modeByReps) {
//				AdvancedOptionsByReps(bpmInitial = bpmInitial, duration = duration)
//			} else {
//				AdvancedOptionsUntilBpmMax(bpmInitial = bpmInitial, duration = duration)
//			}
//		}
//	}
//}
//
//
//@Composable
//private fun AdvancedOptionsByReps(bpmInitial: String, duration: String) {
//	var repetitions by remember { mutableStateOf("") }
//	var bpmIncrement by remember { mutableStateOf("") }
//
//	val bpmInt = bpmInitial.toIntOrNull() ?: 0
//	val incrementInt = bpmIncrement.toIntOrNull() ?: 0
//	val repsInt = repetitions.toIntOrNull() ?: 0
//	val durationInt = duration.toIntOrNull() ?: 0
//	val calculatedBpmMax = if (bpmInt > 0 && repsInt > 0) bpmInt + (repsInt - 1) * incrementInt else bpmInt
//	val totalDuration = durationInt * repsInt
//
//	Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
//		OutlinedTextField(
//			value = repetitions,
//			onValueChange = { repetitions = it },
//			modifier = Modifier.fillMaxWidth(),
//			label = { Text("Número de repeticiones") },
//			placeholder = { Text("Ej: 5") },
//			keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
//			shape = RoundedCornerShape(12.dp)
//		)
//
//		OutlinedTextField(
//			value = bpmIncrement,
//			onValueChange = { bpmIncrement = it },
//			modifier = Modifier.fillMaxWidth(),
//			label = { Text("Incremento de BPM por repetición") },
//			placeholder = { Text("Ej: 5") },
//			keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
//			shape = RoundedCornerShape(12.dp)
//		)
//
//		// --- Resultados mejorados ---
//		Card(
//			modifier = Modifier.fillMaxWidth(),
//			shape = RoundedCornerShape(12.dp),
//			colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
//		) {
//			Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
//				ResultRow(label = "BPM máximo", value = calculatedBpmMax.toString())
//				ResultRow(label = "Duración total aproximada", value = "$totalDuration min")
//			}
//		}
//	}
//}
//
//@Composable
//private fun AdvancedOptionsUntilBpmMax(bpmInitial: String, duration: String) {
//	var bpmIncrement by remember { mutableStateOf("") }
//	var bpmMax by remember { mutableStateOf("") }
//
//	val bpmInt = bpmInitial.toIntOrNull() ?: 0
//	val incrementInt = bpmIncrement.toIntOrNull() ?: 0
//	val bpmMaxInt = bpmMax.toIntOrNull() ?: 0
//	val durationInt = duration.toIntOrNull() ?: 0
//
//	val calculatedReps = if (bpmInt > 0 && incrementInt > 0 && bpmMaxInt > bpmInt) {
//		ceil((bpmMaxInt - bpmInt).toDouble() / incrementInt).toInt()
//	} else 0
//
//	val totalDuration = durationInt * calculatedReps
//
//	Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
//		OutlinedTextField(
//			value = bpmIncrement,
//			onValueChange = { bpmIncrement = it },
//			modifier = Modifier.fillMaxWidth(),
//			label = { Text("Incremento de BPM por repetición") },
//			placeholder = { Text("Ej: 5") },
//			keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
//			shape = RoundedCornerShape(12.dp)
//		)
//
//		OutlinedTextField(
//			value = bpmMax,
//			onValueChange = { bpmMax = it },
//			modifier = Modifier.fillMaxWidth(),
//			label = { Text("BPM máximo") },
//			placeholder = { Text("Ej: 100") },
//			keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
//			shape = RoundedCornerShape(12.dp)
//		)
//
//		Text("Repeticiones necesarias: $calculatedReps")
//		Text("Duración total aproximada: $totalDuration min")
//	}
//}
//
//
//@Composable
//private fun ResultRow(label: String, value: String) {
//	Row(
//		modifier = Modifier.fillMaxWidth(),
//		horizontalArrangement = Arrangement.SpaceBetween,
//		verticalAlignment = Alignment.CenterVertically
//	) {
//		Text(label, style = MaterialTheme.typography.bodyMedium)
//		Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
//	}
//}
//
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//private fun TimeSignatureChip(
//	timeSignature: String,
//	isSelected: Boolean,
//	onSelect: () -> Unit,
//) {
//	FilterChip(
//		selected = isSelected,
//		onClick = onSelect,
//		label = {
//			Text(
//				text = timeSignature,
//				style = MaterialTheme.typography.labelLarge,
//				fontWeight = FontWeight.Medium
//			)
//		},
//		shape = RoundedCornerShape(50.dp),
//		colors = FilterChipDefaults.filterChipColors(
//			selectedContainerColor = MaterialTheme.colorScheme.primary,
//			selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
//			containerColor = MaterialTheme.colorScheme.surface,
//			labelColor = MaterialTheme.colorScheme.onSurface
//		),
//		border = FilterChipDefaults.filterChipBorder(
//			enabled = true,
//			selected = isSelected,
//			borderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
//			selectedBorderColor = Color.Transparent
//		)
//	)
//}
//
//@Composable
//private fun ColorOption(
//	color: Color,
//	isSelected: Boolean,
//	onSelect: () -> Unit,
//) {
//	Box(
//		modifier = Modifier
//			.size(40.dp)
//			.clip(CircleShape)
//			.background(color)
//			.border(
//				width = if (isSelected) 3.dp else 0.dp,
//				color = MaterialTheme.colorScheme.onSurface,
//				shape = CircleShape
//			)
//			.clickable { onSelect() }
//	)
//}
