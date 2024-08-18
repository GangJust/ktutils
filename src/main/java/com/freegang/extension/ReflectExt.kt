package com.freegang.extension

import com.freegang.ktutils.reflect.ConstructorFind
import com.freegang.ktutils.reflect.ConstructorFindBuilder
import com.freegang.ktutils.reflect.FieldFindBuilder
import com.freegang.ktutils.reflect.FiledFind
import com.freegang.ktutils.reflect.KReflectUtils
import com.freegang.ktutils.reflect.MethodFind
import com.freegang.ktutils.reflect.MethodFindBuilder
import java.lang.reflect.Field
import java.lang.reflect.Method
import kotlin.reflect.KClass

/**
 * 直接获取类加载器
 */
val Any.classLoader: ClassLoader?
    get() {
        return when (this) {
            is Class<*> -> this.classLoader
            is KClass<*> -> this.java.classLoader
            else -> this.javaClass.classLoader
        }
    }

/**
 * 直接获取类名
 */
val Any.className: String
    get() {
        return when (this) {
            is Class<*> -> this.name
            is KClass<*> -> this.java.name
            else -> this.javaClass.name
        }
    }

/**
 * 直接获取字段列表，含父类字段
 */
fun Any.fields(): List<Field> {
    return KReflectUtils.reflect(this).fields
}

/**
 * 直接获取获取方法列表，含父类方法
 */
fun Any.methods(): List<Method> {
    return KReflectUtils.reflect(this).methods
}

/**
 * 获取字段值, 当字段内容为null时返回默认值
 *
 * @param any 对象
 * @param default 默认值
 */
inline fun <reified T> Field.getOrDefault(any: Any?, default: T): T {
    return get(any) as? T ?: default
}

/**
 * 返回字段搜索构建DSL
 */
fun Any.findField(
    block: FieldFindBuilder.() -> Unit,
): FiledFind {
    return KReflectUtils.reflect(this).findField(block)
}

/**
 * 查找并返回第一个匹配条件的字段
 *
 * @param block 构建DSL
 */
fun Any.findFieldFirst(
    block: FieldFindBuilder.() -> Unit,
): Field {
    return KReflectUtils.reflect(this).findField(block).first()
}

/**
 * 查找并返回第一个匹配条件的字段或null
 *
 * @param block 构建DSL
 */
fun Any.findFieldFirstOrNull(
    block: FieldFindBuilder.() -> Unit,
): Field? {
    return KReflectUtils.reflect(this).findField(block).firstOrNull()
}

/**
 * 返回字段搜索构建DSL，并设置值
 *
 * @param any 被设置的值, 当为null时应该主动指定类型
 * @param block 构建DSL，可空
 */
fun Any.findFieldSetValue(
    any: Any?,
    block: (FieldFindBuilder.() -> Unit)? = null,
) {
    val find = KReflectUtils.reflect(this).findField {
        if (any != null)
            type(any.javaClass, true)
        block?.invoke(this)
    }
    return find.setValueFirst(this, any)
}

/**
 * 返回字段搜索构建DSL，并获取值
 */
inline fun <reified T> Any.findFieldGetValue(
    noinline block: FieldFindBuilder.() -> Unit,
): T? {
    val find = KReflectUtils.reflect(this).findField(block)
    return find.getValueFirst(this) as T?
}

/**
 * 返回方法搜索构建DSL
 *
 * @param block 构建DSL
 */
fun Any.findMethod(
    block: MethodFindBuilder.() -> Unit,
): MethodFind {
    return KReflectUtils.reflect(this).findMethod(block)
}

/**
 * 查找并返回第一个匹配条件的方法
 *
 * @param block 构建DSL
 */
fun Any.findMethodFirst(
    block: MethodFindBuilder.() -> Unit,
): Method {
    return KReflectUtils.reflect(this).findMethod(block).first()
}

/**
 * 查找并返回第一个匹配条件的方法或null
 *
 * @param block 构建DSL
 */
fun Any.findMethodFirstOrNull(
    block: MethodFindBuilder.() -> Unit,
): Method? {
    return KReflectUtils.reflect(this).findMethod(block).firstOrNull()
}

/**
 * 返回方法搜索构建DSL，并通过传入参数调用
 *
 * @param args 参数值列表, null项将被模糊匹配为任意类型, 建议主动指定类型
 * @param block 构建DSL，可空
 */
inline fun <reified T> Any.findMethodInvoke(
    vararg args: Any?,
    noinline block: (MethodFindBuilder.() -> Unit)? = null,
): T? {
    val argTypes = args.map { it?.javaClass }
    val find = KReflectUtils.reflect(this).findMethod {
        parameterTypes(argTypes, true)
        block?.invoke(this)
    }
    return find.invokeFirst(this, *args) as T?
}

/**
 * 返回构造方法搜索构建
 */
fun Any.findConstructor(): ConstructorFindBuilder {
    return KReflectUtils.reflect(this).findConstructor()
}

/**
 * 返回构造方法搜索构建SDL
 *
 * @param block 构建DSL
 */
fun Any.findConstructor(
    block: ConstructorFindBuilder.() -> Unit,
): ConstructorFind {
    return KReflectUtils.reflect(this).findConstructor(block)
}

/**
 * 返回构造方法搜索构建DSL，并通过传入参数构建
 *
 * @param args 参数值列表, null项将被模糊匹配为任意类型, 建议主动指定类型
 * @param block 构建DSL，可空
 */
inline fun <reified T> Any.findNewInstance(
    vararg args: Any?,
    noinline block: (ConstructorFindBuilder.() -> Unit)? = null,
): T {
    val argTypes = args.map { it?.javaClass }
    val find = KReflectUtils.reflect(this).findConstructor {
        parameterTypes(argTypes, true)
        block?.invoke(this)
    }
    return find.newFirst(*args) as T
}