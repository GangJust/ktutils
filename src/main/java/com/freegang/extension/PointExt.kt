package com.freegang.extension

import android.graphics.Point

/**
 * 将Point转为短字符串表示
 */
fun Point.toShortString(): String {
    return "[$x,$y]"
}