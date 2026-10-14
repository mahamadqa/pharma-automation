package utils;

import java.util.Base64;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import com.microsoft.playwright.Page;

import base.BaseTest;

public class MyListener implements ITestListener {

	private static final Logger log = LogManager.getLogger(utils.MyListener.class);

	@Override
	public void onTestStart(ITestResult result) {
		if (result.getMethod().getCurrentInvocationCount() == 0) {
			ExtentTest currentTest = ExtentManager.getTest();
			if (currentTest == null) {
				ExtentManager.createTest(result.getMethod().getMethodName(), result.getMethod().getDescription());
			}
		}
		log.info("Test Started: " + result.getName());
	}

	@Override
	public void onTestSuccess(ITestResult result) {
		ExtentTest test = ExtentManager.getTest();
		if (test != null && test.getStatus() != Status.FAIL) {
			test.pass("Test Passed");
		}
		log.info("✅ Test Passed: " + result.getName());
	}

	@Override
	public void onTestFailure(ITestResult result) {
		log.error("❌ Test Failed: " + result.getName());
		if (result.getThrowable() != null) {
			log.error("Reason: " + result.getThrowable());
		}

		ExtentTest test = ExtentManager.getTest();
		if (test != null) {
			if (result.getThrowable() != null) {
				test.fail(result.getThrowable());
			}

			// If browser page is still open and no screenshot has been captured yet, capture failure screenshot
			try {
				Page page = BaseTest.getPage();
				if (page != null && !page.isClosed() && (test.getModel().getMedia() == null || test.getModel().getMedia().isEmpty())) {
					byte[] screenshotBytes = page.screenshot(new Page.ScreenshotOptions().setFullPage(true));
					String base64Screenshot = Base64.getEncoder().encodeToString(screenshotBytes);
					test.fail("<b>Screenshot on Failure:</b>",
							MediaEntityBuilder.createScreenCaptureFromBase64String(base64Screenshot,
									result.getName() + " - Failure Screenshot").build());
					log.info("📸 Screenshot captured & embedded in ExtentReport.");
				}
			} catch (Exception e) {
				log.debug("Screenshot in listener skipped: {}", e.getMessage());
			}
		}
	}

	@Override
	public void onTestSkipped(ITestResult result) {
		log.warn("⚠️ Test Skipped: " + result.getName());
		ExtentTest test = ExtentManager.getTest();
		if (test != null) {
			test.skip("Test Skipped");
			if (result.getThrowable() != null) {
				test.skip(result.getThrowable());
			}
		}
	}

	@Override
	public void onFinish(ITestContext context) {
		ExtentManager.flush();
	}

}
