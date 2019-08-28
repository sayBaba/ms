package com.hz.ms.mapper;

import com.hz.ms.model.SecKillGoods;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GoodsMapper {

    /**
     * 获取秒杀商品列表
     * @return
     */
    List<SecKillGoods> getAllSecKillGoods();

}
