package com.hz.ms.service.impl;

import com.hz.ms.mapper.UserMapper;
import com.hz.ms.req.LoginReq;
import com.hz.ms.service.IUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 用户相关接口实现类
 */
@Service
public class UserServiceImpl implements IUserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserMapper userMapper;

    @Override
    public void login(LoginReq loginReq) {
//        userMapper.

    }
}
