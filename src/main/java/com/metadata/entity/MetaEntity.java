package com.metadata.entity;

import com.metadata.service.MetadataQuery;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.annotation.Resource;
import java.util.List;

@Resource
public class MetaEntity implements ObjectEntity{

    private String tableName;
    private List<MetaFieldEntity> fields;
    @Resource
    private MetadataQuery query;

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public List<MetaFieldEntity> getFields() {
        return fields;
    }

    public void setFields(List<MetaFieldEntity> fields) {
        this.fields = fields;
    }

    @Override
    public void save() {

    }
}
