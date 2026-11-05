package com.example.ui.screens.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicNone
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun VoiceInputBar(
    inputPrompt: String,
    onPromptChange: (String) -> Unit,
    isVoiceActive: Boolean,
    isGenerating: Boolean,
    isEnglish: Boolean = false,
    userPrimaryColor: Color,
    onToggleVoice: () -> Unit,
    onSendPrompt: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 8.dp,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Voice Mic button
            IconButton(
                onClick = onToggleVoice,
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(if (isVoiceActive) userPrimaryColor else MaterialTheme.colorScheme.surfaceVariant)
                    .testTag("voice_chat_button")
            ) {
                Icon(
                    imageVector = if (isVoiceActive) Icons.Default.Mic else Icons.Default.MicNone,
                    contentDescription = if (isEnglish) "Voice" else "صوت",
                    tint = if (isVoiceActive) Color.White else MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(22.dp)
                )
            }

            // Input TextField
            TextField(
                value = inputPrompt,
                onValueChange = onPromptChange,
                placeholder = {
                    Text(
                        text = if (isEnglish) "Ask anything..." else "اكتب سؤالك أو طلبك...",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                modifier = Modifier
                    .weight(1f)
                    .testTag("home_chat_input"),
                shape = RoundedCornerShape(24.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                maxLines = 3
            )

            // Send button
            IconButton(
                onClick = {
                    if (inputPrompt.isNotBlank()) {
                        onSendPrompt(inputPrompt)
                    }
                },
                enabled = inputPrompt.isNotBlank() && !isGenerating,
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(
                        if (inputPrompt.isNotBlank()) userPrimaryColor
                        else MaterialTheme.colorScheme.surfaceVariant
                    )
                    .testTag("home_send_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = if (isEnglish) "Send" else "إرسال",
                    tint = if (inputPrompt.isNotBlank()) Color.White else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
