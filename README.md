# 🏥 Pharma Automation — Technical Framework Documentation

[![Java Version](https://img.shields.io/badge/Java-17%2B-orange?logo=openjdk)](https://openjdk.org/)
[![Playwright Version](https://img.shields.io/badge/Playwright-1.52.0-green?logo=playwright)](https://playwright.dev/java/)
[![TestNG Version](https://img.shields.io/badge/TestNG-7.10.2-red?logo=testng)](https://testng.org/)
[![ExtentReports](https://img.shields.io/badge/ExtentReports-5.1.1-blue)](https://extentreports.com/)
[![Log4j2](https://img.shields.io/badge/Log4j2-2.20.0-critical)](https://logging.apache.org/log4j/2.x/)
[![Build Tool](https://img.shields.io/badge/Maven-3.8%2B-C71A36?logo=apachemaven)](https://maven.apache.org/)
[![CI/CD](https://img.shields.io/badge/Jenkins-Declarative%20Pipeline-blue?logo=jenkins)](https://www.jenkins.io/)

> **Audience:** SDETs, QA Automation Engineers, Backend/Frontend Developers, DevOps, and Technical Leads.  
> *For the non-technical / business overview guide, see [README_NON_TECHNICAL.md](file:///home/mahamadgaus/Documents/Pharma_Automation/com.pharma.automation/README_NON_TECHNICAL.md).*

---

## 📑 Table of Contents

1. [Architecture Overview](#-1-architecture-overview)
2. [Technology Stack & Dependencies](#-2-technology-stack--dependencies)
3. [Repository Directory Structure](#-3-repository-directory-structure)
4. [Design Patterns & Engineering Highlights](#-4-design-patterns--engineering-highlights)
   - [ThreadLocal State Isolation](#41-threadlocal-concurrency-isolation)
   - [Page Object Model (POM) Design](#42-page-object-model-pom)
   - [Custom Soft Assertion Engine](#43-custom-soft-assertion-engine-customsoftassert)
   - [Dynamic Retry Analyzer & TestNG Transformer](#44-dynamic-retry-analyzer--annotation-transformer)
   - [Enterprise ZK Framework Synchronization](#45-enterprise-zk-ui-synchronization-waitutils)
   - [Dynamic Multi-Region Test Data Routing](#46-dynamic-multi-region-test-data-routing)
5. [Prerequisites & Environment Setup](#-5-prerequisites--environment-setup)
6. [Configuration Management](#-6-configuration-management)
7. [Test Execution & CLI Reference](#-7-test-execution--cli-reference)
8. [CI/CD Integration (Jenkins Pipeline)](#-8-cicd-integration-jenkins-pipeline)
9. [Reporting & Diagnostic Artifacts](#-9-reporting--diagnostic-artifacts)
10. [Developer Guide: Extending the Framework](#-10-developer-guide-extending-the-framework)
11. [Troubleshooting & Debugging](#-11-troubleshooting--debugging)

---

## 🏛️ 1. Architecture Overview

This framework is an enterprise-grade automated testing solution engineered for a complex **Pharmaceutical ERP** system built on the **ZK Web Framework** (rich RIA with server-driven desktop UI, asynchronous loading overlays, dynamic modal windows, and multi-tier transactional forms).

The framework implements a layered **Page Object Model (POM)** with **ThreadLocal isolation** to decouple test scenario logic from locator maintenance, browser process lifecycles, assertion management, and reporting sinks.

### High-Level Architecture Flow

```
┌────────────────────────────────────────────────────────────────────────────────────────┐
│                                Execution Triggers                                      │
│         CLI: `mvn clean test`         │         Jenkins Declarative Pipeline           │
└───────────────────────────────────────┬────────────────────────────────────────────────┘
                                        │
                                        ▼
┌────────────────────────────────────────────────────────────────────────────────────────┐
│                              Suite Controller (testng.xml)                             │
│   • Listener: utils.MyListener (Extent logging & screenshot attachment)                │
│   • Listener: utils.RetryListener (IAnnotationTransformer dynamic retry wiring)        │
│   • Group Filtering: -Dgroups=basic,advance                                            │
└───────────────────────────────────────┬────────────────────────────────────────────────┘
                                        │
                                        ▼
┌────────────────────────────────────────────────────────────────────────────────────────┐
│                             Base Engine: base.BaseTest                                 │
│   • ThreadLocal<Playwright>, ThreadLocal<Browser>, ThreadLocal<BrowserContext>         │
│   • ThreadLocal<Page>, ThreadLocal<CustomSoftAssert>                                   │
│   • @BeforeSuite: ExtentReports init | @BeforeMethod: Browser setup & Page init        │
│   • @AfterMethod: Soft assertion flush, failure screenshot capture, teardown           │
│   • @AfterSuite: ExtentReports flush                                                   │
└──────────────┬─────────────────────────────────────────────────────────┬───────────────┘
               │                                                         │
               ▼                                                         ▼
┌──────────────────────────────┐                         ┌───────────────────────────────┐
│     Page Object Model        │                         │      Utilities & Services     │
│       (`pages.*`)            │                         │          (`utils.*`)          │
│ • LoginPage                  │                         │ • ConfigReader (CLI override) │
│ • ClientAndRoleSelection     │◄───────────────────────►│ • DataManager (Region router) │
│ • PurchaseOrder              │                         │ • WaitUtils (ZK loader sync)  │
│ • GoodsReceiptNote           │                         │ • CustomSoftAssert & Failed   │
│ • AttributeSetInstance       │                         │ • ExtentManager (ThreadLocal) │
│ • Toolbar & Dashboard        │                         │ • RetryAnalyzer (Max 2 retry) │
└──────────────┬───────────────┘                         └───────────────────────────────┘
               │
               ▼
┌────────────────────────────────────────────────────────────────────────────────────────┐
│                           Playwright Browser Engine (CDP)                              │
│   • Chromium / Google Chrome / Mozilla Firefox                                         │
│   • Viewport: 1920x1080 (Headless) / Native Maximize (Headed)                          │
│   • HTTPS error bypass (self-signed cert support)                                      │
└───────────────────────────────────────┬────────────────────────────────────────────────┘
                                        │
                                        ▼
┌────────────────────────────────────────────────────────────────────────────────────────┐
│                               Outputs & Artifacts                                      │
│ • HTML Report: test-output/ExtentReport.html (embedded Base64 screenshots)            │
│ • Failure Screenshots: screenshots/<TestName>_<timestamp>.png                          │
│ • Surefire XML Reports: target/surefire-reports/*.xml                                  │
│ • Execution Logs: logs/test.log (Log4j2 Console + Rolling File)                        │
└────────────────────────────────────────────────────────────────────────────────────────┘
```

---

## 📦 2. Technology Stack & Dependencies

The project is built on **Java 17 LTS** and orchestrated via **Apache Maven**. Key libraries declared in [pom.xml](file:///home/mahamadgaus/Documents/Pharma_Automation/com.pharma.automation/pom.xml):

| Group / Artifact | Version | Purpose |
| :--- | :--- | :--- |
| `com.microsoft.playwright:playwright` | `1.52.0` | Browser automation engine via Chrome DevTools Protocol (CDP) & Playwright driver. |
| `org.testng:testng` | `7.10.2` | Test execution, annotations (`@Test`, `@BeforeMethod`, etc.), group filtering, and listener hooks. |
| `com.aventstack:extentreports` | `5.1.1` | Rich interactive HTML dashboard reporting with Spark Reporter. |
| `org.apache.logging.log4j:log4j-api` & `core` | `2.20.0` | Asynchronous thread-safe logging framework writing to console and disk. |
| `org.apache.poi:poi-ooxml` | `5.2.5` | Office Open XML Excel data parsing support for data-driven expansions. |
| `org.apache.maven.plugins:maven-compiler-plugin` | `3.13.0` | JDK source and target level configuration (Java 17). |
| `org.apache.maven.plugins:maven-surefire-plugin` | `3.2.5` | Surefire runner binding `testng.xml` to `mvn test`. |

---

## 📂 3. Repository Directory Structure

```text
com.pharma.automation/
├── pom.xml                                    # Maven configuration & dependency declarations
├── testng.xml                                 # Suite definition, listener registry, and package scan
├── Jenkinsfile                                # Declarative CI/CD pipeline script
├── README.md                                  # Technical framework documentation (this file)
├── README_NON_TECHNICAL.md                    # Business / non-technical overview guide
│
├── logs/
│   └── test.log                               # Consolidated Log4j2 execution logs
├── screenshots/                               # Physical PNG screenshots captured on test failures
├── test-output/
│   └── ExtentReport.html                      # Interactive HTML test execution report
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   ├── base/
│   │   │   │   └── BaseTest.java              # Core engine: ThreadLocal Playwright lifecycle & hooks
│   │   │   ├── pages/                         # Page Object Model classes
│   │   │   │   ├── AttributeSetInstance.java  # Batch, MFG/EXP dates, lot traceability, costing
│   │   │   │   ├── ClientAndRoleSelection.java# Organization, tenant, and role selection dialogs
│   │   │   │   ├── Dashboard.java             # Post-login navigation and dashboard verification
│   │   │   │   ├── GoodsReceiptNote.java      # GRN header, lines, PO linking, posting verification
│   │   │   │   ├── HTMLReportsPreview.java    # Embedded preview/print modal validation
│   │   │   │   ├── LoginPage.java             # Authentication dialog, credentials, role triggers
│   │   │   │   ├── PurchaseOrder.java         # PO creation, line entry, doc actions (Prepare/Complete)
│   │   │   │   ├── SOPMaster.java             # Standard Operating Procedure master interactions
│   │   │   │   ├── Toolbar.java               # Standard ERP toolbar (New, Save, DocAction, Grid, Close)
│   │   │   │   └── WorkflowActivities.java    # Workflow approvals and notice management
│   │   │   └── utils/                         # Reusable framework utilities
│   │   │       ├── ConfigReader.java          # Multi-source config loader with CLI override priority
│   │   │       ├── CustomSoftAssert.java      # Enhanced soft assertions with instant screenshot & HTML cards
│   │   │       ├── DataManager.java           # Localization/Non-localization test data router
│   │   │       ├── ExtentManager.java         # Singleton ExtentReports & ThreadLocal ExtentTest manager
│   │   │       ├── FailedAssertion.java       # Model representing an individual assertion mismatch
│   │   │       ├── MyListener.java            # TestNG ITestListener implementation
│   │   │       ├── RetryAnalyzer.java         # IRetryAnalyzer implementation (max 2 attempts)
│   │   │       ├── RetryListener.java         # IAnnotationTransformer for dynamic retry injection
│   │   │       └── WaitUtils.java             # ZK framework async loading indicator synchronization
│   │   └── resources/
│   │
│   └── test/
│       ├── java/
│       │   ├── baseModules/
│       │   │   └── CreateGRNFromPOFlow.java   # End-to-end integration test: PO -> Approval -> GRN -> Accounting
│       │   └── loginTest/
│       │       ├── CustomSoftAssertTest.java  # Unit/regression tests for CustomSoftAssert engine
│       │       └── LoginWithEveryRole.java    # Data-driven role access test suite
│       └── resources/
│           ├── config.properties              # Base configuration (browser, URL, headless flag)
│           ├── log4j2.xml                     # Log4j2 configuration (Console & File appenders)
│           ├── loginCredentials.properties    # Role credentials (Superuser, Initiator, SOP roles)
│           └── testdata/
│               ├── localization.properties    # Domestic / Indian localization dataset (INR, SGST/CGST)
│               └── non-localization.properties# Export / International dataset (USD, W/O GST)
```

---

## 🧠 4. Design Patterns & Engineering Highlights

### 4.1. ThreadLocal Concurrency Isolation

To guarantee thread safety for future or current parallel executions and prevent cross-test contamination, all mutable browser and reporting state in [`BaseTest.java`](file:///home/mahamadgaus/Documents/Pharma_Automation/com.pharma.automation/src/main/java/base/BaseTest.java) is wrapped in `ThreadLocal`:

```java
private static final ThreadLocal<Playwright> playwrightTL = new ThreadLocal<>();
private static final ThreadLocal<Browser> browserTL = new ThreadLocal<>();
private static final ThreadLocal<BrowserContext> contextTL = new ThreadLocal<>();
private static final ThreadLocal<Page> pageTL = new ThreadLocal<>();
private static final ThreadLocal<CustomSoftAssert> softAssertTL = new ThreadLocal<>();
private static final ThreadLocal<Boolean> failureScreenshotCapturedTL = ThreadLocal.withInitial(() -> false);
```

**Lifecycle Rules:**
- **`@BeforeMethod`:** Instantiates a fresh `Playwright`, `BrowserContext` (with `setIgnoreHTTPSErrors(true)` and viewport handling), `Page`, and a new `CustomSoftAssert`.
- **`@AfterMethod`:**
  1. Checks for soft assertion failures; logs each failed assertion card to `ExtentTest`.
  2. If hard failure occurred, logs diagnostic card and captures full-page screenshot.
  3. Closes `BrowserContext`, `Browser`, and `Playwright` instances safely inside a `finally` block.
  4. Removes all `ThreadLocal` entries via `.remove()` to prevent memory leaks.
  5. Executes `sa.assertAll()` to guarantee TestNG marks the test failed with complete root-cause logs.

---

### 4.2. Page Object Model (POM)

Each ERP screen is encapsulated in its own class under `pages.*`:
- **Encapsulated Locators:** Private `Locator` members defined using robust XPath and CSS selectors tailored for ZK elements (`instancename`, `aria-label`, button classes).
- **Domain-Specific Actions:** High-level methods (`purchaseOrder.selectBPartner()`, `goodsReceiptNote.attachPO(poDocNo)`, `toolbar.performDocAction("Complete")`).
- **Chain of Custody:** Methods accept and pass domain IDs (e.g., generated PO Document Number is passed into GRN attachment and lines generation).

---

### 4.3. Custom Soft Assertion Engine (`CustomSoftAssert`)

Unlike standard TestNG `SoftAssert` which only logs mismatches at the end of the test without visual context, [`CustomSoftAssert`](file:///home/mahamadgaus/Documents/Pharma_Automation/com.pharma.automation/src/main/java/utils/CustomSoftAssert.java) provides:

1. **Instant Screenshot Capture:** Captures a full-page screenshot at the **exact millisecond** an assertion fails, while the browser DOM is still in the error state.
2. **Stack-Trace Extraction:** Inspects the runtime call stack and extracts the exact calling file, line number, and method from test code, ignoring TestNG/reflection frames:
   ```java
   String location = extractAssertionLocation(); // e.g. "CreateGRNFromPOFlow.java:134"
   ```
3. **HTML Card Generation:** Directly injects structured HTML cards into the ExtentReport:
   ```html
   <div style='background-color:#fff5f5; border-left:4px solid #e53e3e; ...'>
     <b>Assertion Failure #1:</b> [PO] Total Net Amount mismatch<br/>
     <b>Expected:</b> [150.00] | <b>Actual:</b> [135.00]<br/>
     <b>Location:</b> CreateGRNFromPOFlow.java:68
   </div>
   ```
4. **Clean Assertions:** Successful assertions do not clutter the ExtentReport (suppressing false noise), but log at `DEBUG` level in `logs/test.log`.
5. **Consolidated Summary:** At teardown, attaches a high-level summary table listing all mismatches before invoking `assertAll()`.

---

### 4.4. Dynamic Retry Analyzer & Annotation Transformer

The framework implements an automatic retry mechanism for transient network hiccups:

- **[`RetryAnalyzer.java`](file:///home/mahamadgaus/Documents/Pharma_Automation/com.pharma.automation/src/main/java/utils/RetryAnalyzer.java):** Allows up to **2 retries** (`MAX_RETRY_COUNT = 2`). Marks test attributes with `RETRY_IN_PROGRESS` and the current attempt count.
- **[`RetryListener.java`](file:///home/mahamadgaus/Documents/Pharma_Automation/com.pharma.automation/src/main/java/utils/RetryListener.java):** Implements `IAnnotationTransformer`. Automatically attaches `RetryAnalyzer` to **every** `@Test` in the suite during test compilation, removing the need to manually declare `@Test(retryAnalyzer = ...)` across hundreds of test methods.
- **Deduplication in [`MyListener.java`](file:///home/mahamadgaus/Documents/Pharma_Automation/com.pharma.automation/src/main/java/utils/MyListener.java):** When a test is being retried, `onTestSkipped` discards the failed intermediate node from ExtentReports (`ExtentManager.removeTest(test)`), preventing confusing duplicate test entries in executive reports.

---

### 4.5. Enterprise ZK UI Synchronization (`WaitUtils`)

Pharmaceutical ERP systems using ZK Framework render modal loading glass panes and AJAX indicators (`.z-loading-indicator` and `.busy-dialog-box`). Traditional explicit waits on buttons often fail because the DOM element is present but blocked by a transparent loading overlay.

[`WaitUtils.java`](file:///home/mahamadgaus/Documents/Pharma_Automation/com.pharma.automation/src/main/java/utils/WaitUtils.java) provides:
- `waitForLoadingIndicatorToDisappear(page)`: Probes for `.z-loading-indicator` visibility within 1000ms, and if detected, waits up to 30 seconds for state `HIDDEN`.
- `waitForBigLoadingIndicatorToDisappear(page)`: Handles large blocking busy dialogs (`.busy-dialog-box`) during complex database transactions (e.g., document completion, posting).

---

### 4.6. Dynamic Multi-Region Test Data Routing

[`DataManager.java`](file:///home/mahamadgaus/Documents/Pharma_Automation/com.pharma.automation/src/main/java/utils/DataManager.java) allows switching between domestic Indian tax/currency setups and international/export setups without touching Java code:

- Supports parameter `-Dregion=localization` (maps to `src/test/resources/testdata/localization.properties`).
- Supports parameter `-Dregion=non-localization` (maps to `src/test/resources/testdata/non-localization.properties`).
- Backward-compatible aliases: `india`, `local` → `localization`; `abroad`, `international`, `non_localization` → `non-localization`.
- Fallback safeguard: If a specified file does not exist, safely defaults to `localization.properties` with a logged warning.

---

## 💻 5. Prerequisites & Environment Setup

### 5.1. System Requirements
- **Operating System:** Linux (Ubuntu 20.04+, Debian 11+, RHEL 8+), macOS (Apple Silicon / Intel), or Windows 10/11.
- **Java Development Kit:** JDK 17 (OpenJDK or Oracle JDK).
- **Build Tool:** Apache Maven 3.8.0 or newer.
- **Display Server (Linux CI only):** Xvfb (only required if running headed mode on headless Linux hosts).

### 5.2. Installation Steps

1. **Verify Java 17 and Maven:**
   ```bash
   java -version
   # Expected: openjdk version "17.x.x"

   mvn -version
   # Expected: Apache Maven 3.8.x or 3.9.x
   ```

2. **Clone the Repository:**
   ```bash
   git clone <repo-url> com.pharma.automation
   cd com.pharma.automation
   ```

3. **Install Playwright Browser Binaries:**
   Playwright for Java will download its managed browser binaries automatically on the first test run. To pre-install them explicitly:
   ```bash
   mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install --with-deps"
   ```

4. **Verify Compilation:**
   ```bash
   mvn clean test-compile
   ```

---

## ⚙️ 6. Configuration Management

The framework utilizes a hierarchical configuration model where **CLI system properties (`-D`) always take precedence over static property files**.

### Property Precedence Order:
1. Maven CLI Argument: `-D<key>=<value>`
2. Region Test Data: `src/test/resources/testdata/<region>.properties`
3. Credentials: `src/test/resources/loginCredentials.properties`
4. Global Config: `src/test/resources/config.properties`

### 6.1. Global Configuration ([config.properties](file:///home/mahamadgaus/Documents/Pharma_Automation/com.pharma.automation/src/test/resources/config.properties))
```properties
browser=chrome               # Options: chrome, chromium, firefox
url=https://192.168.2.65:8443/# Target ERP application endpoint
headless=false               # Default local run mode: false (headed), true (headless)
client=PharmaVerge ERP       # Default ERP tenant/client name
```

### 6.2. Credentials Configuration ([loginCredentials.properties](file:///home/mahamadgaus/Documents/Pharma_Automation/com.pharma.automation/src/test/resources/loginCredentials.properties))
Defines user logins and roles across the pharmaceutical workflow hierarchy:
```properties
Superuser=SuperUser
Initiator=abhishek initiator
SuperuserRole=Admin
SOPInitiator=abhishek initiator
SOPInitiatorRole=Engineer
SOPInternalReviewer=gajanan supervisor
SOPInternalReviewerRole=Engineer
SOPDepartmentHead=Aashish Patil
SOPDepartmentHeadRole=Engineer
SOPQAReviewer=mahamad qa
SOPQAReviewerRole=QA
SOPQAManager=deepak qa
SOPQAManagerRole=QA Manager
Password=System
```

### 6.3. Test Data Profiles
- **[`localization.properties`](file:///home/mahamadgaus/Documents/Pharma_Automation/com.pharma.automation/src/test/resources/testdata/localization.properties):** Domestic vendor (`ENIVID LIFE SCIENCES`), location `Ahmedabad`, currency `INR`, taxes `SGST 9% + CGST 9%`.
- **[`non-localization.properties`](file:///home/mahamadgaus/Documents/Pharma_Automation/com.pharma.automation/src/test/resources/testdata/non-localization.properties):** International vendor (`VIRDEV INTERNATIONAL USA`), location `USA`, currency `USD`, taxes `W/O GST`.

---

## 🚀 7. Test Execution & CLI Reference

### 7.1. Standard Executions

* **Execute Default Suite (Runs all test packages in `testng.xml`):**
  ```bash
  mvn clean test
  ```

* **Execute in Headless Mode (Standard for CI/CD):**
  ```bash
  mvn clean test -Dheadless=true
  ```

* **Execute in Headed Mode (Visual debugging):**
  ```bash
  mvn clean test -Dheadless=false
  ```

### 7.2. Test Group Filtering

Groups are mapped in test annotations (e.g. `@Test(groups = {"basic"})`):

* **Run Basic / Smoke Module:**
  ```bash
  mvn clean test -Dgroups=basic
  ```

* **Run Advanced Module:**
  ```bash
  mvn clean test -Dgroups=advance
  ```

* **Run Both Modules:**
  ```bash
  mvn clean test -Dgroups=basic,advance
  ```

### 7.3. Browser & Regional Matrix

* **Run on Firefox in Headless Mode with International Data:**
  ```bash
  mvn clean test -Dbrowser=firefox -Dheadless=true -Dregion=non-localization
  ```

* **Run on Chrome with Indian Domestic Data:**
  ```bash
  mvn clean test -Dbrowser=chrome -Dheadless=true -Dregion=localization
  ```

### 7.4. Single Class / Single Method Execution

* **Run a specific test class:**
  ```bash
  mvn clean test -Dtest=CreateGRNFromPOFlow
  ```

* **Run a single test method:**
  ```bash
  mvn clean test -Dtest=CreateGRNFromPOFlow#verificationOfCreateGRNFromPOFlow
  ```

* **Target custom environment endpoint on the fly:**
  ```bash
  mvn clean test -Durl=https://staging-pharma.internal:8443/ -Dheadless=true
  ```

---

## 🔄 8. CI/CD Integration (Jenkins Pipeline)

The framework includes a fully productionized Jenkins pipeline script ([`Jenkinsfile`](file:///home/mahamadgaus/Documents/Pharma_Automation/com.pharma.automation/Jenkinsfile)) supporting parameterized execution, Xvfb virtual frame buffering, artifact archival, and HTML report notifications.

### 8.1. Pipeline Parameters

| Parameter | Type | Options / Default | Description |
| :--- | :--- | :--- | :--- |
| `REGION` | Choice | `Localization`, `Non-Localization` | Determines the test data property set. |
| `MODULE` | Choice | `Basic`, `Advance`, `Both` | Maps to TestNG `-Dgroups`. |
| `BROWSER` | Choice | `Chrome`, `Firefox` | Target browser engine. |
| `HEADLESS` | Boolean | `true` (Default) | Headless browser execution. |

### 8.2. Key Pipeline Highlights

1. **Virtual Display Support (Xvfb):**  
   If a user requests `HEADLESS=false` on a headless Linux build agent, the pipeline automatically intercepts the call and wraps execution using `xvfb-run --server-args="-screen 0 1920x1080x24"`, preventing display initialization failures.
2. **HTML Report Publishing:**  
   The `publishHTML` step exposes `test-output/ExtentReport.html` as the official "Playwright Extent Report" directly in the Jenkins left navigation sidebar.
3. **Surefire JUnit Results:**  
   Publishes `**/target/surefire-reports/*.xml` to display test health trends and failure graphs.
4. **Artifact Archiving:**  
   Preserves `logs/**` and `test-output/**` for 20 builds.
5. **Rich Email Notification:**  
   Dispatches an executive HTML email via `emailext` with color-coded status banners, execution metadata, and direct build links.

---

## 📊 9. Reporting & Diagnostic Artifacts

### 9.1. Interactive Extent Report (`test-output/ExtentReport.html`)
- Built using **ExtentReports 5 (Spark Reporter)**.
- **Embedded Screenshots:** Failed steps contain embedded Base64 screenshots with thumbnail previews that open in high-resolution modal tabs upon clicking.
- **Detailed Assertion Diagnostics:** Differentiates between soft and hard assertions with highlighted code blocks detailing *Expected*, *Actual*, and file:line locations.
- **Execution Metadata:** Automatically captures OS, Java version, Jenkins Build ID, Triggered User, Browser, and Active Region.

### 9.2. Physical Screenshots (`screenshots/`)
Every failure stores a full-page PNG file named:
```text
screenshots/<TestMethodName>_<timestamp>.png
```

### 9.3. Logging (`logs/test.log`)
Managed via Log4j2 ([`log4j2.xml`](file:///home/mahamadgaus/Documents/Pharma_Automation/com.pharma.automation/src/test/resources/log4j2.xml)).
- Playwright and TestNG internal debug spam is suppressed to `ERROR` level.
- Framework business steps, document numbers, and assertions log with timestamps and thread IDs:
  ```text
  11:29:40 [main] INFO  baseModules.CreateGRNFromPOFlow - Step 2: Navigating to Purchase Order window...
  11:29:45 [main] INFO  pages.PurchaseOrder - Purchase Order created with Doc No: [1000234]
  ```

---

## 🛠️ 10. Developer Guide: Extending the Framework

### 10.1. Creating a New Page Object

1. Place new classes under `src/main/java/pages/`.
2. Store the `Page` instance and initialize `Locator` objects inside the constructor.
3. Use `WaitUtils` for synchronization on ZK async actions.

```java
package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import utils.WaitUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SampleModulePage {

    private static final Logger log = LogManager.getLogger(SampleModulePage.class);
    private final Page page;

    // Locators
    private final Locator sampleInput;
    private final Locator submitButton;

    public SampleModulePage(Page page) {
        this.page = page;
        this.sampleInput = page.locator("//input[@instancename='SampleField']");
        this.submitButton = page.locator("//button[@title='Submit']");
    }

    public void enterSampleData(String value) {
        WaitUtils.waitForLoadingIndicatorToDisappear(page);
        sampleInput.fill(value);
        log.info("Entered sample data: {}", value);
    }
}
```

### 10.2. Writing a New Test Scenario

1. Place test classes under `src/test/java/baseModules/` (or a dedicated module package).
2. Extend [`BaseTest`](file:///home/mahamadgaus/Documents/Pharma_Automation/com.pharma.automation/src/main/java/base/BaseTest.java).
3. Annotate methods with `@Test(groups = {"basic"})` or `groups = {"advance"}`.
4. Use `BaseTest.getSoftAssert()` for non-fatal validations.

```java
package baseModules;

import org.testng.annotations.Test;
import base.BaseTest;
import utils.ConfigReader;
import utils.CustomSoftAssert;

public class SampleTestFlow extends BaseTest {

    @Test(groups = {"basic"}, description = "Verify sample business flow")
    public void verifySampleFlow() {
        // Step 1: Login
        loginPage.login(ConfigReader.get("Initiator"), ConfigReader.get("Password"));
        clientAndRole.selectClientAndRole(ConfigReader.get("client"), ConfigReader.get("SuperuserRole"));

        // Step 2: Custom assertion
        CustomSoftAssert sa = BaseTest.getSoftAssert();
        sa.assertEquals(toolbar.getDocStatus(), "Drafted", "Document status should start as Drafted");

        // The assertion will be checked & screenshotted on mismatch,
        // and finalized automatically during BaseTest.tearDown()!
    }
}
```

---

## 🔧 11. Troubleshooting & Debugging

| Issue | Root Cause | Solution |
| :--- | :--- | :--- |
| **`SEC_ERROR_UNKNOWN_ISSUER` or SSL Handshake Failure** | Self-signed or intranet SSL certificate on ERP server. | `BaseTest.java` automatically sets `setIgnoreHTTPSErrors(true)` on `BrowserContext`. Ensure tests use `initBrowser()` rather than creating ad-hoc contexts. |
| **Element not clickable / intercepted by glass pane** | ZK ERP loading indicator or busy dialog is active over DOM. | Call `WaitUtils.waitForLoadingIndicatorToDisappear(page)` or `WaitUtils.waitForBigLoadingIndicatorToDisappear(page)` before the action. |
| **Elements layout differently in Headless vs Headed** | Default headless viewport in Playwright is 1280x720, whereas headed maximizes to monitor resolution. | `BaseTest.java` enforces `contextOptions.setViewportSize(1920, 1080)` when running in headless mode. |
| **Playwright binaries missing on CI agent** | New build node has no local cache in `~/.cache/ms-playwright`. | Execute `mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install --with-deps"` as a pre-build step. |
| **Headed run fails on Linux with `Missing DISPLAY`** | Running headed mode on Linux terminal without X11 server. | The `Jenkinsfile` wraps headed runs in `xvfb-run`. On local Linux, launch with `xvfb-run mvn test -Dheadless=false` or install X11. |
| **Tests not picked up by `mvn test`** | Surefire is bound to `testng.xml`. Verify `testng.xml` package patterns or class names. | Ensure classes follow the naming pattern or are included in the `<packages>` / `<classes>` section of [testng.xml](file:///home/mahamadgaus/Documents/Pharma_Automation/com.pharma.automation/testng.xml). |

---

## 👥 Contributors & Maintainers

- **Automation SDET:** Mahamadgaus Mulla ([mmulla@logilite.com](mailto:mmulla@logilite.com))
- **Repository:** `com.pharma.automation`
