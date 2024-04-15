package com.freegang.ktutils.media

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.content.PermissionChecker
import com.freegang.extension.storageRootPath
import java.io.File
import java.io.InputStream
import java.io.OutputStream

/**
 * 媒体操作工具类。
 *
 * 如果想要确切查看媒体数据库表结构, 通常Android手机媒体数据库路径位于: /data/data/com.android.providers.media/databases/
 *
 * 参考文档: https://developer.android.google.cn/training/data-storage/shared/media?hl=zh-cn
 */
object KMediaUtils {

    /**
     * 判断是否具有媒体读取权限。
     *
     * @param context Context
     */
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @JvmStatic
    fun hasMediaPermission(context: Context): Boolean {
        return arrayOf(
            Manifest.permission.READ_MEDIA_VIDEO,
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.READ_MEDIA_AUDIO,
        ).all {
            ContextCompat.checkSelfPermission(context, it) == PermissionChecker.PERMISSION_GRANTED
        }
    }

    /**
     * 获取图像真实路径
     *
     * @param context 上下文对象
     * @param uri     图像的 content:// URI
     * @return 图像的真实路径，如果无法获取或出现错误则返回 null
     */
    @JvmStatic
    fun getImageRealPath(context: Context, uri: Uri): String? {
        return getMediaRealPath(context, uri)
    }

    /**
     * 获取视频真实路径
     *
     * @param context 上下文对象
     * @param uri     视频的 content:// URI
     * @return 视频的真实路径，如果无法获取或出现错误则返回 null
     */
    @JvmStatic
    fun getVideoRealPath(context: Context, uri: Uri): String? {
        return getMediaRealPath(context, uri)
    }

    /**
     * 获取音频真实路径
     *
     * @param context 上下文对象
     * @param uri     音频的 content:// URI
     * @return 音频的真实路径，如果无法获取或出现错误则返回 null
     */
    @JvmStatic
    fun getAudioRealPath(context: Context, uri: Uri): String? {
        return getMediaRealPath(context, uri)
    }

