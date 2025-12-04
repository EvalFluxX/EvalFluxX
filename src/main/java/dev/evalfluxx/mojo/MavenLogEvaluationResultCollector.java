package dev.evalfluxx.mojo;

import dev.evalfluxx.evaluation.EvaluationRecord;
import dev.evalfluxx.evaluation.EvaluationResultCollector;
import org.apache.maven.plugin.logging.Log;

/**
 * Simple collector that forwards evaluation measurements to the Maven build log.
 */
public class MavenLogEvaluationResultCollector implements EvaluationResultCollector {

    private final Log log;

    public MavenLogEvaluationResultCollector(Log log) {
        this.log = log;
    }

    @Override
    public void record(EvaluationRecord record) {
        log.info("[Evaluation] " + record.runnerName() + "/" + record.evaluationSetName()
                 + " [" + record.configurationName() + " (" + record.configurationId() + ")" + "] - "
                 + record.metricName() + "=" + record.value() + " " + record.attributes());
    }
}
