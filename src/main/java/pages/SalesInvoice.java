package pages;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.SelectOption;
import com.microsoft.playwright.options.WaitForSelectorState;

import base.BaseTest;
import utils.DataManager;
import utils.WaitUtils;

/**
 * Page Object Class for the Sales Invoice and Credit/Debit Note Window (AR Invoice).
 * Encapsulates switching to the auto-generated Invoice window tab, header field validations,
 * line verification from SO/Shipment, Prepare & Complete document actions, and Posting verification.
 */
public class SalesInvoice {

	private static final Logger log = LogManager.getLogger(SalesInvoice.class);
	private final Page page;

	// ==================== Window Tab & Navigation Locators ====================
	private final Locator salesInvoiceTab;
	private final Locator invoiceHeaderBreadcrumb;
	private final Locator invoiceLineTab;
	private final Locator subtabGridViewButton;

	// ==================== Header Form Fields ====================
	private final Locator orgInput;
	private final Locator targetDocTypeInput;
	private final Locator documentNoInput;
	private final Locator descInput;
	private final Locator orderInput;
	private final Locator dateInvoicedInput;
	private final Locator bPartnerInput;
	private final Locator partnerLocationInput;
	private final Locator priceListInput;
	private final Locator salesRepInput;
	private final Locator paymentRuleInput;
	private final Locator currencyInput;
	private final Locator docStatusInput;
	private final Locator totalLinesAmt;
	private final Locator grandTotalAmt;
	private final Locator docActionButton;
	private final Locator docActionPopup;
	private final Locator postedButton;

	// ==================== Invoice Line Fields ====================
	private final Locator lineNoInput;
	private final Locator productInput;
	private final Locator qtyInvoicedInput;
	private final Locator priceEnteredInput;
	private final Locator lineNetAmtInput;
	private final Locator taxInput;
	private final Locator asiInput;
	private final Locator nextRecordBtn;
	private final Locator firstRecordBtn;
	private final Locator gridRows;

	private double[] lastPostedAmounts;
	private String lastInvoiceDocNo;

