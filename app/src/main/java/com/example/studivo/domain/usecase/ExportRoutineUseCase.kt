package com.example.studivo.domain.usecase

import android.graphics.Bitmap
import com.example.studivo.domain.model.RoutineShareData
import com.example.studivo.domain.repository.RoutineShareRepository
import javax.inject.Inject

class ExportRoutineUseCase @Inject constructor(
private val repository: RoutineShareRepository
) {
	suspend operator fun invoke(routineId: String): RoutineShareData? {
		return repository.exportRoutine(routineId)
	}
}

class ImportRoutineUseCase @Inject constructor(
	private val repository: RoutineShareRepository
) {
	suspend operator fun invoke(routineData: RoutineShareData): String {
		return repository.importRoutine(routineData)
	}
}

class GenerateQRCodeUseCase @Inject constructor(
	private val repository: RoutineShareRepository
) {
	operator fun invoke(data: String, width: Int = 512, height: Int = 512): Bitmap? {
		return repository.generateQRCode(data, width, height)
	}
}

data class RoutineShareUseCases(
	val exportRoutine: ExportRoutineUseCase,
	val importRoutine: ImportRoutineUseCase,
	val generateQRCode: GenerateQRCodeUseCase
)