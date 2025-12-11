package dev.evalfluxx.evaluation.config;

public interface ConfigurationPreProcessor<C> {
    void initEnvirement(C conf);
}
