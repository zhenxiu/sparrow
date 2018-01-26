package com.sparrow.cache;

import com.sparrow.constant.cache.KEY;
import com.sparrow.exception.CacheConnectionException;

import java.util.Map;

/**
 * Created by harry on 2018/1/26.
 */
public interface CacheHash {
    Map<String, String> getAll(KEY key) throws CacheConnectionException;

    <K, T> Map<K, T> getAll(KEY key, Class keyClazz, Class clazz) throws CacheConnectionException;

    <K, T> Map<K, T> getAll(KEY key, Class keyClazz, Class clazz, CacheDataNotFound<Map<K,T>> hook);

    Long getSize(KEY key) throws CacheConnectionException;

    String get(KEY key, String hashKey) throws CacheConnectionException;

    <T> T hash(KEY key, String hashKey, Class clazz) throws CacheConnectionException;

    Long put(KEY key, String hashKey, Object value) throws CacheConnectionException;

    <K,T> Integer put(KEY key, Map<K, T> map) throws CacheConnectionException;
}
