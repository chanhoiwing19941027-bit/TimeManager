package com.example.timemanager.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks ORDER BY createdAt DESC")
    fun getAllTasksFlow(): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE id = :id")
    suspend fun getTaskById(id: Long): Task?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task): Long

    @Update
    suspend fun updateTask(task: Task)

    @Delete
    suspend fun deleteTask(task: Task)
}

@Dao
interface RoutineDao {
    @Query("SELECT * FROM routines ORDER BY startTime ASC")
    fun getAllRoutinesFlow(): Flow<List<Routine>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoutine(routine: Routine)

    @Update
    suspend fun updateRoutine(routine: Routine)

    @Delete
    suspend fun deleteRoutine(routine: Routine)
}

@Dao
interface RecurringActivityDao {
    @Query("SELECT * FROM recurring_activities ORDER BY dayOfWeek, startTime ASC")
    fun getAllRecurringActivitiesFlow(): Flow<List<RecurringActivity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActivity(activity: RecurringActivity)

    @Delete
    suspend fun deleteActivity(activity: RecurringActivity)
}

@Dao
interface TimeLogDao {
    @Query("SELECT * FROM time_logs ORDER BY timestamp DESC")
    fun getAllLogsFlow(): Flow<List<TimeLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: TimeLog): Long

    @Delete
    suspend fun deleteLog(log: TimeLog)

    @Query("SELECT * FROM time_logs WHERE taskId = :taskId")
    suspend fun getLogsForTask(taskId: Long): List<TimeLog>

    @Query("SELECT * FROM time_logs WHERE timestamp >= :start AND timestamp <= :end ORDER BY timestamp DESC")
    fun getLogsBetween(start: Long, end: Long): Flow<List<TimeLog>>
}
