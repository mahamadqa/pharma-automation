package pages;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitForSelectorState;

import utils.WaitUtils;

/**
 * Page Object Class for the Attribute Set Instance Window.
 */
public class AttributeSetInstance {

	private static final Logger log = LogManager.getLogger(AttributeSetInstance.class);
	private final Page page;

	// ==================== Header & Tab Locators ====================
	private final Locator asiTabHeader;
	private final Locator asiBreadcrumb;
	private final Locator descriptionInput;
	private final Locator activeCheckbox;
	private final Locator productHeaderInput;
	private final Locator closeWindowButton;

	// ==================== Subtab Locators ====================
	private final Locator locatedAtTab;
	private final Locator locatedAtProductInput;
	private final Locator qtyOnHandInput;
	private final Locator locatorInput;
	private final Locator subtabGridToggle;

	// ==================== Cost Subtab Locators ====================
	private final Locator costTab;
	private final Locator costingMethodInput;
	private final Locator costElementInput;
	private final Locator accumulatedAmtInput;
	private final Locator subtabNextRecordButton;
	private final Locator subtabRecordInfo;

	public AttributeSetInstance(Page page) {
		this.page = page;

		// Header & Window
		this.asiTabHeader = page.locator(
				"//li[contains(@class,'z-tab')]//span[normalize-space(text())='Attribute Set Instance']");
		this.asiBreadcrumb = page.locator(
				"//div[@instancename='breadcrumb']//span[text()='Attribute Set Instance'] | //div[@instancename='breadcrumb']//a[contains(text(),'Attribute Set Instance')]");
		this.descriptionInput = page.locator(
				"//div[@instancename='M_AttributeSetInstance0Description']//input | //input[@instancename='M_AttributeSetInstance0Description']");
		this.activeCheckbox = page.locator(
				"//span[@instancename='M_AttributeSetInstance0IsActive']//input");
		this.productHeaderInput = page.locator(
				"//div[@instancename='M_AttributeSetInstance0M_Product_ID']//input");
		this.closeWindowButton = page.locator(
				"//li[contains(@class,'z-tab') and contains(.,'Attribute Set Instance')]//i[@aria-label='Close'] | (//div[@class='z-tab-button']//i[@aria-label='Close'])[last()]");

		// Located At Subtab
		this.locatedAtTab = page.locator(
				"//li[contains(@class,'z-tab')]//span[normalize-space(text())='Located At']");
		this.locatedAtProductInput = page.locator(
				"//div[@instancename='M_Storage0M_Product_ID']//input | (//div[contains(@instancename,'M_Storage') and contains(@instancename,'M_Product_ID')]//input)[1]");
		this.qtyOnHandInput = page.locator(
				"(//div[@instancename='M_Storage0QtyOnHand']//input)[1]");
		this.locatorInput = page.locator(
				"//div[@instancename='M_Storage0M_Locator_ID']//input | (//div[contains(@instancename,'M_Storage') and contains(@instancename,'Locator')]//input)[1]");
		this.subtabGridToggle = page.locator(
				"(//div[contains(@class,'z-tabpanel') and not(contains(@style,'display: none')) and not(contains(@style,'display:none'))]//a[contains(@title,'Shift+Alt+T')])[1] | (//div[contains(@class,'adwindow-detailpane-toolbar')]//a[contains(@title,'Shift+Alt+T')])[1]");

		// Cost Subtab
		this.costTab = page.locator(
				"//li[contains(@class,'z-tab')]//span[normalize-space(text())='Cost']");
		this.costingMethodInput = page.locator(
				"//div[@instancename='M_Cost0CostingMethod']//input | //span[@instancename='M_Cost0CostingMethod']//input");
		this.costElementInput = page.locator(
				"//div[@instancename='M_Cost0M_CostElement_ID']//input | //span[@instancename='M_Cost0M_CostElement_ID']//input");
		this.accumulatedAmtInput = page.locator(
				"(//div[@instancename='M_Cost0CumulatedAmt']//input)[1] | (//span[@instancename='M_Cost0CumulatedAmt']//input)[1]");
		this.subtabNextRecordButton = page.locator(
				"//div[contains(@class,'adwindow-detailpane') and not(contains(@style,'display: none'))]//a[contains(@title,'Next record')]");
		this.subtabRecordInfo = page.locator(
				"//div[contains(@class,'adwindow-detailpane') and not(contains(@style,'display: none'))]//a[contains(@class,'breadcrumb-record-info')]");
	}

	// ==================== Window Validations ====================

