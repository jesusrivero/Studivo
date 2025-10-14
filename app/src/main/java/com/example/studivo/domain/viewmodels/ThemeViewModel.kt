package com.example.studivo.domain.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studivo.data.preferences.ThemeDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ThemeViewModel @Inject constructor(
	private val themeDataStore: ThemeDataStore
) : ViewModel() {
	
	val isDarkMode = themeDataStore.isDarkMode
		.stateIn(viewModelScope, SharingStarted.Eagerly, false)
	
	fun toggleDarkMode(enabled: Boolean) {
		viewModelScope.launch {
			themeDataStore.setDarkMode(enabled)
		}
	}
}