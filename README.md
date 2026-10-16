# 🏥 Pharma Automation — Test Automation Framework

> **A beginner-friendly guide to understanding how automated testing works in this project.**  
> *No coding or technical experience needed to read this!*

---

## 📌 1. What Is This Project?

This project is an **automated testing framework** for a Pharmaceutical ERP (Enterprise Resource Planning) web application.

In simple terms:
Instead of a human manual tester sitting at a computer every day to:
1. Open a browser (like Chrome or Firefox)
2. Log into the system with different usernames and passwords
3. Click menus, enter data into forms (like Purchase Orders or Goods Receipt Notes)
4. Verify numbers, calculations, and tax amounts
5. Check if accounting records match

**This framework runs an automated "software robot" that does all of these steps accurately, repeatedly, and in minutes.**

---

## 🏗️ 2. The Framework Architecture in Simple Terms

Think of the framework like a **well-organized construction company or factory**:

```
┌────────────────────────────────────────────────────────┐
│               1. Test Plan / Master Schedule           │
│                       (testng.xml)                     │
│    "Decides which tests to run, in what order, & how"  │
└──────────────────────────┬─────────────────────────────┘
                           │
                           ▼
┌────────────────────────────────────────────────────────┐
│                   2. Test Scenarios                    │
│            (src/test/java/baseModules/)                │
│       "The step-by-step checklists to verify"          │
│   e.g. CreateGRNFromPOFlow, POFlow, GRNFlow            │
└──────────────┬───────────────────────────┬─────────────┘
               │                           │
               ▼                           ▼
┌───────────────────────────────┐ ┌──────────────────────┐
│       3. Test Foundation      │ │  4. Page Screens     │
│       (BaseTest.java)         │ │     (pages/)         │
│  "Prepares browser, logs in,  │ │ "Knows how to click  │
│   cleans up, takes snapshot   │ │  & fill each screen  │
│   if anything fails"          │ │  in the Pharma app"  │
└──────────────┬────────────────┘ └──────────┬───────────┘
               │                             │
               └──────────────┬──────────────┘
                              ▼
┌────────────────────────────────────────────────────────┐
│                   5. Support Tools                     │
│                       (utils/)                         │
│  • Config Reader     • Data Manager   • Auto-Retry     │
│  • Screen Waiter     • Report Maker   • Soft Checks    │
└─────────────────────────────┬──────────────────────────┘
                              ▼
┌────────────────────────────────────────────────────────┐
│            6. Configuration & Test Data                │
│                 (test/resources/)                      │
│   • Web URLs & Browsers  • User Logins  • Region Data  │
└────────────────────────────────────────────────────────┘
```

---

## 🔍 3. The 6 Main Layers Explained

### Layer 1: The Master Controller (`testng.xml`)
* **What it is:** The control center.
* **What it does:**
  - It specifies which groups of tests to trigger (e.g. `basic` daily smoke tests vs `advance` full flows).
  - It attaches auto-retry capabilities and reporting hooks.

### Layer 2: Test Scenarios (`src/test/java/baseModules/`)
* **What it is:** High-level business flows written in clear, logical steps.
* **Examples:**
  - **`CreateGRNFromPOFlow`**: Creates a Purchase Order (PO) → Approves it → Generates a Goods Receipt Note (GRN) from that PO → Fills batch & manufacturing dates → Completes the GRN → Verifies financial debit/credit balance.
  - **`POFlow`**: Tests creating and completing Purchase Orders.
  - **`GRNFlow`**: Tests direct receipt of goods.

### Layer 3: Test Foundation (`src/main/java/base/BaseTest.java`)
* **What it is:** The automatic setup and teardown manager.
* **What it does:**
  - **Before every test:** Launches a brand-new browser session, navigates to the app, and logs in.
  - **After every test:** If anything failed, it immediately snaps a screenshot and embeds it into the report, then gracefully shuts down the browser.
  - **Isolated & Safe:** Each test runs in its own isolated lane (`ThreadLocal`), so tests never interfere with each other.

### Layer 4: Screen / Page Objects (`src/main/java/pages/`)
* **What it is:** The **Page Object Model (POM)** pattern. Each screen in the web app has its own dedicated helper file.
* **Why it matters:**
  - If a button or field changes location on the web page, **we only update one file**, rather than rewriting 50 different tests!
