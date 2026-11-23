package utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
		ExtentManager.flush();
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
		ExtentManager.flush();
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
		ExtentManager.flush();
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
			ExtentManager.flush();
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
		ExtentManager.flush();
	}

	@Override
	public void onFinish(ITestContext context) {
		// 1. Clean up failed tests: If a test method subsequently passed on a retry attempt,
		// remove the earlier failed attempt(s) from TestNG's failedTests so Maven Surefire
		// evaluates the test suite as PASSED instead of failing the build.
		List<ITestResult> failedToRemove = new ArrayList<>();
		for (ITestResult failedTest : context.getFailedTests().getAllResults()) {
			if (context.getPassedTests().getResults(failedTest.getMethod()).size() > 0) {
				failedToRemove.add(failedTest);
			}
		}
		for (ITestResult r : failedToRemove) {
			context.getFailedTests().removeResult(r);
			log.info("Cleaned up retried failed test from TestNG context: {}.{}",
					r.getTestClass().getRealClass().getSimpleName(), r.getName());
		}

		// 2. Clean up skipped tests: If a test method passed or finally failed on retry,
		// remove intermediate skipped attempts so they are not counted as skipped in reports.
		List<ITestResult> skippedToRemove = new ArrayList<>();
		for (ITestResult skippedTest : context.getSkippedTests().getAllResults()) {
			if (context.getPassedTests().getResults(skippedTest.getMethod()).size() > 0
					|| context.getFailedTests().getResults(skippedTest.getMethod()).size() > 0) {
				skippedToRemove.add(skippedTest);
			}
		}
		for (ITestResult r : skippedToRemove) {
			context.getSkippedTests().removeResult(r);
			log.info("Cleaned up retried skipped test from TestNG context: {}.{}",
					r.getTestClass().getRealClass().getSimpleName(), r.getName());
		}

		// 3. Clean up failed configurations: If a configuration (@AfterMethod / @BeforeMethod)
		// failed on an intermediate attempt but the test class subsequently completed successfully
		// (all test methods passed and none failed), remove the failed configuration so Maven Surefire
		// doesn't fail the build due to transient configuration errors during retries.
		List<ITestResult> failedConfigsToRemove = new ArrayList<>();
		for (ITestResult failedConfig : context.getFailedConfigurations().getAllResults()) {
			Class<?> realClass = failedConfig.getTestClass().getRealClass();
			boolean hasFailingTests = context.getFailedTests().getAllResults().stream()
					.anyMatch(r -> r.getTestClass().getRealClass().equals(realClass));
			boolean hasPassingTests = context.getPassedTests().getAllResults().stream()
					.anyMatch(r -> r.getTestClass().getRealClass().equals(realClass));
			if (hasPassingTests && !hasFailingTests) {
				failedConfigsToRemove.add(failedConfig);
			}
		}
		for (ITestResult r : failedConfigsToRemove) {
			context.getFailedConfigurations().removeResult(r);
			log.info("Cleaned up retry configuration failure from TestNG context: {}.{}",
					r.getTestClass().getRealClass().getSimpleName(), r.getName());
		}

		// 4. Deduplicate failed tests: If a test permanently failed across multiple retry attempts,
		// retain only the latest failed attempt so the failure count accurately reflects unique methods.
		Map<String, ITestResult> latestFailures = new HashMap<>();
		List<ITestResult> duplicateFailuresToRemove = new ArrayList<>();
		for (ITestResult failedTest : context.getFailedTests().getAllResults()) {
			String key = failedTest.getTestClass().getName() + "." + failedTest.getMethod().getMethodName();
			ITestResult existing = latestFailures.get(key);
			if (existing == null) {
				latestFailures.put(key, failedTest);
			} else {
				if (failedTest.getEndMillis() > existing.getEndMillis()) {
					duplicateFailuresToRemove.add(existing);
					latestFailures.put(key, failedTest);
				} else {
					duplicateFailuresToRemove.add(failedTest);
				}
			}
		}
		for (ITestResult dup : duplicateFailuresToRemove) {
			context.getFailedTests().removeResult(dup);
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
