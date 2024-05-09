package com.freegang.ktutils.app

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.app.Application.ActivityLifecycleCallbacks
import android.os.Bundle

object KActivityUtils {
    private val mActivities = mutableSetOf<Activity>()
    private val onCreatedList = mutableSetOf<OnCreated>()
    private val onStartedList = mutableSetOf<OnStarted>()
    private val onResumedList = mutableSetOf<OnResumed>()
    private val onPausedList = mutableSetOf<OnPaused>()
    private val onStoppedList = mutableSetOf<OnStopped>()
    private val onSaveInstanceStateList = mutableSetOf<OnSaveInstanceState>()
    private val onDestroyedList = mutableSetOf<OnDestroyed>()

    private val lifecycleCallbacks = object : ActivityLifecycleCallbacks {
        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
            mActivities.add(activity)
            onCreatedList.forEach { it.onActivityCreated(activity, savedInstanceState) }
        }

        override fun onActivityStarted(activity: Activity) {
            onStartedList.forEach { it.onActivityStarted(activity) }
        }

        override fun onActivityResumed(activity: Activity) {
            onResumedList.forEach { it.onActivityResumed(activity) }
        }

        override fun onActivityPaused(activity: Activity) {
            onPausedList.forEach { it.onActivityPaused(activity) }
        }

