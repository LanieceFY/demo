package com.metadata.entity;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MetaContent {

    private Map<String, Object> params = new ConcurrentHashMap<String, Object>();

    public void putParam(String key, Object arg){
        params.put(key, arg);
    }

    public <T> T getParams(String key) {
        return (T) params.get(key);
    }
}
