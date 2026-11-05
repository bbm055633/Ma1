package com.example.ui.screens.home.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class HabitItemData(
    val id: String,
    val nameAr: String,
    val nameEn: String,
    val sublabelAr: String,
    val sublabelEn: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val color: Color,
    val isCompleted: Boolean = false
)

@Composable
fun DailyHabitsGrid(
    isEnglish: Boolean = false,
    modifier: Modifier = Modifier
) {
    var habits by remember {
        mutableStateOf(
            listOf(
                HabitItemData(
                    id = "prayer",
                    nameAr = "الصلاة",
                    nameEn = "Prayers",
                    sublabelAr = "5/5",
                    sublabelEn = "5/5",
                    icon = Icons.Default.WbSunny,
                    color = Color(0xFF10B981), // #10B981
                    isCompleted = true
                ),
                HabitItemData(
                    id = "quran",
                    nameAr = "القرآن",
                    nameEn = "Quran",
                    sublabelAr = "٣ صفحات",
                    sublabelEn = "3 pages",
                    icon = Icons.Default.MenuBook,
                    color = Color(0xFFF59E0B), // #F59E0B
                    isCompleted = false
                ),
                HabitItemData(
                    id = "adhkar",
                    nameAr = "الأذكار",
                    nameEn = "Adhkar",
                    sublabelAr = "صباح ومساء",
                    sublabelEn = "Morning/Eve",
                    icon = Icons.Default.Favorite,
                    color = Color(0xFF8B5CF6), // #8B5CF6
                    isCompleted = false
                ),
                HabitItemData(
                    id = "charity",
                    nameAr = "الصدقة",
                    nameEn = "Charity",
                    sublabelAr = "يومية",
                    sublabelEn = "Daily",
                    icon = Icons.Default.CardGiftcard,
                    color = Color(0xFFEC4899), // #EC4899
                    isCompleted = false
                )
            )
        )
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (isEnglish) "Daily Habits" else "العادات اليومية",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            val completedCount = habits.count { it.isCompleted }
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFF10B981).copy(alpha = 0.15f)
            ) {
                Text(
                    text = if (isEnglish) "$completedCount/4 completed" else "$completedCount/٤ مكتملة",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF10B981),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            habits.forEachIndexed { index, habit ->
                DailyHabitCardItem(
                    habit = habit,
                    isEnglish = isEnglish,
                    modifier = Modifier.weight(1f),
                    onToggle = {
                        habits = habits.toMutableList().apply {
                            this[index] = habit.copy(isCompleted = !habit.isCompleted)
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun DailyHabitCardItem(
    habit: HabitItemData,
    isEnglish: Boolean,
    modifier: Modifier = Modifier,
    onToggle: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (habit.isCompleted) 1.03f else 1.0f,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "scale"
    )

    Surface(
        onClick = onToggle,
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(
            width = if (habit.isCompleted) 1.5.dp else 1.dp,
            color = if (habit.isCompleted) habit.color else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
        ),
        shadowElevation = if (habit.isCompleted) 6.dp else 2.dp,
        modifier = modifier
            .height(104.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .animateContentSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    if (habit.isCompleted) habit.color.copy(alpha = 0.12f)
                    else Color.Transparent
                )
                .padding(8.dp)
        ) {
            // Checkmark Badge in top corner when completed
            if (habit.isCompleted) {
                Box(
                    modifier = Modifier
                        .size(18.dp)
                        .clip(CircleShape)
                        .background(habit.color)
                        .align(Alignment.TopEnd),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Completed",
                        tint = Color.White,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (habit.isCompleted) habit.color.copy(alpha = 0.25f)
                            else habit.color.copy(alpha = 0.12f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = habit.icon,
                        contentDescription = habit.nameAr,
                        tint = habit.color,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = if (isEnglish) habit.nameEn else habit.nameAr,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1
                )

                Text(
                    text = if (isEnglish) habit.sublabelEn else habit.sublabelAr,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (habit.isCompleted) habit.color else MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }
        }
    }
}
