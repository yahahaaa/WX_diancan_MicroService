package com.atzjhydx.user.service.impl;

import com.atzjhydx.user.dataobject.UserInfo;
import com.atzjhydx.user.repository.UserInfoRepository;
import com.atzjhydx.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Auther LeeMZ
 * @Date 2021/2/2
 **/
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserInfoRepository userInfoRepository;


    /**
     * 通过openid查询用户信息
     * @param openid
     * @return
     */
    @Override
    public UserInfo findyByOpenid(String openid) {
        return userInfoRepository.findByOpenid(openid);
    }
}
