package com.sparrow.cache.impl.redis;

import com.sparrow.cache.Keys;
import com.sparrow.constant.cache.KEY;
import com.sparrow.core.spi.JsonFactory;
import com.sparrow.json.Json;
import redis.clients.jedis.ShardedJedis;

/**
 * @author by harry
 */
public class KeysImpl implements Keys {
    private RedisPool redisPool;

    private Json jsonProvider = JsonFactory.getProvider();

    public void setRedisPool(RedisPool redisPool) {
        this.redisPool = redisPool;
    }

    @Override public boolean expire(final KEY key, final Integer expire) {
        return redisPool.write(new RedisWriter() {
            @Override public void write(ShardedJedis jedis) {
                jedis.expire(key.key(), expire);
            }
        });
    }

    @Override public boolean expireAt(final KEY key, final Long expire) {
        return redisPool.write(new RedisWriter() {
            @Override public void write(ShardedJedis jedis) {
                jedis.expireAt(key.key(), expire);
            }
        });
    }
}
