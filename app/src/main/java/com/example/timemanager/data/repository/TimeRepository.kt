package com.example.timemanager.data.repository

import com.example.timemanager.data.database.RecurringActivity
import com.example.timemanager.data.database.RecurringActivityDao
import com.example.timemanager.data.database.Routine
import com.example.timemanager.data.database.RoutineDao
import com.example.timemanager.data.database.Task
import com.example.timemanager.data.database.TaskDao
import com.example.timemanager.data.database.TimeLog
import com.example.timemanager.data.database.TimeLogDao
import kotlinx.coroutines.flow.Flow

class TimeRepository(
    private val taskDao: TaskDao,
    private val timeLogDao: TimeLogDao,
    private val routineDao: RoutineDao,
    private val recurringActivityDao: RecurringActivityDao
) {
    val allTasks: Flow<List<Task>> = taskDao.getAllTasksFlow()
    val allLogs: Flow<List<TimeLog>> = timeLogDao.getAllLogsFlow()
    val allRoutines: Flow<List<Routine>> = routineDao.getAllRoutinesFlow()
    val allRecurringActivities: Flow<List<RecurringActivity>> = recurringActivityDao.getAllRecurringActivitiesFlow()

    suspend fun getTaskById(id: Long): Task? = taskDao.getTaskById(id)
    suspend fun insertTask(task: Task): Long = taskDao.insertTask(task)
    suspend fun updateTask(task: Task) = taskDao.updateTask(task)
    suspend fun deleteTask(task: Task) = taskDao.deleteTask(task)

    suspend fun insertLog(log: TimeLog): Long = timeLogDao.insertLog(log)
    suspend fun deleteLog(log: TimeLog) = timeLogDao.deleteLog(log)
    suspend fun getLogsForTask(taskId: Long): List<TimeLog> = timeLogDao.getLogsForTask(taskId)
    fun getLogsBetween(start: Long, end: Long): Flow<List<TimeLog>> = timeLogDao.getLogsBetween(start, end)

    // Routines
    suspend fun insertRoutine(routine: Routine) = routineDao.insertRoutine(routine)
    suspend fun updateRoutine(routine: Routine) = routineDao.updateRoutine(routine)
    suspend fun deleteRoutine(routine: Routine) = routineDao.deleteRoutine(routine)

    // Recurring Activities
    suspend fun insertRecurringActivity(activity: RecurringActivity) = recurringActivityDao.insertActivity(activity)
    suspend fun deleteRecurringActivity(activity: RecurringActivity) = recurringActivityDao.deleteActivity(activity)
}
