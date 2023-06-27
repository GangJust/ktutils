package com.freegang.ktutils.display

import android.content.Context


object KDisplayUtils {
    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    fun dip2px(context: Context, dpValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    fun px2dip(context: Context, pxValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (pxValue / scale + 0.5f).toInt()
    }
}

fun Context.dip2px(dpValue: Float): Int = KDisplayUtils.dip2px(this, dpValue)
fun Context.px2dip(pxValue: Float): Int = KDisplayUtils.px2dip(this, pxValue)