package com.freegang.ktutils.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Point
import android.graphics.PointF
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.ShapeDrawable
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.view.ViewParent
import android.widget.ListView
import android.widget.TextView
import androidx.annotation.IdRes
import androidx.core.view.marginBottom
import androidx.core.view.marginEnd
import androidx.core.view.marginStart
import androidx.core.view.marginTop
import androidx.core.view.postDelayed
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.freegang.ktutils.color.KColorUtils
import com.freegang.ktutils.display.px2dip
import com.freegang.ktutils.extension.asOrNull
import org.json.JSONArray
import org.json.JSONObject
import java.lang.reflect.Field
import java.util.*
import kotlin.collections.ArrayDeque

object KViewUtils {

    /**
     * 设置 ViewGroup 及其所有子视图的可见性为 [View.VISIBLE]，即全部显示。
     *
     * @param viewGroup 要设置可见性的 ViewGroup。
     */
    @JvmStatic
    fun showAll(view: View) {
        setVisibilityAll(view, View.VISIBLE)
    }

    /**
     * 设置 ViewGroup 及其所有子视图的可见性为 [View.GONE]，即全部隐藏。
     *
     * @param viewGroup 要设置可见性的 ViewGroup。
     */
    @JvmStatic
    fun hideAll(view: View) {
        setVisibilityAll(view, View.GONE)
    }

    /**
     * 设置 ViewGroup 及其所有子视图的可见性为 [View.INVISIBLE]，即全部不可见。
     *
     * @param viewGroup 要设置可见性的 ViewGroup。
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
     * @param viewGroup 要判断可见性的 ViewGroup。
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
     * @param viewGroup 要判断可见性的 ViewGroup。
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

    //
    /**
     * 遍历 View 及其子 View
     *
     * @param view 需要遍历的 View
     * @param block 遍历回调函数，接收一个 View 参数
     */
    @JvmStatic
    fun traverse(view: View, block: (View) -> Unit) {
        if (view !is ViewGroup) {
            block.invoke(view)
            return
        }

        val stack = Stack<View>()
        stack.push(view)
        while (!stack.isEmpty()) {
            val current = stack.pop()
            block.invoke(current)
            if (current is ViewGroup) {
                for (i in current.childCount - 1 downTo 0) {
                    stack.push(current.getChildAt(i))
                }
            }
        }
    }

    /**
     * 深度遍历 View，获取所有的 View（包括 View 本身和其所有子 View）
     *
     * @param view 需要遍历的 View
     * @return 所有的 View 列表
     */
    @JvmStatic
    fun deepViewGroup(view: View): List<View> {
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

        return views
    }

    /**
     * 获取某个View树中所有指定类型的 ChildView
     * @param view View
     * @param targetType 某个View类型, 可以是 View.class
     * @return List<T> T extends View
     */
    @JvmStatic
    fun <T : View> findViews(
        view: View,
        targetType: Class<T>,
    ): List<T> {
        return findViewsExact(view, targetType) { true }
    }

    /**
     * 获取某个View树中所有指定类型的 ChildView
     * 并且该ChildView的[contentDescription]值包含了指定正则表达式[containsDesc]中的内容
     * @param view View
     * @param targetType 某个View类型, 可以是 View.class
     * @param containsDesc 指定正则表达式
     * @return List<T> T extends View
     */
    @JvmStatic
    fun <T : View> findViewsByDesc(
        view: View,
        targetType: Class<T>,
        containsDesc: Regex
    ): List<T> {
        return findViewsExact(view, targetType) {
            val desc = it.contentDescription ?: ""
            desc.contains(containsDesc)
        }
    }

    /**
     * 获取某个View树中所有指定类型的 ChildView
     * 并且该ChildView的[contentDescription]值包含了指定文本[containsDesc]中的内容
     * @param view View
     * @param targetType 某个View类型, 可以是 View.class
     * @param containsDesc 指定文本
     * @return List<T> T extends View
     */
    @JvmStatic
    fun <T : View> findViewsByDesc(
        view: View,
        targetType: Class<T>,
        containsDesc: String,
        ignoreCase: Boolean = false,
    ): List<T> {
        return findViewsExact(view, targetType) {
            val desc = it.contentDescription ?: ""
            desc.contains(containsDesc, ignoreCase)
        }
    }

