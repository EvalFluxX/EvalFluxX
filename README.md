# EvalFluxX Maven Plugin

EvalFluxX definiert eine neue Maven-Lifecycle-Phase `rag-evaluation` und ordnet sie dem Plugin-Goal `evalfluxx:run` zu.
Projekte, die das Plugin als Extension einbinden, können so Evaluierungsprozesse direkt im Build-Lifecycle ausführen.

## Projektstruktur
```
evalfluxx/
    pom.xml
    src/main/java/dev/evalfluxx/mojo/EvalFluxXMojo.java
    src/main/resources/META-INF/maven/lifecycle.xml
    README.md
```

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
<build>
  <extensions>
    <extension>
      <groupId>dev.evalfluxx</groupId>
      <artifactId>evalfluxx</artifactId>
      <version>1.0.0</version>
    </extension>
  </extensions>
  <plugins>
    <plugin>
      <groupId>dev.evalfluxx</groupId>
      <artifactId>evalfluxx</artifactId>
      <version>1.0.0</version>
    </plugin>
  </plugins>
</build>
```

Auslösen der Phase direkt:

```bash
mvn rag-evaluation
```

Als Teil des Standard-Lifecycles (z. B. `verify`):

```bash
mvn verify
```

Das Goal `evalfluxx:run` wird in beiden Fällen im Rahmen der neuen Phase `rag-evaluation` ausgeführt.
