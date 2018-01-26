package com.sparrow.cache;

/**
 * @author harry
 * @date 2018/1/18
 */
public interface CacheClient {

    CacheString string();

    CacheSet set();

    CacheOrderSet orderSet();

    CacheHash hash();

    CacheKey key();

    CacheList list();
}
