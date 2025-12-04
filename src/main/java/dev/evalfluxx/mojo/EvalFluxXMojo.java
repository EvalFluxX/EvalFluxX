package dev.evalfluxx.mojo;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

@Mojo(name = "run", defaultPhase = LifecyclePhase.VERIFY)
public class EvalFluxXMojo extends AbstractMojo {

    @Override
    public void execute() {
        getLog().info("EvalFluxX executed.");
        evaluate();
    }

    private void evaluate() {
        // TODO: future evaluation implementation
    }
}
