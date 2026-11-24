package com.example.modul17.ui.main

import com.example.modul17.data.Task

data class TasksUiState(
    val isLoading: Boolean = false,
    val progress: Float = 0f, // Nilai progress 0.0f - 1.0f (0% - 100%)
    val tasks: List<Task> = emptyList(),
    val announcement: String = "",
    val courseCount: Int? = null,
    val errorMessage: String? = null,
    val tapCount: Int = 0
)
