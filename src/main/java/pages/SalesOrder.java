package pages;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

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
 * Page Object for Sales Order Window and Order Lines in iDempiere ERP.
 */
public class SalesOrder {

	private static final Logger log = LogManager.getLogger(SalesOrder.class);
	private final Page page;

	// Sales Order Header Locators
	private final Locator orgInput;
	private final Locator targetDocTypeInput;
	private final Locator descInput;
	private final Locator bPartnerInput;
	private final Locator salesRepInput;
	private final Locator warehouseInput;
	private final Locator docNoInput;
	private final Locator dateOrderedInput;
	private final Locator partnerLocationInput;
	private final Locator invoiceLocationInput;
	private final Locator currencyInput;
	private final Locator docStatusInput;
	private final Locator totalLinesAmt;
	private final Locator grandTotalAmt;

	// Menu Search Locators
	private final Locator menuSearchInput;
	private final Locator searchSOWindowItem;

	// Breadcrumb & Line Tab Locators
	private final Locator orderLineTab;
	private final Locator orderHeaderBreadcrumb;

	// Order Line Fields
	private final Locator productInput;
	private final Locator qtyEnteredInput;
	private final Locator qtyOrderedInput;
	private final Locator freeQtyInput;
	private final Locator totalQtyInput;
	private final Locator lineNoInput;
	private final Locator priceEnteredInput;
	private final Locator priceListInput;
	private final Locator taxInput;
	private final Locator lineNetAmtInput;
	private final Locator descriptionTextarea;
	private final Locator nextRecordBtn;

	// Popups / Modals
	private final Locator inventoryWarningPopupOkButton;
	private final Locator productInfoModal;
	private final Locator productInfoCancelButton;

	private double lastLineNetAmt = 0.0;
	private double lastTaxPercentage = 0.0;
	private static final ThreadLocal<String> lastSODocNo = ThreadLocal.withInitial(() -> "");

	public SalesOrder(Page page) {
		this.page = page;

		// Header Locators
		this.orgInput = page.locator("//span[@instancename='C_Order0AD_Org_ID']//input");
		this.targetDocTypeInput = page.locator("//span[@instancename='C_Order0C_DocTypeTarget_ID']//input");
		this.descInput = page.locator("//textarea[@instancename='C_Order0Description']");
		this.bPartnerInput = page.locator("//div[@instancename='C_Order0C_BPartner_ID']//input");
		this.salesRepInput = page.locator("//span[@instancename='C_Order0SalesRep_ID']//input");
		this.warehouseInput = page.locator("//span[@instancename='C_Order0M_Warehouse_ID']//input");
		this.docNoInput = page.locator("//input[@instancename='C_Order0DocumentNo']");
		this.dateOrderedInput = page
				.locator("//span[@instancename='C_Order0DateOrdered']//input[not(contains(@class,'z-timebox-input'))]");
		this.partnerLocationInput = page.locator("//span[@instancename='C_Order0C_BPartner_Location_ID']//input");
		this.invoiceLocationInput = page.locator("//span[@instancename='C_Order0Bill_Location_ID']//input");
		this.currencyInput = page.locator("//span[@instancename='C_Order0C_Currency_ID']//input");
		this.docStatusInput = page.locator("//span[@instancename='C_Order0DocStatus']//input");
		this.totalLinesAmt = page.locator("(//div[@instancename='C_Order0TotalLines']//input)[1]");
		this.grandTotalAmt = page.locator("(//div[@instancename='C_Order0GrandTotal']//input)[1]");

		// Menu Search
		this.menuSearchInput = page.locator("//input[@class='z-bandbox-input']");
		this.searchSOWindowItem = page.locator(
				"//tr[contains(.,'Sales Order')]//a[@title='New'] | //td[@title='Enter and change sales orders']/following-sibling::td//a[@title='New'] | //td[contains(.,'Sales Order')]/following-sibling::td//a[@title='New']");

		// Breadcrumb & Tabs
		this.orderLineTab = page.locator(
				"//span[text()='Order Line'] | //div[contains(@class,'z-tab-button-text') and text()='Order Line']");
		this.orderHeaderBreadcrumb = page.locator(
				"//div[@instancename='breadcrumb']//a[text()='Order' or text()='Sales Order'] | (//div[@instancename='breadcrumb']//a[contains(@class,'z-a')])[1]");

		// Order Line Locators
		this.productInput = page.locator("//div[@instancename='C_OrderLine0M_Product_ID']//input");
		this.qtyEnteredInput = page.locator("(//div[@instancename='C_OrderLine0QtyEntered']//input)[1]");
		this.qtyOrderedInput = page.locator("(//div[@instancename='C_OrderLine0QtyOrdered']//input)[1]");
		this.freeQtyInput = page.locator(
				"(//div[@instancename='C_OrderLine0PHX_QtyFree']//input)[1] | //span[@instancename='C_OrderLine0PHX_QtyFree']//input | //input[@instancename='C_OrderLine0PHX_QtyFree']");
		this.totalQtyInput = page.locator(
				"(//div[@instancename='C_OrderLine0TotalQty']//input)[1] | //span[@instancename='C_OrderLine0TotalQty']//input | //input[@instancename='C_OrderLine0TotalQty']");
		this.lineNoInput = page.locator(
				"(//div[@instancename='C_OrderLine0Line']//input)[1] | //input[@instancename='C_OrderLine0Line']");
		this.priceEnteredInput = page.locator("(//div[@instancename='C_OrderLine0PriceEntered']//input)[1]");
		this.priceListInput = page.locator("(//div[@instancename='C_OrderLine0PriceList']//input)[1]");
		this.taxInput = page.locator("//span[@instancename='C_OrderLine0C_Tax_ID']//input");
		this.lineNetAmtInput = page.locator("(//div[@instancename='C_OrderLine0LineNetAmt']//input)[1]");
		this.descriptionTextarea = page.locator("//textarea[@instancename='C_OrderLine0Description']");
		this.nextRecordBtn = page.locator(
				"//div[contains(@class,'adwindow-detailpane') or contains(@class,'z-tabpanel')]//a[contains(@title,'Next record') or contains(@title,'Next Record')] | //a[contains(@title,'Next record') or contains(@title,'Next Record')]");

		// Modal dialogs
		this.inventoryWarningPopupOkButton = page.locator(
				".popup-dialog button[title='OK'], .popup-dialog .btn-ok, //div[contains(@class,'popup-dialog')]//button[@title='OK']");
		this.productInfoModal = page.locator("//div[@instancename='infopanel']");
		this.productInfoCancelButton = page.locator("//div[@instancename='infopanel']//button[@title='Cancel']");
	}

