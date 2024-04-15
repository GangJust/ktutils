package com.freegang.ktutils.io

import java.io.File
import java.io.InputStream
import java.nio.charset.Charset

object KFileUtils {
    /**
     * 强制删除文件或目录，即使文件或目录不存在也不会抛出异常。
     *
     * @param file 要删除的文件或目录
     */
    @JvmStatic
    fun deleteForcefully(file: File): Boolean {
        if (!file.exists()) {
            return false
        }

        try {
            // 文件直接删除
            if (file.isFile) {
                return file.delete()
            }

            // 遍历删除文件夹
            file.listFiles()?.forEach {
                deleteForcefully(it)
            }

            // 删除文件夹本身
            file.delete()
        } catch (e: SecurityException) {
            // 处理没有权限删除的情况
            e.printStackTrace()
            return false
        }

        return true
    }

    /**
     * 递归删除文件或目录。
     *
     * @param file 要删除的文件或目录
     */
    @JvmStatic
    fun deleteRecursively(file: File): Boolean {
        return file.deleteRecursively()
    }

    /**
     * 清理文件名中的特殊字符，返回纯净的文件名。
     *
     * @param filename 原始文件名
     * @return 清理后的文件名
     */
    @JvmStatic
    fun pureFileName(filename: String): String {
        val replaceMap = mapOf(
            " " to "",
            "<" to "‹",
            ">" to "›",
            "\"" to "”",
            "\'" to "’",
            "\\" to "-",
            "$" to "¥",
            "/" to "-",
            "|" to "-",
            "*" to "-",
            ":" to "-",
            "?" to "？"
        )
        var pureName = filename.replace("\\s".toRegex(), "")
        for (entry in replaceMap) pureName = pureName.replace(entry.key, entry.value)
        return pureName
    }

    /**
     * 将文件名转换为安全的文件名，确保文件名不超过255个字节。
     *
     * @param filename 原始文件名
     * @param suffix 指定后缀名, 默认空字符串则按一般文件的后缀分割符 `小数点(.)` 为后缀
     * @return 安全的文件名, 如果处理失败, 则返回原文件名
     */
    @JvmStatic
    @JvmOverloads
    fun secureFilename(filename: String, suffix: String = ""): String {
        var actualSuffix = suffix
        if (actualSuffix.isBlank()) {
            val lastIndexOf = filename.lastIndexOf(".")
            val hasSuffix =
                (lastIndexOf != -1) && (lastIndexOf != 0) && (lastIndexOf != filename.length)
            actualSuffix = if (hasSuffix) filename.substring(lastIndexOf) else ""
        }

        val maxLength = 255
        val actualSuffixByteSize = actualSuffix.toByteArray().size
        val stringList = filename.removeSuffix(actualSuffix).split("")
        var countLength = 0
        var secureFilename = ""
        for (i in stringList.indices) {
            val item = stringList[i]
            if (item.isEmpty()) continue
            countLength += item.toByteArray().size
            if (countLength + actualSuffixByteSize >= maxLength) break
            secureFilename = secureFilename.plus(item)
        }

        return try {
            secureFilename.plus(actualSuffix)
        } catch (e: Exception) {
            return filename
        }
    }

    /**
     * 将指定文本写入对某个文件。
     *
     * @param file 被操作的文件
     * @param text 被写入的文本内容
     * @param charset 编码方式
     */
    @JvmStatic
    @JvmOverloads
    fun writeText(file: File, text: String, charset: Charset = Charsets.UTF_8) {
        file.writeText(text, charset)
    }

    /**
     * 将指定字节数组写入某个文件。
     *
     * @param file 被操作的文件
     * @param array 被写入的字节数组
     */
    @JvmStatic
    fun writeBytes(file: File, array: ByteArray) {
        file.writeBytes(array)
    }

    /**
     * 将指定文件读入字节数组。
     *
     * @param file 被操作的文件
     */
    @JvmStatic
    fun readBytes(file: File): ByteArray {
        return file.readBytes()
    }

    /**
     * 将指定输入流读入字节数组。
     *
     * @param stream 被操作输入流
     */
    @JvmStatic
    fun readBytes(stream: InputStream): ByteArray {
        return stream.readBytes()
    }

    /**
     * 将指定文本内容追加到某个文件末尾。
     *
     * @param file 被操作的文件
     * @param text 被追加的文本内容
     * @param charset 编码方式
     */
    fun appendText(file: File, text: String, charset: Charset = Charsets.UTF_8) {
        file.appendText(text, charset)
    }

    /**
     * 将指定字节数组追加到某个文件末尾。
     *
     * @param file 被操作的文件
     * @param array 被追加的字节数组
     */
    fun appendBytes(file: File, array: ByteArray) {
        file.appendBytes(array)
    }

    /**
     * 获取某个文件的后缀名。
     *
     * @param file 被操作的文件
     * @return 后缀名, 如果是文件夹则为null
     */
    @JvmStatic
    fun getSuffix(file: File): String? {
        return if (file.isDirectory) {
            null
        } else {
            file.extension
        }
    }

    /**
     * 获取某个文件名的后缀名。
     *
     * @param filename 被操作的文件
     * @return 后缀名, 如果没有则返回空字符串
     */
    @JvmStatic
    fun getSuffix(filename: String): String {
        return filename.substringAfterLast(".", "")
    }
}