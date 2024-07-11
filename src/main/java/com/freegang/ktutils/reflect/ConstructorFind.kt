package com.freegang.ktutils.reflect

import java.lang.reflect.Constructor

interface ConstructorFind : BaseFind<Constructor<*>> {
    /**
     * 构建查找匹配到的第一项
     *
     * @param args 参数列表
     */
    fun newFirst(vararg args: Any?): Any

    /**
     * 构建查找匹配到的最后一项
     *
     * @param args 参数列表
     */
    fun newLast(vararg args: Any?): Any

    /**
     * 构建指定下标项
     */
    fun newIndex(index: Int, vararg args: Any?): Any
}

class ConstructorFindBuilder(
    private val constructors: List<Constructor<*>>,
) : ConstructorFind {
    // 修饰符
    private var mModifiers: Int? = null

    // 参数类型列表
    private var mParameterTypes: List<Class<*>?>? = null
    private var mParameterTypesAsFrom: Boolean = false

    // 异常类型列表
    private var mExceptionTypes: List<Class<*>?>? = null
    private var mExceptionTypesAsFrom: Boolean = false

    // 方法注解列表
    private var mAnnotationTypes: List<Class<*>?>? = null

    // 参数列表注解 - future

    // 其他外置条件
    private var mFindPredicate: FindConstructorPredicate? = null

    // 存放查找过的列表
    private var finded: List<Constructor<*>> = listOf()

    /**
     * 修饰符
     *
     * @param modifiers 修饰符, example: `Modifier.PUBLIC or Modifier.STATIC`
     */
    fun modifiers(modifiers: Int): ConstructorFindBuilder {
        this.mModifiers = modifiers

        return this
    }

    /**
     * 参数类型列表
     *
     * @param parameterTypes 类型列表
     * @param isAssignableFrom 是否互相比较继承关系
     */
    fun parameterTypes(
        parameterTypes: List<Class<*>?>,
        isAssignableFrom: Boolean = false,
    ): ConstructorFindBuilder {
        this.mParameterTypes = parameterTypes
        this.mParameterTypesAsFrom = isAssignableFrom

        return this
    }

    /**
     * 异常类型列表
     *
     * @param exceptionTypes 类型列表
     * @param isAssignableFrom 是否互相比较继承关系
     */
    fun exceptionTypes(
        exceptionTypes: List<Class<*>?>,
        isAssignableFrom: Boolean = false,
    ): ConstructorFindBuilder {
        this.mExceptionTypes = exceptionTypes
        this.mExceptionTypesAsFrom = isAssignableFrom

        return this
    }

    /**
     * 方法注解类型列表
     *
     * @param annotation 类型列表
     */
    fun annotations(
        annotation: List<Class<*>?>,
    ): ConstructorFindBuilder {
        this.mAnnotationTypes = annotation

        return this
    }

    /**
     * 其他外置条件
     *
     * @param predicate 外置判断条件
     */
    fun predicate(predicate: FindConstructorPredicate) {
        this.mFindPredicate = predicate
    }

    private fun checkRules() {
        val rules = mModifiers == null
                && mParameterTypes == null
                && mExceptionTypes == null
                && mAnnotationTypes == null

        if (rules)
            throw IllegalArgumentException("at least one matching rule is required.")
    }

    private fun finds(): List<Constructor<*>> {
        checkRules()

        if (finded.isNotEmpty())
            return finded

        var sequence = constructors.asSequence()

        if (mModifiers != null) {
            sequence = sequence.filter {
                it.modifiers == mModifiers
            }
        }

        if (mParameterTypes != null) {
            sequence = sequence.filter {
                KReflectUtils.compareTypes(it.parameterTypes, mParameterTypes!!, mParameterTypesAsFrom)
            }
        }

        if (mExceptionTypes != null) {
            sequence = sequence.filter {
                KReflectUtils.compareTypes(it.exceptionTypes, mExceptionTypes!!, mExceptionTypesAsFrom)
            }
        }

        if (mAnnotationTypes != null) {
            sequence = sequence.filter {
                val annotations = it.annotations
                for (i in annotations.indices) {
                    val annotation = annotations[i]
                    val targetAnnotation = mAnnotationTypes!![i] ?: continue

                    if (annotation.annotationClass.java != targetAnnotation)
                        return@filter false
                }

                true
            }
        }

        if (mFindPredicate != null) {
            sequence = sequence.filter {
                mFindPredicate!!.predicate(it)
            }
        }

        return sequence.toList()
            .also { finded = it }
    }

    override fun forEach(action: (Constructor<*>) -> Unit) {
        finds().forEach(action)
    }

    override fun onEach(action: (Constructor<*>) -> Unit): List<Constructor<*>> {
        return finds().onEach(action)
    }

    override fun isEmpty(): Boolean {
        return finds().isEmpty()
    }

    override fun toList(): List<Constructor<*>> {
        return finds()
    }

    override fun get(index: Int): Constructor<*> {
        return finds()[index]
    }

    override fun count(): Int {
        return finds().size
    }

    override fun first(): Constructor<*> {
        return finds().first()
    }

    override fun firstOrNull(): Constructor<*>? {
        return finds().firstOrNull()
    }

    override fun last(): Constructor<*> {
        return finds().last()
    }

    override fun lastOrNull(): Constructor<*>? {
        return finds().lastOrNull()
    }

    override fun newFirst(vararg args: Any?): Any {
        return first().newInstance(*args)
    }

    override fun newLast(vararg args: Any?): Any {
        return last().newInstance(*args)
    }

    override fun newIndex(index: Int, vararg args: Any?): Any {
        return get(index).newInstance(*args)
    }
}