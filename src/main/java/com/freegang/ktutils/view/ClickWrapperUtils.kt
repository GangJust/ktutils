package com.freegang.ktutils.view

object ClickWrapperUtils {
    private val keys = HashMap<String, Long>()

    @JvmStatic
    fun onClickWrapper(
        interval: Long = 1000L,
        maxStackTrace: Int = 5,
        action: () -> Unit,
    ) {
        val thread = Thread.currentThread()
        val maxSize =
            if (thread.stackTrace.size > maxStackTrace)
                maxStackTrace
            else
                thread.stackTrace.size

        val key = buildStackTraceKey(maxSize, thread.stackTrace)
        val currentTimeMillis = System.currentTimeMillis()
        val lastClickTimeMillis = keys.getOrPut(key) { 0 }

        if (currentTimeMillis - lastClickTimeMillis > interval) {
            action.invoke()
            keys[key] = currentTimeMillis // 重新记录上次点击时间（毫秒）
        }
    }

    private fun buildStackTraceKey(count: Int, stackTrace: Array<StackTraceElement>): String {
        return StringBuilder().apply {
            repeat(count) { append(stackTrace[it].lineNumber) }
        }.toString()
    }
}

inline fun onClickWrapper(
    interval: Long = 1000L,
    maxStackTrace: Int = 5,
    crossinline action: () -> Unit,
) {
    ClickWrapperUtils.onClickWrapper(interval, maxStackTrace) {
        action.invoke()
    }
}