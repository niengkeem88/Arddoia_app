package com.ar.arddoia.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ar.arddoia.data.model.HabitColor
import com.ar.arddoia.data.model.HabitGoal
import com.ar.arddoia.data.model.HabitIcon
import com.ar.arddoia.data.repository.HabitRepository
import com.ar.arddoia.data.repository.HabitWithStats
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.time.temporal.TemporalAdjusters
import java.util.Locale

data class WeekDotUi(
    val shortLabel: String,
    val isCompleted: Boolean,
    val isToday: Boolean,
    val isFuture: Boolean
)

data class HeatmapDayCell(
    val date: LocalDate?,
    val isToday: Boolean,
    val intensity: Float // 0f..1f (for Habit card: 0f or 1f; for Stats card: 0f..1f share)
)

data class WeekdayStatUi(
    val shortLabel: String,
    val fullPluralName: String,
    val ratio: Float
)

data class StatsUiState(
    val onTrackPercent: Int = 0,
    val checkInsLast30Days: Int = 0,
    val bestStreak: Int = 0,
    val allHabitsGrid: List<List<HeatmapDayCell>> = emptyList(), // 20 columns x 7 rows
    val weekdayStats: List<WeekdayStatUi> = emptyList(),
    val strongestWeekdayLabel: String = "Strongest on Sundays",
    val mostConsistent: List<HabitWithStats> = emptyList()
)

data class ArddoiaUiState(
    val todayDateLabel: String = "",
    val todayEpochDay: Long = 0L,
    val habits: List<HabitWithStats> = emptyList(),
    val todoHabits: List<HabitWithStats> = emptyList(),
    val doneHabits: List<HabitWithStats> = emptyList(),
    val completedTodayCount: Int = 0,
    val totalHabitsCount: Int = 0,
    val topStreakHeadline: String = "",
    val weekDots: List<WeekDotUi> = emptyList(),
    val twentyWeekDates: List<List<LocalDate?>> = emptyList(),
    val stats: StatsUiState = StatsUiState(),
    val isAddSheetOpen: Boolean = false
)

