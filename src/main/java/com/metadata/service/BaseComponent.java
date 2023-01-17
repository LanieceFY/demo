package com.metadata.service;

import com.metadata.entity.MetaContent;

public interface BaseComponent {

    void execute(MetaContent content) throws Exception;
}
