package org.whu.fleetingtime.interceptor;


import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import org.whu.fleetingtime.common.Result;
import org.whu.fleetingtime.util.JwtUtils;

import java.io.IOException;

@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Value("${jwt.secret}")
    private String secretKey;

    private final Logger logger = LoggerFactory.getLogger(JwtInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) throws Exception {

        // 放行预检请求
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        //String token = request.getHeader("Authorization");
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            logger.warn("[JwtInterceptor] 请求被拦截：无效的 Authorization 头");
            sendUnauthorizedResponse(response, "invalid authorization header");
            return false;
        }
        String token = authHeader.substring(7); // 截去 "Bearer "

        logger.info("[JwtInterceptor] 拦截请求路径: {}，方法: {}，Token: {}", request.getRequestURI(), request.getMethod(), token);

        if (token == null || token.isEmpty()) {
            logger.warn("[JwtInterceptor] 请求被拦截：未携带 Token");
            sendUnauthorizedResponse(response, "null token");
            return false;
        }

        try {
            Claims claims = JwtUtils.parseJWT(secretKey, token);
            String userId = claims.get("id", String.class);
//            String userIdStr = claims.get("id", String.class);
//            Long userId = Long.parseLong(userIdStr);
//            request.setAttribute("userId", userId);
            logger.info("[JwtInterceptor] Token 解析成功，userId: {}", userId);
            // 把 userId 放入 request 中，供后续使用
            request.setAttribute("userId", userId);
            return true;

        } catch (Exception e) {
            logger.warn("[JwtInterceptor] Token 解析失败: {}", e.getMessage());
            sendUnauthorizedResponse(response, "invalid token");
            return false;
        }
    }

    private void sendUnauthorizedResponse(HttpServletResponse response, String message) throws IOException {
        // 【添加CORS头部 - 关键修改】
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:8082"); // 或者你期望的源
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE, PUT");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization, X-Requested-With"); // 允许常见的头部，特别是 Authorization
        response.setHeader("Access-Control-Allow-Credentials", "true"); // 如果你的前端设置了 withCredentials

        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
        Result<Object> result = Result.failure(401, message);
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(result);
        response.getWriter().write(json);
    }
}

