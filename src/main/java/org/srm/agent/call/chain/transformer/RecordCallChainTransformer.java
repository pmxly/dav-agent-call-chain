package org.srm.agent.call.chain.transformer;

import javassist.*;
import org.srm.agent.call.chain.filter.CallChainClassFilter;
import org.srm.agent.call.chain.filter.CallChainMethodFilter;
import org.srm.agent.call.chain.filter.ClassFilter;
import org.srm.agent.call.chain.filter.MethodFilter;

import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;
import java.util.Objects;

public class RecordCallChainTransformer implements ClassFileTransformer {
    private static final ClassFilter CLASS_FILTER = new CallChainClassFilter();
    private static final MethodFilter METHOD_FILTER = new CallChainMethodFilter();
    private static final String BEFORE_INVOKE_CODE_TEMPLATE =
            "org.srm.assist.callchain.domain.CallPairNode callPairNode = new org.srm.assist.callchain.domain.CallPairNode();" +
                    "callPairNode.setClassName(\"%s\");" +
                    "callPairNode.setMethodName(\"%s\");" +
                    "callPairNode.setArgsString(\"%s\");" +
                    "callPairNode.setDeclaringInterface(\"%s\");" +
                    "org.srm.assist.callchain.enhancer.CallChainEnhancer.before(callPairNode);";
    private static final String AFTER_INVOKE_CODE = "org.srm.assist.callchain.enhancer.CallChainEnhancer.after();";

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) {
        try {
            boolean isModified = false;
            // lambda表达式 className为null
            if (Objects.isNull(className)) {
                return classfileBuffer;
            }
            // java开头的类，直接跳过
            if (className.startsWith("java.")) {
                return classfileBuffer;
            }

            className = className.replace("/", ".");

            boolean matchClassName = CLASS_FILTER.matchClass(className);
            if (!matchClassName) {
                return classfileBuffer;
            }

            ClassPool classPool = initClassPool(loader);
            CtClass ctClass = classPool.get(className);
            boolean matchClass = CLASS_FILTER.matchClass(ctClass);
            if (!matchClass) {
                return classfileBuffer;
            }

            for (CtMethod ctMethod : ctClass.getDeclaredMethods()) {

                if (METHOD_FILTER.matchMethod(ctMethod)) {
                    String declaringInterface = getDeclaringInterface(ctMethod);
                    String beforeInvokeCode = String.format(BEFORE_INVOKE_CODE_TEMPLATE,
                            ctClass.getName(),
                            ctMethod.getName(),
                            ctMethod.getSignature(),
                            declaringInterface // 声明方法的接口
                    );
                    ctMethod.insertBefore(String.format("{ %s }", beforeInvokeCode));
                    ctMethod.insertAfter(String.format("{ %s }", AFTER_INVOKE_CODE));
                    isModified = true;
                }
            }

            if (isModified) {
                return ctClass.toBytecode();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return classfileBuffer;
    }

    private String getDeclaringInterface(CtMethod method) {
        String name = method.getName();
        String signature = method.getSignature();
        CtClass declaringClass = method.getDeclaringClass();
        DeclaringInterfaceHolder declaringInterfaceHolder = new DeclaringInterfaceHolder();
        recursionGetDeclaringInterface(declaringInterfaceHolder, declaringClass, name, signature);
        return declaringInterfaceHolder.declaringInterface;
    }

    private void recursionGetDeclaringInterface(DeclaringInterfaceHolder declaringInterfaceHolder, CtClass declaringClass, String methodName, String signature) {
        if (declaringClass.isInterface()) {
            CtMethod[] declaredMethods = declaringClass.getDeclaredMethods();
            for (CtMethod declaredMethod : declaredMethods) {
                if (Objects.equals(declaredMethod.getName(), methodName) && Objects.equals(declaredMethod.getSignature(), signature)) {
                    declaringInterfaceHolder.declaringInterface = declaringClass.getName();
                    return;
                }
            }
        }

        try {
            CtClass superclass = declaringClass.getSuperclass();
            if (Objects.nonNull(superclass)) {
                recursionGetDeclaringInterface(declaringInterfaceHolder, superclass, methodName, signature);
            }
            CtClass[] interfaces = declaringClass.getInterfaces();
            for (CtClass superInterface : interfaces) {
                recursionGetDeclaringInterface(declaringInterfaceHolder, superInterface, methodName, signature);
                if (!Objects.equals(declaringInterfaceHolder.declaringInterface, "")) {
                    return;
                }
            }
        } catch (NotFoundException e) {
            e.printStackTrace();
        }

    }


    private ClassPool initClassPool(ClassLoader loader) {
        ClassPool classPool = new ClassPool(true);
        if (loader == null) {
            classPool.appendClassPath(new LoaderClassPath(ClassLoader.getSystemClassLoader()));
        } else {
            classPool.appendClassPath(new LoaderClassPath(loader));
        }
        return classPool;
    }

    private static class DeclaringInterfaceHolder {
        String declaringInterface = "";
    }
}
