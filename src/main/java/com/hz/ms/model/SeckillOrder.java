package com.hz.ms.model;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SeckillOrder {

    private Long id;

    private Long userId;

    private Long orderId;

    private Long goodsId;

}