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

package com.sparrow.cache.impl.redis;

import com.sparrow.constant.CONFIG;
import com.sparrow.constant.CONSTANT;
import com.sparrow.exception.CacheConnectionException;
import com.sparrow.utility.Config;
import com.sparrow.utility.StringUtility;
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
    private ShardedJedisPool pool = null;

    private String host;
    private String port;
    private Integer maxActive = 100;
    private Integer maxIdle = 50;
    private Integer maxWait = 50000;
    private Boolean testOnBorrow = true;

    public void setHost(String host) {
        this.host = host;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public void setMaxActive(Integer maxActive) {
        this.maxActive = maxActive;
    }

    public void setMaxIdle(Integer maxIdle) {
        this.maxIdle = maxIdle;
    }

    public void setMaxWait(Integer maxWait) {
        this.maxWait = maxWait;
    }

    public void setTestOnBorrow(Boolean testOnBorrow) {
        this.testOnBorrow = testOnBorrow;
    }

    public String getInfo() {
        StringBuilder poolInfo = new StringBuilder();
        poolInfo.append(",host:");
        poolInfo.append(host);
        poolInfo.append(",port:");
        poolInfo.append(port);
        poolInfo.append("maxActive:");
        poolInfo.append(maxActive);
        poolInfo.append("maxIdle:");
        poolInfo.append(this.maxIdle);
        poolInfo.append("maxWait:");
        poolInfo.append(this.maxWait);//超时时间
        poolInfo.append("testOnBorrow");
        poolInfo.append(this.testOnBorrow);
        return poolInfo.toString();
    }

    public RedisPool() {
        JedisPoolConfig config = new JedisPoolConfig();
        // 最大活动链接
        config.setMaxActive(this.maxActive);
        config.setMaxIdle(this.maxIdle);
        config.setMaxWait(this.maxWait);//超时时间
        config.setTestOnBorrow(this.testOnBorrow);

        // 超过时则报错 阻塞 或增加链接数
        config.setWhenExhaustedAction(GenericObjectPool.WHEN_EXHAUSTED_FAIL);
        String host = Config.getValue(CONFIG.REDIS_HOST);
        if (StringUtility.isNullOrEmpty(host)) {
            host = CONSTANT.LOCALHOST_IP;
        }

        String port = Config.getValue(CONFIG.REDIS_PORT);
        if (StringUtility.isNullOrEmpty(port)) {
            port = String.valueOf(Protocol.DEFAULT_PORT);
        }

        List<JedisShardInfo> jdsInfoList = new ArrayList<JedisShardInfo>(1);
        JedisShardInfo infoA = new JedisShardInfo(host, port);
        jdsInfoList.add(infoA);
        pool = new ShardedJedisPool(config, jdsInfoList, Hashing.MURMUR_HASH,
            Sharded.DEFAULT_KEY_TAG_PATTERN);
    }

    public <T> T execute(Executor<T> reader) throws CacheConnectionException {
        ShardedJedis jedis = null;
        try {
            jedis = this.pool.getResource();
            T result = reader.execute(jedis);
            this.pool.returnResource(jedis);
            return result;
        } catch (JedisConnectionException e) {
            this.pool.returnBrokenResource(jedis);
            logger.error(this.getInfo() + ":" + e.getMessage());
            throw new CacheConnectionException(e.getMessage());
        }
    }
}
