package com.example.modul17.ui.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun TasksScreen(
    modifier: Modifier = Modifier,
    viewModel: TasksViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    TasksContent(
        state = state,
        onLoad = { viewModel.loadData() },
        onLoadWithError = { viewModel.loadData(shouldFail = true) },
        onCancel = viewModel::cancelLoading,
        onTap = viewModel::onTap,
        onBlock = viewModel::blockMainThread,
        onSort = viewModel::sortTasks,
        modifier = modifier
    )
}

@Composable
fun TasksContent(
    state: TasksUiState,
    onLoad: () -> Unit,
    onLoadWithError: () -> Unit,
    onCancel: () -> Unit,
    onTap: () -> Unit,
    onBlock: () -> Unit,
    onSort: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Coroutine Demo", style = MaterialTheme.typography.headlineSmall)

        // Row 1: Load, Load (fail), Cancel
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = onLoad, enabled = !state.isLoading) {
                Text("Load")
            }
            OutlinedButton(
                onClick = onLoadWithError,
                enabled = !state.isLoading
            ) {
                Text("Load (fail)")
            }
            OutlinedButton(onClick = onCancel, enabled = state.isLoading) {
                Text("Cancel")
            }
        }

        // Row 2: Tap me, Block UI, and Sort
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = onTap) {
                Text("Tap me: ${state.tapCount}")
            }
            OutlinedButton(onClick = onBlock) {
                Text("Block UI")
            }
            OutlinedButton(
                onClick = onSort,
                enabled = state.tasks.isNotEmpty() && !state.isLoading
            ) {
                Text("Sort")
            }
        }

        // Modifikasi: LinearProgressIndicator dengan persentase 0% - 100%
        if (state.isLoading) {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                LinearProgressIndicator(
                    progress = { state.progress },
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = "Mengunduh data: ${(state.progress * 100).toInt()}%",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        // Pesan error dengan tombol Retry
        state.errorMessage?.let { message ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = message,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.weight(1f)
                )
                Button(onClick = onLoad) {
                    Text("Retry")
                }
            }
        }

        // Announcement
        if (state.announcement.isNotEmpty()) {
            Text("Announcement: ${state.announcement}")
        }

        // Course Count
        state.courseCount?.let { count ->
            Text(
                "Total Courses: $count",
                style = MaterialTheme.typography.bodyMedium
            )
        }

        // Task List
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(state.tasks, key = { it.id }) { task ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            task.title,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            task.course,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}
