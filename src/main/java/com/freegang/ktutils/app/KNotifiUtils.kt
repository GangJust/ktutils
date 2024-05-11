package com.freegang.ktutils.app

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat

interface IProgressNotification {
    fun setFinishedText(finishedText: String)

    fun setFailedText(failedText: String)

    fun notifyProgress(step: Int, inProgressText: String)
}

object KNotifiUtils {

    /**
     * 显示普通消息通知
     */
    @SuppressLint("WrongConstant")
    @JvmStatic
    @JvmOverloads
    fun showNotification(
        context: Context,
        notifyId: Int,
        channelId: String = "渠道ID",
        channelName: String = "渠道名",
        title: String,
        text: String,
        subText: String? = null,
        intent: PendingIntent? = null,
        ongoing: Boolean = false,
        actions: Array<NotificationCompat.Action> = emptyArray()
    ) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_DEFAULT, // 默认, 酌情修改
            )
            manager.createNotificationChannel(notificationChannel)
        }

        val notify = NotificationCompat.Builder(context, channelId).apply {
            setOngoing(ongoing)
            setAutoCancel(!ongoing) // 自动取消
            setSmallIcon(context.applicationInfo.icon)
            setContentTitle(title)
            setContentText(text)
            subText?.let { setSubText(it) }
            intent?.let { setContentIntent(it) }
            for (action in actions) {
                addAction(action)
            }
        }

        manager.notify(notifyId, notify.build())
    }

    /**
     * 显示自定义布局通知
     */
    @SuppressLint("WrongConstant")
    @JvmStatic
    @JvmOverloads
    fun showCustomNotification(
        context: Context,
        notifyId: Int,
        channelId: String = "渠道ID",
        channelName: String = "渠道名",
        intent: PendingIntent? = null,
        ongoing: Boolean = false,
        contentView: RemoteViews? = null,
        bigContentView: RemoteViews? = null,
        headsUpContentView: RemoteViews? = null,
    ) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_DEFAULT, // 默认, 酌情修改
            )
            manager.createNotificationChannel(notificationChannel)
        }

        val notify = NotificationCompat.Builder(context, channelId).apply {
            setOngoing(ongoing)
            setAutoCancel(!ongoing) // 自动取消
            setSmallIcon(context.applicationInfo.icon)
            intent?.let { setContentIntent(it) }
            setStyle(NotificationCompat.DecoratedCustomViewStyle())
            contentView?.let { setCustomContentView(it) }
            bigContentView?.let { setCustomBigContentView(it) }
            headsUpContentView?.let { setCustomHeadsUpContentView(it) }
        }

        manager.notify(notifyId, notify.build())
    }

    /**
     * 取消某个通知
     */
    @JvmStatic
    fun cancelNotification(
        context: Context,
        notifyId: Int,
    ) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.cancel(notifyId)
    }

    /**
     * 取消所有通知
     */
    @JvmStatic
    fun cancelAllNotification(
        context: Context,
    ) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.cancelAll()
    }

    /**
     * 显示进度条通知
     */
    @SuppressLint("WrongConstant")
    @JvmStatic
    @JvmOverloads
    fun showProgressNotification(
        context: Context,
        notifyId: Int,
        channelId: String = "渠道ID",
        channelName: String = "渠道名",
        title: String = "正在下载..",
        inProgressText: String = "下载中%s%%",
        failedText: String = "下载失败!",
        finishedText: String = "下载完成!",
        intent: PendingIntent? = null,
        listener: ProgressNotificationListener,
    ) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_DEFAULT // 默认, 酌情修改
            )
            manager.createNotificationChannel(notificationChannel)
        }

        // 构建通知
        val notify = NotificationCompat.Builder(context, channelId).apply {
            setAutoCancel(true) // 自动取消
            setSmallIcon(context.applicationInfo.icon)
            setContentTitle(title)
            setContentText(inProgressText.format(0))
            setProgress(100, 0, false)
            // notify.setProgress(0, 0, true) //不确定状态
            intent?.let { setContentIntent(it) }
        }

        // 回调,由调用者设置进度
        listener.on(ProgressNotification(notifyId, inProgressText, failedText, finishedText, manager, notify))
    }


    // 控制类
    class ProgressNotification(
        private val notifyId: Int = 1,
        private var inProgressText: String,
        private var finishedText: String,
        private var failedText: String,
        private val manager: NotificationManager,
        private val notify: NotificationCompat.Builder,
    ) : IProgressNotification {
        /**
         * 由调用者主动设置完成文本
         * @param finishedText 下载完成后展示的文本, 默认为: "下载完成!"
         */
        override fun setFinishedText(
            finishedText: String,
        ) {
            this.finishedText = finishedText
            notify.setProgress(100, 100, false)
            notify.setContentText(this.finishedText)
            manager.notify(notifyId, notify.build())
        }

        /**
         * 由调用者主动设置完成文本
         * @param failedText 下载完成后展示的文本, 默认为: "下载失败!"
         */
        override fun setFailedText(failedText: String) {
            this.failedText = failedText
            notify.setProgress(0, 0, true) // 不确定状态
            notify.setContentText(this.failedText)
            manager.notify(notifyId, notify.build())
        }

        /**
         * 调用者设置进度, 应该由它实时更新
         * @param step 当前进度
         * @param inProgressText 进度文本, 应该预留一个`%s`或者`%d`作为[step]的展示, 默认为: "下载中%s%%"
         */
        override fun notifyProgress(
            step: Int,
            inProgressText: String,
        ) {
            this.inProgressText = inProgressText
            notify.setContentText(this.inProgressText.format(step))
            notify.setProgress(100, step, false)
            manager.notify(notifyId, notify.build())
        }
    }

    fun interface ProgressNotificationListener {
        fun on(notify: ProgressNotification)
    }
}