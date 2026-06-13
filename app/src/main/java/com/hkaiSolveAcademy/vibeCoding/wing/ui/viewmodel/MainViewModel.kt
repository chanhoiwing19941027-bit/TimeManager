package com.hkaiSolveAcademy.vibeCoding.wing.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.hkaiSolveAcademy.vibeCoding.wing.data.database.Category
import com.hkaiSolveAcademy.vibeCoding.wing.data.database.RecurringActivity
import com.hkaiSolveAcademy.vibeCoding.wing.data.database.Routine
import com.hkaiSolveAcademy.vibeCoding.wing.data.database.Task
import com.hkaiSolveAcademy.vibeCoding.wing.data.database.TemplateRegistry
import com.hkaiSolveAcademy.vibeCoding.wing.data.database.TimeLog
import com.hkaiSolveAcademy.vibeCoding.wing.data.repository.TimeRepository
import com.hkaiSolveAcademy.vibeCoding.wing.ui.components.ChartSegment
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

enum class TimerMode(val label: String) {
    POMODORO_FOCUS("專注中"),
    POMODORO_BREAK("休息時間"),
    STOPWATCH("計時中")
}

class MainViewModel(private val repository: TimeRepository) : ViewModel() {

    // 1. Database Flows
    val tasks: StateFlow<List<Task>> = repository.allTasks.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val logs: StateFlow<List<TimeLog>> = repository.allLogs.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val allRoutines: StateFlow<List<Routine>> = repository.allRoutines.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val allRecurringActivities: StateFlow<List<RecurringActivity>> = repository.allRecurringActivities.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // 2. Active Timer State
    private val _isTimerRunning = MutableStateFlow(false)
    val isTimerRunning: StateFlow<Boolean> = _isTimerRunning.asStateFlow()

    private val _timerMode = MutableStateFlow(TimerMode.POMODORO_FOCUS)
    val timerMode: StateFlow<TimerMode> = _timerMode.asStateFlow()

    private val _timeLeftSeconds = MutableStateFlow(25 * 60L) // Default 25m Focus
    val timeLeftSeconds: StateFlow<Long> = _timeLeftSeconds.asStateFlow()

    private val _totalDurationSeconds = MutableStateFlow(25 * 60L)
    val totalDurationSeconds: StateFlow<Long> = _totalDurationSeconds.asStateFlow()

    private val _activeTask = MutableStateFlow<Task?>(null)
    val activeTask: StateFlow<Task?> = _activeTask.asStateFlow()

    private var timerJob: Job? = null

    // 3. Analytics state flows computed reactively
    val totalTrackedMinutes: StateFlow<Float> = logs.map { logList ->
        logList.sumOf { it.durationSeconds }.toFloat() / 60f
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0f)

    val categorySegments: StateFlow<List<ChartSegment>> = logs.map { logList ->
        val totalSecs = logList.sumOf { it.durationSeconds }.toFloat()
        if (totalSecs == 0f) return@map emptyList()

        Category.values().map { category ->
            val catSecs = logList.filter { it.category == category.name }.sumOf { it.durationSeconds }
            ChartSegment(
                category = category,
                value = catSecs.toFloat() / 60f,
                percentage = catSecs.toFloat() / totalSecs
            )
        }.filter { it.value > 0f }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Weekly statistics: last 7 days time logged in minutes
    val weeklyBarData: StateFlow<Pair<List<Float>, List<String>>> = logs.map { logList ->
        val calendar = Calendar.getInstance()
        val dayValues = mutableListOf<Float>()
        val dayLabels = mutableListOf<String>()

        // Look back 7 days
        for (i in 6 downTo 0) {
            val targetCal = Calendar.getInstance()
            targetCal.add(Calendar.DAY_OF_YEAR, -i)
            val dayStart = targetCal.apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis
            val dayEnd = targetCal.apply {
                set(Calendar.HOUR_OF_DAY, 23)
                set(Calendar.MINUTE, 55)
                set(Calendar.SECOND, 55)
            }.timeInMillis

            val dayLogs = logList.filter { it.timestamp in dayStart..dayEnd }
            val dailyMinutes = dayLogs.sumOf { it.durationSeconds }.toFloat() / 60f
            dayValues.add(dailyMinutes)

            val dayLabel = targetCal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.CHINESE) ?: ""
            dayLabels.add(dayLabel)
        }
        Pair(dayValues, dayLabels)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Pair(emptyList(), emptyList()))

