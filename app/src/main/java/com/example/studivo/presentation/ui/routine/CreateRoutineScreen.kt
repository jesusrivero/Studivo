package com.example.studivo.presentation.ui

import android.R.attr.onClick
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
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import com.example.studivo.presentation.navegacion.AppRoutes
import kotlinx.coroutines.delay

import kotlin.math.roundToInt


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateRoutineScreen(
	navController: NavController,
) {
	CreateRoutineScreenContent(navController = navController)
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun CreateRoutineScreenContent(
	navController: NavController,
) {
	// Estado para el nombre de la rutina
	var routineName by remember { mutableStateOf("Mi rutina de Lunes") }
	
	// Estados de ejemplo para las fases
	var phases by remember {
		mutableStateOf(
			listOf(
				PhaseItem(
					id = 1,
					name = "Calentamiento",
					duration = 10,
					bpm = 120,
					icon = PhaseIcon.WARMUP
				),
				PhaseItem(
					id = 2,
					name = "Práctica principal",
					duration = 20,
					bpm = 140,
					icon = PhaseIcon.MAIN
				),
				PhaseItem(
					id = 3,
					name = "Enfriamiento",
					duration = 5,
					bpm = 100,
					icon = PhaseIcon.COOLDOWN
				),
				PhaseItem(
					id = 4,
					name = "Estiramiento",
					duration = 8,
					bpm = 90,
					icon = PhaseIcon.WARMUP
				),
				PhaseItem(
					id = 5,
					name = "Relajación",
					duration = 15,
					bpm = 80,
					icon = PhaseIcon.COOLDOWN
				),
				PhaseItem(
					id = 6,
					name = "Meditación",
					duration = 10,
					bpm = 70,
					icon = PhaseIcon.COOLDOWN
				),
				PhaseItem(
					id = 7,
					name = "Cardio",
					duration = 25,
					bpm = 150,
					icon = PhaseIcon.MAIN
				),
				PhaseItem(
					id = 8,
					name = "Flexibilidad",
					duration = 12,
					bpm = 85,
					icon = PhaseIcon.WARMUP
				)
			)
		)
	}
	
	
	
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
			SaveButton()
		}
	) { innerPadding ->
		Column(
			modifier = Modifier
				.fillMaxSize()
				.padding(innerPadding)
				.padding(horizontal = 16.dp)
		) {
			Spacer(modifier = Modifier.height(16.dp))
			
			RoutineNameField(
				value = routineName,
				onValueChange = { routineName = it }
			)
			
			Spacer(modifier = Modifier.height(24.dp))
			
			PhasesHeader(onClick = {navController.navigate(AppRoutes.CreateFaseScreen)})
			
			Spacer(modifier = Modifier.height(16.dp))
			
			// ✅ Lista mejorada con mejor UX
			EnhancedPhasesReorderableList(
				phases = phases,
				onReordered = { phases = it },
				onItemClick = { navController.navigate(AppRoutes.EditedFaseScreen) }
			)
		}
	}
}

@Composable
private fun RoutineNameField(
	value: String,
	onValueChange: (String) -> Unit,
) {
	OutlinedTextField(
		value = value,
		onValueChange = onValueChange,
		modifier = Modifier.fillMaxWidth(),
		shape = RoundedCornerShape(12.dp),
		colors = OutlinedTextFieldDefaults.colors(
			focusedBorderColor = MaterialTheme.colorScheme.primary,
			unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
			focusedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
			unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
		),
		textStyle = MaterialTheme.typography.bodyLarge
	)
}

