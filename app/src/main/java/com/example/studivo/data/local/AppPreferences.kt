package com.example.studivo.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val Context.dataStore by preferencesDataStore(name = "app_prefs")

class AppPreferences(private val context: Context) {
	
	companion object {
		private val FIRST_USE_DATE = stringPreferencesKey("first_use_date")
	}
	
	fun getFirstUseDateFlow(): Flow<String?> {
		return context.dataStore.data.map { prefs ->
			prefs[FIRST_USE_DATE]
		}
	}
	
	suspend fun getFirstUseDate(): String? {
		return context.dataStore.data.map { it[FIRST_USE_DATE] }.first()
	}
	
	suspend fun saveFirstUseDateIfNotExists() {
		val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
		val today = sdf.format(Date())
		
		val existing = getFirstUseDate()
		if (existing == null) {
			context.dataStore.edit { prefs ->
				prefs[FIRST_USE_DATE] = today
			}
		}
	}
}