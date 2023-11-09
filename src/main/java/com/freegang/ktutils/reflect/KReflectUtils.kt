package com.freegang.ktutils.reflect

import android.util.Log
import java.lang.reflect.Constructor
import java.lang.reflect.Field
import java.lang.reflect.Method

object KReflectUtils {
    private const val TAG = "KReflectUtils"

    private val allFieldsCache = mutableMapOf<String, List<Field>>()
    private val allMethodsCache = mutableMapOf<String, List<Method>>()
    private val allConstructorsCache = mutableMapOf<String, List<Constructor<*>>>()

    private val usingFieldsCache = mutableMapOf<String, Field>()
    private val usingMethodsCache = mutableMapOf<String, Method>()

    /**
     * 获取某个对象的所有字段, 包含其继承的父类字段, 同名字段顺序排列。
     *
     * @param obj 目标对象
     * @return 所有字段列表
     */
    @JvmStatic
    fun getAllFields(obj: Any): List<Field> {
        var currentClass: Class<*> = if (obj is Class<*>) obj else obj.javaClass
        val key = currentClass.name

        // 读缓存
        val fieldList = allFieldsCache[key] ?: emptyList()
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
        allFieldsCache[key] = fields
        return fields
    }

    /**
     * 获取某个对象的所有方法 包含其继承的父类方法, 同名方法顺序排列。
     *
     * @param obj 目标对象
     * @return 所有方法列表
     */
    @JvmStatic
    fun getAllMethods(obj: Any): List<Method> {
        var currentClass: Class<*> = if (obj is Class<*>) obj else obj.javaClass
        val key = currentClass.name

        // 读缓存
        val methodList = allMethodsCache[key] ?: emptyList()
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
        allMethodsCache[key] = methods
        return methods
    }

    /**
     * 获取一个对象的所有构造器（包括父类）。
     *
     * @param obj 需要获取构造器的对象
     * @return 对象的所有构造器列表
     */
    @JvmStatic
    fun getAllConstructors(obj: Any): List<Constructor<*>> {
        var currentClass: Class<*> = if (obj is Class<*>) obj else obj.javaClass
        val key = currentClass.name

        // 读缓存
        val methodList = allConstructorsCache[key] ?: emptyList()
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
        allConstructorsCache[key] = constructors
        return constructors
    }

    /**
     * 按指定要求查找指定字段集合, 同名字段顺序排列。
     *
     * @param obj 目标对象
     * @param name 字段名, 可空
     * @param type 字段类型, 可空
     * @return 满足指定要求的指定字段列表
     * @throws IllegalArgumentException 不允许 [name]、[type] 同时为空
     */
    @JvmStatic
    @Throws(IllegalArgumentException::class)
    fun findFields(
        obj: Any,
        name: String? = null,
        type: Class<*>? = null,
    ): List<Field> {
        if (name == null && type == null) {
            throw IllegalArgumentException("Please provide at least one item for 'name' and 'type', otherwise you should use the 'getAllFields' method.")
        }

        val allFields = getAllFields(obj)
        val filteredFields = mutableListOf<Field>()

        for (field in allFields) {
            if (name != null && type != null) {
                if (field.name == name && compareType(field, type)) {
                    Log.d(TAG, "name: $name, type: $type")
                    filteredFields.add(field)
                }
            } else if (name != null) {
                if (field.name == name) {
                    Log.d(TAG, "name: $name")
                    filteredFields.add(field)
                }
            } else if (type != null) {
                if (compareType(field, type)) {
                    Log.d(TAG, "type: $type")
                    filteredFields.add(field)
                }
            } else {
                return emptyList()
            }
        }
        return filteredFields
    }

    /**
     * 按指定要求查找指定字段。
     *
     * @param obj 目标对象
     * @param name 字段名, 可空
     * @param type 字段类型, 可空
     * @return 满足指定要求的指定字段
     * @throws IllegalArgumentException 不允许 [name]、[type] 同时为空
     */
    @JvmStatic
    @Throws(IllegalArgumentException::class)
    fun findFieldFirst(
        obj: Any,
        name: String? = null,
        type: Class<*>? = null,
    ): Field? {
        if (name == null && type == null) {
            throw IllegalArgumentException("Please provide at least one item for 'name' and 'type', otherwise you should use the 'getAllFields' method.")
        }

        val clazz = obj.javaClass
        val key = "${clazz.name}#$name@${type?.name}"
        if (usingFieldsCache.containsKey(key)) {
            return usingFieldsCache[key]
        }

        val fields = findFields(obj, name, type)
        val field = fields.firstOrNull() ?: return null
        usingFieldsCache[key] = field
        return field
    }

