package com.ar.arddoia.data.model

import androidx.compose.ui.graphics.Color
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

enum class HabitGoal(val targetDaysPerWeek: Int, val label: String) {
    EVERY_DAY(7, "Every day"),
    FIVE_DAYS_A_WEEK(5, "5 days a week"),
    THREE_DAYS_A_WEEK(3, "3 days a week")
}

enum class HabitIcon {
    WATER_DROP,
    BOOK,
    MOON,
    RUN,
    LEAF,
    DUMBBELL,
    EDIT,
    MUSIC,
    HEART,
    SUN,
    BOLT,
    STAR
}

enum class HabitColor(val hex: Long) {
    GREEN(0xFF2FB364),
    BLUE(0xFF4F8EF7),
    AMBER(0xFFF5A623),
    CORAL(0xFFF25F5C),
    PURPLE(0xFF9D6BFF),
    TEAL(0xFF25B8A8),
    PINK(0xFFEE5CA5),
    ORANGE(0xFFF37324);

    val color: Color get() = Color(hex)
    val containerColor: Color get() = Color(hex).copy(alpha = 0.16f)
}

@Entity(tableName = "habits")
data class HabitEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val name: String,
    val icon: HabitIcon,
    val color: HabitColor,
    val goal: HabitGoal,
    val sortOrder: Int = 0,
    val createdAtEpochDay: Long
)

@Entity(
    tableName = "habit_completions",
    primaryKeys = ["habitId", "epochDay"],
    foreignKeys = [
        ForeignKey(
            entity = HabitEntity::class,
            parentColumns = ["id"],
            childColumns = ["habitId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["epochDay"])]
)
data class HabitCompletionEntity(
    val habitId: Long,
    val epochDay: Long
)
