package dev.evalfluxx.evaluation;

import java.util.Objects;

/**
 * Default implementation of an evaluation context.
 */
public class DefaultEvaluationContext implements EvaluationContext {

    private final EvaluationConfiguration configuration;

    public DefaultEvaluationContext(EvaluationConfiguration configuration) {
        this.configuration = Objects.requireNonNull(configuration, "configuration");
    }

    @Override
    public EvaluationConfiguration configuration() {
        return configuration;
    }

    @Override
    public EvaluationResultCollector resultCollector() {
        return EvaluationResultCollectorHolder.getInstance();
    }
}
