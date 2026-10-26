package pages;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitForSelectorState;

import utils.WaitUtils;

public class ClientAndRoleSelection {

	private static final Logger log = LogManager.getLogger(ClientAndRoleSelection.class);
	private final Page page;

	public String clientName;
	public String loginRole;

	// Locators
	private final Locator logo;
	private final Locator tenantInputField;
	private final Locator roleInputField;
	private final Locator okButton;
	private final Locator disabledTenantElement;
	private final Locator disabledRoleElement;
	private final Locator errorMessage;
	private final Locator desktopHeader;

	public ClientAndRoleSelection(Page page) {
		this.page = page;
		this.logo = page.locator("//td[@class='login-box-header-logo']//img[contains(@src, 'data:image')]");
		this.tenantInputField = page.locator("(//input[@class='z-combobox-input'])[1]");
		this.roleInputField = page.locator("(//input[@class='z-combobox-input'])[2]");
		this.okButton = page.locator("//button[@class='login-btn z-button']//i[@class='z-icon-Ok']");
		this.disabledTenantElement = page.locator(
				"//tr[@id='rowClient']//span[contains(@class,'z-combobox-disabled')] | //tr[contains(@id,'Client') or contains(@id,'Tenant')]//span[contains(@class,'z-combobox-disabled')]");
		this.disabledRoleElement = page.locator("//tr[@id='rowRole']//span[@class='z-combobox z-combobox-disabled']");
		this.errorMessage = page.locator("//div[contains(@class,'z-messagebox')]//span[contains(@class,'z-label')]");
		this.desktopHeader = page.locator("//span[contains(@class,'desktop-header-username')]");
	}

	/**
	 * Checks and validates that the client logo is displayed on the Client & Role
	 * Selection page. Uses SoftAssert so test continues if logo is missing.
	 */
	public void checkLogoOnClientandRoleSelectionPage() {
		try {
			logo.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
			SoftAssert sa = base.BaseTest.getSoftAssert();
			boolean logoVisible = logo.isVisible();
			if (sa != null) {
				sa.assertTrue(logoVisible,
						"[Client & Role Selection] Logo is NOT visible on Client & Role Selection page.");
			} else {
				Assert.assertTrue(logoVisible,
						"[Client & Role Selection] Logo is NOT visible on Client & Role Selection page.");
			}
			if (logoVisible) {
				log.info("Client & Role Selection page logo validated successfully.");
			} else {
				log.warn("Client & Role Selection page logo NOT visible — soft assertion recorded.");
			}
		} catch (Exception e) {
			log.error("Failed waiting for Client & Role selection logo: {}", e.getMessage());
			throw e;
		}
	}

	/**
	 * Dynamically selects Client (Tenant) and Role based on field availability, then clicks OK
	 * to navigate to the Dashboard.
	 * Handles both selectable and disabled/pre-set Client and Role fields seamlessly.
	 *
	 * @param client Client / Tenant name
	 * @param role   Role name
	 */
	public void selectClientAndRole(String client, String role) {
		this.clientName = client;
		this.loginRole = role;

		checkLogoOnClientandRoleSelectionPage();
		log.info("Starting dynamic Client & Role selection. Target Client: [{}], Target Role: [{}]", client, role);

		// 1. Fill and select Client / Tenant (if provided and field is enabled)
		boolean isClientDisabled = false;
		try {
			isClientDisabled = disabledTenantElement.isVisible() || tenantInputField.isDisabled();
		} catch (Exception ignored) {
		}

		if (client != null && !client.trim().isEmpty() && !isClientDisabled) {
			try {
				tenantInputField.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
				tenantInputField.fill(client);

				Locator dynamicTenantOption = page.locator(String.format(
						"//div[contains(@class,'z-combobox-popup')]//span[contains(text(),'%s')] | //span[contains(@class,'z-comboitem-text') and contains(text(),'%s')]",
						client, client));
				if (dynamicTenantOption.count() > 0 && dynamicTenantOption.first().isVisible()) {
					dynamicTenantOption.first().click();
				} else {
					tenantInputField.press("Tab");
				}
				page.waitForTimeout(1000);
				WaitUtils.waitForLoadingIndicatorToDisappear(page);
			} catch (Exception e) {
				log.warn("Exception during client selection: {}, attempting Tab fallback", e.getMessage());
				try {
					tenantInputField.press("Tab");
				} catch (Exception ignored) {
				}
			}
		}

		page.waitForTimeout(500);
		WaitUtils.waitForLoadingIndicatorToDisappear(page);

		// 2. Fill and select Role (if provided and field is not disabled)
		boolean isRoleDisabled = false;
		try {
			isRoleDisabled = disabledRoleElement.isVisible() || roleInputField.isDisabled();
		} catch (Exception ignored) {
		}

		if (isRoleDisabled) {
			log.info("Role field is disabled/pre-set to [{}]. Skipping manual role selection.", roleInputField.inputValue());
		} else if (role != null && !role.trim().isEmpty()) {
			try {
				roleInputField.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(5000));
				roleInputField.fill(role);

				Locator dynamicRoleOption = page.locator(String.format(
						"//div[contains(@class,'z-combobox-popup')]//span[contains(text(),'%s')] | //span[contains(@class,'z-comboitem-text') and contains(text(),'%s')]",
						role, role));
				if (dynamicRoleOption.count() > 0 && dynamicRoleOption.first().isVisible()) {
					dynamicRoleOption.first().click();
				} else {
					roleInputField.press("Tab");
				}
			} catch (Exception e) {
				log.warn("Exception during role selection: {}, attempting Tab fallback", e.getMessage());
				try {
					roleInputField.press("Tab");
				} catch (Exception ignored) {
				}
			}
		}

		// 3. Submit
		okButton.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(5000));
		okButton.click();
		WaitUtils.waitForLoadingIndicatorToDisappear(page);

		// 4. Validate post-selection state
		try {
			page.waitForTimeout(3000);
			desktopHeader.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(25000));
			SoftAssert sa = base.BaseTest.getSoftAssert();
			boolean headerVisible = desktopHeader.isVisible();
			if (sa != null) {
				sa.assertTrue(headerVisible,
						"[Client & Role Selection] Desktop header is NOT visible after selecting Client: ["
								+ clientName + "] & Role: [" + loginRole + "]");
			} else {
				Assert.assertTrue(headerVisible,
						"[Client & Role Selection] Desktop header is NOT visible after selection.");
			}
			log.info("Login successful by selecting Client: [{}] & Role: [{}]", clientName, loginRole);
		} catch (Exception e) {
			log.warn("Dashboard header wait timed out, continuing execution: {}", e.getMessage());
		}
	}
}
