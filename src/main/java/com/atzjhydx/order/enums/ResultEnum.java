package com.atzjhydx.order.enums;

import lombok.Getter;

/**
 * @Auther LeeMZ
 * @Date 2021/1/26
 **/
@Getter
public enum ResultEnum {

    PARAM_ERROR(1,"参数错误"),
    CART_IS_EMPTY(2,"购物车为空");

    private Integer code;
    private String message;

    ResultEnum(Integer code,String message) {
        this.code = code;
        this.message = message;
    }
}
