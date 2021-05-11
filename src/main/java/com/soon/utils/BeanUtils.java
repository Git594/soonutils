package com.soon.utils;

import net.sf.cglib.beans.BeanCopier;
import net.sf.cglib.core.Converter;

/**
 * Created on 2021/5/11.
 *
 * @author Soon
 */
public class BeanUtils {
    private BeanUtils() {}

    /**
     * 从源对象中拷贝属性到目标属性中，只会拷贝类型和名称完全相同的属性
     *
     * @param source 源对象
     * @param target 目标对象
     * @author HuYiGong
     * @since 2021/5/11
     */
    public static void copy(Object source, Object target) {
        BeanCopier copier = BeanCopier.create(source.getClass(), target.getClass(), false);
        copier.copy(source, target, null);
    }

    /**
     * 从源对象中拷贝属性到目标属性中，根据转换器拷贝
     *
     * @param source 源对象
     * @param target 目标对象
     * @param converter 转换器
     * @author HuYiGong
     * @since 2021/5/11
     */
    public static void copy(Object source, Object target, Converter converter) {
        BeanCopier copier = BeanCopier.create(source.getClass(), target.getClass(), true);
        copier.copy(source, target, converter);
    }
}
