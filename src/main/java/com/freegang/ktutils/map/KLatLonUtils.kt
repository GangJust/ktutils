package com.freegang.ktutils.map

import kotlin.math.*

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

        val a = sin(dLat / 2).pow(2) + cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLon / 2).pow(2)
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
    fun getDistance(lat1: String, lon1: String, lat2: String, lon2: String): Double {
        return getDistance(
            lat1.toDouble(),
            lon1.toDouble(),
            lat2.toDouble(),
            lon2.toDouble(),
        )
    }
}
