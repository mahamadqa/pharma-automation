package pages;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.microsoft.playwright.Page;

import base.BaseTest;

public class WorkflowActivities {

	private static final Logger log = LogManager.getLogger(WorkflowActivities.class);
	private Page page;

	private String ZoomButton = "//button[@title='Zoom']";

	public WorkflowActivities(Page page) {
		this.page = page;
	}

	public void clickOnAssignedSOPRecod(String nodeStatus, String DocNo) {
		page.waitForTimeout(2000);
		page.locator("//div[@class='z-north-body z-flex z-flex-column']//div[@class='z-listbox-body']")
				.evaluate("e => e.scrollTop = e.scrollHeight");
		page.waitForTimeout(2000);
		page.waitForSelector(
				"//div[@class='z-listbox z-flex-item']//tbody/tr[last()]//td[2]//div[text()='" + nodeStatus + "']");

		assert page.locator(
				"//div[@class='z-listbox z-flex-item']//tbody/tr[last()]//td[2]//div[text()='" + nodeStatus + "']")
				.textContent().equalsIgnoreCase(nodeStatus);

		assert page
				.locator("//div[@class='z-listbox z-flex-item']//tbody/tr[last()]//td[3]//div[contains(text(),'SOP')]")
				.textContent().contains("SOP " + DocNo);

		BaseTest.click("//div[@class='z-listbox z-flex-item']//tbody/tr[last()]//td[3]//div[contains(text(),'SOP')]");

		log.info("Clicked on Assigned SOP Record with Document No : " + DocNo);
	}

	public void zoomToRecord() {
		page.waitForSelector(ZoomButton);
		BaseTest.click(ZoomButton);
		log.info("Clicked on Zoom Button...");
	}
}
