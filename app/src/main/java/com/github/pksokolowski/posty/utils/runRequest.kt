package com.github.pksokolowski.posty.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

fun <R> (suspend () -> R).runRequest(
    onException: (e: Exception) -> Unit,
    onSuccess: (r: R) -> Unit
) {
    val body = this
    GlobalScope.launch {
        try {
            val result = body.invoke()
            withContext(Dispatchers.Main) { onSuccess.invoke(result) }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) { onException.invoke(e) }
        }
    }

}