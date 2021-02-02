package com.atzjhydx.user.VO;

import lombok.Data;

/**
 * @Auther LeeMZ
 * @Date 2021/2/2
 **/
@Data
public class ResultVO<T> {

    private Integer code;

    private String message;

    T data;
}
