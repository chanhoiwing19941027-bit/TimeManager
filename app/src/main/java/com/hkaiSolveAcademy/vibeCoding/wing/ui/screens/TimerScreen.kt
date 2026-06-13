package com.hkaiSolveAcademy.vibeCoding.wing.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.hkaiSolveAcademy.vibeCoding.wing.data.database.Category
import com.hkaiSolveAcademy.vibeCoding.wing.ui.components.CircularTimer
import com.hkaiSolveAcademy.vibeCoding.wing.ui.components.GlassCard
import com.hkaiSolveAcademy.vibeCoding.wing.ui.viewmodel.MainViewModel
import com.hkaiSolveAcademy.vibeCoding.wing.ui.viewmodel.TimerMode
import java.util.Locale

@Composable
fun TimerScreen(
    viewModel: MainViewModel,
    onNavigateToTasks: () -> Unit
) {
    val isRunning by viewModel.isTimerRunning.collectAsState()
    val timerMode by viewModel.timerMode.collectAsState()
    val timeLeft by viewModel.timeLeftSeconds.collectAsState()
    val totalDuration by viewModel.totalDurationSeconds.collectAsState()
    val activeTask by viewModel.activeTask.collectAsState()

    var showSaveStopwatchDialog by remember { mutableStateOf(false) }

    // Progress calculation helper
    val progress = remember(timeLeft, totalDuration, timerMode) {
        if (timerMode == TimerMode.STOPWATCH) {
            // In stopwatch mode, progress is arbitrary or 1.0
            1.0f
        } else {
            if (totalDuration > 0) timeLeft.toFloat() / totalDuration.toFloat() else 0f
        }
    }

    // Timer string formatter
    val formattedTime = remember(timeLeft, timerMode) {
        if (timerMode == TimerMode.STOPWATCH) {
            val h = timeLeft / 3600
            val m = (timeLeft % 3600) / 60
            val s = timeLeft % 60
            if (h > 0) {
                String.format(Locale.getDefault(), "%02d:%02d:%02d", h, m, s)
            } else {
                String.format(Locale.getDefault(), "%02d:%02d", m, s)
            }
        } else {
            val m = timeLeft / 60
            val s = timeLeft % 60
            String.format(Locale.getDefault(), "%02d:%02d", m, s)
        }
    }

    // Accent color determined by active category or timer mode
    val accentColor = remember(activeTask, timerMode) {
        if (timerMode == TimerMode.POMODORO_BREAK) {
            Color(0xFFEC4899) // Pink for break mode
        } else if (activeTask != null) {
            val catObj = Category.values().find { it.name == activeTask!!.category } ?: Category.WORK
            Color(android.graphics.Color.parseColor(catObj.colorHex))
        } else {
            Color(0xFF6366F1) // Indigo default
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Tab Row Mode Selector
        TabRow(
            selectedTabIndex = timerMode.ordinal,
            containerColor = Color.Transparent,
            divider = {},
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[timerMode.ordinal]),
                    color = accentColor
                )
            },
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
        ) {
            TimerMode.values().forEach { mode ->
                Tab(
                    selected = timerMode == mode,
                    onClick = { viewModel.setTimerMode(mode) },
                    text = {
                        Text(
                            text = when (mode) {
                                TimerMode.POMODORO_FOCUS -> "番茄專注"
                                TimerMode.POMODORO_BREAK -> "休息間隔"
                                TimerMode.STOPWATCH -> "秒錶追蹤"
                            },
                            fontWeight = if (timerMode == mode) FontWeight.Bold else FontWeight.Normal,
                            color = if (timerMode == mode) accentColor else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Active Task Card / Select prompt
        AnimatedVisibility(visible = timerMode != TimerMode.POMODORO_BREAK) {
            if (activeTask != null) {
                val catObj = Category.values().find { it.name == activeTask!!.category } ?: Category.WORK
                val catColor = Color(android.graphics.Color.parseColor(catObj.colorHex))
                GlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = activeTask!!.title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .background(catColor.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
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
                        IconButton(onClick = { viewModel.selectTaskForTimer(null) }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Deselect task",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                GlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToTasks() }
                        .padding(horizontal = 8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Assignment,
                            contentDescription = "Link task",
                            tint = accentColor,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "連結至具體任務以追蹤目標 ➔",
                            style = MaterialTheme.typography.bodyMedium,
                            color = accentColor,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Center Circular Timer Canvas
        CircularTimer(
            progress = progress,
            timeString = formattedTime,
            statusLabel = when (timerMode) {
                TimerMode.POMODORO_FOCUS -> "Focus"
                TimerMode.POMODORO_BREAK -> "Break"
                TimerMode.STOPWATCH -> "Track"
            },
            accentColor = accentColor,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Bottom Timer Controls Panel
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Reset / Stop button
            IconButton(
                onClick = { viewModel.resetTimer() },
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Reset Timer",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.width(24.dp))

            // Play / Pause button (Big button in the middle)
            IconButton(
                onClick = {
                    if (isRunning) viewModel.pauseTimer() else viewModel.startTimer()
                },
                modifier = Modifier
                    .size(76.dp)
                    .clip(CircleShape)
                    .background(accentColor)
            ) {
                Icon(
                    imageVector = if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isRunning) "Pause" else "Play",
                    tint = Color.White,
                    modifier = Modifier.size(40.dp)
                )
            }

            // Save session for Stopwatch mode
            if (timerMode == TimerMode.STOPWATCH) {
                Spacer(modifier = Modifier.width(24.dp))

                IconButton(
                    onClick = { showSaveStopwatchDialog = true },
                    enabled = timeLeft > 0,
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(if (timeLeft > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Icon(
                        imageVector = Icons.Default.Save,
                        contentDescription = "Save stopwatch tracking",
                        tint = if (timeLeft > 0) Color.White else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            } else {
                Spacer(modifier = Modifier.width(80.dp)) // Space balance spacer
            }
        }
    }

    // Save Stopwatch Log Dialog
    if (showSaveStopwatchDialog) {
        SaveStopwatchDialog(
            onDismiss = { showSaveStopwatchDialog = false },
            onConfirm = { note ->
                viewModel.saveStopwatchSession(note)
                showSaveStopwatchDialog = false
            }
        )
    }
}

@Composable
fun SaveStopwatchDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var note by remember { mutableStateOf("") }

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
                    text = "儲存計時工作階段",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "為此段時間記錄加上說明，方便往後分析與回顧。",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("記錄說明") },
                    placeholder = { Text("例如：研究演算法設計、撰寫文案...") },
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
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text("放棄記錄")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { onConfirm(note) },
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("確認儲存")
                    }
                }
            }
        }
    }
}
