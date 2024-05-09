package com.freegang.ktutils.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.view.ViewParent
import android.widget.ListView
import android.widget.TextView
import androidx.annotation.IdRes
import androidx.core.view.get
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.freegang.extension.asOrNull
import com.freegang.extension.locationInSurface
import com.freegang.extension.locationInWindow
import com.freegang.extension.locationOnScreen
import com.freegang.extension.marginToString
import com.freegang.extension.paddingToString
import com.freegang.extension.positionRect
import com.freegang.extension.px2dip
import com.freegang.extension.simpleString
import com.freegang.extension.toShortString
import com.freegang.extension.visibilityToString
import com.freegang.ktutils.color.KColorUtils
import org.json.JSONArray
import org.json.JSONObject
import java.lang.reflect.Field
import java.util.*
import kotlin.collections.ArrayDeque

fun interface ViewFunction {
    fun callView(view: View)
}

fun interface ViewWhereFunction {
    fun callView(view: View): Boolean
}

object KViewUtils {
    // 记录快速点击
    private val fastClickRecords: MutableMap<String, Long> = HashMap()

    @JvmStatic
    fun visibility(view: View, v: Boolean) {
        view.isVisible = v
    }

    @JvmStatic
    fun show(view: View) {
        view.visibility = View.VISIBLE
    }

    @JvmStatic
    fun hide(view: View) {
        view.visibility = View.GONE
    }

    fun invisible(view: View) {
        view.visibility = View.INVISIBLE
    }

    /**
     * 设置 ViewGroup 及其所有子视图的可见性为 [View.VISIBLE]，即全部显示。
     *
     * @param view 要设置可见性的 ViewGroup。
     */
    @JvmStatic
    fun showAll(view: View) {
        setVisibilityAll(view, View.VISIBLE)
    }

    /**
     * 设置 ViewGroup 及其所有子视图的可见性为 [View.GONE]，即全部隐藏。
     *
     * @param view 要设置可见性的 ViewGroup。
     */
    @JvmStatic
    fun hideAll(view: View) {
        setVisibilityAll(view, View.GONE)
    }

    /**
     * 设置 ViewGroup 及其所有子视图的可见性为 [View.INVISIBLE]，即全部不可见。
     *
     * @param view 要设置可见性的 ViewGroup。
     */
    @JvmStatic
    fun invisibleAll(view: View) {
        setVisibilityAll(view, View.INVISIBLE)
    }

    /**
     * 设置 ViewGroup 及其所有子视图的可见性。
     *
     * @param view   要设置可见性的 View。
     * @param visibility  要设置的可见性，可选值为 [View.VISIBLE]、[View.GONE] 或 [View.INVISIBLE]。
     */
    private fun setVisibilityAll(view: View, visibility: Int) {
        val stack = Stack<View>()
        stack.push(view)

        while (!stack.isEmpty()) {
            val currentView = stack.pop()

            if (currentView is ViewGroup) {
                val childCount = currentView.childCount
                for (i in 0 until childCount) {
                    val childAt = currentView.getChildAt(i)
                    if (childAt.visibility != visibility) {
                        stack.push(childAt)
                    }
                }
            }

            if (currentView.visibility != visibility) {
                currentView.visibility = visibility
            }
        }
    }


    //
    /**
     * 判断视图是否可见。
     *
     * @param view 要判断可见性的视图。
     * @return 如果视图可见，则返回 `true`；否则返回 `false`。
     */
    @JvmStatic
    fun isVisible(view: View): Boolean {
        return view.visibility == View.VISIBLE
    }

    /**
     * 判断视图是否隐藏。
     *
     * @param view 要判断可见性的视图。
     * @return 如果视图隐藏，则返回 `true`；否则返回 `false`。
     */
    @JvmStatic
    fun isGone(view: View): Boolean {
        return view.visibility == View.GONE
    }

    /**
     * 判断视图是否不可见。
     *
     * @param view 要判断可见性的视图。
     * @return 如果视图不可见，则返回 `true`；否则返回 `false`。
     */
    @JvmStatic
    fun isInvisible(view: View): Boolean {
        return view.visibility == View.INVISIBLE
    }

