package com.code.utils;

import io.jsonwebtoken.Claims;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    public JwtAuthenticationFilter(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    /**
     * 从 Claims 中获取用户的角色列表
     */
    private List<SimpleGrantedAuthority> getAuthorities(Claims claims) {
        if (claims.containsKey("roles")) {
            @SuppressWarnings("unchecked")
            List<String> roles = (List<String>) claims.get("roles");
            return roles.stream()
                    .map(role -> role.startsWith("ROLE_") ? role : "ROLE_" + role)
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
        }
        // 默认角色为 USER，但如果是管理员登录，这应该被覆盖
        return Arrays.asList(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    protected void doFilterInternal(jakarta.servlet.http.HttpServletRequest request, jakarta.servlet.http.HttpServletResponse response, jakarta.servlet.FilterChain filterChain) throws jakarta.servlet.ServletException, IOException {
        String header = request.getHeader("Authorization");

        UsernamePasswordAuthenticationToken authentication = null;
        if (header != null && header.startsWith("Bearer ")) {
            try {
                String token = header.substring(7); // 提取JWT令牌
                Claims claims = jwtUtils.parseJWT(token);

                // 打印出所有的 Claims 键值对，用于调试
                logger.debug("JWT Claims: {}", claims.entrySet().stream()
                        .map(entry -> entry.getKey() + ": " + entry.getValue())
                        .collect(Collectors.joining(", ")));

                // 检查是否是管理员请求路径
                boolean isAdminPath = request.getRequestURI().startsWith("/manager/");

                if (isAdminPath) {
                    // 对于管理员路径，需要检查 sub 字段和 ROLE_ADMIN 权限
                    String subject = claims.getSubject();
                    if (subject == null || subject.isEmpty()) {
                        logger.warn("Admin JWT Token 中未找到有效的 subject");
                        response.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid JWT token: Missing subject");
                        return;
                    }

                    List<SimpleGrantedAuthority> authorities = getAuthorities(claims);
                    boolean isAdmin = authorities.stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

                    if (!isAdmin) {
                        logger.warn("Admin endpoint access denied for non-admin user");
                        response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied: Not an admin user");
                        return;
                    }

                    // 创建 UserDetails 对象并设置认证信息
                    org.springframework.security.core.userdetails.User userDetails =
                            new org.springframework.security.core.userdetails.User(
                                    subject, "", authorities);

                    authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                } else {
                    // 对于普通用户路径，尝试从多个字段中获取用户名
                    Object usernameObj = claims.get("username") != null ? claims.get("username") :
                            (claims.get("name") != null ? claims.get("name") : null);
                    Long expSeconds = claims.getExpiration() != null ? claims.getExpiration().getTime() / 1000 : null;

                    // 打印 username 字段的具体值，用于调试
                    logger.debug("Extracted username from JWT: {}", usernameObj);

                    if (usernameObj == null || !(usernameObj instanceof String) || ((String) usernameObj).isEmpty()) {
                        logger.warn("User JWT Token validation failed. Missing or invalid username.");
                        response.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid JWT token: Missing or invalid username");
                        return;
                    }

                    String username = (String) usernameObj;

                    // 将 exp 转换为秒并与当前时间进行比较
                    long currentTimeInSeconds = System.currentTimeMillis() / 1000;
                    if (expSeconds == null || expSeconds <= currentTimeInSeconds) {
                        logger.warn("User JWT Token has expired. Exp: {}, Current Time: {}", expSeconds, currentTimeInSeconds);
                        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token has expired");
                        return;
                    }

                    // 获取用户的角色权限
                    List<SimpleGrantedAuthority> authorities = getAuthorities(claims);

                    // 创建 UserDetails 对象并设置认证信息，默认为 USER 角色
                    org.springframework.security.core.userdetails.User userDetails =
                            new org.springframework.security.core.userdetails.User(
                                    username, "", authorities);

                    authentication = new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
                }

                SecurityContextHolder.getContext().setAuthentication(authentication);
                logger.info("认证对象已设置为: {}", authentication);
            } catch (Exception e) {
                logger.warn("无效的 JWT 令牌: {}", e.getMessage(), e);
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid JWT token");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}