package com.freegang.ktutils.reflect

import java.lang.reflect.Field

interface FiledFind : BaseFind<Field> {
    /**
     * 获取查找匹配列表下的所有字段值
     *
     * @param any 实例对象
     */
    fun getValues(any: Any?): List<Any?>

    /**
     * 获取查找匹配到的第一项值
     *
     * @param any 实例对象
     */
    fun getValueFirst(any: Any?): Any?

    /**
     * 获取指定下标项的值
     */
    fun getValueIndex(index: Int, any: Any?): Any?

    /**
     * 获取查找匹配到的最后一项值
     *
     * @param any 实例对象
     */
    fun getValueLast(any: Any?): Any?

    /**
     *设置查找匹配到的第一项值
     *
     * @param any 实例对象
     */
    fun setValueFirst(any: Any?, value: Any?)

    /**
     * 设置查找匹配到的最后一项值
     *
     * @param any 实例对象
     */
    fun setValueLast(any: Any?, value: Any?)

    /**
     * 设置指定下标项的值
     */
    fun setValueIndex(index: Int, any: Any?, value: Any?)
}

class FieldFindBuilder(private val fields: List<Field>) : FiledFind {
    // 修饰符
    private var mModifiers: Int? = null

    // 字段名
    private var mName: String? = null

    // 字段类型
    private var mType: Class<*>? = null
    private var mTypeAsFrom: Boolean = false

    // 字段注解列表
    private var mAnnotationTypes: List<Class<*>?>? = null

    // 其他外置条件
    private var mFindPredicate: FindFieldPredicate? = null

    // 存放查找过的列表
    private var finded: List<Field> = listOf()

    /**
     * 修饰符
     *
     * @param modifiers 修饰符, example: `Modifier.PUBLIC or Modifier.STATIC`
     */
    fun modifiers(
        modifiers: Int,
    ): FieldFindBuilder {
        this.mModifiers = modifiers

        return this
    }

    /**
     * 字段名
     *
     * @param name 方法名
     */
    fun name(
        name: String,
    ): FieldFindBuilder {
        this.mName = name
        return this
    }

    /**
     * 字段类型
     *
     * @param type 类型
     * @param isAssignableFrom 是否互相比较继承关系
     */
    fun type(
        type: Class<*>,
        isAssignableFrom: Boolean = false,
    ): FieldFindBuilder {
        this.mType = type
        this.mTypeAsFrom = isAssignableFrom

        return this
    }

    /**
     * 字段注解类型列表
     *
     * @param annotations 类型列表
     */
    fun annotations(
        annotations: List<Class<*>?>,
    ): FieldFindBuilder {
        this.mAnnotationTypes = annotations

        return this
    }

    /**
     * 其他外置条件
     *
     * @param predicate 外置判断条件
     */
    fun predicate(
        predicate: FindFieldPredicate,
    ) {
        this.mFindPredicate = predicate
    }

    private fun checkRules() {
        val rules = mModifiers == null
                && mName == null
                && mType == null
                && mAnnotationTypes == null
                && mFindPredicate == null

        if (rules)
            throw IllegalArgumentException("at least one matching rule is required.")
    }

    private fun finds(): List<Field> {
        checkRules()

        if (finded.isNotEmpty())
            return finded

        var sequence = fields.asSequence()

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

        if (mType != null) {
            sequence = sequence.filter {
                KReflectUtils.compareType(it.type, mType!!, mTypeAsFrom)
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

        if (mAnnotationTypes != null) {
            sequence = sequence.filter {
                mFindPredicate?.predicate(it) ?: true
            }
        }

        return sequence.toList()
            .also { finded = it }
    }

    override fun forEach(action: (Field) -> Unit) {
        finds().forEach(action)
    }

    override fun onEach(action: (Field) -> Unit): List<Field> {
        return finds().onEach(action)
    }

    override fun isEmpty(): Boolean {
        return finds().isEmpty()
    }

    override fun toList(): List<Field> {
        return finds()
    }

    override fun get(index: Int): Field {
        return finds()[index]
    }

    override fun count(): Int {
        return finds().size
    }

    override fun first(): Field {
        return finds().first()
    }

    override fun firstOrNull(): Field? {
        return finds().firstOrNull()
    }

    override fun last(): Field {
        return finds().last()
    }

    override fun lastOrNull(): Field? {
        return finds().lastOrNull()
    }

    override fun getValues(any: Any?): List<Any?> {
        return finds().map { it.get(any) }
    }

    override fun getValueFirst(any: Any?): Any? {
        return first().get(any)
    }

    override fun getValueIndex(index: Int, any: Any?): Any? {
        return get(index).get(any)
    }

    override fun getValueLast(any: Any?): Any? {
        return last().get(any)
    }

    override fun setValueFirst(any: Any?, value: Any?) {
        first().set(any, value)
    }

    override fun setValueLast(any: Any?, value: Any?) {
        last().set(any, value)
    }

    override fun setValueIndex(index: Int, any: Any?, value: Any?) {
        get(index).set(any, value)
    }
}