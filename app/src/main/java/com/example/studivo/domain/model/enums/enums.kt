package com.example.studivo.domain.model.enums

import com.example.studivo.domain.model.entity.FavoriteAmountEntity
import com.example.studivo.presentation.navegacion.AppRoutes


enum class CalculationMode {
	USD_TO_BS,  // Dólares a Bolívares
	BS_TO_USD   // Bolívares a Dólares
}




data class FavoriteAmount(
	val name: String,
	val amountUsd: Double
)


data class FavoriteSuggestion(
	val name: String,
	val amountUsd: Double,
	val icon: String
)




data class FavoritesUiState(
	val favorites: List<FavoriteAmountEntity> = emptyList(),
	val isLoading: Boolean = false,
	val error: String? = null,
	val successMessage: String? = null,
	val message: String? = null
)