    /**
     * 获取媒体真实路径
     *
     * @param context 上下文对象
     * @param uri     content:// URI
     * @return 媒体的真实路径，如果无法获取或出现错误则返回 null
     */
    @JvmStatic
    fun getMediaRealPath(context: Context, uri: Uri): String? {
        val projection = arrayOf(MediaStore.MediaColumns.DATA)
        var cursor: Cursor? = null
        try {
            cursor = context.contentResolver.query(uri, projection, null, null, null)
            cursor?.let {
                if (it.moveToFirst()) {
                    val columnIndex = it.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
                    return it.getString(columnIndex)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            cursor?.close()
        }
        return null
    }

    /**
     * 通知媒体扫描指定文件
     * @param context context
     * @param path 保存路径, 可能会限制: [Environment.getExternalStoragePublicDirectory] 作为父文件夹
     * @param callback 回调方法, 刷新成功才进行回调
     */
    @JvmStatic
    @JvmOverloads
    fun notifyMediaUpdate(
        context: Context,
        path: String,
        callback: ((path: String, uri: Uri) -> Unit)? = null,
    ) {
        MediaScannerConnection.scanFile(
            context,
            arrayOf(path),
            null,
        ) { resultPath, uri ->
            callback?.invoke(resultPath, uri)
        }
    }

    /**
     * 新增或更新图片文件, 如果存在则更新, 否则创建并写入, 请注意该方法需要媒体读取权限(Api 33必要)。
     *
     * @param context Context
     * @param path 父路径, 通常是 [Environment.getExternalStoragePublicDirectory] 目录或其子目录,如: DCIM/test/
     * @param filename 被保存的媒体文件名, 不能含路径分割符, 如: test.png、test.jpg
     * @param callback 携带一个输出流回调参数, 请合理使用并关闭它
     * @return
     */
    @JvmStatic
    @RequiresApi(Build.VERSION_CODES.Q)
    fun insertOrUpdateImage(
        context: Context,
        path: String,
        filename: String,
        callback: (OutputStream?) -> Unit
    ) {
        val uri = queryOrInsert(
            context,
            path,
            filename,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            "image/*"
        )
        if (uri != null) {
            runCatching {
                openOutputStream(context, uri).use {
                    callback.invoke(it)
                }
            }
        }
    }

    /**
     * 新增或更新图片文件, 如果存在则更新, 否则创建并写入, 请注意该方法需要媒体读取权限(Api 33必要)。
     *
     * @param context Context
     * @param file 一个指定的文件, 但通常是 [Environment.getExternalStoragePublicDirectory] 目录或其子目录, 该方法会默认删除首位出现的 `外置存储器路径`
     * @param callback 携带一个输出流回调参数, 请合理使用并关闭它
     * @return
     */
    @JvmStatic
    @RequiresApi(Build.VERSION_CODES.Q)
    fun insertOrUpdateImage(
        context: Context,
        file: File,
        callback: (OutputStream?) -> Unit
    ) {
        val childPath = file.absolutePath.removePrefix(context.storageRootPath)
        val path = childPath.substringBeforeLast("/")
        val filename = childPath.substringAfterLast("/")
        insertOrUpdateImage(
            context = context,
            path = path,
            filename = filename,
            callback = callback,
        )
    }

    /**
     * 新增或更新视频文件, 如果存在则更新, 否则创建并写入, 请注意该方法需要媒体读取权限(Api 33必要)。
     *
     * @param context Context
     * @param path 父路径, 通常是 [Environment.getExternalStoragePublicDirectory] 目录或其子目录,如: DCIM/test/
     * @param filename 被保存的媒体文件名, 不能含路径分割符, 如: test.mp4、test.avi
     * @param callback 携带一个输出流回调参数, 请合理使用并关闭它
     * @return
     */
    @JvmStatic
    @RequiresApi(Build.VERSION_CODES.Q)
    fun insertOrUpdateVideo(
        context: Context,
        path: String,
        filename: String,
        callback: (OutputStream?) -> Unit
    ) {
        val uri = queryOrInsert(
            context,
            path,
            filename,
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            "video/*"
        )
        if (uri != null) {
            runCatching {
                openOutputStream(context, uri).use {
                    callback.invoke(it)
                }
            }
        }
    }

    /**
     * 新增或更新视频文件, 如果存在则更新, 否则创建并写入, 请注意该方法需要媒体读取权限(Api 33必要)。
     *
     * @param context Context
     * @param file 一个指定的文件, 但通常是 [Environment.getExternalStoragePublicDirectory] 目录或其子目录, 该方法会默认删除首位出现的 `外置存储器路径`
     * @param callback 携带一个输出流回调参数, 请合理使用并关闭它
     * @return
     */
    @JvmStatic
    @RequiresApi(Build.VERSION_CODES.Q)
    fun insertOrUpdateVideo(
        context: Context,
        file: File,
        callback: (OutputStream?) -> Unit
    ) {
        val childPath = file.absolutePath.removePrefix(context.storageRootPath)
        val path = childPath.substringBeforeLast("/")
        val filename = childPath.substringAfterLast("/")
        insertOrUpdateVideo(
            context = context,
            path = path,
            filename = filename,
            callback = callback,
        )
    }

    /**
     * 新增或更新音频文件, 如果存在则更新, 否则创建并写入, 请注意该方法需要媒体读取权限(Api 33必要)。
     *
     * @param context Context
     * @param path 父路径, 通常是 [Environment.getExternalStoragePublicDirectory] 目录或其子目录,如: DCIM/test/
     * @param filename 被保存的媒体文件名, 不能含路径分割符, 如: test.mp3、test.wav
     * @param callback 携带一个输出流回调参数, 请合理使用并关闭它
     * @return
     */
    @JvmStatic
    @RequiresApi(Build.VERSION_CODES.Q)
    fun insertOrUpdateAudio(
        context: Context,
        path: String,
        filename: String,
        callback: (OutputStream?) -> Unit
    ) {
        val uri = queryOrInsert(
            context,
            path,
            filename,
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            "audio/*"
        )
        if (uri != null) {
            runCatching {
                openOutputStream(context, uri).use {
                    callback.invoke(it)
                }
            }
        }
    }

    /**
     * 新增或更新音频文件, 如果存在则更新, 否则创建并写入, 请注意该方法需要媒体读取权限(Api 33必要)。
     *
     * @param context Context
     * @param file 一个指定的文件, 但通常是 [Environment.getExternalStoragePublicDirectory] 目录或其子目录, 该方法会默认删除首位出现的 `外置存储器路径`
     * @param callback 携带一个输出流回调参数, 请合理使用并关闭它
     * @return
     */
    @JvmStatic
    @RequiresApi(Build.VERSION_CODES.Q)
    fun insertOrUpdateAudio(
        context: Context,
        file: File,
        callback: (OutputStream?) -> Unit
    ) {
        val childPath = file.absolutePath.removePrefix(context.storageRootPath)
        val path = childPath.substringBeforeLast("/")
        val filename = childPath.substringAfterLast("/")
        insertOrUpdateAudio(
            context = context,
            path = path,
            filename = filename,
            callback = callback,
        )
    }

    /**
     * 新增或更新下载文件, 如果存在则更新, 否则创建并写入, 请注意该方法需要媒体读取权限(Api 33必要)。
     *
     * @param context Context
     * @param path 父路径, 通常是 [Environment.getExternalStoragePublicDirectory] 目录或其子目录,如: DCIM/test/
     * @param filename 被保存的媒体文件名, 不能含路径分割符, 如: test.mp3、test.wav
     * @param mode 文件操作模式, 通常是: "r", "w", "wt", "wa", "rw" or "rwt".
     * @param callback 携带一个输出流回调参数, 请合理使用并关闭它
     * @return
     */
    @JvmStatic
    @RequiresApi(Build.VERSION_CODES.Q)
    fun insertOrUpdateDownload(
        context: Context,
        path: String,
        filename: String,
        mode: String = "w",
        callback: (out: OutputStream?) -> Unit,
    ) {
        val uri = queryOrInsert(context, path, filename, MediaStore.Downloads.EXTERNAL_CONTENT_URI)
        if (uri != null) {
            runCatching {
                openOutputStream(context, uri, mode).use {
                    callback.invoke(it)
                }
            }
        }
    }

    /**
     * 新增或更新下载文件, 如果存在则更新, 否则创建并写入, 请注意该方法需要媒体读取权限(Api 33必要)。
     *
     * @param context Context
     * @param file 一个指定的文件, 但通常是 [Environment.getExternalStoragePublicDirectory] 目录或其子目录, 该方法会默认删除首位出现的 `外置存储器路径`
     * @param mode 文件操作模式, 通常是: "r", "w", "wt", "wa", "rw" or "rwt".
     * @param callback 携带一个输出流回调参数, 请合理使用并关闭它
     * @return
     */
    @JvmStatic
    @RequiresApi(Build.VERSION_CODES.Q)
    fun insertOrUpdateDownload(
        context: Context,
        file: File,
        mode: String = "w",
        callback: (out: OutputStream?) -> Unit,
    ) {
        val childPath = file.absolutePath.removePrefix(context.storageRootPath)
        val path = childPath.substringBeforeLast("/")
        val filename = childPath.substringAfterLast("/")
        insertOrUpdateDownload(
            context = context,
            path = path,
            filename = filename,
            mode = mode,
            callback = callback,
        )
    }

    /**
     * 新增或更新媒体记录, 如果存在则返回, 否则插入记录, 请注意该方法需要媒体读取权限(Api 33必要)。
     *
     * @param context Context
     * @param path 父路径, 通常是 [Environment.getExternalStoragePublicDirectory] 目录或其子目录,如: DCIM/test/
     * @param filename 被保存的媒体文件名, 不能含路径分割符, 如: test.png、test.jpg
     * @param mediaUri 目标媒体表Uri, 如: [MediaStore.Images.Media.EXTERNAL_CONTENT_URI]
     * @param mimeType 目标媒体类型, 如: image/jpeg, image/`*`
     * @return 如果成功则返回它的Uri,否则返回null
     */
    @JvmStatic
    @JvmOverloads
    @RequiresApi(Build.VERSION_CODES.Q)
    fun queryOrInsert(
        context: Context,
        path: String,
        filename: String,
        mediaUri: Uri,
        mimeType: String? = null,
    ): Uri? {
        // relative_path 首位不能出现 `/`, 并且末尾有一个 `/`
        val formatPath = path.removePrefix("/").removePrefix("/").plus("/")

        if (filename.contains("/")) throw IllegalArgumentException("filename cannot contain `/`")

        val resolver = context.contentResolver

        val uri = queryUri(context, path, filename, mediaUri)
        if (uri != null) return uri

        // File doesn't exist, create a new one
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
            put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
            put(MediaStore.MediaColumns.RELATIVE_PATH, formatPath) // api 29
        }
        return resolver.insert(mediaUri, contentValues)
    }


    /**
     * 新增或更新媒体记录, 如果存在则返回, 否则插入记录, 请注意该方法需要媒体读取权限(Api 33必要)。
     *
     * @param context Context
     * @param file 一个指定的文件, 但通常是 [Environment.getExternalStoragePublicDirectory] 目录或其子目录, 该方法会默认删除首位出现的 `外置存储器路径`
     * @param mediaUri 目标媒体表Uri, 如: [MediaStore.Images.Media.EXTERNAL_CONTENT_URI]
     * @param mimeType 目标媒体类型, 如: image/jpeg, image/`*`
     * @return 如果成功则返回它的Uri,否则返回null
     */
    @JvmStatic
    @JvmOverloads
    @RequiresApi(Build.VERSION_CODES.Q)
    fun queryOrInsert(
        context: Context,
        file: File,
        mediaUri: Uri,
        mimeType: String? = null,
    ): Uri? {
        val childPath = file.absolutePath.removePrefix(context.storageRootPath)
        val path = childPath.substringBeforeLast("/")
        val filename = childPath.substringAfterLast("/")
        return queryOrInsert(
            context = context,
            path = path,
            filename = filename,
            mediaUri = mediaUri,
            mimeType = mimeType,
        )
    }

    /**
     * 查询某个媒体Uri, 请注意该方法需要媒体读取权限(Api 33必要)。
     *
     * @param context Context
     * @param path 父路径, 通常是 [Environment.getExternalStoragePublicDirectory] 目录或其子目录,如: DCIM/test/
     * @param filename 被保存的媒体文件名, 不能含路径分割符, 如: test.png、test.jpg
     * @param mediaUri 目标媒体表Uri, 如: [MediaStore.Images.Media.EXTERNAL_CONTENT_URI]
     * @return 如果该媒体存在则返回它的Uri, 否则返回 null
     */
    @JvmStatic
    @RequiresApi(Build.VERSION_CODES.Q)
    fun queryUri(
        context: Context,
        path: String,
        filename: String,
        mediaUri: Uri,
    ): Uri? {
        // relative_path 首位不能出现 `/`, 并且末尾有一个 `/`
        val formatPath = path.removePrefix("/").removePrefix("/").plus("/")

        if (filename.contains("/")) throw IllegalArgumentException("filename cannot contain `/`")

        val resolver = context.contentResolver

        val projection = arrayOf(MediaStore.MediaColumns._ID)
        val selection = "${MediaStore.MediaColumns.DISPLAY_NAME}=? AND ${MediaStore.MediaColumns.RELATIVE_PATH}=?"
        val selectionArgs = arrayOf(filename, formatPath)

        val cursor = resolver.query(mediaUri, projection, selection, selectionArgs, null)

        val uri: Uri? = if (cursor?.moveToFirst() == true) {
            // File exists, get its Uri
            val id =
                cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns._ID))
            Uri.withAppendedPath(mediaUri, id.toString())
        } else {
            null
        }

