package com.soon.utils.security;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

/**
 * TOTP: Time-Based One-Time Password Algorithm
 * TOTP = HOTP(K, T) // T is an integer
 * and represents the number of time steps between the initial counter
 * time T0 and the current Unix time
 * More specifically, T = (Current Unix time - T0) / X, where the
 * default floor function is used in the computation.
 * HOTP(K,C) = Truncate(HMAC-SHA-1(K,C))
 * PWD(K,C,digit) = HOTP(K,C) mod 10^Digit
 * K 代表我们在认证服务器端以及密码生成端（客户设备）之间共享的密钥，在 RFC 4226 中，作者要求共享密钥最小长度是 128 位，而作者本身推荐使用 160 位长度的密钥
 * C 表示事件计数的值，8 字节的整数，称为移动因子（moving factor），需要注意的是，这里的 C 的整数值需要用二进制的字符串表达，比如某个事件计数为 3，则C是 "11"（此处省略了前面的二进制的数字0）
 * HMAC-SHA-1 表示对共享密钥以及移动因子进行 HMAC 的 SHA1 算法加密，得到 160 位长度（20字节）的加密结果
 * Truncate 即截断函数
 * Digit 指定动态密码长度，比如我们常见的都是 6 位长度的动态密码
 *
 * @author HuYiGong
 * @since 2021/4/9
 **/
public class Totp {
    /**
     * 安全秘钥（K）
     */
    private String securityKey;

    /**
     * 自定义时间（T0）
     */
    private long customizeTime;

    /**
     * 步长（X）
     */
    private int step;

    /**
     * 生成的密码位数（Digit）
     */
    private int digit;

    /**
     * 柔性时间回溯
     */
    private int flexTime;

    /**
     * 获取实例
     *
     * @param securityKey 安全秘钥
     * @return com.soon.utils.security.Totp
     *          实例
     * @author HuYiGong
     * @since 2021/4/9 16:25
     */
    public Totp getInstant(String securityKey) {
        return new Totp(securityKey, 0, 30000, 6, 5000);
    }

    /**
     * 获取实例
     *
     * @param securityKey 安全秘钥
     * @param step 步长（毫秒），即密码生效时间，不为0
     * @param digit 生成的密码位数
     * @return com.soon.utils.security.Totp
     *          实例
     * @author HuYiGong
     * @since 2021/4/9 16:31
     */
    public Totp getInstant(String securityKey, int step, int digit) {
        if (step == 0) {
            throw new IllegalArgumentException("Step cannot be 0");
        }
        return new Totp(securityKey, 0, step, digit, 5000);
    }

    /**
     * 获取实例
     *
     * @param securityKey 安全秘钥
     * @param step 步长（毫秒）
     * @param digit 生成的密码位数
     * @param flexTime 弹性时间回溯
     * @param customizeTime 自定义时间（时间戳）
     * @return com.soon.utils.security.Totp
     * @author HuYiGong
     * @since 2021/4/9 16:33
     */
    public Totp getInstant(String securityKey, int step, int digit, int flexTime, long customizeTime) {
        if (step == 0) {
            throw new IllegalArgumentException("Step cannot be 0");
        }
        return new Totp(securityKey, customizeTime, step, digit, flexTime);
    }

    /**
     * 生成totp
     *
     * @return java.lang.String
     *          totp结果
     * @author HuYiGong
     * @since 2021/4/9 18:31
     */
    public String generate() throws InvalidKeyException {
        long timeFactor = computeTimeFactor(System.currentTimeMillis());
        byte[] hmacSha1Res = hmacSha1(securityKey, String.valueOf(timeFactor));
        return truncate(hmacSha1Res);
    }

    /**
     * 截断
     *
     * @param hmacSha1Res HmacSHA1加密后的记过
     * @return java.lang.String
     *          totp结果
     * @author HuYiGong
     * @since 2021/4/9 18:29
     */
    private String truncate(byte[] hmacSha1Res) {
        return null;
    }

    /**
     * 根据HmacSHA1算法加密
     *
     * @param key 秘钥
     * @param data 待加密数据
     * @return byte[]
     * @author HuYiGong
     * @since 2021/4/9 18:28
     */
    private byte[] hmacSha1(String key, String data) throws InvalidKeyException {
        try {
            String algorithm = "HmacSHA1";
            Mac mac = Mac.getInstance(algorithm);
            SecretKeySpec hmacKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), algorithm);
            mac.init(hmacKey);
            return mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return new byte[0];
        }
    }

    /**
     * 计算时间因子
     *
     * @param targetTime 目标时间戳
     * @return long 时间因子
     * @author HuYiGong
     * @since 2021/4/9 16:48
     */
    private long computeTimeFactor(long targetTime) {
        return (targetTime - customizeTime) / step;
    }

    private Totp(String securityKey, long customizeTime, int step, int digit, int flexTime) {
        this.securityKey = securityKey;
        this.customizeTime = customizeTime;
        this.step = step;
        this.digit = digit;
        this.flexTime = flexTime;
    }
}
