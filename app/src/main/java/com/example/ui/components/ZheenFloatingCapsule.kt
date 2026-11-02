package com.example.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.theme.LocalCardTransparency

import androidx.compose.material.icons.filled.Translate

/**
 * بيانات المستخدم والإشعارات المتاحة عبر CompositionLocal
 */
data class UserProfileData(
    val userName: String = "فهد",
    val userProfileImageUri: String? = null,
    val notificationCount: Int = 3,
    val onAvatarClick: (() -> Unit)? = null,
    val onNotificationClick: (() -> Unit)? = null,
    val isEnglish: Boolean = false,
    val onToggleLanguage: (() -> Unit)? = null
)

val LocalUserProfile = staticCompositionLocalOf { UserProfileData() }

/**
 * ZheenFloatingCapsule - كبسولة سفلية/علوية عائمة ومستقلة
 * تُستخدم عبر جميع شاشات التطبيق كبديل أنيق وعصري للـ TopAppBar التقليدي
 *
 * @param screenTitle عنوان الشاشة الحالية والذي يتغير بسلاسة عند التنقل
 * @param modifier محددات التنسيق والموقع
 */
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ZheenFloatingCapsule(
    screenTitle: String,
    isCollapsed: Boolean = false,
    modifier: Modifier = Modifier
) {
    val userProfile = LocalUserProfile.current
    val isDarkMode = MaterialTheme.colorScheme.surface.luminance() < 0.5f
    val isStarry = LocalStarryNightActive.current
    val cardAlpha = LocalCardTransparency.current
    var showNotificationPopup by remember { mutableStateOf(false) }

    // ألوان الكبسولة العائمة المستمدة من نظام الألوان الحديث
    val containerBg = if (isStarry) {
        Color(0xFF121212).copy(alpha = cardAlpha.coerceAtLeast(0.75f))
    } else if (isDarkMode) {
        Color(0xFF1E1E1E).copy(alpha = 0.94f)
    } else {
        Color(0xFFFFFFFF).copy(alpha = 0.96f)
    }

    val borderColor = if (isStarry) {
        Color.White.copy(alpha = 0.20f)
    } else if (isDarkMode) {
        Color(0xFF333333)
    } else {
        Color(0x12000000)
    }

    val primaryTextColor = if (isDarkMode || isStarry) Color.White else Color(0xFF1A1A1A)
    val accentColor = MaterialTheme.colorScheme.primary

    val capsuleWidthFraction by animateFloatAsState(
        targetValue = if (isCollapsed) 0.18f else 1.0f,
        animationSpec = tween(250),
        label = "capsule_width_fraction"
    )

    val capsuleHorizPadding by animateDpAsState(
        targetValue = if (isCollapsed) 16.dp else 16.dp,
        animationSpec = tween(250),
        label = "capsule_horiz_padding"
    )

    val capsuleHeight by animateDpAsState(
        targetValue = if (isCollapsed) 44.dp else 52.dp,
        animationSpec = tween(250),
        label = "capsule_height"
    )

    if (showNotificationPopup) {
        NotificationSimplePopup(
            notificationCount = userProfile.notificationCount,
            isDarkMode = isDarkMode,
            isEnglish = userProfile.isEnglish,
            onDismiss = { showNotificationPopup = false },
            onOpenSettings = {
                showNotificationPopup = false
                userProfile.onNotificationClick?.invoke()
            }
        )
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = capsuleHorizPadding, vertical = 4.dp)
            .testTag("zheen_floating_capsule_wrapper"),
        contentAlignment = Alignment.TopStart
    ) {
        Surface(
            shape = CircleShape,
            color = containerBg,
            shadowElevation = if (isStarry) 2.dp else 6.dp,
            border = BorderStroke(1.dp, borderColor),
            modifier = Modifier
                .then(
                    if (isCollapsed) Modifier.width(52.dp) else Modifier.fillMaxWidth()
                )
                .height(capsuleHeight)
                .testTag("zheen_floating_capsule")
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // 1. الصورة الشخصية للمستخدم (حواف دائرية كاملة بدون أي زوايا حادة)
                Box(
                    modifier = Modifier
                        .size(if (isCollapsed) 36.dp else 40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                        .clickable { userProfile.onAvatarClick?.invoke() }
                        .testTag("capsule_user_avatar"),
                    contentAlignment = Alignment.Center
                ) {
                    if (!userProfile.userProfileImageUri.isNullOrEmpty()) {
                        AsyncImage(
                            model = userProfile.userProfileImageUri,
                            contentDescription = "الصورة الشخصية",
                            contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        val initialLetter = userProfile.userName.trim().take(1).ifEmpty { "ف" }
                        Text(
                            text = initialLetter,
                            fontSize = if (isCollapsed) 16.sp else 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = accentColor
                        )
                    }
                }

                // 2. عنوان الشاشة في الوسط (يختفي بانسيابية تامة عند التمرير لأسفل ويعود عند التمرير لأعلى)
                AnimatedVisibility(
                    visible = !isCollapsed,
                    enter = fadeIn(animationSpec = tween(220)) + expandHorizontally(),
                    exit = fadeOut(animationSpec = tween(150)) + shrinkHorizontally(),
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        AnimatedContent(
                            targetState = screenTitle,
                            transitionSpec = {
                                (fadeIn() + scaleIn(initialScale = 0.92f)).togetherWith(fadeOut() + scaleOut(targetScale = 0.92f))
                            },
                            label = "screen_title_animation"
                        ) { targetTitle ->
                            Text(
                                text = targetTitle,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = com.example.ui.theme.AppTypography.headings,
                                color = primaryTextColor,
                                textAlign = TextAlign.Center,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                // 3. زر تغيير اللغة الموحد وأيقونة الإشعارات (حواف دائرية ناعمة CircleShape بدون أي مربعات أو زوايا حادة)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    if (userProfile.onToggleLanguage != null) {
                        Surface(
                            onClick = { userProfile.onToggleLanguage.invoke() },
                            shape = CircleShape,
                            color = if (isDarkMode) Color(0xFF282828) else Color(0xFFF1EFEA),
                            modifier = Modifier
                                .height(34.dp)
                                .testTag("capsule_language_toggle_button")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Translate,
                                    contentDescription = if (userProfile.isEnglish) "Switch to Arabic" else "Switch to English",
                                    tint = accentColor,
                                    modifier = Modifier.size(13.dp)
                                )
                                Text(
                                    text = if (userProfile.isEnglish) "عربي" else "EN",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = com.example.ui.theme.AppTypography.body,
                                    color = accentColor
                                )
                            }
                        }
                    }

                    // زر الجرس - يفتح لوحة إشعارات منبثقة بتصميم دائري كامل
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(if (isDarkMode) Color(0xFF282828) else Color(0xFFF1EFEA))
                            .clickable { showNotificationPopup = true }
                            .testTag("capsule_notification_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Notifications,
                            contentDescription = "الإشعارات",
                            tint = primaryTextColor.copy(alpha = 0.85f),
                            modifier = Modifier.size(18.dp)
                        )

                        if (userProfile.notificationCount > 0) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .offset(x = 1.dp, y = (-1).dp)
                                    .size(14.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFE63946)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (userProfile.notificationCount > 9) "9+" else userProfile.notificationCount.toString(),
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * لوحة الإشعارات المنبثقة البسيطة والأنيقة (Notification Simple Popup)
 */
@Composable
fun NotificationSimplePopup(
    notificationCount: Int,
    isDarkMode: Boolean,
    isEnglish: Boolean,
    onDismiss: () -> Unit,
    onOpenSettings: () -> Unit
) {
    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(26.dp),
            color = if (isDarkMode) Color(0xFF1E1E1E) else Color.White,
            shadowElevation = 8.dp,
            border = BorderStroke(1.dp, if (isDarkMode) Color(0xFF333333) else Color(0x15000000)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                            modifier = Modifier.size(38.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Outlined.Notifications,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Column {
                            Text(
                                text = if (isEnglish) "Notifications" else "الإشعارات والتنبيهات",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = if (isDarkMode) Color.White else Color(0xFF1A1A1A)
                            )
                            Text(
                                text = if (isEnglish) "$notificationCount new alerts" else "$notificationCount تنبيهات جديدة",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(if (isDarkMode) Color(0xFF282828) else Color(0xFFF1EFEA))
                    ) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Close",
                            tint = if (isDarkMode) Color.White else Color(0xFF1A1A1A),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                HorizontalDivider(color = if (isDarkMode) Color(0xFF2E2E2E) else Color(0xFFF0EFEA))

                // Notifications items
                val notifs = remember(isEnglish) {
                    listOf(
                        Triple(
                            if (isEnglish) "Prayer Reminder" else "تذكير بمواقيت الصلاة",
                            if (isEnglish) "Fajr and daily prayers on Umm Al-Qura time" else "مواقيت الصلاة اليومية متزامنة مع تقويم أم القرى",
                            "🕌"
                        ),
                        Triple(
                            if (isEnglish) "Specialized AI Agents" else "الوكلاء المتخصصون",
                            if (isEnglish) "All intelligent assistants are ready to generate documents" else "مساعدك الذكي جاهز لإعداد دراسات الجدوى والخطط والمهام",
                            "✨"
                        ),
                        Triple(
                            if (isEnglish) "Schedule & Tasks" else "المهام والمواعيد",
                            if (isEnglish) "Organize your priorities for today easily" else "لديك مهام وتذكيرات في جدول اليوم تنتظر إنجازك",
                            "📝"
                        )
                    )
                }

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    notifs.forEach { (title, subtitle, emoji) ->
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = if (isDarkMode) Color(0xFF262626) else Color(0xFFF9F7F2),
                            border = BorderStroke(1.dp, if (isDarkMode) Color(0xFF333333) else Color(0x0F000000)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text(emoji, fontSize = 20.sp)
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = title,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = if (isDarkMode) Color.White else Color(0xFF1A1A1A)
                                    )
                                    Text(
                                        text = subtitle,
                                        fontSize = 11.sp,
                                        color = if (isDarkMode) Color(0xFFA0AEC0) else Color(0xFF64748B)
                                    )
                                }
                            }
                        }
                    }
                }

                // Action button to full menu / control center
                Button(
                    onClick = onOpenSettings,
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (isEnglish) "Open Control Center" else "مركز التحكم الشامل",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = Color.White
                    )
                }
            }
        }
    }
}
