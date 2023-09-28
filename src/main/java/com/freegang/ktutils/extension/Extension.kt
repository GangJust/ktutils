package com.freegang.ktutils.extension

import android.view.MotionEvent

inline fun <reified T> Any.asOrNull(): T? {
    return if (this is T) {
        this
    } else {
        null
    }
}

inline fun <reified T> Array<T>.getOrNull(index: Int): T? {
    return if (index in indices) {
        get(index)
    } else {
        null
    }
}

fun MotionEvent.actionToString(): String {
    return MotionEvent.actionToString(this.action)
}