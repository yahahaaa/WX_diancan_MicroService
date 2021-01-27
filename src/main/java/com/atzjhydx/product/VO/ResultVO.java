package com.atzjhydx.product.VO;

import lombok.Data;

/**
 * http请求返回的最外层对象
 * @Auther LeeMZ
 * @Date 2021/1/26
 **/
@Data
public class ResultVO<T> {

    /**
     * 错误码
     */
    private Integer code;

    /**
     * 提示信息
     */
    private String message;

    /**
     * 具体内容
     */
    private T data;
}
