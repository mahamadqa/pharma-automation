package formulaMaster;

import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.Test;

import base.BaseTest;
import utils.ConfigReader;
import utils.DataManager;

/**
 * End-to-End Test for Manufacturing Workflow & Formula Master Flow.
 * Combines all test cases defined in Manufacturing_Workflow.docx into a single
 * streamlined @Test method:
 * - PHX-310: Creation of Manufacturing Workflow & Workflow Template
 * - PHX-311: Creation of Activity Records in Activity Tab (Nodes: Weighing, Blending, Granulation, Compression, Coating, Packaging)
 * - PHX-313: Creation of Formula Master Record with Header Validations & Template Linkage
 * - PHX-314: Adding API & Excipient Components, Potency Corrections, Weight Calculations,
 *             Overages, Evaporate Flag, and Component Verification.
 */
public class FormulaMaster extends BaseTest {

	private static final Logger log = LogManager.getLogger(FormulaMaster.class);

	private static volatile String activeTemplateWorkflowName;
	private static volatile String activeFormulaMasterDocNo;

	public static String getActiveTemplateWorkflowName() {
		return activeTemplateWorkflowName;
	}

	public static void setActiveTemplateWorkflowName(String name) {
		activeTemplateWorkflowName = name;
	}

	public static String getActiveFormulaMasterDocNo() {
		return activeFormulaMasterDocNo;
	}

	public static void setActiveFormulaMasterDocNo(String docNo) {
		activeFormulaMasterDocNo = docNo;
	}