	/**
	 * Navigates to the Sales Order window via menu search bandbox.
	 */
	public void navigateToSalesOrderWindow() {
		log.info("Navigating to Sales Order window via Menu Search...");
		menuSearchInput.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		menuSearchInput.fill("Sales Order");

		page.waitForTimeout(3000);
		WaitUtils.waitForLoadingIndicatorToDisappear(page);

		Locator targetItem = searchSOWindowItem.first();
		try {
			targetItem.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(5000));
			targetItem.click();
		} catch (Exception e) {
			log.warn("Searched Sales Order item not clicked, pressing Enter: {}", e.getMessage());
			menuSearchInput.press("Enter");
		}

		try {
			orgInput.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(25000));
			log.info("Sales Order window opened successfully.");
		} catch (Exception e) {
			log.warn("Waiting for Sales Order window orgInput timed out: {}", e.getMessage());
		}
		WaitUtils.waitForLoadingIndicatorToDisappear(page);
	}

	public void selectOrg(String orgName) {
		orgInput.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		orgInput.fill(orgName);
		orgInput.press("Tab");
		page.waitForTimeout(2000);
		WaitUtils.waitForLoadingIndicatorToDisappear(page);
		verifyFieldFilled(orgInput, "Organization", orgName);
	}

	public void selectOrg() {
		String org = DataManager.getData("so.org", "org");
		selectOrg(org);
	}

	public void selectTargetDocType(String docType) {
		page.waitForTimeout(1000);
		targetDocTypeInput
				.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		targetDocTypeInput.fill(docType);
		targetDocTypeInput.press("Tab");
		page.waitForTimeout(2000);
		WaitUtils.waitForLoadingIndicatorToDisappear(page);
		verifyFieldFilled(targetDocTypeInput, "Target Document Type", docType);
	}

	public void selectTargetDocType() {
		String docType = DataManager.getData("so.targetDocType", "targetDocType");
		selectTargetDocType(docType);
	}

	public void fillDescription(String description) {
		descInput.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		descInput.click();
		descInput.fill(description);
		log.info("Sales Order description filled: [{}]", description);
	}

	public void fillDescription() {
		String desc = DataManager.getData("so.description", "description");
		fillDescription(desc);
	}

	public void selectBPartner(String partnerName) {
		bPartnerInput.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		bPartnerInput.click();
		bPartnerInput.fill(partnerName);
		bPartnerInput.press("Tab");
		page.waitForTimeout(2000);
		WaitUtils.waitForLoadingIndicatorToDisappear(page);

		// Warning popup may display after selecting bpartner — fetch data and remove
		// popup
		String warning = handleWarningPopup();
		if (!warning.isEmpty()) {
			log.info("Fetched and dismissed Business Partner warning popup: [{}]", warning);
		}

		verifyFieldFilled(bPartnerInput, "Business Partner", partnerName);
	}

	public void selectBPartner() {
		String bPartner = DataManager.getData("so.bPartner", "bPartner");
		selectBPartner(bPartner);
	}

	public void selectSalesRep(String repName) {
		handleWarningPopup();
		salesRepInput.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		salesRepInput.fill(repName);
		salesRepInput.press("Tab");
		page.waitForTimeout(2000);
		WaitUtils.waitForLoadingIndicatorToDisappear(page);
		verifyFieldFilled(salesRepInput, "Sales Representative", repName);
	}

	public void selectSalesRep() {
		String salesRep = DataManager.getData("so.salesRep", "salesRep");
		selectSalesRep(salesRep);
	}

	public void selectWarehouse(String warehouseName) {
		handleWarningPopup();
		warehouseInput.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		warehouseInput.fill(warehouseName);
		warehouseInput.press("Tab");
		page.waitForTimeout(2000);
		WaitUtils.waitForLoadingIndicatorToDisappear(page);
		handleWarningPopup();
		verifyFieldFilled(warehouseInput, "Warehouse", warehouseName);
	}

	public void selectWarehouse() {
		String warehouse = DataManager.getData("so.warehouse", "warehouse");
		selectWarehouse(warehouse);
	}

	/**
	 * Generic popup handler: checks for any warning dialog (.popup-dialog,
	 * .z-messagebox, or modal window),
	 * extracts its text, logs it, and dismisses/removes the popup via OK button,
	 * Close icon, or Enter key.
	 *
	 * @return extracted popup text, or empty string if no popup appeared
	 */
	public String handleWarningPopup() {
		StringBuilder allPopupText = new StringBuilder();
		try {
			int attempts = 0;
			while (attempts < 3) {
				page.waitForTimeout(500);
				Locator popup = page.locator(
						"//div[(contains(@class,'popup-dialog') or contains(@class,'z-messagebox') or contains(@class,'z-window-modal')) and not(contains(@style,'display: none')) and not(contains(@style,'display:none'))]")
						.first();
				if (!popup.isVisible()) {
					break;
				}
				String popupText = popup.innerText().trim();
				if (!popupText.isEmpty()) {
					log.warn("⚠️ Warning popup detected: [{}]", popupText);
					if (allPopupText.length() > 0) {
						allPopupText.append(" | ");
					}
					allPopupText.append(popupText);
				}

				Locator okBtn = page.locator(
						"//div[contains(@class,'popup-dialog') or contains(@class,'z-messagebox') or contains(@class,'z-window-modal')]//button[not(contains(@class,'z-window-close'))]")
						.first();
				Locator closeIcon = page.locator(
						"//div[contains(@class,'popup-dialog') or contains(@class,'z-messagebox') or contains(@class,'z-window-modal')]//*[contains(@class,'z-window-close')]")
						.first();

				try {
					if (okBtn.isVisible()) {
						okBtn.click(new Locator.ClickOptions().setTimeout(3000));
						log.info("Removed warning popup via OK/Action button.");
					} else if (closeIcon.isVisible()) {
						closeIcon.click(new Locator.ClickOptions().setTimeout(3000));
						log.info("Removed warning popup via Close icon.");
					} else {
						page.keyboard().press("Enter");
						log.info("Dismissed warning popup via Enter key.");
					}
				} catch (Exception clickEx) {
					log.warn("Failed clicking popup button directly, trying Enter key: {}", clickEx.getMessage());
					page.keyboard().press("Enter");
				}

				page.waitForTimeout(1000);
				try {
					Locator modalMask = page.locator(".z-modal-mask");
					if (modalMask.count() > 0 && modalMask.first().isVisible()) {
						modalMask.first().waitFor(
								new Locator.WaitForOptions().setState(WaitForSelectorState.HIDDEN).setTimeout(2000));
					}
				} catch (Exception ignored) {
				}
				WaitUtils.waitForLoadingIndicatorToDisappear(page);
				attempts++;
			}
		} catch (Exception e) {
			log.warn("Exception while handling warning popup: {}", e.getMessage());
		}
		return allPopupText.toString();
	}

	public String getSODocNo() {
		docNoInput.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		String docNo = docNoInput.inputValue().trim();
		lastSODocNo.set(docNo);
		log.info("Captured Sales Order Document No: [{}]", docNo);
		return docNo;
	}

	public static String getLastSODocNo() {
		return lastSODocNo.get();
	}

	public static void setLastSODocNo(String docNo) {
		lastSODocNo.set(docNo);
	}

	/**
	 * Validates Date Ordered field matches today's date.
	 */
	public void verifyDateFields() {
		dateOrderedInput.first()
				.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		String actualDate = dateOrderedInput.first().inputValue().trim();
		String expectedDate = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

		SoftAssert sa = base.BaseTest.getSoftAssert();
		boolean isMatch = actualDate.equals(expectedDate);
		if (sa != null) {
			sa.assertTrue(isMatch, "[Sales Order Header] Date Ordered mismatch. Actual: [" + actualDate
					+ "], Expected: [" + expectedDate + "]");
		}
		if (isMatch) {
			log.info("Verified Date Ordered: [{}]", actualDate);
		} else {
			log.warn("Date Ordered mismatch: Actual [{}], Expected [{}]", actualDate, expectedDate);
		}
	}

	/**
	 * Validates Partner Location and Invoice Location fields are populated and
	 * match test data.
	 */
	public void verifyBPLocationFields() {
		partnerLocationInput
				.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		String pLoc = partnerLocationInput.inputValue().trim();
		String invLoc = invoiceLocationInput.inputValue().trim();

		String expectedPLoc = DataManager.getOrDefault("so.bPLocation", "");
		String expectedInvLoc = DataManager.getOrDefault("so.bpInvoiceLocation", "");

		SoftAssert sa = base.BaseTest.getSoftAssert();
		if (sa != null) {
			sa.assertTrue(!pLoc.isEmpty(), "[Sales Order Header] Partner Location is empty.");
			sa.assertTrue(!invLoc.isEmpty(), "[Sales Order Header] Invoice Location is empty.");
			if (!expectedPLoc.isEmpty()) {
				sa.assertEquals(pLoc, expectedPLoc,
						"[Sales Order Header] Partner Location mismatch — Expected: [" + expectedPLoc + "] Actual: ["
								+ pLoc + "]");
			}
			if (!expectedInvLoc.isEmpty()) {
				sa.assertEquals(invLoc, expectedInvLoc,
						"[Sales Order Header] Invoice Location mismatch — Expected: [" + expectedInvLoc + "] Actual: ["
								+ invLoc + "]");
			}
		}
		log.info("Verified Partner Location: [{}], Invoice Location: [{}]", pLoc, invLoc);
	}

	public void verifyCurrency(String expectedCurrency) {
		currencyInput.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		String actualCurrency = currencyInput.inputValue().trim();
		SoftAssert sa = base.BaseTest.getSoftAssert();
		boolean isMatch = actualCurrency.equalsIgnoreCase(expectedCurrency.trim());
		if (sa != null) {
			sa.assertTrue(isMatch, "[Sales Order Header] Currency mismatch. Actual: [" + actualCurrency
					+ "], Expected: [" + expectedCurrency + "]");
		}
		log.info("Verified Currency: [{}]", actualCurrency);
	}

	public void verifyCurrency() {
		String expectedCurrency = DataManager.getData("so.currency", "currency");
		verifyCurrency(expectedCurrency);
	}

	public void verifyAmtFieldsBeforeLines() {
		totalLinesAmt.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		String total = totalLinesAmt.inputValue().trim();
		String grand = grandTotalAmt.inputValue().trim();
		log.info("Sales Order amounts before lines — Total Lines: [{}], Grand Total: [{}]", total, grand);
	}

	// ==================== ORDER LINE TAB METHODS ====================

	public void clickOnOrderLineTab() {
		orderLineTab.first()
				.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		orderLineTab.first().click();
		page.waitForTimeout(3000);
		WaitUtils.waitForLoadingIndicatorToDisappear(page);
		ensureOrderLineFormView();
		log.info("Navigated to 'Order Line' tab.");
	}

	/**
	 * Toggles grid view to form view if table/grid view is displayed by default.
	 */
	public void ensureOrderLineFormView() {
		try {
			page.waitForTimeout(2000);
			if (page.locator("(//div[@class='z-frozen'])[1]").isVisible()) {
				Locator toggleBtn = page
						.locator("//a[@title='Grid Toggle    Alt+T'] | //a[contains(@title,'Shift+Alt+T')]").first();
				toggleBtn.click();
				page.waitForTimeout(2000);
				WaitUtils.waitForLoadingIndicatorToDisappear(page);
			}
		} catch (Exception e) {
			log.warn("Could not check/toggle Order Line grid view: {}", e.getMessage());
		}
	}

	/**
	 * Dismisses the 'No Inventory Available' popup dialog if it appears.
	 */
	public void handleInventoryWarningPopup() {
		try {
			page.waitForTimeout(1000);
			if (inventoryWarningPopupOkButton.first().isVisible()) {
				log.info("Detected 'No Inventory Available' popup dialog. Clicking OK to proceed...");
				inventoryWarningPopupOkButton.first().click();
				page.waitForTimeout(1500);
				WaitUtils.waitForLoadingIndicatorToDisappear(page);
			}
		} catch (Exception ignored) {
		}
	}

	public void enterProduct(String productName) {
		productInput.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		productInput.click();

		// Click visible Product info button (with cart / z-icon-Product)
		Locator cartBtn = page.locator(
				"//div[@instancename='C_OrderLine0M_Product_ID']//button[.//i[contains(@class,'z-icon-Product')]] | " +
						"//div[@instancename='C_OrderLine0M_Product_ID']//button[not(contains(@style,'display:none')) and not(contains(@style,'display: none'))]")
				.first();

		Locator infoModal = page.locator(
				"//div[@instancename='infopanel'] | " +
						"//div[contains(@class,'z-window-modal')]")
				.first();

		cartBtn.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(5000));
		cartBtn.click();
		infoModal.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));

		WaitUtils.waitForLoadingIndicatorToDisappear(page);
		log.info("Info Product modal opened successfully.");

		// 1. Make Price List Version blank
		try {
			Locator priceListVersionInput = page.locator(
					"//div[contains(@class,'info-panel') or @instancename='infopanel']//td[contains(.,'Price List Version')]/following-sibling::td[1]//input")
					.first();
			if (priceListVersionInput.isVisible()) {
				priceListVersionInput.click();
				priceListVersionInput.press("Control+A");
				priceListVersionInput.press("Backspace");
				priceListVersionInput.fill("");
				priceListVersionInput.press("Tab");
				page.waitForTimeout(300);
				log.info("Cleared 'Price List Version' in Info Product modal.");
			} else {
				log.warn("Price List Version input not found in Info Product modal.");
			}
		} catch (Exception ex) {
			log.warn("Could not clear Price List Version: {}", ex.getMessage());
		}

		// 2. Provide search key as '301DICL01'
		String searchKey = DataManager.getOrDefault("so.searchKey", "");
		if (searchKey.isEmpty() && productName.contains("_")) {
			searchKey = productName.substring(productName.lastIndexOf("_") + 1).trim();
		}
		if (searchKey.isEmpty()) {
			searchKey = "301DICL01";
		}

		try {
			Locator searchKeyInput = page.locator(
					"//div[contains(@class,'info-panel') or @instancename='infopanel']//td[contains(.,'Search Key')]/following-sibling::td[1]//input")
					.first();
			searchKeyInput
					.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(5000));
			searchKeyInput.click();
			searchKeyInput.press("Control+A");
			searchKeyInput.fill(searchKey);
			searchKeyInput.press("Tab");
			page.waitForTimeout(300);
			log.info("Provided search key [{}] in Info Product modal.", searchKey);
		} catch (Exception ex) {
			log.warn("Could not fill search key: {}", ex.getMessage());
		}

		// 3. Hit Requery button (title='ReQuery' or class='btn-refresh')
		try {
			Locator requeryBtn = page.locator(
					"//div[contains(@class,'info-panel') or @instancename='infopanel']//button[@title='ReQuery' or contains(@class,'btn-refresh')]")
					.first();
			requeryBtn.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(5000));
			requeryBtn.click();
			log.info("Hit ReQuery button in Info Product modal.");
			WaitUtils.waitForLoadingIndicatorToDisappear(page);
		} catch (Exception ex) {
			log.warn("Could not click Requery button: {}", ex.getMessage());
		}

		// 4. Select the returned product record
		try {
			Locator targetRow = page.locator(
					"//div[contains(@class,'info-panel') or @instancename='infopanel']//div[contains(@class,'z-listbox')]//tr[contains(@class,'z-listitem') and (contains(.,'"
							+ searchKey + "') or contains(.,'DICLOVERGE'))] | " +
							"(//div[contains(@class,'info-panel') or @instancename='infopanel']//div[contains(@class,'z-listbox')]//tr[contains(@class,'z-listitem')])[1]")
					.first();
			targetRow.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(15000));
			String rowText = targetRow.innerText().trim().replace("\n", " ");
			log.info("Found product row: [{}]", rowText);

			Locator checkable = targetRow.locator(".z-listitem-checkable").first();
			if (checkable.isVisible()) {
				checkable.click();
				log.info("Clicked checkable box for product row.");
			} else {
				targetRow.click();
				log.info("Clicked product row directly.");
			}
			page.waitForTimeout(300);

			Locator okBtn = page.locator(
					"//div[contains(@class,'info-panel') or @instancename='infopanel']//button[@title='OK' or contains(@class,'btn-ok')]")
					.first();

			if (okBtn.isVisible() && okBtn.isEnabled()) {
				okBtn.click();
				log.info("Selected product and clicked OK button in Info Product modal.");
			} else {
				log.info("OK button not enabled, double-clicking product row cell.");
				targetRow.locator("td").nth(2).dblclick();
			}
			try {
				infoModal.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.HIDDEN).setTimeout(8000));
			} catch (Exception ignored) {
			}
			WaitUtils.waitForLoadingIndicatorToDisappear(page);
		} catch (Exception ex) {
			log.error("Failed to select product from Info Product modal: {}", ex.getMessage());
			Assert.fail("Failed to select product [" + productName + "] from Info Product modal: " + ex.getMessage());
		}

		// 5. Dismiss any warning popup that may display after selecting product
		String warning = handleWarningPopup();
		if (!warning.isEmpty()) {
			log.info("Fetched and dismissed Product warning popup: [{}]", warning);
		}

		// 6. Verify product field filled
		verifyFieldFilled(productInput, "Product", productName);

		if (descriptionTextarea.isVisible()) {
			descriptionTextarea.click();
		}

		// Check if Price and List Price need manual population
		handlePriceAndPriceListAutoSet();
	}

	public void enterProduct() {
		String product = DataManager.getData("so.product", "product");
		enterProduct(product);
	}

	public void enterQtyEntered(String qty) {
		qtyEnteredInput.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		qtyEnteredInput.click();
		qtyEnteredInput.fill(qty);
		qtyEnteredInput.press("Tab");
		page.waitForTimeout(300);
		WaitUtils.waitForLoadingIndicatorToDisappear(page);

		handleWarningPopup();
		verifyFieldFilled(qtyEnteredInput, "QtyEntered", qty);
	}

	public void enterQtyEntered() {
		String qty = DataManager.getData("so.qtyEntered", "qtyEntered");
		enterQtyEntered(qty);
	}

	public void enterFreeQty(String freeQty) {
		freeQtyInput.first()
				.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		freeQtyInput.first().click();
		freeQtyInput.first().press("Control+A");
		freeQtyInput.first().fill(freeQty);
		freeQtyInput.first().press("Tab");
		page.waitForTimeout(300);
		WaitUtils.waitForLoadingIndicatorToDisappear(page);
		handleWarningPopup();
		verifyFieldFilled(freeQtyInput.first(), "FreeQty", freeQty);
		log.info("Entered Free Qty: [{}]", freeQty);
	}

	public void enterFreeQty() {
		String freeQty = DataManager.getOrDefault("so.freeQty", "30");
		enterFreeQty(freeQty);
	}

	public void verifyTotalQty(String expectedTotalQty) {
		page.waitForTimeout(300);
		WaitUtils.waitForLoadingIndicatorToDisappear(page);
		totalQtyInput.first()
				.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		String actualTotalQty = totalQtyInput.first().inputValue().trim();

		boolean isMatch = actualTotalQty.equalsIgnoreCase(expectedTotalQty.trim());
		try {
			double actual = Double.parseDouble(actualTotalQty.replace(",", ""));
			double expected = Double.parseDouble(expectedTotalQty.replace(",", ""));
			isMatch = Double.compare(actual, expected) == 0;
		} catch (Exception ignored) {
		}

		SoftAssert sa = base.BaseTest.getSoftAssert();
		if (sa != null) {
			sa.assertTrue(isMatch, "[Sales Order Line] Total Qty [" + actualTotalQty + "] does not match expected ["
					+ expectedTotalQty + "]");
		}
		if (isMatch) {
			log.info("Verified Sales Order Line Total Qty: [{}]", actualTotalQty);
		} else {
			log.warn("Sales Order Line Total Qty mismatch: Actual [{}], Expected [{}]", actualTotalQty,
					expectedTotalQty);
			Assert.fail("Total Qty mismatch. Actual: " + actualTotalQty + ", Expected: " + expectedTotalQty);
		}
	}

	public void verifyTotalQty() {
		String expectedTotal = DataManager.getOrDefault("so.totalQty", "130");
		verifyTotalQty(expectedTotal);
	}

	public void verifySOQty(String expectedQty) {
		Locator qtyField = qtyEnteredInput;
		try {
			if (qtyOrderedInput.count() > 0 && qtyOrderedInput.first().isVisible()) {
				qtyField = qtyOrderedInput.first();
			}
		} catch (Exception ignored) {
		}
		qtyField.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		String actualQty = qtyField.inputValue().trim();

		boolean isMatch = actualQty.equalsIgnoreCase(expectedQty.trim());
		try {
			double actual = Double.parseDouble(actualQty.replace(",", ""));
			double expected = Double.parseDouble(expectedQty.replace(",", ""));
			isMatch = Double.compare(actual, expected) == 0;
		} catch (Exception ignored) {
		}

		SoftAssert sa = base.BaseTest.getSoftAssert();
		if (sa != null) {
			sa.assertTrue(isMatch,
					"[Sales Order Line] Qty Ordered [" + actualQty + "] does not match expected [" + expectedQty + "]");
		}
		log.info("Verified Sales Order Line Qty: [{}]", actualQty);
	}

	public void verifySOQty() {
		String expectedQty = qtyEnteredInput.inputValue().trim();
		if (expectedQty.isEmpty()) {
			expectedQty = DataManager.getData("so.qtyEntered", "qtyEntered");
		}
		verifySOQty(expectedQty);
	}

	public boolean isPriceAutoPopulated(Locator locator) {
		try {
			if (!locator.isVisible()) {
				return false;
			}
			String val = locator.inputValue();
			if (val == null || val.trim().isEmpty()) {
				return false;
			}
			double d = Double.parseDouble(val.trim().replace(",", ""));
			return d > 0.0;
		} catch (Exception e) {
			return false;
		}
	}

	public void enterPriceEntered(String price) {
		page.waitForTimeout(1000);
		priceEnteredInput
				.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		priceEnteredInput.click();
		priceEnteredInput.press("Control+A");
		priceEnteredInput.fill(price);
		priceEnteredInput.press("Tab");
		page.waitForTimeout(2000);
		WaitUtils.waitForLoadingIndicatorToDisappear(page);
		verifyFieldFilled(priceEnteredInput, "PriceEntered", price);
	}

	public void enterPriceEntered() {
		String price = DataManager.getData("so.priceEntered", "priceEntered");
		enterPriceEntered(price);
	}

	public void enterPriceList(String priceList) {
		page.waitForTimeout(1000);
		priceListInput.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		priceListInput.click();
		priceListInput.press("Control+A");
		priceListInput.fill(priceList);
		priceListInput.press("Tab");
		page.waitForTimeout(2000);
		WaitUtils.waitForLoadingIndicatorToDisappear(page);
		verifyFieldFilled(priceListInput, "PriceList", priceList);
	}

	public void enterPriceList() {
		String priceListVal = DataManager.getData("so.priceList", "priceList");
		enterPriceList(priceListVal);
	}

	public void handlePriceAndPriceListAutoSet() {
		page.waitForTimeout(1500);
		WaitUtils.waitForLoadingIndicatorToDisappear(page);

		if (!isPriceAutoPopulated(priceEnteredInput)) {
			enterPriceEntered();
		}
		if (!isPriceAutoPopulated(priceListInput)) {
			enterPriceList();
		}
	}

	public void selectTax(String taxName) {
		try {
			page.waitForTimeout(1000);
			taxInput.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));

			Locator comboItem = page.locator(
					"//div[contains(@class,'z-combobox-popup') and not(contains(@style,'display:none')) and not(contains(@style,'display: none'))]//li[contains(@class,'z-comboitem') and contains(.,'"
							+ taxName + "')] | //span[contains(@class,'z-comboitem-text') and contains(.,'" + taxName
							+ "')]")
					.first();

			taxInput.click();
			taxInput.press("Control+A");
			taxInput.fill(taxName);
			page.waitForTimeout(1000);

			if (comboItem.count() > 0 && comboItem.isVisible()) {
				comboItem.click();
				log.info("Clicked tax dropdown item: [{}]", taxName);
			} else {
				taxInput.press("Tab");
			}

			page.waitForTimeout(2000);
			WaitUtils.waitForLoadingIndicatorToDisappear(page);
			handleWarningPopup();

			String actualTax = taxInput.inputValue().trim();
			log.info("Actual Tax field value after selection: [{}] (Requested: [{}])", actualTax, taxName);
			verifyFieldFilled(taxInput, "Tax", taxName);

			String chosenTax = !actualTax.isEmpty() ? actualTax : taxName;
			if (chosenTax != null && !chosenTax.isEmpty()) {
				java.util.regex.Matcher m = java.util.regex.Pattern.compile("(\\d+(?:\\.\\d+)?)\\s*%")
						.matcher(chosenTax);
				double totalPercent = 0.0;
				boolean found = false;
				while (m.find()) {
					totalPercent += Double.parseDouble(m.group(1));
					found = true;
				}
				if (found) {
					lastTaxPercentage = totalPercent;
					log.info("Recorded line tax percentage: [{}%]", lastTaxPercentage);
				}
			}
		} catch (Exception e) {
			log.error("Failed to select tax [{}]: {}", taxName, e.getMessage());
			Assert.fail("Failed to select tax [" + taxName + "]: " + e.getMessage());
		}
	}

	public void selectTax() {
		String tax = DataManager.getData("so.taxType", "taxType");
		selectTax(tax);
	}

	public void verifyLineNetAmt() {
		try {
			if (lineNetAmtInput.count() > 0 && lineNetAmtInput.first().isVisible()) {
				String actualStr = lineNetAmtInput.first().inputValue().trim();
				lastLineNetAmt = Double.parseDouble(actualStr.replace(",", ""));
				log.info("Captured Line Net Amount from field: [{}]", lastLineNetAmt);
			} else {
				double price = Double
						.parseDouble(DataManager.getData("so.priceEntered", "priceEntered").replace(",", ""));
				double qty = Double.parseDouble(DataManager.getData("so.qtyEntered", "qtyEntered").replace(",", ""));
				lastLineNetAmt = price * qty;
				log.info("LineNetAmt input not visible on form, calculated from test data: [{}]", lastLineNetAmt);
			}
		} catch (Exception e) {
			log.warn("Could not determine Line Net Amount: {}", e.getMessage());
			double price = Double.parseDouble(DataManager.getData("so.priceEntered", "priceEntered").replace(",", ""));
			double qty = Double.parseDouble(DataManager.getData("so.qtyEntered", "qtyEntered").replace(",", ""));
			lastLineNetAmt = price * qty;
		}
	}

	public void navigateBackOnSOHeader() {
		orderHeaderBreadcrumb
				.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		orderHeaderBreadcrumb.click();
		page.waitForTimeout(1000);
		WaitUtils.waitForLoadingIndicatorToDisappear(page);
		log.info("Navigated back to Sales Order Header.");
	}

	/**
	 * After performing Prepare document action on Sales Order Header,
	 * navigates to the Order Line tab and verifies that an additional order line
	 * is created with the expected free quantity (e.g. 30).
	 *
	 * @param expectedFreeLineQty Expected Qty in the new order line (e.g. "30")
	 */
	public void verifySecondOrderLineAfterPrepare(String expectedFreeLineQty) {
		log.info("========== Verifying second Order Line created after Prepare action ==========");
		clickOnOrderLineTab();
		page.waitForTimeout(1000);
		WaitUtils.waitForLoadingIndicatorToDisappear(page);

		// If current view is grid, check number of rows
		Locator gridRows = page.locator(
				"//div[contains(@class,'adwindow-detailpane') or contains(@class,'z-tabpanel')]//div[contains(@class,'z-grid-body')]//tr[contains(@class,'z-row')]");
		if (gridRows.count() > 1) {
			log.info("Found [{}] rows in Order Line grid.", gridRows.count());
		}

		// Ensure form view to inspect line fields
		ensureOrderLineFormView();

		// Check initial line qty and line number
		String curQty = qtyEnteredInput.inputValue().trim();
		String curLine = lineNoInput.count() > 0 && lineNoInput.first().isVisible()
				? lineNoInput.first().inputValue().trim()
				: "";
		log.info("Initial Order Line displayed in form view — Line No: [{}], Qty: [{}]", curLine, curQty);

		// If currently on Line 10 (or first record), advance to next record (Line 20)
		boolean isSecondLine = false;
		double expQtyDbl = 0.0;
		try {
			expQtyDbl = Double.parseDouble(expectedFreeLineQty.replace(",", ""));
		} catch (Exception ignored) {
		}

		try {
			double curQtyDbl = Double.parseDouble(curQty.replace(",", ""));
			if (Double.compare(curQtyDbl, expQtyDbl) == 0 && (curLine.isEmpty() || !curLine.equals("10"))) {
				isSecondLine = true;
			}
		} catch (Exception ignored) {
		}

		if (!isSecondLine) {
			log.info("Currently on first order line. Navigating to second order line...");
			Locator nextBtn = page.locator(
					"//div[contains(@class,'adwindow-detailpane') or contains(@class,'z-tabpanel')]//a[contains(@title,'Next record') or contains(@title,'Next Record')] | (//a[contains(@title,'Next record') or contains(@title,'Next Record')])[1]");
			try {
				if (nextBtn.count() > 0 && nextBtn.first().isVisible()
						&& !Boolean.TRUE.equals(nextBtn.first().isDisabled())) {
					nextBtn.first().click();
				} else {
					page.keyboard().press("Shift+Alt+ArrowRight");
				}
			} catch (Exception ex) {
				log.warn("Clicking next button failed, pressing Shift+Alt+ArrowRight: {}", ex.getMessage());
				page.keyboard().press("Shift+Alt+ArrowRight");
			}

			page.waitForTimeout(1000);
			WaitUtils.waitForLoadingIndicatorToDisappear(page);
		}

		// Read the second order line details
		String secondLineNo = lineNoInput.count() > 0 && lineNoInput.first().isVisible()
				? lineNoInput.first().inputValue().trim()
				: "";
		String actualSecondLineQty = qtyEnteredInput.inputValue().trim();
		if (actualSecondLineQty.isEmpty() && qtyOrderedInput.count() > 0 && qtyOrderedInput.first().isVisible()) {
			actualSecondLineQty = qtyOrderedInput.first().inputValue().trim();
		}
		String product = productInput.count() > 0 && productInput.first().isVisible()
				? productInput.first().inputValue().trim()
				: "";

		log.info("Second Order Line details — Line No: [{}], Product: [{}], Qty: [{}]", secondLineNo, product,
				actualSecondLineQty);

		String lineTax = taxInput.count() > 0 && taxInput.first().isVisible() ? taxInput.first().inputValue().trim()
				: "";
		if (!lineTax.isEmpty()) {
			java.util.regex.Matcher m = java.util.regex.Pattern.compile("(\\d+(?:\\.\\d+)?)\\s*%").matcher(lineTax);
			double totalPercent = 0.0;
			boolean found = false;
			while (m.find()) {
				totalPercent += Double.parseDouble(m.group(1));
				found = true;
			}
			if (found) {
				lastTaxPercentage = totalPercent;
				log.info("Recorded line tax percentage from Order Line after Prepare: [{}%]", lastTaxPercentage);
			}
		}

		boolean isQtyMatch = false;
		try {
			double actual = Double.parseDouble(actualSecondLineQty.replace(",", ""));
			double expected = Double.parseDouble(expectedFreeLineQty.replace(",", ""));
			isQtyMatch = Double.compare(actual, expected) == 0;
		} catch (Exception e) {
			isQtyMatch = actualSecondLineQty.equalsIgnoreCase(expectedFreeLineQty.trim());
		}

		SoftAssert sa = base.BaseTest.getSoftAssert();
		if (sa != null) {
			sa.assertTrue(isQtyMatch,
					"[Prepare Action] Second Order Line Qty mismatch. Actual: [" + actualSecondLineQty
							+ "], Expected: [" + expectedFreeLineQty + "]");
			if (!secondLineNo.isEmpty()) {
				sa.assertTrue(!secondLineNo.equals("10"),
						"[Prepare Action] Expected second order line (e.g. Line 20), but Line No is still ["
								+ secondLineNo + "]");
			}
		}

		if (isQtyMatch) {
			log.info("✅ Verified: Another order line is successfully created with qty as [{}] (Line No: [{}])",
					actualSecondLineQty, secondLineNo);
		} else {
			log.error("❌ Second order line Qty mismatch! Actual: [{}], Expected: [{}]", actualSecondLineQty,
					expectedFreeLineQty);
			Assert.fail("Second order line Qty mismatch. Actual: " + actualSecondLineQty + ", Expected: "
					+ expectedFreeLineQty);
		}
	}

	public void verifySecondOrderLineAfterPrepare() {
		String freeQty = DataManager.getOrDefault("so.freeQty", "30");
		verifySecondOrderLineAfterPrepare(freeQty);
	}

	public double getExpectedSOLineAmount() {
		if (lastLineNetAmt > 0) {
			return lastLineNetAmt;
		}
		try {
			double price = Double.parseDouble(DataManager.getData("so.priceEntered", "priceEntered").replace(",", ""));
			double qty = Double.parseDouble(DataManager.getData("so.qtyEntered", "qtyEntered").replace(",", ""));
			return price * qty;
		} catch (Exception e) {
			log.warn("Could not calculate expected line amount from test data: {}", e.getMessage());
			return 0.0;
		}
	}

	public double getTaxPercentage() {
		if (lastTaxPercentage > 0.0) {
			return lastTaxPercentage;
		}
		String taxStr = "";
		try {
			if (taxInput.count() > 0 && taxInput.first().isVisible()
					&& !taxInput.first().inputValue().trim().isEmpty()) {
				taxStr = taxInput.first().inputValue().trim();
			} else {
				taxStr = DataManager.getData("so.taxType", "taxType");
			}
		} catch (Exception ignored) {
			taxStr = DataManager.getOrDefault("so.taxType", "12%");
		}

		if (taxStr != null && !taxStr.isEmpty()) {
			java.util.regex.Matcher m = java.util.regex.Pattern.compile("(\\d+(?:\\.\\d+)?)\\s*%").matcher(taxStr);
			double totalPercent = 0.0;
			boolean found = false;
			while (m.find()) {
				totalPercent += Double.parseDouble(m.group(1));
				found = true;
			}
			if (found) {
				return totalPercent;
			}
		}
		return 0.0;
	}

	public void verifyHeaderTotalLineAmt(double expectedLineAmt) {
		page.waitForTimeout(1000);
		WaitUtils.waitForLoadingIndicatorToDisappear(page);
		totalLinesAmt.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		String actualStr = totalLinesAmt.inputValue().trim();

		boolean isMatch = false;
		double actualAmt = 0.0;
		try {
			actualAmt = Double.parseDouble(actualStr.replace(",", ""));
			isMatch = Math.abs(actualAmt - expectedLineAmt) < 0.01;
		} catch (Exception e) {
			log.error("Failed to parse Header TotalLinesAmt: [{}]", actualStr, e);
		}

		SoftAssert sa = base.BaseTest.getSoftAssert();
		if (sa != null) {
			sa.assertTrue(isMatch,
					"[Sales Order Header] TotalLines amount [" + actualStr + "] does not match expected ["
							+ expectedLineAmt + "]");
		}
		log.info("Verified Header Total Lines Amount: [{}] (Expected: [{}])", actualStr, expectedLineAmt);
	}

	public void verifyHeaderTotalLineAmt() {
		verifyHeaderTotalLineAmt(getExpectedSOLineAmount());
	}

	public void verifyHeaderGrandTotalAmt(double expectedGrandTotal) {
		grandTotalAmt.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		String actualStr = grandTotalAmt.inputValue().trim();

		boolean isMatch = false;
		double actualAmt = 0.0;
		try {
			actualAmt = Double.parseDouble(actualStr.replace(",", ""));
			isMatch = Math.abs(actualAmt - expectedGrandTotal) < 0.01;
		} catch (Exception e) {
			log.error("Failed to parse Header GrandTotalAmt: [{}]", actualStr, e);
		}

		SoftAssert sa = base.BaseTest.getSoftAssert();
		if (sa != null) {
			sa.assertTrue(isMatch,
					"[Sales Order Header] GrandTotal amount [" + actualStr + "] does not match expected Grand Total ["
							+ expectedGrandTotal + "]");
		}
		log.info("Verified Header Grand Total Amount: [{}] (Expected: [{}])", actualStr, expectedGrandTotal);
	}

	public void verifyHeaderGrandTotalAmt() {
		double baseAmt = getExpectedSOLineAmount();
		double taxPct = getTaxPercentage();
		double expectedGrandTotal = baseAmt + (baseAmt * taxPct / 100.0);
		verifyHeaderGrandTotalAmt(expectedGrandTotal);
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
}
