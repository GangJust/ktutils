package com.freegang.ktutils.display

import android.content.Context
import android.content.res.Resources
import android.util.Size


object KDisplayUtils {
    /**
     * 获取屏幕宽高
     * @return size 屏幕的宽高
     */
    @JvmStatic
    fun screenSize(): Size {
        val displayMetrics = Resources.getSystem().displayMetrics
        val heightPixels = displayMetrics.heightPixels
        val widthPixels = displayMetrics.widthPixels
        return Size(widthPixels, heightPixels)
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    @JvmStatic
    fun dip2px(context: Context, dpValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    @JvmStatic
    fun px2dip(context: Context, pxValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (pxValue / scale + 0.5f).toInt()
    }
}

fun Context.dip2px(dpValue: Float): Int = KDisplayUtils.dip2px(this, dpValue)
fun Context.px2dip(pxValue: Float): Int = KDisplayUtils.px2dip(this, pxValue)