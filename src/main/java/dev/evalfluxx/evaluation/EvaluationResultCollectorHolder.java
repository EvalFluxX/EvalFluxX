package dev.evalfluxx.evaluation;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Simple holder to expose the active {@link EvaluationResultCollector} as a
 * singleton service for evaluations.
 */
public final class EvaluationResultCollectorHolder implements EvaluationResultCollector {

    private static final EvaluationResultCollectorHolder INSTANCE = new EvaluationResultCollectorHolder();
    private final List<EvaluationResultCollector> collectors = new CopyOnWriteArrayList<>();

    private EvaluationResultCollectorHolder() {
    }

    public static EvaluationResultCollectorHolder getInstance() {
        return INSTANCE;
    }

    /**
     * Register a collector instance that should receive evaluation results.
     */
    public void register(EvaluationResultCollector collector) {
        Objects.requireNonNull(collector, "collector");
        if (collector == INSTANCE) {
            return;
        }
        if (!collectors.contains(collector)) {
            collectors.add(collector);
        }
    }

    /**
     * Remove a collector that should no longer receive evaluation results.
     */
    public void unregister(EvaluationResultCollector collector) {
        collectors.remove(collector);
    }

    /**
     * Obtain a broadcasting collector that forwards records to every registered
     * collector.
     */
    public Optional<EvaluationResultCollector> get() {
        if (collectors.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(INSTANCE);
    }

    /**
     * Forward a record to all registered collectors.
     */
    @Override
    public void record(EvaluationRecord record) {
        for (EvaluationResultCollector collector : collectors) {
            collector.record(record);
        }
    }

    /**
     * @return immutable snapshot of currently registered collectors
     */
    public List<EvaluationResultCollector> collectors() {
        return List.copyOf(collectors);
    }
}
