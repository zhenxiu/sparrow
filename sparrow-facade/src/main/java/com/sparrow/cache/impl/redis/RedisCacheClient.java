package com.sparrow.cache.impl.redis;

import com.sparrow.cache.CacheClient;
import com.sparrow.constant.cache.KEY;
import com.sparrow.core.TypeConverter;
import com.sparrow.core.spi.JsonFactory;
import com.sparrow.exception.CacheConnectionException;
import com.sparrow.json.Json;
import com.sparrow.support.Entity;
import com.sparrow.utility.StringUtility;
import org.apache.poi.ss.formula.functions.T;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.Tuple;

import java.util.*;

/**
 * Created by harry on 2018/1/18.
 */
public class RedisCacheClient implements CacheClient {
    private RedisPool redisPool;

    private Json jsonProvider = JsonFactory.getProvider();

    public void setRedisPool(RedisPool redisPool) {
        this.redisPool = redisPool;
    }

    @Override
    public Map<String, String> hashGetAll(final KEY key) throws CacheConnectionException {
        return this.hashGetAll(key, String.class, String.class);
    }

    @Override
    public <K, T> Map<K, T> hashGetAll(final KEY key, final Class keyClazz, final Class clazz) throws CacheConnectionException {
        return redisPool.execute(new Executor<Map<K, T>>() {
            @Override
            public Map<K, T> execute(ShardedJedis jedis) throws CacheConnectionException {
                Json json = JsonFactory.getProvider();
                Map<K, T> result = new HashMap<K, T>();
                Map<String, String> map = jedis.hgetAll(key.key());
                TypeConverter typeConverter = new TypeConverter(clazz);
                TypeConverter keyTypeConverter = new TypeConverter(keyClazz);
                for (String k : map.keySet()) {
                    if (StringUtility.isNullOrEmpty(map.get(k))) {
                        continue;
                    }
                    T t = null;
                    if (Entity.class.isAssignableFrom(clazz)) {
                        t = (T) json.parse(map.get(k), clazz);
                    } else {
                        t = (T) typeConverter.convert(map.get(k));
                    }
                    result.put((K) keyTypeConverter.convert(k), t);
                }
                return result;
            }
        });
    }

    @Override
    public Long getHashSize(final KEY key) throws CacheConnectionException {
        return redisPool.execute(new Executor<Long>() {
            @Override
            public Long execute(ShardedJedis jedis) throws CacheConnectionException {
                return jedis.hlen(key.key());
            }
        });
    }

    @Override
    public String hashGet(final KEY key, final String hashKey) throws CacheConnectionException {
        return redisPool.execute(new Executor<String>() {
            @Override
            public String execute(ShardedJedis jedis) throws CacheConnectionException {
                return jedis.hget(key.key(), hashKey);
            }
        });
    }

    @Override
    public <T> T hashGet(final KEY key, final String hashKey, final Class clazz) throws CacheConnectionException {
        return redisPool.execute(new Executor<T>() {
            @Override
            public T execute(ShardedJedis jedis) throws CacheConnectionException {
                String value = jedis.hget(key.key(), hashKey);
                if (StringUtility.isNullOrEmpty(value)) {
                    return null;
                }
                if (Entity.class.isAssignableFrom(clazz)) {
                    return (T)jsonProvider.parse(value, clazz);
                }
                TypeConverter typeConverter = new TypeConverter(clazz);
                return (T) typeConverter.convert(value);
            }
        });
    }

    @Override
    public Long hashSet(final KEY key, final String hashKey, final Object value) throws CacheConnectionException {
        return redisPool.execute(new Executor<Long>() {
            @Override
            public Long execute(ShardedJedis jedis) {
                return jedis.hset(key.key(), hashKey, value.toString());
            }
        });
    }

    @Override
    public Long getSetSize(final KEY key) throws CacheConnectionException {
        return redisPool.execute(new Executor<Long>() {
            @Override
            public Long execute(ShardedJedis jedis) {
                return jedis.scard(key.key());
            }
        });
    }

    @Override
    public Long addToSet(final KEY key, final Object value) throws CacheConnectionException {
        return redisPool.execute(new Executor<Long>() {
            @Override
            public Long execute(ShardedJedis jedis) {
                return jedis.sadd(key.key(), value.toString());
            }
        });
    }

    @Override
    public Long addToSet(final KEY key, final String... value) throws CacheConnectionException {
        return redisPool.execute(new Executor<Long>() {
            @Override
            public Long execute(ShardedJedis jedis) {
                return jedis.sadd(key.key(), value);
            }
        });
    }

    @Override
    public Integer addToSet(final KEY key, final Iterable<Object> values) throws CacheConnectionException {
        return redisPool.execute(new Executor<Integer>() {
            @Override
            public Integer execute(ShardedJedis jedis) {
                int i = 0;
                for (Object value : values) {
                    if (value == null) {
                        continue;
                    }
                    i++;
                    jedis.sadd(key.key(), value.toString());
                }
                return i;
            }
        });
    }

