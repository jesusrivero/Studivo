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
	
	
	fun addTempPhase(phase: TempPhaseItem) {
		tempPhases = tempPhases + phase
	}
	
	
	fun removeTempPhase(phaseId: String) {
		tempPhases = tempPhases.filterNot { it.tempId == phaseId }
	}
	
	
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
		onSuccess: () -> Unit,
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
					subdivision = temp.getSubdivisionEnum(),
					color = temp.color.fromHex(),
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
				
				
				val existingRoutine = useCases.getRoutineById(routineId)
				if (existingRoutine == null) {
					onError("Rutina no encontrada")
					return@launch
				}
				val updatedRoutine = existingRoutine.copy(name = name)
				useCases.updateRoutine(updatedRoutine)
				
				
				val oldPhases = useCases.getPhasesByRoutine(routineId)
				oldPhases.forEach { useCases.deletePhase(it.id) }
				
				
				tempPhases.forEachIndexed { index, temp ->
					val newPhase = Phase(
						id = UUID.randomUUID().toString(),
						routineId = routineId,
						name = temp.name,
						duration = temp.duration,
						bpm = temp.bpmInitial,
						timeSignature = temp.timeSignature,
						subdivision = temp.getSubdivisionEnum(),
						color = temp.color.fromHex(),
						mode = temp.mode,
						repetitions = temp.repetitions,
						bpmIncrement = temp.bpmIncrement,
						bpmMax = temp.bpmMax,
						order = index
					)
					useCases.insertPhase(newPhase)
				}
				
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
			item.copy(tempId = item.tempId)
		}
	}
	
	
	fun loadRoutines() {
		viewModelScope.launch {
			_isLoading.value = true
			try {
				
				val routinesFromDb = useCases.getAllRoutines()
				
				val pairs: List<Pair<Routine, List<Phase>>> = coroutineScope {
					routinesFromDb.map { routine ->
						async {
							val phasesForRoutine = useCases.getPhasesByRoutine(routine.id)
							Pair(routine, phasesForRoutine)
						}
					}.awaitAll()
				}
				
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
			tempId = id,
			name = name,
			duration = duration,
			bpmInitial = bpm,
			timeSignature = timeSignature,
			subdivision = subdivision.name,
			color = color.toHexString(),
			mode = mode,
			repetitions = repetitions,
			bpmIncrement = bpmIncrement,
			bpmMax = bpmMax,
			routineId = routineId,
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
				loadRoutines()
				onSuccess()
			} catch (e: Exception) {
				onError(e.localizedMessage ?: "Error eliminando rutina")
			} finally {
				_isLoading.value = false
			}
		}
	}
}


private fun Phase.calculateRepetitions(): Int {
	return when {
		mode == "BY_REPS" && repetitions > 0 -> repetitions.coerceAtLeast(1)
		mode == "UNTIL_BPM_MAX" && bpmIncrement > 0 && bpmMax > bpm -> {
			val neededRepetitions = ((bpmMax - bpm) / bpmIncrement) + 1
			neededRepetitions.coerceAtLeast(1)
		}
		
		else -> 1
	}
}

