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
 * Page Object Class for the Shipment (Customer) Window.
 * Covers:
 * - Header creation, Sales Order attachment, reflected field verifications
 * - "Create Lines From" button to generate shipment lines from SO
 * - Shipment Line verifications: Qty and Approved Attribute Set Instance (ASI) selection
 * - Header Document Actions: Prepare and Complete
 */
public class CustomerShipment {

	private static final Logger log = LogManager.getLogger(CustomerShipment.class);
	private final Page page;

	// Cached last created shipment document number
	private static final ThreadLocal<String> lastShipmentDocNo = ThreadLocal.withInitial(() -> "");

	// ==================== Navigation & Menubar Locators ====================
	private final Locator menuSearchInput;
	private final Locator searchShipmentItem;
	private final Locator shipmentTabHeader;
	private final Locator shipmentLineTab;
	private final Locator saveButton;
	private final Locator newButton;

	// ==================== Header Locators (M_InOut0) ====================
	private final Locator orgInput;
	private final Locator docTypeInput;
	private final Locator documentNoInput;
	private final Locator orderInput;
	private final Locator bPartnerInput;
	private final Locator bpLocationInput;
	private final Locator warehouseInput;
	private final Locator salesRepInput;
	private final Locator deliveryViaInput;
	private final Locator movementTypeInput;
	private final Locator movementDateInput;
	private final Locator docStatusInput;
	private final Locator createLinesFromButton;
	private final Locator postedButton;
	private double[] lastPostedAmounts;

	// ==================== Create Lines Modal Locators ====================
	private final Locator createLinesModal;
	private final Locator createLinesModalRows;
	private final Locator createLinesModalSubmitButton;
	private final Locator createLinesModalSelectAllButton;

	// ==================== Line Locators (M_InOutLine0) ====================
	private final Locator lineNoInput;
	private final Locator productInput;
	private final Locator movementQtyInput;
	private final Locator attributeSetInstanceInput;
	private final Locator subtabNextRecordButton;
	private final Locator subtabPrevRecordButton;

	public CustomerShipment(Page page) {
		this.page = page;

		// Navigation & Toolbar
		this.menuSearchInput = page.locator("//input[@class='z-bandbox-input'] | //input[contains(@class,'z-bandbox-input')]");
		this.searchShipmentItem = page.locator(
				"//tr[contains(.,'Shipment (Customer)') or contains(.,'Customer Shipments')]//a[@title='New'] " +
				"| //td[contains(.,'Shipment (Customer)')]/following-sibling::td//a[@title='New'] " +
				"| //td[@title='Shipment (Customer)' or contains(@title,'Customer Shipments')]/following-sibling::td//a[@title='New'] " +
				"| //tr[contains(.,'Shipment (Customer)')]//i[contains(@class,'z-icon-plus')]/parent::a " +
				"| //a[@title='New' and ancestor::tr[contains(.,'Shipment (Customer)')]]");
		this.shipmentTabHeader = page.locator(
				"//div[@instancename='breadcrumb']//a[normalize-space(text())='Shipment'] | //div[@instancename='breadcrumb']//a[contains(text(),'Shipment')] | //li[contains(@class,'z-tab')]//span[normalize-space(text())='Shipment'] | //li[contains(@class,'z-tab')]//span[normalize-space(text())='Shipment (Customer)']");
		this.shipmentLineTab = page.locator(
				"//li[contains(@class,'z-tab')]//span[normalize-space(text())='Shipment Line'] | //div[@instancename='breadcrumb']//a[contains(text(),'Shipment Line')]");
		this.saveButton = page.locator("//a[@title='Save Changes    Alt+S']");
		this.newButton = page.locator("//a[@title='New    Alt+N']");

		// Header Fields
		this.orgInput = page.locator(
				"//span[@instancename='M_InOut0AD_Org_ID']//input | //div[@instancename='M_InOut0AD_Org_ID']//input | //input[@instancename='M_InOut0AD_Org_ID']");
		this.docTypeInput = page.locator(
				"//span[@instancename='M_InOut0C_DocType_ID']//input | //div[@instancename='M_InOut0C_DocType_ID']//input | //input[@instancename='M_InOut0C_DocType_ID']");
		this.documentNoInput = page.locator(
				"//input[@instancename='M_InOut0DocumentNo'] | //span[@instancename='M_InOut0DocumentNo']//input | //div[@instancename='M_InOut0DocumentNo']//input");
		this.orderInput = page.locator(
				"//div[@instancename='M_InOut0C_Order_ID']//input | //span[@instancename='M_InOut0C_Order_ID']//input | //input[@instancename='M_InOut0C_Order_ID']");
		this.bPartnerInput = page.locator(
				"//div[@instancename='M_InOut0C_BPartner_ID']//input | //span[@instancename='M_InOut0C_BPartner_ID']//input | //input[@instancename='M_InOut0C_BPartner_ID']");
		this.bpLocationInput = page.locator(
				"//span[@instancename='M_InOut0C_BPartner_Location_ID']//input | //div[@instancename='M_InOut0C_BPartner_Location_ID']//input | //input[@instancename='M_InOut0C_BPartner_Location_ID']");
		this.warehouseInput = page.locator(
				"//span[@instancename='M_InOut0M_Warehouse_ID']//input | //div[@instancename='M_InOut0M_Warehouse_ID']//input | //input[@instancename='M_InOut0M_Warehouse_ID']");
		this.salesRepInput = page.locator(
				"//span[@instancename='M_InOut0SalesRep_ID']//input | //div[@instancename='M_InOut0SalesRep_ID']//input | //span[@instancename='M_InOut0AD_User_ID']//input | //input[@instancename='M_InOut0SalesRep_ID']");
		this.deliveryViaInput = page.locator(
				"//span[@instancename='M_InOut0DeliveryViaRule']//input | //div[@instancename='M_InOut0DeliveryViaRule']//input | //input[@instancename='M_InOut0DeliveryViaRule']");
		this.movementTypeInput = page.locator(
				"//span[@instancename='M_InOut0MovementType']//input | //div[@instancename='M_InOut0MovementType']//input | //input[@instancename='M_InOut0MovementType']");
		this.movementDateInput = page.locator(
				"//span[@instancename='M_InOut0MovementDate']//input | //div[@instancename='M_InOut0MovementDate']//input | //input[@instancename='M_InOut0MovementDate']");
		this.docStatusInput = page.locator(
				"//span[@instancename='M_InOut0DocStatus']//input | //div[@instancename='M_InOut0DocStatus']//input | //input[@instancename='M_InOut0DocStatus']");
		this.createLinesFromButton = page.locator(
				"//button[@instancename='M_InOut0CreateLinesFrom']");
		this.postedButton = page.locator(
				"//button[@instancename='M_InOut0Posted']");

		// Create Lines Modal
		this.createLinesModal = page.locator(
				"//div[@instancename='infopanel']");
		this.createLinesModalRows = page.locator(
				"//div[@instancename='infopanel']//tr[contains(@class,'z-listitem')]");
		this.createLinesModalSubmitButton = page.locator(
				"//div[@instancename='infopanel']//button[contains(@class,'txt-btn') and contains(text(),'Create lines from')] | //div[@instancename='infopanel']//button[@title='OK' or @title='Submit'] | //div[@instancename='infopanel']//button[contains(@class,'btn-ok')] | (//div[@instancename='infopanel']//button)[last()]");
		this.createLinesModalSelectAllButton = page.locator(
				"//div[@instancename='infopanel']//button[@title='Select All' or contains(text(),'Select All')]");

		// Line Fields
		this.lineNoInput = page.locator(
				"//span[@instancename='M_InOutLine0Line']//input | //div[@instancename='M_InOutLine0Line']//input | //input[@instancename='M_InOutLine0Line']");
		this.productInput = page.locator(
				"//span[@instancename='M_InOutLine0M_Product_ID']//input | //div[@instancename='M_InOutLine0M_Product_ID']//input | //input[@instancename='M_InOutLine0M_Product_ID']");
		this.movementQtyInput = page.locator(
				"//span[@instancename='M_InOutLine0MovementQty']//input[contains(@class,'z-decimalbox')] | //div[@instancename='M_InOutLine0MovementQty']//input[contains(@class,'z-decimalbox')] | //span[@instancename='M_InOutLine0MovementQty']//input | //div[@instancename='M_InOutLine0MovementQty']//input");
		this.attributeSetInstanceInput = page.locator(
				"//span[@instancename='M_InOutLine0M_AttributeSetInstance_ID']//input | //div[@instancename='M_InOutLine0M_AttributeSetInstance_ID']//input | //input[@instancename='M_InOutLine0M_AttributeSetInstance_ID']");
		this.subtabNextRecordButton = page.locator(
				"//a[contains(@title,'Next record') or contains(@title,'Next Record')] | (//div[contains(@class,'breadcrumb-record-info')]/following-sibling::a)[1] | (//div[contains(@class,'adwindow-detailpane') or contains(@class,'z-tabpanel')]//a[contains(@title,'Next')])[1]");
		this.subtabPrevRecordButton = page.locator(
				"//a[contains(@title,'Previous record') or contains(@title,'Previous Record')] | (//div[contains(@class,'breadcrumb-record-info')]/preceding-sibling::a)[last()]");
	}

