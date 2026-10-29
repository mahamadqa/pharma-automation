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
				sparkReporter.config().setCss(getCustomCss());

				extent = new ExtentReports();
				extent.attachReporter(sparkReporter);
				extent.keepLastRetryOnly(true);

				extent.setSystemInfo("OS", System.getProperty("os.name"));
				extent.setSystemInfo("Java Version", System.getProperty("java.version"));
				extent.setSystemInfo("User", getReportUser());

				try {
					String jenkinsBuild = System.getenv("BUILD_NUMBER");
					if (jenkinsBuild != null && !jenkinsBuild.trim().isEmpty()) {
						extent.setSystemInfo("Jenkins Build", "#" + jenkinsBuild.trim());
					}
				} catch (Exception ignored) {
				}

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
	 * Updates ExtentReports System/Environment information with Test Class summary metrics.
	 */
	public static synchronized void updateClassSummary() {
		if (extent != null && TestClassTracker.getTotalClasses() > 0) {
			extent.setSystemInfo("Total Test Classes", String.valueOf(TestClassTracker.getTotalClasses()));
			extent.setSystemInfo("Test Classes Passed", String.valueOf(TestClassTracker.getPassedClassesCount()));
			extent.setSystemInfo("Test Classes Failed", String.valueOf(TestClassTracker.getFailedClassesCount()));
			if (TestClassTracker.getSkippedClassesCount() > 0) {
				extent.setSystemInfo("Test Classes Skipped", String.valueOf(TestClassTracker.getSkippedClassesCount()));
			}
			extent.setSystemInfo("Class Pass Rate", String.format("%.1f%%", TestClassTracker.getClassPassRate()));
		}
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
	 * Removes the ExtentTest instance from ExtentReports and clears ThreadLocal.
	 */
	public static synchronized void removeTest(ExtentTest test) {
		if (extent != null && test != null) {
			try {
				extent.removeTest(test);
				log.info("Removed retried test from ExtentReports: {}", test.getModel().getName());
			} catch (Exception e) {
				log.warn("Failed to remove test from ExtentReports: {}", e.getMessage());
			}
		}
		testTL.remove();
	}

	/**
	 * Flushes the ExtentReports instance to write out the HTML report
	 * and enriches the output with the executive Test Classes summary.
	 */
	public static synchronized void flush() {
		if (extent != null) {
			try {
				updateClassSummary();
				extent.flush();
				log.info("ExtentReports flushed successfully.");

				// Post-enrich HTML report with Executive Test Classes Summary Cards, Table & Modern UI styling
				String reportPath = System.getProperty("user.dir") + "/test-output/ExtentReport.html";
				HtmlReportEnricher.enrich(reportPath);
			} catch (Exception e) {
				log.error("Error flushing ExtentReports: {}", e.getMessage(), e);
			}
		}
	}

	private static String getCustomCss() {
		return "body, .app, .card, table { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif !important; }"
				+ ".card { border-radius: 10px !important; box-shadow: 0 2px 10px rgba(0,0,0,0.04) !important; }"
				+ ".card:hover { box-shadow: 0 4px 16px rgba(0,0,0,0.08) !important; }"
				+ ".class-metric-card { transition: transform 0.2s ease; }"
				+ ".class-metric-card:hover { transform: translateY(-3px); }"
				+ ".table thead th { font-weight: 600 !important; text-transform: uppercase; font-size: 11px; letter-spacing: 0.5px; }"
				+ ".badge { font-weight: 600; }"
				+ ".vheader .nav-right .badge-primary { background: linear-gradient(135deg, #2563eb, #1d4ed8) !important; border-radius: 6px; padding: 6px 12px; }";
	}

	/**
	 * Resolves the user name to display in Extent Report.
	 * When running from Jenkins, retrieves the Jenkins user detail name from
	 * system properties (-Djenkins.user, -DbuildUser) or Jenkins environment variables
	 * (BUILD_USER, BUILD_USER_ID, JENKINS_USER, CHANGE_AUTHOR_DISPLAY_NAME, etc.)
	 * instead of displaying generic 'jenkins'.
	 *
	 * @return resolved user display name
	 */
	public static String getReportUser() {
		// 1. System properties passed from command line or Jenkinsfile (-Djenkins.user, -DbuildUser, -Duser)
		String user = System.getProperty("jenkins.user");
		if (isValidUser(user)) {
			return user.trim();
		}

		user = System.getProperty("buildUser");
		if (isValidUser(user)) {
			return user.trim();
		}

		user = System.getProperty("user");
		if (isValidUser(user) && !user.equalsIgnoreCase("jenkins")) {
			return user.trim();
		}

		// 2. Jenkins environment variables (e.g. from Build User Vars plugin)
		user = System.getenv("BUILD_USER");
		if (isValidUser(user)) {
			return user.trim();
		}

		user = System.getenv("BUILD_USER_ID");
		if (isValidUser(user)) {
			return user.trim();
		}

		user = System.getenv("JENKINS_USER");
		if (isValidUser(user)) {
			return user.trim();
		}

		user = System.getenv("CHANGE_AUTHOR_DISPLAY_NAME");
		if (isValidUser(user)) {
			return user.trim();
		}

		user = System.getenv("CHANGE_AUTHOR");
		if (isValidUser(user)) {
			return user.trim();
		}

		user = System.getenv("GIT_AUTHOR_NAME");
		if (isValidUser(user)) {
			return user.trim();
		}

		// 3. Local OS user (if not generic 'jenkins')
		String osUser = System.getProperty("user.name");
		if (isValidUser(osUser) && !osUser.equalsIgnoreCase("jenkins")) {
			return osUser.trim();
		}

		// 4. OS environment variables (USER, LOGNAME) if not generic 'jenkins'
		user = System.getenv("USER");
		if (isValidUser(user) && !user.equalsIgnoreCase("jenkins")) {
			return user.trim();
		}

		user = System.getenv("LOGNAME");
		if (isValidUser(user) && !user.equalsIgnoreCase("jenkins")) {
			return user.trim();
		}

		// 5. If running under Jenkins and user is literally "jenkins", provide descriptive info
		if (System.getenv("JENKINS_HOME") != null || System.getenv("JENKINS_URL") != null) {
			String jobName = System.getenv("JOB_NAME");
			String buildNumber = System.getenv("BUILD_NUMBER");
			if (jobName != null && !jobName.trim().isEmpty()) {
				return "Jenkins User (" + jobName + (buildNumber != null ? " #" + buildNumber : "") + ")";
			}
			return "Jenkins User";
		}

		return isValidUser(osUser) ? osUser.trim() : "Automation User";
	}

	private static boolean isValidUser(String name) {
		return name != null && !name.trim().isEmpty() && !name.equalsIgnoreCase("null");
	}
}