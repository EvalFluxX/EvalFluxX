package dev.evalfluxx.evaluation;

/**
 * Abstraction used by evaluations to propagate measured values to a central system.
 */
public interface EvaluationResultCollector {

    /**
     * Record a single measurement.
     *
     * @param record measurement record produced by an evaluation
     */
    void record(EvaluationRecord record);
}
