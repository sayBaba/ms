package com.hz.ms.service.impl;

import com.hz.ms.mapper.GoodsMapper;
import com.hz.ms.mapper.OrderMapper;
import com.hz.ms.mapper.SeckillOrderMapper;
import com.hz.ms.model.OrderInfo;
import com.hz.ms.model.SecKillGoods;
import com.hz.ms.model.SeckillOrder;
import com.hz.ms.model.User;
import com.hz.ms.redis.RedisUtil;
import com.hz.ms.service.ISeckillOrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * 订单实现接口实现类
 */
@Service
public class SeckillOrderServiceImpl implements ISeckillOrderService {

    private static final Logger logger = LoggerFactory.getLogger(SeckillOrderServiceImpl.class);

    @Autowired
    private GoodsMapper goodsMapper;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private SeckillOrderMapper seckillOrderMapper;

    @Override
    @Transactional //开启事务
    public void createOrder(User user, SecKillGoods goods) {
        //减数据库库存
        int num = goodsMapper.updateStock(goods.getId());
        if(num<=0){
//          redisService.set(SeckillKey.isGoodsOver, ""+goodsId, true , Const.RedisCacheExtime.GOODS_ID);
            redisUtil.set("go"+goods.getId(),true);
            return;
        }

        //入订单详情表
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setCreateDate(new Date());
        orderInfo.setAddrId(0L);
        orderInfo.setGoodsCount(1);
        orderInfo.setGoodsId(goods.getId());
        orderInfo.setGoodsName(goods.getGoodsName());
        orderInfo.setGoodsPrice(goods.getSeckillPrice());
        orderInfo.setOrderChannel(1);
        orderInfo.setStatus(0);
        orderInfo.setUserId((long)user.getId());
        int orderId = orderMapper.insertSelective(orderInfo);
        //入秒杀订单表
        SeckillOrder seckillOrder = new SeckillOrder();
        seckillOrder.setGoodsId(goods.getId());
        seckillOrder.setOrderId(orderInfo.getId());
        seckillOrder.setUserId((long)user.getId());
        //插入秒杀表
        seckillOrderMapper.insertSelective(seckillOrder);

    }
}
