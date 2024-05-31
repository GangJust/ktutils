package com.freegang.ktutils.other

import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.RejectedExecutionException
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

object KThreadPollUtils {
    private const val CORE_POOL_SIZE = 5 // 核心线程数
    private const val MAXIMUM_POOL_SIZE = 10 // 最大线程数
    private const val KEEP_ALIVE_TIME = 60L // 空闲线程存活时间（单位：秒）

    private val threadExecutors = ThreadPoolExecutor(
        CORE_POOL_SIZE,
        MAXIMUM_POOL_SIZE,
        KEEP_ALIVE_TIME,
        TimeUnit.SECONDS,
        LinkedBlockingQueue()
    )

    private val delayExecutors = ScheduledThreadPoolExecutor(
        CORE_POOL_SIZE,
    )

    /**
     * 线程池执行任务
     *
     * @param task 任务
     */
    @JvmStatic
    @Throws(NullPointerException::class, RejectedExecutionException::class)
    fun execute(task: Runnable) {
        threadExecutors.execute(task)
    }


    /**
     * 线程池移除任务
     */
    @JvmStatic
    fun remove(task: Runnable): Boolean {
        return threadExecutors.remove(task)
    }

    /**
     * 线程池开启延时任务[delay]毫秒后执行[task]
     *
     * @param delay 延迟的时间
     * @param task 任务
     */
    @JvmStatic
    @Throws(NullPointerException::class, RejectedExecutionException::class)
    fun schedule(delay: Long, task: Runnable): ScheduledFuture<*>? {
        return delayExecutors.schedule(task, delay, TimeUnit.MILLISECONDS)
    }

    /**
     * 线程池移除定时任务
     *
     * @param task 任务
     */
    @JvmStatic
    fun removeSchedule(task: Runnable): Boolean {
        return delayExecutors.remove(task)
    }

    /**
     * 统一取消
     */
    @JvmStatic
    fun shutdown() {
        threadExecutors.shutdown()
        delayExecutors.shutdown()
    }

    /**
     * 统一取消
     */
    @JvmStatic
    fun shutdownNow() {
        threadExecutors.shutdownNow()
        delayExecutors.shutdownNow()
    }
}