package com.example.ui.viewmodel

data class UiChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val text: String,
    val isUser: Boolean,
    val timestamp: String = java.text.SimpleDateFormat("hh:mm a", java.util.Locale.US).format(java.util.Date())
)
