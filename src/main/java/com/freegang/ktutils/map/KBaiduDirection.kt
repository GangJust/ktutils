package com.freegang.ktutils.map

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlin.math.abs

/**
 * 百度地图 MyLocation 当前位置定位图层箭头旋转方向辅助工具类。
 *
 * ```
 * // 以下是可能的使用示例(java):
 * private float mCurrentDirection;
 *
 * KBaiduDirection.start(context, x -> {
 *     mCurrentDirection = x;
 *     locData = new MyLocationData.Builder()
 *             //... 其他设置
 *             .direction(mCurrentDirection) // 此处设置开发者获取到的方向信息，顺时针0-360
 *             //... 其他设置
 *             .build();
 *     baiduMap.setMyLocationData(locData);
 * });
 * ```
 */
object KBaiduDirection {
    private var sensorManager: SensorManager? = null
    private var listener: Listener? = null
    private var callbacks: MutableList<DirectionChangeCallback> = mutableListOf()

    private var mLastX: Float = 0f

    /**
     * 开始监听方向变化，通常在[onStart]或者之后调用。
     *
     * @param context Context
     */
    @JvmStatic
    @JvmOverloads
    fun start(context: Context, callback: DirectionChangeCallback? = null) {
        callback?.let { callbacks.add(it) }

        sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager?
        sensorManager?.registerListener(
            Listener().also { listener = it },
            sensorManager?.getDefaultSensor(Sensor.TYPE_ORIENTATION),
            SensorManager.SENSOR_DELAY_UI
        )
    }

    /**
     * 停止监听方向变化，通常在[onStop]或者之后调用
     */
    @JvmStatic
    fun stop() {
        callbacks.clear()
        listener?.let { sensorManager?.unregisterListener(it) }
        sensorManager = null
        listener = null
    }

    /**
     * 方向改变回调队列
     */
    @JvmStatic
    fun addDirectionChangeCallback(callback: DirectionChangeCallback) {
        callbacks.add(callback)
    }

    /**
     * 方向监听器
     */
    private class Listener : SensorEventListener {

        override fun onSensorChanged(event: SensorEvent) {
            // 只获取x的值
            val x = event.values[SensorManager.DATA_X]

            // 为了防止经常性的更新
            if (abs(x - mLastX) > 1.0)
                callbacks.forEach { it.direction(x) }

            mLastX = x
        }

        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {

        }
    }

    /**
     * 队列接口
     */
    fun interface DirectionChangeCallback {
        fun direction(x: Float)
    }
}