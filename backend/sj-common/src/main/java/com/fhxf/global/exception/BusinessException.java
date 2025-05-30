package com.fhxf.global.exception;

/**
 * @className: BusinessException
 * @author: 李昌泉
 * @date: 2024/11/14 下午9:16
 * @Version: 1.0
 * @description:
 */

public class BusinessException extends CommonException {
    public BusinessException(String message) {
        super(message, 500);
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause, 500);
    }

    public BusinessException(Throwable cause) {
        super(cause, 500);
    }


}
