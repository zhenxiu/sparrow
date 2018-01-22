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

package com.sparrow.core;

import com.sparrow.enums.STATUS_RECORD;
import com.sparrow.utility.StringUtility;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

/**
 * @author harry
 */
public class TypeConverter {
    public TypeConverter() {
    }

    protected String name;
    protected String value;
    protected Class type;

    /**
     * 实体对象
     *
     * @param name
     * @param value
     * @param type
     */
    public TypeConverter(String name, String value, Class type) {
        this.name = name;
        this.value = value;
        this.type = type;
    }

    public TypeConverter(String name, Class type) {
        this(name, null, type);
    }

    public TypeConverter(Class type) {
        this(null, null, type);
    }

    public String getName() {
        return name;
    }

    public Class getType() {
        return type;
    }

    public Object convert(String value) {
        this.value = value;
        return this.convert();
    }

    public Object convert() {
        if (StringUtility.isNullOrEmpty(value)) {
            return null;
        }
        try {
            if (this.getType() == String.class) {
                return value;
            }

            if (this.getType() == int.class || this.getType() == Integer.class) {
                return Integer.valueOf(value);
            }
            if (this.getType() == long.class || this.getType() == Long.class) {
                return Long.valueOf(value);
            }
            if (this.getType() == Date.class) {
                return Date.valueOf(value);
            }
            if (this.getType() == Timestamp.class) {
                if (value.length() <= 10) {
                    return Timestamp.valueOf(value + " 00:00:00");
                }
                return Timestamp.valueOf(value);
            }
            if (this.getType() == boolean.class || this.getType() == Boolean.class) {
                boolean b = false;
                if (!StringUtility.isNullOrEmpty(value)) {
                    if (String.valueOf(STATUS_RECORD.ENABLE
                            .ordinal()).equals(value) || Boolean.TRUE.toString().equalsIgnoreCase(value)) {
                        b = true;
                    }
                }
                return b;
            }
            if (this.getType() == double.class || this.getType() == Double.class) {
                return Double.valueOf(value);
            }
            if (this.getType() == BigDecimal.class) {
                //留给业务处理
                return new BigDecimal(value);
            }
        } catch (RuntimeException e) {
            return null;
        }
        return null;
    }
}
