package com.freegang.ktutils.map

import android.graphics.Point
import com.baidu.mapapi.map.Projection
import com.baidu.mapapi.model.LatLng
import kotlin.math.pow

/**
 * 百度地图, WMS瓦片图工具类
 * <p>
 * 参考: https://www.jianshu.com/p/47572eb39156
 */
object KBaiduTileUtils {
    // 显示等级数组, 数组长度给定30, 避免Index越界 (百度地图 getMinDisLevel=3, getMaxDisLevel=20)
    private val disLevel = DoubleArray(30).apply {
        for (i in indices) {
            this[i] = 2.0.pow(18 - i)
        }
    }

    /**
     * 通过瓦片图 x,y,z 获取Wms瓦片BBox
     *
     * @param tileSize 瓦片图大小
     * @param x        瓦片图的x
     * @param y        瓦片图的y
     * @param z        瓦片图的z
     * @return length == 4 的 BBox 数组
     */
    @JvmStatic
    fun getWmsBBox(tileSize: Int, x: Int, y: Int, z: Int): DoubleArray {
        val res = disLevel[z]
        val minx = x * tileSize * res
        val miny = y * tileSize * res
        val maxx = (x + 1) * tileSize * res
        val maxy = (y + 1) * tileSize * res

        // 百度墨卡托坐标 -> 百度经纬度坐标
        val bottomLeft: LatLng = KCoordTransform.BD_MKT2WGS(minx, miny)
        val topRight: LatLng = KCoordTransform.BD_MKT2WGS(maxx, maxy)

        // 地图旋转可能导致 坐下右上 互换.
        return doubleArrayOf(
            bottomLeft.longitude,
            bottomLeft.latitude,
            topRight.longitude,
            topRight.latitude,
        ).also { bBox ->
            if (bottomLeft.latitude > topRight.latitude) {
                bBox[1] = bBox[3].also { bBox[3] = bBox[1] }
            }
            if (bottomLeft.longitude > topRight.longitude) {
                bBox[0] = bBox[2].also { bBox[2] = bBox[0] }
            }
        }
    }

    /**
     * 通过点击的经纬度与屏幕点位获取瓦片图层的大概BBox (存在小问题, 勿用)
     *
     * @param level      地图的缩放比例,可通过: MapView.getMapLevel() 获取, 这里的缩放比
     * @param projection 地图屏幕相对位置工具类,可通过 BaiduMap.getProjection() 获取
     * @param layerSize  瓦片图层大小, 按照百度地图的逻辑应该是 256*256 的图层, 网络上又有人说是 64*64 的图层
     * @param latLng     地图上被点击的经纬度, 需要监听地图单击事件, 也可以是你确定的一个点位
     * @return length == 4 的 BBox 数组
     */
    @Deprecated("存在小问题, 勿用")
    @JvmStatic
    fun getWmsBBox(
        level: Int,
        projection: Projection,
        layerSize: Int,
        latLng: LatLng?
    ): DoubleArray {
        // 获取缩放值(瓦片大小)
        val scale = layerSize / level

        // 通过地图经纬度获取相对位置的屏幕坐标
        val screenLocation = projection.toScreenLocation(latLng)

        // 左下角; x左移动, y下移动
        val bottomLeftPoint = Point(screenLocation.x - scale, screenLocation.y + scale)
        // 右上角; x右移动, y上移动
        val topRightPoint = Point(screenLocation.x + scale, screenLocation.y - scale)

        // 将屏幕的相对坐标转换为经纬度
        val bottomLeft = projection.fromScreenLocation(bottomLeftPoint)
        val topRight = projection.fromScreenLocation(topRightPoint)
        val bBox = doubleArrayOf(
            bottomLeft.longitude,
            bottomLeft.latitude,
            topRight.longitude,
            topRight.latitude
        )

        // 地图旋转可能导致 坐下右上 互换.
        if (bottomLeft.latitude > topRight.latitude) {
            val tmp = bBox[1]
            bBox[1] = bBox[3]
            bBox[3] = tmp
        }
        if (bottomLeft.longitude > topRight.longitude) {
            val tmp = bBox[0]
            bBox[0] = bBox[2]
            bBox[2] = tmp
        }
        return bBox
    }
}