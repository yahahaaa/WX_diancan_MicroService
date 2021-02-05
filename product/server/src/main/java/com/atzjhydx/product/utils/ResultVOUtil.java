package com.atzjhydx.product.utils;

import com.atzjhydx.product.VO.ResultVO;

/**
 * @Auther LeeMZ
 * @Date 2021/1/26
 **/
public class ResultVOUtil {

    public static ResultVO success(Object object){
        ResultVO resultVO = new ResultVO();
        resultVO.setData(object);
        resultVO.setCode(0);
        resultVO.setMessage("成功");

        return resultVO;
    }
}
