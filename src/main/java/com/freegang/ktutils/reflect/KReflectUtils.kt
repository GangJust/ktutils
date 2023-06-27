package com.freegang.ktutils.reflect

import java.lang.reflect.Field
import java.lang.reflect.Method

object KReflectUtils {

}

private val fieldCache = mutableMapOf<String, Field>()
private val methodCache = mutableMapOf<String, Method>()

/**
 * 扩展方法, 找到某个字段, 并将其开放权限
 * @param name 字段名
 */
fun Any.findField(name: String): Field {
    val targetClazz = if (this is Class<*>) {
        this
    } else {
        this::class.java
    }

    val key = "${this::class.java.name}#$name"
    return fieldCache.getOrPut(key) {
        var clazz: Class<*>? = targetClazz
        while (clazz != null) {
            try {
                val field = clazz.getDeclaredField(name)
                field.isAccessible = true
                return@getOrPut field
            } catch (e: NoSuchFieldException) {
                // Do nothing
            }
            clazz = clazz.superclass
        }
        throw NoSuchFieldException("Field $name not found in class $this or any of its superclasses")
    }
}

/**
 * 扩展方法, 找到某个字段, 并通过`对象实例`获取到该字段的`对象实例`
 * @param name 字段名
 */
fun Any.findFieldAndGet(name: String): Any? {
    return this::class.java.findField(name).get(this)
}

/**
 * 扩展方法, 找到某个方法, 并将其开放权限
 *
 * @param name 方法名
 * @param parameterTypes 参数类型列表
 */
fun Any.findMethod(name: String, vararg parameterTypes: Class<*>): Method {
    val parameters = if (parameterTypes.isNotEmpty()) parameterTypes.joinToString(",") { it.name } else ""
    val key = "${this::class.java}#$name($parameters)"

    val targetClazz = if (this is Class<*>) {
        this
    } else {
        this::class.java
    }

    return methodCache.getOrPut(key) {
        var currentClass: Class<*>? = targetClazz
        while (currentClass != null) {
            try {
                val method = currentClass.getDeclaredMethod(name, *parameterTypes)
                method.isAccessible = true
                return@getOrPut method
            } catch (e: NoSuchMethodException) {
                // do nothing
            }
            currentClass = currentClass.superclass
        }
        throw NoSuchMethodException("Method $name with parameters $parameters not found in class $this or any of its superclasses")
    }
}

/**
 * 扩展方法, 找到某个方法, 并通过`对象实例`直接调用该`方法`
 *
 * @param name 方法名
 * @param args 参数列表
 */
fun Any.findMethodAndInvoke(name: String, vararg args: Any): Any? {
    val method = this::class.java.findMethod(name, *args.map { it::class.java }.toTypedArray())
    return method.invoke(this, *args)
}