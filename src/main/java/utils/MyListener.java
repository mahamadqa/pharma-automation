package utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;

import base.BaseTest;

public class MyListener implements ITestListener {

	private static final Logger log = LogManager.getLogger(utils.MyListener.class);

	@Override
	public void onTestStart(ITestResult result) {
		int invocation = result.getMethod().getCurrentInvocationCount();
		ExtentTest currentTest = ExtentManager.getTest();
		if (currentTest == null) {
			currentTest = ExtentManager.createTest(result.getMethod().getMethodName(), result.getMethod().getDescription());
			if (invocation > 0) {
				currentTest.info("🔄 Retrying test execution (Attempt " + (invocation + 1) + ")");
			}
		}
		log.info("Test Started: " + result.getName() + (invocation > 0 ? " (Retry Attempt " + (invocation + 1) + ")" : ""));
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

			// Capture failure screenshot (delegates to BaseTest for deduplication & visible <img> rendering)
			BaseTest.captureFailureScreenshot(result.getName());
		}
	}

	@Override
	public void onTestSkipped(ITestResult result) {
		boolean isRetrying = RetryAnalyzer.isRetrying(result);
		ExtentTest test = ExtentManager.getTest();

		if (isRetrying) {
			log.warn("🔄 Test failed and is being retried, discarding prior attempt from ExtentReport: " + result.getName());
			if (test != null) {
				ExtentManager.removeTest(test);
			}
			return;
		}

		log.warn("⚠️ Test Skipped: " + result.getName());
		if (test != null) {
			test.skip("Test Skipped");
			if (result.getThrowable() != null) {
				test.skip(result.getThrowable());
			}
		}
	}

	@Override
	public void onFinish(ITestContext context) {
		// Clean up TestNG context so retried tests that later passed are not counted as skipped in TestNG reports
		java.util.Iterator<ITestResult> skippedTests = context.getSkippedTests().getAllResults().iterator();
		while (skippedTests.hasNext()) {
			ITestResult skippedTest = skippedTests.next();
			if (context.getPassedTests().getResults(skippedTest.getMethod()).size() > 0
					|| context.getFailedTests().getResults(skippedTest.getMethod()).size() > 0) {
				skippedTests.remove();
			}
		}
		ExtentManager.flush();
	}

}
