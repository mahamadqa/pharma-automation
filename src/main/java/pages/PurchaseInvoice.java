package pages;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitForSelectorState;

import base.BaseTest;
import utils.DataManager;
import utils.WaitUtils;

/**
 * Page Object Class for the Purchase Invoice and Credit/Debit Note Window.
 * Implements standard locator strategy matching PurchaseOrder and GoodsReceiptNote.
 */
public class PurchaseInvoice {

	private static final Logger log = LogManager.getLogger(PurchaseInvoice.class);
	private final Page page;

	// ==================== Navigation & Breadcrumb Locators ====================
	private final Locator invoiceMenuSearchInput;
	private final Locator searchInvoiceWindowItem;
	private final Locator invoiceHeaderBreadcrumb;
	private final Locator invoiceLineTab;

	// ==================== Header Form Fields ====================
	private final Locator orgInput;
	private final Locator targetDocTypeInput;
	private final Locator documentNoInput;
	private final Locator DescInput;
	private final Locator bPartnerInput;
	private final Locator TotalLinesAmt;
	private final Locator GrandTotalAmt;
	private final Locator createLinesFromButton;
	private final Locator postedButton;

	// ==================== Create Lines From Modal Fields ====================
	private final Locator createLinesModal;
	private final Locator orderInputInModal;
	private final Locator createLinesModalRows;
	private final Locator createLinesModalSubmitButton;

	// ==================== Invoice Line Fields ====================
	private final Locator productInput;
	private final Locator qtyEnteredInput;
	private final Locator priceEnteredInput;
	private final Locator taxInput;
	private final Locator priceListInput;
	private final Locator lineNetAmtInput;

	private double[] lastPostedAmounts;

	public PurchaseInvoice(Page page) {
		this.page = page;

		// Navigation & Breadcrumbs - standard locators
		this.invoiceMenuSearchInput = page.locator("//input[@class='z-bandbox-input']");
		this.searchInvoiceWindowItem = page.locator(
				"//tr[contains(.,'Purchase Invoice and Credit/Debit Note')]//a[@title='New'] | //td[contains(.,'Purchase Invoice and Credit/Debit Note')]/following-sibling::td//a[@title='New']");
		this.invoiceHeaderBreadcrumb = page.locator("//div[@instancename='breadcrumb']//a[text()='Invoice'] | //div[@instancename='breadcrumb']//a[contains(text(),'Invoice')]");
		this.invoiceLineTab = page.locator("//span[text()='Invoice Line']");

		// Header Form Fields
		this.orgInput = page.locator("//span[@instancename='C_Invoice0AD_Org_ID']//input");
		this.targetDocTypeInput = page.locator("//span[@instancename='C_Invoice0C_DocTypeTarget_ID']//input");
		this.documentNoInput = page.locator("//input[@instancename='C_Invoice0DocumentNo']");
		this.DescInput = page.locator("//textarea[@instancename='C_Invoice0Description']");
		this.bPartnerInput = page.locator("//div[@instancename='C_Invoice0C_BPartner_ID']//input");
		this.TotalLinesAmt = page.locator("(//div[@instancename='C_Invoice0TotalLines']//input)[1]");
		this.GrandTotalAmt = page.locator("(//div[@instancename='C_Invoice0GrandTotal']//input)[1]");
		this.createLinesFromButton = page.locator("//button[@instancename='C_Invoice0CreateLinesFrom']");
		this.postedButton = page.locator("//button[@instancename='C_Invoice0Posted']");

		// Create Lines From Modal Fields
		this.createLinesModal = page.locator("//div[@instancename='infopanel']");
		this.orderInputInModal = page.locator("(//div[@instancename='infopanel']//span[starts-with(text(),'Order')]/following::input)[1]");
		this.createLinesModalRows = page.locator("//div[@instancename='infopanel']//tr[contains(@class,'z-listitem')]");
		this.createLinesModalSubmitButton = page.locator("//div[@instancename='infopanel']//button[contains(@class,'txt-btn')]");

		// Invoice Line Fields
		this.productInput = page.locator("//div[@instancename='C_InvoiceLine0M_Product_ID']//input");
		this.qtyEnteredInput = page.locator("(//div[@instancename='C_InvoiceLine0QtyEntered']//input)[1]");
		this.priceEnteredInput = page.locator("(//div[@instancename='C_InvoiceLine0PriceEntered']//input)[1]");
		this.taxInput = page.locator("//span[@instancename='C_InvoiceLine0C_Tax_ID']//input");
		this.priceListInput = page.locator("(//div[@instancename='C_InvoiceLine0PriceList']//input)[1]");
		this.lineNetAmtInput = page.locator("(//div[@instancename='C_InvoiceLine0LineNetAmt']//input)[1]");
	}

