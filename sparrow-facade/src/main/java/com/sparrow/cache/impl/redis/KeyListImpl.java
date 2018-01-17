package com.sparrow.cache.impl.redis;

import com.sparrow.cache.KeyList;
import com.sparrow.constant.cache.KEY;
import com.sparrow.core.spi.JsonFactory;
import com.sparrow.exception.CacheConnectionException;
import com.sparrow.json.Json;
import redis.clients.jedis.ShardedJedis;

/**
 * @author by harry
 */
public class KeyListImpl implements KeyList {
    private RedisPool redisPool;

    private Json jsonProvider = JsonFactory.getProvider();

    public void setRedisPool(RedisPool redisPool) {
        this.redisPool = redisPool;
    }

    @Override public boolean add(final KEY key, final Object value) {
        return redisPool.write(new RedisWriter() {
            @Override public void write(ShardedJedis jedis) {
                jedis.rpush(key.key(), value.toString());
            }
        });
    }

    @Override public boolean add(final KEY key, final String... value) {
        return redisPool.write(new RedisWriter() {
            @Override public void write(ShardedJedis jedis) {
                jedis.lpush(key.key(), value);
            }
        });
    }

    @Override public Long length(final KEY key) throws CacheConnectionException {
        return redisPool.read(new RedisReader<Long>() {
            @Override public Long read(ShardedJedis jedis) {
                return jedis.llen(key.key());
            }
        });
    }
}
