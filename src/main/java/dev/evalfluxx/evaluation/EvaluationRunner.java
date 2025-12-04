package dev.evalfluxx.evaluation;

import java.util.Collection;
import java.util.Collections;

/**
 * Loads the evaluation configuration and exposes the evaluation sets that should be executed.
 */
public interface EvaluationRunner {

    /**
     * @return human readable name of the runner
     */
    default String getName() {
        return getClass().getSimpleName();
    }

    /**
     * @return evaluation configurations that this runner will execute
     */
    default Collection<EvaluationConfiguration> getConfigurations() {
        return Collections.emptyList();
    }

    /**
     * Load configuration that determines how this runner executes.
     *
     * @param configuration evaluation configuration
     * @throws EvaluationException when the configuration cannot be processed
     */
    void loadConfiguration(EvaluationConfiguration configuration) throws EvaluationException;

    /**
     * @return evaluation sets managed by this runner
     */
    Collection<EvaluationSet> getEvaluationSets();
}
