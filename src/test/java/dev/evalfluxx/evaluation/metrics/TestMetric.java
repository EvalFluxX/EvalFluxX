package dev.evalfluxx.evaluation.metrics;

import dev.evalfluxx.evaluation.metrics.Metric;
import dev.evalfluxx.evaluation.metrics.MetricResult;

public class TestMetric implements Metric<String, String> {
    @Override
    public MetricResult evaluate(String dataset, String result) {
        return new MetricResult("TestMetric", 42.0);
    }

}
