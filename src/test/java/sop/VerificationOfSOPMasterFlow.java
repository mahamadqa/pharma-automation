package sop;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

import base.BaseTest;
import utils.ConfigReader;
import utils.DataManager;
import utils.ExcelReader;

/**
 * Test Case PHX-3499: Verification of SOP Master Flow (Without Effectiveness Check / No DMS Upload).
 * Comprehensive end-to-end verification of SOP Master lifecycle covering:
 * - SOP Master creation, Content sections (Objective, Scope, Responsibility,
 * Procedure)
 * - Subtabs: Abbreviations, Annexure, SOP Formats, Distribution List
 * - Digital signatures (Prepared By, Checked By, Reviewed By, Approver)
 * - Multi-level Workflow approval (EngUser -> EngReviewer -> EngManager ->
 * QAReviewer -> QAManager)
 * - Rejection and correction cycle (Not Approved status & field clearing)
 * - Report Preview and Master Copy stamp verification
 * - System Config SOP_Effective_TNI_Check = 'N' (No DMS upload required)
 * - Approval without DMS Training Completion Report error
 * - Effective date & status transition
 * - Revise SOP flow (Revision 1, Annexure format revision, SOP Formats
 * revision, Revision History)
 * - Previous version superseding and Supersede Copy stamp verification
 */
public class VerificationOfSOPMasterFlow extends BaseTest {

	private static final Logger log = LogManager.getLogger(VerificationOfSOPMasterFlow.class);

	private static volatile String effectiveRevisedSOPNo;
	private static volatile String effectiveRevisedSOPDocNo;

	public static String getEffectiveRevisedSOPNo() {
		return effectiveRevisedSOPNo;
	}

	public static void setEffectiveRevisedSOPNo(String no) {
		effectiveRevisedSOPNo = no;
	}

	public static void setEffectiveRevisedSOPDocNo(String docNo) {
		effectiveRevisedSOPDocNo = docNo;
	}
	//dependsOnMethods = "sop.VerifySOPMasterFlowIfEffectivenessCheckRequired.verificationOfSOPMasterLifecycle",
	@Test(groups = { "advance",
			"sop" },  alwaysRun = true, description = "PHX-3499: Complete Verification of SOP Master Lifecycle & Revisions")
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
		String description = DataManager.getOrDefault("sop.description", "ANNUAL BUILDING MAINTENANCE : Playwright Automation..");
		String department = ExcelReader.getOrDefault("sop.department",
				DataManager.getOrDefault("sop.department", "Maintenance"));
		String respDept = ExcelReader.getOrDefault("sop.respDept",
				DataManager.getOrDefault("sop.respDept", "Maintenance,Quality Assurance"));
		String masterCopyOwner = ExcelReader.getOrDefault("sop.masterCopyOwner",
				DataManager.getOrDefault("sop.masterCopyOwner", "Quality Assurance"));
		String supersedesNo = ExcelReader.getOrDefault("sop.supersedesNo",
				DataManager.getOrDefault("sop.supersedesNo", "NIL"));

		// SOP Contents Data from Excel (sop_contents.xlsx)
		String objective = ExcelReader.getOrDefault("Objective", null);
		String scope = ExcelReader.getOrDefault("Scope", null);
		String responsibility = ExcelReader.getOrDefault("Responsibility",null);
		String procedure = ExcelReader.getOrDefault("Procedure", null);

		// Subtabs Data from Excel / Properties
		String abbrevQAShort = ExcelReader.getOrDefault("sop.abbreviation.qa.short",
				DataManager.getOrDefault("sop.abbreviation.qa.short", "QA"));
		String abbrevQAFull = ExcelReader.getOrDefault("sop.abbreviation.qa.full",
				DataManager.getOrDefault("sop.abbreviation.qa.full", "Quality Assurance"));
		String abbrevSOPShort = ExcelReader.getOrDefault("sop.abbreviation.sop.short",
				DataManager.getOrDefault("sop.abbreviation.sop.short", "SOP"));
		String abbrevSOPFull = ExcelReader.getOrDefault("sop.abbreviation.sop.full",
				DataManager.getOrDefault("sop.abbreviation.sop.full", "Standard Operating Procedure"));

