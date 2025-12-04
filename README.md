# EvalFluxX

**EvalFluxX** ist ein leichtgewichtiges Maven-Plugin, das eine **neue Evaluationsphase** in den Build-Lifecycle einführt.  
Es ermöglicht flexible Prüfungen, Metriken, Validierungen und projektbezogene Regeln – unabhängig vom bestehenden Maven-Flow.

Ziel ist es, Entwicklern ein offenes und erweiterbares Werkzeug zu bieten, um Build-Prozesse um intelligente Evaluationsschritte zu ergänzen.

---

## 🚀 Ziele

- Bereitstellung einer **neuen Maven-Buildphase** für flexible Evaluationsaufgaben  
- Konfigurierbare **Custom Rules** zur Projektvalidierung  
- Unterstützung von **Metrics** & Qualitätschecks (z. B. Größe, Strukturen, Dependency-Analysen)  
- Klare Erweiterbarkeit für **Plugins, Skripte oder externe Systeme**  
- Minimal-invasiv: EvalFluxX soll bestehende Builds **nicht stören**, sondern erweitern  
- Vollständig **Open Source**, für alle nutzbar, adaptierbar und erweiterbar  

---

## 🧩 Vision

EvalFluxX soll ein Framework werden, das es ermöglicht:

- projektübergreifende Compliance-Regeln zu automatisieren  
- Build-Prozesse smarter zu machen  
- Tools wie Linter, Analyzer, Architekturchecker flexibel einzubinden  
- Maven um eine leicht verständliche, eval-orientierte Phase zu erweitern  
- individuelle Unternehmensregeln deklarativ abzubilden  

---

## 📌 TODO / Backlog

### 🔧 Plugin-Basis
- [ ] Maven-Plugin-Skelett erstellen (mojo + descriptors)
- [ ] Eigene Buildphase registrieren (`eval` oder `evaluate`)
- [ ] Erste Minimalfunktion: einfache Ausgabe + Hook im Lifecycle

### ⚙️ Konfiguration
- [ ] YAML-/JSON- oder XML-basierte Rule-Konfiguration definieren
- [ ] Mehrere Evaluations-Tasks pro Projekt ermöglichen
- [ ] Fail/Warning/Info-Modi für Regeln

### 🔍 Core-Funktionen
- [ ] Rule-Engine definieren (Interface + SPI)
- [ ] Beispiel-Regeln (z. B. Dateigrößencheck, Dependency-Check)
- [ ] Aggregation & Reporting der Ergebnisse

### 🧪 Qualität & Tests
- [ ] Unit-Tests für Plugin-Logik
- [ ] Integrationstests mit Maven Invoker Plugin
- [ ] Beispielprojekt zur Demonstration

### 📦 Dokumentation
- [ ] README erweitern (Installation, Konfiguration, Beispiele)
- [ ] Wiki für komplexere Setups
- [ ] API-Dokumentation

### 🌐 Open Source
- [ ] Contribution Guide erstellen
- [ ] Issue Templates & PR Templates einrichten

---

## 🤝 Contributing

EvalFluxX ist offen für alle Ideen, Vorschläge und Erweiterungen.  
Pull Requests, Diskussionen und Issues sind ausdrücklich willkommen!

---

## 📄 Lizenz

MIT License – frei nutzbar, erweiterbar und einsetzbar, auch kommerziell.

