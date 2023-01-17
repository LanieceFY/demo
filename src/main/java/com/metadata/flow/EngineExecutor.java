package com.metadata.flow;

import com.metadata.entity.FlowVO;
import com.metadata.entity.MetaContent;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public interface EngineExecutor {

    void execute(FlowVO flowVO, MetaContent content);

    void execute(FlowVO flowVO, MetaContent content, Lock lock, Condition condition);
}