	/**
	 * Verifies that the Attribute Set Instance window/tab is open and active.
	 */
	public void verifyAttributeSetInstanceWindowOpened() {
		page.waitForTimeout(2000);
		WaitUtils.waitForLoadingIndicatorToDisappear(page);
		
		asiTabHeader.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(15000));
		boolean isOpened = asiTabHeader.isVisible();

		SoftAssert sa = base.BaseTest.getSoftAssert();
		if (sa != null) {
			sa.assertTrue(isOpened, "Attribute Set Instance window did not open after Zoom action!");
		} else {
			Assert.assertTrue(isOpened, "Attribute Set Instance window did not open after Zoom action!");
		}
		log.info("✅ Verified Attribute Set Instance window is successfully opened.");
	}

	/**
	 * Gets the Description value from the Attribute Set Instance header.
	 *
	 * @return Description field text
	 */
	public String getDescription() {
		descriptionInput.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		return descriptionInput.inputValue().trim();
	}

	/**
	 * Matches the Description in Attribute Set Instance window with the Receipt Line's
	 * Attribute Set Instance field value.
	 *
	 * @param expectedASI Expected Attribute Set Instance text from Receipt Line
	 */
	public void verifyDescription(String expectedASI) {
		page.waitForTimeout(1000);
		String actualDescription = getDescription();

		boolean isMatch = actualDescription.equalsIgnoreCase(expectedASI.trim())
				|| actualDescription.contains(expectedASI.trim())
				|| expectedASI.trim().contains(actualDescription);

		SoftAssert sa = base.BaseTest.getSoftAssert();
		if (sa != null) {
			sa.assertTrue(isMatch, "[ASI] Description [" + actualDescription
					+ "] does not match Receipt Line Attribute Set Instance [" + expectedASI + "]");
		} else {
			Assert.assertTrue(isMatch, "[ASI] Description [" + actualDescription
					+ "] does not match Receipt Line Attribute Set Instance [" + expectedASI + "]");
		}

		if (isMatch) {
			log.info("✅ Description matched with Receipt Line Attribute Set Instance: [{}]", actualDescription);
		} else {
			log.warn("⚠️ Description mismatch: Actual [{}], Expected [{}]", actualDescription, expectedASI);
		}
	}

	// ==================== Located At Subtab Actions ====================

	/**
	 * Navigates to the Located At subtab and ensures it is displayed in Form View.
	 */
	public void clickOnLocatedAtTab() {
		page.waitForTimeout(1000);
		locatedAtTab.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		locatedAtTab.click();
		page.waitForTimeout(2000);
		locatedAtTab.click();
		WaitUtils.waitForLoadingIndicatorToDisappear(page);
		ensureLocatedAtFormView();
		log.info("Navigated to 'Located At' subtab in Form View.");
	}

	/**
	 * Checks if the Located At subtab opened in Grid View by default, and toggles
	 * to Form View if needed.
	 */
	public void ensureLocatedAtFormView() {
		try {
			page.waitForTimeout(1000);
			boolean isProductVisible = locatedAtProductInput.count() > 0 && locatedAtProductInput.first().isVisible();
			if (!isProductVisible) {
				if (subtabGridToggle.count() > 0 && subtabGridToggle.first().isVisible()) {
					subtabGridToggle.first().click();
				} else {
					page.keyboard().press("Shift+Alt+T");
				}
				page.waitForTimeout(1500);
				WaitUtils.waitForLoadingIndicatorToDisappear(page);
			}
		} catch (Exception e) {
			log.warn("Could not check/toggle Located At subtab grid view: {}", e.getMessage());
		}
	}

	/**
	 * Gets the Product value from the Located At subtab.
	 *
	 * @return Product input value
	 */
	public String getProduct() {
		locatedAtProductInput.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		return locatedAtProductInput.inputValue().trim();
	}

	/**
	 * Verifies the Product field in the Located At subtab against expected Product name.
	 *
	 * @param expectedProduct Expected Product name
	 */
	public void verifyProduct(String expectedProduct) {
		page.waitForTimeout(1000);
		String actualProduct = getProduct();

		boolean isMatch = actualProduct.equalsIgnoreCase(expectedProduct.trim())
				|| actualProduct.contains(expectedProduct.trim())
				|| expectedProduct.trim().contains(actualProduct);

		SoftAssert sa = base.BaseTest.getSoftAssert();
		if (sa != null) {
			sa.assertTrue(isMatch, "[Located At] Product [" + actualProduct + "] does not match expected [" + expectedProduct + "]");
		} else {
			Assert.assertTrue(isMatch, "Product [" + actualProduct + "] does not match expected [" + expectedProduct + "]");
		}

		if (isMatch) {
			log.info("✅ Located At Product verified successfully: [{}]", actualProduct);
		} else {
			log.warn("⚠️ Located At Product mismatch: Actual [{}], Expected [{}]", actualProduct, expectedProduct);
		}
	}

	/**
	 * Gets the On Hand Quantity value from the Located At subtab.
	 *
	 * @return On Hand Quantity text
	 */
	public String getQtyOnHand() {
		qtyOnHandInput.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		return qtyOnHandInput.inputValue().trim();
	}

	/**
	 * Verifies that On Hand Qty in Located At subtab equals Movement Qty from GRN Receipt Line.
	 *
	 * @param expectedMovementQty Movement Quantity stored from GRN Receipt Line
	 */
	public void verifyQtyOnHand(String expectedMovementQty) {
		page.waitForTimeout(1000);
		String actualQtyOnHand = getQtyOnHand();

		boolean isMatch = actualQtyOnHand.equalsIgnoreCase(expectedMovementQty.trim());
		try {
			double actual = Double.parseDouble(actualQtyOnHand.replace(",", "").trim());
			double expected = Double.parseDouble(expectedMovementQty.replace(",", "").trim());
			isMatch = Double.compare(actual, expected) == 0;
		} catch (Exception ignored) {
		}

		SoftAssert sa = base.BaseTest.getSoftAssert();
		if (sa != null) {
			sa.assertTrue(isMatch, "[Located At] On Hand Qty [" + actualQtyOnHand
					+ "] does not match GRN Movement Qty [" + expectedMovementQty + "]");
		} else {
			Assert.assertTrue(isMatch, "On Hand Qty [" + actualQtyOnHand
					+ "] does not match GRN Movement Qty [" + expectedMovementQty + "]");
		}

		if (isMatch) {
			log.info("✅ Verified On Hand Quantity [{}] equals Movement Quantity [{}] successfully.",
					actualQtyOnHand, expectedMovementQty);
		} else {
			log.error("❌ On Hand Quantity mismatch: Actual [{}], Expected Movement Qty [{}]",
					actualQtyOnHand, expectedMovementQty);
		}
	}

	// ==================== Cost Subtab Actions ====================

	/**
	 * Navigates to the Cost subtab and ensures it is displayed in Form View.
	 */
	public void clickOnCostTab() {
		page.waitForTimeout(1000);

		// If not on ASI tab, click ASI tab header
		if (asiTabHeader.count() > 0 && asiTabHeader.first().isVisible()) {
			try {
				String tabClass = asiTabHeader.first().locator("..").getAttribute("class");
				if (tabClass != null && !tabClass.contains("z-tab-selected")) {
					asiTabHeader.first().click();
					page.waitForTimeout(1000);
				}
			} catch (Exception ignored) {
			}
		}

		costTab.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		costTab.click();
		page.waitForTimeout(2000);
		costTab.click();
		WaitUtils.waitForLoadingIndicatorToDisappear(page);
		ensureCostFormView();
		log.info("Navigated to 'Cost' subtab in Form View.");
	}

	/**
	 * Checks if the Cost subtab is in Grid View and toggles it to Form View.
	 */
	public void ensureCostFormView() {
		try {
			page.waitForTimeout(1000);
			boolean isFormVisible = costingMethodInput.count() > 0 && costingMethodInput.first().isVisible();
			if (!isFormVisible) {
				if (subtabGridToggle.count() > 0 && subtabGridToggle.first().isVisible()) {
					subtabGridToggle.first().click();
				} else {
					page.keyboard().press("Shift+Alt+T");
				}
				page.waitForTimeout(1500);
				WaitUtils.waitForLoadingIndicatorToDisappear(page);
			}
		} catch (Exception e) {
			log.warn("Could not check/toggle Cost subtab grid view: {}", e.getMessage());
		}
	}

	/**
	 * Gets the Costing Method value currently displayed.
	 *
	 * @return Costing Method input text
	 */
	public String getCostingMethod() {
		costingMethodInput.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		return costingMethodInput.inputValue().trim();
	}

	/**
	 * Navigates to the record having the specified Costing Method (e.g. "Average PO").
	 *
	 * @param targetCostingMethod Expected Costing Method name
	 */
	public void navigateToCostingMethod(String targetCostingMethod) {
		page.waitForTimeout(1000);
		log.info("Navigating to record with Costing Method: [{}]...", targetCostingMethod);

		ensureCostFormView();
		String currentMethod = getCostingMethod();

		int maxAttempts = 5;
		int attempts = 0;
		while (!currentMethod.equalsIgnoreCase(targetCostingMethod.trim())
				&& !currentMethod.toLowerCase().contains(targetCostingMethod.toLowerCase().trim())
				&& attempts < maxAttempts) {
			attempts++;

			Locator nextBtn = page.locator("//div[contains(@class,'adwindow-detailpane') and not(contains(@style,'display: none'))]//a[contains(@title,'Next record')]");
			if (nextBtn.count() > 0 && nextBtn.first().isVisible() && !Boolean.TRUE.equals(nextBtn.first().isDisabled())) {
				nextBtn.first().click();
			} else {
				page.keyboard().press("Shift+Alt+ArrowRight");
			}

			page.waitForTimeout(1500);
			WaitUtils.waitForLoadingIndicatorToDisappear(page);
			currentMethod = getCostingMethod();
		}

		log.info("Navigated to record with Costing Method: [{}]", currentMethod);
		SoftAssert sa = base.BaseTest.getSoftAssert();
		boolean isMatch = currentMethod.equalsIgnoreCase(targetCostingMethod.trim())
				|| currentMethod.toLowerCase().contains(targetCostingMethod.toLowerCase().trim());
		if (sa != null) {
			sa.assertTrue(isMatch, "[Cost] Could not find record with Costing Method [" + targetCostingMethod + "]");
		} else {
			Assert.assertTrue(isMatch, "[Cost] Could not find record with Costing Method [" + targetCostingMethod + "]");
		}
	}

	/**
	 * Gets the Accumulated Amount (CumulatedAmt) value from the Cost subtab.
	 *
	 * @return Accumulated Amount text
	 */
	public String getAccumulatedAmt() {
		accumulatedAmtInput.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		return accumulatedAmtInput.inputValue().trim();
	}

	/**
	 * Verifies that the Accumulated Amount equals the expected amount (Posted Debit/Credit).
	 *
	 * @param expectedAmt Expected Amount from Posted Debit/Credit
	 */
	public void verifyAccumulatedAmt(String expectedAmt) {
		page.waitForTimeout(1000);
		String actualAmt = getAccumulatedAmt();

		boolean isMatch = actualAmt.equalsIgnoreCase(expectedAmt.trim());
		try {
			double actual = Double.parseDouble(actualAmt.replace(",", "").trim());
			double expected = Double.parseDouble(expectedAmt.replace(",", "").trim());
			isMatch = Double.compare(actual, expected) == 0;
		} catch (Exception ignored) {
		}

		SoftAssert sa = base.BaseTest.getSoftAssert();
		if (sa != null) {
			sa.assertTrue(isMatch, "[Cost] Accumulated Amount [" + actualAmt
					+ "] does not match expected Posted amount [" + expectedAmt + "]");
		} else {
			Assert.assertTrue(isMatch, "Accumulated Amount [" + actualAmt
					+ "] does not match expected Posted amount [" + expectedAmt + "]");
		}

		if (isMatch) {
			log.info("✅ Verified Accumulated Amount [{}] equals expected Posted amount [{}] successfully.",
					actualAmt, expectedAmt);
		} else {
			log.error("❌ Accumulated Amount mismatch: Actual [{}], Expected [{}]", actualAmt, expectedAmt);
		}
	}

	/**
	 * Closes the Attribute Set Instance window tab.
	 */
	public void closeWindow() {
		if (closeWindowButton.count() > 0 && closeWindowButton.first().isVisible()) {
			closeWindowButton.first().click();
		} else {
			Locator activeClose = page.locator("//li[contains(@class,'z-tab-selected')]//i[@aria-label='Close']");
			if (activeClose.count() > 0 && activeClose.first().isVisible()) {
				activeClose.first().click();
			} else {
				page.keyboard().press("Alt+W");
			}
		}
		page.waitForTimeout(1000);
		WaitUtils.waitForLoadingIndicatorToDisappear(page);
		log.info("Attribute Set Instance window closed.");
	}

	// ==================== Getters ====================

	public Locator getAsiTabHeader() { return asiTabHeader; }
	public Locator getDescriptionInput() { return descriptionInput; }
	public Locator getLocatedAtTab() { return locatedAtTab; }
	public Locator getLocatedAtProductInput() { return locatedAtProductInput; }
	public Locator getQtyOnHandInput() { return qtyOnHandInput; }
	public Locator getLocatorInput() { return locatorInput; }
	public Locator getCostTab() { return costTab; }
	public Locator getCostingMethodInput() { return costingMethodInput; }
	public Locator getCostElementInput() { return costElementInput; }
	public Locator getAccumulatedAmtInput() { return accumulatedAmtInput; }

	public void navigateBackToheader() {
		// TODO Auto-generated method stub
		page.locator("//div[@instancename='breadcrumb']//a[text()='Instance']").click();
	}
}
