package sop;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.Test;

import base.BaseTest;
import utils.ConfigReader;
import utils.DataManager;
import utils.ExcelReader;

/**
 * Test Case PHX-3504: Verification of SOP Format Request and Issue.
 *
 * Covers complete end-to-end lifecycle verification:
 * Step 1: Login with EngUser and Engineer Role.
 * Step 2: Search "SOP Format Request" in Menu Searchbox and click on '+' icon next to SOP Format Request label.
 *         Verify all fields are displaying and default values/states are correct.
 * Step 3: Click on Document Type dropdown arrow -> verify options: "Annexure Request", "SOP Format Request".
 * Step 4: Select Document Type as "SOP Format Request" -> verify Format field enables and becomes mandatory.
 * Step 5: Click on SOP dropdown and select any SOP.
 * Step 6: Click on Format dropdown and select Format of selected SOP.
 * Step 7: Click on Save button.
 * Step 8: Enter Issue Type : Controlled, Requesting Department : Maintenance, Requested Copies : 2 and click on Save.
 *         Verify Document No is auto generated and Document Status is Drafted.
 * Step 9-10: Click on Document Action button -> select Prepare -> click Ok -> Status updates to "In Progress".
 * Step 11: Logout EngUser and Login with QAUser and QA Role.
 * Step 12: Click on "SOP Format Request" document status indicator from Activities dashboard -> SOP Format Issue
 *          window opens with requested SOP Format record.
 * Step 13: Select Control Sequence and click on Save button -> Control Number auto generates, Settings button gets enabled.
 * Step 14: Select Issued By, Issue Date, Issued Copies : 2 and click on Save button.
 * Step 15-16: Click on Settings button from upper toolbar -> select "Print SOP Format" -> "Do you want to start the Process?" popup displays.
 * Step 17: Click OK -> Report : SOP Format window opens with Controlled Copy stamp.
 * Step 18-19: Click on Print button -> Print copies popup displays -> Click Cancel button.
 * Step 20: Navigate back on SOP Format Issue window and click on Requery button -> Print No updates to 1, Printed checkbox becomes true.
 * Step 21: Run Print SOP Format process again -> verify Reprint error -> fill Reprint Reason -> Reprint -> Print No updates to 2, Printed is true.
 * Step 22: Click on SOP Format Copy tab -> verify Document No, Retrieved On, Cancelled, Printed, Active, Reason/Remark fields display.
 * Step 23: Click on Save button -> Record generates with Document No.
 * Step 24-25: Click on '+' button from toolbar -> New record displays -> Click Save button -> Second record generates with Document No.
 * Step 26: Navigate back to the SOP Format Issue window.
 * Step 27-28: Click Document Action button -> select Complete -> click Ok -> Status updates to "Completed",
 *             all fields become read-only except Received By, Retrieved On, Retrieved By.
 * Step 29: Click Document Action button -> select "Closed" -> click OK -> confirm popup ->
 *          verify error message: "All format copy are not received back".
 * Step 30: Enter Received By, Retrieved On, Retrieved By and click on Save button.
 * Step 31-32: Click on SOP Format Copy tab -> enter Retrieved On Date field for both records and click on Save button.
 * Step 33: Navigate back to the SOP Format Issue window.
 * Step 34: Click on Document Action button -> select Closed -> click OK -> confirm popup ->
 *          Document Status updates to Closed, Processed flag becomes true.
 */
public class VerificationOfSOPFormatRequestAndIssueFlow extends BaseTest {

	private static final Logger log = LogManager.getLogger(VerificationOfSOPFormatRequestAndIssueFlow.class);

