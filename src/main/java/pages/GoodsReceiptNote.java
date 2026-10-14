package pages;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitForSelectorState;

import utils.DataManager;
import utils.WaitUtils;

/**
 * Page Object Class for the Goods Receipt Note (Vendor Shipments / Receipts) Window.
 */
public class GoodsReceiptNote {

	private static final Logger log = LogManager.getLogger(GoodsReceiptNote.class);
	private final Page page;

	// ==================== Navigation & Breadcrumb Locators ====================
	private final Locator grnMenuSearchInput;
	private final Locator searchGRNWindowItem;
	private final Locator grnHeaderBreadcrumb;
	private final Locator grnTabHeader;

	// ==================== Goods Receipt Note Header Locators ====================
	private final Locator orgInput;
	private final Locator gateInwardInput;
	private final Locator orderInput;
	private final Locator dateOrderedInput;
	private final Locator rmaInput;
	private final Locator documentNoInput;
	private final Locator poReferenceInput;
	private final Locator descriptionInput;
	private final Locator docTypeInput;
	private final Locator movementDateInput;
	private final Locator accountDateInput;
	private final Locator bPartnerInput;
	private final Locator bPartnerLocationInput;
	private final Locator userInput;
	private final Locator salesRepInput;
	private final Locator warehouseInput;
	private final Locator priorityRuleInput;
	private final Locator deliveryRuleInput;
	private final Locator freightCostRuleInput;
	private final Locator freightAmtInput;
	private final Locator invoiceNoInput;
	private final Locator dateInvoicedInput;
	private final Locator chargeInput;
	private final Locator chargeAmtInput;
	private final Locator receivedByInput;
	private final Locator checkedByInput;
	private final Locator isDropShipCheckbox;
	private final Locator dropShipBPartnerInput;
	private final Locator dropShipLocationInput;
	private final Locator dropShipUserInput;
	private final Locator createLinesFromButton;
	private final Locator deliveryViaRuleInput;
	private final Locator shipperInput;
	private final Locator trackingNoInput;
	private final Locator challanNoInput;
	private final Locator challanDateInput;
	private final Locator lrNoInput;
	private final Locator lrDateInput;
	private final Locator eWayBillNoInput;
	private final Locator departmentInput;
	private final Locator projectInput;
	private final Locator campaignInput;
	private final Locator channelInput;
	private final Locator activityInput;
	private final Locator costCenterInput;
	private final Locator trxOrgInput;
	private final Locator userElement1Input;
	private final Locator userElement2Input;
	private final Locator movementTypeInput;
	private final Locator docStatusInput;
	private final Locator isInTransitCheckbox;
	private final Locator isInDisputeCheckbox;
	private final Locator docActionButton;
	private final Locator dateReceivedInput;
	private final Locator postedButton;

	// ==================== Receipt Line Tab Locators ====================
	private final Locator receiptLineTab;
	private final Locator lineOrgInput;
	private final Locator receiptRefInput;
	private final Locator lineNoInput;
	private final Locator orderLineInput;
	private final Locator vendorProductNoInput;
	private final Locator lineProductInput;
	private final Locator hsnCodeInput;
	private final Locator productCategoryTypeInput;
	private final Locator locatorInput;
	private final Locator lineChargeInput;
	private final Locator lineDescriptionTextarea;
	private final Locator qtyInvoicedInput;
	private final Locator qtyExcessInput;
	private final Locator qtyShortageInput;
	private final Locator qtyEnteredInput;
	private final Locator uomInput;
	private final Locator movementQtyInput;
	private final Locator isQCCheckbox;
	private final Locator noOfCaseInput;
	private final Locator caseSizeInput;
	private final Locator packInput;
	private final Locator attributeSetInstanceInput;
	private final Locator mrpInput;
	private final Locator inwardNoInput;
	private final Locator lotInput;
	private final Locator mfgByInput;
	private final Locator vendorARNoInput;
	private final Locator forMakeInput;
	private final Locator mfgDateInput;
	private final Locator guaranteeDateInput;
	private final Locator retestDateInput;
	private final Locator artworkInput;
	private final Locator checkListButton;
	private final Locator storageConditionInput;
	private final Locator potencyInput;
	private final Locator dateFormatInput;
	private final Locator createContainersButton;
	private final Locator damageInput;
	private final Locator sealInput;
	private final Locator waterPenetrationInput;
	private final Locator labelConditionInput;
	private final Locator outerPackingInput;
	private final Locator lineSaveAndNewButton;
	private final Locator lineSaveButton;

