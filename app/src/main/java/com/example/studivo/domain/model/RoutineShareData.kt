package com.example.studivo.domain.model


import com.example.studivo.presentation.utils.QRDataCompressor
import com.google.gson.Gson

data class RoutineShareData(
	val routine: RoutineExportData,
	val phases: List<PhaseExportData>
) {
	// ✅ Método actualizado con compresión
	fun toCompressedQRData(): String {
		val json = Gson().toJson(this)
		return QRDataCompressor.compressAndEncrypt(json)
	}
	
	// ✅ Mantener el método original por compatibilidad
	fun toJson(): String = Gson().toJson(this)
	
	companion object {
		// ✅ Método actualizado para descomprimir
		fun fromCompressedQRData(compressedData: String): RoutineShareData? {
			return try {
				val json = QRDataCompressor.decryptAndDecompress(compressedData)
				Gson().fromJson(json, RoutineShareData::class.java)
			} catch (e: Exception) {
				null
			}
		}
		
		// ✅ Mantener el método original por compatibilidad
		fun fromJson(json: String): RoutineShareData? {
			return try {
				Gson().fromJson(json, RoutineShareData::class.java)
			} catch (e: Exception) {
				null
			}
		}
	}
}

data class RoutineExportData(
	val name: String,
	val description: String,
	val color: String
)

data class PhaseExportData(
	val name: String,
	val duration: Int,
	val bpm: Int,
	val timeSignature: String,
	val subdivision: String,
	val color: String,
	val mode: String,
	val repetitions: Int,
	val bpmIncrement: Int,
	val bpmMax: Int,
	val order: Int
)