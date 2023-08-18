package com.freegang.ktutils.reflect

import java.lang.reflect.Field
import java.lang.reflect.Method

object KReflectUtils {

    /**
     * 获取某个对象的所有字段, 包含其继承的父类字段, 同名字段顺序排列
     * @param obj 目标对象
     * @return 所有字段列表
     */
    fun getAllFields(obj: Any): List<Field> {
        val fields = mutableListOf<Field>()
        var currentClass: Class<*>? = if (obj.javaClass == Class::class.java) obj as Class<*> else obj.javaClass
        while (currentClass != null && currentClass != Any::class.java) {
            currentClass.declaredFields.forEach {
                it.isAccessible = true
                fields.add(it)
            }
            currentClass = currentClass.superclass
        }
        return fields
    }

    /**
     * 获取某个对象的所有方法 包含其继承的父类方法, 同名方法顺序排列
     * @param obj 目标对象
     * @return 所有方法列表
     */
    fun getAllMethods(obj: Any): List<Method> {
        val methods = mutableListOf<Method>()
        var currentClass: Class<*>? = if (obj.javaClass == Class::class.java) obj as Class<*> else obj.javaClass
        while (currentClass != null && currentClass != Any::class.java) {
            currentClass.declaredMethods.forEach {
                it.isAccessible = true
                methods.add(it)
            }
            currentClass = currentClass.superclass
        }
        return methods
    }

    /**
     * 按指定要求查找指定字段集合, 同名字段顺序排列
     * @param obj 目标对象
     * @param name 字段名, 可空
     * @param type 字段类型, 可空
     * @return 满足指定要求的指定字段列表
     */
    fun findFields(
        obj: Any,
        name: String? = null,
        type: Class<*>? = null,
    ): List<Field> {
        val allFields = getAllFields(obj)
        val filteredFields = mutableListOf<Field>()
        for (field in allFields) {
            if (name != null && type != null) {
                if (field.name == name && field.type == type) {
                    filteredFields.add(field)
                }
            } else if (name != null) {
                if (field.name == name) {
                    filteredFields.add(field)
                }
            } else if (type != null) {
                if (field.type == type) {
                    filteredFields.add(field)
                }
            } else {
                filteredFields.add(field)
            }
        }
        return filteredFields
    }

    /**
     * 按指定要求查找指定字段集合, 同名方法顺序排列
     * @param obj 目标对象
     * @param name 字段名, 可空
     * @param returnType 返回类型, 可空
     * @param paramTypes 参数列表类型, 可选
     * @return 满足指定要求的指定字段列表
     */
    fun findMethods(
        obj: Any,
        name: String? = null,
        returnType: Class<*>? = null,
        vararg paramTypes: Class<*>,
    ): List<Method> {
        val allMethods = getAllMethods(obj)
        val filteredMethods = mutableListOf<Method>()
        for (method in allMethods) {
            if (name != null && returnType != null && paramTypes.isNotEmpty()) {
                if (method.name == name &&
                    method.returnType == returnType &&
                    method.parameterTypes.contentEquals(paramTypes)
                ) {
                    filteredMethods.add(method)
                }
            } else if (name != null && returnType != null) {
                if (method.name == name && method.returnType == returnType) {
                    filteredMethods.add(method)
                }
            } else if (name != null && paramTypes.isNotEmpty()) {
                if (method.name == name && method.parameterTypes.contentEquals(paramTypes)) {
                    filteredMethods.add(method)
                }
            } else if (returnType != null && paramTypes.isNotEmpty()) {
                if (method.returnType == returnType && method.parameterTypes.contentEquals(paramTypes)) {
                    filteredMethods.add(method)
                }
            } else if (name != null) {
                if (method.name == name) {
                    filteredMethods.add(method)
                }
            } else if (returnType != null) {
                if (method.returnType == returnType) {
                    filteredMethods.add(method)
                }
            } else if (paramTypes.isNotEmpty()) {
                if (method.parameterTypes.contentEquals(paramTypes)) {
                    filteredMethods.add(method)
                }
            } else {
                filteredMethods.add(method)
            }
        }

        return filteredMethods
    }
}

val Any.classLoader: ClassLoader?
    get() {
        return if (this.javaClass == Class::class.java) {
            (this as Class<*>).classLoader
        } else {
            this.javaClass.classLoader
        }
    }

fun Any.fields(
    name: String? = null,
    type: Class<*>? = null,
): List<Field> {
    return KReflectUtils.findFields(this, name, type)
}

fun Any.fieldGets(
    name: String? = null,
    type: Class<*>? = null,
): List<Any?> {
    return fields(name, type).map {
        try {
            it.get(this)
        } catch (e: Exception) {
            null
        }
    }
}

fun Any.methods(
    name: String? = null,
    returnType: Class<*>? = null,
    vararg paramTypes: Class<*>,
): List<Method> {
    return KReflectUtils.findMethods(this, name, returnType, *paramTypes)
}

fun Any.methodInvokes(
    name: String? = null,
    vararg args: Any,
): List<Any?> {
    val typedArray = args.map { it::class.java }.toTypedArray()
    return methods(name = name, paramTypes = typedArray).map {
        try {
            it.invoke(this, *args)
        } catch (e: Exception) {
            null
        }
    }
}