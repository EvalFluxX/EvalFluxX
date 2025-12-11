package dev.evalfluxx.evaluation.store;

import java.util.List;

import dev.evalfluxx.evaluation.metrics.MetricResult;

public interface EvaluationStore<C, D, R> {
    void storeEvaluationResult(C configuration, D dataset, R result, List<MetricResult> metricResults);
}
