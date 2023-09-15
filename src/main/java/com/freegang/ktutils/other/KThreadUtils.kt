package com.freegang.ktutils.other

import kotlin.concurrent.thread

object KThreadUtils {

    /**
     * 开启一个新线程
     * @param runnable
     */
    @JvmStatic
    fun newThread(runnable: Runnable): Thread {
        return thread {
            runnable.run()
        }
    }

    /**
     * 开启一个新线程，并在延迟[millisecond]毫秒后执行[runnable]
     * @param millisecond 延迟的时间
     * @param runnable
     */
    @JvmStatic
    fun delay(millisecond: Long, runnable: Runnable) {
        thread {
            runCatching {
                Thread.sleep(millisecond)
                runnable.run()
            }
        }
    }
}