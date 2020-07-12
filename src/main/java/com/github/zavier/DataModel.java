package com.github.zavier;

import java.util.List;

public class DataModel {

    private String traceId;

    private String id;

    private String parentId;

    private String className;

    private String methodName;

    private List<DataModel> childDataModels;

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public List<DataModel> getChildDataModels() {
        return childDataModels;
    }

    public void setChildDataModels(List<DataModel> childDataModels) {
        this.childDataModels = childDataModels;
    }
}