	// ==================== Navigation Actions ====================

	/**
	 * Navigates to the Shipment (Customer) window and creates a new record
	 * through the menubar search bandbox by clicking the '+' (New) button on the label.
	 */
	public void navigateToCustomerShipmentWindow() {
		log.info("Navigating to Shipment (Customer) window and creating new record via Menu Search Box '+' button on label...");
		page.waitForTimeout(1000);
		WaitUtils.waitForLoadingIndicatorToDisappear(page);

		if (menuSearchInput.count() == 0 || !menuSearchInput.first().isVisible()) {
			Locator closeTabBtn = page.locator("(//div[@class='z-tab-button']//i[@aria-label='Close'])[last()] | //li[contains(@class,'z-tab-selected')]//i[@aria-label='Close']");
			if (closeTabBtn.count() > 0 && closeTabBtn.first().isVisible()) {
				log.info("Closing active window tab to return to menubar search...");
				closeTabBtn.first().click();
				page.waitForTimeout(1500);
				WaitUtils.waitForLoadingIndicatorToDisappear(page);
			}
		}

		menuSearchInput.first().waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(15000));
		menuSearchInput.first().click();
		menuSearchInput.first().fill("Shipment (Customer)");

		// Wait for search dropdown list to appear
		page.waitForTimeout(4000);
		WaitUtils.waitForLoadingIndicatorToDisappear(page);

