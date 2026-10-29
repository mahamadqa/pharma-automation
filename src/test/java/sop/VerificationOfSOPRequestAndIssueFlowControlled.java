package sop;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.Test;

import base.BaseTest;
import utils.ConfigReader;
import utils.DataManager;
import utils.ExcelReader;

/**
 * Test Case PHX-3503: Verification of SOP Request and Issue.
 *
 * Covers end-to-end verification of SOP Request & SOP Issue lifecycle:
 * Step 1: Login with EngUser user and Engineer Role.
 * Step 2: Search "SOP Request" in Menu Searchbox and click on '+' icon next to SOP Request label.
 *         Verify all fields are displaying.
 * Step 3: Enter Document Type : SOP Request, SOP : Approved/Effective SOP, Department : Maintenance,
 *         Issue Type : Controlled, and click on Save. Document No auto-generated, Status is Drafted.
 * Step 4-5: Click Document Action, select Prepare, click Ok -> Status updates to In Progress.
 * Step 6: Logout EngUser and Login with QAUser and QA Role.
 * Step 7: Click on "SOP Request" document status indicator from Activities dashboard -> SOP Issue window
 *         opens with requested SOP record.
 * Step 8: Enter Control Sequence, Issue Date, Issued By and Save -> Print No : 0, Control Sequence : Read only,
 *         Printed : False.
 * Step 9-10: Click on Settings button from toolbar -> select Print SOP -> Confirm "Do you want to start the Process?".
 * Step 11: Report : SOP window opens with Master Copy stamp and Controlled Copy stamp.
 * Step 12-13: Click Print button -> Print Copies popup -> Click Cancel to close popup.
 * Step 14: Navigate back on SOP Issue window and click ReQuery -> Print No updates to 1, Printed checkbox becomes true.
 * Step 15-16: Document Action -> Complete -> Document Status updates to Completed, Processed flag becomes true.
 * Step 17-18: Document Action -> Close -> Confirm popup -> Document Status updates to Closed,
 *             Retrieved On auto set to current date, Retrieved By auto set to current user.
 */
public class VerificationOfSOPRequestAndIssueFlowControlled extends BaseTest {

	private static final Logger log = LogManager.getLogger(VerificationOfSOPRequestAndIssueFlowControlled.class);

	@Test(groups = { "advance",
			"sop" },dependsOnMethods = "sop.VerifySOPMasterFlowIfEffectivenessCheckRequired.verificationOfSOPMasterLifecycle", alwaysRun = true, description = "PHX-3503: Verification of SOP Request and Issue Lifecycle")
	public void verificationOfSOPRequestAndIssue() {

		log.info("================================================================================");
		log.info("========== Starting Test: PHX-3503 Verification of SOP Request and Issue ==========");
		log.info("================================================================================");

		// Initialize Test Data
		DataManager.initData();
		String client = ConfigReader.get("client");
		String password = ConfigReader.get("Password");

		// Credentials
		String engUser = ConfigReader.get("SOPInitiator");
		String engUserRole = ConfigReader.get("SOPInitiatorRole");
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
			targetSOP = ExcelReader.getOrDefault("sop.request.targetSOP",
					DataManager.getOrDefault("sop.request.targetSOP", "1000703"));
		}
		log.info("Target SOP selected for SOP Request: [{}]", targetSOP);

		// SOP Request Data from Properties / Defaults
		String docType = ExcelReader.getOrDefault("sop.request.docType",
				DataManager.getOrDefault("sop.request.docType", "SOP Request"));
		String department = ExcelReader.getOrDefault("sop.request.department",
				DataManager.getOrDefault("sop.request.department", "Maintenance"));
		String issueType = ExcelReader.getOrDefault("sop.request.issueType",
				DataManager.getOrDefault("sop.request.issueType", "Controlled"));
		String controlSequence = ExcelReader.getOrDefault("sop.request.controlSequence",
				DataManager.getOrDefault("sop.request.controlSequence", "101"));
		String remark = ExcelReader.getOrDefault("sop.request.remark",
				DataManager.getOrDefault("sop.request.remark", "Playwright Automation SOP Request"));

		// --------------------------------------------------------------------------------------------------
		// Step 1: Login with EngUser user and Engineer Role
		// --------------------------------------------------------------------------------------------------
		log.info("Step 1: Logging in with EngUser [{}] and Role [{}]...", engUser, engUserRole);
		loginPage.login(engUser, password);
		clientAndRole.selectClientAndRole(client, engUserRole);
		dashboard.verifyLoginDetails();

		// --------------------------------------------------------------------------------------------------
		// Step 2: Search "SOP Request" in Menu Searchbox and click '+' icon next to SOP Request label
		// --------------------------------------------------------------------------------------------------
		log.info("Step 2: Searching 'SOP Request' in menu searchbox and clicking '+' icon...");
		sopRequest.navigateToSOPRequestNewRecord();
		sopRequest.verifySOPRequestFieldsDisplaying(engUser);

