package com.hz.ms.mapper;

import com.hz.ms.model.SeckillOrder;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SeckillOrderMapper {

    /**
     * 添加秒杀订单
     * @param record
     * @return
     */
    int insertSelective(SeckillOrder record);

    SeckillOrder selectByUserIdAndGoodsId(@Param("userId") long userId, @Param("goodsId") long goodsId);
}