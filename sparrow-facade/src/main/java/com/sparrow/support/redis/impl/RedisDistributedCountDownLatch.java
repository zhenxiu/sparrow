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
    private KEY monitor;
    private CacheClient cacheClient;

    public RedisDistributedCountDownLatch(CacheClient cacheClient,KEY monitorKey) {
        this.cacheClient=cacheClient;
        this.monitor = monitorKey;
    }

    @Override
    public void consume(final String key) {
        if (this.monitor ==null) {
            throw new UnsupportedOperationException("product key is null");
        }
        while (true) {
            try {
             cacheClient.removeFromSet(this.monitor, key);
                return;
            } catch (CacheConnectionException e) {
                logger.error("monitor consume connection break ", e);
            }
        }
    }

    @Override
    public void product(final String key) {
        if (this.monitor ==null) {
            throw new UnsupportedOperationException("product key is null");
        }
        while (true) {
            try {
                cacheClient.addToSet(this.monitor, key);
                return;
            } catch (CacheConnectionException e) {
                logger.error("monitor product connection break ", e);
            }
        }
    }

    @Override
    public boolean isFinish() {
        if (StringUtility.isNullOrEmpty(this.monitor)) {
            throw new UnsupportedOperationException("product key is null");
        }
        Long productCount = null;
        try {
            productCount = cacheClient.getSetSize(this.monitor);
            Boolean match = productCount==0;
            logger.info("product key{}:count{},match {}", RedisDistributedCountDownLatch.this.isFinish(),
                    productCount,
                    match);
            if (match) {
                while (true) {
                    Long success = cacheClient.expireAt(this.monitor, -1L);
                    if (success>0) {
                        return true;
                    }
                }
            }
            return false;
        } catch (CacheConnectionException e) {
            return false;
        }
    }

    @Override
    public boolean monitor(int secondInterval) {
        while (true) {
            if (isFinish()) {
                return true;
            }
            try {
                Thread.sleep(1000 * secondInterval);
            } catch (InterruptedException ignore) {
            }
        }
    }

    @Override
    public boolean monitor() {
       return this.monitor(2);
    }
}