    /**
     * 按指定要求查找指定字段集合, 同名字段顺序排列。
     *
     * @param fields 目标字段列表
     * @param name 字段名, 可空
     * @param type 字段类型, 可空
     * @return 满足指定要求的指定字段列表
     * @throws IllegalArgumentException 不允许 [name]、[type] 同时为空
     */
    @JvmStatic
    @Throws(IllegalArgumentException::class)
    fun findFields(
        fields: Collection<Field>,
        name: String? = null,
        type: Class<*>? = null,
    ): List<Field> {
        if (name == null && type == null) {
            throw IllegalArgumentException("Please provide at least one item for 'name' and 'type', otherwise you should use the 'getAllFields' method.")
        }

        val filteredFields = mutableListOf<Field>()

        for (field in fields) {
            if (name != null && type != null) {
                if (field.name == name && compareType(field, type)) {
                    Log.d(TAG, "name: $name, type: $type")
                    filteredFields.add(field)
                }
            } else if (name != null) {
                if (field.name == name) {
                    Log.d(TAG, "name: $name")
                    filteredFields.add(field)
                }
            } else if (type != null) {
                if (compareType(field, type)) {
                    Log.d(TAG, "type: $type")
                    filteredFields.add(field)
                }
            } else {
                return emptyList()
            }
        }
        return filteredFields
    }

    /**
     * 按指定要求查找指定字段。
     *
     * @param fields 目标字段列表
     * @param name 字段名, 可空
     * @param type 字段类型, 可空
     * @return 满足指定要求的指定字段
     * @throws IllegalArgumentException 不允许 [name]、[type] 同时为空
     */
    @JvmStatic
    @Throws(IllegalArgumentException::class)
    fun findFieldFirst(
        fields: Collection<Field>,
        name: String? = null,
        type: Class<*>? = null,
    ): Field? {
        if (name == null && type == null) {
            throw IllegalArgumentException("Please provide at least one item for 'name' and 'type', otherwise you should use the 'getAllFields' method.")
        }

        val key = "$fields$name@${type?.name}"
        if (usingFieldsCache.containsKey(key)) {
            return usingFieldsCache[key]
        }

        val finds = findFields(fields, name, type)
        val field = finds.firstOrNull() ?: return null
        usingFieldsCache[key] = field
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
        return type.isAssignableFrom(targetType)
                || targetType.isAssignableFrom(type)
    }

    /**
     * 按指定要求查找指定方法集合, 同名方法顺序排列。
     *
     * @param obj 目标对象
     * @param name 字段名, 可空
     * @param returnType 返回类型, 可空
     * @param paramTypes 参数列表类型, 可选; 当某个参数为null时可模糊匹配如: arrayOf(Int::class.java, null, Char::class.java)
     * @return 满足指定要求的指定字段方法
     * @throws IllegalArgumentException 不允许 [name]、[returnType]、[paramTypes] 同时为空
     */
    @JvmStatic
    @Throws(IllegalArgumentException::class)
    fun findMethods(
        obj: Any,
        name: String? = null,
        returnType: Class<*>? = null,
        vararg paramTypes: Class<*>?,
    ): List<Method> {
        if (name == null && returnType == null && paramTypes.isEmpty()) {
            throw IllegalArgumentException("Please provide at least one of the 'name', 'returnType', and 'paramTypes', otherwise you should use the' getAllMethods' method.")
        }
        val allMethods = getAllMethods(obj)
        val filteredMethods = mutableListOf<Method>()
        for (method in allMethods) {
            if (name != null && returnType != null && paramTypes.isNotEmpty()) {
                if (method.name == name
                    && compareReturnType(method, returnType)
                    && compareParamTypes(method, paramTypes)
                ) {
                    Log.d(TAG, "name: $name, returnType: $returnType, paramTypes: $paramTypes")
                    filteredMethods.add(method)
                }
            } else if (name != null && returnType != null) {
                if (method.name == name && compareReturnType(method, returnType)) {
                    Log.d(TAG, "name: $name, returnType: $returnType")
                    filteredMethods.add(method)
                }
            } else if (name != null && paramTypes.isNotEmpty()) {
                if (method.name == name && compareParamTypes(method, paramTypes)) {
                    Log.d(TAG, "name: $name, paramTypes: $paramTypes")
                    filteredMethods.add(method)
                }
            } else if (returnType != null && paramTypes.isNotEmpty()) {
                if (compareReturnType(method, returnType) && compareParamTypes(
                        method,
                        paramTypes
                    )
                ) {
                    Log.d(TAG, "returnType: $returnType, paramTypes: $paramTypes")
                    filteredMethods.add(method)
                }
            } else if (name != null) {
                if (method.name == name) {
                    Log.d(TAG, "name: $name")
                    filteredMethods.add(method)
                }
            } else if (returnType != null) {
                if (compareReturnType(method, returnType)) {
                    Log.d(TAG, "returnType: $returnType")
                    filteredMethods.add(method)
                }
            } else if (paramTypes.isNotEmpty()) {
                if (compareParamTypes(method, paramTypes)) {
                    Log.d(TAG, "paramTypes: $paramTypes")
                    filteredMethods.add(method)
                }
            } else {
                return emptyList()
            }
        }
        return filteredMethods
    }

