package com.atzjhydx.user.enums;

import lombok.Getter;

/**
 * @Auther LeeMZ
 * @Date 2021/2/2
 **/
@Getter
public enum ResultEnum {

    LOGIN_FAIL("登录失败", 1),
    ROLE_ERROR("角色权限有误",2);

    private String message;
    private Integer code;

    ResultEnum(String message,Integer code) {
        this.message = message;
        this.code = code;
    }
}
