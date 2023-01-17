package com.metadata.service;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLLimit;
import com.alibaba.druid.sql.ast.SQLOrderBy;
import com.alibaba.druid.sql.ast.SQLOrderingSpecification;
import com.alibaba.druid.sql.ast.expr.*;
import com.alibaba.druid.sql.ast.statement.*;
import com.metadata.entity.MetaVO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MySqlSQLParser implements SQLParser {

    public String parseSQL(MetaVO metaVO){
        SQLSelectStatement statement = new SQLSelectStatement();
        SQLSelect select = new SQLSelect();
        SQLSelectQueryBlock queryBlock = new SQLSelectQueryBlock();
        select.setQuery(queryBlock);
        Map<String,Object> params = new HashMap<>();
        params.put("appId", 123);
        params.put("clientId", 23.3);
        for(String item:metaVO.getSelectList()){
            queryBlock.addSelectItem(new SQLSelectItem(new SQLIdentifierExpr(item)));
        }
        MetaVO.From from = metaVO.getFrom();
        if(from.getJoin()!=null){
            List<MetaVO.Join> joins = from.getJoin();
            SQLJoinTableSource joinTableSource = parseJoin(from.getTableName(), joins, joins.size() - 1);
            queryBlock.setFrom(joinTableSource);
        } else {
            queryBlock.setFrom(new SQLIdentifierExpr(from.getTableName()));
        }
        List<MetaVO.Where> wheres = metaVO.getWhere();
        if(wheres!=null){
            SQLBinaryOpExpr opExpr = parseWhere(wheres, wheres.size() - 1, params);
            queryBlock.setWhere(opExpr);
        }
        MetaVO.Limit limit = metaVO.getLimit();
        SQLLimit sqlLimit = parseLimit(limit);
        queryBlock.setLimit(sqlLimit);
        List<MetaVO.OrderBy> orderBys = metaVO.getOrderBy();
        SQLOrderBy sqlOrderBy = parseOrderBy(orderBys);
        queryBlock.setOrderBy(sqlOrderBy);
        statement.setSelect(select);
        return statement.toString();
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
