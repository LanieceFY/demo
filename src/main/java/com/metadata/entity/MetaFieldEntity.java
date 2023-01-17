package com.metadata.entity;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MetaFieldEntity implements RowMapper<MetaFieldEntity> {

    private int tableId;
    private String tableName;
    private String columnName;
    private String objectName;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    private String value;

    public int getTableId() {
        return tableId;
    }

    public void setTableId(int tableId) {
        this.tableId = tableId;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getObjectName() {
        return objectName;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    @Override
    public MetaFieldEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
        MetaFieldEntity entity = new MetaFieldEntity();
        entity.setTableId(rs.getInt("table_id"));
        entity.setObjectName(rs.getString("object_name"));
        entity.setColumnName(rs.getString("column_name"));
        entity.setTableName(rs.getString("table_name"));
        return entity;
    }
}
