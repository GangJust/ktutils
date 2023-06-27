package com.freegang.ktutils.bitmap

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.RectF
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.nio.ByteBuffer

object KBitmapUtils {

    /**
     * 将 Bitmap 转换为字节数组
     *
     * 请注意，转换后的字节数组大小为 bitmap.width * bitmap.height * 4，
     * 其中 bitmap.width 是 Bitmap 的宽度，bitmap.height 是 Bitmap 的高度。
     * 每个像素由 4 个字节表示（ARGB 格式，每个通道占 1 个字节），
     * 因此乘以 4 来计算总字节数。
     *
     * @param bitmap 要转换的 Bitmap 对象
     * @return 转换后的字节数组，如果转换失败则返回 null
     */
    @JvmStatic
    fun bitmap2Bytes(bitmap: Bitmap): ByteArray? {
        // 创建一个 ByteBuffer，分配足够的空间来存储 Bitmap 的像素数据
        val buffer = ByteBuffer.allocate(bitmap.width * bitmap.height * 4)
        // 将 Bitmap 的像素数据复制到 ByteBuffer 中
        bitmap.copyPixelsToBuffer(buffer)
        // 获取 ByteBuffer 的字节数组形式
        val bytes = buffer.array()
        // 清空 ByteBuffer
        buffer.clear()
        return bytes
    }

    /**
     * 将 Bitmap 转换为字节数组
     *
     * (如出现图片失真、花图等情况请调用: KBitmapUtils#bitmap2bytes(Bitmap))
     *
     * @param bitmap           要转换的 Bitmap 对象
     * @param compressFormat   图片压缩格式，如 Bitmap.CompressFormat.PNG、Bitmap.CompressFormat.JPEG
     * @return 转换后的字节数组，如果转换失败则返回 null
     */
    @JvmStatic
    fun bitmap2Bytes(
        bitmap: Bitmap,
        compressFormat: Bitmap.CompressFormat,
    ): ByteArray? {
        var bytes: ByteArray?
        ByteArrayOutputStream().use { outputStream ->
            bitmap.compress(compressFormat, 100, outputStream)
            bytes = outputStream.toByteArray()
        }
        return bytes
    }

