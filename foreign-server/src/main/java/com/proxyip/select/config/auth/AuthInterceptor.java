package com.proxyip.select.config.auth;

import com.proxyip.select.common.exception.BusinessException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @projectName: scan-proxyip
 * @package: com.proxyip.select.config.auth
 * @className: AuthInterceptor
 * @author: Yohann
 * @date: 2024/3/30 18:03
 */
@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Value("${permission-code}")
    private String permissionCode;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String authorizationHeader = request.getHeader("Authorization");
        if (request.getRequestURI().contains("/api")){
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                String token = authorizationHeader.substring(7); // 去掉"Bearer "前缀
                // 验证token（这里可以调用你的验证逻辑）
                boolean isValid = validateToken(token);
                if (isValid) {
                    return true; // 继续处理请求
                } else {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    throw new BusinessException(401, "无权限");
                }
            }else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                throw new BusinessException(401, "无权限");
            }
        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }

    private boolean validateToken(String token) {
        return token.equals(permissionCode);
    }
}
