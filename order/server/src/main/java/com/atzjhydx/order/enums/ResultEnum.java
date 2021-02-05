package com.atzjhydx.order.enums;

import lombok.Getter;

/**
 * @Auther LeeMZ
 * @Date 2021/1/26
 **/
@Getter
public enum ResultEnum {

    PARAM_ERROR(1,"参数错误"),
    CART_IS_EMPTY(2,"购物车为空"),
    ORDER_NOT_EXIST(3,"订单不存在"),
    ORDER_STATUS_ERROR(4,"订单状态错误"),
    ORDER_DETAIL_NOT_EXIST(5,"订单详情不存在");

    private Integer code;
    private String message;

    ResultEnum(Integer code,String message) {
        this.code = code;
        this.message = message;
    }
}
