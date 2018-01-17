package com.sparrow.cache;

import com.sparrow.constant.cache.KEY;
import com.sparrow.exception.CacheConnectionException;
import redis.clients.jedis.ShardedJedis;

/**
 * @author by harry
 */
public interface KeyValue {
    boolean set(KEY key, Object value);

    String get(KEY key) throws CacheConnectionException;

    <T> T get(KEY key, Class clazz) throws CacheConnectionException;

    boolean setnx(KEY key, Object value);

    boolean append(KEY key, Object value);

    boolean decr(KEY key);

    boolean decr(KEY key, Long count);

    boolean incrBy(KEY key, Long count);

    boolean incr(KEY key);

    boolean getbit(KEY key, Integer offset);
}
