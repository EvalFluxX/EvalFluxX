package dev.evalfluxx.evaluation.results;

public class TestProcessor implements Processor<String, String, String> {
    @Override
    public String process(String conf, String data) {
        return "Processed(" + conf + "," + data + ")";
    }
}
