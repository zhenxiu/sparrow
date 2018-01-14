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

import com.sparrow.enums.ACTION;
import com.sparrow.utility.StringUtility;

/**
 * @author harry
 */
public class UrlRewriteEntity {
    private String directType;
    private String regex;
    private String url;
    private int cache;

    public UrlRewriteEntity(String url, String regex, String directType, int cache) {
        this.url = url;
        this.regex = regex;
        this.cache = cache;
        if (StringUtility.isNullOrEmpty(directType)) {
            this.directType = ACTION.URL_REWRITE.toString();
        } else {
            this.directType = directType;
        }
    }

    public UrlRewriteEntity(String url, String regex) {
        this.url = url;
        this.regex = regex;
        this.directType = ACTION.URL_REWRITE.toString();
    }

    public UrlRewriteEntity(String url) {
        this.url = url;
        this.regex = null;
        this.directType = ACTION.URL_REWRITE.toString();
    }

    public String getDirectType() {
        return directType;
    }

    public void setDirectType(String directType) {
        this.directType = directType;
    }

    public String getRegex() {
        return regex;
    }

    public void setRegex(String regex) {
        this.regex = regex;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getCache() {
        return cache;
    }

    public void setCache(int cache) {
        this.cache = cache;
    }
}
