package org.whu.fleetingtime.interceptor;


import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import org.whu.fleetingtime.common.Result;

@Component
public class JwtInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(JwtInterceptor.class);

    // 前置拦截方法
    @Override
    public boolean preHandle(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) throws Exception {

        String token = request.getHeader("Authorization");

        logger.info("[JwtInterceptor]拦截请求路径: {}，方法: {}，Token: {}", request.getRequestURI(), request.getMethod(), token);

        if (token == null || token.isEmpty()) {
            logger.warn("[JwtInterceptor]请求被拦截：未携带Token");

            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
            // 构造统一返回结果
            Result<Object> result = Result.failure(401, "null token");
            // 转成 JSON 返回
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(result);

            response.getWriter().write(json);
            return false;
        }
        logger.info("[JwtInterceptor]请求被放行：已携带Token");
        return true;
    }
}
