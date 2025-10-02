package com.example.studivo.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.studivo.domain.model.entity.PhaseEntity

@Dao
interface PhaseDao {
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insert(phase: PhaseEntity)
	
	@Query("SELECT * FROM phases WHERE routineId = :routineId ORDER BY `order` ASC")
	suspend fun getPhasesByRoutine(routineId: String): List<PhaseEntity>
	
	@Query("DELETE FROM phases WHERE id = :phaseId")
	suspend fun deleteById(phaseId: String)
	
	//Esto es para editar el orden de las fases. Debo cambiar el nombre despues
	@Update
	suspend fun updatePhases(phases: List<PhaseEntity>)
	
	
	//Esto si edita los datos de la fase
	@Update
	suspend fun updatePhase(phase: PhaseEntity)
	
	@Query("SELECT * FROM phases WHERE id = :phaseId")
	suspend fun getById(phaseId: String): PhaseEntity?
}
