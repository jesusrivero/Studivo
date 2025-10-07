package com.example.studivo.presentation.utils

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb


// Convierte Color a String HEX #AARRGGBB
fun Color.toHexString(): String {
	return String.format("#%08X", this.toArgb())
}

// Convierte String HEX a Color
fun String.fromHex(): Color {
	return try {
		Color(android.graphics.Color.parseColor(this))
	} catch (e: Exception) {
		Color(0xFF2196F3) // fallback azul si falla
	}
}