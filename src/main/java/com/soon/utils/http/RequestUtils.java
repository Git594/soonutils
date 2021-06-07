package com.soon.utils.http;

import com.soon.utils.consts.Tips;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Objects;
import java.util.Optional;

/**
 * Created on 2021/5/12.
 *
 * @author Soon
 */
public class RequestUtils {
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
}
