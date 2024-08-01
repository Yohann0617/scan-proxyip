package com.proxyip.select.config.auth;

import org.springframework.boot.autoconfigure.web.servlet.error.ErrorViewResolver;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.InternalResourceView;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @projectName: scan-proxyip
 * @package: com.proxyip.select.config.auth
 * @className: MyErrorViewResolver
 * @author: Yohann
 * @date: 2024/3/30 18:16
 */
@Configuration
public class MyErrorViewResolver implements ErrorViewResolver {

    @Override
    public ModelAndView resolveErrorView(HttpServletRequest request, HttpStatus status, Map<String, Object> model) {
        // Use the request or status to optionally return a ModelAndView
        if (status == HttpStatus.NOT_FOUND ) {
            // We could add custom model values here
            return new ModelAndView(new InternalResourceView("/login.html"),model);
        }
//        // 可以根据其他状态码处理其他错误页面
//        if (status == HttpStatus.UNAUTHORIZED) {
//            return new ModelAndView(new InternalResourceView("/login.html"), model);
//        }
        return new ModelAndView(new InternalResourceView("/proxyIp.html"),model);
    }
}
