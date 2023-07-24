package com.freegang.ktutils.io

import java.io.File

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
                it.deleteForcefully()
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
    fun secureFilename(
        filename: String,
        suffix: String = "",
    ): String {
        var actualSuffix = suffix
        if (actualSuffix.isBlank()) {
            val lastIndexOf = filename.lastIndexOf(".")
            val hasSuffix = (lastIndexOf != -1) && (lastIndexOf != 0) && (lastIndexOf != filename.length)
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
}

///
/**
 * 强制删除文件或目录。
 * 如果文件或目录不存在，则不进行任何操作。
 * 如果删除目录时发生异常，将会打印堆栈轨迹。
 */
fun File.deleteForcefully() = KFileUtils.deleteForcefully(this)

/**
 * 在当前文件路径下创建指定名称的子文件或子目录。
 * 如果当前文件是一个文件而不是目录，将抛出异常。
 *
 * @param name 子文件或子目录的名称
 * @return 创建的子文件或子目录
 * @throws Exception 如果当前文件是一个文件而不是目录
 */
fun File.child(name: String): File {
    if (this.isFile) {
        throw Exception("`File.child(\"$name\")` trying to add a child to a file.")
    }
    return File(this, name)
}

/**
 * 根据需要创建文件或目录。
 * 如果指定的文件不存在，将会创建它。
 * 如果指定的文件是一个目录，将会创建该目录及其父目录（如果不存在）。
 *
 * @param isFile 是否是文件。默认为 `false`，表示创建目录；如果为 `true`，表示创建文件。
 * @return 创建的文件或目录
 * @throws IOException 如果创建文件或目录时发生 I/O 异常
 */
fun File.need(isFile: Boolean = false): File {
    if (isFile) {
        val parent = this.parentFile!!
        if (!parent.exists()) parent.mkdirs()
        if (!this.exists()) this.createNewFile()
        return this
    }

    if (!this.exists()) this.mkdirs()
    return this
}

/**
 * 将字符串转换为文件对象, 请确保该字符串是一个正确的文件路径
 *
 * @return 字符串对应的文件对象
 */
fun String.toFile() = File(this)

/**
 * 获取文件的纯文件名，去除特殊字符。
 *
 * @return 纯文件名
 */
val File.pureName: String
    get() = KFileUtils.pureFileName(this.name)

/**
 * 获取字符串的纯文件名，去除特殊字符。
 *
 * @return 纯文件名
 */
val String.pureFileName: String
    get() = KFileUtils.pureFileName(this)

/**
 * 将字符串转换为安全的文件名，去除特殊字符，并限制总长度不超过255个字节。
 * @param suffix 指定后缀名, 默认空字符串则按一般文件的后缀分割符 `小数点(.)` 为后缀
 * @return 安全的文件名
 */
fun String.secureFilename(suffix: String = ""): String {
    return KFileUtils.secureFilename(this, suffix)
}


