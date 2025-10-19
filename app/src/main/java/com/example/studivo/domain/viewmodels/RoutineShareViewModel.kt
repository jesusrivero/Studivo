package com.example.studivo.domain.viewmodels

import android.content.Context
import android.graphics.Bitmap
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studivo.domain.model.RoutineShareData
import com.example.studivo.domain.usecase.RoutineShareUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import android.util.Log.d
import android.util.Log.e
import android.util.Log.w

@HiltViewModel
class RoutineShareViewModel @Inject constructor(
	private val shareUseCases: RoutineShareUseCases,
	@ApplicationContext private val context: Context
) : ViewModel() {
	
	private val _uiState = MutableStateFlow<ShareUiState>(ShareUiState.Idle)
	val uiState: StateFlow<ShareUiState> = _uiState.asStateFlow()
	
	private val _qrBitmap = MutableStateFlow<Bitmap?>(null)
	val qrBitmap: StateFlow<Bitmap?> = _qrBitmap.asStateFlow()
	
	private val _importState = MutableStateFlow<ImportUiState>(ImportUiState.Idle)
	val importState: StateFlow<ImportUiState> = _importState.asStateFlow()
	
	companion object {
		private const val TAG = "RoutineShareVM"
	}
	
	fun generateQRForRoutine(routineId: String) {
		viewModelScope.launch {
			try {
				d(TAG, "🔄 Iniciando generación de QR para rutina: $routineId")
				_uiState.value = ShareUiState.Loading
				
				val routineData = shareUseCases.exportRoutine(routineId)
				if (routineData == null) {
					e(TAG, "❌ No se pudo exportar la rutina")
					_uiState.value = ShareUiState.Error("No se pudo exportar la rutina")
					return@launch
				}
				
			d(TAG, "✅ Rutina exportada: ${routineData.routine.name} con ${routineData.phases.size} fases")
				
				// Usar compresión
				val compressedData = routineData.toCompressedQRData()
				
				// Log para debugging
				val originalSize = routineData.toJson().length
				val compressedSize = compressedData.length
		d(TAG, "📊 Original: $originalSize bytes | Comprimido: $compressedSize bytes | Reducción: ${((1 - compressedSize.toFloat() / originalSize) * 100).toInt()}%")
				
				if (compressedSize > 2900) {
					w(TAG, "⚠️ El QR podría ser demasiado grande: $compressedSize bytes")
				}
				
				val qrBitmap = shareUseCases.generateQRCode(compressedData)
				
				if (qrBitmap == null) {
					e(TAG, "❌ No se pudo generar el bitmap del QR")
					_uiState.value = ShareUiState.Error("No se pudo generar el código QR. La rutina podría tener demasiadas fases.")
					return@launch
				}
				
			d(TAG, "✅ QR generado exitosamente: ${qrBitmap.width}x${qrBitmap.height}")
				
				_qrBitmap.value = qrBitmap
				_uiState.value = ShareUiState.Success(routineData, qrBitmap)
				
			} catch (e: Exception) {
		e(TAG, "❌ Error generando QR: ${e.message}", e)
				_uiState.value = ShareUiState.Error(e.localizedMessage ?: "Error desconocido")
			}
		}
	}
	
	fun shareQRCode() {
		viewModelScope.launch {
			try {
				val bitmap = _qrBitmap.value ?: return@launch
				
				val cachePath = File(context.cacheDir, "qr_codes")
				cachePath.mkdirs()
				val file = File(cachePath, "routine_qr_${System.currentTimeMillis()}.png")
				
				FileOutputStream(file).use { out ->
					bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
				}
				
				val uri = FileProvider.getUriForFile(
					context,
					"${context.packageName}.fileprovider",
					file
				)
				
				val shareIntent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
					type = "image/png"
					putExtra(android.content.Intent.EXTRA_STREAM, uri)
					putExtra(android.content.Intent.EXTRA_TEXT, "Escanea este código QR para importar mi rutina de práctica")
					addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
				}
				
				context.startActivity(
					android.content.Intent.createChooser(shareIntent, "Compartir rutina").apply {
						addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
					}
				)
				
			} catch (e: Exception) {
				_uiState.value = ShareUiState.Error("Error al compartir: ${e.localizedMessage}")
			}
		}
	}
	
	fun importRoutineFromQR(qrData: String) {
		viewModelScope.launch {
			try {
				_importState.value = ImportUiState.Loading
				
				// ✅ CAMBIO: Intentar descomprimir primero, si falla usar método antiguo
				val routineData = RoutineShareData.fromCompressedQRData(qrData)
					?: RoutineShareData.fromJson(qrData) // Fallback para compatibilidad
				
				if (routineData == null) {
					_importState.value = ImportUiState.Error("Código QR inválido")
					return@launch
				}
				
				val newRoutineId = shareUseCases.importRoutine(routineData)
				_importState.value = ImportUiState.Success(routineData.routine.name, newRoutineId)
				
			} catch (e: Exception) {
				_importState.value = ImportUiState.Error(e.localizedMessage ?: "Error importando rutina")
			}
		}
	}
	
	fun resetImportState() {
		_importState.value = ImportUiState.Idle
	}
	
	fun resetShareState() {
		_uiState.value = ShareUiState.Idle
		_qrBitmap.value = null
	}
}

sealed class ShareUiState {
	object Idle : ShareUiState()
	object Loading : ShareUiState()
	data class Success(val routineData: RoutineShareData, val qrBitmap: Bitmap) : ShareUiState()
	data class Error(val message: String) : ShareUiState()
}

sealed class ImportUiState {
	object Idle : ImportUiState()
	object Loading : ImportUiState()
	data class Success(val routineName: String, val routineId: String) : ImportUiState()
	data class Error(val message: String) : ImportUiState()
}