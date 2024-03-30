package com.proxyip.select.common.exception;

import com.proxyip.select.common.bean.ResponseData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @projectName: scan-proxyip
 * @package: com.proxyip.select.exception
 * @className: GlobalExceptionHandler
 * @author: Yohann
 * @date: 2024/3/30 19:09
 */
@ControllerAdvice
@ResponseBody
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(ExceptionHandler.class);

    @ExceptionHandler
    public ResponseData<String> unknownException(Exception e) {
        if (e instanceof BusinessException) {
            BusinessException be = (BusinessException)e;
            if (be.getCause() != null) {
                logger.error("business exception:{}, original exception : ", be.getMessage(), be.getCause());
            }

            return ResponseData.errorData(be.getCode(), be.getMessage());
        } else {
            return ResponseData.errorData(-1, e.getLocalizedMessage());
        }
    }

}
