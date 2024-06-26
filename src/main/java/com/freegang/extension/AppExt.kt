package com.freegang.extension

import android.content.Context
import android.net.Uri
import com.freegang.ktutils.app.KAppUtils
import com.freegang.ktutils.media.KMediaUtils
import java.io.File

/**
 * 判断当前dalvik虚拟机是否64位
 */
val Any.is64BitDalvik
    get() = KAppUtils.is64BitDalvik()

/**
 * 获取当前dalvik虚拟机指令集
 */
val Any.dalvikInstructionSet
    get() = KAppUtils.getDalvikInstructionSet()

/**
 * 获取当前设备安全补丁级别
 */
val Any.securityPatchLevel
    get() = KAppUtils.getSecurityPatchLevel()

/**
 * 返回当前app的abi架构
 */
val Context.abiBit
    get() = KAppUtils.getAbiBit(this)

/**
 * 获取当前app icon id
 */
val Context.appIconId
    get() = KAppUtils.getAppIconId(this)

/**
 * 获取当前app logo id
 */
val Context.appLogoId
    get() = KAppUtils.getAppLogoId(this)

/**
 * 获取当前app名称
 */
val Context.appLabelName
    get() = KAppUtils.getAppLabelName(this)

/**
 * 获取当前app版本名称
 */
val Context.appVersionName
    get() = KAppUtils.getVersionName(this)

/**
 * 获取当前app版本号
 */
val Context.appVersionCode
    get() = KAppUtils.getVersionCode(this)

/**
 * 获取当前app首次安装时间
 */
val Context.appFirstInstallTime
    get() = KAppUtils.getFirstInstallTime(this)

/**
 * 获取当前app最后一次更新时间
 */
val Context.appLastUpdateTime
    get() = KAppUtils.getLastUpdateTime(this)

/**
 * 判断当前是否处于深色模式
 */
val Context.isDarkMode
    get() = KAppUtils.isDarkMode(this)

/**
 * 获取当前设备状态栏高度
 */
val Context.statusBarHeight
    get() = KAppUtils.getStatusBarHeight(this)

/**
 * 获取当前设备底部导航栏高度
 */
val Context.navigationBarHeight
    get() = KAppUtils.getNavigationBarHeight(this)

/**
 * 获取当前设备底部导航栏交互模式
 */
val Context.navBarInteractionMode
    get() = KAppUtils.getNavBarInteractionMode(this)

/**
 * 获取当前app版本名称和版本号
 *
 * @param spacer 版本名称和版本号之间的分隔符
 * @param reverse 是否反转版本名称和版本号的位置
 */
fun Context.appVersion(
    spacer: String = "-",
    reverse: Boolean = false,
): String {
    return if (reverse) {
        "${KAppUtils.getVersionCode(this)}${spacer}${KAppUtils.getVersionName(this)}"
    } else {
        "${KAppUtils.getVersionName(this)}${spacer}${KAppUtils.getVersionCode(this)}"
    }
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
    authority: String = "${applicationContext.packageName}.fileprovider",
): Uri? {
    return KMediaUtils.getFileProviderUri(this, file, authority)
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