package dev.evalfluxx.evaluation;

/**
 * Represents a collection of evaluation steps that belong together.
 */
public interface EvaluationSet {

    /**
     * @return human readable name of the evaluation set
     */
    String getName();

    /**
     * Execute the evaluation set using the provided context.
     *
     * @param context evaluation context
     * @throws EvaluationException when evaluation cannot be completed
     */
    void execute(EvaluationContext context) throws EvaluationException;
}