		String annexureName = ExcelReader.getOrDefault("sop.annexure.name",
				DataManager.getOrDefault("sop.annexure.name", "Annexure-I"));
		String annexureTitle = ExcelReader.getOrDefault("sop.annexure.title",
				DataManager.getOrDefault("sop.annexure.title", "BUILDING MAINTENANCE SCHEDULE"));
		String annexurePaper = ExcelReader.getOrDefault("sop.annexure.printPaper",
				DataManager.getOrDefault("sop.annexure.printPaper", "A4 Landscape"));
		String annexureContent = ExcelReader.getOrDefault("AnnexureContent",
				null);

		String formatDocType = ExcelReader.getOrDefault("sop.format.docType",
				DataManager.getOrDefault("sop.format.docType", "SOP Format"));
		String formatName = ExcelReader.getOrDefault("sop.format.name",
				DataManager.getOrDefault("sop.format.name", "PAINT RECORD"));
		String formatPaper = ExcelReader.getOrDefault("sop.format.printPaper",
				DataManager.getOrDefault("sop.format.printPaper", "A4 Portrait"));
		String formatContent = ExcelReader.getOrDefault("FormatContent", null);

		String distDept1 = ExcelReader.getOrDefault("sop.distribution.dept1",
				DataManager.getOrDefault("sop.distribution.dept1", "Maintenance"));
		String distCopies1 = ExcelReader.getOrDefault("sop.distribution.copies1",
				DataManager.getOrDefault("sop.distribution.copies1", "1"));
		String distDept2 = ExcelReader.getOrDefault("sop.distribution.dept2",
				DataManager.getOrDefault("sop.distribution.dept2", "Quality Assurance"));
		String distCopies2 = ExcelReader.getOrDefault("sop.distribution.copies2",
				DataManager.getOrDefault("sop.distribution.copies2", "1"));

		// --------------------------------------------------------------------------------------------------
		// Pre-requisite: Verify / Update SOP_Effective_TNI_Check = 'N' in System Configurator
		// 1. Login with System tenant & System Administrator role
		// 2. Open System Configurator window through menu searchbox (click on label, not '+')
		// 3. In lookup record search the record by filling name : "SOP_Effective_TNI_Check"
		// 4. Check configured value is N; if not N update to N and save
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
		systemConfiguration.verifyOrUpdateSystemConfig("SOP_Effective_TNI_Check", "N");
		dashboard.clickLogout();

		// --------------------------------------------------------------------------------------------------
		// Step 1: Login with EngUser and Engineer Role
		// --------------------------------------------------------------------------------------------------
		log.info("Step 1: Logging in with EngUser [{}] and Role [{}]...", engUser, engUserRole);
		loginPage.login(engUser, password);
		clientAndRole.selectClientAndRole(client, engUserRole);
		dashboard.verifyLoginDetails();

		// --------------------------------------------------------------------------------------------------
		// Step 2 & 3: Navigate to SOP Master window via Menu Search '+' icon, add Resource & Equipment, fill Header and Save
		// --------------------------------------------------------------------------------------------------
		log.info("Step 2-3: Opening SOP Master window, populating Resource & Equipment, and filling Header fields...");
		sopMaster.navigateToSOPMasterWindow();
		sopMaster.fillHeaderAndSave(docType, description, department, respDept, masterCopyOwner);
		sopMaster.addResourceAndEquipment();
		sopMaster.saveRecord();
		String sopDocNo = sopMaster.getDocumentNo();
		log.info("Generated SOP Document Number: [{}]", sopDocNo);
		sopMaster.verifyRevisionNo("0");
		sopMaster.verifySOPStatus("Drafted");

		// --------------------------------------------------------------------------------------------------
		// Step 4 to 9: Fill Content Section (Objective, Scope, Responsibility, Procedure) and Save
		// --------------------------------------------------------------------------------------------------
		log.info("Step 4-9: Entering Objective, Scope, Responsibility, and Procedure text...");
		sopMaster.fillContentSection("Objective", objective);
		sopMaster.fillContentSection("Scope", scope);
		sopMaster.fillContentSection("Responsibility", responsibility);
		sopMaster.fillContentSection("Procedure", procedure);
		sopMaster.saveRecord();

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

		log.info("Step 28-29: Signing Prepared By Sign field...");
		sopMaster.completeDigitalSignature("PreparedBy", engUserDept, password);
		sopMaster.verifyDigitalSignature("PreparedBy", engUser, engUserDept);

		// --------------------------------------------------------------------------------------------------
		// Step 30 to 34: Document Action Complete -> Assign to EngReviewer -> Status: 'Internal Reviewal'
		// --------------------------------------------------------------------------------------------------
		log.info("Step 30-34: Completing Document Action and assigning workflow to EngReviewer [{}]...", engReviewer);
		sopMaster.completeDocActionAndAssignTo(engReviewer);
		sopMaster.verifySOPStatus("Internal Reviewal");
		dashboard.clickLogout();
		
