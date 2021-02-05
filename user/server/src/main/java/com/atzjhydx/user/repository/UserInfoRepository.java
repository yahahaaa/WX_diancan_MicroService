package com.atzjhydx.user.repository;

import com.atzjhydx.user.dataobject.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @Auther LeeMZ
 * @Date 2021/2/2
 **/
public interface UserInfoRepository extends JpaRepository<UserInfo,String> {

    UserInfo findByOpenid(String openid);
}
