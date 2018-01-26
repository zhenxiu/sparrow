package com.sparrow.cache;

import com.sparrow.constant.cache.KEY;

/**
 * Created by harry on 2018/1/26.
 */
public interface CacheDataNotFound<T> {
    T read(KEY key);
}