    /**
     * 将字节数组转换为 Bitmap 对象
     *
     * @param bytes   要转换的字节数组
     * @param opts    可选的 BitmapFactory.Options 对象，用于配置 Bitmap 的解码选项
     * @return 转换后的 Bitmap 对象，如果转换失败则返回 null
     */
    @JvmStatic
    @JvmOverloads
    fun bytes2Bitmap(
        bytes: ByteArray,
        opts: BitmapFactory.Options? = null,
    ): Bitmap? {
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size, opts)
    }

    /**
     * 将 Bitmap 对象转换为 Drawable 对象
     *
     * @param context 上下文对象，用于创建 Drawable 对象
     * @param bitmap  要转换的 Bitmap 对象
     * @return 转换后的 Drawable 对象
     */
    @JvmStatic
    fun bitmap2Drawable(
        context: Context,
        bitmap: Bitmap,
    ): Drawable {
        return BitmapDrawable(context.resources, bitmap)
    }

    /**
     * 将 Drawable 对象转换为 Bitmap 对象
     *
     * @param drawable 要转换的 Drawable 对象
     * @param config 位图 Config 默认为 Bitmap.Config.ARGB_8888
     * @return 转换后的 Bitmap 对象
     */
    @JvmStatic
    @JvmOverloads
    fun drawable2Bitmap(
        drawable: Drawable,
        config: Bitmap.Config = Bitmap.Config.ARGB_8888,
    ): Bitmap {
        // 判断 Drawable 是否为 BitmapDrawable 类型
        if (drawable is BitmapDrawable) {
            return drawable.bitmap
        }

        // 获取 Drawable 的宽度和高度
        val width = drawable.intrinsicWidth
        val height = drawable.intrinsicHeight

        // 创建一个 ARGB_8888 格式的 Bitmap 对象
        val bitmap = Bitmap.createBitmap(width, height, config)

        // 创建一个 Canvas 对象，并将 Bitmap 设置为绘制目标
        val canvas = Canvas(bitmap)

        // 将 Drawable 绘制到 Canvas 上
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)

        return bitmap
    }

    /**
     * 将 Bitmap 保存到指定路径
     *
     * @param bitmap 要保存的 Bitmap 对象
     * @param outputPath 保存的文件路径
     * @param compressFormat 保存的图片格式，默认为 JPEG
     * @param quality 保存的图片质量，取值范围为 0-100，默认为 100
     * @return 保存成功返回 true，保存失败返回 false
     */
    @JvmStatic
    @JvmOverloads
    fun bitmap2File(
        bitmap: Bitmap,
        outputPath: String,
        compressFormat: Bitmap.CompressFormat = Bitmap.CompressFormat.JPEG,
        quality: Int = 100,
    ): Boolean {
        return try {
            File(outputPath)
                .outputStream()
                .use { outputStream ->
                    bitmap.compress(compressFormat, quality, outputStream)
                }
            true
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }
    }

    /**
     * 缩放 Bitmap 对象, 该操作会返回一个新的 Bitmap
     *
     * @param bitmap 要缩放的 Bitmap 对象
     * @param newWidth 缩放后的宽度
     * @param newHeight 缩放后的高度
     * @param filter 是否使用滤波器进行缩放，默认为 true
     * @return 缩放后的 Bitmap 对象
     */
    @JvmStatic
    @JvmOverloads
    fun scaleBitmap(
        bitmap: Bitmap,
        newWidth: Int,
        newHeight: Int,
        filter: Boolean = true,
    ): Bitmap {
        // 创建一个 Matrix 对象，并设置缩放比例
        val matrix = Matrix()
        matrix.postScale(newWidth.toFloat() / bitmap.width, newHeight.toFloat() / bitmap.height)

        // 使用 Matrix 对象对 Bitmap 进行缩放
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, filter)
    }

    /**
     * 获取圆角 Bitmap 对象, 该操作会返回一个新的 Bitmap
     *
     * @param bitmap 要设置圆角的 Bitmap 对象
     * @param topLeftRadius 左上角圆角半径，默认为 0
     * @param topRightRadius 右上角圆角半径，默认为 0
     * @param bottomLeftRadius 左下角圆角半径，默认为 0
     * @param bottomRightRadius 右下角圆角半径，默认为 0
     * @return 设置圆角后的 Bitmap 对象
     */
    @JvmStatic
    @JvmOverloads
    fun getRoundCornerBitmap(
        bitmap: Bitmap,
        topLeftRadius: Float = 0f,
        topRightRadius: Float = 0f,
        bottomLeftRadius: Float = 0f,
        bottomRightRadius: Float = 0f
    ): Bitmap {
        val width = bitmap.width
        val height = bitmap.height

        // 创建一个空白的 Bitmap 对象，用于绘制圆角
        val output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)

        // 创建一个 Paint 对象，并设置抗锯齿和图像过滤
        val paint = Paint().apply {
            isAntiAlias = true
            isFilterBitmap = true
            isDither = true
        }

        // 创建一个 RectF 对象，用于绘制圆角矩形
        val rectF = RectF(0f, 0f, width.toFloat(), height.toFloat())

        // 创建一个 Path 对象，并指定四个不同的圆角半径
        val path = Path().apply {
            addRoundRect(
                rectF,
                floatArrayOf(
                    topLeftRadius, topLeftRadius,
                    topRightRadius, topRightRadius,
                    bottomRightRadius, bottomRightRadius,
                    bottomLeftRadius, bottomLeftRadius
                ),
                Path.Direction.CW
            )
        }

        // 在 Canvas 上裁剪出圆角矩形的区域
        canvas.clipPath(path)

        // 将原始 Bitmap 绘制在 Canvas 上
        canvas.drawBitmap(bitmap, 0f, 0f, paint)

        // 返回设置圆角后的 Bitmap 对象
        return output
    }


    /**
     * 为指定 Bitmap 增加左下角文本水印
     * @param bitmap 被增加水印的Bitmap
     * @param markerTexts 水印文本列表，按行绘制
     * @return 添加水印后的Bitmap, 请合理回收
     */
    @JvmStatic
    @JvmOverloads
    fun bitmapAddWaterMarkerText(
        bitmap: Bitmap,
        markerTexts: Array<String>,
        paint: Paint? = null,
    ): Bitmap {
        val resultBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(resultBitmap)

        val textPaint = paint ?: Paint().apply {
            color = Color.RED
            textSize = 80f
            isAntiAlias = true
            style = Paint.Style.FILL
        }

        val padding = 12
        val lineHeight = textPaint.fontMetrics.descent - textPaint.fontMetrics.ascent

        val startY = resultBitmap.height - padding.toFloat()

        for (i in markerTexts.indices) {
            val text = markerTexts[i]
            val textWidth = textPaint.measureText(text)

            val x = padding.toFloat()
            val y = startY - lineHeight * (markerTexts.size - i)

            canvas.drawText(text, x, y, textPaint)
        }

        return resultBitmap
    }
}