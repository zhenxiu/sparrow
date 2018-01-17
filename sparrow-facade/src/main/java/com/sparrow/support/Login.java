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

package com.sparrow.support;

import com.sparrow.constant.CONFIG;
import com.sparrow.constant.CONFIG_KEY_LANGUAGE;
import com.sparrow.constant.USER;
import com.sparrow.cryptogram.Hmac;
import com.sparrow.cryptogram.ThreeDES;
import com.sparrow.utility.Config;
import com.sparrow.utility.StringUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

/**
 * @author harry
 */
public class Login implements Serializable {
    private static final long serialVersionUID = -2215039934860669170L;
    static Logger logger = LoggerFactory.getLogger(Login.class);

    private Long userId;
    private String userName;
    private String nickName;
    private String avatar;
    private Long cent;
    private String ip;
    private String deviceId;
    private Long expireAt;
    private String activate;
    private Integer expireDays;

    public Login() {
    }

    public Login(String permission, String deviceId) {
        // 第一次请求时没有session id
        this.userId = USER.VISITOR_ID;
        this.userName = Config.getLanguageValue(
            CONFIG_KEY_LANGUAGE.USER_VISITOR,
            Config.getValue(CONFIG.LANGUAGE));
        this.avatar = Config.getValue(CONFIG.DEFAULT_USER_HEAD);

        if (StringUtility.isNullOrEmpty(permission)) {
            return;
        }
        try {
            String searchPermission = "&permission=";
            int permissionIndex = permission.lastIndexOf(searchPermission);
            if (permissionIndex < 0) {
                return;
            }
            //id=%1$s&name=%2$s&login=%3$s&expireAt=%4$s&cent=%5$s&avatar=%6$s&deviceId=%7$s&activate=%8$s
            String userInfo = permission.substring(0, permissionIndex);
            String[] userInfoArray = userInfo.split("&");

            Long expireAt = Long.valueOf(userInfoArray[3].substring("expireAt=".length()));
            String dev = userInfoArray[6].substring("deviceId=".length());
            //设备不一致
            if (!dev.equals(deviceId)) {
                return;
            }

            //过期
            if (System
                .currentTimeMillis() > expireAt) {
                return;
            }

            String signature = ThreeDES.getInstance().decrypt(
                Config.getValue(USER.PASSWORD_3DAS_SECRET_KEY),
                permission.substring(permissionIndex
                    + searchPermission.length()));
            String newSignature = Hmac.getInstance().getSHA1Base64(userInfo,
                Config.getValue(USER.PASSWORD_SHA1_SECRET_KEY));

            //签名不一致
            if (signature == null || !signature.equals(newSignature)) {
                return;
            }
            this.userId = Long.valueOf(userInfoArray[0].substring("id=".length()));
            this.nickName = userInfoArray[1].substring("name="
                .length());
            this.userName = userInfoArray[2].substring("login=".length());
            this.cent = Long.valueOf(userInfoArray[4].substring("cent=".length()));
            this.avatar = userInfoArray[5].substring("avatar="
                .length());
            this.deviceId = dev;
            this.expireAt = expireAt;
            this.activate = userInfoArray[7].substring("activate="
                .length());
        } catch (Exception ignore) {
        }
    }

    public static Login create(Long userId, String userName, String nickName, String avatar,
        Long cent, String deviceId, String activate, Integer expireDays) {
        Login login = new Login();
        if (userName.equals(USER.ADMIN)) {
            userId = USER.ADMIN_ID;
        }
        login.userId = userId;
        login.userName = userName;
        login.nickName = nickName;
        login.avatar = avatar;
        login.cent = cent;
        login.deviceId = deviceId;
        login.activate = activate;
        login.expireDays = expireDays;
        if (expireDays > 0) {
            login.expireAt = System.currentTimeMillis() + 1000 * 60 * 60 * 24L * expireDays;
        } else {
            login.expireAt = 0L;
        }
        return login;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getCent() {
        return cent;
    }

    public String getActivate() {
        return activate;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public String getNickName() {
        return nickName;
    }

    public String getUserName() {
        return userName;
    }

    public String getAvatar() {
        return avatar;
    }

    public Integer getExpireDays() {
        if (expireDays == null) {
            expireDays = 1;
        }
        return expireDays;
    }

    public String getPermission() {
        if (this.userName.equals(USER.ADMIN)) {
            userId = USER.ADMIN_ID;
        }
        String userInfo = String.format(
            "id=%1$s&name=%2$s&login=%3$s&expireAt=%4$s&cent=%5$s&avatar=%6$s&deviceId=%7$s&activate=%8$s",
            userId, this.getUserName(), this.getNickName(), this.expireAt,
            cent, this.avatar, deviceId, activate);
        String signature = Hmac.getInstance().getSHA1Base64(userInfo,
            Config.getValue(USER.PASSWORD_SHA1_SECRET_KEY));
        return userInfo
            + "&permission="
            + ThreeDES.getInstance().encrypt(
            Config.getValue(USER.PASSWORD_3DAS_SECRET_KEY),
            signature);
    }

    public static boolean hasEditPrivilege(Long writerId, Long currentUserId) {
        try {
            // 当前是游客则无操作权限
            if (currentUserId.equals(USER.VISITOR_ID)) {
                return false;
            }
            // 当前是admin
            if (currentUserId.equals(USER.ADMIN_ID)) {
                return true;
            }
            // 帖子作者是当前用户id
            if (writerId.equals(currentUserId)) {
                return true;
            }
            return false;
        } catch (Exception e) {
            logger.error("login valid permission error", e);
            return false;
        }
    }
}
