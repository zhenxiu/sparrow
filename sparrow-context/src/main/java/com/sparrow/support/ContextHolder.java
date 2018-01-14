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

import com.sparrow.constant.CONSTANT;
import com.sparrow.datasource.DataSourceFactory;
import com.sparrow.datasource.DatasourceKey;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 * 保持线程请求中的数据 与web应用程序解耦
 *
 * @author harry
 */
public class ContextHolder {

    /**
     * 请求过程中的参数
     */
    private ThreadLocal<Map<String, Object>> holder = new ThreadLocal<Map<String, Object>>();

    /**
     * 请求过程中的事务链接
     */
    private ThreadLocal<Map<String, Connection>> transactionContainer = new ThreadLocal<Map<String, Connection>>();

    /**
     * 请求过程中的普通数据库链接
     */
    private ThreadLocal<Map<String, Stack<Connection>>> connectionContainer = new ThreadLocal<Map<String, Stack<Connection>>>();

    public Map<String, Object> getHolder() {
        if (this.holder.get() == null) {
            this.holder.set(new HashMap<String, Object>());
        }
        return this.holder.get();
    }

    private Map<String, Connection> getTransactionHolder() {
        if (this.transactionContainer.get() == null) {
            this.transactionContainer.set(new HashMap<String, Connection>());
        }
        return this.transactionContainer.get();
    }

    private Map<String, Stack<Connection>> getConnectionHolder() {
        if (this.connectionContainer.get() == null) {
            this.connectionContainer.set(new HashMap<String, Stack<Connection>>());
        }
        return this.connectionContainer.get();
    }

    public void bindConnection(Connection connection) {
        try {
            DatasourceKey dataSourceKey = DataSourceFactory.getInstance().getDatasourceKey(connection);
            if (!connection.getAutoCommit()) {
                this.getTransactionHolder().put(dataSourceKey.getKey(), connection);
                return;
            }
            Stack<Connection> stack = this.getConnectionHolder().get(dataSourceKey.getKey());
            if (stack == null) {
                stack = new Stack<Connection>();
                this.getConnectionHolder().put(dataSourceKey.getKey(), stack);
            }
            stack.push(connection);
        } catch (SQLException ignore) {
            throw new RuntimeException(ignore);
        }
    }

    public void unbindConnection(Connection connection) {
        try {
            DatasourceKey dataSourceKey = DataSourceFactory.getInstance().getDatasourceKey(connection);
            Connection proxyConnection = this.getConnection(dataSourceKey.getKey());
            if (proxyConnection == null) {
                return;
            }
            if (!connection.getAutoCommit()) {
                this.getTransactionHolder().remove(dataSourceKey.getKey());
            } else {
                this.getConnectionHolder().get(dataSourceKey.getKey()).pop();
            }
            proxyConnection.close();
        } catch (SQLException ignore) {
            throw new RuntimeException(ignore);
        }
    }

    public Connection getConnection(String datasourceKey) {
        //以事务为优先，如果当前开启事务，未commit则用事务链接执行
        Connection connection = this.getTransactionHolder().get(datasourceKey);
        if (connection != null) {
            return connection;
        }
        Stack<Connection> connectionStack = this.getConnectionHolder().get(datasourceKey);
        if (connectionStack != null && connectionStack.size() > 0) {
            connection = connectionStack.peek();
        }
        return connection;
    }

    public void removeAll() {
        Map<String, Connection> transactionContainer = this.transactionContainer.get();
        if (transactionContainer != null) {
            for (String key : transactionContainer.keySet()) {
                try {
                    transactionContainer.remove(key).close();
                } catch (SQLException ignore) {
                }
            }
        }
        Map<String, Stack<Connection>> connectionContainer =
            this.connectionContainer.get();
        if (connectionContainer != null) {
            for (String key : connectionContainer.keySet()) {
                Stack<Connection> connectionStack = connectionContainer.get(key);
                while (!connectionStack.empty()) {
                    try {
                        connectionStack.pop().close();
                    } catch (SQLException ignore) {
                    }
                }
            }
        }
        this.transactionContainer.remove();
        this.connectionContainer.remove();
        this.holder.remove();
    }

    public void remove() {
        this.holder.remove();
    }

    private static ContextHolder contextHolder = new ContextHolder();

    public static ContextHolder getInstance() {
        return contextHolder;
    }

    public Object get(String key) {
        return this.getHolder().get(key);
    }

    public void put(String key, Object value) {
        if (value == null) {
            return;
        }
        this.getHolder().put(key, value);
    }

    public void execute(String script) {
        script = "<script type=\"text/javascript\">" + script + "</script>";
        String oldScript = (String) this.getHolder().get(CONSTANT.ACTION_RESULT_JAVASCRIPT);
        if (oldScript != null) {
            script += oldScript;
        }
        this.getHolder().put(CONSTANT.ACTION_RESULT_JAVASCRIPT, script);
    }
}
