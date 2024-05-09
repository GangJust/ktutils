package com.freegang.extension

import com.freegang.ktutils.color.KColorUtils

/**
 * 将给定的颜色值转换为十六进制字符串表示
 *
 * @return 十六进制字符串表示的颜色值，格式为 "#AARRGGBB"
 */
fun Int.toColorHex(): String {
    return KColorUtils.colorIntToHex(this)
}