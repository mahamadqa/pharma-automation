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

import base.BaseTest;
import utils.DataManager;
import utils.WaitUtils;

public class PurchaseOrder {

	private static final Logger log = LogManager.getLogger(PurchaseOrder.class);
	private final Page page;

	private final Locator orgInput;
	private final Locator targetDocTypeInput;
	private final Locator DescInput;
	private final Locator bPartnerInput;
	private final Locator salesRepInput;
	private final Locator warehouseInput;
	private final Locator currency;
	private final Locator poLineTab;
	private final Locator productInput;
	private final Locator descriptionTextarea;
	private final Locator poHeaderBreadcrumb;
	private final Locator poMenuSearchInput;
	private final Locator searchPOWindowItem;
	private final Locator partnerLocation;
	private final Locator invoiceLocation;
	private final Locator TotalLinesAmt;
	private final Locator GrandTotalAmt;
	private final Locator qtyEnteredInput;
	private final Locator qtyOrderedInput;
	private final Locator priceEnteredInput;
	private final Locator taxInput;
	private final Locator priceListInput;
	private final Locator lineNetAmtInput;

	private double lastLineNetAmt = 0.0;
	private static String lastPODocNo = "";

	public PurchaseOrder(Page page) {
		this.page = page;
		this.orgInput = page.locator("//span[@instancename='C_Order0AD_Org_ID']//input");
		this.targetDocTypeInput = page.locator("//span[@instancename='C_Order0C_DocTypeTarget_ID']//input");
		this.DescInput = page.locator("//textarea[@instancename='C_Order0Description']");
		this.bPartnerInput = page.locator("//div[@instancename='C_Order0C_BPartner_ID']//span//input");
		this.salesRepInput = page.locator("//span[@instancename='C_Order0SalesRep_ID']//input");
		this.warehouseInput = page.locator("//span[@instancename='C_Order0M_Warehouse_ID']//input");
		this.currency = page.locator("//span[@instancename='C_Order0C_Currency_ID']//input");
		this.poLineTab = page.locator("//span[text()='PO Line']");
		this.productInput = page.locator("//div[@instancename='C_OrderLine0M_Product_ID']//input");
		this.descriptionTextarea = page.locator("//textarea[@instancename='C_OrderLine0Description']");
		this.poHeaderBreadcrumb = page.locator("//div[@instancename='breadcrumb']//a[text()='Purchase Order']");
		this.poMenuSearchInput = page.locator("//input[@class='z-bandbox-input']");
		this.searchPOWindowItem = page
				.locator("//td[@title='Manage Purchase Orders']/following-sibling::td//a[@title='New']");
		this.partnerLocation = page.locator("//span[@instancename='C_Order0C_BPartner_Location_ID']//input");
		this.invoiceLocation = page.locator("//span[@instancename='C_Order0Bill_Location_ID']//input");
		this.TotalLinesAmt = page.locator("(//div[@instancename='C_Order0TotalLines']//input)[1]");
		this.GrandTotalAmt = page.locator("(//div[@instancename='C_Order0GrandTotal']//input)[1]");
		this.qtyEnteredInput = page.locator("(//div[@instancename='C_OrderLine0QtyEntered']//input)[1]");
		this.qtyOrderedInput = page.locator("(//div[@instancename='C_OrderLine0QtyOrdered']//input)[1]");
		this.priceEnteredInput = page.locator("(//div[@instancename='C_OrderLine0PriceEntered']//input)[1]");
		this.taxInput = page.locator("//span[@instancename='C_OrderLine0C_Tax_ID']//input");
		this.priceListInput = page.locator("(//div[@instancename='C_OrderLine0PriceList']//input)[1]");
		this.lineNetAmtInput = page.locator("(//div[@instancename='C_OrderLine0LineNetAmt']//input)[1]");
	}

	/**
	 * Verifies whether the specified field has data filled in it.
	 *
	 * @param fieldLocator  Locator of the input field
	 * @param fieldName     Descriptive field name for logging and assertions
	 * @param expectedValue Expected value that was filled
	 * @return true if field contains data, false otherwise
	 */
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

	/**
	 * Verifies whether the specified field has data filled in it using a locator
	 * string.
	 *
	 * @param locatorString String locator of the field
	 * @param fieldName     Descriptive name
	 * @param expectedValue Expected value
	 * @return true if filled, false otherwise
	 */
	public boolean verifyFieldFilled(String locatorString, String fieldName, String expectedValue) {
		return verifyFieldFilled(page.locator(locatorString), fieldName, expectedValue);
	}

