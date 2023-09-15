package com.freegang.ktutils.map

import com.baidu.mapapi.map.BaiduMap
import com.baidu.mapapi.map.Marker
import com.baidu.mapapi.map.MarkerOptions
import com.baidu.mapapi.map.Overlay
import com.baidu.mapapi.map.Polygon
import com.baidu.mapapi.map.PolygonOptions
import java.lang.reflect.ParameterizedType

/// 百度地图覆盖物工具类
object KBaiduOverlayUtils {
    private val polygonList: MutableList<Polygon> = mutableListOf()
    private val markerList: MutableList<Marker> = mutableListOf()

    /**
     * 添加多边形折线
     * @param map 百度地图
     * @param option 多边形折线操作项
     */
    @JvmStatic
    fun addPolygon(map: BaiduMap, option: PolygonOptions) {
        val polygon = map.addOverlay(option) as? Polygon ?: return
        polygonList.add(polygon)
    }

    /**
     * 添加多边形折线
     * @param map 百度地图
     * @param options 多边形折线操作项列表
     */
    @JvmStatic
    fun addPolygon(map: BaiduMap, options: List<PolygonOptions>) {
        for (option in options) {
            val polygon = map.addOverlay(option) as? Polygon ?: return
            polygonList.add(polygon)
        }
    }

    /**
     * 移除多边形折线
     * @param map 百度地图
     * @param polygon 多边形折线
     */
    @JvmStatic
    fun removePolygon(map: BaiduMap, polygon: Polygon) {
        map.removeOverLays(listOf(polygon))
        polygonList.remove(polygon)
    }

    /**
     * 移除多边形折线
     * @param map 百度地图
     * @param polygons 多边形折线列表
     */
    @JvmStatic
    fun removePolygon(map: BaiduMap, polygons: List<Polygon>) {
        map.removeOverLays(polygons)
        polygonList.removeAll(polygons)
    }

    /**
     * 移除多边形折线
     * @param map 百度地图
     * @param index 下标
     */
    @JvmStatic
    fun removePolygonAt(map: BaiduMap, index: Int) {
        val removeAt = polygonList.removeAt(index)
        map.removeOverLays(listOf(removeAt))
    }

    /**
     * 添加 MarkerIcon
     * @param map 百度地图
     * @param option Marker操作项
     */
    @JvmStatic
    fun addMarker(map: BaiduMap, option: MarkerOptions) {
        val overlay = map.addOverlay(option) as? Marker ?: return
        markerList.add(overlay)
    }

    /**
     * 添加 MarkerIcon
     * @param map 百度地图
     * @param options Marker操作项列表
     */
    @JvmStatic
    fun addMarker(map: BaiduMap, options: List<MarkerOptions>) {
        for (option in options) {
            val overlay = map.addOverlay(option) as? Marker ?: return
            markerList.add(overlay)
        }
    }

    /**
     * 移除多边形折线
     * @param map 百度地图
     * @param marker Marker
     */
    @JvmStatic
    fun removeMarker(map: BaiduMap, marker: Marker) {
        map.removeOverLays(listOf(marker))
        markerList.remove(marker)
    }

    /**
     * 移除 MarkerIcon
     * @param map 百度地图
     * @param markers Marker列表
     */
    fun removeMarker(map: BaiduMap, markers: List<Marker>) {
        map.removeOverLays(markers)
        markerList.removeAll(markers)
    }

    /**
     * 获取所有覆盖物-反射
     * @param map 百度地图
     */
    @JvmStatic
    fun getBaiduOverlay(map: BaiduMap): List<Overlay> {
        var overlay: List<Overlay> = emptyList()
        val fields = map::class.java.declaredFields
        for (field in fields) {
            if (field.type != List::class.java) continue
            val genericType = field.genericType as ParameterizedType
            val type = genericType.actualTypeArguments[0] as Class<*>
            if (type == Overlay::class.java) {
                field.isAccessible = true
                overlay = (field.get(map) as List<Overlay>?) ?: emptyList()
                break
            }
        }
        return overlay
    }

    /**
     * 清除所有覆盖物
     */
    @JvmStatic
    fun clearOverlay(map: BaiduMap) {
        map.clear()
        polygonList.clear()
        markerList.clear()
    }

    /**
     * 清除所有Marker点位
     */
    @JvmStatic
    fun clearMarker(map: BaiduMap) {
        map.removeOverLays(markerList as List<Overlay>?)
        markerList.clear()
    }
}