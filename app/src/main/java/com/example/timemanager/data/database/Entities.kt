package com.example.timemanager.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class Category(val label: String, val colorHex: String) {
    WORK("工作", "#6366F1"),
    STUDY("學習", "#8B5CF6"),
    PERSONAL("個人", "#EC4899"),
    HEALTH("健康", "#10B981"),
    LEISURE("休閒", "#F59E0B")
}

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String,
    val category: String, // Matches Category enum name
    val targetDurationMinutes: Int,
    val isCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val scheduledDate: String? = null, // Format: YYYY-MM-DD
    val scheduledTime: String? = null,  // Format: HH:mm
    val isFromGoogleCalendar: Boolean = false // New: Flag for synced events
)

@Entity(tableName = "routines")
data class Routine(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val startTime: String, // HH:mm
    val endTime: String,   // HH:mm
    val category: String,
    val isEnabled: Boolean = true
)

@Entity(tableName = "recurring_activities")
data class RecurringActivity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val dayOfWeek: Int, // 1 (Sun) to 7 (Sat)
    val startTime: String, // HH:mm
    val durationMinutes: Int,
    val category: String
)

@Entity(tableName = "time_logs")
data class TimeLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val taskId: Long?, // Null if not associated with a specific task
    val category: String,
    val durationSeconds: Long,
    val timestamp: Long = System.currentTimeMillis(),
    val note: String = ""
)
