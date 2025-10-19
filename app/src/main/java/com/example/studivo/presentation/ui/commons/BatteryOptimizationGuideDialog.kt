package com.example.studivo.presentation.ui.commons

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.studivo.domain.model.helpers.BatteryOptimizationHelper
import com.example.studivo.presentation.utils.isBatteryOptimizationDisabled

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BatteryOptimizationGuideDialog(
	onDismiss: () -> Unit
) {
	val context = LocalContext.current
	val manufacturer = remember { BatteryOptimizationHelper.getCurrentManufacturer() }
	val instructions = remember { BatteryOptimizationHelper.getInstructions(manufacturer) }
	
	AlertDialog(
		onDismissRequest = onDismiss
	) {
		Surface(
			shape = MaterialTheme.shapes.extraLarge,
			tonalElevation = 6.dp
		) {
			Column(
				modifier = Modifier
					.padding(24.dp)
					.verticalScroll(rememberScrollState())
			) {
				Icon(
					Icons.Default.PhoneAndroid,
					contentDescription = null,
					modifier = Modifier.size(48.dp),
					tint = MaterialTheme.colorScheme.primary
				)
				
				Spacer(modifier = Modifier.height(16.dp))
				
				Text(
					text = "Configuración para ${manufacturer.name}",
					style = MaterialTheme.typography.headlineSmall
				)
				
				Spacer(modifier = Modifier.height(8.dp))
				
				Text(
					text = "Sigue estos pasos para que las notificaciones funcionen correctamente:",
					style = MaterialTheme.typography.bodyMedium,
					color = MaterialTheme.colorScheme.onSurfaceVariant
				)
				
				Spacer(modifier = Modifier.height(16.dp))
				
				Card(
					colors = CardDefaults.cardColors(
						containerColor = MaterialTheme.colorScheme.surfaceVariant
					)
				) {
					Column(
						modifier = Modifier.padding(16.dp)
					) {
						instructions.forEach { instruction ->
							Text(
								text = instruction,
								style = MaterialTheme.typography.bodyMedium,
								modifier = Modifier.padding(vertical = 4.dp)
							)
						}
					}
				}
				
				Spacer(modifier = Modifier.height(24.dp))
				
				Row(
					modifier = Modifier.fillMaxWidth(),
					horizontalArrangement = Arrangement.spacedBy(8.dp)
				) {
					TextButton(
						onClick = onDismiss,
						modifier = Modifier.weight(1f)
					) {
						Text("Entendido")
					}
					
					Button(
						onClick = {
							BatteryOptimizationHelper.openBatterySettings(context, manufacturer)
						},
						modifier = Modifier.weight(1f)
					) {
						Text("Ir a config")
					}
				}
			}
		}
	}
}




@Composable
fun BatteryOptimizationWarningBanner(
	onConfigureClick: () -> Unit
) {
	val context = LocalContext.current
	val isOptimized = remember { !context.isBatteryOptimizationDisabled() }
	
	if (isOptimized) {
		Card(
			modifier = Modifier
				.fillMaxWidth()
				.padding(16.dp),
			colors = CardDefaults.cardColors(
				containerColor = MaterialTheme.colorScheme.errorContainer
			)
		) {
			Column(
				modifier = Modifier.padding(16.dp)
			) {
				Text(
					text = "⚠️ Acción requerida",
					style = MaterialTheme.typography.titleMedium,
					color = MaterialTheme.colorScheme.onErrorContainer
				)
				
				Spacer(modifier = Modifier.height(4.dp))
				
				Text(
					text = "Las notificaciones pueden no funcionar correctamente. Configura tu dispositivo para permitir que Studivo se ejecute en segundo plano.",
					style = MaterialTheme.typography.bodyMedium,
					color = MaterialTheme.colorScheme.onErrorContainer
				)
				
				Spacer(modifier = Modifier.height(12.dp))
				
				Button(
					onClick = onConfigureClick,
					colors = ButtonDefaults.buttonColors(
						containerColor = MaterialTheme.colorScheme.error
					)
				) {
					Text("Configurar ahora")
				}
			}
		}
	}
}