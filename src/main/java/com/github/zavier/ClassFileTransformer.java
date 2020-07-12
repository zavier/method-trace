package com.github.zavier;


import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.utility.JavaModule;

public class ClassFileTransformer implements AgentBuilder.Transformer {

    @Override
    public DynamicType.Builder<?> transform(DynamicType.Builder<?> builder, TypeDescription typeDescription, ClassLoader classLoader, JavaModule module) {
        System.out.println("typeDescription: " + typeDescription);
        return builder.method(ElementMatchers.any())
                .intercept(MethodDelegation.to(MethodTraceInterceptor.class));
    }
}
