package pages;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.microsoft.playwright.Page;

public class SOPMaster {
	private static final Logger log = LogManager.getLogger(SOPMaster.class);
	private Page page;
	
	private String AnnexureTab = "//ul[@role='tablist']//span[text()='Annexure']";
	private String FormatTab = "//ul[@role='tablist']//span[text()='SOP Formats']";

	public SOPMaster(Page page) {
		this.page = page;
	}
	
	
	public void goToAnnexureTab() {
		page.locator(AnnexureTab).click();
		page.waitForTimeout(2000);
		page.locator(AnnexureTab).click();
	}
	
	public void goToSOPFormatTab() {
		page.locator(FormatTab).click();
		page.waitForTimeout(2000);
		page.locator(FormatTab).click();
	}
	
}
