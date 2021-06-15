package com.soon.utils.http;

import com.alibaba.fastjson.JSON;
import com.soon.utils.consts.Logs;
import com.soon.utils.consts.Tips;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.lang.Nullable;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.*;

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
                    log.error(ExceptionUtils.getStackTrace(e));
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
     * 根据超时时间，获取RestTemplate实例
     *
     * @param timeout 超时时间
     * @return org.springframework.web.client.RestTemplate 实例
     * @author HuYiGong
     * @since 2021/6/7 9:11
     */
    public static RestTemplate getRestTemplate(int timeout) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setReadTimeout(timeout);
        factory.setConnectTimeout(timeout);
        return new RestTemplate(factory);
    }

    /**
     * 根据超时时间，获取RestTemplate实例
     *
     * @param factory org.springframework.http.client.ClientHttpRequestFactory
     * @return org.springframework.web.client.RestTemplate 实例
     * @author HuYiGong
     * @since 2021/6/7 9:11
     */
    public static RestTemplate getRestTemplate(ClientHttpRequestFactory factory) {
        return new RestTemplate(factory);
    }

    /**
     * 发起post请求
     *
     * @param url 接口地址
     * @param bodyParam body内的参数
     * @param responseType 返回的类别
     * @return T 对应的响应结果
     *         null 出错后
     * @author HuYiGong
     * @since 2021/6/7 11:06
     */
    @Nullable
    public static <T> T postForObject(String url, @Nullable Object bodyParam, Class<T> responseType) {
        if (StringUtils.isBlank(url)) {
            throw new IllegalArgumentException(String.format(Tips.ILLEGAL_PARAMETER, "url"));
        }
        if (Objects.isNull(responseType)) {
            throw new IllegalArgumentException(String.format(Tips.ILLEGAL_PARAMETER, "responseType"));
        }
        long start = System.currentTimeMillis();
        try {
            return getRestTemplate().postForObject(url, bodyParam, responseType);
        } catch (RuntimeException e) {
            log.error(Logs.HTTP_ERROR_LOG, url, JSON.toJSONString(bodyParam), ExceptionUtils.getStackTrace(e));
        } finally {
            log.info(Logs.HTTP_INFO_LOG, url, System.currentTimeMillis() - start);
        }
        return null;
    }

    /**
     * 发起post请求
     *
     * @param url 接口地址
     * @param bodyParam body内的参数
     * @param responseType 返回的类别
     * @param uriVariables url参数
     * @return T 对应的响应结果
     *         null 出错后
     * @author HuYiGong
     * @since 2021/6/7 10:27
     */
    @Nullable
    public static <T> T postForObject(String url, @Nullable Object bodyParam, Class<T> responseType, Map<String, ?> uriVariables) {
        if (StringUtils.isBlank(url)) {
            throw new IllegalArgumentException(String.format(Tips.ILLEGAL_PARAMETER, "url"));
        }
        if (Objects.isNull(responseType)) {
            throw new IllegalArgumentException(String.format(Tips.ILLEGAL_PARAMETER, "responseType"));
        }
        if (Objects.isNull(uriVariables)) {
            throw new IllegalArgumentException(String.format(Tips.ILLEGAL_PARAMETER, "uriVariables"));
        }
        long start = System.currentTimeMillis();
        try {
            return getRestTemplate().postForObject(url, bodyParam, responseType, uriVariables);
        } catch (RuntimeException e) {
            log.error(Logs.HTTP_ERROR_LOG, getUri(url, uriVariables), JSON.toJSONString(bodyParam), ExceptionUtils.getStackTrace(e));
        } finally {
            log.info(Logs.HTTP_INFO_LOG, url, System.currentTimeMillis() - start);
        }
        return null;
    }

    /**
     * 发起get请求
     *
     * @param url 接口地址
     * @param responseType 返回的类别
     * @return T 对应的响应结果
     *         null 出错后
     * @author HuYiGong
     * @since 2021/6/7 13:52
     */
    @Nullable
    public static <T> T getForObject(String url, Class<T> responseType) {
        if (StringUtils.isBlank(url)) {
            throw new IllegalArgumentException(String.format(Tips.ILLEGAL_PARAMETER, "url"));
        }
        long start = System.currentTimeMillis();
        try {
            return getRestTemplate().getForObject(url, responseType);
        } catch (RuntimeException e) {
            log.error(Logs.HTTP_ERROR_LOG, url, null, ExceptionUtils.getStackTrace(e));
        } finally {
            log.info(Logs.HTTP_INFO_LOG, url, System.currentTimeMillis() - start);
        }
        return null;
    }

    /**
     * 发起get请求
     *
     * @param url 接口地址
     * @param responseType 返回的类别
     * @return T 对应的响应结果
     *         null 出错后
     * @author HuYiGong
     * @since 2021/6/7 13:52
     */
    @Nullable
    public static <T> T getForObject(String url, Class<T> responseType, Map<String, ?> uriVariables) {
        if (StringUtils.isBlank(url)) {
            throw new IllegalArgumentException(String.format(Tips.ILLEGAL_PARAMETER, "url"));
        }
        if (Objects.isNull(uriVariables)) {
            throw new IllegalArgumentException(String.format(Tips.PARAMS_CANNOT_BE_NULL, "uriVariables"));
        }
        long start = System.currentTimeMillis();
        try {
            return getRestTemplate().getForObject(url, responseType, uriVariables);
        } catch (RuntimeException e) {
            log.error(Logs.HTTP_ERROR_LOG, getUri(url, uriVariables), null, ExceptionUtils.getStackTrace(e));
        } finally {
            log.info(Logs.HTTP_INFO_LOG, url, System.currentTimeMillis() - start);
        }
        return null;
    }

    /**
     * 发起请求
     *
     * @param url 接口地址
     * @param method 请求方法
     * @param requestEntity 请求实体
     * @param responseType 响应类型
     * @return T 对应的响应结果
     *         null 请求出错时
     * @author HuYiGong
     * @since 2021/6/7 17:12
     */
    @Nullable
    public static <T> T exchange(String url, HttpMethod method, @Nullable HttpEntity<?> requestEntity,
                                 ParameterizedTypeReference<T> responseType, Object... uriVariables) {
        if (StringUtils.isBlank(url)) {
            throw new IllegalArgumentException(String.format(Tips.ILLEGAL_PARAMETER, "url"));
        }
        if (Objects.isNull(method)) {
            throw new IllegalArgumentException(String.format(Tips.PARAMS_CANNOT_BE_NULL, "method"));
        }
        if (Objects.isNull(responseType)) {
            throw new IllegalArgumentException(String.format(Tips.PARAMS_CANNOT_BE_NULL, "responseType"));
        }
        long start = System.currentTimeMillis();
        try {
            ResponseEntity<T> entity = getRestTemplate().exchange(url, method, requestEntity, responseType, uriVariables);
            if (HttpStatus.OK.equals(entity.getStatusCode())) {
                return entity.getBody();
            } else {
                if (log.isDebugEnabled()) {
                    log.error(Logs.HTTP_ERROR_LOG, getUri(url, uriVariables), JSON.toJSONString(requestEntity), JSON.toJSONString(entity));
                }
            }
        } catch (RuntimeException e) {
            log.error(Logs.HTTP_ERROR_LOG, url, JSON.toJSONString(requestEntity), ExceptionUtils.getStackTrace(e));
        } finally {
            log.info(Logs.HTTP_INFO_LOG, url, System.currentTimeMillis() - start);
        }
        return null;
    }

    /**
     * 发起请求
     *
     * @param url 接口地址
     * @param method 请求方法
     * @param requestEntity 请求实体
     * @param responseType 响应类型
     * @param uriVariables url参数
     * @return ResponseEntity<T> 对应的响应结果
     *         null 请求出错时
     * @author HuYiGong
     * @since 2021/6/7 17:10
     */
    @Nullable
    public static <T> ResponseEntity<T> exchangeEntity(String url, HttpMethod method, @Nullable HttpEntity<?> requestEntity,
                                 ParameterizedTypeReference<T> responseType, Object... uriVariables) {
        if (StringUtils.isBlank(url)) {
            throw new IllegalArgumentException(String.format(Tips.ILLEGAL_PARAMETER, "url"));
        }
        if (Objects.isNull(method)) {
            throw new IllegalArgumentException(String.format(Tips.PARAMS_CANNOT_BE_NULL, "method"));
        }
        if (Objects.isNull(responseType)) {
            throw new IllegalArgumentException(String.format(Tips.PARAMS_CANNOT_BE_NULL, "responseType"));
        }
        if (Objects.isNull(uriVariables)) {
            throw new IllegalArgumentException(String.format(Tips.PARAMS_CANNOT_BE_NULL, "uriVariables"));
        }
        long start = System.currentTimeMillis();
        try {
            return getRestTemplate().exchange(url, method, requestEntity, responseType, uriVariables);
        } catch (RuntimeException e) {
            log.error(Logs.HTTP_ERROR_LOG, getUri(url, uriVariables), JSON.toJSONString(requestEntity), ExceptionUtils.getStackTrace(e));
        } finally {
            log.info(Logs.HTTP_INFO_LOG, url, System.currentTimeMillis() - start);
        }
        return null;
    }

    /**
     * 获取uri
     * 例如：
     * url：https://192.168.1.3/{test}?abc=2
     * uriVariables: {"test":"hello"}
     * result：https://192.168.1.3/hello?abc=2 对应的URI
     *
     * @param url 接口地址
     * @param uriVariables url参数
     * @return java.net.URI
     * @author HuYiGong
     * @since 2021/6/9 9:47
     */
    public static URI getUri(String url, Object... uriVariables) {
        if (StringUtils.isBlank(url)) {
            throw new IllegalArgumentException(String.format(Tips.ILLEGAL_PARAMETER, "url"));
        }
        if (Objects.isNull(uriVariables)) {
            throw new IllegalArgumentException(String.format(Tips.ILLEGAL_PARAMETER, "uriVariables"));
        }
        return getRestTemplate().getUriTemplateHandler().expand(url, uriVariables);
    }

    /**
     * 获取uri
     * 例如：
     * url：https://192.168.1.3/{test}?abc=2
     * uriVariables: {"test":"hello"}
     * result：https://192.168.1.3/hello?abc=2 对应的URI
     *
     * @param url 接口地址
     * @param uriVariables url参数
     * @return java.net.URI
     * @author HuYiGong
     * @since 2021/6/7 15:46
     */
    public static URI getUri(String url, Map<String, ?> uriVariables) {
        if (StringUtils.isBlank(url)) {
            throw new IllegalArgumentException(String.format(Tips.ILLEGAL_PARAMETER, "url"));
        }
        if (Objects.isNull(uriVariables)) {
            throw new IllegalArgumentException(String.format(Tips.ILLEGAL_PARAMETER, "uriVariables"));
        }
        return getRestTemplate().getUriTemplateHandler().expand(url, uriVariables);
    }

    /**
     * 验证连通性，只有GET，POST，PUT，DELETE请求可以通过验证
     *
     * @param url 接口地址
     * @param timeout 超时时间
     * @param uriVariables uri参数
     * @return boolean 是否连通
     * @author HuYiGong
     * @since 2021/6/8 9:41
     */
    public static boolean verifyConnectivity(String url, int timeout, Object... uriVariables) {
        if (StringUtils.isBlank(url)) {
            throw new IllegalArgumentException(String.format(Tips.ILLEGAL_PARAMETER, url));
        }
        try {
            Set<HttpMethod> optionsForAllow = getRestTemplate(timeout).optionsForAllow(url, uriVariables);
            HttpMethod[] supportedMethods = {HttpMethod.GET, HttpMethod.POST, HttpMethod.PUT, HttpMethod.DELETE};
            return Arrays.stream(supportedMethods).anyMatch(optionsForAllow::contains);
        } catch (RuntimeException e) {
            return false;
        }
    }
}
