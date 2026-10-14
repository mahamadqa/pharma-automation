package base;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Base64;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Listeners;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;

import pages.ClientAndRoleSelection;
import pages.Dashboard;
import pages.HTMLReportsPreview;
import pages.LoginPage;
import pages.PurchaseOrder;
import pages.SOPMaster;
import pages.Toolbar;
import pages.WorkflowActivities;
import utils.ConfigReader;
import utils.CustomSoftAssert;
import utils.ExtentManager;

@Listeners({ utils.MyListener.class, utils.RetryListener.class })
public class BaseTest {

	private static final Logger log = LogManager.getLogger(BaseTest.class);

	// ThreadLocal holders — one instance per thread instead of one shared static instance
	private static final ThreadLocal<Playwright> playwrightTL = new ThreadLocal<>();
	private static final ThreadLocal<Browser> browserTL = new ThreadLocal<>();
	private static final ThreadLocal<BrowserContext> contextTL = new ThreadLocal<>();
	private static final ThreadLocal<Page> pageTL = new ThreadLocal<>();
	private static final ThreadLocal<Boolean> failureScreenshotCapturedTL = ThreadLocal.withInitial(() -> false);

	// One CustomSoftAssert per test thread — collects failures and asserts in tearDown
	private static final ThreadLocal<CustomSoftAssert> softAssertTL = new ThreadLocal<>();

	/** Returns the CustomSoftAssert for the current test thread. */
	public static CustomSoftAssert getSoftAssert() {
		return softAssertTL.get();
	}

	protected static Properties prop;
	protected static ConfigReader reader;

	protected LoginPage loginPage;
	protected ClientAndRoleSelection clientAndRole;
	protected Toolbar toolbar;
	protected Dashboard dashboard;
	protected HTMLReportsPreview hTMLPreview;
	protected SOPMaster sopMaster;
	protected WorkflowActivities workflowActivities;
	protected PurchaseOrder purchaseOrder;

	protected static ExtentReports extent;
	protected static ExtentSparkReporter sparkReporter;
	public ExtentTest extentTest;

	public static ExtentTest getExtentTest() {
		return ExtentManager.getTest();
	}

	public static Page getPage() {
		return pageTL.get();
	}

	public static Page initBrowser() {
		prop = ConfigReader.initProp();
		Playwright playwright = Playwright.create();
		playwrightTL.set(playwright);

		String browserName = ConfigReader.get("browser");
		reader = new ConfigReader();

		boolean isHeadless = ConfigReader.headlessMode();

		Browser browser;
		switch (browserName.toLowerCase()) {
			case "chrome":
			case "chromium":
				browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
						.setHeadless(isHeadless)
						.setArgs(Arrays.asList("--start-maximized", "--no-sandbox", "--disable-dev-shm-usage")));
				break;

			case "firefox":
				browser = playwright.firefox().launch(new BrowserType.LaunchOptions()
						.setHeadless(isHeadless).setArgs(Arrays.asList("--start-maximized")));
				break;

			default:
				throw new RuntimeException("Browser not supported: " + browserName);
		}
		browserTL.set(browser);

		Browser.NewContextOptions contextOptions = new Browser.NewContextOptions().setIgnoreHTTPSErrors(true);
		if (isHeadless) {
			contextOptions.setViewportSize(1920, 1080);
		} else {
			contextOptions.setViewportSize(null);
		}

		BrowserContext context = browser.newContext(contextOptions);
		contextTL.set(context);

		Page page = context.newPage();
		pageTL.set(page);