        override fun onActivityStopped(activity: Activity) {
            onStoppedList.forEach { it.onActivityStopped(activity) }
        }

        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
            onSaveInstanceStateList.forEach { it.onActivitySaveInstanceState(activity, outState) }
        }

        override fun onActivityDestroyed(activity: Activity) {
            mActivities.remove(activity)
            onDestroyedList.forEach { it.onActivityDestroyed(activity) }
        }
    }

    /**
     * 注册活动生命周期回调
     *
     * @param application 应用程序对象
     */
    @JvmStatic
    fun register(application: Application) {
        application.registerActivityLifecycleCallbacks(lifecycleCallbacks)  // 注册生命周期回调
    }

    /**
     * 取消注册活动生命周期回调
     *
     * @param application 应用程序对象
     */
    @JvmStatic
    fun unregister(application: Application) {
        clear()
        application.unregisterActivityLifecycleCallbacks(lifecycleCallbacks)  // 取消注册生命周期回调
    }

    /**
     * 清空所有已注册的监听器，包括活动列表
     */
    @JvmStatic
    fun clear() {
        onCreatedList.clear()
        onStartedList.clear()
        onResumedList.clear()
        onPausedList.clear()
        onStoppedList.clear()
        onSaveInstanceStateList.clear()
        onDestroyedList.clear()
        mActivities.clear()
    }

    /**
     * 只对 onCreated 回调
     *
     * @param onCreated
     */
    @JvmStatic
    fun onCreatedCallback(onCreated: OnCreated) {
        onCreatedList.add(onCreated)
    }

    /**
     * 只对 onStarted 回调
     *
     * @param onStarted
     */
    @JvmStatic
    fun onStartedCallback(onStarted: OnStarted) {
        onStartedList.add(onStarted)
    }

    /**
     * 只对 onResumed 回调
     *
     * @param onResumed
     */
    @JvmStatic
    fun onResumedCallback(onResumed: OnResumed) {
        onResumedList.add(onResumed)
    }

    /**
     * 只对 onPaused 回调
     *
     * @param onPaused
     */
    @JvmStatic
    fun onPausedCallback(onPaused: OnPaused) {
        onPausedList.add(onPaused)
    }

    /**
     * 只对 onStopped 回调
     *
     * @param onStopped
     */
    @JvmStatic
    fun onStoppedCallback(onStopped: OnStopped) {
        onStoppedList.add(onStopped)
    }

    /**
     * 只对 onSaveInstanceState 回调
     *
     * @param onSaveInstanceState
     */
    @JvmStatic
    fun onSaveInstanceCallback(onSaveInstanceState: OnSaveInstanceState) {
        onSaveInstanceStateList.add(onSaveInstanceState)
    }

    /**
     * 只对 onDestroyed 回调
     *
     * @param onDestroyed
     */
    @JvmStatic
    fun onDestroyedCallback(onDestroyed: OnDestroyed) {
        onDestroyedList.add(onDestroyed)
    }

    /**
     * 获取当前活动
     *
     * @return 当前活动对象，如果活动列表为空，则返回null
     */
    @JvmStatic
    fun getActiveActivity(): Activity? {
        return if (mActivities.isNotEmpty()) {
            mActivities.last()  // 返回列表中最后一个活动，即当前活动
        } else {
            getActiveActivityByReflect()  // 使用反射获取当前活动
        }
    }

    /**
     * 获取活动列表
     *
     * @return 活动列表，如果列表为空，则通过反射获取
     */
    @JvmStatic
    fun getActivities(): Set<Activity> {
        return mActivities.ifEmpty { getActivityListByRef() } // 返回活动列表，如果为空则通过反射获取
    }

    /**
     * 通过反射获取当前活动
     *
     * @return 当前活动对象，如果获取失败则返回null
     */
    private fun getActiveActivityByReflect(): Activity? {
        return try {
            val activities = getActivitiesByReflect()  // 使用反射获取活动列表
            var activeActivity: Activity? = null
            for (activityRecord in activities.values) {
                val activityRecordClass = activityRecord!!.javaClass
                val pausedField = activityRecordClass.getDeclaredField("paused")
                pausedField.isAccessible = true
                val isPaused = pausedField.getBoolean(activityRecord)

                if (!isPaused) {
                    val activityField = activityRecordClass.getDeclaredField("activity")
                    activityField.isAccessible = true
                    activeActivity = activityField.get(activityRecord) as Activity?  // 获取非暂停状态的活动
                    break
                }
            }
            activeActivity
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * 通过反射获取活动列表
     *
     * @return 活动列表
     */
    private fun getActivityListByRef(): Set<Activity> {
        val activities = getActivitiesByReflect()  // 使用反射获取活动列表
        val activityList = mutableSetOf<Activity>()
        for (activityRecord in activities.values) {
            val activityRecordClass = activityRecord!!.javaClass
            val activityField = activityRecordClass.getDeclaredField("activity")
            activityField.isAccessible = true

            val activity = activityField.get(activityRecord) as Activity
            activityList.add(activity)  // 将活动添加到列表中
        }

        return activityList
    }

    /**
     * 通过反射获取活动列表
     *
     * @return 活动列表的映射
     */
    @SuppressLint("PrivateApi", "DiscouragedPrivateApi")
    private fun getActivitiesByReflect(): Map<*, *> {
        val activityThreadClass = Class.forName("android.app.ActivityThread")
        val currentActivityThreadMethod =
            activityThreadClass.getDeclaredMethod("currentActivityThread")
        currentActivityThreadMethod.isAccessible = true
        val currentActivityThread = currentActivityThreadMethod.invoke(null)

        val activitiesField = activityThreadClass.getDeclaredField("mActivities")
        activitiesField.isAccessible = true
        return activitiesField.get(currentActivityThread) as Map<*, *>  // 获取当前线程的活动列表
    }


    fun interface OnCreated {
        fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?)
    }

    fun interface OnStarted {
        fun onActivityStarted(activity: Activity)
    }

    fun interface OnResumed {
        fun onActivityResumed(activity: Activity)
    }

    fun interface OnPaused {
        fun onActivityPaused(activity: Activity)
    }

    fun interface OnStopped {
        fun onActivityStopped(activity: Activity)
    }

    fun interface OnSaveInstanceState {
        fun onActivitySaveInstanceState(activity: Activity, outState: Bundle)
    }

    fun interface OnDestroyed {
        fun onActivityDestroyed(activity: Activity)
    }
}
