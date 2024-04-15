package com.freegang.ktutils.view

import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import com.freegang.ktutils.text.KTextUtils

/// 表单工具类
object KFormUtils {

    /**
     * 检查某个TextView文本是否为空
     *
     * @param view T extends TextView
     * @param needCheckNullStr 是否需要检查为 `null` 字符串
     */
    @JvmStatic
    @JvmOverloads
    fun <T : TextView> checkEmpty(view: T, needCheckNullStr: Boolean = false): Boolean {
        if (view.text.isEmpty() or view.text.isBlank()) return true
        return (view.text == "null") and needCheckNullStr
    }

    /**
     * 按正则表达式检查某个TextView文本
     *
     * @param view T extends TextView
     * @param regex 正则表达式
     */
    @JvmStatic
    fun <T : TextView> checkByRegex(view: T, regex: String): Boolean {
        if (regex.isBlank()) return false
        return view.text.contains(regex.toRegex())
    }

    /**
     * 是否是数字，可以是整数或浮点数
     *
     * @param view T extends TextView
     * @param msg 提示信息
     * @param isPositive 是否正数
     */
    @JvmStatic
    @JvmOverloads
    fun <T : TextView> checkIsNumber(
        view: T,
        msg: CharSequence,
        isPositive: Boolean = true,
    ) {
        val regex = if (isPositive) Regex("^\\d*\\.?\\d+\$") else Regex("^-?\\d*\\.?\\d+\$")
        view.addTextChangedListener {
            if (!regex.matches("${it}0")) {
                it?.clear()
                Toast.makeText(view.context, msg, Toast.LENGTH_SHORT).show()
                return@addTextChangedListener
            }
        }
    }

    /**
     * 是否是整数数字
     *
     * @param view T extends TextView
     * @param msg 提示信息
     * @param isPositive 是否正数
     */
    @JvmStatic
    @JvmOverloads
    fun <T : TextView> checkIsIntegerNumber(
        view: T,
        msg: CharSequence,
        isPositive: Boolean = true
    ) {
        val regex = if (isPositive) Regex("^\\d*$") else Regex("^-?\\d*\$")
        view.addTextChangedListener {
            if (!regex.matches("$it")) {
                it?.clear()
                Toast.makeText(view.context, msg, Toast.LENGTH_SHORT).show()
                return@addTextChangedListener
            }
        }
    }
}