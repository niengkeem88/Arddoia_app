package com.ar.arddoia.data.repository

import com.ar.arddoia.data.local.HabitDao
import com.ar.arddoia.data.model.HabitColor
import com.ar.arddoia.data.model.HabitCompletionEntity
import com.ar.arddoia.data.model.HabitEntity
import com.ar.arddoia.data.model.HabitGoal
import com.ar.arddoia.data.model.HabitIcon
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

data class HabitWithStats(
    val habit: HabitEntity,
    val isDoneToday: Boolean,
    val completedEpochDays: Set<Long>,
    val currentDayStreak: Int,
    val currentWeekCount: Int,
    val currentWeekStreak: Int,
    val thirtyDayConsistencyPercent: Int
) {
    val todaySubtitle: String
        get() = when (habit.goal) {
            HabitGoal.EVERY_DAY -> "$currentDayStreak-day streak"
            else -> "$currentWeekCount of ${habit.goal.targetDaysPerWeek} this week"
        }

    val habitsCardStreakNumber: Int
        get() = when (habit.goal) {
            HabitGoal.EVERY_DAY -> currentDayStreak
            else -> currentWeekStreak
        }

    val habitsCardStreakUnit: String
        get() = when (habit.goal) {
            HabitGoal.EVERY_DAY -> if (habitsCardStreakNumber == 1) "day" else "days"
            else -> if (habitsCardStreakNumber == 1) "week" else "weeks"
        }
}

class HabitRepository(private val dao: HabitDao) {

    fun observeHabitsWithStats(todayProvider: () -> LocalDate = { LocalDate.now() }): Flow<List<HabitWithStats>> {
        return combine(
            dao.observeHabits(),
            dao.observeAllCompletions()
        ) { habits, completions ->
            val today = todayProvider()
            val todayEpoch = today.toEpochDay()
            val currentWeekMonday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
            val completionsByHabit = completions.groupBy({ it.habitId }, { it.epochDay })

            habits.map { habit ->
                val doneDays = completionsByHabit[habit.id]?.toSet() ?: emptySet()
                val isDoneToday = todayEpoch in doneDays
                val dayStreak = calculateDailyStreak(doneDays, today)
                val weekCount = countInWeek(doneDays, currentWeekMonday, today)
                val weekStreak = calculateWeeklyGoalStreak(
                    doneDays = doneDays,
                    targetPerWeek = habit.goal.targetDaysPerWeek,
                    currentWeekMonday = currentWeekMonday
                )
                val consistency = calculateThirtyDayConsistency(
                    doneDays = doneDays,
                    goal = habit.goal,
                    today = today
                )

                HabitWithStats(
                    habit = habit,
                    isDoneToday = isDoneToday,
                    completedEpochDays = doneDays,
                    currentDayStreak = dayStreak,
                    currentWeekCount = weekCount,
                    currentWeekStreak = weekStreak,
                    thirtyDayConsistencyPercent = consistency
                )
            }
        }
    }

    suspend fun toggleCompletion(habitId: Long, date: LocalDate = LocalDate.now()) {
        val epochDay = date.toEpochDay()
        if (dao.isCompleted(habitId, epochDay)) {
            dao.deleteCompletion(habitId, epochDay)
        } else {
            dao.insertCompletion(HabitCompletionEntity(habitId, epochDay))
        }
    }

    suspend fun addHabit(
        name: String,
        icon: HabitIcon,
        color: HabitColor,
        goal: HabitGoal
    ) {
        val trimmed = name.trim()
        if (trimmed.isEmpty()) return
        val nextOrder = dao.getHabitCount()
        dao.insertHabit(
            HabitEntity(
                name = trimmed,
                icon = icon,
                color = color,
                goal = goal,
                sortOrder = nextOrder,
                createdAtEpochDay = LocalDate.now().toEpochDay()
            )
        )
    }

    suspend fun deleteHabit(habitId: Long) {
        dao.deleteHabit(habitId)
    }

    private fun calculateDailyStreak(doneDays: Set<Long>, today: LocalDate): Int {
        var cursor = if (today.toEpochDay() in doneDays) today else today.minusDays(1)
        var streak = 0
        while (cursor.toEpochDay() in doneDays) {
            streak++
            cursor = cursor.minusDays(1)
        }
        return streak
    }

    private fun countInWeek(
        doneDays: Set<Long>,
        weekMonday: LocalDate,
        upToDate: LocalDate
    ): Int {
        var count = 0
        for (i in 0..6) {
            val d = weekMonday.plusDays(i.toLong())
            if (d.isAfter(upToDate)) break
            if (d.toEpochDay() in doneDays) count++
        }
        return count
    }

    private fun calculateWeeklyGoalStreak(
        doneDays: Set<Long>,
        targetPerWeek: Int,
        currentWeekMonday: LocalDate
    ): Int {
        var streak = 0
        val currentWeekCount = (0..6).count { offset ->
            currentWeekMonday.plusDays(offset.toLong()).toEpochDay() in doneDays
        }
        if (currentWeekCount >= targetPerWeek) {
            streak++
        }
        var weekCursor = currentWeekMonday.minusWeeks(1)
        while (true) {
            val count = (0..6).count { offset ->
                weekCursor.plusDays(offset.toLong()).toEpochDay() in doneDays
            }
            if (count >= targetPerWeek) {
                streak++
                weekCursor = weekCursor.minusWeeks(1)
            } else {
                break
            }
        }
        return streak
    }

    private fun calculateThirtyDayConsistency(
        doneDays: Set<Long>,
        goal: HabitGoal,
        today: LocalDate
    ): Int {
        val completedLast30 = (0 until 30).count { offset ->
            today.minusDays(offset.toLong()).toEpochDay() in doneDays
        }
        val expectedIn30 = (goal.targetDaysPerWeek / 7.0) * 30.0
        if (expectedIn30 <= 0.0) return 0
        return ((completedLast30 / expectedIn30) * 100.0)
            .toInt()
            .coerceIn(0, 100)
    }
}
