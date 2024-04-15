package com.freegang.extension

import android.content.Context
import com.freegang.ktutils.app.KAppUtils

/**
 * 判断当前dalvik虚拟机是否64位
 */
val Any.is64BitDalvik
    get() = KAppUtils.is64BitDalvik()

/**
 * 获取当前dalvik虚拟机指令集
 */
val Any.dalvikInstructionSet
    get() = KAppUtils.getDalvikInstructionSet()

/**
 * 获取当前设备安全补丁级别
 */
val Any.securityPatchLevel
    get() = KAppUtils.getSecurityPatchLevel()

/**
 * 返回当前app的abi架构
 */
val Context.abiBit
    get() = KAppUtils.getAbiBit(this)

/**
 * 获取当前app名称
 */
val Context.appLabelName
    get() = KAppUtils.getAppLabelName(this)

/**
 * 获取当前app版本名称
 */
val Context.appVersionName
    get() = KAppUtils.getVersionName(this)

/**
 * 获取当前app版本号
 */
val Context.appVersionCode
    get() = KAppUtils.getVersionCode(this)

/**
 * 判断当前是否处于深色模式
 */
val Context.isDarkMode
    get() = KAppUtils.isDarkMode(this)

/**
 * 获取当前设备状态栏高度
 */
val Context.statusBarHeight
    get() = KAppUtils.getStatusBarHeight(this)

/**
 * 获取当前设备底部导航栏高度
 */
val Context.navigationBarHeight
    get() = KAppUtils.getNavigationBarHeight(this)

/**
 * 获取当前设备底部导航栏交互模式
 */
val Context.navBarInteractionMode
    get() = KAppUtils.getNavBarInteractionMode(this)