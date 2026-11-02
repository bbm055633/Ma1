package com.example.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage
import com.example.ui.theme.LocalCardTransparency

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
 * ZheenFloatingCapsule - عناصر عائمة في الزاوية العليا اليمنى (صورة المستخدم وزر الجرس بجانب بعضهما)
 */
@Composable
fun ZheenFloatingCapsule(
    screenTitle: String,
    isCollapsed: Boolean = false,
    modifier: Modifier = Modifier
) {
    val userProfile = LocalUserProfile.current
    val isDarkMode = MaterialTheme.colorScheme.surface.luminance() < 0.5f
    var showNotificationPopup by remember { mutableStateOf(false) }

    val primaryTextColor = if (isDarkMode) Color.White else Color(0xFF1A1A1A)
    val accentColor = MaterialTheme.colorScheme.primary

    // تلاشي صورة المستخدم تدريجياً عند التمرير (isCollapsed)
    val avatarAlpha by animateFloatAsState(
        targetValue = if (isCollapsed) 0f else 1f,
        animationSpec = tween(250),
        label = "avatar_alpha"
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

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .testTag("zheen_floating_capsule_wrapper")
    ) {
        val maxWidth = maxWidth
        val avatarSize = 44.dp
        val bellSize = 36.dp
        val spacing = 8.dp

        val targetBellX = if (isCollapsed) 0.dp else (maxWidth - bellSize)
        val targetBellY = if (isCollapsed) (avatarSize + spacing) else 0.dp

        val bellOffsetX by animateDpAsState(
            targetValue = targetBellX,
            animationSpec = tween(300),
            label = "bell_x"
        )
        val bellOffsetY by animateDpAsState(
            targetValue = targetBellY,
            animationSpec = tween(300),
            label = "bell_y"
        )

        val containerHeight = if (isCollapsed) (avatarSize + spacing + bellSize) else maxOf(avatarSize, bellSize)

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(containerHeight)
                .zIndex(10f)
                .testTag("zheen_floating_capsule")
        ) {
            // 1. صورة المستخدم في الجهة اليسرى من الأعلى
            Box(
                modifier = Modifier
                    .offset(x = 0.dp, y = 0.dp)
                    .size(avatarSize)
                    .alpha(avatarAlpha)
                    .shadow(4.dp, CircleShape)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                    .border(BorderStroke(1.5.dp, Color.White), CircleShape)
                    .clickable(enabled = avatarAlpha > 0.1f) { userProfile.onAvatarClick?.invoke() }
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
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = accentColor
                    )
                }
            }

            // 2. زر الجرس في الجهة اليمنى بالوضع الكامل، وينتقل إلى أسفل الصورة في العمود الأيسر عند التقلص
            Box(
                modifier = Modifier
                    .offset(x = bellOffsetX, y = bellOffsetY)
                    .size(bellSize)
                    .shadow(4.dp, CircleShape)
                    .clip(CircleShape)
                    .background(if (isDarkMode) Color(0xFF282828) else Color(0xFFF1EFEA))
                    .border(BorderStroke(1.dp, Color.White.copy(alpha = 0.5f)), CircleShape)
                    .clickable { showNotificationPopup = true }
                    .testTag("capsule_notification_button"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Notifications,
                    contentDescription = "الإشعارات",
                    tint = primaryTextColor.copy(alpha = 0.95f),
                    modifier = Modifier.size(20.dp)
                )

                if (userProfile.notificationCount > 0) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = 2.dp, y = (-2).dp)
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