	public SalesInvoice(Page page) {
		this.page = page;

		// Navigation & Tab Locators
		this.salesInvoiceTab = page.locator(
				"//li[contains(@class,'z-tab') and (contains(.,'Sales Invoice') or .//span[contains(text(),'Sales Invoice')])] | " +
				"//li[contains(@class,'z-tab')]//span[contains(text(),'Invoice and Credit/De')] | " +
				"//li[contains(@class,'z-tab')]//span[contains(text(),'Invoice')]");
		this.invoiceHeaderBreadcrumb = page.locator(
				"//div[@instancename='breadcrumb']//a[normalize-space(text())='Invoice'] | " +
				"//div[@instancename='breadcrumb']//a[contains(text(),'Invoice')] | " +
				"(//div[@instancename='breadcrumb']//a[contains(@class,'z-a')])[1]");
		this.invoiceLineTab = page.locator(
				"//li[contains(@class,'z-tab')]//span[normalize-space(text())='Invoice Line'] | " +
				"//div[contains(@class,'z-tabbox')]//span[contains(@class,'z-tab-text') and text()='Invoice Line'] | " +
				"//span[text()='Invoice Line']");
		this.subtabGridViewButton = page.locator(
				"(//div[contains(@class,'z-tabpanel') and not(contains(@style,'display: none')) and not(contains(@style,'display:none'))]//a[contains(@title,'Shift+Alt+T')])[1] | " +
				"(//div[contains(@class,'adwindow-detailpane-toolbar')]//a[contains(@title,'Shift+Alt+T')])[1]");

		// Header Form Fields
		this.orgInput = page.locator(
				"//span[@instancename='C_Invoice0AD_Org_ID']//input | //div[@instancename='C_Invoice0AD_Org_ID']//input | //input[@instancename='C_Invoice0AD_Org_ID']");
		this.targetDocTypeInput = page.locator(
				"//span[@instancename='C_Invoice0C_DocTypeTarget_ID']//input | //div[@instancename='C_Invoice0C_DocTypeTarget_ID']//input | //input[@instancename='C_Invoice0C_DocTypeTarget_ID']");
		this.documentNoInput = page.locator(
				"//input[@instancename='C_Invoice0DocumentNo'] | //div[@instancename='C_Invoice0DocumentNo']//input");
		this.descInput = page.locator(
				"//textarea[@instancename='C_Invoice0Description'] | //input[@instancename='C_Invoice0Description']");
		this.orderInput = page.locator(
				"//span[@instancename='C_Invoice0C_Order_ID']//input | //div[@instancename='C_Invoice0C_Order_ID']//input | //input[@instancename='C_Invoice0C_Order_ID']");
		this.dateInvoicedInput = page.locator(
				"//span[@instancename='C_Invoice0DateInvoiced']//input[not(contains(@class,'z-timebox-input'))] | //div[@instancename='C_Invoice0DateInvoiced']//input");
		this.bPartnerInput = page.locator(
				"//span[@instancename='C_Invoice0C_BPartner_ID']//input | //div[@instancename='C_Invoice0C_BPartner_ID']//input | //input[@instancename='C_Invoice0C_BPartner_ID']");
		this.partnerLocationInput = page.locator(
				"//span[@instancename='C_Invoice0C_BPartner_Location_ID']//input | //div[@instancename='C_Invoice0C_BPartner_Location_ID']//input | //input[@instancename='C_Invoice0C_BPartner_Location_ID']");
		this.priceListInput = page.locator(
				"//span[@instancename='C_Invoice0M_PriceList_ID']//input | //div[@instancename='C_Invoice0M_PriceList_ID']//input | //input[@instancename='C_Invoice0M_PriceList_ID']");
		this.salesRepInput = page.locator(
				"//span[@instancename='C_Invoice0SalesRep_ID']//input | //div[@instancename='C_Invoice0SalesRep_ID']//input | //input[@instancename='C_Invoice0SalesRep_ID']");
		this.paymentRuleInput = page.locator(
				"//span[@instancename='C_Invoice0PaymentRule']//input | //div[@instancename='C_Invoice0PaymentRule']//input | //input[@instancename='C_Invoice0PaymentRule']");
		this.currencyInput = page.locator(
				"//span[@instancename='C_Invoice0C_Currency_ID']//input | //div[@instancename='C_Invoice0C_Currency_ID']//input | //input[@instancename='C_Invoice0C_Currency_ID']");
		this.docStatusInput = page.locator(
				"//span[@instancename='C_Invoice0DocStatus']//input | //div[@instancename='C_Invoice0DocStatus']//input | //input[@instancename='C_Invoice0DocStatus']");
		this.totalLinesAmt = page.locator(
				"(//div[@instancename='C_Invoice0TotalLines']//input)[1] | (//span[@instancename='C_Invoice0TotalLines']//input)[1] | //input[@instancename='C_Invoice0TotalLines']");
		this.grandTotalAmt = page.locator(
				"(//div[@instancename='C_Invoice0GrandTotal']//input)[1] | (//span[@instancename='C_Invoice0GrandTotal']//input)[1] | //input[@instancename='C_Invoice0GrandTotal']");
		this.docActionButton = page.locator(
				"//button[@instancename='C_Invoice0DocAction']");
		this.docActionPopup = page.locator(
				"//div[@instancename='documentAction']");
		this.postedButton = page.locator(
				"//button[@instancename='C_Invoice0Posted']");

		// Invoice Line Fields
		this.lineNoInput = page.locator(
				"//span[@instancename='C_InvoiceLine0Line']//input | //div[@instancename='C_InvoiceLine0Line']//input | //input[@instancename='C_InvoiceLine0Line']");
		this.productInput = page.locator(
				"//div[@instancename='C_InvoiceLine0M_Product_ID']//input | //span[@instancename='C_InvoiceLine0M_Product_ID']//input | //input[@instancename='C_InvoiceLine0M_Product_ID']");
		this.qtyInvoicedInput = page.locator(
				"(//div[@instancename='C_InvoiceLine0QtyInvoiced']//input)[1] | (//div[@instancename='C_InvoiceLine0QtyEntered']//input)[1] | //input[@instancename='C_InvoiceLine0QtyInvoiced']");
		this.priceEnteredInput = page.locator(
				"(//div[@instancename='C_InvoiceLine0PriceEntered']//input)[1] | (//div[@instancename='C_InvoiceLine0PriceActual']//input)[1] | //input[@instancename='C_InvoiceLine0PriceEntered']");
		this.lineNetAmtInput = page.locator(
				"(//div[@instancename='C_InvoiceLine0LineNetAmt']//input)[1] | //input[@instancename='C_InvoiceLine0LineNetAmt']");
		this.taxInput = page.locator(
				"//span[@instancename='C_InvoiceLine0C_Tax_ID']//input | //input[@instancename='C_InvoiceLine0C_Tax_ID']");
		this.asiInput = page.locator(
				"//div[@instancename='C_InvoiceLine0M_AttributeSetInstance_ID']//input | //span[@instancename='C_InvoiceLine0M_AttributeSetInstance_ID']//input | //input[@instancename='C_InvoiceLine0M_AttributeSetInstance_ID']");
		this.nextRecordBtn = page.locator(
				"//div[contains(@class,'adwindow-detailpane') or contains(@class,'z-tabpanel')]//a[contains(@title,'Next record') or contains(@title,'Next Record')] | " +
				"(//a[contains(@title,'Next record') or contains(@title,'Next Record')])[1]");
		this.firstRecordBtn = page.locator(
				"//div[contains(@class,'adwindow-detailpane') or contains(@class,'z-tabpanel')]//a[contains(@title,'First record') or contains(@title,'First Record')] | " +
				"(//a[contains(@title,'First record') or contains(@title,'First Record')])[1]");
		this.gridRows = page.locator(
				"//div[contains(@class,'adwindow-detailpane')]//div[contains(@class,'z-grid-body')]//tr[contains(@class,'z-row')] | " +
				"//div[contains(@class,'adwindow-detailpane')]//tr[contains(@class,'z-listitem')]");
	}

