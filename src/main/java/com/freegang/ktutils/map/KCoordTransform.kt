package com.freegang.ktutils.map

import com.baidu.mapapi.model.LatLng
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.ln
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Created by Daniel on 2016/7/27.
 *
 * 提供了百度坐标（BD09）、国测局坐标（火星坐标，GCJ02）、和WGS84坐标系之间的转换，会有偏差，但是偏差在可接受范围之内
 * 参考: https://www.jianshu.com/p/47572eb39156
 */
object KCoordTransform {
    private const val x_PI = 3.14159265358979324 * 3000.0 / 180.0
    private const val PI = 3.1415926535897932384626
    private const val a = 6378245.0
    private const val ee = 0.00669342162296594323
    private val MCBAND = doubleArrayOf(
        12890594.86,
        8362377.87,
        5591021.0,
        3481989.83,
        1678043.12,
        0.0,
    )
    private val MC2LL = arrayOf(
        doubleArrayOf(
            1.410526172116255e-8,
            0.00000898305509648872,
            -1.9939833816331,
            200.9824383106796,
            -187.2403703815547,
            91.6087516669843,
            -23.38765649603339,
            2.57121317296198,
            -0.03801003308653,
            17337981.2
        ),
        doubleArrayOf(
            -7.435856389565537e-9,
            0.000008983055097726239,
            -0.78625201886289,
            96.32687599759846,
            -1.85204757529826,
            -59.36935905485877,
            47.40033549296737,
            -16.50741931063887,
            2.28786674699375,
            10260144.86
        ),
        doubleArrayOf(
            -3.030883460898826e-8,
            0.00000898305509983578,
            0.30071316287616,
            59.74293618442277,
            7.357984074871,
            -25.38371002664745,
            13.45380521110908,
            -3.29883767235584,
            0.32710905363475,
            6856817.37
        ),
        doubleArrayOf(
            -1.981981304930552e-8,
            0.000008983055099779535,
            0.03278182852591,
            40.31678527705744,
            0.65659298677277,
            -4.44255534477492,
            0.85341911805263,
            0.12923347998204,
            -0.04625736007561,
            4482777.06
        ),
        doubleArrayOf(
            3.09191371068437e-9,
            0.000008983055096812155,
            0.00006995724062,
            23.10934304144901,
            -0.00023663490511,
            -0.6321817810242,
            -0.00663494467273,
            0.03430082397953,
            -0.00466043876332,
            2555164.4
        ),
        doubleArrayOf(
            2.890871144776878e-9,
            0.000008983055095805407,
            -3.068298e-8,
            7.47137025468032,
            -0.00000353937994,
            -0.02145144861037,
            -0.00001234426596,
            0.00010322952773,
            -0.00000323890364,
            826088.5
        )
    )

    /**
     * 百度坐标系 (BD-09) 与 火星坐标系 (GCJ-02)的转换
     * 即 百度 转 谷歌、高德
     *
     * @param bd_lon
     * @param bd_lat
     * @returns {[]} GCJ-02 坐标：[经度，纬度]
     */
    @JvmStatic
    fun transformBD09ToGCJ02(bd_lon: Double, bd_lat: Double): LatLng {
        val x = bd_lon - 0.0065
        val y = bd_lat - 0.006
        val z = sqrt(x * x + y * y) - 0.00002 * sin(y * x_PI)
        val theta = atan2(y, x) - 0.000003 * cos(x * x_PI)
        val gg_lng = z * cos(theta)
        val gg_lat = z * sin(theta)
        return LatLng(gg_lat, gg_lng)
    }

    /**
     * 火星坐标系 (GCJ-02) 与百度坐标系 (BD-09) 的转换
     * 即谷歌、高德 转 百度
     *
     * @param lng
     * @param lat
     * @returns {[]}  BD-09 坐标：[经度，纬度]
     */
    @JvmStatic
    fun transformGCJ02ToBD09(lng: Double, lat: Double): LatLng {
        val z = sqrt(lng * lng + lat * lat) + 0.00002 * sin(lat * x_PI)
        val theta = atan2(lat, lng) + 0.000003 * cos(lng * x_PI)
        val bd_lng = z * cos(theta) + 0.0065
        val bd_lat = z * sin(theta) + 0.006
        return LatLng(bd_lat, bd_lng)
    }

    /**
     * WGS84转GCj02
     *
     * @param lng
     * @param lat
     * @returns {[]} GCj02 坐标：[经度，纬度]
     */
    @JvmStatic
    fun transformWGS84ToGCJ02(lng: Double, lat: Double): LatLng {
        return if (outOfChina(lng, lat)) {
            LatLng(lat, lng)
        } else {
            var dLat = transformLat(lng - 105.0, lat - 35.0)
            var dLng = transformLng(lng - 105.0, lat - 35.0)
            val radLat = lat / 180.0 * PI
            var magic = sin(radLat)
            magic = 1 - ee * magic * magic
            val sqrtMagic = sqrt(magic)
            dLat = (dLat * 180.0) / ((a * (1 - ee)) / (magic * sqrtMagic) * PI)
            dLng = (dLng * 180.0) / (a / sqrtMagic * cos(radLat) * PI)
            val mgLat = lat + dLat
            val mgLng = lng + dLng
            LatLng(mgLat, mgLng)
        }
    }

