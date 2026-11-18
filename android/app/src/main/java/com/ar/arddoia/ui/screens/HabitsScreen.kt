package com.ar.arddoia.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ar.arddoia.data.repository.HabitWithStats
import com.ar.arddoia.ui.components.toImageVector
import com.ar.arddoia.ui.theme.ArddoiaColors
import com.ar.arddoia.ui.viewmodel.ArddoiaUiState
import java.time.LocalDate

@Composable
fun HabitsScreen(
    uiState: ArddoiaUiState,
    onAddClick: () -> Unit,
    onToggleToday: (Long) -> Unit,
    onDeleteHabit: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    var habitPendingDelete by remember { mutableStateOf<HabitWithStats?>(null) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(ArddoiaColors.Background),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Last 20 weeks",
                        fontSize = 13.sp,
                        color = ArddoiaColors.TextSecondary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Habits",
                        style = MaterialTheme.typography.headlineLarge
                    )
                }

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(ArddoiaColors.FabGreen)
                        .clickable(onClick = onAddClick)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Add,
                        contentDescription = "New habit",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
        }

        items(
            items = uiState.habits,
            key = { it.habit.id }
        ) { habitWithStats ->
            HabitHeatmapCard(
                habitWithStats = habitWithStats,
                twentyWeekDates = uiState.twentyWeekDates,
                todayEpochDay = uiState.todayEpochDay,
                onClick = { onToggleToday(habitWithStats.habit.id) },
                onLongClick = { habitPendingDelete = habitWithStats }
            )
        }
    }

    habitPendingDelete?.let { pending ->
        AlertDialog(
            onDismissRequest = { habitPendingDelete = null },
            containerColor = ArddoiaColors.SurfaceCard,
            title = {
                Text(
                    text = "Delete \"${pending.habit.name}\"?",
                    color = ArddoiaColors.TextPrimary
                )
            },
            text = {
                Text(
                    text = "This removes the habit and its check-in history.",
                    color = ArddoiaColors.TextSecondary
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteHabit(pending.habit.id)
                        habitPendingDelete = null
                    }
                ) {
                    Text("Delete", color = Color(0xFFF25F5C))
                }
            },
            dismissButton = {
                TextButton(onClick = { habitPendingDelete = null }) {
                    Text("Cancel", color = ArddoiaColors.TextSecondary)
                }
            }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun HabitHeatmapCard(
    habitWithStats: HabitWithStats,
    twentyWeekDates: List<List<LocalDate?>>,
    todayEpochDay: Long,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    val habit = habitWithStats.habit
    val accent = habit.color.color

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = ArddoiaColors.SurfaceCard,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(11.dp))
                        .background(habit.color.containerColor)
                ) {
                    Icon(
                        imageVector = habit.icon.toImageVector(),
                        contentDescription = habit.name,
                        tint = accent,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = habit.name,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = ArddoiaColors.TextPrimary
                    )
                    Text(
                        text = habit.goal.label,
                        fontSize = 12.sp,
                        color = ArddoiaColors.TextSecondary
                    )
                }

                Text(
                    text = buildAnnotatedString {
                        withStyle(
                            SpanStyle(
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = ArddoiaColors.TextPrimary
                            )
                        ) {
                            append(habitWithStats.habitsCardStreakNumber.toString())
                        }
                        append(" ")
                        withStyle(
                            SpanStyle(
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Normal,
                                color = ArddoiaColors.TextSecondary
                            )
                        ) {
                            append(habitWithStats.habitsCardStreakUnit)
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            TwentyWeekHeatmapGrid(
                twentyWeekDates = twentyWeekDates,
                completedEpochDays = habitWithStats.completedEpochDays,
                todayEpochDay = todayEpochDay,
                activeColor = accent
            )
        }
    }
}

@Composable
private fun TwentyWeekHeatmapGrid(
    twentyWeekDates: List<List<LocalDate?>>,
    completedEpochDays: Set<Long>,
    todayEpochDay: Long,
    activeColor: Color
) {
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(112.dp)
    ) {
        val columns = twentyWeekDates.size.coerceAtLeast(1)
        val rows = 7
        val gap = 3.5.dp.toPx()
        val cellW = (size.width - gap * (columns - 1)) / columns
        val cellH = (size.height - gap * (rows - 1)) / rows
        val cellSize = minOf(cellW, cellH)
        val totalGridWidth = cellSize * columns + gap * (columns - 1)
        val xOffset = (size.width - totalGridWidth) / 2f
        val corner = CornerRadius(3.5.dp.toPx(), 3.5.dp.toPx())

        twentyWeekDates.forEachIndexed { colIndex, week ->
            week.forEachIndexed { rowIndex, date ->
                if (date != null) {
                    val epoch = date.toEpochDay()
                    val isDone = epoch in completedEpochDays
                    val isToday = epoch == todayEpochDay
                    val left = xOffset + colIndex * (cellSize + gap)
                    val top = rowIndex * (cellSize + gap)

                    drawRoundRect(
                        color = if (isDone) activeColor else ArddoiaColors.EmptyCell,
                        topLeft = Offset(left, top),
                        size = Size(cellSize, cellSize),
                        cornerRadius = corner
                    )

                    if (isToday) {
                        drawRoundRect(
                            color = activeColor,
                            topLeft = Offset(left, top),
                            size = Size(cellSize, cellSize),
                            cornerRadius = corner,
                            style = Stroke(width = 1.5.dp.toPx())
                        )
                    }
                }
            }
        }
    }
}
