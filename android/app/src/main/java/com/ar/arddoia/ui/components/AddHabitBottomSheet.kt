package com.ar.arddoia.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ar.arddoia.data.model.HabitColor
import com.ar.arddoia.data.model.HabitGoal
import com.ar.arddoia.data.model.HabitIcon
import com.ar.arddoia.ui.theme.ArddoiaColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddHabitBottomSheet(
    onDismiss: () -> Unit,
    onAddHabit: (name: String, icon: HabitIcon, color: HabitColor, goal: HabitGoal) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var name by remember { mutableStateOf("") }
    var selectedIcon by remember { mutableStateOf(HabitIcon.WATER_DROP) }
    var selectedColor by remember { mutableStateOf(HabitColor.TEAL) }
    var selectedGoal by remember { mutableStateOf(HabitGoal.EVERY_DAY) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = ArddoiaColors.SurfaceCard,
        dragHandle = null,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp, vertical = 24.dp)
        ) {
            Text(
                text = "New habit",
                style = MaterialTheme.typography.headlineSmall,
                color = ArddoiaColors.TextPrimary
            )

            Spacer(modifier = Modifier.height(18.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                placeholder = {
                    Text(
                        text = "Name",
                        color = ArddoiaColors.TextSecondary
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = selectedColor.color,
                    unfocusedBorderColor = ArddoiaColors.TextSecondary.copy(alpha = 0.55f),
                    focusedTextColor = ArddoiaColors.TextPrimary,
                    unfocusedTextColor = ArddoiaColors.TextPrimary,
                    cursorColor = ArddoiaColors.MintAccent
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(22.dp))

            Text(
                text = "Icon",
                style = MaterialTheme.typography.labelMedium,
                color = ArddoiaColors.TextSecondary
            )

            Spacer(modifier = Modifier.height(10.dp))

            val icons = HabitIcon.entries
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                icons.chunked(6).forEach { rowIcons ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        rowIcons.forEach { icon ->
                            val isSelected = icon == selectedIcon
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        if (isSelected) selectedColor.color.copy(alpha = 0.14f)
                                        else ArddoiaColors.SurfaceElevated
                                    )
                                    .then(
                                        if (isSelected) {
                                            Modifier.border(
                                                width = 1.5.dp,
                                                color = selectedColor.color,
                                                shape = RoundedCornerShape(12.dp)
                                            )
                                        } else {
                                            Modifier
                                        }
                                    )
                                    .clickable { selectedIcon = icon }
                            ) {
                                Icon(
                                    imageVector = icon.toImageVector(),
                                    contentDescription = icon.name,
                                    tint = if (isSelected) Color.White else ArddoiaColors.TextPrimary,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            Text(
                text = "Colour",
                style = MaterialTheme.typography.labelMedium,
                color = ArddoiaColors.TextSecondary
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                HabitColor.entries.forEach { habitColor ->
                    val isSelected = habitColor == selectedColor
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .then(
                                if (isSelected) {
                                    Modifier.border(2.5.dp, Color.White, CircleShape)
                                } else {
                                    Modifier
                                }
                            )
                            .clickable { selectedColor = habitColor }
                    ) {
                        Box(
                            modifier = Modifier
                                .size(if (isSelected) 28.dp else 32.dp)
                                .clip(CircleShape)
                                .background(habitColor.color)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            Text(
                text = "Goal",
                style = MaterialTheme.typography.labelMedium,
                color = ArddoiaColors.TextSecondary
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                HabitGoal.entries.forEach { goal ->
                    val isSelected = goal == selectedGoal
                    Surface(
                        onClick = { selectedGoal = goal },
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSelected) ArddoiaColors.GoalSelectedBg else Color.Transparent,
                        border = if (isSelected) null else BorderStroke(
                            width = 1.dp,
                            color = ArddoiaColors.OutlineMuted
                        )
                    ) {
                        Text(
                            text = goal.label,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (isSelected) ArddoiaColors.MintAccent else ArddoiaColors.TextSecondary,
                            modifier = Modifier.padding(horizontal = 13.dp, vertical = 9.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(26.dp))

            Button(
                onClick = {
                    if (name.trim().isNotEmpty()) {
                        onAddHabit(name, selectedIcon, selectedColor, selectedGoal)
                    }
                },
                enabled = name.trim().isNotEmpty(),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = ArddoiaColors.MintButton,
                    contentColor = ArddoiaColors.MintButtonText,
                    disabledContainerColor = ArddoiaColors.MintButton.copy(alpha = 0.55f),
                    disabledContentColor = ArddoiaColors.MintButtonText.copy(alpha = 0.65f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text(
                    text = "Add habit",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
