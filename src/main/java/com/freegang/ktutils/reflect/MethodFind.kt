package com.freegang.ktutils.reflect

import java.lang.reflect.Method

interface MethodFind : BaseFind<Method> {
    /**
     * 调用查找匹配到的第一项
     *
     * @param any 实例对象
     * @param args 实例参数列表
     */
    fun invokeFirst(any: Any?, vararg args: Any?): Any?

    /**
     * 调用查找匹配到的最后一项
     *
     * @param any 实例对象
     * @param args 实例参数列表
     */
    fun invokeLast(any: Any?, vararg args: Any?): Any?

    /**
     * 调用指定下标项
     */
    fun invokeIndex(index: Int, any: Any?, vararg args: Any?): Any?
}

class MethodFindBuilder(private val methods: List<Method>) : MethodFind {
    // 修饰符
    private var mModifiers: Int? = null

    // 方法名
    private var mName: String? = null

    // 返回类型
    private var mReturnType: Class<*>? = null
    private var mReturnTypeAsFrom: Boolean = false

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
    private var mFindPredicate: FindMethodPredicate? = null

    // 存放查找过的列表
    private var finded: List<Method> = listOf()

    /**
     * 修饰符
     *
     * @param modifiers 修饰符, example: `Modifier.PUBLIC or Modifier.STATIC`
     */
    fun modifiers(modifiers: Int): MethodFindBuilder {
        this.mModifiers = modifiers

        return this
    }

    /**
     * 方法名
     *
     * @param name 方法名
     */
    fun name(name: String): MethodFindBuilder {
        this.mName = name

        return this
    }

    /**
     * 返回类型
     *
     * @param returnType 类型
     * @param isAssignableFrom 是否互相比较继承关系
     */
    fun returnType(
        returnType: Class<*>,
        isAssignableFrom: Boolean = false,
    ): MethodFindBuilder {
        this.mReturnType = returnType
        this.mReturnTypeAsFrom = isAssignableFrom

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
    ): MethodFindBuilder {
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
    ): MethodFindBuilder {
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
    ): MethodFindBuilder {
        this.mAnnotationTypes = annotation

        return this
    }

    /**
     * 其他外置条件
     *
     * @param predicate 外置判断条件
     */
    fun predicate(predicate: FindMethodPredicate) {
        this.mFindPredicate = predicate
    }

    private fun checkRules() {
        val rules = mModifiers == null
                && mName == null
                && mReturnType == null
                && mParameterTypes == null
                && mExceptionTypes == null
                && mAnnotationTypes == null
                && mFindPredicate == null

        if (rules)
            throw IllegalArgumentException("at least one matching rule is required.")
    }

    private fun finds(): List<Method> {
        checkRules()

        if (finded.isNotEmpty())
            return finded

        var sequence = methods.asSequence()

        if (mModifiers != null) {
            sequence = sequence.filter {
                it.modifiers == mModifiers
            }
        }

        if (mName != null) {
            sequence = sequence.filter {
                it.name == mName
            }
        }

        if (mReturnType != null) {
            sequence = sequence.filter {
                KReflectUtils.compareType(it.returnType, mReturnType!!, mReturnTypeAsFrom)
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

    override fun forEach(action: (Method) -> Unit) {
        finds().forEach(action)
    }

    override fun onEach(action: (Method) -> Unit): List<Method> {
        return finds().onEach(action)
    }

    override fun isEmpty(): Boolean {
        return finds().isEmpty()
    }

    override fun toList(): List<Method> {
        return finds()
    }

    override fun get(index: Int): Method {
        return finds()[index]
    }

    override fun count(): Int {
        return finds().size
    }

    override fun first(): Method {
        return finds().first()
    }

    override fun firstOrNull(): Method? {
        return finds().firstOrNull()
    }

    override fun last(): Method {
        return finds().last()
    }

    override fun lastOrNull(): Method? {
        return finds().lastOrNull()
    }

    override fun invokeFirst(any: Any?, vararg args: Any?): Any? {
        return first().invoke(any, *args)
    }

    override fun invokeLast(any: Any?, vararg args: Any?): Any? {
        return last().invoke(any, *args)
    }

    override fun invokeIndex(index: Int, any: Any?, vararg args: Any?): Any? {
        return get(index).invoke(any, *args)
    }
}