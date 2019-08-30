package com.hz.ms.controller;

import com.hz.ms.model.User;
import com.hz.ms.redis.RedisUtil;
import com.hz.ms.resp.CodeMsg;
import com.hz.ms.resp.Result;
import com.hz.ms.service.ISecKillService;
import com.hz.ms.utils.CookieUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;

/**
 * 秒杀程序入口
 */
@Controller
@RequestMapping("/seckill")
public class SecKillController implements InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(SecKillController.class);

    @Autowired
    private ISecKillService iSecKillService;

    @Autowired
    private RedisUtil redisUtil;


    /**
     * 第一步，生成隐藏地址
     * @return
     */
    @ResponseBody
    @RequestMapping("/path")
    public Result<String> createPath(@RequestParam String goodsId, HttpServletRequest request){
        logger.info("接收到goodsId:{},生成隐藏的接口地址请求...",goodsId);
        Result result = validate(request);
        if(result.getCode()!=0){
            return result;
        }
        String sessionId = CookieUtil.readLoginToken(request);
        String redisKey = "user_"+sessionId;
        User user = (User) redisUtil.get(redisKey);
        //调用service生成path
        String path = iSecKillService.createPath(goodsId, user);
        result.setMsg(CodeMsg.SUCCESS.getMsg());
        result.setCode(CodeMsg.SUCCESS.getCode());
        result.setData(path);
        return result;
    }

    /**
     * 第二步,将秒杀请求丢给mq处理
     * @return
     */
    @RequestMapping("/{path}/seckill")
    @ResponseBody
    public Result secKill(@PathVariable("path") String path, @RequestParam String goodsId, HttpServletRequest request){

        logger.info("接收到goodsId:{},秒杀请求",goodsId);
        Result result = validate(request);
        if(result.getCode()!=0){
            return result;
        }
        String sessionId = CookieUtil.readLoginToken(request);
        String redisKey = "user_"+sessionId;
        User user = (User) redisUtil.get(redisKey);
        result = iSecKillService.secKill(goodsId,user,path);
        return result;
    }

    /**
     * 第三步，主动去查询mq处理的结果
     * @param goodsId
     */
    @ResponseBody
    @RequestMapping("/result")
    public Result getResult(@RequestParam String goodsId, HttpServletRequest request){
        logger.info("接收到goodsId:{},主动查询mq中秒杀结果",goodsId);
        Result result = validate(request);
        if(result.getCode()!=0){
            return result;
        }
        String sessionId = CookieUtil.readLoginToken(request);
        String redisKey = "user_"+sessionId;
        User user = (User) redisUtil.get(redisKey);
        result = iSecKillService.getKillResult(goodsId,user);
        return result;
    }

    /**
     * 初始化数据，系统启动
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        logger.info("....开始初始化秒杀商品数据...:"+ iSecKillService);
        iSecKillService.initSkillGoods();
        logger.info("....初始化秒杀商品数据结束....");
    }

    /**
     * 登录判断
     * @param request
     * @return
     */
    private Result validate(HttpServletRequest request){
        Result result = new Result();
        //从cookie请求中获取sessionId
        String sessionId = CookieUtil.readLoginToken(request);
        logger.info("sessionId:{}",sessionId);
        if(StringUtils.isEmpty(sessionId)){
            result.setCode(CodeMsg.BIND_ERROR.getCode());
            result.setMsg(CodeMsg.BIND_ERROR.getMsg());
            return result;
        }

        //判断redis中的用户信息是否存在
        String redisKey = "user_"+sessionId;
        User user = (User) redisUtil.get(redisKey);
        if(ObjectUtils.isEmpty(user)){
            result.setCode(CodeMsg.USER_NO_LOGIN.getCode());
            result.setMsg(CodeMsg.USER_NO_LOGIN.getMsg());
            return result;
        }
        result.setCode(CodeMsg.SUCCESS.getCode());
        return result;
    }
}
