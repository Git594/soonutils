package com.soon.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Date;

/**
 * 时间工具类
 *
 * @author HuYiGong
 */
public class DateTimeUtils {
    private DateTimeUtils() {
    }

    /**
     * 获取参数(date)的开始时间
     * 例如：
     * 2021-03-23 -> 2021-03-23 00:00:00
     *
     * @param date 日期
     * @return date这天的开始时间
     * @author HuYiGong
     * @since 2021/3/23
     */
    public static LocalDateTime getStartTime(LocalDate date) {
        return LocalDateTime.of(date, LocalTime.MIN);
    }

    /**
     * 获取参数(date)的结束时间
     * 例如：
     * 2021-03-23 -> 2021-03-23 23:59:59.999999999
     *
     * @param date 日期
     * @return date这天的开始时间
     * @author HuYiGong
     * @since 2021/3/23
     */
    public static LocalDateTime getEndTime(LocalDate date) {
        return LocalDateTime.of(date, LocalTime.MAX);
    }

    /**
     * 默认格式化方法，按照 yyyy-MM-dd HH:mm:ss 格式化
     *
     * @param dateTime 日期时间
     * @return 对应时间的格式化显示
     * @author HuYiGong
     * @since 2021/3/23
     */
    public static String format(LocalDateTime dateTime) {
        return dateTime.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM));
    }

    /**
     * 根据传入pattern进行格式化
     *
     * @param dateTime 日期时间
     * @param pattern 格式字符串
     * @return 格式化后的时间
     * @author HuYiGong
     * @since 2021/5/11
     */
    public static String format(LocalDateTime dateTime, String pattern) {
        return dateTime.format(DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * 获得当月的开始时间
     *
     * @param date 日期
     * @return 当月的开始时间
     * @author HuYiGong
     * @since 2021/5/11
     */
    public static LocalDateTime getStartTimeOfMonth(LocalDate date) {
        return date.withDayOfMonth(1).atStartOfDay();
    }

    /**
     * 获得当年的开始时间
     *
     * @param date 日期
     * @return 当前的开始时间
     * @author HuYiGong
     * @since 2021/5/11
     */
    public static LocalDateTime getStartTimeOfYear(LocalDate date) {
        return date.withDayOfYear(1).atStartOfDay();
    }

    /**
     * LocalDateTime转Date
     *
     * @param dateTime 日期时间
     * @return java.util.Date 转换后的日期时间
     * @author HuYiGong
     * @since 2021/5/11
     */
    public static Date toDate(LocalDateTime dateTime) {
        return Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * Date转LocalDateTime
     *
     * @param date 日期时间
     * @return java.time.LocalDateTime 转换后的日期时间
     * @author HuYiGong
     * @since 2021/5/11
     */
    public static LocalDateTime toLocalDateTime(Date date) {
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }
}
