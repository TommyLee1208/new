package com.code.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;

@Component
public class JwtUtils {

    // 直接在类中定义签名密钥和过期时间
    private static final String SIGN_KEY = "TommyLee1208";
    private static final Long EXPIRE = 43200000L; // 12 小时的毫秒数

    /**
     * 生成JWT令牌
     * @param claims JWT第二部分负载 payload 中存储的内容
     * @return JWT令牌字符串
     */
    public String generateJwt(Map<String, Object> claims) {
        return Jwts.builder()
                .addClaims(claims)
                .signWith(SignatureAlgorithm.HS256, SIGN_KEY)
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRE))
                .compact();
    }

    /**
     * 解析JWT令牌
     * @param jwt JWT令牌
     * @return JWT第二部分负载 payload 中存储的内容
     */
    public Claims parseJWT(String jwt) {
        try {
            return Jwts.parser()
                    .setSigningKey(SIGN_KEY)
                    .parseClaimsJws(jwt)
                    .getBody();
        } catch (Exception e) {
            // 如果解析失败，记录错误日志并抛出异常或返回 null
            throw new RuntimeException("无效的 JWT 令牌", e);
        }
    }
}