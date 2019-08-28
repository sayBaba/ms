package com.hz.ms.controller;

import com.hz.ms.service.IGoodsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 商品相关controller
 */
@Controller
//@RestController // = Controller+ ResponseBody
@RequestMapping("/goods")
public class GoodsController {

    private static final Logger logger = LoggerFactory.getLogger(GoodsController.class);

    @Autowired
    private IGoodsService iGoodsService;

    /**
     * 加载秒杀商品列表
     * @return
     */
    @ResponseBody
    @RequestMapping("/list")
    public String list(Model model, HttpServletRequest request, HttpServletResponse response){
        logger.info("接收到加载秒杀商品列表请求...");
        String html =iGoodsService.getAllSecKillGoods(model,request,response);
        return html;
    }

}
