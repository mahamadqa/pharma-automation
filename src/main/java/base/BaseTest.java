package base;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Properties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;

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

public class BaseTest {

	private static final Logger log = LogManager.getLogger(BaseTest.class);

	// ThreadLocal holders — one instance per thread instead of one shared static instance.
	private static final ThreadLocal<Playwright> playwrightTL = new ThreadLocal<>();
	private static final ThreadLocal<Browser> browserTL = new ThreadLocal<>();
	private static final ThreadLocal<BrowserContext> contextTL = new ThreadLocal<>();
	private static final ThreadLocal<Page> pageTL = new ThreadLocal<>();

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
					.setHeadless(isHeadless).setArgs(Arrays.asList("--start-maximized", "--no-sandbox", "--disable-dev-shm-usage")));
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
		log.info("Launched " + browserName + " browser (headless=" + isHeadless + ") and navigated to URL: " + ConfigReader.get("url"));
		return page;
	}
	
	public static void click(String locator) {
		Page page = pageTL.get();
		page.click(locator);

	}

	public static void fill(String locator, String value) {
		Page page = pageTL.get();
		page.fill(locator, value);

	}
	
	@BeforeSuite
    public void extentReportSetup() {

        try {

            String reportFolder = System.getProperty("user.dir")
                    + "/test-output";

            Files.createDirectories(Paths.get(reportFolder));

            String reportPath = reportFolder
                    + "/ExtentReport.html";

            sparkReporter = new ExtentSparkReporter(reportPath);

            sparkReporter.config().setDocumentTitle("Automation Test Report");
            sparkReporter.config().setReportName("Playwright Automation Report");

            extent = new ExtentReports();

            extent.attachReporter(sparkReporter);

            extent.setSystemInfo("OS",
                    System.getProperty("os.name"));

            extent.setSystemInfo("Java Version",
                    System.getProperty("java.version"));

            extent.setSystemInfo("User",
                    System.getProperty("user.name"));

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

	@BeforeMethod(alwaysRun = true)
	public void loginSetUp(ITestResult result) {
		
		extentTest = extent.createTest(
                result.getMethod().getMethodName()
        );
		
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
//		sopMaster = new SOPMaster(page, dashboard, workflowActivities);

		loginPage.navigateToMainLoginPage();
		loginPage.checkLogoOnLogin();
	}


	@AfterMethod(alwaysRun = true)
	public void tearDown(ITestResult result) {
		
		if (result.getStatus() == ITestResult.FAILURE) {

            extentTest.fail("Test Failed");
            extentTest.fail(result.getThrowable());

        } else if (result.getStatus() == ITestResult.SUCCESS) {

            extentTest.pass("Test Passed");

        } else if (result.getStatus() == ITestResult.SKIP) {

            extentTest.skip("Test Skipped");
        }
		
		BrowserContext context = contextTL.get();
		Browser browser = browserTL.get();
		Playwright playwright = playwrightTL.get();

		if (context != null)
			context.close();
		if (browser != null)
			browser.close();
		if (playwright != null)
			playwright.close();

		pageTL.remove();
		contextTL.remove();
		browserTL.remove();
		playwrightTL.remove();

	}
	
	@AfterSuite
    public void flushReport() {

        if (extent != null) {
            extent.flush();
        }
    }

}
