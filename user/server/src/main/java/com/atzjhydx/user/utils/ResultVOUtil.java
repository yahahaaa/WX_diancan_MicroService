package com.atzjhydx.user.utils;
import com.atzjhydx.user.VO.ResultVO;
import com.atzjhydx.user.enums.ResultEnum;

/**
 * @Auther LeeMZ
 * @Date 2021/2/2
 **/
public class ResultVOUtil {

    public static ResultVO success(Object o){
        ResultVO resultVO = new ResultVO();
        resultVO.setCode(0);
        resultVO.setMessage("成功");
        resultVO.setData(o);
        return resultVO;
    }

    public static ResultVO success(){
        ResultVO resultVO = new ResultVO();
        resultVO.setCode(0);
        resultVO.setMessage("成功");
        return resultVO;
    }

    public static ResultVO error(ResultEnum resultEnum){
        ResultVO resultVO = new ResultVO();
        resultVO.setCode(resultEnum.getCode());
        resultVO.setMessage(resultEnum.getMessage());

        return resultVO;
    }
}
