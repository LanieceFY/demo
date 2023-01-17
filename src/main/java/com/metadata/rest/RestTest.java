package com.metadata.rest;

import com.metadata.entity.*;
import com.metadata.flow.EngineExecutor;
import com.metadata.service.CacheManager;
import com.metadata.service.CachePool;
import com.metadata.service.MySqlMetadataQuery;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

@RestController
@RequestMapping({"/entity"})
public class RestTest {

    @Resource
    private JdbcTemplate jdbcTemplate;
    @Resource
    private MySqlMetadataQuery query;
    @Resource
    private EngineExecutor executor;

    @PostMapping(value="/insert")
    public Object test() throws Exception {
        List<MetaFieldEntity> list = jdbcTemplate.query("select * from t_entity ", new MetaFieldEntity());
        Map<Integer, List<MetaFieldEntity>> map = list.stream().collect(Collectors.groupingBy(MetaFieldEntity::getTableId));
        Map<Integer, MetaEntity> entityMap = new HashMap<>();
        for(Integer key : map.keySet()){
            MetaEntity entity = new MetaEntity();
            entity.setFields(map.get(key));
            entity.setTableName(map.get(key).get(0).getTableName());
            entityMap.put(key, entity);
        }
        return entityMap;
    }

    @PostMapping(value="/query")
    public Object test1(@RequestBody MetaVO data) throws Exception {
        List<MetaFieldEntity> list = jdbcTemplate.query("select * from t_entity ", new MetaFieldEntity());
        Map<Integer, List<MetaFieldEntity>> map = list.stream().collect(Collectors.groupingBy(MetaFieldEntity::getTableId));
        Map<Integer, MetaEntity> entityMap = new HashMap<>();
        for(Integer key : map.keySet()){
            MetaEntity entity = new MetaEntity();
            entity.setFields(map.get(key));
            entity.setTableName(map.get(key).get(0).getTableName());
            entityMap.put(key, entity);
        }
        MetaEntity entity = entityMap.get(123);
        query.query(data);
        return null;
    }

    @PostMapping(value="/test1")
    public Object test2() throws Exception {
        List<MetaFieldEntity> list = jdbcTemplate.query("select * from t_entity ", new MetaFieldEntity());
        Map<Integer, List<MetaFieldEntity>> map = list.stream().collect(Collectors.groupingBy(MetaFieldEntity::getTableId));
        Map<Integer, MetaEntity> entityMap = new HashMap<>();
        for(Integer key : map.keySet()){
            MetaEntity entity = new MetaEntity();
            entity.setFields(map.get(key));
            entity.setTableName(map.get(key).get(0).getTableName());
            entityMap.put(key, entity);
        }
        MetaEntity entity = entityMap.get(123);
        query.query(entity);
        return null;
    }

    @GetMapping(value="/unlock/{key}")
    public void unlock(@PathVariable("key") String key){
        Condition condition = CachePool.getCacheData("condition", key);
        Lock lock = CachePool.getCacheData("lock", key);
        try {
            lock.lock();
            condition.signal();
        } finally {
            lock.unlock();
        }
        System.out.print("12312313");
    }

    @GetMapping(value="/debug/{key}/{breakpoints}")
    public void debug(@PathVariable("key") String key, @PathVariable("breakpoints") String breakpoints){
        FlowVO flowVO = new FlowVO();
        MetaContent content = new MetaContent();
        Lock lock = new ReentrantLock();
        Condition condition = lock.newCondition();
        CacheManager.setLock(key, lock);
        CacheManager.setCondition(key, condition);
        content.putParam("breakpoints", breakpoints);
        executor.execute(flowVO, content, lock, condition);
    }

}
