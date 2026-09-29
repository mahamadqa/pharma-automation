package pages;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.LoadState;
import base.BaseTest;

public class ClientAndRoleSelection {


	private static final Logger log = LogManager.getLogger(ClientAndRoleSelection.class);
	private Page page;

	String clientName;
	String loginRole;
	private String tenantField = "(//input[@class='z-combobox-input'])[1]";
	private String roleField = "(//input[@class='z-combobox-input'])[2]";
	private String ok = "//button[@class='login-btn z-button']//i[@class='z-icon-Ok']";
	private String tenantFieldName = "(//span[contains(text(),'PharmaVerge')])[2]";
	private String disabledRole = "//tr[@id='rowRole']//span[@class='z-combobox z-combobox-disabled']";

	public ClientAndRoleSelection(Page page) {
		
		this.page = page;	
	}

	public void checkLogoOnClientandRoleSelectionPage() {
		page.waitForLoadState(LoadState.DOMCONTENTLOADED);
		page.locator("//td[@class='login-box-header-logo']//img[contains(@src, 'data:image')]").waitFor();
		Assert.assertTrue(
				page.locator("//td[@class='login-box-header-logo']//img[contains(@src, 'data:image')]").isVisible(),
				"Clients Logo is Not Displaying...");
		//log.info("Logo displayed");
	}

	public void selectClientAndRole(String client, String role) {
		page.waitForTimeout(500);
		clientName = client;
		loginRole = role;
		checkLogoOnClientandRoleSelectionPage();
		
		page.fill(tenantField, client);
		page.locator(tenantFieldName).click();
		//log.info("Selected client : " +clientName);
		page.locator(roleField).isVisible();
		page.fill(roleField, role);
		//log.info("Selected role : " +loginRole);
		page.waitForTimeout(1000);
		BaseTest.click(ok);
		page.waitForLoadState();
		log.info("login successfull by selecting client " +clientName+ " & Role " +loginRole);
		page.waitForTimeout(1000);
	}

	public void selectRoleOfUser(String client, String role) {
		// page.fill(tenantField, client); (//span[@class="z-combobox z-combobox-disabled"])[2]//input
		clientName = client;
		loginRole = role;	
		
		if (page.locator(disabledRole).isVisible()) {
			log.info("Client : " +client);
			log.info("Role : " + role);
			page.waitForTimeout(1000);
		} 
		else {
			page.fill(roleField, role);
			log.info("Selected role : " + role);
			page.waitForTimeout(1000);	
		}
		BaseTest.click(ok);
		page.waitForLoadState();
		page.waitForTimeout(1000);
	}

}