    /**
     * 获取某个View视图树中所有指定类型的 ChildView
     * 并且该ChildView的[idName]等于指定的[idName]
     * @param view View
     * @param targetType 某个View类型, 可以是 View.class
     * @param idName idName 举例: @id/textView
     * @return List<T> T extends View
     */
    @JvmStatic
    fun <T : View> findViewsByIdName(
        view: View,
        targetType: Class<T>,
        idName: String
    ): List<T> {
        return findViewsExact(view, targetType) { getIdName(it) == idName }
    }

    /**
     * 获取某个View视图树中所有满足指定逻辑的ChildView
     * @param view View
     * @param targetType 某个View类型, 可以是 View.class
     * @param block 回调方法, 该方法参数会传入所有被遍历的view, 返回一个[Boolean]
     * @return List<T> T extends View
     */
    @JvmStatic
    fun <T : View> findViewsExact(
        view: View,
        targetType: Class<T>,
        block: (T) -> Boolean
    ): List<T> {
        if (view !is ViewGroup) {
            if (targetType.isInstance(view)) {
                val cast = targetType.cast(view)!!
                if (block.invoke(cast)) {
                    return listOf(cast)
                }
            }
            return emptyList()
        }
        val views = mutableListOf<T>()
        val stack = Stack<View>()
        stack.push(view)
        while (!stack.isEmpty()) {
            val current = stack.pop()
            if (targetType.isInstance(current)) {
                val cast = targetType.cast(current)!! // child 应该不存在null吧
                if (block.invoke(cast)) {
                    views.add(cast)
                }
            }
            if (current is ViewGroup) {
                for (i in current.childCount - 1 downTo 0) {
                    stack.push(current.getChildAt(i))
                }
            }
        }
        return views
    }

