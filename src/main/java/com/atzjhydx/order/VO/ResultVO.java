package com.atzjhydx.order.VO;

import lombok.Data;

/**
 * @Auther LeeMZ
 * @Date 2021/1/26
 **/
@Data
public class ResultVO<T> {

    private Integer code;

    private String message;

    private T data;
}
