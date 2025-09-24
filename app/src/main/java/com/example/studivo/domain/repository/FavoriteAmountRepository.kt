package com.example.studivo.domain.repository

import com.example.studivo.domain.model.entity.FavoriteAmountEntity
import kotlinx.coroutines.flow.Flow

interface FavoriteAmountRepository {
	fun getAllFavorites(): Flow<List<FavoriteAmountEntity>>
	suspend fun insertFavorite(favorite: FavoriteAmountEntity)
	suspend fun deleteFavorite(favorite: FavoriteAmountEntity)
	suspend fun updateFavorite(favorite: FavoriteAmountEntity)
}