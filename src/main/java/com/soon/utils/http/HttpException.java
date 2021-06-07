package com.soon.utils.http;

/**
 * http包异常类
 *
 * @author HuYiGong
 * @since 2021/6/7
 **/
public class HttpException extends RuntimeException {
    /**
     * 通过message构造一个HttpException实例
     *
     * @param message 消息
     * @author HuYiGong
     * @since 2021/6/7 9:33
     */
    public HttpException(String message) {
        super(message);
    }

    /**
     * 通过message和cause构造HttpException实例
     *
     * @param message 消息
     * @param cause 异常
     * @author HuYiGong
     * @since 2021/6/7 9:34
     */
    public HttpException(String message, Throwable cause) {
        super(message, cause);
    }
}
