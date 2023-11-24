package com.freegang.ktutils.view

object KFastClickUtils {
    private val records: MutableMap<String, Long> = HashMap()

    /**
     * 判断是否为快速点击
     * @url https://zhuanlan.zhihu.com/p/34841081
     *
     * @param interval 自定义点击间隔时间
     * @return 如果两次点击时间间隔小于指定的点击间隔，则返回true；否则返回false。
     */
    @JvmStatic
    @JvmOverloads
    fun isFastDoubleClick(interval: Long = 500L): Boolean {
        if (records.size > 1000) records.clear()
        // 本方法被调用的文件名和行号作为标记
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
