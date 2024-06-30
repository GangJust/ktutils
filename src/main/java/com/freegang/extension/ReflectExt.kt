package com.freegang.extension

import com.freegang.ktutils.reflect.FieldFindBuilder
import com.freegang.ktutils.reflect.FiledFind
import com.freegang.ktutils.reflect.KReflectUtils
import com.freegang.ktutils.reflect.MethodFind
import com.freegang.ktutils.reflect.MethodFindBuilder
import java.lang.reflect.Field
import java.lang.reflect.Method

/**
 * 获取类加载器
 */
val Any.classLoader: ClassLoader?
    get() {
        return if (this.javaClass == Class::class.java) {
            (this as Class<*>).classLoader
        } else {
            this.javaClass.classLoader
        }
    }

fun Any.fields(): List<Field> {
    return KReflectUtils.reflect(this).fields
}

fun Any.methods(): List<Method> {
    return KReflectUtils.reflect(this).methods
}

/**
 * 返回字段搜索构建
 */
fun Any.findField(): FieldFindBuilder {
    return KReflectUtils.reflect(this).findField()
}

/**
 * 返回字段搜索构建DSL
 */
fun Any.findField(block: FieldFindBuilder.() -> Unit): FiledFind {
    return KReflectUtils.reflect(this).findField(block)
}

/**
 * 返回字段搜索构建DSL，并设置值
 */
fun Any.findFieldSetValue(any: Any?, block: FieldFindBuilder.() -> Unit) {
    val find = KReflectUtils.reflect(this).findField(block)
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
 * 返回方法搜索构建
 */
fun Any.findMethod(): MethodFindBuilder {
    return KReflectUtils.reflect(this).findMethod()
}

/**
 * 返回方法搜索构建DSL
 */
fun Any.findMethod(block: MethodFindBuilder.() -> Unit): MethodFind {
    return KReflectUtils.reflect(this).findMethod(block)
}

/**
 * 返回方法搜索构建DSL，并通过传入参数调用
 */
inline fun <reified T> Any.findMethodInvoke(
    vararg args: Any?,
    noinline block: MethodFindBuilder.() -> Unit,
): T? {
    val find = KReflectUtils.reflect(this).findMethod(block)
    return find.invokeFirst(this, *args) as T?
}