class ArddoiaViewModel(
    private val repository: HabitRepository
) : ViewModel() {

    private val isAddSheetOpen = MutableStateFlow(false)

    val uiState: StateFlow<ArddoiaUiState> = combine(
        repository.observeHabitsWithStats(),
        isAddSheetOpen
    ) { habits, sheetOpen ->
        buildUiState(habits, sheetOpen)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ArddoiaUiState()
    )

    fun toggleHabitToday(habitId: Long) {
        viewModelScope.launch {
            repository.toggleCompletion(habitId, LocalDate.now())
        }
    }

    fun openAddHabitSheet() {
        isAddSheetOpen.value = true
    }

    fun closeAddHabitSheet() {
        isAddSheetOpen.value = false
    }

    fun addHabit(
        name: String,
        icon: HabitIcon,
        color: HabitColor,
        goal: HabitGoal
    ) {
        viewModelScope.launch {
            repository.addHabit(name, icon, color, goal)
            isAddSheetOpen.value = false
        }
    }

    fun deleteHabit(habitId: Long) {
        viewModelScope.launch {
            repository.deleteHabit(habitId)
        }
    }

    private fun buildUiState(
        habits: List<HabitWithStats>,
        sheetOpen: Boolean
    ): ArddoiaUiState {
        val today = LocalDate.now()
        val todayEpoch = today.toEpochDay()
        val dateFormatter = DateTimeFormatter.ofPattern("EEEE, d MMMM", Locale.ENGLISH)

        val todo = habits.filter { !it.isDoneToday }
        val done = habits.filter { it.isDoneToday }

        val topHabit = habits.maxByOrNull { it.currentDayStreak }
        val topStreakText = if (topHabit != null && topHabit.currentDayStreak > 0) {
            "${topHabit.habit.name} · ${topHabit.currentDayStreak}-day streak"
        } else if (habits.isNotEmpty()) {
            "Tap a habit below to check in"
        } else {
            "Add your first habit to begin"
        }

        val currentMonday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        val dayLabels = listOf("M", "T", "W", "T", "F", "S", "S")
        val weekDots = (0..6).map { offset ->
            val date = currentMonday.plusDays(offset.toLong())
            val epoch = date.toEpochDay()
            val completedAny = habits.isNotEmpty() && habits.any { epoch in it.completedEpochDays }
            WeekDotUi(
                shortLabel = dayLabels[offset],
                isCompleted = !date.isAfter(today) && completedAny,
                isToday = date == today,
                isFuture = date.isAfter(today)
            )
        }

        // 20-week calendar matrix: 20 columns (oldest -> current), 7 rows (Mon -> Sun)
        val firstMonday = currentMonday.minusWeeks(19)
        val twentyWeekDates = (0 until 20).map { weekIdx ->
            val weekStart = firstMonday.plusWeeks(weekIdx.toLong())
            (0..6).map { dayIdx ->
                val date = weekStart.plusDays(dayIdx.toLong())
                if (date.isAfter(today)) null else date
            }
        }

        val stats = buildStats(habits, today, twentyWeekDates)

        return ArddoiaUiState(
            todayDateLabel = today.format(dateFormatter),
            todayEpochDay = todayEpoch,
            habits = habits,
            todoHabits = todo,
            doneHabits = done,
            completedTodayCount = done.size,
            totalHabitsCount = habits.size,
            topStreakHeadline = topStreakText,
            weekDots = weekDots,
            twentyWeekDates = twentyWeekDates,
            stats = stats,
            isAddSheetOpen = sheetOpen
        )
    }

    private fun buildStats(
        habits: List<HabitWithStats>,
        today: LocalDate,
        twentyWeekDates: List<List<LocalDate?>>
    ): StatsUiState {
        if (habits.isEmpty()) return StatsUiState()

        val onTrack = habits.map { it.thirtyDayConsistencyPercent }.average().toInt()
        val last30Epochs = (0 until 30).map { today.minusDays(it.toLong()).toEpochDay() }.toSet()
        val checkIns30 = habits.sumOf { habit ->
            habit.completedEpochDays.count { it in last30Epochs }
        }
        val bestStreak = habits.maxOf { it.currentDayStreak }

        val totalHabits = habits.size.toFloat()
        val allHabitsGrid = twentyWeekDates.map { weekColumn ->
            weekColumn.map { date ->
                if (date == null) {
                    HeatmapDayCell(date = null, isToday = false, intensity = 0f)
                } else {
                    val epoch = date.toEpochDay()
                    val count = habits.count { epoch in it.completedEpochDays }
                    HeatmapDayCell(
                        date = date,
                        isToday = date == today,
                        intensity = (count / totalHabits).coerceIn(0f, 1f)
                    )
                }
            }
        }

        val dayLabels = listOf("M", "T", "W", "T", "F", "S", "S")
        val weekdayTotals = IntArray(7)
        for (offset in 0 until 30) {
            val d = today.minusDays(offset.toLong())
            val idx = d.dayOfWeek.value - 1
            val epoch = d.toEpochDay()
            weekdayTotals[idx] += habits.count { epoch in it.completedEpochDays }
        }
        val maxWeekdayCount = weekdayTotals.maxOrNull()?.coerceAtLeast(1) ?: 1
        val weekdayStats = (0..6).map { idx ->
            val dow = DayOfWeek.of(idx + 1)
            val fullName = dow.getDisplayName(TextStyle.FULL, Locale.ENGLISH) + "s"
            WeekdayStatUi(
                shortLabel = dayLabels[idx],
                fullPluralName = fullName,
                ratio = (weekdayTotals[idx].toFloat() / maxWeekdayCount.toFloat()).coerceIn(0.18f, 1f)
            )
        }
        val strongest = weekdayStats.maxByOrNull { it.ratio }?.fullPluralName ?: "Sundays"

        val mostConsistent = habits.sortedByDescending { it.thirtyDayConsistencyPercent }

        return StatsUiState(
            onTrackPercent = onTrack,
            checkInsLast30Days = checkIns30,
            bestStreak = bestStreak,
            allHabitsGrid = allHabitsGrid,
            weekdayStats = weekdayStats,
            strongestWeekdayLabel = "Strongest on $strongest",
            mostConsistent = mostConsistent
        )
    }

    companion object {
        fun factory(repository: HabitRepository): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return ArddoiaViewModel(repository) as T
                }
            }
        }
    }
}
