package com.freegang.ktutils.collection

object KCollectionUtils {

    /**
     * 只是为了方便java调用
     */
    @JvmStatic
    @JvmOverloads
    fun <T> joinToString(
        c: Collection<T>,
        separator: CharSequence = ", ",
        transform: Transform<T>? = null
    ): String {
        transform ?: return c.joinToString(separator = separator)
        return c.joinToString(separator = separator) { transform.transform(it) }
    }

    /**
     * 只是为了方便java调用
     */
    @JvmStatic
    @JvmOverloads
    fun <T> joinToString(
        arr: Array<T>,
        separator: CharSequence = ", ",
        transform: Transform<T>? = null
    ): String {
        transform ?: return arr.joinToString(separator = separator)
        return arr.joinToString(separator = separator) { transform.transform(it) }
    }

    /**
     * 返回在第一个集合中存在但不在第二个集合中存在的元素的集合。
     *
     * @param c1 第一个集合
     * @param c2 第二个集合
     * @return 结果集合，包含在 c1 中存在但不在 c2 中存在的元素
     */
    @JvmStatic
    fun <T> except(c1: Collection<T>, c2: Collection<T>): Collection<T> {
        // 使用集合运算符 `minus` 获取在 c1 中存在但不在 c2 中存在的元素
        return c1.minus(c2)
    }

    /**
     * 返回两个集合的交集。
     *
     * @param c1 第一个集合
     * @param c2 第二个集合
     * @return 交集集合，包含同时存在于 c1 和 c2 中的元素
     */
    @JvmStatic
    fun <T> intersection(c1: Collection<T>, c2: Collection<T>): Collection<T> {
        // 使用集合运算符 `intersect` 获取两个集合的交集
        return c1.intersect(c2)
    }

    /**
     * 返回两个集合的并集。
     *
     * @param c1 第一个集合
     * @param c2 第二个集合
     * @return 并集集合，包含同时存在于 c1 或 c2 中的元素
     */
    @JvmStatic
    fun <T> union(c1: Collection<T>, c2: Collection<T>): Collection<T> {
        // 使用集合运算符 `plus` 将两个集合合并为一个集合
        return c1.plus(c2)
    }

    /**
     * 只是为了方便java调用
     */
    @JvmStatic
    fun <T, R> map(l: List<T>, transformMap: TransformMap<T, R>): List<R> {
        return l.map { transformMap.map(it) }
    }

    /**
     * 只是为了方便java调用
     */
    @JvmStatic
    fun <T, R> map(c: Array<T>, transformMap: TransformMap<T, R>): List<R> {
        return c.map { transformMap.map(it) }
    }

    @FunctionalInterface
    interface Transform<T> {
        fun transform(t: T): CharSequence
    }

    @FunctionalInterface
    interface TransformMap<T, R> {
        fun map(t: T): R
    }
}

// 某个数字是否在集合索引范围内
fun Collection<*>.inIndex(index: Int): Boolean {
    return (index >= 0) and (index < this.size)
}

// 某个数字是否在数组索引范围内
fun Array<*>.inIndex(index: Int): Boolean {
    return (index >= 0) and (index < this.size)
}

// 获取某个值或返回null
inline fun <reified T> Array<T>.getOrNull(index: Int): T? {
    return if (index in indices) {
        get(index)
    } else {
        null
    }
}

// 某个集合不为空, 则返回它的DSL扩展
inline fun <reified T : Collection<*>> T.ifNotEmpty(block: T.() -> Unit) {
    if (isEmpty()) return
    block.invoke(this)
}

inline fun <reified T : Sequence<*>> T.ifNotEmpty(block: T.() -> Unit) {
    if (any()) return
    block.invoke(this)
}

// 某个数组不为空, 则返回它的DSL扩展
inline fun <T> Array<T>.ifNotEmpty(block: Array<T>.() -> Unit) {
    if (isEmpty()) return
    block.invoke(this)
}