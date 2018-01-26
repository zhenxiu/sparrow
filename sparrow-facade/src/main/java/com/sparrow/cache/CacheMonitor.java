package com.sparrow.cache;

import com.sparrow.constant.cache.KEY;

/**
 * Created by harry on 2018/1/25.
 */
public interface CacheMonitor {
    /**
     * true 继续执行false结束
     *
     * @param startTime
     * @param key
     * @return
     */
    boolean before(Long startTime, KEY key);

    /**
     * 时间监控
     *
     * @param startTime
     * @param endTime
     * @param key
     */
    void monitor(Long startTime, Long endTime, KEY key);
}
