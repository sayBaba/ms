package com.hz.ms.service;

import com.hz.ms.model.SecKillGoods;
import com.hz.ms.model.User;
import com.hz.ms.resp.Result;

import java.util.List;

/**
 *秒杀相关接口
 */
public interface ISecKillService {

    /**
     * 生成接口地址
     * @param goodsId
     * @param user
     * @return
     */
    public String createPath(String goodsId, User user);

    /**
     * 初始化秒杀商品
     * @return
     */
    public void initSkillGoods();

    /**
     * 秒杀接口
     * @param goodsId
     * @param user
     * @param path
     */
    public Result secKill(String goodsId, User user, String path);

    /**
     * 查询秒杀结果
     * @param goodsId
     * @param user
     * @return
     */
    public Result getKillResult(String goodsId, User user);



}
