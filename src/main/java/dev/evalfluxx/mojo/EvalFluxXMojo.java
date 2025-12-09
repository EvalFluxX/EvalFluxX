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
import org.apache.maven.project.MavenProject;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.execution.MavenSession;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Mojo that will be bound to the custom rag-evaluation lifecycle phase.
 */
@Mojo(name = "run", defaultPhase = LifecyclePhase.TEST, requiresDependencyResolution = ResolutionScope.COMPILE)
public class EvalFluxXMojo extends AbstractMojo {

    @Parameter(property = "withEvals", defaultValue = "false")
    private boolean withEvals;

    @Parameter(defaultValue = "", readonly = true)
    private MavenSession session;

    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject project;

    @Override
    public void execute() throws MojoExecutionException {

        Collection<String> requestedGoals = session != null && session.getRequest() != null
                ? session.getRequest().getGoals()
                : Collections.emptyList();

        if (!shouldRunEvaluation(requestedGoals, withEvals)) {
            getLog().info(
                    "EvalFluxX RAG Evaluation skipped. Enable it with -DwithEvals or run evalfluxx:run explicitly.");
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

    void setWithEvals(boolean withEvals) {
        this.withEvals = withEvals;
    }

    protected void performEvaluation() throws MojoExecutionException {
        boolean foundRunner = false;
        List<Class<? extends EvaluationRunner>> runnerClasses = new ArrayList<>();
        List<Class<? extends EvaluationResultCollector>> collectorClasses = new ArrayList<>();

        try {
            List<String> elements = project.getCompileClasspathElements();
            URL[] urls = elements.stream()
                    .map(File::new)
                    .map(File::toURI)
                    .map(uri -> {
                        try {
                            getLog().debug("Adding to classloader: " + uri.toString());
                            return uri.toURL();
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .toArray(URL[]::new);

            try (URLClassLoader loader = new URLClassLoader(urls, getClass().getClassLoader())) {
                Reflections reflections = new Reflections("", new SubTypesScanner(false), loader);
                runnerClasses = reflections.getSubTypesOf(EvaluationRunner.class).stream()
                        .filter(model -> !Modifier.isAbstract(model.getModifiers())).toList();
                collectorClasses = reflections.getSubTypesOf(EvaluationResultCollector.class).stream()
                        .filter(model -> !Modifier.isAbstract(model.getModifiers())).toList();
            }
        } catch (Exception e) {
            getLog().error("Could not load project classes", e);
            throw new MojoExecutionException("Could not load project classes", e);
        }

        // Initialiesiere EvaluationResultCollector
        EvaluationResultCollectorHolder collectorHolder = EvaluationResultCollectorHolder.getInstance();
        for (Class<? extends EvaluationResultCollector> collectorClass : collectorClasses) {
            try {
                collectorHolder.register(collectorClass.getConstructor().newInstance());
            } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                    | InvocationTargetException
                    | NoSuchMethodException | SecurityException e) {
                getLog().warn("Could not instantiate EvaluationResultCollector: " + collectorClass.getName(), e);
            }
        }

        // Lade und führe EvaluationRunner aus
        List<EvaluationRunner> runners = new ArrayList<>();
        for (Class<? extends EvaluationRunner> runnerClass : runnerClasses) {
            try {
                runners.add(runnerClass.getConstructor().newInstance());
            } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                    | InvocationTargetException
                    | NoSuchMethodException | SecurityException e) {
                getLog().warn("Could not instantiate EvaluationRunner: " + runnerClass.getName(), e);
            }
        }

        for (EvaluationRunner runner : runners) {
            foundRunner = true;
            getLog().info("Executing EvaluationRunner: " + runner.getName());
            try {
                EvaluationConfiguration defaultConfiguration = DefaultEvaluationConfiguration.empty("default",
                        "Default");
                runner.loadConfiguration(defaultConfiguration);
                Collection<EvaluationConfiguration> configurations = runner.getConfigurations();
                if (configurations == null || configurations.isEmpty()) {
                    configurations = Collections.singletonList(defaultConfiguration);
                }

                for (EvaluationConfiguration configuration : configurations) {
                    EvaluationContext context = new DefaultEvaluationContext(configuration);
                    Collection<EvaluationSet> evaluationSets = runner.getEvaluationSets();
                    if (evaluationSets == null || evaluationSets.isEmpty()) {
                        getLog().warn("EvaluationRunner returned no EvaluationSets: " + runner.getName());
                    } else
                        for (EvaluationSet evaluationSet : evaluationSets) {
                            getLog().info("Executing EvaluationSet: " + evaluationSet.getName()
                                    + " with configuration " + configuration.name() + " (" + configuration.id() + ")");
                            evaluationSet.execute(context);
                        }
                }
            } catch (EvaluationException e) {
                getLog().error("EvaluationRunner failed: " + runner.getName(), e);
            } catch (Exception e) {
                getLog().error("Unexpected error during EvaluationRunner execution: " + runner.getName(), e);
            }
        }

        if (!foundRunner) {
            getLog().warn("No EvaluationRunner implementations found on the classpath.");
        }
    }
}
