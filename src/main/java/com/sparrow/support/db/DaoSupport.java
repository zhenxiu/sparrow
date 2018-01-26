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

package com.sparrow.support.db;

import com.sparrow.enums.STATUS_RECORD;
import com.sparrow.orm.query.AGGREGATE;
import java.util.List;

/**
 * table identify 可以跨db mybatis hibernate jdbc elastic search
 *
 * @param <T>
 * @author harry
 */
public interface DaoSupport<T, I> {

    /**
     * 新建(支持分表)
     *
     * @param model
     */
    Long insert(T model);

    /**
     * 更新
     * <p/>
     * 支持分表
     *
     * @param model
     */
    int update(T model);

    /**
     * 修改记录状态
     *
     * @param ids
     * @param status
     * @return
     */
    int changeStatus(String ids, STATUS_RECORD status);

    /**
     * 删除指定记录
     *
     * @param id
     */
    int delete(I id);

    /**
     * 根据id批量删除
     *
     * @param ids
     */
    int batchDelete(String ids);

    T getEntity(I id);

    T getEntity(Object key, String uniqueKey);

    List<T> getList();

    /**
     * 用来判断重复添加
     * <p/>
     * 对于unique字段
     *
     * @param key
     * @param uniqueKey
     * @return
     */
    Long getCount(Object key, String uniqueKey);

    Long getCount(Object key);

    <X> X getFieldValue(String fieldName, Object key);

    <X> X getFieldValue(String fieldName, Object key, String uniqueKey);

    <X> X getAggregate(String fieldName, AGGREGATE aggregate);
}