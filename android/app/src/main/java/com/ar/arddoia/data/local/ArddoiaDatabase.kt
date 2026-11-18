package com.ar.arddoia.data.local

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.ar.arddoia.data.model.HabitColor
import com.ar.arddoia.data.model.HabitCompletionEntity
import com.ar.arddoia.data.model.HabitEntity
import com.ar.arddoia.data.model.HabitGoal
import com.ar.arddoia.data.model.HabitIcon
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

class ArddoiaConverters {
    @TypeConverter
    fun fromGoal(goal: HabitGoal): String = goal.name

    @TypeConverter
    fun toGoal(value: String): HabitGoal = HabitGoal.valueOf(value)

    @TypeConverter
    fun fromIcon(icon: HabitIcon): String = icon.name

    @TypeConverter
    fun toIcon(value: String): HabitIcon = HabitIcon.valueOf(value)

    @TypeConverter
    fun fromColor(color: HabitColor): String = color.name

    @TypeConverter
    fun toColor(value: String): HabitColor = HabitColor.valueOf(value)
}

@Dao
interface HabitDao {
    @Query("SELECT * FROM habits ORDER BY sortOrder ASC, id ASC")
    fun observeHabits(): Flow<List<HabitEntity>>

    @Query("SELECT * FROM habit_completions")
    fun observeAllCompletions(): Flow<List<HabitCompletionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabit(habit: HabitEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabits(habits: List<HabitEntity>): List<Long>

    @Query("DELETE FROM habits WHERE id = :habitId")
    suspend fun deleteHabit(habitId: Long)

    @Query("SELECT EXISTS(SELECT 1 FROM habit_completions WHERE habitId = :habitId AND epochDay = :epochDay)")
    suspend fun isCompleted(habitId: Long, epochDay: Long): Boolean

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCompletion(completion: HabitCompletionEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCompletions(completions: List<HabitCompletionEntity>)

    @Query("DELETE FROM habit_completions WHERE habitId = :habitId AND epochDay = :epochDay")
    suspend fun deleteCompletion(habitId: Long, epochDay: Long)

    @Query("SELECT COUNT(*) FROM habits")
    suspend fun getHabitCount(): Int
}

@Database(
    entities = [HabitEntity::class, HabitCompletionEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(ArddoiaConverters::class)
abstract class ArddoiaDatabase : RoomDatabase() {
    abstract fun habitDao(): HabitDao

    companion object {
        @Volatile
        private var INSTANCE: ArddoiaDatabase? = null

        fun getInstance(context: Context): ArddoiaDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }
        }

        private fun buildDatabase(context: Context): ArddoiaDatabase {
            lateinit var db: ArddoiaDatabase
            db = Room.databaseBuilder(
                context.applicationContext,
                ArddoiaDatabase::class.java,
                "arddoia.db"
            )
                .addCallback(object : Callback() {
                    override fun onCreate(supportDb: SupportSQLiteDatabase) {
                        super.onCreate(supportDb)
                        CoroutineScope(Dispatchers.IO).launch {
                            seedInitialHabits(db.habitDao())
                        }
                    }
                })
                .build()
            return db
        }

        private suspend fun seedInitialHabits(dao: HabitDao) {
            if (dao.getHabitCount() > 0) return

            val today = LocalDate.now()
            val todayEpoch = today.toEpochDay()
            val startEpoch = today.minusWeeks(20).toEpochDay()

            val initialHabits = listOf(
                HabitEntity(
                    name = "Two litres of water",
                    icon = HabitIcon.WATER_DROP,
                    color = HabitColor.BLUE,
                    goal = HabitGoal.EVERY_DAY,
                    sortOrder = 0,
                    createdAtEpochDay = startEpoch
                ),
                HabitEntity(
                    name = "Cook at home",
                    icon = HabitIcon.STAR,
                    color = HabitColor.ORANGE,
                    goal = HabitGoal.FIVE_DAYS_A_WEEK,
                    sortOrder = 1,
                    createdAtEpochDay = startEpoch
                ),
                HabitEntity(
                    name = "Vitamins",
                    icon = HabitIcon.HEART,
                    color = HabitColor.CORAL,
                    goal = HabitGoal.EVERY_DAY,
                    sortOrder = 2,
                    createdAtEpochDay = startEpoch
                ),
                HabitEntity(
                    name = "Yoga",
                    icon = HabitIcon.LEAF,
                    color = HabitColor.GREEN,
                    goal = HabitGoal.THREE_DAYS_A_WEEK,
                    sortOrder = 3,
                    createdAtEpochDay = startEpoch
                ),
                HabitEntity(
                    name = "Read before bed",
                    icon = HabitIcon.BOOK,
                    color = HabitColor.AMBER,
                    goal = HabitGoal.EVERY_DAY,
                    sortOrder = 4,
                    createdAtEpochDay = startEpoch
                )
            )

            val ids = dao.insertHabits(initialHabits)
            val waterId = ids[0]
            val cookId = ids[1]
            val vitaminsId = ids[2]
            val yogaId = ids[3]
            val readId = ids[4]

            val completions = mutableListOf<HabitCompletionEntity>()

            // Vitamins: 41-day streak up to and including today
            for (d in 0..40) {
                completions += HabitCompletionEntity(vitaminsId, todayEpoch - d)
            }
            for (d in 42..139) {
                if (d % 5 != 0) completions += HabitCompletionEntity(vitaminsId, todayEpoch - d)
            }

            // Water: 14-day streak ending yesterday (not yet checked today)
            for (d in 1..14) {
                completions += HabitCompletionEntity(waterId, todayEpoch - d)
            }
            for (d in 16..139) {
                if (d % 4 != 0) completions += HabitCompletionEntity(waterId, todayEpoch - d)
            }

            // Read before bed: 6-day streak including today
            for (d in 0..5) {
                completions += HabitCompletionEntity(readId, todayEpoch - d)
            }
            for (d in 7..139) {
                if (d % 3 != 0) completions += HabitCompletionEntity(readId, todayEpoch - d)
            }

            // Yoga: 3 days a week for 8 weeks + done today (2 of 3 this week)
            val currentMonday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
            completions += HabitCompletionEntity(yogaId, currentMonday.toEpochDay())
            completions += HabitCompletionEntity(yogaId, todayEpoch)
            for (w in 1..19) {
                val weekMonday = currentMonday.minusWeeks(w.toLong())
                val offsets = if (w <= 8) listOf(0L, 2L, 6L) else listOf(1L, 4L)
                offsets.forEach { offset ->
                    completions += HabitCompletionEntity(yogaId, weekMonday.plusDays(offset).toEpochDay())
                }
            }

            // Cook at home: 1 of 5 this week (Monday), not yet done today
            if (today != currentMonday) {
                completions += HabitCompletionEntity(cookId, currentMonday.toEpochDay())
            }
            for (w in 1..19) {
                val weekMonday = currentMonday.minusWeeks(w.toLong())
                listOf(0L, 1L, 3L, 4L, 6L).forEach { offset ->
                    if ((w + offset.toInt()) % 6 != 0) {
                        completions += HabitCompletionEntity(cookId, weekMonday.plusDays(offset).toEpochDay())
                    }
                }
            }

            dao.insertCompletions(completions)
        }
    }
}
