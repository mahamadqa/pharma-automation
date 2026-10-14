package loginTest;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;

import utils.CustomSoftAssert;
import utils.ExtentManager;
import utils.FailedAssertion;

public class CustomSoftAssertTest {

	@Test
	public void testFailedAssertionsCollectionAndReporting() {
		// Initialize ExtentReports and create a test entry
		ExtentReports extent = ExtentManager.getInstance();
		ExtentTest test = ExtentManager.createTest("testFailedAssertionsCollectionAndReporting",
				"Unit test to verify collection and reporting of failed assertions");

		CustomSoftAssert sa = new CustomSoftAssert();

		// Assertion 1: Passing
		sa.assertEquals("actualVal", "actualVal", "Step 1: Value check should pass");

		// Assertion 2: Failing
		sa.assertEquals("actualDocStatus", "Completed", "Step 2: Document status mismatch");

		// Assertion 3: Passing
		sa.assertTrue(10 > 5, "Step 3: Number check should pass");

		// Assertion 4: Failing
		sa.assertTrue(false, "Step 4: PO line quantity mismatch - expected 100 but found 50");

		// Assertion 5: Failing
		sa.assertEquals(123.45, 999.99, "Step 5: Grand total mismatch");

		// Verify collected failures
		Assert.assertTrue(sa.hasFailures(), "CustomSoftAssert should have failures recorded");
		Assert.assertEquals(sa.getFailureCount(), 3, "Exactly 3 assertions should have failed");

		FailedAssertion f1 = sa.getFailedAssertions().get(0);
		Assert.assertEquals(f1.getIndex(), 1);
		Assert.assertTrue(f1.getMessage().contains("Document status mismatch"));
		Assert.assertEquals(f1.getExpected(), "Completed");
		Assert.assertEquals(f1.getActual(), "actualDocStatus");

		FailedAssertion f2 = sa.getFailedAssertions().get(1);
		Assert.assertEquals(f2.getIndex(), 2);
		Assert.assertTrue(f2.getMessage().contains("PO line quantity mismatch"));

		FailedAssertion f3 = sa.getFailedAssertions().get(2);
		Assert.assertEquals(f3.getIndex(), 3);
		Assert.assertTrue(f3.getMessage().contains("Grand total mismatch"));

		// Attach summary to ExtentReports
		sa.attachAllFailuresToReport();

		// Calling attachAllFailuresToReport a second time should be idempotent
		sa.attachAllFailuresToReport();

		// Verify assertAll() throws AssertionError containing all failures
		boolean threwAssertionError = false;
		try {
			sa.assertAll();
		} catch (AssertionError ae) {
			threwAssertionError = true;
			String errorMsg = ae.getMessage();
			Assert.assertTrue(errorMsg.contains("Document status mismatch"), "Error message should contain failure 1");
			Assert.assertTrue(errorMsg.contains("PO line quantity mismatch"), "Error message should contain failure 2");
			Assert.assertTrue(errorMsg.contains("Grand total mismatch"), "Error message should contain failure 3");
		}
		Assert.assertTrue(threwAssertionError, "assertAll() must throw AssertionError when assertions fail");

		// Flush report to verify HTML creation
		ExtentManager.flush();
	}
}