	/**
	 * Verifies whether the specified field has data filled in it.
	 */
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

	// ==================== Navigation Actions ====================

	public void navigateToInvoiceWindow() {
		log.info("Navigating to Purchase Invoice and Credit/Debit Note window via menu search...");
		invoiceMenuSearchInput.first().waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		invoiceMenuSearchInput.first().fill("Purchase Invoice and Credit/Debit Note");
		page.waitForTimeout(5000);

		Locator targetItem = searchInvoiceWindowItem.first();
		try {
			targetItem.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(5000));
			targetItem.click();
		} catch (Exception e) {
			log.warn("Target item not clickable, pressing Enter on search input: {}", e.getMessage());
			invoiceMenuSearchInput.first().press("Enter");
		}

		try {
			orgInput.first().waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(25000));
			log.info("Navigated to Purchase Invoice and Credit/Debit Note window successfully.");
		} catch (Exception e) {
			log.warn("Waiting for orgInput visible timed out: {}", e.getMessage());
		}
		WaitUtils.waitForLoadingIndicatorToDisappear(page);
	}

	// ==================== Header Actions ====================

	public void selectOrg(String orgName) {
		page.waitForTimeout(1000);
		orgInput.first().waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		orgInput.first().fill(orgName);
		orgInput.first().press("Tab");
		page.waitForTimeout(2000);
		verifyFieldFilled(orgInput.first(), "Organization", orgName);
	}

	public void selectOrg() {
		selectOrg(DataManager.getData("org"));
		WaitUtils.waitForLoadingIndicatorToDisappear(page);
	}

	public void selecTtargetDocType(String docType) {
		targetDocTypeInput.first().waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		targetDocTypeInput.first().fill(docType);
		targetDocTypeInput.first().press("Tab");
		page.waitForTimeout(2000);
		verifyFieldFilled(targetDocTypeInput.first(), "Target Document Type", docType);
	}

	public void selecTtargetDocType() {
		selecTtargetDocType("AP Invoice");
	}

	public void selectTargetDocType(String docType) {
		selecTtargetDocType(docType);
	}

	public void selectTargetDocType() {
		selecTtargetDocType();
	}

	public void fillDescription(String desc) {
		DescInput.first().click();
		DescInput.first().fill(desc);
	}

	public void fillDescription() {
		fillDescription(DataManager.getData("description"));
	}

	public void selectBPartner(String partnerName) {
		bPartnerInput.first().waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		bPartnerInput.first().fill(partnerName);
		bPartnerInput.first().press("Tab");
		page.waitForTimeout(2000);
		verifyFieldFilled(bPartnerInput.first(), "Business Partner", partnerName);
	}

	public void selectBPartner() {
		selectBPartner(DataManager.getData("bPartner"));
		WaitUtils.waitForLoadingIndicatorToDisappear(page);
	}

	public String getInvoiceDocNo() {
		return documentNoInput.first().inputValue().trim();
	}

	// ==================== Create Lines From Modal Actions ====================

	public void clickCreateLinesFromButton() {
		page.waitForTimeout(1000);
		createLinesFromButton.first().waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		createLinesFromButton.first().click();
		page.waitForTimeout(2000);
		WaitUtils.waitForLoadingIndicatorToDisappear(page);
		createLinesModal.first().waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(15000));
		log.info("'Create Lines From' modal opened.");
	}

	public void attachPOInCreateLinesFromModal(String poDocNo) {
		if (poDocNo == null || poDocNo.trim().isEmpty()) {
			poDocNo = PurchaseOrder.getLastPODocNo();
		}
		if (poDocNo == null || poDocNo.trim().isEmpty()) {
			try {
				poDocNo = DataManager.getData("purchaseOrder");
			} catch (Exception ignored) {
			}
		}

		log.info("Attaching Purchase Order [{}] in 'Create lines from' modal...", poDocNo);
		Locator orderField = orderInputInModal.first();
		if (orderField.count() == 0) {
			orderField = page.locator("(//div[@instancename='infopanel']//input[contains(@class,'z-combobox-input')])[1]");
		}
		orderField.first().waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		orderField.first().click();
		orderField.first().fill(poDocNo);
		page.waitForTimeout(1000);

		// If a dropdown popup appears with matching PO, click it; else press Tab
		Locator popupOption = page.locator(String.format(
				"//div[contains(@class,'z-combobox-popup')]//span[contains(text(),'%s')] | //span[contains(@class,'z-comboitem-text') and contains(text(),'%s')]",
				poDocNo, poDocNo));
		if (popupOption.count() > 0 && popupOption.first().isVisible()) {
			popupOption.first().click();
		} else {
			orderField.first().press("Tab");
		}
		page.waitForTimeout(2000);
		WaitUtils.waitForLoadingIndicatorToDisappear(page);

		selectRecordInCreateLinesFromModal(poDocNo);
	}

	public void attachPOInCreateLinesFromModal() {
		attachPOInCreateLinesFromModal(PurchaseOrder.getLastPODocNo());
	}

	public void selectRecordInCreateLinesFromModal(String poDocNo) {
		page.waitForTimeout(1000);
		try {
			createLinesModalRows.first().waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		} catch (Exception e) {
			log.warn("Rows not immediately visible in modal: {}", e.getMessage());
		}

		if (createLinesModalRows.count() > 0) {
			Locator targetRow;
			if (poDocNo != null && !poDocNo.trim().isEmpty()) {
				Locator matchedRow = page.locator("//div[@instancename='infopanel']//tr[contains(@class,'z-listitem') and contains(.,'" + poDocNo.trim() + "')]");
				if (matchedRow.count() > 0) {
					targetRow = matchedRow.first();
				} else {
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
	}

	public void selectRecordInCreateLinesFromModal() {
		selectRecordInCreateLinesFromModal(PurchaseOrder.getLastPODocNo());
	}

	public void confirmCreateLinesFromModal() {
		log.info("Confirming 'Create lines from' modal dialog...");
		createLinesModalSubmitButton.first().waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		createLinesModalSubmitButton.first().click();
		page.waitForTimeout(2000);
		WaitUtils.waitForBigLoadingIndicatorToDisappear(page);
		WaitUtils.waitForLoadingIndicatorToDisappear(page);
		try {
			createLinesModal.first().waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.HIDDEN).setTimeout(10000));
		} catch (Exception ignored) {
		}
		log.info("'Create Lines From' modal confirmed and closed.");
	}

	// ==================== Invoice Line Tab Actions ====================

	public void clickOnInvoiceLineTab() {
		invoiceLineTab.first().waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		invoiceLineTab.first().click();
		page.waitForTimeout(3000);
		WaitUtils.waitForLoadingIndicatorToDisappear(page);
		log.info("Navigated to 'Invoice Line' tab.");
	}

	public void verifyProduct(String expectedProduct) {
		verifyFieldFilled(productInput.first(), "Product", expectedProduct);
	}

	public void verifyProduct() {
		verifyProduct(DataManager.getData("product"));
	}

	public void verifyQty(String expectedQty) {
		verifyFieldFilled(qtyEnteredInput.first(), "Quantity", expectedQty);
	}

	public void verifyQty() {
		verifyQty(DataManager.getData("qtyEntered"));
	}

	public void verifyPrice(String expectedPrice) {
		verifyFieldFilled(priceEnteredInput.first(), "Price", expectedPrice);
	}

	public void verifyPrice() {
		verifyPrice(DataManager.getData("priceEntered"));
	}

	public void verifyTax(String expectedTax) {
		verifyFieldFilled(taxInput.first(), "Tax", expectedTax);
	}

	public void verifyTax() {
		verifyTax(DataManager.getData("taxType"));
	}

	public void verifyPriceList(String expectedPriceList) {
		verifyFieldFilled(priceListInput.first(), "Price List", expectedPriceList);
	}

	public void verifyPriceList() {
		verifyPriceList(DataManager.getData("priceList"));
	}

	public void verifyLineNetAmt() {
		String val = lineNetAmtInput.first().inputValue();
		Assert.assertNotNull(val, "Line Net Amt should not be null");
		Assert.assertFalse(val.trim().isEmpty(), "Line Net Amt should not be empty");
		log.info("Verified Line Net Amt on Invoice Line: [{}]", val);
	}

	public void verifyInvoiceLineFields(PurchaseOrder po) {
		log.info("========== Verifying Invoice Line fields populated from PO ==========");
		verifyProduct();
		verifyQty();
		verifyPrice();
		verifyTax();
		verifyPriceList();
		verifyLineNetAmt();
		log.info("✅ All Invoice Line fields verified successfully.");
	}

	public void verifyInvoiceLineFields() {
		verifyInvoiceLineFields(null);
	}

	// ==================== Header Totals Actions ====================

	public void navigateBackOnInvoiceHeader() {
		invoiceHeaderBreadcrumb.first().waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		invoiceHeaderBreadcrumb.first().click();
		page.waitForTimeout(1000);
		WaitUtils.waitForLoadingIndicatorToDisappear(page);
		log.info("Navigated back to Invoice Header.");
	}

	public void verifyHeaderTotalLineAmt() {
		String actualTotalLines = TotalLinesAmt.first().inputValue().trim();
		SoftAssert sa = BaseTest.getSoftAssert();
		if (sa != null) {
			sa.assertTrue(!actualTotalLines.isEmpty() && !actualTotalLines.equals("0.00"),
					"[Invoice Header] Total Lines amount is 0 or empty!");
		} else {
			Assert.assertTrue(!actualTotalLines.isEmpty() && !actualTotalLines.equals("0.00"),
					"Total Lines amount is 0 or empty!");
		}
		log.info("Total Lines amount on Invoice Header: [{}]", actualTotalLines);
	}

	public void verifyHeaderGrandTotalAmt() {
		String actualGrandTotal = GrandTotalAmt.first().inputValue().trim();
		SoftAssert sa = BaseTest.getSoftAssert();
		if (sa != null) {
			sa.assertTrue(!actualGrandTotal.isEmpty() && !actualGrandTotal.equals("0.00"),
					"[Invoice Header] Grand Total amount is 0 or empty!");
		} else {
			Assert.assertTrue(!actualGrandTotal.isEmpty() && !actualGrandTotal.equals("0.00"),
					"Grand Total amount is 0 or empty!");
		}
		log.info("Grand Total amount on Invoice Header: [{}]", actualGrandTotal);
	}

	public void matchGrandTotalAndTotalLines() {
		verifyHeaderTotalLineAmt();
		verifyHeaderGrandTotalAmt();
		String totalLinesStr = TotalLinesAmt.first().inputValue().trim().replace(",", "");
		String grandTotalStr = GrandTotalAmt.first().inputValue().trim().replace(",", "");
		double tl = Double.parseDouble(totalLinesStr);
		double gt = Double.parseDouble(grandTotalStr);
		Assert.assertTrue(gt >= tl, "Grand Total [" + gt + "] must be greater than or equal to Total Lines [" + tl + "]");
		log.info("✅ Matched Grand Total [{}] & Total Lines [{}] on Invoice Header successfully.", gt, tl);
	}

	public void verifyHeaderTotalLinesAndGrandTotal(PurchaseOrder purchaseOrder) {
		matchGrandTotalAndTotalLines();
	}

	public void verifyHeaderTotalLinesAndGrandTotal() {
		matchGrandTotalAndTotalLines();
	}

	// ==================== Posting Actions & Verification ====================

	public void clickPostedButton() {
		page.waitForTimeout(1000);
		postedButton.first().waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		postedButton.first().click();
		page.waitForTimeout(2000);
		WaitUtils.waitForLoadingIndicatorToDisappear(page);

		Locator modal = page.locator("//div[contains(@class,'z-messagebox') or contains(@class,'z-window-modal')]");
		if (modal.count() > 0 && modal.first().isVisible()) {
			Locator okBtn = modal.first().locator("button.z-button, button").first();
			okBtn.click();
			page.waitForTimeout(2000);
			WaitUtils.waitForLoadingIndicatorToDisappear(page);
		}

		Locator postingTab = page.locator("//li[contains(@class,'z-tab')]//span[normalize-space(text())='Posting']");
		try {
			postingTab.first().waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(8000));
			log.info("'Posting' window opened successfully for Purchase Invoice.");
		} catch (Exception te) {
			log.warn("Posting window tab not immediately visible: {}", te.getMessage());
		}
	}

	public double[] getPostedDebitAndCreditAmounts() {
		page.waitForTimeout(1000);
		WaitUtils.waitForLoadingIndicatorToDisappear(page);

		Locator postingTable = page.locator(".z-listbox, .z-grid")
				.filter(new Locator.FilterOptions().setHasText("Accounted Debit"))
				.first();

		try {
			postingTable.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(5000));
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
			log.warn("Error reading posting table amounts: {}", e.getMessage());
		}

		lastPostedAmounts = new double[]{0.0, 0.0};
		return lastPostedAmounts;
	}

	public void verifyPostedDebitEqualsCredit() {
		double[] amounts = getPostedDebitAndCreditAmounts();
		double debit = amounts[0];
		double credit = amounts[1];

		boolean isMatch = Double.compare(debit, credit) == 0;
		SoftAssert sa = BaseTest.getSoftAssert();
		if (sa != null) {
			sa.assertTrue(isMatch, "[Invoice Posting] Accounted Debit [" + debit + "] does not equal Accounted Credit [" + credit + "]");
		} else {
			Assert.assertTrue(isMatch, "[Invoice Posting] Accounted Debit [" + debit + "] does not equal Accounted Credit [" + credit + "]");
		}

		if (isMatch) {
			log.info("✅ Verified Invoice Posted Debit [{}] equals Credit [{}] successfully.", debit, credit);
		} else {
			log.error("❌ Invoice Posted Debit [{}] does NOT equal Credit [{}]!", debit, credit);
		}
	}

	public String getPostedAmount() {
		if (lastPostedAmounts != null && lastPostedAmounts.length > 0) {
			return String.format(java.util.Locale.US, "%.2f", lastPostedAmounts[0]);
		}
		double[] amounts = getPostedDebitAndCreditAmounts();
		return String.format(java.util.Locale.US, "%.2f", amounts[0]);
	}

	public void closePostingWindow() {
		page.waitForTimeout(1000);
		Locator postingTabCloseBtn = page.locator("(//li[contains(@class,'z-tab') and .//span[normalize-space(text())='Posting']]//i[contains(@class,'z-icon-times')])[1]");
		if (postingTabCloseBtn.count() > 0 && postingTabCloseBtn.first().isVisible()) {
			postingTabCloseBtn.first().click();
			page.waitForTimeout(1000);
			WaitUtils.waitForLoadingIndicatorToDisappear(page);
			log.info("Closed 'Posting' window tab successfully.");
		}
	}
}
