package dev.evalfluxx.evaluation.datasets;

public class TestDatasetProvider implements DatasetProvider<String> {
    @Override
    public java.util.stream.Stream<String> getDatasets() {
        return java.util.stream.Stream.of("Dataset1", "Dataset2", "Dataset3");
    }
}
