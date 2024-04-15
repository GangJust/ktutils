package com.freegang.extension

import android.view.MotionEvent

/**
 * 将MotionEvent的action转换为字符串
 */
fun MotionEvent.actionToString(): String {
    return MotionEvent.actionToString(this.action)
}