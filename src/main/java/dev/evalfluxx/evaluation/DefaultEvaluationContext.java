package dev.evalfluxx.evaluation;

import java.util.Objects;

/**
 * Default implementation of an evaluation context.
 */
public class DefaultEvaluationContext implements EvaluationContext {

    private final EvaluationConfiguration configuration;
    private final EvaluationResultCollector resultCollector;

    public DefaultEvaluationContext(EvaluationConfiguration configuration, EvaluationResultCollector resultCollector) {
        this.configuration = Objects.requireNonNull(configuration, "configuration");
        Objects.requireNonNull(resultCollector, "resultCollector");
        EvaluationResultCollectorHolder.register(resultCollector);
        this.resultCollector = EvaluationResultCollectorHolder.getInstance();
    }

    @Override
    public EvaluationConfiguration configuration() {
        return configuration;
    }

    @Override
    public EvaluationResultCollector resultCollector() {
        return resultCollector;
    }
}
