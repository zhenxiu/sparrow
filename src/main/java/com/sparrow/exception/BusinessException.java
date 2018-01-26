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

package com.sparrow.exception;

import com.sparrow.enums.ERROR_FIELD_SUFFIX;
import com.sparrow.support.ErrorSupport;
import com.sparrow.utility.StringUtility;

import java.util.Collections;
import java.util.List;

/**
 * 业务异常 程序中断
 *
 * @author harry
 */
public class BusinessException extends Exception {
    /**
     * 错误码
     */
    private Integer code;
    /**
     * 国际化标签 key.suffix ［suffix 对应前端界面的name］
     */
    private String key;
    /**
     * 其他参数
     */
    private List<Object> parameters;

    public BusinessException(ErrorSupport errorSupport, ERROR_FIELD_SUFFIX suffix, List<Object> parameters) {
        super(errorSupport.getMessage());
        this.key = errorSupport.name();
        if (!StringUtility.isNullOrEmpty(suffix)) {
            this.key = this.key + "." + suffix.name().toLowerCase();
        }
        this.code = errorSupport.getCode();
        if (parameters != null && parameters.size() > 0 && !StringUtility.isNullOrEmpty(parameters.get(0))) {
            this.parameters = parameters;
        }
    }

    public BusinessException(ErrorSupport errorSupport, ERROR_FIELD_SUFFIX suffix, Object parameter) {
        this(errorSupport, suffix, Collections.singletonList(parameter));
    }

    public BusinessException(ErrorSupport errorSupport, List<Object> parameters) {
        this(errorSupport, null, parameters);
    }

    public BusinessException(ErrorSupport errorSupport, ERROR_FIELD_SUFFIX suffix) {
        this(errorSupport, suffix, "");
    }

    public BusinessException(ErrorSupport errorSupport) {
        this(errorSupport, null, "");
    }

    public Integer getCode() {
        return code;
    }

    public String getKey() {
        return key;
    }

    public List<Object> getParameters() {
        return parameters;
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return super.fillInStackTrace();
    }

    @Override
    public String toString() {
        return String.format("key-%1$s,code-%2$s-parameters-%3$s", key, code, StringUtility.join(parameters));
    }
}
