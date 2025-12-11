package dev.evalfluxx.evaluation;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;

import dev.evalfluxx.evaluation.engine.EvaluationEngine;
import dev.evalfluxx.evaluation.engine.EvaluationEngineBuilder;
import dev.evalfluxx.evaluation.store.EvaluationStore;

public class EvaluationEngineBuilderTest {
    @SuppressWarnings({ "null", "unchecked", "rawtypes" })
    @Test
    public void testBuildEngine() {
        EvaluationEngineBuilder builder = new EvaluationEngineBuilder();

        List<EvaluationEngine<?, ?, ?>> engines = builder.buildEngines(System.out::println,
                this.getClass().getClassLoader());

        assertEquals(engines.size(), 1);

        AtomicInteger processorCounter = new AtomicInteger(0);
        for (EvaluationEngine<?, ?, ?> evaluationEngine : engines) {

            evaluationEngine.addEvaluationStore(new EvaluationStore() {
                @Override
                public void storeEvaluationResult(Object configuration, Object dataset, Object result,
                        List metricResults) {
                    processorCounter.incrementAndGet();
                }
            });

            evaluationEngine.runEvaluations();
        }

        assertEquals(9, processorCounter.get()); // 3 configurations * 3 datasets = 9 evaluations
    }
}
