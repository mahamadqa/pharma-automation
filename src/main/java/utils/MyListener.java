package utils;

import java.nio.file.Paths;

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

				String filePath = System.getProperty("user.dir") + "/screenshots/" + testName + "_"
						+ System.currentTimeMillis() + ".png";

				page.screenshot(new Page.ScreenshotOptions().setPath(Paths.get(filePath)).setFullPage(true));

				test.get().addScreenCaptureFromPath(filePath);

				log.error("TEST FAILED : " + result.getName());

				log.error(result.getThrowable());

				log.info("📸 Screenshot saved: " + filePath);
				
				
				
				
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
