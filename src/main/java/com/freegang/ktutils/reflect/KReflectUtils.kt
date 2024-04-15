package com.freegang.ktutils.reflect

import java.lang.reflect.Constructor
import java.lang.reflect.Field
import java.lang.reflect.Method
import kotlin.reflect.KClass

object KReflectUtils {
    private const val TAG = "KReflectUtils"

    private val fieldsCache = mutableMapOf<String, List<Field>>()
    private val methodsCache = mutableMapOf<String, List<Method>>()
    private val constructorsCache = mutableMapOf<String, List<Constructor<*>>>()

    private val usingFieldCache = mutableMapOf<String, Field>()
    private val usingMethodCache = mutableMapOf<String, Method>()

    private val usingFieldListCache = mutableMapOf<String, List<Field>>()
    private val usingMethodListCache = mutableMapOf<String, List<Method>>()

    private fun getAnyClass(any: Any): Class<*> {
        return when (any) {
            is KClass<*> -> any.java
            is Class<*> -> any
            else -> any.javaClass
        }
    }

    /**
     * 获取某个对象/类的所有字段（含父类），同名字段顺序排列。
     *
     * @param any 目标对象/类
     * @return 所有字段（含父类）列表
     */
    @JvmStatic
    fun getFields(any: Any): List<Field> {
        var currentClass = getAnyClass(any)
        val key = currentClass.name

        // 读缓存
        val fieldList = fieldsCache[key] ?: emptyList()
        if (fieldList.isNotEmpty()) {
            return fieldList
        }

        // 没有缓存
        val fields = mutableListOf<Field>()
        while (currentClass != Any::class.java) {
            currentClass.declaredFields.forEach {
                it.isAccessible = true
                fields.add(it)
            }
            currentClass = currentClass.superclass
        }
        fieldsCache[key] = fields
        return fields
    }

    /**
     * 获取某个对象/类的所有方法（含父类）, 同名方法顺序排列。
     *
     * @param any 目标对象/类
     * @return 所有方法（含父类）列表
     */
    @JvmStatic
    fun getMethods(any: Any): List<Method> {
        var currentClass = getAnyClass(any)
        val key = currentClass.name

        // 读缓存
        val methodList = methodsCache[key] ?: emptyList()
        if (methodList.isNotEmpty()) {
            return methodList
        }

        // 没有缓存
        val methods = mutableListOf<Method>()
        while (currentClass != Any::class.java) {
            currentClass.declaredMethods.forEach {
                it.isAccessible = true
                methods.add(it)
            }
            currentClass = currentClass.superclass
        }
        methodsCache[key] = methods
        return methods
    }

    /**
     * 获取某个对象/类的所有构造器（含父类）。
     *
     * @param any 目标对象/类
     * @return 所有构造器（含父类）列表
     */
    @JvmStatic
    fun getConstructors(any: Any): List<Constructor<*>> {
        var currentClass = getAnyClass(any)
        val key = currentClass.name

        // 读缓存
        val methodList = constructorsCache[key] ?: emptyList()
        if (methodList.isNotEmpty()) {
            return methodList
        }

        // 没有缓存
        val constructors = mutableListOf<Constructor<*>>()
        while (currentClass != Any::class.java) {
            currentClass.declaredConstructors.forEach {
                it.isAccessible = true
                constructors.add(it)
            }
            currentClass = currentClass.superclass
        }
        constructorsCache[key] = constructors
        return constructors
    }

    /**
     * 查找某个对象/类中符合条件的所有字段，同名字段顺序排列。
     *
     * @param any 目标对象/类
     * @param name 字段名，可空
     * @param type 字段类型，可空
     * @return 满足条件的字段列表
     * @throws IllegalArgumentException 不允许 [name]、[type] 同时为空
     */
    @JvmStatic
    @Throws(IllegalArgumentException::class)
    fun findFields(
        any: Any,
        name: String? = null,
        type: Class<*>? = null,
    ): List<Field> {
        return findFields(getFields(any), name, type)
    }

    /**
     * 查找字段列表，返回符合条件的所有字段，同名字段顺序排列。
     *
     * @param fields 目标字段列表
     * @param name 字段名，可空
     * @param type 字段类型，可空
     * @return 满足条件的字段列表
     * @throws IllegalArgumentException 不允许 [name]、[type] 同时为空
     */
    @JvmStatic
    @Throws(IllegalArgumentException::class)
    fun findFields(
        fields: Collection<Field>,
        name: String? = null,
        type: Class<*>? = null,
    ): List<Field> {
        if (fields.isEmpty()) {
            return emptyList()
        }

        if (name == null && type == null) {
            throw IllegalArgumentException("Please provide at least one item for 'name' and 'type', otherwise you should use the 'getFields' method.")
        }

        val key = "${fields.hashCode()}#$name@${type?.hashCode()}"
        if (usingFieldListCache.containsKey(key)) {
            return usingFieldListCache[key] ?: emptyList()
        }

        var sequence = fields.asSequence()

        if (!name.isNullOrEmpty()) {
            sequence = sequence.filter { name == it.name }
        }

        if (type != null) {
            sequence = sequence.filter { compareType(it, type) }
        }

        val toList = sequence.toList()
        usingFieldListCache[key] = toList
        return toList
    }

