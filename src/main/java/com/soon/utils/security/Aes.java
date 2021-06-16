package com.soon.utils.security;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
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
     * @param content 加密的原文
     * @param key 加密的秘钥
     * @param isEncode 是否加密
     * @return java.lang.String
     * @author HuYiGong
     * @since 2021/6/16 18:50
     */
    private static String generateAes(String content, String key, boolean isEncode) {
        try {
            SecretKeySpec sks = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");
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
     * 使用AES加密算法经行加密（可逆）
     *
     * @param content 需要加密的密文
     * @param key 秘钥
     * @return java.lang.String
     * @author HuYiGong
     * @since 2021/6/16 19:43
     */
    public static String encode(String content, String key) {
        return generateAes(content, key, true);
    }

    /**
     * 对使用AES加密算法的密文进行解密
     *
     * @param content 需要解密的密文
     * @param key 秘钥
     * @return java.lang.String
     * @author HuYiGong
     * @since 2021/6/16 19:43
     */
    public static String decode(String content, String key) {
        return generateAes(content, key, false);
    }
}
