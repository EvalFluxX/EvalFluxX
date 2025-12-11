package dev.evalfluxx.evaluation.metrics;

public interface Metric<D, R> {
    default String getName() {
        return this.getClass().getSimpleName();
    }

    MetricResult evaluate(D dataset, R result);

}