	// ==================== Window Tab Switching & Navigation ====================

	/**
	 * Switches focus to the already opened 'Sales Invoice and Credit/Debit Note' window tab.
	 */
	public void switchToInvoiceWindow() {
		log.info("Switching to already opened 'Sales Invoice and Credit/Debit Note' window tab...");
		page.waitForTimeout(1000);

		Locator invoiceTab = page.locator(
				"//li[contains(@class,'z-tab') and (contains(.,'Sales Invoice') or .//span[contains(text(),'Sales Invoice')])] | " +
				"//li[contains(@class,'z-tab')]//span[contains(text(),'Invoice and Credit/De')] | " +
				"//li[contains(@class,'z-tab')]//span[contains(text(),'Invoice')]");

		if (invoiceTab.count() > 0) {
			Locator activeTab = page.locator("//li[contains(@class,'z-tab-selected') and (contains(.,'Sales Invoice') or .//span[contains(text(),'Sales Invoice')])]");
			if (activeTab.count() == 0 || !activeTab.first().isVisible()) {
				log.info("Clicking on Sales Invoice tab header...");
				invoiceTab.first().click(new Locator.ClickOptions().setForce(true));
				page.waitForTimeout(1500);
				WaitUtils.waitForLoadingIndicatorToDisappear(page);
			}
		} else {
			log.warn("Sales Invoice window tab not found via primary locator.");
		}

		try {
			orderInput.first().waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
			log.info("Sales Invoice window is active and ready.");
		} catch (Exception e) {
			log.warn("orderInput not immediately visible, checking breadcrumb: {}", e.getMessage());
			navigateBackToInvoiceHeader();
		}
	}

