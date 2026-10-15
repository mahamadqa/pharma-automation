package pages;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.SelectOption;
import com.microsoft.playwright.options.WaitForSelectorState;

import utils.WaitUtils;

public class Toolbar {

	private static final Logger log = LogManager.getLogger(Toolbar.class);
	private final Page page;

	private final Locator createNewButton;
	private final Locator saveButton;
	private final Locator gridViewButton;
	private final Locator subtabGridViewButton;
	private final Locator previewButton;
	private final Locator searchButton;
	private final Locator docNoSearchInput;
	private final Locator searchOkButton;
	private final Locator docActionButton;
	private final Locator docActionPopup;
	private final Locator closeWindow;

	public Toolbar(Page page) {
		this.page = page;
		this.createNewButton = page.locator("//a[@title='New    Alt+N']");
		this.saveButton = page.locator("//a[@title='Save Changes    Alt+S']");
		this.gridViewButton = page.locator("//a[@title='Grid Toggle    Alt+T']");
		this.subtabGridViewButton = page.locator(
				"//div[contains(@class,'z-tabpanel') and not(contains(@style,'display: none')) and not(contains(@style,'display:none'))]//a[contains(@title,'Shift+Alt+T')] | //div[contains(@class,'adwindow-detailpane-toolbar')]//a[contains(@title,'Shift+Alt+T')]");
		this.previewButton = page.locator("//a[@title='Preview']");
		this.searchButton = page.locator("//a[@title='Lookup Record    Alt+F']");
		this.docNoSearchInput = page.locator("//input[@instancename='DocumentNo']");
		this.searchOkButton = page.locator("(//button[@title='OK'])[1]");
		this.docActionButton = page.locator("//button[@instancename='C_Order0DocAction'] | //button[@instancename='M_InOut0DocAction'] | //button[contains(@instancename,'DocAction')]");
		this.docActionPopup = page.locator("//div[@instancename='documentAction']");
		this.closeWindow = page.locator("//div[@class='z-tab-button']//i[@aria-label='Close']");
	}

	/**
	 * Clicks the Toolbar 'New' button to create a new record.
	 */
	public void createNewRecord() {
		log.info("Creating a new record via Toolbar 'New' button...");
		createNewButton.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		createNewButton.click();
		page.waitForTimeout(1000);
		log.info("New record initialized.");
	}

	/**
	 * Clicks the Toolbar 'Save Changes' button to persist the current record.
	 */
	public void saveRecord() {
		log.info("Saving changes via Toolbar 'Save' button...");
		saveButton.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		saveButton.click();
		page.waitForTimeout(1000);
		WaitUtils.waitForLoadingIndicatorToDisappear(page);
		log.info("Record saved successfully.");
	}
	
	public void closeWindow() {
		closeWindow.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		closeWindow.click();
		page.waitForTimeout(1000);
		WaitUtils.waitForLoadingIndicatorToDisappear(page);
		log.info("Record saved successfully.");
	}

	/**
	 * Toggles between Form View and Grid View on the main window.
	 */
	public void clickOnGrid() {
		log.info("Toggling Form/Grid View via Toolbar...");
		gridViewButton.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		gridViewButton.click();
		page.waitForTimeout(1000);
		log.info("Toggled Grid View.");
	}

	/**
	 * Toggles between Form View and Grid View on the active subtab (detail pane).
	 */
	public void clickOnSubtabGrid() {
		log.info("Toggling Subtab Form/Grid View via subtab toolbar (Shift+Alt+T)...");
		Locator toggleBtn = page.locator("(//div[contains(@class,'z-tabpanel') and not(contains(@style,'display: none')) and not(contains(@style,'display:none'))]//a[contains(@title,'Shift+Alt+T')])[1]");
		toggleBtn.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		toggleBtn.click();
		page.waitForTimeout(1000);
		WaitUtils.waitForLoadingIndicatorToDisappear(page);
		log.info("Toggled Subtab Grid View.");
	}

