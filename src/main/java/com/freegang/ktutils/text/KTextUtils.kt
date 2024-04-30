package com.freegang.ktutils.text

import kotlin.random.Random

object KTextUtils {

    /**
     * 判断字符串是否为 null 或 `null` 字符串，若需要对 `null、空字符串和全空格字符串` 判断，见 [isEmpty]。
     *
     * @param text 字符串，可以为空。
     * @return 字符串是否为空。
     */
    @JvmStatic
    fun <S : CharSequence> isNull(text: S?): Boolean {
        if (text == null) return true
        return "$text" == "null"
    }

    /**
     * 判断字符串是否不为 null 或 `null` 字符串，与 [isNull] 方法相反。
     *
     * @param text 字符串，可以为空。
     * @return 字符串是否为空。
     */
    @JvmStatic
    fun <S : CharSequence> isNotNull(text: S?): Boolean {
        return !isNull(text)
    }

    /**
     * 判断字符串是否为空字符串和全空格字符，若需要对 `null、空字符串和全空格字符串` 判断，见 [isEmpty]。
     *
     * @param text 字符串，可以为空。
     * @return 字符串是否为空。
     */
    @JvmStatic
    fun <S : CharSequence> isBlank(text: S?): Boolean {
        if (text == null) return false
        return "$text".isBlank()
    }

