package com.atzjhydx.order.exception;

import com.atzjhydx.order.enums.ResultEnum;

/**
 * @Auther LeeMZ
 * @Date 2021/1/26
 **/
public class OrderException extends RuntimeException {
    private Integer code;
    public OrderException(Integer code,String message){
        super(message);
        this.code = code;
    }

    public OrderException(ResultEnum resultEnum){
        super(resultEnum.getMessage());
        this.code = resultEnum.getCode();
    }
}
