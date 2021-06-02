package com.soon.utils;

import java.util.UUID;

/**
 * Created on 2021/5/13.
 *
 * @author Soon
 */
public class UidGenerator {
    /**
     * 获取去掉"-"后的UUID
     *
     * @return 去掉"-"后的UUID
     * @author HuYiGong
     * @since 2021/5/13
     */
    public static String getCleanUid() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
