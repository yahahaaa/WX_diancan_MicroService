package com.atzjhydx.user.controller;

import com.atzjhydx.user.VO.ResultVO;
import com.atzjhydx.user.constant.CookieConstant;
import com.atzjhydx.user.constant.RedisConstant;
import com.atzjhydx.user.dataobject.UserInfo;
import com.atzjhydx.user.enums.ResultEnum;
import com.atzjhydx.user.enums.RoleEnum;
import com.atzjhydx.user.service.UserService;
import com.atzjhydx.user.utils.CookieUtil;
import com.atzjhydx.user.utils.ResultVOUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @Auther LeeMZ
 * @Date 2021/2/2
 **/
@RestController
@RequestMapping("/login")
public class LoginController {

    @Autowired
    private UserService userService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 买家登录
     * @param openid
     * @param response
     * @return
     */
    @GetMapping("/buyer")
    public ResultVO buyer(@RequestParam("openid") String openid,
                          HttpServletResponse response){

        //1. openid和数据库里的数据匹配
        UserInfo userInfo = userService.findyByOpenid(openid);

        //2. 判断角色
        if (userInfo == null){
            return ResultVOUtil.error(ResultEnum.LOGIN_FAIL);
        }

        if (!userInfo.getRole().equals(RoleEnum.BUYER.getCode())){
            return ResultVOUtil.error(ResultEnum.ROLE_ERROR);
        }

        //3. 登录成功，设置cookie
        CookieUtil.set(response, CookieConstant.OPENID,openid,CookieConstant.expire);

        return ResultVOUtil.success();
    }

    /**
     * 卖家登录
     * @param openid
     * @param request
     * @param response
     * @return
     */
    @GetMapping("/seller")
    public ResultVO seller(@RequestParam("openid") String openid,
                           HttpServletRequest request,
                           HttpServletResponse response){

        // 判断是否已经登录
        Cookie cookie = CookieUtil.get(request, CookieConstant.TOKEN);
            if (cookie != null && !StringUtils.isEmpty(redisTemplate.opsForValue().get(String.format(RedisConstant.TOKEN_TEMPLATE,cookie.getValue())))){
                return ResultVOUtil.success();
        }

        //1. openid和数据库里的数据匹配
        UserInfo userInfo = userService.findyByOpenid(openid);

        //2. 判断角色
        if (userInfo == null){
            return ResultVOUtil.error(ResultEnum.LOGIN_FAIL);
        }
        if (!userInfo.getRole().equals(RoleEnum.SELLER.getCode())){
            return ResultVOUtil.error(ResultEnum.ROLE_ERROR);
        }

        //3. redis设置key=UUID,value=xyz
        String token = UUID.randomUUID().toString();
        Integer expire = CookieConstant.expire;
        redisTemplate.opsForValue().set(String.format(RedisConstant.TOKEN_TEMPLATE,token),openid,expire, TimeUnit.SECONDS);

        //4. 登录成功，设置cookie
        CookieUtil.set(response, CookieConstant.TOKEN,token,CookieConstant.expire);

        return ResultVOUtil.success();
    }

}
