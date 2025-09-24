package com.example.studivo.presentation.utils

import com.example.studivo.domain.model.enums.FavoriteSuggestion

fun getSuggestedFavorites(): List<FavoriteSuggestion> {
	return listOf(
		FavoriteSuggestion("Alquiler", 150.0, "🏠"),
		FavoriteSuggestion("Netflix", 15.0, "📺"),
		FavoriteSuggestion("Gym", 300.0, "💰"),
		FavoriteSuggestion("Mercado", 50.0, "🛒"),
		FavoriteSuggestion("Gasolina", 20.0, "⛽"),
		FavoriteSuggestion("Internet", 25.0, "🌐"),
	)
}
