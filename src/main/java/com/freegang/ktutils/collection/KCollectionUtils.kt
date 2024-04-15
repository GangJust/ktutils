package com.freegang.ktutils.collection

object KCollectionUtils {
    /**
     * 返回元素的第一个索引，如果数组不包含元素，则返回-1。
     */
    @JvmStatic
    fun <T> indexOf(array: Array<T>, value: T): Int {
        return array.indexOf(value)
    }

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
     * 只是为了方便java调用
     */
    @JvmStatic
    fun <T> filter(
        c: Collection<T>,
        predicate: Predicate<T>,
    ): List<T> {
        return c.filter { predicate.predicate(it) }
    }

    /**
     * 只是为了方便java调用
     */
    @JvmStatic
    fun <T> filter(
        c: Array<T>,
        predicate: Predicate<T>,
    ): List<T> {
        return c.filter { predicate.predicate(it) }
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
        return c1.minus(c2.toSet())
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
        return c1.intersect(c2.toSet())
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

    fun interface Transform<T> {
        fun transform(t: T): CharSequence
    }

    fun interface TransformMap<T, R> {
        fun map(t: T): R
    }

    fun interface Predicate<T> {
        fun predicate(value: T): Boolean
    }
}