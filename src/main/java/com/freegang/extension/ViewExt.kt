package com.freegang.extension

import android.graphics.Point
import android.graphics.Rect
import android.os.Build
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.view.marginBottom
import androidx.core.view.marginEnd
import androidx.core.view.marginStart
import androidx.core.view.marginTop
import com.freegang.ktutils.view.KViewUtils

/**
 * 将View转换为JSON字符串表示。
 *
 * @return 转换后的JSON字符串
 */
fun View.toViewJson(indentSpaces: Int = 0): String {
    return KViewUtils.toViewJson(this, indentSpaces)
}

/**
 * 遍历给定View的所有父View，并对每个指定类型的父View执行一个操作。
 *
 * @param block 对每个指定类型的父View执行的操作。这个操作接受一个父View作为参数，直到它的为null为止
 */
fun View.forEachParent(block: (view: View) -> Unit) {
    KViewUtils.forEachParent(this, block)
}

/**
 * 遍历给定View的所有父View，并对每个指定类型的父View执行一个操作。
 *
 * @param block 对每个指定类型的父View执行的操作。这个操作接受一个父View作为参数，并返回一个布尔值。
 *              如果这个布尔值为 true，那么遍历将立即停止。否则，遍历将继续。
 */
fun View.forEachWhereParent(block: (view: View) -> Boolean) {
    KViewUtils.forEachWhereParent(this, block)
}

/**
 * 遍历 View 及其子 View。
 *
 * @param block 遍历回调函数，接收一个 View 参数
 */
fun View.forEachChild(block: (view: View) -> Unit) {
    KViewUtils.forEachChild(this, block)
}

/**
 * 条件遍历 View 及其子 View。
 *
 * @param block 遍历回调函数，接收一个 View 参数，返回一个 Boolean 如果为 `true` 则直接从树遍历中结束
 */
fun View.forEachWhereChild(block: (view: View) -> Boolean) {
    KViewUtils.forEachWhereChild(this, block)
}

/**
 * 获取第一个匹配类型的父View。
 *
 * @param clazz 父View类型
 */
fun <T : View> View.firstParentOrNull(clazz: Class<out T>): T? {
    var view: View? = null
    KViewUtils.forEachWhereParent(this) {
        if (clazz.isInstance(it)) {
            view = it
            return@forEachWhereParent true
        }
        false
    }
    return view?.let { clazz.cast(it) }
}

/**
 * 获取第一个匹配类型的父View。
 *
 * @param clazz 父View类型
 * @param where 条件判断，接收一个 View 参数，返回一个 Boolean 如果为 `true` 则直接从树遍历中结束
 */
fun <T : View> View.firstParentOrNull(clazz: Class<out T>, where: (view: T) -> Boolean): T? {
    var view: T? = null
    KViewUtils.forEachWhereParent(this) {
        if (!clazz.isInstance(it))
            return@forEachWhereParent false

        val cast = clazz.cast(it)!! //~
        if (where.invoke(cast)) {
            view = cast
            return@forEachWhereParent true
        }

        false
    }

    return view
}

/**
 * 获取第一个匹配类型的子View。
 *
 * @param clazz 子View类型
 */
fun <T : View> View.firstOrNull(clazz: Class<out T>): T? {
    var view: View? = null
    KViewUtils.forEachWhereChild(this) {
        if (clazz.isInstance(it)) {
            view = it
            return@forEachWhereChild true
        }
        false
    }
    return view?.let { clazz.cast(it) }
}

/**
 * 获取第一个匹配类型的子View。
 *
 * @param clazz 子View类型
 * @param where 条件判断，接收一个 View 参数，返回一个 Boolean 如果为 `true` 则直接从树遍历中结束
 */
fun <T : View> View.firstOrNull(clazz: Class<out T>, where: (view: T) -> Boolean): T? {
    var view: T? = null
    KViewUtils.forEachWhereChild(this) {
        if (!clazz.isInstance(it))
            return@forEachWhereChild false

        val cast = clazz.cast(it)!! //~
        if (where.invoke(cast)) {
            view = cast
            return@forEachWhereChild true
        }

        false
    }

    return view
}

/**
 * View过滤器，返回所有满足条件的子View序列。
 *
 * @param where 条件判断，接收一个 View 参数，返回一个 Boolean
 */
fun View.filter(where: (view: View) -> Boolean): Sequence<View> {
    val views = mutableListOf<View>()
    KViewUtils.forEachChild(this) {
        if (where.invoke(it))
            views.add(it)
    }
    return views.asSequence()
}

/**
 * 设置View的宽高。
 *
 * @param needWidth 需要设置的宽度
 * @param needHeight 需要设置的高度
 */
fun View.setLayoutSize(needWidth: Int, needHeight: Int) {
    layoutParams = layoutParams?.apply {
        width = needWidth
        height = needHeight
    }
}

/**
 * 设置View的宽度。
 *
 * @param needWidth 需要设置的宽度
 */
fun View.setLayoutWidth(needWidth: Int) {
    layoutParams = layoutParams?.apply {
        width = needWidth
    }
}

/**
 * 设置View的高度。
 *
 * @param needHeight 需要设置的高度
 */
fun View.setLayoutHeight(needHeight: Int) {
    layoutParams = layoutParams?.apply {
        height = needHeight
    }
}

/**
 * 设置增强型点击事件监听器，防止重复点击。
 *
 * @param interval 点击间隔时间
 * @param l 原始点击事件监听器
 */
fun View.setEnhanceOnClickListener(interval: Long = 200L, l: View.OnClickListener) {
    KViewUtils.setEnhanceOnClickListener(this, interval, l)
}