	/**
	 * Single comprehensive end-to-end test method covering:
	 * 1. Admin Login & Role Selection.
	 * 2. PHX-310: Creation of Manufacturing Workflow & Template Flag.
	 * 3. PHX-313: Creation of Formula Master Header, Validations & Template Workflow Linkage.
	 * 4. PHX-314: Adding API & Excipient Components, Potency Corrections, Weight Summation,
	 *    Overage testing, Evaporate Flag, and Verify Components button validation.
	 */
	@Test(groups = { "advance", "formula",
			"manufacturing" }, description = "End-to-End Flow: Create Manufacturing Workflow Template → Create Formula Master → Add Components & Verify")
	public void verificationOfManufacturingWorkflowAndFormulaMasterFlow() {

		log.info("================================================================================");
		log.info("========== Starting Test: Manufacturing Workflow & Formula Master Flow ==========");
		log.info("================================================================================");

		// Initialize Test Data
		DataManager.initData();
		String client = ConfigReader.get("client");
		String superUser = ConfigReader.get("Superuser");
		String password = ConfigReader.get("Password");
		String adminRole = ConfigReader.get("SuperuserRole");

		// --- Test Data: Manufacturing Workflow (PHX-310) ---
		long timestamp = System.currentTimeMillis() % 100000;
		String baseWfName = DataManager.getOrDefault("mfg.workflow.name", "Test workflow Tablet Template");
		String wfName = baseWfName + " " + timestamp;
		String wfType = DataManager.getOrDefault("mfg.workflow.type", "Manufacturing");
		String processType = DataManager.getOrDefault("mfg.workflow.processType", "Batch Flow ");
		String resource = DataManager.getOrDefault("mfg.workflow.resource", "Tablet Plant - Pipan");
		String qtyBatchSize = DataManager.getOrDefault("mfg.workflow.qtyBatchSize", "100000");
		String priority = DataManager.getOrDefault("mfg.workflow.priority", "1");
		String validFromWf = DataManager.getOrDefault("mfg.workflow.validFrom", "10/01/2026");
		String validToWf = DataManager.getOrDefault("mfg.workflow.validTo", "09/01/2028");
		String pubStatus = DataManager.getOrDefault("mfg.workflow.publishStatus", "Under Revision");
		String author = DataManager.getOrDefault("mfg.workflow.author", "Test Team New");
		boolean isTemplate = Boolean.parseBoolean(DataManager.getOrDefault("mfg.workflow.isTemplate", "true"));

		// --- Test Data: Formula Master Header (PHX-313) ---
		String docType = DataManager.getOrDefault("fm.docType", "MFR");
		String make = DataManager.getOrDefault("fm.make", "ENIVID LIFE SCIENCES");
		String marketedBy = DataManager.getOrDefault("fm.marketedBy", "MEDWISE OVERSEAS PVT LTD");
		String product = DataManager.getOrDefault("fm.product", "ARIP01_aripiprazole_-1");
		String masterCardNo = DataManager.getOrDefault("fm.masterCardNo", "Test/MFR/Jan/ 10");
		String batchSize = DataManager.getOrDefault("fm.batchSize", "1000000");
		String validFromFm = DataManager.getOrDefault("fm.validFrom", "10/01/2023");
		String validToFm = DataManager.getOrDefault("fm.validTo", "09/01/2024");
		String avgBatchUom = DataManager.getOrDefault("fm.avgBatchUom", "kg");
		String labelClaimQty = DataManager.getOrDefault("fm.labelClaimPerQty", "1");
		String labelClaimUom = DataManager.getOrDefault("fm.labelClaimUom", "Tab");

		// --- Test Data: API Component (PHX-314) ---
		String comp1Product = DataManager.getOrDefault("fm.comp1.product", "Aceclofenac");
		boolean comp1CalcWeight = Boolean
				.parseBoolean(DataManager.getOrDefault("fm.comp1.calcWeight", "true"));
		String comp1ValidFrom = DataManager.getOrDefault("fm.comp1.validFrom", "10/01/2023");
		String comp1ValidTo = DataManager.getOrDefault("fm.comp1.validTo", "10/01/2024");
		String comp1Remarks = DataManager.getOrDefault("fm.comp1.remarks", "API Component");
		String comp1DispenseAt = DataManager.getOrDefault("fm.comp1.dispenseAt", "Blending");
		boolean comp1AllowPartly = Boolean
				.parseBoolean(DataManager.getOrDefault("fm.comp1.allowPartly", "true"));
		boolean comp1PerLabelClaim = Boolean
				.parseBoolean(DataManager.getOrDefault("fm.comp1.perLabelClaim", "true"));
		String comp1QtyPerLabelClaim = DataManager.getOrDefault("fm.comp1.qtyPerLabelClaim", "612");
		String comp1LabelClaimUom = DataManager.getOrDefault("fm.comp1.labelClaimUom", "Milligram");
		String comp1ExpectedBatchUom = DataManager.getOrDefault("fm.comp1.expectedPerBatchQtyBatchUom", "6.12");
		String comp1OveragePct = DataManager.getOrDefault("fm.comp1.overagePct", "10");

		// --- Test Data: Excipient Component (PHX-314) ---
		String comp2Product = DataManager.getOrDefault("fm.comp2.product", "Xanthan gum (Transparent gum)");
		String comp2Potency = DataManager.getOrDefault("fm.comp2.potencyCorrection", "Compensate");
		boolean comp2AllowPartly = Boolean
				.parseBoolean(DataManager.getOrDefault("fm.comp2.allowPartly", "true"));
		boolean comp2CalcWeight = Boolean
				.parseBoolean(DataManager.getOrDefault("fm.comp2.calcWeight", "true"));
		boolean comp2PerLabelClaim = Boolean
				.parseBoolean(DataManager.getOrDefault("fm.comp2.perLabelClaim", "true"));
		String comp2QtyPerLabelClaim = DataManager.getOrDefault("fm.comp2.qtyPerLabelClaim", "250");
		String comp2LabelClaimUom = DataManager.getOrDefault("fm.comp2.labelClaimUom", "Milligram");
		String comp2ValidFrom = DataManager.getOrDefault("fm.comp2.validFrom", "13/01/2023");
		String comp2ValidTo = DataManager.getOrDefault("fm.comp2.validTo", "31/01/2023");
		String comp2Remarks = DataManager.getOrDefault("fm.comp2.remarks", "Excipient Component");

		// -------------------------------------------------------------------------
		// STEP 1: Login into ERP with Admin Role
		// -------------------------------------------------------------------------
		log.info("Step 1: Logging in with user: [{}] and role: [{}]...", superUser, adminRole);
		loginPage.login(superUser, password);
		clientAndRole.selectClientAndRole(client, adminRole);

		// -------------------------------------------------------------------------
		// PHASE 1: Manufacturing Workflow Creation (PHX-310)
		// -------------------------------------------------------------------------
		log.info("----- Starting Phase 1: Create Manufacturing Workflow & Template (PHX-310) -----");

		// Step 2: Navigate to Manufacturing Workflows window
		log.info("Step 2: Searching and navigating to 'Manufacturing Workflows' window...");
		manufacturingWorkflow.navigateToManufacturingWorkflowsWindow();

		// Step 3: Click on New [+] Button
		log.info("Step 3: Initializing new Manufacturing Workflow record...");
		manufacturingWorkflow.clickNewRecord();

		// Step 4: Check whether mandatory fields are indicated by asterisk (*)
		log.info("Step 4: Checking mandatory field indicators (* symbol) on Name and Author...");
		manufacturingWorkflow.verifyMandatoryFields();

		// Step 5: Enter details in the fields
		log.info("Step 5: Entering Manufacturing Workflow details: Name=[{}], Type=[{}], Process=[{}]...",
				wfName, wfType, processType);
		manufacturingWorkflow.fillWorkflowDetails(wfName, wfType, processType, resource, qtyBatchSize,
				priority, validFromWf, validToWf, pubStatus, author, isTemplate);

		// Step 6: Make True Mark on Template Checkbox
		log.info("Step 6: Ensuring Template Checkbox is set to True...");
		manufacturingWorkflow.setTemplate(isTemplate);

		// Step 7: Click Save Button
		log.info("Step 7: Saving Manufacturing Workflow record...");
		manufacturingWorkflow.saveRecord();

		// Step 7.1: Create Activity records in Activity tab (PHX-311)
		log.info("Step 7.1: Creating Activity records in Activity tab (PHX-311)...");
		String nodesConfig = DataManager.getOrDefault("mfg.workflow.nodes",
				"Weighing,Blending,Granulation,Compression,Coating,Packaging");
		List<String> nodes = Arrays.asList(nodesConfig.split(","));
		manufacturingWorkflow.createActivityRecords(nodes);

		// Step 7.2: Return to Manufacturing Workflow header and close window
		log.info("Step 7.2: Returning to Manufacturing Workflow header and closing window...");
		manufacturingWorkflow.returnToWorkflowHeader();
		toolbar.closeWindow();

		activeTemplateWorkflowName = wfName;
		log.info("Phase 1 Complete: Manufacturing Workflow Template & Activities created with Name: [{}]",
				activeTemplateWorkflowName);

		// -------------------------------------------------------------------------
		// PHASE 2: Formula Master Record Creation (PHX-313)
		// -------------------------------------------------------------------------
		log.info("----- Starting Phase 2: Create Formula Master Record (PHX-313) -----");

		// Step 8: Search and navigate to Formula Master window
		log.info("Step 8: Searching and navigating to 'Formula Master' window...");
		formulaMasterPage.navigateToFormulaMasterWindow();

		// Step 9: Click on New [+] Button
		log.info("Step 9: Initializing new Formula Master record...");
		formulaMasterPage.clickNewRecord();

		// Step 10: Check whether mandatory fields are indicated by asterisk (*)
		log.info("Step 10: Checking mandatory field indicators (* symbol) on Product and Master Card...");
		formulaMasterPage.verifyMandatoryFields();

		// Steps 11 to 13: Enter Formula Master header details (Document Type: MFR) and select Template Workflow
		log.info("Steps 11-13: Filling Formula Master header details (DocType=[{}]) and selecting Template Workflow [{}]...",
				docType, activeTemplateWorkflowName);
		formulaMasterPage.fillFormulaMasterHeader(docType, make, marketedBy, product, masterCardNo, batchSize,
				validFromFm, validToFm, avgBatchUom, labelClaimQty, labelClaimUom,
				activeTemplateWorkflowName);

		// Step 14: Save Formula Master Record
		log.info("Step 14: Saving Formula Master record...");
		formulaMasterPage.saveFormulaMasterRecord();

		activeFormulaMasterDocNo = formulaMasterPage.getFormulaMasterDocumentNo();
		log.info("Phase 2 Complete: Formula Master record created with Doc No: [{}]", activeFormulaMasterDocNo);

		// -------------------------------------------------------------------------
		// PHASE 3: Formula Master Components Addition & Verifications (PHX-314)
		// -------------------------------------------------------------------------
		log.info("----- Starting Phase 3: Add Components in Formula Master (PHX-314) -----");

		// Step 15: Navigate to Child Tab "Component"
		log.info("Step 15: Navigating to child tab 'Component'...");
		formulaMasterPage.navigateToComponentTab();

		// Step 16: Add Component 1 (API Component)
		log.info("Step 16: Adding API Component [{}]...", comp1Product);
		formulaMasterPage.addApiComponent(comp1Product, comp1CalcWeight, comp1ValidFrom, comp1ValidTo,
				comp1Remarks, comp1DispenseAt, comp1AllowPartly, comp1PerLabelClaim,
				comp1QtyPerLabelClaim, comp1LabelClaimUom, comp1OveragePct);

		// Step 17: Verify Weight of Component in Average Batch Size
		log.info("Step 17: Verifying API component weight [{}] in Formula Master Average Batch Size...",
				comp1ExpectedBatchUom);
		formulaMasterPage.verifyAverageBatchSize(comp1ExpectedBatchUom);

		// Step 18: Add Component 2 (Excipient Component)
		log.info("Step 18: Adding Excipient Component [{}] with Potency Correction [{}]...",
				comp2Product, comp2Potency);
		formulaMasterPage.navigateToComponentTab();
		formulaMasterPage.addExcipientComponent(comp2Product, comp2Potency, comp2AllowPartly,
				comp2CalcWeight, comp2PerLabelClaim, comp2QtyPerLabelClaim, comp2LabelClaimUom,
				comp2ValidFrom, comp2ValidTo, comp2Remarks);

		// Step 19: Verify updated Average Batch Size includes component weight
		log.info("Step 19: Verifying updated Average Batch Size includes Excipient Component weight...");
		formulaMasterPage.verifyAverageBatchSize(null);

		// Step 20: Toggle "Evaporate ?" checkbox on component and verify weight deduction
		log.info("Step 20: Toggling Evaporate checkbox and verifying weight adjustment...");
		formulaMasterPage.toggleEvaporateCheckbox(true);

		// Step 21: Click "Verify Components" button and validate "Verified" checkbox is True
		log.info("Step 21: Clicking 'Verify Components' and validating Verified status is True...");
		formulaMasterPage.clickVerifyComponentsAndValidate();

		log.info("================================================================================");
		log.info("========== Completed Manufacturing Workflow & Formula Master Flow Successfully ==========");
		log.info("================================================================================");
	}
}