	/**
	 * Checks if a field has data filled (not empty).
	 *
	 * @param fieldLocator Locator of the input field
	 * @return true if filled, false if null or empty
	 */
	public boolean isFieldFilled(Locator fieldLocator) {
		String actualValue = fieldLocator.inputValue();
		return actualValue != null && !actualValue.trim().isEmpty();
	}

	/**
	 * Checks if a field has data filled using locator string.
	 *
	 * @param locatorString String locator
	 * @return true if filled, false if null or empty
	 */
	public boolean isFieldFilled(String locatorString) {
		return isFieldFilled(page.locator(locatorString));
	}

	/**
	 * Gets the current filled value of a field.
	 *
	 * @param fieldLocator Locator of the input field
	 * @return filled text value
	 */
	public String getFieldValue(Locator fieldLocator) {
		return fieldLocator.inputValue();
	}

	/**
	 * Gets the current filled value of a field using locator string.
	 *
	 * @param locatorString String locator
	 * @return filled text value
	 */
	public String getFieldValue(String locatorString) {
		return page.locator(locatorString).inputValue();
	}

	/**
	 * Navigates to the Manage Purchase Orders window via search bandbox.
	 */
	public void navigateToPOWindow() {
		page.waitForTimeout(1000);
		log.info("Navigating to Purchase Order window...");
		poMenuSearchInput
				.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		poMenuSearchInput.fill("Purchase Order");
		page.waitForTimeout(2000);
		WaitUtils.waitForLoadingIndicatorToDisappear(page);
		searchPOWindowItem
				.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.ATTACHED).setTimeout(10000));
		long startTime = System.currentTimeMillis();
		long timeout = 10000;
		searchPOWindowItem.click();
		while (!orgInput.isVisible()
		        && System.currentTimeMillis() - startTime < timeout) {

		    searchPOWindowItem.click();

		    page.waitForTimeout(300);
		}
		page.waitForTimeout(1000);
		WaitUtils.waitForLoadingIndicatorToDisappear(page);
		log.info("Opened Purchase Order window successfully.");
	}

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

	public void selecTtargetDocType(String docType) {
		log.info("Selecting Target Document Type: [{}]", docType);
		targetDocTypeInput
				.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		targetDocTypeInput.fill(docType);
		targetDocTypeInput.press("Tab");
		page.waitForTimeout(2000);
		verifyFieldFilled(targetDocTypeInput, "Target Document Type", docType);
		log.info("Selected Target Document Type: [{}]", docType);
	}

	public void selecTtargetDocType() {
		selecTtargetDocType(DataManager.getData("targetDocType"));
	}
	
	public void fillDescription() {
		DescInput.click();
		DescInput.fill(DataManager.getData("description"));
		log.info("Description filled : " +DataManager.getData("description"));
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

	public void selectSalesRep(String repName) {
		log.info("Selecting Sales Representative: [{}]", repName);
		salesRepInput.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		salesRepInput.fill(repName);
		salesRepInput.press("Tab");
		page.waitForTimeout(2000);
		verifyFieldFilled(salesRepInput, "Sales Representative", repName);
		log.info("Selected Sales Representative: [{}]", repName);
	}

	public void selectSalesRep() {
		selectSalesRep(DataManager.getData("salesRep"));
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

	public void clickOnPOLineTab() {
		log.info("Clicking on 'PO Line' tab...");
		poLineTab.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		poLineTab.click();
		page.waitForTimeout(1000);
		log.info("Navigated to 'PO Line' tab.");
	}

	public void enterProdct(String productName) {
		page.waitForTimeout(1000);
		log.info("Entering Product: [{}]", productName);
		productInput.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		productInput.fill(productName);
		productInput.press("Tab");
		page.waitForTimeout(2000);
		verifyFieldFilled(productInput, "Product", productName);

		if (descriptionTextarea.isVisible()) {
			descriptionTextarea.click();
		}
		log.info("Entered Product: [{}] successfully.", productName);
	}

	public void enterProdct() {
		enterProdct(DataManager.getData("product"));
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

	public void enterQty(String qty) {
		enterQtyEntered(qty);
	}

	public void enterQty() {
		enterQtyEntered();
	}

	public void verifyPOQty(String expectedQty) {
		qtyOrderedInput.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		String actualQty = qtyOrderedInput.inputValue().trim();
		log.info("Verifying POQty (QtyOrdered): Actual [{}], Expected [{}]", actualQty, expectedQty);

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
					"[PO Line] POQty [" + actualQty + "] does not match expected QtyEnter [" + expectedQty + "]");
		} else {
			Assert.assertTrue(isMatch,
					"POQty [" + actualQty + "] does not match expected QtyEnter [" + expectedQty + "]");
		}
		if (isMatch) {
			log.info("POQty verified successfully: [{}]", actualQty);
		} else {
			log.warn("POQty mismatch: Actual [{}], Expected [{}]", actualQty, expectedQty);
		}
	}

	public void verifyPOQty() {
		String qtyEnterValue = qtyEnteredInput.inputValue().trim();
		if (qtyEnterValue.isEmpty()) {
			qtyEnterValue = DataManager.getData("qtyEntered");
		}
		verifyPOQty(qtyEnterValue);
	}

	public void verifyPriceEntered(String expectedPrice) {
		priceEnteredInput.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		String actualPrice = priceEnteredInput.inputValue().trim();
		log.info("Verifying PriceEntered: Actual [{}], Expected [{}]", actualPrice, expectedPrice);

		boolean isMatch = actualPrice.equalsIgnoreCase(expectedPrice.trim());
		try {
			double actual = Double.parseDouble(actualPrice.replace(",", ""));
			double expected = Double.parseDouble(expectedPrice.replace(",", ""));
			isMatch = Double.compare(actual, expected) == 0;
		} catch (Exception ignored) {
		}

		SoftAssert sa = base.BaseTest.getSoftAssert();
		if (sa != null) {
			sa.assertTrue(isMatch,
					"[PO Line] PriceEntered [" + actualPrice + "] does not match expected [" + expectedPrice + "]");
		} else {
			Assert.assertTrue(isMatch,
					"PriceEntered [" + actualPrice + "] does not match expected [" + expectedPrice + "]");
		}
		if (isMatch) {
			log.info("PriceEntered verified successfully: [{}]", actualPrice);
		} else {
			log.warn("PriceEntered mismatch: Actual [{}], Expected [{}]", actualPrice, expectedPrice);
		}
	}

	public void verifyPriceEntered() {
		verifyPriceEntered(DataManager.getData("priceEntered"));
	}

	public void selectTax(String taxName) {
		page.waitForTimeout(1000);
		log.info("Selecting Tax: [{}]", taxName);
		taxInput.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		taxInput.click();
		taxInput.fill(taxName);
		taxInput.press("Tab");
		page.waitForTimeout(2000);
		WaitUtils.waitForLoadingIndicatorToDisappear(page);
		verifyFieldFilled(taxInput, "Tax", taxName);
		log.info("Selected Tax: [{}]", taxName);
	}

	public void selectTax() {
		String taxValue;
		try {
			taxValue = DataManager.getData("taxType");
		} catch (Exception e) {
			taxValue = DataManager.getData("tax");
		}
		selectTax(taxValue);
	}

	public void verifyPriceList(String expectedPriceList) {
		priceListInput.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		String actualPriceList = priceListInput.inputValue().trim();
		log.info("Verifying PriceList: Actual [{}], Expected [{}]", actualPriceList, expectedPriceList);

		boolean isMatch = actualPriceList.equalsIgnoreCase(expectedPriceList.trim());
		try {
			double actual = Double.parseDouble(actualPriceList.replace(",", ""));
			double expected = Double.parseDouble(expectedPriceList.replace(",", ""));
			isMatch = Double.compare(actual, expected) == 0;
		} catch (Exception ignored) {
		}

		SoftAssert sa = base.BaseTest.getSoftAssert();
		if (sa != null) {
			sa.assertTrue(isMatch,
					"[PO Line] PriceList [" + actualPriceList + "] does not match expected [" + expectedPriceList + "]");
		} else {
			Assert.assertTrue(isMatch,
					"PriceList [" + actualPriceList + "] does not match expected [" + expectedPriceList + "]");
		}
		if (isMatch) {
			log.info("PriceList verified successfully: [{}]", actualPriceList);
		} else {
			log.warn("PriceList mismatch: Actual [{}], Expected [{}]", actualPriceList, expectedPriceList);
		}
	}

	public void verifyPriceList() {
		String priceListVal;
		try {
			priceListVal = DataManager.getData("priceList");
		} catch (Exception e) {
			priceListVal = DataManager.getData("priceEntered");
		}
		verifyPriceList(priceListVal);
	}

	public void verifyLineNetAmt() {
		lineNetAmtInput.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		String actualAmtStr = lineNetAmtInput.inputValue().trim();
		log.info("Verifying LineNetAmt (ListPrice): [{}]", actualAmtStr);

		double price = 0.0;
		double qty = 0.0;

		try {
			String priceStr = priceEnteredInput.inputValue().trim();
			if (priceStr.isEmpty()) {
				priceStr = DataManager.getData("priceEntered");
			}
			price = Double.parseDouble(priceStr.replace(",", ""));
		} catch (Exception e) {
			price = Double.parseDouble(DataManager.getData("priceEntered").replace(",", ""));
		}

		try {
			String qtyStr = qtyEnteredInput.inputValue().trim();
			if (qtyStr.isEmpty()) {
				qtyStr = DataManager.getData("qtyEntered");
			}
			qty = Double.parseDouble(qtyStr.replace(",", ""));
		} catch (Exception e) {
			qty = Double.parseDouble(DataManager.getData("qtyEntered").replace(",", ""));
		}

		double expectedAmt = price * qty;
		this.lastLineNetAmt = expectedAmt;
		double actualAmt = 0.0;
		boolean isMatch = false;
		try {
			actualAmt = Double.parseDouble(actualAmtStr.replace(",", ""));
			isMatch = Math.abs(actualAmt - expectedAmt) < 0.01;
		} catch (Exception e) {
			log.error("Failed to parse LineNetAmt: [{}]", actualAmtStr, e);
		}

		SoftAssert sa = base.BaseTest.getSoftAssert();
		if (sa != null) {
			sa.assertTrue(isMatch,
					"[PO Line] LineNetAmt [" + actualAmtStr + "] does not match calculated (Price [" + price + "] * Qty [" + qty + "] = [" + expectedAmt + "])");
		} else {
			Assert.assertTrue(isMatch,
					"LineNetAmt [" + actualAmtStr + "] does not match calculated (Price [" + price + "] * Qty [" + qty + "] = [" + expectedAmt + "])");
		}
		if (isMatch) {
			log.info("LineNetAmt verified successfully: Actual [{}], Calculated [{} * {} = {}]", actualAmtStr, price, qty, expectedAmt);
		} else {
			log.warn("LineNetAmt mismatch: Actual [{}], Calculated [{} * {} = {}]", actualAmtStr, price, qty, expectedAmt);
		}
	}

	public void verifyLineNetAmt(double expectedAmt) {
		this.lastLineNetAmt = expectedAmt;
		lineNetAmtInput.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		String actualAmtStr = lineNetAmtInput.inputValue().trim();
		boolean isMatch = false;
		try {
			double actualAmt = Double.parseDouble(actualAmtStr.replace(",", ""));
			isMatch = Math.abs(actualAmt - expectedAmt) < 0.01;
		} catch (Exception ignored) {
		}

		SoftAssert sa = base.BaseTest.getSoftAssert();
		if (sa != null) {
			sa.assertTrue(isMatch,
					"[PO Line] LineNetAmt [" + actualAmtStr + "] does not match expected [" + expectedAmt + "]");
		} else {
			Assert.assertTrue(isMatch,
					"LineNetAmt [" + actualAmtStr + "] does not match expected [" + expectedAmt + "]");
		}
	}

	public void verifyListPrice() {
		verifyLineNetAmt();
	}

	public void navigateBackOnPoHeader() {
		log.info("Navigating back to Purchase Order header tab...");
		poHeaderBreadcrumb
				.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		poHeaderBreadcrumb.click();
		page.waitForTimeout(1000);
		WaitUtils.waitForLoadingIndicatorToDisappear(page);
		log.info("Navigated back to Purchase Order header.");
	}
	
	public void verifyDateFields() {

		Locator DateOrdered = page.locator("//span[@instancename='C_Order0DateOrdered']//input[@class='z-datebox-input']");
		String dateOrdered = DateOrdered.inputValue();

		Locator DatePromised = page.locator("//span[@instancename='C_Order0DatePromised']//input[@class='z-datebox-input']");
		String datePromised = DatePromised.inputValue();

		String currentDate = LocalDate.now()
				.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

		SoftAssert sa = base.BaseTest.getSoftAssert();
		if (sa != null) {
			sa.assertEquals(dateOrdered, currentDate,
					"[PO] DateOrdered [" + dateOrdered + "] does not match current date [" + currentDate + "]");
			sa.assertEquals(datePromised, currentDate,
					"[PO] DatePromised [" + datePromised + "] does not match current date [" + currentDate + "]");
		} else {
			Assert.assertEquals(dateOrdered, currentDate);
			Assert.assertEquals(datePromised, currentDate);
		}
		log.info("DateOrdered & DatePromised verified — both should be [{}]", currentDate);
	}
	
	public void verifyDocStatus(String status) {
		Locator DocStatus = page.locator("//span[@instancename='C_Order0DocStatus']//input");
		String actualStatus = DocStatus.inputValue();

		SoftAssert sa = base.BaseTest.getSoftAssert();
		if (sa != null) {
			sa.assertTrue(actualStatus.equalsIgnoreCase(status),
					"[PO] DocStatus mismatch — Expected: [" + status + "] Actual: [" + actualStatus + "]");
		} else {
			if (!actualStatus.equalsIgnoreCase(status)) {
				Assert.fail("docStatus Not Matching, Actual: " + actualStatus + " Expected: " + status);
			}
		}
		if (actualStatus.equalsIgnoreCase(status)) {
			log.info("DocStatus verified successfully: [{}]", actualStatus);
		} else {
			log.warn("DocStatus mismatch — Expected: [{}] Actual: [{}] — soft assertion recorded.", status, actualStatus);
		}
	}
	
	public void verifyBPLocationfields() {
		String bpLocation = partnerLocation.inputValue();
		String bpInvLocation = invoiceLocation.inputValue();
		String expectedLocation = DataManager.getData("bPLocation");
		String expectedInvLocation = DataManager.getData("bpInvoiceLocation");

		SoftAssert sa = base.BaseTest.getSoftAssert();
		if (sa != null) {
			sa.assertEquals(bpLocation, expectedLocation,
					"[PO] BP Partner Location mismatch — Expected: [" + expectedLocation + "] Actual: [" + bpLocation + "]");
			sa.assertEquals(bpInvLocation, expectedInvLocation,
					"[PO] BP Invoice Location mismatch — Expected: [" + expectedInvLocation + "] Actual: [" + bpInvLocation + "]");
		} else {
			Assert.assertEquals(bpLocation, expectedLocation);
			Assert.assertEquals(bpInvLocation, expectedInvLocation);
		}
		log.info("BP Location fields verified for region [{}] — Location: [{}]", DataManager.getRegion(), bpLocation);
	}
	
	public String getPODocNo() {
		Locator documentSequence = page.locator("//input[@title='Document sequence number of the document']");
		String value = documentSequence.inputValue();
		if (value != null && !value.trim().isEmpty()) {
			lastPODocNo = value.trim();
		}
		return value;
	}

	public static String getLastPODocNo() {
		return lastPODocNo;
	}

	public static void setLastPODocNo(String docNo) {
		lastPODocNo = docNo;
	}

	public void verifyAmtFieldsBeforeInvLines() {
		String totalLines = TotalLinesAmt.inputValue();
		String grandTotal = GrandTotalAmt.inputValue();

		SoftAssert sa = base.BaseTest.getSoftAssert();
		if (sa != null) {
			sa.assertEquals(totalLines, "0.00",
					"[PO] TotalLines amount should be 0.00 before adding PO lines, but was: [" + totalLines + "]");
			sa.assertEquals(grandTotal, "0.00",
					"[PO] GrandTotal amount should be 0.00 before adding PO lines, but was: [" + grandTotal + "]");
		} else {
			Assert.assertEquals(totalLines, "0.00");
			Assert.assertEquals(grandTotal, "0.00");
		}
		log.info("Amount fields before PO lines — TotalLines: [{}], GrandTotal: [{}]", totalLines, grandTotal);
	}

	public void verifyCurrency() {
		String actualCurrency = currency.inputValue();
		String expectedCurrency = DataManager.getData("currency");

		SoftAssert sa = base.BaseTest.getSoftAssert();
		if (sa != null) {
			sa.assertEquals(actualCurrency, expectedCurrency,
					"[PO] Currency mismatch — Expected: [" + expectedCurrency + "] Actual: [" + actualCurrency + "]");
		} else {
			Assert.assertEquals(actualCurrency, expectedCurrency);
		}
		log.info("Currency verified for region [{}]: [{}]", DataManager.getRegion(), actualCurrency);
	}

	/**
	 * Gets the expected PO Line amount. Uses the last verified PO Line Net Amount,
	 * or falls back to calculating priceEntered * qtyEntered from test data.
	 *
	 * @return expected PO Line amount
	 */
	public double getExpectedPOLineAmount() {
		if (lastLineNetAmt > 0) {
			return lastLineNetAmt;
		}
		try {
			double price = Double.parseDouble(DataManager.getData("priceEntered").replace(",", ""));
			double qty = Double.parseDouble(DataManager.getData("qtyEntered").replace(",", ""));
			return price * qty;
		} catch (Exception e) {
			log.warn("Could not calculate expected line amount from test data: {}", e.getMessage());
			return 0.0;
		}
	}

	/**
	 * Dynamically extracts and calculates the total tax percentage from the provided taxType in test data.
	 * Examples:
	 * - "SGST 9% + CGST 9%" -> 9.0 + 9.0 = 18.0%
	 * - "GST 18%"           -> 18.0%
	 * - "Customs / Zero-Rated" -> 0.0%
	 *
	 * @return dynamically calculated tax percentage
	 */
	public double getTaxPercentage() {
		String taxStr = "";
		try {
			taxStr = DataManager.getData("taxType");
		} catch (Exception e) {
			try {
				taxStr = DataManager.getData("tax");
			} catch (Exception ignored) {
			}
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
				log.info("Dynamically extracted tax rate [{}%] from taxType: [{}]", totalPercent, taxStr);
				return totalPercent;
			}
		}
		log.info("No percentage found in taxType [{}], defaulting tax rate to 0.0%", taxStr);
		return 0.0;
	}

	/**
	 * Validation 1: On Header, Total Line amount should be equal to PO Lines Line amount.
	 *
	 * @param expectedLineAmt Expected PO Line Net Amount
	 */
	public void verifyHeaderTotalLineAmt(double expectedLineAmt) {
		TotalLinesAmt.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		String actualStr = TotalLinesAmt.inputValue().trim();
		log.info("Verifying Header TotalLinesAmt: Actual [{}], Expected PO Line Amount [{}]", actualStr, expectedLineAmt);

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
					"[PO Header] TotalLines amount [" + actualStr + "] does not match PO Lines Line amount [" + expectedLineAmt + "]");
		} else {
			Assert.assertTrue(isMatch,
					"TotalLines amount [" + actualStr + "] does not match PO Lines Line amount [" + expectedLineAmt + "]");
		}
		if (isMatch) {
			log.info("Header TotalLines amount verified successfully: [{}]", actualStr);
		} else {
			log.warn("Header TotalLines amount mismatch: Actual [{}], Expected [{}]", actualStr, expectedLineAmt);
		}
	}

	public void verifyHeaderTotalLineAmt() {
		verifyHeaderTotalLineAmt(getExpectedPOLineAmount());
	}

	/**
	 * Validation 2: On Header, Grand Total amount should be equal to PO Lines Line amount + taxType.
	 *
	 * @param expectedGrandTotal Expected Grand Total Amount
	 */
	public void verifyHeaderGrandTotalAmt(double expectedGrandTotal) {
		GrandTotalAmt.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		String actualStr = GrandTotalAmt.inputValue().trim();
		log.info("Verifying Header GrandTotalAmt: Actual [{}], Expected [{}]", actualStr, expectedGrandTotal);

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
					"[PO Header] GrandTotal amount [" + actualStr + "] does not match calculated Line amount + Tax [" + expectedGrandTotal + "]");
		} else {
			Assert.assertTrue(isMatch,
					"GrandTotal amount [" + actualStr + "] does not match calculated Line amount + Tax [" + expectedGrandTotal + "]");
		}
		if (isMatch) {
			log.info("Header GrandTotal amount verified successfully: [{}]", actualStr);
		} else {
			log.warn("Header GrandTotal amount mismatch: Actual [{}], Expected [{}]", actualStr, expectedGrandTotal);
		}
	}

	public void verifyHeaderGrandTotalAmt() {
		double lineAmt = getExpectedPOLineAmount();
		double taxRate = getTaxPercentage();
		double taxAmt = (lineAmt * taxRate) / 100.0;
		double expectedGrandTotal = lineAmt + taxAmt;
		log.info("Calculating expected GrandTotal: LineAmt [{}] + Tax [{}% = {}] = [{}]", lineAmt, taxRate, taxAmt, expectedGrandTotal);
		verifyHeaderGrandTotalAmt(expectedGrandTotal);
	}

	/**
	 * Validates both Header Total Line amount and Grand Total amount after returning from PO Line tab.
	 */
	public void verifyAmtFieldsAfterPOLines() {
		verifyHeaderTotalLineAmt();
		verifyHeaderGrandTotalAmt();
	}
}
