package pages;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitForSelectorState;

public class LoginPage {

	private static final Logger log = LogManager.getLogger(LoginPage.class);
	private final Page page;

	public String UserName;

	// Locators - clean, exact selectors without and/or/|
	private final Locator navigateLoginButton;
	private final Locator usernameInput;
	private final Locator passwordInput;
	private final Locator selectRoleCheckbox;
	private final Locator okButton;
	private final Locator loginLogo;
	private final Locator nextScreenLogo;
	private final Locator errorMessage;

	public LoginPage(Page page) {
		this.page = page;
		this.navigateLoginButton = page.locator("//li[@id='main-requestBtns-btnLogin']");
		this.usernameInput = page.locator("//td[@class='login-field']//input[@autocomplete='username']");
		this.passwordInput = page.locator("//td[@class='login-field']//input[@autocomplete='current-password']");
		this.selectRoleCheckbox = page.locator("(//input[@type='checkbox'])[1]");
		this.okButton = page.locator("(//button[@class='login-btn z-button'])[1]");
		this.loginLogo = page.locator("//td[@class='login-box-header-logo']//img[contains(@src, 'data:image')]");
		this.nextScreenLogo = page.locator("//img[@class='z-image']");
		this.errorMessage = page.locator("//div[contains(@class,'z-messagebox')]//span[contains(@class,'z-label')]");
	}

	/**
	 * Navigates to the main login dialog and waits for the username input field to be visible.
	 */
	public void navigateToMainLoginPage() {
		log.info("Navigating to the main login dialog...");
		navigateLoginButton.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		navigateLoginButton.click();
		usernameInput.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		log.info("Login dialog opened successfully; username field is ready.");
	}

	/**
	 * Verifies whether the client logo is visible on the login screen.
	 *
	 * @return true if logo is visible, false otherwise.
	 */
	public boolean isLogoDisplayed() {
		try {
			loginLogo.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
			return loginLogo.isVisible();
		} catch (Exception e) {
			log.warn("Login logo was not visible within timeout: {}", e.getMessage());
			return false;
		}
	}

	/**
	 * Validates that the client logo is displayed on the login screen.
	 */
	public void checkLogoOnLogin() {
		Assert.assertTrue(isLogoDisplayed(), "Client logo is not displaying on the login screen.");
		log.info("Client logo validated successfully on login screen.");
	}

	/**
	 * Enters user credentials, checks the role checkbox if unchecked, clicks submit,
	 * and validates synchronization and successful transition to the Client & Role selection screen.
	 *
	 * @param user Username
	 * @param pass Password
	 */
	public void login(String user, String pass) {
		log.info("Starting login with username: [{}]", user);
		this.UserName = user;

		// 1. Enter username
		usernameInput.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		usernameInput.fill(user);
		log.info("Entered username: [{}]", user);

		// 2. Enter password
		passwordInput.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		passwordInput.fill(pass);
		log.info("Entered password.");

		// 3. Ensure role checkbox is selected
		selectRoleCheckbox.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.ATTACHED).setTimeout(5000));
		if (!selectRoleCheckbox.isChecked()) {
			selectRoleCheckbox.check();
			log.info("Checked 'Select Role' checkbox.");
		}

		// 4. Click OK / Login submit button
		okButton.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(5000));
		okButton.click();
		log.info("Clicked login submit button (OK).");

		// 5. Synchronize and validate post-login transition
		try {
			nextScreenLogo.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(15000));
			Assert.assertTrue(nextScreenLogo.isVisible(), "Client/Role selection logo is not visible after login.");
			log.info("Login successful for user: [{}], transitioned to Client & Role selection screen.", user);
		} catch (Exception e) {
			if (errorMessage.isVisible()) {
				String errText = errorMessage.innerText().trim();
				log.error("Login failed for user [{}] with application error: [{}]", user, errText);
				Assert.fail("Login failed for user [" + user + "] with application error: " + errText);
			} else {
				log.error("Login failed or timed out waiting for next screen: {}", e.getMessage());
				throw e;
			}
		}
	}

	/**
	 * Gets the logged-in username.
	 *
	 * @return username string
	 */
	public String getUserName() {
		return UserName;
	}
}
