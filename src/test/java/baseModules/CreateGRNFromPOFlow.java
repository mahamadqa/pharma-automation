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
 * 7. Complete GRN doc action and verify posting
 * 8. Open Attribute Set Instance, verify Located At and Cost tabs
 * 9. In Menubar search "Purchase Invoice and Credit/Debit Note" and create new record
 * 10. Fill Org & BPartner, attach created PO via "Create lines from" button
 * 11. Verify the fields in Invoice line
 * 12. Match Grand total & Total lines on header
 * 13. Complete doc action on Invoice
 * 14. Verify invoice posting (Debit equals Credit)
 */
public class CreateGRNFromPOFlow extends BaseTest {

	private static final Logger log = LogManager.getLogger(CreateGRNFromPOFlow.class);

	@Test(groups = { "basic"}, description = "End-to-End Flow: Login → Create PO → Create GRN from PO & Verify → Create Purchase Invoice from PO & Verify Lines, Header Totals, DocAction & Posting")
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

		// Close Goods Receipt Note window
		toolbar.closeWindow();

		log.info("Goods Receipt Note [{}] processed successfully. Proceeding to Purchase Invoice Flow...", grnDocNo);

		// ==================== PURCHASE INVOICE FLOW ====================
		// Step 13: In Menubar search "Purchase Invoice and Credit/Debit Note" and open new record
		log.info("Step 13: Opening 'Purchase Invoice and Credit/Debit Note' window through menu search...");
		purchaseInvoice.navigateToInvoiceWindow();

		// Step 14: Fill Org & BPartner, save header, and attach created PO via "Create lines from" button
		log.info("Step 14: Filling Org & BPartner on Invoice Header, saving record, and attaching created PO [{}]...", poDocNo);
		purchaseInvoice.selectOrg();
		purchaseInvoice.selectTargetDocType("AP Invoice");
		purchaseInvoice.selectBPartner();
		purchaseInvoice.fillDescription();
		toolbar.saveRecord();

		String invoiceDocNo = purchaseInvoice.getInvoiceDocNo();
		log.info("Purchase Invoice created with Doc No: [{}]", invoiceDocNo);
		toolbar.verifyDocStatus("Drafted");

		purchaseInvoice.clickCreateLinesFromButton();
		purchaseInvoice.attachPOInCreateLinesFromModal(poDocNo);
		purchaseInvoice.confirmCreateLinesFromModal();

		// Step 15: Verify the fields in Invoice line
		log.info("Step 15: Navigating to Invoice Line tab and verifying line fields...");
		purchaseInvoice.clickOnInvoiceLineTab();
		toolbar.ensureSubtabFormView();
		purchaseInvoice.verifyInvoiceLineFields(purchaseOrder);

		// Step 16: Match Grand total & Total lines on header
		log.info("Step 16: Navigating back to Invoice Header and matching Grand total & Total lines...");
		purchaseInvoice.navigateBackOnInvoiceHeader();
		purchaseInvoice.verifyHeaderTotalLinesAndGrandTotal(purchaseOrder);

		// Step 17: Do complete the doc action
		log.info("Step 17: Completing document action for Purchase Invoice [{}]...", invoiceDocNo);
		toolbar.performDocAction("Complete");
		toolbar.verifyDocStatus("Completed");
		log.info("Purchase Invoice [{}] completed successfully.", invoiceDocNo);

		// Step 18: Verify the posting
		log.info("Step 18: Clicking 'Posted' button on Purchase Invoice and verifying posting...");
		purchaseInvoice.clickPostedButton();
		purchaseInvoice.verifyPostedDebitEqualsCredit();
		String invoicePostedAmt = purchaseInvoice.getPostedAmount();
		log.info("Step 18: Verified Purchase Invoice Posted Debit and Credit amounts are equal: [{}]", invoicePostedAmt);
		purchaseInvoice.closePostingWindow();

		// Close Purchase Invoice window
		toolbar.closeWindow();

		log.info("========== Goods Receipt Note & Purchase Invoice Flow from PO Completed Successfully! GRN Doc No: [{}], Invoice Doc No: [{}] ==========", grnDocNo, invoiceDocNo);
	}
}
