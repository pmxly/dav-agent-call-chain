package org.srm.agent.call.chain.filter;

import javassist.CtClass;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CallChainClassFilter implements ClassFilter {

    private final List<ClassFilterRule> filterRules = new ArrayList<>();

    public CallChainClassFilter() {
        ClassFilterRule srmControllerFilterRule = new ClassFilterRule(new String[]{"org", "srm", "api", "controller"}, "Controller");
        ClassFilterRule srmServiceFilterRule = new ClassFilterRule(new String[]{"org", "srm", "app", "service", "impl"}, "ServiceImpl");
        ClassFilterRule srmDomainServiceFilterRule = new ClassFilterRule(new String[]{"org", "srm", "domain", "service", "impl"}, "ServiceImpl");
        ClassFilterRule srmRepositoryFilterRule = new ClassFilterRule(new String[]{"org", "srm", "infra", "repository", "impl"}, "RepositoryImpl");
        ClassFilterRule srmDomainRepositoryFilterRule = new ClassFilterRule(new String[]{"org", "srm", "domain", "repository", "impl"}, "RepositoryImpl");
        ClassFilterRule hzeroControllerFilterRule = new ClassFilterRule(new String[]{"org", "hzero", "api", "controller"}, "Controller");
        ClassFilterRule hzeroServiceFilterRule = new ClassFilterRule(new String[]{"org", "hzero", "app", "service", "impl"}, "ServiceImpl");
        ClassFilterRule hzeroDomainServiceFilterRule = new ClassFilterRule(new String[]{"org", "hzero", "domain", "service", "impl"}, "ServiceImpl");
        ClassFilterRule hzeroRepositoryFilterRule = new ClassFilterRule(new String[]{"org", "hzero", "infra", "repository", "impl"}, "RepositoryImpl");
        ClassFilterRule hzeroDomainRepositoryFilterRule = new ClassFilterRule(new String[]{"org", "hzero", "domain", "repository", "impl"}, "RepositoryImpl");

        filterRules.add(srmControllerFilterRule);
        filterRules.add(srmServiceFilterRule);
        filterRules.add(srmRepositoryFilterRule);
        filterRules.add(srmDomainServiceFilterRule);
        filterRules.add(srmDomainRepositoryFilterRule);

        filterRules.add(hzeroControllerFilterRule);
        filterRules.add(hzeroServiceFilterRule);
        filterRules.add(hzeroRepositoryFilterRule);
        filterRules.add(hzeroDomainServiceFilterRule);
        filterRules.add(hzeroDomainRepositoryFilterRule);

    }

    @Override
    public boolean matchClass(String className) {
        if (Objects.isNull(className)) {
            return false;
        }
        for (ClassFilterRule filterRule : filterRules) {
            boolean match = filterRule.match(className);
            if (match) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean matchClass(CtClass ctClass) {
        if (Objects.isNull(ctClass)) {
            return false;
        }
        if (ctClass.isInterface()) {
            return false;
        }

        return true;
    }
}
