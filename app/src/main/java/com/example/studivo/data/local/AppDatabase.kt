package com.example.studivo.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.studivo.domain.model.entity.PhaseEntity
import com.example.studivo.domain.model.entity.RoutineEntity

// --- AppDatabase.kt ---
@Database(entities = [RoutineEntity::class, PhaseEntity::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
	abstract fun routineDao(): RoutineDao
	abstract fun phaseDao(): PhaseDao
}

