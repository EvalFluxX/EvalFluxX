package dev.evalfluxx.evaluation.engine;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.javatuples.Triplet;
import org.reflections.Reflections;
import org.reflections.scanners.Scanner;
import org.reflections.scanners.SubTypesScanner;

import dev.evalfluxx.evaluation.config.ConfigurationPreProcessor;
import dev.evalfluxx.evaluation.config.ConfigurationProvider;
import dev.evalfluxx.evaluation.datasets.DatasetProvider;
import dev.evalfluxx.evaluation.metrics.Metric;
import dev.evalfluxx.evaluation.results.Processor;
import dev.evalfluxx.evaluation.store.EvaluationStore;

/**
 * Klasse zum Erstellen von EvaluationEngine-Instanzen. Hierfür wird Reflection
 * genutzt um die zugehörigen Generics zu ermitteln. Diese Klasse wird vom Mojo
 * verwendet und die einzelnen Enginens werden dort ausgeführt.
 */
public class EvaluationEngineBuilder {
    @SuppressWarnings({ "rawtypes", "unchecked", "null" })
    public List<EvaluationEngine<?, ?, ?>> buildEngines(Consumer<String> logConsumer, ClassLoader classLoader) {
        Map<Triplet<Class<?>, Class<?>, Class<?>>, EvaluationEngine<?, ?, ?>> engines = new java.util.HashMap<>();

        Reflections reflections = new Reflections("", new SubTypesScanner(false), classLoader);
        List<Class<? extends ConfigurationProvider>> configurationProvider = find(reflections,
                ConfigurationProvider.class);
        List<Class<? extends ConfigurationPreProcessor>> configurationPreProcessor = find(reflections,
                ConfigurationPreProcessor.class);
        List<Class<? extends DatasetProvider>> datasetProviders = find(reflections, DatasetProvider.class);
        List<Class<? extends Metric>> metrics = find(reflections, Metric.class);
        List<Class<? extends Processor>> processors = find(reflections, Processor.class);
        List<Class<? extends EvaluationStore>> evaluationStores = find(reflections, EvaluationStore.class);

        try {
            for (Class<? extends Processor> pClass : processors) {
                Class<?> cType = getGenericType(pClass, Processor.class, 0);
                Class<?> dType = getGenericType(pClass, Processor.class, 1);
                Class<?> rType = getGenericType(pClass, Processor.class, 2);

                engines.computeIfAbsent(Triplet.with(dType, cType, rType), t -> new EvaluationEngine<>())
                        .addProcessor(pClass.getConstructor().newInstance());
            }
            enhance(engines, configurationProvider, ConfigurationProvider.class, 0, 1,
                    EvaluationEngine::addConfigurationProvider);
            enhance(engines, configurationPreProcessor, ConfigurationPreProcessor.class, 0, 1,
                    EvaluationEngine::addConfigurationPreProcessor);
            enhance(engines, datasetProviders, DatasetProvider.class, 0, 0, EvaluationEngine::addDatasetProvider);
            enhance(engines, metrics, Metric.class, 0, 0, EvaluationEngine::addMetric);
            enhance(engines, evaluationStores, EvaluationStore.class, 0, 1, EvaluationEngine::addEvaluationStore);

        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException | NoSuchMethodException | SecurityException e) {
            throw new RuntimeException("Could not build EvaluationEngines", e);
        }
        return List.copyOf(engines.values());
    }

    @SuppressWarnings("rawtypes")
    private <T> void enhance(Map<Triplet<Class<?>, Class<?>, Class<?>>, EvaluationEngine<?, ?, ?>> engines,
            List<Class<? extends T>> typList, Class<T> rawType, int genericIndex,
            int typeIndex, BiConsumer<EvaluationEngine, T> enhancer)
            throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        for (Class<? extends T> cpClass : typList) {
            Class<?> gType = getGenericType(cpClass, rawType, genericIndex);
            List<Triplet<Class<?>, Class<?>, Class<?>>> list = engines.keySet().stream()
                    .filter(tr -> tr.getValue(typeIndex).equals(gType)).toList();
            for (Triplet<Class<?>, Class<?>, Class<?>> key : list) {
                enhancer.accept(engines.get(key), cpClass.getConstructor().newInstance());
            }
        }
    }

    private <T> List<Class<? extends T>> find(Reflections reflections, Class<T> cls) {
        return reflections
                .getSubTypesOf(cls).stream()
                .filter(model -> !Modifier.isAbstract(model.getModifiers())).toList();
    }

    public static Class<?> getGenericType(Class<?> metricClass, Class<?> rawType, int index) {
        for (Type type : metricClass.getGenericInterfaces()) {
            if (type instanceof ParameterizedType pt) {
                if (pt.getRawType().equals(rawType)) {
                    Type actualType = pt.getActualTypeArguments()[index];
                    if (actualType instanceof Class<?> c) {
                        return c;
                    }
                }
            }
        }
        return null;
    }
}
