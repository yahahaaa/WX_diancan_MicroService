package com.atzjhydx.product.enums;

import lombok.Data;
import lombok.Getter;

/**
 * @Auther LeeMZ
 * @Date 2021/1/27
 **/
@Getter
public enum ResultEnum {

    PRODUCT_NOT_EXIT(1,"商品不存"),
    PRODUCT_STOCK_ERROR(2,"库存不足");

    private Integer code;

    private String message;

    ResultEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