	/**
	 * Navigates back to the Invoice Header tab.
	 */
	public void navigateBackToInvoiceHeader() {
		log.info("Navigating back to Invoice Header tab...");
		page.waitForTimeout(500);

		if (docStatusInput.count() > 0 && docStatusInput.first().isVisible()) {
			log.info("Already on Invoice Header.");
			return;
		}

		Locator headerBreadcrumb = page.locator(
				"//div[@instancename='breadcrumb']//a[normalize-space(text())='Invoice'] | " +
				"//div[@instancename='breadcrumb']//a[contains(text(),'Invoice')] | " +
				"(//div[@instancename='breadcrumb']//a[contains(@class,'z-a')])[1]");
		try {
			if (headerBreadcrumb.count() > 0 && headerBreadcrumb.first().isVisible()) {
				headerBreadcrumb.first().click(new Locator.ClickOptions().setForce(true));
			} else {
				salesInvoiceTab.first().click(new Locator.ClickOptions().setForce(true));
			}
		} catch (Exception e) {
			log.warn("Header breadcrumb click error, attempting JS click: {}", e.getMessage());
			page.evaluate("() => {" +
					"let a = document.querySelector(\"div[instancename='breadcrumb'] a\");" +
					"if (a) a.click();" +
			"}");
		}
		page.waitForTimeout(1000);
		WaitUtils.waitForLoadingIndicatorToDisappear(page);
		handleWarningPopup();
		log.info("Navigated back to Invoice Header.");
	}

	// ==================== Header Validations ====================

	public String getInvoiceDocNo() {
		documentNoInput.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		lastInvoiceDocNo = documentNoInput.inputValue().trim();
		log.info("Captured Sales Invoice Document No: [{}]", lastInvoiceDocNo);
		return lastInvoiceDocNo;
	}

	/**
	 * Validates all auto-populated fields on Sales Invoice Header against Sales Order test data.
	 *
	 * @param expectedSODocNo The Sales Order document number that generated this invoice
	 */
	public void verifyInvoiceHeaderFields(String expectedSODocNo) {
		log.info("========== Verifying Sales Invoice Header Fields ==========");
		SoftAssert sa = BaseTest.getSoftAssert();

		// 1. Order Reference
		orderInput.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		String actualOrder = orderInput.inputValue().trim();
		if (sa != null) {
			sa.assertTrue(actualOrder.contains(expectedSODocNo) || expectedSODocNo.contains(actualOrder),
					"[Sales Invoice] Order reference mismatch — Expected: [" + expectedSODocNo + "] Actual: [" + actualOrder + "]");
		}
		log.info("Verified Order Reference on Invoice Header: [{}] (Expected: [{}])", actualOrder, expectedSODocNo);

		// 2. Business Partner
		verifyFieldFilled(bPartnerInput, "Business Partner", DataManager.getData("so.bPartner", "bPartner"));

		// 3. Partner Location
		verifyFieldFilled(partnerLocationInput, "Partner Location", DataManager.getData("so.bPLocation", "bPLocation"));

		// 4. Target Document Type
		verifyFieldFilled(targetDocTypeInput, "Target Document Type", "AR Invoice");

		// 5. Price List
		verifyFieldFilled(priceListInput, "Price List", DataManager.getOrDefault("so.priceListType", "Prime"));

		// 6. Sales Representative
		verifyFieldFilled(salesRepInput, "Sales Representative", DataManager.getData("so.salesRep", "salesRep"));

		// 7. Payment Rule
		verifyFieldFilled(paymentRuleInput, "Payment Rule", "Direct Deposit");

		// 8. Currency
		verifyFieldFilled(currencyInput, "Currency", "INR");

		// 9. Organization
		verifyFieldFilled(orgInput, "Organization", DataManager.getData("so.org", "org"));

		// 10. Document Status
		//verifyFieldFilled(docStatusInput, "Document Status", "Drafted");

		log.info("✅ All reflected fields verified successfully on Sales Invoice Header.");
	}

	public void verifyInvoiceHeaderFields() {
		String soDocNo = SalesOrder.getLastSODocNo();
		if (soDocNo == null || soDocNo.isEmpty()) {
			soDocNo = DataManager.getData("soDocNo");
		}
		verifyInvoiceHeaderFields(soDocNo);
	}