    /**
     * 获取某个View指定类型的 父View
     * @param view 被获取父类的 View
     * @param targetType 被指定的父类
     * @param deep 回退深度, 默认找到第1个
     * @param T 父View实例
     */
    @JvmStatic
    fun <T : View> findParentExact(
        view: View,
        targetType: Class<T>,
        deep: Int = 1,
    ): T? {
        var deepCount = deep
        var parent: ViewParent? = view.parent
        while (parent != null) {
            if (targetType.isInstance(parent)) deepCount -= 1
            if (deepCount == 0) return targetType.cast(parent)
            parent = parent.parent
        }

        return null
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
        val rootJsonObject = getViewJsonItem(0, view)
        val jsonArray = JSONArray()
        rootJsonObject.put("children", jsonArray)
        // 创建存放JSONArray的栈
        val jsonStack = Stack<JSONArray>()
        jsonStack.push(jsonArray)

        // 创建存放ViewGroup的栈
        val viewStack = Stack<ViewGroup>()
        viewStack.push(view)

        // 循环处理ViewGroup及其子View
        while (!viewStack.isEmpty()) {
            // 弹出当前的JSONArray和ViewGroup
            val currentJSONArray = jsonStack.pop()
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
                    currentJSONArray.put(jsonObject)
                    // 将子View的JSONArray入栈，将子ViewGroup入栈
                    jsonStack.push(childJsonArray)
                    viewStack.push(child)
                } else {
                    // 如果子View不是ViewGroup，则直接将子View的JSONObject添加到当前的JSONArray中
                    currentJSONArray.put(jsonObject)
                }
            }
        }

        // 返回根JSONObject的字符串表示
        return if (indentSpaces == 0) {
            return rootJsonObject.toString()
        } else {
            rootJsonObject.toString(indentSpaces)
        }
    }

    /**
     * 获取View的JSONObject表示。
     *
     * @param view 要转换的View
     * @return View的JSONObject表示
     */
    private fun getViewJsonItem(
        index: Int,
        view: View,
    ): JSONObject {
        val jsonObject = JSONObject()
        val pkg = view.javaClass.`package`
        jsonObject.put("index", index)
        jsonObject.put("className", view.javaClass.name)
        jsonObject.put("package", pkg?.name ?: "")
        jsonObject.put("superClass", view.javaClass.superclass.name)
        jsonObject.put("id", view.id)
        jsonObject.put("idHex", getIdHex(view))
        jsonObject.put("idName", getIdName(view))
        jsonObject.put("context", "${view.context}")
        jsonObject.put("width", "${view.context.px2dip(view.width.toFloat())}")
        jsonObject.put("height", "${view.context.px2dip(view.height.toFloat())}")
        jsonObject.put("descr", "${view.contentDescription}")
        jsonObject.put("alpha", "${view.alpha}")

        val paddingStartDp = view.context.px2dip(view.paddingStart.toFloat())
        val paddingTopDp = view.context.px2dip(view.paddingTop.toFloat())
        val paddingEndDp = view.context.px2dip(view.paddingEnd.toFloat())
        val paddingBottomDp = view.context.px2dip(view.paddingBottom.toFloat())
        jsonObject.put(
            "paddingLTRB",
            "[${paddingStartDp}dp, ${paddingTopDp}dp, ${paddingEndDp}dp, ${paddingBottomDp}dp]"
        )

        val marginStartDp = view.context.px2dip(view.marginStart.toFloat())
        val marginTopDp = view.context.px2dip(view.marginTop.toFloat())
        val marginEndDp = view.context.px2dip(view.marginEnd.toFloat())
        val marginBottomDp = view.context.px2dip(view.marginBottom.toFloat())
        jsonObject.put(
            "marginLTRB",
            "[${marginStartDp}dp, ${marginTopDp}dp, ${marginEndDp}dp, ${marginBottomDp}dp]"
        )

        when (view.visibility) {
            View.VISIBLE -> jsonObject.put("visibility", "VISIBLE")
            View.GONE -> jsonObject.put("visibility", "GONE")
            View.INVISIBLE -> jsonObject.put("visibility", "INVISIBLE")
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val foreground = view.foreground
            if (foreground is ColorDrawable) {
                jsonObject.put("foreground", "ColorDrawable(${KColorUtils.colorIntToHex(foreground.color)})")
            } else {
                jsonObject.put("foreground", "$foreground")
            }
        }
        val background = view.background
        if (background is ColorDrawable) {
            jsonObject.put(
                "background",
                "ColorDrawable(color: ${KColorUtils.colorIntToHex(background.color)})"
            )
        } else if (background is ShapeDrawable) {
            jsonObject.put(
                "background",
                "ShapeDrawable(color: ${KColorUtils.colorIntToHex(background.paint.color)})"
            )
        } else {
            jsonObject.put("background", "$background")
        }

        // 文本框
        if (view is TextView) {
            jsonObject.put("text", "${view.text}")
            jsonObject.put("hint", "${view.hint}")
            jsonObject.put("textSize", "${view.context.px2dip(view.textSize)}dp")
            if (view.typeface.isBold) {
                jsonObject.put("textStyle", "bold")
            } else if (view.typeface.isItalic) {
                jsonObject.put("textStyle", "italic")
            } else {
                jsonObject.put("textStyle", "normal")
            }
            jsonObject.put("currentTextColor", KColorUtils.colorIntToHex(view.currentTextColor))
            jsonObject.put("currentHintTextColor", KColorUtils.colorIntToHex(view.currentHintTextColor))
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
        // 点击事件
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
        jsonObject.put("viewPosition", "${view.x}, ${view.y}")
        jsonObject.put("pivotPosition", "${view.pivotX}, ${view.pivotY}")
        jsonObject.put("rotationPosition", "${view.rotationX}, ${view.rotationY}")
        val outOnScreen = IntArray(2) { 0 }
        view.getLocationOnScreen(outOnScreen)
        jsonObject.put("locationOnScreen", "${outOnScreen[0]}, ${outOnScreen[1]}")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val outInSurface = IntArray(2) { 0 }
            view.getLocationInSurface(outOnScreen)
            jsonObject.put("locationInSurface", "${outInSurface[0]}, ${outInSurface[1]}")
        }
        val outInWindow = IntArray(2) { 0 }
        view.getLocationInWindow(outInWindow)
        jsonObject.put("locationInWindow", "${outInWindow[0]}, ${outInWindow[1]}")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val outRect = Rect()
            view.getClipBounds(outRect)
            jsonObject.put("clipBounds", outRect.toShortString())
        } else {
            jsonObject.put("clipBounds", "${view.clipBounds?.toShortString()}")
        }
        jsonObject.put("rect", Rect(view.left, view.top, view.right, view.bottom).toShortString())
        return jsonObject
    }


    /**
     * 构建View节点树
     * @param view ViewGroup
     * @return ViewNode viewGroup作为根节点
     */
    @JvmStatic
    fun buildViewTree(view: View): ViewNode {
        if (view !is ViewGroup) return ViewNode(null, view, 0, mutableListOf())

        val root = ViewNode(view, view, 0, mutableListOf())
        val stack = Stack<ViewNode>()
        stack.push(root)
        while (!stack.isEmpty()) {
            val current = stack.pop()
            val currentView = current.view
            if (currentView is ViewGroup) {
                for (i in currentView.childCount - 1 downTo 0) {
                    val child = currentView.getChildAt(i)
                    val childNode = ViewNode(currentView, child, current.depth + 1, mutableListOf())
                    current.children.add(0, childNode) // 添加到当前节点的子节点列表头部
                    stack.push(childNode)
                }
            }
        }
        return root
    }

    data class ViewNode(
        var parent: ViewGroup? = null,  // 父视图
        var view: View? = null,  // 当前视图
        var depth: Int = 1,  // 当前树的深度
        var children: MutableList<ViewNode> = mutableListOf(), // 子节点
    ) {

        // 销毁当前节点下的所有视图树
        fun destroy() {
            destroy(this)
        }

        private fun destroy(node: ViewNode) {
            val stack = Stack<ViewNode>()
            stack.push(node)
            while (!stack.isEmpty()) {
                val current = stack.pop()
                for (childNode in current.children) {
                    stack.push(childNode)
                }
                current.parent = null
                current.children.clear()
                current.view = null
            }
        }

        // 当前节点下的所有视图树深度遍历到字符串
        fun deepToString(
            indent: Int = 4,
            format: (ViewNode) -> String = { it.toString() },
        ): String {
            val buffer = StringBuffer()
            deepChildren("|-", indent, this) { trunk, node ->
                buffer.append("$trunk${format(node)}\n")
            }
            return buffer.toString()
        }

        fun deepChildren(
            trunk: String = "|-",
            indent: Int = 4,
            node: ViewNode,
            block: (trunk: String, node: ViewNode) -> Unit
        ) {
            var indentTrunk = ""
            for (i in 0 until indent) indentTrunk += "-"

            val stack = Stack<Pair<ViewNode, Int>>()
            stack.push(Pair(node, 0))
            while (!stack.isEmpty()) {
                val (current, level) = stack.pop()
                val currentTrunk = if (level == 0) trunk else trunk + indentTrunk.repeat(level)
                block.invoke(currentTrunk, current)
                val size = current.children.size
                for (i in size - 1 downTo 0) {
                    stack.push(Pair(current.children[i], level + 1))
                }
            }
        }

        override fun toString(): String {
            val view = view
                ?: return "ViewNode{parent=${parent}, view=null, depth=${depth}, hashCode=${this.hashCode()}}"

            val build = StringBuilder()
            build.append("ViewNode{")
            build.append("className=", view.javaClass.name, ", ")
            build.append("package=", view.javaClass.`package`?.name, ", ")
            build.append("superClass=", view.javaClass.superclass.name, ", ")
            build.append("id=", view.id, ", ")
            build.append("idHex=", getIdHex(view), ", ")
            build.append("idName=", getIdName(view), ", ")
            build.append("context=", view.context, ", ")
            build.append("width=", view.context.px2dip(view.width.toFloat()), "dp, ")
            build.append("height=", view.context.px2dip(view.height.toFloat()), "dp, ")
            build.append("desc=", view.contentDescription, ", ")
            build.append("alpha=", view.alpha, ", ")
            val paddingStartDp = view.context.px2dip(view.paddingStart.toFloat())
            val paddingTopDp = view.context.px2dip(view.paddingTop.toFloat())
            val paddingEndDp = view.context.px2dip(view.paddingEnd.toFloat())
            val paddingBottomDp = view.context.px2dip(view.paddingBottom.toFloat())
            build.append("paddingLTRB=")
                .append("[")
                .append(paddingStartDp, "dp, ")
                .append(paddingTopDp, "dp, ")
                .append(paddingEndDp, "dp, ")
                .append(paddingBottomDp, "dp")
                .append("], ")
            val marginStartDp = view.context.px2dip(view.marginStart.toFloat())
            val marginTopDp = view.context.px2dip(view.marginTop.toFloat())
            val marginEndDp = view.context.px2dip(view.marginEnd.toFloat())
            val marginBottomDp = view.context.px2dip(view.marginBottom.toFloat())
            build.append("marginLTRB")
                .append("[")
                .append(marginStartDp, "dp, ")
                .append(marginTopDp, "dp, ")
                .append(marginEndDp, "dp, ")
                .append(marginBottomDp, "dp")
                .append("], ")
            when (view.visibility) {
                View.VISIBLE -> build.append("visibility=VISIBLE, ")
                View.GONE -> build.append("visibility=GONE, ")
                View.INVISIBLE -> build.append("visibility=INVISIBLE, ")
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                when (val foreground = view.foreground) {
                    is ColorDrawable -> {
                        build.append("foreground=ColorDrawable(")
                            .append(KColorUtils.colorIntToHex(foreground.color))
                            .append("), ")
                    }

                    is ShapeDrawable -> {
                        build.append("foreground=ShapeDrawable(color: ")
                            .append(KColorUtils.colorIntToHex(foreground.paint.color))
                            .append("), ")
                    }

                    else -> {
                        build.append("foreground=", foreground, ", ")
                    }
                }
            }
            when (val background = view.background) {
                is ColorDrawable -> {
                    build.append("background=ColorDrawable(")
                        .append(KColorUtils.colorIntToHex(background.color))
                        .append("), ")
                }

                is ShapeDrawable -> {
                    build.append("background=ShapeDrawable(color: ")
                        .append(KColorUtils.colorIntToHex(background.paint.color))
                        .append("), ")
                }

                else -> {
                    build.append("background=", background, ", ")
                }
            }
            // 文本框
            if (view is TextView) {
                build.append("text=", view.text, ", ")
                build.append("hint=", view.hint, ", ")
                build.append("textSize=", view.context.px2dip(view.textSize), "dp, ")
                if (view.typeface.isBold) {
                    build.append("textStyle=bold, ")
                } else if (view.typeface.isItalic) {
                    build.append("textStyle=italic, ")
                } else {
                    build.append("textStyle=normal, ")
                }
                build.append("currentTextColor=", KColorUtils.colorIntToHex(view.currentTextColor), ", ")
                build.append(
                    "currentHintTextColor=",
                    KColorUtils.colorIntToHex(view.currentHintTextColor),
                    ", "
                )
                build.append("highlightColor=", KColorUtils.colorIntToHex(view.highlightColor), ", ")
                build.append("selectable=", view.isTextSelectable, ", ")
                build.append("minHeight=", view.minHeight, ", ")
                build.append("maxHeight=", view.maxHeight)
                build.append("lineHeight=", view.lineHeight, ", ")
                build.append("minLines=", view.minLines, ", ")
                build.append("maxLines=", view.maxLines, ", ")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    build.append("singleLine=", view.isSingleLine, ", ")
                }
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                build.append("tooltipText=", view.tooltipText, ", ")
            }
            build.append("enabled=", view.isEnabled, ", ")
            build.append("pressed=", view.isPressed, ", ")
            build.append("hovered=", view.isHovered, ", ")
            build.append("focusable=", view.isFocusable, ", ")
            build.append("focused=", view.isFocused, ", ")
            build.append("selected=", view.isSelected, ", ")
            build.append("clickable=", view.isClickable, ", ")
            build.append("longClickable=", view.isLongClickable, ", ")
            // 点击事件
            val clickListener = getOnClickListener(view)
            if (clickListener != null) {
                build.append("onClickListener=", clickListener, ", ")
            }
            val longClickListener = getOnLongClickListener(view)
            if (longClickListener != null) {
                build.append("longClickListener=", longClickListener, ", ")
            }
            val onTouchListener = getOnTouchListener(view)
            if (onTouchListener != null) {
                build.append("onTouchListener=", onTouchListener, ", ")
            }
            // 列表类适配器
            if (view is ViewPager) {
                build.append("ViewPagerAdapter", view.adapter, ", ")
                build.append("ViewPagerCurrentItem", view.currentItem, ", ")
            }
            if (view is ListView) {
                build.append("ListViewAdapter", view.adapter, ", ")
            }
            if (view is RecyclerView) {
                build.append("RecyclerViewAdapter", view.adapter, ", ")
            }
            build.append("tag=", view.tag, ", ")
            build.append("viewPosition=", PointF(view.x, view.y), ", ")
            build.append("pivotPosition=", PointF(view.pivotX, view.pivotY), ", ")
            build.append("rotationPosition=", PointF(view.rotationX, view.rotationY), ", ")
            val outOnScreen = IntArray(2) { 0 }
            view.getLocationOnScreen(outOnScreen)
            build.append("locationOnScreen=", Point(outOnScreen[0], outOnScreen[1]), ", ")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val outInSurface = IntArray(2) { 0 }
                view.getLocationInSurface(outOnScreen)
                build.append("locationInSurface=", Point(outInSurface[0], outInSurface[1]), ", ")
            }
            val outInWindow = IntArray(2) { 0 }
            view.getLocationInWindow(outInWindow)
            build.append("locationInWindow=", Point(outInWindow[0], outInWindow[1]), ", ")

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val outRect = Rect()
                view.getClipBounds(outRect)
                build.append("locationInWindow=", outRect.toShortString(), ", ")
            } else {
                build.append("clipBounds=", view.clipBounds?.toShortString(), ", ")
            }
            build.append("rect=", Rect(view.left, view.top, view.right, view.bottom).toShortString(), ", ")
            build.append("childrenSize=", children.size, ", ")
            build.append("}")

            return build.toString()
        }
    }
}

