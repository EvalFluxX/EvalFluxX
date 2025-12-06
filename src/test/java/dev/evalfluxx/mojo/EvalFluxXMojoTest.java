package dev.evalfluxx.mojo;

import org.apache.maven.plugin.MojoExecutionException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class EvalFluxXMojoTest {

    @TempDir
    Path tempDir;

    @Test
    void createsWorkDirectoryWhenMissing() throws MojoExecutionException {
        Path workDir = tempDir.resolve("evalfluxx");
        TestEvalMojo mojo = new TestEvalMojo();
        mojo.setWorkDirectory(workDir.toFile());

        mojo.initializeWorkDirectory();

        assertTrue(Files.isDirectory(workDir), "Work directory should be created when missing");
    }

    @Test
    void leavesExistingWorkDirectoryUntouched() throws Exception {
        Path workDir = tempDir.resolve("existing-evalfluxx");
        Files.createDirectories(workDir);
        TestEvalMojo mojo = new TestEvalMojo();
        mojo.setWorkDirectory(workDir.toFile());

        mojo.initializeWorkDirectory();

        assertTrue(Files.isDirectory(workDir), "Existing work directory should remain available");
    }

    @Test
    void executesWithWorkDirectoryInitialization() throws Exception {
        Path workDir = tempDir.resolve("run-evalfluxx");
        TestEvalMojo mojo = new TestEvalMojo();
        mojo.setWorkDirectory(workDir.toFile());
        mojo.setWithEvals(true);

        mojo.execute();

        assertTrue(Files.isDirectory(workDir), "Work directory should be created during eval execution");
        assertTrue(mojo.initialized, "Initialization should be invoked during execution");
        assertTrue(mojo.performedEvaluation, "Evaluation should run after initialization");
    }

    private static class TestEvalMojo extends EvalFluxXMojo {
        private boolean performedEvaluation;
        private boolean initialized;

        @Override
        protected void performEvaluation() {
            this.performedEvaluation = true;
        }

        @Override
        void initializeWorkDirectory() throws MojoExecutionException {
            super.initializeWorkDirectory();
            this.initialized = true;
        }
    }
}
