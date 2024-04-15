package com.freegang.extension

import com.freegang.ktutils.json.JSONArrayForeachFunction
import com.freegang.ktutils.json.JSONObjectForeachFunction
import com.freegang.ktutils.json.KJSONUtils
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.Reader
import java.io.Writer


/// Extended Json ///
fun String.parseJSON(): JSONObject {
    return KJSONUtils.parse(this)
}

fun String.readJSON(read: Reader): JSONObject {
    return try {
        val json = read.use { it.readText() }
        JSONObject(json)
    } catch (e: Exception) {
        JSONObject()
    }
}

fun JSONObject.getStringOrDefault(key: String, default: String = ""): String {
    return KJSONUtils.getString(this, key, default)
}

fun JSONObject.getIntOrDefault(key: String, default: Int = 0): Int {
    return KJSONUtils.getInt(this, key, default)
}

fun JSONObject.getLongOrDefault(key: String, default: Long = 0L): Long {
    return KJSONUtils.getLong(this, key, default)
}

fun JSONObject.getDoubleOrDefault(key: String, default: Double = 0.0): Double {
    return KJSONUtils.getDouble(this, key, default)
}

fun JSONObject.getBooleanOrDefault(key: String, default: Boolean = false): Boolean {
    return KJSONUtils.getBoolean(this, key, default)
}

fun JSONObject.getJSONObjectOrDefault(key: String, default: JSONObject = JSONObject()): JSONObject {
    return try {
        this.getJSONObject(key)
    } catch (e: JSONException) {
        default
    }
}

fun JSONObject.getJSONArrayOrDefault(key: String, default: JSONArray = JSONArray()): JSONArray {
    return try {
        this.getJSONArray(key)
    } catch (e: JSONException) {
        default
    }
}

fun JSONObject.getJSONArraysOrDefault(
    key: String,
    default: Array<JSONObject> = emptyArray(),
): Array<JSONObject> {
    return try {
        val array = this.getJSONArray(key)
        if (array.length() == 0) return emptyArray()
        Array(array.length()) { it -> array.getJSONObject(it) }
    } catch (e: JSONException) {
        default
    }
}

fun JSONObject.toMap(): Map<String, Any?> {
    return KJSONUtils.toMap(this)
}

fun Map<*, *>.toJSONObject(): JSONObject {
    val json = JSONObject()
    this.forEach { entry ->
        val key = entry.key
        val value = entry.value
        runCatching {
            if (value is Map<*, *>) {
                json.put("$key", value.toJSONObject())
            } else {
                json.put("$key", value)
            }
        }.onFailure {
            json.put("$key", null)
        }
    }
    return json
}

fun JSONObject.forEach(block: JSONObjectForeachFunction) {
    KJSONUtils.forEach(this, block)
}

fun JSONObject.write(out: Writer): Boolean {
    return try {
        out.use { it.write(this.toString()) }
        true
    } catch (e: Exception) {
        false
    }
}

val JSONObject.isEmpty: Boolean
    get() = KJSONUtils.isEmpty(this)


/// Extended Json Array ///
fun String.parseJSONArray(): JSONArray {
    return KJSONUtils.parseArray(this)
}

fun String.readJSONArray(read: Reader): JSONArray {
    return try {
        val json = read.use { it.readText() }
        JSONArray(json)
    } catch (e: Exception) {
        JSONArray()
    }
}

fun JSONArray.getStringOrDefault(index: Int, default: String = ""): String {
    return KJSONUtils.getString(this, index, default)
}

fun JSONArray.getIntOrDefault(index: Int, default: Int = 0): Int {
    return KJSONUtils.getInt(this, index, default)
}

fun JSONArray.getLongOrDefault(index: Int, default: Long = 0L): Long {
    return KJSONUtils.getLong(this, index, default)
}

fun JSONArray.getDoubleOrDefault(index: Int, default: Double = 0.0): Double {
    return KJSONUtils.getDouble(this, index, default)
}

fun JSONArray.getBooleanOrDefault(index: Int, default: Boolean = false): Boolean {
    return KJSONUtils.getBoolean(this, index, default)
}

fun JSONArray.getJSONObjectOrDefault(index: Int, default: JSONObject = JSONObject()): JSONObject {
    return try {
        this.getJSONObject(index)
    } catch (e: JSONException) {
        default
    }
}

fun JSONArray.getJSONArrayOrDefault(index: Int, default: JSONArray = JSONArray()): JSONArray {
    return try {
        this.getJSONArray(index)
    } catch (e: JSONException) {
        default
    }
}

fun JSONArray.toMaps(): List<Map<String, Any?>> {
    return KJSONUtils.toMaps(this)
}

fun JSONArray.forEach(block: JSONArrayForeachFunction) {
    KJSONUtils.forEach(this, block)
}

fun JSONArray.write(out: Writer): Boolean {
    return try {
        out.use { it.write(this.toString()) }
        true
    } catch (e: Exception) {
        false
    }
}

val JSONArray.isEmpty: Boolean
    get() = KJSONUtils.isEmpty(this)

fun JSONArray.firstJsonObject(default: JSONObject = JSONObject()): JSONObject {
    if (this.length() == 0 || this.isEmpty) return default
    return KJSONUtils.getJSONObject(this, 0)
}

fun JSONArray.firstStringOrDefault(default: String = ""): String {
    if (this.length() == 0 || this.isEmpty) return default
    return KJSONUtils.getString(this, 0, default)
}

fun JSONArray.firstIntOrDefault(default: Int = 0): Int {
    if (this.length() == 0 || this.isEmpty) return default
    return KJSONUtils.getInt(this, 0, default)
}

fun JSONArray.firstLongOrDefault(default: Long = 0L): Long {
    if (this.length() == 0 || this.isEmpty) return default
    return KJSONUtils.getLong(this, 0, default)
}

fun JSONArray.firstDoubleOrDefault(default: Double = 0.0): Double {
    if (this.length() == 0 || this.isEmpty) return default
    return KJSONUtils.getDouble(this, 0, default)
}

fun JSONArray.firstBooleanOrDefault(default: Boolean = false): Boolean {
    if (this.length() == 0 || this.isEmpty) return default
    return KJSONUtils.getBoolean(this, 0, default)
}

fun JSONArray.lastJsonObject(default: JSONObject = JSONObject()): JSONObject {
    if (this.length() == 0 || this.isEmpty) return default
    return KJSONUtils.getJSONObject(this, this.length() - 1)
}

fun JSONArray.lastStringOrDefault(default: String = ""): String {
    if (this.length() == 0 || this.isEmpty) return default
    return KJSONUtils.getString(this, this.length() - 1, default)
}

fun JSONArray.lastIntOrDefault(default: Int = 0): Int {
    if (this.length() == 0 || this.isEmpty) return default
    return KJSONUtils.getInt(this, this.length() - 1, default)
}

fun JSONArray.lastLongOrDefault(default: Long = 0L): Long {
    if (this.length() == 0 || this.isEmpty) return default
    return KJSONUtils.getLong(this, this.length() - 1, default)
}

fun JSONArray.lastDoubleOrDefault(default: Double = 0.0): Double {
    if (this.length() == 0 || this.isEmpty) return default
    return KJSONUtils.getDouble(this, this.length() - 1, default)
}

fun JSONArray.lastBooleanOrDefault(default: Boolean = false): Boolean {
    if (this.length() == 0 || this.isEmpty) return default
    return KJSONUtils.getBoolean(this, this.length() - 1, default)
}

fun JSONArray.toJSONObjectArray(): Array<JSONObject> {
    return KJSONUtils.toJSONObjects(this)
}