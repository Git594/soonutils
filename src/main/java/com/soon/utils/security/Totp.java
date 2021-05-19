package com.soon.utils.security;

import org.apache.commons.lang3.RandomStringUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.NumberFormat;

/**
 * TOTP: Time-Based One-Time Password Algorithm
 * TOTP = HOTP(K, T) // T is an integer
 * and represents the number of time steps between the initial counter
 * time T0 and the current Unix time
 * More specifically, T = (Current Unix time - T0) / X, where the
 * default floor function is used in the computation.
 * HOTP(K,C) = Truncate(HMAC-SHA-1(K,C))
 * PWD(K,C,digit) = HOTP(K,C) mod 10^Digit
 * K 代表我们在认证服务器端以及密码生成端（客户设备）之间共享的密钥，在 RFC 4226 中，作者要求共享密钥最小长度是 128 位，而作者
 *   本身推荐使用 160 位长度的密钥
 * C 表示事件计数的值，8 字节的整数，称为移动因子（moving factor），需要注意的是，这里的 C 的整数值需要用二进制的字符串表达，
 *   比如某个事件计数为 3，则C是 "11"（此处省略了前面的二进制的数字0）
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
    private final String securityKey;

    /**
     * 自定义时间（T0）
     */
    private final long customizeTime;

    /**
     * 步长（X）
     */
    private final int step;

    /**
     * 生成的密码位数（Digit）
     */
    private final int digit;

    /**
     * 柔性时间回溯
     */
    private final int flexedTime;

    /**
     * 柔性验证
     *
     * @param userName 用户名
     * @param password 密码
     * @param code 动态密码
     * @return boolean
     * @author HuYiGong
     * @since 2021/5/14 17:21
     */
    public boolean verifyFlexibly(String userName, String password, String code) {
        Totp totp = getInstant(securityKey + userName + password);
        try {
            if (totp.generateFlexibly().equals(code)) {
                return true;
            } else {
                return totp.generate().equals(code);
            }
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 获取实例
     *
     * @param securityKey 安全秘钥
     * @return com.soon.utils.security.Totp
     *          实例
     * @author HuYiGong
     * @since 2021/4/9 16:25
     */
    public static Totp getInstant(String securityKey) {
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
    public static Totp getInstant(String securityKey, int step, int digit) {
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
    public static Totp getInstant(String securityKey, int step, int digit, int flexTime, long customizeTime) {
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
     * @throws InvalidKeyException 加密方法名（HmacSHA1）错误时抛出
     */
    public String generate() throws InvalidKeyException {
        long timeFactor = computeTimeFactor(System.currentTimeMillis());
        byte[] hmacSha1Res = hmacSha1(securityKey, String.valueOf(timeFactor));
        return truncate(hmacSha1Res);
    }

    /**
     * 生成柔性totp
     *
     * @return java.lang.String
     *          totp结果
     * @author HuYiGong
     * @since 2021/4/9 18:31
     * @throws InvalidKeyException 加密方法名（HmacSHA1）错误时抛出
     */
    public String generateFlexibly() throws InvalidKeyException {
        long timeFactor = computeTimeFactor(System.currentTimeMillis() - flexedTime);
        byte[] hmacSha1Res = hmacSha1(securityKey, String.valueOf(timeFactor));
        return truncate(hmacSha1Res);
    }

    /**
     * 截断
     *
     * 先从第一步通过 SHA-1 算法加密得到的 20 字节长度的结果中选取最后一个字节的低字节位的 4 位（注意：动态密码算法中采用的大
     * 端(big-endian)存储）；
     * 将这 4 位的二进制值转换为无标点数的整数值，得到 0 到 15（包含 0 和 15）之间的一个数，这个数字作为 20 个字节中从 0 开始
     * 的偏移量；
     * 接着从指定偏移位开始，连续截取 4 个字节（32 位），最后返回 32 位中的后面 31 位。
     * 回到算法本身，在获得 31 位的截断结果之后，我们将其又转换为无标点的整数值，这个值的取值范围是 0 ~ 2^31，也即
     * 0 ~ 2.147483648E9，最后我们将这个数对10的乘方（digit 指数范围 1-10）取模，得到一个余值，对其前面补0得到指定位数的字符串
     *
     * @param hmacSha1Res HmacSHA1加密后的记过
     * @return java.lang.String
     *          totp结果
     * @author HuYiGong
     * @since 2021/4/9 18:29
     */
    private String truncate(byte[] hmacSha1Res) {
        int offset = hmacSha1Res[hmacSha1Res.length - 1] & 0xf;
        long truncation = (hmacSha1Res[offset] & 0x7f) << 24 | (hmacSha1Res[offset + 1] & 0xff) << 16
                | (hmacSha1Res[offset + 2] & 0xff) << 8 | hmacSha1Res[offset + 3] & 0xff;
        long mold = (long) (truncation % Math.pow(10, digit));
        NumberFormat format = NumberFormat.getInstance();
        format.setGroupingUsed(false);
        format.setMinimumIntegerDigits(digit);
        format.setMaximumIntegerDigits(digit);
        return format.format(mold);
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

    private Totp(String securityKey, long customizeTime, int step, int digit, int flexedTime) {
        this.securityKey = securityKey;
        this.customizeTime = customizeTime;
        this.step = step;
        this.digit = digit;
        this.flexedTime = flexedTime;
    }
}
