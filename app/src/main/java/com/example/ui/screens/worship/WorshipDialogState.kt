package com.example.ui.screens.worship

import androidx.compose.runtime.Stable

/**
 * Centralized Dialog State management for WorshipScreen.
 * Replaces individual boolean states with a single type-safe sealed class.
 */
@Stable
sealed class WorshipDialogState {
    object None : WorshipDialogState()
    object MorningAdhkar : WorshipDialogState()
    object EveningAdhkar : WorshipDialogState()
    object Qibla : WorshipDialogState()
    object Achievements : WorshipDialogState()
}
