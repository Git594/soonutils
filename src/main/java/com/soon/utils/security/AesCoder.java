package com.soon.utils.security;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Aes加密
 *
 * @author HuYiGong
 * @since 2021/6/16
 **/
public class AesCoder {
    private AesCoder() {}

    private static final String ALGORITHM = "AES";
    private static final String SECURE_ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final SecureRandom random = new SecureRandom();

    public static byte[] generateKey() {
        try {
            //实例化密钥生成器
            KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM);
            //初始化密钥生成器
            SecretKey secretKey = keyGenerator.generateKey();

            return secretKey.getEncoded();
        } catch (NoSuchAlgorithmException e) {
            return new byte[0];
        }
    }

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
    private static String generateBase64Aes(String content, byte[] key, boolean isEncode) {
        try {
            byte[] byteIv = new byte[16];
            random.nextBytes(byteIv);
            SecretKeySpec sks = new SecretKeySpec(key, ALGORITHM);
            IvParameterSpec iv = new IvParameterSpec(byteIv);
            Cipher cipher = Cipher.getInstance(SECURE_ALGORITHM);
            if (isEncode) {
                cipher.init(Cipher.ENCRYPT_MODE, sks, iv);
                return Base64.getEncoder().encodeToString(cipher.doFinal(content.getBytes()));
            } else {
                cipher.init(Cipher.DECRYPT_MODE, sks, iv);
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
    private static byte[] generateAes(byte[] bytes, byte[] key, boolean isEncode) {
        try {
            byte[] byteIv = new byte[16];
            random.nextBytes(byteIv);
            SecretKeySpec sks = new SecretKeySpec(key, ALGORITHM);
            IvParameterSpec iv = new IvParameterSpec(byteIv);
            Cipher cipher = Cipher.getInstance(SECURE_ALGORITHM);
            if (isEncode) {
                cipher.init(Cipher.ENCRYPT_MODE, sks, iv);
            } else {
                cipher.init(Cipher.DECRYPT_MODE, sks, iv);
            }
            return cipher.doFinal(bytes);
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
    public static String encode(String content, byte[] key) {
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
    public static String decode(String content, byte[] key) {
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
    public static byte[] encode(byte[] bytes, byte[] key) {
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
    public static byte[] decode(byte[] bytes, byte[] key) {
        return generateAes(bytes, key, false);
    }

    public static void main(String[] args) {
        byte[] key = generateKey();
        System.out.println("生成的key：" + Base64.getEncoder().encodeToString(key));
        String content = "测试一下";
        System.out.println("原文：" + content);
        String ciphertext = encode(content, key);
        System.out.println("密文：" + ciphertext);
        String text = decode(ciphertext, key);
        System.out.println("解密后：" + text);
    }
}
