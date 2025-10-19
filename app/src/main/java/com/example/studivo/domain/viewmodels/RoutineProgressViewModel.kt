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
	
	
	fun loadCurrentStreak() {
		viewModelScope.launch {
			try {
				if (_firstUseDate.value == null) {
					_firstUseDate.value = appPreferences.getFirstUseDate()
				}
				val calendar = progressUseCases.getProgressCalendar(_firstUseDate.value)
				val completedDays = calendar.filter { it.hasCompletedRoutine }.map { it.date }
				_currentStreak.value = calculateStreak(completedDays)
			} catch (e: Exception) {
				_errorMessage.value = e.localizedMessage ?: "Error cargando racha"
			}
		}
	}
	
//	fun loadCurrentStreak() {
//		viewModelScope.launch {
//			try {
//				val calendar = progressUseCases.getProgressCalendar(_firstUseDate.value)
//				val completedDays = calendar.filter { it.hasCompletedRoutine }.map { it.date }
//				_currentStreak.value = calculateStreak(completedDays)
//			} catch (e: Exception) {
//				_errorMessage.value = e.localizedMessage ?: "Error cargando racha"
//			}
//		}
//	}
	
	private fun calculateStreak(completedDates: List<String>): Int {
		if (completedDates.isEmpty()) return 0
		
		val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
		val today = sdf.format(Date())
		
		val completed = completedDates.map { sdf.parse(it)!! }.sortedDescending()
		val completedSet = completedDates.toSet()
		
		var streak = 0
		val current = Calendar.getInstance()

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
				if (day.before(current.apply { add(Calendar.DAY_OF_YEAR, -1) })) {
					break
				}
			}
		}
		
		return streak
	}
	
	
	fun loadProgressCalendar() {
		viewModelScope.launch {
			appPreferences.saveFirstUseDateIfNotExists()
			val firstDate = appPreferences.getFirstUseDate()
			_firstUseDate.value = firstDate
			
			val calendar = progressUseCases.getProgressCalendar(firstDate)
			_progressCalendar.value = calendar
		}
	}
	
	
	fun initializeCalendarIfNeeded() {
		viewModelScope.launch {
			val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
			val today = sdf.format(Date())
			
			if (_firstUseDate.value == null) {
				_firstUseDate.value = today
				loadProgressCalendar()
			} else {
				loadProgressCalendar()
			}
		}
	}
	
	fun updateProgress(
		routineId: String,
		routineName: String = "",
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
					routineName = routineName,
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
				
				// 🔁 Recalcular racha y calendario después de guardar
				loadProgressCalendar()
				loadCurrentStreak()
			} catch (e: Exception) {
				_errorMessage.value = e.localizedMessage ?: "Error guardando progreso"
			} finally {
				_isLoading.value = false
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