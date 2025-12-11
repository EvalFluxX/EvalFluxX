package dev.evalfluxx.evaluation.results;

public interface Processor<C, D, R> {
    R process(C conf, D data);

}
