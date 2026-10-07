package pages;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;

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

	public PurchaseOrder(Page page) {
		this.page = page;
		this.orgInput = page.locator("//span[@instancename='C_Order0AD_Org_ID']//input");
		this.targetDocTypeInput = page.locator("//span[@instancename='C_Order0C_DocTypeTarget_ID']//input");
		this.DescInput = page.locator("//textarea[@instancename='C_Order0Description']");
		this.bPartnerInput = page.locator("//div[@instancename='C_Order0C_BPartner_ID']//span//input");
		this.salesRepInput = page.locator("//span[@instancename='C_Order0SalesRep_ID']//input");
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
		this.TotalLinesAmt = page.locator("//div[@instancename='C_Order0TotalLines']//input[contains(@class,'z-decimalbox-disabled')]");
		this.GrandTotalAmt = page.locator("//div[@instancename='C_Order0GrandTotal']//input[contains(@class,'z-decimalbox-disabled')]");
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
		searchPOWindowItem.click();
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
		
		Assert.assertEquals(dateOrdered, currentDate);
		Assert.assertEquals(datePromised, currentDate);
		log.info("DateOrdered & DatePromised are Current Date");
	}
	
	public void verifyDocStatus(String status) {
		Locator DocStatus = page.locator("//span[@instancename='C_Order0DocStatus']//input");
		
		if(DocStatus.inputValue().equalsIgnoreCase(status)) {;
			log.info("docStatus : verified successfully " +DocStatus.inputValue());
		}
		else {
			Assert.fail("docStatus Not Matching, Actual : " +DocStatus.inputValue()+ "Expected : " +status);
		}	
	}
	
	public void verifyBPLocationfields() {
		String BPPartnerLocation = partnerLocation.inputValue();
		String BPPartnerinvLocation = invoiceLocation.inputValue();
		Assert.assertEquals(BPPartnerLocation, DataManager.getData("bPLocation"));
		Assert.assertEquals(BPPartnerinvLocation, DataManager.getData("bpInvoiceLocation"));
		log.info("Both locations are verified for the region " +DataManager.getRegion()+ " " +BPPartnerLocation);
	}
	
	public String getPODocNo() {
		Locator documentSequence = page.locator("//input[@title='Document sequence number of the document']");
		String value = documentSequence.inputValue();
		return value;
	}

	public void verifyAmtFieldsBeforeInvLines() {	
		Assert.assertEquals(TotalLinesAmt.inputValue(), "0.00");
		Assert.assertEquals(GrandTotalAmt.inputValue(), "0.00");
	
	}

	public void verifyCurrency() {
		Assert.assertEquals(currency.inputValue(), DataManager.getData("currency"));
		log.info("Currency is verified for the region " +DataManager.getRegion()+ " : " +currency.inputValue());
	}
	
}
