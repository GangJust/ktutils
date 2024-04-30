package com.freegang.ktutils.app

import android.app.Application
import android.content.Intent
import android.os.Build
import android.os.Looper
import android.os.Process
import android.widget.Toast
import com.freegang.extension.abiBit
import com.freegang.extension.activeActivity
import com.freegang.extension.appLabelName
import com.freegang.extension.appVersionCode
import com.freegang.extension.appVersionName
import com.freegang.extension.dalvikInstructionSet
import com.freegang.extension.is64BitDalvik
import com.freegang.extension.securityPatchLevel
import com.freegang.ktutils.log.KLogCat
import kotlin.system.exitProcess

/// 全局未捕获异常工具
/// 请在 Application.onCrate() 中初始化
class KAppCrashUtils : Thread.UncaughtExceptionHandler {
    private var mDefaultHandler: Thread.UncaughtExceptionHandler? = null

    private var mApp: Application? = null
    private var mIntent: Intent? = null
    private var mMessage: String? = null
    private var mCallback: CrashCallback? = null
    private var mCrashMessage: CrashMessage = CrashMessage()

    companion object {
        private val instance: KAppCrashUtils = KAppCrashUtils()

        const val CRASH_MESSAGE = "CRASH_MESSAGE"

        @JvmStatic
        @JvmOverloads
        fun init(
            app: Application,
            message: String? = null,
            intent: Intent? = null,
            callback: CrashCallback? = null,
        ) {
            instance.mApp = app
            instance.mIntent = intent
            instance.mMessage = message
            instance.mCallback = callback

            // 获取系统默认的UncaughtException处理器
            instance.mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler()
            // 设置KAppCrashUtils为程序的默认处理器
            Thread.setDefaultUncaughtExceptionHandler(instance)
        }
    }

    /// 全局异常处理方法
    override fun uncaughtException(t: Thread, e: Throwable) {
        if (!handlerException(e) && mDefaultHandler != null) {
            mDefaultHandler?.uncaughtException(t, e)
        } else {
            exitAppOrStartErrActivity(e)
        }
    }

    /// 异常处理
    private fun handlerException(e: Throwable): Boolean {
        Thread {
            Looper.prepare()
            Toast.makeText(mApp, mMessage, Toast.LENGTH_SHORT).show()
            Looper.loop()
        }.start()
        return mCallback?.callback(e, mCrashMessage) ?: true
    }

    /// 结束应用
    private fun exitAppOrStartErrActivity(e: Throwable) {
        val crashMessage = buildCrashMessage(e)
        KLogCat.e(crashMessage)

        // 传递错误信息
        if (mIntent?.component != null) {
            mIntent?.putExtra(CRASH_MESSAGE, crashMessage)
            mIntent?.flags =
                Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            mApp?.startActivity(mIntent)
            Process.killProcess(Process.myPid())
            exitProcess(1)
        } else {
            // 等待3秒toast显示
            try {
                Thread.sleep(3000)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            mApp?.activeActivity?.finishAffinity()
            Process.killProcess(Process.myPid())
            exitProcess(1)
        }
    }

    private fun buildCrashMessage(e: Throwable): String {
        // 构建错误信息
        val defaultCrashMessage = "错误信息: ${e.message}\n" +
                "出现时间: ${KLogCat.dateTimeFormat.format(System.currentTimeMillis())}\n" +
                "设备信息: ${Build.MANUFACTURER} ${Build.MODEL}\n" +
                "系统版本: Android ${Build.VERSION.RELEASE} (${Build.VERSION.SDK_INT})\n" +
                "应用版本: ${mApp!!.appLabelName} ${mApp!!.appVersionName} (${mApp!!.appVersionCode})\n" +
                "应用架构: ${mApp!!.abiBit}\n" +
                "Google安全补丁级别: ${mApp!!.securityPatchLevel}\n" +
                "Dalvik虚拟机: instructionSet=${mApp!!.dalvikInstructionSet}; is64Bit=${mApp!!.is64BitDalvik}\n" +
                "堆栈信息: ${e.stackTraceToString()}"

        return mCrashMessage.crashMessage ?: defaultCrashMessage
    }

    fun interface CrashCallback {
        fun callback(e: Throwable, msg: CrashMessage): Boolean
    }

    class CrashMessage {
        private var mCrashMessage: String? = null

        val crashMessage get() = mCrashMessage

        fun setCrashMessage(message: String) {
            this.mCrashMessage = message
        }
    }
}