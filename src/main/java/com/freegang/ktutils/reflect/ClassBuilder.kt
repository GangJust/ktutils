package com.freegang.ktutils.reflect

import java.lang.reflect.Field
import java.lang.reflect.Method

class ClassBuilder(private val clazz: Class<*>) {
    private val mFields: List<Field> by lazy {
        val list = mutableListOf<Field>()
        var currentClass = clazz
        while (currentClass != Any::class.java) {
            currentClass.declaredFields.forEach {
                runCatching { it.isAccessible = true }
                list.add(it)
            }
            currentClass = currentClass.superclass
        }

        list
    }

    private val mMethods: List<Method> by lazy {
        val list = mutableListOf<Method>()
        var currentClass = clazz
        while (currentClass != Any::class.java) {
            currentClass.declaredMethods.forEach {
                runCatching { it.isAccessible = true }
                list.add(it)
            }
            currentClass = currentClass.superclass
        }

        list
    }

    /**
     * 字段列表，含父类所有字段
     */
    val fields: List<Field>
        get() = mFields

    /**
     * 方法列表，含父类所有方法
     */
    val methods: List<Method>
        get() = mMethods

    /**
     * 返回字段搜索构建
     */
    fun findField(): FieldFindBuilder {
        return FieldFindBuilder(mFields)
    }

    /**
     * 返回字段搜索构建DSL
     */
    @JvmName("_findField_")
    fun findField(block: FieldFindBuilder.() -> Unit): FiledFind {
        val builder = FieldFindBuilder(mFields)
        block.invoke(builder)
        return builder
    }

    /**
     * 返回方法搜索构建
     */
    fun findMethod(): MethodFindBuilder {
        return MethodFindBuilder(mMethods)
    }

    /**
     * 返回方法搜索构建DSL
     */
    @JvmName("_findMethod_")
    fun findMethod(block: MethodFindBuilder.() -> Unit): MethodFind {
        val builder = MethodFindBuilder(mMethods)
        block.invoke(builder)
        return builder
    }
}