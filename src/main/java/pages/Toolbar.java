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
		createNewButton.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		createNewButton.click();
		page.waitForTimeout(1000);
		log.info("New record initialized.");
	}

	/**
	 * Clicks the Toolbar 'Save Changes' button to persist the current record.
	 */
	public void saveRecord() {
		saveButton.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		saveButton.click();
		page.waitForTimeout(300);
		WaitUtils.waitForLoadingIndicatorToDisappear(page);
		log.info("Record saved successfully.");
	}
	
	public void closeWindow() {
		closeWindow.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		closeWindow.click();
		page.waitForTimeout(400);
		WaitUtils.waitForLoadingIndicatorToDisappear(page);
	}

	/**
	 * Toggles between Form View and Grid View on the main window.
	 */
	public void clickOnGrid() {
		gridViewButton.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		gridViewButton.click();
		page.waitForTimeout(1000);
	}

	/**
	 * Toggles between Form View and Grid View on the active subtab (detail pane).
	 */
	public void clickOnSubtabGrid() {
		Locator toggleBtn = page.locator("(//div[contains(@class,'z-tabpanel') and not(contains(@style,'display: none')) and not(contains(@style,'display:none'))]//a[contains(@title,'Shift+Alt+T')])[1]");
		toggleBtn.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		toggleBtn.click();
		page.waitForTimeout(1000);
		WaitUtils.waitForLoadingIndicatorToDisappear(page);
	}

	/**
	 * Checks if the active subtab is opened in Grid View (table view), and if so,
	 * clicks Grid Toggle (Shift+Alt+T) to switch to normal Form View.
	 */
	public void ensureSubtabFormView() {
		try {
			Locator formView = page.locator("//div[contains(@class,'z-tabpanel') and not(contains(@style,'display: none')) and not(contains(@style,'display:none'))]//div[contains(@class,'adwindow-form')]");
			Locator toggleBtn = page.locator("(//div[contains(@class,'z-tabpanel') and not(contains(@style,'display: none')) and not(contains(@style,'display:none'))]//a[contains(@title,'Shift+Alt+T')])[1]");
			if (toggleBtn.isVisible()) {
				boolean isFormVisible = formView.count() > 0 && formView.first().isVisible();
				if (!isFormVisible) {
					toggleBtn.click();
					page.waitForTimeout(1000);
					WaitUtils.waitForLoadingIndicatorToDisappear(page);
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
		previewButton.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		previewButton.click();
		page.waitForTimeout(1000);
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
	}

	public void performDocAction(String action) {
		docActionButton.first().waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		docActionButton.first().click();
		WaitUtils.waitForLoadingIndicatorToDisappear(page);
		docActionPopup.first().waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(5000));
		Locator dropdown = page.locator("//div[@instancename='documentAction']//select");
		dropdown.first().selectOption(new SelectOption().setLabel(action));
		page.click("//div[@instancename='documentAction']//button[@title='OK']");
		WaitUtils.waitForBigLoadingIndicatorToDisappear(page);
		page.waitForTimeout(400);
	}
	
	public void verifyDocStatus(String status) {
		Locator DocStatus = page.locator("//span[contains(@instancename,'DocStatus')]//input | //div[contains(@instancename,'DocStatus')]//input | //input[contains(@instancename,'DocStatus')]");
		String actualStatus = DocStatus.first().inputValue();

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

	public void navigateBackToheader() {
		// TODO Auto-generated method stub
		page.locator("(//div[@instancename='breadcrumb']//a[@class='z-a'])[1] | (//div[@instancename='breadcrumb']//a[@class='z-a'])[2] | (//div[@instancename='breadcrumb']//a[@class='z-a'])[3]").click();
	}

}