    /**
     * 判断 ViewGroup 及其所有子视图是否全部可见。
     *
     * @param view 要判断可见性的 ViewGroup。
     * @return 如果 ViewGroup 及其所有子视图全部可见，则返回 `true`；否则返回 `false`。
     */
    @JvmStatic
    fun isVisibleAll(view: View): Boolean {
        val resultList = traverseVisibilityAll(view)
        return !resultList.contains(View.GONE) && !resultList.contains(View.INVISIBLE)
    }

    /**
     * 判断 ViewGroup 及其所有子视图是否全部隐藏。
     *
     * @param view 要判断可见性的 ViewGroup。
     * @return 如果 ViewGroup 及其所有子视图全部隐藏，则返回 `true`；否则返回 `false`。
     */
    @JvmStatic
    fun isGoneAll(view: View): Boolean {
        val resultList = traverseVisibilityAll(view)
        return resultList.contains(View.GONE) && !resultList.contains(View.VISIBLE)
    }

    /**
     * 判断 ViewGroup 及其所有子视图是否全部不可见。
     *
     * @param viewGroup 要判断可见性的 ViewGroup。
     * @return 如果 ViewGroup 及其所有子视图全部不可见，则返回 `true`；否则返回 `false`。
     */
    @JvmStatic
    fun isInvisibleAll(view: View): Boolean {
        val resultList = traverseVisibilityAll(view)
        return !resultList.contains(View.GONE) && resultList.contains(View.INVISIBLE)
    }

    /**
     * 遍历给定视图（包括其所有子视图）的可见性状态，并返回一个包含所有可见性状态的列表。
     * 如果给定的视图是一个视图组，则会遍历其所有子视图（包括嵌套的视图组）。
     * 如果给定的视图不是一个视图组，则只会返回该视图的可见性状态。
     *
     * @param view 需要遍历的视图。
     * @return 包含所有遍历到的视图的可见性状态的列表。
     */
    private fun traverseVisibilityAll(view: View): List<Int> {
        // 创建一个用于存储所有视图可见性状态的列表
        val resultList = mutableListOf<Int>()

        // 创建一个堆栈，用于存储待处理的视图
        val stack = ArrayDeque<View>()
        stack.addFirst(view)  // 首先添加给定的视图

        // 当堆栈不为空时，继续处理视图
        while (stack.isNotEmpty()) {
            // 从堆栈中取出一个视图
            val current = stack.removeFirst()
            // 添加当前视图的可见性状态到结果列表中
            resultList.add(current.visibility)

            // 如果当前视图是一个视图组，则遍历其所有子视图
            if (current is ViewGroup) {
                for (i in 0 until current.childCount) {
                    val child = current.getChildAt(i)
                    // 将子视图添加到堆栈中，以便后续处理
                    stack.addFirst(child)
                }
            }
        }
        // 返回包含所有视图可见性状态的列表
        return resultList
    }


    //
    /**
     * 设置视图及其所有子视图的启用状态。
     *
     * @param view 需要设置的视图。这个视图及其所有子视图的启用状态都会被改变。
     * @param enabled 新的启用状态。如果为 true，视图及其所有子视图将被启用；如果为 false，它们将被禁用。
     */
    fun setEnabledAll(view: View, enabled: Boolean) {
        // 创建一个双端队列来存储待处理的视图
        val stack = ArrayDeque<View>()
        // 首先添加传入的视图到队列
        stack.addFirst(view)
        // 当队列不为空时，继续处理
        while (stack.isNotEmpty()) {
            // 从队列首部移除并获取一个视图
            val currentView = stack.removeFirst()
            // 设置该视图的启用状态
            currentView.isEnabled = enabled
            // 如果当前视图是一个视图组（即，它有子视图）
            if (currentView is ViewGroup) {
                // 遍历并添加所有子视图到队列，以便之后处理
                for (i in 0 until currentView.childCount) {
                    stack.addFirst(currentView.getChildAt(i))
                }
            }
        }
    }