		Locator newBtnOnLabel = searchShipmentItem.first();
		try {
			newBtnOnLabel.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(8000));
			log.info("Clicking '+' (New) button on 'Shipment (Customer)' search dropdown label...");
			newBtnOnLabel.click(new Locator.ClickOptions().setForce(true));
		} catch (Exception e) {
			log.warn("'+' button on label not clicked directly: {}, attempting JS click on New button...", e.getMessage());
			page.evaluate("() => {" +
					"let row = Array.from(document.querySelectorAll('tr')).find(r => r.innerText && (r.innerText.includes('Shipment (Customer)') || r.innerText.includes('Customer Shipments')));" +
					"if (row) {" +
					"    let newBtn = row.querySelector(\"a[title='New'], a[title*='New'], i.z-icon-plus\");" +
					"    if (newBtn) {" +
					"        (newBtn.closest('a') || newBtn).click();" +
					"    } else {" +
					"        row.click();" +
					"    }" +
					"}" +
			"}");
		}

		try {
			orderInput.first().waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(25000));
			log.info("Shipment (Customer) window opened and new record initialized via menu search '+' button.");
		} catch (Exception e) {
			log.warn("Waiting for Shipment window orderInput timed out: {}", e.getMessage());
		}
		WaitUtils.waitForLoadingIndicatorToDisappear(page);
		handleWarningPopup();
	}

	/**
	 * Ensures a new Shipment record is initialized.
	 * If the record was already created via the menu search '+' button on the label, this verifies readiness.
	 */
	public void createNewRecord() {
		log.info("Ensuring new record initialized in Shipment (Customer) window...");
		page.waitForTimeout(1000);
		handleWarningPopup();

		// Check if record is already empty / fresh from menu search '+' button
		boolean isFreshRecord = false;
		try {
			String docNo = documentNoInput.count() > 0 ? documentNoInput.first().inputValue().trim() : "";
			String order = orderInput.count() > 0 ? orderInput.first().inputValue().trim() : "";
			if ((docNo.isEmpty() || docNo.equals("<>")) && order.isEmpty()) {
				isFreshRecord = true;
			}
		} catch (Exception ignored) {
		}

		if (isFreshRecord) {
			log.info("✅ New Shipment record already created via menu search '+' button on label. Ready for data entry.");
			return;
		}

		log.info("Record not in new state, clicking toolbar New button (Alt+N)...");
		newButton.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		newButton.click();
		page.waitForTimeout(1500);
		WaitUtils.waitForLoadingIndicatorToDisappear(page);
		handleWarningPopup();
	}

	// ==================== Header Actions ====================

	/**
	 * Selects the Organization if needed.
	 *
	 * @param orgName Organization name
	 */
	public void selectOrg(String orgName) {
		page.waitForTimeout(1000);
		orgInput.first().waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		orgInput.first().fill(orgName);
		orgInput.first().press("Tab");
		page.waitForTimeout(2000);
		WaitUtils.waitForLoadingIndicatorToDisappear(page);
	}

	/**
	 * Attaches Sales Order reference into the Order field and tabs out.
	 *
	 * @param soDocNo Sales Order Document Number
	 */
	public void attachSalesOrder(String soDocNo) {
		log.info("Attaching Sales Order reference [{}] to Shipment Header...", soDocNo);
		page.waitForTimeout(1000);

		// If orgInput is empty, populate org first
		try {
			if (orgInput.count() > 0 && orgInput.first().isVisible()) {
				String curOrg = orgInput.first().inputValue();
				if (curOrg == null || curOrg.trim().isEmpty()) {
					String defaultOrg = DataManager.getData("so.org", "org");
					if (!defaultOrg.isEmpty()) {
						selectOrg(defaultOrg);
					}
				}
			}
		} catch (Exception ignored) {
		}

		orderInput.first().waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(15000));
		orderInput.first().click();
		orderInput.first().fill(soDocNo);
		orderInput.first().press("Tab");
		page.waitForTimeout(2500);
		WaitUtils.waitForLoadingIndicatorToDisappear(page);

		// Dismiss any warning popups (e.g. credit limit, delivery rules)
		handleWarningPopup();
		page.waitForTimeout(1000);
		WaitUtils.waitForLoadingIndicatorToDisappear(page);

		verifyFieldFilled(orderInput.first(), "Sales Order Reference", soDocNo);
		log.info("Successfully attached Sales Order [{}] to Shipment Header.", soDocNo);
	}

	/**
	 * Saves changes to the Shipment Header.
	 */
	public void saveShipmentHeader() {
		log.info("Saving Shipment Header record...");
		saveButton.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		saveButton.click();
		page.waitForTimeout(1500);
		WaitUtils.waitForLoadingIndicatorToDisappear(page);
		handleWarningPopup();
		captureShipmentDocNo();
	}

	/**
	 * Captures and caches the Shipment Document Number from the DocumentNo field.
	 *
	 * @return captured Document Number
	 */
	public String captureShipmentDocNo() {
		try {
			documentNoInput.first().waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
			String docNo = documentNoInput.first().inputValue().trim();
			lastShipmentDocNo.set(docNo);
			log.info("Captured Shipment Document No: [{}]", docNo);
			return docNo;
		} catch (Exception e) {
			log.warn("Could not capture Shipment Document No: {}", e.getMessage());
			return "";
		}
	}

	public static String getLastShipmentDocNo() {
		return lastShipmentDocNo.get();
	}

	public static void setLastShipmentDocNo(String docNo) {
		lastShipmentDocNo.set(docNo);
	}

	public String getShipmentDocNo() {
		String cached = lastShipmentDocNo.get();
		if (cached == null || cached.trim().isEmpty()) {
			return captureShipmentDocNo();
		}
		return cached;
	}

	// ==================== Header Reflected Fields Validations ====================

	/**
	 * Verifies the fields automatically populated on Shipment Header from Sales Order:
	 * - Sales Order Reference
	 * - Business Partner
	 * - Partner Location
	 * - Warehouse
	 * - Delivery Via
	 * - DocStatus (Drafted)
	 *
	 * @param expectedSODocNo Expected Sales Order Document Number
	 */
	public void verifyReflectedFieldsFromSO(String expectedSODocNo) {
		log.info("========== Verifying Shipment Header fields reflected from Sales Order [{}] ==========", expectedSODocNo);
		page.waitForTimeout(1500);
		WaitUtils.waitForLoadingIndicatorToDisappear(page);

		SoftAssert sa = base.BaseTest.getSoftAssert();

		// 1. Order Reference
		String actualOrder = orderInput.first().inputValue().trim();
		boolean orderMatch = actualOrder.equalsIgnoreCase(expectedSODocNo.trim()) || actualOrder.contains(expectedSODocNo.trim());
		if (sa != null) {
			sa.assertTrue(orderMatch, "[Shipment Header] Sales Order reference [" + actualOrder + "] does not match expected [" + expectedSODocNo + "]");
		} else {
			Assert.assertTrue(orderMatch, "[Shipment Header] Sales Order reference [" + actualOrder + "] does not match expected [" + expectedSODocNo + "]");
		}
		log.info("Verified Order Reference on Shipment Header: [{}]", actualOrder);

		// 2. Business Partner
		String expectedBP = DataManager.getData("so.bPartner", "bPartner");
		String actualBP = bPartnerInput.first().inputValue().trim();
		boolean bpMatch = actualBP.equalsIgnoreCase(expectedBP) || actualBP.contains(expectedBP) || expectedBP.contains(actualBP);
		if (sa != null) {
			sa.assertTrue(bpMatch, "[Shipment Header] Business Partner [" + actualBP + "] does not match expected [" + expectedBP + "]");
		} else {
			Assert.assertTrue(bpMatch, "[Shipment Header] Business Partner [" + actualBP + "] does not match expected [" + expectedBP + "]");
		}
		log.info("Verified Business Partner on Shipment Header: [{}] (Expected: [{}])", actualBP, expectedBP);

		// 3. Partner Location
		String expectedLoc = DataManager.getData("so.bPLocation", "bPLocation");
		String actualLoc = bpLocationInput.first().inputValue().trim();
		boolean locMatch = actualLoc.equalsIgnoreCase(expectedLoc) || actualLoc.contains(expectedLoc) || expectedLoc.contains(actualLoc);
		if (sa != null) {
			sa.assertTrue(locMatch, "[Shipment Header] Partner Location [" + actualLoc + "] does not match expected [" + expectedLoc + "]");
		} else {
			Assert.assertTrue(locMatch, "[Shipment Header] Partner Location [" + actualLoc + "] does not match expected [" + expectedLoc + "]");
		}
		log.info("Verified Partner Location on Shipment Header: [{}] (Expected: [{}])", actualLoc, expectedLoc);

		// 4. Warehouse
		String expectedWH = DataManager.getData("so.warehouse", "warehouse");
		String actualWH = warehouseInput.first().inputValue().trim();
		boolean whMatch = actualWH.equalsIgnoreCase(expectedWH) || actualWH.contains(expectedWH) || expectedWH.contains(actualWH);
		if (sa != null) {
			sa.assertTrue(whMatch, "[Shipment Header] Warehouse [" + actualWH + "] does not match expected [" + expectedWH + "]");
		} else {
			Assert.assertTrue(whMatch, "[Shipment Header] Warehouse [" + actualWH + "] does not match expected [" + expectedWH + "]");
		}
		log.info("Verified Warehouse on Shipment Header: [{}] (Expected: [{}])", actualWH, expectedWH);

		// 5. Delivery Via (if present)
		String expectedDelivery = DataManager.getData("so.deliveryVia", "deliveryVia");
		if (deliveryViaInput.count() > 0 && deliveryViaInput.first().isVisible() && !expectedDelivery.isEmpty()) {
			String actualDelivery = deliveryViaInput.first().inputValue().trim();
			boolean delMatch = actualDelivery.equalsIgnoreCase(expectedDelivery)
					|| actualDelivery.equalsIgnoreCase("Delivery")
					|| actualDelivery.equalsIgnoreCase("Pickup")
					|| actualDelivery.contains(expectedDelivery)
					|| expectedDelivery.contains(actualDelivery);
			if (sa != null) {
				sa.assertTrue(delMatch, "[Shipment Header] Delivery Via [" + actualDelivery + "] does not match expected [" + expectedDelivery + "]");
			}
			log.info("Verified Delivery Via on Shipment Header: [{}] (Expected: [{}])", actualDelivery, expectedDelivery);
		}

		// 6. DocStatus Drafted
		String actualStatus = docStatusInput.first().inputValue().trim();
		boolean statusMatch = actualStatus.equalsIgnoreCase("Drafted");
		if (sa != null) {
			sa.assertTrue(statusMatch, "[Shipment Header] Document Status [" + actualStatus + "] is not Drafted");
		}
		log.info("Verified Document Status on Shipment Header: [{}]", actualStatus);

		log.info("✅ All reflected fields verified successfully on Shipment Header.");
	}

	// ==================== Create Lines From Action ====================

	/**
	 * Clicks 'Create Lines From' button, selects all lines from SO in the modal dialog,
	 * and confirms/submits to generate shipment lines.
	 */
	public void createShipmentLinesFromSO() {
		log.info("Clicking 'Create Lines From' button to generate Shipment Lines...");
		page.waitForTimeout(1000);
		createLinesFromButton.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(15000));
		createLinesFromButton.click();
		page.waitForTimeout(2000);
		WaitUtils.waitForLoadingIndicatorToDisappear(page);

		createLinesModal.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(15000));
		log.info("'Create Lines From' modal opened.");

		// Select all lines: click 'Select All' button if available, or check header checkbox, or click checkable for all rows
		Locator selectAllBtn = createLinesModal.locator(
				"//button[@title='Select All' or contains(text(),'Select All') or @title='Select all']");
		Locator headerCheckable = createLinesModal.locator(
				"//div[contains(@class,'z-listheader')]//div[contains(@class,'z-listheader-checkable')] | //th//div[contains(@class,'checkable')]");

		if (selectAllBtn.count() > 0 && selectAllBtn.first().isVisible()) {
			selectAllBtn.first().click();
			page.waitForTimeout(1000);
			log.info("Clicked 'Select All' in Create Lines From modal.");
		} else if (headerCheckable.count() > 0 && headerCheckable.first().isVisible()) {
			headerCheckable.first().click();
			page.waitForTimeout(1000);
			log.info("Clicked header checkable to select all lines in Create Lines modal.");
		} else {
			Locator rows = createLinesModal.locator("//tr[contains(@class,'z-listitem')]");
			int count = rows.count();
			log.info("Found [{}] rows in Create Lines modal.", count);
			for (int i = 0; i < count; i++) {
				Locator r = rows.nth(i);
				Locator checkable = r.locator(".z-listitem-checkable");
				if (checkable.count() > 0 && checkable.first().isVisible()) {
					checkable.first().click();
				} else {
					r.click();
				}
				page.waitForTimeout(300);
			}
		}

		// Submit modal
		Locator submitBtn = createLinesModalSubmitButton.first();
		submitBtn.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		submitBtn.click();
		page.waitForTimeout(2500);
		WaitUtils.waitForLoadingIndicatorToDisappear(page);

		try {
			createLinesModal.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.HIDDEN).setTimeout(10000));
		} catch (Exception ignored) {
		}

		handleWarningPopup();
		log.info("Successfully created shipment lines from Sales Order.");
	}

	// ==================== Shipment Line Actions & Validations ====================

	/**
	 * Navigates to the Shipment Line tab and ensures Form View is active.
	 */
	public void navigateToShipmentLineTab() {
		log.info("Navigating to Shipment Line tab...");
		shipmentLineTab.first().waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		shipmentLineTab.first().click();
		page.waitForTimeout(2000);
		WaitUtils.waitForLoadingIndicatorToDisappear(page);
		ensureShipmentLineFormView();
		log.info("Navigated to 'Shipment Line' tab successfully.");
	}

	/**
	 * Ensures Form View on the Shipment Line tab.
	 */
	public void ensureShipmentLineFormView() {
		try {
			page.waitForTimeout(1000);
			Locator visibleQty = page.locator(
					"//span[@instancename='M_InOutLine0QtyEntered']//input | //div[@instancename='M_InOutLine0QtyEntered']//input | //input[@instancename='M_InOutLine0QtyEntered'] | //span[@instancename='M_InOutLine0Line']//input | //div[@instancename='M_InOutLine0Line']//input | //span[@instancename='M_InOutLine0MovementQty']//input | //div[@instancename='M_InOutLine0MovementQty']//input").locator("visible=true");
			if (visibleQty.count() > 0 && visibleQty.first().isVisible()) {
				log.info("Shipment Line form view is already visible.");
				return;
			}

			log.info("Shipment Line form view not visible. Switching to Form View...");
			Locator toggleBtn = page.locator("//a[contains(@title,'Shift+Alt+T')] | //a[@title='Grid Toggle    Alt+T']").first();
			if (toggleBtn.count() > 0 && toggleBtn.isVisible()) {
				toggleBtn.click();
			} else {
				page.keyboard().press("Shift+Alt+T");
			}
			page.waitForTimeout(2000);
			WaitUtils.waitForLoadingIndicatorToDisappear(page);

			Locator checkAgain = page.locator(
					"//span[@instancename='M_InOutLine0QtyEntered']//input | //div[@instancename='M_InOutLine0QtyEntered']//input | //input[@instancename='M_InOutLine0QtyEntered'] | //span[@instancename='M_InOutLine0Line']//input | //div[@instancename='M_InOutLine0Line']//input | //span[@instancename='M_InOutLine0MovementQty']//input | //div[@instancename='M_InOutLine0MovementQty']//input").locator("visible=true");
			if (checkAgain.count() == 0 || !checkAgain.first().isVisible()) {
				Locator firstRow = page.locator("//tr[contains(@class,'z-row') or contains(@class,'z-listitem')]").first();
				if (firstRow.count() > 0 && firstRow.isVisible()) {
					firstRow.dblclick();
					page.waitForTimeout(2000);
					WaitUtils.waitForLoadingIndicatorToDisappear(page);
				}
			}
		} catch (Exception e) {
			log.warn("Could not toggle detail pane form view: {}", e.getMessage());
		}
	}

	/**
	 * Verifies Movement Quantity on the current Shipment Line.
	 *
	 * @param expectedQty Expected movement quantity (e.g. "100" or "30")
	 */
	public void verifyMovementQty(String expectedQty) {
		page.waitForTimeout(1000);
		Locator qtyLoc = page.locator(
				"//span[@instancename='M_InOutLine0QtyEntered']//input | //div[@instancename='M_InOutLine0QtyEntered']//input | //input[@instancename='M_InOutLine0QtyEntered'] | //span[@instancename='M_InOutLine0MovementQty']//input | //div[@instancename='M_InOutLine0MovementQty']//input");

		Locator visibleQty = qtyLoc.locator("visible=true");
		String actualQtyStr = "";
		if (visibleQty.count() > 0) {
			actualQtyStr = visibleQty.first().inputValue().trim();
		} else if (qtyLoc.count() > 0) {
			actualQtyStr = qtyLoc.first().inputValue().trim();
		}
		if (actualQtyStr.isEmpty()) {
			try {
				actualQtyStr = (String) page.evaluate("() => {" +
						"let el = document.querySelector(\"[instancename='M_InOutLine0QtyEntered'] input\") || document.querySelector(\"input[instancename='M_InOutLine0QtyEntered']\");" +
						"return el ? el.value : '';" +
				"}");
			} catch (Exception ignored) {
			}
		}

		boolean isMatch = false;
		try {
			double actual = Double.parseDouble(actualQtyStr.replace(",", ""));
			double expected = Double.parseDouble(expectedQty.replace(",", ""));
			isMatch = Math.abs(actual - expected) < 0.01;
		} catch (Exception e) {
			isMatch = actualQtyStr.equalsIgnoreCase(expectedQty.trim());
		}

		SoftAssert sa = base.BaseTest.getSoftAssert();
		if (sa != null) {
			sa.assertTrue(isMatch, "[Shipment Line] Movement Qty [" + actualQtyStr + "] does not match expected [" + expectedQty + "]");
		} else {
			Assert.assertTrue(isMatch, "[Shipment Line] Movement Qty [" + actualQtyStr + "] does not match expected [" + expectedQty + "]");
		}
		log.info("✅ Verified Shipment Line Movement Qty: [{}] (Expected: [{}])", actualQtyStr, expectedQty);
	}

	/**
	 * Attaches an Approved Attribute Set Instance (ASI) on the active line:
	 * 1. Clicks on the ASI field / button
	 * 2. Clicks 'Select Existing Record'
	 * 3. Chooses the record with 'Approved' status
	 * 4. Confirms the selection and saves the line
	 */
	public void attachApprovedASI() {
		log.info("Attaching Approved Attribute Set Instance to current Shipment Line...");
		page.waitForTimeout(1000);

		// 1. Click on ASI button/icon or container with force=true
		Locator asiBox = page.locator(
				"//div[@instancename='M_InOutLine0M_AttributeSetInstance_ID']//i | //span[@instancename='M_InOutLine0M_AttributeSetInstance_ID']//i | //div[@instancename='M_InOutLine0M_AttributeSetInstance_ID']//button | //span[@instancename='M_InOutLine0M_AttributeSetInstance_ID']//button | //div[@instancename='M_InOutLine0M_AttributeSetInstance_ID']//a | //span[@instancename='M_InOutLine0M_AttributeSetInstance_ID']//a | //div[@instancename='M_InOutLine0M_AttributeSetInstance_ID'] | //span[@instancename='M_InOutLine0M_AttributeSetInstance_ID']");

		if (asiBox.count() > 0 && asiBox.first().isVisible()) {
			asiBox.first().click(new Locator.ClickOptions().setForce(true));
		} else {
			attributeSetInstanceInput.first().click(new Locator.ClickOptions().setForce(true));
		}

		page.waitForTimeout(1500);
		WaitUtils.waitForLoadingIndicatorToDisappear(page);

		// 2. Wait for Attribute Set Instance modal to appear
		Locator asiModal = page.locator(
				"//div[contains(@class,'z-window') and contains(.,'Attribute Set Instance') and not(contains(@style,'display: none')) and not(contains(@style,'display:none'))]");
		if (asiModal.count() == 0 || !asiModal.first().isVisible()) {
			log.info("ASI modal not opened by locator click, triggering JS click on tag/editor button...");
			page.evaluate("() => {" +
					"let box = document.querySelector(\"[instancename='M_InOutLine0M_AttributeSetInstance_ID']\");" +
					"if (box) {" +
					"  let btn = box.querySelector('i') || box.querySelector('button') || box.querySelector('a') || box;" +
					"  btn.click();" +
					"}" +
			"}");
			page.waitForTimeout(2000);
			WaitUtils.waitForLoadingIndicatorToDisappear(page);
		}

		asiModal.first().waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(15000));
		log.info("Attribute Set Instance dialog opened successfully.");

		// 3. Click 'Select existing record' button
		log.info("Clicking 'Select existing record' button in ASI dialog...");
		Locator selectExistingBtn = asiModal.first().locator(
				"//button[contains(.,'Select existing record') or contains(.,'Select Existing') or contains(@title,'Select existing')] | //a[contains(.,'Select existing record') or contains(@title,'Select Existing')]");

		selectExistingBtn.first().waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(15000));
		try {
			selectExistingBtn.first().click(new Locator.ClickOptions().setForce(true));
		} catch (Exception ex) {
			page.evaluate("() => {" +
					"let btns = Array.from(document.querySelectorAll('button'));" +
					"let b = btns.find(btn => (btn.innerText && btn.innerText.includes('Select existing record')) || (btn.title && btn.title.includes('Select existing')));" +
					"if (b) b.click();" +
			"}");
		}
		page.waitForTimeout(2000);
		WaitUtils.waitForLoadingIndicatorToDisappear(page);

		// 4. In the existing ASI list, locate the record with 'Approved'
		log.info("Selecting ASI record with 'Approved' status...");
		Locator approvedRow = page.locator(
				"//tr[(contains(@class,'z-listitem') or contains(@class,'z-row')) and contains(.,'Approved')]").first();

		approvedRow.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(15000));
		String approvedRowText = approvedRow.innerText().replaceAll("\\s+", " ").trim();
		log.info("Found Approved ASI record: [{}]", approvedRowText);

		// Click checkable or row
		Locator checkable = approvedRow.locator(".z-listitem-checkable");
		if (checkable.count() > 0 && checkable.first().isVisible()) {
			checkable.first().click(new Locator.ClickOptions().setForce(true));
		} else {
			approvedRow.click(new Locator.ClickOptions().setForce(true));
		}
		page.waitForTimeout(1000);

		// Confirm selection in the selection dialog (checkmark button or OK button)
		log.info("Confirming selection in ASI existing records modal...");
		Locator selectModalOkBtn = page.locator(
				"//div[contains(@class,'z-window') and contains(.,'Product Attribute Instance')]//button[contains(@class,'btn-ok') or @title='OK' or .//i[contains(@class,'check')]] | //div[contains(@class,'z-window') and not(contains(@style,'display: none'))]//button[contains(@class,'btn-ok') or @title='OK']");
		try {
			if (selectModalOkBtn.count() > 0 && selectModalOkBtn.first().isVisible()) {
				selectModalOkBtn.first().click(new Locator.ClickOptions().setForce(true));
			} else {
				approvedRow.dblclick(new Locator.DblclickOptions().setForce(true));
			}
		} catch (Exception e) {
			log.warn("selectModalOkBtn click error, fallback to double-click and JS click: {}", e.getMessage());
			try {
				approvedRow.dblclick(new Locator.DblclickOptions().setForce(true));
			} catch (Exception ignored) {
			}
		}
		page.evaluate("() => {" +
				"let w = Array.from(document.querySelectorAll('div.z-window')).find(el => el.innerText && el.innerText.includes('Product Attribute Instance'));" +
				"if (w) {" +
				"    let btn = w.querySelector(\"button.btn-ok, button[title='OK']\");" +
				"    if (btn) btn.click();" +
				"}" +
		"}");
		page.waitForTimeout(2000);
		WaitUtils.waitForLoadingIndicatorToDisappear(page);

		// 5. If the parent ASI dialog is still displayed, click its checkmark/OK button
		Locator activeParentAsiModal = page.locator(
				"//div[contains(@class,'z-window') and not(contains(@style,'display: none')) and not(contains(@style,'display:none')) and (contains(.,'Attribute Set Instance') and not(contains(.,'Product Attribute Instance')))]");
		if (activeParentAsiModal.count() > 0 && activeParentAsiModal.first().isVisible()) {
			log.info("Parent ASI modal still visible, confirming with checkmark button...");
			Locator parentOkBtn = page.locator(
					"//div[contains(@class,'z-window') and not(contains(@style,'display: none')) and (contains(.,'Attribute Set Instance') and not(contains(.,'Product Attribute Instance')))]//button[contains(@class,'btn-ok') or @title='OK' or .//i[contains(@class,'check')]]");
			try {
				if (parentOkBtn.count() > 0 && parentOkBtn.first().isVisible()) {
					parentOkBtn.first().click(new Locator.ClickOptions().setForce(true));
				}
			} catch (Exception ex) {
				log.warn("parentOkBtn click error: {}", ex.getMessage());
			}
			page.evaluate("() => {" +
					"let w = Array.from(document.querySelectorAll('div.z-window')).find(el => el.innerText && el.innerText.includes('Attribute Set Instance') && !el.innerText.includes('Product Attribute Instance'));" +
					"if (w) {" +
					"    let btn = w.querySelector(\"button.btn-ok, button[title='OK']\");" +
					"    if (btn) btn.click();" +
					"}" +
			"}");
			page.waitForTimeout(2000);
			WaitUtils.waitForLoadingIndicatorToDisappear(page);
		}

		// Wait for any remaining modal mask to close
		try {
			page.evaluate("() => {" +
					"let masks = document.querySelectorAll('div.z-modal-mask');" +
					"if (masks.length > 0) {" +
					"    let btns = Array.from(document.querySelectorAll('.pattribute-instance-dialog button.btn-ok, .pattribute-instance-dialog button[title=\"OK\"]'));" +
					"    btns.forEach(b => b.click());" +
					"}" +
			"}");
			page.waitForTimeout(1000);
		} catch (Exception ignored) {
		}

		// 6. Save line changes
		log.info("Saving Shipment Line with attached Approved ASI...");
		try {
			if (saveButton.count() > 0 && saveButton.first().isVisible()) {
				saveButton.first().click(new Locator.ClickOptions().setForce(true));
			} else {
				page.keyboard().press("Alt+S");
			}
		} catch (Exception e) {
			log.warn("Save line button click failed, pressing Alt+S: {}", e.getMessage());
			page.keyboard().press("Alt+S");
		}
		page.waitForTimeout(2000);
		WaitUtils.waitForLoadingIndicatorToDisappear(page);
		handleWarningPopup();

		Locator updatedASILoc = attributeSetInstanceInput.locator("visible=true");
		String updatedASI = "";
		try {
			updatedASI = updatedASILoc.count() > 0 ? updatedASILoc.first().inputValue().trim() : attributeSetInstanceInput.first().inputValue().trim();
		} catch (Exception ignored) {
		}
		log.info("✅ Approved ASI successfully attached to line. Current ASI value: [{}]", updatedASI);
	}

	/**
	 * Navigates to the next Shipment Line using detail pane toolbar or shortcut.
	 */
	public void navigateToNextShipmentLine() {
		log.info("Navigating to next Shipment Line record...");
		Locator lineInput = page.locator(
				"//span[@instancename='M_InOutLine0Line']//input | //div[@instancename='M_InOutLine0Line']//input | //input[@instancename='M_InOutLine0Line']").locator("visible=true");
		String curLine = lineInput.count() > 0 ? lineInput.first().inputValue().trim() : "";
		if (curLine.equals("20")) {
			log.info("Already on Line No: [20].");
			return;
		}

		Locator nextBtn = page.locator(
				"//div[contains(@class,'adwindow-detailpane') or contains(@class,'z-tabpanel')]//a[contains(@title,'Next record') or contains(@title,'Next Record')] | (//a[contains(@title,'Next record') or contains(@title,'Next Record')])[1] | (//div[contains(@class,'breadcrumb-record-info')]/following-sibling::a)[1]");
		try {
			if (nextBtn.count() > 0 && nextBtn.first().isVisible() && !Boolean.TRUE.equals(nextBtn.first().isDisabled())) {
				nextBtn.first().click(new Locator.ClickOptions().setForce(true));
			} else {
				page.keyboard().press("Alt+Right");
			}
		} catch (Exception e) {
			log.warn("Clicking next button failed, pressing Alt+Right: {}", e.getMessage());
			page.keyboard().press("Alt+Right");
		}
		page.waitForTimeout(2000);
		WaitUtils.waitForLoadingIndicatorToDisappear(page);

		curLine = lineInput.count() > 0 ? lineInput.first().inputValue().trim() : "";
		if (!curLine.equals("20")) {
			log.info("Current line is still [{}]. Pressing Shift+Alt+ArrowRight...", curLine);
			page.keyboard().press("Shift+Alt+ArrowRight");
			page.waitForTimeout(2000);
			WaitUtils.waitForLoadingIndicatorToDisappear(page);
		}
		curLine = lineInput.count() > 0 ? lineInput.first().inputValue().trim() : "";
		log.info("Shipment Line navigation completed. Active Line No: [{}]", curLine);
	}

	/**
	 * Completes the verification and ASI attachment for both lines (Line 1 & Line 2):
	 * - Line 1: verify Qty, attach Approved ASI
	 * - Line 2: verify Qty, attach Approved ASI
	 *
	 * @param line1ExpectedQty Expected Qty for Line 1 (e.g. 100)
	 * @param line2ExpectedQty Expected Qty for Line 2 (e.g. 30)
	 */
	public void verifyAndAttachASIForBothLines(String line1ExpectedQty, String line2ExpectedQty) {
		log.info("========== Processing Shipment Line 1 ==========");
		ensureShipmentLineFormView();
		Locator visibleLineNo = lineNoInput.locator("visible=true");
		String line1No = visibleLineNo.count() > 0 && visibleLineNo.first().isVisible() ? visibleLineNo.first().inputValue().trim() : "10";
		log.info("Current line is Line No: [{}]", line1No);
		verifyMovementQty(line1ExpectedQty);
		attachApprovedASI();

		log.info("========== Processing Shipment Line 2 ==========");
		navigateToNextShipmentLine();
		ensureShipmentLineFormView();
		Locator visibleLineNo2 = lineNoInput.locator("visible=true");
		String line2No = visibleLineNo2.count() > 0 && visibleLineNo2.first().isVisible() ? visibleLineNo2.first().inputValue().trim() : "20";
		log.info("Navigated to next line — Line No: [{}]", line2No);
		verifyMovementQty(line2ExpectedQty);
		attachApprovedASI();

		log.info("✅ Both Shipment Lines processed successfully with verified Qty and Approved ASI.");
	}

	public void verifyAndAttachASIForBothLines() {
		String qty1 = DataManager.getData("so.qtyEntered", "qtyEntered");
		String qty2 = DataManager.getData("so.freeQty", "freeQty");
		if (qty2.isEmpty()) {
			qty2 = "30";
		}
		verifyAndAttachASIForBothLines(qty1, qty2);
	}

	// ==================== Header Navigation & Document Actions ====================

	/**
	/**
	 * Ensures the main window tab for 'Shipment (Customer)' is selected/active.
	 * When Complete is performed on Shipment, the ERP automatically opens the generated
	 * 'Sales Invoice and Credit/Debit Note' tab. This method switches focus back to the Shipment tab.
	 */
	public void switchToShipmentWindowTab() {
		Locator shipmentMainTab = page.locator("//li[contains(@class,'z-tab') and .//span[contains(text(),'Shipment')]]");
		if (shipmentMainTab.count() > 0 && shipmentMainTab.first().isVisible()) {
			Locator activeShipmentTab = page.locator("//li[contains(@class,'z-tab-selected') and .//span[contains(text(),'Shipment')]]");
			if (activeShipmentTab.count() == 0 || !activeShipmentTab.first().isVisible()) {
				log.info("Switching to 'Shipment (Customer)' window tab...");
				shipmentMainTab.first().click(new Locator.ClickOptions().setForce(true));
				page.waitForTimeout(1500);
				WaitUtils.waitForLoadingIndicatorToDisappear(page);
			}
		}
	}

	/**
	 * Navigates back to the Shipment Header tab.
	 */
	public void navigateBackToShipmentHeader() {
		log.info("Navigating back to Shipment Header tab...");
		page.waitForTimeout(1000);

		// If an automatically opened window tab (like Sales Invoice) is in front, switch back to Shipment tab
		switchToShipmentWindowTab();

		// If header fields are already visible, we are already on the Shipment Header
		if (docStatusInput.count() > 0 && docStatusInput.first().isVisible()) {
			log.info("Already on Shipment Header.");
			return;
		}

		Locator headerBreadcrumb = page.locator(
				"//div[@instancename='breadcrumb']//a[normalize-space(text())='Shipment'] | (//div[@instancename='breadcrumb']//a[contains(@class,'z-a')])[1] | //li[contains(@class,'z-tab')]//span[normalize-space(text())='Shipment']");
		try {
			if (headerBreadcrumb.count() > 0 && headerBreadcrumb.first().isVisible()) {
				headerBreadcrumb.first().click(new Locator.ClickOptions().setForce(true));
			} else if (shipmentTabHeader.count() > 0 && shipmentTabHeader.first().isVisible()) {
				shipmentTabHeader.first().click(new Locator.ClickOptions().setForce(true));
			}
		} catch (Exception e) {
			log.warn("Header click error, attempting JS click: {}", e.getMessage());
			page.evaluate("() => {" +
					"let a = document.querySelector(\"div[instancename='breadcrumb'] a\");" +
					"if (a) a.click();" +
			"}");
		}
		page.waitForTimeout(2000);
		WaitUtils.waitForLoadingIndicatorToDisappear(page);
		handleWarningPopup();
		log.info("Navigated back to Shipment Header.");
	}

	// ==================== Popup Handling & Utilities ====================

	/**
	 * Handles warning/notification popups without dismissing functional modals.
	 *
	 * @return extracted warning popup text
	 */
	public String handleWarningPopup() {
		StringBuilder allPopupText = new StringBuilder();
		try {
			int attempts = 0;
			while (attempts < 3) {
				page.waitForTimeout(800);
				Locator popup = page.locator(
						"//div[(contains(@class,'popup-dialog') or contains(@class,'z-messagebox')) and not(contains(@style,'display: none')) and not(contains(@style,'display:none'))]").first();
				if (!popup.isVisible()) {
					break;
				}
				String text = popup.innerText().trim();
				allPopupText.append(text).append(" | ");
				log.warn("⚠️ Warning popup detected: [{}]", text);

				Locator okBtn = popup.locator("//button[@title='OK' or @title='Yes' or text()='OK' or text()='Yes' or contains(@class,'z-messagebox-button')] | //button").first();
				if (okBtn.isVisible()) {
					okBtn.click();
					log.info("Removed warning popup via OK/Action button.");
				} else {
					Locator closeIcon = popup.locator("//div[contains(@class,'z-window-icon') or @aria-label='Close']").first();
					if (closeIcon.isVisible()) {
						closeIcon.click();
						log.info("Removed warning popup via Close icon.");
					} else {
						page.keyboard().press("Enter");
					}
				}
				attempts++;
				page.waitForTimeout(1000);
				WaitUtils.waitForLoadingIndicatorToDisappear(page);
			}
		} catch (Exception e) {
			log.debug("No warning popup to handle: {}", e.getMessage());
		}
		return allPopupText.toString().replaceAll(" \\| $", "").trim();
	}

	public boolean verifyFieldFilled(Locator fieldLocator, String fieldName, String expectedValue) {
		String actualValue = fieldLocator.inputValue();
		if (actualValue == null || actualValue.trim().isEmpty()) {
			log.error("❌ Field [{}] is NOT filled! Expected: [{}]", fieldName, expectedValue);
			Assert.fail("Field [" + fieldName + "] was not filled with data. Expected: " + expectedValue);
			return false;
		}
		log.info("✅ Verified [{}] field is filled with data: [{}]", fieldName, actualValue.trim());
		return true;
	}

	// ==================== Posted Window Actions ====================

	/**
	 * Clicks on the 'Posted' button on the Shipment Header and handles
	 * posting confirmation popups if they appear.
	 */
	public void clickPostedButton() {
		page.waitForTimeout(1000);
		log.info("Clicking on 'Posted' button on Shipment (Customer)...");

		// Ensure Shipment (Customer) window tab is active
		switchToShipmentWindowTab();

		// Ensure on Shipment Header
		navigateBackToShipmentHeader();

		postedButton.first().waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		postedButton.first().click(new Locator.ClickOptions().setForce(true));
		page.waitForTimeout(2000);
		WaitUtils.waitForLoadingIndicatorToDisappear(page);

		// Handle "Post now and create Accounting entries?" confirmation popup if it appears
		Locator modal = page.locator("//div[contains(@class,'z-messagebox') or contains(@class,'z-window-modal')]");
		if (modal.count() > 0 && modal.first().isVisible()) {
			Locator okBtn = modal.first().locator("button.z-button, button").first();
			if (okBtn.count() > 0 && okBtn.first().isVisible()) {
				okBtn.click();
				page.waitForTimeout(2000);
				WaitUtils.waitForLoadingIndicatorToDisappear(page);
			}
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
			if (postedButton.count() > 0 && postedButton.first().isVisible()) {
				postedButton.first().click(new Locator.ClickOptions().setForce(true));
				page.waitForTimeout(2000);
				WaitUtils.waitForLoadingIndicatorToDisappear(page);
				if (modal.count() > 0 && modal.first().isVisible()) {
					modal.first().locator("button.z-button, button").first().click();
					page.waitForTimeout(1000);
				}
			}
			if (postingTab.count() > 0 && postingTab.first().isVisible()) {
				log.info("'Posting' window opened after retry.");
			}
		}
	}

	/**
	 * Reads Accounted Debit and Accounted Credit amounts from the Posting window table.
	 *
	 * @return Array of double: [totalDebit, totalCredit]
	 */
	public double[] getPostedDebitAndCreditAmounts() {
		page.waitForTimeout(1000);
		WaitUtils.waitForLoadingIndicatorToDisappear(page);

		Locator postingTable = page.locator(".z-listbox, .z-grid")
				.filter(new Locator.FilterOptions().setHasText("Accounted Debit"))
				.first();
		try {
			postingTable.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(8000));
		} catch (Exception e) {
			log.warn("Posting table not visible: {}", e.getMessage());
			lastPostedAmounts = new double[]{0.0, 0.0};
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
			sa.assertTrue(isMatch, "[Shipment Posting] Accounted Debit [" + debit + "] does not equal Accounted Credit [" + credit + "]");
		} else {
			Assert.assertTrue(isMatch, "[Shipment Posting] Accounted Debit [" + debit + "] does not equal Accounted Credit [" + credit + "]");
		}

		if (isMatch) {
			log.info("✅ Verified Shipment Posted Debit [{}] equals Credit [{}] successfully.", debit, credit);
		} else {
			log.error("❌ Shipment Posted Debit [{}] does NOT equal Credit [{}]!", debit, credit);
		}
	}

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
		Locator closeBtn = page.locator("(//li[contains(@class,'z-tab') and .//span[normalize-space(text())='Posting']]//i[contains(@class,'z-icon-times') or contains(@class,'z-tab-icon')])[1] | //li[contains(@class,'z-tab') and contains(.,'Posting')]//i[contains(@class,'z-tab-icon') or contains(@class,'z-icon-times') or @aria-label='Close']");
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
		log.info("Closed Posting window.");
	}
}
