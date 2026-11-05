package com.example.ui.viewmodel

sealed class ActiveScreen {
    object MainTabs : ActiveScreen()
    object Profile : ActiveScreen()
    object Worship : ActiveScreen()
    object AgentSettings : ActiveScreen()
    object PermissionsSecurity : ActiveScreen()
    object PaymentHistory : ActiveScreen()
    object MobeenAiSpecSheet : ActiveScreen()
    object FullScreenMenu : ActiveScreen()
    object StarryNightCustomizer : ActiveScreen()
    object Login : ActiveScreen()
    data class AgentChat(val agentId: String) : ActiveScreen()
    data class ProductGenerator(val productId: String) : ActiveScreen()
    data class AdhkarSession(val isMorning: Boolean) : ActiveScreen()
}
