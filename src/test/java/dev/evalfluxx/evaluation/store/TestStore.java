package dev.evalfluxx.evaluation.store;

public class TestStore implements EvaluationStore<String, String, String> {
    @Override
    public void storeEvaluationResult(String configuration, String dataset, String result,
            java.util.List<dev.evalfluxx.evaluation.metrics.MetricResult> metricResults) {
        System.out.println("Storing evaluation result for configuration: " + configuration);
        System.out.println("Dataset: " + dataset);
        System.out.println("Result: " + result);
        System.out.println("Metrics: " + metricResults);
    }
}
