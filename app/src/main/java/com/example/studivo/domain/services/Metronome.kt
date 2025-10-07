package com.example.studivo.domain.services

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import android.util.Log
import com.example.studivo.R
import com.example.studivo.domain.model.NoteSubdivision
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.max

class Metronome(private val context: Context) {
	
	private var soundPool: SoundPool? = null
	private var soundIdHigh: Int = 0
	private var soundIdLow: Int = 0
	private var metronomeJob: Job? = null
	
	private var currentBpm: Int = 120
	private var currentTimeSignature: String = "4/4"
	private var currentSubdivision: NoteSubdivision = NoteSubdivision.QUARTER
	private var isRunning: Boolean = false
	
	// Variables para compensación de timing
	private var startTime: Long = 0
	private var clickCount: Int = 0
	
	// Callback para notificar beats al ViewModel
	private var onBeatCallback: ((currentBeat: Int, beatsPerMeasure: Int) -> Unit)? = null
	
	companion object {
		private const val TAG = "Metronome"
		private const val MAX_STREAMS = 2
	}
	
	init {
		setupSoundPool()
	}
	
	private fun setupSoundPool() {
		try {
			Log.d(TAG, "🎧 Inicializando SoundPool...")
			
			val audioAttributes = AudioAttributes.Builder()
				.setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
				.setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
				.build()
			
			soundPool = SoundPool.Builder()
				.setMaxStreams(MAX_STREAMS)
				.setAudioAttributes(audioAttributes)
				.build()
			
			soundIdHigh = soundPool?.load(context, R.raw.metronome_high, 1) ?: 0
			soundIdLow = soundPool?.load(context, R.raw.metronome_low, 1) ?: 0
			
			Log.d(TAG, "✅ SoundPool preparado. High ID: $soundIdHigh, Low ID: $soundIdLow")
			
		} catch (e: Exception) {
			Log.e(TAG, "❌ Error preparando SoundPool: ${e.message}", e)
		}
	}
	
	fun setOnBeatListener(callback: (currentBeat: Int, beatsPerMeasure: Int) -> Unit) {
		onBeatCallback = callback
	}
	
	/**
	 * Inicia el metrónomo con soporte para subdivisiones rítmicas
	 */
	fun start(
		bpm: Int,
		timeSignature: String,
		subdivision: NoteSubdivision = NoteSubdivision.QUARTER,
		coroutineScope: CoroutineScope
	) {
		if (isRunning) stop()
		
		currentBpm = bpm
		currentTimeSignature = timeSignature
		currentSubdivision = subdivision
		isRunning = true
		
		val beatsPerMeasure = parseTimeSignature(timeSignature)
		val beatIntervalMs = (60_000.0 / bpm)
		val clicksPerBeat = subdivision.clicksPerBeat
		
		// Intervalo entre clicks (considerando subdivisión)
		val clickIntervalMs = beatIntervalMs / clicksPerBeat
		
		Log.d(
			TAG,
			"▶️ Iniciando metrónomo: BPM=$bpm, Compás=$timeSignature, Subdivisión=${subdivision.displayName}"
		)
		Log.d(TAG, "   Beats=$beatsPerMeasure, Clicks por beat=$clicksPerBeat")
		Log.d(TAG, "   Intervalo entre beats: ${beatIntervalMs}ms, Intervalo entre clicks: ${clickIntervalMs}ms")
		
		metronomeJob = coroutineScope.launch {
			startTime = System.currentTimeMillis()
			clickCount = 0
			
			while (isActive && isRunning) {
				val expectedTime = startTime + (clickCount * clickIntervalMs).toLong()
				val currentTime = System.currentTimeMillis()
				val drift = currentTime - expectedTime
				
				val beatInMeasure = clickCount / clicksPerBeat        // índice del tiempo actual
				val positionInBeat = clickCount % clicksPerBeat      // posición dentro del tiempo
				val beatNumber = (beatInMeasure % beatsPerMeasure) + 1
				
				when (currentSubdivision) {
					NoteSubdivision.QUARTER -> {
						// 🔹 Negra: el primer pulso del compás suena fuerte, los demás débiles
						if (beatInMeasure % beatsPerMeasure == 0) {
							playHighClick() // primer tiempo del compás
						} else {
							playLowClick()  // otros tiempos del compás
						}
					}
					else -> {
						// 🔹 Subdivisiones
						if (positionInBeat == 0) {
							playHighClick() // fuerte al inicio de cada tiempo
						} else {
							playSubdivisionClick() // suave en subdivisiones
						}
					}
				}
				
				// Notificar solo al inicio de cada tiempo
				if (positionInBeat == 0) {
					onBeatCallback?.invoke(beatNumber, beatsPerMeasure)
				}
				
				clickCount++
				val nextClickTime = startTime + (clickCount * clickIntervalMs).toLong()
				val delayTime = max(0L, nextClickTime - System.currentTimeMillis())
				
				delay(delayTime)
			}
		}
		
		
	}
	
