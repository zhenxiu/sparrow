package com.sparrow.cache;

import com.sparrow.constant.cache.KEY;
import com.sparrow.exception.CacheConnectionException;

/**
 * Created by harry on 2018/1/26.
 */
public interface CacheKey {
    Long expire(KEY key, Integer expire) throws CacheConnectionException;

    Long delete(KEY key) throws CacheConnectionException;

    Long expireAt(KEY key, Long expire) throws CacheConnectionException;
}
