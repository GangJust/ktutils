package com.freegang.ktutils.view

import android.view.View

object KFastClickUtils {
    private var lastClickTime: Long = 0
    private const val DEFAULT_CLICK_INTERVAL: Long = 2000L // 默认点击间隔为2000毫秒

    /**
     * 判断是否为快速点击
     *
     * @return 如果两次点击时间间隔小于默认点击间隔，则返回true；否则返回false。
     */
    @JvmStatic
    fun isFastClick(): Boolean {
        val currentTime = System.currentTimeMillis()
        val elapsedTime = currentTime - lastClickTime
        if (elapsedTime < DEFAULT_CLICK_INTERVAL) return true
        lastClickTime = currentTime
        return false
    }

    /**
     * 判断是否为快速点击
     *
     * @param interval 自定义点击间隔时间
     * @return 如果两次点击时间间隔小于指定的点击间隔，则返回true；否则返回false。
     */
    @JvmStatic
    fun isFastClick(interval: Long): Boolean {
        val currentTime = System.currentTimeMillis()
        val elapsedTime = currentTime - lastClickTime
        if (elapsedTime < interval) return true
        lastClickTime = currentTime
        return false
    }
}

/**
 * 设置防止快速点击的点击事件监听器
 *
 * @param interval 点击间隔时间（毫秒），默认为2000毫秒
 * @param l 点击事件监听器
 */
fun View.setFastClickListener(
    interval: Long = 2000, // 默认点击间隔为2000毫秒
    l: View.OnClickListener,
) {
    var lastClickTime: Long = 0
    setOnClickListener {
        val currentTime = System.currentTimeMillis()
        val elapsedTime = currentTime - lastClickTime
        if (elapsedTime < interval) return@setOnClickListener
        lastClickTime = currentTime
        l.onClick(it)
    }
}

