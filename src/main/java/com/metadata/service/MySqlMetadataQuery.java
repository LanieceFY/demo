package com.metadata.service;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.*;
import com.alibaba.druid.sql.ast.expr.*;
import com.alibaba.druid.sql.ast.statement.*;
import com.metadata.entity.MetaEntity;
import com.metadata.entity.MetaFieldEntity;
import com.metadata.entity.MetaVO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class MySqlMetadataQuery implements MetadataQuery {

    @Resource
    private JdbcTemplate jdbcTemplate;

    public void query(MetaEntity entity, String... args){
        SQLSelectStatement statement = new SQLSelectStatement();
        SQLSelect select = new SQLSelect();
        SQLSelectQueryBlock queryBlock = new SQLSelectQueryBlock();
        List<SQLStatement> statementList = SQLUtils.parseStatements(
                "select id,name,key from t_client a left join t_app b on a.id = b.id left join t_user d on a.id = d.id and a.name = d.name where a.id= '123' and b.name is null and k =1.3 limit 1,10 order by id desc", DbType.mysql);
        queryBlock.setFrom(new SQLIdentifierExpr(entity.getTableName()));
        for(MetaFieldEntity fieldEntity : entity.getFields()){
            SQLBinaryOpExpr epr = new SQLBinaryOpExpr();
            epr.setOperator(SQLBinaryOperator.BooleanAnd);
            SQLBinaryOpExpr right = new SQLBinaryOpExpr();
            right.setOperator(SQLBinaryOperator.Equality);
//            right.set
            epr.setRight(epr);
            queryBlock.setWhere(epr);
//            queryBlock.set
        }
        List<SQLSelectItem> selectList = new ArrayList<>();
        select.setQuery(queryBlock);
        statement.setSelect(select);
    }

    public List<Map<String, Object>> query(MetaVO metaVO){
        SQLParser sqlParser = SQLFactory.getSQLUtil(DbType.mysql.toString());
        String sql = sqlParser.parseSQL(metaVO);
        return jdbcTemplate.queryForList(sql);
    }

    @Override
    public Map<String, Object> queryUnique(MetaVO metaVO) {
        SQLParser sqlParser = SQLFactory.getSQLUtil(DbType.mysql.toString());
        String sql = sqlParser.parseSQL(metaVO);
        return  jdbcTemplate.queryForMap(sql);
    }

    @Override
    public List<Map<String, Object>> query(String sql) {
        SQLStatement statementList = SQLUtils.parseSingleStatement(sql, DbType.mysql, false);
        String sqlPreCompile = statementList.toString();
        return jdbcTemplate.queryForList(sqlPreCompile);
    }

    private SQLJoinTableSource parseJoin(String tableName, List<MetaVO.Join> joins, int offset){
        SQLJoinTableSource joinTableSource = new SQLJoinTableSource();
        joinTableSource.setRight(new SQLExprTableSource(joins.get(offset).getTableName()));
        joinTableSource.setJoinType(SQLJoinTableSource.JoinType.valueOf(joins.get(offset).getJoinType()));
        joinTableSource.setCondition(new SQLIdentifierExpr(joins.get(offset).getCondition()));
        offset--;
        if(offset == -1){
            joinTableSource.setLeft(new SQLExprTableSource(tableName));
            return joinTableSource;
        }
        joinTableSource.setLeft(parseJoin(tableName, joins, offset));
        return joinTableSource;
    }

    private SQLBinaryOpExpr parseWhere(List<MetaVO.Where> wheres, int offset, Map<String,Object> params){
        SQLBinaryOpExpr opExpr = new SQLBinaryOpExpr();
        MetaVO.Where.Attribute attribute = wheres.get(offset).getAttribute();
        SQLBinaryOpExpr opExprCl = new SQLBinaryOpExpr();
        opExprCl.setRight(params.get(attribute.getRight())==null
                ? new SQLIdentifierExpr(attribute.getRight()) : judgeClassType(params.get(attribute.getRight())));
        opExprCl.setLeft(params.get(attribute.getLeft())==null
                ? new SQLIdentifierExpr(attribute.getLeft()) : judgeClassType(params.get(attribute.getLeft())));
        opExprCl.setOperator(SQLBinaryOperator.valueOf(attribute.getOperator()));
        opExpr.setRight(opExprCl);
        opExpr.setOperator(SQLBinaryOperator.valueOf(wheres.get(offset).getOperator()));
        offset--;
        if(offset == -1){
            opExpr.setLeft(new SQLIdentifierExpr("1=1"));
            opExpr.setOperator(SQLBinaryOperator.BooleanAnd);
            return opExpr;
        }
        opExpr.setLeft(parseWhere(wheres, offset, params));
        return opExpr;
    }

    public void save(MetaEntity entity){
        SQLInsertStatement statement = new SQLInsertStatement();
        SQLInsertStatement.ValuesClause value = new SQLInsertStatement.ValuesClause();
        statement.setTableSource(new SQLExprTableSource(new SQLIdentifierExpr(entity.getTableName())));
        for(MetaFieldEntity fieldEntity : entity.getFields()){
            statement.addColumn(new SQLIdentifierExpr(fieldEntity.getColumnName()));
            value.addValue(new SQLVariantRefExpr(fieldEntity.getValue()));
        }
        statement.setValues(value);
        String sql = SQLUtils.toSQLString(statement, DbType.mysql);

    }

    private SQLExpr judgeClassType(Object param){
        if(int.class.isInstance(param)||short.class.isInstance(param)
                ||long.class.isInstance(param)||float.class.isInstance(param)
                ||double.class.isInstance(param)||byte.class.isInstance(param)||param instanceof Number){
            SQLNumberExpr numberExpr = new SQLNumberExpr((Number)param);
            return numberExpr;
        }else if(String.class.isInstance(param)){
            SQLCharExpr charExpr = new SQLCharExpr((String)param);
            return charExpr;
        }else {
            SQLCharExpr charExpr = new SQLCharExpr((String)param);
            return charExpr;
        }
    }

    private SQLLimit parseLimit(MetaVO.Limit limit){
        if(limit==null){
            return null;
        }
        SQLLimit sqlLimit = new SQLLimit();
        sqlLimit.setRowCount(limit.getRowCount());
        sqlLimit.setOffset(limit.getOffset());
        return sqlLimit;
    }

    private SQLOrderBy parseOrderBy(List<MetaVO.OrderBy> orderBys){
        if(orderBys==null){
            return null;
        }
        SQLOrderBy sqlOrderBy = new SQLOrderBy();
        for(MetaVO.OrderBy orderBy:orderBys){
            SQLSelectOrderByItem item = new SQLSelectOrderByItem();
            SQLIdentifierExpr expr = new SQLIdentifierExpr(orderBy.getColumn());
            item.setExpr(expr);
            item.setType(SQLOrderingSpecification.valueOf(orderBy.getType()));
            sqlOrderBy.addItem(item);
        }
        return sqlOrderBy;
    }

}
