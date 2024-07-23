package com.freegang.ktutils.media

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.documentfile.provider.DocumentFile
import java.io.File
import java.io.FileNotFoundException
import java.io.InputStream
import java.io.OutputStream

/**
 * SAF读写工具类, 需要授权某个目录。请手动加入以下依赖：
 * ```
 * implementation("androidx.documentfile:documentfile:<last>")
 * ```
 *
 * 参考文档: https://developer.android.google.cn/training/data-storage/shared/documents-files?hl=zh-cn#persist-permissions
 */
object KDocumentUtils {

    // 原始数据流
    const val MIME_TYPE_OCTET_STREAM = "application/octet-stream"

    /**
     * 保留(持久化)指定 目录/文件 的访问控制权限。
     * ```
     * // MainActivity
     * val launcher = registerForActivityResult(ActivityResultContracts.OpenDocumentTree()) {uri->
     *     Log.d("TAG", "Uri: $uri")
     *     KDocumentUtils.takePersistableUriPermission(this, uri!!)
     * }
     * launcher.launch(null)
     * ```
     *
     * @param context 应用程序上下文。用于获取 ContentResolver 实例。
     * @param uri 被授予访问控制权限的 目录/文件。
     */
    @JvmStatic
    fun takePersistableUriPermission(context: Context, uri: Uri) {
        val takeFlags: Int = Intent.FLAG_GRANT_READ_URI_PERMISSION or
                Intent.FLAG_GRANT_WRITE_URI_PERMISSION
        context.applicationContext.contentResolver.takePersistableUriPermission(uri, takeFlags)
    }

    /**
     * 获取指定文件的 DocumentFile 实例。
     *
     * @param context 应用程序上下文。
     * @param uri 目录的 Uri。应该是已经通过 SAF 授权的目录的 Uri。
     * @return 如果文件存在，返回表示该文件的 DocumentFile 实例；否则，返回 null。
     */
    @JvmStatic
    fun getDocumentFile(
        context: Context,
        uri: Uri?
    ): DocumentFile? {
        uri ?: return null
        return DocumentFile.fromSingleUri(context, uri)
    }

    /**
     * 获取指定目录下的指定文件。
     *
     * @param context 应用程序上下文。
     * @param parentTreeUri 要搜索的目录的 Uri。应该是已经通过 SAF 授权的目录的 Uri。
     * @param displayName 要查找的文件的名称，如果为空，则返回指定目录（当前目录）。
     * @return 如果在指定目录下找到了指定的文件，返回该文件的；否则，返回 null。
     */
    @JvmStatic
    @JvmOverloads
    fun getDocumentChildFile(
        context: Context,
        parentTreeUri: Uri,
        displayName: String = "",
    ): DocumentFile? {
        return if (displayName.isEmpty()) {
            DocumentFile.fromTreeUri(context, parentTreeUri)
        } else {
            DocumentFile.fromTreeUri(context, parentTreeUri)?.findFile(displayName)
        }
    }

    /**
     * 创建多级目录，如果子目录已经存在，则直接返回该子目录。
     *
     * @param documentFile 父目录。
     * @param displayName 要创建的子目录的名称。
     */
    @JvmStatic
    fun createDirectories(
        documentFile: DocumentFile?,
        displayName: String,
    ): DocumentFile? {
        var parentDirectory = documentFile
        for (directory in displayName.split("/")) {
            parentDirectory = parentDirectory?.findFile(directory)
                ?: parentDirectory?.createDirectory(directory)
        }
        return parentDirectory
    }

    /**
     * 创建文件，默认创建一个空的原始文件
     *
     * @param documentFile 父目录。
     * @param displayName 要创建的子文件的名称。
     * @param mimeType 文件类型。
     */
    @JvmStatic
    @JvmOverloads
    fun createFile(
        documentFile: DocumentFile?,
        displayName: String,
        mimeType: String = MIME_TYPE_OCTET_STREAM,
    ): DocumentFile? {
        return documentFile?.createFile(mimeType, displayName)
    }

    /**
     * 通过指定文件打开一个输入流。
     *
     * @param context 应用程序上下文。
     * @param documentFile 指定文件。
     * @return 如果文件存在并且可以读取，返回表示文件内容的 InputStream 实例；否则，返回 null。
     * @throws FileNotFoundException 如果文件不存在或无法打开。
     * 注意：调用者应该在使用完 InputStream 后合理关闭它，以释放系统资源。
     */
    @JvmStatic
    fun openInputStream(
        context: Context,
        documentFile: DocumentFile,
    ): InputStream? {
        return context.applicationContext.contentResolver.openInputStream(documentFile.uri)
    }

    /**
     * 通过指定文件打开一个输出流。
     *
     * @param context 应用程序上下文。
     * @param documentFile 指定文件。
     * @param mode 打开模式，如 "w" 表示写入，可选值有 "wa" 追加写入，"rw" 读写，"rwt" 读写并清空。
     * @return 如果文件存在并且可以读取，返回表示文件内容的 InputStream 实例；否则，返回 null。
     * @throws FileNotFoundException 如果文件不存在或无法打开。
     * 注意：调用者应该在使用完 InputStream 后合理关闭它，以释放系统资源。
     */
    @JvmStatic
    fun openOutputStream(
        context: Context,
        documentFile: DocumentFile,
        mode: String = "w",
    ): OutputStream? {
        return context.applicationContext.contentResolver.openOutputStream(documentFile.uri, mode)
    }

    /**
     * 获取某个文件的 FileProviderUri 形式
     * @param context Context
     * @param file 文件
     * @param authority see: https://blog.csdn.net/AoXue2017/article/details/126105906
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