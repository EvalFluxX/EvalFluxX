package dev.evalfluxx.evaluation;

/**
 * Exception that signals problems during evaluation execution.
 */
public class EvaluationException extends Exception {

    public EvaluationException(String message) {
        super(message);
    }

    public EvaluationException(String message, Throwable cause) {
        super(message, cause);
    }
}
