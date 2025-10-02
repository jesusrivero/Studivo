package com.example.studivo.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.studivo.domain.model.entity.RoutineEntity
import retrofit2.http.GET

@Dao
interface RoutineDao {
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insert(routine: RoutineEntity)
	
	@Query("SELECT * FROM routines")
	suspend fun getAll(): List<RoutineEntity>
	
	@Query("DELETE FROM routines WHERE id = :routineId")
	suspend fun deleteById(routineId: String)
	
	
	
	@Query("SELECT * FROM routines WHERE id = :routineId")
	suspend fun getById(routineId: String): RoutineEntity?
	
	@Update
	suspend fun update(routine: RoutineEntity)
}

