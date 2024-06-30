package com.freegang.ktutils.reflect

import kotlin.reflect.KClass

object KReflectUtils {
    private val cacheClazz = mutableMapOf<Class<*>, ClassBuilder>()

    /**
     * 对某个的Class或instance进行反射构建绑定
     *
     * @param any Class或instance
     */
    @JvmStatic
    fun reflect(any: Any): ClassBuilder {
        val clazz = when (any) {
            is KClass<*> -> any.java
            is Class<*> -> any
            else -> any.javaClass
        }
        return cacheClazz.getOrPut(clazz) { ClassBuilder(clazz) }
    }

    /**
     * 通过class判断两个类是否分别为 基础类型 和 包装类型
     *
     * @param c1
     * @param c2
     */
    @JvmStatic
    fun isCompatible(c1: Class<*>, c2: Class<*>): Boolean {
        return if (c1.isPrimitive) {
            getWrapperClass(c1) == c2
        } else if (c2.isPrimitive) {
            getWrapperClass(c2) == c1
        } else {
            false
        }
    }

    /**
     * 返回对应基础类型的包装类
     *
     * @param primitiveClazz 基础类型
     */
    @JvmStatic
    fun getWrapperClass(primitiveClazz: Class<*>): Class<*> {
        return when (primitiveClazz) {
            Boolean::class.javaPrimitiveType -> Boolean::class.javaObjectType
            Byte::class.javaPrimitiveType -> Byte::class.javaObjectType
            Char::class.javaPrimitiveType -> Char::class.javaObjectType
            Short::class.javaPrimitiveType -> Short::class.javaObjectType
            Int::class.javaPrimitiveType -> Int::class.javaObjectType
            Long::class.javaPrimitiveType -> Long::class.javaObjectType
            Float::class.javaPrimitiveType -> Float::class.javaObjectType
            Double::class.javaPrimitiveType -> Double::class.javaObjectType
            else -> primitiveClazz
        }
    }

    /**
     * 比较类型
     *
     * @param type 类型1
     * @param targetType 类型2
     * @param isAssignableFrom 是否互相比较继承关系
     */
    @JvmStatic
    fun compareType(
        type: Class<*>,
        targetType: Class<*>,
        isAssignableFrom: Boolean,
    ): Boolean {
        // 类直接比较
        if (type == targetType) {
            return true
        }

        // 基本数据类型比较
        if (isCompatible(type, targetType)) {
            return true
        }

        // 继承关系比较
        return isAssignableFrom && isAssignableFrom(type, targetType)
    }

    /**
     * 比较类型列表
     *
     * @param types 类型列表1
     * @param targetTypes 类型列表2
     * @param isAssignableFrom 是否互相比较继承关系
     */
    @JvmStatic
    fun compareTypes(
        types: Array<out Class<*>>,
        targetTypes: List<Class<*>?>,
        isAssignableFrom: Boolean,
    ): Boolean {
        if (types.size != targetTypes.size)
            return false

        for (i in types.indices) {
            val type = types[i]
            val targetType = targetTypes[i] ?: continue // null则模糊匹配

            val cmp = compareType(type, targetType, isAssignableFrom)
            if (!cmp)
                return false
        }

        return true
    }

    /**
     * 判断两个类是否互为继承关系
     *
     * @param c1
     * @param c2
     */
    @JvmStatic
    fun isAssignableFrom(c1: Class<*>, c2: Class<*>): Boolean {
        if (c1 == Any::class.java || c2 == Any::class.java) return false
        return c1.isAssignableFrom(c2) || c2.isAssignableFrom(c1)
    }
}