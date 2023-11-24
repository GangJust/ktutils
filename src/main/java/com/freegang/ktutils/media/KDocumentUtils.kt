package com.freegang.ktutils.media

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
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

    /**
     * 保留(持久化)指定 目录/文件 的访问控制权限。
     *
     * @param context 应用程序上下文。用于获取 ContentResolver 实例。
     * @param uri 被授予访问控制权限的 目录/文件
     */
    @JvmStatic
    fun takePersistableUriPermission(context: Context, uri: Uri) {
        val takeFlags: Int = Intent.FLAG_GRANT_READ_URI_PERMISSION or
                Intent.FLAG_GRANT_WRITE_URI_PERMISSION
        context.applicationContext.contentResolver.takePersistableUriPermission(uri, takeFlags)
    }

    /**
     * 在指定的父目录下创建子目录。
     *
     * @param context 应用程序上下文。用于获取 DocumentFile 实例。
     * @param parentTreeUri 父目录的 Uri。应该是已经通过 SAF 授权的目录的 Uri。
     * @param directoryName 要创建的子目录的名称。
     * @return 如果子目录成功创建，返回表示该子目录的 DocumentFile 实例；否则，返回 null。
     */
    @JvmStatic
    fun createSubdirectory(
        context: Context,
        parentTreeUri: Uri,
        directoryName: String,
    ): DocumentFile? {
        // 使用 SAF 的 DocumentFile.fromTreeUri 方法获取表示父目录的 DocumentFile 实例
        val parentDirectory = DocumentFile.fromTreeUri(context, parentTreeUri)

        // 使用 DocumentFile 的 createDirectory 方法创建子目录
        // 如果子目录成功创建，createDirectory 方法将返回一个新的 DocumentFile 实例，表示新创建的子目录
        // 如果子目录未能创建，createDirectory 方法将返回 null
        return parentDirectory?.createDirectory(directoryName)
    }

    /**
     * 获取指定目录下指定子文件夹的 Uri。
     *
     * @param context 应用程序上下文。用于获取 ContentResolver 和 DocumentFile 实例。
     * @param parentTreeUri 要搜索的目录的 Uri。应该是已经通过 SAF 授权的目录的 Uri。
     * @param directoryName 要搜索的子文件夹的名称。
     * @return 如果在指定目录下找到了指定的子文件夹，返回该子文件夹的 Uri；否则，返回 null。
     */
    @JvmStatic
    fun getSubdirectoryUri(
        context: Context,
        parentTreeUri: Uri,
        directoryName: String
    ): Uri? {
        // 使用 SAF 的 DocumentFile.fromTreeUri 方法获取表示目录的 DocumentFile 实例
        val dir = DocumentFile.fromTreeUri(context, parentTreeUri)

        // 检查目录是否存在并且是一个目录
        if (dir != null && dir.exists() && dir.isDirectory) {
            // 遍历目录下的所有子文件夹
            for (folder in dir.listFiles()) {
                // 检查子文件夹是否存在并且是一个目录，而不是文件
                if (folder != null && folder.exists() && folder.isDirectory) {
                    // 检查子文件夹的名称是否与指定的名称匹配
                    if (folder.name == directoryName) {
                        // 如果找到了匹配的子文件夹，返回该子文件夹的 Uri
                        return folder.uri
                    }
                }
            }
        }

        // 如果没有找到匹配的子文件夹，返回 null
        return null
    }

    /**
     * 在指定的目录下创建文件。
     *
     * @param context 应用程序上下文。用于获取 DocumentFile 实例。
     * @param parentTreeUri 父目录的 Uri。应该是已经通过 SAF 授权的目录的 Uri。
     * @param fileName 要创建的文件的名称。
     * @param mimeType 文件的 MIME 类型，例如 "text/plain" 或 "image/jpeg"。如果为 null，则默认创建二进制类型的文件。
     * @return 如果文件成功创建，返回表示该文件的 DocumentFile 实例；否则，返回 null。
     */
    @JvmStatic
    fun createFile(
        context: Context,
        parentTreeUri: Uri,
        fileName: String,
        mimeType: String? = "application/octet-stream"
    ): DocumentFile? {
        // 使用 SAF 的 DocumentFile.fromTreeUri 方法获取表示父目录的 DocumentFile 实例
        val parentDirectory = DocumentFile.fromTreeUri(context, parentTreeUri)

        // 使用 DocumentFile 的 createFile 方法创建文件
        // 如果文件成功创建，createFile 方法将返回一个新的 DocumentFile 实例，表示新创建的文件
        // 如果文件未能创建，createFile 方法将返回 null
        return parentDirectory?.createFile(mimeType!!, fileName)
    }

    /**
     * 获取指定目录下指定文件的 Uri。
     *
     * @param context 应用程序上下文。用于获取 ContentResolver 和 DocumentFile 实例。
     * @param parentTreeUri 要搜索的目录的 Uri。应该是已经通过 SAF 授权的目录的 Uri。
     * @param filename 要搜索的文件的名称。
     * @return 如果在指定目录下找到了指定的文件，返回该文件的 Uri；否则，返回 null。
     */
    @JvmStatic
    fun getFileUri(
        context: Context,
        parentTreeUri: Uri,
        filename: String
    ): Uri? {
        // 使用 SAF 的 DocumentFile.fromTreeUri 方法获取表示目录的 DocumentFile 实例
        val dir = DocumentFile.fromTreeUri(context, parentTreeUri)

        // 检查目录是否存在并且是一个目录
        if (dir != null && dir.exists() && dir.isDirectory) {
            // 遍历目录下的所有文件
            for (file in dir.listFiles()) {
                // 检查文件是否存在并且是一个文件，而不是目录
                if (file != null && file.exists() && !file.isDirectory) {
                    // 检查文件的名称是否与指定的文件名称匹配
                    if (file.name == filename) {
                        // 如果找到了匹配的文件，返回该文件的 Uri
                        return file.uri
                    }
                }
            }
        }

        // 如果没有找到匹配的文件，返回 null
        return null
    }

    /**
     * 通过指定文件打开一个输入流。
     *
     * @param documentFile 指定文件
     */
    @JvmStatic
    fun openInputStream(
        context: Context,
        documentFile: DocumentFile,
    ): InputStream? {
        return context.contentResolver.openInputStream(documentFile.uri)
    }

    /**
     * 通过指定文件的Uri打开一个输入流。
     *
     * @param context 应用程序上下文。用于获取 ContentResolver 和 DocumentFile 实例。
     * @param fileUri 要读取的文件的 Uri。应该是已经通过 SAF 授权的文件的 Uri。
     * @return 如果文件存在并且可以读取，返回表示文件内容的 InputStream 实例；否则，返回 null。
     * @throws FileNotFoundException 如果文件不存在或无法打开。
     * 注意：调用者应该在使用完 InputStream 后合理关闭它，以释放系统资源。
     */
    @JvmStatic
    fun openInputStream(
        context: Context,
        fileUri: Uri
    ): InputStream? {
        // 使用 SAF 的 DocumentFile.fromSingleUri 方法获取表示文件的 DocumentFile 实例
        val file = DocumentFile.fromSingleUri(context, fileUri)

        // 检查文件是否存在并且可以读取
        return if (file != null && file.exists() && file.canRead()) {
            // 使用 ContentResolver 的 openInputStream 方法打开文件并返回一个 InputStream 实例
            context.contentResolver.openInputStream(file.uri)
        } else {
            null
        }
    }

    /**
     * 通过指定目录的Uri获取某个文件，并返回该文件的输入流。
     *
     * @param context 应用程序上下文。用于获取 ContentResolver 实例。
     * @param parentTreeUri 要在其中创建或打开文件的目录的 Uri。应该是已经通过 SAF 授权的目录的 Uri。
     * @param filename 要创建或打开的文件的名称。
     * @return 如果能成功获取到 InputStream，返回该 InputStream；否则，返回 null。
     * 注意：调用者应该在使用完 InputStream 后合理关闭它，以释放系统资源。
     */
    @JvmStatic
    fun openInputStream(
        context: Context,
        parentTreeUri: Uri,
        filename: String,
    ): InputStream? {
        val uri = getFileUri(context, parentTreeUri, filename) ?: return null
        return openInputStream(context, uri)
    }

    /**
     * 通过指定文件打开一个输出流。
     *
     * @param documentFile 指定文件
     */
    @JvmStatic
    fun openOutputStream(
        context: Context,
        documentFile: DocumentFile,
        mode: String = "w",
    ): OutputStream? {
        return context.contentResolver.openOutputStream(documentFile.uri, mode)
    }

    /**
     * 通过指定文件的Uri打开一个输出流。
     *
     * @param context 应用程序上下文。用于获取 ContentResolver 实例。
     * @param fileUri 要获取 OutputStream 的文件的 Uri。应该是已经通过 SAF 授权的文件的 Uri。
     * @return 如果能成功获取到 OutputStream，返回该 OutputStream；否则，返回 null。
     * 注意：调用者应该在使用完 InputStream 后合理关闭它，以释放系统资源。
     */
    @JvmStatic
    fun openOutputStream(
        context: Context,
        fileUri: Uri,
        mode: String = "w",
    ): OutputStream? {
        // 使用 SAF 的 DocumentFile.fromSingleUri 方法获取表示文件的 DocumentFile 实例
        val file = DocumentFile.fromSingleUri(context, fileUri)

        // 检查文件是否存在并且可以写出
        return if (file != null && file.exists() && file.canWrite()) {
            // 使用 ContentResolver 的 openOutputStream 方法打开文件并返回一个 OutputStream 实例
            // 如果文件不存在或无法打开，openOutputStream 方法将返回 null
            context.contentResolver.openOutputStream(file.uri, mode)
        } else {
            null
        }
    }

    /**
     * 通过指定目录的Uri获取或创建某个文件，并返回该文件的输出流。
     *
     * @param context 应用程序上下文。用于获取 ContentResolver 实例。
     * @param parentTreeUri 要在其中创建或打开文件的目录的 Uri。应该是已经通过 SAF 授权的目录的 Uri。
     * @param filename 要打开的文件的名称。
     * @return 如果能成功获取到 OutputStream，返回该 OutputStream；否则，返回 null。
     * 注意：调用者应该在使用完 InputStream 后合理关闭它，以释放系统资源。
     */
    @JvmStatic
    fun openOutputStream(
        context: Context,
        parentTreeUri: Uri,
        filename: String,
        mode: String = "w",
        mimeType: String? = "application/octet-stream"
    ): OutputStream? {
        getFileUri(context, parentTreeUri, filename)?.let {
            return openOutputStream(context, it, mode)
        }
        createFile(context, parentTreeUri, filename, mimeType)?.let {
            return openOutputStream(context, it.uri, mode)
        }
        return null
    }
}