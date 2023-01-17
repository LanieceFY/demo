package com.metadata.service;

import com.alibaba.druid.sql.dialect.db2.ast.stmt.DB2SelectQueryBlock;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlSelectQueryBlock;

public class SQLStatementFactory {

    static {
        MySqlSelectQueryBlock selectQueryBlock = new MySqlSelectQueryBlock();
        DB2SelectQueryBlock db2SelectQueryBlock = new DB2SelectQueryBlock();
    }
}