    /**
     * 判断给定的 ViewGroup 及其所有子视图是否全部可用。
     *
     * @param view 要判断的 ViewGroup。
     * @return 是否全部可用，如果全部可用返回 true，否则返回 false。
     */
    @JvmStatic
    fun isEnabledAll(view: View): Boolean {
        // 创建一个视图堆栈
        val stack = Stack<View>()
        // 将传入的视图压入堆栈
        stack.push(view)

        // 当堆栈不为空时，继续执行循环
        while (stack.isNotEmpty()) {
            // 弹出堆栈顶部的视图
            val currentView = stack.pop()

            // 如果当前视图处于禁用状态，返回 false
            if (!currentView.isEnabled) {
                return false
            }

            // 如果当前视图是一个视图组（即可以包含其他视图的视图）
            if (currentView is ViewGroup) {
                // 遍历该视图组中的所有子视图，并将它们压入堆栈
                for (i in 0 until currentView.childCount) {
                    stack.push(currentView.getChildAt(i))
                }
            }
        }

        // 如果所有视图都处于启用状态，返回 true
        return true
    }


    //
    /**
     * 获取视图的 ID 的十六进制表示形式
     *
     * @param view 目标视图
     * @return 视图 ID 的十六进制字符串表示形式，如果视图的 ID 为 View.NO_ID，则返回字符串 "${View.NO_ID}"
     */
    @JvmStatic
    fun getIdHex(view: View): String {
        return try {
            if (view.id == View.NO_ID) {
                "${View.NO_ID}"
            } else {
                "0x${Integer.toHexString(view.id)}"
            }
        } catch (e: Exception) {
            "${View.NO_ID}"
        }
    }

    /**
     * 获取视图的 ID 的资源名称
     *
     * @param view 目标视图
     * @return 视图 ID 的资源名称，如果视图的 ID 为 View.NO_ID，则返回字符串 "${View.NO_ID}"
     */
    @JvmStatic
    fun getIdName(view: View): String {
        return try {
            if (view.id == View.NO_ID) {
                "${View.NO_ID}"
            } else {
                "@id/${view.resources.getResourceEntryName(view.id)}"
            }
        } catch (e: Exception) {
            "${View.NO_ID}"
        }
    }

    /**
     * 获取指定资源 ID 的资源名称
     *
     * @param context 上下文对象
     * @param resId 目标资源 ID
     * @return 资源 ID 的资源名称，如果资源 ID 为 View.NO_ID，则返回字符串 "${View.NO_ID}"
     */
    @JvmStatic
    fun getIdName(context: Context, @IdRes resId: Int): String {
        return try {
            if (resId == View.NO_ID) {
                "${View.NO_ID}"
            } else {
                "@id/${context.resources.getResourceEntryName(resId)}"
            }
        } catch (e: Exception) {
            "${View.NO_ID}"
        }
    }


    //
    /**
     * 获取 View 的点击事件监听器。
     *
     * @param view 要获取点击事件监听器的 View
     * @return View.OnClickListener 对象，如果不存在则返回 null
     */
    @JvmStatic
    fun <T : View> getOnClickListener(view: T): View.OnClickListener? {
        try {
            val listenerInfo = getListenerInfo(view) ?: return null
            val mOnClickListenerField = listenerInfo.javaClass.getDeclaredField("mOnClickListener")
            mOnClickListenerField.isAccessible = true
            val onClickListener = mOnClickListenerField.get(listenerInfo)
            return if (onClickListener is View.OnClickListener) onClickListener else null
        } catch (e: NoSuchFieldException) {
            e.printStackTrace() // 捕获 NoSuchFieldException 异常并打印异常信息
        } catch (e: IllegalAccessException) {
            e.printStackTrace() // 捕获 IllegalAccessException 异常并打印异常信息
        } catch (e: Exception) {
            e.printStackTrace() // 捕获其他异常并打印异常信息
        }
        return null
    }

