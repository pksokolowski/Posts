package com.github.pksokolowski.posty.utils

sealed class Status{
    object OK : Status()
    data class ERROR(val messageResId: Int): Status()
}