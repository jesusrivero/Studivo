package com.example.studivo.domain.services

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import android.util.Log
import com.example.studivo.R
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
	private var isRunning: Boolean = false
	
	// Variables para compensación de timing
	private var startTime: Long = 0
	private var beatCount: Int = 0
	
	// ✨ NUEVO: Callback para notificar beats al ViewModel
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
	
	// ✨ NUEVO: Método para registrar el callback
	fun setOnBeatListener(callback: (currentBeat: Int, beatsPerMeasure: Int) -> Unit) {
		onBeatCallback = callback
	}
	
	fun start(bpm: Int, timeSignature: String, coroutineScope: CoroutineScope) {
		if (isRunning) stop()
		
		currentBpm = bpm
		currentTimeSignature = timeSignature
		isRunning = true
		
		val beatsPerMeasure = parseTimeSignature(timeSignature)
		val beatIntervalMs = (60_000.0 / bpm)
		
		Log.d(TAG, "▶️ Iniciando metrónomo: BPM=$bpm, Compás=$timeSignature, Intervalo=$beatIntervalMs ms")
		
		metronomeJob = coroutineScope.launch {
			startTime = System.currentTimeMillis()
			beatCount = 0
			
			while (isActive && isRunning) {
				val expectedTime = startTime + (beatCount * beatIntervalMs).toLong()
				val currentTime = System.currentTimeMillis()
				val drift = currentTime - expectedTime
				
				val currentBeatInMeasure = (beatCount % beatsPerMeasure) + 1
				
				if (beatCount % beatsPerMeasure == 0) {
					playHighClick()
				} else {
					playLowClick()
				}
				
				// ✨ NUEVO: Notificar al ViewModel del beat actual
				onBeatCallback?.invoke(currentBeatInMeasure, beatsPerMeasure)
				
				beatCount++
				
				val nextBeatTime = startTime + (beatCount * beatIntervalMs).toLong()
				val delayTime = max(0L, nextBeatTime - System.currentTimeMillis())
				
				if (beatCount % 4 == 0) {
					Log.d(TAG, "⏱️ Beat $beatCount | Drift: ${drift}ms | Delay: ${delayTime}ms")
				}
				
				delay(delayTime)
			}
		}
	}
	
	fun stop() {
		Log.d(TAG, "⏹️ Deteniendo metrónomo")
		isRunning = false
		metronomeJob?.cancel()
		metronomeJob = null
		beatCount = 0
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
	
	private fun parseTimeSignature(timeSignature: String): Int {
		return try {
			timeSignature.split("/").firstOrNull()?.toIntOrNull() ?: 4
		} catch (e: Exception) {
			Log.e(TAG, "⚠️ Error parseando compás: ${e.message}")
			4
		}
	}
	
	fun updateTempo(bpm: Int, timeSignature: String, coroutineScope: CoroutineScope) {
		Log.d(TAG, "🔄 Actualizando tempo a $bpm BPM")
		if (isRunning) {
			stop()
			coroutineScope.launch {
				delay(50)
				start(bpm, timeSignature, coroutineScope)
			}
		}
	}
	
	fun scheduleTempoChange(newBpm: Int, newSignature: String, coroutineScope: CoroutineScope) {
		Log.d(TAG, "🕒 Cambio de tempo programado: $newBpm BPM en el próximo compás")
		
		if (!isRunning) {
			updateTempo(newBpm, newSignature, coroutineScope)
			return
		}
		
		val beatsPerMeasure = parseTimeSignature(currentTimeSignature)
		val remainingBeats = beatsPerMeasure - (beatCount % beatsPerMeasure)
		
		coroutineScope.launch {
			val beatIntervalMs = (60_000.0 / currentBpm).toLong()
			delay(remainingBeats * beatIntervalMs)
			
			Log.d(TAG, "🎵 Aplicando cambio de tempo tras $remainingBeats beats restantes")
			updateTempo(newBpm, newSignature, coroutineScope)
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