    @Override
    public Long removeFromSet(final KEY key, final Object value) throws CacheConnectionException {
        return redisPool.execute(new Executor<Long>() {
            @Override
            public Long execute(ShardedJedis jedis) {
                return jedis.srem(key.key(), value.toString());
            }
        });
    }

    @Override
    public Boolean existInSet(final KEY key, final Object value) throws CacheConnectionException {
        return redisPool.execute(new Executor<Boolean>() {
            @Override
            public Boolean execute(ShardedJedis jedis) {
                return jedis.sismember(key.key(), value.toString());
            }
        });
    }


    //order set
    @Override
    public Long getOrderSetSize(final KEY key) throws CacheConnectionException {
        return redisPool.execute(new Executor<Long>() {
            @Override
            public Long execute(ShardedJedis jedis) {
                return jedis.zcard(key.key());
            }
        });
    }

    @Override
    public Long addToOrderSet(final KEY key, final Object value, final Long score) throws CacheConnectionException {
        return redisPool.execute(new Executor<Long>() {
            @Override
            public Long execute(ShardedJedis jedis) {
                return jedis.zadd(key.key(),score, value.toString());
            }
        });
    }


    @Override
    public Long removeFromOrderSet(final KEY key, final Object value) throws CacheConnectionException {
        return redisPool.execute(new Executor<Long>() {
            @Override
            public Long execute(ShardedJedis jedis) {
                return jedis.zrem(key.key(), value.toString());
            }
        });
    }

    @Override
    public Long removeFromOrderSet(final KEY key, final Long from, final Long to) throws CacheConnectionException {
        return redisPool.execute(new Executor<Long>() {
            @Override
            public Long execute(ShardedJedis jedis) {
                return jedis.zremrangeByRank(key.key(), from, to);
            }
        });
    }

    @Override
    public Double getScore(final KEY key, final Object value) throws CacheConnectionException {
        return redisPool.execute(new Executor<Double>() {
            @Override
            public Double execute(ShardedJedis jedis) {
                return jedis.zscore(key.key(), value.toString());
            }
        });
    }

    @Override
    public Long getIndexOfOrderSet(final KEY key, final Object value) throws CacheConnectionException {
        return redisPool.execute(new Executor<Long>() {
            @Override
            public Long execute(ShardedJedis jedis) {
                return jedis.zrank(key.key(), value.toString());
            }
        });
    }

    @Override
    public <T> Map<T,Double> getAllWithScore(final KEY key,final Class clazz) throws CacheConnectionException {
        return redisPool.execute(new Executor<Map<T,Double>>() {
            @Override
            public Map<T,Double> execute(ShardedJedis jedis) {
                Set<Tuple> tuples= jedis.zrevrangeWithScores(key.key(),0,-1);
                Map<T,Double> scoreMap=new LinkedHashMap<T, Double>(tuples.size());
                for(Tuple tuple:tuples){
                    T k;
                    if (Entity.class.isAssignableFrom(tuple.getElement().getClass())) {
                        k= (T)JsonFactory.getProvider().parse(tuple.getElement(), clazz);
                    }
                    else {
                        TypeConverter typeConverter = new TypeConverter(tuple.getElement().getClass());
                        k = (T) typeConverter.convert(tuple.getElement());
                    }
                    scoreMap.put(k,tuple.getScore());
                }
                return scoreMap;
            }
        });
    }


    @Override
    public Long getListSize(final KEY key) throws CacheConnectionException {
        return redisPool.execute(new Executor<Long>() {
            @Override
            public Long execute(ShardedJedis jedis) {
                return jedis.llen(key.key());
            }
        });
    }

    @Override
    public Long addToList(final KEY key, final Object value) throws CacheConnectionException {
        return redisPool.execute(new Executor<Long>() {
            @Override
            public Long execute(ShardedJedis jedis) {
                return jedis.rpush(key.key(), value.toString());
            }
        });
    }

    @Override
    public Long removeFromList(final KEY key, final Object value) throws CacheConnectionException {
        return redisPool.execute(new Executor<Long>() {
            @Override
            public Long execute(ShardedJedis jedis) {
                return jedis.lrem(key.key(), 1L, value.toString());
            }
        });
    }


    @Override
    public Long addToList(final KEY key, final String... value) throws CacheConnectionException {
        return redisPool.execute(new Executor<Long>() {
            @Override
            public Long execute(ShardedJedis jedis) {
                return jedis.lpush(key.key(), value);
            }
        });
    }

    @Override
    public Integer addToList(final KEY key, final Iterable<Object> values) throws CacheConnectionException {
        return redisPool.execute(new Executor<Integer>() {
            @Override
            public Integer execute(ShardedJedis jedis) {
                int i = 0;
                for (Object value : values) {
                    if (value == null) {
                        continue;
                    }
                    i++;
                    jedis.lpush(key.key(), value.toString());
                }
                return i;
            }
        });
    }

    @Override
    public Long expire(final KEY key, final Integer expire) throws CacheConnectionException {
        return redisPool.execute(new Executor<Long>() {
            @Override
            public Long execute(ShardedJedis jedis) {
                return jedis.expire(key.key(), expire);
            }
        });
    }