	@Test(groups = { "advance",
			"sop" }, dependsOnMethods = "sop.VerifySOPMasterFlowIfEffectivenessCheckRequired.verificationOfSOPMasterLifecycle", alwaysRun = true, description = "PHX-3504: Verification of SOP Format Request and Issue Lifecycle")
	public void verificationOfSOPFormatRequestAndIssue() {

		log.info("================================================================================");
		log.info("========== Starting Test: PHX-3504 Verification of SOP Format Request and Issue ==========");
		log.info("================================================================================");

		// Initialize Test Data
		DataManager.initData();
		String client = ConfigReader.get("client");
		String password = ConfigReader.get("Password");

		// Credentials
		String engUser = ConfigReader.get("SOPInitiator");
		String engUserRole = ConfigReader.get("SuperuserRole");
		String qaUser = ConfigReader.get("SOPQAReviewer");
		String qaUserRole = ConfigReader.get("SOPQAReviewerRole");

		// Target SOP: Dynamically taken from root class in testng.xml (VerifySOPMasterFlowIfEffectivenessCheckRequired)
		String targetSOP = VerifySOPMasterFlowIfEffectivenessCheckRequired.getEffectiveRevisedSOPNo();
		if (targetSOP == null || targetSOP.trim().isEmpty()) {
			targetSOP = VerificationOfSOPMasterFlow.getEffectiveRevisedSOPNo();
		}
		if (targetSOP == null || targetSOP.trim().isEmpty()) {
			targetSOP = System.getProperty("effective.revised.sop.no");
		}
		if (targetSOP == null || targetSOP.trim().isEmpty()) {
			targetSOP = ExcelReader.getOrDefault("sop.formatRequest.targetSOP",
					DataManager.getOrDefault("sop.formatRequest.targetSOP", "1000914_1"));
		}
		log.info("Target SOP selected for SOP Format Request: [{}]", targetSOP);

		String formatName = ExcelReader.getOrDefault("sop.format.name",
				DataManager.getOrDefault("sop.format.name", "PAINT RECORD"));

		// Test Data from properties / Defaults
		String department = ExcelReader.getOrDefault("sop.formatRequest.department",
				DataManager.getOrDefault("sop.formatRequest.department", "Maintenance"));
		String issueType = ExcelReader.getOrDefault("sop.formatRequest.issueType",
				DataManager.getOrDefault("sop.formatRequest.issueType", "Controlled"));
		String requestedCopies = ExcelReader.getOrDefault("sop.formatRequest.requestedCopies",
				DataManager.getOrDefault("sop.formatRequest.requestedCopies", "2"));
		String issuedCopies = ExcelReader.getOrDefault("sop.formatRequest.issuedCopies",
				DataManager.getOrDefault("sop.formatRequest.issuedCopies", "2"));
		String controlSequence = ExcelReader.getOrDefault("sop.formatRequest.controlSequence",
				DataManager.getOrDefault("sop.formatRequest.controlSequence", "101"));

		// --------------------------------------------------------------------------------------------------
		// Step 1: Login with EngUser user and Engineer Role
		// --------------------------------------------------------------------------------------------------
		log.info("Step 1: Logging in with EngUser [{}] and Role [{}]...", engUser, engUserRole);
		loginPage.login(engUser, password);
		clientAndRole.selectClientAndRole(client, engUserRole);
		dashboard.verifyLoginDetails();

		// --------------------------------------------------------------------------------------------------
		// Step 2: Search "SOP Format Request" in Menu Searchbox and click '+' icon next to SOP Format Request label
		// --------------------------------------------------------------------------------------------------
		log.info("Step 2: Searching 'SOP Format Request' in menu searchbox and clicking '+' icon...");
		sopFormatRequest.navigateToSOPFormatRequestNewRecord();
		//sopFormatRequest.verifySOPFormatRequestFieldsDisplaying(engUser);

		// --------------------------------------------------------------------------------------------------
		// Step 3: Click on Document Type dropdown arrow and verify options: "Annexure Request", "SOP Format Request"
		// --------------------------------------------------------------------------------------------------
		log.info("Step 3: Verifying Document Type dropdown options...");
		//sopFormatRequest.verifyDocTypeDropdownOptions();

		// --------------------------------------------------------------------------------------------------
		// Step 4: Select Document Type as "SOP Format Request" -> Format field enables and becomes mandatory
		// --------------------------------------------------------------------------------------------------
		log.info("Step 4: Selecting Document Type as 'SOP Format Request'...");
		sopFormatRequest.selectDocTypeAsSOPFormatRequest();
		sopFormatRequest.fillDepartment(department);
		// --------------------------------------------------------------------------------------------------
		// Step 5, 6 & 7: Select SOP, select Format of SOP, click on Save button
		// --------------------------------------------------------------------------------------------------
		log.info("Step 5-7: Selecting SOP [{}], Format [{}], and clicking Save...", targetSOP, formatName);
		sopFormatRequest.selectSOPAndFormatAndSave(targetSOP, formatName);

		// --------------------------------------------------------------------------------------------------
		// Step 8: Enter Issue Type: Controlled, Requesting Department: Maintenance, Requested Copies: 2, and Save
		// --------------------------------------------------------------------------------------------------
		log.info("Step 8: Entering Issue Type [{}], Department [{}], Requested Copies [{}] and saving...", issueType,
				department, requestedCopies);
		sopFormatRequest.fillIssueDetailsAndSave(issueType, requestedCopies);
		String sopFmtReqDocNo = sopFormatRequest.getDocumentNo();
		log.info("SOP Format Request record created successfully with Document No: [{}]", sopFmtReqDocNo);

		// --------------------------------------------------------------------------------------------------
		// Step 9 & 10: Document Action -> Prepare -> Status updates to In Progress
		// --------------------------------------------------------------------------------------------------
		log.info("Step 9-10: Executing Document Action Prepare...");
		sopFormatRequest.prepareDocumentAction();

		// --------------------------------------------------------------------------------------------------
		// Step 11: Logout EngUser and Login with QAUser and QA Role
		// --------------------------------------------------------------------------------------------------
		log.info("Step 11: Logging out EngUser and logging in with QAUser [{}] and Role [{}]...", qaUser, qaUserRole);
		dashboard.clickLogout();
		loginPage.login(qaUser, password);
		clientAndRole.selectClientAndRole(client, qaUserRole);
		dashboard.verifyLoginDetails();

		// --------------------------------------------------------------------------------------------------
		// Step 12: Click on "SOP Format Request" document status indicator from Activities dashboard
		// --------------------------------------------------------------------------------------------------
		log.info("Step 12: Clicking on 'SOP Format Request' document status indicator from Activities dashboard...");
		dashboard.clickDocumentStatusIndicator("SOP Format Request");
		sopFormatRequest.verifySOPFormatIssueRequestedRecord(sopFmtReqDocNo);

		// --------------------------------------------------------------------------------------------------
		// Step 13: Select Control Sequence and click on Save -> Control Number auto generates, Settings button enabled
		// --------------------------------------------------------------------------------------------------
		log.info("Step 13: Selecting Control Sequence and saving...");
		sopFormatRequest.selectControlSequenceAndSave(controlSequence);

		// --------------------------------------------------------------------------------------------------
		// Step 14: Select Issued By, Issue Date, Issued Copies: 2 and click on Save
		// --------------------------------------------------------------------------------------------------
		log.info("Step 14: Entering Issued By [{}], Issue Date, Issued Copies [{}] and saving...", qaUser, issuedCopies);
		sopFormatRequest.fillIssuedDetailsAndSave(qaUser, null, issuedCopies);

		// --------------------------------------------------------------------------------------------------
		// Step 15 & 16: Click on Settings button from toolbar -> select "Print SOP Format" -> Confirm Dialog
		// --------------------------------------------------------------------------------------------------
		log.info("Step 15-16: Clicking Settings (Process) button and selecting Print SOP Format...");
		sopFormatRequest.clickSettingsAndSelectPrintSOPFormat();
		sopFormatRequest.confirmPrintSOPFormatDialog(issueType);

		// --------------------------------------------------------------------------------------------------
		// Step 17: Report : SOP Format window opens with Controlled Copy stamp
		// --------------------------------------------------------------------------------------------------
		log.info("Step 17: Verifying Report : SOP Format window and Controlled Copy stamp...");
		printReport.verifyReportOpened();
		printReport.verifyControlledCopyStamp(true);
		printReport.verifyMasterCopyStamp(true);
		// --------------------------------------------------------------------------------------------------
		// Step 18 & 19: Click on Print button -> Cancel popup
		// --------------------------------------------------------------------------------------------------
		log.info("Step 18-19: Clicking Print button and dismissing printer popup...");
		printReport.clickPrintAndCancel();
		dashboard.clickDocumentStatusIndicator("SOP Format Request");
		sopFormatRequest.verifySOPFormatIssueRequestedRecord(sopFmtReqDocNo);

		// --------------------------------------------------------------------------------------------------
		// Step 20: Navigate back on SOP Format Issue window and click ReQuery -> Print No: 1, Printed: True
		// --------------------------------------------------------------------------------------------------
		log.info("Step 20: Closing Report tab and navigating back to SOP Format Issue window...");
		
		printReport.closeReportTab();
		sopFormatRequest.navigateBackToSOPFormatIssueAndRequery();
		sopFormatRequest.verifyPrintNoAndPrintedStatus("1", true);

		// --------------------------------------------------------------------------------------------------
		// Step 21: Reprint -> verify mandatory error -> fill reason -> reprint -> Print No: 2, Printed: True
		// --------------------------------------------------------------------------------------------------
		log.info("Step 21: Running Print SOP Format again to verify mandatory Reprint Reason error and reprinting...");
		sopFormatRequest.clickSettingsAndSelectPrintSOPFormat();
		sopFormatRequest.confirmPrintSOPFormatDialog(issueType);
		sopFormatRequest.verifyReprintError();
		sopFormatRequest.fillReprintReason();
		sopFormatRequest.clickSettingsAndSelectPrintSOPFormat();
		sopFormatRequest.confirmPrintSOPFormatDialog(issueType);
		printReport.verifyReportOpened();
		printReport.clickPrintAndCancel();
		/*Refresh page*/
		dashboard.clickDocumentStatusIndicator("SOP Format Request");
		sopFormatRequest.verifySOPFormatIssueRequestedRecord(sopFmtReqDocNo);

		sopFormatRequest.navigateBackToSOPFormatIssueAndRequery();
		sopFormatRequest.verifyPrintNoAndPrintedStatus("2", true);

		// --------------------------------------------------------------------------------------------------
		// Step 22 & 23: Click on SOP Format Copy tab -> verify fields -> click Save -> record generated with DocNo
		// --------------------------------------------------------------------------------------------------
		log.info("Step 22-23: Opening SOP Format Copy tab and saving first record...");
		sopFormatRequest.navigateToSOPFormatCopyTab();
		String copy1DocNo = sopFormatRequest.saveFirstFormatCopyRecord();
		log.info("First SOP Format Copy Document No: [{}]", copy1DocNo);

		// --------------------------------------------------------------------------------------------------
		// Step 26: Navigate back to the SOP Format Issue window
		// --------------------------------------------------------------------------------------------------
		log.info("Step 26: Navigating back to SOP Format Issue window header...");
		sopFormatRequest.navigateBackToSOPFormatIssueHeader();

		// --------------------------------------------------------------------------------------------------
		// Step 27 & 28: Document Action -> Complete -> Status: Completed, fields read-only except Received By, Retrieved On, Retrieved By
		// --------------------------------------------------------------------------------------------------
		log.info("Step 27-28: Executing Document Action Complete...");
		sopFormatRequest.completeDocumentAction();

		// --------------------------------------------------------------------------------------------------
		// Step 29: Document Action -> Close -> Confirm popup -> Verify error: 'All format copy are not received back'
		// --------------------------------------------------------------------------------------------------
		log.info("Step 29: Attempting to Close document before receiving copies back...");
		sopFormatRequest.verifyCannotCloseWithoutCopiesReceivedError();

		// --------------------------------------------------------------------------------------------------
		// Step 30: Enter Received By, Retrieved On, Retrieved By on header and click Save
		// --------------------------------------------------------------------------------------------------
		log.info("Step 30: Entering Received By, Retrieved On, Retrieved By on header and saving...");
		sopFormatRequest.fillReceivedAndRetrievedHeaderDetailsAndSave(qaUser, null);

		// --------------------------------------------------------------------------------------------------
		// Step 31 & 32: Click SOP Format Copy tab -> enter Retrieved On Date field for both records and click Save
		// --------------------------------------------------------------------------------------------------
		log.info("Step 31-32: Entering Retrieved On date for both copies in SOP Format Copy tab and saving...");
		sopFormatRequest.fillRetrievedOnDateForBothFormatCopiesAndSave(null);

		// --------------------------------------------------------------------------------------------------
		// Step 33: Navigate back to the SOP Format Issue window
		// --------------------------------------------------------------------------------------------------
		log.info("Step 33: Navigating back to SOP Format Issue window header...");
		sopFormatRequest.navigateBackToSOPFormatIssueHeader();

		// --------------------------------------------------------------------------------------------------
		// Step 34: Document Action -> Close -> Confirm popup -> Status: Closed, Processed: true
		// --------------------------------------------------------------------------------------------------
		log.info("Step 34: Executing Document Action Close...");
		sopFormatRequest.closeDocumentAction();

		log.info("================================================================================");
		log.info("========== Completed Test: PHX-3504 Verification of SOP Format Request and Issue Successfully ==========");
		log.info("================================================================================");
	}
}
