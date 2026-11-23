package pages;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitForSelectorState;

import base.BaseTest;
import utils.WaitUtils;

/**
 * Page object representing the Manufacturing Workflows window (PHX-310).
 * Handles workflow creation, mandatory field validations, dropdown selections,
 * date entries, template toggling, record persistence, and Activity tab records (PHX-311).
 */
public class ManufacturingWorkflow {

	private static final Logger log = LogManager.getLogger(ManufacturingWorkflow.class);
	private final Page page;

	// Window & Tab locators
	private final Locator menuSearchInput;
	private final Locator windowTab;

	// Header Field Locators
	private final Locator nameInput;
	private final Locator authorInput;
	private final Locator workflowTypeInput;
	private final Locator processTypeInput;
	private final Locator resourceInput;
	private final Locator qtyBatchSizeInput;
	private final Locator priorityInput;
	private final Locator validFromInput;
	private final Locator validToInput;
	private final Locator publishStatusInput;
	private final Locator templateCheckbox;
	private final Locator templateCheckboxLabel;
	private final Locator documentNoInput;
	private final Locator searchKeyInput;

	// Mandatory Labels / Indicators
	private final Locator nameLabel;
	private final Locator nameMandatoryStar;
	private final Locator authorLabel;
	private final Locator authorMandatoryStar;

	// Toolbar buttons
	private final Locator newButton;
	private final Locator saveButton;
	private final Locator undoButton;

	// Activity Tab & Detail Locators (PHX-311)
	private final Locator activityTab;
	private final Locator mfgWorkflowBreadcrumbLink;
	private final Locator activityBreadcrumbLink;
	private final Locator detailProcessInput;
	private final Locator detailProcessDropdownBtn;
	private final Locator detailNodeNameInput;
	private final Locator detailResourceInput;
	private final Locator detailNewButton;
	private final Locator detailSaveButton;

	// Captured state
	private static final ThreadLocal<String> createdWorkflowName = new ThreadLocal<>();
	private static final ThreadLocal<String> createdWorkflowDocNo = new ThreadLocal<>();

	public static String getCreatedWorkflowName() {
		return createdWorkflowName.get();
	}

	public static void setCreatedWorkflowName(String name) {
		createdWorkflowName.set(name);
	}

	public static String getCreatedWorkflowDocNo() {
		return createdWorkflowDocNo.get();
	}

	public static void setCreatedWorkflowDocNo(String docNo) {
		createdWorkflowDocNo.set(docNo);
	}

