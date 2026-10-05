package pages;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.LoadState;
import com.microsoft.playwright.options.WaitForSelectorState;

import base.BaseTest;
import utils.DataManager;

public class PurchaseOrder {

	private static final Logger log = LogManager.getLogger(PurchaseOrder.class);
	private Page page;
		
	
	protected String org = "//span[@instancename='C_Order0AD_Org_ID']//input";
	protected String targetDocType = "//span[@instancename='C_Order0C_DocTypeTarget_ID']//input";
	protected String bPartner = "//div[@instancename='C_Order0C_BPartner_ID']//span//input";
	protected String salesRep = "//span[@instancename='C_Order0SalesRep_ID']//input";
	protected String POLineTab = "//span[text()='PO Line']";
	protected String product = "//div[@instancename='C_OrderLine0M_Product_ID']//input";
	
	
	public PurchaseOrder(Page page) {
        this.page = page;
    }
	
	public void selectOrg(String orgName) {
		BaseTest.fill(org, orgName);
	}

	public void selectOrg() {
		selectOrg(DataManager.getData("org"));
	}
	
	public void selecTtargetDocType(String docType) {
		BaseTest.fill(targetDocType, docType);
	}

	public void selecTtargetDocType() {
		selecTtargetDocType(DataManager.getData("targetDocType"));
	}
	
	public void selectBPartner(String partnerName) {
		BaseTest.fill(bPartner, partnerName);
	}

	public void selectBPartner() {
		selectBPartner(DataManager.getData("bPartner"));
	}
	
	public void selectSalesRep(String repName) {
		BaseTest.fill(salesRep, repName);
	}

	public void selectSalesRep() {
		selectSalesRep(DataManager.getData("salesRep"));
	}
	
	public void clickOnPOLineTab() {
		BaseTest.click(POLineTab);
	}
	
	public void navigateToPOWindow() {
		Locator searchPOWindow = page.locator("//td[@title='Manage Purchase Orders']//span[normalize-space()='Purchase Order']");
		page.locator("//input[@class='z-bandbox-input']").fill("Purchase Order");
		
		searchPOWindow.waitFor(new Locator.WaitForOptions()
		        .setState(WaitForSelectorState.ATTACHED));
		//page.waitForLoadState(LoadState.NETWORKIDLE);
		searchPOWindow.click();	
	}
	
	public void enterProdct(String productName) {
		page.fill(product, productName);
		page.click("//textarea[@instancename='C_OrderLine0Description']");
	}

	public void enterProdct() {
		enterProdct(DataManager.getData("product"));
	}
	
	public void navigateBackOnPoHeader() {
		page.click("//div[@instancename='breadcrumb']//a[text()='Purchase Order']");
	}
	
	
	
	
	
	
}