		log.info("Stage 3 - Step 64: EngReviewer [{}] signs Checked By and assigns to EngManager [{}]...", engReviewer,
				engManager);
		loginPage.login(engReviewer, password);
		clientAndRole.selectClientAndRole(client, engReviewerRole);

		workflowActivities.navigateToWorkflowActivities();
		workflowActivities.clickOnAssignedSOPRecord("Internal Review", sopDocNo);
		workflowActivities.zoomToRecord();
		sopMaster.completeDigitalSignature("CheckedBy", engReviewerDept, password);
		sopMaster.verifyDigitalSignature("CheckedBy", engReviewer, engReviewerDept);
		sopMaster.performDocActionWithAnswer("Yes", engManager);
		sopMaster.verifySOPStatus("Department Approval");
		dashboard.clickLogout();

		log.info("Stage 3 - Step 65: EngManager [{}] signs Reviewed By and approves workflow -> 'QA Approval'...",
				engManager);
		loginPage.login(engManager, password);
		clientAndRole.selectClientAndRole(client, engManagerRole);

		workflowActivities.navigateToWorkflowActivities();
		workflowActivities.clickOnAssignedSOPRecord("Department Approval", sopDocNo);
		workflowActivities.zoomToRecord();
		sopMaster.verifySOPStatus("Department Approval");
		sopMaster.completeDigitalSignature("ReviewedBy", engManagerDept, password);
		sopMaster.verifyDigitalSignature("ReviewedBy", engManager, engManagerDept);
		sopMaster.performDocActionWithAnswer("Yes", null);
		sopMaster.verifySOPStatus("QA Approval");
		dashboard.clickLogout();

		log.info("Stage 3 - Step 66-69: QAReviewer [{}] signs Approver Sign and approves workflow -> 'Approved'...",
				qaReviewer);
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
		sopMaster.verifyDigitalSignature("Approver", qaReviewer, qaReviewerDept);
		sopMaster.performDocActionWithAnswer("Yes", null);
		sopMaster.verifySOPStatus("Approved");
		sopMaster.verifyApprovedCheckbox(true);
		dashboard.clickLogout();

		// --------------------------------------------------------------------------------------------------
		// Step 70 to 74: Login with QAManager, preview Report (Master Copy stamp check), and approve SOP
		// --------------------------------------------------------------------------------------------------
		log.info("Step 70-74: QAManager [{}] verifies Master Copy stamp and approves SOP with Effective Date...", qaManager);
		loginPage.login(qaManager, password);
		clientAndRole.selectClientAndRole(client, qaManagerRole);

		workflowActivities.navigateToWorkflowActivities();
		workflowActivities.clickOnAssignedSOPRecord("QA Manager Approval", sopDocNo);
		workflowActivities.zoomToRecord();
		sopMaster.verifySOPStatus("Approved");
		sopMaster.verifyApprovedCheckbox(true);
		sopMaster.clickPreview();
		printReport.verifyReportOpened();
		printReport.verifyMasterCopyStamp(true);
		printReport.verifyNoSectionOverlap();
		printReport.verifyNoTextOverlap();
		printReport.closeReportTab();

		// --------------------------------------------------------------------------------------------------
		// Step 75: Document Action with Effective Date -> Status: 'Effective' (No DMS upload required when TNI Check = 'N')
		// --------------------------------------------------------------------------------------------------
		log.info("Step 75: Approving SOP with Effective Date (No DMS upload required as SOP_Effective_TNI_Check = 'N')...");
		String effectiveDate = new java.text.SimpleDateFormat("dd/MM/yyyy").format(new java.util.Date());
		sopMaster.approveWithEffectiveDate(effectiveDate);
		sopMaster.verifySOPStatus("Effective");
		sopMaster.verifyProcessedCheckbox(true);

		// 1. After made effective SOP verify Next review date is Review After (Month) + Effective Date
		log.info("Requirement 1: Verifying Next Review Date = Effective Date + Review After (Month)...");
		sopMaster.verifyNextReviewDate(effectiveDate);

		sopMaster.clickPreview();
		printReport.verifyReportOpened();
		printReport.verifyMasterCopyStamp(true);
		printReport.verifyNoSectionOverlap();
		printReport.verifyNoTextOverlap();
		printReport.closeReportTab();

