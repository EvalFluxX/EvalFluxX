package dev.evalfluxx.evaluation;

/**
 * Context passed to evaluation sets that bundles configuration and the result collector.
 */
public interface EvaluationContext extends EvaluationResultCollector {

    EvaluationConfiguration configuration();

    EvaluationResultCollector resultCollector();

    @Override
    default void record(EvaluationRecord record) {
        resultCollector().record(record);
    }
}
