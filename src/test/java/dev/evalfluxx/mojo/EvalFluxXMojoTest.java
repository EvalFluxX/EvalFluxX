package dev.evalfluxx.mojo;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EvalFluxXMojoTest {

    @Test
    void verifyWithoutFlagSkipsEvaluation() {
        boolean shouldRun = EvalFluxXMojo.shouldRunEvaluation(List.of("verify"), false);

        assertFalse(shouldRun, "Evaluation should not run for verify without -DwithEvals");
    }

    @Test
    void verifyWithFlagRunsEvaluation() {
        boolean shouldRun = EvalFluxXMojo.shouldRunEvaluation(List.of("verify"), true);

        assertTrue(shouldRun, "Evaluation should run for verify when -DwithEvals is set");
    }

    @Test
    void explicitGoalAlwaysRunsEvaluation() {
        boolean shouldRun = EvalFluxXMojo.shouldRunEvaluation(Collections.singletonList("evalfluxx:eval"), false);

        assertTrue(shouldRun, "Explicit evalfluxx:eval invocation should always run evaluation");
    }
}
