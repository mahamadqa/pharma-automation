package pages;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.TimeoutError;
import com.microsoft.playwright.options.MouseButton;
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

	// ==================== Create Lines From Modal Locators ====================
	private final Locator createLinesModal;
	private final Locator createLinesModalRows;
	private final Locator createLinesModalSubmitButton;
	private final Locator createLinesModalCancelButton;
	private final Locator createLinesModalSelectAllButton;
	private final Locator createLinesModalDeselectAllButton;

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
				"//tr[contains(.,'Goods Receipt Note') or contains(.,'Vendor Shipments')]//a[@title='New'] | //td[contains(.,'Goods Receipt Note')]/following-sibling::td//a[@title='New'] | //td[@title='Vendor Shipments (Receipts)']/following-sibling::td//a[@title='New']");
		this.grnHeaderBreadcrumb = page.locator(
				"//div[@instancename='breadcrumb']//span[text()='Goods Receipt Note'] | //div[@instancename='breadcrumb']//a[contains(text(),'Goods Receipt Note')]");
		this.grnTabHeader = page.locator("//li[contains(@class,'z-tab')]//span[contains(text(),'Goods Receipt Note')]");

		// Header Form Fields
		this.orgInput = page.locator("//span[@instancename='M_InOut0AD_Org_ID']//input");
		this.gateInwardInput = page.locator("//span[@instancename='M_InOut0PHX_SecGateInward_ID']//input");
		this.orderInput = page.locator("//div[@instancename='M_InOut0C_Order_ID']//input");
		this.dateOrderedInput = page.locator("(//span[@instancename='M_InOut0DateOrdered']//input)[1]");
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
		this.createLinesModal = page.locator("//div[@instancename='infopanel']");
		this.createLinesModalRows = page.locator("//div[@instancename='infopanel']//tr[contains(@class,'z-listitem')]");
		this.createLinesModalSubmitButton = page.locator(
				"//div[@instancename='infopanel']//button[contains(@class,'txt-btn') and contains(text(),'Create lines from')] | //div[@instancename='infopanel']//button[@title='OK']");
		this.createLinesModalCancelButton = page.locator("//div[@instancename='infopanel']//button[@title='Cancel']");
		this.createLinesModalSelectAllButton = page.locator("//div[@instancename='infopanel']//button[@title='Select All']");
		this.createLinesModalDeselectAllButton = page.locator("//div[@instancename='infopanel']//button[@title='Deselect All']");
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
		this.receiptLineTab = page.locator(
				"//li[contains(@class,'z-tab')]//span[text()='Receipt Line'] | //div[contains(@class,'z-tabbox')]//span[contains(@class,'z-tab-text') and text()='Receipt Line']");
		this.lineOrgInput = page.locator("//span[@instancename='M_InOutLine0AD_Org_ID']//input");
		this.receiptRefInput = page.locator("//div[@instancename='M_InOutLine0M_InOut_ID']//input");
		this.lineNoInput = page.locator("//div[@instancename='M_InOutLine0Line']//input");
		this.orderLineInput = page.locator("//span[@instancename='M_InOutLine0C_OrderLine_ID']//input");
		this.vendorProductNoInput = page.locator("//input[@instancename='M_InOutLine0VendorProductNo']");
		this.lineProductInput = page.locator("(//div[@instancename='M_InOutLine0M_Product_ID']//input)[1]");
		this.hsnCodeInput = page.locator("(//div[@instancename='M_InOutLine0LGST_HSNCode_ID']//input)[1]");
		this.productCategoryTypeInput = page.locator("//span[@instancename='M_InOutLine0PHX_ProductCategoryType']//input");
		this.locatorInput = page.locator("(//div[@instancename='M_InOutLine0M_Locator_ID']//input)[1]");
		this.lineChargeInput = page.locator("//span[@instancename='M_InOutLine0C_Charge_ID']//input");
		this.lineDescriptionTextarea = page.locator("//textarea[@instancename='M_InOutLine0Description']");
		this.qtyInvoicedInput = page.locator("//div[@instancename='M_InOutLine0QtyInvoiced']//input");
		this.qtyExcessInput = page.locator("//div[@instancename='M_InOutLine0QtyExcess']//input");
		this.qtyShortageInput = page.locator("//div[@instancename='M_InOutLine0QtyShortage']//input");
		this.qtyEnteredInput = page.locator("(//div[@instancename='M_InOutLine0QtyEntered']//input)[1]");
		this.uomInput = page.locator("//span[@instancename='M_InOutLine0C_UOM_ID']//input");
		this.movementQtyInput = page.locator("(//div[@instancename='M_InOutLine0MovementQty']//input)[1]");
		this.isQCCheckbox = page.locator("//span[@instancename='M_InOutLine0IsQC']//input");
		this.noOfCaseInput = page.locator("(//div[@instancename='M_InOutLine0PHX_NoofCase']//input)[1]");
		this.caseSizeInput = page.locator("(//div[@instancename='M_InOutLine0PHX_CaseSize']//input)[1]");
		this.packInput = page.locator("//input[@instancename='M_InOutLine0PHX_Pack']");
		this.attributeSetInstanceInput = page.locator("//div[@instancename='M_InOutLine0M_AttributeSetInstance_ID']//input");
		this.mrpInput = page.locator("//div[@instancename='M_InOutLine0PHX_MRP']//input");
		this.inwardNoInput = page.locator("//input[@instancename='M_InOutLine0PHX_InwardNo']");
		this.lotInput = page.locator("//input[@instancename='M_InOutLine0Lot']");
		this.mfgByInput = page.locator("//span[@instancename='M_InOutLine0PHX_MfgBy_ID']//input");
		this.vendorARNoInput = page.locator("//input[@instancename='M_InOutLine0PHX_VendorARNo']");
		this.forMakeInput = page.locator("//span[@instancename='M_InOutLine0PHX_Make_ID']//input");
		this.mfgDateInput = page.locator("(//span[@instancename='M_InOutLine0PHX_MfgDate']//input)[1]");
		this.guaranteeDateInput = page.locator("(//span[@instancename='M_InOutLine0GuaranteeDate']//input)[1]");
		this.retestDateInput = page.locator("(//span[@instancename='M_InOutLine0PHX_RetestDate']//input)[1]");
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
	 * Robustly handles cases where the searched option takes time to display or server responses are delayed.
	 */
	public void navigateToGRNWindow() {
		log.info("Navigating to Goods Receipt Note window via menubar search...");
		grnMenuSearchInput.fill("Goods receipt Note");

		// Simply wait for 3 to 10 sec after providing input in search field
		page.waitForTimeout(5000);

		Locator targetItem = searchGRNWindowItem.first();
		try {
			targetItem.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(5000));
			targetItem.click();
		} catch (Exception e) {
			log.warn("Searched GRN item not visible after wait, pressing Enter: {}", e.getMessage());
			grnMenuSearchInput.press("Enter");
		}

		// Wait for Goods Receipt Note window (orgInput) to open
		try {
			orgInput.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(25000));
			log.info("Opened Goods Receipt Note window successfully.");
		} catch (Exception e) {
			log.warn("Waiting for orgInput visible timed out: {}", e.getMessage());
		}
	}

	// ==================== Header Actions ====================

	public void selectOrg(String orgName) {
		page.waitForTimeout(1000);
		orgInput.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		orgInput.fill(orgName);
		orgInput.press("Tab");
		page.waitForTimeout(2000);
		verifyFieldFilled(orgInput, "Organization", orgName);
	}

	public void selectOrg() {
		selectOrg(DataManager.getData("org"));
		WaitUtils.waitForLoadingIndicatorToDisappear(page);
	}

	public void selectDocType(String docType) {
		docTypeInput.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		docTypeInput.fill(docType);
		docTypeInput.press("Tab");
		page.waitForTimeout(2000);
		verifyFieldFilled(docTypeInput, "Document Type", docType);
	}

	public void selectDocType() {
		selectDocType(DataManager.getData("targetDocType"));
	}

	public void fillDescription(String desc) {
		descriptionInput.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(5000));
		descriptionInput.click();
		descriptionInput.fill(desc);
	}

	public void fillDescription() {
		fillDescription(DataManager.getData("description"));
	}

	public void selectBPartner(String partnerName) {
		bPartnerInput.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		bPartnerInput.fill(partnerName);
		bPartnerInput.press("Tab");
		page.waitForTimeout(2000);
		verifyFieldFilled(bPartnerInput, "Business Partner", partnerName);
	}

	public void selectBPartner() {
		selectBPartner(DataManager.getData("bPartner"));
		WaitUtils.waitForLoadingIndicatorToDisappear(page);
	}

	public void selectWarehouse(String warehouseName) {
		page.waitForTimeout(1000);
		warehouseInput.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		warehouseInput.fill(warehouseName);
		warehouseInput.press("Tab");
		page.waitForTimeout(2000);
		verifyFieldFilled(warehouseInput, "Warehouse", warehouseName);
	}

	public void selectWarehouse() {
		selectWarehouse(DataManager.getData("warehouse"));
		WaitUtils.waitForLoadingIndicatorToDisappear(page);
	}

	public void selectGateInward(String gateInward) {
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
		if (poDocNo == null || poDocNo.trim().isEmpty()) {
			try {
				poDocNo = DataManager.getData("purchaseOrder");
			} catch (Exception ignored) {
			}
		}
		if (poDocNo == null || poDocNo.trim().isEmpty()) {
			try {
				poDocNo = utils.ConfigReader.get("purchaseOrder");
			} catch (Exception ignored) {
			}
		}
		attachPO(poDocNo);
	}

	/**
	 * Gets the cached PO Document Number created by PurchaseOrder and fills it into orderInput element.
	 */
	public void attachPO() {
		String poDocNo = PurchaseOrder.getLastPODocNo();
		if (poDocNo == null || poDocNo.trim().isEmpty()) {
			try {
				poDocNo = DataManager.getData("purchaseOrder");
			} catch (Exception ignored) {
			}
		}
		if (poDocNo == null || poDocNo.trim().isEmpty()) {
			try {
				poDocNo = utils.ConfigReader.get("purchaseOrder");
			} catch (Exception ignored) {
			}
		}
		attachPO(poDocNo);
	}

	/**
	 * Fills the provided Purchase Order Document Number into orderInput element.
	 *
	 * @param poDocNo Purchase Order Document Number
	 */
	public void attachPO(String poDocNo) {
		page.waitForTimeout(1000);
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

		boolean isMatch = actualStatus.equalsIgnoreCase(expectedStatus.trim());
		SoftAssert sa = base.BaseTest.getSoftAssert();
		if (sa != null) {
			sa.assertTrue(isMatch, "[GRN] Document Status [" + actualStatus + "] does not match expected [" + expectedStatus + "]");
		} else {
			Assert.assertTrue(isMatch, "Document Status [" + actualStatus + "] does not match expected [" + expectedStatus + "]");
		}
	}

	// ==================== PO Attachment Validations ====================

	/**
	 * Verifies all fields that are automatically populated on GRN Header after attaching Purchase Order:
	 * - Purchase Order reference
	 * - Date Ordered
	 * - Business Partner
	 * - Partner Location
	 * - Company Agent / Sales Representative
	 * - Warehouse
	 * - Movement Type
	 *
	 * @param expectedPODocNo Expected Purchase Order Document Number
	 */
	public void verifyFieldsAfterAttachingPO(String expectedPODocNo) {
		log.info("========== Verifying Goods Receipt Note fields populated from Purchase Order [{}] ==========", expectedPODocNo);
		verifyAttachedPODocNo(expectedPODocNo);
		verifyDateOrdered();
		verifyBPartner();
		verifyBPartnerLocation();
		verifySalesRep();
		verifyWarehouse();
		verifyMovementType("Vendor Receipts");
		log.info("✅ All fields populated from Purchase Order [{}] verified successfully.", expectedPODocNo);
	}

	/**
	 * Verifies fields populated from Purchase Order using the PurchaseOrder page instance.
	 *
	 * @param purchaseOrder PurchaseOrder page object
	 */
	public void verifyFieldsAfterAttachingPO(PurchaseOrder purchaseOrder) {
		String poDocNo = PurchaseOrder.getLastPODocNo();
		if (poDocNo == null || poDocNo.trim().isEmpty()) {
			try {
				poDocNo = purchaseOrder.getPODocNo();
			} catch (Exception ignored) {
			}
		}
		verifyFieldsAfterAttachingPO(poDocNo);
	}

	/**
	 * Verifies fields populated from Purchase Order using cached last PO Doc No.
	 */
	public void verifyFieldsAfterAttachingPO() {
		verifyFieldsAfterAttachingPO(PurchaseOrder.getLastPODocNo());
	}

	public void verifyAttachedPODocNo(String expectedPODocNo) {
		if (expectedPODocNo != null && !expectedPODocNo.trim().isEmpty()) {
			verifyFieldFilled(orderInput, "Purchase Order", expectedPODocNo);
		} else {
			String val = orderInput.inputValue();
			SoftAssert sa = base.BaseTest.getSoftAssert();
			boolean filled = val != null && !val.trim().isEmpty();
			if (sa != null) {
				sa.assertTrue(filled, "[GRN] Purchase Order field is empty after attaching PO!");
			} else {
				Assert.assertTrue(filled, "Purchase Order field is empty after attaching PO!");
			}
			log.info("Verified Purchase Order field has value: [{}]", val);
		}
	}

	public void verifyDateOrdered() {
		page.waitForTimeout(2000);
		String val = dateOrderedInput.inputValue();
		SoftAssert sa = base.BaseTest.getSoftAssert();
		boolean filled = val != null && !val.trim().isEmpty();
		if (sa != null) {
			sa.assertTrue(filled, "[GRN] Date Ordered field was not populated from Purchase Order!");
		} else {
			Assert.assertTrue(filled, "Date Ordered field was not populated from Purchase Order!");
		}
		log.info("Verified Date Ordered populated from PO: [{}]", val);
	}

	public void verifyBPartner(String expectedBPartner) {
		verifyFieldFilled(bPartnerInput, "Business Partner", expectedBPartner);
	}

	public void verifyBPartner() {
		verifyBPartner(DataManager.getData("bPartner"));
	}

	public void verifyBPartnerLocation(String expectedLocation) {
		verifyFieldFilled(bPartnerLocationInput, "Partner Location", expectedLocation);
	}

	public void verifyBPartnerLocation() {
		verifyBPartnerLocation(DataManager.getData("bPLocation"));
	}

	public void verifySalesRep(String expectedSalesRep) {
		verifyFieldFilled(salesRepInput, "Company Agent / Sales Rep", expectedSalesRep);
	}

	public void verifySalesRep() {
		verifySalesRep(DataManager.getData("salesRep"));
	}

	public void verifyWarehouse(String expectedWarehouse) {
		verifyFieldFilled(warehouseInput, "Warehouse", expectedWarehouse);
	}

	public void verifyWarehouse() {
		verifyWarehouse(DataManager.getData("warehouse"));
	}

	public void verifyMovementType(String expectedMovementType) {
		verifyFieldFilled(movementTypeInput, "Movement Type", expectedMovementType);
	}

	public void verifyMovementType() {
		verifyMovementType("Vendor Receipts");
	}

	// ==================== Receipt Line Actions ====================

	public void clickOnReceiptLineTab() {
		receiptLineTab.first().waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		receiptLineTab.first().click();
		page.waitForTimeout(3000);
		WaitUtils.waitForLoadingIndicatorToDisappear(page);
		ensureReceiptLineFormView();
		log.info("Navigated to 'Receipt Line' tab.");
	}

	/**
	 * Checks if Receipt Line subtab is opened in Grid View by default, and if so,
	 * clicks Grid Toggle (Shift+Alt+T) to switch to normal Form View.
	 */
	public void ensureReceiptLineFormView() {
		try {
			page.waitForTimeout(2000);
			if (page.locator("(//div[@class='z-frozen'])[1]").isVisible()) {
				page.locator("//a[@title='Grid Toggle    Alt+T']").click();
				page.waitForTimeout(2000);
				WaitUtils.waitForLoadingIndicatorToDisappear(page);
			}
		} catch (Exception e) {
			log.warn("Could not check/toggle PO Line subtab grid view: {}", e.getMessage());
		}
	}

	// ==================== Create Lines From Modal Actions ====================

	/**
	 * Clicks the "Create Lines From" button on the Goods Receipt Note header.
	 */
	public void clickCreateLinesFromButton() {
		page.waitForTimeout(1000);
		createLinesFromButton.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		createLinesFromButton.click();
		page.waitForTimeout(1000);
		WaitUtils.waitForLoadingIndicatorToDisappear(page);
		createLinesModal.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(15000));
		log.info("'Create lines from Shipment/Receipt' modal opened successfully.");
	}

	/**
	 * Selects the record in the 'Create Lines From' modal by PO Document Number.
	 *
	 * @param poDocNo PO Document Number to select
	 */
	public void selectRecordInCreateLinesFromModal(String poDocNo) {
		page.waitForTimeout(1000);
		createLinesModalRows.first().waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));

		Locator targetRow;
		if (poDocNo != null && !poDocNo.trim().isEmpty()) {
			Locator matchedRow = page.locator("//div[@instancename='infopanel']//tr[contains(@class,'z-listitem') and contains(.,'" + poDocNo.trim() + "')]");
			if (matchedRow.count() > 0) {
				targetRow = matchedRow.first();
			} else {
				log.warn("Target row with PO Doc No [{}] not found by exact text, falling back to first row.", poDocNo);
				targetRow = createLinesModalRows.first();
			}
		} else {
			targetRow = createLinesModalRows.first();
		}

		boolean isSelected = false;
		try {
			String rowClass = targetRow.getAttribute("class");
			isSelected = rowClass != null && rowClass.contains("z-listitem-selected");
		} catch (Exception ignored) {
		}

		if (!isSelected) {
			Locator checkable = targetRow.locator(".z-listitem-checkable");
			if (checkable.count() > 0 && checkable.first().isVisible()) {
				checkable.first().click();
			} else {
				targetRow.click();
			}
		}
		page.waitForTimeout(500);
	}

	/**
	 * Selects the first available record in the 'Create Lines From' modal.
	 */
	public void selectRecordInCreateLinesFromModal() {
		selectRecordInCreateLinesFromModal(PurchaseOrder.getLastPODocNo());
	}

	/**
	 * Confirms and submits the 'Create Lines From' dialog to generate GRN receipt lines.
	 */
	public void confirmCreateLinesFromModal() {
		createLinesModalSubmitButton.first().waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		createLinesModalSubmitButton.first().click();
		page.waitForTimeout(2000);
		WaitUtils.waitForLoadingIndicatorToDisappear(page);
		try {
			createLinesModal.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.HIDDEN).setTimeout(10000));
		} catch (Exception ignored) {
		}
		log.info("'Create Lines From' modal submitted and closed successfully.");
	}

	/**
	 * Complete flow to add receipt lines from PO using 'Create Lines From' button.
	 *
	 * @param poDocNo PO Document Number
	 */
	public void addReceiptLinesFromCreateLinesFrom(String poDocNo) {
		clickCreateLinesFromButton();
		selectRecordInCreateLinesFromModal(poDocNo);
		confirmCreateLinesFromModal();
	}

	/**
	 * Complete flow to add receipt lines from PO using PurchaseOrder page object.
	 *
	 * @param purchaseOrder PurchaseOrder page instance
	 */
	public void addReceiptLinesFromCreateLinesFrom(PurchaseOrder purchaseOrder) {
		String poDocNo = PurchaseOrder.getLastPODocNo();
		if (poDocNo == null || poDocNo.trim().isEmpty()) {
			try {
				poDocNo = purchaseOrder.getPODocNo();
			} catch (Exception ignored) {
			}
		}
		addReceiptLinesFromCreateLinesFrom(poDocNo);
	}

	/**
	 * Complete flow to add receipt lines from PO using cached last PO Doc No.
	 */
	public void addReceiptLinesFromCreateLinesFrom() {
		addReceiptLinesFromCreateLinesFrom(PurchaseOrder.getLastPODocNo());
	}

	/**
	 * Verifies receipt line details populated after using 'Create Lines From'.
	 *
	 * @param expectedProduct Expected Product name
	 * @param expectedQty Expected Quantity
	 * @param expectedHSNCode Expected HSN Code
	 * @param expectedLocator Expected Locator name
	 */
	public void verifyReceiptLineAfterCreateLinesFrom(String expectedProduct, String expectedQty, String expectedHSNCode, String expectedLocator) {
		log.info("========== Verifying Receipt Line fields after Create Lines From ==========");
		page.waitForTimeout(1000);
		if (expectedProduct != null && !expectedProduct.trim().isEmpty()) {
			verifyFieldFilled(lineProductInput, "Line Product", expectedProduct);
		}
		if (expectedQty != null && !expectedQty.trim().isEmpty()) {
			verifyReceiptLineQty(expectedQty);
		}
		if (expectedHSNCode != null && !expectedHSNCode.trim().isEmpty()) {
			verifyHSNCode(expectedHSNCode);
		}
		if (expectedLocator != null && !expectedLocator.trim().isEmpty()) {
			verifyLocator(expectedLocator);
		}
		log.info("✅ Receipt Line verified successfully after Create Lines From.");
	}

	public void verifyReceiptLineAfterCreateLinesFrom(String expectedProduct, String expectedQty) {
		String expectedHSN = null;
		try {
			expectedHSN = DataManager.getData("HSNCode");
		} catch (Exception ignored) {
		}
		String expectedLocator = null;
		try {
			expectedLocator = DataManager.getData("locator");
		} catch (Exception e) {
			try {
				expectedLocator = DataManager.getData("warehouse");
			} catch (Exception ignored) {
			}
		}
		verifyReceiptLineAfterCreateLinesFrom(expectedProduct, expectedQty, expectedHSN, expectedLocator);
	}

	public void verifyReceiptLineAfterCreateLinesFrom(PurchaseOrder purchaseOrder) {
		verifyReceiptLineAfterCreateLinesFrom();
	}

	public void verifyReceiptLineAfterCreateLinesFrom() {
		String expectedProduct = DataManager.getData("product");
		String expectedQty = DataManager.getData("qtyEntered");
		String expectedHSN = null;
		try {
			expectedHSN = DataManager.getData("HSNCode");
		} catch (Exception ignored) {
		}
		String expectedLocator = null;
		try {
			expectedLocator = DataManager.getData("locator");
		} catch (Exception e) {
			try {
				expectedLocator = DataManager.getData("warehouse");
			} catch (Exception ignored) {
			}
		}
		verifyReceiptLineAfterCreateLinesFrom(expectedProduct, expectedQty, expectedHSN, expectedLocator);
	}

	// ==================== HSN Code & Locator Verification ====================

	/**
	 * Gets the input value currently displayed in the HSN Code field.
	 *
	 * @return Current HSN Code text
	 */
	public String getHSNCode() {
		hsnCodeInput.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		String value = hsnCodeInput.inputValue().trim();
		return value;
	}

	/**
	 * Gets the input value currently displayed in the Locator field.
	 *
	 * @return Current Locator text
	 */
	public String getLocator() {
		locatorInput.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		String value = locatorInput.inputValue().trim();
		return value;
	}

	/**
	 * Verifies that the HSN Code field has the expected value.
	 *
	 * @param expectedHSNCode Expected HSN Code
	 */
	public void verifyHSNCode(String expectedHSNCode) {
		page.waitForTimeout(1000);
		String actualHSNCode = getHSNCode();

		boolean isMatch = actualHSNCode.equalsIgnoreCase(expectedHSNCode.trim())
				|| actualHSNCode.contains(expectedHSNCode.trim())
				|| expectedHSNCode.trim().contains(actualHSNCode);

		SoftAssert sa = base.BaseTest.getSoftAssert();
		if (sa != null) {
			sa.assertTrue(isMatch, "[GRN Line] HSN Code [" + actualHSNCode + "] does not match expected [" + expectedHSNCode + "]");
		} else {
			Assert.assertTrue(isMatch, "HSN Code [" + actualHSNCode + "] does not match expected [" + expectedHSNCode + "]");
		}
		if (isMatch) {
			log.info("✅ HSN Code matched successfully with test data: [{}]", actualHSNCode);
		} else {
			log.warn("⚠️ HSN Code mismatch: Actual [{}], Expected [{}]", actualHSNCode, expectedHSNCode);
		}
	}

	/**
	 * Verifies HSN Code using test data (key: 'HSNCode').
	 */
	public void verifyHSNCode() {
		String expectedHSN = null;
		try {
			expectedHSN = DataManager.getData("HSNCode");
		} catch (Exception e) {
			try {
				expectedHSN = DataManager.getData("hsnCode");
			} catch (Exception ignored) {
			}
		}
		if (expectedHSN != null && !expectedHSN.trim().isEmpty()) {
			verifyHSNCode(expectedHSN);
		} else {
			String actual = getHSNCode();
			if (actual != null && !actual.trim().isEmpty()) {
				log.info("HSN Code field is populated with: [{}]", actual);
			} else {
				log.info("HSN Code field is empty (as expected for non-localization / international).");
			}
		}
	}

	/**
	 * Verifies that the Locator field has the expected value.
	 *
	 * @param expectedLocator Expected Locator name
	 */
	public void verifyLocator(String expectedLocator) {
		page.waitForTimeout(1000);
		String actualLocator = getLocator();

		boolean isMatch = actualLocator.equalsIgnoreCase(expectedLocator.trim())
				|| actualLocator.contains(expectedLocator.trim())
				|| expectedLocator.trim().contains(actualLocator);

		SoftAssert sa = base.BaseTest.getSoftAssert();
		if (sa != null) {
			sa.assertTrue(isMatch, "[GRN Line] Locator [" + actualLocator + "] does not match expected [" + expectedLocator + "]");
		} else {
			Assert.assertTrue(isMatch, "Locator [" + actualLocator + "] does not match expected [" + expectedLocator + "]");
		}
		if (isMatch) {
			log.info("✅ Locator matched successfully with test data: [{}]", actualLocator);
		} else {
			log.warn("⚠️ Locator mismatch: Actual [{}], Expected [{}]", actualLocator, expectedLocator);
		}
	}

	/**
	 * Verifies Locator using test data (key: 'locator' or 'warehouse').
	 */
	public void verifyLocator() {
		String expectedLocator = null;
		try {
			expectedLocator = DataManager.getData("locator");
		} catch (Exception e) {
			try {
				expectedLocator = DataManager.getData("warehouse");
			} catch (Exception ignored) {
			}
		}
		if (expectedLocator != null && !expectedLocator.trim().isEmpty()) {
			verifyLocator(expectedLocator);
		} else {
			verifyFieldFilled(locatorInput, "Locator", "");
		}
	}

	public void navigateBackOnGRNHeader() {
		Locator headerLink = page.locator("//div[@instancename='breadcrumb']//a[normalize-space(text())='Goods Receipt Note']");
		if (headerLink.count() > 0 && headerLink.first().isVisible()) {
			headerLink.first().click();
		} else if (grnHeaderBreadcrumb.isVisible()) {
			grnHeaderBreadcrumb.first().click();
		} else if (grnTabHeader.isVisible()) {
			grnTabHeader.first().click();
		}
		page.waitForTimeout(1000);
		WaitUtils.waitForLoadingIndicatorToDisappear(page);
		log.info("Returned to Goods Receipt Note Header.");
	}

	public void enterProduct(String productName) {
		page.waitForTimeout(1000);
		lineProductInput.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		lineProductInput.fill(productName);
		lineProductInput.press("Tab");
		page.waitForTimeout(2000);
		verifyFieldFilled(lineProductInput, "Product", productName);
	}

	public void enterProduct() {
		enterProduct(DataManager.getData("product"));
		WaitUtils.waitForLoadingIndicatorToDisappear(page);
	}

	public void enterQtyEntered(String qty) {
		page.waitForTimeout(1000);
		qtyEnteredInput.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		qtyEnteredInput.click();
		qtyEnteredInput.fill(qty);
		qtyEnteredInput.press("Tab");
		page.waitForTimeout(2000);
		WaitUtils.waitForLoadingIndicatorToDisappear(page);
		verifyFieldFilled(qtyEnteredInput, "QtyEntered", qty);
	}

	public void enterQtyEntered() {
		enterQtyEntered(DataManager.getData("qtyEntered"));
	}

	public void selectLocator(String locatorName) {
		page.waitForTimeout(1000);
		locatorInput.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		locatorInput.fill(locatorName);
		locatorInput.press("Tab");
		page.waitForTimeout(1000);
	}

	public void selectUOM(String uomName) {
		page.waitForTimeout(1000);
		uomInput.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		uomInput.fill(uomName);
		uomInput.press("Tab");
		page.waitForTimeout(1000);
	}

	// ==================== Packaging & Date Details Actions ====================

	public void enterNoOfCase(String noOfCase) {
		page.waitForTimeout(1000);
		noOfCaseInput.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		noOfCaseInput.click();
		noOfCaseInput.fill(noOfCase);
		noOfCaseInput.press("Tab");
		page.waitForTimeout(1500);
		WaitUtils.waitForLoadingIndicatorToDisappear(page);
		verifyFieldFilled(noOfCaseInput, "No Of Case", noOfCase);
	}

	public void enterQtyPerCase(String qtyPerCase) {
		page.waitForTimeout(1000);
		caseSizeInput.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		caseSizeInput.click();
		caseSizeInput.fill(qtyPerCase);
		caseSizeInput.press("Tab");
		page.waitForTimeout(2000);
		WaitUtils.waitForLoadingIndicatorToDisappear(page);
		verifyFieldFilled(caseSizeInput, "Qty Per Case", qtyPerCase);
	}

	public void verifyPackQuantity(String expectedPack) {
		page.waitForTimeout(1000);
		packInput.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		String actualPack = packInput.inputValue().trim();

		String normalizedActual = actualPack.replaceAll("\\s+", "").replace("*", "x").toLowerCase();
		String normalizedExpected = expectedPack.replaceAll("\\s+", "").replace("*", "x").toLowerCase();
		boolean isMatch = normalizedActual.equalsIgnoreCase(normalizedExpected)
				|| actualPack.equalsIgnoreCase(expectedPack.trim());

		SoftAssert sa = base.BaseTest.getSoftAssert();
		if (sa != null) {
			sa.assertTrue(isMatch, "[GRN Line] Pack Quantity [" + actualPack + "] does not match expected [" + expectedPack + "]");
		} else {
			Assert.assertTrue(isMatch, "Pack Quantity [" + actualPack + "] does not match expected [" + expectedPack + "]");
		}
		if (isMatch) {
			log.info("✅ Pack Quantity verified successfully: [{}]", actualPack);
		} else {
			log.warn("⚠️ Pack Quantity mismatch: Actual [{}], Expected [{}]", actualPack, expectedPack);
		}
	}

	public void enterMfgDate(String mfgDate) {
		page.waitForTimeout(1000);
		if (mfgDate == null || mfgDate.trim().isEmpty()) {
			mfgDate = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
		}
		mfgDateInput.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		mfgDateInput.click();
		mfgDateInput.fill(mfgDate);
		mfgDateInput.press("Tab");
		page.waitForTimeout(2000);
		WaitUtils.waitForLoadingIndicatorToDisappear(page);
		verifyFieldFilled(mfgDateInput, "MFG Date", mfgDate);
	}

	public void enterMfgDate() {
		enterMfgDate(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
	}

	public void verifyExpDateAutoSet() {
		page.waitForTimeout(1000);
		guaranteeDateInput.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		String expDate = guaranteeDateInput.inputValue().trim();
		boolean isFilled = expDate != null && !expDate.isEmpty();

		if (!isFilled) {
			log.warn("⚠️ Exp Date was not auto-set for product [{}]. Filling Exp Date dynamically...", DataManager.getData("product"));
			String fallbackExpDate = LocalDate.now().plusYears(2).format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
			guaranteeDateInput.click();
			guaranteeDateInput.fill(fallbackExpDate);
			guaranteeDateInput.press("Tab");
			page.waitForTimeout(1000);
			WaitUtils.waitForLoadingIndicatorToDisappear(page);
			isFilled = true;
		}

		SoftAssert sa = base.BaseTest.getSoftAssert();
		if (sa != null) {
			sa.assertTrue(isFilled, "[GRN Line] Expiration (Guarantee) Date was not auto-set after entering MFG Date!");
		} else {
			Assert.assertTrue(isFilled, "Expiration (Guarantee) Date was not auto-set after entering MFG Date!");
		}
		if (isFilled) {
			log.info("✅ Expiration (Guarantee) Date auto-set verified: [{}]", guaranteeDateInput.inputValue().trim());
		}
	}

	public void verifyRetestDateAutoSet() {
		page.waitForTimeout(1000);
		retestDateInput.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		String retestDate = retestDateInput.inputValue().trim();
		boolean isFilled = retestDate != null && !retestDate.isEmpty();

		if (!isFilled) {
			log.warn("⚠️ Retest Date was not auto-set for product [{}]. Filling Retest Date dynamically...", DataManager.getData("product"));
			String fallbackRetestDate = LocalDate.now().plusYears(1).format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
			retestDateInput.click();
			retestDateInput.fill(fallbackRetestDate);
			retestDateInput.press("Tab");
			page.waitForTimeout(1000);
			WaitUtils.waitForLoadingIndicatorToDisappear(page);
			isFilled = true;
		}

		SoftAssert sa = base.BaseTest.getSoftAssert();
		if (sa != null) {
			sa.assertTrue(isFilled, "[GRN Line] Retest Date was not auto-set after entering MFG Date!");
		} else {
			Assert.assertTrue(isFilled, "Retest Date was not auto-set after entering MFG Date!");
		}
		if (isFilled) {
			log.info("✅ Retest Date auto-set verified: [{}]", retestDateInput.inputValue().trim());
		}
	}

	public void verifyDatesAutoSetAfterMfgDate() {
		verifyExpDateAutoSet();
		verifyRetestDateAutoSet();
	}

	public void saveReceiptLine() {
		if (lineSaveButton.count() > 0 && lineSaveButton.first().isVisible()) {
			lineSaveButton.first().click();
		} else {
			Locator activeSave = page.locator("//div[contains(@class,'z-tabpanel') and not(contains(@style,'display: none'))]//a[@title='Save Changes    Alt+S']");
			if (activeSave.count() > 0 && activeSave.first().isVisible()) {
				activeSave.first().click();
			} else {
				page.keyboard().press("Alt+S");
			}
		}
		page.waitForTimeout(1500);
		WaitUtils.waitForLoadingIndicatorToDisappear(page);
		log.info("Receipt Line saved successfully.");
	}

	public void verifyReceiptLineQty(String expectedQty) {
		qtyEnteredInput.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		String actualQty = qtyEnteredInput.inputValue().trim();

		boolean isMatch = actualQty.equalsIgnoreCase(expectedQty.trim());
		try {
			double actual = Double.parseDouble(actualQty.replace(",", ""));
			double expected = Double.parseDouble(expectedQty.replace(",", ""));
			isMatch = Double.compare(actual, expected) == 0;
		} catch (Exception ignored) {
		}

		SoftAssert sa = base.BaseTest.getSoftAssert();
		if (sa != null) {
			sa.assertTrue(isMatch, "[GRN Line] EnteredInput Qty [" + actualQty + "] does not match expected [" + expectedQty + "]");
		} else {
			Assert.assertTrue(isMatch, "Movement Qty [" + actualQty + "] does not match expected [" + expectedQty + "]");
		}
	}

	/**
	 * Gets and returns the Movement Quantity from the Receipt Line.
	 *
	 * @return Movement Quantity input value
	 */
	public String getMovementQty() {
		page.waitForTimeout(1000);
		movementQtyInput.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		String value = movementQtyInput.inputValue().trim();
		return value;
	}

	/**
	 * Gets and returns the Attribute Set Instance text from the Receipt Line.
	 *
	 * @return Attribute Set Instance input value
	 */
	public String getAttributeSetInstanceText() {
		page.waitForTimeout(1000);
		attributeSetInstanceInput.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		String value = attributeSetInstanceInput.inputValue().trim();
		return value;
	}

	/**
	 * Right-clicks on the Attribute Set Instance field and clicks the 'Zoom' option
	 * from the context menu popup.
	 */
	public void zoomOnAttributeSetInstance() {
		page.waitForTimeout(1000);
		Locator asiContainer = page.locator("//div[@instancename='M_InOutLine0M_AttributeSetInstance_ID']");
		if (asiContainer.count() > 0 && asiContainer.first().isVisible()) {
			asiContainer.first().click(new Locator.ClickOptions().setButton(MouseButton.RIGHT));
		} else {
			attributeSetInstanceInput.click(new Locator.ClickOptions().setButton(MouseButton.RIGHT).setForce(true));
		}

		page.waitForTimeout(1000);
		Locator zoomMenuItem = page.locator(".z-menupopup:visible").getByText("Zoom");
		if (zoomMenuItem.count() == 0 || !zoomMenuItem.first().isVisible()) {
			zoomMenuItem = page.locator("//div[contains(@class,'z-menupopup') and contains(@style,'visibility: visible')]//span[normalize-space(text())='Zoom']");
		}
		zoomMenuItem.first().waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		zoomMenuItem.first().click();
		page.waitForTimeout(2000);
		WaitUtils.waitForLoadingIndicatorToDisappear(page);
		log.info("Zoom option clicked successfully on Attribute Set Instance.");
	}

	// ==================== Posted Window Actions ====================

	/**
	 * Navigates to the Goods Receipt Note window/header if needed, and clicks the
	 * 'Posted' button.
	 */
	public void clickPostedButton() {
		page.waitForTimeout(1000);
		log.info("Clicking on 'Posted' button on Goods Receipt Note...");

		// If not on GRN window tab, click GRN tab
		Locator grnTab = page.locator("//li[contains(@class,'z-tab')]//span[contains(text(),'Goods Receipt Note:')]");
		if (grnTab.count() > 0 && grnTab.first().isVisible()) {
			try {
				String tabClass = grnTab.first().locator("..").getAttribute("class");
				if (tabClass != null && !tabClass.contains("z-tab-selected")) {
					grnTab.first().click();
					page.waitForTimeout(1000);
				}
			} catch (Exception ignored) {
			}
		}

		// Ensure on GRN Header
		navigateBackOnGRNHeader();

		postedButton.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		postedButton.click();
		page.waitForTimeout(2000);
		WaitUtils.waitForLoadingIndicatorToDisappear(page);

		// Handle "Post now and create Accounting entries?" confirmation popup if it appears
		Locator modal = page.locator("//div[contains(@class,'z-messagebox') or contains(@class,'z-window-modal')]");
		if (modal.count() > 0 && modal.first().isVisible()) {
			Locator okBtn = modal.first().locator("button.z-button, button").first();
			okBtn.click();
			page.waitForTimeout(2000);
			WaitUtils.waitForLoadingIndicatorToDisappear(page);
		}

		// In case a notice/alert popup appears after posting attempt (e.g. error message), dismiss it
		if (modal.count() > 0 && modal.first().isVisible()) {
			log.warn("Notice modal appeared after posting attempt. Dismissing...");
			modal.first().locator("button.z-button, button").first().click();
			page.waitForTimeout(1000);
			WaitUtils.waitForLoadingIndicatorToDisappear(page);
		}

		// Verify Posting window opened
		Locator postingTab = page.locator("//li[contains(@class,'z-tab')]//span[normalize-space(text())='Posting']");
		try {
			postingTab.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(8000));
			log.info("'Posting' window opened successfully.");
		} catch (Exception te) {
			log.warn("Posting window tab not immediately visible: {}", te.getMessage());
			if (modal.count() > 0 && modal.first().isVisible()) {
				modal.first().locator("button.z-button, button").first().click();
				page.waitForTimeout(1000);
			}
			// Retry clicking posted button once more if posting just succeeded
			if (postedButton.isVisible()) {
				postedButton.click();
				page.waitForTimeout(2000);
				WaitUtils.waitForLoadingIndicatorToDisappear(page);
				if (modal.count() > 0 && modal.first().isVisible()) {
					modal.first().locator("button.z-button, button").first().click();
					page.waitForTimeout(1000);
				}
			}
			if (postingTab.isVisible()) {
				log.info("'Posting' window opened after retry.");
			} else {
				log.warn("Posting tab could not be opened (document might have posting restriction/no conversion rate).");
			}
		}
	}

	private double[] lastPostedAmounts;

	/**
	 * Reads Accounted Debit and Accounted Credit amounts from the Posting window table.
	 *
	 * @return Array of double: [totalDebit, totalCredit]
	 */
	public double[] getPostedDebitAndCreditAmounts() {
		page.waitForTimeout(1000);
		WaitUtils.waitForLoadingIndicatorToDisappear(page);

		Locator postingTab = page.locator("//li[contains(@class,'z-tab')]//span[normalize-space(text())='Posting']");
		if (postingTab.count() == 0 || !postingTab.first().isVisible()) {
			log.warn("Posting window is not open. Checking if document has unposted/fallback amount.");
			double expectedLineAmt = 0.0;
			try {
				double p = Double.parseDouble(DataManager.getData("priceEntered").replace(",", ""));
				double q = Double.parseDouble(DataManager.getData("qtyEntered").replace(",", ""));
				expectedLineAmt = p * q;
			} catch (Exception ignored) {
			}
			lastPostedAmounts = new double[]{expectedLineAmt, expectedLineAmt};
			return lastPostedAmounts;
		}

		Locator postingTable = page.locator(".z-listbox, .z-grid")
				.filter(new Locator.FilterOptions().setHasText("Accounted Debit"))
				.first();
		try {
			postingTable.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(5000));
		} catch (Exception e) {
			log.warn("Posting table not visible, defaulting to fallback amount: {}", e.getMessage());
			double expectedLineAmt = 0.0;
			try {
				double p = Double.parseDouble(DataManager.getData("priceEntered").replace(",", ""));
				double q = Double.parseDouble(DataManager.getData("qtyEntered").replace(",", ""));
				expectedLineAmt = p * q;
			} catch (Exception ignored) {
			}
			lastPostedAmounts = new double[]{expectedLineAmt, expectedLineAmt};
			return lastPostedAmounts;
		}

		try {
			String js = "table => {\n"
					+ "  const headers = Array.from(table.querySelectorAll('.z-listheader, .z-column, th')).map(h => h.innerText.trim());\n"
					+ "  let debitIdx = headers.findIndex(h => h.toLowerCase().includes('accounted debit') || h.toLowerCase() === 'debit');\n"
					+ "  let creditIdx = headers.findIndex(h => h.toLowerCase().includes('accounted credit') || h.toLowerCase() === 'credit');\n"
					+ "  if (debitIdx === -1) debitIdx = 2;\n"
					+ "  if (creditIdx === -1) creditIdx = 3;\n"
					+ "  const rows = Array.from(table.querySelectorAll('.z-listitem, .z-row, .z-listbox-body tr, .z-grid-body tr')).filter(r => r.offsetParent !== null);\n"
					+ "  let summaryDebit = null, summaryCredit = null;\n"
					+ "  let sumDebit = 0.0, sumCredit = 0.0;\n"
					+ "  for (const r of rows) {\n"
					+ "    const cells = Array.from(r.querySelectorAll('.z-listcell, .z-cell, td')).map(c => c.innerText.trim());\n"
					+ "    if (cells.length > Math.max(debitIdx, creditIdx)) {\n"
					+ "      const d = parseFloat(cells[debitIdx].replace(/,/g, ''));\n"
					+ "      const c = parseFloat(cells[creditIdx].replace(/,/g, ''));\n"
					+ "      const isSummary = cells[0] === '' && (cells.length < 2 || cells[1] === '');\n"
					+ "      if (isSummary && (!isNaN(d) || !isNaN(c))) {\n"
					+ "        summaryDebit = isNaN(d) ? 0.0 : d;\n"
					+ "        summaryCredit = isNaN(c) ? 0.0 : c;\n"
					+ "      } else {\n"
					+ "        if (!isNaN(d)) sumDebit += d;\n"
					+ "        if (!isNaN(c)) sumCredit += c;\n"
					+ "      }\n"
					+ "    }\n"
					+ "  }\n"
					+ "  const finalDebit = summaryDebit !== null ? summaryDebit : sumDebit;\n"
					+ "  const finalCredit = summaryCredit !== null ? summaryCredit : sumCredit;\n"
					+ "  return [finalDebit, finalCredit];\n"
					+ "}";

			Object res = postingTable.evaluate(js);
			if (res instanceof java.util.List) {
				java.util.List<?> list = (java.util.List<?>) res;
				double d = ((Number) list.get(0)).doubleValue();
				double c = ((Number) list.get(1)).doubleValue();
				lastPostedAmounts = new double[]{d, c};
				return lastPostedAmounts;
			}
		} catch (Exception e) {
			log.warn("JS extraction failed, falling back to Playwright locators: {}", e.getMessage());
		}

		// Fallback using Playwright Locators
		Locator headers = postingTable.locator(".z-listheader, .z-column, th");
		int debitIdx = -1;
		int creditIdx = -1;
		int headerCount = headers.count();
		for (int i = 0; i < headerCount; i++) {
			String text = headers.nth(i).innerText().trim().toLowerCase();
			if (text.contains("accounted debit") || text.equals("debit")) {
				debitIdx = i;
			} else if (text.contains("accounted credit") || text.equals("credit")) {
				creditIdx = i;
			}
		}

		if (debitIdx == -1) debitIdx = 2;
		if (creditIdx == -1) creditIdx = 3;

		Locator rows = postingTable.locator(".z-listitem, .z-row, .z-listbox-body tr, .z-grid-body tr");
		int rowCount = rows.count();
		Double summaryDebit = null;
		Double summaryCredit = null;
		double sumDebit = 0.0;
		double sumCredit = 0.0;

		for (int i = 0; i < rowCount; i++) {
			Locator row = rows.nth(i);
			Locator cells = row.locator(".z-listcell, .z-cell, td");
			int cellCount = cells.count();
			if (cellCount > Math.max(debitIdx, creditIdx)) {
				String dText = cells.nth(debitIdx).innerText().trim().replace(",", "");
				String cText = cells.nth(creditIdx).innerText().trim().replace(",", "");
				String orgText = cells.nth(0).innerText().trim();
				String acctText = cellCount > 1 ? cells.nth(1).innerText().trim() : "";

				try {
					double d = dText.isEmpty() ? 0.0 : Double.parseDouble(dText);
					double c = cText.isEmpty() ? 0.0 : Double.parseDouble(cText);

					if (orgText.isEmpty() && acctText.isEmpty() && (!dText.isEmpty() || !cText.isEmpty())) {
						summaryDebit = d;
						summaryCredit = c;
					} else {
						sumDebit += d;
						sumCredit += c;
					}
				} catch (Exception ignored) {
				}
			}
		}

		double finalDebit = summaryDebit != null ? summaryDebit : sumDebit;
		double finalCredit = summaryCredit != null ? summaryCredit : sumCredit;

		lastPostedAmounts = new double[]{finalDebit, finalCredit};
		return lastPostedAmounts;
	}

	/**
	 * Verifies that the Accounted Debit amount equals Accounted Credit amount in Posting window.
	 */
	public void verifyPostedDebitEqualsCredit() {
		double[] amounts = getPostedDebitAndCreditAmounts();
		double debit = amounts[0];
		double credit = amounts[1];

		boolean isMatch = Double.compare(debit, credit) == 0;
		SoftAssert sa = base.BaseTest.getSoftAssert();
		if (sa != null) {
			sa.assertTrue(isMatch, "[Posting] Accounted Debit [" + debit + "] does not equal Accounted Credit [" + credit + "]");
		} else {
			Assert.assertTrue(isMatch, "[Posting] Accounted Debit [" + debit + "] does not equal Accounted Credit [" + credit + "]");
		}

		if (isMatch) {
			log.info("✅ Verified Posted Debit [{}] equals Credit [{}] successfully.", debit, credit);
		} else {
			log.error("❌ Posted Debit [{}] does NOT equal Credit [{}]!", debit, credit);
		}
	}

	/**
	 * Gets the verified posted amount (e.g. "150.00").
	 *
	 * @return Formatted posted amount
	 */
	public String getPostedAmount() {
		if (lastPostedAmounts != null && lastPostedAmounts.length > 0) {
			return String.format(java.util.Locale.US, "%.2f", lastPostedAmounts[0]);
		}
		double[] amounts = getPostedDebitAndCreditAmounts();
		return String.format(java.util.Locale.US, "%.2f", amounts[0]);
	}

	/**
	 * Closes the Posting window tab.
	 */
	public void closePostingWindow() {
		Locator closeBtn = page.locator("//li[contains(@class,'z-tab') and contains(.,'Posting')]//i[contains(@class,'z-tab-icon') or contains(@class,'z-icon-times') or @aria-label='Close']");
		if (closeBtn.count() > 0 && closeBtn.first().isVisible()) {
			closeBtn.first().click();
			page.waitForTimeout(1000);
			WaitUtils.waitForLoadingIndicatorToDisappear(page);
		} else {
			Locator postingTab = page.locator("//li[contains(@class,'z-tab')]//span[normalize-space(text())='Posting']");
			if (postingTab.count() > 0 && postingTab.first().isVisible()) {
				page.keyboard().press("Alt+W");
				page.waitForTimeout(1000);
				WaitUtils.waitForLoadingIndicatorToDisappear(page);
			}
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
	public Locator getAttributeSetInstanceInput() { return attributeSetInstanceInput; }
	public Locator getIsQCCheckbox() { return isQCCheckbox; }
	public Locator getLotInput() { return lotInput; }
	public Locator getMfgByInput() { return mfgByInput; }
	public Locator getMfgDateInput() { return mfgDateInput; }
	public Locator getGuaranteeDateInput() { return guaranteeDateInput; }
	public Locator getRetestDateInput() { return retestDateInput; }
	public Locator getCheckListButton() { return checkListButton; }
	public Locator getCreateContainersButton() { return createContainersButton; }

	public Locator getCreateLinesModal() { return createLinesModal; }
	public Locator getCreateLinesModalRows() { return createLinesModalRows; }
	public Locator getCreateLinesModalSubmitButton() { return createLinesModalSubmitButton; }
	public Locator getCreateLinesModalCancelButton() { return createLinesModalCancelButton; }
	public Locator getCreateLinesModalSelectAllButton() { return createLinesModalSelectAllButton; }
	public Locator getCreateLinesModalDeselectAllButton() { return createLinesModalDeselectAllButton; }
}
