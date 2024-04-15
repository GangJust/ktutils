package com.freegang.ktutils.json

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.Reader
import java.io.Writer

fun interface JSONObjectForeachFunction {
    fun invoke(key: String, value: Any?)
}

fun interface JSONArrayForeachFunction {
    fun invoke(index: Int, value: Any?)
}

object KJSONUtils {

    /// Json ///
    @JvmStatic
    fun parse(json: String): JSONObject {
        return try {
            JSONObject(json)
        } catch (e: JSONException) {
            JSONObject()
        }
    }

    @JvmStatic
    @JvmOverloads
    fun getString(json: JSONObject, key: String, default: String = ""): String {
        return try {
            if (json.isNull(key))
                default
            else
                json.getString(key)
        } catch (e: JSONException) {
            default
        }
    }

    @JvmStatic
    @JvmOverloads
    fun getInt(json: JSONObject, key: String, default: Int = 0): Int {
        return try {
            if (json.isNull(key))
                default
            else
                json.getInt(key)
        } catch (e: JSONException) {
            default
        }
    }

    @JvmStatic
    @JvmOverloads
    fun getLong(json: JSONObject, key: String, default: Long = 0L): Long {
        return try {
            if (json.isNull(key))
                default
            else
                json.getLong(key)
        } catch (e: JSONException) {
            default
        }
    }

    @JvmStatic
    @JvmOverloads
    fun getDouble(json: JSONObject, key: String, default: Double = 0.0): Double {
        return try {
            if (json.isNull(key))
                default
            else
                json.getDouble(key)
        } catch (e: JSONException) {
            default
        }
    }

    @JvmStatic
    @JvmOverloads
    fun getBoolean(json: JSONObject, key: String, default: Boolean = false): Boolean {
        return try {
            if (json.isNull(key))
                default
            else
                json.getBoolean(key)
        } catch (e: JSONException) {
            default
        }
    }

    @JvmStatic
    fun getJSONObject(json: JSONObject, key: String): JSONObject {
        return try {
            json.getJSONObject(key)
        } catch (e: JSONException) {
            JSONObject()
        }
    }

    @JvmStatic
    fun getJSONArray(json: JSONObject, key: String): JSONArray {
        return try {
            json.getJSONArray(key)
        } catch (e: JSONException) {
            JSONArray()
        }
    }

    @JvmStatic
    fun getJSONArrays(json: JSONObject, key: String): Array<JSONObject> {
        val array = getJSONArray(json, key)
        if (array.length() == 0) return emptyArray()
        return Array(array.length()) { array.getJSONObject(it) }
    }

    @JvmStatic
    fun toMap(json: JSONObject): Map<String, Any?> {
        val iterator = json.keys().iterator()
        val map = mutableMapOf<String, Any?>()
        while (iterator.hasNext()) {
            val key = iterator.next()
            map[key] = json.get(key)
        }
        return map
    }

    @JvmStatic
    fun mapToJson(map: Map<String, Any?>): JSONObject {
        val json = JSONObject()
        map.forEach { (t, u) -> json.put(t, u) }
        return json
    }

    @JvmStatic
    fun forEach(json: JSONObject, block: JSONObjectForeachFunction) {
        val iterator = json.keys().iterator()
        while (iterator.hasNext()) {
            val key = iterator.next()
            block.invoke(key, json.get(key))
        }
    }

    @JvmStatic
    fun isNull(json: JSONObject, key: String): Boolean {
        return json.isNull(key)
    }

    @JvmStatic
    fun hasKey(json: JSONObject, key: String): Boolean {
        return json.has(key)
    }

    @JvmStatic
    fun isEmpty(json: JSONObject): Boolean {
        val jsonStr = json.toString()
        return json == JSONObject.NULL || jsonStr == "{}" || jsonStr == "null" || jsonStr == ""
    }

    /// Json Array ///
    @JvmStatic
    fun parseArray(json: String): JSONArray {
        return try {
            JSONArray(json)
        } catch (e: JSONException) {
            JSONArray()
        }
    }