    /**
     * 获取 View 的长按事件监听器。
     *
     * @param view 要获取长按事件监听器的 View
     * @return View.OnLongClickListener 对象，如果不存在则返回 null
     */
    @JvmStatic
    fun <T : View> getOnLongClickListener(view: T): View.OnLongClickListener? {
        try {
            val listenerInfo = getListenerInfo(view) ?: return null
            val mOnLongClickListenerField =
                listenerInfo.javaClass.getDeclaredField("mOnLongClickListener")
            mOnLongClickListenerField.isAccessible = true
            val onLongClickListener = mOnLongClickListenerField.get(listenerInfo)
            return if (onLongClickListener is View.OnLongClickListener) onLongClickListener else null
        } catch (e: NoSuchFieldException) {
            e.printStackTrace() // 捕获 NoSuchFieldException 异常并打印异常信息
        } catch (e: IllegalAccessException) {
            e.printStackTrace() // 捕获 IllegalAccessException 异常并打印异常信息
        } catch (e: Exception) {
            e.printStackTrace() // 捕获其他异常并打印异常信息
        }
        return null
    }

    /**
     * 获取 View 的触摸事件监听器。
     *
     * @param view 要获取触摸事件监听器的 View
     * @return View.OnTouchListener 对象，如果不存在则返回 null
     */
    @JvmStatic
    fun <T : View> getOnTouchListener(view: T): View.OnTouchListener? {
        try {
            val listenerInfo = getListenerInfo(view) ?: return null
            val mOnTouchListenerField = listenerInfo.javaClass.getDeclaredField("mOnTouchListener")
            mOnTouchListenerField.isAccessible = true
            val onTouchListener = mOnTouchListenerField.get(listenerInfo)
            return if (onTouchListener is View.OnTouchListener) onTouchListener else null
        } catch (e: NoSuchFieldException) {
            e.printStackTrace() // 捕获 NoSuchFieldException 异常并打印异常信息
        } catch (e: IllegalAccessException) {
            e.printStackTrace() // 捕获 IllegalAccessException 异常并打印异常信息
        } catch (e: Exception) {
            e.printStackTrace() // 捕获其他异常并打印异常信息
        }
        return null
    }

    /**
     * 获取 View 的 ListenerInfo 对象，用于访问 View 的点击、长按和触摸事件监听器。
     *
     * @param view 要获取 ListenerInfo 对象的 View
     * @return ListenerInfo 对象，如果不存在则返回 null
     */
    private fun <T : View> getListenerInfo(view: T): Any? {
        try {
            val mListenerInfoField = findFieldRecursiveImpl(view::class.java, "mListenerInfo")
            mListenerInfoField?.isAccessible = true
            return mListenerInfoField?.get(view)
        } catch (e: NoSuchFieldException) {
            e.printStackTrace() // 捕获 NoSuchFieldException 异常并打印异常信息
        } catch (e: IllegalAccessException) {
            e.printStackTrace() // 捕获 IllegalAccessException 异常并打印异常信息
        }
        return null
    }

    /**
     * 递归查找指定类及其父类中的字段。
     *
     * @param clazz 要查找字段的类
     * @param fieldName 字段名
     * @return 找到的字段对象，如果未找到则抛出 NoSuchFieldException 异常
     * @throws NoSuchFieldException 如果未找到字段则抛出 NoSuchFieldException 异常
     */
    @Throws(NoSuchFieldException::class)
    private fun findFieldRecursiveImpl(clazz: Class<*>, fieldName: String): Field? {
        var currentClass: Class<*>? = clazz
        return try {
            currentClass?.getDeclaredField(fieldName)
        } catch (e: NoSuchFieldException) {
            while (true) {
                currentClass = currentClass?.superclass
                if (currentClass == null || currentClass == Any::class.java) break
                try {
                    return currentClass.getDeclaredField(fieldName)
                } catch (ignored: NoSuchFieldException) {
                }
            }
            throw e
        }
    }


    //
    /**
     * 将某个View直接转换为Bitmap
     * @param view 被操作的View
     */
    @JvmStatic
    fun viewToBitmap(view: View): Bitmap {
        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }

    /**
     * 判断是否为快速点击
     * @url https://zhuanlan.zhihu.com/p/34841081
     *
     * @param interval 自定义点击间隔时间。
     * @return 如果两次点击时间间隔小于指定的点击间隔，则返回true; 否则返回false。
     */
    @JvmStatic
    @JvmOverloads
    fun isFastClick(interval: Long = 200L): Boolean {
        if (fastClickRecords.size > 1000)
            fastClickRecords.clear()

        // 本方法被调用的文件名和行号作为标记
        val ste = Throwable().stackTrace[1]
        val key = ste.fileName + ste.lineNumber
        var lastClickTime = fastClickRecords[key]
        val thisClickTime = System.currentTimeMillis()
        fastClickRecords[key] = thisClickTime
        lastClickTime = lastClickTime ?: 0
        val timeDuration = thisClickTime - lastClickTime
        return timeDuration < interval
    }

