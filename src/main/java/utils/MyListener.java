package utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.ISuite;
import org.testng.ISuiteListener;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;

import base.BaseTest;

public class MyListener implements ITestListener, ISuiteListener {

	private static final Logger log = LogManager.getLogger(utils.MyListener.class);

	@Override
	public void onStart(ISuite suite) {
		TestClassTracker.reset();
		log.info("Test Suite started: " + suite.getName());
	}

	@Override
	public void onTestStart(ITestResult result) {
		TestClassTracker.onTestStart(result);

		int invocation = result.getMethod().getCurrentInvocationCount();
		String className = result.getTestClass().getRealClass().getSimpleName();
		String methodName = result.getMethod().getMethodName();
		String testDisplayName = className + " - " + methodName;

		ExtentTest currentTest = ExtentManager.getTest();
		if (currentTest == null || !testDisplayName.equals(currentTest.getModel().getName())) {
			currentTest = ExtentManager.createTest(testDisplayName, result.getMethod().getDescription());
			if (currentTest != null) {
				currentTest.assignAuthor(ExtentManager.capitalizeWord(ExtentManager.getReportUser()));
				for (String group : result.getMethod().getGroups()) {
					if (group != null && !group.trim().isEmpty()) {
						currentTest.assignCategory(group.trim());
					}
				}
			}
			if (invocation > 0) {
				currentTest.info("🔄 Retrying test execution (Attempt " + (invocation + 1) + ")");
			}
		}
		log.info("Test Started: " + className + "." + result.getName() + (invocation > 0 ? " (Retry Attempt " + (invocation + 1) + ")" : ""));
	}

	@Override
	public void onTestSuccess(ITestResult result) {
		TestClassTracker.onTestSuccess(result);

		String className = result.getTestClass().getRealClass().getSimpleName();
		ExtentTest test = ExtentManager.getTest();
		if (test != null && test.getStatus() != Status.FAIL) {
			test.pass("Test Passed");
		}
		log.info("✅ Test Passed: " + className + "." + result.getName());
		ExtentManager.removeTest();
	}

	@Override
	public void onTestFailure(ITestResult result) {
		TestClassTracker.onTestFailure(result);

		String className = result.getTestClass().getRealClass().getSimpleName();
		log.error("❌ Test Failed: " + className + "." + result.getName());
		if (result.getThrowable() != null) {
			log.error("Reason: " + result.getThrowable());
		}

		ExtentTest test = ExtentManager.getTest();
		if (test != null) {
			if (result.getThrowable() != null) {
				test.fail(result.getThrowable());
			}

			// Capture failure screenshot (delegates to BaseTest for deduplication & visible <img> rendering)
			BaseTest.captureFailureScreenshot(className + "_" + result.getName());
		}
		ExtentManager.removeTest();
	}

	@Override
	public void onTestSkipped(ITestResult result) {
		TestClassTracker.onTestSkipped(result);

		String className = result.getTestClass().getRealClass().getSimpleName();
		String methodName = result.getMethod().getMethodName();
		String testDisplayName = className + " - " + methodName;
		boolean isRetrying = RetryAnalyzer.isRetrying(result);
		ExtentTest test = ExtentManager.getTest();

		if (isRetrying) {
			log.warn("🔄 Test failed and is being retried, discarding prior attempt from ExtentReport: " + className + "." + result.getName());
			if (test != null && testDisplayName.equals(test.getModel().getName())) {
				ExtentManager.removeTest(test);
			} else {
				ExtentManager.removeTest();
			}
			return;
		}

		log.warn("⚠️ Test Skipped: " + className + "." + result.getName());
		if (test != null) {
			test.skip("Test Skipped");
			if (result.getThrowable() != null) {
				test.skip(result.getThrowable());
			}
		}
		ExtentManager.removeTest();
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
		ExtentManager.updateClassSummary();
		ExtentManager.flush();
	}

	@Override
	public void onFinish(ISuite suite) {
		ExtentManager.updateClassSummary();
		ExtentManager.flush();
		log.info("Test Suite finished: " + suite.getName());
	}

}
