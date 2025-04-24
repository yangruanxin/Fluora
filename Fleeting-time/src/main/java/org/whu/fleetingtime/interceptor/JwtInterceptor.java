package org.whu.fleetingtime.interceptor;


import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import org.whu.fleetingtime.pojo.Result;

@Component
public class JwtInterceptor implements HandlerInterceptor {
    // 前置拦截方法
    @Override
    public boolean preHandle(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) throws Exception {

        String token = request.getHeader("Authorization");

        if (token == null || token.isEmpty()) {
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
            // 构造统一返回结果
            Result<Object> result = Result.failure(401, "未提供Token");
            // 转成 JSON 返回
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(result);

            response.getWriter().write(json);
            return false;
        }
        return true;
    }
}
