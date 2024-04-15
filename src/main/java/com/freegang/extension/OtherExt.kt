package com.freegang.extension

/**
 * 类型转换，如果转换失败则返回null
 */
inline fun <reified T> Any.asOrNull(): T? {
    return if (this is T) {
        this
    } else {
        null
    }
}

/**
 * 兼容Java和Kotlin的基本数据类型类的比较
 *
 * Kotlin中对于Java基本数据的区别在于如下
 * val int:Int = 0   //对应java中的 int
 * val int:Int? = 0 //对应java中的 Integer
 */
val Class<*>.isPrimitiveObjectType: Boolean
    get() {
        return when (this) {
            Boolean::class.javaPrimitiveType, Boolean::class.javaObjectType -> true
            Byte::class.javaPrimitiveType, Byte::class.javaObjectType -> true
            Char::class.javaPrimitiveType, Char::class.javaObjectType -> true
            Short::class.javaPrimitiveType, Short::class.javaObjectType -> true
            Int::class.javaPrimitiveType, Int::class.javaObjectType -> true
            Long::class.javaPrimitiveType, Long::class.javaObjectType -> true
            Float::class.javaPrimitiveType, Float::class.javaObjectType -> true
            Double::class.javaPrimitiveType, Double::class.javaObjectType -> true
            else -> false
        }
    }