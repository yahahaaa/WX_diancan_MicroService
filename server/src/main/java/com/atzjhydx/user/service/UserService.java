package com.atzjhydx.user.service;

import com.atzjhydx.user.dataobject.UserInfo;

public interface UserService {

    UserInfo findyByOpenid(String openid);
}
