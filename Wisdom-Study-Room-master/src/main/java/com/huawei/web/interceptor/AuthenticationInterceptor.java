package com.huawei.web.interceptor;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.huawei.web.util.TokenUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 拦截器：验证 Token
 */
public class AuthenticationInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("token");
        // 如果不是映射到方法直接通过
        if (!(handler instanceof org.springframework.web.method.HandlerMethod)) {
            return true;
        }

        if (token == null || token.isEmpty()) {
            response.setStatus(401);
            return false;
        }

        try {
            TokenUtils.verify(token);
        } catch (JWTVerificationException e) {
            response.setStatus(401);
            return false;
        }

        return true;
    }
}
