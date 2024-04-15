package com.freegang.ktutils.io

import android.Manifest
import android.content.Context
import android.os.Build
import android.os.Environment
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import com.freegang.extension.child
import com.freegang.extension.toFile
import java.io.File

object KStorageUtils {

    /**
     * 获取外置存储器的根路径。
     * 通常为：/storage/emulated/0/。
     *
     * @param context Context
     * @return 外置存储器的根路径
     */
    @JvmStatic
    fun getStoragePath(context: Context): String {
        var externalFilesDir = context.getExternalFilesDir(null) ?: return ""
        do {
            externalFilesDir = externalFilesDir.parentFile ?: return ""
        } while (externalFilesDir.absolutePath.contains("/Android"))

        return externalFilesDir.absolutePath.plus("/")
    }

    /**
     * 获取外置存储器的根文件对象。
     * 通常为：/storage/emulated/0/。
     *
     * @param context Context
     * @return 外置存储器的根文件对象
     */
    @JvmStatic
    fun getStorageFile(context: Context): File {
        return getStoragePath(context).toFile()
    }

    /**
     * 检查外置存储器是否可读写。
     * 在外置存储器的指定目录下尝试创建和删除一个名为`.temp`的临时文件，
     * 根据创建和删除成功与否来判断外置存储器的读写权限是否可用。
     *
     * @param context Context
     * @param directory 目录名, 如果null则在根目录创建
     * @return
     */
    @JvmStatic
    @Synchronized
    fun hasOperationStorage(
        context: Context,
        directory: File? = null,
    ): Boolean {
        return try {
            val test = directory?.child(".temp") ?: getStorageFile(context).child(".temp")
            val created = test.createNewFile()
            if (created || test.exists()) test.delete()
            true
        } catch (e: Exception) {
            false
        }
    }


    /**
     * 检查是否具有外置存储管理或读写权限
     *
     * @param context Context
     * @return
     */
    @JvmStatic
    fun hasStoragePermission(context: Context): Boolean {
        val permission = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
        ).all {
            ContextCompat.checkSelfPermission(context, it) == PermissionChecker.PERMISSION_GRANTED
        }

        // 外置存储器管理权限 Android 11+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {

            // 高版本 但 低Target
            // if (context.applicationInfo.targetSdkVersion < Build.VERSION_CODES.R) {
            //     return permission
            // }

            return Environment.isExternalStorageManager() || permission
        }

        // 外置存储器读写权限 Android 11-
        return permission
    }
}