    /**
     * 代理某个View的点击事件，避免快速点击
     * @param view 目标View。
     * @param interval 间隔时间, 毫秒。
     * @param l 需要响应的点击事件
     */
    @JvmStatic
    @JvmOverloads
    fun setEnhanceOnClickListener(view: View, interval: Long = 200L, l: View.OnClickListener) {
        view.setOnClickListener {
            if (isFastClick(interval))
                return@setOnClickListener

            l.onClick(it)
        }
    }


    //
    /**
     * 深度遍历 View，获取所有的 View（包括 View 本身和其所有子 View）
     *
     * @param view 需要遍历的 View
     * @return 所有的 View 列表
     */
    @JvmStatic
    fun deepViewGroup(view: View): Sequence<View> {
        val views = mutableListOf<View>()
        val stack = Stack<View>()
        stack.push(view)

        while (!stack.isEmpty()) {
            val current = stack.pop()
            views.add(current)
            if (current is ViewGroup) {
                // 遍历当前 ViewGroup 的所有子 View
                for (i in current.childCount - 1 downTo 0) {
                    stack.push(current.getChildAt(i))
                }
            }
        }

        return views.asSequence()
    }

    /**
     * 遍历 View 及其子 View。
     *
     * @param view 需要遍历的 View
     * @param block 遍历回调函数，接收一个 View 参数
     */
    @JvmStatic
    fun forEachChild(view: View, block: ViewFunction) {
        if (view !is ViewGroup) {
            block.callView(view)
            return
        }

        val stack = Stack<View>()
        stack.push(view)
        while (!stack.isEmpty()) {
            val current = stack.pop()
            block.callView(current)
            if (current is ViewGroup) {
                for (i in current.childCount - 1 downTo 0) {
                    stack.push(current.getChildAt(i))
                }
            }
        }
    }

    /**
     * 条件遍历 View 及其子 View。
     *
     * @param view 需要遍历的 View
     * @param block 遍历回调函数，接收一个 View 参数，返回一个 Boolean 如果为 `true` 则直接从树遍历中结束
     */
    @JvmStatic
    fun forEachWhereChild(view: View, block: ViewWhereFunction) {
        if (view !is ViewGroup) {
            block.callView(view)
            return
        }

        val stack = Stack<View>()
        stack.push(view)
        while (!stack.isEmpty()) {
            val current = stack.pop()
            if (block.callView(current)) {
                return
            }
            if (current is ViewGroup) {
                for (i in current.childCount - 1 downTo 0) {
                    stack.push(current.getChildAt(i))
                }
            }
        }
    }

    /**
     * 遍历给定视图的所有父视图，并对每个指定类型的父视图执行一个操作。
     *
     * @param view 要开始遍历的视图。
     * @param block 对每个指定类型的父视图执行的操作。这个操作接受一个父视图作为参数，直到它的为null为止
     */
    @JvmStatic
    fun forEachParent(view: View, block: ViewFunction) {
        var parent: ViewParent? = view.parent
        while (parent != null) {
            if (parent is View) {
                block.callView(parent)
            }
            parent = parent.parent
        }
    }

    /**
     * 遍历给定视图的所有父视图，并对每个指定类型的父视图执行一个操作。
     *
     * @param view 要开始遍历的视图。
     * @param block 对每个指定类型的父视图执行的操作。这个操作接受一个父视图作为参数，并返回一个布尔值。
     *              如果这个布尔值为 true，那么遍历将立即停止。否则，遍历将继续。
     */
    @JvmStatic
    fun forEachWhereParent(view: View, block: ViewWhereFunction) {
        var parent: ViewParent? = view.parent
        while (parent != null) {
            if (parent is View && block.callView(parent)) {
                return
            }
            parent = parent.parent
        }
    }

