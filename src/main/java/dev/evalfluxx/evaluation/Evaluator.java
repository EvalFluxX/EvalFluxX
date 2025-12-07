package dev.evalfluxx.evaluation;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Base implementation of an {@link EvaluationRunner} that turns annotated
 * methods into {@link EvaluationSet}s.
 */
public abstract class Evaluator implements EvaluationRunner {
    private final List<EvaluationSet> evaluationSets = new ArrayList<>();

    @Override
    public Collection<EvaluationSet> getEvaluationSets() {
        if (evaluationSets == null)
            for (Method method : getEvaluationMethods()) {
                evaluationSets.add(createEvaluationSet(method));
            }
        return Collections.unmodifiableList(evaluationSets);
    }

    private List<Method> getEvaluationMethods() {
        List<Method> methods = new ArrayList<>();
        for (Method method : getClass().getMethods()) {
            if (method.isAnnotationPresent(Evaluation.class)) {
                methods.add(method);
            }
        }
        return methods;
    }

    private EvaluationSet createEvaluationSet(Method method) {
        Evaluation annotation = method.getAnnotation(Evaluation.class);
        String name = annotation.value().isEmpty() ? method.getName() : annotation.value();
        return new MethodBackedEvaluationSet(this, method, name);
    }

    private static final class MethodBackedEvaluationSet implements EvaluationSet {
        private final Evaluator target;
        private final Method method;
        private final String name;

        MethodBackedEvaluationSet(Evaluator target, Method method, String name) {
            this.target = target;
            this.method = method;
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public void execute(EvaluationContext context) throws EvaluationException {
            try {
                method.setAccessible(true);
                Class<?>[] parameterTypes = method.getParameterTypes();
                if (parameterTypes.length == 0) {
                    method.invoke(target);
                } else if (parameterTypes.length == 1 && EvaluationContext.class.isAssignableFrom(parameterTypes[0])) {
                    method.invoke(target, context);
                } else {
                    throw new EvaluationException("Unsupported parameter signature for evaluation method: " + method);
                }
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new EvaluationException("Failed to execute evaluation method: " + method, e);
            }
        }
    }
}