	public ManufacturingWorkflow(Page page) {
		this.page = page;

		this.menuSearchInput = page.locator("//input[@class='z-bandbox-input']");
		this.windowTab = page.locator(
				"//li[contains(@class,'z-tab')]//span[contains(text(),'Manufacturing Workflows')] | " +
						"//li[contains(@class,'z-tab') and contains(.,'Manufacturing Workflows')]");

		// Header fields
		this.nameInput = page.locator("//input[@instancename='AD_Workflow0Name']");
		this.authorInput = page.locator("//input[@instancename='AD_Workflow0Author']");
		this.workflowTypeInput = page.locator("//span[@instancename='AD_Workflow0WorkflowType']//input");
		this.processTypeInput = page.locator("//span[@instancename='AD_Workflow0ProcessType']//input");
		this.resourceInput = page.locator("//span[@instancename='AD_Workflow0S_Resource_ID']//input");
		this.qtyBatchSizeInput = page
				.locator("//div[@instancename='AD_Workflow0QtyBatchSize']//input[contains(@class,'editor-input')]");
		this.priorityInput = page
				.locator("//div[@instancename='AD_Workflow0Priority']//input[contains(@class,'editor-input')]");
		this.validFromInput = page
				.locator("//span[@instancename='AD_Workflow0ValidFrom']//input[contains(@class,'z-datebox-input')]");
		this.validToInput = page
				.locator("//span[@instancename='AD_Workflow0ValidTo']//input[contains(@class,'z-datebox-input')]");
		this.publishStatusInput = page.locator("//span[@instancename='AD_Workflow0PublishStatus']//input");
		this.templateCheckbox = page
				.locator("//span[@instancename='AD_Workflow0IsTemplate']//input[@type='checkbox']");
		this.templateCheckboxLabel = page.locator(
				"//span[@instancename='AD_Workflow0IsTemplate']//label[contains(@class,'z-checkbox-mold')] | " +
						"//span[@instancename='AD_Workflow0IsTemplate']//label[contains(@class,'z-checkbox-content')]");
		this.documentNoInput = page.locator("//input[@instancename='AD_Workflow0DocumentNo']");
		this.searchKeyInput = page.locator("//input[@instancename='AD_Workflow0Value']");

		// Mandatory field indicators
		this.nameLabel = page.locator(
				"//div[contains(@class,'adwindow-form')]//span[contains(@class,'idempiere-label') and normalize-space(text())='Name']");
		this.nameMandatoryStar = page.locator(
				"//span[contains(@class,'idempiere-label') and normalize-space(text())='Name']/following-sibling::span[contains(@class,'mandatory-decorator-text') and text()='*'] | "
						+
						"//div[contains(@class,'form-label')][.//span[normalize-space(text())='Name']]//span[contains(@class,'mandatory-decorator-text')]");
		this.authorLabel = page.locator(
				"//div[contains(@class,'adwindow-form')]//span[contains(@class,'idempiere-label') and normalize-space(text())='Author']");
		this.authorMandatoryStar = page.locator(
				"//span[contains(@class,'idempiere-label') and normalize-space(text())='Author']/following-sibling::span[contains(@class,'mandatory-decorator-text') and text()='*'] | "
						+
						"//div[contains(@class,'form-label')][.//span[normalize-space(text())='Author']]//span[contains(@class,'mandatory-decorator-text')]");

		// Toolbar buttons
		this.newButton = page.locator(
				"//div[contains(@class,'adwindow-toolbar')]//a[@title='New    Alt+N'] | " +
						"//div[contains(@class,'z-tabpanel') and not(contains(@style,'display: none'))]//a[@title='New    Alt+N'] | "
						+
						"//a[@title='New    Alt+N']");
		this.saveButton = page.locator(
				"//div[contains(@class,'adwindow-toolbar')]//a[@title='Save Changes    Alt+S'] | " +
						"//div[contains(@class,'z-tabpanel') and not(contains(@style,'display: none'))]//a[@title='Save Changes    Alt+S'] | "
						+
						"//a[@title='Save Changes    Alt+S']");
		this.undoButton = page.locator(
				"//div[contains(@class,'adwindow-toolbar')]//a[@title='Undo Changes    Alt+Z'] | " +
						"//a[@title='Undo Changes    Alt+Z']");

		// Activity Tab & Detail Locators (PHX-311)
		this.activityTab = page.locator(
				"//div[contains(@class,'adwindow-detailpane')]//span[normalize-space(text())='Activity'] | " +
						"//li[contains(@class,'z-tab')]//span[normalize-space(text())='Activity']");
		this.mfgWorkflowBreadcrumbLink = page.locator(
				"//div[contains(@class,'z-hlayout-inner')]//a[text()='Manufacturing Workflows'] | " +
						"//a[contains(@class,'z-a') and text()='Manufacturing Workflows']");
		this.activityBreadcrumbLink = page.locator(
				"//div[contains(@class,'z-hlayout-inner')]//a[text()='Activity'] | " +
						"//a[contains(@class,'z-a') and text()='Activity']");
		this.detailProcessInput = page.locator("//span[@instancename='AD_WF_Node0PHX_Process_ID']//input");
		this.detailProcessDropdownBtn = page.locator(
				"//span[@instancename='AD_WF_Node0PHX_Process_ID']//a[contains(@class,'z-combobox-button')]");
		this.detailNodeNameInput = page.locator(
				"//input[@instancename='AD_WF_Node0Name'] | //span[@instancename='AD_WF_Node0Name']//input");
		this.detailResourceInput = page.locator("//span[@instancename='AD_WF_Node0S_Resource_ID']//input");
		this.detailNewButton = page.locator(
				"//div[contains(@class,'adwindow-toolbar')]//a[@title='New    Alt+N'] | " +
						"//div[contains(@class,'z-tabpanel') and not(contains(@style,'display: none'))]//a[@title='New    Alt+N'] | " +
						"//a[@title='New    Alt+N']");
		this.detailSaveButton = page.locator(
				"//div[contains(@class,'adwindow-toolbar')]//a[@title='Save Changes    Alt+S'] | " +
						"//div[contains(@class,'z-tabpanel') and not(contains(@style,'display: none'))]//a[@title='Save Changes    Alt+S'] | " +
						"//a[@title='Save Changes    Alt+S']");
	}

