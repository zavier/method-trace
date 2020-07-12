package com.github.zavier;

import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.Callable;

public class MethodTraceInterceptor {

    private static final ThreadLocal<Stack<String>> parentIdLocal = new ThreadLocal<>();

    private static final ThreadLocal<List<DataModel>> threadLocal = new ThreadLocal<>();

    @RuntimeType
    public static Object intercept(@Origin Method method, @SuperCall Callable<?> callable) throws Exception {
        Stack<String> parentIdStack = parentIdLocal.get();
        String parentId = null;
        if (parentIdStack == null) {
            parentIdLocal.set(new Stack<>());
        } else {
            parentId = parentIdStack.peek();
        }

        // parentId入栈
        String thisId = UUID.randomUUID().toString();
        parentIdLocal.get().push(thisId);

        // 创建dataModel
        DataModel dataModel = new DataModel();
        dataModel.setParentId(parentId);
        dataModel.setId(thisId);
        dataModel.setClassName(method.getDeclaringClass().getName());
        dataModel.setMethodName(method.getName());

        // 添加到list中
        List<DataModel> dataModels = threadLocal.get();
        if (dataModels == null) {
            threadLocal.set(new ArrayList<>());
            dataModels = threadLocal.get();
        }
        dataModels.add(dataModel);

        // 调用实际方法
        Object call = callable.call();

        // parentId出栈
        if (!parentIdLocal.get().isEmpty()) {
            parentIdLocal.get().pop();
        }

        // 如果是入口，那么当前线程任务完成，清除数据
        if (parentId == null) {
            // 打印结果
            DataModel root = formatRootTree(threadLocal.get());
            print2Mermaid(null, root);
            // 清除数据
            parentIdLocal.remove();
            threadLocal.remove();
        }
        return call;
    }

    private static DataModel formatRootTree(List<DataModel> dataModels) {
        if (dataModels == null || dataModels.size() == 0) {
            return null;
        }
        Map<String, List<DataModel>> dataModelMap = new HashMap<>();
        DataModel root = null;
        for (DataModel dataModel : dataModels) {
            if (dataModel.getParentId() == null) {
                root = dataModel;
                continue;
            }
            dataModelMap.computeIfAbsent(dataModel.getParentId(), k -> new ArrayList<>());
            dataModelMap.get(dataModel.getParentId()).add(dataModel);
        }
        formatData(root, dataModelMap);
        return root;
    }

    private static void formatData(DataModel root, Map<String, List<DataModel>> dataModelMap) {
        if (root == null) {
            return;
        }
        String parentId = root.getId();
        List<DataModel> dataModels = dataModelMap.get(parentId);
        root.setChildDataModels(dataModels);
        if (dataModels != null && dataModelMap.size() > 0) {
            for (DataModel dataModel : dataModels) {
                formatData(dataModel, dataModelMap);
            }
        }
    }

    private static void print2Mermaid(DataModel parent, DataModel root) {
        if (root == null) {
            return;
        }
        if (parent != null) {
            System.out.println(parent.getClassName() + "->>" + root.getClassName() + ":" + root.getMethodName());
        } else {
            System.out.println("Start->>" + root.getClassName() + ":" + root.getMethodName());
        }
        List<DataModel> childDataModels = root.getChildDataModels();
        if (childDataModels != null && childDataModels.size() != 0) {
            for (DataModel dataModel : childDataModels) {
                print2Mermaid(root, dataModel);
            }
        }
    }
}