        cursor?.close()

        return uri
    }

    /**
     * 查询某个媒体Uri, 请注意该方法需要媒体读取权限(Api 33必要)。
     *
     * @param context Context
     * @param file 一个指定的文件, 但通常是 [Environment.getExternalStoragePublicDirectory] 目录或其子目录, 该方法会默认删除首位出现的 `外置存储器路径`
     * @param mediaUri 目标媒体表Uri, 如: [MediaStore.Images.Media.EXTERNAL_CONTENT_URI]
     * @return 如果该图片存在则返回它的Uri, 否则返回 null
     */
    @JvmStatic
    @RequiresApi(Build.VERSION_CODES.Q)
    fun queryUri(
        context: Context,
        file: File,
        mediaUri: Uri,
    ): Uri? {
        val childPath = file.absolutePath.removePrefix(context.storageRootPath)
        val path = childPath.substringBeforeLast("/")
        val filename = childPath.substringAfterLast("/")
        return queryUri(
            context = context,
            path = path,
            filename = filename,
            mediaUri = mediaUri,
        )
    }

    /**
     * 通过某个媒体Uri打开输出流并返回
     * @param context Context
     * @param uri 被操作的文件Uri
     * @param mode 文件操作模式, 通常是: "r", "w", "wt", "wa", "rw" or "rwt".
     * @return 输出流
     */
    @JvmStatic
    fun openOutputStream(
        context: Context,
        uri: Uri,
        mode: String = "w",
    ): OutputStream? {
        val resolver = context.applicationContext.contentResolver
        return resolver.openOutputStream(uri, mode)
    }

    /**
     * 通过某个媒体Uri打开输入流并返回
     * @param context Context
     * @param uri 被操作的文件Uri
     * @return 输如流
     */
    @JvmStatic
    fun openInputStream(
        context: Context,
        uri: Uri,
    ): InputStream? {
        val resolver = context.applicationContext.contentResolver
        return resolver.openInputStream(uri)
    }

    /**
     * 获取某个文件的 FileProviderUri 形式
     * @param context Context
     * @param file 文件
     * @param authority 由 AndroidManifset.xml <provider android:name="androidx.core.content.FileProvider" ...> 指定的 `authorities` 节点
     */
    @JvmStatic
    @JvmOverloads
    fun getFileProviderUri(
        context: Context,
        file: File,
        authority: String = "${context.applicationContext.packageName}.fileprovider",
    ): Uri? {
        return FileProvider.getUriForFile(context.applicationContext, authority, file)
    }
}