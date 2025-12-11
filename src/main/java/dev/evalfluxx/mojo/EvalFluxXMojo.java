package dev.evalfluxx.mojo;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

import dev.evalfluxx.evaluation.engine.EvaluationEngine;
import dev.evalfluxx.evaluation.engine.EvaluationEngineBuilder;

/**
 * Mojo that will be bound to the custom rag-evaluation lifecycle phase.
 */
@Mojo(name = "run", defaultPhase = LifecyclePhase.TEST, requiresDependencyResolution = ResolutionScope.RUNTIME)
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

                List<EvaluationEngine<?, ?, ?>> engines = new EvaluationEngineBuilder().buildEngines(getLog()::info,
                        loader);
                for (EvaluationEngine<?, ?, ?> evaluationEngine : engines) {
                    foundRunner = true;
                    evaluationEngine.runEvaluations();
                }
            }
        } catch (Exception e) {
            getLog().error("Could not load project classes", e);
            throw new MojoExecutionException("Could not load project classes", e);
        }

        if (!foundRunner) {
            getLog().warn("No EvaluationEngine build.");
        }
    }
}
