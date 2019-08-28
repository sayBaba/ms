package com.hz.ms.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 秒杀列表信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SecKillGoods {

    private BigDecimal seckillPrice;

    private Integer stockCount;

    private Date startDate;

    private Date endDate;

    private Long id;

    private String goodsName;

    private String goodsTitle;

    private String goodsImg;

    private BigDecimal goodsPrice;

    private Integer goodsStock;

    private Date createDate;

    private Date updateDate;

    private String goodsDetail;

    public static void main(String[] args) {
         BigDecimal seckillPrice = new BigDecimal("9999999999999999999");
        System.out.println();
    }

}
