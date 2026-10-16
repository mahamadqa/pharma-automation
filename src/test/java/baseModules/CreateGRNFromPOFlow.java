package baseModules;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.Test;

import base.BaseTest;
import utils.ConfigReader;
import utils.DataManager;

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

		// Step 10: Click on Posted button on GRN window & verify Accounted Debit == Accounted Credit
		log.info("Step 10: Clicking on 'Posted' button on Goods Receipt Note...");
		goodsReceiptNote.clickPostedButton();
		goodsReceiptNote.verifyPostedDebitEqualsCredit();
		String postedAmt = goodsReceiptNote.getPostedAmount();
		log.info("Step 10: Verified Posted Debit and Credit amounts are equal: [{}]", postedAmt);
		goodsReceiptNote.closePostingWindow();

		// Step 11: After completing doc action for GRN, go to Receipt Line tab
		log.info("Step 11: Navigating to Receipt Line tab after completing GRN doc action...");
		goodsReceiptNote.clickOnReceiptLineTab();

		// 1. Get the Movement Quantity & store it
		String movementQty = goodsReceiptNote.getMovementQty();
		log.info("Step 11.1: Retrieved & stored Movement Quantity: [{}]", movementQty);

		// Store Receipt Line's Attribute Set Instance field text
		String receiptLineASI = goodsReceiptNote.getAttributeSetInstanceText();
		log.info("Step 11.1: Retrieved & stored Receipt Line Attribute Set Instance field value: [{}]", receiptLineASI);

		// 2. Do right click on Attribute Set Instance & click on Zoom option
		log.info("Step 11.2: Right clicking on Attribute Set Instance field and clicking on Zoom option...");
		goodsReceiptNote.zoomOnAttributeSetInstance();

		// 3. Verify Attribute Set Instance window is opened & match description with receipt line's Attribute Set Instance field
		log.info("Step 11.3: Verifying Attribute Set Instance window is opened & matching Description...");
		attributeSetInstance.verifyAttributeSetInstanceWindowOpened();
		attributeSetInstance.verifyDescription(receiptLineASI);

		// 4. Go to Located At subtab, verify product field & On hand qty = Movement qty from GRN Receipt Line
		log.info("Step 11.4: Navigating to Located At subtab and verifying Product and On Hand Quantity...");
		attributeSetInstance.clickOnLocatedAtTab();
		attributeSetInstance.verifyProduct(DataManager.getData("product"));
		attributeSetInstance.verifyQtyOnHand(movementQty);
		attributeSetInstance.navigateBackToheader();

		// Step 12: In Attribute Set Instance, go to Cost subtab & navigate to the record having Average PO as costing method
		log.info("Step 12: Navigating to Cost subtab in Attribute Set Instance and locating 'Average PO' record...");
		attributeSetInstance.clickOnCostTab();
		attributeSetInstance.navigateToCostingMethod("Average PO");

		// Fetch Accumulated amt & verify it equals Posted Credit & Debit amount
		String accumulatedAmt = attributeSetInstance.getAccumulatedAmt();
		log.info("Step 12: Fetched Accumulated Amount: [{}]", accumulatedAmt);
		attributeSetInstance.verifyAccumulatedAmt(postedAmt);

		// Close Attribute Set Instance window
		attributeSetInstance.closeWindow();

		log.info("========== Goods Receipt Note from PO Flow Completed Successfully! Doc No: [{}] ==========", grnDocNo);
	}
}
