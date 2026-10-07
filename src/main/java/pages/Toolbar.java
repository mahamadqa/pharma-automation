package pages;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
	private final Locator previewButton;
	private final Locator searchButton;
	private final Locator docNoSearchInput;
	private final Locator searchOkButton;
	private final Locator docActionButton;
	private final Locator docActionPopup;

	public Toolbar(Page page) {
		this.page = page;
		this.createNewButton = page.locator("//a[@title='New    Alt+N']");
		this.saveButton = page.locator("//a[@title='Save Changes    Alt+S']");
		this.gridViewButton = page.locator("//a[@title='Grid Toggle    Alt+T']");
		this.previewButton = page.locator("//a[@title='Preview']");
		this.searchButton = page.locator("//a[@title='Lookup Record    Alt+F']");
		this.docNoSearchInput = page.locator("//input[@instancename='DocumentNo']");
		this.searchOkButton = page.locator("(//button[@title='OK'])[1]");
		this.docActionButton = page.locator("//button[@instancename='C_Order0DocAction']");
		this.docActionPopup = page.locator("//div[@instancename='documentAction']");
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

	/**
	 * Toggles between Form View and Grid View.
	 */
	public void clickOnGrid() {
		log.info("Toggling Form/Grid View via Toolbar...");
		gridViewButton.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		gridViewButton.click();
		page.waitForTimeout(1000);
		log.info("Toggled Grid View.");
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
	
}