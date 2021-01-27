package com.atzjhydx.order.form;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * @Auther LeeMZ
 * @Date 2021/1/26
 **/
@Data
public class OrderForm {

    @NotEmpty(message = "姓名必填")
    private String name;

    @NotEmpty(message = "手机号必填")
    private String phone;

    @NotEmpty(message = "地址必填")
    private String address;

    @NotEmpty(message = "openid必填")
    private String openid;

    @NotEmpty(message = "购物车不能为空")
    //购物车中包含商品id和购买的商品数量
    private String items;
}