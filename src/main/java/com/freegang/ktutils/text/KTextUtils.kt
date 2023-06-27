package com.freegang.ktutils.text

object KTextUtils {

    /**
     * 获取任何对象的字符串表示，如果对象为空，则返回默认字符串。
     *
     * @param any 任何对象，可以为空。
     * @return 对象的字符串表示或默认字符串。
     */
    @JvmStatic
    @JvmOverloads
    fun get(any: Any?, default: String = ""): String {
        if (any == null) return default
        return any.toString()
    }

    /**
     * 获取字符串的值，如果字符串为空，则返回默认值。
     *
     * @param text 字符串，可以为空。
     * @param default 默认值。
     * @return 字符串的值或默认值。
     */
    @JvmStatic
    @JvmOverloads
    fun get(text: String?, default: String = ""): String {
        if (isEmpty(text)) return default
        return text!!
    }

    /**
     * 去掉字符串的前导和尾随空格，如果字符串为空，则返回默认值。
     *
     * @param text 字符串，可以为空。
     * @param default 默认值。
     * @return 去掉前导和尾随空格的字符串或默认值。
     */
    @JvmStatic
    @JvmOverloads
    fun trim(text: String?, default: String = ""): String {
        if (isEmpty(text)) return default
        return text!!.trim()
    }

    /**
     * 去掉字符串的前导空格，如果字符串为空，则返回默认值。
     *
     * @param text 字符串，可以为空。
     * @param default 默认值。
     * @return 去掉前导空格的字符串或默认值。
     */
    @JvmStatic
    @JvmOverloads
    fun trimStart(text: String?, default: String = ""): String {
        if (isEmpty(text)) return default
        return text!!.trimStart()
    }

    /**
     * 去掉字符串的尾随空格，如果字符串为空，则返回默认值。
     *
     * @param text 字符串，可以为空。
     * @param default 默认值。
     * @return 去掉尾随空格的字符串或默认值。
     */
    @JvmStatic
    @JvmOverloads
    fun trimEnd(text: String?, default: String = ""): String {
        if (isEmpty(text)) return default
        return text!!.trimEnd()
    }

    /**
     * 对某个字符串unicode解码
     * @param text 需要被unicode编码的字符串
     * @return unicode解码之后的字符串
     */
    @JvmStatic
    fun deUnicode(text: String?): String {
        text ?: return ""

        val stringBuilder = StringBuilder()
        val regex = Regex("\\\\u([0-9a-fA-F]{4})")
        val matches = regex.findAll(text)
        var lastIndex = 0
        for (matchResult in matches) {
            stringBuilder.append(text.substring(lastIndex, matchResult.range.first))
            lastIndex = matchResult.range.last + 1
            val hex = matchResult.groupValues[1]
            val decimal = hex.toInt(16)
            stringBuilder.append(decimal.toChar())
        }
        stringBuilder.append(text.substring(lastIndex, text.length))
        return stringBuilder.toString()
    }

    /**
     * 对某个字符串unicode编码
     * @param text 需要被unicode编码的字符串
     * @return unicode编码之后的字符串
     */
    @JvmStatic
    fun enUnicode(text: String?): String {
        text ?: return ""
        val stringBuilder = StringBuilder()
        for (char in text) {
            stringBuilder.append("\\u${char.toInt().toString(16).padStart(4, '0')}")
        }
        return stringBuilder.toString()
    }

    /**
     * 在字符串的开头用指定的字符或空格填充到指定的长度
     */
    @JvmStatic
    fun padStart(text: String?, length: Int, padChar: Char): String {
        text ?: return ""
        return text.padStart(length, padChar)
    }

    /**
     * 在字符串末尾用指定的字符或空格填充到指定的长度
     */
    @JvmStatic
    fun padEnd(text: String?, length: Int, padChar: Char): String {
        text ?: return ""
        return text.padEnd(length, padChar)
    }

