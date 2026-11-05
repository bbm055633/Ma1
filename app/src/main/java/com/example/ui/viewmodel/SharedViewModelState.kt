package com.example.ui.viewmodel

import kotlinx.coroutines.flow.StateFlow

/**
 * Shared interface for inter-ViewModel state communication.
 */
sealed interface SharedViewModelState {
    val selectedDate: StateFlow<String>
    val isDarkMode: StateFlow<Boolean>
    val isEnglish: StateFlow<Boolean>
    val userName: StateFlow<String>
    val userEmail: StateFlow<String>
}
