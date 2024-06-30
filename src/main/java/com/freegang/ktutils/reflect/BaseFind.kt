package com.freegang.ktutils.reflect


interface BaseFind<T> {
    fun forEach(action: (T) -> Unit)

    fun onEach(action: (T) -> Unit): List<T>

    fun isEmpty(): Boolean

    fun toList(): List<T>

    /**
     * 下标获取查找列表项
     *
     * @param index 下标
     */
    operator fun get(index: Int): T

    /**
     * 查找匹配，返回第一项，并确保不为null
     */
    fun first(): T

    /**
     * 查找匹配，返回第一项，并可以为null
     */
    fun fistOrNull(): T?

    /**
     * 查找匹配，返回最后一项，并确保不为null
     */
    fun last(): T

    /**
     * 查找匹配，返回最后一项，并可以为null
     */
    fun lastOrNull(): T?
}