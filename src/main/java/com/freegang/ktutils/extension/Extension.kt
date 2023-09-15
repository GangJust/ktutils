package com.freegang.ktutils.extension

fun <T> Any.asOrNull(): T? {
    return try {
        this as T?
    } catch (_: Exception) {
        null
    }
}