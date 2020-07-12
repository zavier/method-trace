package com.github.zavier;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.matcher.ElementMatchers;

import java.lang.instrument.Instrumentation;

public class JavaAgent {

    public static void premain(String agentArgs, Instrumentation instrumentation) {
        System.out.println("agentArgs:"+ agentArgs);
//        String interceptPackagePrefix = agentArgs.replace('.', '/');
        new AgentBuilder.Default()
                .type(ElementMatchers.nameStartsWith(agentArgs))
                .transform(new ClassFileTransformer())
                .installOn(instrumentation);
    }

}
