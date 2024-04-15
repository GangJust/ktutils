package com.freegang.extension

import android.content.Context
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import com.freegang.ktutils.media.KMediaUtils
import java.io.File

/**
 * 检查是否有媒体文件的权限。
 *
 * @return 是否有媒体文件的权限。
 */
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
fun Context.hasMediaPermission(): Boolean {
    return KMediaUtils.hasMediaPermission(this)
}

/**
 * 获取图片文件的真实路径。
 *
 * @param uri 图片文件的 Uri。
 */
fun Context.getImageRealPath(uri: Uri): String? {
    return KMediaUtils.getImageRealPath(this, uri)
}

/**
 * 获取视频文件的真实路径。
 *
 * @param uri     视频的 content:// URI
 * @return 视频的真实路径，如果无法获取或出现错误则返回 null
 */
fun Context.getVideoRealPath(uri: Uri): String? {
    return KMediaUtils.getVideoRealPath(this, uri)
}

/**
 * 获取音频真实路径
 *
 * @param uri     音频的 content:// URI
 * @return 音频的真实路径，如果无法获取或出现错误则返回 null
 */
fun Context.getAudioRealPath(uri: Uri): String? {
    return KMediaUtils.getAudioRealPath(this, uri)
}

/**
 * 获取媒体文件的真实路径。
 *
 * @param uri 媒体文件的Uri。
 * @return 媒体文件的真实路径。
 */
fun Context.getMediaRealPath(uri: Uri): String? {
    return KMediaUtils.getMediaRealPath(this, uri)
}

/**
 * 使用FileProvider获取文件的Uri。
 *
 * @param file 获取Uri的文件。
 * @param authority FileProvider 的权限。
 * @return 文件的 Uri。
 */
fun Context.getFileProviderUri(
    file: File,
    authority: String? = null,
): Uri? {
    val defaultAuthority = authority ?: "${applicationContext.packageName}.fileprovider"
    return KMediaUtils.getFileProviderUri(this, file, defaultAuthority)
}

/**
 * 使用 FileProvider 获取文件的 URI。
 *
 * @param file 获取 Uri 的文件。
 * @param authority FileProvider 的权限。
 * @return 文件的 Uri。
 */
fun File.getFileProviderUri(
    context: Context,
    authority: String = "${context.applicationContext.packageName}.fileprovider",
): Uri? {
    return KMediaUtils.getFileProviderUri(context, this, authority)
}