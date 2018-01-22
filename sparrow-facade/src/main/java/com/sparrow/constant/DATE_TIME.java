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

package com.sparrow.constant;

import com.sparrow.constant.magic.DIGIT;
import com.sparrow.enums.DATE_TIME_UNIT;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author harry
 */
public class DATE_TIME {

    public static final String FORMAT_YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";

    public static final String FORMAT_YYYY_MM_DD_HH_MM = "yyyy-MM-dd HH:mm";

    public static final String FORMAT_YYYY_MM_DD = "yyyy-MM-dd";

    public static final String FORMAT_MM_DD = "MM-dd";

    public static final String FORMAT_YYYYMMDD = "yyyyMMdd";

    public static final String FORMAT_YYYYMMDDHHMMSS = "yyyyMMddHHmmss";

    private static final Map<String, ThreadLocal<DateFormat>> DATE_FORMAT_CONTAINER = new ConcurrentHashMap<String, ThreadLocal<DateFormat>>();

    private static Lock lock = new ReentrantLock();

    public static DateFormat getInstance(final String format) {
        if (DATE_FORMAT_CONTAINER.get(format) != null) {
            return DATE_FORMAT_CONTAINER.get(format).get();
        } else {
            lock.lock();
            try {
                if (DATE_FORMAT_CONTAINER.get(format) != null) {
                    return DATE_FORMAT_CONTAINER.get(format).get();
                } else {
                    ThreadLocal<DateFormat> dateFormatThreadLocal = new ThreadLocal<DateFormat>() {
                        @Override
                        protected DateFormat initialValue() {
                            return new SimpleDateFormat(format);
                        }
                    };
                    DATE_FORMAT_CONTAINER.put(format, dateFormatThreadLocal);
                    return dateFormatThreadLocal.get();
                }
            } finally {
                lock.unlock();
            }
        }
    }

    /**
     * min unix timestamp
     */
    public static final Timestamp MIN_UNIX_TIMESTAMP = Timestamp
            .valueOf("1970-01-01 08:00:00");


    public static final Timestamp MAX_UNIX_TIMESTAMP = Timestamp.valueOf("9999-12-31 23:59:59");

    /**
     * time unit from Millis to year
     */
    @SuppressWarnings("serial")
    public static final Map<String, Integer> BEFORE_FORMAT = new LinkedHashMap<String, Integer>() {
        {
            put("秒前", 60);
            put("分钟前", 60);
            put("小时前", 24);
            put("天前", 30);
            put("月前", 12);
            put("年前", 100);
        }
    };

    public static Map<DATE_TIME_UNIT, Integer> DEFAULT_FIRST_VALUE = new HashMap<DATE_TIME_UNIT, Integer>() {
        {

            DEFAULT_FIRST_VALUE.put(DATE_TIME_UNIT.MONTH, DIGIT.ONE);
            DEFAULT_FIRST_VALUE.put(DATE_TIME_UNIT.DAY, DIGIT.ONE);
            DEFAULT_FIRST_VALUE.put(DATE_TIME_UNIT.HOUR, DIGIT.ZERO);
            DEFAULT_FIRST_VALUE.put(DATE_TIME_UNIT.MINUTE, DIGIT.ZERO);
            DEFAULT_FIRST_VALUE.put(DATE_TIME_UNIT.SECOND, DIGIT.ZERO);
        }
    };

    public static Map<DATE_TIME_UNIT, Integer> DATE_TIME_UNIT_CALENDER_CONVERTER = new HashMap<DATE_TIME_UNIT, Integer>(){
        {
            DATE_TIME_UNIT_CALENDER_CONVERTER.put(DATE_TIME_UNIT.MONTH,Calendar.MONTH);
            DATE_TIME_UNIT_CALENDER_CONVERTER.put(DATE_TIME_UNIT.DAY,Calendar.DATE);
            DATE_TIME_UNIT_CALENDER_CONVERTER.put(DATE_TIME_UNIT.HOUR,Calendar.HOUR_OF_DAY);
            DATE_TIME_UNIT_CALENDER_CONVERTER.put(DATE_TIME_UNIT.MINUTE,Calendar.MINUTE);
            DATE_TIME_UNIT_CALENDER_CONVERTER.put(DATE_TIME_UNIT.SECOND,Calendar.SECOND);
            DATE_TIME_UNIT_CALENDER_CONVERTER.put(DATE_TIME_UNIT.YEAR,Calendar.YEAR);
    }};

    public class MILLIS_UNIT {
        /**
         * second
         */
        public static final int SECOND = 1000;
        /**
         * minute
         */
        public static final int MINUTE = SECOND * 60;
        /**
         * hour
         */
        public static final int HOUR = MINUTE * 60;
        /**
         * day
         */
        public static final int DAY = HOUR * 24;
        /**
         * week
         */
        public static final int WEEK = DAY * 7;
        /**
         * month
         */
        public static final int MONTH = DAY * 30;
    }
}
