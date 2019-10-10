package com.github.pksokolowski.posty.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import javax.inject.Inject

/**
 * A component handling ongoing background/long running tasks.
 * It is not app-wide in and out of itself, so various components
 * might have their own instances of it and their own counters consequently.
 */
class OngoingTasksTracker @Inject constructor() {
    private val isAnyTaskInProgress = MutableLiveData<Boolean>()
    fun areThereOngoingTasks() = isAnyTaskInProgress as LiveData<Boolean>

    private var ongoingTasks = 0
    fun startOne() {
        if (++ongoingTasks > 0) isAnyTaskInProgress.value = true
    }

    fun endOne() {
        if (--ongoingTasks == 0) isAnyTaskInProgress.value = false
    }
}