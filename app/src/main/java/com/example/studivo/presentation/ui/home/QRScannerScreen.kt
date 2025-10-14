package com.example.studivo.presentation.ui.home

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.studivo.domain.viewmodels.ImportUiState
import com.example.studivo.domain.viewmodels.RoutineShareViewModel
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.CompoundBarcodeView
import kotlinx.coroutines.delay


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QRScannerScreen(
	navController: NavController,
	viewModel: RoutineShareViewModel = hiltViewModel(),
) {
	val importState by viewModel.importState.collectAsState()
	var hasScanned by remember { mutableStateOf(false) }
	var showConfirmDialog by remember { mutableStateOf(false) }
	var scannedData by remember { mutableStateOf("") }
	var hasCameraPermission by remember { mutableStateOf(false) }
	
	
	val permissionLauncher = rememberLauncherForActivityResult(
		contract = ActivityResultContracts.RequestPermission()
	) { isGranted ->
		hasCameraPermission = isGranted
	}
	val context = LocalContext.current
	
	LaunchedEffect(Unit) {
		
		if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
			== PackageManager.PERMISSION_GRANTED
		) {
			hasCameraPermission = true
		} else {
			permissionLauncher.launch(Manifest.permission.CAMERA)
		}
	}
	
	
	LaunchedEffect(importState) {
		if (importState is ImportUiState.Success) {
			navController.popBackStack()
		}
	}
	
	Scaffold(
		topBar = {
			TopAppBar(
				title = { Text("Escanear Rutina") },
				navigationIcon = {
					IconButton(onClick = { navController.popBackStack() }) {
						Icon(Icons.Default.Close, contentDescription = "Cerrar")
					}
				},
				colors = TopAppBarDefaults.topAppBarColors(
					containerColor = MaterialTheme.colorScheme.surface
				)
			)
		}
	) { padding ->
		Box(
			modifier = Modifier
				.fillMaxSize()
				.padding(padding)
		) {
			when {
				
				hasCameraPermission -> {
					AndroidView(
						factory = { context ->
							CompoundBarcodeView(context).apply {
								val callback = object : BarcodeCallback {
									override fun barcodeResult(result: BarcodeResult?) {
										result?.text?.let { qrData ->
											if (!hasScanned) {
												hasScanned = true
												scannedData = qrData
												showConfirmDialog = true
												pause()
											}
										}
									}
								}
								decodeContinuous(callback)
								resume()
							}
						},
						modifier = Modifier.fillMaxSize(),
						update = { view ->
							if (!hasScanned) view.resume()
						}
					)
				}
				
				
				else -> {
					Column(
						modifier = Modifier
							.fillMaxSize()
							.padding(32.dp),
						verticalArrangement = Arrangement.Center,
						horizontalAlignment = Alignment.CenterHorizontally
					) {
						Text(
							"Necesitas otorgar permiso de cámara para escanear códigos QR.",
							textAlign = TextAlign.Center,
							style = MaterialTheme.typography.bodyLarge
						)
						Spacer(modifier = Modifier.height(16.dp))
						Button(onClick = {
							permissionLauncher.launch(Manifest.permission.CAMERA)
						}) {
							Text("Conceder permiso")
						}
					}
				}
			}
		}
	}
	
	
	if (showConfirmDialog) {
		ImportConfirmationDialog(
			onConfirm = {
				viewModel.importRoutineFromQR(scannedData)
				showConfirmDialog = false
			},
			onDismiss = {
				showConfirmDialog = false
				hasScanned = false
			},
			isLoading = importState is ImportUiState.Loading
		)
	}
	
	
	if (importState is ImportUiState.Error) {
		LaunchedEffect(Unit) {
			delay(3000)
			viewModel.resetImportState()
			hasScanned = false
		}
		
		Box(
			modifier = Modifier
				.fillMaxSize()
				.padding(16.dp),
			contentAlignment = Alignment.BottomCenter
		) {
			Surface(
				shape = RoundedCornerShape(12.dp),
				color = MaterialTheme.colorScheme.errorContainer
			) {
				Text(
					text = (importState as ImportUiState.Error).message,
					color = MaterialTheme.colorScheme.onErrorContainer,
					modifier = Modifier.padding(16.dp),
					style = MaterialTheme.typography.bodyMedium
				)
			}
		}
	}
}
@Composable
fun ImportConfirmationDialog(
	onConfirm: () -> Unit,
	onDismiss: () -> Unit,
	isLoading: Boolean,
) {
	AlertDialog(
		onDismissRequest = onDismiss,
		icon = {
			Box(
				modifier = Modifier
					.size(80.dp)
					.background(
						color = MaterialTheme.colorScheme.primaryContainer,
						shape = CircleShape
					),
				contentAlignment = Alignment.Center
			) {
				Icon(
					imageVector = Icons.Default.Download, // puedes cambiar el ícono si deseas
					contentDescription = null,
					modifier = Modifier.size(40.dp),
					tint = MaterialTheme.colorScheme.onPrimaryContainer
				)
			}
		},
		title = {
			Text(
				text = "¿Importar rutina?",
				style = MaterialTheme.typography.titleLarge.copy(
					fontWeight = FontWeight.Bold
				),
				textAlign = TextAlign.Center
			)
		},
		text = {
			if (isLoading) {
				Box(
					modifier = Modifier
						.fillMaxWidth()
						.padding(vertical = 16.dp),
					contentAlignment = Alignment.Center
				) {
					CircularProgressIndicator(
						color = MaterialTheme.colorScheme.primary
					)
				}
			} else {
				Text(
					text = "Esta rutina con todas sus fases será agregada a tu biblioteca.",
					textAlign = TextAlign.Center,
					style = MaterialTheme.typography.bodyMedium,
					color = MaterialTheme.colorScheme.onSurfaceVariant
				)
			}
		},
		confirmButton = {
			Button(
				onClick = onConfirm,
				enabled = !isLoading,
				modifier = Modifier
					.fillMaxWidth()
					.height(52.dp),
				shape = RoundedCornerShape(16.dp),
				contentPadding = PaddingValues(vertical = 14.dp),
				elevation = ButtonDefaults.buttonElevation(
					defaultElevation = 4.dp,
					pressedElevation = 8.dp
				)
			) {
				if (isLoading) {
					CircularProgressIndicator(
						modifier = Modifier.size(20.dp),
						strokeWidth = 2.dp,
						color = MaterialTheme.colorScheme.onPrimary
					)
				} else {
					Icon(
						imageVector = Icons.Default.Download,
						contentDescription = null,
						modifier = Modifier.size(20.dp)
					)
					Spacer(modifier = Modifier.width(8.dp))
					Text(
						text = "Agregar rutina",
						style = MaterialTheme.typography.titleMedium,
						fontWeight = FontWeight.Bold
					)
				}
			}
		},
		dismissButton = {
			TextButton(
				onClick = onDismiss,
				enabled = !isLoading,
				modifier = Modifier.fillMaxWidth(),
				shape = RoundedCornerShape(16.dp)
			) {
				Text(
					text = "Cancelar",
					style = MaterialTheme.typography.bodyMedium,
					color = MaterialTheme.colorScheme.onSurfaceVariant
				)
			}
		},
		containerColor = MaterialTheme.colorScheme.surface,
		shape = RoundedCornerShape(24.dp)
	)
}
