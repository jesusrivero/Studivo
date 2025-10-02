package com.example.studivo.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.studivo.domain.model.entity.PhaseEntity
import com.example.studivo.domain.model.entity.RoutineEntity
//import com.example.studivo.domain.model.helpers.Converters

// --- AppDatabase.kt ---
@Database(entities = [RoutineEntity::class, PhaseEntity::class], version = 2)
//@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
	abstract fun routineDao(): RoutineDao
	abstract fun phaseDao(): PhaseDao
}