* **Key pages included:**
  - `LoginPage` — Handles typing credentials and signing in.
  - `ClientAndRoleSelection` — Picks the organization/role (e.g., Admin, Superuser).
  - `PurchaseOrder` — Creates, fills, and approves purchase orders.
  - `GoodsReceiptNote` — Creates, matches, and completes goods receipt notes.
  - `AttributeSetInstance` — Verifies batch numbers, expiry dates, and lot quantities.
  - `Toolbar` — Clicks common buttons like Save, Complete, Prepare, and Close.

### Layer 5: Support Tools & Utilities (`src/main/java/utils/`)
* **`ConfigReader`**: Reads settings like application URL, headless mode, and browser type without modifying code.
* **`DataManager`**: Pulls region-specific test data (e.g. India localization vs international non-localization).
* **`RetryAnalyzer` & `RetryListener`**: Automatically retries a failed test up to **2 times** if an unexpected network blip or temporary glitch happens.
* **`CustomSoftAssert`**: Rather than stopping a test on the first minor issue, it collects all mismatches and reports them all together.
* **`ExtentManager` & `MyListener`**: Builds visual HTML test reports.

### Layer 6: Configuration & Data Files (`src/test/resources/`)
* `config.properties`: Global settings (URL, browser name, headless setting).
* `loginCredentials.properties`: User credentials for Initiators, Approvers, and Admins.
* `testdata/localization.properties`: Data specific to local/domestic business rules.
* `testdata/non-localization.properties`: Data specific to export/international business rules.

---

## 🔄 4. What Happens When a Test Runs? (Step-by-Step)

Here is the exact journey from start to finish:

1. **Start:** The tester or Jenkins CI server runs `mvn test`.
2. **Setup:** The framework reads `config.properties` and launches the selected browser (e.g., Google Chrome).
3. **Login:** It automatically fills in credentials and selects the assigned user role.
4. **Execution:** The test scenario performs business actions (clicks, selects dropdowns, enters dates and quantities).
5. **Validation:** It verifies whether on-screen calculations, totals, and document statuses match expectations.
6. **Error Handling & Retry:**
   - If something fails unexpectedly, it retries up to 2 times to ensure it wasn't a temporary network lag.
   - If it still fails, it automatically captures a full-page screenshot.
7. **Report:** All results, timings, and screenshots are compiled into an interactive HTML report.

---

## 📊 5. Reports and Proof of Testing

Every test run automatically generates:
- **Interactive HTML Report:** Located in the `test-output/` folder.
  - Shows green badges for **Passed** tests and red badges for **Failed** tests.
  - Lists test duration, tester/author name, and step-by-step audit logs.
- **Visual Failure Screenshots:** Located in `screenshots/`.
  - When a step fails, the system immediately snaps an image of what was on the screen so team members can see exactly what went wrong.

---

## 🛠️ 6. Technology Stack at a Glance

| Technology | Role | Why We Use It |
| :--- | :--- | :--- |
| **Java 17** | Programming Language | Stable, industry-standard language. |
| **Microsoft Playwright** | Browser Automation | Extremely fast, reliable modern browser driver (faster and more stable than legacy tools). |
| **TestNG** | Test Execution Engine | Manages test ordering, grouping, retries, and data passing. |
| **ExtentReports** | Visual Reporting | Produces clean, executive-ready HTML dashboards. |
| **Log4j2** | Event Logging | Keeps a detailed historical record of every test step. |
| **Maven** | Build Manager | Handles project dependencies and makes running tests a one-command task. |
| **Jenkins** | Automation Server | Schedules and runs tests automatically without manual human intervention. |

---

## 🚦 7. How to Run the Tests

You can run the tests from any terminal or command prompt:

* **Run all tests:**
  ```bash
  mvn test
  ```

* **Run only basic/smoke tests:**
  ```bash
  mvn test -Dgroups=basic
  ```

* **Run in headless mode (silent background mode, no browser popup):**
  ```bash
  mvn test -Dheadless=true
  ```

* **Run with a specific browser:**
  ```bash
  mvn test -Dbrowser=chrome
  mvn test -Dbrowser=firefox
  ```

* **Run with specific regional data:**
  ```bash
  mvn test -Dregion=localization
  mvn test -Dregion=non-localization
  ```

---

## 💡 8. Key Advantages for the Business

- **High Speed & Efficiency:** Validates critical end-to-end pharmacy flows in minutes instead of hours of manual clicking.
- **Early Defect Catching:** Detects bugs, incorrect calculations, or broken workflows before software goes to production.
- **Accurate & Consistent:** Eliminates human fatigue; steps are executed identically every single run.
- **Clear Audit Trail:** Automatic screenshots and detailed execution logs provide clear proof for quality assurance and compliance.
