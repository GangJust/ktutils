package com.freegang.ktutils.net

import android.Manifest
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat


/**
 * 在使用这个类之前，需要在AndroidManifest.xml中添加以下权限：
 * ```
 * <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
 * <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
 * ```
 * 注意：在Android Q及以上版本，你可能需要额外的权限来访问Wi-Fi的RSSI值。在这种情况下，你可能需要添加ACCESS_FINE_LOCATION或ACCESS_COARSE_LOCATION权限，并在运行时请求这些权限。
 */
object KWifiUtils {

    /**
     * 检查应用是否具有Wifi操作权限的方法。
     *
     * @param context 上下文对象，通常是Activity或Application的实例。
     * @return 如果应用具有Wifi操作权限则返回true，否则返回false。
     */
    fun hasWifiPermission(context: Context): Boolean {
        return listOf(
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.CHANGE_WIFI_STATE
        ).all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    /**
     * 跳转到系统Wifi设置页
     * @param context Context
     */
    fun toWifiSetting(context: Context) {
        val intent = Intent(Settings.ACTION_WIFI_SETTINGS)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
    }

    /**
     * Wifi是否已经开启
     * @param context Context
     */
    @JvmStatic
    fun isWifiEnabled(context: Context): Boolean {
        val wifiManager =
            context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        return wifiManager.isWifiEnabled
    }

    /**
     * 获取当前连接的Wifi名称
     * @param context Context
     */
    @JvmStatic
    fun getWifiName(context: Context): String? {
        val wifiManager =
            context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val wifiInfo = wifiManager.connectionInfo
        return if (wifiInfo.ssid.isNotEmpty()) {
            wifiInfo.ssid.removePrefix("\"").removeSuffix("\"") // 去除可能存在的引号
        } else {
            null
        }
    }

    /**
     * 扫描wifi列表, 需要增加以下权限
     * ```
     * <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
     * ```
     * @param context Context
     * @param result ScanWifiResult 扫描Wifi回调
     */
    @JvmStatic
    fun scanWifiList(
        context: Context,
        result: ScanWifiResult,
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            scanWifiListAfterM(context, result)
        } else {
            scanWifiListBeforeM(context, result)
        }
    }

    // 扫描Wifi列表 Android 6-
    @SuppressLint("MissingPermission")
    private fun scanWifiListBeforeM(
        context: Context,
        result: ScanWifiResult,
    ) {
        val wifiManager =
            context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val wifiScanReceiver = object : BroadcastReceiver() {
            @RequiresApi(Build.VERSION_CODES.M)
            override fun onReceive(context: Context, intent: Intent) {
                if (result.onSuccess(wifiManager.scanResults)) { // 返回true才结束广播
                    context.unregisterReceiver(this)
                }
            }
        }

        val intentFilter = IntentFilter()
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
        context.registerReceiver(wifiScanReceiver, intentFilter)

        val success = wifiManager.startScan()
        if (!success) {
            result.onFailure()
        }
    }

    // 扫描Wifi列表 Android 6+
    @SuppressLint("MissingPermission")
    private fun scanWifiListAfterM(
        context: Context,
        result: ScanWifiResult,
    ) {
        val wifiManager =
            context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val wifiScanReceiver = object : BroadcastReceiver() {
            @RequiresApi(Build.VERSION_CODES.M)
            override fun onReceive(context: Context, intent: Intent) {
                val success =
                    intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false) // 只多了这一个
                if (success) {
                    if (result.onSuccess(wifiManager.scanResults)) { // 返回true才结束广播
                        context.unregisterReceiver(this)
                    }
                } else {
                    result.onFailure()
                }
            }
        }

        val intentFilter = IntentFilter()
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
        context.registerReceiver(wifiScanReceiver, intentFilter)

        val success = wifiManager.startScan()
        if (!success) {
            result.onFailure()
        }
    }

    // 连接到指定Wifi, Android 10 以后出现各种限制, 看得眼花缭乱的, 先放着。
    // ...

    // Wifi扫描结果回调接口
    interface ScanWifiResult {

        /**
         * Wifi扫描成功回调该方法, 将通过广播连续回调
         * 当返回值为 ture 时才会终止广播
         * @param result 返回扫描结果列表
         */
        fun onSuccess(result: List<ScanResult>): Boolean


        /**
         * 扫描失败回调该方法
         */
        fun onFailure()
    }

}