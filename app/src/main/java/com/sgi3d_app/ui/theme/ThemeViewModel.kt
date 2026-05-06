package com.sgi3d_app.ui.theme

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class ThemeViewModel : ViewModel() {

    var isDarkMode = mutableStateOf(false)
        private set

    fun toggleTheme() {
        isDarkMode.value = !isDarkMode.value
    }

    fun setDarkMode(value: Boolean) {
        isDarkMode.value = value
    }
}