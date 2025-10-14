package com.example.studivo.domain.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studivo.domain.model.RoutineProgress
import com.example.studivo.domain.repository.RoutineProgressRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject


@HiltViewModel
class HistoryViewModel @Inject constructor(
	private val repository: RoutineProgressRepository
) : ViewModel() {
	
	private val _history = MutableStateFlow<Map<String, List<RoutineProgress>>>(emptyMap())
	val history = _history.asStateFlow()
	
	init {
		loadHistory()
	}
	
	private fun loadHistory() {
		viewModelScope.launch {
			val progressList = repository.getAllProgressOrderedByDate()
				.filter { it.isCompleted  }
			val grouped = progressList.groupBy { it.date }
			_history.value = grouped.toSortedMap(compareByDescending { it })
		}
	}
}
