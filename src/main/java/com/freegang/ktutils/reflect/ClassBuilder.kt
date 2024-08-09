package com.freegang.ktutils.reflect

import java.lang.reflect.Constructor
import java.lang.reflect.Field
import java.lang.reflect.Method

class ClassBuilder(
    private val clazz: Class<*>,
) {
    private val mFields: List<Field> by lazy {
        val list = mutableListOf<Field>()
        var currentClass: Class<*>? = clazz
        while (currentClass != null && currentClass != Any::class.java) {
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
        var currentClass: Class<*>? = clazz
        while (currentClass != null && currentClass != Any::class.java) {
            currentClass.declaredMethods.forEach {
                runCatching { it.isAccessible = true }
                list.add(it)
            }
            currentClass = currentClass.superclass
        }

        list
    }

    private val mConstructors: List<Constructor<*>> by lazy {
        val list = mutableListOf<Constructor<*>>()
        var currentClass = clazz
        while (currentClass != Any::class.java) {
            currentClass.declaredConstructors.forEach {
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
     * 构造方法列表，含父类的所有构造方法
     */
    val constructors: List<Constructor<*>>
        get() = mConstructors

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
    fun findField(
        block: FieldFindBuilder.() -> Unit,
    ): FiledFind {
        val builder = findField()
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
    fun findMethod(
        block: MethodFindBuilder.() -> Unit,
    ): MethodFind {
        val builder = findMethod()
        block.invoke(builder)
        return builder
    }

    /**
     * 返回构造方法搜索构建
     */
    fun findConstructor(): ConstructorFindBuilder {
        return ConstructorFindBuilder(mConstructors)
    }

    /**
     * 返回构造方法搜索构建DSL
     */
    @JvmName("_findConstructor_")
    fun findConstructor(
        block: ConstructorFindBuilder.() -> Unit,
    ): ConstructorFind {
        val builder = findConstructor()
        block.invoke(builder)
        return builder
    }
}