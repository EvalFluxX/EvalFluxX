package dev.evalfluxx.evaluation;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Default in-memory configuration backing for evaluations.
 */
public class DefaultEvaluationConfiguration implements EvaluationConfiguration {

    private final String id;
    private final String name;
    private final Map<String, String> settings;

    public DefaultEvaluationConfiguration(String id, String name, Map<String, String> settings) {
        this.id = id == null ? "default" : id;
        this.name = name == null ? this.id : name;
        this.settings = settings == null ? Collections.emptyMap() : new HashMap<>(settings);
    }

    public static DefaultEvaluationConfiguration empty(String id, String name) {
        return new DefaultEvaluationConfiguration(id, name, Collections.emptyMap());
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public Map<String, String> settings() {
        return Collections.unmodifiableMap(settings);
    }
}
