package com.metadata.service;

import com.alibaba.druid.DbType;

public class SQLFactory {

    public static SQLParser getSQLUtil(String dbType){
        if(DbType.mysql.equals(dbType)){
            return new MySqlSQLParser();
        }
        return null;
    }
}
