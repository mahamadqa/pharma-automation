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
 * Page Object for the System Configuration / System Configurator window.
 * Handles opening via menu searchbox (clicking label, not '+'),
 * searching records in the Lookup Record dialog,
 * verifying and updating configuration values, and saving.
 */
public class SystemConfiguration {

	private static final Logger log = LogManager.getLogger(SystemConfiguration.class);
	private final Page page;

	// Menu Search
	private final Locator menuSearchInput;
	private final Locator systemConfiguratorLabel;

	// Lookup Record Dialog
	private final Locator lookupButton;
	private final Locator lookupNameInput;
	private final Locator lookupOkButton;

	// Form Fields
	private final Locator nameInput;
	private final Locator valueInput;
	private final Locator descriptionInput;

	// Toolbar & Window Tabs
	private final Locator saveButton;
	private final Locator closeWindowButton;
	private final Locator windowTab;

	public SystemConfiguration(Page page) {
		this.page = page;

		// Menu Search Locators
		this.menuSearchInput = page.locator("//input[contains(@class,'z-bandbox-input')] | (//input[@placeholder='Alt+G'])[1]");
		this.systemConfiguratorLabel = page.locator(
				"//div[contains(@class,'z-bandbox-popup')]//tr[contains(.,'System Configurator')]//span[contains(text(),'System Configurator')] | " +
				"//div[contains(@class,'z-bandbox-popup')]//td[contains(@class,'z-listcell')]//span[contains(text(),'System Configurator')] | " +
				"//span[contains(@class,'highlight') and contains(text(),'System Configurator')]");

		// Lookup Record Dialog Locators
		this.lookupButton = page.locator("//a[@title='Lookup Record    Alt+F'] | //a[contains(@title,'Lookup Record')]");
		this.lookupNameInput = page.locator(
				"//div[contains(@class,'z-window')]//input[@instancename='Name'] | " +
				"//input[@instancename='Name']");
		this.lookupOkButton = page.locator(
				"//div[contains(@class,'z-window')]//button[@title='OK'] | " +
				"//button[@title='OK'] | //button[contains(@class,'btn-ok')]");

		// Form Fields on System Configurator Window
		this.nameInput = page.locator(
				"//input[@instancename='AD_SysConfig0Name'] | " +
				"//input[contains(@instancename,'SysConfig') and contains(@instancename,'Name')]");
		this.valueInput = page.locator(
				"//input[@instancename='AD_SysConfig0Value'] | " +
				"//input[contains(@instancename,'SysConfig') and contains(@instancename,'Value')]");
		this.descriptionInput = page.locator(
				"//input[@instancename='AD_SysConfig0Description'] | " +
				"//input[contains(@instancename,'SysConfig') and contains(@instancename,'Description')]");

		// Toolbar & Window
		this.saveButton = page.locator(
				"//div[contains(@class,'adwindow-toolbar')]//a[@title='Save Changes    Alt+S'] | " +
				"//a[@title='Save Changes    Alt+S'] | (//a[contains(@title,'Save Changes')])[1]");
		this.closeWindowButton = page.locator(
				"//li[contains(@class,'z-tab') and contains(.,'System Configurator')]//i[contains(@class,'z-tab-icon') or contains(@class,'z-icon-times')] | " +
				"//div[@class='z-tab-button']//i[@aria-label='Close'] | " +
				"//i[@class='z-icon-times z-tab-icon']");
		this.windowTab = page.locator("//li[contains(@class,'z-tab')]//span[contains(text(),'System Configurator')]");
	}

