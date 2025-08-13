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

import org.apache.doris.persist.DropDbInfo;
import org.apache.doris.persist.gson.GsonUtils;

import com.google.gson.annotations.SerializedName;

/**
 * 记录删除数据库的binlog记录
 */
public class DropDatabaseRecord {
    @SerializedName(value = "commitSeq")
    private long commitSeq;
    @SerializedName(value = "dbName")
    private String dbName;
    @SerializedName(value = "forceDrop")
    private boolean forceDrop;
    @SerializedName(value = "rawSql")
    private String rawSql;
    @SerializedName(value = "dbId")
    private long dbId;

    public DropDatabaseRecord() {
    }

    public DropDatabaseRecord(long commitSeq, DropDbInfo dropDbInfo) {
        this.commitSeq = commitSeq;
        this.dbName = dropDbInfo.getDbName();
        this.forceDrop = dropDbInfo.isForceDrop();
        this.dbId = dropDbInfo.getDbId();
        this.rawSql = this.forceDrop 
            ? String.format("DROP DATABASE IF EXISTS `%s` FORCE", this.dbName)
            : String.format("DROP DATABASE IF EXISTS `%s`", this.dbName);
    }

    public long getCommitSeq() {
        return commitSeq;
    }

    public String getDbName() {
        return dbName;
    }

    public boolean isForceDrop() {
        return forceDrop;
    }

    public String getRawSql() {
        return rawSql;
    }
    
    public long getDbId() {
        return dbId;
    }
    
    public void setDbId(long dbId) {
        this.dbId = dbId;
    }

    public String toJson() {
        return GsonUtils.GSON.toJson(this);
    }

    public static DropDatabaseRecord fromJson(String json) {
        return GsonUtils.GSON.fromJson(json, DropDatabaseRecord.class);
    }

    @Override
    public String toString() {
        return toJson();
    }
}