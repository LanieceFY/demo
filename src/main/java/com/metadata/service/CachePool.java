package com.metadata.service;

import java.util.HashMap;
import java.util.Map;

public class CachePool {

    public static  <T> T getCacheData(String type, String key){
        return (T) CacheManager.getCache(type).get(key);
    }
}
