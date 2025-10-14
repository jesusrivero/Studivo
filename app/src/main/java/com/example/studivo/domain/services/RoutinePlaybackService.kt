//// domain/services/RoutinePlaybackService.kt
//package com.example.studivo.domain.services
//
//import android.app.Notification
//import android.app.NotificationChannel
//import android.app.NotificationManager
//import android.app.PendingIntent
//import android.app.Service
//import android.content.Context
//import android.content.Intent
//import android.graphics.PixelFormat
//import android.os.Build
//import android.os.IBinder
//import android.view.Gravity
//import android.view.LayoutInflater
//import android.view.MotionEvent
//import android.view.View
//import android.view.WindowManager
//import android.widget.ImageButton
//import android.widget.TextView
//import androidx.core.app.NotificationCompat
//import com.example.studivo.R
//import com.example.studivo.infraestructure.MainActivity
//
//class RoutinePlaybackService : Service() {
//
//	private var windowManager: WindowManager? = null
//	private var floatingView: View? = null
//	private var isPlaying = true
//	private var routineId: String = ""
//	private var routineName: String = "Rutina"
//
//	companion object {
//		const val CHANNEL_ID = "routine_playback_channel"
//		const val NOTIFICATION_ID = 1
//
//		const val ACTION_TOGGLE_PLAY_PAUSE = "com.example.studivo.TOGGLE_PLAY_PAUSE"
//		const val ACTION_CLOSE_BUBBLE = "com.example.studivo.CLOSE_BUBBLE"
//		const val ACTION_OPEN_PLAYBACK = "com.example.studivo.OPEN_PLAYBACK"
//
//		const val EXTRA_ROUTINE_NAME = "routine_name"
//		const val EXTRA_ROUTINE_ID = "routine_id"
//		const val EXTRA_IS_PLAYING = "is_playing"
//
//		private var callback: PlaybackControlCallback? = null
//
//		fun setCallback(callback: PlaybackControlCallback?) {
//			this.callback = callback
//		}
//
//		fun updateBubble(context: Context, routineName: String, isPlaying: Boolean) {
//			val intent = Intent(context, RoutinePlaybackService::class.java).apply {
//				action = "UPDATE_STATE"
//				putExtra(EXTRA_ROUTINE_NAME, routineName)
//				putExtra(EXTRA_IS_PLAYING, isPlaying)
//			}
//			context.startService(intent)
//		}
//	}
//
//	interface PlaybackControlCallback {
//		fun onPlayPause()
//		fun onStop()
//	}
//
//	override fun onBind(intent: Intent?): IBinder? = null
//
//	override fun onCreate() {
//		super.onCreate()
//		createNotificationChannel()
//	}
//
//	override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
//		when (intent?.action) {
//			ACTION_TOGGLE_PLAY_PAUSE -> {
//				togglePlayPause()
//			}
//			ACTION_CLOSE_BUBBLE -> {
//				// Solo cerrar la burbuja, no detener la rutina
//				removeBubble()
//			}
//			"UPDATE_STATE" -> {
//				routineName = intent.getStringExtra(EXTRA_ROUTINE_NAME) ?: "Rutina"
//				isPlaying = intent.getBooleanExtra(EXTRA_IS_PLAYING, true)
//				updateBubbleUI(routineName, isPlaying)
//			}
//			else -> {
//				routineName = intent?.getStringExtra(EXTRA_ROUTINE_NAME) ?: "Rutina"
//				routineId = intent?.getStringExtra(EXTRA_ROUTINE_ID) ?: ""
//				isPlaying = intent?.getBooleanExtra(EXTRA_IS_PLAYING, true) ?: true
//				removeBubble()
//				startForeground(NOTIFICATION_ID, createNotification(routineName, isPlaying))
//				showFloatingBubble(routineName, routineId)
//			}
//		}
//		return START_STICKY
//	}
//
//	private fun createNotificationChannel() {
//		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//			val channel = NotificationChannel(
//				CHANNEL_ID,
//				"Reproducción de Rutina",
//				NotificationManager.IMPORTANCE_LOW
//			).apply {
//				description = "Control de reproducción de rutina en segundo plano"
//				setShowBadge(false)
//			}
//
//			val notificationManager = getSystemService(NotificationManager::class.java)
//			notificationManager.createNotificationChannel(channel)
//		}
//	}
//
//	private fun createNotification(routineName: String, isPlaying: Boolean): Notification {
//		// ✅ CORREGIDO: Intent que navega a la pantalla de reproducción sin cerrarla
//		val openPlaybackIntent = PendingIntent.getActivity(
//			this,
//			0,
//			Intent(this, MainActivity::class.java).apply {
//				flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
//				action = ACTION_OPEN_PLAYBACK
//				putExtra(EXTRA_ROUTINE_ID, routineId)
//			},
//			PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
//		)
//
//		val playPauseIntent = PendingIntent.getService(
//			this,
//			1,
//			Intent(this, RoutinePlaybackService::class.java).apply {
//				action = ACTION_TOGGLE_PLAY_PAUSE
//			},
//			PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
//		)
//
//		return NotificationCompat.Builder(this, CHANNEL_ID)
//			.setContentTitle("Rutina en reproducción")
//			.setContentText(routineName)
//			.setSmallIcon(R.drawable.ic_metronomo)
//			.setContentIntent(openPlaybackIntent)
//			.addAction(
//				if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play,
//				if (isPlaying) "Pausar" else "Reanudar",
//				playPauseIntent
//			)
//			.setOngoing(true)
//			.setPriority(NotificationCompat.PRIORITY_LOW)
//			.build()
//	}
//
//	private fun showFloatingBubble(routineName: String, routineId: String) {
//		floatingView?.let {
//			try {
//				windowManager?.removeView(it)
//			} catch (e: Exception) {
//				e.printStackTrace()
//			}
//		}
//		floatingView = null
//
//		windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
//
//		val layoutType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//			WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
//		} else {
//			@Suppress("DEPRECATION")
//			WindowManager.LayoutParams.TYPE_PHONE
//		}
//
//		val params = WindowManager.LayoutParams(
//			WindowManager.LayoutParams.WRAP_CONTENT,
//			WindowManager.LayoutParams.WRAP_CONTENT,
//			layoutType,
//			WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
//			PixelFormat.TRANSLUCENT
//		).apply {
//			gravity = Gravity.TOP or Gravity.END
//			x = 20
//			y = 200
//		}
//
//		floatingView = LayoutInflater.from(this).inflate(R.layout.floating_bubble, null)
//
//		setupBubbleListeners(floatingView!!, params, routineName, routineId)
//
//		try {
//			windowManager?.addView(floatingView, params)
//		} catch (e: Exception) {
//			e.printStackTrace()
//		}
//	}
//
//	private fun setupBubbleListeners(
//		view: View,
//		params: WindowManager.LayoutParams,
//		routineName: String,
//		routineId: String
//	) {
//		val tvRoutineName = view.findViewById<TextView>(R.id.tvRoutineName)
//		val btnPlayPause = view.findViewById<ImageButton>(R.id.btnPlayPause)
//		val btnClose = view.findViewById<ImageButton>(R.id.btnClose)
//		val bubbleContainer = view.findViewById<View>(R.id.bubbleContainer)
//
//		tvRoutineName.text = routineName
//		updatePlayPauseButton(btnPlayPause, isPlaying)
//
//		// Variables para detectar arrastre vs click
//		var initialX = 0
//		var initialY = 0
//		var initialTouchX = 0f
//		var initialTouchY = 0f
//		var isDragging = false
//
//		// Arrastrar la burbuja completa
//		bubbleContainer.setOnTouchListener { v, event ->
//			when (event.action) {
//				MotionEvent.ACTION_DOWN -> {
//					initialX = params.x
//					initialY = params.y
//					initialTouchX = event.rawX
//					initialTouchY = event.rawY
//					isDragging = false
//					true
//				}
//				MotionEvent.ACTION_MOVE -> {
//					val dx = event.rawX - initialTouchX
//					val dy = event.rawY - initialTouchY
//
//					// Si se movió más de 10 pixels, es arrastre
//					if (Math.abs(dx) > 10 || Math.abs(dy) > 10) {
//						isDragging = true
//						params.x = initialX - dx.toInt() // Negativo porque está en el lado derecho
//						params.y = initialY + dy.toInt()
//						windowManager?.updateViewLayout(floatingView, params)
//					}
//					true
//				}
//				MotionEvent.ACTION_UP -> {
//					isDragging = false
//					true
//				}
//				else -> false
//			}
//		}
//
//		// Click en el nombre de la rutina → Abrir pantalla de reproducción
//		tvRoutineName.setOnClickListener {
//			if (!isDragging) {
//				openPlaybackScreen(routineId)
//			}
//		}
//
//		// Click en Play/Pause
//		btnPlayPause.setOnClickListener {
//			togglePlayPause()
//		}
//
//		// Click en Cerrar → Solo cierra la burbuja
//		btnClose.setOnClickListener {
//			removeBubble()
//		}
//	}
//
//	private fun updateBubbleUI(routineName: String, isPlaying: Boolean) {
//		this.isPlaying = isPlaying
//
//		floatingView?.let { view ->
//			val tvRoutineName = view.findViewById<TextView>(R.id.tvRoutineName)
//			val btnPlayPause = view.findViewById<ImageButton>(R.id.btnPlayPause)
//
//			tvRoutineName.text = routineName
//			updatePlayPauseButton(btnPlayPause, isPlaying)
//		}
//
//		// Actualizar notificación
//		val notification = createNotification(routineName, isPlaying)
//		val notificationManager = getSystemService(NotificationManager::class.java)
//		notificationManager.notify(NOTIFICATION_ID, notification)
//	}
//
//	private fun updatePlayPauseButton(button: ImageButton, isPlaying: Boolean) {
//		button.setImageResource(
//			if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play
//		)
//	}
//
//	private fun togglePlayPause() {
//		isPlaying = !isPlaying
//		callback?.onPlayPause()
//
//		floatingView?.let { view ->
//			val btnPlayPause = view.findViewById<ImageButton>(R.id.btnPlayPause)
//			updatePlayPauseButton(btnPlayPause, isPlaying)
//		}
//
//		// Actualizar la notificación también
//		val notification = createNotification(routineName, isPlaying)
//		val notificationManager = getSystemService(NotificationManager::class.java)
//		notificationManager.notify(NOTIFICATION_ID, notification)
//	}
//
//	private fun openPlaybackScreen(routineId: String) {
//		val intent = Intent(this, MainActivity::class.java).apply {
//			flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
//			action = ACTION_OPEN_PLAYBACK
//			putExtra(EXTRA_ROUTINE_ID, routineId)
//		}
//		startActivity(intent)
//	}
//
//	private fun stopBubbleAndService() {
//		callback?.onStop()
//		removeBubble()
//		stopForeground(STOP_FOREGROUND_REMOVE)
//		stopSelf()
//	}
//
//	private fun removeBubble() {
//		floatingView?.let {
//			try {
//				windowManager?.removeView(it)
//			} catch (e: Exception) {
//				e.printStackTrace()
//			}
//			floatingView = null
//		}
//	}
//
//	override fun onDestroy() {
//		super.onDestroy()
//		removeBubble()
//		callback = null
//	}
//}