package com.soon.utils.security;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

/**
 * 非对称加密算法RSA算法组件
 *
 * @author HuYiGong
 * @since 2021/6/17
 **/
public class RsaCoder {

    /**
     * 非对称密钥算法名称
     */
    private static final String KEY_ALGORITHM = "RSA";

    /**
     * 密钥长度，DH算法的默认密钥长度是1024
     * 密钥长度必须是64的倍数，在512到65536位之间
     */
    private static final int KEY_SIZE = 512;

    /**
     * 加密块大小
     */
    private static final int ENCODE_SIZE = KEY_SIZE / 8 - 11;

    /**
     * 解密块大小
     */
    private static final int DECODE_SIZE = ENCODE_SIZE + 11;

    /**
     * 公钥的键
     */
    private static final String PUBLIC_KEY = "RSAPublicKey";

    /**
     * 私钥的键
     */
    private static final String PRIVATE_KEY = "RSAPrivateKey";

    /**
     * 生成密钥对
     *
     * @return java.util.Map<java.lang.String,java.lang.Object>
     *          密钥的Map
     * @author HuYiGong
     * @since 2021/6/18 11:15
     */
    public static Map<String, Object> generateKey() {
        try {
            //实例化密钥生成器
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(KEY_ALGORITHM);
            //初始化密钥生成器
            keyPairGenerator.initialize(KEY_SIZE);
            //生成密钥对
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            //甲方公钥
            RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
            //甲方私钥
            RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
            //将密钥存储在map中
            Map<String, Object> keyMap = new HashMap<>(4);
            keyMap.put(PUBLIC_KEY, publicKey);
            keyMap.put(PRIVATE_KEY, privateKey);
            return keyMap;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 通过私钥加密
     *
     * @param data 待加密数据
     * @param key  密钥
     * @return byte[] 加密数据
     * @author HuYiGong
     * @since 2021/6/18 11:16
     */
    public static byte[] encryptByPrivateKey(byte[] data, byte[] key) throws GeneralSecurityException {
        PrivateKey privateKey = toPrivateKey(key);
        Cipher cipher = Cipher.getInstance(privateKey.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, privateKey);
        return handleByBlock(cipher, data, ENCODE_SIZE);
    }

    /**
     * 公钥加密
     *
     * @param data 待加密数据
     * @param key  密钥
     * @return byte[] 加密数据
     * @author HuYiGong
     * @since 2021/6/18 11:18
     */
    public static byte[] encryptByPublicKey(byte[] data, byte[] key) throws GeneralSecurityException {
        PublicKey publicKey = toPublicKey(key);
        Cipher cipher = Cipher.getInstance(publicKey.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return handleByBlock(cipher, data, ENCODE_SIZE);
    }

    /**
     * 私钥解密
     *
     * @param data 待解密数据
     * @param key  密钥
     * @return byte[] 解密数据
     * @author HuYiGong
     * @since 2021/6/18 11:39
     */
    public static byte[] decryptByPrivateKey(byte[] data, byte[] key) throws GeneralSecurityException {
        PrivateKey privateKey = toPrivateKey(key);
        Cipher cipher = Cipher.getInstance(privateKey.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return handleByBlock(cipher, data, DECODE_SIZE);
    }

    /**
     * 公钥解密
     *
     * @param data 待解密数据
     * @param key  密钥
     * @return byte[] 解密数据
     * @author HuYiGong
     * @since 2021/6/18 11:39
     */
    public static byte[] decryptByPublicKey(byte[] data, byte[] key) throws GeneralSecurityException {
        PublicKey publicKey = toPublicKey(key);
        Cipher cipher = Cipher.getInstance(publicKey.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, publicKey);
        return handleByBlock(cipher, data, DECODE_SIZE);
    }

    /**
     * 将字节数组转化为私钥
     *
     * @param key 私钥的字节数组
     * @return java.security.PublicKey
     * @author HuYiGong
     * @since 2021/6/17 14:52
     */
    private static PrivateKey toPrivateKey(byte[] key) throws GeneralSecurityException {
        //实例化密钥工厂
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        //取得私钥
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(key);
        //生成私钥
        return keyFactory.generatePrivate(pkcs8KeySpec);
    }

    /**
     * 将字节数组转化为公钥
     *
     * @param key 公钥的字节数组
     * @return java.security.PublicKey
     * @author HuYiGong
     * @since 2021/6/17 14:52
     */
    private static PublicKey toPublicKey(byte[] key) throws GeneralSecurityException {
        //实例化密钥工厂
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        //密钥材料转换
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(key);
        //产生公钥
        return keyFactory.generatePublic(x509KeySpec);
    }

    /**
     * 对于超长数据，进行分段处理
     *
     * @param cipher 初始化后的Cipher对象
     * @param data 待加解密的数据
     * @param block 每段的长度
     * @return byte[]
     * @author HuYiGong
     * @since 2021/6/18 11:47
     */
    private static byte[] handleByBlock(Cipher cipher, byte[] data, int block) throws GeneralSecurityException {
        int offset = 0;
        int length = data.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buff;
        // 分段加密
        while (length > offset) {
            int rest = length - offset;
            if (rest > block) {
                buff = cipher.doFinal(data, offset, block);
            } else {
                buff = cipher.doFinal(data, offset, rest);
            }
            out.write(buff, 0, buff.length);
            offset += block;
        }
        return out.toByteArray();
    }

/// demo
//    public static void main(String[] args) throws Exception {
//        //初始化密钥
//        //生成密钥对
//        Map<String, Object> keyMap = RsaCoder.generateKey();
//        //公钥
//        RSAPublicKey publicKey = (RSAPublicKey) keyMap.get(PUBLIC_KEY);
//        //私钥
//        RSAPrivateKey privateKey = (RSAPrivateKey) keyMap.get(PRIVATE_KEY);
//        System.out.println("公钥：\n" + Base64.getEncoder().encodeToString(publicKey.getEncoded()));
//        System.out.println("私钥：\n" + Base64.getEncoder().encodeToString(privateKey.getEncoded()));
//
//        System.out.println("================密钥对构造完毕,甲方将公钥公布给乙方，开始进行加密数据的传输=============");
//        String str = "RSA密码交换算法";
//        System.out.println("\n===========甲方向乙方发送加密数据==============");
//        System.out.println("原文:" + str);
//        //甲方进行数据的加密
//        byte[] code1 = RsaCoder.encryptByPrivateKey(str.getBytes(), privateKey.getEncoded());
//        System.out.println("加密后的数据：" + Base64.getEncoder().encodeToString(code1));
//        System.out.println("===========乙方使用甲方提供的公钥对数据进行解密==============");
//        //乙方进行数据的解密
//        byte[] decode1 = RsaCoder.decryptByPublicKey(code1, publicKey.getEncoded());
//        System.out.println("乙方解密后的数据：" + new String(decode1) + "\n\n");
//
//        System.out.println("===========反向进行操作，乙方向甲方发送数据==============\n\n");
//
//        str = "乙方向甲方发送数据RSA算法测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试" +
//                "测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试";
//
//        System.out.println("原文:" + str);
//
//        //乙方使用公钥对数据进行加密
//        byte[] code2 = RsaCoder.encryptByPublicKey(str.getBytes(), publicKey.getEncoded());
//        System.out.println("===========乙方使用公钥对数据进行加密==============");
//        System.out.println("加密后的数据：" + Base64.getEncoder().encodeToString(code2));
//
//        System.out.println("=============乙方将数据传送给甲方======================");
//        System.out.println("===========甲方使用私钥对数据进行解密==============");
//
//        //甲方使用私钥对数据进行解密
//        byte[] decode2 = RsaCoder.decryptByPrivateKey(code2, privateKey.getEncoded());
//
//        System.out.println("甲方解密后的数据：" + new String(decode2));
//    }
}
