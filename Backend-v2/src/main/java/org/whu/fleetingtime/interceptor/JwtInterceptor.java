package org.whu.fleetingtime.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.whu.fleetingtime.controller.UserController;
import org.whu.fleetingtime.util.JwtUtil;

@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    private static final Logger logger = LoggerFactory.getLogger(JwtInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        logger.info("【JWT拦截器】拦截请求: {}",request.getRequestURI());
        // 如果是OPTIONS预检请求，直接放行，不进行JWT校验
        // CORS的响应头会由全局CORS配置（如WebConfig中的addCorsMappings）来处理
        if (HttpMethod.OPTIONS.name().equalsIgnoreCase(request.getMethod())) {
            logger.debug("【JWT拦截器】检测到OPTIONS预检请求，路径: {}，直接放行", request.getRequestURI());
            response.setStatus(HttpServletResponse.SC_OK); // 有些框架或配置可能需要为OPTIONS返回200 OK
            return true;
        }
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }
        String token = authHeader.substring(7); // 截取真正的token部分

        if (!jwtUtil.validateToken(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }
        String userId = jwtUtil.getUserIdFromToken(token);
        logger.info("【JWT解析】userId: {}",userId);
        request.setAttribute("userId", userId);
        return true;
    }
}