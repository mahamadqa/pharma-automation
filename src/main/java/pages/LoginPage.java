package pages;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.LoadState;
import base.BaseTest;

public class LoginPage {

	private static final Logger log = LogManager.getLogger(LoginPage.class);
	private Page page;

	String UserName;
	private String navigateLogin = "//li[@id='main-requestBtns-btnLogin']";
	private String username = "//td[@class='login-field']//input[@autocomplete='username']";
	private String password = "//td[@class='login-field']//input[@autocomplete='current-password']";
	private String selectRole = "(//input[@type='checkbox'])[1]";
	private String ok = "(//button[@class='login-btn z-button'])[1]";

	public LoginPage(Page page) {
		this.page = page;
	}

	public void navigateToMainLoginPage() {
		page.click(navigateLogin);
	}

	public void checkLogoOnLogin() {
		page.waitForLoadState(LoadState.DOMCONTENTLOADED);
		page.locator("//td[@class='login-box-header-logo']//img[contains(@src, 'data:image')]").waitFor();
		Assert.assertTrue(
				page.locator("//td[@class='login-box-header-logo']//img[contains(@src, 'data:image')]").isVisible(),
				"Clients Logo is Not Displaying...");
	}

	public void login(String user, String pass) {
		page.fill(username, user);
		UserName = user;
		//log.info("Username Entered : " + UserName);
		page.fill(password, pass);
		//log.info("Passwords Entered");
		page.click(selectRole);
		BaseTest.click(ok);
		page.waitForLoadState();
		page.waitForLoadState(LoadState.DOMCONTENTLOADED);
		page.locator("//img[@class='z-image']").waitFor();
		Assert.assertTrue(page.locator("//img[@class='z-image']").isVisible(), "Clients Logo is Not Displaying...");
		//log.info("Login Successfull");
	}

//	public void loginWithUser(String user, String pass) {
//		page.fill(username, user);
//		UserName = user;
//		page.fill(password, pass);
//		page.click(selectRole);
//		BaseTest.click(ok);				
//		page.waitForLoadState();
//		page.waitForLoadState(LoadState.DOMCONTENTLOADED);
//		page.locator("//img[@class='z-image']").waitFor();
//        Assert.assertTrue(page.locator("//img[@class='z-image']").isVisible(),
//				"Clients Logo is Not Displaying...");  
//	}

}