    /**
     * 查找某个对象/类中的指定字段，返回符合条件的第一个字段。
     *
     * @param any 目标对象
     * @param name 字段名，可空
     * @param type 字段类型，可空
     * @return 满足条件的第一个字段
     * @throws IllegalArgumentException 不允许 [name]、[type] 同时为空
     */
    @JvmStatic
    @Throws(IllegalArgumentException::class)
    fun findField(
        any: Any,
        name: String? = null,
        type: Class<*>? = null,
    ): Field? {
        if (name == null && type == null) {
            throw IllegalArgumentException("Please provide at least one item for 'name' and 'type', otherwise you should use the 'getFields' method.")
        }

        val clazz = getAnyClass(any)

        val key = "${clazz.hashCode()}#$name@${type?.hashCode()}"
        if (usingFieldCache.containsKey(key)) {
            return usingFieldCache[key]
        }

        val field = findFields(any, name, type).firstOrNull() ?: return null
        usingFieldCache[key] = field
        return field
    }

    /**
     * 查找字段列表，返回符合条件的第一个字段。
     *
     * @param fields 目标字段列表
     * @param name 字段名，可空
     * @param type 字段类型，可空
     * @return 满足条件的第一个字段
     * @throws IllegalArgumentException 不允许 [name]、[type] 同时为空
     */
    @JvmStatic
    @Throws(IllegalArgumentException::class)
    fun findField(
        fields: Collection<Field>,
        name: String? = null,
        type: Class<*>? = null,
    ): Field? {
        if (name == null && type == null) {
            throw IllegalArgumentException("Please provide at least one item for 'name' and 'type', otherwise you should use the 'getFields' method.")
        }

        val key = "$fields$name@${type?.hashCode()}"
        if (usingFieldCache.containsKey(key)) {
            return usingFieldCache[key]
        }

        val field = findFields(fields, name, type).firstOrNull() ?: return null
        usingFieldCache[key] = field
        return field
    }

    private fun compareType(field: Field, targetType: Class<*>): Boolean {
        val type = field.type

        // 类直接比较
        if (type == targetType) {
            return true
        }

        // 基本数据类型比较
        if (isCompatible(type, targetType)) {
            return true
        }

        // 继承关系比较
        return isAssignableFrom(type, targetType)
    }

    /**
     * 查找某个对象/类中符合条件的所有方法，同名方法顺序排列。
     *
     * @param any 目标对象/类
     * @param name 方法名，可空
     * @param returnType 返回类型，可空
     * @param paramTypes 参数列表类型，可选；允许某个参数为`null`时的模糊匹配，如：`arrayOf(Int::class.java, null, Char::class.java)`
     * @return 满足条件的方法列表
     * @throws IllegalArgumentException 不允许 [name]、[returnType]、[paramTypes] 同时为空
     */
    @JvmStatic
    @Throws(IllegalArgumentException::class)
    fun findMethods(
        any: Any,
        name: String? = null,
        returnType: Class<*>? = null,
        vararg paramTypes: Class<*>?,
    ): List<Method> {
        return findMethods(getMethods(any), name, returnType, *paramTypes)
    }

    /**
     * 查找方法列表，返回符合条件的所有方法，同名方法顺序排列。
     *
     * @param methods 目标方法列表
     * @param name 方法名，可空
     * @param returnType 返回类型，可空
     * @param paramTypes 参数列表类型，可选；允许某个参数为`null`的可模糊匹配，如：`arrayOf(Int::class.java, null, Char::class.java)`
     * @return 满足条件的方法列表
     * @throws IllegalArgumentException 不允许 [name]、[returnType]、[paramTypes] 同时为空
     */
    @JvmStatic
    @Throws(IllegalArgumentException::class)
    fun findMethods(
        methods: Collection<Method>,
        name: String? = null,
        returnType: Class<*>? = null,
        vararg paramTypes: Class<*>?,
    ): List<Method> {
        if (methods.isEmpty()) {
            return emptyList()
        }

        if (name == null && returnType == null && paramTypes.isEmpty()) {
            throw IllegalArgumentException("Please provide at least one of the 'name', 'returnType', and 'paramTypes', otherwise you should use the' getMethods' method.")
        }

        val key = "${methods.hashCode()}#$name@${returnType?.hashCode()}[${paramTypes.joinToString { "${it?.hashCode()}" }}]"
        if (usingMethodListCache.containsKey(key)) {
            return usingMethodListCache[key] ?: emptyList()
        }

        var sequence = methods.asSequence()

        if (!name.isNullOrEmpty()) {
            sequence = sequence.filter { name == it.name }
        }

        if (returnType != null) {
            sequence = sequence.filter { compareReturnType(it, returnType) }
        }

        if (paramTypes.isNotEmpty()) {
            sequence = sequence.filter { compareParamTypes(it, paramTypes) }
        }

        val toList = sequence.toList()
        usingMethodListCache[key] = toList
        return toList
    }

