package com.freegang.ktutils.display

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
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)。
     *
     * @param dpValue 需要转换的dp值
     * @return 转换结果，px值
     */
    @JvmStatic
    fun dip2px(dpValue: Float): Int {
        val scale = Resources.getSystem().displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp。
     *
     * @param pxValue 需要转换的px值
     * @return 转换结果，dp值
     */
    @JvmStatic
    fun px2dip(pxValue: Float): Int {
        val scale = Resources.getSystem().displayMetrics.density
        return (pxValue / scale + 0.5f).toInt()
    }
}
