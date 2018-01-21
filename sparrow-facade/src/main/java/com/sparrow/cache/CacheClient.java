package com.sparrow.cache;

import com.sparrow.constant.cache.KEY;
import com.sparrow.exception.CacheConnectionException;

import java.util.Map;

/**
 * @author harry
 * @date 2018/1/18
 */
public interface CacheClient {
    Map<String,String> hashGetAll(KEY key) throws CacheConnectionException;

    <T> Map<String, T> hashGetAll(KEY key, Class clazz) throws CacheConnectionException;

    Long getHashSize(KEY key) throws CacheConnectionException;

    Long getSetSize(KEY key) throws CacheConnectionException;

    Long getListSize(KEY key) throws CacheConnectionException;

    String hashGet(KEY key, String hashKey) throws CacheConnectionException;

    <T> T hashGet(KEY key, String hashKey, Class clazz) throws CacheConnectionException;

    Long hashSet(KEY key, String hashKey, Object value) throws CacheConnectionException;

    Long addToSet(KEY key, Object value) throws CacheConnectionException;

    Long addToSet(KEY key, String... value) throws CacheConnectionException;

    Integer addToSet(KEY key, Iterable<Object> values) throws CacheConnectionException;

    Long addToList(KEY key, Object value) throws CacheConnectionException;

    Long removeFromList(KEY key, Object value) throws CacheConnectionException;

    Long removeFromSet(KEY key, Object value) throws CacheConnectionException;

    Boolean existInSet(KEY key, Object value) throws CacheConnectionException;

    Long addToList(KEY key, String... value) throws CacheConnectionException;

    Integer addToList(KEY key, Iterable<Object> values) throws CacheConnectionException;

    Long expire(KEY key, Integer expire) throws CacheConnectionException;

    Long delete(KEY key) throws CacheConnectionException;

    Long expireAt(KEY key, Long expire) throws CacheConnectionException;

    String setExpire(KEY key, Integer seconds, Object value) throws CacheConnectionException;

    String set(KEY key, Object value) throws CacheConnectionException;

    String get(KEY key) throws CacheConnectionException;

    <T> T get(KEY key, Class clazz) throws CacheConnectionException;

    Long setIfNotExist(KEY key, Object value) throws CacheConnectionException;

    Long append(KEY key, Object value) throws CacheConnectionException;

    Long decrease(KEY key) throws CacheConnectionException;

    Long decrease(KEY key, Long count) throws CacheConnectionException;

    Long increase(KEY key, Long count) throws CacheConnectionException;

    Long increase(KEY key) throws CacheConnectionException;

    boolean bit(KEY key, Integer offset) throws CacheConnectionException;
}
