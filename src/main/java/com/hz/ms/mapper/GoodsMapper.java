package com.hz.ms.mapper;

import com.hz.ms.model.SecKillGoods;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GoodsMapper {

    /**
     * 获取秒杀商品列表
     * @return
     */
    List<SecKillGoods> getAllSecKillGoods();

    /**
     * 根据商品Id查询商品详情
     * @param goodsId
     * @return
     */
    SecKillGoods getSecKillGoods(@Param("goodsId") String goodsId);

    /**
     * 减库存
     * @param goodsId
     * @return
     */
    int updateStock(long goodsId);


}
