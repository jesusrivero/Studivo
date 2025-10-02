package com.example.studivo.domain.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studivo.domain.model.Phase
import com.example.studivo.domain.model.TempPhaseItem
import com.example.studivo.domain.usecase.RoutineUseCases
import com.example.studivo.presentation.ui.routine.Routine
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class RoutineViewModel @Inject constructor(
	private val useCases: RoutineUseCases,
) : ViewModel() {
	
	
	private val _isLoading = MutableStateFlow(false)
	val isLoading: StateFlow<Boolean> = _isLoading
	
	private val _errorMessage = MutableStateFlow<String?>(null)
	val errorMessage: StateFlow<String?> = _errorMessage
	
	
	var routines by mutableStateOf<List<Routine>>(emptyList())
		private set
	
	
	var phases by mutableStateOf<List<Phase>>(emptyList())
		private set
	
	
	// ✅ Ahora usamos TempPhaseItem para la UI
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
	
	
	fun createRoutineWithTempPhases(name: String, onSuccess: () -> Unit) {
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
					color = if (temp.color != 0) Color(temp.color) else Color(0xFF2196F3), // default azul
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
						color = Color(temp.color), // ✅
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
			routines = useCases.getAllRoutines()
		}
	}
	
	
	
	
	
	fun Phase.toTempPhaseItem(): TempPhaseItem {
		return TempPhaseItem(
			tempId = id, // usamos el mismo id temporalmente
			name = name,
			duration = duration,
			bpmInitial = bpm,
			timeSignature = timeSignature,
			color = color.value.toInt(), // Color a Int
			mode = mode,
			repetitions = repetitions,
			bpmIncrement = bpmIncrement,
			bpmMax = bpmMax,
			
			routineId = routineId, // 👈 muy importante
		)
	}
	
	fun getPhaseById(phaseId: String, onResult: (Phase?) -> Unit) {
		viewModelScope.launch {
			val phase = useCases.getPhaseById(phaseId) // ✅ ya tienes el caso de uso
			onResult(phase)
		}
	}
	
	
	fun loadPhaseForEditing(phaseId: String) {
		viewModelScope.launch {
			val phase = useCases.getPhaseById(phaseId)  // ✅ ahora sí funciona
			if (phase != null) {
				tempPhases = listOf(phase.toTempPhaseItem())
			} else {
				_errorMessage.value = "Fase no encontrada"
			}
		}
	}
	
	fun updatePhase(phase: Phase) {
		viewModelScope.launch {
			useCases.updatePhase(phase)
		}
	}
	
	
}
