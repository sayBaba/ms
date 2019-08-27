package com.hz.ms.service;

import com.hz.ms.req.LoginReq;

/**
 * 用户相关接口
 */
public interface IUserService {

    /**
     * 登录接口
     * @param loginReq
     */
    public void login(LoginReq loginReq);
}
