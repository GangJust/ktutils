package com.freegang.ktutils.app

import android.Manifest
import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.pm.PackageManager.PackageInfoFlags
import android.content.res.Configuration
import android.os.Build
import android.os.Environment
import android.provider.Settings
import androidx.core.content.PermissionChecker
import com.freegang.ktutils.extension.asOrNull
import com.freegang.ktutils.reflect.fieldGetFirst
import com.freegang.ktutils.reflect.methodInvokeFirst
import java.io.File
import java.lang.reflect.InvocationTargetException
import java.security.MessageDigest
import kotlin.system.exitProcess


object KAppUtils {
    private var _application: Application? = null

    @JvmStatic
    fun setApplication(app: Application) {
        _application = app
    }

    /**
     * 获取 application 引用
     *
     * 应该首先进行 [setApplication]
     */
    @JvmStatic
    val getApplication: Application
        get() {
            if (_application != null) return _application!!
            return getRefApplication()
        }

    @SuppressLint("PrivateApi")
    private fun getRefApplication(): Application {
        try {
            val activityThread = Class.forName("android.app.ActivityThread")
            val thread = activityThread.getMethod("currentActivityThread").invoke(null)
            val app =
                activityThread.getMethod("getApplication").invoke(thread)
                    ?: throw NullPointerException("u should init first")
            return app as Application
        } catch (e: NoSuchMethodException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        } catch (e: InvocationTargetException) {
            e.printStackTrace()
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        }
        throw NullPointerException("should be initialized first, please refer to: #KAppUtils.setApplication()")
    }

    /**
     * 获取应用程序的名称。
     *
     * @param context 上下文对象，用于获取资源和包信息。
     * @param packageName 要获取应用名称的包名，默认为当前应用的包名。
     * @return 应用程序的名称。
     */
    @JvmStatic
    @JvmOverloads
    fun getAppLabelName(
        context: Context,
        packageName: String = context.packageName,
    ): String {
        val packageInfo = getPackageInfo(context, packageName)

        val labelRes = packageInfo?.applicationInfo?.labelRes ?: return ""

        return try {
            context.resources.getString(labelRes)
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }

    /**
     * 返回某个App的版本名
     *
     * @param context 上下文对象，用于获取资源和包信息。
     * @param packageName 要获取应用名称的包名，默认为当前应用的包名。
     * @return String
     */
    @JvmStatic
    @JvmOverloads
    fun getVersionName(
        context: Context,
        packageName: String = context.packageName,
    ): String {
        return getPackageInfo(context, packageName)?.versionName ?: ""
    }

    /**
     * 返回某个App的版本号
     *
     * @param context 上下文对象，用于获取资源和包信息。
     * @param packageName 包名(需要App已安装)
     * @return Long
     */
    @JvmStatic
    @JvmOverloads
    fun getVersionCode(
        context: Context,
        packageName: String = context.packageName,
    ): Long {
        val packageInfo = getPackageInfo(context, packageName)
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            packageInfo?.longVersionCode ?: 0
        } else {
            packageInfo?.versionCode?.toLong() ?: 0
        }
    }

    /**
     * 返回某个App的基本信息
     *
     * @param context 上下文对象，用于获取资源和包信息。
     * @param packageName 包名(需要App已安装)
     * @param flags 标志，见: PackageManager#flag
     * @return PackageInfo
     */
    @JvmStatic
    @JvmOverloads
    fun getPackageInfo(
        context: Context,
        packageName: String = context.packageName,
        flags: Int = PackageManager.GET_ACTIVITIES,
    ): PackageInfo? {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                context.packageManager.getPackageInfo(
                    packageName,
                    PackageInfoFlags.of(flags.toLong())
                )
            } else {
                context.packageManager.getPackageInfo(packageName, flags)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * 返回某个Apk的版本名
     *
     * @param context 上下文对象，用于获取资源和包信息。
     * @param apkFile apk文件路径
     * @return String
     */
    @JvmStatic
    fun getApkVersionName(
        context: Context,
        apkFile: File,
    ): String {
        return getApkPackageInfo(context, apkFile)?.versionName ?: ""
    }

    /**
     * 返回某个Apk的版本号
     *
     * @param context 上下文对象，用于获取资源和包信息。
     * @param apkFile apk文件路径
     * @return Long
     */
    @JvmStatic
    fun getApkVersionCode(
        context: Context,
        apkFile: File,
    ): Long {
        val packageInfo = getApkPackageInfo(context, apkFile)
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            packageInfo?.longVersionCode ?: 0
        } else {
            packageInfo?.versionCode?.toLong() ?: 0
        }
    }