    /**
     * 查找某个对象/类中的指定方法，返回符合条件的第一个方法。
     *
     * @param any 目标对象
     * @param name 方法名，可空
     * @param returnType 返回类型，可空
     * @param paramTypes 参数类型列表，可选；允许某个参数为`null`的可模糊匹配，如：`arrayOf(Int::class.java, null, Char::class.java)`
     * @return 满足条件的第一个方法
     * @throws IllegalArgumentException 不允许 [name]、[returnType]、[paramTypes] 同时为空
     */
    @JvmStatic
    @Throws(IllegalArgumentException::class)
    fun findMethod(
        any: Any,
        name: String? = null,
        returnType: Class<*>? = null,
        vararg paramTypes: Class<*>?,
    ): Method? {
        if (name == null && returnType == null && paramTypes.isEmpty()) {
            throw IllegalArgumentException("Please provide at least one of the 'name', 'returnType', and 'paramTypes', otherwise you should use the' getMethods' method.")
        }

        val clazz = getAnyClass(any)
        val key = "${clazz.hashCode()}#$name@${returnType?.hashCode()}[${paramTypes.joinToString { "${it?.hashCode()}" }}]"
        if (usingMethodCache.containsKey(key)) {
            return usingMethodCache[key]
        }

        val method = findMethods(any, name, returnType, *paramTypes).firstOrNull() ?: return null
        usingMethodCache[key] = method
        return method
    }

    /**
     * 查找方法列表，返回符合条件的第一个方法。
     *
     * @param methods 目标方法列表
     * @param name 方法名，可空
     * @param returnType 返回类型，可空
     * @param paramTypes 参数类型列表，可选；允许某个参数为`null`的可模糊匹配，如：`arrayOf(Int::class.java, null, Char::class.java)`
     * @return 满足条件的第一个方法
     * @throws IllegalArgumentException 不允许 [name]、[returnType]、[paramTypes] 同时为空
     */
    @JvmStatic
    @Throws(IllegalArgumentException::class)
    fun findMethod(
        methods: Collection<Method>,
        name: String? = null,
        returnType: Class<*>? = null,
        vararg paramTypes: Class<*>?,
    ): Method? {
        if (name == null && returnType == null && paramTypes.isEmpty()) {
            throw IllegalArgumentException("Please provide at least one of the 'name', 'returnType', and 'paramTypes', otherwise you should use the' getMethods' method.")
        }

        val key = "${methods.hashCode()}$name@${returnType?.hashCode()}[${paramTypes.joinToString { "${it?.hashCode()}" }}]"
        if (usingMethodCache.containsKey(key)) {
            return usingMethodCache[key]
        }

        val method = findMethods(methods, name, returnType, *paramTypes).firstOrNull() ?: return null
        usingMethodCache[key] = method
        return method
    }

    private fun compareReturnType(method: Method, targetReturnType: Class<*>): Boolean {
        val returnType = method.returnType

        // 类直接比较
        if (returnType == targetReturnType) {
            return true
        }

        // 基本数据类型比较
        if (isCompatible(returnType, targetReturnType)) {
            return true
        }

        // 继承关系比较
        return isAssignableFrom(returnType, targetReturnType)
    }

    private fun compareParamTypes(method: Method, targetParamTypes: Array<out Class<*>?>): Boolean {
        val parameterTypes = method.parameterTypes

        // 比较数量
        if (parameterTypes.size != targetParamTypes.size) {
            return false
        }

        for (i in parameterTypes.indices) {
            val type = parameterTypes[i]
            val targetType = targetParamTypes[i] ?: continue // null则模糊匹配

            // 类直接比较
            if (type == targetType) {
                continue
            }

            // 基本数据类型比较
            if (isCompatible(type, targetType)) {
                continue
            }

            // 继承关系比较
            if (isAssignableFrom(type, targetType)) {
                continue
            }

            // 参数类型不一致
            return false
        }

        // 所有参数类型一致
        return true
    }

    private fun isCompatible(c1: Class<*>, c2: Class<*>): Boolean {
        return if (c1.isPrimitive) {
            getWrapperClass(c1) == c2
        } else if (c2.isPrimitive) {
            getWrapperClass(c2) == c1
        } else {
            false
        }
    }

    private fun getWrapperClass(primitiveClass: Class<*>): Class<*> {
        return when (primitiveClass) {
            Boolean::class.javaPrimitiveType -> Boolean::class.javaObjectType
            Byte::class.javaPrimitiveType -> Byte::class.javaObjectType
            Char::class.javaPrimitiveType -> Char::class.javaObjectType
            Short::class.javaPrimitiveType -> Short::class.javaObjectType
            Int::class.javaPrimitiveType -> Int::class.javaObjectType
            Long::class.javaPrimitiveType -> Long::class.javaObjectType
            Float::class.javaPrimitiveType -> Float::class.javaObjectType
            Double::class.javaPrimitiveType -> Double::class.javaObjectType
            else -> primitiveClass
        }
    }

    private fun isAssignableFrom(c1: Class<*>, c2: Class<*>): Boolean {
        if (c1 == Any::class.java || c2 == Any::class.java) return false
        return c1.isAssignableFrom(c2) || c2.isAssignableFrom(c1)
    }
}