package com.sparrow.redis;

import com.sparrow.cache.CacheClient;
import com.sparrow.cache.impl.redis.RedisPool;
import com.sparrow.constant.cache.KEY;
import com.sparrow.constant.cache.key.KEY_CMS;
import com.sparrow.container.Container;
import com.sparrow.container.impl.SparrowContainerImpl;
import com.sparrow.exception.CacheConnectionException;
import com.sparrow.support.ModuleSupport;

/**
 * @author by harry
 */
public class RedisTest {
    public static void main(String[] args) throws CacheConnectionException {
        Container container = new SparrowContainerImpl();
        //定义模块，一个业务会存在多个模块
        ModuleSupport OD=new ModuleSupport() {
            @Override
            public String code() {
                return "01";
            }

            @Override
            public String name() {
                return "OD";
            }
        };

        //相同模块下会存在多个业务
        KEY.Business od=new KEY.Business(OD,"POOL");
        container.init();
        CacheClient client = container.getBean("cacheClient");
        //相同业务下存在多个KEY
        KEY key = new KEY.Builder().business(od).businessId("BJS","CHI","HU").build();
        client.set(key, "test");

        KEY k2=KEY.parse("OD.POOL:BJS.CHI.HU");
        System.out.println("key:"+k2.key()+",module:"+k2.getModule()+" business:"+k2.getBusiness());
    }
}
