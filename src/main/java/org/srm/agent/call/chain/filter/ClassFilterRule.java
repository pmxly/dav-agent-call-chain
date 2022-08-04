package org.srm.agent.call.chain.filter;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class ClassFilterRule {
    private final String[] pkgKeyWord;
    private final String endWith;

    public ClassFilterRule(String[] pkgKeyWord, String endWith) {
        this.pkgKeyWord = pkgKeyWord;
        this.endWith = endWith;
    }

    public boolean match(String className) {
        if (Objects.nonNull(endWith) && !className.endsWith(endWith)) {
            return false;
        }
        List<String> classNameSegments = Arrays.asList(className.split("\\."));
        return Arrays.stream(pkgKeyWord).allMatch(classNameSegments::contains);
    }
}
