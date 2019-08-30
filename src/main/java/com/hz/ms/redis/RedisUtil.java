package com.hz.ms.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * redis工具类
 */
@Component
public final class RedisUtil {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;


    /**
     * 根据key存value
     * @param key
     * @param value
     * @return
     */
    public boolean set(String key, Object value) {
        try {
            redisTemplate.opsForValue().set(key, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;

        }
    }

    /**
     * 根据key存value 并设置有效期
     * @param key
     * @param value
     * @param time
     * @return
     */
    public boolean set(String key, Object value, long time) {
        try {
            if (time > 0) {
                redisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
            } else {
                set(key, value);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 根据key获取值
     * @param key
     * @return
     */
    public Object get(String key){
        return key == null ? null : redisTemplate.opsForValue().get(key);
    }

    /**
     * redis中的值-1
     * @param key
     * @return
     */
    public int decrement(String key) {
        try {
           Long a = redisTemplate.opsForValue().decrement(key);
           return a.intValue();
        } catch (Exception e) {
            return -1;

        }
    }

    /**
     * redis中的值+1
     * @param key
     * @return
     */
    public int increment(String key) {
        try {
            Long a = redisTemplate.opsForValue().increment(key);
            return a.intValue();
        } catch (Exception e) {
            return -1;

        }
    }


    public void tt(){
        redisTemplate.opsForValue().decrement("goods1");
//        redisTemplate.opsForValue().increment("goods2");

    }

}