/**
 * 调用View.post方法，确保在主线程中执行。
 *
 * @param block 执行的操作
 */
fun <V : View> V.postRunning(block: (view: V) -> Unit) {
    post { block.invoke(this) }
}

/**
 * 延迟调用View.post方法，确保在主线程中执行。
 *
 * @param delayInMillis 延迟时间
 * @param block 延迟执行的操作
 */
fun <V : View> V.postDelayedRunning(delayInMillis: Long, block: (view: V) -> Unit) {
    postDelayed({ block.invoke(this) }, delayInMillis)
}

/**
 * 从当前View的父View中移除当前View，并返回被移除后的当前View。
 * */
fun View.removeInParent(): View? {
    val parentView = this.parent?.asOrNull<ViewGroup>() ?: return null
    parentView.removeView(this)
    return this
}

/**
 * 从当前View的父View中移除当前View，并返回当前View在父View中的索引。
 * */
fun View.removeInParentIndex(): Int {
    val parentView = this.parent?.asOrNull<ViewGroup>() ?: return -1
    val indexOfChild = parentView.indexOfChild(this)
    if (indexOfChild != -1) {
        parentView.removeViewAt(indexOfChild)
    }
    return indexOfChild
}

/**
 * 从父View中对当前View进行替换。
 *
 * @param newView 新的View
 */
fun View.replaceWith(newView: View) {
    KViewUtils.replaceWith(this, newView)
}

/**
 * 获取当前View相对位置的兄弟View，如: relativeIndex 为 1 则表示下一个兄弟View，为 -1 则表示上一个兄弟View。
 *
 * @param relativeIndex 相对位置索引
 */
fun View.getSiblingViewAt(relativeIndex: Int): View? {
    return KViewUtils.getSiblingViewAt(this, relativeIndex)
}

/**
 * 获取View可见性的字符串表示。
 */
fun View.visibilityToString(): String {
    return when (visibility) {
        View.VISIBLE -> "VISIBLE"
        View.INVISIBLE -> "INVISIBLE"
        View.GONE -> "GONE"
        else -> "UNKNOWN"
    }
}

/**
 * 获取View内间距的字符串表示。
 */
fun View.paddingToString(): String {
    val paddingStartDp = context.px2dip(paddingStart.toFloat())
    val paddingTopDp = context.px2dip(paddingTop.toFloat())
    val paddingEndDp = context.px2dip(paddingEnd.toFloat())
    val paddingBottomDp = context.px2dip(paddingBottom.toFloat())
    return "[${paddingStartDp}dp, ${paddingTopDp}dp, ${paddingEndDp}dp, ${paddingBottomDp}dp]"
}

/**
 * 获取View外间距的字符串表示。
 */
fun View.marginToString(): String {
    val marginStartDp = context.px2dip(marginStart.toFloat())
    val marginTopDp = context.px2dip(marginTop.toFloat())
    val marginEndDp = context.px2dip(marginEnd.toFloat())
    val marginBottomDp = context.px2dip(marginBottom.toFloat())

    return "[${marginStartDp}dp, ${marginTopDp}dp, ${marginEndDp}dp, ${marginBottomDp}dp]"
}

/**
 * View在Window上的位置。
 */
val View.locationInWindow: Point
    get() {
        val position = IntArray(2)
        this.getLocationInWindow(position)
        return Point(position[0], position[1])
    }

/**
 * View在屏幕上的位置。
 */
val View.locationOnScreen: Point
    get() {
        val position = IntArray(2)
        this.getLocationOnScreen(position)
        return Point(position[0], position[1])
    }

/**
 * ViewSurface在屏幕上的位置。
 */
val View.locationInSurface: Point
    @RequiresApi(Build.VERSION_CODES.Q)
    get() {
        val position = IntArray(2)
        this.getLocationInSurface(position)
        return Point(position[0], position[1])
    }

/**
 * View的全局可见性，可通过它判断该View是否在屏幕上显示。
 */
val View.localVisibleRect: Rect
    get() {
        val rect = Rect()
        this.getLocalVisibleRect(rect)
        return rect
    }

/**
 * View相对于其ParentView的位置
 */
val View.positionRect: Rect
    get() {
        toString()
        return Rect(left, top, right, bottom)
    }

/**
 * 获取 View 的点击事件监听器。
 */
val View.getOnClickListener: View.OnClickListener?
    get() = KViewUtils.getOnClickListener(this)

/**
 * 获取 View 的长按事件监听器。
 */
val View.getOnLongClickListener: View.OnLongClickListener?
    get() = KViewUtils.getOnLongClickListener(this)

/**
 * 获取 View 的触摸事件监听器。
 */
val View.getOnTouchListener: View.OnTouchListener?
    get() = KViewUtils.getOnTouchListener(this)

/**
 * View的父View。
 */
val View.parentView
    get() = this.parent?.asOrNull<ViewGroup>()

/**
 * View的ID名称。
 */
val View.idName
    get() = KViewUtils.getIdName(this)

/**
 * View的ID十六进制表示。
 */
val View.idHex
    get() = KViewUtils.getIdHex(this)

/**
 * View是否位于屏幕显示。
 */
val View.isDisplay: Boolean
    get() {
        // val screenSize = KDisplayUtils.screenSize()
        // val temp = IntArray(2) { 0 }
        // this.getLocationOnScreen(temp)
        // return (temp[0] >= 0 && temp[0] <= screenSize.width) && (temp[1] >= 0 && temp[1] <= screenSize.height)
        val rect = Rect()
        return getLocalVisibleRect(rect)
    }

/**
 * View 转 Bitmap。
 */
val View.toBitmap
    get() = KViewUtils.viewToBitmap(this)