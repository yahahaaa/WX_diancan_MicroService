package com.atzjhydx.product.common;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @Auther LeeMZ
 * @Date 2021/1/28
 **/
@Data
public class ProductInfoOutput {
    //商品ID，主键
    private String productId;

    //商品名
    private String productName;

    //商品价格
    private BigDecimal productPrice;

    //商品库存
    private Integer productStock;

    //商品描述
    private String productDescription;

    //商品小图
    private String productIcon;

    //商品状态 0在架1下架，默认状态为在架
    private Integer productStatus;

    //类目编号
    private Integer categoryType;
}
