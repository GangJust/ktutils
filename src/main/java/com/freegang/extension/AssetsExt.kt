package com.freegang.extension

import android.content.Context
import android.content.res.AssetManager
import com.freegang.ktutils.app.KAssetsUtils

/**
 * 以文本形式打开Assets中的文件并返回内容
 */
fun Context.readAssetsAsText(fileName: String): String {
    return KAssetsUtils.readAsText(this, fileName)
}

/**
 * 以字节数组形式打开Assets中的文件并返回内容
 */
fun Context.readAssetsAsBytes(fileName: String): ByteArray {
    return KAssetsUtils.readAsBytes(this, fileName)
}

/**
 * 将 Assets 中的文件解压到临时文件并返回该临时文件对象。
 */
fun AssetManager.readAssetsAsText(fileName: String): String {
    return this.open(fileName).readBytes().decodeToString()
}

/**
 * 将 Assets 中的文件解压到临时文件并返回该临时文件对象。
 */
fun AssetManager.readAssetsAsBytes(fileName: String): ByteArray {
    return this.open(fileName).readBytes()
}
