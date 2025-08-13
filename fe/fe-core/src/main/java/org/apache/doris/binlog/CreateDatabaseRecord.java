// Licensed to the Apache Software Foundation (ASF) under one
// or more contributor license agreements.  See the NOTICE file
// distributed with this work for additional information
// regarding copyright ownership.  The ASF licenses this file
// to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// with the License.  You may obtain a copy of the License at
//
//   http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.

package org.apache.doris.binlog;

import org.apache.doris.catalog.Database;
import org.apache.doris.catalog.Env;
import org.apache.doris.common.io.Writable;
import org.apache.doris.persist.gson.GsonUtils;

import com.google.common.collect.Maps;
import com.google.gson.annotations.SerializedName;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Map;

/**
 * 记录创建数据库的binlog记录
 */
public class CreateDatabaseRecord {
    private static final Logger LOG = LogManager.getLogger(CreateDatabaseRecord.class);

    @SerializedName(value = "commitSeq")
    private long commitSeq;
    @SerializedName(value = "dbId")
    private long dbId;
    @SerializedName(value = "dbName")
    private String dbName;
    @SerializedName(value = "sql")
    private String sql;
    @SerializedName(value = "properties")
    private Map<String, String> properties;

    public CreateDatabaseRecord(long commitSeq, Database db) {
        this.commitSeq = commitSeq;
        this.dbId = db.getId();
        this.dbName = db.getFullName();
        
        // 构建创建数据库的SQL语句
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("CREATE DATABASE IF NOT EXISTS `");
        sqlBuilder.append(dbName);
        sqlBuilder.append("`");
        
        // 获取数据库的所有属性
        this.properties = db.getDbProperties().getProperties();
        
        // 添加数据库属性到SQL语句
        if (properties != null && !properties.isEmpty()) {
            sqlBuilder.append(" PROPERTIES (");
            boolean first = true;
            for (Map.Entry<String, String> entry : properties.entrySet()) {
                if (!first) {
                    sqlBuilder.append(", ");
                }
                sqlBuilder.append("\"").append(entry.getKey()).append("\"=\"").append(entry.getValue()).append("\"");
                first = false;
            }
            sqlBuilder.append(")");
        }
        
        this.sql = sqlBuilder.toString();
    }

    public long getCommitSeq() {
        return commitSeq;
    }

    public long getDbId() {
        return dbId;
    }

    public String getDbName() {
        return dbName;
    }

    public String getSql() {
        return sql;
    }

    public String toJson() {
        return GsonUtils.GSON.toJson(this);
    }

    @Override
    public String toString() {
        return toJson();
    }
}
