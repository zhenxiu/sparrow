/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sparrow.support.redis.impl;

import com.sparrow.cache.CacheClient;
import com.sparrow.constant.cache.KEY;
import com.sparrow.exception.CacheConnectionException;
import com.sparrow.support.latch.DistributedCountDownLatch;
import com.sparrow.utility.StringUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by harry on 2018/1/11.
 */
public class RedisDistributedCountDownLatch implements DistributedCountDownLatch {
    private static Logger logger = LoggerFactory.getLogger(RedisDistributedCountDownLatch.class);
    private CacheClient cacheClient;

    public RedisDistributedCountDownLatch() {
    }

    public RedisDistributedCountDownLatch(CacheClient cacheClient) {
        this.cacheClient = cacheClient;
    }

    public void setCacheClient(CacheClient cacheClient) {
        this.cacheClient = cacheClient;
    }

    @Override public boolean exist(KEY monitor, String key) {
        try {
            return cacheClient.set().exist(monitor, key);
        } catch (CacheConnectionException e) {
            logger.error("keys is exist connection exception " + key, e);
            return false;
        }
    }

    @Override
    public void consume(KEY monitor, final String key) {
        if (monitor == null) {
            throw new UnsupportedOperationException("product key is null");
        }
        while (true) {
            try {
                cacheClient.set().remove(monitor, key);
                return;
            } catch (CacheConnectionException e) {
                logger.error("monitor consume connection break ", e);
            }
        }
    }

    @Override
    public void product(KEY monitor, final String key) {
        if (monitor == null) {
            throw new UnsupportedOperationException("product key is null");
        }
        while (true) {
            try {
                cacheClient.set().add(monitor, key);
                return;
            } catch (CacheConnectionException e) {
                logger.error("monitor product connection break ", e);
            }
        }
    }

    @Override
    public boolean isFinish(KEY monitor) {
        if (StringUtility.isNullOrEmpty(monitor)) {
            throw new UnsupportedOperationException("product key is null");
        }
        try {
            Long productCount = cacheClient.set().getSize(monitor);
            Boolean match = productCount == 0;
            logger.info("product key {}:count {},match {}", monitor.key(),
                productCount,
                match);
            if (!match) {
                return false;
            }
        } catch (CacheConnectionException e) {
            return false;
        }
        while (true) {
            try {
                cacheClient.key().delete(monitor);
                return true;
            } catch (CacheConnectionException ignore) {
                logger.error("monitor error", ignore);
            }
        }
    }

    @Override
    public boolean monitor(KEY monitor, int secondInterval) {
        while (true) {
            if (isFinish(monitor)) {
                return true;
            }
            try {
                Thread.sleep(1000 * secondInterval);
            } catch (InterruptedException ignore) {
            }
        }
    }

    @Override
    public boolean monitor(KEY monitor) {
        return this.monitor(monitor, 2);
    }

}