    // 4. Timer Logic
    fun startTimer() {
        if (_isTimerRunning.value) return
        _isTimerRunning.value = true
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                if (_timerMode.value == TimerMode.STOPWATCH) {
                    _timeLeftSeconds.value += 1
                } else {
                    if (_timeLeftSeconds.value > 0) {
                        _timeLeftSeconds.value -= 1
                    } else {
                        onTimerComplete()
                        break
                    }
                }
            }
        }
    }

    fun pauseTimer() {
        timerJob?.cancel()
        _isTimerRunning.value = false
    }

    fun resetTimer() {
        pauseTimer()
        setTimerMode(_timerMode.value)
    }

    fun selectTaskForTimer(task: Task?) {
        _activeTask.value = task
        if (task != null) {
            setTimerMode(TimerMode.POMODORO_FOCUS)
            val secs = task.targetDurationMinutes * 60L
            _timeLeftSeconds.value = secs
            _totalDurationSeconds.value = secs
        }
    }

    fun setTimerMode(mode: TimerMode) {
        pauseTimer()
        _timerMode.value = mode
        when (mode) {
            TimerMode.POMODORO_FOCUS -> {
                val duration = (_activeTask.value?.targetDurationMinutes?.toLong() ?: 25L) * 60L
                _timeLeftSeconds.value = duration
                _totalDurationSeconds.value = duration
            }
            TimerMode.POMODORO_BREAK -> {
                val duration = 5 * 60L // 5 minute break
                _timeLeftSeconds.value = duration
                _totalDurationSeconds.value = duration
            }
            TimerMode.STOPWATCH -> {
                _timeLeftSeconds.value = 0
                _totalDurationSeconds.value = 0
            }
        }
    }

    private suspend fun onTimerComplete() {
        _isTimerRunning.value = false
        val elapsedSeconds = _totalDurationSeconds.value - _timeLeftSeconds.value

        if (_timerMode.value == TimerMode.POMODORO_FOCUS) {
            // Log completed work focus session
            val categoryStr = _activeTask.value?.category ?: Category.WORK.name
            val log = TimeLog(
                taskId = _activeTask.value?.id,
                category = categoryStr,
                durationSeconds = elapsedSeconds,
                note = "番茄鐘完成: " + (_activeTask.value?.title ?: "自由專注")
            )
            repository.insertLog(log)

            // Switch to break mode
            setTimerMode(TimerMode.POMODORO_BREAK)
        } else if (_timerMode.value == TimerMode.POMODORO_BREAK) {
            // Switch back to focus mode
            setTimerMode(TimerMode.POMODORO_FOCUS)
        }
    }

    // Stopwatch save log manually
    fun saveStopwatchSession(note: String) {
        if (_timerMode.value != TimerMode.STOPWATCH) return
        val elapsedSeconds = _timeLeftSeconds.value
        if (elapsedSeconds <= 0) return

        viewModelScope.launch {
            val categoryStr = _activeTask.value?.category ?: Category.WORK.name
            val log = TimeLog(
                taskId = _activeTask.value?.id,
                category = categoryStr,
                durationSeconds = elapsedSeconds,
                note = note.ifEmpty { "秒錶記錄: " + (_activeTask.value?.title ?: "自由記錄") }
            )
            repository.insertLog(log)
            resetTimer()
        }
    }

    // 5. CRUD Task functions
    fun addTask(
        title: String,
        description: String,
        category: Category,
        targetDuration: Int,
        scheduledDate: String? = null,
        scheduledTime: String? = null
    ) {
        viewModelScope.launch {
            val task = Task(
                title = title,
                description = description,
                category = category.name,
                targetDurationMinutes = targetDuration,
                scheduledDate = scheduledDate,
                scheduledTime = scheduledTime
            )
            repository.insertTask(task)
        }
    }

    fun toggleTaskComplete(task: Task) {
        viewModelScope.launch {
            val updatedTask = task.copy(isCompleted = !task.isCompleted)
            repository.updateTask(updatedTask)
            // If the completed task is currently active in the timer, deselect it
            if (updatedTask.isCompleted && _activeTask.value?.id == task.id) {
                selectTaskForTimer(null)
            }
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            repository.deleteTask(task)
            if (_activeTask.value?.id == task.id) {
                selectTaskForTimer(null)
            }
        }
    }

    // 6. Manual Log actions
    fun logTimeManually(category: Category, durationMinutes: Int, note: String) {
        viewModelScope.launch {
            val log = TimeLog(
                taskId = null,
                category = category.name,
                durationSeconds = durationMinutes * 60L,
                note = note.ifEmpty { "手動記錄: " + category.label }
            )
            repository.insertLog(log)
        }
    }

    fun deleteTimeLog(log: TimeLog) {
        viewModelScope.launch {
            repository.deleteLog(log)
        }
    }

    // 7. Routines & Recurring Activities Actions
    fun addRoutine(name: String, start: String, end: String, category: Category) {
        viewModelScope.launch {
            repository.insertRoutine(Routine(name = name, startTime = start, endTime = end, category = category.name))
        }
    }

    fun deleteRoutine(routine: Routine) {
        viewModelScope.launch {
            repository.deleteRoutine(routine)
        }
    }

    fun addRecurringActivity(name: String, dayOfWeek: Int, start: String, duration: Int, category: Category) {
        viewModelScope.launch {
            repository.insertRecurringActivity(RecurringActivity(name = name, dayOfWeek = dayOfWeek, startTime = start, durationMinutes = duration, category = category.name))
        }
    }

    fun deleteRecurringActivity(activity: RecurringActivity) {
        viewModelScope.launch {
            repository.deleteRecurringActivity(activity)
        }
    }

    // 8. Timetable Templates Logic
    fun applyTemplate(templateId: String, clearExisting: Boolean = false) {
        val template = TemplateRegistry.templates.find { it.id == templateId } ?: return
        val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        viewModelScope.launch {
            if (clearExisting) {
                val currentTasks = tasks.first()
                currentTasks.filter { it.scheduledDate == todayStr }.forEach { taskToDelete: Task ->
                    repository.deleteTask(taskToDelete)
                }
            }

            // 1. Add Routines as tasks first
            val routines = allRoutines.first()
            routines.filter { it.isEnabled }.forEach { routine ->
                repository.insertTask(Task(
                    title = "固定常規: ${routine.name}",
                    description = "每日固定行程",
                    category = routine.category,
                    targetDurationMinutes = 0, // Duration is fixed by start/end
                    scheduledDate = todayStr,
                    scheduledTime = routine.startTime
                ))
            }

            // 2. Add Template tasks
            template.tasks.forEach { preset ->
                // Basic collision check: if there's already a routine at this time, skip or offset?
                // For now, we just insert. Advanced logic could find the next "free slot".
                val isOccupied = routines.any { it.startTime == preset.scheduledTime }
                
                if (!isOccupied) {
                    val task = Task(
                        title = preset.title,
                        description = "套用模板: ${template.name}",
                        category = preset.category.name,
                        targetDurationMinutes = preset.durationMinutes,
                        scheduledDate = todayStr,
                        scheduledTime = preset.scheduledTime
                    )
                    repository.insertTask(task)
                }
            }
        }
    }
}

// Custom ViewModel Factory since we need to inject the repository
class ViewModelFactory(private val repository: TimeRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
