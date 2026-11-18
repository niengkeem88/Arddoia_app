package com.ar.arddoia.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.Bookmark
import androidx.compose.material.icons.rounded.DirectionsRun
import androidx.compose.material.icons.rounded.Eco
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FitnessCenter
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.NightsStay
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.WaterDrop
import androidx.compose.material.icons.rounded.WbSunny
import androidx.compose.ui.graphics.vector.ImageVector
import com.ar.arddoia.data.model.HabitIcon

fun HabitIcon.toImageVector(): ImageVector = when (this) {
    HabitIcon.WATER_DROP -> Icons.Rounded.WaterDrop
    HabitIcon.BOOK -> Icons.Rounded.Bookmark
    HabitIcon.MOON -> Icons.Rounded.NightsStay
    HabitIcon.RUN -> Icons.Rounded.DirectionsRun
    HabitIcon.LEAF -> Icons.Rounded.Eco
    HabitIcon.DUMBBELL -> Icons.Rounded.FitnessCenter
    HabitIcon.EDIT -> Icons.Rounded.Edit
    HabitIcon.MUSIC -> Icons.Rounded.MusicNote
    HabitIcon.HEART -> Icons.Rounded.Favorite
    HabitIcon.SUN -> Icons.Rounded.WbSunny
    HabitIcon.BOLT -> Icons.Rounded.Bolt
    HabitIcon.STAR -> Icons.Rounded.Star
}
