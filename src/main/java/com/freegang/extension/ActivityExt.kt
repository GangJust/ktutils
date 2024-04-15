package com.freegang.extension

import android.app.Activity
import android.view.ViewGroup
import android.view.Window
import com.freegang.ktutils.app.KActivityUtils

/**
 * 获取当前活动的Activity
 */
val Any.activeActivity
    get() = KActivityUtils.getActiveActivity()

/**
 * 获取当前Window的ContentView
 */
val Activity.contentView
    get() = this.window.decorView.findViewById<ViewGroup>(Window.ID_ANDROID_CONTENT)

/**
 * 获取当前Window的ContentView
 */
val Window.contentView
    get() = this.decorView.findViewById<ViewGroup>(Window.ID_ANDROID_CONTENT)