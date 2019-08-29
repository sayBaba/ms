package com.hz.ms.service.impl;

import com.hz.ms.mapper.UserMapper;
import com.hz.ms.model.User;
import com.hz.ms.redis.RedisUtil;
import com.hz.ms.req.LoginReq;
import com.hz.ms.resp.CodeMsg;
import com.hz.ms.resp.Result;
import com.hz.ms.service.IUserService;
import com.hz.ms.utils.MD5Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户相关接口实现类
 */
@Service
public class UserServiceImpl implements IUserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public Result login(LoginReq loginReq,String sessionId) {
        Map<String,String> map = new HashMap<String,String>();
        map.put("phone",loginReq.getMobile());
        User user = userMapper.checkPhone(map);
        //判断用户是否存在
        if (ObjectUtils.isEmpty(user)){
            return Result.getFail(CodeMsg.MOBILE_NOT_EXIST.getCode(),CodeMsg.MOBILE_NOT_EXIST.getMsg());
        }
        //判断密码是否正确
        String password = loginReq.getPassword();
        String calcPass = MD5Util.formPassToDBPass(password, user.getSalt());

        String dbPassword = user.getPassword();

        if(!dbPassword.equals(calcPass)){
            return Result.getFail(CodeMsg.PASSWORD_ERROR.getCode(),CodeMsg.PASSWORD_ERROR.getMsg());
        }
        //返回登录成功
        Result result = new Result();
        result.setCode(CodeMsg.SUCCESS.getCode());
        result.setMsg(CodeMsg.SUCCESS.getMsg());
        user.setPassword("");
        result.setData(user);
        String redisKey = "user_"+sessionId;

        //用户信息缓存到redis
        redisUtil.set(redisKey,user);
        return result;
    }
}
