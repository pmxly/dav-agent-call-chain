package org.srm.agent.call.chain;

import org.srm.agent.call.chain.transformer.RecordCallChainTransformer;

import java.lang.instrument.Instrumentation;

public class Main {
    public static void premain(String args, Instrumentation instrumentation) {
        instrumentation.addTransformer(new RecordCallChainTransformer());
    }
}
