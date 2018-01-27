package com.sparrow.cache;

import com.sparrow.constant.cache.KEY;
import com.sparrow.exception.CacheConnectionException;

import java.util.List;

/**
 * Created by harry on 2018/1/26.
 */
public interface CacheList {
    Long getSize(KEY key) throws CacheConnectionException;

    <T> Long add(KEY key, T value) throws CacheConnectionException;

    Long add(KEY key, String... value) throws CacheConnectionException;

    <T> Integer add(KEY key, Iterable<T> values) throws CacheConnectionException;

    <T> Long remove(KEY key, T value) throws CacheConnectionException;

    List<String> list(KEY key) throws CacheConnectionException;

    <T> List<T> list(KEY key, Class clazz) throws CacheConnectionException;

    List<String> list(KEY key, CacheDataNotFound<List<String>> hook);

    <T> List<T> list(KEY key, Class clazz, CacheDataNotFound<List<T>> hook);
}
