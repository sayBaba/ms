package com.hz.ms.service.impl;

import com.hz.ms.common.RedisKeyConstant;
import com.hz.ms.mapper.GoodsMapper;
import com.hz.ms.mapper.SeckillOrderMapper;
import com.hz.ms.model.SecKillGoods;
import com.hz.ms.model.SeckillOrder;
import com.hz.ms.model.User;
import com.hz.ms.mq.MQConfig;
import com.hz.ms.mq.SeckillMessage;
import com.hz.ms.redis.RedisUtil;
import com.hz.ms.resp.CodeMsg;
import com.hz.ms.resp.Result;
import com.hz.ms.service.ISecKillService;
import com.hz.ms.utils.MD5Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.UUID;

/**
 * 秒杀接口实现类
 */
@Service
public class SecKillServiceImpl implements ISecKillService {

    private static final Logger logger = LoggerFactory.getLogger(SecKillServiceImpl.class);

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private GoodsMapper goodsMapper;

    @Autowired
    private SeckillOrderMapper seckillOrderMapper;

    @Autowired
    private AmqpTemplate amqpTemplate;

    @Override
    public String createPath(String goodsId, User user) {
        String redisKey = user.getId()+goodsId;
        //第一步生成加密串
        String path = MD5Util.inputPassToFormPass(UUID.randomUUID().toString().replace("-",""));
        logger.info("redisKey为：{},生成的path：{}",redisKey,path);
        //第二步保存redis
        redisUtil.set(redisKey,path);
        return path;
    }


    @Override
    public void initSkillGoods() {
        //第一步从数据库读出来
        List<SecKillGoods> killGoodsList = goodsMapper.getAllSecKillGoods();
        if(CollectionUtils.isEmpty(killGoodsList)){
            return;
        }
        //第二步写入redis
        for (SecKillGoods killGoods:killGoodsList){
            redisUtil.set(RedisKeyConstant.GOODS_NUM + killGoods.getId(),killGoods.getStockCount());
        }

    }

    @Override
    public Result secKill(String goodsId, User user, String path) {
        //验证path
        String redisKey = user.getId()+goodsId;
        String oldPath = (String) redisUtil.get(redisKey);
        if (StringUtils.isEmpty(oldPath)){
            return Result.getFail(CodeMsg.REQUEST_ILLEGAL.getCode(),CodeMsg.REQUEST_ILLEGAL.getMsg());
        }
        if(!oldPath.equals(path)){
            return Result.getFail(CodeMsg.REQUEST_ILLEGAL.getCode(),CodeMsg.REQUEST_ILLEGAL.getMsg());
        }

        //检查是否存在订单
        SeckillOrder seckillOrder = seckillOrderMapper.selectByUserIdAndGoodsId(user.getId(),Long.parseLong(goodsId));
        if (!ObjectUtils.isEmpty(seckillOrder)){
            return Result.getFail(CodeMsg.REPEATE_MIAOSHA.getCode(),CodeMsg.REPEATE_MIAOSHA.getMsg());
        }

        //检查库存是否充足并预减库存
        int num = redisUtil.decrement(RedisKeyConstant.GOODS_NUM +goodsId);
        if(num < 0){
            return Result.getFail(CodeMsg.MIAO_SHA_OVER.getCode(),CodeMsg.MIAO_SHA_OVER.getMsg());
        }
        //最后存mq,异步消费
        SeckillMessage seckillMessage = new SeckillMessage();
        seckillMessage.setUser(user);
        seckillMessage.setGoodsId(Long.parseLong(goodsId));
        amqpTemplate.convertAndSend(MQConfig.MIAOSHA_QUEUE,seckillMessage);
        Result result = new Result();
        result.setCode(CodeMsg.SUCCESS.getCode());
        result.setMsg(CodeMsg.SUCCESS.getMsg());
        return result;
    }

    @Override
    public Result getKillResult(String goodsId, User user) {
        //根据用户id 和商品id 查询预订单
        Result result = new Result();
        result.setCode(CodeMsg.SUCCESS.getCode());
        result.setMsg(CodeMsg.SUCCESS.getMsg());
        SeckillOrder seckillOrder = seckillOrderMapper.selectByUserIdAndGoodsId(user.getId(),Long.parseLong(goodsId));
        if (!ObjectUtils.isEmpty(redisUtil.get("go"+goodsId))){
            boolean flag = (boolean)redisUtil.get("go"+goodsId);
            if(flag == true){
                result.setData(-1);
                return result;
            }
        }

        if(ObjectUtils.isEmpty(seckillOrder)){
            result.setData(0);
            return result;
        }

        String orderId = seckillOrder.getOrderId().toString();
        result.setData(orderId);
        return result;
    }

}
