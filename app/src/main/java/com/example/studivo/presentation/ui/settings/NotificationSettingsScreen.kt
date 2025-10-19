package com.example.studivo.presentation.ui.settings


import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import com.example.studivo.domain.viewmodels.NotificationSettingsViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationSettingsScreen(
	navController: NavController,
	viewModel: NotificationSettingsViewModel = hiltViewModel(),
	onNavigateBack: () -> Unit,
) {
	val context = LocalContext.current
	val notificationsEnabled by viewModel.notificationsEnabled.collectAsState()
	val notificationTimes by viewModel.notificationTimes.collectAsState()
	
	var showTimePickerDialog by remember { mutableStateOf(false) }
	var showBatteryDialog by remember { mutableStateOf(false) }
	
	Scaffold(
		topBar = {
			TopAppBar(
				title = { Text("Configuración de Notificaciones") },
				navigationIcon = {
					IconButton(onClick = onNavigateBack) {
						Icon(Icons.Default.ArrowBack, "Volver")
					}
				}
			)
		}
	) { padding ->
		Column(
			modifier = Modifier
				.fillMaxSize()
				.padding(padding)
				.verticalScroll(rememberScrollState())
				.padding(16.dp)
		) {
			
			// ============================================
			// Activar/Desactivar Notificaciones
			// ============================================
			Card(
				modifier = Modifier.fillMaxWidth(),
				colors = CardDefaults.cardColors(
					containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
				)
			) {
				Row(
					modifier = Modifier
						.fillMaxWidth()
						.padding(16.dp),
					horizontalArrangement = Arrangement.SpaceBetween,
					verticalAlignment = Alignment.CenterVertically
				) {
					Column(modifier = Modifier.weight(1f)) {
						Text(
							text = "Recordatorios diarios",
							style = MaterialTheme.typography.titleMedium
						)
						Text(
							text = "Recibe recordatorios para mantener tu racha",
							style = MaterialTheme.typography.bodySmall,
							color = MaterialTheme.colorScheme.onSurfaceVariant
						)
					}
					
					Switch(
						checked = notificationsEnabled,
						onCheckedChange = { enabled ->
							viewModel.setNotificationsEnabled(enabled)
						},
						colors = SwitchDefaults.colors(
							checkedThumbColor = MaterialTheme.colorScheme.primary,
							checkedTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
							uncheckedTrackColor = Color(0xFFE0E0E0),
							uncheckedThumbColor = Color.White
						)
					)
				}
			}
			
			Spacer(modifier = Modifier.height(16.dp))
			
			// ============================================
			// Horarios de Notificaciones
			// ============================================
			if (notificationsEnabled) {
				Card(
					modifier = Modifier.fillMaxWidth(),
					colors = CardDefaults.cardColors(
						containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
					)
				) {
					Column(
						modifier = Modifier.padding(16.dp)
					) {
						Row(
							modifier = Modifier.fillMaxWidth(),
							horizontalArrangement = Arrangement.SpaceBetween,
							verticalAlignment = Alignment.CenterVertically
						) {
							Text(
								text = "Horarios de recordatorio",
								style = MaterialTheme.typography.titleMedium
							)
							
							IconButton(onClick = { showTimePickerDialog = true }) {
								Icon(Icons.Default.Add, "Agregar horario")
							}
						}
						
						Spacer(modifier = Modifier.height(8.dp))
						
						if (notificationTimes.isEmpty()) {
							Text(
								text = "No hay horarios configurados",
								style = MaterialTheme.typography.bodyMedium,
								color = MaterialTheme.colorScheme.onSurfaceVariant
							)
						} else {
							notificationTimes.forEachIndexed { index, time ->
								Row(
									modifier = Modifier
										.fillMaxWidth()
										.padding(vertical = 4.dp),
									horizontalArrangement = Arrangement.SpaceBetween,
									verticalAlignment = Alignment.CenterVertically
								) {
									Row(
										verticalAlignment = Alignment.CenterVertically
									) {
										Icon(
											Icons.Default.Notifications,
											contentDescription = null,
											tint = MaterialTheme.colorScheme.primary
										)
										Spacer(modifier = Modifier.width(12.dp))
										Text(
											text = String.format("%02d:%02d", time.first, time.second),
											style = MaterialTheme.typography.bodyLarge
										)
									}
									
									IconButton(
										onClick = { viewModel.removeNotificationTime(index) }
									) {
										Icon(
											Icons.Default.Delete,
											contentDescription = "Eliminar",
											tint = MaterialTheme.colorScheme.error
										)
									}
								}
								
								if (index < notificationTimes.size - 1) {
									HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
								}
							}
						}
					}
				}
				
				Spacer(modifier = Modifier.height(16.dp))
			}
			
			// ============================================
			// Optimización de Batería
			// ============================================
//			Card(
//				modifier = Modifier.fillMaxWidth(),
//				colors = CardDefaults.cardColors(
//					containerColor = MaterialTheme.colorScheme.errorContainer
//				)
//			) {
//				Column(
//					modifier = Modifier.padding(16.dp)
//				) {
//					Row(
//						verticalAlignment = Alignment.CenterVertically
//					) {
//						Icon(
//							Icons.Default.Warning,
//							contentDescription = null,
//							tint = MaterialTheme.colorScheme.error
//						)
//						Spacer(modifier = Modifier.width(8.dp))
//						Text(
//							text = "Importante para notificaciones",
//							style = MaterialTheme.typography.titleMedium,
//							color = MaterialTheme.colorScheme.onErrorContainer
//						)
//					}
//
//					Spacer(modifier = Modifier.height(8.dp))
//
//					Text(
//						text = "Para que las notificaciones funcionen correctamente incluso con batería baja, necesitas desactivar la optimización de batería para Studivo.",
//						style = MaterialTheme.typography.bodyMedium,
//						color = MaterialTheme.colorScheme.onErrorContainer
//					)
//
//					Spacer(modifier = Modifier.height(12.dp))
//
//					Button(
//						onClick = { showBatteryDialog = true },
//						modifier = Modifier.fillMaxWidth(),
//						colors = ButtonDefaults.buttonColors(
//							containerColor = MaterialTheme.colorScheme.error
//						)
//					) {
//						Icon(Icons.Default.Settings, contentDescription = null)
//						Spacer(modifier = Modifier.width(8.dp))
//						Text("Configurar ahora")
//					}
//				}
//			}
//
//			Spacer(modifier = Modifier.height(16.dp))
			
			// ============================================
			// Información adicional
			// ============================================
			Card(
				modifier = Modifier.fillMaxWidth(),
				colors = CardDefaults.cardColors(
					containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
				)
			) {
				Column(
					modifier = Modifier.padding(16.dp)
				) {
					Row(
						verticalAlignment = Alignment.CenterVertically
					) {
						Icon(
							Icons.Default.Info,
							contentDescription = null,
							tint = MaterialTheme.colorScheme.primary
						)
						Spacer(modifier = Modifier.width(8.dp))
						Text(
							text = "Sobre los recordatorios",
							style = MaterialTheme.typography.titleMedium
						)
					}
					
					Spacer(modifier = Modifier.height(8.dp))
					
					Text(
						text = """
                            • Los mensajes varían según tus días sin estudiar
                            • Si estudias hoy, no recibirás recordatorios
                            • Los recordatorios ayudan a mantener tu racha
                            • Puedes configurar múltiples horarios
                        """.trimIndent(),
						style = MaterialTheme.typography.bodyMedium,
						color = MaterialTheme.colorScheme.onSurfaceVariant
					)
				}
			}
			
			Spacer(modifier = Modifier.height(24.dp))
			
			// ============================================
			// Botón de prueba
			// ============================================
//			OutlinedButton(
//				onClick = { viewModel.sendTestNotification() },
//				modifier = Modifier.fillMaxWidth()
//			) {
//				Icon(Icons.Default.Send, contentDescription = null)
//				Spacer(modifier = Modifier.width(8.dp))
//				Text("Enviar notificación de prueba")
//			}
		}
	}
	
	// ============================================
	// Time Picker Dialog
	// ============================================
	if (showTimePickerDialog) {
		TimePickerDialog(
			onDismiss = { showTimePickerDialog = false },
			onTimeSelected = { hour, minute ->
				viewModel.addNotificationTime(hour, minute)
				showTimePickerDialog = false
			}
		)
	}
	
	// ============================================
	// Battery Optimization Dialog
	// ============================================
	if (showBatteryDialog) {
		AlertDialog(
			onDismissRequest = { showBatteryDialog = false },
			icon = { Icon(Icons.Default.Warning, contentDescription = null) },
			title = { Text("Desactivar optimización de batería") },
			text = {
				Text(
					"""
                    Para que las notificaciones funcionen correctamente:
                    
                    1. Toca "Ir a configuración"
                    2. Busca "Studivo" en la lista
                    3. Selecciona "No optimizar"
                    4. Confirma tu elección
                    
                    Esto permitirá que recibas recordatorios incluso cuando tu batería esté baja.
                    """.trimIndent()
				)
			},
			confirmButton = {
				Button(
					onClick = {
						showBatteryDialog = false
						openBatteryOptimizationSettings(context)
					}
				) {
					Text("Ir a configuración")
				}
			},
			dismissButton = {
				TextButton(onClick = { showBatteryDialog = false }) {
					Text("Más tarde")
				}
			}
		)
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialog(
	onDismiss: () -> Unit,
	onTimeSelected: (hour: Int, minute: Int) -> Unit
) {
	val timePickerState = rememberTimePickerState(
		initialHour = 10,
		initialMinute = 0,
		is24Hour = true
	)
	
	AlertDialog(
		onDismissRequest = onDismiss
	) {
		Surface(
			shape = MaterialTheme.shapes.extraLarge,
			tonalElevation = 6.dp
		) {
			Column(
				modifier = Modifier.padding(24.dp),
				horizontalAlignment = Alignment.CenterHorizontally
			) {
				Text(
					text = "Selecciona la hora",
					style = MaterialTheme.typography.titleLarge
				)
				
				Spacer(modifier = Modifier.height(16.dp))
				
				TimePicker(state = timePickerState)
				
				Spacer(modifier = Modifier.height(16.dp))
				
				Row(
					modifier = Modifier.fillMaxWidth(),
					horizontalArrangement = Arrangement.End
				) {
					TextButton(onClick = onDismiss) {
						Text("Cancelar")
					}
					Spacer(modifier = Modifier.width(8.dp))
					Button(
						onClick = {
							onTimeSelected(
								timePickerState.hour,
								timePickerState.minute
							)
						}
					) {
						Text("Confirmar")
					}
				}
			}
		}
	}
}

private fun openBatteryOptimizationSettings(context: android.content.Context) {
	if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
		try {
			val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
				data = Uri.parse("package:${context.packageName}")
			}
			context.startActivity(intent)
		} catch (e: Exception) {
			// Fallback: abrir configuración general de batería
			try {
				val intent = Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)
				context.startActivity(intent)
			} catch (e: Exception) {
				// No hacer nada si falla
			}
		}
	}
}

