package org.srm.agent.call.chain.filter;

import javassist.CtClass;

public interface ClassFilter {
    boolean matchClass(String className);
    boolean matchClass(CtClass ctClass);
}
