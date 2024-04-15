package com.freegang.extension

import com.freegang.ktutils.io.KFileUtils
import java.io.File
import java.io.IOException

/**
 * 强制删除文件或目录。
 * 如果文件或目录不存在，则不进行任何操作。
 * 如果删除目录时发生异常，将会打印堆栈轨迹。
 */
fun File.deleteForcefully(): Boolean {
    return KFileUtils.deleteForcefully(this)
}

/**
 * 获取当前路径下指定名称的子文件或子目录。
 * 如需要创建子文件或子目录, 请结合[need]使用
 *
 * @param name 子文件或子目录的名称
 * @return 当前路径下的子文件或子目录
 */
fun File.child(name: String): File {
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
@Throws(IOException::class)
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
fun String.toFile(): File {
    return File(this)
}

/**
 * 获取字符串以以小数点结尾的后缀
 *
 * @return 后缀子串, 如果没有则返回空字符串
 */
val String.suffix: String
    get() = KFileUtils.getSuffix(this)

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
 *
 * @param suffix 指定后缀名, 默认空字符串则按一般文件的后缀分割符 `小数点(.)` 为后缀
 * @return 安全的文件名
 */
fun String.secureFilename(suffix: String = ""): String {
    return KFileUtils.secureFilename(this, suffix)
}
