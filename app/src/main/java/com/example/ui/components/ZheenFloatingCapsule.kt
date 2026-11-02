package com.example.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
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
    modifier: Modifier = Modifier
) {
    val userProfile = LocalUserProfile.current
    val isDarkMode = MaterialTheme.colorScheme.surface.luminance() < 0.5f
    val isStarry = LocalStarryNightActive.current
    val cardAlpha = LocalCardTransparency.current

    // ألوان الكبسولة العائمة المستمدة من نظام الألوان الحديث
    val containerBg = if (isStarry) {
        Color(0xFF121212).copy(alpha = cardAlpha.coerceAtLeast(0.75f))
    } else if (isDarkMode) {
        Color(0xFF1E1E1E).copy(alpha = 0.92f)
    } else {
        Color(0xFFFFFFFF).copy(alpha = 0.95f)
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

    Box(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .testTag("zheen_floating_capsule_wrapper"),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            shape = RoundedCornerShape(32.dp),
            color = containerBg,
            shadowElevation = if (isStarry) 2.dp else 8.dp,
            border = BorderStroke(1.dp, borderColor),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .testTag("zheen_floating_capsule")
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // 1. الصورة الشخصية للمستخدم (ثابتة في كل الصفحات)
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF064E3B))
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
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                // 2. عنوان الشاشة في الوسط (يتغير بسلاسة عند التنقل بين التبويبات)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 12.dp),
                    contentAlignment = Alignment.Center
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
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = primaryTextColor,
                            textAlign = TextAlign.Center,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // 3. زر تغيير اللغة الموحد وأيقونة الإشعارات
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    if (userProfile.onToggleLanguage != null) {
                        Surface(
                            onClick = { userProfile.onToggleLanguage.invoke() },
                            shape = RoundedCornerShape(20.dp),
                            color = if (isDarkMode) Color(0xFF282828) else Color(0xFFF0EFEA),
                            border = BorderStroke(1.dp, if (isDarkMode) Color(0xFF383838) else Color(0x0F000000)),
                            modifier = Modifier.testTag("capsule_language_toggle_button")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Translate,
                                    contentDescription = if (userProfile.isEnglish) "Switch to Arabic" else "Switch to English",
                                    tint = accentColor,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = if (userProfile.isEnglish) "عربي" else "EN",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = accentColor
                                )
                            }
                        }
                    }

                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .clickable { userProfile.onNotificationClick?.invoke() }
                            .testTag("capsule_notification_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Notifications,
                            contentDescription = "الإشعارات",
                            tint = primaryTextColor.copy(alpha = 0.85f),
                            modifier = Modifier.size(20.dp)
                        )

                        if (userProfile.notificationCount > 0) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .offset(x = (-2).dp, y = 2.dp)
                                    .size(15.dp)
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