	/**
	 * Opens System Configurator window through menu searchbox.
	 * Explicitly clicks on the label/row, NOT on the '+' button.
	 */
	public void openSystemConfiguratorWindow() {
		log.info("Searching 'System Configurator' in menu searchbox and clicking on the label (not '+' button)...");
		menuSearchInput.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		menuSearchInput.click();
		menuSearchInput.fill("System Configurator");
		page.waitForTimeout(3000);

		systemConfiguratorLabel.first().waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		systemConfiguratorLabel.first().click();
		page.waitForTimeout(2000);
		WaitUtils.waitForLoadingIndicatorToDisappear(page);

		windowTab.first().waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(15000));
		log.info("System Configurator window opened successfully.");
	}

	/**
	 * Searches for a record in the Lookup Record dialog by filling the Name field and clicking OK.
	 *
	 * @param configName Name of the System Configuration record (e.g. SOP_Effective_TNI_Check)
	 */
	public void searchRecordInLookup(String configName) {
		log.info("Looking up System Configuration record with Name: [{}]...", configName);

		// If Lookup Record dialog is not automatically opened, click Toolbar 'Lookup Record (Alt+F)' button
		boolean isLookupOpen = false;
		try {
			isLookupOpen = lookupNameInput.first().isVisible();
		} catch (Exception ignored) {
		}

		if (!isLookupOpen) {
			log.info("Lookup Record dialog not open automatically, clicking Toolbar Lookup Record button...");
			lookupButton.first().waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(5000));
			lookupButton.first().click();
			page.waitForTimeout(1000);
		}

		lookupNameInput.first().waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		lookupNameInput.first().fill(configName);
		page.waitForTimeout(500);

		lookupOkButton.first().waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(5000));
		lookupOkButton.first().click();
		page.waitForTimeout(2000);
		WaitUtils.waitForLoadingIndicatorToDisappear(page);

		// Validate that the record is loaded
		nameInput.first().waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(15000));
		String actualName = nameInput.first().inputValue().trim();
		log.info("System Configuration record loaded. Name: [{}]", actualName);
		Assert.assertTrue(actualName.equalsIgnoreCase(configName),
				"Loaded System Config record name [" + actualName + "] does not match expected: [" + configName + "]");
	}

	/**
	 * Gets the current configured Value for the loaded record.
	 *
	 * @return Configured value string
	 */
	public String getConfiguredValue() {
		valueInput.first().waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(5000));
		return valueInput.first().inputValue().trim();
	}

	/**
	 * Checks if the configured value matches expected value.
	 * If it does not match, updates it to expectedValue and saves the record.
	 *
	 * @param expectedValue Expected configured value (e.g. "D")
	 */
	public void verifyOrUpdateConfigValue(String expectedValue) {
		String actualValue = getConfiguredValue();
		log.info("Current configured value for record is: [{}] (Expected: [{}])", actualValue, expectedValue);

		if (actualValue.equalsIgnoreCase(expectedValue)) {
			log.info("Configured value is already set to [{}]. No update required.", expectedValue);
		} else {
			log.info("Configured value [{}] does NOT match expected [{}]. Updating to [{}] and saving...",
					actualValue, expectedValue, expectedValue);
			valueInput.first().fill(expectedValue);
			valueInput.first().press("Tab");
			page.waitForTimeout(500);

			saveButton.first().waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(5000));
			saveButton.first().click();
			page.waitForTimeout(1000);
			WaitUtils.waitForLoadingIndicatorToDisappear(page);

			String updatedValue = getConfiguredValue();
			log.info("Record saved. Verified updated configured value: [{}]", updatedValue);
			Assert.assertEquals(updatedValue, expectedValue,
					"Updated configured value [" + updatedValue + "] does not match expected [" + expectedValue + "]");
		}
	}

	/**
	 * Convenience reusable method to complete the entire verification and update flow:
	 * 1. Open System Configurator window (via label)
	 * 2. Search record by name in lookup
	 * 3. Check value == expectedValue, update and save if not
	 * 4. Close the window
	 *
	 * @param configName    Name of the configuration record
	 * @param expectedValue Expected configured value
	 */
	public void verifyOrUpdateSystemConfig(String configName, String expectedValue) {
		openSystemConfiguratorWindow();
		searchRecordInLookup(configName);
		verifyOrUpdateConfigValue(expectedValue);
		closeWindow();
	}

	/**
	 * Closes the System Configurator window tab.
	 */
	public void closeWindow() {
		log.info("Closing System Configurator window...");
		try {
			if (closeWindowButton.first().isVisible()) {
				closeWindowButton.first().click();
				page.waitForTimeout(500);
				WaitUtils.waitForLoadingIndicatorToDisappear(page);
			}
		} catch (Exception e) {
			log.warn("Could not close System Configurator tab directly: {}", e.getMessage());
		}
	}

}