    /**
     * 判断字符串是否为空，包括 null、空字符串和全空格字符串。
     *
     * @param text 字符串，可以为空。
     * @return 字符串是否为空。
     */
    @JvmStatic
    fun <S : CharSequence> isEmpty(text: S?): Boolean {
        if (text == null) return true
        return text.isEmpty() or text.isBlank() or (text == "null")
    }

    /**
     * 判断字符串是否不为空，与 isEmpty 方法相反。
     *
     * @param text 字符串，可以为空。
     * @return 字符串是否不为空。
     */
    @JvmStatic
    fun <S : CharSequence> isNotEmpty(text: S?): Boolean {
        return !isEmpty(text)
    }

    /**
     * 判断一个字符串是否与任何一个字符串数组元素相等
     * @param text 要检查的字符串
     * @param arr 字符串数组，要比较的对象
     * @return 如果 text 与 arr 中任意一个字符串相等，则返回 true，否则返回 false
     */
    @JvmStatic
    fun <S : CharSequence> anyEquals(text: S?, vararg arr: S): Boolean {
        return arr.map { "$text" == it }.contains(true)
    }

    /**
     * 判断一个字符串是否包含任何一个数组元素值
     * @param text 要检查的字符串
     * @param arr 字符串数组，要比较的对象
     * @return 如果 text 包含 arr 中任意一个字符串，则返回 true，否则返回 false
     */
    @JvmStatic
    fun <S : CharSequence> anyContains(text: S?, vararg arr: S): Boolean {
        return arr.map { "$text".contains(it) }.contains(true)
    }

    /**
     * 判断一个字符串是否是数字，可以是整数或浮点数
     * @param text 要检查的字符串
     * @param isPositive 是否为正数
     * @return 如果 text 是数字，则返回 true，否则返回 false
     */
    @JvmStatic
    @JvmOverloads
    fun <S : CharSequence> isNumber(text: S?, isPositive: Boolean = true): Boolean {
        if (isPositive) {
            return Regex("^\\d*\\.?\\d+$").matches("$text")
        }
        return Regex("^-?\\d*\\.?\\d+$").matches("$text")
    }

    /**
     * 判断一个字符串是否是整数
     * @param text 要检查的字符串
     * @param isPositive 是否为正数
     * @return 如果 text 是数字，则返回 true，否则返回 false
     */
    @JvmStatic
    @JvmOverloads
    fun <S : CharSequence> isIntegerNumber(text: S?, isPositive: Boolean = true): Boolean {
        if (isPositive) {
            return Regex("^\\d*$").matches("$text")
        }
        return Regex("^-?\\d*$").matches("$text")
    }

    /**
     * 将一个字符串转换为浮点数
     * @param text 要转换的字符串
     * @param default 转换失败时的默认值
     * @return 如果转换成功，则返回该浮点数，否则返回默认值
     */
    @JvmStatic
    @JvmOverloads
    fun <S : CharSequence> toFloat(text: S?, default: Float = 0.0f): Float {
        return try {
            "$text".toFloat()
        } catch (e: NumberFormatException) {
            default
        }
    }

    /**
     * 将一个字符串转换为双精度浮点数
     * @param text 要转换的字符串
     * @param default 转换失败时的默认值
     * @return 如果转换成功，则返回该双精度浮点数，否则返回默认值
     */
    @JvmStatic
    @JvmOverloads
    fun <S : CharSequence> toDouble(text: S?, default: Double = 0.0): Double {
        return try {
            "$text".toDouble()
        } catch (e: NumberFormatException) {
            default
        }
    }

    /**
     * 将一个字符串转换为整数
     * @param text 要转换的字符串
     * @param default 转换失败时的默认值
     * @return 如果转换成功，则返回该整数，否则返回默认值
     */
    @JvmStatic
    @JvmOverloads
    fun <S : CharSequence> toInt(text: S?, default: Int = 0): Int {
        return try {
            "$text".toInt()
        } catch (e: NumberFormatException) {
            default
        }
    }
}