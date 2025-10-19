package com.example.studivo

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class MyApp : Application(), Configuration.Provider {
	
	@Inject
	lateinit var workerFactory: HiltWorkerFactory
	
	// ✅ Usar propiedad en lugar de función
	override val workManagerConfiguration: Configuration
		get() = Configuration.Builder()
			.setWorkerFactory(workerFactory)
			.setMinimumLoggingLevel(android.util.Log.INFO)
			.build()
	
	override fun onCreate() {
		super.onCreate()
		// La inicialización de WorkManager es automática con Configuration.Provider
	}
}