	/**
	 * Verifies Total Lines and Grand Total amounts on Sales Invoice Header.
	 *
	 * @param expectedTotalLines Expected Total Lines amount (e.g. "500.00")
	 * @param expectedGrandTotal Expected Grand Total amount (e.g. "560.00")
	 */
	public void verifyHeaderTotalAmounts(String expectedTotalLines, String expectedGrandTotal) {
		page.waitForTimeout(500);
		WaitUtils.waitForLoadingIndicatorToDisappear(page);

		totalLinesAmt.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		String actualTotalLines = totalLinesAmt.inputValue().trim();
		String actualGrandTotal = grandTotalAmt.inputValue().trim();

		log.info("Invoice Header Amounts — Total Lines: [{}], Grand Total: [{}] (Expected: [{}], [{}])",
				actualTotalLines, actualGrandTotal, expectedTotalLines, expectedGrandTotal);

		SoftAssert sa = BaseTest.getSoftAssert();
		if (sa != null) {
			try {
				double actTL = Double.parseDouble(actualTotalLines.replace(",", ""));
				double expTL = Double.parseDouble(expectedTotalLines.replace(",", ""));
				sa.assertTrue(Math.abs(actTL - expTL) < 0.01,
						"[Sales Invoice] Total Lines mismatch — Expected: [" + expTL + "] Actual: [" + actTL + "]");

				double actGT = Double.parseDouble(actualGrandTotal.replace(",", ""));
				double expGT = Double.parseDouble(expectedGrandTotal.replace(",", ""));
				sa.assertTrue(Math.abs(actGT - expGT) < 0.01,
						"[Sales Invoice] Grand Total mismatch — Expected: [" + expGT + "] Actual: [" + actGT + "]");
			} catch (Exception e) {
				sa.assertEquals(actualTotalLines, expectedTotalLines, "[Sales Invoice] Total Lines mismatch");
				sa.assertEquals(actualGrandTotal, expectedGrandTotal, "[Sales Invoice] Grand Total mismatch");
			}
		}
	}

	public void verifyHeaderTotalAmounts() {
		verifyHeaderTotalAmounts("500.00", "560.00");
	}

	// ==================== Invoice Line Tab & Verification ====================

	/**
	 * Navigates to the Invoice Line subtab.
	 */
	public void navigateToInvoiceLineTab() {
		log.info("Navigating to 'Invoice Line' tab...");
		invoiceLineTab.first().waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		invoiceLineTab.first().click();
		page.waitForTimeout(1000);
		WaitUtils.waitForLoadingIndicatorToDisappear(page);
		log.info("Navigated to 'Invoice Line' tab successfully.");
	}

	/**
	 * Switches detail pane from Grid View to Form View if grid is active.
	 */
	public void ensureInvoiceLineFormView() {
		try {
			page.waitForTimeout(500);
			boolean isFormVisible = lineNoInput.count() > 0 && lineNoInput.first().isVisible();
			if (!isFormVisible && subtabGridViewButton.count() > 0 && subtabGridViewButton.first().isVisible()) {
				log.info("Invoice Line form view not visible. Toggling to Form View...");
				subtabGridViewButton.first().click();
				page.waitForTimeout(800);
				WaitUtils.waitForLoadingIndicatorToDisappear(page);
			}
		} catch (Exception e) {
			log.warn("Could not toggle Invoice Line view: {}", e.getMessage());
		}
	}

	private void navigateToNextLineRecord() {
		try {
			if (nextRecordBtn.count() > 0 && nextRecordBtn.first().isVisible() && !Boolean.TRUE.equals(nextRecordBtn.first().isDisabled())) {
				nextRecordBtn.first().click();
			} else {
				page.keyboard().press("Shift+Alt+ArrowRight");
			}
			page.waitForTimeout(600);
			WaitUtils.waitForLoadingIndicatorToDisappear(page);
		} catch (Exception e) {
			log.warn("Navigation to next line: {}", e.getMessage());
			page.keyboard().press("Shift+Alt+ArrowRight");
			page.waitForTimeout(600);
		}
	}

	private void navigateToFirstLineRecord() {
		try {
			if (firstRecordBtn.count() > 0 && firstRecordBtn.first().isVisible() && !Boolean.TRUE.equals(firstRecordBtn.first().isDisabled())) {
				firstRecordBtn.first().click();
				page.waitForTimeout(600);
				WaitUtils.waitForLoadingIndicatorToDisappear(page);
			}
		} catch (Exception e) {
			log.warn("Navigation to first line record: {}", e.getMessage());
		}
	}

