package com.soon.utils.http;

import org.springframework.lang.Nullable;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Objects;

/**
 * @author soon
 * @since 2021/05/31
 */
public class HttpContextUtils {
    private HttpContextUtils() {}

    /**
     * 获取request对象
     *
     * @return javax.servlet.http.HttpServletRequest
     *         request对象
     *         当requestAttributes为null时，返回null
     * @author HuYiGong
     * @since 2021/5/31 14:03
     */
    @Nullable
    public static HttpServletRequest getHttpServletRequest() {
        ServletRequestAttributes requestAttributes = getServletRequestAttributes();
        if (requestAttributes != null) {
            return requestAttributes.getRequest();
        }
        return null;
    }

    /**
     * 获取 Response 对象
     *
     * @return javax.servlet.http.HttpServletResponse
     *         response对象
     *         当requestAttributes为null时，返回null
     * @author HuYiGong
     * @since 2021/5/31 14:05
     */
    @Nullable
    public static HttpServletResponse getHttpServletResponse() {
        ServletRequestAttributes requestAttributes = getServletRequestAttributes();
        if (requestAttributes != null) {
            return requestAttributes.getResponse();
        }
        return null;
    }

    /**
     * 获取当前绑定到线程的RequestAttributes
     *
     * @return org.springframework.web.context.request.ServletRequestAttributes
     *         请求属性
     * @author HuYiGong
     * @since 2021/5/31 14:05
     */
    @Nullable
    private static ServletRequestAttributes getServletRequestAttributes() {
        return (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    }

    /**
     * 获取session
     *
     * @return javax.servlet.http.HttpSession
     *         session对象
     *         当request为null时，返回null
     * @author HuYiGong
     * @since 2021/5/31 14:27
     */
    @Nullable
    public static HttpSession getSession() {
        HttpServletRequest request = getHttpServletRequest();
        if (Objects.isNull(request)) {
            return null;
        } else {
            return request.getSession();
        }
    }

    /**
     * 获取session
     *
     * @param sessionName session的key
     * @return java.lang.Object
     *         session的value
     *         request为null或者没有对应的key时，返回null
     * @author HuYiGong
     * @since 2021/5/31 14:48
     */
    @Nullable
    public static Object getSession(String sessionName) {
        HttpServletRequest request = getHttpServletRequest();
        if (Objects.isNull(request)) {
            return null;
        }
        HttpSession session = request.getSession();
        return session.getAttribute(sessionName);
    }

    /**
     * 添加session值
     *
     * @param sessionName session的key
     * @param object session的value
     * @author HuYiGong
     * @since 2021/5/31 14:32
     */
    public static void setSession(String sessionName, Object object) {
        HttpServletRequest request = getHttpServletRequest();
        if (Objects.isNull(request)) {
            return;
        }
        HttpSession session = request.getSession();
        if (null != object) {
            session.setAttribute(sessionName, object);
        }
    }
}
