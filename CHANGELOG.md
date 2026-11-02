# 📝 Changelog

All notable changes to the **Pharma Automation Framework** will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

---

## 📌 Versioning Scheme (`MAJOR.MINOR.PATCH`)

- **`MAJOR` (X.0.0):** Incompatible or breaking architectural changes (e.g., upgrading base Java version, major test runner overhaul, breaking locator abstraction redesign).
- **`MINOR` (0.Y.0):** Backward-compatible additions and feature enhancements (e.g., new Page Objects, new business test flows, new utility modules, new reporting capabilities).
- **`PATCH` (0.0.Z):** Backward-compatible bug fixes, locator updates, synchronization adjustments, or documentation refinements.

---

## [Unreleased]

### Added
- **Plant Type Parameterization & Config Overwriting:**
  - Added `PLANT_TYPE` choice parameter in `Jenkinsfile` with options:
    - `Formulation (F)`
    - `Medical Device (M)`
    - `API/Chemical Plant (A)`
  - Automatic resolution to short codes (`F`, `M`, `A`) and persistence/overwriting directly into `config.properties`.
  - Added `-DplantType` runtime JVM system property flag pass-through to Maven Surefire.
  - Added `ConfigReader.getPlantType()` and bidirectional synchronization between System properties and `config.properties`.
  - Added `Plant Type` parameter display in ExtentReports System/Environment dashboard and Jenkins execution emails.

### Planned
- Parallel suite execution optimization across multiple browser instances.
- Docker containerization for standardized test agent runners.
- Performance metric collectors for ZK ERP page load times.

---

## [1.0.0] - 2026-11-02

### 🎉 Initial Stable Release
Initial official release of the enterprise-grade **Pharma Automation Framework** engineered for testing pharmaceutical ERP web applications built on the ZK Web Framework.

### Added
- **Core Automation Architecture:**
  - Modern browser automation powered by **Playwright Java 1.52.0** and **Java 17+**.
  - Strict **ThreadLocal concurrency isolation** across `Playwright`, `Browser`, `BrowserContext`, `Page`, `ExtentTest`, and `CustomSoftAssert`.
  - Layered **Page Object Model (POM)** pattern decoupling test scenarios from DOM locators and browser lifecycles.

- **Pharmaceutical ERP Business Flow Coverage:**
  - **Standard Operating Procedure (SOP) Life Cycle:**
    - SOP Request and Issue (Controlled and Non-Controlled / Display flows).
    - SOP Format Request and Issue.
    - SOP Annexure Request and Issue.
    - SOP Master creation, revisions, effectiveness checks, approval workflows, report previews, and supersede verification.
  - **Procure-to-Pay (P2P) Lifecycle:**
    - Purchase Order (PO) creation, tax calculation, and completion.
    - Goods Receipt Note (GRN) with Attribute Set Instance (ASI) & Lot creation.
    - Purchase Invoice generation and document verification.
  - **Order-to-Cash (O2C) Lifecycle:**
    - Sales Order (SO) flow with customer selection, line items, and completion.
    - Customer Shipment generation and inventory movement verification.
    - Sales Invoice matching and document status lifecycle.
  - **Shared System Components:**
    - Unified Login with multi-role support (`ClientAndRoleSelection`).
    - ERP Toolbar actions (New, Save, Complete, Print, Report Preview, Zoom, Search).
    - Workflow Activities management and Document Explorer.

- **ZK Framework Synchronization Engine (`WaitUtils`):**
  - Robust handling of dynamic ZK UI elements: asynchronous modal loading overlays (`.z-loading`, `.z-apply-mask`), popup windows, and multi-layered iframe dialogs.
  - Dynamic JavaScript polling (`pollInputValue`, `waitForDocumentReady`) ensuring resilient element stabilization without arbitrary hard sleeps.

- **Custom Soft Assertion System (`CustomSoftAssert`):**
  - Test thread-isolated assertion engine enabling tests to run full workflows without early termination on non-fatal field check failures.
  - Automatic on-failure screenshot capture embedded directly into execution logs and HTML reports.
  - Automatic assertion aggregation during `BaseTest.tearDown()`.

- **Multi-Region & Environment Routing (`DataManager` & `ConfigReader`):**
  - Dynamic routing between `Localization` and `Non-Localization` test data properties.
  - Runtime override via JVM system properties (`-Dregion=...`, `-Dbrowser=...`, `-Dheadless=...`).

- **Reporting & Diagnostics:**
  - **ExtentReports 5.1.1** integration (`ExtentManager`) with custom dark-accented modern styling.
  - Dedicated **Test Class Summary** tracking total classes, passed, failed, skipped, and class pass rate.
  - Last retry deduplication (`keepLastRetryOnly`) to maintain clean execution reports.
  - Layered **Log4j2** logging with console formatting and daily rolling file appenders (`logs/app.log`).

- **Dynamic Test Retry (`RetryAnalyzer` & `RetryListener`):**
  - TestNG `IAnnotationTransformer` auto-wires automatic retry logic to flaky network-sensitive tests without manual test annotations.

- **CI/CD Integration (`Jenkinsfile`):**
  - Production-ready Declarative Jenkins Pipeline with parameterized triggers (`REGION`, `MODULE`, `BROWSER`, `HEADLESS`).
  - Virtual display support via `xvfb-run` for headless and headed browser execution on headless Linux agents.
  - Automated HTML report archiving and rich email notification dispatch.

---

## 🛠️ How to Maintain & Bump Framework Versions

When preparing a new release or update:

1. **Update Maven `pom.xml`:**
   ```xml
   <version>X.Y.Z</version>
   ```

2. **Update Runtime Configuration (`src/test/resources/config.properties`):**
   ```properties
   framework.version=X.Y.Z
   ```

3. **Record Changes in `CHANGELOG.md`:**
   - Move relevant items from `[Unreleased]` into a new section `[X.Y.Z] - YYYY-MM-DD`.
   - Categorize entries under `Added`, `Changed`, `Deprecated`, `Removed`, `Fixed`, or `Security`.

4. **Update Version Badge in `README.md`:**
   ```markdown
   [![Framework Version](https://img.shields.io/badge/Framework%20Version-vX.Y.Z-informational?logo=git)](CHANGELOG.md)
   ```

5. **Commit and Tag Git Release:**
   ```bash
   git add pom.xml src/test/resources/config.properties CHANGELOG.md README.md
   git commit -m "chore(release): bump framework version to vX.Y.Z"
   git tag -a vX.Y.Z -m "Release version vX.Y.Z"
   ```
