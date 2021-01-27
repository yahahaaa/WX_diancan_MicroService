package com.atzjhydx.order.dto;

import lombok.Data;

/**
 * @Auther LeeMZ
 * @Date 2021/1/27
 **/
@Data
public class CartDTO {

    /**
     * 商品Id
     */
    private String productId;

    /**
     * 商品数量
     */
    private Integer productQuantity;

    public CartDTO(String productId, Integer productQuantity) {
        this.productId = productId;
        this.productQuantity = productQuantity;
    }

    public CartDTO(){}
}
