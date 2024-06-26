package com.freegang.extension

import android.content.Context
import androidx.documentfile.provider.DocumentFile
import com.freegang.ktutils.media.KDocumentUtils
import java.io.InputStream
import java.io.OutputStream

/**
 * 创建多级目录，如果子目录已经存在，则直接返回该子目录。
 *
 * @param displayName 要创建的子目录的名称。
 */
fun DocumentFile.createDirectories(
    displayName: String,
): DocumentFile? {
    return KDocumentUtils.createDirectories(this, displayName)
}

/**
 * 直接打开输入流并返回。
 *
 * @param context 应用程序上下文。
 * @return 如果成功打开输入流，返回该输入流；否则，返回 null。
 * 注意：调用者应该在使用完 InputStream 后合理关闭它，以释放系统资源。
 */
fun DocumentFile.openInputStream(
    context: Context,
): InputStream? {
    return KDocumentUtils.openInputStream(context, this)
}

/**
 * 直接打开输出流并返回。
 *
 * @param context 应用程序上下文。
 * @param mode 打开模式，如 "w" 表示写入，可选值有 "wa" 追加写入，"rw" 读写，"rwt" 读写并清空。
 * @return 如果成功打开输出流，返回该输出流；否则，返回 null。
 * 注意：调用者应该在使用完 OutputStream 后合理关闭它，以释放系统资源。
 */
fun DocumentFile.openOutputStream(
    context: Context,
    mode: String = "w"
): OutputStream? {
    return KDocumentUtils.openOutputStream(context, this, mode)
}