    @JvmStatic
    @JvmOverloads
    fun getString(array: JSONArray, index: Int, default: String = ""): String {
        return try {
            if (array.isNull(index))
                default
            else
                array.getString(index)
        } catch (e: JSONException) {
            default
        }
    }

    @JvmStatic
    @JvmOverloads
    fun getInt(array: JSONArray, index: Int, default: Int = 0): Int {
        return try {
            if (array.isNull(index))
                default
            else
                array.getInt(index)
        } catch (e: JSONException) {
            default
        }
    }

    @JvmStatic
    @JvmOverloads
    fun getLong(array: JSONArray, index: Int, default: Long = 0): Long {
        return try {
            if (array.isNull(index))
                default
            else
                array.getLong(index)
        } catch (e: JSONException) {
            default
        }
    }

    @JvmStatic
    @JvmOverloads
    fun getDouble(array: JSONArray, index: Int, default: Double = 0.0): Double {
        return try {
            if (array.isNull(index))
                default
            else
                array.getDouble(index)
        } catch (e: JSONException) {
            default
        }
    }

    @JvmStatic
    @JvmOverloads
    fun getBoolean(array: JSONArray, index: Int, default: Boolean = false): Boolean {
        return try {
            if (array.isNull(index))
                default
            else
                array.getBoolean(index)
        } catch (e: JSONException) {
            default
        }
    }

    @JvmStatic
    fun getJSONObject(array: JSONArray, index: Int): JSONObject {
        return try {
            array.getJSONObject(index)
        } catch (e: JSONException) {
            JSONObject()
        }
    }

    @JvmStatic
    fun getJSONArray(array: JSONArray, index: Int): JSONArray {
        return try {
            array.getJSONArray(index)
        } catch (e: JSONException) {
            JSONArray()
        }
    }

    @JvmStatic
    fun toJSONObjects(array: JSONArray): Array<JSONObject> {
        if (array.length() == 0) return emptyArray()
        return Array(array.length()) { array.getJSONObject(it) }
    }

    @JvmStatic
    fun toMaps(array: JSONArray): List<Map<String, Any?>> {
        if (array.length() == 0) return emptyList()
        val list = mutableListOf<Map<String, Any?>>()
        for (i in 0 until array.length()) {
            list.add(toMap(getJSONObject(array, i)))
        }
        return list
    }

    @JvmStatic
    fun forEach(json: JSONArray, block: JSONArrayForeachFunction) {
        for (index in 0 until json.length()) {
            block.invoke(index, json.get(index))
        }
    }

    @JvmStatic
    fun isNull(array: JSONArray, index: Int): Boolean {
        return array.isNull(index)
    }

    @JvmStatic
    fun isEmpty(array: JSONArray): Boolean {
        if (array.length() == 0) return true
        val jsonStr = array.toString()
        return jsonStr == "[]" || jsonStr == "null" || jsonStr == ""
    }


    /// factory ///
    class Factory(json: String) {
        private var mJsonObject: JSONObject? = null
        private var mJsonArray: JSONArray? = null

        constructor(jsonObject: JSONObject) : this("") {
            mJsonObject = jsonObject
        }

        constructor(jsonArray: JSONArray) : this("") {
            mJsonArray = jsonArray
        }


        init {
            if (json.isNotBlank()) {
                if (json[0] == '{') {
                    mJsonObject = parse(json)
                } else {
                    mJsonArray = parseArray(json)
                }
            }
        }

        fun next(key: String): Factory {
            val jsonObject = getJSONObject(mJsonObject!!, key)
            if (isEmpty(jsonObject)) {
                mJsonArray = getJSONArray(mJsonObject!!, key)
            } else {
                mJsonObject = jsonObject
            }
            return this
        }

        fun next(index: Int): Factory {
            val jsonArray = getJSONArray(mJsonArray!!, index)
            if (isEmpty(jsonArray)) {
                mJsonObject = getJSONObject(mJsonArray!!, index)
            } else {
                mJsonArray = jsonArray
            }
            return this
        }

        val jsonObject: JSONObject get() = mJsonObject!!

        val jsonArray: JSONArray get() = mJsonArray!!
    }
}