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

package com.sparrow.support.latch;

import com.sparrow.constant.cache.KEY;

/**
 * Created by harry on 2018/1/11.
 */
public interface DistributedCountDownLatch {
    /**
     * KEY消费
     *
     * @param consumerKey monitor ey
     * @param key         consume msg key
     */
    void consume(KEY consumerKey, String key);

    /**
     * 消费幂等
     *
     * @param consumeKey
     * @return
     */
    boolean consumable(KEY consumeKey, String key);

    /**
     * KEY生产
     *
     * @param productKey productKey
     * @param key        product msg key
     */
    void product(KEY productKey, String key);

    /**
     * 是否结束
     *
     * @param productKey  productKey
     * @param consumerKey consumerKey
     * @return
     */
    boolean isFinish(KEY productKey, KEY consumerKey);

    /**
     * monitor
     *
     * @param productKey     product key
     * @param consumerKey    consumer key
     * @param secondInterval 探测时间间隔
     * @return
     */
    boolean monitor(KEY productKey, KEY consumerKey, int secondInterval);

    /**
     * 默认2秒控测一次
     *
     * @param productKey  productKey
     * @param consumerKey consumer key
     * @return
     */
    boolean monitor(KEY productKey, KEY consumerKey);

}
