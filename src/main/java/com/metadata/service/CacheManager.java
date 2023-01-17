package com.metadata.service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class CacheManager {

    private static Map<String, Map<String, Object>> map = new HashMap<>();

    private static Map<String, Object> conditions = new HashMap<>();

    private static Map<String, Object> locks = new HashMap<>();

    static {
        map.put("condition", conditions);
        map.put("lock", locks);
    }
    public static Map<String, Object> getCache(String type){
        return map.get(type);
    }

    public static void setCondition(String key, Condition condition){
        conditions.putIfAbsent(key, condition);
    }

    public static void setLock(String key, Lock lock){
        locks.putIfAbsent(key, lock);
    }

}
