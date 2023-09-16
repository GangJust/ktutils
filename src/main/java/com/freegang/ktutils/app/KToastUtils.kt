package com.freegang.ktutils.app

import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.NinePatchDrawable
import android.os.CountDownTimer
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.StringRes

object KToastUtils {
    private var mToast: Toast? = null
    private var countDownTimer: CountDownTimer? = null

    @JvmStatic
    @JvmOverloads
    fun show(
        context: Context,
        message: String,
        duration: Long = 3000,
    ) {
        hide()
        mToast = Toast.makeText(context.applicationContext, null, Toast.LENGTH_SHORT)
        mToast?.setText(message)
        setToastTheme(context.applicationContext, mToast)
        showToastWithDuration(mToast, duration)
    }

    fun show(
        context: Context,
        @StringRes rsId: Int,
        duration: Long = 3000,
    ) {
        hide()
        mToast = Toast.makeText(context.applicationContext, null, Toast.LENGTH_SHORT)
        mToast?.setText(rsId)
        setToastTheme(context.applicationContext, mToast)
        showToastWithDuration(mToast, duration)
    }

    @JvmStatic
    @JvmOverloads
    fun show(
        context: Context,
        customView: View,
        duration: Long = 3000
    ) {
        hide()
        mToast = Toast.makeText(context.applicationContext, null, Toast.LENGTH_SHORT)
        mToast?.view = customView
        showToastWithDuration(mToast, duration)
    }

    private fun showToastWithDuration(toast: Toast?, duration: Long) {
        if (duration <= 0) {
            toast?.duration = Toast.LENGTH_SHORT
            toast?.show()
        } else if (duration == Toast.LENGTH_LONG.toLong()) {
            toast?.duration = Toast.LENGTH_LONG
            toast?.show()
        } else {
            countDownTimer = object : CountDownTimer(duration, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    toast?.duration = Toast.LENGTH_LONG
                    toast?.show()
                }

                override fun onFinish() {
                    cancel()
                    hide()
                }
            }
            countDownTimer?.start()
            toast?.duration = Toast.LENGTH_LONG
            toast?.show()
        }
    }

    private fun setToastTheme(context: Context, toast: Toast?) {
        runCatching {
            val modeNight = context.isDarkMode

            // 取消点击事件
            toast?.view?.isClickable = false
            toast?.view?.isLongClickable = false

            // 背景色
            val drawable = toast?.view?.background as NinePatchDrawable?
            drawable?.colorFilter = if (modeNight) {
                PorterDuffColorFilter(Color.parseColor("#FF161823"), PorterDuff.Mode.SRC_IN)
            } else {
                PorterDuffColorFilter(Color.parseColor("#FFFFFFFF"), PorterDuff.Mode.SRC_IN)
            }

            // 文字颜色
            val textView: TextView? = toast?.view?.findViewById(android.R.id.message)
            textView?.setTextColor(
                if (modeNight) {
                    Color.parseColor("#FFFFFFFF")
                } else {
                    Color.parseColor("#FF161823")
                }
            )
        }.onFailure {
            it.printStackTrace()
        }
    }

    @JvmStatic
    fun hide() {
        countDownTimer?.cancel()
        countDownTimer = null
        mToast?.cancel()
        mToast = null
    }

    @JvmStatic
    val isShowing get() = mToast != null
}
