package com.github.pksokolowski.posty.utils

import com.github.pksokolowski.posty.di.PerApp
import kotlinx.coroutines.*
import javax.inject.Inject

@PerApp
class RequestRunner @Inject constructor(private val ongoingTasksTracker: OngoingTasksTracker) {

    fun <R> run(
        body: (suspend () -> R),
        onException: (e: Exception) -> Unit,
        onSuccess: (r: R) -> Unit,
        scope: CoroutineScope = GlobalScope
    ) {
        ongoingTasksTracker.startOne()
        scope.launch {
            try {
                val result = body.invoke()
                withContext(Dispatchers.Main) {
                    ongoingTasksTracker.endOne()
                    onSuccess.invoke(result)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    ongoingTasksTracker.endOne()
                    onException.invoke(e)
                }
            }
        }

    }

    fun areThereAnyOngoingRequests() = ongoingTasksTracker.areThereOngoingTasks()
}