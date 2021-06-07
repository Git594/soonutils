package com.soon.utils.http;

import com.alibaba.fastjson.JSON;
import com.soon.utils.consts.Logs;
import com.soon.utils.consts.Tips;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Created on 2021/5/12.
 *
 * @author Soon
 */
public class RequestUtils {
    private static final Logger log = LoggerFactory.getLogger(RequestUtils.class);

    private RequestUtils() {}
    /**
     * 获取请求的真实ip地址
     *
     * @param request 请求
     * @return 真实ip
     *         null 出现未知情况
     * @author HuYiGong
     * @since 2021/5/12
     */
    public static String getRealIp(HttpServletRequest request) {
        Objects.requireNonNull(request, String.format(Tips.PARAMS_CANNOT_BE_NULL, "request"));
        String unknown = "unknown";
        String ipAddress = request.getHeader("x-forwarded-for");
        if (StringUtils.isBlank(ipAddress) || unknown.equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if (StringUtils.isBlank(ipAddress) || unknown.equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if (StringUtils.isBlank(ipAddress) || unknown.equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
            String localIpv4 = "127.0.0.1";
            String localIpv6 = "0:0:0:0:0:0:0:1";
            if (localIpv4.equals(ipAddress) || localIpv6.equals(ipAddress)) {
                //根据网卡取本机配置的IP
                InetAddress inet;
                try {
                    inet = InetAddress.getLocalHost();
                    ipAddress = inet.getHostAddress();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
            }
        }
        //对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
        int pos = Optional.ofNullable(ipAddress).orElse("").indexOf(',');
        if (pos > 0) {
            ipAddress = ipAddress.substring(0, pos);
        }
        return ipAddress;
    }

    /**
     * 获取RestTemplate实例
     *
     * @return org.springframework.web.client.RestTemplate 实例
     * @author HuYiGong
     * @since 2021/6/7 9:11
     */
    public static RestTemplate getRestTemplate() {
        return new RestTemplate();
    }

    /**
     * 发送post请求
     *
     * @param url 接口地址
     * @param bodyParam body内的参数
     * @param responseType 返回的类别
     * @return T 对应类的返回参数
     *         null 出错后
     * @author HuYiGong
     * @since 2021/6/7 11:06
     */
    @Nullable
    public static<T> T postForObject(String url, @Nullable Object bodyParam, Class<T> responseType) {
        long start = System.currentTimeMillis();
        try {
            return getRestTemplate().postForObject(url, bodyParam, responseType);
        } catch (Exception e) {
            log.error(Logs.HTTP_ERROR_LOG, url, JSON.toJSONString(bodyParam), e.getCause());
        } finally {
            log.info(Logs.HTTP_INFO_LOG, url, System.currentTimeMillis() - start);
        }
        return null;
    }

    /**
     * 发送post请求
     *
     * @param url 接口地址
     * @param bodyParam body内的参数
     * @param responseType 返回的类别
     * @param uriVariables url参数
     * @return T 对应类的返回参数
     *         null 出错后
     * @author HuYiGong
     * @since 2021/6/7 10:27
     */
    @Nullable
    public static<T> T postForObject(String url, @Nullable Object bodyParam, Class<T> responseType, Map<String, ?> uriVariables) {
        long start = System.currentTimeMillis();
        try {
            return getRestTemplate().postForObject(url, bodyParam, responseType, uriVariables);
        } catch (Exception e) {
            String completeUrl = getCompleteUrl(url, uriVariables);
            log.error(Logs.HTTP_ERROR_LOG, completeUrl, JSON.toJSONString(bodyParam), e.getCause());
        } finally {
            log.info(Logs.HTTP_INFO_LOG, url, System.currentTimeMillis() - start);
        }
        return null;
    }

    private static String getCompleteUrl(String url, Map<String,?> uriVariables) {
        if (Objects.isNull(uriVariables)) {
            uriVariables =  new HashMap<>(2);
        }
        StringBuilder sb = new StringBuilder(url);
        int count = 1;
        for (Map.Entry<String, ?> entry : uriVariables.entrySet()) {
            if (count++ == 1) {
                sb.append("?");
            } else {
                sb.append("&");
            }
            sb.append(entry.getKey()).append("=").append(entry.getValue());
        }
        return sb.toString();
    }
}
