package com.code.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtUtils jwtUtils;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // 配置CORS支持
                .csrf(csrf -> csrf.disable()) // 禁用CSRF保护（如果你的应用确实不需要它）
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/user/login", "/manager/login").permitAll() // 允许未认证用户访问登录接口
                        .requestMatchers("/manager/**").hasRole("ADMIN") // 使用 hasRole 并确保包含 ROLE_ 前缀
                        .anyRequest().authenticated()) // 所有其他请求都需要认证
                .addFilterBefore(new JwtAuthenticationFilter(jwtUtils), CsrfFilter.class); // 添加自定义过滤器

        return http.build();
    }

    private CorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        // 在生产环境中，请指定具体的来源域名，而不是使用 "*"。
        // 这里我们暂时允许所有来源，以便于开发和测试。
        config.setAllowedOrigins(List.of("http://localhost:5173"));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        config.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
        config.setAllowCredentials(true); // 如果需要发送凭证信息（如Cookie等），则设置为true

        source.registerCorsConfiguration("/**", config);
        return source;
    }
}