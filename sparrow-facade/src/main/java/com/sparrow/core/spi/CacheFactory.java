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

package com.sparrow.core.spi;

import com.sparrow.cache.CacheClient;
import com.sparrow.json.Json;

import javax.json.JsonException;
import java.util.Iterator;
import java.util.ServiceLoader;

/**
 * @author harry
 */
public class CacheFactory {
    private static final String DEFAULT_PROVIDER = "com.sparrow.cache.impl.redis.RedisCacheClient";
    private volatile static CacheClient client;

    public static CacheClient getProvider() {
        if (client != null) {
            return client;
        }
        synchronized (CacheFactory.class) {
            if (client != null) {
                return client;
            }
            ServiceLoader<CacheClient> loader = ServiceLoader.load(CacheClient.class);
            Iterator<CacheClient> it = loader.iterator();

            if (it.hasNext()) {
                client = it.next();
                return client;
            }

            try {
                Class<?> jsonClazz = Class.forName(DEFAULT_PROVIDER);
                client = (CacheClient) jsonClazz.newInstance();
                return client;
            } catch (ClassNotFoundException x) {
                throw new JsonException(
                    "Provider " + DEFAULT_PROVIDER + " not found", x);
            } catch (Exception x) {
                throw new JsonException(
                    "Provider " + DEFAULT_PROVIDER + " could not be instantiated: " + x,
                    x);
            }
        }
    }
}
