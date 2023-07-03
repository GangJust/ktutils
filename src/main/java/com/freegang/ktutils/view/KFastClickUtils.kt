package com.freegang.ktutils.view

import android.view.View

object KFastClickUtils {
    private val records: MutableMap<String, Long> = HashMap()

    /**
     * 判断是否为快速点击
     * @url https://zhuanlan.zhihu.com/p/34841081
     *
     * @param interval 自定义点击间隔时间
     * @return 如果两次点击时间间隔小于指定的点击间隔，则返回true；否则返回false。
     */
    fun isFastDoubleClick(interval: Long): Boolean {
        if (records.size > 1000) records.clear()
        //本方法被调用的文件名和行号作为标记
        val ste = Throwable().stackTrace[1]
        val key = ste.fileName + ste.lineNumber
        var lastClickTime = records[key]
        val thisClickTime = System.currentTimeMillis()
        records[key] = thisClickTime
        lastClickTime = lastClickTime ?: 0
        val timeDuration = thisClickTime - lastClickTime
        return timeDuration < interval
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

