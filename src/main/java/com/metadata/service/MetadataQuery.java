package com.metadata.service;

import com.metadata.entity.MetaVO;
import com.metadata.entity.ObjectEntity;

import java.util.List;
import java.util.Map;

public interface MetadataQuery {
    List<Map<String, Object>> query(MetaVO metaVO);
    Map<String, Object> queryUnique(MetaVO metaVO);
    List<Map<String, Object>> query(String sql);

}