///
fun View.toViewJson(indentSpaces: Int = 0): String {
    return KViewUtils.toViewJson(this, indentSpaces)
}

fun View.getViewTree(): KViewUtils.ViewNode {
    return KViewUtils.buildViewTree(this)
}

fun View.toViewTreeString(
    indent: Int = 4,
    format: (KViewUtils.ViewNode) -> String = { it.toString() },
): String {
    return KViewUtils
        .buildViewTree(this)
        .deepToString(indent, format)
}

fun <V : View> V.postRunning(block: V.() -> Unit) {
    this.post {
        block.invoke(this)
    }
}

fun <V : View> V.postDelayedRunning(delayInMillis: Long, block: V.() -> Unit) {
    this.postDelayed(delayInMillis) {
        block.invoke(this)
    }
}

fun View.traverse(block: View.() -> Unit) {
    KViewUtils.traverse(this, block)
}

fun View.setLayoutSize(needWidth: Int, needHeight: Int) {
    layoutParams = layoutParams?.apply {
        width = needWidth
        height = needHeight
    }
}

fun View.setLayoutWidth(needWidth: Int) {
    layoutParams = layoutParams?.apply {
        width = needWidth
    }
}

fun View.setLayoutHeight(needHeight: Int) {
    layoutParams = layoutParams?.apply {
        height = needHeight
    }
}

