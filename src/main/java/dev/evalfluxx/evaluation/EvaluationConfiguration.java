package dev.evalfluxx.evaluation;

import java.util.Map;

/**
 * Configuration used by {@link EvaluationRunner} instances to decide which evaluation
 * settings should be executed.
 */
public interface EvaluationConfiguration {

    /**
     * @return stable identifier of the configuration (e.g. file name, profile id)
     */
    String id();

    /**
     * @return human readable name of the configuration
     */
    String name();

    /**
     * @return immutable view of configuration settings
     */
    Map<String, String> settings();

    /**
     * Convenience method to access individual configuration values.
     *
     * @param key configuration key
     * @return configuration value or {@code null} when undefined
     */
    default String get(String key) {
        return settings().get(key);
    }
}
