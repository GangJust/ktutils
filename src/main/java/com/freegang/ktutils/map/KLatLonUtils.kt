package com.freegang.ktutils.map

import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

object KLatLonUtils {
    private const val EARTH_RADIUS = 6371.0 // 地球半径，单位：km

    /**
     * 获取两个经纬度之间的距离，返回间隔多少米
     * @param lat1 第一个坐标点的纬度
     * @param lon1 第一个坐标点的经度
     * @param lat2 第二个坐标点的纬度
     * @param lon2 第二个坐标点的经度
     * @return 两个坐标点之间的距离，单位：米
     */
    @JvmStatic
    fun getDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)

        val a = sin(dLat / 2).pow(2) +
                cos(Math.toRadians(lat1)) *
                cos(Math.toRadians(lat2)) *
                sin(dLon / 2).pow(2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return EARTH_RADIUS * c * 1000 // 转换为米
    }

    /**
     * 获取两个经纬度之间的距离，返回间隔多少米
     * @param lat1 第一个坐标点的纬度
     * @param lon1 第一个坐标点的经度
     * @param lat2 第二个坐标点的纬度
     * @param lon2 第二个坐标点的经度
     * @return 两个坐标点之间的距离，单位：米
     */
    @JvmStatic
    fun getDistance(lat1: String, lon1: String, lat2: String, lon2: String): Double {
        return getDistance(
            lat1.toDouble(),
            lon1.toDouble(),
            lat2.toDouble(),
            lon2.toDouble(),
        )
    }

    /**
     * 判断给定的经纬度是否合理。
     *
     * @param lat 纬度值
     * @param lon 经度值
     * @return 经纬度是否合理，合理返回 true，否则返回 false
     */
    @JvmStatic
    fun isLatLngValid(lat: Double, lon: Double): Boolean {
        val validLatRange = -90.0..90.0
        val validLonRange = -180.0..180.0

        return lat in validLatRange && lon in validLonRange
    }

    /**
     * 判断给定的经纬度是否合理。
     *
     * @param lat 纬度值
     * @param lon 经度值
     * @return 经纬度是否合理，合理返回 true，否则返回 false
     */
    @JvmStatic
    fun isLatLngValid(lat: String, lon: String): Boolean {
        return isLatLngValid(lat.toDouble(), lon.toDouble())
    }
}
