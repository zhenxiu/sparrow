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

package com.sparrow.enums;

/**
 * 后缀以_分隔  类型安全
 *
 * @author harry
 */
public enum ERROR_FIELD_SUFFIX {
    /**
     * 用户注册
     */
    USER_REGISTER,
    /**
     * 发帖
     */
    BLOG_PUBLISH,
    /**
     * 评论
     */
    BLOG_COMMENT,
    /**
     * 登录
     */
    USER_LOGIN,

    /**
     * 登录验证码
     */
    USER_LOGIN_VALIDATE_CODE,
    /**
     * 登录用户名
     */
    USER_LOGIN_USER_NAME,
    /**
     * 登录用户email
     */
    USER_LOGIN_USER_EMAIL,
    /**
     * 登录用户密码
     */
    USER_LOGIN_PASSWORD,

    /**
     * 用户注册验证码
     */
    USER_REGISTER_VALIDATE_CODE,
    /**
     * 用户注册用户名
     */
    USER_REGISTER_USER_NAME,
    /**
     * 用户注册手机号
     */
    USER_REGISTER_MOBILE,
    /**
     * 用户注册email
     */
    USER_REGISTER_USER_EMAIL,
    /**
     * 用户注册密码
     */
    USER_REGISTER_PASSWORD,

    /**
     * 旧密码
     */
    USER_OLD_PASSWORD,
    /**
     * 新密码
     */
    USER_NEW_PASSWORD,

    /**
     * 用户密码重置
     */
    USER_PASSWORD_RESET_MOBILE,
    /**
     * 密码重置验证码
     */
    USER_PASSWORD_RESET_VALIDATE_CODE
}
