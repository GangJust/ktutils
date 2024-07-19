package com.freegang.ktutils.app

import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.ShortcutManager
import android.os.Build
import android.text.TextUtils
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import com.freegang.extension.appIconId
import com.freegang.extension.appLabelName

object KShortcutUtils {
    private const val TAG = "KShortcutUtils"

    private const val APP_SHORTCUT_INFO_ID: String = ".shortcutId"

    /**
     * 检查桌面上是否存在快捷方式
     *
     * @param context context
     * @param shortcutId 快捷方式ID
     */
    @RequiresApi(Build.VERSION_CODES.N_MR1)
    @JvmStatic
    @JvmOverloads
    fun checkHasShortcut(
        context: Context,
        shortcutId: String = APP_SHORTCUT_INFO_ID,
    ): Boolean {
        val manager = context.getSystemService(Context.SHORTCUT_SERVICE) as ShortcutManager
        val shortcuts = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            manager.getShortcuts(ShortcutManager.FLAG_MATCH_PINNED)
        } else {
            // manager.manifestShortcuts //FLAG_MATCH_MANIFEST
            // manager.dynamicShortcuts //FLAG_MATCH_DYNAMIC
            manager.pinnedShortcuts // FLAG_MATCH_PINNED
        }

        if (shortcuts.isNotEmpty()) {
            for (shortcut in shortcuts) {
                if (TextUtils.equals(shortcutId, shortcut.id)) {
                    return true
                }
            }
        }
        return false
    }

    /**
     * 创建快捷方式
     * label必须是唯一的，如果桌面存在相同label的icon则无法创建
     *
     * @param context context
     * @param className 类名
     * @param label 快捷方式名称
     * @param shortcutId 快捷方式ID
     */
    @RequiresApi(Build.VERSION_CODES.N_MR1)
    @JvmStatic
    @JvmOverloads
    fun createShortcut(
        context: Context,
        className: String,
        label: String,
        shortcutId: String = APP_SHORTCUT_INFO_ID,
    ) {
        if (TextUtils.isEmpty(className)) {
            Log.w(TAG, "createShortcut error: empty className")
            return
        }

        val application: Application = context.applicationContext as Application
        val intent = Intent()
        intent.setClassName(application.packageName, className)
        createShortcut(context, intent, label, shortcutId)
    }

    /**
     * 创建快捷方式
     * label必须是唯一的，如果桌面存在相同label的icon则无法创建
     *
     * @param context context
     * @param intent Intent
     * @param label 快捷方式名称
     * @param shortcutId 快捷方式ID
     */
    @RequiresApi(Build.VERSION_CODES.N_MR1)
    @JvmStatic
    @JvmOverloads
    fun createShortcut(
        context: Context,
        intent: Intent,
        label: String,
        shortcutId: String = APP_SHORTCUT_INFO_ID,
    ) {
        val shortcutSupported: Boolean = ShortcutManagerCompat.isRequestPinShortcutSupported(context)
        if (!shortcutSupported) {
            Log.w(TAG, "createShortcut error: no shortcutSupported")
            return
        }

        val broadcastIntent = Intent("${context.packageName}.SHORTCUT_ADDED")
        broadcastIntent.setPackage(context.packageName)

        try {
            intent.action = Intent.ACTION_VIEW
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra(ShortcutManagerCompat.EXTRA_SHORTCUT_ID, shortcutId)
            val shortcutInfo: ShortcutInfoCompat = newShortcutInfo(context, intent, label, shortcutId)
            val flag = PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            val pendingIntent: PendingIntent = PendingIntent.getBroadcast(context, 0, broadcastIntent, flag)
            // 返回值仅仅是调用成功，并不是快捷方式创建成功
            val result: Boolean = ShortcutManagerCompat.requestPinShortcut(context, shortcutInfo, pendingIntent.intentSender)
            Log.i(TAG, "requestPinShortcut result = $result, shortcutId: $shortcutId")
        } catch (e: Exception) {
            Log.e(TAG, "requestPinShortcut error: " + e.message)
        }
    }

    /**
     * 生成快捷方式信息
     */
    @Throws(Exception::class)
    private fun newShortcutInfo(
        context: Context,
        intent: Intent,
        label: String,
        shortcutId: String,
    ): ShortcutInfoCompat {
        var labelStr = label
        val application: Application = context.applicationContext as Application
        val builder: ShortcutInfoCompat.Builder = ShortcutInfoCompat.Builder(application, shortcutId)

        if (TextUtils.isEmpty(labelStr)) {
            labelStr = application.appLabelName
        }

        intent.component?.let {
            builder.setActivity(it) // 这个必须加防止有些机型快捷方式不显示
        }

        return builder
            .setIcon(IconCompat.createWithResource(application, application.appIconId)) // 快捷方式图标
            .setLongLabel(labelStr)
            .setShortLabel(labelStr)
            .setIntent(intent)
            .build()
    }
}