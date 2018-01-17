package com.sparrow.cache.impl.redis;

import com.sparrow.cache.KeyValue;
import com.sparrow.constant.cache.KEY;
import com.sparrow.core.spi.JsonFactory;
import com.sparrow.exception.CacheConnectionException;
import com.sparrow.json.Json;
import com.sparrow.utility.StringUtility;
import redis.clients.jedis.ShardedJedis;

/**
 * @author by harry
 */
public class KeyValueImpl implements KeyValue {

    private RedisPool redisPool;

    private Json jsonProvider = JsonFactory.getProvider();

    public void setRedisPool(RedisPool redisPool) {
        this.redisPool = redisPool;
    }

    @Override public boolean set(final KEY key, final Object value) {
        return redisPool.write(new RedisWriter() {
            @Override public void write(ShardedJedis jedis) {
                jedis.set(key.key(), value.toString());
            }
        });
    }

    @Override public String get(final KEY key) throws CacheConnectionException {
        return redisPool.read(new RedisReader<String>() {
            @Override public String read(ShardedJedis jedis) throws CacheConnectionException {
                return jedis.get(key.key());
            }
        });
    }

    @Override public <T> T get(final KEY key, final Class clazz) throws CacheConnectionException {
        return redisPool.read(new RedisReader<T>() {
            @Override public T read(ShardedJedis jedis) throws CacheConnectionException {
                String json = jedis.get(key.key());
                if (StringUtility.isNullOrEmpty(json)) {
                    return null;
                }
                return (T) jsonProvider.parse(json, clazz);
            }
        });
    }

    @Override public boolean setnx(final KEY key, final Object value) {
        return redisPool.write(new RedisWriter() {
            @Override public void write(ShardedJedis jedis) {
                jedis.setnx(key.key(), value.toString());
            }
        });
    }


    @Override public boolean append(final KEY key, final Object value) {
        return redisPool.write(new RedisWriter() {
            @Override public void write(ShardedJedis jedis) {
                jedis.append(key.key(), value.toString());
            }
        });
    }

    @Override public boolean decr(final KEY key) {
        return redisPool.write(new RedisWriter() {
            @Override public void write(ShardedJedis jedis) {
                jedis.decr(key.key());
            }
        });
    }


    @Override public boolean decr(final KEY key, final Long count) {
        return redisPool.write(new RedisWriter() {
            @Override public void write(ShardedJedis jedis) {
                jedis.decrBy(key.key(),count);
            }
        });
    }

    @Override public boolean incrBy(final KEY key, final Long count) {
        return redisPool.write(new RedisWriter() {
            @Override public void write(ShardedJedis jedis) {
                jedis.incrBy(key.key(),count);
            }
        });
    }

    @Override public boolean incr(final KEY key) {
        return redisPool.write(new RedisWriter() {
            @Override public void write(ShardedJedis jedis) {
                jedis.incr(key.key());
            }
        });
    }

    @Override public boolean getbit(final KEY key, final Integer offset) {
        return redisPool.write(new RedisWriter() {
            @Override public void write(ShardedJedis jedis) {
                jedis.getbit(key.key(),offset);
            }
        });
    }
}
