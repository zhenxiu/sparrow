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

package com.sparrow.cache;

import com.sparrow.constant.cache.KEY;
import com.sparrow.exception.CacheConnectionException;

import java.util.Map;

/**
 *
 * @author harry
 * @date 2018/1/26
 */
public interface CacheHash {
    Map<String, String> getAll(KEY key) throws CacheConnectionException;

    <K, T> Map<K, T> getAll(KEY key, Class keyClazz, Class clazz) throws CacheConnectionException;

    <K, T> Map<K, T> getAll(KEY key, Class keyClazz, Class clazz, CacheDataNotFound<Map<K,T>> hook);

    Long getSize(KEY key) throws CacheConnectionException;

    String get(KEY key, String field) throws CacheConnectionException;

    <T> T get(KEY key, String field, Class clazz) throws CacheConnectionException;

    Long put(KEY key, String field, Object value) throws CacheConnectionException;

    <K,T> Integer put(KEY key, Map<K, T> map) throws CacheConnectionException;

    Long incrBy(KEY key, String field, long count) throws CacheConnectionException;
}
