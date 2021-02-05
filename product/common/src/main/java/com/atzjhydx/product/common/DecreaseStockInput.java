package com.atzjhydx.product.common;

import lombok.Data;

/**
 * @Auther LeeMZ
 * @Date 2021/1/28
 **/
@Data
public class DecreaseStockInput {
    private String productId;

    private Integer productQuantity;

    public DecreaseStockInput(){}

    public DecreaseStockInput(String productId, Integer productQuantity){
        this.productId = productId;
        this.productQuantity = productQuantity;
    }
}
