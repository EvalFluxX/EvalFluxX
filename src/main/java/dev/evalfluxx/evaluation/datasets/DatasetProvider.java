package dev.evalfluxx.evaluation.datasets;

import java.util.stream.Stream;

public interface DatasetProvider<D> {
    Stream<D> getDatasets();
}