    /**
     * 判断字符串是否不为空字符串和全空格字符，与 [isBlank] 方法相反。
     *
     * @param text 字符串，可以为空。
     * @return 字符串是否为空。
     */
    @JvmStatic
    fun <S : CharSequence> isNotBlank(text: S?): Boolean {
        if (text == null) return false
        return "$text".isBlank()
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
     * 判断字符串是否不为空，与 [isEmpty] 方法相反。
     *
     * @param text 字符串，可以为空。
     * @return 字符串是否不为空。
     */
    @JvmStatic
    fun <S : CharSequence> isNotEmpty(text: S?): Boolean {
        return !isEmpty(text)
    }

    /**
     * 判断一个字符串是否与任何一个字符串数组元素相等。
     *
     * @param text 要检查的字符串
     * @param arr 字符串数组，要比较的对象
     * @return 如果 text 与 arr 中任意一个字符串相等，则返回 true，否则返回 false
     */
    @JvmStatic
    fun <S : CharSequence> anyEquals(text: S?, vararg arr: S): Boolean {
        return arr.map { "$text" == it }.contains(true)
    }

    /**
     * 判断一个字符串是否包含任何一个数组元素值。
     *
     * @param text 要检查的字符串
     * @param arr 字符串数组，要比较的对象
     * @return 如果 text 包含 arr 中任意一个字符串，则返回 true，否则返回 false
     */
    @JvmStatic
    fun <S : CharSequence> anyContains(text: S?, vararg arr: S): Boolean {
        if (text == null) return false
        return arr.map { "$text".contains(it) }.contains(true)
    }

    /**
     * 判断一个字符串是否包含数组元素中的所有值。
     *
     * @param text 要检查的字符串
     * @param arr 字符串数组，要比较的对象
     * @return 如果 text 包含 arr 中的所有字符子串，则返回 true，否则返回 false
     */
    @JvmStatic
    fun <S : CharSequence> allContains(text: S?, vararg arr: S): Boolean {
        if (text == null) return false
        return !arr.map { "$text".contains(it) }.contains(false)
    }

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
    fun <S : CharSequence> get(text: S?, default: String = ""): String {
        if (isEmpty(text)) return default
        return "$text"
    }

    /**
     * 获取左边字符子串，如果该字符串未出现，则返回默认值。
     * see: [String.substringBefore]
     *
     * @param text 字符串，可以为空。
     * @param delimiter 分割字符串。
     * @param default 默认值。
     * @return 截取后的子串或默认值。
     */
    @JvmStatic
    @JvmOverloads
    fun <S : CharSequence> left(text: S?, delimiter: String, default: String = ""): String {
        if (isEmpty(text)) return default
        val index = text!!.indexOf(delimiter)
        return if (index == -1) "$text" else text.substring(0, index)
    }

    /**
     * 获取右边字符子串，如果该字符串未出现，则返回默认值。
     * see: [String.substringAfter]
     *
     * @param text 字符串，可以为空。
     * @param delimiter 分割字符串。
     * @param default 默认值。
     * @return 截取后的子串或默认值。
     */
    @JvmStatic
    @JvmOverloads
    fun <S : CharSequence> right(text: S?, delimiter: String, default: String = ""): String {
        if (isEmpty(text)) return default
        val index = text!!.indexOf(delimiter)
        return if (index == -1) "$text" else text.substring(index + delimiter.length, text.length)
    }

    /**
     * 禁止某个字符串中的某些字符连续出现，如果出现则只保留该字符的唯一出现 (字符串去重)。
     *
     * @param text 输入的字符串，可为空。如果为空，则返回空字符串。
     * @param target 需要禁止连续出现的字符，可为空。如果为空，则禁止所有字符连续出现。
     *
     * @return 返回处理后的字符串。如果输入字符串为空，则返回空字符串。
     *
     * 例1: text = "abcaabccddeef" -> return  "abcabcdef"
     * 例2: text = "abcaabccddeef", target = "e" -> return "abcaabccddef"
     */
    @JvmStatic
    @JvmOverloads
    fun <S : CharSequence> removeConsecutive(text: S?, target: Char? = null): String {
        text ?: return ""

        val stringBuilder = StringBuilder()
        var prevChar: Char? = null

        for (char in text) {
            if (prevChar == null || char != prevChar || (target != null && char != target)) {
                stringBuilder.append(char)
                prevChar = char
            }
        }

        return stringBuilder.toString()
    }

    /**
     * 对给定的文本进行省略处理，返回指定长度的字符串。
     *
     * @param text 要进行省略处理的文本，可以为null。
     * @param length 最大长度，表示要截取的字符数。
     * @return 处理后的字符串，如果文本为空或长度小于等于指定长度，则返回原始文本。
     */
    @JvmStatic
    fun <S : CharSequence> ellipsis(text: S?, length: Int): String? {
        if (text == null || text.length <= length || length <= 0) {
            return text?.toString()
        }

        val ellipsis = "..."
        val truncatedText = text.subSequence(0, length).toString()
        return truncatedText + ellipsis
    }

    /**
     * 将字符串数组按指定字符串做分割符并拼接为一个字符串。see: [Array.joinToString]
     *
     * @param texts 字符串数组
     * @param s 用作拼接的字符串
     */
    @JvmStatic
    fun <S : CharSequence> splicing(texts: Array<S>, s: String): String {
        return texts.joinToString(s)
    }

    /**
     * 将字符串集合按指定字符串做分割符并拼接为一个字符串。see: [Collection.joinToString]
     *
     * @param texts 字符串集合
     * @param s 用作拼接的字符串
     */
    @JvmStatic
    fun <S : CharSequence> splicing(texts: Collection<S>, s: String): String {
        return texts.joinToString(s)
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
    fun <S : CharSequence> trim(text: S?, default: String = ""): String {
        if (isEmpty(text)) return default
        return "$text".trim()
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
    fun <S : CharSequence> trimStart(text: S?, default: String = ""): String {
        if (isEmpty(text)) return default
        return "$text".trimStart()
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
    fun <S : CharSequence> trimEnd(text: S?, default: String = ""): String {
        if (isEmpty(text)) return default
        return "$text".trimEnd()
    }

    /**
     * 对某个字符串unicode解码。
     *
     * @param text 需要被unicode编码的字符串
     * @return unicode解码之后的字符串
     */
    @JvmStatic
    fun <S : CharSequence> deUnicode(text: S?): String {
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
     * 对某个字符串unicode编码。
     *
     * @param text 需要被unicode编码的字符串
     * @return unicode编码之后的字符串
     */
    @JvmStatic
    fun <S : CharSequence> enUnicode(text: S?): String {
        text ?: return ""
        val stringBuilder = StringBuilder()
        for (char in text) {
            stringBuilder.append("\\u${char.toInt().toString(16).padStart(4, '0')}")
        }
        return stringBuilder.toString()
    }

    /**
     * 对给定的字符串在开始处进行填充以达到指定的长度。
     *
     * @param text 需要进行填充的字符串。如果为null，则返回空字符串。
     * @param length 目标长度。如果此长度小于原字符串的长度，原字符串将保持不变。
     * @param padChar 用于填充的字符，默认为空格。
     * @return 填充后的字符串。如果输入字符串为null，返回空字符串。
     */
    @JvmStatic
    @JvmOverloads
    fun <S : CharSequence> padStart(text: S?, length: Int, padChar: Char = ' '): String {
        text ?: return ""
        return "$text".padStart(length, padChar)
    }

    /**
     * 将对给定对象转换为字符串[toString], 并对该字符串在开始处进行填充以达到指定的长度。
     *
     * @param any 需要进行填充的对象。如果为null，则返回空字符串。
     * @param length 目标长度。如果此长度小于原字符串的长度，原字符串将保持不变。
     * @param padChar 用于填充的字符，默认为空格。
     * @return 填充后的字符串。如果输入字符串为null，返回空字符串。
     */
    @JvmStatic
    @JvmOverloads
    fun padStart(any: Any?, length: Int, padChar: Char = ' '): String {
        any ?: return ""
        return "$any".padStart(length, padChar)
    }

    /**
     * 将对给定字符串在结尾处进行填充以达到指定的长度。
     *
     * @param text 需要进行填充的对象。如果为null，则返回空字符串。
     * @param length 目标长度。如果此长度小于原字符串的长度，原字符串将保持不变。
     * @param padChar 用于填充的字符，默认为空格。
     * @return 填充后的字符串。如果输入字符串为null，返回空字符串。
     */
    @JvmStatic
    @JvmOverloads
    fun <S : CharSequence> padEnd(text: S?, length: Int, padChar: Char = ' '): String {
        text ?: return ""
        return "$text".padEnd(length, padChar)
    }

    /**
     * 将对给定对象转换为字符串[toString], 并对该字符串在结尾处进行填充以达到指定的长度。
     *
     * @param any 需要进行填充的对象。如果为null，则返回空字符串。
     * @param length 目标长度。如果此长度小于原字符串的长度，原字符串将保持不变。
     * @param padChar 用于填充的字符，默认为空格。
     * @return 填充后的字符串。如果输入字符串为null，返回空字符串。
     */
    @JvmStatic
    @JvmOverloads
    fun padEnd(any: Any?, length: Int, padChar: Char = ' '): String {
        any ?: return ""
        return "$any".padEnd(length, padChar)
    }

    /**
     * 根据指定长度随机生成一个乱序字符串
     * 字符串中的字符来自ASCII码表中的可打印字符
     *
     * @param length 字符串的长度
     * @return 生成的随机字符串
     */
    @JvmStatic
    fun random(length: Int): String {
        val charPool: List<Char> = (32..126).map { it.toChar() }
        return (1..length)
            .asSequence()
            .map { Random.nextInt(0, charPool.size) }
            .map(charPool::get)
            .joinToString("")
    }

    /**
     * 根据指定最小长度和最大长度随机生成一个乱序字符串
     * 字符串中的字符来自ASCII码表中的可打印字符
     *
     * @param min 字符串的最小长度
     * @param max 字符串的最大长度
     * @return 生成的随机字符串
     */
    @JvmStatic
    fun random(min: Int, max: Int): String {
        val charPool: List<Char> = (32..126).map { it.toChar() }
        val length = Random.nextInt(min, max + 1)
        return (1..length)
            .asSequence()
            .map { Random.nextInt(0, charPool.size) }
            .map(charPool::get)
            .joinToString("")
    }

    /**
     * 根据指定的字符表随机生成一个指定长度的乱序字符串
     *
     * @param dict 提供的字符表
     * @param length 字符串的长度
     * @return 生成的随机字符串
     */
    @JvmStatic
    fun random(dict: String, length: Int): String {
        val dictLength = dict.length
        val randomChars = CharArray(length)
        for (i in 0 until length) {
            val randomIndex = Random.nextInt(dictLength)
            randomChars[i] = dict[randomIndex]
        }
        return String(randomChars)
    }

    /**
     * 根据指定长度随机生成一个乱序字母字符串
     *
     * @param length 字符串的长度
     * @return 生成的随机字母字符串
     */
    @JvmStatic
    fun randomAlphabet(length: Int): String {
        val alphabets = CharArray(length)
        for (i in 0 until length) {
            val randomInt = Random.nextInt(52)
            alphabets[i] = when {
                randomInt < 26 -> (randomInt + 97).toChar()
                else -> ((randomInt - 26) + 65).toChar()
            }
        }
        return String(alphabets)
    }

    /**
     * 根据指定长度随机生成一个乱序数字串
     *
     * @param length 字符串的长度
     * @return 生成的随机数字字符串
     */
    @JvmStatic
    fun randomNum(length: Int): String {
        val numbers = CharArray(length)
        for (i in 0 until length) {
            numbers[i] = (Random.nextInt(0, 10) + 48).toChar()
        }
        return String(numbers)
    }


    ////  number  ////
    /**
     * 判断一个字符串是否是数字，可以是整数或浮点数。
     *
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
     * 判断一个字符串是否是整数。
     *
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
     * 将一个字符串转换为浮点数。
     *
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
     * 将一个字符串转换为双精度浮点数。
     *
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
     * 将一个字符串转换为整数。
     *
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

    /**
     * 将一个字符串转换为长整数。
     *
     * @param text 要转换的字符串
     * @param default 转换失败时的默认值
     * @return 如果转换成功，则返回该整数，否则返回默认值
     */
    @JvmStatic
    @JvmOverloads
    fun <S : CharSequence> toLong(text: S?, default: Long = 0L): Long {
        return try {
            "$text".toLong()
        } catch (e: NumberFormatException) {
            default
        }
    }
}