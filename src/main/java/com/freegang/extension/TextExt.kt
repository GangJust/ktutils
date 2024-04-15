package com.freegang.extension

import com.freegang.ktutils.text.KTextUtils

/**
 * 如果字符串不为空则执行，并返回block的执行结果，否则返回null。
 */
inline fun <S : CharSequence, R> S.ifNotEmpty(block: (S) -> R): R? {
    if (KTextUtils.isEmpty(this))
        return null

    return block.invoke(this)
}

/**
 * 如果字符串为空则执行，并返回block的执行结果，否则返回null。
 */
inline fun <S : CharSequence, R> S.ifEmpty(block: (S) -> R): R? {
    if (!KTextUtils.isEmpty(this))
        return null

    return block.invoke(this)
}

/**
 * 获取字符串的值，如果字符串为空，则返回默认值。
 *
 * @param default 默认值。
 * @return 字符串的值或默认值。
 */
fun <S : CharSequence> S.emptyOr(default: String): String {
    return KTextUtils.get(this, default)
}

/**
 * 获取左边字符子串，如果该字符串未出现，则返回默认值。
 * see: [String.substringBefore]
 *
 * @param delimiter 分割字符串。
 * @param default 默认值。
 * @return 截取后的子串或默认值。
 */
fun <S : CharSequence> S.left(delimiter: String, default: String): String {
    return KTextUtils.left(this, delimiter, default)
}

/**
 * 获取右边字符子串，如果该字符串未出现，则返回默认值。
 * see: [String.substringAfter]
 *
 * @param delimiter 分割字符串。
 * @param default 默认值。
 * @return 截取后的子串或默认值。
 */
fun <S : CharSequence> S.right(delimiter: String, default: String): String {
    return KTextUtils.right(this, delimiter, default)
}

/**
 * 对给定的文本进行省略处理，返回指定长度的字符串。
 *
 * @param length 最大长度，表示要截取的字符数。
 * @return 处理后的字符串，如果文本为空或长度小于等于指定长度，则返回原始文本。
 */
fun CharSequence.ellipsis(length: Int): String? {
    return KTextUtils.ellipsis(this, length)
}