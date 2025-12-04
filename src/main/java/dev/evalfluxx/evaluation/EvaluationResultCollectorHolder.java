package dev.evalfluxx.evaluation;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Simple holder to expose the active {@link EvaluationResultCollector} as a singleton service for evaluations.
 */
public final class EvaluationResultCollectorHolder implements EvaluationResultCollector {

    private static final EvaluationResultCollectorHolder INSTANCE = new EvaluationResultCollectorHolder();
    private static final List<EvaluationResultCollector> COLLECTORS = new CopyOnWriteArrayList<>();

    private EvaluationResultCollectorHolder() {
    }

    public static EvaluationResultCollectorHolder getInstance() {
        return INSTANCE;
    }

    /**
     * Register a collector instance that should receive evaluation results.
     */
    public static void register(EvaluationResultCollector collector) {
        Objects.requireNonNull(collector, "collector");
        if (collector == INSTANCE) {
            return;
        }
        if (!COLLECTORS.contains(collector)) {
            COLLECTORS.add(collector);
        }
    }

    /**
     * Remove a collector that should no longer receive evaluation results.
     */
    public static void unregister(EvaluationResultCollector collector) {
        COLLECTORS.remove(collector);
    }

    /**
     * Obtain a broadcasting collector that forwards records to every registered collector.
     */
    public static Optional<EvaluationResultCollector> get() {
        if (COLLECTORS.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(INSTANCE);
    }

    /**
     * Forward a record to all registered collectors.
     */
    @Override
    public void record(EvaluationRecord record) {
        for (EvaluationResultCollector collector : COLLECTORS) {
            collector.record(record);
        }
    }

    /**
     * @return immutable snapshot of currently registered collectors
     */
    public static List<EvaluationResultCollector> collectors() {
        return List.copyOf(COLLECTORS);
    }
}
