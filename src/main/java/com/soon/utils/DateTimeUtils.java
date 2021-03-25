package com.soon.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class DateTimeUtils {

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
     * 2021-03-23 -> 2021-03-23 00:00:00
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
     * 默认格式化方法，按照 yyyy-MM-ddTHH:mm:ss 格式化
     *
     * @param dateTime 日期时间
     * @return 对应时间的格式化显示
     * @author HuYiGong
     * @since 2021/3/23
     */
    public static String format(LocalDateTime dateTime) {
        return dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    /**
     * 获取当月第一天0点
     *
     * @param date 日期
     * @return 当月第一天0点
     * @author HuYiGong
     * @since 2021/3/25
     */
    public static LocalDateTime getFirstDayOfMonth(LocalDate date) {
        return date.withDayOfMonth(1).atStartOfDay();
    }

    public static void main(String[] args) {
        LocalDateTime start = getStartTime(LocalDate.now());
        LocalDateTime end = getEndTime(LocalDate.now());
        System.out.println(start);
        System.out.println(end);
        System.out.println(getFirstDayOfMonth(LocalDate.now()));
    }
}
