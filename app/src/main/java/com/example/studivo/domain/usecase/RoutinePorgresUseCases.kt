package com.example.studivo.domain.usecase

import com.example.studivo.domain.model.DayProgress
import com.example.studivo.domain.model.RoutineProgress
import com.example.studivo.domain.repository.RoutineProgressRepository
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

class SaveRoutineProgressUseCase @Inject constructor(
	private val repository: RoutineProgressRepository
) {
	suspend operator fun invoke(progress: RoutineProgress) {
		repository.saveProgress(progress)
	}
}



class GetRoutineProgressUseCase @Inject constructor(
	private val repository: RoutineProgressRepository
) {
	suspend operator fun invoke(routineId: String, date: String): RoutineProgress? {
		return repository.getProgressByRoutineAndDate(routineId, date)
	}
}




class GetRoutineProgressFlowUseCase @Inject constructor(
	private val repository: RoutineProgressRepository
) {
	operator fun invoke(routineId: String, date: String): Flow<RoutineProgress?> {
		return repository.getProgressByRoutineAndDateFlow(routineId, date)
	}
}




class GetProgressHistoryUseCase @Inject constructor(
	private val repository: RoutineProgressRepository
) {
	suspend operator fun invoke(routineId: String): List<RoutineProgress> {
		return repository.getProgressHistoryByRoutine(routineId)
	}
}



class DeleteRoutineProgressUseCase @Inject constructor(
	private val repository: RoutineProgressRepository
) {
	suspend operator fun invoke(routineId: String, date: String) {
		repository.deleteProgress(routineId, date)
	}
}




class GetCompletedRoutinesTodayUseCase @Inject constructor(
	private val repository: RoutineProgressRepository
) {
	suspend operator fun invoke(): Int {
		return repository.getCompletedRoutinesToday()
	}
}


// IsRoutineCompletedTodayUseCase.kt

class IsRoutineCompletedTodayUseCase @Inject constructor(
	private val repository: RoutineProgressRepository
) {
	suspend operator fun invoke(routineId: String, date: String): Boolean {
		return repository.isRoutineCompletedToday(routineId, date)
	}
}


class GetCurrentStreakUseCase @Inject constructor(
	private val repository: RoutineProgressRepository
) {
	suspend operator fun invoke(): Int {
		val allProgress = repository.getAllProgressOrderedByDate()
		if (allProgress.isEmpty()) return 0
		
		val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
		val today = Calendar.getInstance()
		val todayString = sdf.format(today.time)
		
		val uniqueDatesWithProgress = allProgress
			.filter { it.progressPercentage > 0 || it.isCompleted }
			.map { it.date }
			.distinct()
			.sortedDescending()
		
		if (uniqueDatesWithProgress.isEmpty()) return 0
		
		val lastProgressDate = uniqueDatesWithProgress.first()
		val yesterday = Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, -1) }
		val yesterdayString = sdf.format(yesterday.time)
		
	
		if (lastProgressDate != todayString && lastProgressDate != yesterdayString) {
			return 0
		}
		
		var streak = 0
		val calendar = Calendar.getInstance()
		if (lastProgressDate == todayString) {
			calendar.time = today.time
		} else {
			calendar.time = yesterday.time
		}
		
		while (true) {
			val currentDateString = sdf.format(calendar.time)
			if (uniqueDatesWithProgress.contains(currentDateString)) {
				streak++
				calendar.add(Calendar.DAY_OF_MONTH, -1)
			} else {
				break
			}
		}
		
		return streak
	}
}




class GetProgressCalendarUseCase @Inject constructor(
	private val repository: RoutineProgressRepository
) {
	suspend operator fun invoke(firstUseDate: String?): List<DayProgress> {
		val allProgress = repository.getAllProgressOrderedByDate()
		val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
		
		val today = Calendar.getInstance()
		
		
		val startDate = if (firstUseDate != null) {
			Calendar.getInstance().apply {
				time = sdf.parse(firstUseDate)!!
			}
		} else {
			Calendar.getInstance()
		}
		
		val dayProgressList = mutableListOf<DayProgress>()
		val calendar = Calendar.getInstance()
		calendar.time = startDate.time
		

		val progressByDate = allProgress.groupBy { it.date }
		

		while (calendar.timeInMillis <= today.timeInMillis) {
			val dateString = sdf.format(calendar.time)
			val dayProgress = progressByDate[dateString]
			
			val hasProgress = dayProgress != null && dayProgress.any { it.progressPercentage > 0 }
			val hasCompletedRoutine = dayProgress?.any { it.isCompleted } ?: false
			val totalProgressPercentage = if (hasProgress) {
				dayProgress!!.maxOf { it.progressPercentage }
			} else 0
			
	
			val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
			val dayInitial = when (dayOfWeek) {
				Calendar.SUNDAY -> "D"
				Calendar.MONDAY -> "L"
				Calendar.TUESDAY -> "M"
				Calendar.WEDNESDAY -> "M"
				Calendar.THURSDAY -> "J"
				Calendar.FRIDAY -> "V"
				Calendar.SATURDAY -> "S"
				else -> ""
			}
			
			dayProgressList.add(
				DayProgress(
					date = dateString,
					dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH),
					dayOfWeekInitial = dayInitial,
					hasProgress = hasProgress,
					hasCompletedRoutine = hasCompletedRoutine,
					totalProgressPercentage = totalProgressPercentage,
					routinesCount = dayProgress?.size ?: 0
				)
			)
			
			calendar.add(Calendar.DAY_OF_MONTH, 1)
		}
		
		return dayProgressList
	}
}


data class RoutineProgressUseCases(
	val saveProgress: SaveRoutineProgressUseCase,
	val getProgress: GetRoutineProgressUseCase,
	val getProgressFlow: GetRoutineProgressFlowUseCase,
	val getProgressHistory: GetProgressHistoryUseCase,
	val deleteProgress: DeleteRoutineProgressUseCase,
	val getCompletedRoutinesToday: GetCompletedRoutinesTodayUseCase,
	val isRoutineCompletedToday: IsRoutineCompletedTodayUseCase,
	val getCurrentStreak: GetCurrentStreakUseCase,
	val getProgressCalendar: GetProgressCalendarUseCase
)
