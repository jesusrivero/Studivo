package com.example.studivo.domain.viewmodels


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studivo.data.local.AppPreferences
import com.example.studivo.domain.model.DayProgress
import com.example.studivo.domain.model.RoutineProgress
import com.example.studivo.domain.usecase.RoutineProgressUseCases

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID
import javax.inject.Inject


@HiltViewModel
class RoutineProgressViewModel @Inject constructor(
	private val progressUseCases: RoutineProgressUseCases,
	private val appPreferences: AppPreferences
) : ViewModel() {
	
	// ✅ NUEVO: StateFlow para guardar la fecha de primer uso
	private val _firstUseDate = MutableStateFlow<String?>(null)
	
	private val _currentProgress = MutableStateFlow<RoutineProgress?>(null)
	val currentProgress: StateFlow<RoutineProgress?> = _currentProgress.asStateFlow()
	
	private val _progressHistory = MutableStateFlow<List<RoutineProgress>>(emptyList())
	val progressHistory: StateFlow<List<RoutineProgress>> = _progressHistory.asStateFlow()
	
	private val _completedRoutinesToday = MutableStateFlow(0)
	val completedRoutinesToday: StateFlow<Int> = _completedRoutinesToday.asStateFlow()
	

	private val _currentStreak = MutableStateFlow(0)
	val currentStreak: StateFlow<Int> = _currentStreak.asStateFlow()
	
	private val _progressCalendar = MutableStateFlow<List<DayProgress>>(emptyList())
	val progressCalendar: StateFlow<List<DayProgress>> = _progressCalendar.asStateFlow()
	
	private val _isLoading = MutableStateFlow(false)
	val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
	
	private val _errorMessage = MutableStateFlow<String?>(null)
	val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
	
	private val progressFlows = mutableMapOf<String, StateFlow<RoutineProgress?>>()
	
	private fun getCurrentDateString(): String {
		val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
		return sdf.format(Date())
	}
	
	fun getProgressForRoutine(routineId: String): StateFlow<RoutineProgress?> {
		if (progressFlows.containsKey(routineId)) {
			return progressFlows[routineId]!!
		}
		
		val date = getCurrentDateString()
		val flow = progressUseCases.getProgressFlow(routineId, date)
			.stateIn(
				scope = viewModelScope,
				started = SharingStarted.WhileSubscribed(5000),
				initialValue = null
			)
		
		progressFlows[routineId] = flow
		return flow
	}
	
	//Cargar racha actual
	
	// Cargar racha actual
	fun loadCurrentStreak() {
		viewModelScope.launch {
			try {
				val calendar = progressUseCases.getProgressCalendar(_firstUseDate.value)
				val completedDays = calendar.filter { it.hasCompletedRoutine }.map { it.date }
				_currentStreak.value = calculateStreak(completedDays)
			} catch (e: Exception) {
				_errorMessage.value = e.localizedMessage ?: "Error cargando racha"
			}
		}
	}
	
	private fun calculateStreak(completedDates: List<String>): Int {
		if (completedDates.isEmpty()) return 0
		
		val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
		val today = sdf.format(Date())
		
		val completed = completedDates.map { sdf.parse(it)!! }.sortedDescending()
		val completedSet = completedDates.toSet()
		
		var streak = 0
		val current = Calendar.getInstance()
		
		// Si hoy NO hay rutina completada, empieza desde AYER
		if (!completedSet.contains(today)) {
			current.add(Calendar.DAY_OF_YEAR, -1)
		}
		
		for (date in completed) {
			val day = Calendar.getInstance().apply { time = date }
			
			if (day.get(Calendar.YEAR) == current.get(Calendar.YEAR) &&
				day.get(Calendar.DAY_OF_YEAR) == current.get(Calendar.DAY_OF_YEAR)
			) {
				streak++
				current.add(Calendar.DAY_OF_YEAR, -1)
			} else {
				// Si hay una brecha de más de 1 día, se rompe la racha
				if (day.before(current.apply { add(Calendar.DAY_OF_YEAR, -1) })) {
					break
				}
			}
		}
		
		return streak
	}
	
	
	fun loadProgressCalendar() {
		viewModelScope.launch {
			appPreferences.saveFirstUseDateIfNotExists() // Guarda si no existe
			val firstDate = appPreferences.getFirstUseDate()
			_firstUseDate.value = firstDate
			
			val calendar = progressUseCases.getProgressCalendar(firstDate)
			_progressCalendar.value = calendar
		}
	}
	
//	// ✅ MODIFICADO: Cargar calendario usando fecha de primer uso
//	fun loadProgressCalendar(firstUseDate: String? = null) {
//		viewModelScope.launch {
//			try {
//				_isLoading.value = true
//
//				// Si se proporciona firstUseDate, guardarlo
//				if (firstUseDate != null) {
//					_firstUseDate.value = firstUseDate
//				}
//
//				// Cargar calendario desde la fecha de primer uso
//				val calendar = progressUseCases.getProgressCalendar(_firstUseDate.value)
//				_progressCalendar.value = calendar
//			} catch (e: Exception) {
//				_errorMessage.value = e.localizedMessage ?: "Error cargando calendario"
//			} finally {
//				_isLoading.value = false
//			}
//		}
//	}
	
	
	// ✅ NUEVO: Método para establecer la fecha de primer uso
	fun setFirstUseDate(date: String) {
		_firstUseDate.value = date
		loadProgressCalendar()
	}
	
	fun loadTodayProgress(routineId: String) {
		viewModelScope.launch {
			try {
				_isLoading.value = true
				val date = getCurrentDateString()
				val progress = progressUseCases.getProgress(routineId, date)
				_currentProgress.value = progress
			} catch (e: Exception) {
				_errorMessage.value = e.localizedMessage ?: "Error cargando progreso"
			} finally {
				_isLoading.value = false
			}
		}
	}
	
	fun observeTodayProgress(routineId: String) {
		viewModelScope.launch {
			val date = getCurrentDateString()
			progressUseCases.getProgressFlow(routineId, date).collect { progress ->
				_currentProgress.value = progress
			}
		}
	}
	
	fun initializeCalendarIfNeeded() {
		viewModelScope.launch {
			val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
			val today = sdf.format(Date())
			
			if (_firstUseDate.value == null) {
				// Guardamos la fecha actual como primer uso
				_firstUseDate.value = today
				loadProgressCalendar()
			} else {
				// Si ya existe, cargamos normalmente
				loadProgressCalendar()
			}
		}
	}
	
	fun updateProgress(
		routineId: String,
		routineName: String = "", // ✅ NUEVO parámetro
		progressPercentage: Int,
		currentPhaseIndex: Int = 0,
		currentRepetition: Int = 1,
		totalElapsedTime: Int = 0,
		isCompleted: Boolean = false
	) {
		viewModelScope.launch {
			try {
				_isLoading.value = true
				val date = getCurrentDateString()

				val progress = RoutineProgress(
					id = UUID.randomUUID().toString(),
					routineId = routineId,
					routineName = routineName, // ✅ NUEVO
					date = date,
					progressPercentage = progressPercentage.coerceIn(0, 100),
					currentPhaseIndex = currentPhaseIndex,
					currentRepetition = currentRepetition,
					totalElapsedTime = totalElapsedTime,
					isCompleted = isCompleted,
					lastUpdated = System.currentTimeMillis(),
					createdAt = System.currentTimeMillis()
				)

				progressUseCases.saveProgress(progress)
				_currentProgress.value = progress
			} catch (e: Exception) {
				_errorMessage.value = e.localizedMessage ?: "Error guardando progreso"
			} finally {
				_isLoading.value = false
			}
		}
	}
	
	fun loadProgressHistory(routineId: String) {
		viewModelScope.launch {
			try {
				_isLoading.value = true
				val history = progressUseCases.getProgressHistory(routineId)
				_progressHistory.value = history
			} catch (e: Exception) {
				_errorMessage.value = e.localizedMessage ?: "Error cargando historial"
			} finally {
				_isLoading.value = false
			}
		}
	}
	
	fun loadCompletedRoutinesToday() {
		viewModelScope.launch {
			try {
				val count = progressUseCases.getCompletedRoutinesToday()
				_completedRoutinesToday.value = count
			} catch (e: Exception) {
				_errorMessage.value = e.localizedMessage ?: "Error cargando estadísticas"
			}
		}
	}
	
	fun isCompletedToday(routineId: String, onResult: (Boolean) -> Unit) {
		viewModelScope.launch {
			try {
				val date = getCurrentDateString()
				val isCompleted = progressUseCases.isRoutineCompletedToday(routineId, date)
				onResult(isCompleted)
			} catch (e: Exception) {
				_errorMessage.value = e.localizedMessage ?: "Error verificando estado"
				onResult(false)
			}
		}
	}
	
	fun deleteProgress(routineId: String) {
		viewModelScope.launch {
			try {
				val date = getCurrentDateString()
				progressUseCases.deleteProgress(routineId, date)
				_currentProgress.value = null
			} catch (e: Exception) {
				_errorMessage.value = e.localizedMessage ?: "Error eliminando progreso"
			}
		}
	}
	
	fun clearErrorMessage() {
		_errorMessage.value = null
	}
}