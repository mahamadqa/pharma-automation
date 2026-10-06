package pages;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitForSelectorState;

public class WorkflowActivities {

	private static final Logger log = LogManager.getLogger(WorkflowActivities.class);
	private final Page page;

	// Locators - clean single selectors without and/or/|
	private final Locator zoomButton;
	private final Locator listboxScrollBody;

	protected Dashboard dashboard;

	public WorkflowActivities(Page page) {
		this(page, null);
	}

	public WorkflowActivities(Page page, Dashboard dashboard) {
		this.page = page;
		this.dashboard = dashboard;
		this.zoomButton = page.locator("//button[@title='Zoom']");
		this.listboxScrollBody = page
				.locator("//div[@class='z-north-body z-flex z-flex-column']//div[@class='z-listbox-body']");
	}

	/**
	 * Scrolls to and selects the assigned SOP record matching the given status and
	 * document number.
	 *
	 * @param nodeStatus Expected status (e.g. "Draft", "Approved")
	 * @param docNo      Document Number (e.g. "SOP-001")
	 */
	public void clickOnAssignedSOPRecod(String nodeStatus, String docNo) {
		log.info("Locating assigned SOP Record with Status: [{}] and Document No: [{}]", nodeStatus, docNo);

		// Scroll to bottom of listbox to ensure recent records are rendered
		if (listboxScrollBody.count() > 0 && listboxScrollBody.first().isVisible()) {
			listboxScrollBody.first().evaluate("e => e.scrollTop = e.scrollHeight");
		}

		Locator statusCell = page.locator(
				"//div[@class='z-listbox z-flex-item']//tbody/tr[last()]//td[2]//div[text()='" + nodeStatus + "']");
		statusCell.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(15000));

		String actualStatus = statusCell.innerText().trim();
		Assert.assertTrue(actualStatus.equalsIgnoreCase(nodeStatus),
				"Assigned record status mismatch. Expected: [" + nodeStatus + "], Found: [" + actualStatus + "]");

		Locator sopCell = page
				.locator("//div[@class='z-listbox z-flex-item']//tbody/tr[last()]//td[3]//div[contains(text(),'SOP')]");
		sopCell.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));

		String sopCellText = sopCell.innerText().trim();
		Assert.assertTrue(sopCellText.contains(docNo),
				"Assigned record document number mismatch. Cell text [" + sopCellText + "] does not contain: " + docNo);

		sopCell.click();
		log.info("Selected Assigned SOP Record with Document No: [{}] and Status: [{}]", docNo, nodeStatus);
	}

	/**
	 * Clicks the Zoom button to zoom into the selected workflow record.
	 */
	public void zoomToRecord() {
		log.info("Clicking on Zoom button...");
		zoomButton.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		zoomButton.click();
		log.info("Zoom button clicked successfully.");
	}
}
