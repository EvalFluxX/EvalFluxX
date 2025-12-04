# EvalFluxX Maven Plugin

EvalFluxX ist ein minimaler Startpunkt für zukünftige Evaluierungs-Features in Maven-Builds. Das Plugin stellt ein einziges Goal `run` bereit, das im Standardfall in der `verify`-Phase ausgeführt wird und derzeit eine einfache Konsolenausgabe erzeugt.

## Projektstruktur
```
evalfluxx/
├─ pom.xml
├─ src/main/java/dev/evalfluxx/mojo/EvalFluxXMojo.java
└─ src/main/resources/META-INF/maven/ (wird automatisch generiert)
```

## Voraussetzungen
- Java 21
- Maven 3.9+

## Build
Baue und installiere das Plugin lokal:

```bash
mvn clean install
```

## Verwendung
Führe das Goal `run` in einem beliebigen Maven-Projekt aus:

```bash
mvn dev.evalfluxx:evalfluxx:1.0.0:run
```

Die Ausführung gibt aktuell eine kurze Meldung im Build-Log aus und dient als Erweiterungspunkt für künftige Evaluierungsschritte.
