package org.srm.agent.call.chain.filter;

import javassist.CtMethod;
import javassist.Modifier;

import java.util.Arrays;

public class CallChainMethodFilter implements MethodFilter {
    private static final String[] objectMethods;

    static {
        objectMethods = new String[]{"getClass", "hashCode", "clone", "toString", "equals", "finalize", "notify", "notifyAll", "registerNatives", "wait"};
    }

    @Override
    public boolean matchMethod(CtMethod ctMethod) {
        // 构造方法不拦截
        if (ctMethod.getMethodInfo2().isConstructor()) {
            return false;
        }
        // private abstract native方法不拦截
        int modifiers = ctMethod.getModifiers();
        if (Modifier.isPrivate(modifiers) || Modifier.isAbstract(modifiers) || Modifier.isNative(modifiers)) {
            return false;
        }
        // 从Object继承来的方法不拦截
        return Arrays.stream(objectMethods).noneMatch(objectMethod -> objectMethod.equals(ctMethod.getName()));
    }
}
