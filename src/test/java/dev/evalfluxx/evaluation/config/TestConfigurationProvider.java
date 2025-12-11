package dev.evalfluxx.evaluation.config;

public class TestConfigurationProvider implements ConfigurationProvider<String> {
    @Override
    public java.util.stream.Stream<String> getConfigurations() {
        return java.util.stream.Stream.of("Config1", "Config2", "Config3");
    }
}