    /**
     * 按指定要求查找指定方法。
     *
     * @param obj 目标对象
     * @param name 字段名, 可空
     * @param returnType 返回类型, 可空
     * @param paramTypes 参数列表类型, 可选; 当某个参数为null时可模糊匹配如: arrayOf(Int::class.java, null, Char::class.java)
     * @return 满足指定要求的指定方法
     * @throws IllegalArgumentException 不允许 [name]、[returnType]、[paramTypes] 同时为空
     */
    @JvmStatic
    @Throws(IllegalArgumentException::class)
    fun findMethodFirst(
        obj: Any,
        name: String? = null,
        returnType: Class<*>? = null,
        vararg paramTypes: Class<*>?,
    ): Method? {
        if (name == null && returnType == null && paramTypes.isEmpty()) {
            throw IllegalArgumentException("Please provide at least one of the 'name', 'returnType', and 'paramTypes', otherwise you should use the' getAllMethods' method.")
        }
        val clazz = obj.javaClass
        val key =
            "${clazz.name}#$name@${returnType?.name}[${paramTypes.joinToString { "${it?.name}" }}]"
        if (usingMethodsCache.containsKey(key)) {
            return usingMethodsCache[key]
        }

        val methods = findMethods(obj, name, returnType, *paramTypes)
        val method = methods.firstOrNull() ?: return null
        usingMethodsCache[key] = method
        return method
    }

