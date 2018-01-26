package com.sparrow.cache;

import com.sparrow.constant.cache.KEY;
import com.sparrow.exception.CacheConnectionException;

/**
 * Created by harry on 2018/1/26.
 */
public interface CacheString {
    String set(KEY key, Object value) throws CacheConnectionException;

    String get(KEY key) throws CacheConnectionException;

    String get(KEY key, CacheDataNotFound<String> hook);

    <T> T get(KEY key, Class clazz, CacheDataNotFound<T> hook);

    <T> T get(KEY key, Class clazz) throws CacheConnectionException;

    Long append(KEY key, Object value) throws CacheConnectionException;

    Long decrease(KEY key) throws CacheConnectionException;

    Long decrease(KEY key, Long count) throws CacheConnectionException;

    Long increase(KEY key, Long count) throws CacheConnectionException;

    Long increase(KEY key) throws CacheConnectionException;

    boolean bit(KEY key, Integer offset) throws CacheConnectionException;

    String setExpire(KEY key, Integer seconds, Object value) throws CacheConnectionException;

    Long setIfNotExist(KEY key, Object value) throws CacheConnectionException;
}
