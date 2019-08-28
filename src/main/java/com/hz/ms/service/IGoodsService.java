package com.hz.ms.service;

import com.hz.ms.model.SecKillGoods;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 商品相关接口
 */
public interface IGoodsService {

    /**
     * 获取秒杀商品列表
     * @return
     */
    public String getAllSecKillGoods(Model model, HttpServletRequest request, HttpServletResponse response);
}
