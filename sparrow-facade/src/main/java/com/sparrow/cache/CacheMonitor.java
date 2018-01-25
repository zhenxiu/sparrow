package com.sparrow.cache;

import com.sparrow.constant.cache.KEY;

/**
 * Created by harry on 2018/1/25.
 */
public interface CacheMonitor {
    void monitor(Long startTime, Long endTime, KEY key);
}
