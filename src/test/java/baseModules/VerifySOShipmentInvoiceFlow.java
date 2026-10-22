package baseModules;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.Test;

import base.BaseTest;
import utils.ConfigReader;

/**
 * Test class verifying Sales Order Creation, Header Field Validations,
 * Order Line product entry, Prepare & Complete document actions,
 * and final Header Totals verification.
 */
public class VerifySOShipmentInvoiceFlow extends BaseTest {

	private static final Logger log = LogManager.getLogger(VerifySOShipmentInvoiceFlow.class);

	@Test(groups = { "basic" }, description = "Sales Order Flow: Login → Open SO via Menu Search → Add Mandatory Details & Verify Header → Add Order Line with Product & Verify → Prepare & Complete DocAction → Verify Totals")
	public void verificationOfSOShipmentInvoiceFlow() {

		log.info("========== Starting Verify Sales Order Flow ==========");

		// Step 1: Login with Initiator credentials and Admin role
		log.info("Step 1: Logging in with Initiator credentials and Admin role...");
		loginPage.login(ConfigReader.get("Initiator"), ConfigReader.get("Password"));
		clientAndRole.selectClientAndRole(ConfigReader.get("client"), ConfigReader.get("SuperuserRole"));

		// Step 2: Open Sales Order window through menusearchbox
		log.info("Step 2: Navigating to Sales Order window through Menu Search Box...");
		salesOrder.navigateToSalesOrderWindow();

		// Step 3: Add mandatory details to Sales Order Header
		log.info("Step 3: Filling mandatory details on Sales Order Header...");
		salesOrder.selectOrg();
		salesOrder.selectTargetDocType();
		salesOrder.fillDescription();
		salesOrder.selectBPartner();
		salesOrder.selectSalesRep();
		salesOrder.selectWarehouse();
		salesOrder.handleWarningPopup();
		toolbar.saveRecord();

		String soDocNo = salesOrder.getSODocNo();
		log.info("Sales Order created with Doc No: [{}]", soDocNo);

		// Step 4: Verify Header fields as per previous implementations
		log.info("Step 4: Verifying auto-populated and mandatory header fields...");
		salesOrder.verifyDateFields();
		toolbar.verifyDocStatus("Drafted");
		salesOrder.verifyBPLocationFields();
		salesOrder.verifyCurrency();
		salesOrder.verifyAmtFieldsBeforeLines();

		// Step 5: Add 1 record in Order Line tab, fill product, qty, free qty, verify total qty & enter tax
		log.info("Step 5: Navigating to Order Line tab and adding 1 record with product...");
		salesOrder.clickOnOrderLineTab();
		toolbar.createNewRecord();
		salesOrder.enterProduct();
		salesOrder.enterQtyEntered();
		salesOrder.enterFreeQty();
		salesOrder.verifyTotalQty();
		salesOrder.verifySOQty();
		salesOrder.enterPriceEntered();
		salesOrder.enterPriceList();
		salesOrder.selectTax();
		salesOrder.verifyLineNetAmt();
		salesOrder.handleWarningPopup();
		toolbar.saveRecord();
		log.info("Order Line record saved successfully.");

		// Step 6: Navigate back to Order Header
		log.info("Step 6: Navigating back to Order Header...");
		salesOrder.navigateBackOnSOHeader();

		// Step 7: Prepare Document Action on Header & verify another order line created
		log.info("Step 7: Performing Prepare document action on Sales Order Header...");
		toolbar.performDocAction("Prepare");
		toolbar.verifyDocStatus("In Progress");

		// Step 7.1: Verify another order line is created with qty as 30 after Prepare action
		log.info("Step 7.1: Verifying another order line is created with Free Qty after Prepare action...");
		salesOrder.verifySecondOrderLineAfterPrepare();
		salesOrder.navigateBackOnSOHeader();

		// Step 7.2: Complete Document Action on Header
		log.info("Step 7.2: Performing Complete document action on Sales Order Header...");
		toolbar.performDocAction("Complete");
		toolbar.verifyDocStatus("Completed");
		log.info("Sales Order [{}] completed successfully.", soDocNo);

		// Step 8: Verify Total Lines & Grand Total amounts after document completion
		log.info("Step 8: Verifying Total Lines & Grand Total amounts after document completion...");
		salesOrder.verifyHeaderTotalLineAmt();
		salesOrder.verifyHeaderGrandTotalAmt();

		log.info("========== Sales Order Flow Completed for Doc No: [{}] ==========", soDocNo);
		toolbar.closeWindow();

		// ==================== Shipment (Customer) Flow ====================
		log.info("========== Starting Shipment (Customer) Flow ==========");

		// Step 9: In Menubar search "Shipment (Customer)" and click on '+' button to create new record
		log.info("Step 9: In Menubar searching 'Shipment (Customer)' and clicking '+' to create new record...");
		customerShipment.navigateToCustomerShipmentWindow();

		// Step 10: Attach Sales Order reference, save and verify reflected fields
		log.info("Step 10: Attaching Sales Order reference [{}] to new Shipment Header...", soDocNo);
		customerShipment.createNewRecord();
		customerShipment.attachSalesOrder(soDocNo);
		customerShipment.saveShipmentHeader();
		String shipmentDocNo = customerShipment.getShipmentDocNo();
		log.info("Shipment (Customer) record created with Doc No: [{}]", shipmentDocNo);
		customerShipment.verifyReflectedFieldsFromSO(soDocNo);

		// Step 11: Create lines from button to create shipment lines from SO
		log.info("Step 11: Creating shipment lines using 'Create Lines From' button...");
		customerShipment.createShipmentLinesFromSO();

		// Step 12: Navigate to Shipment Line tab, verify Qty & attach Approved ASI for both lines
		log.info("Step 12: Navigating to Shipment Line tab, verifying Qty and attaching Approved ASI for both lines...");
		customerShipment.navigateToShipmentLineTab();
		customerShipment.verifyAndAttachASIForBothLines();

		// Step 13: Go back to header
		log.info("Step 13: Navigating back to Shipment Header...");
		customerShipment.navigateBackToShipmentHeader();

		// Step 14: Prepare Document Action on Shipment Header
		log.info("Step 14: Performing Prepare document action on Shipment Header...");
		toolbar.performDocAction("Prepare");
		toolbar.verifyDocStatus("In Progress");

		// Step 15: Complete Document Action on Shipment Header
		log.info("Step 15: Performing Complete document action on Shipment Header...");
		toolbar.performDocAction("Complete");
		toolbar.verifyDocStatus("Completed");
		log.info("Shipment (Customer) [{}] completed successfully.", shipmentDocNo);

		// Step 16: Verify Posting by clicking on Posted button
		log.info("Step 16: Verifying Posting by clicking on Posted button on Shipment Header...");
		customerShipment.clickPostedButton();
		customerShipment.verifyPostedDebitEqualsCredit();
		String postedAmt = customerShipment.getPostedAmount();
		log.info("Step 16: Verified Shipment Posted Debit and Credit amounts are equal: [{}]", postedAmt);
		customerShipment.closePostingWindow();

		// ==================== Sales Invoice Flow ====================
		log.info("========== Starting Sales Invoice Flow ==========");

		// Step 17: Go to Invoice window which is already opened and verify header fields
		log.info("Step 17: Switching to already opened Invoice window and verifying header fields...");
		salesInvoice.switchToInvoiceWindow(); 
		String invoiceDocNo = salesInvoice.getInvoiceDocNo();
		log.info("Active Sales Invoice Document No: [{}]", invoiceDocNo);
		salesInvoice.verifyInvoiceHeaderFields(soDocNo);
		salesInvoice.verifyHeaderTotalAmounts();

		// Step 18: Verify lines from SO on Invoice Line tab
		log.info("Step 18: Verifying invoice lines populated from SO / Shipment...");
		salesInvoice.verifyInvoiceLinesFromSO();

		// Step 19: Prepare document action on Invoice Header
		log.info("Step 19: Performing Prepare document action on Invoice Header...");
		salesInvoice.performDocAction("Prepare");
		salesInvoice.verifyDocStatus("In Progress");

		// Step 20: Complete document action on Invoice Header
		log.info("Step 20: Performing Complete document action on Invoice Header...");
		salesInvoice.performDocAction("Complete");
		salesInvoice.verifyDocStatus("Completed");
		log.info("Sales Invoice [{}] completed successfully.", invoiceDocNo);

		// Step 21: Verify Posting by clicking on Posted button on Invoice Header
		log.info("Step 21: Verifying Posting by clicking on Posted button on Invoice Header...");
		salesInvoice.clickPostedButton();
		salesInvoice.verifyPostedDebitEqualsCredit();
		String invoicePostedAmt = salesInvoice.getPostedAmount();
		log.info("Step 21: Verified Sales Invoice Posted Debit and Credit amounts are equal: [{}]", invoicePostedAmt);
		salesInvoice.closePostingWindow();

		log.info("========== Verify SO, Shipment & Invoice Flow Completed Successfully! SO: [{}], Shipment: [{}], Invoice: [{}] ==========",
				soDocNo, shipmentDocNo, invoiceDocNo);
	}
}