		log.info("Effective Stage: Previewing Annexure print report...");
		sopMaster.goToAnnexureTab();
		sopMaster.clickPreview();
		printReport.verifyAnnexureReport(annexureName, true);

		log.info("Effective Stage: Previewing SOP Formats print report...");
		sopMaster.goToSOPFormatTab();
		sopMaster.clickPreview();
		printReport.verifySOPFormatReport(formatName, true);
		sopMaster.navigateBackOnSOPHeader();

		dashboard.clickLogout();

		// -----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
		// Step 83 to 90: Login with QA Reviewer -> Revise SOP -> New Revision 1 created in Drafted status
		// -----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
		log.info("Step 83-90: Starting Revise SOP workflow to create Revision 1...");
		loginPage.login(qaReviewer, password);
		clientAndRole.selectClientAndRole(client, qaReviewerRole);

		sopMaster.navigateToSOPrecord(sopDocNo);
		sopMaster.clickReviseSOP();
		sopMaster.openRevisedSOPLink();

		// 2. When SOP is revised then new SOP record window will opened then go back to old one & verify SOP Status is In Revision.
		log.info("Requirement 2: Going back to old SOP record window and verifying SOP Status is 'In Revision'...");
		sopMaster.switchToOldSOPRecordTab();
		sopMaster.verifySOPStatus("In Revision");
		sopMaster.verifyProcessedCheckbox(true);
		sopMaster.closeFirstSOPTab();

		// 3. In new one record ie. revised then create a revision history record.
		log.info("Requirement 3: Switching to new SOP record (Revision 1) window and creating Revision History record...");
		sopMaster.switchToNewSOPRecordTab();
		sopMaster.verifyRevisionNo("1");
		sopMaster.verifySOPStatus("Drafted");
		sopMaster.verifyProcessedCheckbox(false);
		sopMaster.verifyApprovedCheckbox(false);

		String revReason = ExcelReader.getOrDefault("sop.revision.reason",
				"Annual Review & building maintenance checklist update");
		String revCCNo = ExcelReader.getOrDefault("sop.revision.changeControlNo", "CC-2026-001");
		sopMaster.addRevisionHistory("1", revReason, revCCNo);
		sopMaster.navigateBackOnSOPHeader();
		sopMaster.saveRecord();

		log.info("Revising Annexure format on Revision 1...");
		sopMaster.goToAnnexureTab();
		toolbar.checkGridView();
		sopMaster.clickReviseSOPAnnexure(); /* Annexure revised */
		toolbar.clickOnRequeryButton();
		sopMaster.verifyRefrenceFieldsOnAnnexure(annexureName, sopDocNo);
		sopMaster.verifyAnnexureRevisedCheckbox(true);

		log.info("Revising SOP Formats format on Revision 1...");
		sopMaster.goToSOPFormatTab();
		toolbar.checkGridView();
		sopMaster.clickReviseSOPFormat(); /* Format revised */
		toolbar.clickOnRequeryButton();
		sopMaster.verifyRefrenceFieldsOnFormat(formatName, sopDocNo);
		sopMaster.verifyFormatRevisedCheckbox(true);
		sopMaster.navigateBackOnSOPHeader();
		dashboard.clickLogout();

		// Complete Document Actions on Revision 1 up to Effective stage:
		// Step 4.1: Initiator signs Prepared By & submits to EngReviewer
		log.info("Revision 1 - Signing Prepared By and assigning to EngReviewer [{}]...", engReviewer);
		loginPage.login(engUser, password);
		clientAndRole.selectClientAndRole(client, engUserRole);

		sopMaster.navigateToSOPrecord(sopDocNo);
		sopMaster.completeDigitalSignature("PreparedBy", engUserDept, password);
		sopMaster.completeDocActionAndAssignTo(engReviewer);
		sopMaster.verifySOPStatus("Internal Reviewal");
		dashboard.clickLogout();

		// Step 4.2: EngReviewer signs Checked By & assigns to EngManager
		log.info("Revision 1 - EngReviewer [{}] signs Checked By and assigns to EngManager [{}]...", engReviewer,
				engManager);
		loginPage.login(engReviewer, password);
		clientAndRole.selectClientAndRole(client, engReviewerRole);
		workflowActivities.navigateToWorkflowActivities();
		workflowActivities.clickOnAssignedSOPRecord("Internal Review", sopDocNo);
		workflowActivities.zoomToRecord();
		sopMaster.completeDigitalSignature("CheckedBy", engReviewerDept, password);
		sopMaster.performDocActionWithAnswer("Yes", engManager);
		sopMaster.verifySOPStatus("Department Approval");
		dashboard.clickLogout();

