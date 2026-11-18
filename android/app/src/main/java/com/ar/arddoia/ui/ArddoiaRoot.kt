package com.ar.arddoia.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.GridView
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ar.arddoia.ui.components.AddHabitBottomSheet
import com.ar.arddoia.ui.screens.HabitsScreen
import com.ar.arddoia.ui.screens.StatsScreen
import com.ar.arddoia.ui.screens.TodayScreen
import com.ar.arddoia.ui.theme.ArddoiaColors
import com.ar.arddoia.ui.viewmodel.ArddoiaViewModel

enum class ArddoiaTab(val label: String, val icon: ImageVector) {
    TODAY("Today", Icons.Rounded.Home),
    HABITS("Habits", Icons.Rounded.GridView),
    STATS("Stats", Icons.Rounded.BarChart)
}

@Composable
fun ArddoiaRoot(viewModel: ArddoiaViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var currentTab by rememberSaveable { mutableStateOf(ArddoiaTab.TODAY) }

    Scaffold(
        containerColor = ArddoiaColors.Background,
        bottomBar = {
            NavigationBar(
                containerColor = ArddoiaColors.BottomBar,
                contentColor = ArddoiaColors.TextPrimary
            ) {
                ArddoiaTab.entries.forEach { tab ->
                    val selected = tab == currentTab
                    NavigationBarItem(
                        selected = selected,
                        onClick = { currentTab = tab },
                        icon = {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = tab.label
                            )
                        },
                        label = {
                            Text(
                                text = tab.label,
                                fontSize = 12.sp,
                                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = ArddoiaColors.TextPrimary,
                            selectedTextColor = ArddoiaColors.TextPrimary,
                            indicatorColor = ArddoiaColors.NavIndicator,
                            unselectedIconColor = ArddoiaColors.TextSecondary,
                            unselectedTextColor = ArddoiaColors.TextSecondary
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        val contentModifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)

        when (currentTab) {
            ArddoiaTab.TODAY -> TodayScreen(
                uiState = uiState,
                onToggleHabit = viewModel::toggleHabitToday,
                onAddClick = viewModel::openAddHabitSheet,
                modifier = contentModifier
            )

            ArddoiaTab.HABITS -> HabitsScreen(
                uiState = uiState,
                onAddClick = viewModel::openAddHabitSheet,
                onToggleToday = viewModel::toggleHabitToday,
                onDeleteHabit = viewModel::deleteHabit,
                modifier = contentModifier
            )

            ArddoiaTab.STATS -> StatsScreen(
                uiState = uiState,
                modifier = contentModifier
            )
        }
    }

    if (uiState.isAddSheetOpen) {
        AddHabitBottomSheet(
            onDismiss = viewModel::closeAddHabitSheet,
            onAddHabit = viewModel::addHabit
        )
    }
}