    /**
     * GCJ02 转换为 WGS84
     *
     * @param lng
     * @param lat
     * @returns {[]} WGS84 坐标：[经度，纬度]
     */
    @JvmStatic
    fun transformGCJ02ToWGS84(lng: Double, lat: Double): LatLng {
        return if (outOfChina(lng, lat)) {
            LatLng(lat, lng)
        } else {
            var dLat = transformLat(lng - 105.0, lat - 35.0)
            var dLng = transformLng(lng - 105.0, lat - 35.0)
            val radLat = lat / 180.0 * PI
            var magic = sin(radLat)
            magic = 1 - ee * magic * magic
            val sqrtMagic = sqrt(magic)
            dLat = (dLat * 180.0) / ((a * (1 - ee)) / (magic * sqrtMagic) * PI)
            dLng = (dLng * 180.0) / (a / sqrtMagic * cos(radLat) * PI)
            val mgLat = lat + dLat
            val mgLng = lng + dLng
            LatLng(lat * 2 - mgLat, lng * 2 - mgLng)
        }
    }

    @JvmStatic
    fun transformLonLatToMecator(lng: Double, lat: Double): LatLng {
        val earthRad = 6378137.0
        val x = lng * PI / 180 * earthRad
        val a = lat * PI / 180
        val y = earthRad / 2 * ln((1.0 + sin(a)) / (1.0 - sin(a)))
        return LatLng(y, x) //[12727039.383734727, 3579066.6894065146]
    }

    /**
     * 百度坐标BD09 转 WGS84
     *
     * @param lng 经度
     * @param lat 纬度
     * @return {[]} WGS84 坐标：[经度，纬度]
     */
    @JvmStatic
    fun transformBD09ToWGS84(lng: Double, lat: Double): LatLng {
        val latLng = transformBD09ToGCJ02(lng, lat)
        return transformGCJ02ToWGS84(latLng.latitude, latLng.longitude)
    }

    /**
     * WGS84 转 百度坐标BD09
     *
     * @param lng 经度
     * @param lat 纬度
     * @return {[]} BD09 坐标：[经度，纬度]
     */
    @JvmStatic
    fun transformWGS84ToBD09(lng: Double, lat: Double): LatLng {
        val latLng = transformWGS84ToGCJ02(lng, lat)
        return transformGCJ02ToBD09(latLng.latitude, latLng.longitude)
    }

    private fun transformLat(lng: Double, lat: Double): Double {
        var ret =
            -100.0 + 2.0 * lng + 3.0 * lat + 0.2 * lat * lat + 0.1 * lng * lat + 0.2 * sqrt(
                abs(lng)
            )
        ret += (20.0 * sin(6.0 * lng * PI) + 20.0 * sin(2.0 * lng * PI)) * 2.0 / 3.0
        ret += (20.0 * sin(lat * PI) + 40.0 * sin(lat / 3.0 * PI)) * 2.0 / 3.0
        ret += (160.0 * sin(lat / 12.0 * PI) + 320 * sin(lat * PI / 30.0)) * 2.0 / 3.0
        return ret
    }

    private fun transformLng(lng: Double, lat: Double): Double {
        var ret = 300.0 + lng + 2.0 * lat + 0.1 * lng * lng + 0.1 * lng * lat + 0.1 * sqrt(abs(lng))
        ret += (20.0 * sin(6.0 * lng * PI) + 20.0 * sin(2.0 * lng * PI)) * 2.0 / 3.0
        ret += (20.0 * sin(lng * PI) + 40.0 * sin(lng / 3.0 * PI)) * 2.0 / 3.0
        ret += (150.0 * sin(lng / 12.0 * PI) + 300.0 * sin(lng / 30.0 * PI)) * 2.0 / 3.0
        return ret
    }

    /**
     * 判断是否在国内，不在国内则不做偏移
     *
     * @param lng
     * @param lat
     * @returns {boolean}
     */
    @JvmStatic
    fun outOfChina(lng: Double, lat: Double): Boolean {
        return (lng < 72.004 || lng > 137.8347) || (lat < 0.8293 || lat > 55.8271)
    }

    /**
     * 百度墨卡托坐标转WGS坐标
     *
     * @param lng
     * @param lat
     * @return
     */
    @JvmStatic
    fun BD_MKT2WGS(lng: Double, lat: Double): LatLng {
        var cF: DoubleArray? = null
        val lngAbs = abs(lng)
        val latAbs = abs(lat)
        for (cE in MCBAND.indices) {
            if (latAbs >= MCBAND[cE]) {
                cF = MC2LL[cE]
                break
            }
        }
        if (cF == null) {
            return LatLng(0.0, 0.0)
        }

        val lngRes = cF[0] + cF[1] * lngAbs
        val cC = latAbs / cF[9]
        val latRes =
            cF[2] + cF[3] * cC + cF[4] * cC * cC + cF[5] * cC * cC * cC + cF[6] * cC * cC * cC * cC + cF[7] * cC * cC * cC * cC * cC + cF[8] * cC * cC * cC * cC * cC * cC
        return LatLng(latRes * if (latRes < 0) -1 else 1, lngRes * if (lngRes < 0) -1 else 1)
    }
}