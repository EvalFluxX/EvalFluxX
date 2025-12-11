package dev.evalfluxx.evaluation.config;

import java.util.stream.Stream;

public interface ConfigurationProvider<C> {
    Stream<C> getConfigurations();
}
