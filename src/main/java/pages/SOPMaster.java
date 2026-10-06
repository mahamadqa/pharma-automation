package pages;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitForSelectorState;

public class SOPMaster {

	private static final Logger log = LogManager.getLogger(SOPMaster.class);
	private final Page page;

	// Locators - clean single selectors without and/or/|
	private final Locator annexureTab;
	private final Locator formatTab;

	protected Dashboard dashboard;

	public SOPMaster(Page page) {
		this(page, null);
	}

	public SOPMaster(Page page, Dashboard dashboard) {
		this.page = page;
		this.dashboard = dashboard;
		this.annexureTab = page.locator("//ul[@role='tablist']//span[text()='Annexure']");
		this.formatTab = page.locator("//ul[@role='tablist']//span[text()='SOP Formats']");
	}

	/**
	 * Navigates to the Annexure tab in SOP Master.
	 */
	public void goToAnnexureTab() {
		log.info("Navigating to 'Annexure' tab...");
		annexureTab.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		annexureTab.click();
		log.info("Opened 'Annexure' tab.");
	}

	/**
	 * Navigates to the SOP Formats tab in SOP Master.
	 */
	public void goToSOPFormatTab() {
		log.info("Navigating to 'SOP Formats' tab...");
		formatTab.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		formatTab.click();
		log.info("Opened 'SOP Formats' tab.");
	}
}
