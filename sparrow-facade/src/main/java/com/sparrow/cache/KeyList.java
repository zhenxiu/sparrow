package com.sparrow.cache;

import com.sparrow.constant.cache.KEY;
import com.sparrow.exception.CacheConnectionException;

/**
 * @author by harry
 */
public interface KeyList {
    boolean add(KEY key, Object value);

    boolean add(KEY key, String... value);

    Long length(KEY key) throws CacheConnectionException;
}
