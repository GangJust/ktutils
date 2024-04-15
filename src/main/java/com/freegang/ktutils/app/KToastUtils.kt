package com.freegang.ktutils.app

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RoundRectShape
import android.os.CountDownTimer
import android.util.TypedValue
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.StringRes
import com.freegang.extension.dip2px
import com.freegang.extension.isDarkMode

object KToastUtils {
    private var mToast: Toast? = null
    private var countDownTimer: CountDownTimer? = null

    @JvmStatic
    @JvmOverloads
    fun showOriginal(
        context: Context,
        message: String,
        duration: Int = Toast.LENGTH_SHORT,
    ) {
        Toast.makeText(context, message, duration).show()
    }

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
        setToastTheme(context, mToast, message)
        showToastWithDuration(mToast, duration)
    }

    @JvmStatic
    @JvmOverloads
    fun show(
        context: Context,
        @StringRes rsId: Int,
        duration: Long = 3000,
    ) {
        hide()
        mToast = Toast.makeText(context.applicationContext, null, Toast.LENGTH_SHORT)
        mToast?.setText(rsId)
        setToastTheme(context, mToast, context.getString(rsId))
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

    private fun setToastTheme(context: Context, toast: Toast?, message: String) {
        runCatching {
            val isDarkMode = context.isDarkMode

            val backgroundColor = if (isDarkMode) "#FF333333" else "#FFD3D3D3"
            val textColor = if (isDarkMode) "#FFD3D3D3" else "#FF333333"
            val paddingHorizontal = context.dip2px(16f)
            val paddingVertical = context.dip2px(12f)
            val cornerRadius = 24f
            val shadowColor = "#22666666" // semi-transparent black for shadow
            val shadowRadius = context.dip2px(0.5f)

            // Create a new LinearLayout to be used as the Toast's view
            val linearLayout = LinearLayout(context).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                )

                // Create a new ShapeDrawable with rounded corners for shadow
                val shadowRadii = FloatArray(8) { cornerRadius + shadowRadius }
                val shadowShape = RoundRectShape(shadowRadii, null, null)
                val shadowDrawable = ShapeDrawable(shadowShape).apply {
                    paint.color = Color.parseColor(shadowColor)
                }

                // Create a new ShapeDrawable with rounded corners for background
                val backgroundRadii = FloatArray(8) { cornerRadius }
                val backgroundShape = RoundRectShape(backgroundRadii, null, null)
                val backgroundDrawable = ShapeDrawable(backgroundShape).apply {
                    paint.color = Color.parseColor(backgroundColor)
                    setPadding(paddingHorizontal, paddingVertical, paddingHorizontal, paddingVertical)
                }

                // Create a LayerDrawable with the shadow as the bottom layer and the background as the top layer
                background = LayerDrawable(arrayOf(shadowDrawable, backgroundDrawable)).apply {
                    // Offset the top layer to create the shadow effect
                    setLayerInset(1, shadowRadius, shadowRadius, shadowRadius, shadowRadius)
                }

                // Create a new TextView to be used as the Toast's text
                val textView = TextView(context).apply {
                    id = android.R.id.message
                    text = message
                    setTextColor(Color.parseColor(textColor))
                    // Set the text size and style to match the default Toast style
                    setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
                }

                // Add the TextView to the LinearLayout
                addView(textView)
            }

            // Use the LinearLayout as the Toast's view
            toast?.view = linearLayout
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