		// --------------------------------------------------------------------------------------------------
		// Step 3: Enter details (DocType, SOP, Department, Issue Type, Remark) and Save
		// --------------------------------------------------------------------------------------------------
		log.info("Step 3: Entering SOP Request details and saving...");
		sopRequest.fillSOPRequestAndSave(docType, targetSOP, department, issueType, remark);
		String sopReqDocNo = sopRequest.getDocumentNo();
		log.info("SOP Request record created successfully with Document No: [{}]", sopReqDocNo);

		// --------------------------------------------------------------------------------------------------
		// Step 4 & 5: Document Action -> Prepare -> Status updates to In Progress
		// --------------------------------------------------------------------------------------------------
		log.info("Step 4-5: Executing Document Action Prepare...");
		sopRequest.prepareDocumentAction();

		// --------------------------------------------------------------------------------------------------
		// Step 6: Login with QAUser and QA Role
		// --------------------------------------------------------------------------------------------------
		log.info("Step 6: Logging out EngUser and logging in with QAUser [{}] and Role [{}]...", qaUser, qaUserRole);
		dashboard.clickLogout();
		loginPage.login(qaUser, password);
		clientAndRole.selectClientAndRole(client, qaUserRole);
		dashboard.verifyLoginDetails();

		// --------------------------------------------------------------------------------------------------
		// Step 7: Click on "SOP Request" document status indicator from Activities dashboard
		// --------------------------------------------------------------------------------------------------
		log.info("Step 7: Clicking on 'SOP Request' document status indicator from Activities dashboard...");
		dashboard.clickDocumentStatusIndicator("SOP Request");
		sopRequest.verifySOPIssueRequestedRecord(sopReqDocNo);

		// --------------------------------------------------------------------------------------------------
		// Step 8: Enter Control Sequence, Issue Date, Issued By and click on Save
		// --------------------------------------------------------------------------------------------------
		log.info("Step 8: Entering Control Sequence, Issue Date, Issued By and saving...");
		sopRequest.fillSOPIssueDetailsAndSave(controlSequence, null, qaUser);

		// --------------------------------------------------------------------------------------------------
		// Step 9 & 10: Click on Settings button from toolbar -> Print SOP -> Confirm Dialog
		// --------------------------------------------------------------------------------------------------
		log.info("Step 9-10: Clicking Settings (Process) button and selecting Print SOP...");
		sopRequest.clickSettingsAndSelectPrintSOP();
		sopRequest.confirmPrintControlledSOPDialog();

		// --------------------------------------------------------------------------------------------------
		// Step 11: Report : SOP window opens with Master Copy stamp and Controlled Copy stamp
		// --------------------------------------------------------------------------------------------------
		log.info("Step 11: Verifying Report : SOP window, Master Copy stamp and Controlled Copy stamp...");
		printReport.verifyReportOpened();
		printReport.verifyMasterCopyStamp(true);
		printReport.verifyControlledCopyStamp(true);

		// --------------------------------------------------------------------------------------------------
		// Step 12 & 13: Click on Print button -> Cancel popup
		// --------------------------------------------------------------------------------------------------
		log.info("Step 12-13: Clicking Print button and dismissing printer popup...");
		printReport.clickPrintAndCancel();
		/*Refresh page*/
		dashboard.clickDocumentStatusIndicator("SOP Request");
		sopRequest.verifySOPIssueRequestedRecord(sopReqDocNo);
		
		// --------------------------------------------------------------------------------------------------
		// Step 14: Navigate back on SOP Issue window and click ReQuery -> Print No: 1, Printed: True
		// --------------------------------------------------------------------------------------------------
		log.info("Step 14: Closing Report tab and navigating back to SOP Issue window...");
		printReport.closeReportTab();
		sopRequest.navigateBackToSOPIssueAndRequery();
		sopRequest.verifyPrintNoAndPrintedStatus("1", true);
		
		//Reprint
		sopRequest.clickSettingsAndSelectPrintSOP();
		sopRequest.confirmPrintControlledSOPDialog();
		sopRequest.verifyReprintError();
		sopRequest.fillReprintReason();
		sopRequest.clickSettingsAndSelectPrintSOP();
		sopRequest.confirmPrintControlledSOPDialog();
		printReport.verifyReportOpened();
		printReport.clickPrintAndCancel();
		/*Refresh page*/
		dashboard.clickDocumentStatusIndicator("SOP Request");
		sopRequest.verifySOPIssueRequestedRecord(sopReqDocNo);
		
		sopRequest.navigateBackToSOPIssueAndRequery();
		sopRequest.verifyPrintNoAndPrintedStatus("2", true);

		// --------------------------------------------------------------------------------------------------
		// Step 15 & 16: Document Action -> Complete -> Status: Completed, Processed: true
		// --------------------------------------------------------------------------------------------------
		log.info("Step 15-16: Executing Document Action Complete...");
		sopRequest.completeDocumentAction();

		// --------------------------------------------------------------------------------------------------
		// Step 17 & 18: Document Action -> Close -> Confirm Dialog -> Status: Closed, Retrieved On & By set
		// --------------------------------------------------------------------------------------------------
		log.info("Step 17-18: Executing Document Action Close...");
		sopRequest.closeDocumentAction(qaUser);

		log.info("================================================================================");
		log.info("========== Completed Test: PHX-3503 Verification of SOP Request and Issue Successfully ==========");
		log.info("================================================================================");
	}
}
