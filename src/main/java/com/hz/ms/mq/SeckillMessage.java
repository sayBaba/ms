package com.hz.ms.mq;


import com.hz.ms.model.User;
import lombok.Data;

import java.io.Serializable;

/**
 * 秒杀请求的数据
 */
@Data
public class SeckillMessage implements Serializable {

	private User user;

	private long goodsId;

}
