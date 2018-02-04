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
     * msg key是否存在
     *
     * @param monitor
     * @param key
     * @return
     */
    boolean exist(KEY monitor, String key);

    /**
     * KEY消费
     *
     * @param monitor monitor ey
     * @param key consume msg key
     */
    void consume(KEY monitor, String key);

    /**
     * KEY生产
     *
     * @param monitor monitor key
     * @param key product msg key
     */
    void product(KEY monitor, String key);

    /**
     * 是否结束
     *
     * @param monitor monitor key
     * @return
     */
    boolean isFinish(KEY monitor);

    /**
     * monitor
     *
     * @param monitor monitor key
     * @param secondInterval 探测时间间隔
     * @return
     */
    boolean monitor(KEY monitor, int secondInterval);

    /**
     * 默认2秒控测一次
     *
     * @param monitor monitor key
     * @return
     */
    boolean monitor(KEY monitor);

}
