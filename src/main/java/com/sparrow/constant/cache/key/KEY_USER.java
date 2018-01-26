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

package com.sparrow.constant.cache.key;

import com.sparrow.constant.SPARROW_MODULE;
import com.sparrow.constant.cache.KEY;
import com.sparrow.enums.DATE_TIME_UNIT;
import com.sparrow.utility.LockEntity;

import java.util.HashMap;
import java.util.Map;

public class KEY_USER {
    /**
     * user profile
     */
    public static final KEY.Business USER_SIMPLE_INFO = new KEY.Business(SPARROW_MODULE.USER, "ENTITY", "USER");

    public static final String USER_PROPERTY_NAME = "userName";
    public static final String USER_PROPERTY_LOGIN_NAME = "userLoginName";
    public static final String USER_PROPERTY_CENT = "cent";
    public static final String USER_PROPERTY_HEAD_IMG = "headImg";
    public static final String USER_PROPERTY_REGISTER_TIME = "registerTime";
    public static final String USER_PROPERTY_LAST_LOGIN_TIME = "lastLoginTime";
    public static final String USER_PROPERTY_SEX = "sex";
    public static final String USER_PROPERTY_SIGNATURE = "signature";
    public static final String USER_PROPERTY_ACTIVATE = "activate";
    public static final String USER_PROPERTY_ACTIVATE_TIME = "activateTime";
    public static final String USER_PROPERTY_EMAIL = "email";

    public static final Map<String, LockEntity> LOCK_CONFIG = new HashMap<String, LockEntity>() {
        /**
         *
         */
        private static final long serialVersionUID = 3143693232489958715L;

        {
            // 5分钟不能超10次 不顺延
            put(LOCK_PUBLISH.getKey(), new LockEntity(5 * 60, 10,
                LOCK_PUBLISH.getKey(), false, false));
            // 30分钟之内不超过5次//todo 上线前要修改
            put(LOCK_LOGIN, new LockEntity(30 * 60, 100000, LOCK_LOGIN,
                true, false));
            // 24小时内登录一次加一次积分
            put(LOCK_LOGIN_CENT, new LockEntity(24 * 60 * 60, 1,
                LOCK_LOGIN_CENT, false, false));
            // 30分钟之内不超过5次
            put(LOCK_FIND_PASSWORD, new LockEntity(30 * 60, 5,
                LOCK_FIND_PASSWORD, true, false));
            // 12小时内不超过20次
            put(LOCK_REGISTER, new LockEntity(12 * 60 * 60, 20,
                LOCK_FIND_PASSWORD, false, false));
            // 1天内只允许1 次
            put(LOCK_DIG, new LockEntity(DATE_TIME_UNIT.DAY, 1,
                LOCK_DIG));
            // 1天只允许1次
            put(LOCK_LIKE, new LockEntity(DATE_TIME_UNIT.DAY, 1,
                LOCK_LIKE));
        }
    };

    /**
     * attention list
     */
    public static final KEY.Business LIST_ATTENTION = new KEY.Business(SPARROW_MODULE.USER, "LIST", "ATTENTION");
    /**
     * fans list
     */
    public static final KEY.Business LIST_FANS = new KEY.Business(SPARROW_MODULE.USER, "LIST", "FANS");
    /**
     * user thread count or comment count limit lock
     */
    public static final KEY.Business LOCK_PUBLISH = new KEY.Business(SPARROW_MODULE.USER, "LOCK", "PUBLISH");
    /**
     * user find password lock
     */
    public static final String LOCK_FIND_PASSWORD = "LOCK.FIND.PASSWORD:%1$s";
    /**
     * user login times lock
     */
    public static final String LOCK_LOGIN = "LOCK.LOGIN:%1$s";
    /**
     * user cent lock
     */
    public static final String LOCK_LOGIN_CENT = "LOCK.LOGIN.CENT:%1$s";
    /**
     * user register lock
     */
    public static final String LOCK_REGISTER = "LOCK.REGISTER:%1$s";
    /**
     * user dig lock
     */
    public static final String LOCK_DIG = "LOCK.DIG:%1$s";
    /**
     * user like lock
     */
    public static final String LOCK_LIKE = "LOCK.LIKE:%1$s";

    /**
     * user cent sort
     */
    public static final String SORT_CENT = "SORT:USER.CENT";
    /**
     * lastest login and high cent
     */
    public static final String SORT_POPULARITY = "SORT:POPULARITY";
}
