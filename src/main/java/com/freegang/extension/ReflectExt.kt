package com.freegang.extension

import com.freegang.ktutils.reflect.KReflectUtils
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

/**
 * 获取某个类/实例的所有字段
 *
 * @param name 字段名
 * @param type 字段类型
 */
fun Any.fields(
    name: String? = null,
    type: Class<*>? = null,
): List<Field> {
    return if (name == null && type == null) {
        KReflectUtils.getFields(this)
    } else {
        KReflectUtils.findFields(this, name, type)
    }
}

/**
 * 获取某个实例的所有字段的值
 *
 * @param name 字段名
 * @param type 字段类型
 */
fun Any.fieldGets(
    name: String? = null,
    type: Class<*>? = null,
): List<Any?> {
    return fields(name = name, type = type).map {
        try {
            it.get(this)
        } catch (e: Exception) {
            null
        }
    }
}

/**
 * 获取某个类/实例的某个字段
 *
 * @param name 字段名
 * @param type 字段类型
 */
fun Any.field(
    name: String? = null,
    type: Class<*>? = null,
): Field? {
    return KReflectUtils.findField(this, name, type)
}

/**
 * 获取某个实例的某个字段的值
 *
 * @param name 字段名
 * @param type 字段类型
 */
@Throws(IllegalArgumentException::class, IllegalAccessException::class)
fun Any.fieldGet(
    name: String? = null,
    type: Class<*>? = null,
): Any? {
    val field = field(name = name, type = type)
    return field?.get(this)
}

/**
 * 设置某个实例的某个字段的值
 *
 * @param name 字段名
 * @param value 字段值
 */
@Throws(IllegalArgumentException::class, IllegalAccessException::class)
fun Any.fieldSet(
    name: String,
    value: Any?,
) {
    val field = field(name = name, type = value?.javaClass)
    field?.set(this, value)
}

/**
 * 获取某个类/实例的所有方法
 *
 * @param name 方法名
 * @param returnType 返回值类型
 */
fun Any.methods(
    name: String? = null,
    returnType: Class<*>? = null,
    vararg paramTypes: Class<*>?,
): List<Method> {
    return if (name == null && returnType == null && paramTypes.isEmpty()) {
        KReflectUtils.getMethods(this)
    } else {
        KReflectUtils.findMethods(this, name, returnType, *paramTypes)
    }
}

/**
 * 获取某个类/实例的某个方法
 *
 * @param name 方法名
 * @param returnType 返回值类型
 */
fun Any.method(
    name: String? = null,
    returnType: Class<*>? = null,
    vararg paramTypes: Class<*>?,
): Method? {
    return KReflectUtils.findMethod(this, name, returnType, *paramTypes)
}

/**
 * 调用某个实例中符合条件的所有方法，返回所有方法的返回值
 *
 * @param name 方法名
 * @param returnType 返回值类型
 * @param args 方法参数
 */
fun Any.methodInvokes(
    name: String? = null,
    returnType: Class<*>? = null,
    vararg args: Any?,
): List<Any?> {
    val typedArray = args.map { it?.javaClass }.toTypedArray()
    return methods(name = name, returnType = returnType, paramTypes = typedArray).map {
        try {
            it.invoke(this, *args)
        } catch (e: Exception) {
            null
        }
    }
}

/**
 * 调用某个实例中符合条件的某个方法，返回方法的返回值
 *
 * @param name 方法名
 * @param returnType 返回值类型
 * @param args 方法参数
 */
@Throws(IllegalArgumentException::class, IllegalAccessException::class)
fun Any.methodInvoke(
    name: String? = null,
    returnType: Class<*>? = null,
    vararg args: Any,
): Any? {
    val typedArray = args.map { it.javaClass }.toTypedArray()
    val method = method(name = name, returnType = returnType, paramTypes = typedArray)
    return method?.invoke(this, *args)
}