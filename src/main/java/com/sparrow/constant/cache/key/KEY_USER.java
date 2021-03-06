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
import com.sparrow.utility.LockConfig;
import java.util.HashMap;
import java.util.Map;

public class KEY_USER {
    /**
     * user profile
     */
    public static final KEY.Business USER_SIMPLE_INFO = new KEY.Business(SPARROW_MODULE.USER, "ENTITY", "USER");
    /**
     * 用户权限码
     */
    public static final KEY.Business PERMISSION = new KEY.Business(SPARROW_MODULE.USER, "PERMISSION");

    public static final Map<KEY.Business, LockConfig> LOCK_CONFIG = new HashMap<KEY.Business, LockConfig>() {
        /**
         *
         */
        private static final long serialVersionUID = 3143693232489958715L;

        {
            // 5分钟不能超10次 不顺延
            put(LOCK_PUBLISH, LockConfig.getRelativeLock(5 * 60, 10, false, false));
            // 30分钟之内不超过5次//todo 上线前要修改
            put(LOCK_LOGIN, LockConfig.getRelativeLock(30 * 60, 100000,
                    true, false));
            // 24小时内登录一次加一次积分
            put(LOCK_LOGIN_CENT, LockConfig.getRelativeLock(24 * 60 * 60, 1, false, false));
            // 30分钟之内不超过5次
            put(LOCK_FIND_PASSWORD, LockConfig.getRelativeLock(30 * 60, 5,true, false));
            // 12小时内不超过20次
            put(LOCK_REGISTER, LockConfig.getRelativeLock(12 * 60 * 60, 20, false, false));
            // 1天内只允许1 次
            put(LOCK_DIG, LockConfig.getAbsoluteLock(DATE_TIME_UNIT.DAY, 1));
            // 1天只允许1次
            put(LOCK_LIKE,  LockConfig.getAbsoluteLock(DATE_TIME_UNIT.DAY, 1));
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
    public static final KEY.Business LOCK_FIND_PASSWORD =new KEY.Business(SPARROW_MODULE.USER,"LOCK","FIND","PASSWORD");
    /**
     * user login times lock
     */
    public static final KEY.Business LOCK_LOGIN = new KEY.Business(SPARROW_MODULE.USER,"LOCK","LOGIN");
    /**
     * user cent lock
     */
    public static final KEY.Business LOCK_LOGIN_CENT = new KEY.Business(SPARROW_MODULE.USER,"LOCK","LOGIN","CENT");
    /**
     * user register lock
     */
    public static final KEY.Business LOCK_REGISTER =new KEY.Business(SPARROW_MODULE.USER,"LOCK","REGISTER");
    /**
     * user dig lock
     */
    public static final KEY.Business LOCK_DIG =new KEY.Business(SPARROW_MODULE.USER,"LOCK","DIG");
    /**
     * user like lock
     */
    public static final KEY.Business LOCK_LIKE =new KEY.Business(SPARROW_MODULE.USER,"LOCK","LIKE");

    /**
     * user cent sort
     */
    public static final KEY.Business SORT_CENT =new KEY.Business(SPARROW_MODULE.USER,"SORT","CENT");
    /**
     * lastest login and high cent
     */
    public static final KEY.Business SORT_POPULARITY = new KEY.Business(SPARROW_MODULE.USER,"SORT","POPULARITY");
}
