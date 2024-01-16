package com.freegang.ktutils.bitmap

import android.graphics.Bitmap
import android.graphics.Color
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter

/**
 * 请手动增加Zxing依赖。
 *
 * implementation 'com.google.zxing:core:<version>'
 */
object KMultiCodeUtils {

    /**
     * 创建生成二维码。
     *
     * @param contents 二维码内容
     * @param width 宽度
     * @param height 高度
     * @param margin 背景边距
     * @param contentColor 内容颜色，默认黑色
     * @param backgroundColor 背景颜色，默认白色
     */
    @JvmStatic
    @JvmOverloads
    fun createQRCode(
        contents: String,
        width: Int = 254,
        height: Int = 254,
        margin: Int = 1,
        contentColor: Int = Color.BLACK,
        backgroundColor: Int = Color.WHITE,
    ): Bitmap? {
        return create(
            contents,
            BarcodeFormat.QR_CODE,
            width,
            height,
            contentColor,
            backgroundColor,
            mapOf(EncodeHintType.MARGIN to margin),
        )
    }

    /**
     * 创建生成条形码。
     *
     * @param contents 条形码内容，不能超过81个字符
     * @param width 宽度
     * @param height 高度
     * @param margin 背景边距
     * @param contentColor 内容颜色，默认黑色
     * @param backgroundColor 背景颜色，默认白色
     */
    @JvmStatic
    @JvmOverloads
    fun createBarcode(
        contents: String,
        width: Int = 254,
        height: Int = 254,
        margin: Int = 1,
        contentColor: Int = Color.BLACK,
        backgroundColor: Int = Color.WHITE,
    ): Bitmap? {
        return create(
            contents,
            BarcodeFormat.CODE_128,
            width,
            height,
            contentColor,
            backgroundColor,
            mapOf(EncodeHintType.MARGIN to margin),
        )
    }

    private fun create(
        contents: String,
        format: BarcodeFormat,
        width: Int = 254,
        height: Int = 254,
        contentColor: Int = Color.BLACK,
        backgroundColor: Int = Color.WHITE,
        hints: Map<EncodeHintType, *>? = null,
    ): Bitmap? {
        val writer = MultiFormatWriter()
        val matrix = writer.encode(
            contents,
            format,
            width,
            height,
            hints,
        )
        val newWidth = matrix.width
        val newHeight = matrix.height

        // 矩阵转换, 构建 Bitmap
        val pixels = IntArray(newWidth * newHeight)
        for (y in 0 until newHeight) {
            val offset = y * newWidth
            for (x in 0 until newWidth) {
                pixels[offset + x] = if (matrix.get(x, y)) contentColor else backgroundColor
            }
        }

        // 创建Bitmap
        val bitmap = Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.ARGB_8888)
        bitmap.setPixels(pixels, 0, newWidth, 0, 0, newWidth, newHeight)
        return bitmap
    }
}