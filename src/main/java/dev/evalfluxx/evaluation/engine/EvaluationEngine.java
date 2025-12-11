package dev.evalfluxx.evaluation.engine;

import java.util.List;

import dev.evalfluxx.evaluation.config.ConfigurationPreProcessor;
import dev.evalfluxx.evaluation.config.ConfigurationProvider;
import dev.evalfluxx.evaluation.datasets.DatasetProvider;
import dev.evalfluxx.evaluation.metrics.Metric;
import dev.evalfluxx.evaluation.metrics.MetricResult;
import dev.evalfluxx.evaluation.results.Processor;
import dev.evalfluxx.evaluation.store.EvaluationStore;

public class EvaluationEngine<D, C, R extends D> {
    List<DatasetProvider<D>> datasetProviders = new java.util.ArrayList<>();
    List<ConfigurationProvider<C>> configurationProviders = new java.util.ArrayList<>();
    List<ConfigurationPreProcessor<C>> configurationPreProcessors = new java.util.ArrayList<>();
    List<Processor<C, D, R>> processors = new java.util.ArrayList<>();
    List<Metric<D, R>> metrics = new java.util.ArrayList<>();
    List<EvaluationStore<C, D, R>> evaluationStores = new java.util.ArrayList<>();

    public EvaluationEngine() {
    }

    // Methods to add dataset providers, configuration providers, pre-processors,
    // processors, and metrics would go here
    public EvaluationEngine<D, C, R> addDatasetProvider(DatasetProvider<D> provider) {
        datasetProviders.add(provider);
        return this;
    }

    public EvaluationEngine<D, C, R> addConfigurationProvider(ConfigurationProvider<C> provider) {
        configurationProviders.add(provider);
        return this;
    }

    public EvaluationEngine<D, C, R> addConfigurationPreProcessor(ConfigurationPreProcessor<C> preProcessor) {
        configurationPreProcessors.add(preProcessor);
        return this;
    }

    public EvaluationEngine<D, C, R> addProcessor(Processor<C, D, R> processor) {
        processors.add(processor);
        return this;
    }

    public EvaluationEngine<D, C, R> addMetric(Metric<D, R> metric) {
        metrics.add(metric);
        return this;
    }

    public EvaluationEngine<D, C, R> addEvaluationStore(EvaluationStore<C, D, R> store) {
        evaluationStores.add(store);
        return this;
    }

    public void runEvaluations() {
        assert !datasetProviders.isEmpty() : "No DatasetProviders registered!";
        assert !configurationProviders.isEmpty() : "No ConfigurationProviders registered!";
        assert !processors.isEmpty() : "No Processors registered!";
        assert !metrics.isEmpty() : "No Metrics registered!";

        // Implementation of the evaluation execution logic would go here
        configurationProviders.stream().flatMap(ConfigurationProvider::getConfigurations).forEach(configuration -> {
            configurationPreProcessors.stream().forEach(preProcessor -> preProcessor.initEnvirement(configuration));
            datasetProviders.stream().flatMap(DatasetProvider::getDatasets).forEach(ds -> {
                processors.stream().map(p -> p.process(configuration, ds)).forEach(pr -> {
                    List<MetricResult> metriResults = metrics.stream().map(metric -> metric.evaluate(ds, pr)).toList();

                    if (evaluationStores.isEmpty()) {
                        // Wenn keine Stores registriert sind, Ausgabe auf der Konsole
                        System.out
                                .println("Storing evaluation result for configuration: " + configuration);
                        System.out.println("Dataset: " + ds);
                        System.out.println("Result: " + pr);
                        System.out.println("Metrics: " + metriResults);
                    } else
                        evaluationStores.stream()
                                .forEach(store -> store.storeEvaluationResult(configuration, ds, pr, metriResults));
                });
            });
        });
    }
}
