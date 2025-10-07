package com.example.studivo.domain.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studivo.domain.model.Phase
import com.example.studivo.domain.model.RoutineSummary
import com.example.studivo.domain.model.TempPhaseItem
import com.example.studivo.domain.usecase.RoutineUseCases
import com.example.studivo.presentation.ui.routine.Routine
import com.example.studivo.presentation.utils.fromHex
import com.example.studivo.presentation.utils.toHexString
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject
import kotlin.math.ceil

@HiltViewModel
class RoutineViewModel @Inject constructor(
	private val useCases: RoutineUseCases,
) : ViewModel() {
	
	var routineSummaries by mutableStateOf<List<RoutineSummary>>(emptyList())
		private set
	
	private val _isLoading = MutableStateFlow(false)
	val isLoading: StateFlow<Boolean> = _isLoading
	
	private val _errorMessage = MutableStateFlow<String?>(null)
	val errorMessage: StateFlow<String?> = _errorMessage
	
	
	var routines by mutableStateOf<List<Routine>>(emptyList())
		private set
	
	
	var phases by mutableStateOf<List<Phase>>(emptyList())
		private set
	
	

	var tempPhases by mutableStateOf<List<TempPhaseItem>>(emptyList())
		private set
	
	var routineName by mutableStateOf("")
		private set
	
	// Añadir fase temporal
	fun addTempPhase(phase: TempPhaseItem) {
		tempPhases = tempPhases + phase
	}
	
	// Eliminar fase temporal
	fun removeTempPhase(phaseId: String) {
		tempPhases = tempPhases.filterNot { it.tempId == phaseId }
	}
	
	// ✅ NUEVO: Actualizar fase temporal existente
	fun updateTempPhase(phase: TempPhaseItem) {
		val currentList = tempPhases.toMutableList()
		val index = currentList.indexOfFirst { it.tempId == phase.tempId }
		if (index != -1) {
			currentList[index] = phase
			tempPhases = currentList
		}
	}
	
	
	fun loadRoutineForEditing(routineId: String) {
		viewModelScope.launch {
			val routine = useCases.getRoutineById(routineId)
			if (routine != null) {
				routineName = routine.name
				val phasesList = useCases.getPhasesByRoutine(routineId)
				tempPhases = phasesList.map { it.toTempPhaseItem() }
			}
		}
	}
	
	
	fun createRoutineWithTempPhases(
		name: String,
		onSuccess: () -> Unit
	) {
		viewModelScope.launch {
			val routineId = UUID.randomUUID().toString()
			val routine = Routine(
				id = routineId,
				name = name,
				phases = emptyList(),
				createdAt = System.currentTimeMillis(),
			)
			useCases.insertRoutine(routine)
			
			tempPhases.forEachIndexed { index, temp ->
				val phase = Phase(
					id = UUID.randomUUID().toString(),
					routineId = routineId,
					name = temp.name,
					duration = temp.duration,
					bpm = temp.bpmInitial,
					timeSignature = temp.timeSignature,
					subdivision = temp.getSubdivisionEnum(), // ✨ NUEVO
					color = temp.color.fromHex(), // ✅ String -> Color
					mode = temp.mode,
					repetitions = temp.repetitions,
					bpmIncrement = temp.bpmIncrement,
					bpmMax = temp.bpmMax,
					order = index
				)
				useCases.insertPhase(phase)
			}
			
			tempPhases = emptyList()
			loadRoutines()
			onSuccess()
		}
	}
	
	
	
	
	
	fun updateRoutineWithTempPhases(
		routineId: String,
		name: String,
		onSuccess: () -> Unit,
		onError: (String) -> Unit,
	) {
		viewModelScope.launch {
			try {
				_isLoading.value = true
				
				// 1️⃣ Actualizar nombre de la rutina
				val existingRoutine = useCases.getRoutineById(routineId)
				if (existingRoutine == null) {
					onError("Rutina no encontrada")
					return@launch
				}
				val updatedRoutine = existingRoutine.copy(name = name)
				useCases.updateRoutine(updatedRoutine)
				
				// 2️⃣ Eliminar fases antiguas
				val oldPhases = useCases.getPhasesByRoutine(routineId)
				oldPhases.forEach { useCases.deletePhase(it.id) }
				
				// 3️⃣ Guardar nuevas fases desde tempPhases
				tempPhases.forEachIndexed { index, temp ->
					val newPhase = Phase(
						id = UUID.randomUUID().toString(),
						routineId = routineId,
						name = temp.name,
						duration = temp.duration,
						bpm = temp.bpmInitial,
						timeSignature = temp.timeSignature,
						subdivision = temp.getSubdivisionEnum(), // ✨ NUEVO
						color = temp.color.fromHex(), // ✅ String -> Color
						mode = temp.mode,
						repetitions = temp.repetitions,
						bpmIncrement = temp.bpmIncrement,
						bpmMax = temp.bpmMax,
						order = index
					)
					useCases.insertPhase(newPhase)
				}
				
				// 4️⃣ Limpiar fases temporales y refrescar rutinas
				tempPhases = emptyList()
				loadRoutines()
				onSuccess()
				
			} catch (e: Exception) {
				onError(e.localizedMessage ?: "Error desconocido")
			} finally {
				_isLoading.value = false
			}
		}
	}

	fun reorderTempPhases(newOrder: List<TempPhaseItem>) {
		tempPhases = newOrder.mapIndexed { index, item ->
			item.copy(tempId = item.tempId) // conservas el ID
		}
	}
	
	
	
	fun loadRoutines() {
		viewModelScope.launch {
			_isLoading.value = true
			try {
				// 1️⃣ Trae todas las rutinas (sin fases)
				val routinesFromDb = useCases.getAllRoutines() // List<Routine>
				
				// 2️⃣ Para cada rutina trae sus fases en paralelo
				val pairs: List<Pair<Routine, List<Phase>>> = coroutineScope {
					routinesFromDb.map { routine ->
						async {
							val phasesForRoutine = useCases.getPhasesByRoutine(routine.id)
							Pair(routine, phasesForRoutine)
						}
					}.awaitAll()
				}
				
				// Actualiza esta sección en tu función loadRoutines()
				val summaries = pairs.map { (routine, phases) ->
					val totalPhases = phases.size
					val totalDuration = phases.sumOf { phase ->
						val reps = phase.calculateRepetitions()
						phase.duration * reps
					}
					RoutineSummary(
						id = routine.id,
						name = routine.name,
						description = routine.description,
						totalPhases = totalPhases,
						totalDuration = totalDuration,
						createdAt = routine.createdAt
					)
				}
				
				routineSummaries = summaries
				
				// 4️⃣ Opcional: almacenar rutinas completas con sus fases
				routines = pairs.map { (routine, phases) ->
					routine.copy(phases = phases)
				}
				
			} catch (e: Exception) {
				_errorMessage.value = e.localizedMessage ?: "Error cargando rutinas"
			} finally {
				_isLoading.value = false
			}
		}
	}


	
	
	fun Phase.toTempPhaseItem(): TempPhaseItem {
		return TempPhaseItem(
			tempId = id, // usamos el mismo id temporalmente
			name = name,
			duration = duration,
			bpmInitial = bpm,
			timeSignature = timeSignature,
			subdivision = subdivision.name, // ✨ NUEVO
			color = color.toHexString(),
			mode = mode,
			repetitions = repetitions,
			bpmIncrement = bpmIncrement,
			bpmMax = bpmMax,
			
			routineId = routineId, // 👈 muy importante
		)
	}
	
	fun getPhaseById(phaseId: String, onResult: (Phase?) -> Unit) {
		viewModelScope.launch {
			val phase = useCases.getPhaseById(phaseId)
			onResult(phase)
		}
	}
	
	fun deleteRoutineWithPhases(routineId: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
		viewModelScope.launch {
			try {
				_isLoading.value = true
				useCases.deleteRoutineWithPhases(routineId)
				loadRoutines() // refrescamos lista
				onSuccess()
			} catch (e: Exception) {
				onError(e.localizedMessage ?: "Error eliminando rutina")
			} finally {
				_isLoading.value = false
			}
		}
	}
}

// Agregá esta función helper en tu RoutineViewModel
private fun Phase.calculateRepetitions(): Int {
	return when {
		// Modo BY_REPS: usar las repeticiones definidas
		mode == "BY_REPS" && repetitions > 0 -> repetitions
		
		// Modo UNTIL_BPM_MAX: calcular repeticiones necesarias
		mode == "UNTIL_BPM_MAX" && bpmIncrement > 0 && bpmMax > bpm -> {
			// Fórmula: (bpmMax - bpmInicial) / incremento + 1
			val neededRepetitions = ((bpmMax - bpm) / bpmIncrement) + 1
			neededRepetitions.coerceAtLeast(1)
		}
		
		// Caso por defecto: 1 repetición
		else -> 1
	}
}