    /**
     * 用指定的视图替换当前视图。
     *
     * @param view 用于替换当前视图的新视图。
     * @return 如果替换成功，返回true；如果当前视图没有父视图或者在父视图中找不到当前视图，返回false。
     *
     * 注意：这个方法只会复制当前视图的ID和布局参数到新视图，如果当前视图有其他状态需要保留，需要手动进行复制。
     */
    @JvmStatic
    fun replaceWith(view: View, newView: View): Boolean {
        val parentView = view.parent?.asOrNull<ViewGroup>() ?: return false
        val indexOfChild = parentView.indexOfChild(view)
        return if (indexOfChild != -1) {
            parentView.removeViewAt(indexOfChild)
            newView.apply {
                id = view.id
                layoutParams = view.layoutParams
            }
            parentView.addView(newView, indexOfChild)
            true
        } else {
            false
        }
    }

    /**
     * 获取相对于当前View的某个位置的兄弟View。
     *
     * @param relativeIndex 相对于当前View的位置索引。正数表示当前View后面的兄弟View，负数表示当前View前面的兄弟View。
     * @return 如果存在位于relativeIndex位置的兄弟View，则返回该View，否则返回null。
     */
    @JvmStatic
    fun getSiblingViewAt(view: View, relativeIndex: Int): View? {
        val parent = view.parent?.asOrNull<ViewGroup>() ?: return null
        val indexOfChild = parent.indexOfChild(view)
        val targetIndex = indexOfChild + relativeIndex
        return if (targetIndex in 0 until parent.childCount) {
            parent[targetIndex]
        } else {
            null
        }
    }

    /**
     * 将View转换为JSON字符串表示。
     *
     * @param view 要转换的View
     * @return 转换后的JSON字符串
     */
    @JvmStatic
    @JvmOverloads
    fun toViewJson(view: View, indentSpaces: Int = 0): String {
        if (view !is ViewGroup) return getViewJsonItem(0, view).toString()

        // 创建根JSONObject
        val rootJson = getViewJsonItem(0, view)
        val jsonArray = JSONArray()
        rootJson.put("children", jsonArray)
        // 创建存放JSONArray的栈
        val jsonArrayStack = Stack<JSONArray>()
        jsonArrayStack.push(jsonArray)

        // 创建存放ViewGroup的栈
        val viewStack = Stack<ViewGroup>()
        viewStack.push(view)

        // 循环处理ViewGroup及其子View
        while (!viewStack.isEmpty()) {
            // 弹出当前的JSONArray和ViewGroup
            val currentJsonArray = jsonArrayStack.pop()
            val currentViewGroup = viewStack.pop()

            // 遍历子View
            val childCount = currentViewGroup.childCount
            for (i in 0 until childCount) {
                val child = currentViewGroup.getChildAt(i)
                // 创建子View的JSONObject
                val jsonObject = getViewJsonItem(i, child)

                if (child is ViewGroup) {
                    // 如果子View是ViewGroup，则创建子View的JSONArray，并将子View的JSONObject添加到当前的JSONArray中
                    val childJsonArray = JSONArray()
                    jsonObject.put("children", childJsonArray)
                    currentJsonArray.put(jsonObject)
                    // 将子View的JSONArray入栈，将子ViewGroup入栈
                    jsonArrayStack.push(childJsonArray)
                    viewStack.push(child)
                } else {
                    // 如果子View不是ViewGroup，则直接将子View的JSONObject添加到当前的JSONArray中
                    currentJsonArray.put(jsonObject)
                }
            }
        }

        // 返回根JSONObject的字符串表示
        return if (indentSpaces == 0) {
            return rootJson.toString()
        } else {
            rootJson.toString(indentSpaces)
        }
    }

