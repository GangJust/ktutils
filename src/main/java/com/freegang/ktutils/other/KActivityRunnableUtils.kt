package com.freegang.ktutils.other

import android.app.Activity
import android.app.Application
import android.app.Application.ActivityLifecycleCallbacks
import android.content.Context
import android.os.Bundle
import kotlin.concurrent.thread


/**
 * 一个监听器工具类
 *
 * 需要在 application 中注册和注销
 *
 * 举个例子
 * ```
 * class MainActivity : AppCompatActivity(), ActivityRunnable {
 *  override fun running(context: Context) {
 *    //该方法会在activity生命周期内按指定间隔时间一直运行, 可作定时监测
 *    //该方法属于非UI线程方法
 *  }
 * }
 * ```
 */
object KActivityRunnableUtils {
    private var status = 0
    private var runThread: Thread? = null
    private var runnableList: MutableList<ActivityRunnable> = mutableListOf()
    private val activityLifecycleCallback: ActivityLifecycleCallbacks = object : ActivityLifecycleCallbacks {
        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
            if (activity is ActivityRunnable) {
                runnableList.add(activity)
            }
        }

        override fun onActivityStarted(activity: Activity) {

        }

        override fun onActivityResumed(activity: Activity) {

        }

        override fun onActivityPaused(activity: Activity) {

        }

        override fun onActivityStopped(activity: Activity) {

        }

        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {

        }

        override fun onActivityDestroyed(activity: Activity) {
            if (activity is ActivityRunnable) {
                runnableList.remove(activity)
            }
        }
    }

    /**
     * 注册
     * @param app Application
     * @param interval 间隔时间，默认2000毫秒
     */
    @JvmStatic
    @JvmOverloads
    fun register(app: Application, interval: Long = 2000L) {
        if (runThread != null && runThread!!.isAlive) return
        if (status != 0) return

        status = 1
        app.registerActivityLifecycleCallbacks(activityLifecycleCallback)
        runThread = thread {
            runCatching {
                while (true) {
                    Thread.sleep(interval)
                    runnableList.forEach { it.running(it as Context) }
                }
            }
        }
    }

    /**
     * 注销
     * @param app Application
     */
    @JvmStatic
    fun unregister(app: Application) {
        status = 0
        app.unregisterActivityLifecycleCallbacks(activityLifecycleCallback)
        runCatching {
            runnableList.clear()
            runThread?.interrupt()
            runThread = null
        }
    }
}

interface ActivityRunnable {
    fun running(context: Context)
}