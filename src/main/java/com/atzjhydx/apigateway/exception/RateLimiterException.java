package com.atzjhydx.apigateway.exception;

/**
 * @Auther LeeMZ
 * @Date 2021/2/1
 **/
public class RateLimiterException extends RuntimeException {
    private Integer code;

    public RateLimiterException(Integer code,String message) {
        super(message);
        this.code = code;
    }
}
