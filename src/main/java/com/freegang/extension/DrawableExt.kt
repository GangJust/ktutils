package com.freegang.extension

import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.ShapeDrawable

/**
 * 获取简单的 Drawable 字符串表示
 */
fun Drawable.simpleString(): String {
    return when (this) {
        is ColorDrawable -> {
            "ColorDrawable(color: ${color.toColorHex()})"
        }

        is ShapeDrawable -> {
            "ShapeDrawable(paintColor: ${paint.color.toColorHex()})"
        }

        else -> {
            "$this"
        }
    }
}