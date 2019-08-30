package com.hz.ms.mapper;

import com.hz.ms.model.OrderInfo;
import org.springframework.stereotype.Repository;

/**
 *
 */
@Repository
public interface OrderMapper {

    /**
     * 添加订单
     * @param orderInfo
     * @return
     */
    public int insertSelective(OrderInfo orderInfo);
}