    /**
     * 从方法列表中查找指定方法
     * @param methods 目标方法列表
     * @param name 字段名, 可空
     * @param returnType 返回类型, 可空
     * @param paramTypes 参数列表类型, 可选; 当某个参数为null时可模糊匹配如: arrayOf(Int::class.java, null, Char::class.java)
     * @return 满足指定要求的指定方法列表
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
        if (name == null && returnType == null && paramTypes.isEmpty()) {
            throw IllegalArgumentException("Please provide at least one of the 'name', 'returnType', and 'paramTypes', otherwise you should use the' getAllMethods' method.")
        }
        val filteredMethods = mutableListOf<Method>()
        for (method in methods) {
            if (name != null && returnType != null && paramTypes.isNotEmpty()) {
                if (method.name == name
                    && compareReturnType(method, returnType)
                    && compareParamTypes(method, paramTypes)
                ) {
                    Log.d(TAG, "name: $name, returnType: $returnType, paramTypes: $paramTypes")
                    filteredMethods.add(method)
                }
            } else if (name != null && returnType != null) {
                if (method.name == name && compareReturnType(method, returnType)) {
                    Log.d(TAG, "name: $name, returnType: $returnType")
                    filteredMethods.add(method)
                }
            } else if (name != null && paramTypes.isNotEmpty()) {
                if (method.name == name && compareParamTypes(method, paramTypes)) {
                    Log.d(TAG, "name: $name, paramTypes: $paramTypes")
                    filteredMethods.add(method)
                }
            } else if (returnType != null && paramTypes.isNotEmpty()) {
                if (compareReturnType(method, returnType) && compareParamTypes(
                        method,
                        paramTypes
                    )
                ) {
                    Log.d(TAG, "returnType: $returnType, paramTypes: $paramTypes")
                    filteredMethods.add(method)
                }
            } else if (name != null) {
                if (method.name == name) {
                    Log.d(TAG, "name: $name")
                    filteredMethods.add(method)
                }
            } else if (returnType != null) {
                if (compareReturnType(method, returnType)) {
                    Log.d(TAG, "returnType: $returnType")
                    filteredMethods.add(method)
                }
            } else if (paramTypes.isNotEmpty()) {
                if (compareParamTypes(method, paramTypes)) {
                    Log.d(TAG, "paramTypes: $paramTypes")
                    filteredMethods.add(method)
                }
            } else {
                return emptyList()
            }
        }
        return filteredMethods
    }

    /**
     * 按指定要求查找指定方法。
     *
     * @param methods 目标方法列表
     * @param name 字段名, 可空
     * @param returnType 返回类型, 可空
     * @param paramTypes 参数列表类型, 可选; 当某个参数为null时可模糊匹配如: arrayOf(Int::class.java, null, Char::class.java)
     * @return 满足指定要求的指定方法
     * @throws IllegalArgumentException 不允许 [name]、[returnType]、[paramTypes] 同时为空
     */
    @JvmStatic
    @Throws(IllegalArgumentException::class)
    fun findMethodFirst(
        methods: Collection<Method>,
        name: String? = null,
        returnType: Class<*>? = null,
        vararg paramTypes: Class<*>?,
    ): Method? {
        if (name == null && returnType == null && paramTypes.isEmpty()) {
            throw IllegalArgumentException("Please provide at least one of the 'name', 'returnType', and 'paramTypes', otherwise you should use the' getAllMethods' method.")
        }
        val key =
            "$methods$name@${returnType?.name}[${paramTypes.joinToString { "${it?.name}" }}]"
        if (usingMethodsCache.containsKey(key)) {
            return usingMethodsCache[key]
        }

        val finds = findMethods(methods, name, returnType, *paramTypes)
        val method = finds.firstOrNull() ?: return null
        usingMethodsCache[key] = method
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
        if (c1.isPrimitive) {
            return getWrapperClass(c1) == c2
        } else if (c2.isPrimitive) {
            return getWrapperClass(c2) == c1
        }
        return false
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
    return if (name == null && type == null) {
        KReflectUtils.getAllFields(this)
    } else {
        KReflectUtils.findFields(this, name, type)
    }
}

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

fun Any.fieldFirst(
    name: String? = null,
    type: Class<*>? = null,
): Field? {
    return KReflectUtils.findFieldFirst(this, name, type)
}

@Throws(IllegalArgumentException::class, IllegalAccessException::class)
fun Any.fieldGetFirst(
    name: String? = null,
    type: Class<*>? = null,
): Any? {
    val field = fieldFirst(name = name, type = type)
    return field?.get(this)
}

@Throws(IllegalArgumentException::class, IllegalAccessException::class)
fun Any.fieldSetFirst(
    name: String,
    value: Any?,
) {
    val field = fieldFirst(name = name, type = value?.javaClass)
    field?.set(this, value)
}

fun Any.methods(
    name: String? = null,
    returnType: Class<*>? = null,
    vararg paramTypes: Class<*>?,
): List<Method> {
    return if (name == null && returnType == null && paramTypes.isEmpty()) {
        KReflectUtils.getAllMethods(this)
    } else {
        KReflectUtils.findMethods(this, name, returnType, *paramTypes)
    }
}

fun Any.methodFirst(
    name: String? = null,
    returnType: Class<*>? = null,
    vararg paramTypes: Class<*>?,
): Method? {
    return KReflectUtils.findMethodFirst(this, name, returnType, *paramTypes)
}

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

@Throws(IllegalArgumentException::class, IllegalAccessException::class)
fun Any.methodInvokeFirst(
    name: String? = null,
    returnType: Class<*>? = null,
    vararg args: Any,
): Any? {
    val typedArray = args.map { it.javaClass }.toTypedArray()
    val method = methodFirst(name = name, returnType = returnType, paramTypes = typedArray)
    return method?.invoke(this, *args)
}