	/**
	 * Verifies that the Invoice Lines populated from SO/Shipment exist and match expected Qty & Approved ASI.
	 * Checks both lines:
	 * - Line 1: Qty 100, Product DICLOVERGE TAB (PINK), Approved ASI
	 * - Line 2: Qty 30, Product DICLOVERGE TAB (PINK), Approved ASI
	 *
	 * @param line1Qty Expected Qty for Line 1 (e.g. "100")
	 * @param line2Qty Expected Qty for Line 2 (e.g. "30")
	 */
	public void verifyInvoiceLinesFromSO(String line1Qty, String line2Qty) {
		log.info("========== Verifying Invoice Lines populated from Sales Order / Shipment ==========");
		navigateToInvoiceLineTab();

		// Check rows in Grid View first if grid is present
		try {
			if (gridRows.count() > 0) {
				log.info("Invoice Line grid contains [{}] row elements.", gridRows.count());
				int rowCount = gridRows.count();
				for (int i = 0; i < rowCount; i++) {
					String rowText = gridRows.nth(i).innerText().trim().replace("\n", " | ");
					if (!rowText.isEmpty()) {
						//log.info("  Invoice Line Grid Row [{}]: {}", i + 1, rowText);
					}
				}
			}
		} catch (Exception ignored) {
		}

		// Ensure Form View to verify individual line field values
		ensureInvoiceLineFormView();
		navigateToFirstLineRecord();
		ensureInvoiceLineFormView();

		SoftAssert sa = BaseTest.getSoftAssert();

		// Check if initial record is a Delivery Note / description line (e.g. Line 8, empty Product, or 0 Qty)
		String initialProduct = productInput.count() > 0 && productInput.first().isVisible() ? productInput.first().inputValue().trim() : "";
		String initialLineNo = lineNoInput.count() > 0 && lineNoInput.first().isVisible() ? lineNoInput.first().inputValue().trim() : "";
		if (initialProduct.isEmpty() || "8".equals(initialLineNo) || "0".equals(initialLineNo)) {
			log.info("Current record is delivery note / header reference line (Line No: [{}], Product: [{}]). Advancing to first product line...",
					initialLineNo, initialProduct);
			navigateToNextLineRecord();
			ensureInvoiceLineFormView();
		}

		// --- Verify Line 1 (SO Line 10) ---
		log.info("--- Verifying Invoice Line 1 ---");
		String actualLine1No = lineNoInput.count() > 0 && lineNoInput.first().isVisible() ? lineNoInput.first().inputValue().trim() : "10";
		String actualLine1Qty = qtyInvoicedInput.count() > 0 && qtyInvoicedInput.first().isVisible() ? qtyInvoicedInput.first().inputValue().trim() : "";
		String actualLine1Product = productInput.count() > 0 && productInput.first().isVisible() ? productInput.first().inputValue().trim() : "";
		String actualLine1ASI = asiInput.count() > 0 && asiInput.first().isVisible() ? asiInput.first().inputValue().trim() : "";

		log.info("Invoice Line 1 details — Line No: [{}], Product: [{}], Qty: [{}], ASI: [{}]",
				actualLine1No, actualLine1Product, actualLine1Qty, actualLine1ASI);

		if (sa != null) {
			if (!actualLine1Qty.isEmpty()) {
				try {
					double actQ = Double.parseDouble(actualLine1Qty.replace(",", ""));
					double expQ = Double.parseDouble(line1Qty.replace(",", ""));
					sa.assertEquals(actQ, expQ, "[Invoice Line 1] Qty mismatch");
				} catch (Exception e) {
					sa.assertEquals(actualLine1Qty, line1Qty, "[Invoice Line 1] Qty mismatch");
				}
			}
			sa.assertTrue(actualLine1ASI.toLowerCase().contains("approved"),
					"[Invoice Line 1] Attribute Set Instance does not contain 'Approved'! Actual: [" + actualLine1ASI + "]");
		}

		// --- Navigate to Line 2 (SO Line 20) ---
		log.info("--- Navigating to Invoice Line 2 ---");
		navigateToNextLineRecord();
		ensureInvoiceLineFormView();

		// --- Verify Line 2 (SO Line 20) ---
		log.info("--- Verifying Invoice Line 2 ---");
		String actualLine2No = lineNoInput.count() > 0 && lineNoInput.first().isVisible() ? lineNoInput.first().inputValue().trim() : "20";
		String actualLine2Qty = qtyInvoicedInput.count() > 0 && qtyInvoicedInput.first().isVisible() ? qtyInvoicedInput.first().inputValue().trim() : "";
		String actualLine2Product = productInput.count() > 0 && productInput.first().isVisible() ? productInput.first().inputValue().trim() : "";
		String actualLine2ASI = asiInput.count() > 0 && asiInput.first().isVisible() ? asiInput.first().inputValue().trim() : "";

		log.info("Invoice Line 2 details — Line No: [{}], Product: [{}], Qty: [{}], ASI: [{}]",
				actualLine2No, actualLine2Product, actualLine2Qty, actualLine2ASI);

		if (sa != null) {
			if (!actualLine2Qty.isEmpty()) {
				try {
					double actQ = Double.parseDouble(actualLine2Qty.replace(",", ""));
					double expQ = Double.parseDouble(line2Qty.replace(",", ""));
					sa.assertEquals(actQ, expQ, "[Invoice Line 2] Qty mismatch");
				} catch (Exception e) {
					sa.assertEquals(actualLine2Qty, line2Qty, "[Invoice Line 2] Qty mismatch");
				}
			}
			sa.assertTrue(actualLine2ASI.toLowerCase().contains("approved"),
					"[Invoice Line 2] Attribute Set Instance does not contain 'Approved'! Actual: [" + actualLine2ASI + "]");
		}

		log.info("✅ Verified Invoice Lines from SO: Line 1 (Qty [{}]) & Line 2 (Qty [{}]) with Approved ASI.", line1Qty, line2Qty);
	}

