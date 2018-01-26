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

package com.sparrow.orm;

import com.sparrow.constant.CACHE_KEY;
import com.sparrow.constant.CONFIG_KEY_DB;
import com.sparrow.constant.CONSTANT;
import com.sparrow.constant.magic.SYMBOL;
import com.sparrow.core.Cache;
import com.sparrow.enums.DATABASE_SPLIT_STRATEGY;
import com.sparrow.enums.ORM_ENTITY_META_DATA;
import com.sparrow.utility.Config;
import com.sparrow.utility.StringUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Column;

import javax.persistence.*;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @author harry
 */
public class EntityManager {
    private static Logger logger = LoggerFactory.getLogger(EntityManager.class);
    private String schema;
    private Field primary;
    private Field status;
    /**
     * 属性名和field
     */
    private Map<String, Field> fieldMap;
    /**
     * 列名与属性名映射
     */
    private Map<String, String> columnAttributeMap;
    private Map<String, Field> uniqueFieldMap;
    private List<Field> hashFieldList;
    private String tableName;
    private Dialect dialect;
    private int tableBucketCount;
    private int databaseSplitMaxId;
    private DATABASE_SPLIT_STRATEGY databaseSplitStrategy;
    private String insert;
    private String update;
    private String delete;
    private String fields;

    public EntityManager(Class clazz) {
        Method[] methods = clazz.getDeclaredMethods();
        int fieldCount = methods.length;
        // 当前实体类名
        String clazzName = clazz.getSimpleName();
        logger.info("************INIT META DATA ========:" + clazzName);
        List<Field> fields = new ArrayList<Field>(fieldCount);
        uniqueFieldMap = new HashMap<String, Field>();
        columnAttributeMap = new HashMap<String, String>(fieldCount);
        hashFieldList = new ArrayList<Field>();

        StringBuilder insertSQL = new StringBuilder("insert into ");
        StringBuilder insertParameter = new StringBuilder();
        StringBuilder updateSQL = new StringBuilder("update ");

        boolean isSplitTable = false;
        // 初始化表名
        if (clazz.isAnnotationPresent(Table.class)) {
            Table table = (Table) clazz.getAnnotation(Table.class);
            Split split = (Split) clazz.getAnnotation(Split.class);
            this.tableName = table.name();
            this.schema = table.schema();
            this.dialect = Dialect.getInstance(schema);
            if (split != null) {
                isSplitTable = true;
                // 分表的桶数
                int bucketCount = 0;
                if (split.table_bucket_count() > 1) {
                    bucketCount = split.table_bucket_count();
                    String bucketCountConfigKey = clazzName + "." + ORM_ENTITY_META_DATA.TABLE_BUCKET_COUNT.toString().toLowerCase();
                    Object configBucketCount = Config.getValue(bucketCountConfigKey);
                    if (configBucketCount != null) {
                        bucketCount = Integer.valueOf(configBucketCount.toString());
                    }
                }
                this.tableBucketCount = bucketCount;
                this.databaseSplitMaxId = split.database_max_id();
                this.databaseSplitStrategy = split.strategy();
            }
        }

        updateSQL.append(tableName);
        insertSQL.append(tableName);
        if (isSplitTable) {
            insertSQL.append(CONSTANT.TABLE_SUFFIX);
            updateSQL.append(CONSTANT.TABLE_SUFFIX);
        }
        insertSQL.append("(");
        updateSQL.append(" set ");
        for (Method method : methods) {
            if (method.getName().startsWith("set")) {
                continue;
            }
            if (!method.isAnnotationPresent(Column.class) && !method.isAnnotationPresent(Hash.class)) {
                continue;
            }

            Column column = method.getAnnotation(Column.class);
            Hash hash = method.getAnnotation(Hash.class);
            Status status = method.getAnnotation(Status.class);
            GeneratedValue generatedValue = method.getAnnotation(GeneratedValue.class);
            Id id = method.getAnnotation(Id.class);
            String propertyName = StringUtility.setFirstByteLowerCase(StringUtility.getFieldByGetMethod(method.getName()));
            Field field = new Field(propertyName, method.getReturnType(), column, hash, generatedValue, id);
            fields.add(field);
            if (field.isUnique()) {
                uniqueFieldMap.put(field.getName().toLowerCase(), field);
            }
            if (column != null && "status".equalsIgnoreCase(column.name())) {
                this.status = field;
            }

            if (hash != null) {
                this.hashFieldList.add(field);
                if (!field.isPersistence()) {
                    continue;
                }
            }

            if (column == null) {
                continue;
            }

            this.columnAttributeMap.put(column.name().toLowerCase(), propertyName.toLowerCase());
            String fieldName = dialect.getOpenQuote() + column.name()
                + dialect.getCloseQuote();
            // insertSQL
            if (!HashType.ONLY_HASH.equals(field.getHashStrategy()) && !GenerationType.IDENTITY.equals(field.getGenerationType())) {
                insertSQL.append(fieldName);
                insertSQL.append(SYMBOL.COMMA);
                insertParameter.append("?,");
            }

            // updateSQL
            if (field.isPrimary()) {
                this.primary = field;
            } else if (column.updatable()) {
                updateSQL.append(fieldName + SYMBOL.EQUAL);
                updateSQL.append("?,");
            }
        }

        insertSQL.deleteCharAt(insertSQL.length() - 1);
        insertParameter.deleteCharAt(insertParameter.length() - 1);
        insertSQL.append(")values(");
        insertSQL.append(insertParameter);
        insertSQL.append(SYMBOL.RIGHT_PARENTHESIS);

        updateSQL.deleteCharAt(updateSQL.length() - 1).append(
            " where " + this.primary.getColumnName() + "=?");

        String deleteSQL = "delete from " + tableName + " where "
            + this.primary.getColumnName() + "=?";

        this.insert = insertSQL.toString();
        // 初始化delete SQL语句
        this.delete = deleteSQL;
        // 初始化update SQL语句
        this.update = updateSQL.toString();
        // 初始化字段列表
        this.fieldMap = new LinkedHashMap<String, Field>(fieldCount);

        StringBuilder fieldBuilder = new StringBuilder();
        for (Field field : fields) {
            if (fieldBuilder.length() > 0) {
                fieldBuilder.append(",");
            }
            if (field.isPersistence()) {
                fieldBuilder.append(StringUtility.getEntityNameByClass(clazz) + "." + dialect.getOpenQuote() + field.getColumnName() + dialect.getCloseQuote());
            }
            this.fieldMap.put(field.getName().toLowerCase(), field);
        }
        this.fields = fieldBuilder.toString();
        Cache.getInstance().put(CACHE_KEY.ORM,
            StringUtility.getEntityNameByClass(clazz), this);
    }

