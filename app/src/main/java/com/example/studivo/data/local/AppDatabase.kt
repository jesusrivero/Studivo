package com.example.studivo.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

import com.example.studivo.domain.model.entity.PhaseEntity
import com.example.studivo.domain.model.entity.RoutineEntity
import com.example.studivo.domain.model.entity.RoutineProgressEntity


@Database(
	entities = [
		RoutineEntity::class,
		PhaseEntity::class,
		RoutineProgressEntity::class
	],
	version = 8, // ✅ Incrementar a 8 para la nueva migración
	exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
	abstract fun routineDao(): RoutineDao
	abstract fun phaseDao(): PhaseDao
	abstract fun progressDao(): RoutineProgressDao
	
	companion object {
		// Migración anterior (mantener)
		val MIGRATION_6_7 = object : Migration(6, 7) {
			override fun migrate(database: SupportSQLiteDatabase) {
				database.execSQL("""
                    CREATE TABLE IF NOT EXISTS routine_progress (
                        id TEXT PRIMARY KEY NOT NULL,
                        routineId TEXT NOT NULL,
                        date TEXT NOT NULL,
                        progressPercentage INTEGER NOT NULL,
                        currentPhaseIndex INTEGER NOT NULL,
                        currentRepetition INTEGER NOT NULL,
                        totalElapsedTime INTEGER NOT NULL,
                        isCompleted INTEGER NOT NULL,
                        lastUpdated INTEGER NOT NULL,
                        createdAt INTEGER NOT NULL,
                        FOREIGN KEY (routineId) REFERENCES routines(id) ON DELETE CASCADE
                    )
                """)
				database.execSQL("CREATE INDEX IF NOT EXISTS index_routine_progress_routineId ON routine_progress(routineId)")
				database.execSQL("CREATE INDEX IF NOT EXISTS index_routine_progress_date ON routine_progress(date)")
			}
		}
		
		// ✅ NUEVA MIGRACIÓN: Agregar routineName y hacer routineId nullable
		val MIGRATION_7_8 = object : Migration(7, 8) {
			override fun migrate(database: SupportSQLiteDatabase) {
				// Paso 1: Crear tabla temporal con la nueva estructura
				database.execSQL("""
                    CREATE TABLE routine_progress_new (
                        id TEXT PRIMARY KEY NOT NULL,
                        routineId TEXT,
                        routineName TEXT NOT NULL DEFAULT '',
                        date TEXT NOT NULL,
                        progressPercentage INTEGER NOT NULL DEFAULT 0,
                        currentPhaseIndex INTEGER NOT NULL DEFAULT 0,
                        currentRepetition INTEGER NOT NULL DEFAULT 1,
                        totalElapsedTime INTEGER NOT NULL DEFAULT 0,
                        isCompleted INTEGER NOT NULL DEFAULT 0,
                        lastUpdated INTEGER NOT NULL,
                        createdAt INTEGER NOT NULL,
                        FOREIGN KEY(routineId) REFERENCES routines(id) ON DELETE SET NULL
                    )
                """)
				
				// Paso 2: Copiar datos existentes
				database.execSQL("""
                    INSERT INTO routine_progress_new
                    (id, routineId, routineName, date, progressPercentage, currentPhaseIndex,
                     currentRepetition, totalElapsedTime, isCompleted, lastUpdated, createdAt)
                    SELECT id, routineId, '', date, progressPercentage, currentPhaseIndex,
                           currentRepetition, totalElapsedTime, isCompleted, lastUpdated, createdAt
                    FROM routine_progress
                """)
				
				// Paso 3: Eliminar tabla antigua
				database.execSQL("DROP TABLE routine_progress")
				
				// Paso 4: Renombrar tabla nueva
				database.execSQL("ALTER TABLE routine_progress_new RENAME TO routine_progress")
				
				// Paso 5: Recrear índices
				database.execSQL("""
                    CREATE INDEX index_routine_progress_routineId_date
                    ON routine_progress(routineId, date)
                """)
				database.execSQL("""
                    CREATE INDEX index_routine_progress_routineId
                    ON routine_progress(routineId)
                """)
			}
		}
	}
}