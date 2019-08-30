package com.hz.ms.mq;

import com.hz.ms.mapper.GoodsMapper;
import com.hz.ms.mapper.SeckillOrderMapper;
import com.hz.ms.model.SecKillGoods;
import com.hz.ms.model.SeckillOrder;
import com.hz.ms.model.User;
import com.hz.ms.service.IGoodsService;
import com.hz.ms.service.ISeckillOrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

/**
 * 秒杀消费者
 */
@Component
public class SecKillCustomer {

    private static final Logger logger = LoggerFactory.getLogger(SecKillCustomer.class);

    @Autowired
    private GoodsMapper goodsMapper;

    @Autowired
    private SeckillOrderMapper seckillOrderMapper;

    @Autowired
    private ISeckillOrderService iSeckillOrderService;

    //注解使用：https://www.jianshu.com/p/382d6f609697
    @RabbitListener(queues=MQConfig.MIAOSHA_QUEUE)
    public void receive(SeckillMessage seckillMessage){
        logger.info("秒杀消费者接受：userId:{},goodsId:{},的请求...",seckillMessage.getUser().getId(),seckillMessage.getGoodsId());
        String goodsId = String.valueOf(seckillMessage.getGoodsId());
        User user = seckillMessage.getUser();
        if(StringUtils.isEmpty(goodsId) || ObjectUtils.isEmpty(user)){
            logger.info("goodsId或者user对象为空");
            return;
        }

        //第一步，从数据库检查商品库存
        SecKillGoods secKillGoods = goodsMapper.getSecKillGoods(goodsId);
        if(ObjectUtils.isEmpty(secKillGoods)){
            logger.info("goodsId:{}没有查到对应的商品信息",goodsId);
            return;
        }
        int count = secKillGoods.getStockCount();
        if(count<=0){
            logger.info("goodsId:{}对应的商品库存不足,count:{}",goodsId,count);
            return;
        }
        //第二步，判断是否已经秒杀到了,防止MQ中有重复消息
        SeckillOrder seckillOrder = seckillOrderMapper.selectByUserIdAndGoodsId(user.getId(),Long.parseLong(goodsId));
        if(!ObjectUtils.isEmpty(seckillOrder)){
            logger.info("userId:{},goodsId:{}已经秒杀到商品",user.getId(),goodsId);
            return;
        }

        //第三步，开始生成订单
        iSeckillOrderService.createOrder(user,secKillGoods);

    }


}
