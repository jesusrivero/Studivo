package com.example.studivo.presentation.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.studivo.domain.viewmodels.ThemeViewModel
import com.example.studivo.presentation.navegacion.AppRoutes


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
	navController: NavController,
) {
	SettingsScreenContent(navController = navController)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreenContent(
	navController: NavController,
	viewModel: ThemeViewModel = hiltViewModel(),
) {
	val isDarkMode by viewModel.isDarkMode.collectAsState()
	val context = LocalContext.current
	
	Scaffold(
		topBar = {
			CenterAlignedTopAppBar(
				title = { Text("Ajustes", style = MaterialTheme.typography.titleLarge) },
				navigationIcon = {
					IconButton(onClick = { navController.navigateUp() }) {
						Icon(
							imageVector = Icons.Default.ArrowBack,
							contentDescription = "Back"
						)
					}
				},
				colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
					containerColor = MaterialTheme.colorScheme.surface
				)
			)
		},
		containerColor = MaterialTheme.colorScheme.surface
	) { innerPadding ->
		Column(
			modifier = Modifier
				.padding(innerPadding)
				.fillMaxSize()
				.verticalScroll(rememberScrollState())
				.padding(horizontal = 20.dp, vertical = 12.dp)
		) {
			
			SettingsSectionTitle("Apariencia")
			SettingsSwitchItem(
				title = "Modo oscuro",
				icon = Icons.Default.DarkMode,
				checked = isDarkMode,
				onCheckedChange = { viewModel.toggleDarkMode(it) },
			)
			
//			Spacer(modifier = Modifier.height(12.dp))
//			Divider(Modifier.alpha(0.2f))
//			Spacer(modifier = Modifier.height(12.dp))
//
//			// Nueva sección para Notificaciones
//			SettingsSectionTitle("Notificaciones")
//			SettingsItem(
//				title = "Configuración de notificaciones",
//				icon = Icons.Default.Notifications
//			) {
//				// Navegar a NotificationSettingsScreen
//				navController.navigate(AppRoutes.NotificationSettingsScreen)
//			}
			
			Spacer(modifier = Modifier.height(12.dp))
			Divider(Modifier.alpha(0.2f))
			Spacer(modifier = Modifier.height(12.dp))
			
			SettingsSectionTitle("Información Legal")
			SettingsItem("Políticas de privacidad", Icons.Default.PrivacyTip) {
				openUrl(context, "https://jesusrivero.github.io/Politicas-de-privacidad/")
			}
			SettingsItem("Términos y condiciones", Icons.Default.Description) {
				openUrl(context, "https://jesusrivero.github.io/Terminos-y-condiciones-Studivo/")
			}
			
			Spacer(modifier = Modifier.height(28.dp))
			
			
			Text(
				text = "Versión 1.0.0",
				style = MaterialTheme.typography.bodySmall,
				color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
				modifier = Modifier.align(Alignment.CenterHorizontally)
			)
			
			Spacer(modifier = Modifier.height(12.dp))
			

			Text(
				text = "Studivo es una app creada para facilitar el estudio musical, permitiendo organizar rutinas, practicar con metrónomo y compartir ejercicios mediante códigos QR cifrados.\nBuscamos ofrecer una experiencia segura y educativa.",
				style = MaterialTheme.typography.bodySmall,
				color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
				textAlign = TextAlign.Center,
				modifier = Modifier
					.align(Alignment.CenterHorizontally)
					.padding(horizontal = 16.dp)
			)
			
			Spacer(modifier = Modifier.height(20.dp))
		}
	}
}


private fun openUrl(context: Context, url: String) {
	try {
		val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
		context.startActivity(intent)
	} catch (e: Exception) {
		Toast.makeText(context, "No se pudo abrir el enlace", Toast.LENGTH_SHORT).show()
	}
}

@Composable
fun SettingsSectionTitle(title: String) {
	Text(
		text = title,
		style = MaterialTheme.typography.titleMedium.copy(
			color = MaterialTheme.colorScheme.primary.copy(alpha = 0.9f)
		),
		modifier = Modifier.padding(bottom = 6.dp, top = 4.dp)
	)
}

@Composable
fun SettingsSwitchItem(
	title: String,
	icon: ImageVector,
	checked: Boolean,
	onCheckedChange: (Boolean) -> Unit,
) {
	Row(
		modifier = Modifier
			.fillMaxWidth()
			.clip(RoundedCornerShape(12.dp))
			.clickable { onCheckedChange(!checked) }
			.padding(horizontal = 8.dp, vertical = 12.dp),
		verticalAlignment = Alignment.CenterVertically
	) {
		Icon(
			imageVector = icon,
			contentDescription = null,
			tint = MaterialTheme.colorScheme.primary,
			modifier = Modifier.size(24.dp)
		)
		Spacer(modifier = Modifier.width(16.dp))
		Text(
			text = title,
			style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
			modifier = Modifier.weight(1f)
		)
		Switch(
			checked = checked,
			onCheckedChange = onCheckedChange,
			colors = SwitchDefaults.colors(
				checkedThumbColor = MaterialTheme.colorScheme.primary,
				checkedTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
				uncheckedTrackColor = Color(0xFFE0E0E0),
				uncheckedThumbColor = Color.White
			)
		)
	}
}

@Composable
fun SettingsItem(
	title: String,
	icon: ImageVector,
	onClick: () -> Unit,
) {
	Row(
		modifier = Modifier
			.fillMaxWidth()
			.clip(RoundedCornerShape(12.dp))
			.clickable { onClick() }
			.padding(horizontal = 8.dp, vertical = 12.dp),
		verticalAlignment = Alignment.CenterVertically
	) {
		Icon(
			imageVector = icon,
			contentDescription = null,
			tint = MaterialTheme.colorScheme.primary,
			modifier = Modifier.size(24.dp)
		)
		Spacer(modifier = Modifier.width(16.dp))
		Text(
			text = title,
			style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
			modifier = Modifier.weight(1f)
		)
		Icon(
			imageVector = Icons.Default.KeyboardArrowRight,
			contentDescription = null,
			tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
		)
	}
}