fun <T : View> View.findViewsByType(targetType: Class<T>): List<T> {
    return KViewUtils.findViews(this, targetType)
}

fun <T : View> View.findViewsByDesc(targetType: Class<T>, containsDesc: Regex): List<T> {
    return KViewUtils.findViewsByDesc(this, targetType, containsDesc)
}

fun <T : View> View.findViewsByDesc(
    targetType: Class<T>,
    containsDesc: String,
    ignoreCase: Boolean = false
): List<T> {
    return KViewUtils.findViewsByDesc(this, targetType, containsDesc, ignoreCase)
}

fun <T : View> View.findViewsByExact(
    targetType: Class<T>,
    block: T.() -> Boolean,
): List<T> {
    return KViewUtils.findViewsExact(this, targetType, block)
}

fun <T : View> View.findViewsByIdName(
    targetType: Class<T>,
    idName: String,
): List<T> {
    return KViewUtils.findViewsByIdName(this, targetType, idName)
}

fun <T : View> View.findParentExact(
    targetType: Class<T>,
    deep: Int = 1,
): T? {
    return KViewUtils.findParentExact(this, targetType, deep)
}

fun View.removeInParent(): View? {
    val parentView = this.parent.asOrNull<ViewGroup>() ?: return null
    parentView.removeView(this)
    return this
}

