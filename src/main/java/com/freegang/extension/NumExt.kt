package com.freegang.extension

/**
 * 从当前整数到指定的结束整数进行迭代，并在每次迭代时执行指定的动作。
 *
 * @param end 目标整数，迭代的结束点。
 * @param inclusive 是否包含结束点在内。默认为 true，即包含结束点。
 * @param action 在每次迭代时需要执行的动作，该动作以当前迭代的整数为参数。
 */
inline fun Int.rangeToInclusive(
    end: Int,
    inclusive: Boolean = true,
    action: (Int) -> Unit,
) {
    // 10 -> 0
    if (end < this) {
        val finalTo = if (inclusive) end else end + 1
        for (i in this downTo finalTo) action.invoke(i)
    } else { // 0 -> 10
        val finalTo = if (inclusive) end else end - 1
        for (i in this..finalTo) action.invoke(i)
    }
}


/**
 * 对范围内的每个整数执行变换操作，并返回最终结果。
 *
 * @param start 循环开始的整数。
 * @param initialValue 循环开始前的初始值。
 * @param inclusiveEnd 是否包含结束值在内。默认为false，即不包含。
 * @param transform 对每个整数执行的变换操作，接收上一次的结果，并返回新的结果。
 *
 * @return 经过所有变换操作后的最终结果。
 */
inline fun <T> Int.iterateAndTransform(
    start: Int,
    initialValue: T,
    inclusiveEnd: Boolean = false,
    transform: (previous: T) -> T,
): T {
    var result: T = initialValue

    if (inclusiveEnd) {
        for (i in start..this) {
            result = transform(result)
        }
        return result
    }

    for (i in start until this) {
        result = transform(result)
    }
    return result
}