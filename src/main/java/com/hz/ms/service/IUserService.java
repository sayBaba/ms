package com.hz.ms.service;

import com.hz.ms.req.LoginReq;
import com.hz.ms.resp.Result;

/**
 * 用户相关接口
 */
public interface IUserService {

    /**
     * 登录接口
     * @param loginReq
     */
    public Result login(LoginReq loginReq,String sessionId);
}