	/**
	 * Checks if the active subtab is opened in Grid View (table view), and if so,
	 * clicks Grid Toggle (Shift+Alt+T) to switch to normal Form View.
	 */
	public void ensureSubtabFormView() {
		log.info("Checking if active subtab is in Grid View...");
		try {
			Locator formView = page.locator("//div[contains(@class,'z-tabpanel') and not(contains(@style,'display: none')) and not(contains(@style,'display:none'))]//div[contains(@class,'adwindow-form')]");
			Locator toggleBtn = page.locator("(//div[contains(@class,'z-tabpanel') and not(contains(@style,'display: none')) and not(contains(@style,'display:none'))]//a[contains(@title,'Shift+Alt+T')])[1]");
			if (toggleBtn.isVisible()) {
				boolean isFormVisible = formView.count() > 0 && formView.first().isVisible();
				if (!isFormVisible) {
					log.info("Subtab is in Grid View by default. Clicking Grid Toggle (Shift+Alt+T) to view normally...");
					toggleBtn.click();
					page.waitForTimeout(1000);
					WaitUtils.waitForLoadingIndicatorToDisappear(page);
					log.info("Subtab successfully switched to normal Form View.");
				} else {
					log.info("Subtab is already displaying normally in Form View.");
				}
			}
		} catch (Exception e) {
			log.warn("Could not check/toggle subtab grid view: {}", e.getMessage());
		}
	}

	/**
	 * Clicks the Toolbar 'Preview' button to open report preview.
	 */
	public void clickOnPreviewButton() {
		log.info("Clicking on Toolbar 'Preview' button...");
		previewButton.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		previewButton.click();
		page.waitForTimeout(1000);
		log.info("Preview button clicked.");
	}

	/**
	 * Searches and opens a record by Document Number.
	 *
	 * @param docNo Document Number to search
	 */
	public void searchDocNo(String docNo) {
		log.info("Looking up record with Document No: [{}]", docNo);
		searchButton.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		searchButton.click();
		page.waitForTimeout(1000);
		docNoSearchInput.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		docNoSearchInput.fill(docNo);
		page.waitForTimeout(1000);
		searchOkButton.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(5000));
		searchOkButton.click();
		page.waitForTimeout(1000);
		log.info("Lookup submitted for Document No: [{}]", docNo);
	}

	public void performDocAction(String action) {
		page.waitForTimeout(1000);
		docActionButton.click();
		WaitUtils.waitForLoadingIndicatorToDisappear(page);
		docActionPopup.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(5000));
		Locator dropdown = page.locator("//div[@instancename='documentAction']//select");
		dropdown.selectOption(new SelectOption().setLabel(action));
		page.click("//div[@instancename='documentAction']//button[@title='OK']");
		WaitUtils.waitForBigLoadingIndicatorToDisappear(page);
		page.waitForTimeout(1000);
	}
	
	public void verifyDocStatus(String status) {
		Locator DocStatus = page.locator("//span[contains(@instancename,'DocStatus')]//input");
		String actualStatus = DocStatus.inputValue();

		SoftAssert sa = base.BaseTest.getSoftAssert();
		if (sa != null) {
			sa.assertTrue(actualStatus.equalsIgnoreCase(status),
					"[PO] DocStatus mismatch — Expected: [" + status + "] Actual: [" + actualStatus + "]");
		} else {
			if (!actualStatus.equalsIgnoreCase(status)) {
				Assert.fail("docStatus Not Matching, Actual: " + actualStatus + " Expected: " + status);
			}
		}
		if (actualStatus.equalsIgnoreCase(status)) {
			log.info("DocStatus verified successfully: [{}]", actualStatus);
		} else {
			log.warn("DocStatus mismatch — Expected: [{}] Actual: [{}] — soft assertion recorded.", status, actualStatus);
		}
	}

}