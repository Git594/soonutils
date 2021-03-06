package com.soon.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;

/**
 * 应用上下文工具类
 *
 * @author soon
 * @since 2021/05/28
 */
@Component
public class ApplicationContextUtils implements ApplicationContextAware {
    private ApplicationContextUtils() {}

    private static ApplicationContext applicationContext;

    /**
     * 设置context，只能设置一次，设置后不能再次修改
     *
     * @param appContext context
     * @author HuYiGong
     * @since 2021/5/31 11:37
     */
    @Override
    public void setApplicationContext (ApplicationContext appContext) throws BeansException {
        if (Objects.isNull(applicationContext)) {
            synchronized (ApplicationContextUtils.class) {
                if (Objects.isNull(applicationContext)) {
                    applicationContext = appContext;
                }
            }
        }
    }

    /**
     * 获取spring容器中的bean
     *
     * @param name bean的实例名 例：类ApplicationContextUtils的实例名为applicationContextUtils
     * @return java.lang.Object 对应的实例
     * @author HuYiGong
     * @since 2021/5/31 11:38
     */
    public static Object getBean (String name) {
        return applicationContext.getBean(name);
    }

    /**
     * 获取spring容器中的bean
     *
     * @param clazz bean的字节码
     * @return T 对应的实例
     * @author HuYiGong
     * @since 2021/5/31 11:42
     */
    public static <T> T getBean (Class<T> clazz) {
        return applicationContext.getBean(clazz);
    }

    /**
     * 获取该类的所有对象
     *
     * @param clazz 类
     * @return java.util.Map<java.lang.String,T> 所有的对象
     * @author HuYiGong
     * @since 2021/5/31 11:47
     */
    public static <T> Map<String, T> getBeansOfType(Class<T> clazz) {
        return applicationContext.getBeansOfType(clazz);
    }
}
