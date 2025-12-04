package dev.evalfluxx.evaluation;

import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Captures a single measurement that has been produced during evaluation.
 */
public record EvaluationRecord(String runnerName, String evaluationSetName, String metricName, Object value,
                               EvaluationConfiguration configuration, Instant timestamp,
                               Map<String, String> attributes) {

    public EvaluationRecord {
        configuration = configuration == null
                ? DefaultEvaluationConfiguration.empty("", "")
                : configuration;
        if (timestamp == null) {
            timestamp = Instant.now();
        }
        attributes = attributes == null ? Collections.emptyMap() : Collections.unmodifiableMap(new HashMap<>(attributes));
    }

    public String configurationId() {
        return Objects.toString(configuration.id(), "");
    }

    public String configurationName() {
        String name = configuration.name();
        return name == null ? configurationId() : name;
    }
}
