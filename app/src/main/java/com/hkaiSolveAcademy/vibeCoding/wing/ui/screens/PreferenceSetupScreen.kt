package com.hkaiSolveAcademy.vibeCoding.wing.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.hkaiSolveAcademy.vibeCoding.wing.data.database.Category
import com.hkaiSolveAcademy.vibeCoding.wing.data.database.Routine
import com.hkaiSolveAcademy.vibeCoding.wing.ui.components.GlassCard
import com.hkaiSolveAcademy.vibeCoding.wing.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreferenceSetupScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val routines by viewModel.allRoutines.collectAsState()
    val recurringActivities by viewModel.allRecurringActivities.collectAsState()
    
    var showAddRoutineDialog by remember { mutableStateOf(false) }
    var showAddRecurringDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("智慧作息配置", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            if (selectedTab < 2) {
                FloatingActionButton(onClick = { 
                    if (selectedTab == 0) showAddRoutineDialog = true else showAddRecurringDialog = true 
                }) {
                    Icon(Icons.Default.Add, contentDescription = "Add")
                }
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            TabRow(selectedTabIndex = selectedTab) {
                Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text("每日固定") })
                Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text("每週重複") })
                Tab(selected = selectedTab == 2, onClick = { selectedTab = 2 }, text = { Text("Google 同步") })
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                when (selectedTab) {
                    0 -> {
                        item {
                            InfoCard("每日固定常規", "設定如起床、三餐、通勤等不動點，智慧排程會自動避開這些時段。")
                        }
                        items(routines) { routine ->
                            RoutineItem(routine = routine, onDelete = { viewModel.deleteRoutine(routine) })
                        }
                    }
                    1 -> {
                        item {
                            InfoCard("每週重複活動", "設定每週固定發出的活動（如：週二健身），系統會自動填充至對應日期。")
                        }
                        items(recurringActivities) { activity ->
                            RecurringActivityItem(activity = activity, onDelete = { viewModel.deleteRecurringActivity(activity) })
                        }
                    }
                    2 -> {
                        item {
                            GoogleSyncCard()
                        }
                    }
                }
            }
        }
    }

    if (showAddRoutineDialog) {
        AddRoutineDialog(
            onDismiss = { showAddRoutineDialog = false },
            onConfirm = { name, start, end, cat ->
                viewModel.addRoutine(name, start, end, cat)
                showAddRoutineDialog = false
            }
        )
    }

    if (showAddRecurringDialog) {
        AddRecurringDialog(
            onDismiss = { showAddRecurringDialog = false },
            onConfirm = { name, day, start, dur, cat ->
                viewModel.addRecurringActivity(name, day, start, dur, cat)
                showAddRecurringDialog = false
            }
        )
    }
}

@Composable
fun InfoCard(title: String, description: String) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f)),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Schedule, null, tint = MaterialTheme.colorScheme.secondary)
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(text = title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Text(text = description, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
fun RoutineItem(routine: Routine, onDelete: () -> Unit) {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(text = routine.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(
                    text = "${routine.startTime} - ${routine.endTime}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
fun RecurringActivityItem(activity: com.hkaiSolveAcademy.vibeCoding.wing.data.database.RecurringActivity, onDelete: () -> Unit) {
    val days = listOf("日", "一", "二", "三", "四", "五", "六")
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(text = activity.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(
                    text = "每週${days[activity.dayOfWeek - 1]} | ${activity.startTime} (${activity.durationMinutes} min)",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
fun GoogleSyncCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "Google 日曆同步", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "將您的工作會議自動同步至 App，排程系統會智慧避開衝突時段。",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { /* TODO: Google Auth */ }, shape = RoundedCornerShape(8.dp)) {
                Text("立即連接 Google 帳戶")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddRoutineDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, Category) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var startTime by remember { mutableStateOf("08:00") }
    var endTime by remember { mutableStateOf("09:00") }
    var selectedCategory by remember { mutableStateOf(Category.HEALTH) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(text = "新增固定常規", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("活動名稱 (例如: 早餐)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = startTime,
                        onValueChange = { startTime = it },
                        label = { Text("開始時間") },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("HH:mm") }
                    )
                    OutlinedTextField(
                        value = endTime,
                        onValueChange = { endTime = it },
                        label = { Text("結束時間") },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("HH:mm") }
                    )
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text("取消") }
                    Button(onClick = { onConfirm(name, startTime, endTime, selectedCategory) }) { Text("新增") }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddRecurringDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, Int, String, Int, Category) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var dayOfWeek by remember { mutableIntStateOf(2) } // Monday
    var startTime by remember { mutableStateOf("18:00") }
    var duration by remember { mutableStateOf("60") }
    
    Dialog(onDismissRequest = onDismiss) {
        Surface(shape = RoundedCornerShape(24.dp), color = MaterialTheme.colorScheme.surface) {
            Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(text = "新增每週重複活動", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("活動名稱") }, modifier = Modifier.fillMaxWidth())
                
                // Day selector simple version
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    (1..7).forEach { day ->
                        val isSelected = dayOfWeek == day
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
                                .clickable { dayOfWeek = day },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = listOf("日", "一", "二", "三", "四", "五", "六")[day - 1],
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = startTime, onValueChange = { startTime = it }, label = { Text("開始時間") }, modifier = Modifier.weight(1f))
                    OutlinedTextField(value = duration, onValueChange = { duration = it }, label = { Text("時長(分)") }, modifier = Modifier.weight(1f))
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text("取消") }
                    Button(onClick = { onConfirm(name, dayOfWeek, startTime, duration.toIntOrNull() ?: 60, Category.PERSONAL) }) { Text("新增") }
                }
            }
        }
    }
}
