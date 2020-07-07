package com.github.zavier;

import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;

public class JavaAgent {

    public static void premain(String agentArgs, Instrumentation instrumentation) {
        System.out.println("agentArgs:"+ agentArgs);
        instrumentation.addTransformer(new MethodClassFileTransformer(agentArgs));
    }

    public static void agentmain(String agentArgs, Instrumentation instrumentation) {
        instrumentation.addTransformer(new MethodClassFileTransformer(agentArgs));
        try {
            instrumentation.retransformClasses(String.class);
        } catch (UnmodifiableClassException e) {
            e.printStackTrace();
        }
    }
}
