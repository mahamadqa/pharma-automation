package utils;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.asserts.IAssert;
import org.testng.asserts.SoftAssert;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.microsoft.playwright.Page;

import base.BaseTest;

/**
 * Custom SoftAssert that collects all failed assertions, captures screenshots at
 * the exact moment of failure, immediately attaches each failed assertion to
 * ExtentReports, and attaches a consolidated summary table when assertAll() or
 * tearDown is executed.
 */
public class CustomSoftAssert extends SoftAssert {

	private static final Logger log = LogManager.getLogger(CustomSoftAssert.class);

	private final List<FailedAssertion> failedAssertions = Collections.synchronizedList(new ArrayList<>());
	private int failureCounter = 0;
	private boolean summaryAttached = false;

	@Override
	public void onAssertFailure(IAssert<?> assertCommand, AssertionError ex) {
		failureCounter++;

		String message = assertCommand.getMessage() != null && !assertCommand.getMessage().trim().isEmpty()
				? assertCommand.getMessage().trim()
				: (ex != null && ex.getMessage() != null ? ex.getMessage().trim() : "Assertion failed");

		Object expected = assertCommand.getExpected();
		Object actual = assertCommand.getActual();
		String location = extractAssertionLocation();
		String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));

		// Capture screenshot at the exact moment of failure while the page is still open
		String screenshotBase64 = captureFailureScreenshot(message);

		FailedAssertion failure = new FailedAssertion(
				failureCounter, message, expected, actual, location, screenshotBase64, timestamp);
		failedAssertions.add(failure);

		log.error("❌ [Failed Assertion #{}] {} | Expected: [{}] Actual: [{}] at {}",
				failureCounter, message, expected, actual, location);

		// Log individual failure step immediately to Extent Report
		logIndividualFailureToExtent(failure);
	}

	@Override
	public void onAssertSuccess(IAssert<?> assertCommand) {
		// Only log to debug console if needed, NOT into Extent Reports.
		// The test assertions use failure descriptions (e.g. "[PO] DocStatus mismatch - Expected: ..."),
		// so logging them as "Assertion Passed" creates confusing and contradictory report entries.
		// As requested, only failed assertions are collected and attached to Extent Reports.
		String msg = assertCommand.getMessage();
		if (msg != null && !msg.trim().isEmpty()) {
			log.debug("Assertion passed: {}", msg.trim());
		}
	}

	/**
	 * Extracts the first calling stack trace frame outside testng, java, and assertion framework.
	 */
	private String extractAssertionLocation() {
		StackTraceElement[] stack = Thread.currentThread().getStackTrace();
		for (StackTraceElement elem : stack) {
			String cls = elem.getClassName();
			if (!cls.equals(CustomSoftAssert.class.getName())
					&& !cls.equals(FailedAssertion.class.getName())
					&& !cls.startsWith("org.testng.")
					&& !cls.startsWith("java.lang.")
					&& !cls.startsWith("base.BaseTest")
					&& !cls.startsWith("jdk.internal.")) {
				return elem.getFileName() + ":" + elem.getLineNumber() + " (" + elem.getMethodName() + ")";
			}
		}
		return "Unknown Location";
	}

	/**
	 * Captures full-page screenshot using Playwright and saves both to file and returns Base64.
	 */
	private String captureFailureScreenshot(String message) {
		try {
			Page page = BaseTest.getPage();
			if (page != null && !page.isClosed()) {
				String screenshotDir = System.getProperty("user.dir") + "/screenshots";
				Files.createDirectories(Paths.get(screenshotDir));
				String sanitizedMsg = message.replaceAll("[^a-zA-Z0-9_-]", "_");
				if (sanitizedMsg.length() > 30) {
					sanitizedMsg = sanitizedMsg.substring(0, 30);
				}
				String filePath = screenshotDir + "/AssertFail_" + failureCounter + "_" + sanitizedMsg + "_"
						+ System.currentTimeMillis() + ".png";

				byte[] screenshotBytes = page.screenshot(
						new Page.ScreenshotOptions().setPath(Paths.get(filePath)).setFullPage(true));
				log.info("📸 Assertion failure screenshot saved: {}", filePath);
				return Base64.getEncoder().encodeToString(screenshotBytes);
			}
		} catch (Exception e) {
			log.warn("Could not capture screenshot for assertion failure: {}", e.getMessage());
		}
		return null;
	}

	/**
	 * Logs individual failed assertion card and embeds screenshot into ExtentReport.
	 */
	private void logIndividualFailureToExtent(FailedAssertion failure) {
		ExtentTest test = ExtentManager.getTest();
		if (test != null) {
			StringBuilder html = new StringBuilder();
			html.append("<div style='background-color:#fff5f5; border:1px solid #feb2b2; border-left:4px solid #e53e3e; padding:10px 14px; border-radius:4px; margin:6px 0; font-family: -apple-system, BlinkMacSystemFont, \"Segoe UI\", Roboto, sans-serif;'>");
			html.append("  <span style='color:#c53030; font-weight:bold; font-size:13px;'>❌ Failed Assertion #")
					.append(failure.getIndex()).append("</span><br/>");
			html.append("  <div style='margin-top:4px; color:#2d3748;'><b>Details:</b> ")
					.append(escapeHtml(failure.getMessage())).append("</div>");

			if (failure.getExpected() != null || failure.getActual() != null) {
				html.append("  <div style='margin-top:4px; font-size:12px;'>");
				html.append("    <b>Expected:</b> <code style='color:#2b6cb0; background:#edf2f7; padding:2px 6px; border-radius:3px;'>")
						.append(escapeHtml(String.valueOf(failure.getExpected()))).append("</code> | ");
				html.append("    <b>Actual:</b> <code style='color:#c53030; background:#edf2f7; padding:2px 6px; border-radius:3px;'>")
						.append(escapeHtml(String.valueOf(failure.getActual()))).append("</code>");
				html.append("  </div>");
			}

			html.append("  <div style='margin-top:4px; font-size:11px; color:#718096;'>");
			html.append("    <b>Location:</b> ").append(escapeHtml(failure.getLocation()))
					.append(" &nbsp;|&nbsp; <b>Time:</b> ").append(failure.getTimestamp());
			html.append("  </div>");
			html.append("</div>");

			if (failure.getScreenshotBase64() != null) {
				test.fail(html.toString(),
						MediaEntityBuilder.createScreenCaptureFromBase64String(failure.getScreenshotBase64(),
								"Failed Assertion #" + failure.getIndex()).build());
			} else {
				test.fail(html.toString());
			}
		}
	}

	/**
	 * Attaches a consolidated, styled HTML summary table of all collected failed assertions
	 * into the Extent Report. Safe to call multiple times (idempotent).
	 */
	public synchronized void attachAllFailuresToReport() {
		if (summaryAttached || failedAssertions.isEmpty()) {
			return;
		}
		summaryAttached = true;

		ExtentTest test = ExtentManager.getTest();
		if (test != null) {
			StringBuilder html = new StringBuilder();
			html.append("<div style='background-color: #fffaf0; border: 1px solid #fbd38d; border-left: 5px solid #dd6b20; border-radius: 6px; padding: 14px; margin: 12px 0; font-family: -apple-system, BlinkMacSystemFont, \"Segoe UI\", Roboto, Helvetica, Arial, sans-serif;'>");
			html.append("  <div style='display: flex; justify-content: space-between; align-items: center; margin-bottom: 10px; border-bottom: 1px solid #feebc8; padding-bottom: 8px;'>");
			html.append("    <span style='font-size: 14px; font-weight: bold; color: #9c4221;'>");
			html.append("      ⚠️ Summary of Failed Assertions (Total: ").append(failedAssertions.size()).append(")");
			html.append("    </span>");
			html.append("    <span style='background-color: #e53e3e; color: #ffffff; padding: 2px 8px; border-radius: 10px; font-size: 11px; font-weight: bold;'>FAILED ASSERTIONS</span>");
			html.append("  </div>");

			html.append("  <table style='width: 100%; border-collapse: collapse; font-size: 12px; text-align: left; background: #ffffff; border-radius: 4px; overflow: hidden; box-shadow: 0 1px 2px rgba(0,0,0,0.06);'>");
			html.append("    <thead>");
			html.append("      <tr style='background: #2d3748; color: #ffffff;'>");
			html.append("        <th style='padding: 8px 10px; border: 1px solid #e2e8f0; width: 5%; text-align: center;'>#</th>");
			html.append("        <th style='padding: 8px 10px; border: 1px solid #e2e8f0; width: 45%;'>Assertion Message / Details</th>");
			html.append("        <th style='padding: 8px 10px; border: 1px solid #e2e8f0; width: 15%;'>Expected</th>");
			html.append("        <th style='padding: 8px 10px; border: 1px solid #e2e8f0; width: 15%;'>Actual</th>");
			html.append("        <th style='padding: 8px 10px; border: 1px solid #e2e8f0; width: 20%;'>Location</th>");
			html.append("      </tr>");
			html.append("    </thead>");
			html.append("    <tbody>");

			for (FailedAssertion f : failedAssertions) {
				html.append("      <tr style='border-bottom: 1px solid #edf2f7;'>");
				html.append("        <td style='padding: 8px 10px; border: 1px solid #e2e8f0; font-weight: bold; text-align: center; color: #e53e3e;'>")
						.append(f.getIndex()).append("</td>");
				html.append("        <td style='padding: 8px 10px; border: 1px solid #e2e8f0; color: #2d3748;'><b>")
						.append(escapeHtml(f.getMessage())).append("</b></td>");
				html.append("        <td style='padding: 8px 10px; border: 1px solid #e2e8f0; color: #2b6cb0; font-family: monospace; word-break: break-all;'>")
						.append(escapeHtml(String.valueOf(f.getExpected()))).append("</td>");
				html.append("        <td style='padding: 8px 10px; border: 1px solid #e2e8f0; color: #c53030; font-family: monospace; word-break: break-all;'>")
						.append(escapeHtml(String.valueOf(f.getActual()))).append("</td>");
				html.append("        <td style='padding: 8px 10px; border: 1px solid #e2e8f0; font-family: monospace; font-size: 11px; color: #718096;'>")
						.append(escapeHtml(f.getLocation())).append("</td>");
				html.append("      </tr>");
			}

			html.append("    </tbody>");
			html.append("  </table>");
			html.append("</div>");

			test.fail(html.toString());
		}
	}

	@Override
	public void assertAll() {
		assertAll("The following " + failedAssertions.size() + " assertion(s) failed:");
	}

	@Override
	public void assertAll(String message) {
		if (!failedAssertions.isEmpty()) {
			attachAllFailuresToReport();

			StringBuilder sb = new StringBuilder(message).append("\n");
			for (FailedAssertion f : failedAssertions) {
				sb.append("  ").append(f.getIndex()).append(") ")
						.append(f.getMessage())
						.append(" [Expected: ").append(f.getExpected())
						.append(" | Actual: ").append(f.getActual()).append("]")
						.append(" at ").append(f.getLocation()).append("\n");
			}
			throw new AssertionError(sb.toString());
		}
	}

	public List<FailedAssertion> getFailedAssertions() {
		return Collections.unmodifiableList(failedAssertions);
	}

	public boolean hasFailures() {
		return !failedAssertions.isEmpty();
	}

	public int getFailureCount() {
		return failedAssertions.size();
	}

	public void reset() {
		failedAssertions.clear();
		failureCounter = 0;
		summaryAttached = false;
	}

	private static String escapeHtml(String text) {
		if (text == null) {
			return "";
		}
		return text.replace("&", "&amp;")
				.replace("<", "&lt;")
				.replace(">", "&gt;")
				.replace("\"", "&quot;")
				.replace("'", "&#39;");
	}
}
