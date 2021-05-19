package com.soon.utils;

import java.text.NumberFormat;

/**
 * 数字工具
 *
 * @author HuYiGong
 * @since 2021/5/14
 **/
public class NumberUtils {
    private NumberUtils() {}

    /**
     * 补零
     *
     * @param number 数字
     * @param digit 位数
     * @return java.lang.String 补零后的字符串
     * @author HuYiGong
     * @since 2021/5/14 16:40
     */
    public static String fillZero(long number, int digit) {
        NumberFormat format = NumberFormat.getInstance();
        format.setGroupingUsed(false);
        format.setMinimumIntegerDigits(digit);
        format.setMaximumIntegerDigits(digit);
        return format.format(number);
    }
}
