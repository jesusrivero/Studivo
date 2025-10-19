package com.example.studivo.presentation.utils

import android.content.Context
import android.os.Build

fun Context.isBatteryOptimizationDisabled(): Boolean {
	if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
		val powerManager = getSystemService(Context.POWER_SERVICE) as android.os.PowerManager
		return powerManager.isIgnoringBatteryOptimizations(packageName)
	}
	return true // En versiones antiguas no hay optimización
}