@Composable
private fun PhasesHeader(
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
			// ✅ CORREGIDO: Ahora ejecuta la función onClick correctamente
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



@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun EnhancedDraggablePhaseItem(
	phase: PhaseItem,
	index: Int,
	dragDropState: EnhancedDragDropState,
	modifier: Modifier = Modifier,
	onClick: () -> Unit
) {
	val isDragging = dragDropState.draggedIndex == index
	val isTarget = dragDropState.targetIndex == index && dragDropState.draggedIndex != index
	val shouldShowTopLine = dragDropState.targetIndex == index &&
			dragDropState.draggedIndex?.let { it > index } == true
	val shouldShowBottomLine = dragDropState.targetIndex == index &&
			dragDropState.draggedIndex?.let { it < index } == true
	
	// Animaciones suaves
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
	
	// Indicadores visuales de posición
	Column(
		modifier = modifier
	) {
		// Indicador superior
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
				// Handle de arrastre visual
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
				
				// Icono de fase
				PhaseIconBox(
					icon = phase.icon,
					modifier = Modifier.size(48.dp)
				)
				
				Spacer(modifier = Modifier.width(16.dp))
				
				// Información de la fase
				Column(
					modifier = Modifier.weight(1f)
				) {
					Text(
						text = phase.name,
						style = MaterialTheme.typography.titleMedium,
						fontWeight = FontWeight.SemiBold,
						color = MaterialTheme.colorScheme.onSurface
					)
					
					Spacer(modifier = Modifier.height(4.dp))
					
					Text(
						text = "${phase.duration} min • ${phase.bpm} BPM",
						style = MaterialTheme.typography.bodyMedium,
						color = MaterialTheme.colorScheme.primary
					)
				}
				
				// Indicador de posición durante drag
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
				
				// Botón de opciones (solo visible cuando no se está arrastrando)
				AnimatedVisibility(
					visible = !dragDropState.isDragging,
					enter = fadeIn(),
					exit = fadeOut()
				) {
					IconButton(
						onClick = { /* TODO: Mostrar opciones */ },
						modifier = Modifier.size(40.dp)
					) {
						Icon(
							Icons.Default.MoreVert,
							contentDescription = "Opciones",
							tint = MaterialTheme.colorScheme.onSurfaceVariant,
							modifier = Modifier.size(20.dp)
						)
					}
				}
			}
		}
		
		// Indicador inferior
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun EnhancedPhasesReorderableList(
	phases: List<PhaseItem>,
	onReordered: (List<PhaseItem>) -> Unit,
	onItemClick: (PhaseItem) -> Unit,
) {
	val dragDropState = rememberEnhancedDragDropState()
	val listState = rememberLazyListState()
	val haptic = LocalHapticFeedback.current
	
	// Auto-scroll durante el drag
	LaunchedEffect(dragDropState.isDragging, dragDropState.targetIndex) {
		if (dragDropState.isDragging) {
			while (dragDropState.isDragging) {
				val layoutInfo = listState.layoutInfo
				val visibleItems = layoutInfo.visibleItemsInfo
				
				// Auto-scroll hacia arriba si el objetivo está arriba del viewport
				dragDropState.targetIndex?.let { target ->
					val firstVisibleIndex = visibleItems.firstOrNull()?.index ?: 0
					val lastVisibleIndex = visibleItems.lastOrNull()?.index ?: 0
					
					when {
						target < firstVisibleIndex -> {
							// Scroll hacia arriba
							listState.animateScrollToItem(target)
						}
						target > lastVisibleIndex -> {
							// Scroll hacia abajo
							listState.animateScrollToItem(target)
						}
					}
				}
				
				delay(16) // 60 FPS
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
			key = { _, item -> item.id }
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
										println("🔥 POINTER PRESS - Index: $index, Item: ${item.name}")
										longPressStartTime = System.currentTimeMillis()
										isDragging = false
										accumulatedDrag = 0f
										
										// Obtener altura del ítem
										val layoutInfo = listState.layoutInfo
										val currentItemInfo = layoutInfo.visibleItemsInfo.find { it.index == index }
										itemHeight = currentItemInfo?.size?.toFloat() ?: 100f
										println("🔥 Item height: $itemHeight")
									}
									
									PointerEventType.Move -> {
										if (!isDragging) {
											val holdTime = System.currentTimeMillis() - longPressStartTime
											if (holdTime > 500) { // 500ms para long press
												println("🔥 STARTING DRAG after ${holdTime}ms")
												haptic.performHapticFeedback(HapticFeedbackType.LongPress)
												dragDropState.startDrag(index)
												isDragging = true
											}
										}
										
										if (isDragging) {
											val change = event.changes.first()
											val dragAmount = change.position.y - change.previousPosition.y
											accumulatedDrag += dragAmount
											
											println("🔥 DRAGGING - Accumulated: $accumulatedDrag, DragAmount: $dragAmount")
											
											// Cálculo simplificado
											val threshold = itemHeight * 0.6f
											val positions = (accumulatedDrag / threshold).toInt()
											val newTargetIndex = (index + positions).coerceIn(0, phases.size - 1)
											
											println("🔥 Target calculation - Positions: $positions, NewTarget: $newTargetIndex, CurrentTarget: ${dragDropState.targetIndex}")
											
											if (newTargetIndex != dragDropState.targetIndex) {
												println("🔥 UPDATING TARGET from ${dragDropState.targetIndex} to $newTargetIndex")
												haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
												dragDropState.updateTarget(newTargetIndex)
											}
											
											change.consume()
										}
									}
									
									PointerEventType.Release -> {
										if (isDragging) {
											println("🔥 DRAG END CALLED")
											val (from, to) = dragDropState.endDrag()
											println("🔥 Drag ended - From: $from, To: $to")
											if (from != null && to != null && from != to) {
												haptic.performHapticFeedback(HapticFeedbackType.LongPress)
												val newList = phases.toMutableList()
												println("🔥 ANTES DEL REORDENAMIENTO:")
												newList.forEachIndexed { i, phase ->
													println("🔥   [$i] ${phase.name}")
												}
												
												println("🔥 Moviendo ítem de posición $from a $to")
												newList.move(from, to)
												
												println("🔥 DESPUÉS DEL REORDENAMIENTO:")
												newList.forEachIndexed { i, phase ->
													println("🔥   [$i] ${phase.name}")
												}
												onReordered(newList)
											}
											isDragging = false
										} else {
											println("🔥 TOUCH RELEASED - No drag happened")
										}
									}
								}
							}
						}
					},
				// ✅ CORREGIDO: Ahora pasa el item como parámetro a onClick
				onClick = { onItemClick(item) }
			)
		}
	}
}

