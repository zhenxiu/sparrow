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

package com.sparrow.orm.query.sql.impl.operation;

import com.sparrow.constant.magic.SYMBOL;
import com.sparrow.orm.EntityManager;
import com.sparrow.orm.query.Criteria;
import com.sparrow.orm.query.sql.RelationOperationEntity;
import com.sparrow.orm.query.sql.RelationalOperation;

/**
 * @author harry
 */
public class InOperation implements RelationalOperation {
    private String join(Iterable iterable) {
        StringBuilder sb = new StringBuilder();
        for (Object key : iterable) {
            if (sb.length() > 0) {
                sb.append(SYMBOL.COMMA);
            }
            if (key instanceof Number) {
                sb.append(key);
            } else {
                sb.append('\'');
                sb.append(key);
                sb.append('\'');
            }
        }
        return sb.toString();
    }

    @Override public RelationOperationEntity operation(Criteria criteria) {
        Iterable iterable = (Iterable) criteria.getCriteriaEntry().getValue();
        String column = EntityManager.get(criteria.getField().getAlias()).getColumnName(criteria.getField().getName());
        String condition = (criteria.isAlias() ? criteria.getField().getAlias() + SYMBOL.DOT : "") + column + SYMBOL.BLANK + criteria.getCriteriaEntry().getKey().rendered() + "(" + this.join(iterable) + ")";
        return new RelationOperationEntity(condition, null);
    }
}
