package com.freegang.ktutils.date

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.*
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

/**
 * 对于 Java8 中新出现的 time 包
 */
object KTimeUtils {
    const val PATTERN_FULL = "yyyy-MM-dd HH:mm:ss"
    const val PATTERN_DATE = "yyyy-MM-dd"
    const val PATTERN_TIME = "HH:mm:ss"
    const val PATTERN_MONTH = "yyyy-MM"
    const val PATTERN_YEAR = "yyyy"

    /**
     * 获取当前日期
     * @return 当前日期
     */
    @JvmStatic
    @get:RequiresApi(Build.VERSION_CODES.O)
    val currentDate: LocalDate
        get() = LocalDate.now()

    /**
     * 获取当前日期时间
     * @return 当前日期时间
     */
    @JvmStatic
    @get:RequiresApi(Build.VERSION_CODES.O)
    val currentDateTime: LocalDateTime
        get() = LocalDateTime.now()

    /**
     * 根据给定的时间戳，返回对应的 LocalDate 对象。
     *
     * @param timeInMillis 时间戳，表示自 1970 年 1 月 1 日 00:00:00 GMT 以来的毫秒数
     * @return 对应的 LocalDate 对象
     */
    @JvmStatic
    @RequiresApi(Build.VERSION_CODES.O)
    fun getLocalDate(timeInMillis: Long): LocalDate {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timeInMillis), ZoneId.systemDefault()).toLocalDate()
    }

    /**
     * 根据给定的时间戳，返回对应的 LocalDateTime 对象。
     *
     * @param timeInMillis 时间戳，表示自 1970 年 1 月 1 日 00:00:00 GMT 以来的毫秒数
     * @return 对应的 LocalDateTime 对象
     */
    @JvmStatic
    @RequiresApi(Build.VERSION_CODES.O)
    fun getLocalDateTime(timeInMillis: Long): LocalDateTime {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timeInMillis), ZoneId.systemDefault())
    }

    /**
     * 格式化日期为字符串
     *
     * @param date 日期对象
     * @param pattern 日期格式化模式，默认为 "yyyy-MM-dd HH:mm:ss"
     * @return 格式化后的日期字符串
     */
    @JvmStatic
    @RequiresApi(Build.VERSION_CODES.O)
    fun formatDateToString(date: LocalDate, pattern: String = PATTERN_DATE): String {
        val formatter = DateTimeFormatter.ofPattern(pattern, Locale.CHINA)
        return date.format(formatter)
    }

    /**
     * 格式化日期时间为字符串
     *
     * @param dateTime 日期时间对象
     * @param pattern 日期时间格式化模式，默认为 "yyyy-MM-dd HH:mm:ss"
     * @return 格式化后的日期时间字符串
     */
    @JvmStatic
    @RequiresApi(Build.VERSION_CODES.O)
    fun formatDateTimeToString(dateTime: LocalDateTime, pattern: String = PATTERN_FULL): String {
        val formatter = DateTimeFormatter.ofPattern(pattern, Locale.CHINA)
        return dateTime.format(formatter)
    }

    /**
     * 将日期字符串解析为日期对象
     *
     * @param dateString 日期字符串
     * @param pattern 日期格式化模式，默认为 "yyyy-MM-dd"
     * @return 解析后的日期对象，解析失败时返回 null
     */
    @JvmStatic
    @RequiresApi(Build.VERSION_CODES.O)
    fun parseStringToDate(dateString: String, pattern: String = PATTERN_DATE): LocalDate? {
        val formatter = DateTimeFormatter.ofPattern(pattern, Locale.CHINA)
        return try {
            LocalDate.parse(dateString, formatter)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * 将日期时间字符串解析为日期时间对象
     *
     * @param dateTimeString 日期时间字符串
     * @param pattern 日期时间格式化模式，默认为 "yyyy-MM-dd HH:mm:ss"
     * @return 解析后的日期时间对象，解析失败时返回 null
     */
    @JvmStatic
    @RequiresApi(Build.VERSION_CODES.O)
    fun parseStringToDateTime(dateTimeString: String, pattern: String = PATTERN_FULL): LocalDateTime? {
        val formatter = DateTimeFormatter.ofPattern(pattern, Locale.CHINA)
        return try {
            LocalDateTime.parse(dateTimeString, formatter)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * 获取指定时间与当前时间之间的相对时间差。
     * 如果时间差超过三个月，则返回具体的日期和时间，精确到分钟；
     * 否则返回相对时间差。
     *
     * @param dateTime 指定的时间
     * @return 相对时间差或具体的日期和时间
     */
    @JvmStatic
    @RequiresApi(Build.VERSION_CODES.O)
    fun getRelativeTime(dateTime: LocalDateTime): String {
        val currentDateTime = LocalDateTime.now()
        val difference = ChronoUnit.MINUTES.between(dateTime, currentDateTime)
        val limit = 3 * 30 * 24 * 60 //最大超过3个月,返回具体时间
        return if (difference > limit) {
            formatDateTimeToString(dateTime, "yyyy-MM-dd HH:mm")
        } else {
            val months = ChronoUnit.MONTHS.between(dateTime, currentDateTime)
            val days = ChronoUnit.DAYS.between(dateTime, currentDateTime)
            val hours = ChronoUnit.HOURS.between(dateTime, currentDateTime)
            val minutes = ChronoUnit.MINUTES.between(dateTime, currentDateTime)
            when {
                months > 0 -> "$months 个月前"
                days > 0 -> "$days 天前"
                hours > 0 -> "$hours 小时前"
                minutes > 0 -> "$minutes 分钟前"
                else -> "刚刚"
            }
        }
    }

    /**
     * 获取指定时间与当前时间之间的相对时间差。
     * 如果时间差超过三个月，则返回具体的日期和时间，精确到分钟；
     * 否则返回相对时间差。
     *
     * @param timeInMillis 指定时间的毫秒值
     * @return 相对时间差或具体的日期和时间
     */
    @JvmStatic
    @RequiresApi(Build.VERSION_CODES.O)
    fun getRelativeTime(timeInMillis: Long): String {
        val dateTime = getLocalDateTime(timeInMillis)
        return getRelativeTime(dateTime)
    }

    /**
     * 获取指定日期时间的起始时间（当天的 00:00:00）。
     *
     * @param dateTime 指定的日期时间
     * @return 起始时间的 LocalDateTime 对象
     */
    @JvmStatic
    @RequiresApi(Build.VERSION_CODES.O)
    fun getStartOfDay(dateTime: LocalDateTime): LocalDateTime {
        return LocalDateTime.of(dateTime.toLocalDate(), LocalTime.MIN)
    }

    /**
     * 获取指定日期时间的结束时间（当天的 23:59:59）。
     *
     * @param dateTime 指定的日期时间
     * @return 结束时间的 LocalDateTime 对象
     */
    @JvmStatic
    @RequiresApi(Build.VERSION_CODES.O)
    fun getEndOfDay(dateTime: LocalDateTime): LocalDateTime {
        return LocalDateTime.of(dateTime.toLocalDate(), LocalTime.MAX)
    }
}
