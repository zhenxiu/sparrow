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

package com.sparrow.support.web;

import com.sparrow.constant.CONFIG;
import com.sparrow.constant.USER;
import com.sparrow.exception.CacheConnectionException;
import com.sparrow.support.Login;
import com.sparrow.support.redis.RedisReader;
import com.sparrow.support.redis.RedisWriter;
import com.sparrow.utility.Config;
import com.sparrow.utility.JSUtility;
import com.sparrow.utility.RedisPool;
import com.sparrow.utility.StringUtility;
import redis.clients.jedis.ShardedJedis;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author harry
 */
public class CookieUtility {

    public static void set(HttpServletResponse response, String key,
        String value, int days) {
        set(response, key, value, days, null);
    }

    public static void setRoot(HttpServletResponse response, String key,
        String value, int days) {
        String domain = Config.getValue(CONFIG.ROOT_DOMAIN);
        set(response, key, value, days, domain);
    }

    public static void set(HttpServletResponse response, String key,
        String value, int days, String domain) {
        if (StringUtility.isNullOrEmpty(domain)) {
            domain = Config.getValue(CONFIG.DOMAIN);
        }
        Cookie cookie = new Cookie(key, JSUtility.encodeURIComponent(value));
        cookie.setDomain(domain);
        cookie.setPath("/");
        if (days > 0) {
            cookie.setMaxAge(days * 24 * 60 * 60);
        }
        response.addCookie(cookie);
    }

    public static String get(Cookie[] cookies, String key) {
        if (cookies == null || cookies.length == 0) {
            return null;
        } else {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(key)) {
                    return JSUtility.decodeURIComponent(cookie.getValue());
                }
            }
        }
        return null;
    }

    public static Login getUser(HttpServletRequest request) {
        final String sessionId = request.getSession().getId();
        String permission;

        //如果支持redis
        if (RedisPool.getInstance().isOpen()) {
            try {
                permission = RedisPool.getInstance().read(new RedisReader<String>() {
                    @Override
                    public String read(ShardedJedis jedis) throws CacheConnectionException {
                        return jedis.hget(USER.PERMISSION, sessionId);
                    }
                });
            } catch (CacheConnectionException e) {
                permission = (String) request.getSession().getAttribute(USER.PERMISSION);
            }
        } else {
            permission = (String) request.getSession().getAttribute(USER.PERMISSION);
        }

        if (StringUtility.isNullOrEmpty(permission)) {

            String permissionKey = Config.getValue(CONFIG.PERMISSION);
            if (StringUtility.isNullOrEmpty(permissionKey)) {
                permissionKey = USER.PERMISSION;
            }

            permission = CookieUtility.get(request.getCookies(), permissionKey);
            if (!StringUtility.isNullOrEmpty(permission)) {
                if (RedisPool.getInstance().isOpen()) {
                    final String finalPermission = permission;
                    RedisPool.getInstance().write(new RedisWriter() {
                        @Override
                        public void write(ShardedJedis jedis) {
                            jedis.hset(USER.PERMISSION, sessionId, finalPermission);
                        }
                    });
                } else {
                    request.getSession().setAttribute(USER.PERMISSION, permission);
                }
            }
        }
        return new Login(permission, ServletUtility.getInstance().getClientIp(request));
    }
}
