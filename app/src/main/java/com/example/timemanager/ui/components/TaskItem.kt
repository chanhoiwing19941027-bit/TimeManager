package com.example.timemanager.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.timemanager.data.database.Category
import com.example.timemanager.data.database.Task
import com.example.timemanager.ui.theme.Slate400

@Composable
fun TaskItem(
    modifier: Modifier = Modifier,
    task: Task,
    onToggleComplete: () -> Unit,
    onDelete: () -> Unit,
    onStartTracking: () -> Unit
) {
    val categoryObj = Category.values().find { it.name == task.category } ?: Category.WORK
    val categoryColor = Color(android.graphics.Color.parseColor(categoryObj.colorHex))

    GlassCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        cornerRadius = 12.dp
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 1. Completion Checkbox (Circle style)
            IconButton(
                onClick = onToggleComplete,
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
            ) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(if (task.isCompleted) categoryColor else Color.Transparent)
                        .border(2.dp, categoryColor, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    if (task.isCompleted) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Completed",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // 2. Task Text Content (Title & Description)
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = if (task.isCompleted) Slate400 else MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold,
                    textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 2.dp)
                ) {
                    // Category Badge
                    Box(
                        modifier = Modifier
                            .background(categoryColor.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = categoryObj.label,
                            style = MaterialTheme.typography.labelSmall,
                            color = categoryColor,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    // Target duration
                    Text(
                        text = "目標: ${task.targetDurationMinutes} 分鐘",
                        style = MaterialTheme.typography.bodySmall,
                        color = Slate400
                    )
                    if (task.scheduledTime != null) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "排程: ${task.scheduledTime}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // 3. Actions (Play if not completed, Delete)
            Row {
                if (!task.isCompleted) {
                    IconButton(onClick = onStartTracking) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Start Tracking",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }
}
