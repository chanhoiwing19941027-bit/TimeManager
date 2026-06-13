package com.hkaiSolveAcademy.vibeCoding.wing

import android.app.Application
import com.hkaiSolveAcademy.vibeCoding.wing.data.database.AppDatabase
import com.hkaiSolveAcademy.vibeCoding.wing.data.repository.TimeRepository

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