@Composable
private fun PhaseIconBox(
	icon: PhaseIcon,
	modifier: Modifier = Modifier,
) {
	Box(
		modifier = modifier
			.background(
				color = when (icon) {
					PhaseIcon.WARMUP -> Color(0xFF4CAF50).copy(alpha = 0.15f)
					PhaseIcon.MAIN -> Color(0xFF2196F3).copy(alpha = 0.15f)
					PhaseIcon.COOLDOWN -> Color(0xFF4CAF50).copy(alpha = 0.15f)
				},
				shape = RoundedCornerShape(12.dp)
			),
		contentAlignment = Alignment.Center
	) {
		when (icon) {
			PhaseIcon.WARMUP -> {
				Icon(
					Icons.Default.PlayArrow,
					contentDescription = null,
					tint = Color(0xFF4CAF50),
					modifier = Modifier.size(24.dp)
				)
			}
			
			PhaseIcon.MAIN -> {
				Icon(
					Icons.Default.Add,
					contentDescription = null,
					tint = Color(0xFF2196F3),
					modifier = Modifier.size(24.dp)
				)
			}
			
			PhaseIcon.COOLDOWN -> {
				Icon(
					Icons.Default.PlayArrow,
					contentDescription = null,
					tint = Color(0xFF4CAF50),
					modifier = Modifier.size(24.dp)
				)
			}
		}
	}
}

@Composable
private fun SaveButton() {
	Surface(
		modifier = Modifier
			.fillMaxWidth()
			.navigationBarsPadding(),
		color = MaterialTheme.colorScheme.surface,
		shadowElevation = 8.dp
	) {
		Button(
			onClick = { /* TODO: Guardar rutina */ },
			modifier = Modifier
				.fillMaxWidth()
				.padding(16.dp)
				.height(56.dp),
			shape = RoundedCornerShape(28.dp),
			colors = ButtonDefaults.buttonColors(
				containerColor = MaterialTheme.colorScheme.primary
			)
		) {
			Text(
				"Guardar cambios",
				style = MaterialTheme.typography.titleMedium,
				fontWeight = FontWeight.SemiBold
			)
		}
	}
}


// Estado mejorado para drag and drop
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
	
	fun cancelDrag() {
		isDragging = false
		draggedIndex = null
		targetIndex = null
	}
}
// Modelos de datos
data class PhaseItem(
	val id: Int,
	val name: String,
	val duration: Int,
	val bpm: Int,
	val icon: PhaseIcon,
)

enum class PhaseIcon {
	WARMUP,
	MAIN,
	COOLDOWN
}

// Función de utilidad para mover elementos en una lista
private fun <T> MutableList<T>.move(from: Int, to: Int) {
	if (from == to || from !in indices || to !in indices) return
	val item = removeAt(from)
	add(to, item)
}