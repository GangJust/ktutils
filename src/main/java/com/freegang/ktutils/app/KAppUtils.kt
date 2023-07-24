package com.freegang.ktutils.app

import android.Manifest
import android.annotation.SuppressLint
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
                activityThread.getMethod("getApplication").invoke(thread) ?: throw NullPointerException("u should init first")
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
                context.packageManager.getPackageInfo(packageName, PackageInfoFlags.of(flags.toLong()))
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
            context.packageManager.getPackageArchiveInfo(apkFile.absolutePath, PackageManager.GET_ACTIVITIES)
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
                context.packageManager.getPackageInfo(packageName, PackageInfoFlags.of(PackageManager.GET_ACTIVITIES.toLong()))
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
                context.packageManager.getPackageInfo(packageName, PackageInfoFlags.of(PackageManager.GET_ACTIVITIES.toLong()))
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

        //悬浮窗
        if (permission == Manifest.permission.SYSTEM_ALERT_WINDOW) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return Settings.canDrawOverlays(application)
            }
            return true
        }

        //修改系统设置
        if (permission == Manifest.permission.WRITE_SETTINGS) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return Settings.System.canWrite(application)
            }
            return true
        }

        return PermissionChecker.checkSelfPermission(application, permission) == PermissionChecker.PERMISSION_GRANTED
    }

    /**
     * 判断某个App是否是深色模式
     * @param context 上下文对象
     */
    fun isDarkMode(context: Context): Boolean {
        val currentNightMode: Int = context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
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
}

///
val Context.appLabelName get() = KAppUtils.getAppLabelName(this)

val Context.appVersionName get() = KAppUtils.getVersionName(this)

val Context.appVersionCode get() = KAppUtils.getVersionCode(this)

val Context.isDarkMode get() = KAppUtils.isDarkMode(this)