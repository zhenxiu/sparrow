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
import com.sparrow.constant.CONSTANT;
import com.sparrow.enums.PLATFORM_TYPE;
import com.sparrow.utility.Config;

/**
 * @author harry
 */
public class PlatformConfig {
    /**
     * 接入类型 pc app
     */
    private PLATFORM_TYPE type;
    /**
     * 应用名称 网站名称
     */
    private String name;
    /**
     * 开放平台
     */
    private String platform;
    /**
     * app key
     */
    private String appkey;
    /**
     * 密钥
     */
    private String appSecret;
    /**
     * 回调的url
     */
    private String callBackUrl;
    /**
     * 状态
     */
    private String state;
    /**
     * 允许调用的方法
     */
    private String scope;

    /**
     * 分享的状态标志位
     */
    private long shareStatus;
    /**
     * 绑定的状态标志位
     */
    private long bindStatus;

    /**
     * 卖家商号
     */
    private String partner;

    /**
     * 卖家帐号
     */
    private String sellerAccount;

    /**
     * 支付密钥
     */
    private String paySecret;
    /**
     * 通知url
     */
    private String notifyUrl;

    private String charset;

    //pc_alipay_sparrow
    public PlatformConfig(String configKey) {
        String[] keyArray = configKey.split("_");
        this.type = PLATFORM_TYPE.valueOf(keyArray[0].toUpperCase());
        this.platform = keyArray[1];
        this.name = keyArray[2];
        this.appkey = Config.getValue(configKey + "_" + CONFIG.PLATFORM_APP_KEY);
        this.appSecret = Config.getValue(configKey + "_" + CONFIG.PLATFORM_APP_SECRET);

        this.state = Config.getValue(configKey + "_" + CONFIG.PLATFORM_STATE);
        this.scope = Config.getValue(configKey + "_" + CONFIG.PLATFORM_SCOPE);
        this.partner = Config.getValue(configKey + "_" + CONFIG.PLATFORM_PARTNER);
        this.sellerAccount = Config.getValue(configKey + "_" + CONFIG.PLATFORM_SELLER_ACCOUNT);
        this.paySecret = Config.getValue(configKey + "_" + CONFIG.PLATFORM_PAY_SECRET);
        this.notifyUrl = Config.getValue(configKey + "_" + CONFIG.PLATFORM_NOTIFY_URL);
        this.callBackUrl = Config.getValue(configKey + "_" + CONFIG.PLATFORM_CALL_BACK_URL);
        this.charset = Config.getValue(configKey + "_" + CONFIG.PLATFORM_CHARSET);
    }

    public PlatformConfig(String name, String platform,
        PLATFORM_TYPE type, String appkey, String appSecret,
        String callBackUrl, String state, String scope, long shareStatus,
        long bindStatus) {
        this.name = name;
        this.platform = platform;
        this.appkey = appkey;
        this.callBackUrl = callBackUrl;
        this.state = state;
        this.scope = scope;
        this.appSecret = appSecret;
        this.type = type;
        this.shareStatus = shareStatus;
        this.bindStatus = bindStatus;
    }

    public String getKey() {
        return (type + "_" + platform + "_" + name).toUpperCase();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAppkey() {
        return appkey;
    }

    public void setAppkey(String appkey) {
        this.appkey = appkey;
    }

    public String getAppSecret() {
        return appSecret;
    }

    public void setAppSecret(String appSecret) {
        this.appSecret = appSecret;
    }

    public long getShareStatus() {
        return shareStatus;
    }

    public void setShareStatus(long shareStatus) {
        this.shareStatus = shareStatus;
    }

    public long getBindStatus() {
        return bindStatus;
    }

    public void setBindStatus(long bindStatus) {
        this.bindStatus = bindStatus;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public PLATFORM_TYPE getType() {
        return type;
    }

    public void setType(PLATFORM_TYPE type) {
        this.type = type;
    }

    public String getCallBackUrl() {
        if (callBackUrl.startsWith(CONSTANT.HTTP_PROTOCOL)) {
            return callBackUrl;
        } else {
            return Config.getValue(CONFIG.ROOT_PATH) + this.callBackUrl;
        }
    }

    public void setCallBackUrl(String callBackUrl) {
        this.callBackUrl = callBackUrl;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getSellerAccount() {
        return sellerAccount;
    }

    public void setSellerAccount(String sellerAccount) {
        this.sellerAccount = sellerAccount;
    }

    public String getPartner() {
        return partner;
    }

    public void setPartner(String partner) {
        this.partner = partner;
    }

    public String getPaySecret() {
        return paySecret;
    }

    public void setPaySecret(String paySecret) {
        this.paySecret = paySecret;
    }

    public String getNotifyUrl() {
        return notifyUrl;
    }

    public void setNotifyUrl(String notifyUrl) {
        this.notifyUrl = notifyUrl;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }
}
