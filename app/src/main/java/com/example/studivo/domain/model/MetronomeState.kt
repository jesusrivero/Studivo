package com.example.studivo.domain.model

data class MetronomeState(
	val bpm: Int = 120,
	val timeSignature: String = "4/4",
	val isPlaying: Boolean = false,
	val subdivision: NoteSubdivision = NoteSubdivision.QUARTER, // ✨ NUEVO
	val currentBeat: Int = 1
)