package com.metadata.entity;

import java.util.Map;

public class FlowVO {

    private Boolean end;
    private String flowId;
    private String flowType;
    private Integer seq;
    private Map<Integer, String> seqNext;
    private String beanName;
    private Map<String, Object> param;

    public Boolean getEnd() {
        return end;
    }

    public void setEnd(Boolean end) {
        this.end = end;
    }

    public String getFlowId() {
        return flowId;
    }

    public void setFlowId(String flowId) {
        this.flowId = flowId;
    }

    public String getFlowType() {
        return flowType;
    }

    public void setFlowType(String flowType) {
        this.flowType = flowType;
    }

    public Integer getSeq() {
        return seq;
    }

    public void setSeq(Integer seq) {
        this.seq = seq;
    }

    public Map<Integer, String> getSeqNext() {
        return seqNext;
    }

    public void setSeqNext(Map<Integer, String> seqNext) {
        this.seqNext = seqNext;
    }

    public String getBeanName() {
        return beanName;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    public Map<String, Object> getParam() {
        return param;
    }

    public void setParam(Map<String, Object> param) {
        this.param = param;
    }
}
