package com.example.timemanager

import android.app.Application
import com.example.timemanager.data.database.AppDatabase
import com.example.timemanager.data.repository.TimeRepository

class TimeManagerApplication : Application() {
    val database by lazy { AppDatabase.getDatabase(this) }
    val repository by lazy {
        TimeRepository(
            database.taskDao(),
            database.timeLogDao(),
            database.routineDao(),
            database.recurringActivityDao()
        )
    }
}
