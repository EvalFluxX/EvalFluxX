package dev.evalfluxx.mojo;

import dev.evalfluxx.evaluation.DefaultEvaluationConfiguration;
import dev.evalfluxx.evaluation.DefaultEvaluationContext;
import dev.evalfluxx.evaluation.EvaluationConfiguration;
import dev.evalfluxx.evaluation.EvaluationContext;
import dev.evalfluxx.evaluation.EvaluationException;
import dev.evalfluxx.evaluation.EvaluationRunner;
import dev.evalfluxx.evaluation.EvaluationSet;
import dev.evalfluxx.evaluation.EvaluationResultCollector;
import dev.evalfluxx.evaluation.EvaluationResultCollectorHolder;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.execution.MavenSession;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.ServiceLoader;

/**
 * Mojo that will be bound to the custom rag-evaluation lifecycle phase.
 */
@Mojo(name = "run", defaultPhase = LifecyclePhase.VERIFY,
      requiresDependencyResolution = ResolutionScope.TEST)
public class EvalFluxXMojo extends AbstractMojo {

    @Parameter(property = "withEvals", defaultValue = "false")
    private boolean withEvals;

    @Parameter(defaultValue = "${session}", readonly = true)
    private MavenSession session;

    @Override
    public void execute() throws MojoExecutionException {
        Collection<String> requestedGoals = session != null && session.getRequest() != null
                ? session.getRequest().getGoals()
                : Collections.emptyList();

        if (!shouldRunEvaluation(requestedGoals, withEvals)) {
            getLog().info("EvalFluxX RAG Evaluation skipped. Enable it with -DwithEvals or run evalfluxx:run explicitly.");
            return;
        }

        getLog().info("EvalFluxX RAG Evaluation Phase executed.");
        performEvaluation();
    }

    static boolean shouldRunEvaluation(Collection<String> requestedGoals, boolean withEvals) {
        boolean explicitlyRequested = isExplicitInvocation(requestedGoals);
        return explicitlyRequested || withEvals;
    }

    private static boolean isExplicitInvocation(Collection<String> requestedGoals) {
        if (requestedGoals == null) {
            return false;
        }
        for (String goal : requestedGoals) {
            if (goal != null && goal.contains("evalfluxx:run")) {
                return true;
            }
        }
        return false;
    }

    private void performEvaluation() {
        EvaluationResultCollectorHolder collectorHolder = EvaluationResultCollectorHolder.getInstance();
        ServiceLoader<EvaluationResultCollector> collectorLoader = ServiceLoader.load(EvaluationResultCollector.class,
                Thread.currentThread().getContextClassLoader());
        Iterator<EvaluationResultCollector> collectorIterator = collectorLoader.iterator();
        while (collectorIterator.hasNext()) {
            EvaluationResultCollectorHolder.register(collectorIterator.next());
        }
        MavenLogEvaluationResultCollector mavenLogCollector = new MavenLogEvaluationResultCollector(getLog());
        EvaluationResultCollectorHolder.register(mavenLogCollector);

        ServiceLoader<EvaluationRunner> loader = ServiceLoader.load(EvaluationRunner.class,
                Thread.currentThread().getContextClassLoader());
        boolean foundRunner = false;
        for (EvaluationRunner runner : loader) {
            foundRunner = true;
            getLog().info("Executing EvaluationRunner: " + runner.getName());
            try {
                EvaluationConfiguration defaultConfiguration = DefaultEvaluationConfiguration.empty("default", "Default");
                runner.loadConfiguration(defaultConfiguration);
                Collection<EvaluationConfiguration> configurations = runner.getConfigurations();
                if (configurations.isEmpty()) {
                    configurations = Collections.singletonList(defaultConfiguration);
                }

                for (EvaluationConfiguration configuration : configurations) {
                    EvaluationContext context = new DefaultEvaluationContext(configuration, collectorHolder);
                    Collection<EvaluationSet> evaluationSets = runner.getEvaluationSets();
                    if (evaluationSets.isEmpty()) {
                        getLog().warn("EvaluationRunner returned no EvaluationSets: " + runner.getName());
                    }
                    for (EvaluationSet evaluationSet : evaluationSets) {
                        getLog().info("Executing EvaluationSet: " + evaluationSet.getName()
                                + " with configuration " + configuration.name() + " (" + configuration.id() + ")");
                        evaluationSet.execute(context);
                    }
                }
            } catch (EvaluationException e) {
                getLog().error("EvaluationRunner failed: " + runner.getName(), e);
            }
        }

        if (!foundRunner) {
            getLog().warn("No EvaluationRunner implementations found on the classpath.");
        }
    }
}
