package com.example.studivo.domain.repository

import android.graphics.Bitmap
import com.example.studivo.domain.model.RoutineShareData

interface RoutineShareRepository {
	suspend fun exportRoutine(routineId: String): RoutineShareData?
	suspend fun importRoutine(routineData: RoutineShareData): String
	fun generateQRCode(data: String, width: Int = 512, height: Int = 512): Bitmap?
}