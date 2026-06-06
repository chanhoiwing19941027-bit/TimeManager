package com.example.timemanager.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.AvTimer
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.example.timemanager.ui.components.GlassCard
import com.example.timemanager.ui.components.TaskItem
import com.example.timemanager.ui.viewmodel.MainViewModel
import androidx.compose.material.icons.filled.Settings
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DashboardScreen(
    viewModel: MainViewModel,
    onNavigateToTimer: () -> Unit,
    onNavigateToTasks: () -> Unit,
    onNavigateToModes: () -> Unit,
    onNavigateToPreferences: () -> Unit
) {
    val totalMins by viewModel.totalTrackedMinutes.collectAsState()
    val tasks by viewModel.tasks.collectAsState()
    val activeTask by viewModel.activeTask.collectAsState()

    val todayDateStr = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()) }
    val todayTasks = tasks.filter { it.scheduledDate == todayDateStr || (it.scheduledDate == null && !it.isCompleted) }
        .sortedBy { it.scheduledTime ?: "99:99" }

    val activeTasksCount = tasks.count { !it.isCompleted }
    val completedTasksCount = tasks.count { it.isCompleted }

    var showManualLogDialog by remember { mutableStateOf(false) }

    val currentDate = remember {
        SimpleDateFormat("yyyy年MM月dd日 EEEE", Locale.CHINESE).format(Date())
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.padding(vertical = 12.dp)) {
                        Text(
                            text = "今日專注",
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = currentDate,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = onNavigateToPreferences) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Preferences",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                
                // Mode Entry Card
                Card(
                    onClick = onNavigateToModes,
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "挑戰精英的一天？",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "探索馬斯克或深度工作模式 ➔",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Stats row (Focus Time, Tasks Progress)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Total tracked minutes
                    GlassCard(modifier = Modifier.weight(1f)) {
                        Column {
                            Icon(
                                imageVector = Icons.Default.Schedule,
                                contentDescription = "Focus Time",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "累計時間",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = String.format(Locale.getDefault(), "%.1f 分鐘", totalMins),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Completed tasks
                    GlassCard(modifier = Modifier.weight(1f)) {
                        Column {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Tasks",
                                tint = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "已完成任務",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "$completedTasksCount / ${tasks.size}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Active / Running Timer State card
            item {
                Text(
                    text = "當前專注任務",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                if (activeTask != null) {
                    val catObj = Category.values().find { it.name == activeTask!!.category } ?: Category.WORK
                    val catColor = Color(android.graphics.Color.parseColor(catObj.colorHex))
                    GlassCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onNavigateToTimer() }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = activeTask!!.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .background(catColor.copy(alpha = 0.2f), RoundedCornerShape(4.0.dp))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = catObj.label,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = catColor,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "目標: ${activeTask!!.targetDurationMinutes} 分鐘",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            Button(
                                onClick = onNavigateToTimer,
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = "Start Timer"
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("開始計時")
                            }
                        }
                    }
                } else {
                    GlassCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onNavigateToTasks() }
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ListAlt,
                                contentDescription = "No active task",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                modifier = Modifier.size(36.0.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "今日尚無進行中的任務",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "點擊前往任務清單設定目標 ➔",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Quick Actions header
            item {
                Text(
                    text = "快速操作",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                GlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showManualLogDialog = true }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.AvTimer,
                            contentDescription = "Quick log",
                            tint = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier.size(28.0.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "手動記錄專注時間",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "快速補登您的時間花費",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Text(
                            text = "+ 新增",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.tertiary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Timetable / Daily Schedule
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "今日行程 (Timetable)",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            if (todayTasks.isEmpty()) {
                item {
                    Text(
                        text = "今日暫無排程任務",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                }
            } else {
                items(todayTasks) { task ->
                    TaskItem(
                        task = task,
                        onToggleComplete = { viewModel.toggleTaskComplete(task) },
                        onDelete = { viewModel.deleteTask(task) },
                        onStartTracking = {
                            viewModel.selectTaskForTimer(task)
                            onNavigateToTimer()
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }

        // Floating button to go directly to timer
        FloatingActionButton(
            onClick = onNavigateToTimer,
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = Color.White,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
        ) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = "Go to Timer",
                modifier = Modifier.size(28.0.dp)
            )
        }
    }

    // Manual Time Logger Dialog
    if (showManualLogDialog) {
        ManualLogDialog(
            onDismiss = { showManualLogDialog = false },
            onConfirm = { category, minutes, note ->
                viewModel.logTimeManually(category, minutes, note)
                showManualLogDialog = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManualLogDialog(
    onDismiss: () -> Unit,
    onConfirm: (Category, Int, String) -> Unit
) {
    var selectedCategory by remember { mutableStateOf(Category.WORK) }
    var durationText by remember { mutableStateOf("25") }
    var noteText by remember { mutableStateOf("") }
    var dropdownExpanded by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.0.dp),
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
                    text = "手動記錄專注時間",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                // Category dropdown selector
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

                // Duration text field
                OutlinedTextField(
                    value = durationText,
                    onValueChange = { durationText = it.filter { char -> char.isDigit() } },
                    label = { Text("時間（分鐘）") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = MaterialTheme.colorScheme.onBackground,
                        unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    )
                )

                // Note text field
                OutlinedTextField(
                    value = noteText,
                    onValueChange = { noteText = it },
                    label = { Text("備註 (可選)") },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("例如：閱讀書籍、專案開發...") },
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = MaterialTheme.colorScheme.onBackground,
                        unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    )
                )

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
                            val duration = durationText.toIntOrNull() ?: 25
                            onConfirm(selectedCategory, duration, noteText)
                        },
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("確認儲存")
                    }
                }
            }
        }
    }
}
