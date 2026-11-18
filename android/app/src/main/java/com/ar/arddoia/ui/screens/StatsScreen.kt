package com.ar.arddoia.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ar.arddoia.data.repository.HabitWithStats
import com.ar.arddoia.ui.theme.ArddoiaColors
import com.ar.arddoia.ui.viewmodel.ArddoiaUiState
import com.ar.arddoia.ui.viewmodel.HeatmapDayCell
import com.ar.arddoia.ui.viewmodel.WeekdayStatUi

@Composable
fun StatsScreen(
    uiState: ArddoiaUiState,
    modifier: Modifier = Modifier
) {
    val stats = uiState.stats

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(ArddoiaColors.Background),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Column {
                Text(
                    text = "Last 30 days",
                    fontSize = 13.sp,
                    color = ArddoiaColors.TextSecondary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Stats",
                    style = MaterialTheme.typography.headlineLarge
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatKpiCard(
                    value = "${stats.onTrackPercent}%",
                    label = "On track",
                    modifier = Modifier.weight(1f)
                )
                StatKpiCard(
                    value = stats.checkInsLast30Days.toString(),
                    label = "Check-ins",
                    modifier = Modifier.weight(1f)
                )
                StatKpiCard(
                    value = stats.bestStreak.toString(),
                    label = "Best streak",
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            AllHabitsHeatmapCard(grid = stats.allHabitsGrid)
        }

        item {
            ByWeekdayCard(
                strongestLabel = stats.strongestWeekdayLabel,
                weekdayStats = stats.weekdayStats
            )
        }

        item {
            MostConsistentCard(habits = stats.mostConsistent)
        }
    }
}

@Composable
private fun StatKpiCard(
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = ArddoiaColors.SurfaceCard,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            Text(
                text = value,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = ArddoiaColors.TextPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                fontSize = 12.sp,
                color = ArddoiaColors.TextSecondary
            )
        }
    }
}

@Composable
private fun AllHabitsHeatmapCard(grid: List<List<HeatmapDayCell>>) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = ArddoiaColors.SurfaceCard,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Text(
                text = "All habits",
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = ArddoiaColors.TextPrimary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Share of habits done each day",
                fontSize = 12.sp,
                color = ArddoiaColors.TextSecondary
            )
            Spacer(modifier = Modifier.height(14.dp))

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(112.dp)
            ) {
                if (grid.isEmpty()) return@Canvas
                val columns = grid.size
                val rows = 7
                val gap = 3.5.dp.toPx()
                val cellW = (size.width - gap * (columns - 1)) / columns
                val cellH = (size.height - gap * (rows - 1)) / rows
                val cellSize = minOf(cellW, cellH)
                val totalGridWidth = cellSize * columns + gap * (columns - 1)
                val xOffset = (size.width - totalGridWidth) / 2f
                val corner = CornerRadius(3.5.dp.toPx(), 3.5.dp.toPx())

                grid.forEachIndexed { colIndex, column ->
                    column.forEachIndexed { rowIndex, cell ->
                        if (cell.date != null) {
                            val left = xOffset + colIndex * (cellSize + gap)
                            val top = rowIndex * (cellSize + gap)
                            val color = when {
                                cell.intensity <= 0f -> ArddoiaColors.EmptyCell
                                cell.intensity < 0.35f -> Color(0xFF39593C)
                                cell.intensity < 0.65f -> Color(0xFF538357)
                                cell.intensity < 0.90f -> Color(0xFF77B27C)
                                else -> ArddoiaColors.MintAccent
                            }

                            drawRoundRect(
                                color = color,
                                topLeft = Offset(left, top),
                                size = Size(cellSize, cellSize),
                                cornerRadius = corner
                            )

                            if (cell.isToday) {
                                drawRoundRect(
                                    color = ArddoiaColors.MintAccent,
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
    }
}

@Composable
private fun ByWeekdayCard(
    strongestLabel: String,
    weekdayStats: List<WeekdayStatUi>
) {
    val maxRatio = weekdayStats.maxOfOrNull { it.ratio } ?: 1f

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = ArddoiaColors.SurfaceCard,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Text(
                text = "By weekday",
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = ArddoiaColors.TextPrimary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = strongestLabel,
                fontSize = 12.sp,
                color = ArddoiaColors.TextSecondary
            )
            Spacer(modifier = Modifier.height(18.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(132.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {
                weekdayStats.forEach { day ->
                    val isStrongest = day.ratio >= maxRatio - 0.001f
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            contentAlignment = Alignment.BottomCenter
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(0.52f)
                                    .fillMaxHeight(day.ratio.coerceIn(0.18f, 1f))
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(
                                        if (isStrongest) ArddoiaColors.MintAccent
                                        else Color(0xFF425944)
                                    )
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = day.shortLabel,
                            fontSize = 12.sp,
                            color = ArddoiaColors.TextSecondary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MostConsistentCard(habits: List<HabitWithStats>) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = ArddoiaColors.SurfaceCard,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "Most consistent",
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = ArddoiaColors.TextPrimary
            )

            habits.forEach { item ->
                val fraction = (item.thirtyDayConsistencyPercent / 100f).coerceIn(0f, 1f)
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = item.habit.name,
                            fontSize = 14.sp,
                            color = ArddoiaColors.TextPrimary
                        )
                        Text(
                            text = "${item.thirtyDayConsistencyPercent}%",
                            fontSize = 13.sp,
                            color = ArddoiaColors.TextSecondary
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(ArddoiaColors.EmptyCell)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(fraction)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(3.dp))
                                .background(item.habit.color.color)
                        )
                    }
                }
            }
        }
    }
}
