package com.freegang.ktutils.app

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.app.Application.ActivityLifecycleCallbacks
import android.os.Bundle
import android.view.ViewGroup
import android.view.Window

object KActivityUtils {
    private val mActivities = mutableListOf<Activity>()  // 存储活动的列表
    private val lifecycleCallbacks = object : ActivityLifecycleCallbacks {
        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
            mActivities.add(activity)  // 活动创建时将其添加到列表中
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
            mActivities.remove(activity)  // 活动销毁时从列表中移除
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
        application.unregisterActivityLifecycleCallbacks(lifecycleCallbacks)  // 取消注册生命周期回调
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
    fun getActivities(): List<Activity> {
        return mActivities.ifEmpty { getActivityListByRef() }  // 返回活动列表，如果为空则通过反射获取
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
    private fun getActivityListByRef(): List<Activity> {
        val activities = getActivitiesByReflect()  // 使用反射获取活动列表
        val activityList = mutableListOf<Activity>()
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
        val currentActivityThreadMethod = activityThreadClass.getDeclaredMethod("currentActivityThread")
        currentActivityThreadMethod.isAccessible = true
        val currentActivityThread = currentActivityThreadMethod.invoke(null)

        val activitiesField = activityThreadClass.getDeclaredField("mActivities")
        activitiesField.isAccessible = true
        return activitiesField.get(currentActivityThread) as Map<*, *>  // 获取当前线程的活动列表
    }
}

val Any.activeActivity get() = KActivityUtils.getActiveActivity()

val Activity.contentView get() = this.window.decorView.findViewById<ViewGroup>(Window.ID_ANDROID_CONTENT)

val Window.contentView get() = this.decorView.findViewById<ViewGroup>(Window.ID_ANDROID_CONTENT)