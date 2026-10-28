package pages;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.PlaywrightException;
import com.microsoft.playwright.options.WaitForSelectorState;

import utils.WaitUtils;

public class Dashboard {

	private static final Logger log = LogManager.getLogger(Dashboard.class);
	private final Page page;
	protected LoginPage loginPage;
	protected ClientAndRoleSelection clientAndRole;

	// Locators - clean single selectors without and/or/|
	private final Locator usernameLabel;
	private final Locator logoutButton;
	private final Locator windowCloseIcon;
	private final Locator menuSearchInput;

	public Dashboard(Page page, LoginPage loginPage, ClientAndRoleSelection clientAndRole) {
		this.page = page;
		this.loginPage = loginPage;
		this.clientAndRole = clientAndRole;
		this.usernameLabel = page.locator("//span[@class='desktop-header-font desktop-header-username z-label']");
		this.logoutButton = page
				.locator("//a[@class='desktop-header-font link z-toolbarbutton']//span[text()='Log Out']");
		this.windowCloseIcon = page.locator("//i[@class='z-icon-times z-tab-icon']");
		this.menuSearchInput = page.locator("//input[@class='z-bandbox-input']");
	}

	/**
	 * Validates that the desktop header displays the expected username, client, and
	 * role.
	 */
	public void verifyLoginDetails() {
		usernameLabel.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		String actualDetails = usernameLabel.innerText().trim();

		if (loginPage != null && loginPage.UserName != null) {
			Assert.assertTrue(actualDetails.contains(loginPage.UserName),
					"Header user details [" + actualDetails + "] does not contain expected username: "
							+ loginPage.UserName);
		}
		if (clientAndRole != null && clientAndRole.clientName != null) {
			Assert.assertTrue(actualDetails.contains(clientAndRole.clientName),
					"Header user details [" + actualDetails + "] does not contain expected client: "
							+ clientAndRole.clientName);
		}
		if (clientAndRole != null && clientAndRole.loginRole != null) {
			Assert.assertTrue(actualDetails.contains(clientAndRole.loginRole),
					"Header user details [" + actualDetails + "] does not contain expected role: "
							+ clientAndRole.loginRole);
		}
		log.info("Login details verified successfully on Dashboard.");
	}

	/**
	 * Searches for a menu/window in the navigation bandbox and opens it.
	 * Robustly handles cases where the searched option takes time to display.
	 *
	 * @param menuName Window / Menu title to search and open
	 */
	public void menuSearchandNavigateToWindow(String menuName) {
		log.info("Searching and navigating to menu window: [{}]", menuName);
		menuSearchInput.fill(menuName);

		// Simply wait for 3 to 10 sec after providing input
		page.waitForTimeout(5000);

		Locator menuItem = page.locator(
				"//div[@class='z-listcell-content']//span[text()='" + menuName + "'] | //td[contains(.,'" + menuName
						+ "')]");
		try {
			menuItem.first().waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(5000));
			menuItem.first().click();
		} catch (Exception e) {
			log.warn("Menu item [{}] not visible after wait, pressing Enter: {}", menuName, e.getMessage());
			menuSearchInput.press("Enter");
		}

