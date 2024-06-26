package com.freegang.extension

import com.freegang.ktutils.display.KDisplayUtils

/**
 * 获取屏幕宽高
 * @return size 屏幕的宽高
 */
val screenSize by lazy { KDisplayUtils.screenSize() }

/**
 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)。
 *
 * @return 转换结果，px值
 */
fun Number.dip2px(): Int {
    return KDisplayUtils.dip2px(this.toFloat())
}

/**
 * 根据手机的分辨率从 px(像素) 的单位 转成为 dp。
 *
 * @return 转换结果，dp值
 */
fun Number.px2dip(): Int {
    return KDisplayUtils.px2dip(this.toFloat())
}