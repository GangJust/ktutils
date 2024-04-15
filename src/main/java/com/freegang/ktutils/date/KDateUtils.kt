package com.freegang.ktutils.date

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * 关于日期格式化模式中的各个字母的含义解释:
 *
 * yyyy: 年份，四位数，例如：2023
 * MM: 月份，两位数，例如：01 表示一月
 * dd: 日期，两位数，例如：01 表示第一天
 * HH: 小时，24小时制，两位数，例如：08 表示早上8点
 * mm: 分钟，两位数，例如：30 表示30分钟
 * ss: 秒钟，两位数，例如：45 表示45秒钟
 * SSS: 毫秒，三位数，例如：500 表示500毫秒
 * Z: 时区偏移量，例如：+0800 表示东八区
 * EEE: 星期几，例如：星期一、星期二等
 *
 * ISO 8601 中的 T：T是日期和时间的分隔符，用于将日期部分和时间部分分隔开来，例如：yyyy-MM-dd'T'HH:mm:ss
 */
object KDateUtils {
    const val PATTERN_FULL = "yyyy-MM-dd HH:mm:ss"
    const val PATTERN_DATE = "yyyy-MM-dd"
    const val PATTERN_TIME = "HH:mm:ss"
    const val PATTERN_MONTH = "yyyy-MM"
    const val PATTERN_YEAR = "yyyy"

    /**
     * 获取日历实例
     * @return Calendar
     */
    @JvmStatic
    val calendar: Calendar
        get() = Calendar.getInstance()

    /**
     * 获取当前日期
     * @return 当前日期
     */
    @JvmStatic
    val current: Date
        get() = calendar.time

    /**
     * 获取当前日期时间戳
     * @return 当前日期时间戳
     */
    @JvmStatic
    val currentTime: Long
        get() = current.time

    /**
     * 获取当前日期的年份。
     * @return 当前日期的年份。
     */
    @JvmStatic
    val year: Int
        get() = calendar.get(Calendar.YEAR)

    /**
     * 获取当前日期的月份。
     * @return 当前日期的月份，范围是 1-12。
     */
    @JvmStatic
    val month: Int
        get() = calendar.get(Calendar.MONTH) + 1 // 注意：Calendar.MONTH 的取值范围是 0-11，所以需要加1

    /**
     * 获取当前日期的天数。
     * @return 当前日期的天数，范围是 1-31。
     */
    @JvmStatic
    val day: Int
        get() = calendar.get(Calendar.DAY_OF_MONTH)

    /**
     * 获取当前日期是周几。
     * @return 当前日期是周几的整数表示，范围为 1-7，1 表示周日，2 表示周一，依此类推。
     */
    @JvmStatic
    val week: Int
        get() = calendar.get(Calendar.DAY_OF_WEEK)

    /**
     * 格式化日期为字符串
     *
     * @param date 日期对象
     * @param pattern 日期格式化模式，默认为 "yyyy-MM-dd HH:mm:ss"
     * @return 格式化后的日期字符串
     */
    @JvmStatic
    @JvmOverloads
    fun format(date: Date = current, pattern: String = PATTERN_FULL): String {
        val format = SimpleDateFormat(pattern, Locale.CHINA)
        return format.format(date)
    }

    /**
     * 格式化当前日期为字符串
     *
     * @param pattern 日期格式化模式，默认为 "yyyy-MM-dd HH:mm:ss"
     * @return 格式化后的日期字符串
     */
    @JvmStatic
    fun format(pattern: String = PATTERN_FULL): String {
        return format(current, pattern)
    }

