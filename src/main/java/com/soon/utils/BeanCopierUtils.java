package com.soon.utils;

import com.soon.utils.consts.Tips;
import net.sf.cglib.beans.BeanCopier;
import net.sf.cglib.core.Converter;

import java.util.Objects;

/**
 * Created on 2021/5/11.
 *
 * @author Soon
 */
public class BeanCopierUtils {
    private BeanCopierUtils() {}

    /**
     * 从源对象中拷贝属性到目标属性中，只会拷贝类型和名称完全相同的属性
     *
     * @param source 源对象
     * @param target 目标对象
     * @author HuYiGong
     * @since 2021/5/11
     */
    public static void copy(Object source, Object target) {
        Objects.requireNonNull(source, String.format(Tips.PARAMS_NOT_NULL, "source"));
        Objects.requireNonNull(source, String.format(Tips.PARAMS_NOT_NULL, "target"));
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
        Objects.requireNonNull(source, String.format(Tips.PARAMS_NOT_NULL, "source"));
        Objects.requireNonNull(target, String.format(Tips.PARAMS_NOT_NULL, "target"));
        Objects.requireNonNull(converter, String.format(Tips.PARAMS_NOT_NULL, "converter"));
        BeanCopier copier = BeanCopier.create(source.getClass(), target.getClass(), true);
        copier.copy(source, target, converter);
    }
}
