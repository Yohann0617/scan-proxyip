package com.proxyip.select.config.auth;

import cn.hutool.core.util.StrUtil;
import com.proxyip.select.common.exception.BusinessException;
import com.proxyip.select.utils.ExpiringCache;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.TimeUnit;

/**
 * @projectName: scan-proxyip
 * @package: com.proxyip.select.config.auth
 * @className: AuthInterceptor
 * @author: Yohann
 * @date: 2024/3/30 18:03
 */
@Component
public class AuthInterceptor implements HandlerInterceptor {
    @Resource
    private ExpiringCache<String, String> expiringCache;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!request.getRequestURI().contains("/session/login")) {
            String token = request.getHeader("Authorization");
            String tokenInCache = expiringCache.get("token");
            if (StrUtil.isBlank(tokenInCache) || !token.equals(tokenInCache)) {
                response.sendRedirect("/login.html");
                return false;
            }
            expiringCache.put("token", token, 10, TimeUnit.MINUTES);
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
}
