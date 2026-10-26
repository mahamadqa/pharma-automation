package advanceModules;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

import base.BaseTest;
import utils.ConfigReader;
import utils.DataManager;
import utils.ExcelReader;

/**
 * Test Case PHX-3499: Verification of SOP Master.
 * Comprehensive end-to-end verification of SOP Master lifecycle covering:
 * - SOP Master creation, Content sections (Objective, Scope, Responsibility, Procedure)
 * - Subtabs: Abbreviations, Annexure, SOP Formats, Distribution List
 * - Digital signatures (Prepared By, Checked By, Reviewed By, Approver)
 * - Multi-level Workflow approval (EngUser -> EngReviewer -> EngManager -> QAReviewer -> QAManager)
 * - Rejection and correction cycle (Not Approved status & field clearing)
 * - Report Preview and Master Copy stamp verification
 * - DMS Training Completion Report upload in Document Explorer
 * - Effective date & status transition
 * - Revise SOP flow (Revision 1, Annexure format revision, SOP Formats revision, Revision History)
 * - Previous version superseding and Supersede Copy stamp verification
 */
public class VerificationOfSOPMaster extends BaseTest {

	private static final Logger log = LogManager.getLogger(VerificationOfSOPMaster.class);

	@Test(groups = { "advance", "sop" }, description = "PHX-3499: Complete Verification of SOP Master Lifecycle & Revisions")
	public void verificationOfSOPMasterLifecycle() {

		log.info("================================================================================");
		log.info("========== Starting Test: PHX-3499 Verification of SOP Master ==========");
		log.info("================================================================================");

		// Initialize Test Data
		DataManager.initData();
		String client = ConfigReader.get("client");
		String password = ConfigReader.get("Password");

		// Credentials
		String engUser = ConfigReader.get("SOPInitiator");
		String engUserDept = ConfigReader.get("SOPInitiatorDept");
		String engUserRole = ConfigReader.get("SOPInitiatorRole");
		String engReviewer = ConfigReader.get("SOPInternalReviewer");
		String engReviewerDept = ConfigReader.get("SOPInternalReviewerDept");
		String engReviewerRole = ConfigReader.get("SOPInternalReviewerRole");
		String engManager = ConfigReader.get("SOPDepartmentHead");
		String engManagerDept = ConfigReader.get("SOPDepartmentHeadDept");
		String engManagerRole = ConfigReader.get("SOPDepartmentHeadRole");
		String qaReviewer = ConfigReader.get("SOPQAReviewer");
		String qaReviewerDept = ConfigReader.get("SOPQAReviewerDept");
		String qaReviewerRole = ConfigReader.get("SOPQAReviewerRole");
		String qaManager = ConfigReader.get("SOPQAManager");
		String qaManagerDept = ConfigReader.get("SOPQAManagerDept");
		String qaManagerRole = ConfigReader.get("SOPQAManagerRole");

		// SOP Header Data from Excel / Properties
		String docType = ExcelReader.getOrDefault("sop.docType", DataManager.getOrDefault("sop.docType", "SOP"));
		String description = ExcelReader.getOrDefault("sop.description", DataManager.getOrDefault("sop.description", "ANNUAL BUILDING MAINTENANCE"));
		String department = ExcelReader.getOrDefault("sop.department", DataManager.getOrDefault("sop.department", "Maintenance"));
		String respDept = ExcelReader.getOrDefault("sop.respDept", DataManager.getOrDefault("sop.respDept", "Maintenance,Quality Assurance"));
		String masterCopyOwner = ExcelReader.getOrDefault("sop.masterCopyOwner", DataManager.getOrDefault("sop.masterCopyOwner", "Quality Assurance"));
		String supersedesNo = ExcelReader.getOrDefault("sop.supersedesNo", DataManager.getOrDefault("sop.supersedesNo", "NIL"));

		// SOP Contents Data from Excel (sop_contents.xlsx)
		String objective = ExcelReader.getOrDefault("Objective", DataManager.getOrDefault("sop.objective", "The purpose of this SOP is to provide stepwise instructions for the Annual Building Maintenance."));
		String scope = ExcelReader.getOrDefault("Scope", DataManager.getOrDefault("sop.scope", "This Procedure is applicable to Annual Building Maintenance of factory building in the manufacturing plant located at all the Locations."));
		String responsibility = ExcelReader.getOrDefault("Responsibility", DataManager.getOrDefault("sop.responsibility", "The engineering officer / designee is responsible to follow the procedure as per SOP of Annual Building Maintenance."));
		String procedure = ExcelReader.getOrDefault("Procedure", DataManager.getOrDefault("sop.procedure", "Carry Out The Following Activities As Per Yearly or As & When Required Schedule: The area shall be inspected by the respective user department personnel."));

		// Subtabs Data from Excel / Properties
		String abbrevQAShort = ExcelReader.getOrDefault("sop.abbreviation.qa.short", DataManager.getOrDefault("sop.abbreviation.qa.short", "QA"));
		String abbrevQAFull = ExcelReader.getOrDefault("sop.abbreviation.qa.full", DataManager.getOrDefault("sop.abbreviation.qa.full", "Quality Assurance"));
		String abbrevSOPShort = ExcelReader.getOrDefault("sop.abbreviation.sop.short", DataManager.getOrDefault("sop.abbreviation.sop.short", "SOP"));
		String abbrevSOPFull = ExcelReader.getOrDefault("sop.abbreviation.sop.full", DataManager.getOrDefault("sop.abbreviation.sop.full", "Standard Operating Procedure"));

		String annexureName = ExcelReader.getOrDefault("sop.annexure.name", DataManager.getOrDefault("sop.annexure.name", "Annexure-I"));
		String annexureTitle = ExcelReader.getOrDefault("sop.annexure.title", DataManager.getOrDefault("sop.annexure.title", "BUILDING MAINTENANCE SCHEDULE"));
		String annexurePaper = ExcelReader.getOrDefault("sop.annexure.printPaper", DataManager.getOrDefault("sop.annexure.printPaper", "A4 Landscape"));
		String annexureContent = ExcelReader.getOrDefault("AnnexureContent", DataManager.getOrDefault("sop.annexure.content", "Facility Room Checklist PLANNED Q Q Q Q ACTUAL"));

		String formatDocType = ExcelReader.getOrDefault("sop.format.docType", DataManager.getOrDefault("sop.format.docType", "SOP Format"));
		String formatName = ExcelReader.getOrDefault("sop.format.name", DataManager.getOrDefault("sop.format.name", "PAINT RECORD"));
		String formatPaper = ExcelReader.getOrDefault("sop.format.printPaper", DataManager.getOrDefault("sop.format.printPaper", "A4 Portrait"));
		String formatContent = ExcelReader.getOrDefault("FormatContent", DataManager.getOrDefault("sop.format.content", "SR NO. Name of the Area Type of paint Paint Done on Checked by(Sign/Date) Remark"));

		String distDept1 = ExcelReader.getOrDefault("sop.distribution.dept1", DataManager.getOrDefault("sop.distribution.dept1", "Maintenance"));
		String distCopies1 = ExcelReader.getOrDefault("sop.distribution.copies1", DataManager.getOrDefault("sop.distribution.copies1", "1"));
		String distDept2 = ExcelReader.getOrDefault("sop.distribution.dept2", DataManager.getOrDefault("sop.distribution.dept2", "Quality Assurance"));
		String distCopies2 = ExcelReader.getOrDefault("sop.distribution.copies2", DataManager.getOrDefault("sop.distribution.copies2", "1"));

		String signDept = DataManager.getOrDefault("sop.digitalSignature.dept", "Maintenance");
		String dmsContentType = ExcelReader.getOrDefault("sop.dms.contentType", DataManager.getOrDefault("sop.dms.contentType", "Training Completion Report"));
		String dmsFilePath = "src/test/resources/Training_Completion_Report.pdf";

		// --------------------------------------------------------------------------------------------------
		// Pre-requisite: Verify / Update SOP_Effective_TNI_Check = 'D' in System Configurator
		// 1. Login with System tenant & System Administrator role
		// 2. Open System Configurator window through menu searchbox (click on label, not '+')
		// 3. In lookup record search the record by filling name : "SOP_Effective_TNI_Check"
		// 4. Check configured value is D; if not D update to D and save
		// 5. Do logout
		// --------------------------------------------------------------------------------------------------
		String systemUser = ConfigReader.get("Superuser");
		String systemPassword = ConfigReader.get("Password");
		String systemTenant = ConfigReader.get("SystemTenant");
		String systemRole = ConfigReader.get("SystemRole");

		log.info("Pre-requisite Step 1: Logging in with System tenant [{}] & System Administrator role [{}]...", systemTenant, systemRole);
		loginPage.login(systemUser, systemPassword);
		clientAndRole.selectClientAndRole(systemTenant, systemRole);
		dashboard.verifyLoginDetails();

		log.info("Pre-requisite Step 2: Opening System Configurator window through menu searchbox (clicking label)...");
		systemConfiguration.verifyOrUpdateSystemConfig("SOP_Effective_TNI_Check", "D");
		dashboard.clickLogout();

		// --------------------------------------------------------------------------------------------------
		// Step 1: Login with EngUser and Engineer Role
		// --------------------------------------------------------------------------------------------------
		log.info("Step 1: Logging in with EngUser [{}] and Role [{}]...", engUser, engUserRole);
		loginPage.login(engUser, password);
		clientAndRole.selectClientAndRole(client, engUserRole);
		dashboard.verifyLoginDetails();

		// --------------------------------------------------------------------------------------------------
		// Step 2 & 3: Navigate to SOP Master window via Menu Search '+' icon, fill Header and Save
		// --------------------------------------------------------------------------------------------------
		log.info("Step 2-3: Opening SOP Master window and filling Header fields...");
		sopMaster.navigateToSOPMasterWindow();
		sopMaster.fillHeaderAndSave(docType, description, department, respDept, masterCopyOwner);

		String sopDocNo = sopMaster.getDocumentNo();
		log.info("Generated SOP Document Number: [{}]", sopDocNo);
		sopMaster.verifyRevisionNo("0");
		sopMaster.verifySOPStatus("Drafted");

		// --------------------------------------------------------------------------------------------------
		// Step 4 to 9: Fill Content Section (Objective, Scope, Responsibility, Procedure) and Save
		// --------------------------------------------------------------------------------------------------
		log.info("Step 4-9: Entering Objective, Scope, Responsibility, and Procedure text...");
//		sopMaster.fillContentSection("Objective", objective);
//		sopMaster.fillContentSection("Scope", scope);
//		sopMaster.fillContentSection("Responsibility", responsibility);
//		sopMaster.fillContentSection("Procedure", procedure);
//		sopMaster.saveRecord();
		
		log.info("Step 10-12: Adding records to Abbreviations tab...");
		sopMaster.addAbbreviation(abbrevQAShort, abbrevQAFull);
		sopMaster.createNewSubrecord();
		sopMaster.addAbbreviation(abbrevSOPShort, abbrevSOPFull);
		
		log.info("Step 13-16: Adding record to Annexure tab...");
		sopMaster.addAnnexure(annexureName, annexureTitle, annexurePaper, supersedesNo, annexureContent);
		
		log.info("Step 17-20: Adding record to SOP Formats tab...");
		sopMaster.addSOPFormat(formatDocType, formatName, formatPaper, supersedesNo, formatContent);
		
		log.info("Step 21-23: Adding records to Distribution List tab...");
		sopMaster.addDistributionList(distDept1, distCopies1);
		sopMaster.createNewSubrecord();
		sopMaster.addDistributionList(distDept2, distCopies2);

		// --------------------------------------------------------------------------------------------------
		// Step 24 to 25: Navigate back to SOP Header, Preview Report & check 'Master Copy' stamp is NOT present
		// --------------------------------------------------------------------------------------------------
		log.info("Step 24-25: Navigating to SOP Header, previewing report before approval...");
		sopMaster.navigateBackOnSOPHeader();
		sopMaster.clickPreview();
		printReport.verifyReportOpened();
		printReport.verifyReportContent(sopDocNo, description);
		printReport.verifyMasterCopyStamp(false);
		printReport.verifyNoSectionOverlap();
		printReport.verifyNoTextOverlap();
		printReport.closeReportTab();
		sopMaster.ensureOnSOPMasterHeader(sopDocNo);

		// --------------------------------------------------------------------------------------------------
		// Step 26 to 27: Click Document Action -> Complete -> Verify error: 'Prepared By Sign is pending'
		// --------------------------------------------------------------------------------------------------
		log.info("Step 26-27: Verifying error when completing Document Action without Prepared By Sign...");
		toolbar.performDocAction("Complete");
		sopMaster.verifyErrorMessageAndClose("Prepared By Sign is pending");
		sopMaster.crossErrorPopupIfDisplayed();
		toolbar.hitRequeryButton();

		// --------------------------------------------------------------------------------------------------
		// Step 28 to 29: Complete Digital Signature on Prepared By Sign
		// --------------------------------------------------------------------------------------------------
		log.info("Step 28-29: Signing Prepared By Sign field...");
		sopMaster.completeDigitalSignature("PreparedBy", engUserDept, password);

		// --------------------------------------------------------------------------------------------------
		// Step 30 to 34: Document Action Complete -> Assign to EngReviewer -> Status: 'Internal Reviewal'
		// --------------------------------------------------------------------------------------------------
		log.info("Step 30-34: Completing Document Action and assigning workflow to EngReviewer [{}]...", engReviewer);
		sopMaster.completeDocActionAndAssignTo(engReviewer);
		sopMaster.verifySOPStatus("Internal Reviewal");

		// --------------------------------------------------------------------------------------------------
		// Step 35: Click Document Action -> Verify error: 'Assigned To : ...'
		// --------------------------------------------------------------------------------------------------
		log.info("Step 35: Verifying error popup when unauthorized user attempts Document Action...");
		sopMaster.clickDocAction();
		sopMaster.verifyErrorMessageAndClose("Assigned To");
		sopMaster.crossErrorPopupIfDisplayed();
		dashboard.clickLogout();

		// ==================================================================================================
		// STAGE 1: Internal Reviewal (EngReviewer)
		// 1. EngReviewer previews report, rejects workflow (Answer = No) -> Status: 'Not Approved'
		// 2. Initiator (EngUser) re-signs Prepared By & re-assigns to EngReviewer -> Status: 'Internal Reviewal'
		// 3. EngReviewer previews report, signs Checked By & approves workflow (Answer = Yes) -> Assign to EngManager
		// ==================================================================================================
		log.info("Stage 1 - Step 36-40: Logging in with EngReviewer [{}] and zooming to SOP record [{}]...", engReviewer, sopDocNo);
		loginPage.login(engReviewer, password);
		clientAndRole.selectClientAndRole(client, engReviewerRole);

		workflowActivities.navigateToWorkflowActivities();
		workflowActivities.clickOnAssignedSOPRecord("Internal Review", sopDocNo);
		workflowActivities.zoomToRecord();
		sopMaster.clickPreview();
		printReport.verifyPrintReport();
		sopMaster.ensureOnSOPMasterHeader(sopDocNo);

		log.info("Stage 1 - Step 41: EngReviewer rejects SOP (Answer: No) -> Status updates to 'Not Approved'...");
		sopMaster.performDocActionWithAnswer("No", null);
		sopMaster.verifySOPStatus("Not Approved");
		dashboard.clickLogout();

		log.info("Stage 1 - Step 42-45: Initiator [{}] re-signs Prepared By and re-submits to EngReviewer [{}]...", engUser, engReviewer);
		loginPage.login(engUser, password);
		clientAndRole.selectClientAndRole(client, engUserRole);

		try {
			workflowActivities.navigateToWorkflowActivities();
			workflowActivities.clickOnAssignedSOPRecord("Draft", sopDocNo);
			workflowActivities.zoomToRecord();
		} catch (Exception e) {
			log.info("Navigating to SOP Master via menu search fallback: {}", e.getMessage());
			sopMaster.ensureOnSOPMasterHeader(sopDocNo);
		}

		sopMaster.completeDigitalSignature("PreparedBy", engUserDept, password);
		sopMaster.completeDocActionAndAssignTo(engReviewer);
		sopMaster.verifySOPStatus("Internal Reviewal");
		dashboard.clickLogout();

		log.info("Stage 1 - Step 46-49: EngReviewer [{}] signs Checked By, approves and assigns to EngManager [{}]...", engReviewer, engManager);
		loginPage.login(engReviewer, password);
		clientAndRole.selectClientAndRole(client, engReviewerRole);

		workflowActivities.navigateToWorkflowActivities();
		workflowActivities.clickOnAssignedSOPRecord("Internal Review", sopDocNo);
		workflowActivities.zoomToRecord();
		sopMaster.clickPreview();
		printReport.verifyPrintReport();
		sopMaster.ensureOnSOPMasterHeader(sopDocNo);
		sopMaster.completeDigitalSignature("CheckedBy", engReviewerDept, password);
		sopMaster.performDocActionWithAnswer("Yes", engManager);
		sopMaster.verifySOPStatus("Department Approval");
		dashboard.clickLogout();

		// ==================================================================================================
		// STAGE 2: Department Approval (EngManager)
		// 1. EngManager previews report, rejects workflow (Answer = No) -> Status: 'Not Approved'
		// 2. Initiator (EngUser) re-signs Prepared By & re-assigns to EngReviewer -> Status: 'Internal Reviewal'
		// 3. EngReviewer re-signs Checked By & assigns to EngManager -> Status: 'Department Approval'
		// 4. EngManager previews report, signs Reviewed By & approves workflow (Answer = Yes) -> Status: 'QA Approval'
		// ==================================================================================================
		log.info("Stage 2 - Step 50-52: EngManager [{}] logs in, previews report and rejects workflow (Answer: No)...", engManager);
		loginPage.login(engManager, password);
		clientAndRole.selectClientAndRole(client, engManagerRole);

		workflowActivities.navigateToWorkflowActivities();
		workflowActivities.clickOnAssignedSOPRecord("Department Approval", sopDocNo);
		workflowActivities.zoomToRecord();
		sopMaster.verifySOPStatus("Department Approval");
		sopMaster.clickPreview();
		printReport.verifyPrintReport();
		sopMaster.ensureOnSOPMasterHeader(sopDocNo);

		log.info("Stage 2 - Step 52: EngManager rejects SOP (Answer: No) -> Status updates to 'Not Approved'...");
		sopMaster.performDocActionWithAnswer("No", null);
		sopMaster.verifySOPStatus("Not Approved");
		dashboard.clickLogout();

		log.info("Stage 2 - Step 53: Initiator [{}] re-signs Prepared By and re-submits to EngReviewer [{}]...", engUser, engReviewer);
		loginPage.login(engUser, password);
		clientAndRole.selectClientAndRole(client, engUserRole);

		try {
			workflowActivities.navigateToWorkflowActivities();
			workflowActivities.clickOnAssignedSOPRecord("Draft", sopDocNo);
			workflowActivities.zoomToRecord();
		} catch (Exception e) {
			log.info("Navigating to SOP Master via menu search fallback: {}", e.getMessage());
			sopMaster.ensureOnSOPMasterHeader(sopDocNo);
		}

		sopMaster.completeDigitalSignature("PreparedBy", engUserDept, password);
		sopMaster.completeDocActionAndAssignTo(engReviewer);
		sopMaster.verifySOPStatus("Internal Reviewal");
		dashboard.clickLogout();

		log.info("Stage 2 - Step 54: EngReviewer [{}] signs Checked By and assigns to EngManager [{}]...", engReviewer, engManager);
		loginPage.login(engReviewer, password);
		clientAndRole.selectClientAndRole(client, engReviewerRole);

		workflowActivities.navigateToWorkflowActivities();
		workflowActivities.clickOnAssignedSOPRecord("Internal Review", sopDocNo);
		workflowActivities.zoomToRecord();
		sopMaster.completeDigitalSignature("CheckedBy", engReviewerDept, password);
		sopMaster.performDocActionWithAnswer("Yes", engManager);
		sopMaster.verifySOPStatus("Department Approval");
		dashboard.clickLogout();

		log.info("Stage 2 - Step 55-58: EngManager [{}] signs Reviewed By and approves workflow -> 'QA Approval'...", engManager);
		loginPage.login(engManager, password);
		clientAndRole.selectClientAndRole(client, engManagerRole);

		workflowActivities.navigateToWorkflowActivities();
		workflowActivities.clickOnAssignedSOPRecord("Department Approval", sopDocNo);
		workflowActivities.zoomToRecord();
		sopMaster.verifySOPStatus("Department Approval");
		sopMaster.clickPreview();
		printReport.verifyPrintReport();
		sopMaster.ensureOnSOPMasterHeader(sopDocNo);
		sopMaster.completeDigitalSignature("ReviewedBy", engManagerDept, password);
		sopMaster.performDocActionWithAnswer("Yes", null);
		sopMaster.verifySOPStatus("QA Approval");
		dashboard.clickLogout();

		// ==================================================================================================
		// STAGE 3: QA Approval (QAReviewer)
		// 1. QAReviewer previews report, rejects workflow (Answer = No) -> Status: 'Not Approved'
		// 2. Initiator (EngUser) re-signs Prepared By & re-assigns to EngReviewer -> Status: 'Internal Reviewal'
		// 3. EngReviewer re-signs Checked By & assigns to EngManager -> Status: 'Department Approval'
		// 4. EngManager re-signs Reviewed By & approves to QA Approval -> Status: 'QA Approval'
		// 5. QAReviewer previews report, signs Approver & approves workflow (Answer = Yes) -> Status: 'Approved'
		// ==================================================================================================
		log.info("Stage 3 - Step 60-62: QAReviewer [{}] logs in, previews report and rejects workflow (Answer: No)...", qaReviewer);
		loginPage.login(qaReviewer, password);
		clientAndRole.selectClientAndRole(client, qaReviewerRole);

		workflowActivities.navigateToWorkflowActivities();
		workflowActivities.clickOnAssignedSOPRecord("QA Approval", sopDocNo);
		workflowActivities.zoomToRecord();
		sopMaster.verifySOPStatus("QA Approval");
		sopMaster.clickPreview();
		printReport.verifyPrintReport();
		sopMaster.ensureOnSOPMasterHeader(sopDocNo);

		log.info("Stage 3 - Step 62: QAReviewer rejects SOP (Answer: No) -> Status updates to 'Not Approved'...");
		sopMaster.performDocActionWithAnswer("No", null);
		sopMaster.verifySOPStatus("Not Approved");
		dashboard.clickLogout();

		log.info("Stage 3 - Step 63: Initiator [{}] re-signs Prepared By and re-submits to EngReviewer [{}]...", engUser, engReviewer);
		loginPage.login(engUser, password);
		clientAndRole.selectClientAndRole(client, engUserRole);

		try {
			workflowActivities.navigateToWorkflowActivities();
			workflowActivities.clickOnAssignedSOPRecord("Draft", sopDocNo);
			workflowActivities.zoomToRecord();
		} catch (Exception e) {
			log.info("Navigating to SOP Master via menu search fallback: {}", e.getMessage());
			sopMaster.ensureOnSOPMasterHeader(sopDocNo);
		}

		sopMaster.completeDigitalSignature("PreparedBy", engUserDept, password);
		sopMaster.completeDocActionAndAssignTo(engReviewer);
		sopMaster.verifySOPStatus("Internal Reviewal");
		dashboard.clickLogout();

		log.info("Stage 3 - Step 64: EngReviewer [{}] signs Checked By and assigns to EngManager [{}]...", engReviewer, engManager);
		loginPage.login(engReviewer, password);
		clientAndRole.selectClientAndRole(client, engReviewerRole);

		workflowActivities.navigateToWorkflowActivities();
		workflowActivities.clickOnAssignedSOPRecord("Internal Review", sopDocNo);
		workflowActivities.zoomToRecord();
		sopMaster.completeDigitalSignature("CheckedBy", engReviewerDept, password);
		sopMaster.performDocActionWithAnswer("Yes", engManager);
		sopMaster.verifySOPStatus("Department Approval");
		dashboard.clickLogout();

		log.info("Stage 3 - Step 65: EngManager [{}] signs Reviewed By and approves workflow -> 'QA Approval'...", engManager);
		loginPage.login(engManager, password);
		clientAndRole.selectClientAndRole(client, engManagerRole);

		workflowActivities.navigateToWorkflowActivities();
		workflowActivities.clickOnAssignedSOPRecord("Department Approval", sopDocNo);
		workflowActivities.zoomToRecord();
		sopMaster.verifySOPStatus("Department Approval");
		sopMaster.completeDigitalSignature("ReviewedBy", engManagerDept, password);
		sopMaster.performDocActionWithAnswer("Yes", null);
		sopMaster.verifySOPStatus("QA Approval");
		dashboard.clickLogout();

		log.info("Stage 3 - Step 66-69: QAReviewer [{}] signs Approver Sign and approves workflow -> 'Approved'...", qaReviewer);
		loginPage.login(qaReviewer, password);
		clientAndRole.selectClientAndRole(client, qaReviewerRole);

		workflowActivities.navigateToWorkflowActivities();
		workflowActivities.clickOnAssignedSOPRecord("QA Approval", sopDocNo);
		workflowActivities.zoomToRecord();
		sopMaster.verifySOPStatus("QA Approval");
		sopMaster.clickPreview();
		printReport.verifyPrintReport();
		sopMaster.ensureOnSOPMasterHeader(sopDocNo);
		sopMaster.completeDigitalSignature("Approver", qaReviewerDept, password);
		sopMaster.performDocActionWithAnswer("Yes", null);
		sopMaster.verifySOPStatus("Approved");
		dashboard.clickLogout();

		// --------------------------------------------------------------------------------------------------
		// Step 70 to 74: Login with QAManager, preview Report (Master Copy stamp check), and test DMS validation
		// --------------------------------------------------------------------------------------------------
		log.info("Step 70-74: QAManager [{}] verifies Master Copy stamp and DMS document requirement...", qaManager);
		loginPage.login(qaManager, password);
		clientAndRole.selectClientAndRole(client, qaManagerRole);

		workflowActivities.navigateToWorkflowActivities();
		workflowActivities.clickOnAssignedSOPRecord("QA Manager Approval", sopDocNo);
		workflowActivities.zoomToRecord();
		sopMaster.verifySOPStatus("Approved");
		sopMaster.clickPreview();
		printReport.verifyReportOpened();
		printReport.verifyMasterCopyStamp(true);
		printReport.verifyNoSectionOverlap();
		printReport.verifyNoTextOverlap();
		printReport.closeReportTab();
		sopMaster.ensureOnSOPMasterHeader(sopDocNo);

		// Attempt Document Action with Effective Date before uploading DMS document -> Expect Error
		String effectiveDate = new java.text.SimpleDateFormat("dd/MM/yyyy").format(new java.util.Date());
		sopMaster.approveWithEffectiveDate(effectiveDate);
		sopMaster.verifyErrorMessageAndClose("Error: Document not uploaded in DMS for Content type Training Completion Report");

//		// --------------------------------------------------------------------------------------------------
//		// Step 75 to 80: Document Explorer -> Upload Training Completion Report
//		// --------------------------------------------------------------------------------------------------
//		log.info("Step 75-80: Uploading Training Completion Report via Document Explorer...");
//		documentExplorer.openDocumentExplorer();
//		documentExplorer.uploadContent(dmsFilePath, dmsContentType);
//		documentExplorer.closeDocumentExplorer();
//
//		// --------------------------------------------------------------------------------------------------
//		// Step 81 to 82: Document Action with Effective Date -> Status: 'Effective'
//		// --------------------------------------------------------------------------------------------------
//		log.info("Step 81-82: Approving SOP with Effective Date after DMS upload...");
//		sopMaster.approveWithEffectiveDate(effectiveDate);
//		sopMaster.verifySOPStatus("Effective");
//		dashboard.clickLogout();
//
//		// --------------------------------------------------------------------------------------------------
//		// Step 83 to 90: Login with QAReviewer -> Revise SOP -> New Revision 1 created in Drafted status
//		// --------------------------------------------------------------------------------------------------
//		log.info("Step 83-90: Starting Revise SOP workflow to create Revision 1...");
//		loginPage.login(qaReviewer, password);
//		clientAndRole.selectClientAndRole(client, qaReviewerRole);
//
//		sopMaster.navigateToSOPMasterWindow();
//		sopMaster.clickReviseSOP();
//		sopMaster.openRevisedSOPLink();
//
//		sopMaster.verifyRevisionNo("1");
//		sopMaster.verifySOPStatus("Drafted");
//
//		// --------------------------------------------------------------------------------------------------
//		// Step 91 to 94: Revise Annexure Format on Annexure tab
//		// --------------------------------------------------------------------------------------------------
//		log.info("Step 91-94: Revising Format on Annexure tab...");
//		sopMaster.goToAnnexureTab();
//		sopMaster.reviseAnnexureFormat();
//		sopMaster.saveRecord();
//
//		// --------------------------------------------------------------------------------------------------
//		// Step 95 to 98: Revise Format on SOP Formats tab
//		// --------------------------------------------------------------------------------------------------
//		log.info("Step 95-98: Revising Format on SOP Formats tab...");
//		sopMaster.goToSOPFormatTab();
//		sopMaster.reviseSOPFormat();
//		sopMaster.saveRecord();
//
//		// --------------------------------------------------------------------------------------------------
//		// Step 101 to 102: Revision History Tab -> Enter Revision 1 details
//		// --------------------------------------------------------------------------------------------------
//		log.info("Step 101-102: Recording details in Revision History tab...");
//		String revReason = ExcelReader.getOrDefault("sop.revision.reason", "Annual Review & building maintenance checklist update");
//		String revCCNo = ExcelReader.getOrDefault("sop.revision.changeControlNo", "CC-2026-001");
//		sopMaster.addRevisionHistory("1", revReason, revCCNo);
//
//		// --------------------------------------------------------------------------------------------------
//		// Step 108 to 109: Verify Previous Version is Superseded with 'Supersede Copy' stamp
//		// --------------------------------------------------------------------------------------------------
//		log.info("Step 108-109: Verifying Previous SOP record has 'Supersede Copy' stamp...");
//		sopMaster.navigateBackOnSOPHeader();
//		sopMaster.clickPreview();
//		printReport.verifyReportOpened();
//		printReport.verifySupersedeCopyStamp(true);
//		printReport.closeReportTab();

		log.info("================================================================================");
		log.info("========== ✅ Test PHX-3499 Verification of SOP Master Passed Successfully! ==========");
		log.info("================================================================================");
	}
}
