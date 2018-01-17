package com.sparrow.cache;

import com.sparrow.constant.cache.KEY;

/**
 * @author by harry
 */
public interface Keys {
    boolean expire(KEY key, Integer expire);

    boolean expireAt(KEY key, Long expire);
}
