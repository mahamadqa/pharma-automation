package baseModules;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.Test;

import base.BaseTest;
import utils.ConfigReader;

/**
 * End-to-end test flow:
 * 1. Login with Admin role
 * 2. Create and complete a Purchase Order (PO)
 * 3. In Menubar search "Goods receipt Note" and click on '+' button to create new record
 * 4. Attach PO to GRN Header & verify auto-populated fields
 * 5. Add Receipt lines from "Create Lines From" button & select the PO record
 * 6. Verify receipt line details and navigate back to GRN Header
 */
public class CreateGRNFromPOFlow extends BaseTest {

	private static final Logger log = LogManager.getLogger(CreateGRNFromPOFlow.class);

	@Test(groups = { "basic"}, description = "Goods Receipt Note Flow from Purchase Order: Login (Admin) → Create PO → Open GRN & Click New Record (+) → Attach PO → Add Receipt Lines via 'Create Lines From' → Verify & Save")
	public void verificationOfCreateGRNFromPOFlow() {

		log.info("========== Starting Goods Receipt Note from PO Flow ==========");

		// Step 1: Login with admin role
		log.info("Step 1: Logging in with Superuser credentials and Admin role...");
		loginPage.login(ConfigReader.get("Initiator"), ConfigReader.get("Password"));
		clientAndRole.selectClientAndRole(ConfigReader.get("client"), ConfigReader.get("SuperuserRole"));

		// Step 2: Create Purchase Order (PO)
		log.info("Step 2: Navigating to Purchase Order window and creating PO...");
		purchaseOrder.navigateToPOWindow();
		purchaseOrder.selectOrg();
		purchaseOrder.selecTtargetDocType();
		purchaseOrder.fillDescription();
		purchaseOrder.selectBPartner();
		purchaseOrder.selectSalesRep();
		purchaseOrder.selectWarehouse();
		toolbar.saveRecord();

		String poDocNo = purchaseOrder.getPODocNo();
		log.info("Purchase Order created with Doc No: [{}]", poDocNo);

		// Validations on PO Header
		purchaseOrder.verifyDateFields();
		toolbar.verifyDocStatus("Drafted");
		purchaseOrder.verifyBPLocationfields();
		purchaseOrder.verifyCurrency();
		purchaseOrder.verifyAmtFieldsBeforeInvLines();

		// Add PO Line
		purchaseOrder.clickOnPOLineTab();
		purchaseOrder.enterProdct();
		purchaseOrder.enterQtyEntered();
		purchaseOrder.verifyPOQty();
		purchaseOrder.verifyPriceEntered();
		purchaseOrder.selectTax();
		purchaseOrder.verifyPriceList();
		purchaseOrder.verifyLineNetAmt();
		toolbar.saveRecord();

		// Complete PO so it is available for Goods Receipt Note
		purchaseOrder.navigateBackOnPoHeader();
		purchaseOrder.verifyHeaderTotalLineAmt();
		purchaseOrder.verifyHeaderGrandTotalAmt();
		toolbar.performDocAction("Prepare");
		toolbar.verifyDocStatus("In Progress");
		toolbar.performDocAction("Complete");
		toolbar.verifyDocStatus("Completed");
		log.info("Purchase Order [{}] completed successfully.", poDocNo);
		toolbar.closeWindow();
		
		// Step 3: In Menubar search "Goods receipt Note" and click on + button to create new record
		log.info("Step 3: In Menubar searching 'Goods receipt Note' and clicking '+' to create new record...");
		goodsReceiptNote.navigateToGRNWindow();

		// Step 4: Attach PO to GRN Header
		log.info("Step 4: Filling GRN Header and attaching Purchase Order [{}]...", poDocNo);
		goodsReceiptNote.selectOrg();
		goodsReceiptNote.attachPO(poDocNo);

		// Verify fields automatically populated from Purchase Order
		goodsReceiptNote.verifyFieldsAfterAttachingPO(poDocNo);
		goodsReceiptNote.fillDescription();
		toolbar.saveRecord();

		String grnDocNo = goodsReceiptNote.getGRNDocNo();
		log.info("Goods Receipt Note created with Doc No: [{}]", grnDocNo);
		goodsReceiptNote.verifyDocStatus("Drafted");

		// Step 5: Add Receipt lines from "Create Lines From" button & select the record
		log.info("Step 5: Clicking 'Create Lines From' button, selecting the record, and confirming...");
		goodsReceiptNote.clickCreateLinesFromButton();
		goodsReceiptNote.selectRecordInCreateLinesFromModal(poDocNo);
		goodsReceiptNote.confirmCreateLinesFromModal();

		// Step 6: Verify Receipt Lines populated from PO
		log.info("Step 6: Navigating to Receipt Line tab and verifying line items created from PO...");
		goodsReceiptNote.clickOnReceiptLineTab();
		goodsReceiptNote.verifyReceiptLineAfterCreateLinesFrom(purchaseOrder);
		goodsReceiptNote.verifyHSNCode();
		goodsReceiptNote.verifyLocator();

		// Step 7: Enter No Of Case = 1, Qty per case = 150 and verify Pack Quantity is displaying 1*150
		log.info("Step 7: Entering No Of Case = 1, Qty per case = 150 and verifying Pack Quantity...");
		goodsReceiptNote.enterNoOfCase("1");
		goodsReceiptNote.enterQtyPerCase("150");
		goodsReceiptNote.verifyPackQuantity("1*150");

		// Step 8: Enter MFG date & verify Exp date, Retest date auto set
		log.info("Step 8: Entering MFG date and verifying Exp date & Retest date auto-set...");
		goodsReceiptNote.enterMfgDate();
		goodsReceiptNote.verifyDatesAutoSetAfterMfgDate();
		goodsReceiptNote.saveReceiptLine();

		// Step 9: Navigate back to GRN Header & verify status
		log.info("Step 9: Navigating back to GRN Header...");
		goodsReceiptNote.navigateBackOnGRNHeader();
		toolbar.verifyDocStatus("Drafted");
		
		toolbar.performDocAction("Prepare");
		toolbar.verifyDocStatus("In Progress");

		toolbar.performDocAction("Complete");
		toolbar.verifyDocStatus("Completed");
		

		log.info("========== Goods Receipt Note from PO Flow Completed Successfully! Doc No: [{}] ==========", grnDocNo);
	}
}
