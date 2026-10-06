package pages;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitForSelectorState;

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
		this.logoutButton = page.locator("//a[@class='desktop-header-font link z-toolbarbutton']//span[text()='Log Out']");
		this.windowCloseIcon = page.locator("//i[@class='z-icon-times z-tab-icon']");
		this.menuSearchInput = page.locator("//input[@class='z-bandbox-input']");
	}

	/**
	 * Validates that the desktop header displays the expected username, client, and role.
	 */
	public void verifyLoginDetails() {
		usernameLabel.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		String actualDetails = usernameLabel.innerText().trim();
		log.info("Desktop header displayed user details: [{}]", actualDetails);

		if (loginPage != null && loginPage.UserName != null) {
			Assert.assertTrue(actualDetails.contains(loginPage.UserName),
					"Header user details [" + actualDetails + "] does not contain expected username: " + loginPage.UserName);
		}
		if (clientAndRole != null && clientAndRole.clientName != null) {
			Assert.assertTrue(actualDetails.contains(clientAndRole.clientName),
					"Header user details [" + actualDetails + "] does not contain expected client: " + clientAndRole.clientName);
		}
		if (clientAndRole != null && clientAndRole.loginRole != null) {
			Assert.assertTrue(actualDetails.contains(clientAndRole.loginRole),
					"Header user details [" + actualDetails + "] does not contain expected role: " + clientAndRole.loginRole);
		}
		log.info("Login details verified successfully on Dashboard.");
	}

	/**
	 * Searches for a menu/window in the navigation bandbox and opens it.
	 *
	 * @param menuName Window / Menu title to search and open
	 */
	public void menuSearchandNavigateToWindow(String menuName) {
		log.info("Searching and navigating to menu window: [{}]", menuName);
		menuSearchInput.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		menuSearchInput.fill(menuName);

		Locator menuItem = page.locator("//div[@class='z-listcell-content']//span[text()='" + menuName + "']");
		try {
			menuItem.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(5000));
			menuItem.click();
		} catch (Exception e) {
			menuSearchInput.press("Enter");
		}

		// Wait for window tab to appear
		Locator openedTab = page.locator("//li[contains(@class,'z-tab')]//span[text()='" + menuName + "']");
		try {
			openedTab.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
			log.info("Navigated to [{}] Window successfully.", menuName);
		} catch (Exception e) {
			log.warn("Opened tab for [{}] was not immediately visible, continuing: {}", menuName, e.getMessage());
		}
	}

	/**
	 * Clicks the Logout button and waits for the login screen to return.
	 */
	public void clickLogout() {
		log.info("Logging out current user...");
		logoutButton.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		logoutButton.click();

		Locator loginBtn = page.locator("//li[@id='main-requestBtns-btnLogin']");
		try {
			loginBtn.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
			log.info("User logged out successfully and returned to login screen.");
		} catch (Exception e) {
			log.warn("Waiting for login button after logout timed out: {}", e.getMessage());
		}
	}

	/**
	 * Closes the currently active window tab.
	 */
	public void closeWindow() {
		log.info("Closing active window tab...");
		windowCloseIcon.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(5000));
		windowCloseIcon.click();
		log.info("Closed active window tab.");
	}

	/**
	 * Pauses script execution for interactive debugging.
	 */
	public void pauseScript() {
		page.pause();
	}
}
