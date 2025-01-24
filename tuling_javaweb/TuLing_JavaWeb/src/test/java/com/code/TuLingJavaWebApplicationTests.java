package com.code;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest
class TuLingJavaWebApplicationTests {


    @Test
    void contextLoads() {
    }
    /**
     * 测试JWT令牌的生成
     */
    @Test
    public void testGenJwt(){
        Map<String, Object> claims = new HashMap<>();
        claims.put("id",1);
        claims.put("name","Tommy");
        String jwt = Jwts.builder()
                .signWith(SignatureAlgorithm.HS256,"TommyLee")//签名算法
                .setClaims(claims)//自定义内容(载荷)
                .setExpiration(new Date(System.currentTimeMillis()+3600 * 1000))//设置为1个小时
                .compact();
        System.out.println(jwt);
    }

    /**
     * JWT令牌解析
     */
    @Test
    public void testParseJwt(){
        Claims claims = Jwts.parser()
                .setSigningKey("TommyLee")//指定签名密钥
                //密钥要与创建的密钥一直才行
                .parseClaimsJws("eyJhbGciOiJIUzI1NiJ9.eyJuYW1lIjoiVG9tbXkiLCJpZCI6MSwiZXhwIjoxNzM3MDgwMjUxfQ.4Du9P9iE07ZHLknfnJNGnZFmQeUPkygc1kl98JjZKaA")
                .getBody();
        System.out.println(claims);
    }

}