fun View.removeInParentIndex(): Int {
    val parentView = this.parent.asOrNull<ViewGroup>() ?: return -1
    val indexOfChild = parentView.indexOfChild(this)
    if (indexOfChild != -1) {
        parentView.removeViewAt(indexOfChild)
    }
    return indexOfChild
}

fun View.replaceCurrentView(view: View): Boolean {
    val parentView = this.parent.asOrNull<ViewGroup>() ?: return false
    val indexOfChild = parentView.indexOfChild(this)
    if (indexOfChild != -1) {
        parentView.removeViewAt(indexOfChild)
        parentView.addView(view, indexOfChild)
        return true
    }
    return false
}

fun View.setEnabledAll(enabled: Boolean) {
    KViewUtils.setEnabledAll(this, enabled)
}

val View.idName get() = KViewUtils.getIdName(this)

val View.idHex get() = KViewUtils.getIdHex(this)

val View.parentView get() = this.parent?.asOrNull<ViewGroup>()

val View.isDisplay: Boolean
    get() {
        // val screenSize = KDisplayUtils.screenSize()
        // val temp = IntArray(2) { 0 }
        // this.getLocationOnScreen(temp)
        // return (temp[0] >= 0 && temp[0] <= screenSize.width) && (temp[1] >= 0 && temp[1] <= screenSize.height)
        val rect = Rect()
        return getLocalVisibleRect(rect)
    }

val View.toBitmap get() = KViewUtils.viewToBitmap(this)