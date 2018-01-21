package com.sparrow.redis;

import com.sparrow.cache.CacheClient;
import com.sparrow.constant.cache.KEY;
import com.sparrow.constant.cache.key.KEY_CMS;
import com.sparrow.container.Container;
import com.sparrow.container.impl.SparrowContainerImpl;
import com.sparrow.exception.CacheConnectionException;

/**
 * @author by harry
 */
public class RedisTest {
    public static void main(String[] args) throws CacheConnectionException {
        Container container = new SparrowContainerImpl();
        container.init();
        CacheClient client = container.getBean("cacheClient");
        KEY key = new KEY.Builder().business(KEY_CMS.CMS_ALL_CRAWL).businessId(1, 2).build();

        client.set(key, "test");
    }
}