    public static Logger getLogger() {
        return logger;
    }

    public Field getPrimary() {
        return primary;
    }

    public Field getStatus() {
        return status;
    }

    public String getTableName() {
        return tableName;
    }

    public Dialect getDialect() {
        return dialect;
    }

    public int getTableBucketCount() {
        return tableBucketCount;
    }

    public int getDatabaseSplitMaxId() {
        return databaseSplitMaxId;
    }

    public String getInsert() {
        return insert;
    }

    public String getUpdate() {
        return update;
    }

    public String getDelete() {
        return delete;
    }

    public String getFields() {
        return fields;
    }

    public void setDialect(Dialect dialect) {
        this.dialect = dialect;
    }

    public Map<String, Field> getFieldMap() {
        return fieldMap;
    }

    public Field getUniqueField(String unique) {
        unique = unique.toLowerCase();
        if (unique.equals(CONFIG_KEY_DB.ORM_PRIMARY_KEY_UNIQUE.toLowerCase())) {
            return this.primary;
        } else {
            return this.uniqueFieldMap.get(unique);
        }
    }

    public static EntityManager get(Class clazz) {
        return Cache.getInstance().get(CACHE_KEY.ORM, StringUtility.getEntityNameByClass(clazz));
    }

    public static EntityManager get(String entityName) {
        return Cache.getInstance().get(CACHE_KEY.ORM, entityName.trim());
    }

    public String getAttribute(String columnName) {
        return columnAttributeMap.get(columnName.toLowerCase());
    }

    public Field getField(String attributeName) {
        return fieldMap.get(attributeName.toLowerCase());
    }

    public String getColumnName(String attributeName) {
        return this.getField(attributeName).getColumnName();
    }

    public String getTableSuffix(Map<Integer, Object> suffixParameters) {
        List<Object> tableSuffix = new ArrayList<Object>();
        for (Integer key : suffixParameters.keySet()) {
            tableSuffix.add(suffixParameters.get(key));
        }
        return getTableSuffix(tableSuffix);
    }

    public String getTableSuffix(List<Object> suffixParameters) {
        Map<Integer, String> resultSuffix = new TreeMap<Integer, String>();
        for (Field field : this.getHashFieldList()) {
            if (suffixParameters.size() <= 0 || field.getHashIndex() > suffixParameters.size()) {
                continue;
            }
            Object parameter = null;
            if (suffixParameters.size() > 0) {
                parameter = suffixParameters.get(field.getHashIndex());
            }

            if (parameter == null) {
                continue;
            }
            String hash = null;
            switch (field.getHashStrategy()) {
                case HASH:
                    //only hash 不是数字
                    Long hashKey = Long.valueOf(parameter.toString());
                    if (hashKey == -1) {
                        logger.warn("hashKey is -1");
                        break;
                    }
                    hash = String.valueOf(hashKey % this.getTableBucketCount());
                    break;
                case ONLY_HASH:
                    hash = parameter.toString().toLowerCase();
                    break;
                default:
            }
            resultSuffix.put(field.getHashIndex(), hash);
        }
        if (resultSuffix.size() > 0) {
            return SYMBOL.UNDERLINE + StringUtility.join(resultSuffix, SYMBOL.UNDERLINE);
        }
        return SYMBOL.EMPTY;
    }

    public DATABASE_SPLIT_STRATEGY getDatabaseSplitStrategy() {
        if (databaseSplitStrategy == null) {
            databaseSplitStrategy = DATABASE_SPLIT_STRATEGY.DEFAULT;
        }
        return databaseSplitStrategy;
    }

    public List<Field> getHashFieldList() {
        return hashFieldList;
    }

    public void parseField(Field field, List<Parameter> parameters, Object o, Map<Integer, Object> tableSuffix,
        boolean update) {
        if (field.isPersistence()) {
            if (!update || field.isUpdatable()) {
                parameters.add(new Parameter(field, o));
            }
        }
        if (field.getHashIndex() > -1) {
            tableSuffix.put(field.getHashIndex(), o);
        }
    }

    public String getSchema() {
        return schema;
    }
}
