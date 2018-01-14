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

package org.slf4j.impl;

import com.sparrow.concurrent.SparrowThreadFactory;
import com.sparrow.constant.CACHE_KEY;
import com.sparrow.constant.CONFIG;
import com.sparrow.constant.CONSTANT;
import com.sparrow.core.Cache;
import com.sparrow.enums.LOG_LEVEL;
import com.sparrow.utility.Config;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.slf4j.ILoggerFactory;
import org.slf4j.spi.LoggerFactoryBinder;

/**
 * @author harry
 */
public class StaticLoggerBinder implements LoggerFactoryBinder {

    /**
     * The unique instance of this class.
     */
    private static final StaticLoggerBinder SINGLETON = new StaticLoggerBinder();

    /**
     * Return the singleton of this class.
     *
     * @return the StaticLoggerBinder singleton
     */
    public static final StaticLoggerBinder getSingleton() {
        return SINGLETON;
    }

    private static final String LOGGER_FACTORY_CLASS = SparrowLoggerFactory.class
        .getName();

    private final ILoggerFactory loggerFactory;

    private StaticLoggerBinder() {
        Integer level = LOG_LEVEL.INFO.ordinal();
        Cache.getInstance().put(CACHE_KEY.LOG, CONFIG.LOG_LEVEL, level);
        Cache.getInstance().put(CACHE_KEY.LOG, CONFIG.LOG_PRINT_CONSOLE, true);

        ScheduledExecutorService scheduledExecutorService = new ScheduledThreadPoolExecutor(1,
            new SparrowThreadFactory.Builder().namingPattern("log-config-%d").daemon(true).build());

        scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override public void run() {
                Map<String, String> config = Config.loadFromClassesPath("/log.properties");
                if (config != null) {
                    if (config.get(CONFIG.LOG_LEVEL) != null) {
                        Cache.getInstance().put(CACHE_KEY.LOG, CONFIG.LOG_LEVEL, LOG_LEVEL.valueOf(config.get(CONFIG.LOG_LEVEL).toUpperCase()).ordinal());
                    }
                    if (config.get(CONFIG.LOG_PRINT_CONSOLE) != null) {
                        Cache.getInstance().put(CACHE_KEY.LOG, CONFIG.LOG_PRINT_CONSOLE, Boolean.valueOf(config.get(CONFIG.LOG_PRINT_CONSOLE)));
                    }
                }
            }
        }, 0, 1, TimeUnit.SECONDS);
        loggerFactory = new SparrowLoggerFactory();
    }

    @Override
    public ILoggerFactory getLoggerFactory() {
        return loggerFactory;
    }

    @Override
    public String getLoggerFactoryClassStr() {
        return LOGGER_FACTORY_CLASS;
    }
}
