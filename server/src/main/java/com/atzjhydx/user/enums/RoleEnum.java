package com.atzjhydx.user.enums;

import lombok.Getter;

/**
 * @Auther LeeMZ
 * @Date 2021/2/2
 **/
@Getter
public enum RoleEnum {

    BUYER(1,"买家"),
    SELLER(2,"卖家");

    private Integer code;
    private String message;

    RoleEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
