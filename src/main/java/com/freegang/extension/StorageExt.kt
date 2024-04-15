package com.freegang.extension

import android.content.Context
import com.freegang.ktutils.io.KStorageUtils
import java.io.File

/**
 * 获取外置存储器的根路径，通常是：/storage/emulated/0/
 */
val Context.storageRootPath: String
    get() = KStorageUtils.getStoragePath(this)

/**
 * 获取外置存储器的根文件对象，通常是：/storage/emulated/0/
 */
val Context.storageRootFile: File
    get() = KStorageUtils.getStoragePath(this).toFile()

/**
 * 检查外置存储器是否可读写
 *
 * @param directory 目录名，如果为null则在根目录创建
 */
fun Context.hasOperationStorage(directory: File? = null): Boolean {
    return KStorageUtils.hasOperationStorage(this, directory)
}

/**
 * 检查是否具有外置存储管理或读写权限
 */
fun Context.hasStoragePermission(): Boolean {
    return KStorageUtils.hasStoragePermission(this)
}