    /**
     * 获取View的JSONObject表示。
     *
     * @param view 要转换的View
     * @return View的JSONObject表示
     */
    private fun getViewJsonItem(index: Int, view: View): JSONObject {
        val jsonObject = JSONObject()
        val pkg = view.javaClass.`package`
        jsonObject.put("index", index)
        jsonObject.put("className", view.javaClass.name)
        jsonObject.put("package", "${pkg?.name}")
        jsonObject.put("superClass", view.javaClass.superclass.name)
        jsonObject.put("id", view.id)
        jsonObject.put("idHex", getIdHex(view))
        jsonObject.put("idName", getIdName(view))
        jsonObject.put("context", "${view.context}")
        jsonObject.put("width", "${view.context.px2dip(view.width.toFloat())}")
        jsonObject.put("height", "${view.context.px2dip(view.height.toFloat())}")
        jsonObject.put("descr", "${view.contentDescription}")
        jsonObject.put("alpha", "${view.alpha}")
        jsonObject.put("paddingLTRB", view.paddingToString())
        jsonObject.put("marginLTRB", view.marginToString())
        jsonObject.put("visibility", view.visibilityToString())

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            jsonObject.put("foreground", "${view.foreground?.simpleString()}")
        }

        jsonObject.put("background", "${view.background?.simpleString()}")

        // 文本框
        if (view is TextView) {
            jsonObject.put("text", "${view.text}")
            jsonObject.put("hint", "${view.hint}")
            jsonObject.put("textSize", "${view.context.px2dip(view.textSize)}dp")
            view.typeface?.let {
                if (it.isBold) {
                    jsonObject.put("textStyle", "bold")
                } else if (it.isItalic) {
                    jsonObject.put("textStyle", "italic")
                } else {
                    jsonObject.put("textStyle", "normal")
                }
            }
            jsonObject.put("currentTextColor", KColorUtils.colorIntToHex(view.currentTextColor))
            jsonObject.put(
                "currentHintTextColor",
                KColorUtils.colorIntToHex(view.currentHintTextColor)
            )
            jsonObject.put("highlightColor", KColorUtils.colorIntToHex(view.highlightColor))
            jsonObject.put("selectable", view.isTextSelectable)
            jsonObject.put("minHeight", view.minHeight)
            jsonObject.put("maxHeight", view.maxHeight)
            jsonObject.put("lineHeight", view.lineHeight)
            jsonObject.put("minLines", view.minLines)
            jsonObject.put("maxLines", view.maxLines)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                jsonObject.put("singleLine", view.isSingleLine)
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            jsonObject.put("tooltipText", "${view.tooltipText}")
        }

        jsonObject.put("enabled", "${view.isEnabled}")
        jsonObject.put("pressed", "${view.isPressed}")
        jsonObject.put("hovered", "${view.isHovered}")
        jsonObject.put("focusable", "${view.isFocusable}")
        jsonObject.put("focused", "${view.isFocused}")
        jsonObject.put("selected", "${view.isSelected}")
        jsonObject.put("clickable", "${view.isClickable}")
        jsonObject.put("longClickable", "${view.isLongClickable}")

        // 事件
        val clickListener = getOnClickListener(view)
        if (clickListener != null) {
            jsonObject.put("onClickListener", clickListener)
        }
        val longClickListener = getOnLongClickListener(view)
        if (longClickListener != null) {
            jsonObject.put("longClickListener", longClickListener)
        }
        val onTouchListener = getOnTouchListener(view)
        if (onTouchListener != null) {
            jsonObject.put("onTouchListener", onTouchListener)
        }

        // 列表类适配器
        if (view is ViewPager) {
            jsonObject.put("ViewPagerAdapter", "${view.adapter}")
            jsonObject.put("ViewPagerCurrentItem", "${view.currentItem}")
        }
        if (view is ListView) {
            jsonObject.put("ListViewAdapter", "${view.adapter}")
        }
        if (view is RecyclerView) {
            jsonObject.put("RecyclerViewAdapter", "${view.adapter}")
        }

        jsonObject.put("tag", "${view.tag}")
        jsonObject.put("viewPosition", "[${view.x}, ${view.y}]")
        jsonObject.put("pivotPosition", "[${view.pivotX}, ${view.pivotY}]")
        jsonObject.put("rotationPosition", "[${view.rotationX}, ${view.rotationY}]")

        jsonObject.put("locationOnScreen", view.locationOnScreen.toShortString())
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            jsonObject.put("locationInSurface", view.locationInSurface.toShortString())
        }
        jsonObject.put("locationInWindow", view.locationInWindow.toShortString())
        jsonObject.put("rect", view.positionRect.toShortString())
        return jsonObject
    }
}