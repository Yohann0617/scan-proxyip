package com.proxyip.select.common.exception;

/**
 * @projectName: scan-proxyip
 * @package: com.proxyip.select.exception
 * @className: BusinessException
 * @author: Yohann
 * @date: 2024/3/30 19:10
 */
public class BusinessException extends RuntimeException {
    private final int errorCode;

    public BusinessException(int code, String msg) {
        super(msg);
        this.errorCode = code;
    }

    public BusinessException(int code, String msg, Exception e) {
        super(msg, e);
        this.errorCode = code;
    }

    public int getCode() {
        return this.errorCode;
    }
}
