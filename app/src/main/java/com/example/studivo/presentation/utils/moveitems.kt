package com.example.studivo.presentation.utils


// Función de utilidad para mover elementos en una lista
internal fun <T> MutableList<T>.move(from: Int, to: Int) {
	if (from == to || from !in indices || to !in indices) return
	val item = removeAt(from)
	add(to, item)
}