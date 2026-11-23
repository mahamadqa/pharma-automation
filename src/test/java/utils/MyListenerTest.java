package utils;

import java.lang.reflect.Proxy;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.testng.Assert;
import org.testng.IResultMap;
import org.testng.ITestClass;
import org.testng.ITestContext;
import org.testng.ITestNGMethod;
import org.testng.ITestResult;
import org.testng.annotations.Test;

public class MyListenerTest {

	private static class SimpleResultMap implements IResultMap {
		private final Set<ITestResult> results = new HashSet<>();

		@Override
		public void addResult(ITestResult result) {
			results.add(result);
		}

		@Override
		public Set<ITestResult> getResults(ITestNGMethod method) {
			Set<ITestResult> res = new HashSet<>();
			for (ITestResult r : results) {
				if (r.getMethod() != null && r.getMethod().equals(method)) {
					res.add(r);
				}
			}
			return res;
		}

		@Override
		public void removeResult(ITestNGMethod method) {
			results.removeIf(r -> r.getMethod() != null && r.getMethod().equals(method));
		}

		@Override
		public void removeResult(ITestResult result) {
			results.remove(result);
		}

		@Override
		public Set<ITestResult> getAllResults() {
			return results;
		}

		@Override
		public int size() {
			return results.size();
		}

		@Override
		public Collection<ITestNGMethod> getAllMethods() {
			Set<ITestNGMethod> methods = new HashSet<>();
			for (ITestResult r : results) {
				methods.add(r.getMethod());
			}
			return methods;
		}
	}

	private ITestNGMethod createMockMethod(String methodName, Class<?> testClass) {
		ITestClass mockTestClass = (ITestClass) Proxy.newProxyInstance(
				ITestClass.class.getClassLoader(),
				new Class<?>[] { ITestClass.class },
				(proxy, method, args) -> {
					if ("getRealClass".equals(method.getName())) {
						return testClass;
					}
					if ("getName".equals(method.getName())) {
						return testClass.getName();
					}
					return null;
				});

		return (ITestNGMethod) Proxy.newProxyInstance(
				ITestNGMethod.class.getClassLoader(),
				new Class<?>[] { ITestNGMethod.class },
				(proxy, method, args) -> {
					if ("getMethodName".equals(method.getName())) {
						return methodName;
					}
					if ("getTestClass".equals(method.getName())) {
						return mockTestClass;
					}
					if ("getRealClass".equals(method.getName())) {
						return testClass;
					}
					if ("equals".equals(method.getName())) {
						return proxy == args[0];
					}
					if ("hashCode".equals(method.getName())) {
						return methodName.hashCode();
					}
					return null;
				});
	}

	private ITestResult createMockTestResult(ITestNGMethod method, int status, long endMillis) {
		ITestClass testClass = method.getTestClass();
		return (ITestResult) Proxy.newProxyInstance(
				ITestResult.class.getClassLoader(),
				new Class<?>[] { ITestResult.class },
				(proxy, m, args) -> {
					switch (m.getName()) {
						case "getMethod":
							return method;
						case "getStatus":
							return status;
						case "getEndMillis":
							return endMillis;
						case "getTestClass":
							return testClass;
						case "getName":
							return method.getMethodName();
						case "equals":
							return proxy == args[0];
						case "hashCode":
							return System.identityHashCode(proxy);
						default:
							return null;
					}
				});
	}

	@Test
	public void testRetriedTestCleanupInOnFinish() {
		MyListener listener = new MyListener();

		Class<?> testClass = MyListenerTest.class;
		ITestNGMethod ngMethod = createMockMethod("sampleTestMethod", testClass);
		ITestNGMethod ngFailMethod = createMockMethod("permanentFailMethod", testClass);

		// Create ResultMaps
		IResultMap passedTests = new SimpleResultMap();
		IResultMap failedTests = new SimpleResultMap();
		IResultMap skippedTests = new SimpleResultMap();
		IResultMap failedConfigs = new SimpleResultMap();

		// Simulate: sampleTestMethod failed on attempt 1
		ITestResult attempt1Fail = createMockTestResult(ngMethod, ITestResult.FAILURE, 1000);
		failedTests.addResult(attempt1Fail);

		// Simulate: sampleTestMethod was skipped during retry
		ITestResult attempt1Skip = createMockTestResult(ngMethod, ITestResult.SKIP, 1000);
		skippedTests.addResult(attempt1Skip);

		// Simulate: sampleTestMethod passed on attempt 2
		ITestResult attempt2Pass = createMockTestResult(ngMethod, ITestResult.SUCCESS, 2000);
		passedTests.addResult(attempt2Pass);

		// Simulate: permanentFailMethod failed and never passed
		ITestResult permFail = createMockTestResult(ngFailMethod, ITestResult.FAILURE, 1500);
		failedTests.addResult(permFail);

		// Mock ITestContext
		ITestContext mockContext = (ITestContext) Proxy.newProxyInstance(
				ITestContext.class.getClassLoader(),
				new Class<?>[] { ITestContext.class },
				(proxy, method, args) -> {
					switch (method.getName()) {
						case "getPassedTests":
							return passedTests;
						case "getFailedTests":
							return failedTests;
						case "getSkippedTests":
							return skippedTests;
						case "getFailedConfigurations":
							return failedConfigs;
						case "getName":
							return "TestContext";
						default:
							return null;
					}
				});

		// Verify before cleanup
		Assert.assertEquals(failedTests.getAllResults().size(), 2, "Before cleanup, failedTests should have 2 entries");
		Assert.assertEquals(skippedTests.getAllResults().size(), 1, "Before cleanup, skippedTests should have 1 entry");
		Assert.assertEquals(passedTests.getAllResults().size(), 1, "Before cleanup, passedTests should have 1 entry");

		// Run listener.onFinish
		listener.onFinish(mockContext);

		// Verify after cleanup:
		// 1. sampleTestMethod must be REMOVED from failedTests because it passed on retry
		// 2. permanentFailMethod must REMAIN in failedTests
		Assert.assertEquals(failedTests.getAllResults().size(), 1, "After cleanup, retried pass should be removed from failedTests");
		boolean containsPermanentFail = false;
		for (ITestResult r : failedTests.getAllResults()) {
			if ("permanentFailMethod".equals(r.getMethod().getMethodName())) {
				containsPermanentFail = true;
				break;
			}
		}
		Assert.assertTrue(containsPermanentFail, "Only the permanently failing test should remain in failedTests");

		// 3. skippedTests for sampleTestMethod must be removed
		Assert.assertEquals(skippedTests.getAllResults().size(), 0, "After cleanup, skippedTests should be empty");

		// 4. passedTests must still have sampleTestMethod
		Assert.assertEquals(passedTests.getAllResults().size(), 1, "passedTests should still have the passed test");
	}
}
