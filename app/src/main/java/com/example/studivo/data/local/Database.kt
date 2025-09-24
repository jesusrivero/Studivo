package com.example.studivo.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.studivo.domain.model.entity.FavoriteAmountEntity

@Database(
	entities = [FavoriteAmountEntity::class],
	version = 1,
	exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
	abstract fun favoriteAmountDao(): FavoriteAmountDao
}