package com.freegang.extension

import com.freegang.ktutils.date.KDateUtils
import java.util.Date


/**
 * 判断日期是否为闰年
 */
val Date.isLeapYear
    get() = KDateUtils.isLeapYear(this)

/**
 * 对日期进行格式化
 */
fun Date.format(pattern: String = KDateUtils.PATTERN_FULL): String {
    return KDateUtils.format(pattern)
}

/**
 * 对指定日期增加指定年数
 */
fun Date.addYears(years: Int): Date {
    return KDateUtils.addYears(this, years)
}

/**
 * 对指定日期增加指定月数
 */
fun Date.addMonths(months: Int): Date {
    return KDateUtils.addMonths(this, months)
}

/**
 * 对指定日期增加指定天数
 */
fun Date.addDays(days: Int): Date {
    return KDateUtils.addDays(this, days)
}

/**
 * 对指定日期增加指定小时数
 */
fun Date.addHours(hours: Int): Date {
    return KDateUtils.addHours(this, hours)
}

/**
 * 对指定日期增加指定分钟数
 */
fun Date.addMinutes(minutes: Int): Date {
    return KDateUtils.addMinutes(this, minutes)
}

/**
 * 对指定日期增加指定秒数
 */
fun Date.addSeconds(seconds: Int): Date {
    return KDateUtils.addSeconds(this, seconds)
}