    @Override
    public Long delete(final KEY key) throws CacheConnectionException {
        return redisPool.execute(new Executor<Long>() {
            @Override
            public Long execute(ShardedJedis jedis) {
                return jedis.expireAt(key.key(), -1L);
            }
        });
    }

    @Override
    public Long expireAt(final KEY key, final Long expire) throws CacheConnectionException {
        return redisPool.execute(new Executor<Long>() {
            @Override
            public Long execute(ShardedJedis jedis) {
                return jedis.expireAt(key.key(), expire);
            }
        });
    }

    @Override
    public String setExpire(final KEY key, final Integer seconds, final Object value) throws CacheConnectionException {
        return redisPool.execute(new Executor<String>() {
            @Override
            public String execute(ShardedJedis jedis) {
                return jedis.setex(key.key(), seconds, value.toString());
            }
        });
    }

    @Override
    public String set(final KEY key, final Entity value) throws CacheConnectionException {
        return redisPool.execute(new Executor<String>() {
            @Override
            public String execute(ShardedJedis jedis) {
                return jedis.set(key.key(), jsonProvider.toString(value));
            }
        });
    }

    @Override
    public String set(final KEY key, final Object value) throws CacheConnectionException {
        return redisPool.execute(new Executor<String>() {
            @Override
            public String execute(ShardedJedis jedis) {
                return jedis.set(key.key(), value.toString());
            }
        });
    }

    @Override
    public String get(final KEY key) throws CacheConnectionException {
        return redisPool.execute(new Executor<String>() {
            @Override
            public String execute(ShardedJedis jedis) throws CacheConnectionException {
                return jedis.get(key.key());
            }
        });
    }

    @Override
    public <T> T get(final KEY key, final Class clazz) throws CacheConnectionException {
        return redisPool.execute(new Executor<T>() {
            @Override
            public T execute(ShardedJedis jedis) throws CacheConnectionException {
                String json = jedis.get(key.key());
                if (StringUtility.isNullOrEmpty(json)) {
                    return null;
                }
                if (Entity.class.isAssignableFrom(clazz)) {
                    return (T) jsonProvider.parse(json, clazz);
                }
                TypeConverter typeConverter = new TypeConverter(clazz);
                return (T) typeConverter.convert(json);
            }
        });
    }

    @Override
    public List<String> getAllOfList(final KEY key) throws CacheConnectionException {
        return this.getAllOfList(key, String.class);
    }

    @Override
    public <T> List<T> getAllOfList(final KEY key, final Class clazz) throws CacheConnectionException {
        return redisPool.execute(new Executor<List<T>>() {
            @Override
            public List<T> execute(ShardedJedis jedis) throws CacheConnectionException {
                Long size = jedis.llen(key.key());
                List<String> list = jedis.lrange(key.key(), 0, size - 1);
                List<T> tList = new ArrayList<T>(size.intValue());
                TypeConverter typeConverter = new TypeConverter(null, clazz);
                for (String s : list) {
                    if (Entity.class.isAssignableFrom(clazz)) {
                        tList.add((T) jsonProvider.parse(s, clazz));
                        continue;
                    }
                    tList.add((T) typeConverter.convert(s));
                }
                return tList;
            }
        });
    }

    @Override
    public Long setIfNotExist(final KEY key, final Object value) throws CacheConnectionException {
        return redisPool.execute(new Executor<Long>() {
            @Override
            public Long execute(ShardedJedis jedis) {
                return jedis.setnx(key.key(), value.toString());
            }
        });
    }

    @Override
    public Long append(final KEY key, final Object value) throws CacheConnectionException {
        return redisPool.execute(new Executor<Long>() {
            @Override
            public Long execute(ShardedJedis jedis) {
                return jedis.append(key.key(), value.toString());
            }
        });
    }

    @Override
    public Long decrease(final KEY key) throws CacheConnectionException {
        return redisPool.execute(new Executor<Long>() {
            @Override
            public Long execute(ShardedJedis jedis) {
                return jedis.decr(key.key());
            }
        });
    }

    @Override
    public Long decrease(final KEY key, final Long count) throws CacheConnectionException {
        return redisPool.execute(new Executor<Long>() {
            @Override
            public Long execute(ShardedJedis jedis) {
                return jedis.decrBy(key.key(), count);
            }
        });
    }

    @Override
    public Long increase(final KEY key, final Long count) throws CacheConnectionException {
        return redisPool.execute(new Executor<Long>() {
            @Override
            public Long execute(ShardedJedis jedis) {
                return jedis.incrBy(key.key(), count);
            }
        });
    }

    @Override
    public Long increase(final KEY key) throws CacheConnectionException {
        return redisPool.execute(new Executor<Long>() {
            @Override
            public Long execute(ShardedJedis jedis) {
                return jedis.incr(key.key());
            }
        });
    }

    @Override
    public boolean bit(final KEY key, final Integer offset) throws CacheConnectionException {
        return redisPool.execute(new Executor<Boolean>() {
            @Override
            public Boolean execute(ShardedJedis jedis) {
                return jedis.getbit(key.key(), offset);
            }
        });
    }
}
