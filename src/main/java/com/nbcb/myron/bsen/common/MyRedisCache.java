package com.nbcb.myron.bsen.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

/**
 * @author:黄孟军
 * @date:2019/1/15
 * @description:我的缓存类用于缓存用户id等
 */
public class MyRedisCache extends RedisCache {
    private final Logger logger = LoggerFactory.getLogger(MyRedisCache.class);
    private RedisTemplate redisTemplate;

    public MyRedisCache(String id) {
        super(id);
    }

    @Override
    public void putObject(Object key, Object value) {
        RedisTemplate redisTemplate = getRedisTemplate();
        String ckey = (String)key;
        ValueOperations<String,Object> opsForValue = redisTemplate.opsForValue();
        opsForValue.set(ckey, value);
    }

    @Override
    public Object getObject(Object key) {
        RedisTemplate redisTemplate = getRedisTemplate();
        String ckey = (String)key;
        ValueOperations<String,Object> opsForValue = redisTemplate.opsForValue();
        return opsForValue.get(ckey);
    }

    private RedisTemplate getRedisTemplate() {
        if (redisTemplate == null) {
            redisTemplate = ApplicationContextHolder.getBean("redisTemplate");
        }
        return redisTemplate;
    }
}
