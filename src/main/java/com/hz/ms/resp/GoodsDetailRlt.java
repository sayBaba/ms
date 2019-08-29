package com.hz.ms.resp;

import com.hz.ms.model.SecKillGoods;
import com.hz.ms.model.User;
import lombok.Data;

/**
 * 返回商品详情bean
 */
@Data
public class GoodsDetailRlt {

    private User user;

    private SecKillGoods secKillGoods;

    private String remainSeconds; //秒杀活动状态, -1代表活动结束，0-代理活动进行中，1-活动未开始

    private int code;

    private String msg;


}
