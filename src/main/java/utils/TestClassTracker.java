package utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.testng.ITestResult;

/**
 * Thread-safe tracker for test class execution metrics.
 * Tracks test classes, methods executed within each class,
 * pass/fail/skip outcomes, execution durations, and overall class status.
 */
public class TestClassTracker {

	public static class ClassSummary {
		private final String fullClassName;
		private final String simpleClassName;
		private final String packageName;
		private final Map<String, String> methodResults = new ConcurrentHashMap<>();
		private final AtomicLong startTime = new AtomicLong(0);
		private final AtomicLong endTime = new AtomicLong(0);

		public ClassSummary(String fullClassName) {
			this.fullClassName = fullClassName;
			int lastDot = fullClassName.lastIndexOf('.');
			if (lastDot > 0) {
				this.simpleClassName = fullClassName.substring(lastDot + 1);
				this.packageName = fullClassName.substring(0, lastDot);
			} else {
				this.simpleClassName = fullClassName;
				this.packageName = "default";
			}
		}

		public void recordMethodStart(String methodName, long time) {
			if (time > 0) {
				startTime.updateAndGet(cur -> (cur == 0 || time < cur) ? time : cur);
				endTime.updateAndGet(cur -> (time > cur) ? time : cur);
			}
		}

		public void recordMethodSuccess(String methodName, long time) {
			methodResults.put(methodName, "PASS");
			if (time > 0) {
				endTime.updateAndGet(cur -> (time > cur) ? time : cur);
			}
		}

		public void recordMethodFailure(String methodName, long time) {
			methodResults.put(methodName, "FAIL");
			if (time > 0) {
				endTime.updateAndGet(cur -> (time > cur) ? time : cur);
			}
		}

		public void recordMethodSkipped(String methodName, long time) {
			// Don't overwrite PASS with SKIP
			methodResults.putIfAbsent(methodName, "SKIP");
			if (time > 0) {
				endTime.updateAndGet(cur -> (time > cur) ? time : cur);
			}
		}

		public String getFullClassName() {
			return fullClassName;
		}

		public String getSimpleClassName() {
			return simpleClassName;
		}

		public String getPackageName() {
			return packageName;
		}

		public int getTotalMethods() {
			return methodResults.size();
		}

		public int getPassedMethods() {
			int count = 0;
			for (String status : methodResults.values()) {
				if ("PASS".equalsIgnoreCase(status)) {
					count++;
				}
			}
			return count;
		}

		public int getFailedMethods() {
			int count = 0;
			for (String status : methodResults.values()) {
				if ("FAIL".equalsIgnoreCase(status)) {
					count++;
				}
			}
			return count;
		}

		public int getSkippedMethods() {
			int count = 0;
			for (String status : methodResults.values()) {
				if ("SKIP".equalsIgnoreCase(status)) {
					count++;
				}
			}
			return count;
		}

		public String getStatus() {
			if (getFailedMethods() > 0) {
				return "FAIL";
			}
			if (getPassedMethods() > 0) {
				return "PASS";
			}
			if (getSkippedMethods() > 0) {
				return "SKIP";
			}
			return "UNKNOWN";
		}

		public long getDurationMs() {
			long start = startTime.get();
			long end = endTime.get();
			return (start > 0 && end >= start) ? (end - start) : 0;
		}

		public String getDurationFormatted() {
			long ms = getDurationMs();
			if (ms <= 0) {
				return "0s";
			}
			long seconds = ms / 1000;
			long minutes = seconds / 60;
			long remSeconds = seconds % 60;
			if (minutes > 0) {
				return minutes + "m " + remSeconds + "s";
			}
			return String.format("%.2fs", ms / 1000.0);
		}
	}

	private static final Map<String, ClassSummary> classMap = new ConcurrentHashMap<>();

	private TestClassTracker() {
		// Utility class
	}

	public static ClassSummary getOrCreate(String fullClassName) {
		return classMap.computeIfAbsent(fullClassName, ClassSummary::new);
	}

	public static void onTestStart(ITestResult result) {
		if (result == null || result.getTestClass() == null) {
			return;
		}
		String fullClassName = result.getTestClass().getName();
		String methodName = result.getMethod().getMethodName();
		long time = result.getStartMillis() > 0 ? result.getStartMillis() : System.currentTimeMillis();
		getOrCreate(fullClassName).recordMethodStart(methodName, time);
	}

	public static void onTestSuccess(ITestResult result) {
		if (result == null || result.getTestClass() == null) {
			return;
		}
		String fullClassName = result.getTestClass().getName();
		String methodName = result.getMethod().getMethodName();
		long time = result.getEndMillis() > 0 ? result.getEndMillis() : System.currentTimeMillis();
		getOrCreate(fullClassName).recordMethodSuccess(methodName, time);
	}

	public static void onTestFailure(ITestResult result) {
		if (result == null || result.getTestClass() == null) {
			return;
		}
		String fullClassName = result.getTestClass().getName();
		String methodName = result.getMethod().getMethodName();
		long time = result.getEndMillis() > 0 ? result.getEndMillis() : System.currentTimeMillis();
		getOrCreate(fullClassName).recordMethodFailure(methodName, time);
	}

	public static void onTestSkipped(ITestResult result) {
		if (result == null || result.getTestClass() == null) {
			return;
		}
		if (RetryAnalyzer.isRetrying(result)) {
			// In-flight retry attempt; discard so it doesn't skew statistics
			return;
		}
		String fullClassName = result.getTestClass().getName();
		String methodName = result.getMethod().getMethodName();
		long time = result.getEndMillis() > 0 ? result.getEndMillis() : System.currentTimeMillis();
		getOrCreate(fullClassName).recordMethodSkipped(methodName, time);
	}

	public static int getTotalClasses() {
		return classMap.size();
	}

	public static int getPassedClassesCount() {
		int count = 0;
		for (ClassSummary cs : classMap.values()) {
			if ("PASS".equalsIgnoreCase(cs.getStatus())) {
				count++;
			}
		}
		return count;
	}

	public static int getFailedClassesCount() {
		int count = 0;
		for (ClassSummary cs : classMap.values()) {
			if ("FAIL".equalsIgnoreCase(cs.getStatus())) {
				count++;
			}
		}
		return count;
	}

	public static int getSkippedClassesCount() {
		int count = 0;
		for (ClassSummary cs : classMap.values()) {
			if ("SKIP".equalsIgnoreCase(cs.getStatus())) {
				count++;
			}
		}
		return count;
	}

	public static double getClassPassRate() {
		int total = getTotalClasses();
		if (total == 0) {
			return 0.0;
		}
		return ((double) getPassedClassesCount() / total) * 100.0;
	}

	public static List<ClassSummary> getClassSummaries() {
		List<ClassSummary> list = new ArrayList<>(classMap.values());
		list.sort(Comparator.comparing(ClassSummary::getSimpleClassName));
		return Collections.unmodifiableList(list);
	}

	public static void reset() {
		classMap.clear();
	}
}
