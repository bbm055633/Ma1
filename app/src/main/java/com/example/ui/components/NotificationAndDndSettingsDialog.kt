package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
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
import androidx.compose.ui.window.Dialog

@Composable
fun NotificationAndDndSettingsDialog(
    currentSound: String,
    onSoundSelected: (String) -> Unit,
    isDndEnabled: Boolean,
    onDndToggled: (Boolean) -> Unit,
    selectedPrayers: Set<String>,
    onPrayerToggled: (String) -> Unit,
    dndDuration: Int,
    onDurationChanged: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    val soundOptions = listOf(
        Triple("default", "صوت الإشعار الافتراضي للنظام", "System Default Chime"),
        Triple("adhan", "صوت الأذان الهادئ المهدئ", "Gentle Adhan Chime"),
        Triple("soft_chime", "رنين ناعم ومتناسق", "Soft Harmonic Chime"),
        Triple("enterprise_bell", "جرس مؤسسي رسمي", "Official Enterprise Bell")
    )

    val prayerList = listOf("الفجر", "الظهر", "العصر", "المغرب", "العشاء")
    val durationOptions = listOf(15, 20, 30, 45, 60)

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            shadowElevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(16.dp)
                .testTag("notification_dnd_dialog")
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFF0F766E).copy(alpha = 0.12f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.NotificationsActive,
                                    contentDescription = "الإشعارات وعدم الإزعاج",
                                    tint = Color(0xFF0F766E),
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = "إعدادات التنبيهات وعدم الإزعاج",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1C1B1A)
                                )
                                Text(
                                    text = "تخصيص الأصوات ووضع الصامت التلقائي للعبادات",
                                    fontSize = 12.sp,
                                    color = Color(0xFF6E685F)
                                )
                            }
                        }
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier.testTag("close_dialog_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "إغلاق",
                                tint = Color(0xFF6E685F)
                            )
                        }
                    }
                }

                item {
                    HorizontalDivider(color = Color(0xFFE5E7EB), thickness = 1.dp)
                }

                // Section 1: Notification Sounds
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "🔊 تخصيص صوت تنبيهات الصلاة والمهام",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F766E)
                        )
                        soundOptions.forEach { (key, labelAr, labelEn) ->
                            val isSelected = currentSound == key
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) Color(0xFF0F766E).copy(alpha = 0.08f) else Color(0xFFF9F6F0),
                                border = BorderStroke(
                                    1.dp,
                                    if (isSelected) Color(0xFF0F766E) else Color(0xFFE5E7EB)
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { onSoundSelected(key) }
                                    .testTag("sound_option_$key")
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = labelAr,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = Color(0xFF1C1B1A)
                                        )
                                        Text(
                                            text = labelEn,
                                            fontSize = 11.sp,
                                            color = Color(0xFF6E685F)
                                        )
                                    }
                                    RadioButton(
                                        selected = isSelected,
                                        onClick = { onSoundSelected(key) },
                                        colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF0F766E))
                                    )
                                }
                            }
                        }
                    }
                }

                item {
                    HorizontalDivider(color = Color(0xFFE5E7EB), thickness = 1.dp)
                }

                // Section 2: Do Not Disturb Mode during worship
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "🔕 وضع عدم الإزعاج أثناء العبادات",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0F766E)
                                )
                                Text(
                                    text = "تحويل الهاتف إلى وضع الصامت/الاهتزاز تلقائياً عند دخول أوقات الصلاة",
                                    fontSize = 11.sp,
                                    color = Color(0xFF6E685F)
                                )
                            }
                            Switch(
                                checked = isDndEnabled,
                                onCheckedChange = onDndToggled,
                                colors = SwitchDefaults.colors(checkedTrackColor = Color(0xFF0F766E)),
                                modifier = Modifier.testTag("dnd_switch")
                            )
                        }

                        if (isDndEnabled) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "تحديد الصلوات المشمولة بالوضع الصامت:",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF374151)
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                prayerList.forEach { prayer ->
                                    val isChecked = selectedPrayers.contains(prayer)
                                    FilterChip(
                                        selected = isChecked,
                                        onClick = { onPrayerToggled(prayer) },
                                        label = { Text(prayer, fontSize = 11.sp) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = Color(0xFF0F766E),
                                            selectedLabelColor = Color.White
                                        ),
                                        modifier = Modifier.testTag("dnd_prayer_chip_$prayer")
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "مدة وضع الصامت التلقائي بعد الأذان (بالدقائق):",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF374151)
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                durationOptions.forEach { minutes ->
                                    val isSelected = dndDuration == minutes
                                    Button(
                                        onClick = { onDurationChanged(minutes) },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (isSelected) Color(0xFF0F766E) else Color(0xFFF1EFE9),
                                            contentColor = if (isSelected) Color.White else Color(0xFF374151)
                                        ),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(36.dp)
                                            .testTag("dnd_duration_$minutes")
                                    ) {
                                        Text("${minutes}د", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }

                // Footer Button
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F766E)),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("save_notification_settings_button")
                    ) {
                        Text(
                            text = "حفظ الإعدادات وتطبيقها ✅",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}
