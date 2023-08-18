package com.freegang.ktutils.app

import android.content.Context
import android.os.CountDownTimer
import android.view.View
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
        mToast?.xOffset
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
        } else if (duration == Toast.LENGTH_SHORT.toLong()) {
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
