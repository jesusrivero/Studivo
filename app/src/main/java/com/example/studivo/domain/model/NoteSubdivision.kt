package com.example.studivo.domain.model

enum class NoteSubdivision(
	val displayName: String,
	val clicksPerBeat: Int,
	val musicSymbol: String
) {
	QUARTER("Negra", 1, "♩"),
	EIGHTH("Corchea", 2, "♪♪"),
	TRIPLET("Tresillo", 3, "♪³"),
	SIXTEENTH("Semicorchea", 4, "♬");
	
	companion object {
		fun fromName(name: String): NoteSubdivision {
			return try {
				valueOf(name)
			} catch (e: Exception) {
				QUARTER
			}
		}
	}
}