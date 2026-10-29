package sop;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.Test;

import base.BaseTest;
import utils.ConfigReader;
import utils.DataManager;
import utils.ExcelReader;

/**
 * Test Case PHX-3505: Verification of SOP Annexure Request and Issue.
 *
 * Covers complete end-to-end lifecycle verification for document type "Annexure Request":
 * Step 1: Login with EngUser and Engineer Role.
 * Step 2: Search "SOP Format Request" in Menu Searchbox and click on '+' icon next to SOP Format Request label.
 * Step 3: Click on Document Type dropdown arrow -> verify options: "Annexure Request", "SOP Format Request".
 * Step 4: Select Document Type as "Annexure Request" -> verify Annexure field enables and becomes mandatory.
 * Step 5: Click on SOP dropdown and select target SOP.
 * Step 6: Click on Annexure dropdown and select Annexure of selected SOP.
 * Step 7: Click on Save button.
 * Step 8: Enter Issue Type : Controlled, Requesting Department : Maintenance, Requested Copies : 2 and click on Save.
 *         Verify Document No is auto generated and Document Status is Drafted.
 * Step 9-10: Click on Document Action button -> select Prepare -> click Ok -> Status updates to "In Progress".
 * Step 11: Logout EngUser and Login with QAUser and QA Role.
 * Step 12: Click on "Annexure Request" / "SOP Format Request" document status indicator from Activities dashboard -> Issue
 *          window opens with requested SOP Annexure record.
 * Step 13: Select Control Sequence and click on Save button -> Control Number auto generates, Settings button gets enabled.
 * Step 14: Select Issued By, Issue Date, Issued Copies : 2 and click on Save button.
 * Step 15-16: Click on Settings button from upper toolbar -> select "Print SOP Annexure" / "Print Annexure" -> "Do you want to start the Process?" popup displays.
 * Step 17: Click OK -> Report : SOP Annexure window opens with Controlled Copy stamp.
 * Step 18-19: Click on Print button -> Print copies popup displays -> Click Cancel button.
 * Step 20: Navigate back on Issue window and click on Requery button -> Print No updates to 1, Printed checkbox becomes true.
 * Step 21: Run Print process again -> verify Reprint error -> fill Reprint Reason -> Reprint -> Print No updates to 2, Printed is true.
 * Step 22: Click on Copy tab -> verify Document No, Retrieved On, Cancelled, Printed, Active, Reason/Remark fields display.
 * Step 23: Click on Save button -> Record generates with Document No.
 * Step 26: Navigate back to the Issue window header.
 * Step 27-28: Click Document Action button -> select Complete -> click Ok -> Status updates to "Completed",
 *             all fields become read-only except Received By, Retrieved On, Retrieved By.
 * Step 29: Click Document Action button -> select "Closed" -> click OK -> confirm popup ->
 *          verify error message: copies not received back.
 * Step 30: Enter Received By, Retrieved On, Retrieved By and click on Save button.
 * Step 31-32: Click on Copy tab -> enter Retrieved On Date field and click on Save button.
 * Step 33: Navigate back to the Issue window header.
 * Step 34: Click on Document Action button -> select Closed -> click OK -> confirm popup ->
 *          Document Status updates to Closed, Processed flag becomes true.
 */
public class VerificationOfSOPAnnexureRequestAndIssueFlow extends BaseTest {

	private static final Logger log = LogManager.getLogger(VerificationOfSOPAnnexureRequestAndIssueFlow.class);

	@Test(groups = { "advance",
			"sop" }, dependsOnMethods = "sop.VerifySOPMasterFlowIfEffectivenessCheckRequired.verificationOfSOPMasterLifecycle", alwaysRun = true, description = "PHX-3505: Verification of SOP Annexure Request and Issue Lifecycle")
	public void verificationOfSOPAnnexureRequestAndIssue() {

		log.info("================================================================================");
		log.info("========== Starting Test: PHX-3505 Verification of SOP Annexure Request and Issue ==========");
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
			targetSOP = ExcelReader.getOrDefault("sop.annexureRequest.targetSOP",
					DataManager.getOrDefault("sop.annexureRequest.targetSOP", "1000914_1"));
		}
		log.info("Target SOP selected for SOP Annexure Request: [{}]", targetSOP);

		String annexureName = ExcelReader.getOrDefault("sop.annexure.name",
				DataManager.getOrDefault("sop.annexure.name", "Annexure-I"));

