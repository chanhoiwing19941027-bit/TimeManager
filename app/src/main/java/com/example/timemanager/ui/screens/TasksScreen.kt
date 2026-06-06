package com.example.timemanager.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.PlaylistAddCheck
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.timemanager.data.database.Category
import com.example.timemanager.data.database.Task
import com.example.timemanager.ui.components.TaskItem
import com.example.timemanager.ui.viewmodel.MainViewModel

@Composable
fun TasksScreen(
    viewModel: MainViewModel,
    onNavigateToTimer: () -> Unit
) {
    val tasks by viewModel.tasks.collectAsState()
    var showAddTaskDialog by remember { mutableStateOf(false) }

    val activeTasks = tasks.filter { !it.isCompleted }
    val completedTasks = tasks.filter { it.isCompleted }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Screen Header
            item {
                Column(modifier = Modifier.padding(vertical = 16.dp)) {
                    Text(
                        text = "任務清單",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "設定您的專注目標，開始計時追蹤",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Incomplete Tasks Section
            if (activeTasks.isNotEmpty()) {
                item {
                    Text(
                        text = "進行中",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
                items(activeTasks, key = { it.id }) { task ->
                    TaskItem(
                        task = task,
                        onToggleComplete = { viewModel.toggleTaskComplete(task) },
                        onDelete = { viewModel.deleteTask(task) },
                        onStartTracking = {
                            viewModel.selectTaskForTimer(task)
                            onNavigateToTimer()
                        }
                    )
                }
            }

            // Completed Tasks Section
            if (completedTasks.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "已完成",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
                items(completedTasks, key = { it.id }) { task ->
                    TaskItem(
                        task = task,
                        onToggleComplete = { viewModel.toggleTaskComplete(task) },
                        onDelete = { viewModel.deleteTask(task) },
                        onStartTracking = {}
                    )
                }
            }

            // Empty state
            if (tasks.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 100.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlaylistAddCheck,
                            contentDescription = "No tasks",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.size(72.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "目前沒有任何任務",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "點擊右下角按鈕，新增第一個任務！",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                        )
                    }
                }
            }

            // Bottom space for floating action button offset
            item {
                Spacer(modifier = Modifier.height(88.dp))
            }
        }

        // Add Task Floating Action Button
        FloatingActionButton(
            onClick = { showAddTaskDialog = true },
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = Color.White,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add Task",
                modifier = Modifier.size(28.dp)
            )
        }
    }

    // Add Task Dialog Form
    if (showAddTaskDialog) {
        AddTaskDialog(
            onDismiss = { showAddTaskDialog = false },
            onConfirm = { title, desc, category, duration, date, time ->
                viewModel.addTask(title, desc, category, duration, date, time)
                showAddTaskDialog = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, Category, Int, String?, String?) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(Category.WORK) }
    var durationText by remember { mutableStateOf("25") }
    var scheduledDate by remember { mutableStateOf("") }
    var scheduledTime by remember { mutableStateOf("") }
    var dropdownExpanded by remember { mutableStateOf(false) }

    var isTitleError by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "新增專注任務",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                // Title field
                OutlinedTextField(
                    value = title,
                    onValueChange = {
                        title = it
                        if (it.isNotEmpty()) isTitleError = false
                    },
                    label = { Text("任務名稱") },
                    isError = isTitleError,
                    placeholder = { Text("例如：讀程式書、運動30分鐘...") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = MaterialTheme.colorScheme.onBackground,
                        unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    )
                )

                // Category selector
                ExposedDropdownMenuBox(
                    expanded = dropdownExpanded,
                    onExpandedChange = { dropdownExpanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedCategory.label,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("分類") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            focusedTextColor = MaterialTheme.colorScheme.onBackground,
                            unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = dropdownExpanded,
                        onDismissRequest = { dropdownExpanded = false }
                    ) {
                        Category.values().forEach { category ->
                            DropdownMenuItem(
                                text = { Text(category.label) },
                                onClick = {
                                    selectedCategory = category
                                    dropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                // Target duration (minutes)
                OutlinedTextField(
                    value = durationText,
                    onValueChange = { durationText = it.filter { char -> char.isDigit() } },
                    label = { Text("目標時間（分鐘）") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = MaterialTheme.colorScheme.onBackground,
                        unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    )
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = scheduledDate,
                        onValueChange = { scheduledDate = it },
                        label = { Text("日期 (YYYY-MM-DD)") },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("2024-01-01") },
                        colors = TextFieldDefaults.colors(
                            focusedTextColor = MaterialTheme.colorScheme.onBackground,
                            unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent
                        )
                    )
                    OutlinedTextField(
                        value = scheduledTime,
                        onValueChange = { scheduledTime = it },
                        label = { Text("時間 (HH:mm)") },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("14:30") },
                        colors = TextFieldDefaults.colors(
                            focusedTextColor = MaterialTheme.colorScheme.onBackground,
                            unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent
                        )
                    )
                }

                // Action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text("取消")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (title.trim().isEmpty()) {
                                isTitleError = true
                            } else {
                                val duration = durationText.toIntOrNull() ?: 25
                                onConfirm(
                                    title,
                                    desc,
                                    selectedCategory,
                                    duration,
                                    scheduledDate.ifEmpty { null },
                                    scheduledTime.ifEmpty { null }
                                )
                            }
                        },
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("建立任務")
                    }
                }
            }
        }
    }
}