    /**
     * 格式化日期为字符串
     *
     * @param year
     * @param month
     * @param day
     * @param hour
     * @param minute
     * @param second
     * @param pattern 日期格式化模式，默认为 "yyyy-MM-dd HH:mm:ss"
     * @return 格式化后的日期字符串
     */
    @JvmStatic
    @JvmOverloads
    fun format(
        year: Int,
        month: Int,
        day: Int,
        hour: Int = 0,
        minute: Int = 0,
        second: Int = 0,
        pattern: String = PATTERN_FULL,
    ): String {
        val format = SimpleDateFormat(pattern, Locale.CHINA)
        val instance = Calendar.getInstance()
        instance.set(Calendar.YEAR, year)
        instance.set(Calendar.MONTH, month)
        instance.set(Calendar.DAY_OF_MONTH, day)
        instance.set(Calendar.HOUR_OF_DAY, hour)
        instance.set(Calendar.MINUTE, minute)
        instance.set(Calendar.SECOND, second)
        return format.format(instance.time)
    }

    /**
     * 将日期字符串解析为日期对象
     *
     * @param dateString 日期字符串
     * @param pattern 日期格式化模式，默认为 "yyyy-MM-dd HH:mm:ss"
     * @return 解析后的日期对象，解析失败时返回 null
     */
    @JvmStatic
    @JvmOverloads
    fun parse(dateString: String, pattern: String = PATTERN_FULL): Date? {
        val format = SimpleDateFormat(pattern, Locale.CHINA)
        return try {
            format.parse(dateString)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * 获取两个日期之间的天数差
     *
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 两个日期之间的天数差，若 startDate 在 endDate 之后，则返回负数
     */
    @JvmStatic
    fun getDaysBetweenDates(startDate: Date, endDate: Date): Long {
        val start = getStartOfDay(startDate)
        val end = getStartOfDay(endDate)
        val diff = end.time - start.time
        return diff / (1000 * 60 * 60 * 24)
    }

    /**
     * 获取指定日期的起始时间（当天的 00:00:00）
     *
     * @param date 指定日期
     * @return 起始时间的日期对象
     */
    @JvmStatic
    @JvmOverloads
    fun getStartOfDay(date: Date = current): Date {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.time
    }

    /**
     * 获取指定日期的结束时间（当天的 23:59:59）
     *
     * @param date 指定日期
     * @return 结束时间的日期对象
     */
    @JvmStatic
    @JvmOverloads
    fun getEndOfDay(date: Date = current): Date {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        return calendar.time
    }

    /**
     * 为指定日期[date]增加指定年份[years], 返回增加指定年数后的日期
     *
     * @param date 起始日期
     * @param years 年份（正数表示之后，负数表示之前）
     * @return 增加指定年数后的日期
     */
    @JvmStatic
    @JvmOverloads
    fun addYears(date: Date = current, years: Int): Date {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.add(Calendar.YEAR, years)
        return calendar.time
    }

    /**
     * 为指定日期[date]增加指定月份[months], 返回增加指定月份后的日期
     *
     * @param date 起始日期
     * @param months 月份（正数表示之后，负数表示之前）
     * @return 增加指定年数后的日期
     */
    @JvmStatic
    @JvmOverloads
    fun addMonths(date: Date = current, months: Int): Date {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.add(Calendar.MONTH, months)
        return calendar.time
    }

    /**
     * 为指定日期[date]增加指定天数[days], 返回增加指定天数后的日期
     *
     * @param date 起始日期
     * @param days 天数（正数表示之后，负数表示之前）
     * @return 增加指定天数后的日期
     */
    @JvmStatic
    @JvmOverloads
    fun addDays(date: Date = current, days: Int): Date {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.add(Calendar.DAY_OF_YEAR, days)
        return calendar.time
    }

    /**
     * 为指定日期[date]增加指定小时数[hours], 返回增加指定小时数后的日期
     *
     * @param date 起始日期
     * @param hours 小时数（正数表示之后，负数表示之前）
     * @return 增加指定小时数后的日期
     */
    @JvmStatic
    @JvmOverloads
    fun addHours(date: Date = current, hours: Int): Date {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.add(Calendar.HOUR_OF_DAY, hours)
        return calendar.time
    }

    /**
     * 为指定日期[date]增加指定分钟数[minutes], 返回增加指定分钟数后的日期
     *
     * @param date 起始日期
     * @param minutes 分钟数（正数表示之后，负数表示之前）
     * @return 增加指定分钟数后的日期
     */
    @JvmStatic
    @JvmOverloads
    fun addMinutes(date: Date = current, minutes: Int): Date {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.add(Calendar.MINUTE, minutes)
        return calendar.time
    }

    /**
     * 为指定日期[date]增加指定秒数[seconds], 返回增加指定秒数后的日期
     *
     * @param date 起始日期
     * @param seconds 秒数（正数表示之后，负数表示之前）
     * @return 增加指定秒数后的日期
     */
    @JvmStatic
    @JvmOverloads
    fun addSeconds(date: Date = current, seconds: Int): Date {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.add(Calendar.SECOND, seconds)
        return calendar.time
    }

    /**
     * (全站日期) 获取指定时间与当前时间之间的相对时间差。
     * 如果时间差超过三个月，则返回具体的日期和时间，精确到分钟；
     * 否则返回相对时间差。
     * @param timeInMillis 指定时间的毫秒值。
     * @return 相对时间差的字符串表示，例如："10秒前"、"2023年1月1日 10:30" 或 "10 seconds ago"、"January 1, 2023 10:30 AM"。
     */
    @JvmStatic
    fun getRelativeTime(timeInMillis: Long): String {
        val currentTime = System.currentTimeMillis()
        val diff = currentTime - timeInMillis
        val seconds = diff / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        val days = hours / 24
        val months = days / 30

        return when {
            months >= 3 -> format(Date(timeInMillis), "yyyy-MM-dd HH:mm")
            days >= 1 -> "$days 天前"
            hours >= 1 -> "$hours 小时前"
            minutes >= 1 -> "$minutes 分钟前"
            else -> "刚刚"
        }
    }

    /**
     * (全站日期) 获取指定时间与当前时间之间的相对时间差。
     * 如果时间差超过三个月，则返回具体的日期和时间，精确到分钟；
     * 否则返回相对时间差。
     * @param date 指定的时间
     * @return 相对时间差的字符串表示，例如："10秒前"、"2023年1月1日 10:30" 或 "10 seconds ago"、"January 1, 2023 10:30 AM"。
     */
    @JvmStatic
    fun getRelativeTime(date: Date): String {
        return getRelativeTime(date.time)
    }

    /**
     * 判断给定的日期所在年份是否是闰年。
     *
     * @param date 给定的日期
     * @return 如果是闰年则返回 true，否则返回 false
     */
    @JvmStatic
    @JvmOverloads
    fun isLeapYear(date: Date = current): Boolean {
        val calendar = Calendar.getInstance()
        calendar.time = date
        val year = calendar.get(Calendar.YEAR)
        return year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)
    }


    /**
     * 对某个时间进行系列操作
     */
    class Builder(private var date: Date = current) {

        constructor(time: Long) : this(Date(time))

        fun addYears(years: Int): Builder {
            date = addYears(date, years)
            return this
        }

        fun addMonths(months: Int): Builder {
            date = addMonths(date, months)
            return this
        }

        fun addDays(days: Int): Builder {
            date = addDays(date, days)
            return this
        }

        fun addHour(hours: Int): Builder {
            date = addHours(date, hours)
            return this
        }

        fun addMinutes(minutes: Int): Builder {
            date = addMinutes(date, minutes)
            return this
        }

        fun addSeconds(seconds: Int): Builder {
            date = addSeconds(date, seconds)
            return this
        }

        fun getStartOfDay(): Builder {
            date = getStartOfDay(date)
            return this
        }

        fun getEndOfDay(): Builder {
            date = getEndOfDay(date)
            return this
        }

        fun build(): Date {
            return date
        }

        @JvmOverloads
        fun buildFormat(pattern: String = KDateUtils.PATTERN_FULL): String {
            return format(date, pattern)
        }
    }
}





