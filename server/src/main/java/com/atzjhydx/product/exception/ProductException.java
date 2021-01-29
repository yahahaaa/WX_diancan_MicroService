package com.atzjhydx.product.exception;

import com.atzjhydx.product.enums.ResultEnum;
import lombok.Data;

/**
 * @Auther LeeMZ
 * @Date 2021/1/27
 **/
@Data
public class ProductException extends RuntimeException {

    private Integer code;

    public ProductException(Integer code,String message){
        super(message);
        this.code = code;
    }

    public ProductException(ResultEnum resultEnum){
        super(resultEnum.getMessage());
        this.code = resultEnum.getCode();
    }
}
