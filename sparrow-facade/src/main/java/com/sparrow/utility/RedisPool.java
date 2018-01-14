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

package com.sparrow.utility;

import com.sparrow.constant.CONFIG;
import com.sparrow.constant.CONSTANT;
import com.sparrow.exception.CacheConnectionException;
import com.sparrow.support.redis.RedisReader;
import com.sparrow.support.redis.RedisWriter;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.*;
import redis.clients.jedis.exceptions.JedisConnectionException;
import redis.clients.util.Hashing;
import redis.clients.util.Sharded;

import java.util.ArrayList;
import java.util.List;

/**
 * @author harry
 */
public class RedisPool {
    private Logger logger = LoggerFactory.getLogger(RedisPool.class);
    private static RedisPool redisPool = new RedisPool();
    private ShardedJedisPool pool = null;

    private String poolInfo;

    public boolean isOpen() {
        String redisOpen = Config.getValue(CONFIG.REDIS_OPEN);
        return redisOpen != null && ("open".equalsIgnoreCase(redisOpen) || Boolean.TRUE.toString().equalsIgnoreCase(redisOpen));
    }

    public RedisPool() {
        StringBuilder poolInfo = new StringBuilder();
        JedisPoolConfig config = new JedisPoolConfig();
        // 最大活动链接
        config.setMaxActive(100);
        config.setMaxIdle(50);
        config.setMaxWait(50000);//超时时间
        config.setTestOnBorrow(true);

        // 超过时则报错 阻塞 或增加链接数
        config.setWhenExhaustedAction(GenericObjectPool.WHEN_EXHAUSTED_FAIL);
        String host = Config.getValue(CONFIG.REDIS_HOST);
        if (StringUtility.isNullOrEmpty(host)) {
            host = CONSTANT.LOCALHOST_IP;
        }

        poolInfo.append(",host:" + host);
        String port = Config.getValue(CONFIG.REDIS_PORT);
        if (StringUtility.isNullOrEmpty(port)) {
            port = String.valueOf(Protocol.DEFAULT_PORT);
        }
        poolInfo.append(",port:" + port);
        this.poolInfo = poolInfo.toString();
        List<JedisShardInfo> jdsInfoList = new ArrayList<JedisShardInfo>(1);
        JedisShardInfo infoA = new JedisShardInfo(host, port);
        jdsInfoList.add(infoA);
        pool = new ShardedJedisPool(config, jdsInfoList, Hashing.MURMUR_HASH,
            Sharded.DEFAULT_KEY_TAG_PATTERN);
    }

    public static RedisPool getInstance() {
        return redisPool;
    }

    public boolean write(RedisWriter writer) {
        if (!isOpen()) {
            return false;
        }
        ShardedJedis jedis = null;
        try {
            jedis = this.pool.getResource();
            writer.write(jedis);
            this.pool.returnResource(jedis);
            return true;
        } catch (JedisConnectionException e) {
            this.pool.returnBrokenResource(jedis);
            logger.error(this.poolInfo + ":" + e.getMessage());
            return false;
        }
    }

    public <T> T read(RedisReader<T> reader) throws CacheConnectionException {
        if (!this.isOpen()) {
            throw new CacheConnectionException("not support");
        }
        ShardedJedis jedis = null;
        try {
            jedis = this.pool.getResource();
            T result = reader.read(jedis);
            this.pool.returnResource(jedis);
            return result;
        } catch (JedisConnectionException e) {
            this.pool.returnBrokenResource(jedis);
            logger.error(this.poolInfo + ":" + e.getMessage());
            throw new CacheConnectionException(e.getMessage());
        }
    }
}
