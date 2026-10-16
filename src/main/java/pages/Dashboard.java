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
		log.info("Desktop header displayed user details: [{}]", actualDetails);

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
		try {
			menuSearchInput.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(15000));
			menuSearchInput.click();
			menuSearchInput.fill("");
			page.waitForTimeout(300);
			menuSearchInput.fill(menuName);
		} catch (Exception e) {
			log.warn("Exception focusing/filling menuSearchInput: {}", e.getMessage());
			try {
				menuSearchInput.fill(menuName);
			} catch (Exception ignored) {
			}
		}

		page.waitForTimeout(1000);
		WaitUtils.waitForLoadingIndicatorToDisappear(page);

		Locator menuItem = page.locator(
				"//div[@class='z-listcell-content']//span[text()='" + menuName + "'] | //td[contains(.,'" + menuName + "')]");
		boolean itemFound = false;
		long timeout = 30000; // Allow up to 30s as option sometimes takes time to display
		long start = System.currentTimeMillis();

		while (System.currentTimeMillis() - start < timeout) {
			try {
				WaitUtils.waitForLoadingIndicatorToDisappear(page);
				if (menuItem.first().isVisible()) {
					itemFound = true;
					log.info("Menu item [{}] displayed after [{}] ms.", menuName, System.currentTimeMillis() - start);
					break;
				}
			} catch (Exception ignored) {
			}

			long elapsed = System.currentTimeMillis() - start;
			if (elapsed > 5000 && elapsed % 5000 < 600) {
				try {
					log.info("Menu search taking time to display [{}], pressing Enter...", menuName);
					menuSearchInput.press("Enter");
				} catch (Exception ignored) {
				}
			}

			page.waitForTimeout(500);
		}

		if (itemFound) {
			try {
				menuItem.first().click(new Locator.ClickOptions().setTimeout(5000));
			} catch (Exception e) {
				log.warn("Clicking menu item [{}] threw exception: {}, trying force click...", menuName, e.getMessage());
				try {
					menuItem.first().click(new Locator.ClickOptions().setForce(true).setTimeout(3000));
				} catch (Exception e2) {
					menuSearchInput.press("Enter");
				}
			}
		} else {
			log.warn("Menu item [{}] not found in dropdown after [{}] ms, pressing Enter as fallback...", menuName,
					timeout);
			menuSearchInput.press("Enter");
		}

		// Wait for window tab to appear
		Locator openedTab = page.locator("//li[contains(@class,'z-tab')]//span[text()='" + menuName
				+ "'] | //li[contains(@class,'z-tab')]//span[contains(text(),'" + menuName + "')]");
		try {
			openedTab.first().waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(20000));
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

}
