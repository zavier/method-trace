package com.github.zavier;

import javassist.ByteArrayClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

public class MethodClassFileTransformer implements ClassFileTransformer {

    private final String classPrefix;

    public MethodClassFileTransformer(String classPrefix) {
        this.classPrefix = classPrefix;

    }

    public byte[] transform(ClassLoader loader,
                            String className,
                            Class<?> classBeingRedefined,
                            ProtectionDomain protectionDomain,
                            byte[] classfileBuffer) throws IllegalClassFormatException {
        return traceMethod(className, classfileBuffer);
    }

    private byte[] traceMethod(String className, byte[] classfileBuffer) {
        String realClassName = getDotClassName(className);
        if (!realClassName.startsWith(classPrefix)) {
            return classfileBuffer;
        }
        System.out.println("className:" + className);

        try {
            ClassPool classPool = ClassPool.getDefault();
            classPool.insertClassPath(new ByteArrayClassPath(realClassName, classfileBuffer));
            CtClass cls = classPool.get(realClassName);

            boolean isInterface = cls.isInterface();
            if (isInterface) {
                return classfileBuffer;
            }

            CtMethod[] methods = cls.getDeclaredMethods();
            for (CtMethod method : methods) {
                boolean empty = method.isEmpty();
                if (!empty) {
                    method.insertBefore("StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();\n" +
                            "                if (stackTrace.length >= 3) {\n" +
                            "                    StackTraceElement end = stackTrace[1];\n" +
                            "                    StackTraceElement start = stackTrace[2];\n" +
                            "                    String sb = start.getClassName() + \".\" + start.getMethodName() +\n" +
                            "                            \" ->> \" +\n" +
                            "                            end.getClassName() + \".\" + end.getMethodName();\n" +
                            "                    System.out.println(sb);\n" +
                            "                }");
                }
//                StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
//                if (stackTrace.length >= 3) {
//                    StackTraceElement end = stackTrace[1];
//                    StackTraceElement start = stackTrace[2];
//                    String sb = start.getClassName() + "." + start.getMethodName() +
//                            " ->> " +
//                            end.getClassName() + "." + end.getMethodName();
//                    System.out.println(sb);
//                }

            }
            return cls.toBytecode();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return classfileBuffer;
    }


    private String getDotClassName(String className) {
        return className.replace('/', '.');
    }
}
