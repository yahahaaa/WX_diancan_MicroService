package com.atzjhydx.order.utils;

import com.atzjhydx.order.VO.ResultVO;

/**
 * @Auther LeeMZ
 * @Date 2021/1/26
 **/
public class ResultVOUtil {
    public static ResultVO success(Object o){
        ResultVO resultVO = new ResultVO();
        resultVO.setCode(0);
        resultVO.setMessage("成功");
        resultVO.setData(o);

        return resultVO;
    }
}