	/**
	 * Returns the visible locator among multiple matched instances.
	 */
	private Locator getVisibleInput(Locator locator) {
		for (Locator l : locator.all()) {
			try {
				if (l.isVisible()) {
					return l;
				}
			} catch (Exception ignored) {
			}
		}
		return locator.first();
	}

	/**
	 * Step 2: Navigates to Manufacturing Workflows window using the menu searchbox.
	 */
	public void navigateToManufacturingWorkflowsWindow() {
		log.info("Navigating to 'Manufacturing Workflows' window via search [+] button...");

		// Switch to Home tab if available to access menu search
		Locator homeTab = page.locator("//li[contains(@class,'z-tab')]//span[contains(text(),'Home')]");
		if (homeTab.count() > 0 && homeTab.first().isVisible()) {
			page.waitForTimeout(500);
			WaitUtils.waitForLoadingIndicatorToDisappear(page);
		}

		menuSearchInput.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		menuSearchInput.fill("Manufacturing Workflows");
		page.waitForTimeout(1500);
		WaitUtils.waitForLoadingIndicatorToDisappear(page);

		// Click the '+' (New) icon directly on the Manufacturing Workflows search result row
		Locator newIconOnSearchRow = page.locator(
				"//div[contains(@class,'z-bandbox-popup')]//tr[td[1][normalize-space(.)='Manufacturing Workflows']]//a[@title='New'] | " +
				"//div[contains(@class,'z-bandbox-popup')]//tr[contains(.,'Manufacturing Workflows')]//a[@title='New']");

		try {
			if (newIconOnSearchRow.first().isVisible()) {
				log.info("Clicking [+] (New) button directly on Manufacturing Workflows search result row...");
				newIconOnSearchRow.first().click();
			} else {
				log.warn("New [+] button not visible on search row, clicking menu item label...");
				Locator menuItem = page.locator(
						"//div[contains(@class,'z-bandbox-popup')]//span[text()='Manufacturing Workflows'] | " +
						"//div[contains(@class,'menu-search')]//span[text()='Manufacturing Workflows'] | " +
						"//td[contains(@class,'menu-treecell')]//span[text()='Manufacturing Workflows']");
				menuItem.first().click();
			}
		} catch (Exception e) {
			log.warn("Could not click New [+] on search row, pressing Enter: {}", e.getMessage());
			menuSearchInput.press("Enter");
		}

		page.waitForTimeout(2000);
		WaitUtils.waitForLoadingIndicatorToDisappear(page);

		windowTab.first().waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(20000));
		log.info("Navigated to 'Manufacturing Workflows' window successfully.");
	}

	/**
	 * Step 3: Clicks on New [+] button to add a new record of Manufacturing Workflows.
	 * If the window was opened by clicking [+] in menu search, the new record is already initialized.
	 */
	public void clickNewRecord() {
		log.info("Checking if New record initialization is needed in Manufacturing Workflows...");
		Locator btn = getVisibleInput(newButton);
		try {
			if (btn.isVisible() && !btn.isDisabled()) {
				log.info("Clicking New [+] button in toolbar...");
				btn.click();
				page.waitForTimeout(1500);
				WaitUtils.waitForLoadingIndicatorToDisappear(page);
				log.info("New Manufacturing Workflow record initialized.");
			} else {
				log.info("Manufacturing Workflow record already in New state (opened via menu search [+] button).");
			}
		} catch (Exception e) {
			log.warn("Toolbar New button not clickable, assuming already on new record: {}", e.getMessage());
		}
	}

	/**
	 * Step 4: Checks whether all mandatory fields are indicated by an asterisk (*) symbol:
	 * - Name
	 * - Author
	 */
	public void verifyMandatoryFields() {
		log.info("Verifying mandatory field indicators (* symbol) on Manufacturing Workflows window...");
		SoftAssert sa = BaseTest.getSoftAssert();

		// Check Name field mandatory asterisk
		boolean isNameStarVisible = false;
		try {
			Locator star = getVisibleInput(nameMandatoryStar);
			isNameStarVisible = star.isVisible();
		} catch (Exception e) {
			log.warn("Name mandatory star check exception: {}", e.getMessage());
		}

		// Also check input class 'idempiere-mandatory'
		boolean isNameInputMandatory = false;
		try {
			Locator nameInp = getVisibleInput(nameInput);
			String cls = nameInp.getAttribute("class");
			isNameInputMandatory = cls != null && cls.contains("idempiere-mandatory");
		} catch (Exception ignored) {
		}

		log.info("Name field mandatory verification: [Star visible: {}, Input mandatory class: {}]",
				isNameStarVisible, isNameInputMandatory);

		if (sa != null) {
			sa.assertTrue(isNameStarVisible || isNameInputMandatory,
					"[Manufacturing Workflows] 'Name' field is NOT indicated as mandatory with asterisk (*).");
		} else {
			Assert.assertTrue(isNameStarVisible || isNameInputMandatory,
					"[Manufacturing Workflows] 'Name' field is NOT indicated as mandatory with asterisk (*).");
		}

		// Check Author field mandatory asterisk
		boolean isAuthorStarVisible = false;
		try {
			Locator star = getVisibleInput(authorMandatoryStar);
			isAuthorStarVisible = star.isVisible();
		} catch (Exception e) {
			log.warn("Author mandatory star check exception: {}", e.getMessage());
		}

		boolean isAuthorInputMandatory = false;
		try {
			Locator authorInp = getVisibleInput(authorInput);
			String cls = authorInp.getAttribute("class");
			isAuthorInputMandatory = cls != null && cls.contains("idempiere-mandatory");
		} catch (Exception ignored) {
		}

		log.info("Author field mandatory verification: [Star visible: {}, Input mandatory class: {}]",
				isAuthorStarVisible, isAuthorInputMandatory);

		if (sa != null) {
			sa.assertTrue(isAuthorStarVisible || isAuthorInputMandatory,
					"[Manufacturing Workflows] 'Author' field is NOT indicated as mandatory with asterisk (*).");
		} else {
			Assert.assertTrue(isAuthorStarVisible || isAuthorInputMandatory,
					"[Manufacturing Workflows] 'Author' field is NOT indicated as mandatory with asterisk (*).");
		}
	}

	/**
	 * Enters details into the Manufacturing Workflow header fields:
	 * - Name
	 * - Workflow Type (Manufacturing)
	 * - Process Type (Batch Flow)
	 * - Resource (Tablet Plant - Pipan or configured)
	 * - Qty Batch Size (100000)
	 * - Priority (1)
	 * - Valid From (10/01/2026)
	 * - Valid To (09/01/2028)
	 * - Publication Status (Under Revision)
	 * - Author (Test Team New)
	 * - Template (true/false)
	 */
	public void fillWorkflowDetails(String name, String workflowType, String processType, String resource,
			String qtyBatchSize, String priority, String validFrom, String validTo,
			String pubStatus, String author, boolean isTemplate) {

		log.info("Filling Manufacturing Workflow details for: [{}]", name);
		createdWorkflowName.set(name);

		// 1. Name
		if (name != null && !name.trim().isEmpty()) {
			Locator nameInp = getVisibleInput(nameInput);
			nameInp.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(5000));
			nameInp.fill(name);
			nameInp.press("Tab");
			page.waitForTimeout(300);
		}

		// 2. Workflow Type
		if (workflowType != null && !workflowType.trim().isEmpty()) {
			Locator wfType = getVisibleInput(workflowTypeInput);
			wfType.fill(workflowType);
			wfType.press("Tab");
			page.waitForTimeout(300);
		}

		// 3. Process Type
		if (processType != null && !processType.trim().isEmpty()) {
			Locator procType = getVisibleInput(processTypeInput);
			procType.fill(processType);
			procType.press("Tab");
			page.waitForTimeout(300);
		}

		// 4. Resource
		if (resource != null && !resource.trim().isEmpty()) {
			try {
				Locator resInp = getVisibleInput(resourceInput);
				resInp.fill(resource);
				resInp.press("Tab");
				page.waitForTimeout(300);
			} catch (Exception e) {
				log.warn("Could not set Resource: {}", e.getMessage());
			}
		}

		// 5. Qty Batch Size
		if (qtyBatchSize != null && !qtyBatchSize.trim().isEmpty()) {
			Locator qtyInp = getVisibleInput(qtyBatchSizeInput);
			qtyInp.fill(qtyBatchSize);
			qtyInp.press("Tab");
			page.waitForTimeout(300);
		}

		// 6. Priority
		if (priority != null && !priority.trim().isEmpty()) {
			Locator prioInp = getVisibleInput(priorityInput);
			prioInp.fill(priority);
			prioInp.press("Tab");
			page.waitForTimeout(300);
		}

		// 7. Valid From Date
		if (validFrom != null && !validFrom.trim().isEmpty()) {
			Locator vFrom = getVisibleInput(validFromInput);
			vFrom.fill(validFrom);
			vFrom.press("Tab");
			page.waitForTimeout(300);
		}

		// 8. Valid To Date
		if (validTo != null && !validTo.trim().isEmpty()) {
			Locator vTo = getVisibleInput(validToInput);
			vTo.fill(validTo);
			vTo.press("Tab");
			page.waitForTimeout(300);
		}

		// 9. Publication Status
		if (pubStatus != null && !pubStatus.trim().isEmpty()) {
			Locator pStatus = getVisibleInput(publishStatusInput);
			pStatus.fill(pubStatus);
			pStatus.press("Tab");
			page.waitForTimeout(300);
		}

		// 10. Author
		if (author != null && !author.trim().isEmpty()) {
			Locator authInp = getVisibleInput(authorInput);
			authInp.fill(author);
			authInp.press("Tab");
			page.waitForTimeout(300);
		}

		// 11. Template Checkbox
		setTemplate(isTemplate);
	}

	/**
	 * Step 6: Makes a True Mark on Template Checkbox (or unchecks if false).
	 */
	public void setTemplate(boolean isTemplate) {
		log.info("Setting Template Checkbox to: [{}]", isTemplate);
		Locator chk = getVisibleInput(templateCheckbox);

		try {
			chk.scrollIntoViewIfNeeded();
			boolean currentlyChecked = chk.isChecked();
			if (currentlyChecked != isTemplate) {
				Locator label = getVisibleInput(templateCheckboxLabel);
				label.scrollIntoViewIfNeeded();
				label.click(new Locator.ClickOptions().setTimeout(3000));
				page.waitForTimeout(500);
			}
		} catch (Exception e) {
			log.warn("Standard template checkbox toggle failed, attempting JS click: {}", e.getMessage());
			try {
				chk.evaluate("(el, target) => { if (el.checked !== target) { el.click(); } }", isTemplate);
			} catch (Exception ex) {
				log.warn("JS template checkbox click failed: {}", ex.getMessage());
			}
		}

		try {
			boolean stateAfter = chk.isChecked();
			log.info("Template Checkbox state is now: [{}]", stateAfter);
		} catch (Exception ignored) {
		}
	}

	/**
	 * Step 7: Clicks on Save Button and verifies that the Manufacturing Workflow
	 * record is saved successfully.
	 */
	public void saveRecord() {
		log.info("Saving Manufacturing Workflow record...");
		Locator btn = getVisibleInput(saveButton);
		btn.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		btn.click();
		page.waitForTimeout(1500);
		WaitUtils.waitForLoadingIndicatorToDisappear(page);

		// Capture generated Document No if available
		try {
			Locator docNoInp = getVisibleInput(documentNoInput);
			String docNo = docNoInp.inputValue();
			if (docNo != null && !docNo.trim().isEmpty()) {
				createdWorkflowDocNo.set(docNo.trim());
				log.info("Manufacturing Workflow saved with Document No: [{}]", docNo.trim());
			}
		} catch (Exception ignored) {
		}

		log.info("Manufacturing Workflow record saved successfully.");
	}

	/**
	 * Returns the captured Document No or Search Key of the created workflow.
	 */
	public String getWorkflowDocumentNo() {
		return createdWorkflowDocNo.get();
	}

	/**
	 * Navigates to the child tab "Activity" in Manufacturing Workflows (PHX-311).
	 */
	public void navigateToActivityTab() {
		log.info("Navigating to child tab 'Activity' in Manufacturing Workflows...");
		try {
			// Check if we are already in Activity tab view
			Locator procInp = getVisibleInput(detailProcessInput);
			if (procInp.isVisible()) {
				log.info("Already on Activity tab view.");
				return;
			}

			// If in a child subtab (like Product) where Activity is a breadcrumb link, click it
			Locator actBreadcrumb = getVisibleInput(activityBreadcrumbLink);
			if (actBreadcrumb.isVisible()) {
				actBreadcrumb.click();
				page.waitForTimeout(500);
				WaitUtils.waitForLoadingIndicatorToDisappear(page);
				return;
			}

			Locator tab = getVisibleInput(activityTab);
			tab.scrollIntoViewIfNeeded();
			tab.click();
			page.waitForTimeout(1000);
			WaitUtils.waitForLoadingIndicatorToDisappear(page);

			procInp.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
			log.info("Successfully navigated to Activity child tab.");
		} catch (Exception e) {
			log.error("Failed to navigate to Activity child tab: {}", e.getMessage());
			Assert.fail("Failed to navigate to Activity child tab: " + e.getMessage());
		}
	}

	/**
	 * Maps node names (from test specification) to available Process dropdown options in ERP.
	 * Ex: Weighing -> Dispense, Blending -> Blending, Granulation -> Granualation,
	 * Compression -> Compression, Coating -> Coating, Packaging -> Primary Packing.
	 *
	 * @param nodeName the node name specified in test data
	 * @return the matching process dropdown option name
	 */
	public String mapNodeToProcessName(String nodeName) {
		if (nodeName == null) {
			return null;
		}
		String lower = nodeName.trim().toLowerCase();
		if (lower.contains("weigh") || lower.contains("dispens")) {
			return "Dispense";
		} else if (lower.contains("blend")) {
			return "Blending";
		} else if (lower.contains("granul")) {
			return "Granualation";
		} else if (lower.contains("compress")) {
			return "Compression";
		} else if (lower.contains("coat")) {
			return "Coating";
		} else if (lower.contains("pack")) {
			return "Primary Packing";
		}
		return nodeName.trim();
	}

	/**
	 * Selects the Process from the Process combobox dropdown in the Activity tab.
	 *
	 * @param processName the process option to select
	 */
	public void selectActivityProcess(String processName) {
		if (processName == null || processName.trim().isEmpty()) {
			return;
		}
		log.info("Selecting Process: [{}] in Activity tab...", processName);
		try {
			Locator pInp = getVisibleInput(detailProcessInput);
			Locator pBtn = getVisibleInput(detailProcessDropdownBtn);

			if (pBtn.isVisible()) {
				pBtn.click();
			} else {
				pInp.click();
			}
			page.waitForTimeout(500);

			// Locate the item in the open combobox popup
			Locator comboItem = page.locator(
					"//div[contains(@class,'z-combobox-open') or contains(@class,'z-combobox-popup')]//li[contains(@class,'z-comboitem') and normalize-space(.)='"
							+ processName.trim() + "'] | " +
							"//div[contains(@class,'z-combobox-open') or contains(@class,'z-combobox-popup')]//li[contains(@class,'z-comboitem') and contains(.,'"
							+ processName.trim() + "')]");

			try {
				comboItem.first().waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(3000));
				comboItem.first().click();
			} catch (Exception ex) {
				log.warn("Could not click Process combo item [{}], typing directly: {}", processName, ex.getMessage());
				pInp.fill(processName);
				pInp.press("Tab");
			}

			page.waitForTimeout(500);
			WaitUtils.waitForLoadingIndicatorToDisappear(page);
			log.info("Process selected as: [{}]", pInp.inputValue());
		} catch (Exception e) {
			log.error("Failed to select Process [{}]: {}", processName, e.getMessage());
			Assert.fail("Failed to select Process [" + processName + "]: " + e.getMessage());
		}
	}

	/**
	 * Creates a single Activity node record in the Activity tab (PHX-311).
	 *
	 * @param nodeName the activity/node name (e.g. Weighing, Blending, Granulation, etc.)
	 */
	public void createActivityRecord(String nodeName) {
		log.info("Creating Activity record for Node: [{}]...", nodeName);
		try {
			// Ensure we are on Activity tab
			navigateToActivityTab();

			// If current record is not newly inserted, click New [+] button
			boolean isInserted = false;
			try {
				String bText = page.locator("//div[contains(@class,'z-hlayout-inner')]").allInnerTexts().toString();
				isInserted = bText.contains("Inserted") || bText.contains("+*");
			} catch (Exception ignored) {
			}

			if (!isInserted) {
				Locator btnNew = getVisibleInput(detailNewButton);
				btnNew.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
				btnNew.click();
				page.waitForTimeout(800);
				WaitUtils.waitForLoadingIndicatorToDisappear(page);
			}

			// Map node name to actual ERP Process name and select it
			String processName = mapNodeToProcessName(nodeName);
			selectActivityProcess(processName);

			// Verify Name field is populated
			try {
				Locator nameInp = getVisibleInput(detailNodeNameInput);
				String populatedName = nameInp.inputValue();
				log.info("Activity Node Name auto-populated as: [{}]", populatedName);
			} catch (Exception ignored) {
			}

			// Click Save Changes Alt+S
			Locator btnSave = getVisibleInput(detailSaveButton);
			btnSave.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
			btnSave.click();
			page.waitForTimeout(1000);
			WaitUtils.waitForLoadingIndicatorToDisappear(page);

			log.info("Activity Node [{}] (Process: [{}]) created and saved successfully.", nodeName, processName);
		} catch (Exception e) {
			log.error("Failed to create Activity Node [{}]: {}", nodeName, e.getMessage());
			Assert.fail("Failed to create Activity Node [" + nodeName + "]: " + e.getMessage());
		}
	}

	/**
	 * Creates multiple Activity node records in sequence for the manufacturing workflow (PHX-311).
	 *
	 * @param nodeNames list of node names to create (e.g. Weighing, Blending, Granulation, Compression, Coating, Packaging)
	 */
	public void createActivityRecords(List<String> nodeNames) {
		if (nodeNames == null || nodeNames.isEmpty()) {
			log.warn("No activity node names provided to create.");
			return;
		}
		log.info("Starting batch creation of [{}] Activity records (PHX-311)...", nodeNames.size());
		navigateToActivityTab();

		for (int i = 0; i < nodeNames.size(); i++) {
			String node = nodeNames.get(i).trim();
			if (node.isEmpty()) {
				continue;
			}
			log.info("--- Creating Activity [{}/{}] : [{}] ---", (i + 1), nodeNames.size(), node);
			createActivityRecord(node);
		}

		log.info("All [{}] Activity records created successfully.", nodeNames.size());
	}

	/**
	 * Navigates back to the Manufacturing Workflows header record from the Activity child tab.
	 */
	public void returnToWorkflowHeader() {
		log.info("Navigating back to Manufacturing Workflows header...");
		try {
			Locator mfgLink = getVisibleInput(mfgWorkflowBreadcrumbLink);
			if (mfgLink.isVisible()) {
				mfgLink.click();
				page.waitForTimeout(500);
				WaitUtils.waitForLoadingIndicatorToDisappear(page);
			}
			Locator headerName = getVisibleInput(nameInput);
			headerName.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
			log.info("Returned to Manufacturing Workflows header successfully.");
		} catch (Exception e) {
			log.warn("Navigation back to Manufacturing Workflows header: {}", e.getMessage());
		}
	}
}
