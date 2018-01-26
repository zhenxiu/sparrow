package com.sparrow.cache;

import com.sparrow.constant.cache.KEY;
import com.sparrow.exception.CacheConnectionException;

import java.util.Set;

/**
 * Created by harry on 2018/1/26.
 */
public interface CacheSet {

    Long getSize(KEY key) throws CacheConnectionException;

    Long add(KEY key, Object value) throws CacheConnectionException;

    Long add(KEY key, String... value) throws CacheConnectionException;

    <T> Integer add(KEY key, Iterable<T> values) throws CacheConnectionException;

    Long remove(KEY key, Object value) throws CacheConnectionException;

    Boolean exist(KEY key, Object value) throws CacheConnectionException;

    Set<String> list(KEY key) throws CacheConnectionException;

    <T> Set<T> list(KEY key, Class clazz) throws CacheConnectionException;

    Set<String> list(KEY key, CacheDataNotFound<Set<String>> hook);

    <T> Set<T> list(KEY key, Class clazz, CacheDataNotFound<Set<T>> hook);
}
