package dev.evalfluxx.mojo;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;

/**
 * Mojo that will be bound to the custom rag-evaluation lifecycle phase.
 */
@Mojo(name = "run", defaultPhase = LifecyclePhase.VERIFY,
      requiresDependencyResolution = ResolutionScope.TEST)
public class EvalFluxXMojo extends AbstractMojo {

    @Override
    public void execute() throws MojoExecutionException {
        getLog().info("EvalFluxX RAG Evaluation Phase executed.");
        performEvaluation();
    }

    private void performEvaluation() {
        // TODO later evaluation logic
    }
}
