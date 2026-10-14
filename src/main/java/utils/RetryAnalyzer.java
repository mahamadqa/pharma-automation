package utils;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

public class RetryAnalyzer implements IRetryAnalyzer {

	private int retryCount = 0;

	private static final int MAX_RETRY_COUNT = 2;

	@Override
	public boolean retry(ITestResult result) {

		if (retryCount < MAX_RETRY_COUNT) {

			retryCount++;
			result.setAttribute("RETRY_IN_PROGRESS", Boolean.TRUE);
			result.setAttribute("RETRY_COUNT", retryCount);

			System.out.println("🔄 RETRYING TEST: " + result.getMethod().getMethodName() + " | Attempt: " + retryCount
					+ "/" + MAX_RETRY_COUNT);

			return true;
		}

		return false;
	}

	public int getRetryCount() {
		return retryCount;
	}

	public static boolean isRetrying(ITestResult result) {
		if (result == null) {
			return false;
		}
		if (Boolean.TRUE.equals(result.getAttribute("RETRY_IN_PROGRESS"))) {
			return true;
		}
		return result.wasRetried();
	}
}