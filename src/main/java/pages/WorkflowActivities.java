package pages;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitForSelectorState;

import utils.WaitUtils;

public class WorkflowActivities {

	private static final Logger log = LogManager.getLogger(WorkflowActivities.class);
	private final Page page;

	private final Locator zoomButton;
	private final Locator listboxScrollBody;
	private final Locator requeryButton;
	private final Locator answerSelect;
	private final Locator okButton;
	private final Locator menuSearchInput;

	protected Dashboard dashboard;

	public WorkflowActivities(Page page) {
		this(page, null);
	}

	public WorkflowActivities(Page page, Dashboard dashboard) {
		this.page = page;
		this.dashboard = dashboard;
		this.zoomButton = page.locator("//button[@title='Zoom']");
		this.listboxScrollBody = page.locator(
				"//div[@class='z-north-body z-flex z-flex-column']//div[@class='z-listbox-body'] | //div[contains(@class,'z-listbox-body')]");
		this.requeryButton = page.locator(
				"//div[contains(@class,'z-tabpanel') and not(contains(@style,'display: none'))]//button[@title='ReQuery'] | //button[@title='ReQuery']");
		this.answerSelect = page.locator(
				"//div[contains(@class,'z-tabpanel') and not(contains(@style,'display: none'))]//select[contains(@class,'z-select')] | //select[contains(@class,'z-select')]");
		this.okButton = page.locator(
				"//div[contains(@class,'z-tabpanel') and not(contains(@style,'display: none'))]//button[@title='OK'] | //button[@title='OK' and not(ancestor::div[contains(@class,'z-window')])] | //button[text()='OK']");
		this.menuSearchInput = page.locator("//input[@class='z-bandbox-input']");
	}

	/**
	 * Navigates to Workflow Activities window using Menu Search Box.
	 */
	public void navigateToWorkflowActivities() {
		log.info("Navigating to Workflow Activities window...");
		menuSearchInput.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		menuSearchInput.fill("Workflow Activities");
		page.waitForTimeout(3000);

		Locator menuItem = page.locator(
				"//div[contains(@class,'z-bandbox-popup')]//tr[contains(.,'Workflow Activities')] | //div[contains(@class,'z-bandbox-popup')]//span[text()='Workflow Activities']");
		try {
			menuItem.first()
					.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(5000));
			menuItem.first().click();
		} catch (Exception e) {
			log.warn("Menu item not clicked, pressing Enter: {}", e.getMessage());
			menuSearchInput.press("Enter");
		}
		page.waitForTimeout(2000);
		WaitUtils.waitForLoadingIndicatorToDisappear(page);
		log.info("Workflow Activities window opened successfully.");
	}

	/**
	 * Scrolls to and selects the assigned SOP record matching the given status and
	 * document number.
	 *
	 * @param nodeStatus Expected status (e.g. "Internal Review", "Department
	 *                   Approval", "QA Approval", "Draft")
	 * @param docNo      Document Number (e.g. "1000714")
	 */
	public void clickOnAssignedSOPRecord(String nodeStatus, String docNo) {
		log.info("Locating assigned SOP Record with Status: [{}] and Document No: [{}]", nodeStatus, docNo);
		page.waitForTimeout(1000);
		clickReQuery();
		page.waitForTimeout(1000);

		Locator listbox = page.locator("//div[@class='adform-content z-window-content']//div[@class='z-listbox-body']")
				.first();
		if (listbox.count() > 0 && listbox.isVisible()) {
			listbox.click();
			page.waitForTimeout(300);
			// Wheel scroll down through virtual list
			for (int i = 0; i < 10; i++) {
				page.mouse().wheel(0, 1500);
				page.waitForTimeout(150);
			}
			page.keyboard().press("End");
			page.waitForTimeout(1000);
			WaitUtils.waitForLoadingIndicatorToDisappear(page);
		}

		Locator matchingRow = page.locator(
				"//div[contains(@class,'z-listbox')]//tr[contains(.,'" + docNo + "')]");
		try {
			matchingRow.first()
					.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(15000));
			matchingRow.first().click();
			log.info("Selected Assigned SOP Record matching Doc No: [{}]", docNo);
		} catch (Exception e) {
			log.warn("Direct row match not visible yet, clicking last visible item: {}", e.getMessage());
			Assert.fail("Assigned record not found on workflow activities" + e);
		}
		page.waitForTimeout(1000);
		WaitUtils.waitForLoadingIndicatorToDisappear(page);
	}

	/**
	 * Selects an answer option (Yes or No) in the Workflow Answer dropdown.
	 *
	 * @param answer "Yes" or "No"
	 */
	public void selectAnswer(String answer) {
		log.info("Selecting Answer: [{}] in Workflow Activities...", answer);
		answerSelect.first()
				.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		answerSelect.first().selectOption(answer);
		page.waitForTimeout(500);
		log.info("Selected answer: [{}]", answer);
	}

	/**
	 * Clicks OK button to submit / approve / reject the workflow activity.
	 */
	public void clickOk() {
		log.info("Submitting workflow activity via OK button...");
		okButton.first().waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		okButton.first().click();
		page.waitForTimeout(2000);
		WaitUtils.waitForLoadingIndicatorToDisappear(page);
		log.info("Workflow activity submitted successfully.");
	}

	/**
	 * Clicks the Zoom button to open the SOP Master window for the selected record.
	 */
	public void zoomToRecord() {
		log.info("Clicking Zoom button to open SOP Master record...");
		Locator zoomBtn = page.locator(
				"//button[@title='Zoom'] | //button[contains(@class,'z-button') and contains(@title,'Zoom')] | //button[contains(.,'Zoom')]")
				.first();

		// Ensure row selection activated the zoom button
		for (int i = 0; i < 15; i++) {
			try {
				if (zoomBtn.isEnabled()) {
					log.info("Zoom button is enabled and ready to click.");
					break;
				}
			} catch (Exception ignored) {
				Assert.fail("Zoom button is not enabled on Workflow Activities" + ignored);
			}

		}

		try {
			zoomBtn.click(new Locator.ClickOptions().setTimeout(5000));
		} catch (Exception e) {
			log.warn("Standard Zoom click failed, attempting double-click on last row: {}", e.getMessage());
			Assert.fail("Zoom button is not Worked" + e);
		}
		page.waitForTimeout(3000);
		WaitUtils.waitForLoadingIndicatorToDisappear(page);
		log.info("Zoomed into SOP Master record successfully.");
	}

	/**
	 * Clicks ReQuery button to refresh the activities list.
	 */
	public void clickReQuery() {
		if (requeryButton.count() > 0 && requeryButton.first().isVisible()) {
			requeryButton.first().click();
			page.waitForTimeout(1000);
			WaitUtils.waitForLoadingIndicatorToDisappear(page);
			log.info("Workflow Activities requery executed.");
		}
	}
}
