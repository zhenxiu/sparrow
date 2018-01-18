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

import com.sparrow.cache.impl.redis.RedisPool;
import com.sparrow.constant.CONFIG;
import com.sparrow.constant.cache.KEY;
import com.sparrow.constant.cache.key.KEY_USER;
import com.sparrow.core.spi.CacheFactory;
import com.sparrow.exception.CacheConnectionException;
import com.sparrow.support.Login;
import com.sparrow.utility.Config;
import com.sparrow.utility.JSUtility;
import com.sparrow.utility.StringUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author harry
 */
public class CookieUtility {
    private static Logger logger = LoggerFactory.getLogger(CookieUtility.class);

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
        KEY permissionKey = new KEY.Builder().business(KEY_USER.PERMISSION).businessId(sessionId).build();
        //如果支持redis
        if (RedisPool.getInstance().isOpen()) {
            try {
                permission = CacheFactory.getProvider().get(permissionKey);
            } catch (CacheConnectionException e) {
                permission = (String) request.getSession().getAttribute(permissionKey.getBusiness());
            }
        } else {
            permission = (String) request.getSession().getAttribute(permissionKey.getBusiness());
        }

        if (StringUtility.isNullOrEmpty(permission)) {

            permission = CookieUtility.get(request.getCookies(), permissionKey.getBusiness());
            if (!StringUtility.isNullOrEmpty(permission)) {
                if (RedisPool.getInstance().isOpen()) {
                    try {
                        CacheFactory.getProvider().setExpire(permissionKey, 60 * 60, permission);
                    } catch (CacheConnectionException ignore) {
                        logger.error("cookie connectin break", ignore);
                    }
                } else {
                    request.getSession().setAttribute(permissionKey.getBusiness(), permission);
                }
            }
        }
        return new Login(permission, ServletUtility.getInstance().getClientIp(request));
    }
}
