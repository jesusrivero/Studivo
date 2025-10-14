package com.example.studivo.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowRight
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
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.studivo.domain.viewmodels.ThemeViewModel


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
	
	// Estado para controlar el Dialog
	var dialogText by remember { mutableStateOf<String?>(null) }
	var dialogTitle by remember { mutableStateOf<String?>(null) }
	
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
			// 🌗 APARIENCIA
			SettingsSectionTitle("Apariencia")
			SettingsSwitchItem(
				title = "Modo oscuro",
				icon = Icons.Default.DarkMode,
				checked = isDarkMode,
				onCheckedChange = { viewModel.toggleDarkMode(it) },
			)
			
			Spacer(modifier = Modifier.height(12.dp))
			Divider(Modifier.alpha(0.2f))
			Spacer(modifier = Modifier.height(12.dp))
			
			// ℹ️ INFORMACIÓN GENERAL
			SettingsSectionTitle("Información")
			SettingsItem("Sobre nosotros", Icons.Default.Info) {
				dialogTitle = "Sobre nosotros"
				dialogText = "Aquí va todo el contenido de Sobre Nosotros..."
			}
			SettingsItem("Políticas de privacidad", Icons.Default.PrivacyTip) {
				dialogTitle = "Políticas de privacidad"
				dialogText = "Aquí va todo el contenido de Políticas de Privacidad..."
			}
			SettingsItem("Términos y condiciones", Icons.Default.Description) {
				dialogTitle = "Términos y condiciones"
				dialogText = "Aquí va todo el contenido de Términos y Condiciones..."
			}
			
			Spacer(modifier = Modifier.height(28.dp))
			
			// 🔢 VERSIÓN
			Text(
				text = "Versión 1.0.0",
				style = MaterialTheme.typography.bodySmall,
				color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
				modifier = Modifier.align(Alignment.CenterHorizontally)
			)
		}
		
		// Dialog modal
		// Dentro de SettingsScreenContent
		if (dialogText != null && dialogTitle != null) {
			Dialog(onDismissRequest = { dialogText = null; dialogTitle = null }) {
				Box(
					modifier = Modifier
						.fillMaxWidth()
						.fillMaxHeight(0.9f) // ocupa 90% de la pantalla
						.clip(RoundedCornerShape(16.dp))
						.background(MaterialTheme.colorScheme.surface)
						.padding(16.dp)
				) {
					Column(
						modifier = Modifier
							.fillMaxWidth()
							.verticalScroll(rememberScrollState())
					) {
						Text(
							text = dialogTitle!!,
							style = MaterialTheme.typography.titleLarge.copy(
								fontWeight = FontWeight.Bold
							),
							modifier = Modifier.padding(bottom = 12.dp)
						)
						Text(
							text = dialogText!!,
							style = MaterialTheme.typography.bodyMedium,
							modifier = Modifier.padding(bottom = 24.dp)
						)
						TextButton(
							onClick = { dialogText = null; dialogTitle = null },
							modifier = Modifier.align(Alignment.End)
						) {
							Text("Cerrar")
						}
					}
				}
			}
		}
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
