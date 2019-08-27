package com.hz.ms.controller;

import com.hz.ms.req.LoginReq;
import com.hz.ms.service.IUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@Controller
public class LoginController {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private IUserService iUserService;

    @RequestMapping("/show/login")
    public String showLogin(){
        logger.info("加载登录页面...");
        return "login";
    }

    @ResponseBody
    @RequestMapping("/user/login")
    public void userLogin(HttpServletResponse response, HttpSession session, @Valid LoginReq loginReq){
        logger.info("接受到手机号:{}的登录请求....",loginReq.getMobile());
        iUserService.login(loginReq);

    }

}
