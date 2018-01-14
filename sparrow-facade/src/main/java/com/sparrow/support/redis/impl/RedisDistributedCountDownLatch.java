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

import com.sparrow.exception.CacheConnectionException;
import com.sparrow.support.latch.DistributedCountDownLatch;
import com.sparrow.support.redis.RedisReader;
import com.sparrow.support.redis.RedisWriter;
import com.sparrow.utility.RedisPool;
import com.sparrow.utility.StringUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.ShardedJedis;

/**
 * Created by harry on 2018/1/11.
 */
public class RedisDistributedCountDownLatch implements DistributedCountDownLatch {
    private static Logger logger = LoggerFactory.getLogger(RedisDistributedCountDownLatch.class);
    private RedisPool redisPool;
    private String productKey;
    private String consumeKey;

    public RedisDistributedCountDownLatch(RedisPool redisPool, String productKey, String consumeKey) {
        this.productKey = productKey;
        this.consumeKey = consumeKey;
        this.redisPool = redisPool;
    }

    public static RedisDistributedCountDownLatch getConsumer(RedisPool redisPool, String consumeKey) {
        return new RedisDistributedCountDownLatch(redisPool, null, consumeKey);
    }

    @Override
    public void consume(final String key) {
        if (StringUtility.isNullOrEmpty(this.productKey)) {
            throw new UnsupportedOperationException("consume key is null");
        }
        redisPool.write(new RedisWriter() {
            @Override
            public void write(ShardedJedis jedis) {
                jedis.sadd(RedisDistributedCountDownLatch.this.consumeKey, key);
            }
        });
    }

    @Override
    public void product(final String key) {
        if (StringUtility.isNullOrEmpty(this.productKey)) {
            throw new UnsupportedOperationException("product key is null");
        }
        redisPool.write(new RedisWriter() {
            @Override
            public void write(ShardedJedis jedis) {
                jedis.sadd(RedisDistributedCountDownLatch.this.productKey, key);
            }
        });
    }

    @Override
    public boolean isFinish() {
        if (StringUtility.isNullOrEmpty(this.productKey)) {
            throw new UnsupportedOperationException("product key is null");
        }
        try {
            return redisPool.read(new RedisReader<Boolean>() {
                @Override
                public Boolean read(ShardedJedis jedis) {
                    Long productCount = jedis.scard(RedisDistributedCountDownLatch.this.productKey);
                    Long consumeCount = jedis.scard(RedisDistributedCountDownLatch.this.consumeKey);
                    Boolean match = productCount.equals(consumeCount);
                    logger.info("product key{}:count{},consume key{}:count{}, match {}", RedisDistributedCountDownLatch.this.productKey,
                        productCount,
                        RedisDistributedCountDownLatch.this.consumeKey,
                        consumeCount,
                        match);
                    if (match) {
                        while (true) {
                            if (redisPool.write(new RedisWriter() {
                                @Override
                                public void write(ShardedJedis jedis) {
                                    jedis.expireAt(RedisDistributedCountDownLatch.this.productKey, -1);
                                    jedis.expireAt(RedisDistributedCountDownLatch.this.consumeKey, -1);
                                }
                            })) {
                                return true;
                            }
                        }
                    }
                    return false;
                }
            });
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
}
