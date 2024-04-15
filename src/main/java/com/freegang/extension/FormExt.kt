package com.freegang.extension

import android.widget.TextView
import com.freegang.ktutils.text.KTextUtils
import com.freegang.ktutils.view.KFormUtils

/**
 * 检查某个TextView文本是否为空
 *
 * @param needCheckNullStr 是否需要检查为 `null` 字符串
 */
fun <T : TextView> T.checkEmpty(needCheckNullStr: Boolean = false): Boolean {
    return KFormUtils.checkEmpty(this, needCheckNullStr)
}

/**
 * 按正则表达式检查某个TextView文本
 *
 * @param regex 正则表达式
 */
fun <T : TextView> T.checkByRegex(regex: String): Boolean {
    return KFormUtils.checkByRegex(this, regex)
}

/**
 * 是否是数字，可以是整数或浮点数
 *
 * @param msg 提示信息
 * @param isPositive 是否正数
 */
fun <T : TextView> T.checkIsNumber(
    msg: CharSequence,
    isPositive: Boolean = true,
) {
    KFormUtils.checkIsNumber(this, msg, isPositive)
}

/**
 * 是否是整数数字
 *
 * @param msg 提示信息
 * @param isPositive 是否正数
 */
fun <T : TextView> T.checkIsIntegerNumber(
    msg: CharSequence,
    isPositive: Boolean = true,
) {
    KFormUtils.checkIsIntegerNumber(this, msg, isPositive)
}

/**
 * 获取TextView的文本内容，如果为空则返回默认值
 *
 * @param default 默认值
 */
fun <T : TextView> T.textString(default: String = ""): String {
    return KTextUtils.get(this.text, default)
}

/**
 * 获取TextView的tag内容，会将tag进行toString()转换，如果为空则返回默认值
 */
fun <T : TextView> T.tagString(default: String = ""): String {
    return KTextUtils.get(this.tag, default)
}

/**
 * 清空TextView的文本内容
 */
fun <T : TextView> T.clear() {
    this.text = java.lang.String()
}