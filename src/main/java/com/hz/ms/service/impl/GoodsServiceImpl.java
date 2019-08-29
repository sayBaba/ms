package com.hz.ms.service.impl;

import com.hz.ms.mapper.GoodsMapper;
import com.hz.ms.model.SecKillGoods;
import com.hz.ms.model.User;
import com.hz.ms.redis.RedisUtil;
import com.hz.ms.resp.CodeMsg;
import com.hz.ms.resp.GoodsDetailRlt;
import com.hz.ms.service.IGoodsService;
import com.hz.ms.utils.CookieUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.thymeleaf.context.IWebContext;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;

/**
 * 商品相关接口实现类
 */
@Service
public class GoodsServiceImpl implements IGoodsService {

    private static final Logger logger = LoggerFactory.getLogger(GoodsServiceImpl.class);

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private GoodsMapper goodsMapper;

    @Autowired
    private ThymeleafViewResolver thymeleafViewResolver;


    /**
     * 生成商品列表的静态页面
     * @return
     */
    @Override
    public String getAllSecKillGoods(Model model, HttpServletRequest request, HttpServletResponse response) {
        //1.redis中获取静态页面
        String redisKey = "static_secKill_goods_html";
        String html = (String) redisUtil.get(redisKey);
        //判断是否为空
        if (!StringUtils.isEmpty(html)) {
            return html;
        }
        //查秒杀商品
        List<SecKillGoods> secKillGoodsList = goodsMapper.getAllSecKillGoods();
        model.addAttribute("goodsList", secKillGoodsList);

        //生成静态页面
//      SpringWebContext swc = new SpringWebContext();
        //1.获取应用上下文
        IWebContext ctx =new WebContext(request,response,
                request.getServletContext(),request.getLocale(),model.asMap());
        //2.thymeleafViewResolve 生成页面
        html = thymeleafViewResolver.getTemplateEngine().process("goods_list",ctx);
        //3.静态页面缓存redis
        if (!StringUtils.isEmpty(html)) {
            redisUtil.set(redisKey,html);
        }
        return html;
    }

    @Override
    public GoodsDetailRlt getGoodsDetail(String goodsId,HttpServletRequest request) {
        GoodsDetailRlt rlt = new GoodsDetailRlt();
        String sessionId = CookieUtil.readLoginToken(request);
        logger.info("获取的cookie的值为：{}",sessionId);

        //cookie中没有获取到sessionId
        if(StringUtils.isEmpty(sessionId)){
            //TODO
            logger.info("session为空");
            return rlt;
        }
        String redisKey = "user_"+sessionId;
        User user = (User) redisUtil.get(redisKey);
        //redis中查找不到用户信息
        if(ObjectUtils.isEmpty(user)){
            logger.info("redis中没有找到user信息");
            return rlt;
        }
        rlt.setUser(user); //存用户信息

        //获取秒杀商品详情
        SecKillGoods secKillGoods = goodsMapper.getSecKillGoods(goodsId);
        //没有找到商品，返回
        if (ObjectUtils.isEmpty(secKillGoods)){
            logger.info("没有找到对应商品的商品信息");
            return rlt;
        }
        rlt.setSecKillGoods(secKillGoods); //存秒杀商品详情

//      String remainSeconds = null;//秒杀活动状态
        long startTime = secKillGoods.getStartDate().getTime(); //活动开始时间
        long endTime = secKillGoods.getEndDate().getTime(); //活动结束时间
        long now = System.currentTimeMillis(); //当前时间
        if(now<startTime){
            rlt.setRemainSeconds("1");
        }else if(now > startTime && now < endTime){
            rlt.setRemainSeconds("0");
        }else {
            rlt.setRemainSeconds("-1");
        }
        rlt.setCode(CodeMsg.SUCCESS.getCode());
        rlt.setMsg(CodeMsg.SUCCESS.getMsg());
        return rlt;
    }
}
