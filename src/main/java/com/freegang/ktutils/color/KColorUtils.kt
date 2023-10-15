package com.freegang.ktutils.color

import android.graphics.Color
import java.util.Locale

object KColorUtils {

    /**
     * 更改指定颜色的透明度
     * @param color 指定颜色
     * @param alpha 指定透明度 0~255
     */
    fun alpha(color: Int, alpha: Int): Int {
        val red = Color.red(color)
        val green = Color.green(color)
        val blue = Color.blue(color)
        return Color.argb(alpha, red, green, blue)
    }

    /**
     * 将给定的颜色值转换为十六进制字符串表示
     *
     * @param color 颜色值
     * @return 十六进制字符串表示的颜色值，格式为 "#AARRGGBB"
     */
    fun colorIntToHex(color: Int): String {
        // 提取颜色的透明度、红、绿、蓝分量，并将它们转换为十六进制字符串
        val alphaHex = Color.alpha(color).toString(16).padStart(2, '0')
        val redHex = Color.red(color).toString(16).padStart(2, '0')
        val greenHex = Color.green(color).toString(16).padStart(2, '0')
        val blueHex = Color.blue(color).toString(16).padStart(2, '0')

        // 拼接十六进制字符串并转换为大写形式
        return "#$alphaHex$redHex$greenHex$blueHex".toUpperCase(Locale.getDefault())
    }

    /**
     * 判断给定的 8 位 ARGB 颜色是否为亮色或暗色
     *
     * @param color 8 位 ARGB 颜色值
     * @return 是否为亮色，true 为亮色，false 为暗色
     */
    fun isDarkColor(color: Int): Boolean {
        // 提取颜色的红、绿、蓝分量
        val red = color shr 16 and 0xFF
        val green = color shr 8 and 0xFF
        val blue = color and 0xFF

        // 计算颜色的相对亮度
        val brightness = (red * 299 + green * 587 + blue * 114) / 1000

        // 判断相对亮度是否超过阈值（128），超过则为亮色，否则为暗色
        return brightness < 128
    }
}