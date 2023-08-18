package com.freegang.ktutils.io

import java.io.File

object KFileLockUtils {
    private val lockedFiles = mutableListOf<String>()

    fun lock(file: File): Boolean {
        val filePath = file.absolutePath
        if (lockedFiles.contains(filePath)) {
            throw Exception("The lock represented by '$filePath' has already been used. If you need to use it, please unlock it first!")
        }

        if (file.exists()) {
            return true // 锁已存在
        }

        return try {
            file.createNewFile()
                .also { lockedFiles.add(filePath) }
        } catch (e: Exception) {
            false // 加锁失败
        }
    }

    fun unlock(file: File): Boolean {
        val filePath = file.absolutePath
        if (!file.exists()) {
            return false // 锁不存在
        }

        return try {
            file.delete() // 删除文件，返回 true 表示解锁成功
                .also { lockedFiles.remove(filePath) } // 从 lockedFiles 列表中移除文件路径
        } catch (e: Exception) {
            false // 解锁失败
        }
    }

    fun locked(file: File): Boolean {
        return file.exists() // 如果文件存在，则表示已经加锁，否则表示未加锁
    }
}
