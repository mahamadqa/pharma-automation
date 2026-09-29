package pages;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.LoadState;

public class Dashboard {

	private static final Logger log = LogManager.getLogger(Dashboard.class);
	private Page page;
	protected LoginPage loginPage;
	protected ClientAndRoleSelection clientAndRole;

	private String loginDetails = "//span[@class='desktop-header-font desktop-header-username z-label']";
	private String logOut = "//a[@class='desktop-header-font link z-toolbarbutton']//span[text()='Log Out']";
	private String windowClose = "//i[@class='z-icon-times z-tab-icon']";

	public Dashboard(Page page, LoginPage loginPage, ClientAndRoleSelection clientAndRole) {
		this.page = page;
		this.loginPage = loginPage;
		this.clientAndRole = clientAndRole;
	}

	public void verifyLoginDetails() {
		String loginDetailsText = page.locator(loginDetails).innerText().trim();
		String enteredLoginDetails = new StringBuilder().append(loginPage.UserName).append("@")
				.append(clientAndRole.clientName).append(".*/").append(clientAndRole.loginRole).toString();
		Assert.assertEquals(loginDetailsText, enteredLoginDetails);
		// log.info(loginDetailsText);
	}

	public void menuSearchandNavigateToWindow(String menuName) {
		page.waitForTimeout(1000);
		page.locator("//input[@class='z-bandbox-input']").fill(menuName);
		page.waitForSelector("//div[@class='z-listcell-content']//span[text()='" + menuName + "']");
		page.keyboard().press("Enter");
		page.waitForTimeout(1000);
		log.info("Navigated to " +menuName+ " Window..");
	}
	

	public void clickLogout() {
		page.waitForTimeout(2000);
		page.click(logOut);
		page.waitForTimeout(2000);
		page.waitForLoadState(LoadState.DOMCONTENTLOADED);
		log.info("User Logout..");
	}

	public void closeWindow() {
		page.click(windowClose);
		page.waitForTimeout(2000);
	}

	public void pauseScript() {
		page.pause();
	}

}
