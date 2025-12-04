package dev.evalfluxx.evaluation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a method as an evaluation entry point that should be executed by the default runner.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Evaluation {

    /**
     * Optional custom name for the evaluation. If empty, the method name is used instead.
     */
    String value() default "";
}
