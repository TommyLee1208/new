package com.code.filter;

import com.alibaba.fastjson.JSONObject;
import com.code.pojo.Result;
import com.code.utils.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
public class LoginCheckFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;

    @Autowired
    public LoginCheckFilter(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        // 获取请求的 URL 并去除多余的空格（包括解码后的 %20）
        String originalUrl = request.getRequestURI();
        String normalizedUrl = originalUrl.trim().replaceAll("%20", "");

        log.info("原始请求的 URL 是 {}", originalUrl);
        log.info("规范化后的请求 URL 是 {}", normalizedUrl);

        // 判断是否为登录操作，如果是则放行
        if (normalizedUrl.contains("login")) {
            log.info("登录操作, 放行...");
            chain.doFilter(request, response);
            return;
        }

        // 获取请求头中的 JWT 令牌（通常是 Authorization: Bearer <token>）
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            sendUnauthorizedResponse(response, "未提供有效的授权信息");
            return;
        }

        String jwt = authHeader.substring(7); // 去掉 "Bearer " 前缀

        try {
            // 解析JWT令牌并验证其有效性
            jwtUtils.parseJWT(jwt);
            log.info("令牌合法，放行路径：{}", normalizedUrl);

            // 使用规范化后的URL继续过滤器链
            HttpServletRequest wrappedRequest = new HttpServletRequestWrapper(request) {
                @Override
                public String getRequestURI() {
                    return normalizedUrl;
                }
            };

            chain.doFilter(wrappedRequest, response);
        } catch (Exception e) {
            log.error("解析令牌失败：{}", e.getMessage());
            sendUnauthorizedResponse(response, "无效的令牌");
        }
    }

    private void sendUnauthorizedResponse(HttpServletResponse resp, String message) throws IOException {
        resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        resp.setContentType("application/json;charset=UTF-8");
        resp.getWriter().write(JSONObject.toJSONString(Result.error(message)));
    }
}