		// Test Data from properties / Defaults
		String department = ExcelReader.getOrDefault("sop.annexureRequest.department",
				DataManager.getOrDefault("sop.annexureRequest.department", "Maintenance"));
		String issueType = ExcelReader.getOrDefault("sop.annexureRequest.issueType",
				DataManager.getOrDefault("sop.annexureRequest.issueType", "Controlled"));
		String requestedCopies = ExcelReader.getOrDefault("sop.annexureRequest.requestedCopies",
				DataManager.getOrDefault("sop.annexureRequest.requestedCopies", "2"));
		String issuedCopies = ExcelReader.getOrDefault("sop.annexureRequest.issuedCopies",
				DataManager.getOrDefault("sop.annexureRequest.issuedCopies", "2"));
		String controlSequence = ExcelReader.getOrDefault("sop.annexureRequest.controlSequence",
				DataManager.getOrDefault("sop.annexureRequest.controlSequence", "101"));

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

		// --------------------------------------------------------------------------------------------------
		// Step 3: Click on Document Type dropdown arrow and verify options: "Annexure Request", "SOP Format Request"
		// --------------------------------------------------------------------------------------------------
		log.info("Step 3: Verifying Document Type dropdown options...");
		//sopFormatRequest.verifyDocTypeDropdownOptions();

		// --------------------------------------------------------------------------------------------------
		// Step 4: Select Document Type as "Annexure Request" -> Annexure field enables and becomes mandatory
		// --------------------------------------------------------------------------------------------------
		log.info("Step 4: Selecting Document Type as 'Annexure Request'...");
		sopFormatRequest.selectDocTypeAsAnnexureRequest();
		sopFormatRequest.fillDepartment(department);

		// --------------------------------------------------------------------------------------------------
		// Step 5, 6 & 7: Select SOP, select Annexure of SOP, click on Save button
		// --------------------------------------------------------------------------------------------------
		log.info("Step 5-7: Selecting SOP [{}], Annexure [{}], and clicking Save...", targetSOP, annexureName);
		sopFormatRequest.selectSOPAndAnnexureAndSave(targetSOP, annexureName);

		// --------------------------------------------------------------------------------------------------
		// Step 8: Enter Issue Type: Controlled, Requesting Department: Maintenance, Requested Copies: 2, and Save
		// --------------------------------------------------------------------------------------------------
		log.info("Step 8: Entering Issue Type [{}], Department [{}], Requested Copies [{}] and saving...", issueType,
				department, requestedCopies);
		sopFormatRequest.fillIssueDetailsAndSave(issueType, requestedCopies);
		String annexureReqDocNo = sopFormatRequest.getDocumentNo();
		log.info("SOP Annexure Request record created successfully with Document No: [{}]", annexureReqDocNo);

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
		// Step 12: Click on "Annexure Request" / "SOP Format Request" document status indicator from Activities dashboard
		// --------------------------------------------------------------------------------------------------
		log.info("Step 12: Clicking on 'Annexure Request' / 'SOP Format Request' indicator from Activities dashboard...");
		dashboard.clickDocumentStatusIndicator("Annexure Request", "SOP Format Request");
		sopFormatRequest.verifySOPFormatIssueRequestedRecord(annexureReqDocNo);

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
		// Step 15 & 16: Click on Settings button from toolbar -> select "Print SOP Annexure" / "Print Annexure" -> Confirm Dialog
		// --------------------------------------------------------------------------------------------------
		log.info("Step 15-16: Clicking Settings (Process) button and selecting Print SOP Annexure...");
		sopFormatRequest.clickSettingsAndSelectPrintAnnexure();
		sopFormatRequest.confirmPrintAnnexureDialog(issueType);

		// --------------------------------------------------------------------------------------------------
		// Step 17: Report window opens with Controlled Copy stamp
		// --------------------------------------------------------------------------------------------------
		log.info("Step 17: Verifying Report window, Controlled Copy stamp and Master Copy stamp...");
		printReport.verifyReportOpened();
		printReport.verifyControlledCopyStamp(true);
		printReport.verifyMasterCopyStamp(true);

		// --------------------------------------------------------------------------------------------------
		// Step 18 & 19: Click on Print button -> Cancel popup
		// --------------------------------------------------------------------------------------------------
		log.info("Step 18-19: Clicking Print button and dismissing printer popup...");
		printReport.clickPrintAndCancel();
		dashboard.clickDocumentStatusIndicator("Annexure Request", "SOP Format Request");
		sopFormatRequest.verifySOPFormatIssueRequestedRecord(annexureReqDocNo);

