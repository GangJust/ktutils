package com.freegang.extension

import android.content.Context
import com.freegang.ktutils.app.KSPUtils

/**
 * 获取 String 类型数据
 *
 * @param key key
 * @param default 默认值
 * @param spName SharedPreferences 名称
 */
fun Context.getSpString(
    key: String,
    default: String = "",
    spName: String = KSPUtils.DEFAULT_SP_NAME,
): String {
    return KSPUtils.getString(this, key, default, spName)
}

/**
 * 获取 Boolean 类型数据
 *
 * @param key key
 * @param default 默认值
 * @param spName SharedPreferences 名称
 */
fun Context.getSpBoolean(
    key: String,
    default: Boolean = false,
    spName: String = KSPUtils.DEFAULT_SP_NAME,
): Boolean {
    return KSPUtils.getBoolean(this, key, default, spName)
}

/**
 * 获取 Int 类型数据
 *
 * @param key key
 * @param default 默认值
 * @param spName SharedPreferences 名称
 */
fun Context.getSpInt(
    key: String,
    default: Int = 0,
    spName: String = KSPUtils.DEFAULT_SP_NAME,
): Int {
    return KSPUtils.getInt(this, key, default, spName)
}

/**
 * 获取 Long 类型数据
 *
 * @param key key
 * @param default 默认值
 * @param spName SharedPreferences 名称
 */
fun Context.getSpLong(
    key: String,
    default: Long = 0L,
    spName: String = KSPUtils.DEFAULT_SP_NAME,
): Long {
    return KSPUtils.getLong(this, key, default, spName)
}

/**
 * 获取 Float 类型数据
 *
 * @param key key
 * @param default 默认值
 * @param spName SharedPreferences 名称
 */
fun Context.getSpFloat(
    key: String,
    default: Float = 0f,
    spName: String = KSPUtils.DEFAULT_SP_NAME,
): Float {
    return KSPUtils.getFloat(this, key, default, spName)
}

/**
 * 保存 String 类型数据
 *
 * @param key key
 * @param value value
 * @param spName SharedPreferences 名称
 */
fun Context.putSpString(
    key: String,
    value: String,
    spName: String = KSPUtils.DEFAULT_SP_NAME,
) {
    KSPUtils.putString(this, key, value, spName)
}

/**
 * 保存 Boolean 类型数据
 *
 * @param key key
 * @param value value
 * @param spName SharedPreferences 名称
 */
fun Context.putSpBoolean(
    key: String,
    value: Boolean,
    spName: String = KSPUtils.DEFAULT_SP_NAME,
) {
    KSPUtils.putBoolean(this, key, value, spName)
}

/**
 * 保存 Int 类型数据
 *
 * @param key key
 * @param value value
 * @param spName SharedPreferences 名称
 */
fun Context.putSpInt(
    key: String,
    value: Int,
    spName: String = KSPUtils.DEFAULT_SP_NAME,
) {
    KSPUtils.putInt(this, key, value, spName)
}

/**
 * 保存 Long 类型数据
 *
 * @param key key
 * @param value value
 * @param spName SharedPreferences 名称
 */
fun Context.putSpLong(
    key: String,
    value: Long,
    spName: String = KSPUtils.DEFAULT_SP_NAME,
) {
    KSPUtils.putLong(this, key, value, spName)
}

/**
 * 保存 Float 类型数据
 *
 * @param key key
 * @param value value
 * @param spName SharedPreferences 名称
 */
fun Context.putSpFloat(
    key: String,
    value: Float,
    spName: String = KSPUtils.DEFAULT_SP_NAME,
) {
    KSPUtils.putFloat(this, key, value, spName)
}

/**
 * 删除指定 key 的数据
 *
 * @param key key
 * @param spName SharedPreferences 名称
 */
fun Context.removeSpValue(
    key: String,
    spName: String = KSPUtils.DEFAULT_SP_NAME,
) {
    KSPUtils.remove(this, key, spName)
}

/**
 * 清空 SharedPreferences
 *
 * @param spName SharedPreferences 名称
 */
fun Context.clearSp(spName: String = KSPUtils.DEFAULT_SP_NAME) {
    KSPUtils.clear(this, spName)
}