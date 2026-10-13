package utils;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.microsoft.playwright.Page;

import base.BaseTest;

public class MyListener implements ITestListener {

	private static final Logger log = LogManager.getLogger(utils.MyListener.class);
	ExtentReports extent = ExtentManager.getInstance();

	static ThreadLocal<ExtentTest> test = new ThreadLocal<>();

	// String filePath = null;

	@Override
	public void onTestStart(ITestResult result) {

		if (result.getMethod().getCurrentInvocationCount() == 0) {
			ExtentTest extentTest = extent.createTest(result.getMethod().getMethodName());

			test.set(extentTest);
		}
		log.info("Test Started: " + result.getName());
	}

	@Override
	public void onTestSuccess(ITestResult result) {

		test.get().pass("Test Passed");
		log.info("✅ Test Passed: " + result.getName());
	}

	@Override
	public void onTestFailure(ITestResult result) {

		log.error("❌ Test Failed: " + result.getName());
		log.error("Reason: " + result.getThrowable());
		test.get().fail(result.getThrowable());

		try {

			Page page = BaseTest.getPage();

			if (page != null) {

				String testName = result.getName();

				// ── Save screenshot to file ──────────────────────────────
				String screenshotDir = System.getProperty("user.dir") + "/screenshots";
				Files.createDirectories(Paths.get(screenshotDir));
				String filePath = screenshotDir + "/" + testName + "_" + System.currentTimeMillis() + ".png";
				byte[] screenshotBytes = page.screenshot(
						new Page.ScreenshotOptions().setPath(Paths.get(filePath)).setFullPage(true));

				// ── Embed as Base64 in ExtentReport (self-contained HTML) ─
				String base64Screenshot = Base64.getEncoder().encodeToString(screenshotBytes);
				test.get().fail("<b>Screenshot on Failure:</b>")
						.addScreenCaptureFromBase64String(base64Screenshot, testName + " - Failure Screenshot");

				log.info("📸 Screenshot saved & embedded in report: " + filePath);
			}

		} catch (Exception e) {

			log.error("Screenshot capture failed", e);
		}
	}

	@Override
	public void onFinish(ITestContext context) {

		extent.flush();
	}

	@Override
	public void onTestSkipped(ITestResult result) {
		log.warn("⚠️ Test Skipped: " + result.getName());
	}

}
