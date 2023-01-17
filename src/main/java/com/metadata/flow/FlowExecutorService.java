package com.metadata.flow;

import com.metadata.entity.FlowVO;
import com.metadata.entity.MetaContent;
import com.metadata.service.BaseComponent;
import com.metadata.service.CacheManager;
import com.metadata.service.CachePool;
import com.metadata.service.ComponentFactory;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Component
public class FlowExecutorService implements EngineExecutor{

    @Resource
    private ComponentFactory<BaseComponent> compFactory;

    @Override
    public void execute(FlowVO flowVO, MetaContent content) {
        if(flowVO==null){
            return;
        }
        FlowVO nextFlow = processFLow(flowVO, content);
        execute(nextFlow, content);
    }

    @Override
    public void execute(FlowVO flowVO, MetaContent content, Lock lock, Condition condition) {
        if (lock!=null){
            try{
                lock.lock();
                if(flowVO == null){
                    return;
                }
//                FlowVO nextFlow = processFLow(flowVO, content);
                FlowVO nextFlow = new FlowVO();
                nextFlow.setFlowId("123123");
                List<String> breakpoints = content.getParams("breakpoints");
                if(breakpoints.contains(nextFlow.getFlowId())){
                    condition.await(120, TimeUnit.SECONDS);
                }
                execute(nextFlow, content, lock, condition);
            } catch (InterruptedException e){
                throw new RuntimeException(e);
            } finally {
                lock.unlock();
            }
        } else {
            execute(flowVO, content);
        }
    }

    private FlowVO getNextStep(FlowVO flowVO, int retCode){
        String nextFlowId = flowVO.getSeqNext().get(retCode);
        if(Strings.isBlank(nextFlowId)){
            return null;
        }
        return CachePool.getCacheData("flow", nextFlowId);
    }

    private FlowVO processFLow(FlowVO flowVO, MetaContent content){
        Integer retCode = 0;
        if(flowVO.getFlowType().equals("1")){
            String beanName = flowVO.getBeanName();
            BaseComponent service = compFactory.getBean(beanName);
            try {
                service.execute(content);
            } catch (Exception e) {

            }
        } else if(flowVO.getFlowType().equals("2")){
            //脚本引擎
        }
        return getNextStep(flowVO, retCode);
    }
}