	public GoodsReceiptNote(Page page) {
		this.page = page;

		// Navigation & Breadcrumbs
		this.grnMenuSearchInput = page.locator("//input[@class='z-bandbox-input']");
		this.searchGRNWindowItem = page.locator(
				"//td[contains(.,'Goods Receipt Note')]/following-sibling::td//a[@title='New'] | //td[@title='Vendor Shipments (Receipts)']/following-sibling::td//a[@title='New']");
		this.grnHeaderBreadcrumb = page.locator(
				"//div[@instancename='breadcrumb']//span[text()='Goods Receipt Note'] | //div[@instancename='breadcrumb']//a[contains(text(),'Goods Receipt Note')]");
		this.grnTabHeader = page.locator("//li[contains(@class,'z-tab')]//span[contains(text(),'Goods Receipt Note')]");

		// Header Form Fields
		this.orgInput = page.locator("//span[@instancename='M_InOut0AD_Org_ID']//input");
		this.gateInwardInput = page.locator("//span[@instancename='M_InOut0PHX_SecGateInward_ID']//input");
		this.orderInput = page.locator("//div[@instancename='M_InOut0C_Order_ID']//input");
		this.dateOrderedInput = page.locator("//span[@instancename='M_InOut0DateOrdered']//input");
		this.rmaInput = page.locator("//div[@instancename='M_InOut0M_RMA_ID']//input");
		this.documentNoInput = page.locator("//input[@instancename='M_InOut0DocumentNo']");
		this.poReferenceInput = page.locator("//input[@instancename='M_InOut0POReference']");
		this.descriptionInput = page.locator("//textarea[@instancename='M_InOut0Description']");
		this.docTypeInput = page.locator("//span[@instancename='M_InOut0C_DocType_ID']//input");
		this.movementDateInput = page.locator("//span[@instancename='M_InOut0MovementDate']//input");
		this.accountDateInput = page.locator("//span[@instancename='M_InOut0DateAcct']//input");
		this.bPartnerInput = page.locator("//div[@instancename='M_InOut0C_BPartner_ID']//input");
		this.bPartnerLocationInput = page.locator("//span[@instancename='M_InOut0C_BPartner_Location_ID']//input");
		this.userInput = page.locator("//span[@instancename='M_InOut0AD_User_ID']//input");
		this.salesRepInput = page.locator("//span[@instancename='M_InOut0SalesRep_ID']//input");
		this.warehouseInput = page.locator("//span[@instancename='M_InOut0M_Warehouse_ID']//input");
		this.priorityRuleInput = page.locator("//span[@instancename='M_InOut0PriorityRule']//input");
		this.deliveryRuleInput = page.locator("//span[@instancename='M_InOut0DeliveryRule']//input");
		this.freightCostRuleInput = page.locator("//span[@instancename='M_InOut0FreightCostRule']//input");
		this.freightAmtInput = page.locator("//div[@instancename='M_InOut0FreightAmt']//input");
		this.invoiceNoInput = page.locator("//input[@instancename='M_InOut0InvoiceNo']");
		this.dateInvoicedInput = page.locator("//span[@instancename='M_InOut0DateInvoiced']//input");
		this.chargeInput = page.locator("//span[@instancename='M_InOut0C_Charge_ID']//input");
		this.chargeAmtInput = page.locator("//div[@instancename='M_InOut0ChargeAmt']//input");
		this.receivedByInput = page.locator("//div[@instancename='M_InOut0PHX_ReceivedBy_ID']//input");
		this.checkedByInput = page.locator("//div[@instancename='M_InOut0PHX_CheckedBy_ID']//input");
		this.isDropShipCheckbox = page.locator("//span[@instancename='M_InOut0IsDropShip']//input");
		this.dropShipBPartnerInput = page.locator("//div[@instancename='M_InOut0DropShip_BPartner_ID']//input");
		this.dropShipLocationInput = page.locator("//span[@instancename='M_InOut0DropShip_Location_ID']//input");
		this.dropShipUserInput = page.locator("//span[@instancename='M_InOut0DropShip_User_ID']//input");
		this.createLinesFromButton = page.locator("//button[@instancename='M_InOut0CreateLinesFrom']");
		this.deliveryViaRuleInput = page.locator("//span[@instancename='M_InOut0DeliveryViaRule']//input");
		this.shipperInput = page.locator("//span[@instancename='M_InOut0M_Shipper_ID']//input");
		this.trackingNoInput = page.locator("//input[@instancename='M_InOut0TrackingNo']");
		this.challanNoInput = page.locator("//input[@instancename='M_InOut0PHX_ChallanNo']");
		this.challanDateInput = page.locator("//span[@instancename='M_InOut0PHX_ChallanDate']//input");
		this.lrNoInput = page.locator("//input[@instancename='M_InOut0LGST_LR_No']");
		this.lrDateInput = page.locator("//span[@instancename='M_InOut0LGST_LR_Date']//input");
		this.eWayBillNoInput = page.locator("//input[@instancename='M_InOut0PHX_EWayBillNo']");
		this.departmentInput = page.locator("//span[@instancename='M_InOut0C_Department_ID']//input");
		this.projectInput = page.locator("//span[@instancename='M_InOut0C_Project_ID']//input");
		this.campaignInput = page.locator("//span[@instancename='M_InOut0C_Campaign_ID']//input");
		this.channelInput = page.locator("//span[@instancename='M_InOut0C_Channel_ID']//input");
		this.activityInput = page.locator("//span[@instancename='M_InOut0C_Activity_ID']//input");
		this.costCenterInput = page.locator("//span[@instancename='M_InOut0C_CostCenter_ID']//input");
		this.trxOrgInput = page.locator("//span[@instancename='M_InOut0AD_OrgTrx_ID']//input");
		this.userElement1Input = page.locator("//div[@instancename='M_InOut0User1_ID']//input");
		this.userElement2Input = page.locator("//div[@instancename='M_InOut0User2_ID']//input");
		this.movementTypeInput = page.locator("//span[@instancename='M_InOut0MovementType']//input");
		this.docStatusInput = page.locator("//span[@instancename='M_InOut0DocStatus']//input");
		this.isInTransitCheckbox = page.locator("//span[@instancename='M_InOut0IsInTransit']//input");
		this.isInDisputeCheckbox = page.locator("//span[@instancename='M_InOut0IsInDispute']//input");
		this.docActionButton = page.locator("//button[@instancename='M_InOut0DocAction']");
		this.dateReceivedInput = page.locator("//span[@instancename='M_InOut0DateReceived']//input");
		this.postedButton = page.locator("//button[@instancename='M_InOut0Posted']");

		// Receipt Line Tab Fields
		this.receiptLineTab = page.locator("//span[text()='Receipt Line']");
		this.lineOrgInput = page.locator("//span[@instancename='M_InOutLine0AD_Org_ID']//input");
		this.receiptRefInput = page.locator("//div[@instancename='M_InOutLine0M_InOut_ID']//input");
		this.lineNoInput = page.locator("//div[@instancename='M_InOutLine0Line']//input");
		this.orderLineInput = page.locator("//span[@instancename='M_InOutLine0C_OrderLine_ID']//input");
		this.vendorProductNoInput = page.locator("//input[@instancename='M_InOutLine0VendorProductNo']");
		this.lineProductInput = page.locator("//div[@instancename='M_InOutLine0M_Product_ID']//input");
		this.hsnCodeInput = page.locator("//div[@instancename='M_InOutLine0LGST_HSNCode_ID']//input");
		this.productCategoryTypeInput = page.locator("//span[@instancename='M_InOutLine0PHX_ProductCategoryType']//input");
		this.locatorInput = page.locator("//div[@instancename='M_InOutLine0M_Locator_ID']//input");
		this.lineChargeInput = page.locator("//span[@instancename='M_InOutLine0C_Charge_ID']//input");
		this.lineDescriptionTextarea = page.locator("//textarea[@instancename='M_InOutLine0Description']");
		this.qtyInvoicedInput = page.locator("//div[@instancename='M_InOutLine0QtyInvoiced']//input");
		this.qtyExcessInput = page.locator("//div[@instancename='M_InOutLine0QtyExcess']//input");
		this.qtyShortageInput = page.locator("//div[@instancename='M_InOutLine0QtyShortage']//input");
		this.qtyEnteredInput = page.locator("//div[@instancename='M_InOutLine0QtyEntered']//input");
		this.uomInput = page.locator("//span[@instancename='M_InOutLine0C_UOM_ID']//input");
		this.movementQtyInput = page.locator("//div[@instancename='M_InOutLine0MovementQty']//input");
		this.isQCCheckbox = page.locator("//span[@instancename='M_InOutLine0IsQC']//input");
		this.noOfCaseInput = page.locator("//div[@instancename='M_InOutLine0PHX_NoofCase']//input");
		this.caseSizeInput = page.locator("//div[@instancename='M_InOutLine0PHX_CaseSize']//input");
		this.packInput = page.locator("//input[@instancename='M_InOutLine0PHX_Pack']");
		this.attributeSetInstanceInput = page.locator("//div[@instancename='M_InOutLine0M_AttributeSetInstance_ID']//input");
		this.mrpInput = page.locator("//div[@instancename='M_InOutLine0PHX_MRP']//input");
		this.inwardNoInput = page.locator("//input[@instancename='M_InOutLine0PHX_InwardNo']");
		this.lotInput = page.locator("//input[@instancename='M_InOutLine0Lot']");
		this.mfgByInput = page.locator("//span[@instancename='M_InOutLine0PHX_MfgBy_ID']//input");
		this.vendorARNoInput = page.locator("//input[@instancename='M_InOutLine0PHX_VendorARNo']");
		this.forMakeInput = page.locator("//span[@instancename='M_InOutLine0PHX_Make_ID']//input");
		this.mfgDateInput = page.locator("//span[@instancename='M_InOutLine0PHX_MfgDate']//input");
		this.guaranteeDateInput = page.locator("//span[@instancename='M_InOutLine0GuaranteeDate']//input");
		this.retestDateInput = page.locator("//span[@instancename='M_InOutLine0PHX_RetestDate']//input");
		this.artworkInput = page.locator("//span[@instancename='M_InOutLine0PHX_Artwork_ID']//input");
		this.checkListButton = page.locator("//button[@instancename='M_InOutLine0PHX_CheckList']");
		this.storageConditionInput = page.locator("//div[@instancename='M_InOutLine0PHX_StorageCondition_ID']//input");
		this.potencyInput = page.locator("//div[@instancename='M_InOutLine0PHX_Potency']//input");
		this.dateFormatInput = page.locator("//span[@instancename='M_InOutLine0PHX_DateFormat_ID']//input");
		this.createContainersButton = page.locator("//button[@instancename='M_InOutLine0PHX_CreateContainers']");
		this.damageInput = page.locator("//input[@instancename='M_InOutLine0PHX_Damage']");
		this.sealInput = page.locator("//input[@instancename='M_InOutLine0PHX_Seal']");
		this.waterPenetrationInput = page.locator("//input[@instancename='M_InOutLine0PHX_WaterPanetration']");
		this.labelConditionInput = page.locator("//input[@instancename='M_InOutLine0PHX_LabelCondition']");
		this.outerPackingInput = page.locator("//input[@instancename='M_InOutLine0PHX_OuterPacking']");
		this.lineSaveAndNewButton = page.locator("//div[contains(@class,'adwindow-detailpane')]//a[contains(@title,'Save and Create New')]");
		this.lineSaveButton = page.locator("//div[contains(@class,'adwindow-detailpane')]//a[contains(@title,'Save Changes')]");
	}

