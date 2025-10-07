package com.example.studivo.infraestructure.di

import android.content.Context
import androidx.room.Room
import com.example.studivo.data.local.AppDatabase
import com.example.studivo.data.local.PhaseDao
import com.example.studivo.data.local.RoutineDao
import com.example.studivo.data.preferences.ThemeDataStore
import com.example.studivo.data.repository.RoutineRepositoryImpl
import com.example.studivo.domain.repository.RoutineRepository
import com.example.studivo.domain.usecase.DeletePhaseUseCase
import com.example.studivo.domain.usecase.DeleteRoutineUseCase
import com.example.studivo.domain.usecase.DeleteRoutineWithPhasesUseCase
import com.example.studivo.domain.usecase.GetAllRoutinesUseCase
import com.example.studivo.domain.usecase.GetPhaseByIdUseCase
import com.example.studivo.domain.usecase.GetPhasesByRoutineUseCase
import com.example.studivo.domain.usecase.GetRoutineByIdUseCase
import com.example.studivo.domain.usecase.InsertPhaseUseCase
import com.example.studivo.domain.usecase.InsertRoutineUseCase
import com.example.studivo.domain.usecase.RoutineUseCases
import com.example.studivo.domain.usecase.StartRoutinePlaybackUseCase
import com.example.studivo.domain.usecase.UpdatePhaseUseCase
import com.example.studivo.domain.usecase.UpdatePhasesOrderUseCase
import com.example.studivo.domain.usecase.UpdateRoutineUseCase

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton



@Module
@InstallIn(SingletonComponent::class)
object AppModule {
	
	
	@Provides
	@Singleton
	fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
		Room.databaseBuilder(context, AppDatabase::class.java, "app_db").build()
	
	@Provides
	fun provideRoutineDao(db: AppDatabase): RoutineDao = db.routineDao()
	
	@Provides
	fun providePhaseDao(db: AppDatabase): PhaseDao = db.phaseDao()
	

	
	@Provides
	@Singleton
	fun provideRoutineRepository(
		routineDao: RoutineDao,
		phaseDao: PhaseDao
	): RoutineRepository {
		return RoutineRepositoryImpl(routineDao, phaseDao)
	}
	
	@Provides
	@Singleton
	fun provideRoutineUseCases(repository: RoutineRepository): RoutineUseCases {
		return RoutineUseCases(
			insertRoutine = InsertRoutineUseCase(repository),
			insertPhase = InsertPhaseUseCase(repository),
			getPhasesByRoutine = GetPhasesByRoutineUseCase(repository),
			deleteRoutine = DeleteRoutineUseCase(repository),
			deletePhase = DeletePhaseUseCase(repository),
			updatePhasesOrder = UpdatePhasesOrderUseCase(repository),
			getRoutineById = GetRoutineByIdUseCase(repository),
			updateRoutine= UpdateRoutineUseCase(repository),
			updatePhase= UpdatePhaseUseCase(repository),
			getPhaseById= GetPhaseByIdUseCase(repository),
			deleteRoutineWithPhases = DeleteRoutineWithPhasesUseCase(repository),
			getAllRoutines= GetAllRoutinesUseCase(repository))
	}
	
	@Provides @Singleton
	fun provideThemeDataStore(@ApplicationContext context: Context): ThemeDataStore =
		ThemeDataStore(context)
	
	
	
	
	
	
	@Provides
	fun provideStartRoutinePlaybackUseCase(
		repository: RoutineRepository
	): StartRoutinePlaybackUseCase {
		return StartRoutinePlaybackUseCase(repository)
	}
}