    /**
     * 返回某个Apk的基本信息
     *
     * @param context 上下文对象，用于获取资源和包信息。
     * @param apkFile apk文件路径
     * @return PackageInfo
     */
    @JvmStatic
    fun getApkPackageInfo(
        context: Context,
        apkFile: File,
    ): PackageInfo? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val flags = PackageInfoFlags.of(PackageManager.GET_ACTIVITIES.toLong())
            context.packageManager.getPackageArchiveInfo(apkFile.absolutePath, flags)
        } else {
            context.packageManager.getPackageArchiveInfo(
                apkFile.absolutePath,
                PackageManager.GET_ACTIVITIES
            )
        }
    }

    /**
     * 返回指定包名的签名信息
     *
     * @param context 上下文对象，用于获取资源和包信息。
     * @param packageName 包名
     */
    @JvmStatic
    fun getSignature(
        context: Context,
        packageName: String = context.packageName,
    ): ByteArray {
        return try {
            val pm = context.packageManager
            val packageInfo = pm.getPackageInfo(packageName, PackageManager.GET_SIGNATURES)
            val signatures = packageInfo.signatures
            val signature = signatures[0]
            signature.toByteArray()
        } catch (e: Exception) {
            e.printStackTrace()
            return ByteArray(0)
        }
    }

    /**
     * 返回指定包名的 MD5 值
     *
     * @param context 上下文对象，用于获取资源和包信息。
     * @param packageName 包名
     */
    @JvmStatic
    @JvmOverloads
    fun getMD5(
        context: Context,
        packageName: String = context.packageName,
    ): String {
        return getDigest(getSignature(context, packageName), "MD5")
    }

    /**
     * 返回指定包名的 SHA1 值
     *
     * @param context 上下文对象，用于获取资源和包信息。
     * @param packageName 包名
     */
    @JvmStatic
    @JvmOverloads
    fun getSHA1(
        context: Context,
        packageName: String = context.packageName,
    ): String {
        return getDigest(getSignature(context, packageName), "SHA1")
    }

    /**
     * 返回指定包名的 SHA256 值
     *
     * @param context 上下文对象，用于获取资源和包信息。
     * @param packageName 包名
     */
    @JvmStatic
    @JvmOverloads
    fun getSHA256(
        context: Context,
        packageName: String = context.packageName,
    ): String {
        return getDigest(getSignature(context, packageName), "SHA256")
    }

    private fun getDigest(
        signatures: ByteArray,
        algorithm: String,
    ): String {
        if (signatures.isEmpty()) return "";
        return try {
            val md = MessageDigest.getInstance(algorithm)
            md.update(signatures)
            val digest = md.digest()
            digest.joinToString(":") { String.format("%02x", it) }
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }

    /**
     * 判断某个App是否Debug状态
     * @param context 上下文对象，用于获取资源和包信息。
     * @param packageName 目标包名
     */
    @JvmStatic
    @JvmOverloads
    fun isAppInDebug(
        context: Context,
        packageName: String = context.packageName,
    ): Boolean {
        return try {
            val packageInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                context.packageManager.getPackageInfo(
                    packageName,
                    PackageInfoFlags.of(PackageManager.GET_ACTIVITIES.toLong())
                )
            } else {
                context.packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)
            }
            val applicationInfo = packageInfo.applicationInfo
            applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            false
        }
    }

    /**
     * 判断某个App是否安装
     * @param context 上下文对象，用于获取资源和包信息。
     * @param packageName 目标包名
     */
    @JvmStatic
    @JvmOverloads
    fun isAppInstalled(
        context: Context,
        packageName: String = context.packageName,
    ): Boolean {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                context.packageManager.getPackageInfo(
                    packageName,
                    PackageInfoFlags.of(PackageManager.GET_ACTIVITIES.toLong())
                )
            } else {
                context.packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)
            }
            true
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            false
        }
    }

    /**
     * 判断是否具有某个权限
     * @param application Application
     * @param permission 权限名
     */
    @JvmStatic
    fun checkPermission(
        application: Application,
        permission: String,
    ): Boolean {
        // 管理外部存储
        if (permission == Manifest.permission.MANAGE_EXTERNAL_STORAGE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                return Environment.isExternalStorageManager()
            }
            return true
        }

        // 悬浮窗
        if (permission == Manifest.permission.SYSTEM_ALERT_WINDOW) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return Settings.canDrawOverlays(application)
            }
            return true
        }

        // 修改系统设置
        if (permission == Manifest.permission.WRITE_SETTINGS) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return Settings.System.canWrite(application)
            }
            return true
        }

        return PermissionChecker.checkSelfPermission(
            application,
            permission
        ) == PermissionChecker.PERMISSION_GRANTED
    }

    /**
     * 判断某个App是否是深色模式
     * @param context 上下文对象
     */
    fun isDarkMode(context: Context): Boolean {
        val currentNightMode: Int =
            context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return currentNightMode == Configuration.UI_MODE_NIGHT_YES
    }

    /**
     * 重新启动App
     * @param context 上下文对象，用于获取资源和包信息。
     */
    @SuppressLint("WrongConstant")
    fun restartApplication(context: Context) {
        val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            (PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        )
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager?
        alarmManager?.set(AlarmManager.RTC, System.currentTimeMillis() + 500, pendingIntent)
        exitProcess(0)
    }

    /**
     * 杀死指定包名的应用进程。
     *
     * @param context 上下文对象，通常是Activity或Application的实例。
     * @param packageName 要杀死进程的应用的包名。
     *
     * 注意：
     * 1. 这个方法需要 "android.permission.KILL_BACKGROUND_PROCESSES" 权限。
     * 2. 只能杀死后台进程，如果应用正在前台运行，这个方法可能无效。
     * 3. 即使杀死了应用的进程，如果系统需要，可能会自动重启应用的进程。
     * 4. 从Android 9开始，获取其他应用进程信息的能力被限制，可能无法杀死其他应用的后台进程。
     */
    @JvmStatic
    fun killAppProcess(context: Context, packageName: String) {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val runningAppProcesses = activityManager.runningAppProcesses ?: return

        for (appProcess in runningAppProcesses) {
            if (appProcess.processName == packageName) {
                android.os.Process.killProcess(appProcess.pid)
                return
            }
        }
    }

    ///
    /**
     * 返回当前app的abi架构
     *
     * @param context 上下文对象，用于获取资源和包信息。
     */
    @JvmStatic
    fun getAbiBit(context: Context): String {
        val nativeLibraryDir = context.applicationInfo.nativeLibraryDir
        val nextIndexOfLastSlash: Int = nativeLibraryDir.lastIndexOf('/') + 1
        return nativeLibraryDir.substring(nextIndexOfLastSlash)
    }

    /**
     * 获取当前dalvik指令集，结果应该与 [getAbiBit] 相同，但不保证
     */
    @JvmStatic
    fun getDalvikInstructionSet(): String {
        try {
            val forName = Class.forName("dalvik.system.VMRuntime")
            val runtime = forName.methodInvokeFirst(name = "getRuntime")
            return runtime?.methodInvokeFirst("vmInstructionSet")?.asOrNull<String>() ?: "unknown"
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return "unknown"
    }

    /**
     * 判断当前dalvik虚拟机是否64位
     */
    @JvmStatic
    fun is64BitDalvik(): Boolean {
        try {
            val forName = Class.forName("dalvik.system.VMRuntime")
            val runtime = forName.methodInvokeFirst(name = "getRuntime")
            return runtime?.methodInvokeFirst("is64Bit")?.asOrNull<Boolean>() ?: false
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    /**
     * 获取当前设备安全补丁级别
     *
     * 如: 2023-04-01
     *
     * SECURITY_PATCH 字段在 Android 6.0 (23) 设备上是存在的，可以直接通过静态常量获取，这里采用反射(低于23反射也无法获取，因为没有该字段)
     *
     * ```
     * // Api23以上可以直接获取
     * val securityPatch Build.VERSION.SECURITY_PATCH
     * ```
     */
    @JvmStatic
    fun getSecurityPatchLevel(): String {
        return Build.VERSION::class.java.fieldGetFirst(name = "SECURITY_PATCH")?.asOrNull<String>()
            ?: "unknown"
    }


    /**
     * 获取状态栏高度, 旧方式
     */
    @SuppressLint("DiscouragedApi", "InternalInsetResource")
    fun getStatusBarHeight(context: Context): Int {
        val resourceId = context.resources.getIdentifier(
            "status_bar_height", "dimen", "android",
        )
        return if (resourceId > 0) {
            context.resources.getDimensionPixelSize(resourceId)
        } else {
            0
        }
    }


    /**
     * 获取底部导航栏高度, 旧方式
     */
    @SuppressLint("InternalInsetResource", "DiscouragedApi")
    @JvmStatic
    fun getNavigationBarHeight(context: Context): Int {
        val resourceId = context.resources.getIdentifier(
            "navigation_bar_height", "dimen", "android",
        )
        if (resourceId > 0) {
            return context.resources.getDimensionPixelSize(resourceId)
        }
        return 0
    }

    /**
     * 判断当前底部导航栏类型, 旧方式
     *
     * 0 : Navigation is displayed with 3 buttons
     * 1 : Navigation is displayed with 2 button(Android P navigation mode)
     * 2 : 全屏手势(Android Q上的手势)
     */
    @SuppressLint("InternalInsetResource", "DiscouragedApi")
    @JvmStatic
    fun getNavBarInteractionMode(context: Context): Int {
        val resources = context.resources
        val resourceId = resources.getIdentifier(
            "config_navBarInteractionMode", "integer", "android",
        )
        return if (resourceId > 0) {
            resources.getInteger(resourceId)
        } else 0
    }
}

///
val Any.is64BitDalvik get() = KAppUtils.is64BitDalvik()

val Any.dalvikInstructionSet get() = KAppUtils.getDalvikInstructionSet()

val Any.securityPatchLevel get() = KAppUtils.getSecurityPatchLevel()

val Context.abiBit get() = KAppUtils.getAbiBit(this)

val Context.appLabelName get() = KAppUtils.getAppLabelName(this)

val Context.appVersionName get() = KAppUtils.getVersionName(this)

val Context.appVersionCode get() = KAppUtils.getVersionCode(this)

val Context.isDarkMode get() = KAppUtils.isDarkMode(this)

val Context.statusBarHeight get() = KAppUtils.getStatusBarHeight(this)

val Context.navigationBarHeight get() = KAppUtils.getNavigationBarHeight(this)

val Context.navBarInteractionMode get() = KAppUtils.getNavBarInteractionMode(this)