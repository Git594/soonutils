package com.soon.utils.security;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

/**
 * Aes加密
 *
 * @author HuYiGong
 * @since 2021/6/16
 **/
public class Aes {
    private Aes() {}

    /**
     * 使用KeyGenerator双向加密，AES，注意这里转化为字符串的时候是用Base64进行加解密
     *
     * @param content 加密或解密的原文
     * @param key 秘钥
     * @param isEncode 是否加密
     * @return java.lang.String
     * @author HuYiGong
     * @since 2021/6/16 18:50
     */
    private static String generateBase64Aes(String content, String key, boolean isEncode) {
        try {
            SecretKeySpec sks = new SecretKeySpec(key.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            if (isEncode) {
                cipher.init(Cipher.ENCRYPT_MODE, sks);
                return Base64.getEncoder().encodeToString(cipher.doFinal(content.getBytes()));
            } else {
                cipher.init(Cipher.DECRYPT_MODE, sks);
                return new String(cipher.doFinal(Base64.getDecoder().decode(content)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 使用KeyGenerator双向加密，AES
     *
     * @param bytes 加密或解密的字节数组
     * @param key 秘钥
     * @param isEncode 是否加密
     * @return java.lang.String
     * @author HuYiGong
     * @since 2021/6/16 18:50
     */
    private static byte[] generateAes(byte[] bytes, String key, boolean isEncode) {
        try {
            SecretKeySpec sks = new SecretKeySpec(key.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            if (isEncode) {
                cipher.init(Cipher.ENCRYPT_MODE, sks);
                return cipher.doFinal(bytes);
            } else {
                cipher.init(Cipher.DECRYPT_MODE, sks);
                return cipher.doFinal(bytes);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new byte[0];
        }
    }

    /**
     * 使用AES加密算法经行加密（可逆）
     *
     * @param content 需要加密的密文
     * @param key 秘钥
     * @return 加密后的密文
     * @author HuYiGong
     * @since 2021/6/16 19:43
     */
    public static String encode(String content, String key) {
        return generateBase64Aes(content, key, true);
    }

    /**
     * 对使用AES加密算法的密文进行解密
     *
     * @param content 需要解密的密文
     * @param key 秘钥
     * @return 原文
     * @author HuYiGong
     * @since 2021/6/16 19:43
     */
    public static String decode(String content, String key) {
        return generateBase64Aes(content, key, false);
    }


    /**
     * 使用AES加密算法经行加密（可逆）
     *
     * @param bytes 需要加密的字节数组
     * @param key 秘钥
     * @return 加密后的字节数组
     * @author HuYiGong
     * @since 2021/6/16 19:43
     */
    public static byte[] encode(byte[] bytes, String key) {
        return generateAes(bytes, key, true);
    }

    /**
     * 对使用AES加密算法的密文进行解密
     *
     * @param bytes 需要解密的字节数组
     * @param key 秘钥
     * @return 解密后的字节数组
     * @author HuYiGong
     * @since 2021/6/16 19:43
     */
    public static byte[] decode(byte[] bytes, String key) {
        return generateAes(bytes, key, false);
    }
}
