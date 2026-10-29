package utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class TestClassTrackerTest {

	@BeforeMethod
	@AfterMethod
	public void cleanUp() {
		TestClassTracker.reset();
	}

	@Test
	public void testClassTrackingLifecycle() {
		// Simulate Class 1: all pass -> Class PASSED
		TestClassTracker.ClassSummary cs1 = TestClassTracker.getOrCreate("sop.VerificationOfSOPMasterFlow");
		cs1.recordMethodStart("testLifecycle", 1000);
		cs1.recordMethodSuccess("testLifecycle", 3000);

		// Simulate Class 2: 1 pass, 1 fail -> Class FAILED
		TestClassTracker.ClassSummary cs2 = TestClassTracker.getOrCreate("baseModules.CreateGRNFromPOFlow");
		cs2.recordMethodStart("testCreatePO", 1000);
		cs2.recordMethodSuccess("testCreatePO", 2000);
		cs2.recordMethodStart("testCreateGRN", 2000);
		cs2.recordMethodFailure("testCreateGRN", 4000);

		// Simulate Class 3: 1 skipped -> Class SKIPPED
		TestClassTracker.ClassSummary cs3 = TestClassTracker.getOrCreate("sop.VerificationOfSOPRequestAndIssueFlowForDisplay");
		cs3.recordMethodStart("testRequest", 1000);
		cs3.recordMethodSkipped("testRequest", 1500);

		// Assertions
		Assert.assertEquals(TestClassTracker.getTotalClasses(), 3, "Total classes should be 3");
		Assert.assertEquals(TestClassTracker.getPassedClassesCount(), 1, "Passed classes should be 1");
		Assert.assertEquals(TestClassTracker.getFailedClassesCount(), 1, "Failed classes should be 1");
		Assert.assertEquals(TestClassTracker.getSkippedClassesCount(), 1, "Skipped classes should be 1");

		double passRate = TestClassTracker.getClassPassRate();
		Assert.assertEquals(Math.round(passRate), 33, "Pass rate should be ~33%");

		Assert.assertEquals(cs1.getStatus(), "PASS");
		Assert.assertEquals(cs2.getStatus(), "FAIL");
		Assert.assertEquals(cs3.getStatus(), "SKIP");

		Assert.assertEquals(cs1.getTotalMethods(), 1);
		Assert.assertEquals(cs2.getTotalMethods(), 2);
		Assert.assertEquals(cs2.getPassedMethods(), 1);
		Assert.assertEquals(cs2.getFailedMethods(), 1);
	}

	@Test
	public void testHtmlReportEnrichment() throws IOException {
		// Create a temporary minimal extent report HTML
		Path tempReport = Files.createTempFile("ExtentReport_Test_", ".html");
		String minimalHtml = "<!DOCTYPE html><html><head><style type=\"text/css\"></style></head><body>"
				+ "<ul class=\"nav-right\"><li><a href=\"#\">Playwright</a></li></ul>"
				+ "<div class=\"test-wrapper row view test-view\">"
				+ "<ul class=\"test-list-item\">"
				+ "<li class=\"test-item\" status=\"pass\" tag=\"sop VerificationOfSOPMasterFlow\">"
				+ "<p class=\"name\">VerificationOfSOPMasterFlow - verificationOfSOPMasterLifecycle</p>"
				+ "</li>"
				+ "</ul></div>"
				+ "<div class=\"container-fluid p-4 view dashboard-view\">"
				+ "<div class=\"row\"><p>Dashboard</p></div>"
				+ "<div class=\"card-header\"><p>System/Environment</p></div>"
				+ "<table><tbody><tr><td>OS</td><td>Linux</td></tr></tbody></table>"
				+ "</div></body></html>";

		Files.writeString(tempReport, minimalHtml);

		// TestClassTracker with 1 passed class
		TestClassTracker.ClassSummary cs = TestClassTracker.getOrCreate("sop.VerificationOfSOPMasterFlow");
		cs.recordMethodStart("verificationOfSOPMasterLifecycle", 1000);
		cs.recordMethodSuccess("verificationOfSOPMasterLifecycle", 5000);

		boolean enriched = HtmlReportEnricher.enrich(tempReport.toString());
		Assert.assertTrue(enriched, "Report should be enriched successfully");

		String content = Files.readString(tempReport);
		Assert.assertTrue(content.contains("Test Classes Executive Summary"), "Should contain executive summary");
		Assert.assertTrue(content.contains("Total Test Classes"), "Should contain Total Test Classes card");
		Assert.assertTrue(content.contains("Classes Passed"), "Should contain Classes Passed card");
		Assert.assertTrue(content.contains("Classes Failed"), "Should contain Classes Failed card");
		Assert.assertTrue(content.contains("Test Classes Execution Breakdown"), "Should contain breakdown table");
		Assert.assertTrue(content.contains("VerificationOfSOPMasterFlow"), "Should contain class name");

		Files.deleteIfExists(tempReport);
	}
}
