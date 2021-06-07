package com.soon.utils.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.soon.utils.consts.Tips;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.Map;
import java.util.Objects;

/**
 * token工具类
 *
 * @author HuYiGong
 * @since 2021/6/2
 **/
public class Token {
    private final String secretKey;
    private final long expireTime;
    private static Token instance;

    private Token(String secretKey, long expireTime) {
        this.secretKey = secretKey;
        this.expireTime = expireTime;
    }

    /**
     * 获取实例
     *
     * @return com.soon.utils.ApplicationContextUtils 实例
     * @author HuYiGong
     * @since 2021/5/31 11:31
     */
    public static Token getInstance(String secretKey, long expireTime) {
        Objects.requireNonNull(secretKey, String.format(Tips.PARAMS_CANNOT_BE_NULL, "secretKey"));
        if (expireTime > 0) {
            throw new IllegalArgumentException(String.format(Tips.ILLEGAL_PARAMETER, "expireTime"));
        }
        if (Objects.isNull(instance)) {
            synchronized (Token.class) {
                if (Objects.isNull(instance)) {
                    instance = new Token(secretKey, expireTime);
                }
            }
        }
        return instance;
    }

    /**
     * 创建token
     *
     * @param claim 声称
     * @return java.lang.String
     *         token
     * @author HuYiGong
     * @since 2021/6/3 10:36
     */
    public String create(Map<String, Object> claim) {
        Date date = new Date(System.currentTimeMillis() + expireTime);
        Algorithm algorithm = Algorithm.HMAC256(secretKey);
        return JWT.create()
                .withPayload(claim)
                .withIssuedAt(new Date())
                .withExpiresAt(date)
                .sign(algorithm);
    }

    /**
     * 验证
     *
     * @param token token字符串
     * @return boolean
     *         是否验证通过
     * @author HuYiGong
     * @since 2021/6/3 15:06
     */
    public boolean verify(String token) {
        if (StringUtils.isBlank(token)) {
            throw new IllegalArgumentException(String.format(Tips.ILLEGAL_PARAMETER, "token"));
        }
        try {
            Algorithm algorithm = Algorithm.HMAC256(secretKey);
            JWTVerifier verifier = JWT.require(algorithm).build();
            verifier.verify(token);
            return true;
        } catch (JWTVerificationException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 根据key获取claim（声明）
     *
     * @param token token字符串
     * @param key claim（声明）的键
     * @return com.auth0.jwt.interfaces.Claim
     *         声明
     * @author HuYiGong
     * @since 2021/6/3 15:15
     */
    public Claim getClaim(String token, String key) {
        if (StringUtils.isBlank(token)) {
            throw new IllegalArgumentException(String.format(Tips.ILLEGAL_PARAMETER, "token"));
        }
        Algorithm algorithm = Algorithm.HMAC256(secretKey);
        JWTVerifier verifier = JWT.require(algorithm).build();
        DecodedJWT decodedJwt = verifier.verify(token);
        return decodedJwt.getClaim(key);
    }
}
