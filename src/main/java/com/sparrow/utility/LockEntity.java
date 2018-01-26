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

package com.sparrow.utility;

import com.sparrow.enums.DATE_TIME_UNIT;

/**
 * @author harry
 */
public class LockEntity {
    /**
     * 相对时间构照
     *
     * 字节锁
     *
     * @param lockTime
     * @param maxTimes
     * @param key
     * @param isContinueLockTime
     * @param containOperationId
     */
    public LockEntity(int lockTime, int maxTimes, String key,
        boolean isContinueLockTime, boolean containOperationId) {
        this.absolute = false;
        this.setLockTime(lockTime);
        this.setMaxTimes(maxTimes);
        this.setContinueLockTime(isContinueLockTime);
        this.setContainOperationId(containOperationId);
    }

    /**
     * 绝对时间构照 每次必须new生成，因为不允许为单例出现，线程不安全
     *
     * hash锁
     *
     * lockTime=1
     *
     * @param maxTimes
     * @param key
     */
    public LockEntity(DATE_TIME_UNIT dateTimeUnit, int maxTimes, String key) {
        this.absolute = true;
        this.lockTime = 1;
        this.dateTimeUnit = dateTimeUnit;
        this.setMaxTimes(maxTimes);
        this.containOperationId = true;
    }

    private boolean absolute;
    /**
     * 锁定时间
     */
    private int lockTime;

    /**
     * 锁过期时间
     */
    private DATE_TIME_UNIT dateTimeUnit;
    /**
     * 锁定时间内最多操作次数
     */
    private int maxTimes;
    /**
     * 是否会顺延锁定时间
     */
    private boolean isContinueLockTime;
    /**
     * key中是否包含operationId
     */
    private boolean containOperationId;

    public boolean isContainOperationId() {
        return containOperationId;
    }

    public void setContainOperationId(boolean containOperationId) {
        this.containOperationId = containOperationId;
    }

    public long getAbsoluteLockTime() {
        return DateTimeUtility.getLimitTime(this.dateTimeUnit, this.lockTime);
    }

    public int getLockTime() {
        return lockTime;
    }

    public void setLockTime(int lockTime) {
        this.lockTime = lockTime;
    }

    public int getMaxTimes() {
        return maxTimes;
    }

    public void setMaxTimes(int maxTimes) {
        this.maxTimes = maxTimes;
    }

    public boolean isContinueLockTime() {
        return isContinueLockTime;
    }

    public void setContinueLockTime(boolean isContinueLockTime) {
        this.isContinueLockTime = isContinueLockTime;
    }

    public DATE_TIME_UNIT getDateTimeUnit() {
        return dateTimeUnit;
    }

    public void setDateTimeUnit(DATE_TIME_UNIT dateTimeUnit) {
        this.dateTimeUnit = dateTimeUnit;
    }

    public boolean isAbsolute() {
        return absolute;
    }
}