	fun stop() {
		Log.d(TAG, "⏹️ Deteniendo metrónomo")
		isRunning = false
		metronomeJob?.cancel()
		metronomeJob = null
		clickCount = 0
	}
	
	private fun playHighClick() {
		try {
			soundPool?.play(soundIdHigh, 1.0f, 1.0f, 1, 0, 1.0f)
		} catch (e: Exception) {
			Log.e(TAG, "❌ Error reproduciendo click fuerte: ${e.message}", e)
		}
	}
	
	private fun playLowClick() {
		try {
			soundPool?.play(soundIdLow, 0.7f, 0.7f, 0, 0, 1.0f)
		} catch (e: Exception) {
			Log.e(TAG, "❌ Error reproduciendo click suave: ${e.message}", e)
		}
	}
	
	private fun playSubdivisionClick() {
		try {
			// Clicks de subdivisión más suaves aún
			soundPool?.play(soundIdLow, 0.4f, 0.4f, 0, 0, 1.0f)
		} catch (e: Exception) {
			Log.e(TAG, "❌ Error reproduciendo click de subdivisión: ${e.message}", e)
		}
	}
	
	private fun parseTimeSignature(timeSignature: String): Int {
		return try {
			timeSignature.split("/").firstOrNull()?.toIntOrNull() ?: 4
		} catch (e: Exception) {
			Log.e(TAG, "⚠️ Error parseando compás: ${e.message}")
			4
		}
	}
	
	fun updateTempo(
		bpm: Int,
		timeSignature: String,
		subdivision: NoteSubdivision = NoteSubdivision.QUARTER,
		coroutineScope: CoroutineScope
	) {
		Log.d(TAG, "🔄 Actualizando tempo a $bpm BPM, Subdivisión: ${subdivision.displayName}")
		if (isRunning) {
			stop()
			coroutineScope.launch {
				delay(50)
				start(bpm, timeSignature, subdivision, coroutineScope)
			}
		}
	}
	
	fun scheduleTempoChange(
		newBpm: Int,
		newSignature: String,
		newSubdivision: NoteSubdivision = NoteSubdivision.QUARTER,
		coroutineScope: CoroutineScope
	) {
		Log.d(TAG, "🕒 Cambio de tempo programado: $newBpm BPM")
		
		if (!isRunning) {
			updateTempo(newBpm, newSignature, newSubdivision, coroutineScope)
			return
		}
		
		val beatsPerMeasure = parseTimeSignature(currentTimeSignature)
		val clicksPerBeat = currentSubdivision.clicksPerBeat
		val clicksPerMeasure = beatsPerMeasure * clicksPerBeat
		val remainingClicks = clicksPerMeasure - (clickCount % clicksPerMeasure)
		
		coroutineScope.launch {
			val beatIntervalMs = (60_000.0 / currentBpm)
			val clickIntervalMs = beatIntervalMs / clicksPerBeat
			delay((remainingClicks * clickIntervalMs).toLong())
			
			Log.d(TAG, "🎵 Aplicando cambio de tempo tras $remainingClicks clicks restantes")
			updateTempo(newBpm, newSignature, newSubdivision, coroutineScope)
		}
	}
	
	fun release() {
		Log.d(TAG, "🧹 Liberando recursos del metrónomo")
		stop()
		soundPool?.release()
		soundPool = null
		onBeatCallback = null
	}
}
