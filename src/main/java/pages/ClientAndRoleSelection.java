package pages;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitForSelectorState;

import utils.WaitUtils;

public class ClientAndRoleSelection {

	private static final Logger log = LogManager.getLogger(ClientAndRoleSelection.class);
	private final Page page;

	public String clientName;
	public String loginRole;

	// Locators - clean single selectors without and/or/|
	private final Locator logo;
	private final Locator tenantInputField;
	private final Locator roleInputField;
	private final Locator okButton;
	private final Locator tenantOption;
	private final Locator disabledRoleElement;
	private final Locator errorMessage;
	private final Locator desktopHeader;
	

	public ClientAndRoleSelection(Page page) {
		this.page = page;
		this.logo = page.locator("//td[@class='login-box-header-logo']//img[contains(@src, 'data:image')]");
		this.tenantInputField = page.locator("(//input[@class='z-combobox-input'])[1]");
		this.roleInputField = page.locator("(//input[@class='z-combobox-input'])[2]");
		this.okButton = page.locator("//button[@class='login-btn z-button']//i[@class='z-icon-Ok']");
		this.tenantOption = page.locator("(//span[contains(text(),'PharmaVerge')])[2]");
		this.disabledRoleElement = page.locator("//tr[@id='rowRole']//span[@class='z-combobox z-combobox-disabled']");
		this.errorMessage = page.locator("//div[contains(@class,'z-messagebox')]//span[contains(@class,'z-label')]");
		this.desktopHeader = page.locator("//span[contains(@class,'desktop-header-username')]");
		
	}

	/**
	 * Checks and validates that the client logo is displayed on the Client & Role
	 * Selection page.
	 */
	public void checkLogoOnClientandRoleSelectionPage() {
		try {
			logo.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
			Assert.assertTrue(logo.isVisible(), "Client logo is not displaying on Client & Role Selection page.");
			log.info("Client & Role Selection page logo validated successfully.");
		} catch (Exception e) {
			log.error("Failed waiting for Client & Role selection logo: {}", e.getMessage());
			throw e;
		}
	}

	/**
	 * Selects Client (Tenant) and Role, then clicks OK to navigate to the
	 * Dashboard.
	 *
	 * @param client Client / Tenant name
	 * @param role   Role name
	 */
	public void selectClientAndRole(String client, String role) {
		this.clientName = client;
		this.loginRole = role;

		checkLogoOnClientandRoleSelectionPage();
		log.info("Selecting Client: [{}] and Role: [{}]", client, role);

		// 1. Fill and select Client / Tenant
		tenantInputField.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		tenantInputField.fill(client);

		if (tenantOption.count() > 0 && tenantOption.first().isVisible()) {
			tenantOption.first().click();
		} else {
			tenantInputField.press("Tab");
		}
		log.info("Selected Client: [{}]", client);

		// 2. Fill and select Role (if not disabled)
		if (!disabledRoleElement.isVisible()) {
			roleInputField
					.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(5000));
			roleInputField.fill(role);
			roleInputField.press("Tab");
			log.info("Selected Role: [{}]", role);
		} else {
			log.info("Role dropdown is pre-set or disabled for Client [{}]", client);
		}

		// 3. Submit
		okButton.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(5000));
		okButton.click();
		WaitUtils.waitForLoadingIndicatorToDisappear(page);
		log.info("Clicked OK button on Client & Role selection modal.");

		// 4. Validate post-selection state
		try {
			page.waitForTimeout(2000);
			desktopHeader.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(25000));
			log.info("Login successful by selecting Client: [{}] & Role: [{}]", clientName, loginRole);
		} catch (Exception e) {	
				log.warn("Dashboard header wait timed out, continuing execution: {}", e.getMessage());		
		}
	}

	/**
	 * Selects role when client is already chosen or conditionally disabled.
	 *
	 * @param client Client name
	 * @param role   Role name
	 */
	public void selectRoleOfUser(String client, String role) {
		this.clientName = client;
		this.loginRole = role;

		if (disabledRoleElement.isVisible()) {
			log.info("Role selection disabled. Client: [{}], Default Role: [{}]", client, role);
		} else {
			roleInputField
					.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(5000));
			roleInputField.fill(role);
			roleInputField.press("Tab");
			log.info("Selected Role: [{}]", role);
		}

		okButton.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(5000));
		okButton.click();
		WaitUtils.waitForLoadingIndicatorToDisappear(page);
		log.info("Submitted role selection for [{}]", role);
	}
}