	// ==================== Navigation Actions ====================

	/**
	 * Navigates to the Goods Receipt Note window via menubar search bandbox and clicks '+/New'.
	 */
	public void navigateToGRNWindow() {
		page.waitForTimeout(1000);
		log.info("Navigating to Goods Receipt Note window via menubar search...");
		grnMenuSearchInput.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		grnMenuSearchInput.fill("Goods receipt Note");
		page.waitForTimeout(2000);
		WaitUtils.waitForLoadingIndicatorToDisappear(page);

		searchGRNWindowItem.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.ATTACHED).setTimeout(10000));
		long startTime = System.currentTimeMillis();
		long timeout = 10000;
		searchGRNWindowItem.click();

		while (!orgInput.isVisible() && System.currentTimeMillis() - startTime < timeout) {
			searchGRNWindowItem.click();
			page.waitForTimeout(300);
		}

		page.waitForTimeout(1000);
		WaitUtils.waitForLoadingIndicatorToDisappear(page);
		log.info("Opened Goods Receipt Note window successfully.");
	}

	// ==================== Header Actions ====================

	public void selectOrg(String orgName) {
		page.waitForTimeout(1000);
		log.info("Selecting Organization: [{}]", orgName);
		orgInput.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		orgInput.fill(orgName);
		orgInput.press("Tab");
		page.waitForTimeout(2000);
		verifyFieldFilled(orgInput, "Organization", orgName);
		log.info("Selected Organization: [{}]", orgName);
	}

	public void selectOrg() {
		selectOrg(DataManager.getData("org"));
		WaitUtils.waitForLoadingIndicatorToDisappear(page);
	}

	public void selectDocType(String docType) {
		log.info("Selecting Document Type: [{}]", docType);
		docTypeInput.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		docTypeInput.fill(docType);
		docTypeInput.press("Tab");
		page.waitForTimeout(2000);
		verifyFieldFilled(docTypeInput, "Document Type", docType);
		log.info("Selected Document Type: [{}]", docType);
	}

	public void selectDocType() {
		selectDocType(DataManager.getData("targetDocType"));
	}

	public void fillDescription(String desc) {
		descriptionInput.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(5000));
		descriptionInput.click();
		descriptionInput.fill(desc);
		log.info("Description filled: [{}]", desc);
	}

	public void fillDescription() {
		fillDescription(DataManager.getData("description"));
	}

	public void selectBPartner(String partnerName) {
		log.info("Selecting Business Partner: [{}]", partnerName);
		bPartnerInput.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		bPartnerInput.fill(partnerName);
		bPartnerInput.press("Tab");
		page.waitForTimeout(2000);
		verifyFieldFilled(bPartnerInput, "Business Partner", partnerName);
		log.info("Selected Business Partner: [{}]", partnerName);
	}

	public void selectBPartner() {
		selectBPartner(DataManager.getData("bPartner"));
		WaitUtils.waitForLoadingIndicatorToDisappear(page);
	}

	public void selectWarehouse(String warehouseName) {
		page.waitForTimeout(1000);
		log.info("Selecting Warehouse: [{}]", warehouseName);
		warehouseInput.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		warehouseInput.fill(warehouseName);
		warehouseInput.press("Tab");
		page.waitForTimeout(2000);
		verifyFieldFilled(warehouseInput, "Warehouse", warehouseName);
		log.info("Selected Warehouse: [{}]", warehouseName);
	}

	public void selectWarehouse() {
		selectWarehouse(DataManager.getData("warehouse"));
		WaitUtils.waitForLoadingIndicatorToDisappear(page);
	}

	public void selectGateInward(String gateInward) {
		log.info("Selecting Gate Inward: [{}]", gateInward);
		gateInwardInput.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(5000));
		gateInwardInput.fill(gateInward);
		gateInwardInput.press("Tab");
		page.waitForTimeout(1000);
	}

	/**
	 * Gets the Document Number from PurchaseOrder page object and fills it into orderInput element.
	 *
	 * @param purchaseOrder The PurchaseOrder page object instance
	 */
	public void attachPO(PurchaseOrder purchaseOrder) {
		String poDocNo = "";
		try {
			poDocNo = purchaseOrder.getPODocNo();
		} catch (Exception e) {
			log.warn("Could not read PO Doc No from current active screen: {}", e.getMessage());
		}
		if (poDocNo == null || poDocNo.trim().isEmpty()) {
			poDocNo = PurchaseOrder.getLastPODocNo();
		}
		log.info("Retrieved PO Document No [{}] from PurchaseOrder", poDocNo);
		attachPO(poDocNo);
	}

	/**
	 * Gets the cached PO Document Number created by PurchaseOrder and fills it into orderInput element.
	 */
	public void attachPO() {
		String poDocNo = PurchaseOrder.getLastPODocNo();
		log.info("Retrieved cached PO Document No [{}] from PurchaseOrder", poDocNo);
		attachPO(poDocNo);
	}

	/**
	 * Fills the provided Purchase Order Document Number into orderInput element.
	 *
	 * @param poDocNo Purchase Order Document Number
	 */
	public void attachPO(String poDocNo) {
		page.waitForTimeout(1000);
		log.info("Filling Purchase Order Doc No [{}] into orderInput...", poDocNo);
		orderInput.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		orderInput.click();
		orderInput.fill(poDocNo);
		orderInput.press("Tab");
		page.waitForTimeout(2000);
		WaitUtils.waitForLoadingIndicatorToDisappear(page);
		verifyFieldFilled(orderInput, "Purchase Order", poDocNo);
		log.info("Successfully attached Purchase Order [{}] into orderInput.", poDocNo);
	}

	/**
	 * Alias for attachPO(PurchaseOrder purchaseOrder).
	 */
	public void attachPurchaseOrder(PurchaseOrder purchaseOrder) {
		attachPO(purchaseOrder);
	}

	/**
	 * Alias for attachPO(String poDocNo).
	 */
	public void attachPurchaseOrder(String poDocNo) {
		attachPO(poDocNo);
	}

	/**
	 * Alias for attachPO().
	 */
	public void attachPurchaseOrder() {
		attachPO();
	}

	public void selectOrder(String orderNo) {
		attachPO(orderNo);
	}

	public void selectOrder(PurchaseOrder purchaseOrder) {
		attachPO(purchaseOrder);
	}

	public String getGRNDocNo() {
		Locator documentSequence = page.locator("//input[@instancename='M_InOut0DocumentNo']");
		String value = documentSequence.inputValue();
		return value;
	}

	public void verifyDocStatus(String expectedStatus) {
		page.waitForTimeout(1000);
		docStatusInput.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		String actualStatus = docStatusInput.inputValue().trim();
		log.info("Verifying Document Status: Actual [{}], Expected [{}]", actualStatus, expectedStatus);

		boolean isMatch = actualStatus.equalsIgnoreCase(expectedStatus.trim());
		SoftAssert sa = base.BaseTest.getSoftAssert();
		if (sa != null) {
			sa.assertTrue(isMatch, "[GRN] Document Status [" + actualStatus + "] does not match expected [" + expectedStatus + "]");
		} else {
			Assert.assertTrue(isMatch, "Document Status [" + actualStatus + "] does not match expected [" + expectedStatus + "]");
		}
	}

	// ==================== Receipt Line Actions ====================

	public void clickOnReceiptLineTab() {
		log.info("Clicking on 'Receipt Line' tab...");
		receiptLineTab.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		receiptLineTab.click();
		page.waitForTimeout(1000);
		WaitUtils.waitForLoadingIndicatorToDisappear(page);
		log.info("Navigated to 'Receipt Line' tab.");
	}

	public void navigateBackOnGRNHeader() {
		log.info("Navigating back to Goods Receipt Note Header...");
		if (grnHeaderBreadcrumb.isVisible()) {
			grnHeaderBreadcrumb.click();
		} else if (grnTabHeader.isVisible()) {
			grnTabHeader.click();
		}
		page.waitForTimeout(1000);
		WaitUtils.waitForLoadingIndicatorToDisappear(page);
		log.info("Returned to Goods Receipt Note Header.");
	}

	public void enterProduct(String productName) {
		page.waitForTimeout(1000);
		log.info("Entering Line Product: [{}]", productName);
		lineProductInput.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		lineProductInput.fill(productName);
		lineProductInput.press("Tab");
		page.waitForTimeout(2000);
		verifyFieldFilled(lineProductInput, "Product", productName);
		log.info("Entered Line Product: [{}] successfully.", productName);
	}

	public void enterProduct() {
		enterProduct(DataManager.getData("product"));
		WaitUtils.waitForLoadingIndicatorToDisappear(page);
	}

	public void enterQtyEntered(String qty) {
		page.waitForTimeout(1000);
		log.info("Entering QtyEntered: [{}]", qty);
		qtyEnteredInput.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		qtyEnteredInput.click();
		qtyEnteredInput.fill(qty);
		qtyEnteredInput.press("Tab");
		page.waitForTimeout(2000);
		WaitUtils.waitForLoadingIndicatorToDisappear(page);
		verifyFieldFilled(qtyEnteredInput, "QtyEntered", qty);
		log.info("Entered QtyEntered: [{}] successfully.", qty);
	}

	public void enterQtyEntered() {
		enterQtyEntered(DataManager.getData("qtyEntered"));
	}

	public void selectLocator(String locatorName) {
		page.waitForTimeout(1000);
		log.info("Selecting Locator: [{}]", locatorName);
		locatorInput.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		locatorInput.fill(locatorName);
		locatorInput.press("Tab");
		page.waitForTimeout(1000);
	}

	public void selectUOM(String uomName) {
		page.waitForTimeout(1000);
		log.info("Selecting UOM: [{}]", uomName);
		uomInput.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		uomInput.fill(uomName);
		uomInput.press("Tab");
		page.waitForTimeout(1000);
	}

	public void verifyReceiptLineQty(String expectedQty) {
		movementQtyInput.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		String actualQty = movementQtyInput.inputValue().trim();
		log.info("Verifying Movement Quantity: Actual [{}], Expected [{}]", actualQty, expectedQty);

		boolean isMatch = actualQty.equalsIgnoreCase(expectedQty.trim());
		try {
			double actual = Double.parseDouble(actualQty.replace(",", ""));
			double expected = Double.parseDouble(expectedQty.replace(",", ""));
			isMatch = Double.compare(actual, expected) == 0;
		} catch (Exception ignored) {
		}

		SoftAssert sa = base.BaseTest.getSoftAssert();
		if (sa != null) {
			sa.assertTrue(isMatch, "[GRN Line] Movement Qty [" + actualQty + "] does not match expected [" + expectedQty + "]");
		} else {
			Assert.assertTrue(isMatch, "Movement Qty [" + actualQty + "] does not match expected [" + expectedQty + "]");
		}
	}

	// ==================== Field Verification Helpers ====================

	public boolean verifyFieldFilled(Locator fieldLocator, String fieldName, String expectedValue) {
		String actualValue = fieldLocator.inputValue();
		if (actualValue == null || actualValue.trim().isEmpty()) {
			log.error("❌ Field [{}] is NOT filled! Expected: [{}]", fieldName, expectedValue);
			Assert.fail("Field [" + fieldName + "] was not filled with data. Expected: " + expectedValue);
			return false;
		}

		log.info("✅ Verified [{}] field is filled with data: [{}]", fieldName, actualValue.trim());
		if (expectedValue != null && !expectedValue.trim().isEmpty()) {
			boolean matches = actualValue.trim().equalsIgnoreCase(expectedValue.trim())
					|| actualValue.toLowerCase().contains(expectedValue.toLowerCase())
					|| expectedValue.toLowerCase().contains(actualValue.toLowerCase());
			if (!matches) {
				log.warn("⚠️ Field [{}] value [{}] does not strictly match expected [{}], but data is filled.",
						fieldName, actualValue.trim(), expectedValue);
			}
		}
		return true;
	}

	public boolean isFieldFilled(Locator fieldLocator) {
		String actualValue = fieldLocator.inputValue();
		return actualValue != null && !actualValue.trim().isEmpty();
	}

	public String getFieldValue(Locator fieldLocator) {
		return fieldLocator.inputValue();
	}

	// ==================== Getters for Locators ====================

	public Locator getOrgInput() { return orgInput; }
	public Locator getGateInwardInput() { return gateInwardInput; }
	public Locator getOrderInput() { return orderInput; }
	public Locator getDateOrderedInput() { return dateOrderedInput; }
	public Locator getRmaInput() { return rmaInput; }
	public Locator getDocumentNoInput() { return documentNoInput; }
	public Locator getPoReferenceInput() { return poReferenceInput; }
	public Locator getDescriptionInput() { return descriptionInput; }
	public Locator getDocTypeInput() { return docTypeInput; }
	public Locator getMovementDateInput() { return movementDateInput; }
	public Locator getAccountDateInput() { return accountDateInput; }
	public Locator getBPartnerInput() { return bPartnerInput; }
	public Locator getBPartnerLocationInput() { return bPartnerLocationInput; }
	public Locator getUserInput() { return userInput; }
	public Locator getSalesRepInput() { return salesRepInput; }
	public Locator getWarehouseInput() { return warehouseInput; }
	public Locator getPriorityRuleInput() { return priorityRuleInput; }
	public Locator getDeliveryRuleInput() { return deliveryRuleInput; }
	public Locator getFreightCostRuleInput() { return freightCostRuleInput; }
	public Locator getFreightAmtInput() { return freightAmtInput; }
	public Locator getInvoiceNoInput() { return invoiceNoInput; }
	public Locator getDateInvoicedInput() { return dateInvoicedInput; }
	public Locator getChargeInput() { return chargeInput; }
	public Locator getChargeAmtInput() { return chargeAmtInput; }
	public Locator getReceivedByInput() { return receivedByInput; }
	public Locator getCheckedByInput() { return checkedByInput; }
	public Locator getIsDropShipCheckbox() { return isDropShipCheckbox; }
	public Locator getCreateLinesFromButton() { return createLinesFromButton; }
	public Locator getDeliveryViaRuleInput() { return deliveryViaRuleInput; }
	public Locator getShipperInput() { return shipperInput; }
	public Locator getTrackingNoInput() { return trackingNoInput; }
	public Locator getChallanNoInput() { return challanNoInput; }
	public Locator getChallanDateInput() { return challanDateInput; }
	public Locator getLrNoInput() { return lrNoInput; }
	public Locator getLrDateInput() { return lrDateInput; }
	public Locator getEWayBillNoInput() { return eWayBillNoInput; }
	public Locator getDepartmentInput() { return departmentInput; }
	public Locator getProjectInput() { return projectInput; }
	public Locator getDocStatusInput() { return docStatusInput; }
	public Locator getDocActionButton() { return docActionButton; }
	public Locator getDateReceivedInput() { return dateReceivedInput; }
	public Locator getPostedButton() { return postedButton; }

	public Locator getReceiptLineTab() { return receiptLineTab; }
	public Locator getLineOrgInput() { return lineOrgInput; }
	public Locator getLineNoInput() { return lineNoInput; }
	public Locator getLineProductInput() { return lineProductInput; }
	public Locator getHsnCodeInput() { return hsnCodeInput; }
	public Locator getLocatorInput() { return locatorInput; }
	public Locator getLineDescriptionTextarea() { return lineDescriptionTextarea; }
	public Locator getQtyEnteredInput() { return qtyEnteredInput; }
	public Locator getUomInput() { return uomInput; }
	public Locator getMovementQtyInput() { return movementQtyInput; }
	public Locator getIsQCCheckbox() { return isQCCheckbox; }
	public Locator getLotInput() { return lotInput; }
	public Locator getMfgByInput() { return mfgByInput; }
	public Locator getMfgDateInput() { return mfgDateInput; }
	public Locator getGuaranteeDateInput() { return guaranteeDateInput; }
	public Locator getRetestDateInput() { return retestDateInput; }
	public Locator getCheckListButton() { return checkListButton; }
	public Locator getCreateContainersButton() { return createContainersButton; }
}