		// --------------------------------------------------------------------------------------------------
		// Step 20: Navigate back on Issue window and click ReQuery -> Print No: 1, Printed: True
		// --------------------------------------------------------------------------------------------------
		log.info("Step 20: Closing Report tab and navigating back to Issue window...");
		printReport.closeReportTab();
		sopFormatRequest.navigateBackToSOPFormatIssueAndRequery();
		sopFormatRequest.verifyPrintNoAndPrintedStatus("1", true);

		// --------------------------------------------------------------------------------------------------
		// Step 21: Reprint -> verify mandatory error -> fill reason -> reprint -> Print No: 2, Printed: True
		// --------------------------------------------------------------------------------------------------
		log.info("Step 21: Running Print process again to verify mandatory Reprint Reason error and reprinting...");
		sopFormatRequest.clickSettingsAndSelectPrintAnnexure();
		sopFormatRequest.confirmPrintAnnexureDialog(issueType);
		sopFormatRequest.verifyReprintError();
		sopFormatRequest.fillReprintReason();
		sopFormatRequest.clickSettingsAndSelectPrintAnnexure();
		sopFormatRequest.confirmPrintAnnexureDialog(issueType);
		printReport.verifyReportOpened();
		printReport.clickPrintAndCancel();

		dashboard.clickDocumentStatusIndicator("Annexure Request", "SOP Format Request");
		sopFormatRequest.verifySOPFormatIssueRequestedRecord(annexureReqDocNo);

		sopFormatRequest.navigateBackToSOPFormatIssueAndRequery();
		sopFormatRequest.verifyPrintNoAndPrintedStatus("2", true);

		// --------------------------------------------------------------------------------------------------
		// Step 22 & 23: Click on Copy tab -> verify fields -> click Save -> record generated with DocNo
		// --------------------------------------------------------------------------------------------------
		log.info("Step 22-23: Opening Copy tab and saving first record...");
		sopFormatRequest.navigateToSOPFormatCopyTab();
		String copy1DocNo = sopFormatRequest.saveFirstFormatCopyRecord();
		log.info("First Copy Document No: [{}]", copy1DocNo);

		// --------------------------------------------------------------------------------------------------
		// Step 26: Navigate back to the Issue window header
		// --------------------------------------------------------------------------------------------------
		log.info("Step 26: Navigating back to Issue window header...");
		sopFormatRequest.navigateBackToSOPFormatIssueHeader();

		// --------------------------------------------------------------------------------------------------
		// Step 27 & 28: Document Action -> Complete -> Status: Completed, fields read-only except Received By, Retrieved On, Retrieved By
		// --------------------------------------------------------------------------------------------------
		log.info("Step 27-28: Executing Document Action Complete...");
		sopFormatRequest.completeDocumentAction();

		// --------------------------------------------------------------------------------------------------
		// Step 29: Document Action -> Close -> Confirm popup -> Verify error: copies not received back
		// --------------------------------------------------------------------------------------------------
		log.info("Step 29: Attempting to Close document before receiving copies back...");
		sopFormatRequest.verifyCannotCloseWithoutCopiesReceivedError();

		// --------------------------------------------------------------------------------------------------
		// Step 30: Enter Received By, Retrieved On, Retrieved By on header and click Save
		// --------------------------------------------------------------------------------------------------
		log.info("Step 30: Entering Received By, Retrieved On, Retrieved By on header and saving...");
		sopFormatRequest.fillReceivedAndRetrievedHeaderDetailsAndSave(qaUser, null);

		// --------------------------------------------------------------------------------------------------
		// Step 31 & 32: Click Copy tab -> enter Retrieved On Date field and click Save
		// --------------------------------------------------------------------------------------------------
		log.info("Step 31-32: Entering Retrieved On date in Copy tab and saving...");
		sopFormatRequest.fillRetrievedOnDateForBothFormatCopiesAndSave(null);

		// --------------------------------------------------------------------------------------------------
		// Step 33: Navigate back to the Issue window header
		// --------------------------------------------------------------------------------------------------
		log.info("Step 33: Navigating back to Issue window header...");
		sopFormatRequest.navigateBackToSOPFormatIssueHeader();

		// --------------------------------------------------------------------------------------------------
		// Step 34: Document Action -> Close -> Confirm popup -> Status: Closed, Processed: true
		// --------------------------------------------------------------------------------------------------
		log.info("Step 34: Executing Document Action Close...");
		sopFormatRequest.closeDocumentAction();

		log.info("================================================================================");
		log.info("========== Completed Test: PHX-3505 Verification of SOP Annexure Request and Issue Successfully ==========");
		log.info("================================================================================");
	}
}