		// Step 4.3: EngManager signs Reviewed By & approves to QA Approval
		log.info("Revision 1 - EngManager [{}] signs Reviewed By and approves workflow -> 'QA Approval'...",
				engManager);
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

		// Step 4.4: QAReviewer signs Approver & approves to Approved
		log.info("Revision 1 - QAReviewer [{}] signs Approver and approves workflow -> 'Approved'...", qaReviewer);
		loginPage.login(qaReviewer, password);
		clientAndRole.selectClientAndRole(client, qaReviewerRole);
		workflowActivities.navigateToWorkflowActivities();
		workflowActivities.clickOnAssignedSOPRecord("QA Approval", sopDocNo);
		workflowActivities.zoomToRecord();
		sopMaster.verifySOPStatus("QA Approval");
		sopMaster.completeDigitalSignature("Approver", qaReviewerDept, password);
		sopMaster.performDocActionWithAnswer("Yes", null);
		sopMaster.verifySOPStatus("Approved");
		sopMaster.verifyApprovedCheckbox(true);
		dashboard.clickLogout();

		// Step 4.5: QAManager approves with Effective Date -> Effective (No DMS upload required)
		log.info("Revision 1 - QAManager [{}] approves SOP with Effective Date -> 'Effective'...", qaManager);
		loginPage.login(qaManager, password);
		clientAndRole.selectClientAndRole(client, qaManagerRole);
		workflowActivities.navigateToWorkflowActivities();
		workflowActivities.clickOnAssignedSOPRecord("QA Manager Approval", sopDocNo);
		workflowActivities.zoomToRecord();
		sopMaster.verifySOPStatus("Approved");
		sopMaster.verifyApprovedCheckbox(true);

		String effectiveDate1 = new java.text.SimpleDateFormat("dd/MM/yyyy").format(new java.util.Date());
		sopMaster.approveWithEffectiveDate(effectiveDate1);
		sopMaster.verifySOPStatus("Effective");
		sopMaster.verifyProcessedCheckbox(true);
		sopMaster.verifyNextReviewDate(effectiveDate1);
		sopMaster.verifyRevisionNo("1");

		// Capture effective revised SOP number for downstream test classes (PHX-3503, PHX-3504)
		effectiveRevisedSOPDocNo = sopMaster.getDocumentNo();
		String revNo = sopMaster.getRevisionNo();
		if (revNo == null || revNo.trim().isEmpty()) {
			revNo = "1";
		}
		if (effectiveRevisedSOPDocNo != null && effectiveRevisedSOPDocNo.contains("_")) {
			effectiveRevisedSOPNo = effectiveRevisedSOPDocNo;
		} else {
			effectiveRevisedSOPNo = effectiveRevisedSOPDocNo + "_" + revNo;
		}
		System.setProperty("effective.revised.sop.no", effectiveRevisedSOPNo);
		System.setProperty("effective.revised.sop.docno", effectiveRevisedSOPDocNo);
		log.info("Captured Effective Revised SOP Number for downstream tests: [{}] (DocNo: [{}])",
				effectiveRevisedSOPNo, effectiveRevisedSOPDocNo);

		// --------------------------------------------------------------------------------------------------
		// Requirement 3: After new revised SOP made effective then From header zoom to Previous version field
		// & verify sop status updated to Revised & on print report Superseded copy stamp is displaying.
		// --------------------------------------------------------------------------------------------------
		log.info("Requirement 3: Zooming to Previous Version field from SOP header...");
		sopMaster.zoomToPreviousVersion();
		sopMaster.closeFirstSOPTab();
		sopMaster.switchToNewSOPRecordTab();
		sopMaster.verifySOPStatus("Revised");
		sopMaster.verifyRevisionNo("0");
		sopMaster.verifyActiveCheckbox(false);
		log.info(
				"Requirement 3: Previewing Print Report for Previous Version and verifying 'Superseded copy' stamp...");
		sopMaster.clickPreview();
		printReport.verifyReportOpened();
		printReport.verifySupersedeCopyStamp(true);
		printReport.verifyNoSectionOverlap();
		printReport.verifyNoTextOverlap();
		printReport.closeReportTab();

		log.info("================================================================================");
		log.info("========== ✅ Test PHX-3499 Verification of SOP Master Passed Successfully! ==========");
		log.info("================================================================================");
	}

}
