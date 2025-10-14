package com.example.studivo.data.repository

import android.graphics.Bitmap
import com.example.studivo.data.local.PhaseDao
import com.example.studivo.data.local.RoutineDao
import com.example.studivo.domain.model.PhaseExportData
import com.example.studivo.domain.model.RoutineExportData
import com.example.studivo.domain.model.RoutineShareData
import com.example.studivo.domain.model.entity.PhaseEntity
import com.example.studivo.domain.model.entity.RoutineEntity
import com.example.studivo.domain.repository.RoutineShareRepository
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import java.util.UUID
import javax.inject.Inject

class RoutineShareRepositoryImpl @Inject constructor(
	private val routineDao: RoutineDao,
	private val phaseDao: PhaseDao,
) : RoutineShareRepository {
	
	override suspend fun exportRoutine(routineId: String): RoutineShareData? {
		val routine = routineDao.getById(routineId) ?: return null
		val phases = phaseDao.getPhasesByRoutine(routineId)
		
		val routineExport = RoutineExportData(
			name = routine.name,
			description = routine.description,
			color = routine.color
		)
		
		val phasesExport = phases.map { phase ->
			PhaseExportData(
				name = phase.name,
				duration = phase.duration,
				bpm = phase.bpm,
				timeSignature = phase.timeSignature,
				subdivision = phase.subdivision,
				color = phase.color,
				mode = phase.mode,
				repetitions = phase.repetitions,
				bpmIncrement = phase.bpmIncrement,
				bpmMax = phase.bpmMax,
				order = phase.order
			)
		}
		
		return RoutineShareData(routineExport, phasesExport)
	}
	
	override suspend fun importRoutine(routineData: RoutineShareData): String {
		val newRoutineId = UUID.randomUUID().toString()
		
		
		val routineEntity = RoutineEntity(
			id = newRoutineId,
			name = routineData.routine.name,
			description = routineData.routine.description,
			color = routineData.routine.color,
			createdAt = System.currentTimeMillis()
		)
		
		routineDao.insert(routineEntity)
		
		
		routineData.phases.forEach { phaseData ->
			val phaseEntity = PhaseEntity(
				id = UUID.randomUUID().toString(),
				routineId = newRoutineId,
				name = phaseData.name,
				duration = phaseData.duration,
				bpm = phaseData.bpm,
				timeSignature = phaseData.timeSignature,
				subdivision = phaseData.subdivision,
				color = phaseData.color,
				mode = phaseData.mode,
				repetitions = phaseData.repetitions,
				bpmIncrement = phaseData.bpmIncrement,
				bpmMax = phaseData.bpmMax,
				order = phaseData.order
			)
			phaseDao.insert(phaseEntity)
		}
		
		return newRoutineId
	}
	
	override fun generateQRCode(data: String, width: Int, height: Int): Bitmap? {
		return try {
			val hints = hashMapOf<EncodeHintType, Any>()
			hints[EncodeHintType.CHARACTER_SET] = "UTF-8"
			hints[EncodeHintType.MARGIN] = 1
			
			hints[EncodeHintType.ERROR_CORRECTION] =
				com.google.zxing.qrcode.decoder.ErrorCorrectionLevel.L
			
			val qrCodeWriter = QRCodeWriter()
			val bitMatrix = qrCodeWriter.encode(data, BarcodeFormat.QR_CODE, width, height, hints)
			
			val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
			for (x in 0 until width) {
				for (y in 0 until height) {
					bitmap.setPixel(
						x, y,
						if (bitMatrix[x, y]) android.graphics.Color.BLACK
						else android.graphics.Color.WHITE
					)
				}
			}
			
			bitmap
		} catch (e: Exception) {
			
			e.printStackTrace()
			null
		}
	}
}
