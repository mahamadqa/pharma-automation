package utils;

import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

/**
 * Unified ExtentReports manager providing thread-safe singleton ExtentReports
 * and ThreadLocal ExtentTest instances across BaseTest, Listeners, and assertions.
 */
public class ExtentManager {

	private static final Logger log = LogManager.getLogger(ExtentManager.class);
	private static volatile ExtentReports extent;
	private static ExtentSparkReporter sparkReporter;
	private static final ThreadLocal<ExtentTest> testTL = new ThreadLocal<>();

	private ExtentManager() {
		// Utility class
	}

	/**
	 * Retrieves the singleton ExtentReports instance, initializing it if necessary.
	 */
	public static synchronized ExtentReports getInstance() {
		if (extent == null) {
			try {
				String reportFolder = System.getProperty("user.dir") + "/test-output";
				Files.createDirectories(Paths.get(reportFolder));

				String reportPath = reportFolder + "/ExtentReport.html";
				sparkReporter = new ExtentSparkReporter(reportPath);

				sparkReporter.config().setDocumentTitle("Automation Test Report");
				sparkReporter.config().setReportName("Playwright Automation Report");
				sparkReporter.config().setTheme(Theme.STANDARD);
				sparkReporter.config().setTimeStampFormat("MMM dd, yyyy HH:mm:ss");

				extent = new ExtentReports();
				extent.attachReporter(sparkReporter);

				extent.setSystemInfo("OS", System.getProperty("os.name"));
				extent.setSystemInfo("Java Version", System.getProperty("java.version"));
				extent.setSystemInfo("User", System.getProperty("user.name"));

				try {
					String browser = ConfigReader.get("browser");
					if (browser != null && !browser.isEmpty()) {
						extent.setSystemInfo("Browser", browser);
					}
				} catch (Exception ignored) {
				}

				try {
					String region = DataManager.getRegion();
					if (region != null && !region.isEmpty()) {
						extent.setSystemInfo("Region / Profile", region);
					}
				} catch (Exception ignored) {
				}

				log.info("ExtentReports initialized successfully → {}", reportPath);
			} catch (Exception e) {
				log.error("Failed to initialize ExtentReports: {}", e.getMessage(), e);
			}
		}
		return extent;
	}

	/**
	 * Creates a new ExtentTest and registers it in ThreadLocal.
	 */
	public static synchronized ExtentTest createTest(String testName, String description) {
		ExtentReports reports = getInstance();
		if (reports != null) {
			ExtentTest test;
			if (description != null && !description.isEmpty()) {
				test = reports.createTest(testName, description);
			} else {
				test = reports.createTest(testName);
			}
			testTL.set(test);
			return test;
		}
		return null;
	}

	public static synchronized ExtentTest createTest(String testName) {
		return createTest(testName, null);
	}

	/**
	 * Returns the ExtentTest instance for the current thread.
	 */
	public static ExtentTest getTest() {
		return testTL.get();
	}

	/**
	 * Sets the ExtentTest instance for the current thread.
	 */
	public static void setTest(ExtentTest test) {
		testTL.set(test);
	}

	/**
	 * Removes the ExtentTest instance for the current thread.
	 */
	public static void removeTest() {
		testTL.remove();
	}

	/**
	 * Flushes the ExtentReports instance to write out the HTML report.
	 */
	public static synchronized void flush() {
		if (extent != null) {
			try {
				extent.flush();
				log.info("ExtentReports flushed successfully.");
			} catch (Exception e) {
				log.error("Error flushing ExtentReports: {}", e.getMessage(), e);
			}
		}
	}
}