		// Wait for window tab to appear
		Locator openedTab = page.locator("//li[contains(@class,'z-tab')]//span[text()='" + menuName
				+ "'] | //li[contains(@class,'z-tab')]//span[contains(text(),'" + menuName + "')]");
		try {
			openedTab.first()
					.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(15000));
			log.info("Navigated to [{}] Window successfully.", menuName);
		} catch (Exception e) {
			log.warn("Opened tab for [{}] was not immediately visible, continuing: {}", menuName, e.getMessage());
		}
	}

	/**
	 * Clicks the Logout button and waits for the login screen to return.
	 */
	public void clickLogout() {
		log.info("Attempting to click Logout...");
		// Dismiss any modal dialog or message box if present
		Locator modalClose = page.locator("div.z-window-modal div.z-window-close, div.z-window-highlighted div.z-window-close, div.z-messagebox div.z-window-close, div.z-messagebox button, div.z-window-modal button.btn-primary, div.z-window-highlighted button.btn-primary");
		if (modalClose.count() > 0 && modalClose.first().isVisible()) {
			try {
				modalClose.first().click(new Locator.ClickOptions().setTimeout(2000));
				page.waitForTimeout(500);
			} catch (Exception ignored) {
			}
		}

		logoutButton.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		try {
			logoutButton.click(new Locator.ClickOptions().setTimeout(5000));
			log.info("User logged out successfully and returned to login screen.");
		} catch (Exception e) {
			log.warn("Standard click on logout button blocked, attempting force click: {}", e.getMessage());
			logoutButton.click(new Locator.ClickOptions().setForce(true));
			log.info("User logged out successfully and returned to login screen..");
		}
		page.waitForTimeout(2000);
		page.locator("//td[@class='login-field']//input[@autocomplete='username']").waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
	}

	/**
	 * Switches focus back to the Home / Dashboard tab.
	 */
	public void clickHomeTab() {
		log.info("Switching to Home / Dashboard tab...");
		Locator homeTab = page.locator("//li[contains(@class,'z-tab')]//span[contains(text(),'Home')]");
		if (homeTab.count() > 0 && homeTab.first().isVisible()) {
			homeTab.first().click();
			page.waitForTimeout(1000);
			WaitUtils.waitForLoadingIndicatorToDisappear(page);
		}
	}

	/**
	 * Searches for a window in Menu Search and clicks the '+' (New) icon next to it.
	 *
	 * @param menuName Window title to search and initialize new record for
	 */
	public void menuSearchAndClickNew(String menuName) {
		log.info("Searching [{}] in menu searchbox and clicking '+' icon...", menuName);
		clickHomeTab();
		menuSearchInput.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		menuSearchInput.click();
		menuSearchInput.fill(menuName);
		page.waitForTimeout(3000);

		Locator newIcon = page.locator(
				"//div[contains(@class,'z-bandbox-popup')]//tr[contains(.,'" + menuName + "')]//a[@title='New'] | "
						+ "//tr[contains(.,'" + menuName + "')]//a[@title='New'] | "
						+ "//span[contains(text(),'" + menuName + "')]/ancestor::tr//a[@title='New']").first();
		try {
			newIcon.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(5000));
			newIcon.click();
		} catch (Exception e) {
			log.warn("New '+' icon not clicked directly for [{}], attempting Enter: {}", menuName, e.getMessage());
			menuSearchInput.press("Enter");
		}
		page.waitForTimeout(2000);
		WaitUtils.waitForLoadingIndicatorToDisappear(page);
		log.info("Navigated to [{}] window with new record.", menuName);
	}

	/**
	 * Clicks a document status indicator under the Activities widget on Dashboard.
	 *
	 * @param indicatorName Indicator title (e.g. "SOP Request", "SOP Format Request")
	 */
	public void clickDocumentStatusIndicator(String indicatorName) {
		log.info("Clicking on [{}] document status indicator from Activities dashboard...", indicatorName);
		clickHomeTab();

		// Refresh activities widget if refresh icon is present
		Locator refreshBtn = page.locator(
				"//div[contains(@class,'dashboard-widget') and .//div[contains(.,'Activities')]]//a[@title='ReQuery']");
		if (refreshBtn.count() > 0 && refreshBtn.first().isVisible()) {
			try {
				refreshBtn.first().click();
				page.waitForTimeout(1000);
				WaitUtils.waitForLoadingIndicatorToDisappear(page);
			} catch (Exception ignored) {
			}
		}

		Locator indicator = page.locator(
				"//div[contains(@class,'activities-box')]//span[contains(text(),'" + indicatorName + "')] | "
						+ "//div[contains(@class,'activities-box')]//div[contains(.,'" + indicatorName + "')] | "
						+ "//span[contains(@class,'z-label') and contains(text(),'" + indicatorName + "')]").first();
		try {
			indicator.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(15000));
			indicator.click();
		} catch (Exception e) {
			log.warn("Activity indicator click fallback: {}", e.getMessage());
			indicator.click(new Locator.ClickOptions().setForce(true));
		}
		page.waitForTimeout(3000);
		WaitUtils.waitForLoadingIndicatorToDisappear(page);
		log.info("Clicked on [{}] document status indicator.", indicatorName);
	}

	/**
	 * Closes the currently active window tab.
	 */
	public void closeWindow() {
		windowCloseIcon.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(5000));
		windowCloseIcon.click();
	}

}
