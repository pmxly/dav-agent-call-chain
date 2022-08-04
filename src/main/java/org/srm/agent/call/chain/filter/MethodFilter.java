package org.srm.agent.call.chain.filter;

import javassist.CtMethod;

public interface MethodFilter {
    boolean matchMethod(CtMethod ctMethod);
}
