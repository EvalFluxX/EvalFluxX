package dev.evalfluxx.evaluation.config;

public class TestPreProzessor implements ConfigurationPreProcessor<String> {
    @Override
    public void initEnvirement(String conf) {
        System.out.println("Initializing environment for configuration: " + conf);
    }
}