		page.navigate(ConfigReader.get("url"));
		page.bringToFront();
		page.locator("body").click();
		log.info("Launched " + browserName + " browser (headless=" + isHeadless + ") and navigated to URL: "
				+ ConfigReader.get("url"));
		return page;
	}

	public static void click(String locator) {
		Page page = pageTL.get();
		if (page != null) {
			page.click(locator);
		}
	}

	public static void fill(String locator, String value) {
		Page page = pageTL.get();
		if (page != null) {
			page.fill(locator, value);
		}
	}

	@BeforeSuite(alwaysRun = true)
	public void extentReportSetup() {
		try {
			extent = ExtentManager.getInstance();
			log.info("ExtentReports initialized successfully via ExtentManager.");
		} catch (Exception e) {
			log.error("FAILED to initialize ExtentReports in @BeforeSuite: " + e.getMessage(), e);
		}
	}

	@BeforeMethod(alwaysRun = true)
	public void loginSetUp(ITestResult result) {

		// Reset screenshot capture flag for this test thread
		failureScreenshotCapturedTL.set(false);

		// Fresh CustomSoftAssert for every test thread — collects all failed assertions
		softAssertTL.set(new CustomSoftAssert());

		// Initialize ExtentTest via ExtentManager
		ExtentTest currentTest = ExtentManager.getTest();
		if (currentTest == null) {
			currentTest = ExtentManager.createTest(
					result.getMethod().getMethodName(),
					result.getMethod().getDescription());
		}
		this.extentTest = currentTest;

		Page page = initBrowser();

		loginPage = new LoginPage(page);
		clientAndRole = new ClientAndRoleSelection(page);
		toolbar = new Toolbar(page);

		reader = new ConfigReader();

		dashboard = new Dashboard(page, loginPage, clientAndRole);
		purchaseOrder = new PurchaseOrder(page);
		workflowActivities = new WorkflowActivities(page);
		hTMLPreview = new HTMLReportsPreview(page);
		sopMaster = new SOPMaster(page);

		loginPage.navigateToMainLoginPage();
		loginPage.checkLogoOnLogin();
	}

	@AfterMethod(alwaysRun = true)
	public void tearDown(ITestResult result) {

		CustomSoftAssert sa = softAssertTL.get();
		boolean hasSoftFailures = (sa != null && sa.hasFailures());

		// 1. If soft assertion failures occurred, attach all of them to Extent Report
		// while the browser/page is still active!
		if (hasSoftFailures) {
			log.error("Soft assertion failure(s) detected in [{}] (Total: {})",
					result.getMethod().getMethodName(), sa.getFailureCount());
			sa.attachAllFailuresToReport();
			result.setStatus(ITestResult.FAILURE);
		}

		// 2. Capture screenshot & log status if test failed
		if (result.getStatus() == ITestResult.FAILURE) {
			Throwable t = result.getThrowable();
			ExtentTest test = ExtentManager.getTest();

			// If it's a hard assertion failure
			if (t instanceof AssertionError && !hasSoftFailures) {
				log.error("Hard assertion failure in [{}]: {}", result.getMethod().getMethodName(), t.getMessage());
				attachHardAssertionFailureToReport(t);
			}

			// Capture final screenshot while browser page is still open (if not already captured by soft asserts)
			if (!hasSoftFailures) {
				captureFailureScreenshot(result.getMethod().getMethodName());
			}

		} else if (result.getStatus() == ITestResult.SUCCESS) {
			ExtentTest test = ExtentManager.getTest();
			if (test != null && !hasSoftFailures) {
				test.pass("Test Passed");
			}
		} else if (result.getStatus() == ITestResult.SKIP) {
			ExtentTest test = ExtentManager.getTest();
			if (test != null) {
				test.skip("Test Skipped");
			}
		}

		// 3. Close browser resources safely
		try {
			BrowserContext context = contextTL.get();
			Browser browser = browserTL.get();
			Playwright playwright = playwrightTL.get();

			if (context != null)
				context.close();
			if (browser != null)
				browser.close();
			if (playwright != null)
				playwright.close();
		} catch (Exception e) {
			log.warn("Error closing browser resources: {}", e.getMessage());
		} finally {
			failureScreenshotCapturedTL.remove();
			pageTL.remove();
			contextTL.remove();
			browserTL.remove();
			playwrightTL.remove();
		}

		// 4. Finally call sa.assertAll() if there were soft assertion failures
		// This ensures TestNG records the test as failed with full assertion details!
		if (sa != null) {
			softAssertTL.remove();
			if (hasSoftFailures) {
				sa.assertAll();
			}
		}
	}

	private void attachHardAssertionFailureToReport(Throwable t) {
		ExtentTest test = ExtentManager.getTest();
		if (test == null) {
			return;
		}

		String message = t.getMessage() != null ? t.getMessage() : "Hard assertion failed";
		String location = "Unknown";
		for (StackTraceElement elem : t.getStackTrace()) {
			String cls = elem.getClassName();
			if (!cls.startsWith("org.testng.") && !cls.startsWith("java.lang.") && !cls.startsWith("base.BaseTest")) {
				location = elem.getFileName() + ":" + elem.getLineNumber() + " (" + elem.getMethodName() + ")";
				break;
			}
		}

		StringBuilder html = new StringBuilder();
		html.append("<div style='background-color:#fff5f5; border:1px solid #feb2b2; border-left:4px solid #e53e3e; padding:10px 14px; border-radius:4px; margin:6px 0; font-family:sans-serif;'>");
		html.append("  <span style='color:#c53030; font-weight:bold;'>❌ Hard Assertion Failure</span><br/>");
		html.append("  <div style='margin-top:4px;'><b>Details:</b> ").append(escapeHtml(message)).append("</div>");
		html.append("  <div style='margin-top:4px; font-size:11px; color:#718096;'><b>Location:</b> ").append(escapeHtml(location)).append("</div>");
		html.append("</div>");

		test.fail(html.toString());
	}

	public static void captureFailureScreenshot(String testName) {
		if (Boolean.TRUE.equals(failureScreenshotCapturedTL.get())) {
			return;
		}
		try {
			Page page = pageTL.get();
			if (page != null && !page.isClosed()) {
				failureScreenshotCapturedTL.set(true);
				String screenshotDir = System.getProperty("user.dir") + "/screenshots";
				Files.createDirectories(Paths.get(screenshotDir));
				String filePath = screenshotDir + "/" + testName + "_" + System.currentTimeMillis() + ".png";
				byte[] screenshotBytes = page.screenshot(
						new Page.ScreenshotOptions().setPath(Paths.get(filePath)).setFullPage(true));

				String base64Screenshot = Base64.getEncoder().encodeToString(screenshotBytes);
				ExtentTest test = ExtentManager.getTest();
				if (test != null) {
					StringBuilder html = new StringBuilder();
					html.append("<div style='margin-top:10px;'>");
					html.append("  <b style='color:#e53e3e; font-size:13px;'>📸 Screenshot on Failure:</b>");
					html.append("  <div style='font-size:11px; color:#718096; margin-top:2px; margin-bottom:6px;'>Saved to: ")
							.append(escapeHtml(filePath)).append("</div>");
					html.append("  <a href='data:image/png;base64,").append(base64Screenshot)
							.append("' target='_blank' title='Click to view full screenshot in a new tab'>");
					html.append("    <img src='data:image/png;base64,").append(base64Screenshot)
							.append("' style='width:100%; max-width:750px; height:auto; border:1px solid #cbd5e0; border-radius:6px; box-shadow:0 2px 6px rgba(0,0,0,0.15); display:block; cursor:pointer;' alt='Failure Screenshot' />");
					html.append("  </a>");
					html.append("</div>");

					test.fail(html.toString());
				}
				log.info("📸 Screenshot saved & embedded in report: {}", filePath);
			}
		} catch (Exception e) {
			log.warn("Screenshot capture failed: {}", e.getMessage());
		}
	}

	private static String escapeHtml(String text) {
		if (text == null) {
			return "";
		}
		return text.replace("&", "&amp;")
				.replace("<", "&lt;")
				.replace(">", "&gt;")
				.replace("\"", "&quot;")
				.replace("'", "&#39;");
	}

	@AfterSuite(alwaysRun = true)
	public void flushReport() {
		ExtentManager.flush();
	}

}