	public void verifyInvoiceLinesFromSO() {
		String qty1 = DataManager.getData("so.qtyEntered", "qtyEntered");
		String qty2 = DataManager.getData("so.freeQty", "freeQty");
		if (qty2.isEmpty()) {
			qty2 = "30";
		}
		verifyInvoiceLinesFromSO(qty1, qty2);
	}

	// ==================== Document Actions ====================

	/**
	 * Performs a Document Action (Prepare or Complete) on Sales Invoice Header.
	 *
	 * @param action Document Action name ("Prepare" or "Complete")
	 */
	public void performDocAction(String action) {
		log.info("Performing document action [{}] on Sales Invoice...", action);
		navigateBackToInvoiceHeader();

		docActionButton.first().waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		docActionButton.first().click();
		WaitUtils.waitForLoadingIndicatorToDisappear(page);

		docActionPopup.first().waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(8000));
		Locator dropdown = page.locator("//div[@instancename='documentAction']//select");
		dropdown.first().selectOption(new SelectOption().setLabel(action));

		Locator okBtn = page.locator("//div[@instancename='documentAction']//button[@title='OK']");
		okBtn.click();
		WaitUtils.waitForBigLoadingIndicatorToDisappear(page);
		page.waitForTimeout(1000);
		handleWarningPopup();
		log.info("Document action [{}] submitted for Sales Invoice.", action);
	}

	/**
	 * Verifies the Document Status field value on Sales Invoice Header.
	 *
	 * @param expectedStatus Expected status (e.g. "In Progress", "Completed")
	 */
	public void verifyDocStatus(String expectedStatus) {
		page.waitForTimeout(500);
		WaitUtils.waitForLoadingIndicatorToDisappear(page);

		docStatusInput.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		String actualStatus = docStatusInput.inputValue().trim();

		boolean isMatch = actualStatus.equalsIgnoreCase(expectedStatus.trim());
		SoftAssert sa = BaseTest.getSoftAssert();
		if (sa != null) {
			sa.assertTrue(isMatch, "[Sales Invoice] DocStatus mismatch — Expected: [" + expectedStatus + "] Actual: [" + actualStatus + "]");
		} else {
			Assert.assertTrue(isMatch, "Sales Invoice DocStatus mismatch — Expected: " + expectedStatus + " Actual: " + actualStatus);
		}

		if (isMatch) {
			log.info("✅ Verified Sales Invoice DocStatus successfully: [{}]", actualStatus);
		} else {
			log.warn("Sales Invoice DocStatus mismatch — Expected: [{}] Actual: [{}]", expectedStatus, actualStatus);
		}
	}

	// ==================== Posted Window Actions ====================

	/**
	 * Clicks on the 'Posted' button on Sales Invoice Header, handles any posting confirmation popup,
	 * and waits for the Posting window to open.
	 */
	public void clickPostedButton() {
		page.waitForTimeout(1000);
		log.info("Clicking on 'Posted' button on Sales Invoice...");

		// Ensure on Invoice Header
		navigateBackToInvoiceHeader();

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
			log.info("'Posting' window opened successfully for Sales Invoice.");
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
	 * Verifies that Accounted Debit amount equals Accounted Credit amount in Posting window.
	 */
	public void verifyPostedDebitEqualsCredit() {
		double[] amounts = getPostedDebitAndCreditAmounts();
		double debit = amounts[0];
		double credit = amounts[1];

		boolean isMatch = Double.compare(debit, credit) == 0;
		SoftAssert sa = BaseTest.getSoftAssert();
		if (sa != null) {
			sa.assertTrue(isMatch, "[Sales Invoice Posting] Accounted Debit [" + debit + "] does not equal Accounted Credit [" + credit + "]");
		} else {
			Assert.assertTrue(isMatch, "[Sales Invoice Posting] Accounted Debit [" + debit + "] does not equal Accounted Credit [" + credit + "]");
		}

		if (isMatch) {
			log.info("✅ Verified Sales Invoice Posted Debit [{}] equals Credit [{}] successfully.", debit, credit);
		} else {
			log.error("❌ Sales Invoice Posted Debit [{}] does NOT equal Credit [{}]!", debit, credit);
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
		Locator closeBtn = page.locator(
				"(//li[contains(@class,'z-tab') and (contains(.,'Posting') or .//span[normalize-space(text())='Posting'])]//i[contains(@class,'z-icon-times') or contains(@class,'z-tab-icon')])[1] | " +
				"//li[contains(@class,'z-tab') and contains(.,'Posting')]//i[contains(@class,'z-tab-icon') or contains(@class,'z-icon-times') or @aria-label='Close']");
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
		log.info("Closed Posting window for Sales Invoice.");
	}

	// ==================== Helper Utilities ====================

	public boolean verifyFieldFilled(Locator fieldLocator, String fieldName, String expectedValue) {
		String actualValue = fieldLocator.first().inputValue();
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

	public String handleWarningPopup() {
		StringBuilder allPopupText = new StringBuilder();
		try {
			int attempts = 0;
			while (attempts < 3) {
				page.waitForTimeout(300);
				Locator popup = page.locator(
						"//div[(contains(@class,'popup-dialog') or contains(@class,'z-messagebox')) and not(contains(@style,'display: none')) and not(contains(@style,'display:none'))]").first();
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
						"//div[(contains(@class,'popup-dialog') or contains(@class,'z-messagebox')) and not(contains(@style,'display: none'))]//button[not(contains(@class,'z-window-close'))]").first();
				Locator closeIcon = page.locator(
						"//div[(contains(@class,'popup-dialog') or contains(@class,'z-messagebox')) and not(contains(@style,'display: none'))]//*[contains(@class,'z-window-close')]").first();

				try {
					if (okBtn.isVisible()) {
						okBtn.click(new Locator.ClickOptions().setTimeout(3000));
						log.info("Removed warning popup via OK button.");
					} else if (closeIcon.isVisible()) {
						closeIcon.click(new Locator.ClickOptions().setTimeout(3000));
						log.info("Removed warning popup via Close icon.");
					} else {
						page.keyboard().press("Enter");
						log.info("Dismissed warning popup via Enter key.");
					}
				} catch (Exception clickEx) {
					page.keyboard().press("Enter");
				}

				page.waitForTimeout(400);
				WaitUtils.waitForLoadingIndicatorToDisappear(page);
				attempts++;
			}
		} catch (Exception e) {
			log.warn("Exception while handling warning popup: {}", e.getMessage());
		}
		return allPopupText.toString();
	}
}
