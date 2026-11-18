package com.ar.arddoia.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ar.arddoia.data.repository.HabitWithStats
import com.ar.arddoia.ui.components.toImageVector
import com.ar.arddoia.ui.theme.ArddoiaColors
import com.ar.arddoia.ui.viewmodel.ArddoiaUiState
import com.ar.arddoia.ui.viewmodel.WeekDotUi

@Composable
fun TodayScreen(
    uiState: ArddoiaUiState,
    onToggleHabit: (Long) -> Unit,
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(ArddoiaColors.Background),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            TodayHeader(
                dateLabel = uiState.todayDateLabel,
                onAddClick = onAddClick
            )
        }

        item {
            Spacer(modifier = Modifier.height(4.dp))
            TodayHeroSummaryCard(
                completedCount = uiState.completedTodayCount,
                totalCount = uiState.totalHabitsCount,
                topStreakHeadline = uiState.topStreakHeadline,
                weekDots = uiState.weekDots
            )
            Spacer(modifier = Modifier.height(10.dp))
        }

        if (uiState.todoHabits.isNotEmpty()) {
            item {
                Text(
                    text = "To do · ${uiState.todoHabits.size}",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = ArddoiaColors.TextPrimary,
                    modifier = Modifier.padding(top = 4.dp, bottom = 2.dp)
                )
            }
            items(
                items = uiState.todoHabits,
                key = { "todo_${it.habit.id}" }
            ) { item ->
                HabitCheckRowCard(
                    habitWithStats = item,
                    onToggle = { onToggleHabit(item.habit.id) }
                )
            }
        }

        if (uiState.doneHabits.isNotEmpty()) {
            item {
                Text(
                    text = "Done · ${uiState.doneHabits.size}",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = ArddoiaColors.TextPrimary,
                    modifier = Modifier.padding(top = 10.dp, bottom = 2.dp)
                )
            }
            items(
                items = uiState.doneHabits,
                key = { "done_${it.habit.id}" }
            ) { item ->
                HabitCheckRowCard(
                    habitWithStats = item,
                    onToggle = { onToggleHabit(item.habit.id) }
                )
            }
        }
    }
}

@Composable
private fun TodayHeader(
    dateLabel: String,
    onAddClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = dateLabel,
                fontSize = 13.sp,
                color = ArddoiaColors.TextSecondary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Today",
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
}

@Composable
private fun TodayHeroSummaryCard(
    completedCount: Int,
    totalCount: Int,
    topStreakHeadline: String,
    weekDots: List<WeekDotUi>
) {
    val progressTarget = if (totalCount > 0) completedCount.toFloat() / totalCount.toFloat() else 0f
    val animatedProgress by animateFloatAsState(
        targetValue = progressTarget,
        animationSpec = tween(durationMillis = 450),
        label = "todayProgress"
    )
    val remaining = (totalCount - completedCount).coerceAtLeast(0)
    val statusTitle = when {
        totalCount == 0 -> "No habits yet"
        remaining == 0 -> "All done!"
        else -> "$remaining to go"
    }

    Surface(
        shape = RoundedCornerShape(24.dp),
        color = ArddoiaColors.HeroGreen,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 22.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(106.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val strokeWidth = 10.dp.toPx()
                    drawArc(
                        color = ArddoiaColors.HeroTrack,
                        startAngle = -90f,
                        sweepAngle = 360f,
                        useCenter = false,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )
                    drawArc(
                        color = ArddoiaColors.MintAccent,
                        startAngle = -90f,
                        sweepAngle = 360f * animatedProgress,
                        useCenter = false,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$completedCount/$totalCount",
                        fontSize = 23.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "done",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }

            Spacer(modifier = Modifier.width(20.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = statusTitle,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = topStreakHeadline,
                    fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.78f),
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(14.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    weekDots.forEach { dot ->
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            val fillColor = when {
                                dot.isToday && !dot.isCompleted -> Color.Transparent
                                dot.isCompleted -> ArddoiaColors.MintAccent.copy(alpha = if (dot.isToday) 0.55f else 0.9f)
                                else -> ArddoiaColors.HeroTrack.copy(alpha = 0.75f)
                            }
                            Box(
                                modifier = Modifier
                                    .size(18.dp)
                                    .clip(CircleShape)
                                    .background(fillColor)
                                    .then(
                                        if (dot.isToday) {
                                            Modifier.border(
                                                width = 1.5.dp,
                                                color = ArddoiaColors.MintAccent,
                                                shape = CircleShape
                                            )
                                        } else {
                                            Modifier
                                        }
                                    )
                            )
                            Spacer(modifier = Modifier.height(5.dp))
                            Text(
                                text = dot.shortLabel,
                                fontSize = 11.sp,
                                fontWeight = if (dot.isToday) FontWeight.Bold else FontWeight.Normal,
                                color = if (dot.isToday) Color.White else Color.White.copy(alpha = 0.68f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HabitCheckRowCard(
    habitWithStats: HabitWithStats,
    onToggle: () -> Unit
) {
    val habit = habitWithStats.habit
    val accent = habit.color.color
    val isDone = habitWithStats.isDoneToday

    val circleBg by animateColorAsState(
        targetValue = if (isDone) accent else Color.Transparent,
        animationSpec = tween(180),
        label = "checkBg"
    )
    val checkTint by animateColorAsState(
        targetValue = if (isDone) Color.White else accent.copy(alpha = 0.35f),
        animationSpec = tween(180),
        label = "checkTint"
    )

    Surface(
        onClick = onToggle,
        shape = RoundedCornerShape(18.dp),
        color = ArddoiaColors.SurfaceCard,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(habit.color.containerColor)
            ) {
                Icon(
                    imageVector = habit.icon.toImageVector(),
                    contentDescription = habit.name,
                    tint = accent,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = habit.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = ArddoiaColors.TextPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = habitWithStats.todaySubtitle,
                    fontSize = 13.sp,
                    color = ArddoiaColors.TextSecondary
                )
            }

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(circleBg)
                    .then(
                        if (!isDone) {
                            Modifier.border(
                                width = 1.5.dp,
                                color = accent.copy(alpha = 0.45f),
                                shape = CircleShape
                            )
                        } else {
                            Modifier
                        }
                    )
            ) {
                Icon(
                    imageVector = Icons.Rounded.Check,
                    contentDescription = if (isDone) "Completed" else "Mark complete",
                    tint = checkTint,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
