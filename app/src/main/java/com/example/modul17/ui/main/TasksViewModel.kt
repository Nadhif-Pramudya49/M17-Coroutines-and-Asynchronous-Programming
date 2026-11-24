package com.example.modul17.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.modul17.data.TaskRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

class TasksViewModel(
    private val repository: TaskRepository = TaskRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(TasksUiState())
    val uiState: StateFlow<TasksUiState> = _uiState.asStateFlow()

    private var loadJob: Job? = null

    fun loadData(shouldFail: Boolean = false) {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    errorMessage = null,
                    progress = 0f
                )
            }
            try {
                coroutineScope {
                    // Coroutine untuk mengalirkan progress 1% sampai 100% secara berkala (20ms * 100 = 2000ms)
                    val progressJob = launch {
                        for (p in 1..100) {
                            delay(20L)
                            _uiState.update { it.copy(progress = p / 100f) }
                        }
                    }

                    val tasks = async { repository.getTasks(shouldFail) }
                    val announcement = async { repository.getAnnouncement() }
                    val courseCount = async { repository.getCourseCount() }

                    val loadedTasks = tasks.await()
                    val loadedAnnouncement = announcement.await()
                    val loadedCourseCount = courseCount.await()
                    progressJob.join()

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            progress = 1f,
                            tasks = loadedTasks,
                            announcement = loadedAnnouncement,
                            courseCount = loadedCourseCount
                        )
                    }
                }
            } catch (e: IOException) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        progress = 0f,
                        errorMessage = "Failed to load: ${e.message}"
                    )
                }
            }
        }
    }

    fun cancelLoading() {
        loadJob?.cancel()
        _uiState.update {
            it.copy(
                isLoading = false,
                progress = 0f,
                errorMessage = "Loading cancelled"
            )
        }
    }

    fun onTap() {
        _uiState.update { it.copy(tapCount = it.tapCount + 1) }
    }

    fun blockMainThread() {
        Thread.sleep(3000L)
    }

    fun sortTasks() {
        viewModelScope.launch {
            val currentTasks = _uiState.value.tasks
            if (currentTasks.isNotEmpty()) {
                val sorted = withContext(Dispatchers.Default) {
                    currentTasks.sortedBy { it.title }
                }
                _uiState.update { it.copy(tasks = sorted) }
            }
        }
    }
}
