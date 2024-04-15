package com.freegang.extension

import android.content.Context
import com.freegang.ktutils.display.KDisplayUtils

/**
 * 获取屏幕宽高
 * @return size 屏幕的宽高
 */
val screenSize by lazy { KDisplayUtils.screenSize() }

/**
 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)。
 *
 * @param dpValue 需要转换的dp值
 * @return 转换结果，px值
 */
fun Context.dip2px(dpValue: Float): Int {
    return KDisplayUtils.dip2px(this, dpValue)
}

/**
 * 根据手机的分辨率从 px(像素) 的单位 转成为 dp。
 *
 * @param pxValue 需要转换的px值
 * @return 转换结果，dp值
 */
fun Context.px2dip(pxValue: Float): Int {
    return KDisplayUtils.px2dip(this, pxValue)
}