package com.freegang.extension

/**
 * 某个数字是否在集合索引范围内
 */
fun Collection<*>.inIndex(index: Int): Boolean {
    return (index >= 0) and (index < this.size)
}

/**
 * 某个集合不为空, 则返回它的DSL扩展
 */
inline fun <reified T : Collection<*>> T.ifNotEmpty(block: T.() -> Unit) {
    if (isEmpty()) return
    block.invoke(this)
}

/**
 * 某个集合不为空, 则返回它的DSL扩展
 */
inline fun <reified T : Sequence<*>> T.ifNotEmpty(block: T.() -> Unit) {
    if (any()) return
    block.invoke(this)
}

/**
 * 某个数字是否在数组索引范围内
 */
fun Array<*>.inIndex(index: Int): Boolean {
    return (index >= 0) and (index < this.size)
}

/**
 * 获取某个值或返回null
 */
inline fun <reified T> Array<T>.getOrNull(index: Int): T? {
    return if (index in indices) {
        get(index)
    } else {
        null
    }
}

/**
 * 某个数组不为空, 则返回它的DSL扩展
 */
inline fun <T> Array<T>.ifNotEmpty(block: Array<T>.() -> Unit) {
    if (isEmpty()) return
    block.invoke(this)
}