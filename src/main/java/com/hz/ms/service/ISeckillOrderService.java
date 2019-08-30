package com.hz.ms.service;

import com.hz.ms.model.SecKillGoods;
import com.hz.ms.model.User;

/**
 * 订单接口
 */
public interface ISeckillOrderService {

    /**
     * 生成订单
     * @param user
     * @param goods
     */
    public void createOrder(User user, SecKillGoods goods);
}
