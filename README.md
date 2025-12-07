# EvalFluxX Maven Plugin

EvalFluxX definiert eine neue Maven-Lifecycle-Phase `rag-evaluation` und ordnet sie dem Plugin-Goal `evalfluxx:eval` zu.
Projekte, die das Plugin als Extension einbinden, können so Evaluierungsprozesse direkt im Build-Lifecycle ausführen.

## Voraussetzungen
- Java 21
- Maven 3.9+

## Build
Installiere das Plugin lokal:

```bash
mvn clean install
```

## Verwendung
Binde das Plugin als Extension in einem Projekt ein:

```xml

<properties>
    <evalfluxx.version>1.0.0</evalfluxx.version>
</properties>
<dependencies>
    <dependency>
        <groupId>dev.evalfluxx</groupId>
        <artifactId>evalfluxx</artifactId>
        <version>${evalfluxx.version}</version>
    </dependency>
</dependencies>
<build>
  <extensions>
    <extension>
      <groupId>dev.evalfluxx</groupId>
      <artifactId>evalfluxx</artifactId>
      <version>${evalfluxx.version}</version>
    </extension>
  </extensions>
  <plugins>
    <plugin>
      <groupId>dev.evalfluxx</groupId>
      <artifactId>evalfluxx</artifactId>
      <version>${evalfluxx.version}</version>
      <executions>
          <execution>
              <goals>
                  <goal>eval</goal>
              </goals>
          </execution>
      </executions>
    </plugin>
  </plugins>
</build>
```

Auslösen der Phase direkt:

```bash
mvn evalfluxx:eval
```

Als Teil des Standard-Lifecycles (z. B. `verify`) kann die RAG-Evaluation optional aktiviert werden:

```bash
mvn verify -DwithEvals
```

Ohne das Property `-DwithEvals` wird während `verify` keine Evaluation gestartet. Das Goal `evalfluxx:eval` wird immer
ausgeführt, wenn es explizit aufgerufen wird, unabhängig von gesetzten Properties.

### Beispiel: Eigener `Evaluator` mit `@Evaluation`

Erweitere den `Evaluator` und markiere Evaluationsmethoden mit `@Evaluation`:

```java
import dev.evalfluxx.evaluation.*;
import java.time.Instant;
import java.util.Map;

public class GreetingEvaluator extends Evaluator {

    @Evaluation("greeting-check")
    public void evaluateGreeting(EvaluationContext context) {
        String expectedGreeting = (String) context.configuration()
                .settings()
                .getOrDefault("expectedGreeting", "Hello, EvalFluxX!");

        boolean greetingPresent = expectedGreeting.startsWith("Hello");

        context.resultCollector().record(new EvaluationRecord(
                getClass().getSimpleName(),
                "greeting-check",
                "greeting-present",
                greetingPresent,
                context.configuration(),
                Instant.now(),
                Map.of("source", "integration-test", "greeting", expectedGreeting))
        );
    }
}
```

Lege den Evaluator z. B. unter `src/evaluation/java` ab. Bei `mvn evalfluxx:eval` oder innerhalb
von `mvn verify -DwithEvals` wird er über den `ServiceLoader` automatisch entdeckt und ausgeführt.
