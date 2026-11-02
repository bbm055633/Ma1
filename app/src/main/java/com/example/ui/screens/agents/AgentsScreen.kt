package com.example.ui.screens.agents

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.models.AgentDataDefaults
import com.example.domain.models.AgentInfo
import com.example.ui.components.IslamicGeometricPatternBackground
import com.example.ui.components.generateGradientForColor
import com.example.ui.util.*
import com.example.ui.viewmodel.ActiveScreen
import com.example.ui.viewmodel.MainViewModel
import com.example.ui.theme.zheenCardStyle
import com.example.ui.theme.AppDesignStyle

/**
 * Samsung One UI 9 - Agents Screen (مركز الوكلاء المتخصصين)
 *
 * Design features:
 * - One UI 9 Reachability Header with generous spacing
 * - Background: #F9F6F0 warm off-white with 8% Islamic geometric pattern
 * - White 24px radius cards, soft shadow, 56x56 colorful circular icon on right with dynamic gradient
 * - Liquid glass category filter capsules
 * - Exact same functionality 100% preserved
 */
@Composable
fun AgentsScreen(
    viewModel: MainViewModel,
    onNavigate: (ActiveScreen) -> Unit
) {
    val userPrimaryColor by viewModel.userPrimaryColor.collectAsState()
    val isEnglish by viewModel.isEnglish.collectAsState()

    var selectedCategoryIndex by remember { mutableIntStateOf(0) }
    val categoriesAr = listOf("الكل", "الشريعة واللغة", "الإنتاجية", "المالية والضريبة", "الأنظمة والأعمال", "السياحة والقيادة")
    val categoriesEn = listOf("All", "Islamic & Arabic", "Productivity", "Finance & Tax", "Business & Law", "Travel & Tourism")
    val categories = if (isEnglish) categoriesEn else categoriesAr

    val filteredAgents = remember(selectedCategoryIndex) {
        when (selectedCategoryIndex) {
            1 -> AgentDataDefaults.AGENTS.filter { it.id == "mobeen" }
            2 -> AgentDataDefaults.AGENTS.filter { it.id in listOf("secretary", "hr") }
            3 -> AgentDataDefaults.AGENTS.filter { it.id in listOf("accountant", "financial", "sales") }
            4 -> AgentDataDefaults.AGENTS.filter { it.id in listOf("business", "legal") }
            5 -> AgentDataDefaults.AGENTS.filter { it.id in listOf("executive", "travel") }
            else -> AgentDataDefaults.AGENTS
        }
    }

    IslamicGeometricPatternBackground(
        patternColor = userPrimaryColor.copy(alpha = 0.08f)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 80.dp, start = 16.dp, end = 16.dp)
        ) {
            // Agents list: One UI 9 White Cards
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 12.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                items(filteredAgents) { agent ->
                    OneUi9AgentCard(
                        agent = agent,
                        primaryColor = userPrimaryColor,
                        isEnglish = isEnglish,
                        onStartChat = { onNavigate(ActiveScreen.AgentChat(agent.id)) }
                    )
                }
            }

            // Liquid Glass Categories Filter Row at the bottom right above bottom nav bar
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = Color.White.copy(alpha = 0.95f),
                shadowElevation = 6.dp,
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE8E4DC)),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            ) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(categories.size) { index ->
                        val cat = categories[index]
                        val isSelected = selectedCategoryIndex == index
                        Surface(
                            onClick = { selectedCategoryIndex = index },
                            shape = RoundedCornerShape(20.dp),
                            color = if (isSelected) userPrimaryColor else Color(0xFFF9F6F0),
                            shadowElevation = if (isSelected) 3.dp else 0.dp,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) userPrimaryColor else Color(0xFFE8E4DC)
                            ),
                            modifier = Modifier.testTag("agent_category_$index")
                        ) {
                            Text(
                                text = cat,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else Color(0xFF1C1B1A),
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Samsung One UI 9 Agent Card:
 * - White 24px radius card, soft shadow, 20px padding
 * - 56x56 circular colorful icon with dynamic gradient on the right (RTL start)
 * - Title Bold 18, Description Regular 14 gray
 * - Voice indicator & start chat action
 */
@Composable
fun OneUi9AgentCard(
    agent: AgentInfo,
    primaryColor: Color,
    isEnglish: Boolean = false,
    onStartChat: () -> Unit
) {
    val accentColor = Color(agent.accentColor)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .zheenCardStyle(RoundedCornerShape(24.dp), accentColor)
            .clickable { onStartChat() }
            .testTag("agent_card_${agent.id}")
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // 56x56 Circular Colorful Icon on Right
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(generateGradientForColor(accentColor, agent.name.length)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isEnglish) agent.id.first().uppercase() else (agent.name.firstOrNull()?.toString() ?: "و"),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = agent.localizedName(isEnglish),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1C1B1A)
                        )
                        Text(
                            text = agent.localizedRole(isEnglish),
                            fontSize = 13.sp,
                            color = accentColor,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = accentColor.copy(alpha = 0.12f)
                ) {
                    Text(
                        text = agent.localizedBadge(isEnglish),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = accentColor,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = agent.localizedDescription(isEnglish),
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = Color(0xFF6E685F),
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFF9F6F0)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.RecordVoiceOver,
                            contentDescription = null,
                            tint = Color(0xFF6E685F),
                            modifier = Modifier.size(15.dp)
                        )
                        Text(
                            text = agent.localizedVoice(isEnglish),
                            fontSize = 12.sp,
                            color = Color(0xFF6E685F),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Button(
                    onClick = onStartChat,
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    modifier = Modifier.height(40.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(if (isEnglish) "Start Chat" else "بدء المحادثة", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Icon(
                            if (isEnglish) Icons.AutoMirrored.Filled.ArrowForward else Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                            modifier = Modifier.size(15.dp)
                        )
                    }
                }
            }
        }
    }
}
