package pages;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;

import com.microsoft.playwright.FrameLocator;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.MouseButton;
import com.microsoft.playwright.options.WaitForSelectorState;

import base.BaseTest;
import utils.CustomSoftAssert;
import utils.WaitUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Page object representing the SOP Master window, subtabs (Abbreviations,
 * Annexure,
 * SOP Formats, Distribution List, Revision History), Content sections with HTML
 * Editor,
 * Digital Signature dialog, and Document Actions.
 */
public class SOPMaster {

	private static final Logger log = LogManager.getLogger(SOPMaster.class);
	private final Page page;

	// Menu Search
	private final Locator menuSearchInput;
	private final Locator sopMasterNewIcon;

	// Header Locators
	private final Locator orgInput;
	private final Locator docTypeInput;
	private final Locator documentNoInput;
	private final Locator revisionNoInput;
	private final Locator descriptionInput;
	private final Locator departmentInput;
	private final Locator respDeptTextbox;
	private final Locator masterCopyOwnerInput;
	private final Locator supersedesNoInput;
	private final Locator prevSOPInput;
	private final Locator sopStatusInput;
	private final Locator docStatusInput;
	private final Locator processedCheckbox;
	private final Locator approvedCheckbox;
	private final Locator activeCheckbox;
	private final Locator reviseButton;
	private final Locator docActionButton;
	private final Locator effectiveDateInput;
	private final Locator resourceInput;
	private final Locator resourceMoreBtn;
	private final Locator equipmentInput;
	private final Locator equipmentMoreBtn;

	// Content Section Locators
	private final Locator objectiveField;
	private final Locator scopeField;
	private final Locator responsibilityField;
	private final Locator procedureField;

	// Subtabs
	private final Locator abbreviationsTab;
	private final Locator annexureTab;
	private final Locator formatTab;
	private final Locator distributionTab;
	private final Locator revisionHistoryTab;

	// Abbreviations Tab Fields
	private final Locator abbrevShortFormInput;
	private final Locator abbrevFullFormInput;

	// Annexure Tab Fields
	private final Locator annexureNameInput;
	private final Locator annexureNoInput;
	private final Locator annexureTitleInput;
	private final Locator annexurePrintPaperInput;
	private final Locator annexureSupersedesNoInput;
	private final Locator annexureContentField;
	private final Locator annexureReviseFormatBtn;
	private final Locator annexureRevisedCheckbox;
	
	private final Locator annexurePreviousVersion;
	private final Locator annexureBaseVersion;
	private final Locator annexureOriginalSOPVersion;

	// SOP Formats Tab Fields
	private final Locator formatDocTypeInput;
	private final Locator formatNameInput;
	private final Locator formatPrintPaperInput;
	private final Locator formatSupersedesNoInput;
	private final Locator formatContentField;
	private final Locator formatReviseFormatBtn;
	private final Locator formatRevisedCheckbox;
	
	private final Locator formatPreviousVersion;
	private final Locator formatBaseVersion;
	private final Locator formatOriginalSOPVersion;

	// Distribution List Tab Fields
	private final Locator distDepartmentInput;
	private final Locator distCopiesInput;

	// Revision History Tab Fields
	private final Locator historyRevisionInput;
	private final Locator historyReasonInput;
	private final Locator historyCCNoInput;

	// Digital Signatures
	private final Locator preparedBySignBtn;
	private final Locator checkedBySignBtn;
	private final Locator reviewedBySignBtn;
	private final Locator approverSignBtn;

	// Breadcrumb & Toolbar
	private final Locator sopHeaderBreadcrumb;
	private final Locator createNewButton;
	private final Locator saveButton;
	private final Locator previewButton;

	// Active SOP Document Number & Annexure & Format
	private static final ThreadLocal<String> activeSOPDocNo = ThreadLocal.withInitial(() -> "");
	private static final ThreadLocal<String> activeAnnexureName = ThreadLocal.withInitial(() -> "");
	private static final ThreadLocal<String> activeFormatName = ThreadLocal.withInitial(() -> "");

	protected Dashboard dashboard;

	public SOPMaster(Page page) {
		this(page, null);
	}

	public SOPMaster(Page page, Dashboard dashboard) {
		this.page = page;
		this.dashboard = dashboard;

		// Menu Search
		this.menuSearchInput = page.locator("//input[@class='z-bandbox-input']");
		this.sopMasterNewIcon = page.locator(
				"//div[contains(@class,'z-bandbox-popup')]//tr[contains(.,'SOP Master')]//a[@title='New'] | //tr[contains(.,'SOP Master')]//a[@title='New']");

		// Header Fields
		this.orgInput = page.locator("//span[@instancename='PHX_SOP0AD_Org_ID']//input");
		this.docTypeInput = page.locator("//span[@instancename='PHX_SOP0C_DocType_ID']//input");
		this.documentNoInput = page.locator("//input[@instancename='PHX_SOP0DocumentNo']");
		this.revisionNoInput = page.locator(
				"//div[@instancename='PHX_SOP0Revision']//input[contains(@class,'z-decimalbox')] | //div[@instancename='PHX_SOP0Revision']//input | //input[@instancename='PHX_SOP0Revision']");
		this.descriptionInput = page.locator("//input[@instancename='PHX_SOP0Description']");
		this.departmentInput = page.locator("//span[@instancename='PHX_SOP0C_Department_ID']//input");
		this.respDeptTextbox = page.locator(
				"//div[@instancename='PHX_SOP0PHX_RespDept_ID']//input[contains(@class,'multi-select-textbox')]");
		this.masterCopyOwnerInput = page.locator("//span[@instancename='PHX_SOP0PHX_MasterCopyOwner_ID']//input");
		this.supersedesNoInput = page.locator("//input[@instancename='PHX_SOP0PHX_SupersedesNo']");
		this.prevSOPInput = page.locator("//span[@instancename='PHX_SOP0PHX_PrevSOP_ID']//input");
		this.sopStatusInput = page.locator("//span[@instancename='PHX_SOP0PHX_SOPStatus']//input");
		this.docStatusInput = page.locator("//span[@instancename='PHX_SOP0DocStatus']//input");
		this.processedCheckbox = page.locator(
				"//span[@instancename='PHX_SOP0Processed']//input | //span[@instancename='PHX_SOP0Processed']");
		this.approvedCheckbox = page.locator(
				"//span[@instancename='PHX_SOP0IsApproved']//input | //span[@instancename='PHX_SOP0IsApproved']");
		this.activeCheckbox = page.locator(
				"//span[@instancename='PHX_SOP0IsActive']//input | //span[@instancename='PHX_SOP0IsActive'] | "
						+ "//span[contains(@class,'z-checkbox')][.//label[normalize-space(text())='Active']]//input | "
						+ "//span[contains(@class,'z-checkbox')][.//label[normalize-space(text())='Active']]");
		this.reviseButton = page.locator("//button[@instancename='PHX_SOP0Revise']");
		this.docActionButton = page.locator("//button[@instancename='PHX_SOP0DocAction']");
		this.effectiveDateInput = page
				.locator("//span[@instancename='PHX_SOP0DateFrom']//input[not(contains(@class,'z-timebox-input'))]");
		this.resourceInput = page.locator(
				"//div[@instancename='PHX_SOP0S_Resource_ID']//input | //span[@instancename='PHX_SOP0S_Resource_ID']//input");
		this.resourceMoreBtn = page.locator(
				"//div[@instancename='PHX_SOP0S_Resource_ID']//button[contains(@class,'editor-button') and not(contains(@style,'display:none'))] | "
						+ "//span[@instancename='PHX_SOP0S_Resource_ID']//button[contains(@class,'editor-button') and not(contains(@style,'display:none'))]");
		this.equipmentInput = page.locator(
				"//div[@instancename='PHX_SOP0A_Asset_ID']//input | //span[@instancename='PHX_SOP0A_Asset_ID']//input");
		this.equipmentMoreBtn = page.locator(
				"//div[@instancename='PHX_SOP0A_Asset_ID']//button[contains(@class,'editor-button') and not(contains(@style,'display:none'))] | "
						+ "//span[@instancename='PHX_SOP0A_Asset_ID']//button[contains(@class,'editor-button') and not(contains(@style,'display:none'))]");

		// Content Sections
		this.objectiveField = page.locator("//div[@instancename='PHX_SOP0PHX_Objective']");
		this.scopeField = page.locator("//div[@instancename='PHX_SOP0PHX_Scope']");
		this.responsibilityField = page.locator("//div[@instancename='PHX_SOP0PHX_Responsibility']");
		this.procedureField = page.locator("//div[@instancename='PHX_SOP0PHX_Procedure']");

		// Subtabs
		this.abbreviationsTab = page.locator(
				"//ul[@role='tablist']//span[text()='Abbreviations'] | //div[contains(@class,'adwindow-detailpane')]//span[text()='Abbreviations']");
		this.annexureTab = page.locator(
				"//ul[@role='tablist']//span[text()='Annexure'] | //div[contains(@class,'adwindow-detailpane')]//span[text()='Annexure']");
		this.formatTab = page.locator(
				"//ul[@role='tablist']//span[text()='SOP Formats'] | //div[contains(@class,'adwindow-detailpane')]//span[text()='SOP Formats']");
		this.distributionTab = page.locator(
				"//ul[@role='tablist']//span[text()='Distribution List'] | //div[contains(@class,'adwindow-detailpane')]//span[text()='Distribution List']");
		this.revisionHistoryTab = page.locator(
				"//ul[@role='tablist']//span[text()='Revision History'] | //div[contains(@class,'adwindow-detailpane')]//span[text()='Revision History']");

		// Abbreviations Fields
		this.abbrevShortFormInput = page.locator("//input[@instancename='PHX_SOPAbbreviation0PHX_ShortForm']");
		this.abbrevFullFormInput = page.locator("//input[@instancename='PHX_SOPAbbreviation0PHX_FullForm']");

		// Annexure Fields
		this.annexurePreviousVersion = page.locator(
				"(//span[@instancename='PHX_SOPAnnexure0PHX_PrevVersion_ID']//input)[1] | "
						+ "(//div[@instancename='PHX_SOPAnnexure0PHX_PrevVersion_ID']//input)[1] | "
						+ "//input[contains(@instancename,'PHX_PrevVersion_ID')]");
		this.annexureBaseVersion = page.locator(
				"(//div[@instancename='PHX_SOPAnnexure0PHX_Base_ID']//input)[1] | "
						+ "(//span[@instancename='PHX_SOPAnnexure0PHX_Base_ID']//input)[1] | "
						+ "//input[contains(@instancename,'PHX_Base_ID')]");
		this.annexureOriginalSOPVersion = page.locator(
				"(//span[@instancename='PHX_SOPAnnexure0PHX_OrgSOPVer_ID']//input)[1] | "
						+ "(//div[@instancename='PHX_SOPAnnexure0PHX_OrgSOPVer_ID']//input)[1] | "
						+ "//input[contains(@instancename,'PHX_OrgSOPVer_ID')]");
		this.annexureNameInput = page.locator("//input[@instancename='PHX_SOPAnnexure0Annexure']");
		this.annexureNoInput = page.locator("//input[@instancename='PHX_SOPAnnexure0AnnexureNo']");
		this.annexureTitleInput = page.locator("//input[@instancename='PHX_SOPAnnexure0Title']");
		this.annexurePrintPaperInput = page.locator("//span[@instancename='PHX_SOPAnnexure0AD_PrintPaper_ID']//input");
		this.annexureSupersedesNoInput = page.locator("//input[@instancename='PHX_SOPAnnexure0PHX_SupersedesNo']");
		this.annexureContentField = page.locator(
				"//div[@instancename='PHX_SOPAnnexure0ContentText'] | //div[contains(@instancename,'Content')]");
		this.annexureReviseFormatBtn = page
				.locator("//button[contains(.,'Revise Format')] | //button[@instancename='PHX_SOPAnnexure0Revise']");
		this.annexureRevisedCheckbox = page.locator(
				"//span[@instancename='PHX_SOPAnnexure0isRevised']//input | //span[@instancename='PHX_SOPAnnexure0IsRevised']//input | "
						+ "//span[translate(@instancename, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz')='phx_sopannexure0isrevised']//input | "
						+ "//span[contains(@class,'z-checkbox')][.//label[normalize-space(text())='Revised']]//input");

		// SOP Formats Fields
		this.formatPreviousVersion = page.locator(
				"(//span[@instancename='PHX_SOPFormats0PHX_PrevVersion_ID']//input)[1] | "
						+ "(//div[@instancename='PHX_SOPFormats0PHX_PrevVersion_ID']//input)[1] | "
						+ "//input[contains(@instancename,'PHX_PrevVersion_ID')]");
		this.formatBaseVersion = page.locator(
				"(//div[@instancename='PHX_SOPFormats0PHX_Base_ID']//input)[1] | "
						+ "(//span[@instancename='PHX_SOPFormats0PHX_Base_ID']//input)[1] | "
						+ "//input[contains(@instancename,'PHX_Base_ID')]");
		this.formatOriginalSOPVersion = page.locator(
				"(//span[@instancename='PHX_SOPFormats0PHX_OrgSOPVer_ID']//input)[1] | "
						+ "(//div[@instancename='PHX_SOPFormats0PHX_OrgSOPVer_ID']//input)[1] | "
						+ "//input[contains(@instancename,'PHX_OrgSOPVer_ID')]");
		this.formatDocTypeInput = page.locator("//span[@instancename='PHX_SOPFormats0C_DocType_ID']//input");
		this.formatNameInput = page.locator("//input[@instancename='PHX_SOPFormats0Name']");
		this.formatPrintPaperInput = page.locator("//span[@instancename='PHX_SOPFormats0AD_PrintPaper_ID']//input");
		this.formatSupersedesNoInput = page.locator("//input[@instancename='PHX_SOPFormats0PHX_SupersedesNo']");
		this.formatContentField = page
				.locator("//div[@instancename='PHX_SOPFormats0Format'] | //div[contains(@instancename,'Format')]");
		this.formatReviseFormatBtn = page
				.locator("//button[contains(.,'Revise Format')] | //button[@instancename='PHX_SOPFormats0Revise']");
		this.formatRevisedCheckbox = page.locator(
				"//span[@instancename='PHX_SOPFormats0isRevised']//input | //span[@instancename='PHX_SOPFormats0IsRevised']//input | "
						+ "//span[translate(@instancename, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz')='phx_sopformats0isrevised']//input | "
						+ "//span[contains(@class,'z-checkbox')][.//label[normalize-space(text())='Revised']]//input");

		// Distribution List Fields
		this.distDepartmentInput = page.locator(
				"//span[@instancename='PHX_SOPDistribution0C_Department_ID']//input | //div[contains(@class,'z-row')]//span[contains(@instancename,'Department')]//input");
		this.distCopiesInput = page.locator(
				"//div[@instancename='PHX_SOPDistribution0Copies']//input | " +
						"//input[@instancename='PHX_SOPDistribution0Copies'] | " +
						"//div[contains(@class,'z-row') or contains(@class,'z-grid-body')]//input[contains(@class,'z-decimalbox') or contains(@class,'z-intbox') or contains(@class,'editor-input')]");

		// Revision History Fields
		this.historyRevisionInput = page.locator(
				"//div[@instancename='PHX_SOPHistory0Revision']//input | " +
						"//input[@instancename='PHX_SOPHistory0Revision'] | " +
						"//input[contains(@instancename,'SOPHistory') and contains(@instancename,'Revision')] | " +
						"//div[contains(@class,'z-row') and .//span[normalize-space(text())='Revision' or normalize-space(text())='Revision No']]//input | "
						+
						"//div[contains(@class,'z-row') or contains(@class,'z-grid-body')]//input[contains(@class,'z-decimalbox') or contains(@class,'z-intbox') or contains(@class,'editor-input')]");
		this.historyReasonInput = page.locator(
				"//input[@instancename='PHX_SOPHistory0Reason'] | " +
						"//textarea[contains(@instancename,'Reason')] | " +
						"//div[contains(@class,'z-row') and .//span[normalize-space(text())='Reason']]//input | " +
						"//div[contains(@class,'z-row') and .//span[normalize-space(text())='Reason']]//textarea");
		this.historyCCNoInput = page.locator(
				"//input[contains(@instancename,'ChangeControl')] | " +
						"//input[contains(@instancename,'Controll')] | " +
						"//div[contains(@class,'z-row') and .//span[contains(text(),'Change Control')]]//input");

		// Digital Signatures
		this.preparedBySignBtn = page.locator("//div[@instancename='PHX_SOP0PHX_PreparedBySign_ID']//button");
		this.checkedBySignBtn = page.locator("//div[@instancename='PHX_SOP0PHX_CheckedBy_ID']//button");
		this.reviewedBySignBtn = page.locator("//div[@instancename='PHX_SOP0ReviewerSign_ID']//button");
		this.approverSignBtn = page.locator("//div[@instancename='PHX_SOP0ApproverSign_ID']//button");

		// Breadcrumb & Toolbar
		this.sopHeaderBreadcrumb = page.locator("//div[@instancename='breadcrumb']//a").first();
		this.createNewButton = page.locator(
				"//div[contains(@class,'z-tabpanel') and not(contains(@style,'display: none'))]//a[contains(@title,'Shift+Alt+N') or contains(@title,'Save and Create New')] | //div[contains(@class,'adwindow-detailpane-toolbar')]//a[contains(@title,'Shift+Alt+N')]");
		this.saveButton = page.locator(
				"//div[contains(@class,'adwindow-toolbar')]//a[contains(@title,'Save Changes') or contains(@title,'Alt+S')] | "
						+
						"//a[contains(@title,'Save Changes') or contains(@title,'Alt+S')]");
		this.previewButton = page.locator(
				"//div[contains(@class,'adwindow-toolbar')]//a[@title='Preview'] | " +
						"//div[contains(@class,'z-tabpanel') and not(contains(@style,'display: none'))]//a[@title='Preview'] | "
						+
						"//a[@title='Preview']");
	}

	public static String getActiveSOPDocNo() {
		return activeSOPDocNo.get();
	}

	public static void setActiveSOPDocNo(String docNo) {
		activeSOPDocNo.set(docNo);
	}

	public static String getActiveAnnexureName() {
		return activeAnnexureName.get();
	}

	public static void setActiveAnnexureName(String annexure) {
		activeAnnexureName.set(annexure);
	}

	public static String getActiveFormatName() {
		return activeFormatName.get();
	}

	public static void setActiveFormatName(String formatName) {
		activeFormatName.set(formatName);
	}

	/**
	 * Navigates to SOP Master window by searching in Menu Search Box and clicking
	 * '+' icon.
	 */
	public void navigateToSOPMasterWindow() {
		log.info("Searching 'SOP Master' in menu searchbox and clicking '+' icon...");
		menuSearchInput.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		menuSearchInput.fill("SOP Master");
		page.waitForTimeout(3000);

		try {
			sopMasterNewIcon
					.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(5000));
			sopMasterNewIcon.click();
		} catch (Exception e) {
			log.warn("New '+' icon not clicked directly, attempting row click / enter: {}", e.getMessage());
			menuSearchInput.press("Enter");
		}
		page.waitForTimeout(2000);
		WaitUtils.waitForLoadingIndicatorToDisappear(page);
		docTypeInput.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(20000));
		log.info("SOP Master window opened successfully with new record.");
	}

	/**
	 * Fills mandatory and standard details on SOP Master Header and saves the
	 * record.
	 */
	public void fillHeaderAndSave(String docType, String description, String department, String respDepts,
			String masterCopyOwner) {
		log.info("Filling SOP Master Header. DocType: [{}], Desc: [{}], Dept: [{}]", docType, description, department);

		docTypeInput.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		docTypeInput.fill(docType);
		docTypeInput.press("Tab");
		page.waitForTimeout(1000);

		descriptionInput.fill(description);
		descriptionInput.press("Tab");
		page.waitForTimeout(1000);

		departmentInput.fill(department);
		departmentInput.press("Tab");
		page.waitForTimeout(1000);

		// Select Responsible Departments via multi-select popup
		if (respDepts != null && !respDepts.isEmpty()) {
			selectResponsibleDepartments(respDepts);
		}

		if (masterCopyOwner != null && !masterCopyOwner.isEmpty()) {
			masterCopyOwnerInput.fill(masterCopyOwner);
			masterCopyOwnerInput.press("Tab");
			page.waitForTimeout(1000);
		}

		saveRecord();

		String generatedDocNo = getDocumentNo();
		setActiveSOPDocNo(generatedDocNo);
		log.info("SOP Master Header saved. Generated Document No: [{}]", generatedDocNo);
	}

	/**
	 * Populates Resource and Equipment fields on the SOP Master window using their
	 * lookup modal: clicks the lookup button, hits 'ReQuery', selects the 1st option,
	 * and confirms with OK.
	 */
	public void addResourceAndEquipment() {
		addResource();
		addEquipment();
	}

	/**
	 * Populates Resource field using lookup modal: clicks more button,
	 * clicks ReQuery, selects the 1st option, and clicks OK.
	 */
	public void addResource() {
		log.info("Adding Resource on SOP Master window...");
		if (resourceMoreBtn.count() > 0 && resourceMoreBtn.first().isVisible()) {
			resourceMoreBtn.first().click();
		} else {
			resourceInput.click();
			resourceInput.press("Enter");
		}
		page.waitForTimeout(1000);
		WaitUtils.waitForLoadingIndicatorToDisappear(page);

		Locator resModal = page.locator("div.z-window-modal, div.z-window-highlighted");
		resModal.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(15000));

		Locator resRequeryBtn = resModal.locator("button:has-text('ReQuery'), button[title*='ReQuery']");
		resRequeryBtn.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		resRequeryBtn.click();
		page.waitForTimeout(1500);
		WaitUtils.waitForLoadingIndicatorToDisappear(page);

		Locator firstResOption = resModal.locator(".z-listitem .z-listcell").first();
		firstResOption.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		firstResOption.click();
		page.waitForTimeout(500);

		Locator resOkBtn = resModal.locator("button:has-text('OK'), button[title='OK'], button.btn-primary, button.btn-ok").first();
		resOkBtn.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(5000));
		resOkBtn.click();
		page.waitForTimeout(1000);
		WaitUtils.waitForLoadingIndicatorToDisappear(page);

		String selectedResource = resourceInput.inputValue();
		log.info("Resource selected and populated: [{}]", selectedResource);
		Assert.assertFalse(selectedResource.trim().isEmpty(), "Resource should be populated from lookup");
	}

	/**
	 * Populates Equipment field using lookup modal: clicks more button,
	 * clicks ReQuery, selects the 1st option, and clicks OK.
	 */
	public void addEquipment() {
		log.info("Adding Equipment on SOP Master window...");
		if (equipmentMoreBtn.count() > 0 && equipmentMoreBtn.first().isVisible()) {
			equipmentMoreBtn.first().click();
		} else {
			equipmentInput.click();
			equipmentInput.press("Enter");
		}
		page.waitForTimeout(1000);
		WaitUtils.waitForLoadingIndicatorToDisappear(page);

		Locator equipModal = page.locator("div.z-window-modal, div.z-window-highlighted");
		equipModal.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(15000));

		Locator equipRequeryBtn = equipModal.locator("button:has-text('ReQuery'), button[title*='ReQuery']");
		equipRequeryBtn.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		equipRequeryBtn.click();
		page.waitForTimeout(1500);
		WaitUtils.waitForLoadingIndicatorToDisappear(page);

		Locator firstEquipOption = equipModal.locator(".z-listitem .z-listcell").first();
		firstEquipOption.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		firstEquipOption.click();
		page.waitForTimeout(500);

		Locator equipOkBtn = equipModal.locator("button:has-text('OK'), button[title='OK'], button.btn-primary, button.btn-ok").first();
		equipOkBtn.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(5000));
		equipOkBtn.click();
		page.waitForTimeout(1000);
		WaitUtils.waitForLoadingIndicatorToDisappear(page);

		String selectedEquip = equipmentInput.inputValue();
		log.info("Equipment selected and populated: [{}]", selectedEquip);
		Assert.assertFalse(selectedEquip.trim().isEmpty(), "Equipment should be populated from lookup");
	}

	public String getResource() {
		return resourceInput.inputValue();
	}

	public String getEquipment() {
		return equipmentInput.inputValue();
	}

	/**
	 * Handles multi-select checkbox selection for Responsible Departments.
	 */
	private void selectResponsibleDepartments(String commaSeparatedDepts) {
		try {
			// Open multi-select popup safely via ZK widget or click
			page.evaluate(
					"() => { const p = document.querySelector(\"div[instancename='PHX_SOP0PHX_RespDept_ID'] .multi-select-popup\"); if (p && window.zk) zk.Widget.$(p).open(); }");
			page.waitForTimeout(500);

			String[] depts = commaSeparatedDepts.split(",");
			for (String dept : depts) {
				String d = dept.trim();
				Locator checkboxLabel = page.locator(
						"//div[contains(@class,'multi-select-popup')]//label[contains(@class,'z-checkbox-content') and text()='"
								+ d + "']");
				if (checkboxLabel.count() > 0 && checkboxLabel.first().isVisible()) {
					checkboxLabel.first().click();
					page.waitForTimeout(200);
				}
			}
			// Close popup
			page.keyboard().press("Escape");
			page.waitForTimeout(300);
		} catch (Exception e) {
			log.warn("Multi-select department selection encountered non-critical error: {}", e.getMessage());
			try {
				page.keyboard().press("Escape");
			} catch (Exception ignored) {
			}
		}
	}

	/**
	 * Fills a rich-text content section (Objective, Scope, Responsibility,
	 * Procedure)
	 * via the CKEditor modal popup.
	 */
	public void fillContentSection(String sectionName, String text) {
		log.info("Entering text for section: [{}]", sectionName);
		Locator targetDiv;
		switch (sectionName.toLowerCase()) {
			case "objective":
				targetDiv = objectiveField;
				break;
			case "scope":
				targetDiv = scopeField;
				break;
			case "responsibility":
				targetDiv = responsibilityField;
				break;
			case "procedure":
				targetDiv = procedureField;
				break;
			default:
				throw new IllegalArgumentException("Unknown content section: " + sectionName);
		}

		targetDiv.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		targetDiv.dblclick();
		page.waitForTimeout(1500);

		// Fill inside CKEditor iframe in the active text-editor-dialog
		FrameLocator editorFrame = page
				.frameLocator(
						"//div[contains(@class,'text-editor-dialog')]//iframe[contains(@class,'cke_wysiwyg_frame')]")
				.last();
		try {
			editorFrame.locator("body")
					.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
			editorFrame.locator("body").fill(text);
			page.waitForTimeout(500);
		} catch (Exception e) {
			log.warn("Direct iframe fill failed, trying evaluate: {}", e.getMessage());
			editorFrame.locator("body").evaluate("(el, t) => el.innerText = t", text);
		}

		// Click OK on text-editor-dialog
		Locator okBtn = page.locator("//div[contains(@class,'text-editor-dialog')]//button[@title='OK']").last();
		okBtn.click();
		page.waitForTimeout(1000);
		WaitUtils.waitForLoadingIndicatorToDisappear(page);
		log.info("Section [{}] filled and saved.", sectionName);
	}

	/**
	 * Returns the visible element among all matched instances of a locator.
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
	 * Ensures the detail pane subtab is in form view and ready for record input.
	 */
	public void ensureSubrecordFormReady(Locator targetInput) {
		log.info("Ensuring subrecord form is ready for input...");
		for (Locator loc : targetInput.all()) {
			try {
				if (loc.isVisible() && loc.isEnabled()) {
					log.info("Target input is already visible and enabled.");
					return;
				}
			} catch (Exception ignored) {
			}
		}

		// Click New in visible detail pane toolbar
		createNewSubrecord();

		for (Locator loc : targetInput.all()) {
			try {
				if (loc.isVisible() && loc.isEnabled()) {
					log.info("Target input is visible and enabled after createNewSubrecord.");
					return;
				}
			} catch (Exception ignored) {
			}
		}

		// If still in grid view, toggle to Form view
		log.info("Subrecord form not yet active. Toggling form view...");
		toggleSubtabView();

		for (Locator loc : targetInput.all()) {
			try {
				if (loc.isVisible() && loc.isEnabled()) {
					log.info("Target input is ready after toggling form view.");
					return;
				}
			} catch (Exception ignored) {
			}
		}

		// If still not ready, click New once more in form view
		createNewSubrecord();

		for (Locator loc : targetInput.all()) {
			try {
				if (loc.isVisible()) {
					log.info("Target input is ready and visible.");
					return;
				}
			} catch (Exception ignored) {
			}
		}

		getVisibleInput(targetInput)
				.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		log.info("Subrecord form input is ready.");
	}

	/**
	 * Navigates to Abbreviations tab, adds an abbreviation and saves.
	 */
	public void addAbbreviation(String shortForm, String fullForm) {
		log.info("Adding Abbreviation: [{}] = [{}]", shortForm, fullForm);
		goToAbbreviationsTab();
		ensureSubrecordFormReady(abbrevShortFormInput);

		Locator shortInp = getVisibleInput(abbrevShortFormInput);
		shortInp.fill(shortForm);
		// shortInp.press("Tab");

		Locator fullInp = getVisibleInput(abbrevFullFormInput);
		fullInp.fill(fullForm);
		// fullInp.press("Tab");

		saveSubrecord();
		page.waitForTimeout(2000);
		log.info("Abbreviation [{}] saved successfully.", shortForm);
	}

	/**
	 * Navigates to Annexure tab, fills details, content and saves.
	 */
	public void addAnnexure(String annexure, String title, String printPaper, String supersedesNo, String content) {
		log.info("Adding Annexure: [{}], Title: [{}]", annexure, title);
		setActiveAnnexureName(annexure);
		goToAnnexureTab();
		ensureSubrecordFormReady(annexureNameInput);

		Locator nameInp = getVisibleInput(annexureNameInput);
		nameInp.fill(annexure);
		nameInp.press("Tab");

		Locator titleInp = getVisibleInput(annexureTitleInput);
		titleInp.fill(title);
		titleInp.press("Tab");

		if (printPaper != null && !printPaper.isEmpty()) {
			Locator paperInp = getVisibleInput(annexurePrintPaperInput);
			paperInp.fill(printPaper);
			paperInp.press("Tab");
		}

		if (supersedesNo != null && !supersedesNo.isEmpty()) {
			Locator superInp = getVisibleInput(annexureSupersedesNoInput);
			superInp.fill(supersedesNo);
			superInp.press("Tab");
		}

		saveSubrecord();

		if (content != null && !content.isEmpty()) {
			Locator contentField = getVisibleInput(annexureContentField);
			contentField.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(5000));
			contentField.dblclick();
			page.waitForTimeout(1500);

			FrameLocator editorFrame = page.frameLocator(
					"//div[contains(@class,'text-editor-dialog')]//iframe[contains(@class,'cke_wysiwyg_frame')]")
					.last();
			editorFrame.locator("body").fill(content);
			page.locator("//div[contains(@class,'text-editor-dialog')]//button[@title='OK']").last().click();
			page.waitForTimeout(1000);
			saveSubrecord();
		}
		log.info("Annexure [{}] saved successfully.", annexure);
	}

	/**
	 * Navigates to SOP Formats tab, fills format details, content and saves.
	 */
	public void addSOPFormat(String docType, String name, String printPaper, String supersedesNo, String content) {
		log.info("Adding SOP Format: [{}], Name: [{}]", docType, name);
		setActiveFormatName(name);
		goToSOPFormatTab();
		ensureSubrecordFormReady(formatDocTypeInput);

		Locator docInp = getVisibleInput(formatDocTypeInput);
		docInp.fill(docType);
		docInp.press("Tab");

		Locator nameInp = getVisibleInput(formatNameInput);
		nameInp.fill(name);
		nameInp.press("Tab");

		if (printPaper != null && !printPaper.isEmpty()) {
			Locator paperInp = getVisibleInput(formatPrintPaperInput);
			paperInp.fill(printPaper);
			paperInp.press("Tab");
		}

		if (supersedesNo != null && !supersedesNo.isEmpty()) {
			Locator superInp = getVisibleInput(formatSupersedesNoInput);
			superInp.fill(supersedesNo);
			superInp.press("Tab");
		}

		saveSubrecord();

		if (content != null && !content.isEmpty()) {
			Locator formatField = getVisibleInput(formatContentField);
			formatField.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(5000));
			formatField.dblclick();
			page.waitForTimeout(1500);

			FrameLocator editorFrame = page.frameLocator(
					"//div[contains(@class,'text-editor-dialog')]//iframe[contains(@class,'cke_wysiwyg_frame')]")
					.last();
			editorFrame.locator("body").fill(content);
			page.locator("//div[contains(@class,'text-editor-dialog')]//button[@title='OK']").last().click();
			page.waitForTimeout(1000);
			saveSubrecord();
		}
		log.info("SOP Format [{}] saved successfully.", name);
	}

	/**
	 * Navigates to Distribution List tab and adds department distribution.
	 */
	public void addDistributionList(String department, String copies) {
		log.info("Adding Distribution List: Department: [{}], Copies: [{}]", department, copies);
		goToDistributionTab();
		ensureSubrecordFormReady(distDepartmentInput);

		Locator deptInp = getVisibleInput(distDepartmentInput);
		deptInp.fill(department);
		deptInp.press("Tab");
		page.waitForTimeout(300);

		try {
			Locator copiesInp = getVisibleInput(distCopiesInput);
			if (copiesInp.isVisible()) {
				copiesInp.fill(copies);
				copiesInp.press("Tab");
			} else {
				log.info("Direct copies input not visible, typing via keyboard into focused cell...");
				page.keyboard().type(copies);
				page.keyboard().press("Tab");
				page.evaluate("(val) => { " +
						"let inp = document.querySelector(\"div[instancename='PHX_SOPDistribution0Copies'] input\") || "
						+
						"document.querySelector(\"input[instancename='PHX_SOPDistribution0Copies']\") || " +
						"document.querySelector(\".z-decimalbox.editor-input\"); " +
						"if (inp && inp.value !== val) { inp.focus(); inp.value = val; inp.dispatchEvent(new Event('input', {bubbles:true})); inp.dispatchEvent(new Event('change', {bubbles:true})); } "
						+
						"}", copies);
			}
		} catch (Exception e) {
			log.warn("Direct fill failed for copies: {}, attempting keyboard type...", e.getMessage());
			page.keyboard().type(copies);
			page.keyboard().press("Tab");
		}

		saveSubrecord();
		page.waitForTimeout(2000);
		log.info("Distribution record for [{}] saved successfully.", department);
	}

	/**
	 * Navigates to Revision History tab and records revision details.
	 */
	public void addRevisionHistory(String revisionNo, String reason, String changeControlNo) {
		log.info("Adding Revision History record: Rev: [{}], Reason: [{}]", revisionNo, reason);
		goToRevisionHistoryTab();
		ensureSubrecordFormReady(historyRevisionInput);

		Locator revInp = getVisibleInput(historyRevisionInput);
		if (revInp.isVisible()) {
			revInp.fill(revisionNo);
			revInp.press("Tab");
		}
		Locator reasonInp = getVisibleInput(historyReasonInput);
		if (reasonInp.isVisible()) {
			reasonInp.fill(reason);
			reasonInp.press("Tab");
		}
		Locator ccInp = getVisibleInput(historyCCNoInput);
		if (ccInp.isVisible()) {
			ccInp.fill(changeControlNo);
			ccInp.press("Tab");
		}
		saveSubrecord();
		log.info("Revision History record saved.");
	}

	/**
	 * Ensures Approval groupbox on SOP Master header is expanded.
	 */
	public void ensureApprovalSectionExpanded() {
		Locator approvalGroup = page
				.locator("//div[contains(@class,'z-groupbox') and .//span[normalize-space(text())='Approval']]");
		if (approvalGroup.count() > 0) {
			String cls = approvalGroup.first().getAttribute("class");
			if (cls != null && (cls.contains("collapsed") || cls.contains("z-groupbox-collapsed"))) {
				log.info("Approval section is collapsed, expanding it...");
				page.locator(
						"//span[normalize-space(text())='Approval'] | //div[contains(@class,'z-groupbox')]//div[contains(@class,'z-caption')]")
						.first().click();
				page.waitForTimeout(500);
			}
		}
	}

	/**
	 * Completes digital signature on a specific signature section.
	 */
	public void completeDigitalSignature(String signType, String department, String password) {
		log.info("Completing Digital Signature for: [{}]", signType);
		ensureApprovalSectionExpanded();

		Locator signBtn;
		switch (signType.toLowerCase()) {
			case "preparedby":
			case "prepared":
				signBtn = getVisibleInput(preparedBySignBtn);
				break;
			case "checkedby":
			case "checked":
				signBtn = getVisibleInput(checkedBySignBtn);
				break;
			case "reviewedby":
			case "reviewed":
				signBtn = getVisibleInput(reviewedBySignBtn);
				break;
			case "approver":
			case "approved":
				signBtn = getVisibleInput(approverSignBtn);
				break;
			default:
				throw new IllegalArgumentException("Unknown signature type: " + signType);
		}

		signBtn.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		signBtn.click();
		page.waitForTimeout(1000);

		Locator signModal = page.locator(
				"//div[contains(@class,'z-window') and (contains(.,'Digital-Signature') or contains(.,'Digital Signature')) and not(contains(@style,'display: none')) and not(contains(@style,'display:none'))]");
		signModal.first()
				.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));

		Locator deptInput = signModal.locator("//span[text()='Department']//parent::div//span//input");
		deptInput.first()
				.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		if (deptInput.count() > 0 && deptInput.first().isVisible()) {
			deptInput.first().fill(department);
			deptInput.first().press("Tab");

		}

		Locator pwdInput = signModal.locator("input[type='password']");
		pwdInput.first().fill(password);
		page.waitForTimeout(300);

		Locator okBtn = signModal.locator("//button[@title='OK'] | //button[normalize-space(text())='OK']");
		okBtn.first().click();
		page.waitForTimeout(2000);
		WaitUtils.waitForLoadingIndicatorToDisappear(page);
		log.info("Digital signature [{}] completed successfully.", signType);

		// Verify digital signature immediately after signing
		verifyDigitalSignature(signType, department);

		// Cross error popup if displayed & ReQuery
		// crossErrorPopupIfDisplayed();
		// dismissReQueryModalIfPresent();
	}

	private Locator getSignatureInputLocator(String signType) {
		switch (signType.toLowerCase()) {
			case "preparedby":
			case "prepared":
				return page.locator(
						"//div[@instancename='PHX_SOP0PHX_PreparedBySign_ID']//input | "
								+ "//span[@instancename='PHX_SOP0PHX_PreparedBySign_ID']//input | "
								+ "//input[contains(@instancename,'PreparedBySign')] | "
								+ "//div[contains(@class,'z-row') and .//span[contains(text(),'Prepared By')]]//input");
			case "checkedby":
			case "checked":
				return page.locator(
						"//div[@instancename='PHX_SOP0PHX_CheckedBy_ID']//input | "
								+ "//span[@instancename='PHX_SOP0PHX_CheckedBy_ID']//input | "
								+ "//input[contains(@instancename,'CheckedBy')] | "
								+ "//div[contains(@class,'z-row') and .//span[contains(text(),'Checked By')]]//input");
			case "reviewedby":
			case "reviewed":
				return page.locator(
						"//div[@instancename='PHX_SOP0ReviewerSign_ID']//input | "
								+ "//span[@instancename='PHX_SOP0ReviewerSign_ID']//input | "
								+ "//input[contains(@instancename,'ReviewerSign')] | "
								+ "//div[contains(@class,'z-row') and .//span[contains(text(),'Reviewed By')]]//input");
			case "approver":
			case "approved":
				return page.locator(
						"//div[@instancename='PHX_SOP0ApproverSign_ID']//input | "
								+ "//span[@instancename='PHX_SOP0ApproverSign_ID']//input | "
								+ "//input[contains(@instancename,'ApproverSign')] | "
								+ "//div[contains(@class,'z-row') and .//span[contains(text(),'Approver')]]//input");
			default:
				throw new IllegalArgumentException("Unknown signature type: " + signType);
		}
	}

	public String getDigitalSignatureText(String signType) {
		ensureApprovalSectionExpanded();
		Locator inputLocator = getSignatureInputLocator(signType);
		Locator visibleInp = getVisibleInput(inputLocator);
		long start = System.currentTimeMillis();
		String actualText = "";
		while (System.currentTimeMillis() - start < 10000) {
			try {
				if (visibleInp.isVisible()) {
					actualText = visibleInp.inputValue();
					if (actualText == null || actualText.trim().isEmpty()) {
						actualText = visibleInp.getAttribute("value");
					}
					if (actualText == null || actualText.trim().isEmpty()) {
						actualText = visibleInp.innerText();
					}
					if (actualText != null && actualText.toLowerCase().contains("signed by")) {
						break;
					}
				}
			} catch (Exception ignored) {
			}
			page.waitForTimeout(500);
			visibleInp = getVisibleInput(inputLocator);
		}
		return actualText != null ? actualText.trim() : "";
	}

	/**
	 * Reusable method to verify digital signature content.
	 * Verifies that the signature field contains:
	 * 1. "Signed by" text
	 * 2. Login username
	 * 3. Date & Time up to minutes (e.g. yyyy-MM-dd HH:mm)
	 * 4. Department (e.g. "from <department>")
	 *
	 * Syntax example:
	 * "Signed by abhishek initiator on 2026-10-27 14:28:28.062 as Helper from Maintenance"
	 *
	 * @param signType Section name ("PreparedBy", "CheckedBy", "ReviewedBy", "Approver")
	 * @param expectedUser Username expected in signature (if null, auto-retrieved from header)
	 * @param expectedDept Department expected in signature
	 * @param expectedDateTimeMinutes Date & time up to minutes (e.g. "2026-10-27 14:28") or null for auto-validation
	 */
	public void verifyDigitalSignature(String signType, String expectedUser, String expectedDept,
			String expectedDateTimeMinutes) {
		log.info("Verifying Digital Signature for [{}]...", signType);
		String actualText = getDigitalSignatureText(signType);
		log.info("Retrieved Digital Signature text for [{}]: [{}]", signType, actualText);

		// 1. Verify "Signed by" text
		boolean containsSignedBy = actualText.toLowerCase().contains("signed by");
		softAssertTrue(containsSignedBy,
				"[" + signType + "] Digital signature does not contain 'Signed by'! Text: [" + actualText + "]");
		if (containsSignedBy) {
			//log.info("✅ [{}] Signature contains 'Signed by'.", signType);
		} else {
			log.error("❌ [{}] Signature does NOT contain 'Signed by'! Found: [{}]", signType, actualText);
		}

		// 2. Verify Login username
		if (expectedUser == null || expectedUser.trim().isEmpty()) {
			try {
				Locator headerUser = page.locator("//span[contains(@class,'desktop-header-username')]");
				if (headerUser.count() > 0 && headerUser.first().isVisible()) {
					String fullHeader = headerUser.first().innerText();
					if (fullHeader.contains("@")) {
						expectedUser = fullHeader.split("@")[0].trim();
					} else {
						expectedUser = fullHeader.trim();
					}
				}
			} catch (Exception ignored) {
			}
		}

		if (expectedUser != null && !expectedUser.trim().isEmpty()) {
			boolean containsUser = actualText.toLowerCase().contains(expectedUser.trim().toLowerCase());
			softAssertTrue(containsUser,
					"[" + signType + "] Digital signature does not contain expected username: [" + expectedUser
							+ "]! Text: [" + actualText + "]");
			if (containsUser) {
				//log.info("✅ [{}] Signature contains username: [{}]", signType, expectedUser);
			} else {
				log.error("❌ [{}] Signature does NOT contain username: [{}]! Found: [{}]", signType, expectedUser,
						actualText);
			}
		}

		// 3. Verify Date time up to minutes (e.g. 2026-10-27 14:28)
		boolean dateValid = false;
		if (expectedDateTimeMinutes != null && !expectedDateTimeMinutes.trim().isEmpty()) {
			dateValid = actualText.contains(expectedDateTimeMinutes.trim());
			softAssertTrue(dateValid,
					"[" + signType + "] Digital signature does not contain expected date/time ["
							+ expectedDateTimeMinutes + "]! Text: [" + actualText + "]");
		} else {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			long now = System.currentTimeMillis();
			String curMin = sdf.format(new Date(now));
			String prevMin = sdf.format(new Date(now - 60000));
			String nextMin = sdf.format(new Date(now + 60000));
			boolean timeWindowMatch = actualText.contains(curMin) || actualText.contains(prevMin)
					|| actualText.contains(nextMin);
			boolean patternMatch = Pattern.compile("on\\s+\\d{4}-\\d{2}-\\d{2}\\s+\\d{2}:\\d{2}").matcher(actualText)
					.find()
					|| Pattern.compile("\\d{4}-\\d{2}-\\d{2}\\s+\\d{2}:\\d{2}").matcher(actualText).find();
			dateValid = timeWindowMatch || patternMatch;
			softAssertTrue(dateValid,
					"[" + signType + "] Digital signature does not contain valid date-time up to minutes (expected ~["
							+ curMin + "])! Text: [" + actualText + "]");
		}
		if (dateValid) {
			//log.info("✅ [{}] Signature contains valid date-time up to minutes.", signType);
		} else {
			log.error("❌ [{}] Signature date-time verification failed! Found: [{}]", signType, actualText);
		}

		// 4. Verify Department
		if (expectedDept != null && !expectedDept.trim().isEmpty()) {
			boolean containsDept = actualText.toLowerCase().contains(expectedDept.trim().toLowerCase());
			softAssertTrue(containsDept,
					"[" + signType + "] Digital signature does not contain expected department: [" + expectedDept
							+ "]! Text: [" + actualText + "]");
			if (containsDept) {
				//log.info("✅ [{}] Signature contains department: [{}]", signType, expectedDept);
			} else {
				log.error("❌ [{}] Signature does NOT contain department: [{}]! Found: [{}]", signType, expectedDept,
						actualText);
			}
		}

		if (containsSignedBy && dateValid) {
			log.info("✅ [{}] Digital signature verified successfully: [{}]", signType, actualText);
		}
	}

	public void verifyDigitalSignature(String signType, String expectedUser, String expectedDept) {
		verifyDigitalSignature(signType, expectedUser, expectedDept, null);
	}

	public void verifyDigitalSignature(String signType, String expectedDept) {
		verifyDigitalSignature(signType, null, expectedDept, null);
	}

	/**
	 * Checks and dismisses any modal messagebox such as "Current record was changed
	 * by another user, please ReQuery".
	 */
	public void dismissReQueryModalIfPresent() {
		try {
			Locator modal = page.locator(
					"//div[contains(@class,'z-messagebox') or (contains(@class,'z-window-modal') and (contains(.,'changed by another user') or contains(.,'please ReQuery') or contains(.,'ReQuery')))]");
			if (modal.count() > 0 && modal.first().isVisible()) {
				String txt = modal.first().innerText();
				log.info("Detected modal: [{}]", txt.trim());
				Locator crossBtn = modal.first().locator(
						"div.z-window-close, div.z-window-icon-close, [aria-label='Close'], div.z-window-header-close, button.z-window-close")
						.first();
				if (crossBtn.count() > 0 && crossBtn.first().isVisible()) {
					log.info("Crossing popup via 'X' button...");
					crossBtn.first().click(new Locator.ClickOptions().setForce(true).setTimeout(3000));
				} else {
					Locator okBtn = modal.first()
							.locator("button.btn-primary, button.z-messagebox-btn, button[title='OK']").first();
					if (okBtn.count() > 0 && okBtn.isVisible()) {
						okBtn.click(new Locator.ClickOptions().setForce(true).setTimeout(3000));
					} else {
						page.keyboard().press("Escape");
					}
				}
				page.waitForTimeout(1000);
				WaitUtils.waitForLoadingIndicatorToDisappear(page);

				if (txt.contains("ReQuery") || txt.contains("changed by another user")) {
					log.info("Performing ReQuery from toolbar to synchronize record...");
					Toolbar tb = new Toolbar(page);
					tb.hitRequeryButton();
				}
			}
		} catch (Exception e) {
			log.warn("Exception in dismissReQueryModalIfPresent: {}", e.getMessage());
		}
	}

	/**
	 * Performs Complete Document Action and assigns to the specified user in
	 * Approver Window.
	 */
	public void completeDocActionAndAssignTo(String assignToUser) {
		log.info("Executing Document Action 'Complete' and assigning to: [{}]", assignToUser);
		crossErrorPopupIfDisplayed();
		dismissReQueryModalIfPresent();

		Locator visibleDocAction = getVisibleInput(docActionButton);
		visibleDocAction.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(15000));
		visibleDocAction.click();
		WaitUtils.waitForLoadingIndicatorToDisappear(page);

		Locator docActionDialog = page.locator("//div[@instancename='documentAction']");
		try {
			docActionDialog
					.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(6000));
			Locator actionSelect = docActionDialog.locator("select");
			if (actionSelect.count() > 0 && actionSelect.first().isVisible()) {
				try {
					actionSelect.first()
							.selectOption(new com.microsoft.playwright.options.SelectOption().setLabel("Complete"));
				} catch (Exception ignored) {
				}
			}
			Locator docActionOkBtn = docActionDialog.locator("button.btn-primary, button[title='OK']").first();
			docActionOkBtn.click();
			WaitUtils.waitForBigLoadingIndicatorToDisappear(page);
			page.waitForTimeout(1000);
		} catch (Exception e) {
			log.warn("Document action dialog handling note: {}", e.getMessage());
		}

		// Fill Assign To on Approver Window
		WaitUtils.waitForLoadingIndicatorToDisappear(page);
		page.waitForTimeout(1000);
		Locator approverModal = page.locator("div.z-window:not(.z-window-embedded)")
				.filter(new Locator.FilterOptions().setHasText("Assign To")).first();
		approverModal.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(25000));

		Locator assignInput = approverModal.locator(
				"tr:has-text('Assign To') input, span[instancename*='AD_User_ID'] input, input:not([type='hidden'])")
				.first();
		assignInput.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		assignInput.click();
		assignInput.fill(assignToUser);
		assignInput.press("Tab");
		page.waitForTimeout(1000);

		// Handle Lookup window if it opened
		Locator lookupWindow = page.locator("div.z-window:not(.z-window-embedded)")
				.filter(new Locator.FilterOptions().setHasText("User"));
		if (lookupWindow.count() > 0 && lookupWindow.first().isVisible()) {
			Locator reqBtn = lookupWindow.locator("button[title*='ReQuery'], button:has-text('ReQuery')");
			if (reqBtn.count() > 0 && reqBtn.first().isVisible()) {
				reqBtn.first().click();
				page.waitForTimeout(1000);
			}
			Locator userRow = lookupWindow
					.locator("tr:has-text('" + assignToUser + "'), span:has-text('" + assignToUser + "')");
			if (userRow.count() > 0 && userRow.first().isVisible()) {
				userRow.first().click();
				page.waitForTimeout(500);
			} else {
				Locator nameInp = lookupWindow.locator("input[instancename*='Name'], input.z-textbox").first();
				if (nameInp.isVisible()) {
					nameInp.fill(assignToUser);
					nameInp.press("Enter");
					page.waitForTimeout(500);
				}
			}
			Locator lookupOk = lookupWindow.locator("button.btn-primary, button[title='OK']").first();
			if (lookupOk.isVisible()) {
				lookupOk.click();
				page.waitForTimeout(500);
			}
		}

		Locator okBtn = approverModal.locator("button.btn-primary, button:has(.z-icon-check), button[title='OK']")
				.first();
		okBtn.click();
		page.waitForTimeout(3000);
		WaitUtils.waitForLoadingIndicatorToDisappear(page);
		log.info("Approver assigned to [{}] and submitted successfully.", assignToUser);
	}

	/**
	 * Performs Document Action and selects Yes/No answer option.
	 */
	public void performDocActionWithAnswer(String answer, String assignToUser) {
		log.info("Performing Document Action with Answer: [{}]", answer);

		// Handle any lingering dialogs
		Locator alertMsg = page.locator(
				"//div[contains(@class,'z-messagebox') or contains(@class,'popup-dialog') or contains(@class,'z-window-modal')]");
		if (alertMsg.count() > 0 && alertMsg.first().isVisible()) {
			String txt = alertMsg.first().innerText();
			log.info("Detected dialog before Document Action: [{}]", txt.trim());
			Locator okBtn = alertMsg.locator(
					"//button[contains(.,'OK') or contains(@class,'z-messagebox-btn')] | //button[@title='OK']");
			if (okBtn.count() > 0 && okBtn.first().isVisible()) {
				okBtn.first().click();
				page.waitForTimeout(1000);
			}
			if (txt.contains("ReQuery")) {
				log.info("Performing ReQuery before Document Action...");
				page.keyboard().press("Alt+F5");
				page.waitForTimeout(2000);
				WaitUtils.waitForLoadingIndicatorToDisappear(page);
			}
		}

		Locator visibleDocAction = getVisibleInput(docActionButton);
		visibleDocAction.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		visibleDocAction.click();
		page.waitForTimeout(1000);
		WaitUtils.waitForLoadingIndicatorToDisappear(page);

		Locator answerSelect = page.locator(
				"//div[contains(@class,'doc-action-dialog')]//select | //div[@instancename='documentAction']//select");
		if (answerSelect.count() > 0 && answerSelect.first().isVisible()) {
			try {
				answerSelect.first().selectOption(answer);
			} catch (Exception e) {
				try {
					answerSelect.first()
							.selectOption(new com.microsoft.playwright.options.SelectOption().setLabel(answer));
				} catch (Exception ignored) {
				}
			}
		}

		Locator docActionOkBtn = page.locator(
				"//div[contains(@class,'doc-action-dialog')]//button[@title='OK'] | //div[@instancename='documentAction']//button[@title='OK']");
		docActionOkBtn.first()
				.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		docActionOkBtn.first().click();
		page.waitForTimeout(2000);

		if (assignToUser != null && !assignToUser.isEmpty()) {
			WaitUtils.waitForLoadingIndicatorToDisappear(page);
			page.waitForTimeout(1000);
			Locator approverModal = page.locator("div.z-window:not(.z-window-embedded)")
					.filter(new Locator.FilterOptions().setHasText("Assign To")).first();
			try {
				approverModal
						.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(20000));
			} catch (Exception e) {
				log.warn("Approver modal not found or already closed: {}", e.getMessage());
			}

			if (approverModal.count() > 0 && approverModal.first().isVisible()) {
				Locator assignInput = approverModal.locator(
						"tr:has-text('Assign To') input, span[instancename*='AD_User_ID'] input, input:not([type='hidden'])")
						.first();
				assignInput
						.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
				assignInput.click();
				assignInput.fill(assignToUser);
				assignInput.press("Tab");
				page.waitForTimeout(1000);

				// Handle Lookup window if opened
				Locator lookupWindow = page.locator("div.z-window:not(.z-window-embedded)")
						.filter(new Locator.FilterOptions().setHasText("User"));
				if (lookupWindow.count() > 0 && lookupWindow.first().isVisible()) {
					Locator nameInp = lookupWindow.locator("input[instancename*='Name'], input.z-textbox").first();
					nameInp.fill(assignToUser);
					nameInp.press("Enter");
					page.waitForTimeout(500);
					Locator lookupOk = lookupWindow.locator("button.btn-primary, button[title='OK']").first();
					lookupOk.click();
					page.waitForTimeout(500);
				}

				Locator okBtn = approverModal
						.locator("button.btn-primary, button:has(.z-icon-check), button[title='OK']").first();
				okBtn.click();
				page.waitForTimeout(2000);
			}
		}
		WaitUtils.waitForLoadingIndicatorToDisappear(page);
		log.info("Document action with Answer [{}] completed.", answer);
	}

	/**
	 * Performs Document Action with Effective Date input (for QAManager approval
	 * step).
	 */
	public void approveWithEffectiveDate(String date) {
		log.info("Approving Document Action with Effective Date: [{}]", date);
		dismissReQueryModalIfPresent();
		Locator visibleDocAction = getVisibleInput(docActionButton);
		visibleDocAction.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		visibleDocAction.click();
		page.waitForTimeout(1000);
		dismissReQueryModalIfPresent();

		Locator dateInput = page.locator(
				"//div[contains(@class,'doc-action-dialog')]//input[contains(@class,'z-datebox-input')] | //div[@instancename='documentAction']//input[contains(@class,'z-datebox-input')]");
		if (dateInput.count() > 0 && dateInput.first().isVisible()) {
			dateInput.first().fill(date);
			dateInput.first().press("Tab");
		}

		Locator okBtn = page.locator(
				"//div[contains(@class,'doc-action-dialog')]//button[@title='OK'] | //div[@instancename='documentAction']//button[@title='OK']");
		okBtn.first().click();
		page.waitForTimeout(2000);
		WaitUtils.waitForLoadingIndicatorToDisappear(page);
	}

	/**
	 * Clicks Document Action button and submits default action if dialog appears.
	 */
	public void clickDocAction() {
		log.info("Clicking Document Action button...");
		dismissReQueryModalIfPresent();
		Locator visibleDocAction = getVisibleInput(docActionButton);
		visibleDocAction.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		visibleDocAction.click();
		page.waitForTimeout(1000);
		WaitUtils.waitForLoadingIndicatorToDisappear(page);
		dismissReQueryModalIfPresent();

		Locator okBtn = page.locator(
				"//div[contains(@class,'doc-action-dialog')]//button[@title='OK'] | //div[@instancename='documentAction']//button[@title='OK']");
		if (okBtn.count() > 0 && okBtn.first().isVisible()) {
			okBtn.first().click();
			page.waitForTimeout(1000);
		}
	}

	/**
	 * Verifies an error popup dialog message or inline status error and dismisses
	 * it if dialog.
	 */
	public void verifyErrorMessageAndClose(String expectedText) {
		log.info("Verifying error message containing: [{}]...", expectedText);
		page.waitForTimeout(1000);

		String expLower = expectedText.toLowerCase();
		long startTime = System.currentTimeMillis();
		boolean found = false;
		String actualMessage = "";

		Locator msgBox = page.locator(
				"//div[contains(@class,'z-messagebox') or contains(@class,'popup-dialog') or contains(@class,'z-window-modal') or contains(@class,'z-window-highlighted')]");
		Locator inlineStatus = page.locator(
				"//div[contains(@class,'status-line')] | " +
						"//div[contains(@class,'adwindow-north')]//*[contains(@class,'z-label')] | " +
						"//div[contains(@class,'adwindow')]//*[contains(@style,'color: red') or contains(@style,'color:red') or contains(@class,'error')] | "
						+
						"//*[contains(text(),'pending') or contains(text(),'Assigned To') or contains(text(),'not uploaded') or contains(text(),'Error')]");

		while (System.currentTimeMillis() - startTime < 10000) {
			// 1. Check modal popup dialog
			try {
				page.waitForTimeout(2000);
				if (msgBox.count() > 0 && msgBox.first().isVisible()) {
					actualMessage = msgBox.first().innerText().trim();
					found = true;
					// Cross the error popup via OK button or 'X' button
					Locator okBtn = msgBox.locator(
							"button.z-messagebox-button, button.z-button, button.btn-primary, button[title='OK'], button")
							.first();
					if (okBtn.count() > 0 && okBtn.isVisible()) {
						okBtn.click(new Locator.ClickOptions().setForce(true).setTimeout(3000));
						log.info("Clicked OK button on error popup.");
					} else {
						Locator crossBtn = msgBox.locator(
								"div[class*='z-window-close'], a[class*='z-window-close'], div[class*='z-window-icon'], [aria-label='Close'], i[class*='times'], i[class*='close']")
								.first();
						if (crossBtn.count() > 0 && crossBtn.isVisible()) {
							log.info("Crossing error popup via 'X' button...");
							crossBtn.click(new Locator.ClickOptions().setForce(true).setTimeout(3000));
						}
					}
					page.waitForTimeout(1000);
					break;
				}
			} catch (Exception ignored) {
			}

			// 2. Check inline status message on the window
			try {
				if (inlineStatus.count() > 0) {
					for (Locator loc : inlineStatus.all()) {
						if (loc.isVisible()) {
							String txt = loc.innerText().trim();
							if (!txt.isEmpty() && (txt.toLowerCase().contains("pending")
									|| txt.toLowerCase().contains("assigned to") || txt.toLowerCase().contains("error")
									|| txt.toLowerCase().contains("not uploaded"))) {
								actualMessage = txt;
								found = true;
								log.info("Found inline status error with text: [{}]", actualMessage);
								break;
							}
						}
					}
					if (found) {
						break;
					}
				}
			} catch (Exception ignored) {
			}

			page.waitForTimeout(500);
		}

		Assert.assertTrue(found,
				"No error message or modal popup was displayed within 10s for expected: [" + expectedText + "]");

		boolean textMatches = actualMessage.toLowerCase().contains(expLower)
				|| (expLower.contains("prepared by") && actualMessage.toLowerCase().contains("prepared by")
						&& actualMessage.toLowerCase().contains("pending"))
				|| (expLower.contains("assigned to") && actualMessage.toLowerCase().contains("assigned to"))
				|| (expLower.contains("document not uploaded")
						&& actualMessage.toLowerCase().contains("document not uploaded"));

		Assert.assertTrue(textMatches,
				"Error message text mismatch. Expected to contain: [" + expectedText + "], but got: [" + actualMessage
						+ "]");
		log.info("✅ Error message verified successfully: [{}]", actualMessage);
		crossErrorPopupIfDisplayed();
	}

	/**
	 * Crosses (closes) any active error popup by clicking the 'X' (cross) icon or
	 * OK button.
	 */
	public void crossErrorPopup() {
		log.info("Crossing error popup by clicking 'X' icon or OK button...");
		try {
			Locator modal = page.locator(
					"//div[contains(@class,'z-messagebox') or contains(@class,'popup-dialog') or contains(@class,'z-window-highlighted') or (contains(@class,'z-window-modal') and not(contains(@class,'doc-action-dialog')) and not(contains(@instancename,'documentAction')) and not(contains(.,'Approver')))]")
					.first();
			if (modal.count() > 0 && modal.isVisible()) {
				// 1. Try OK / checkmark button inside modal
				Locator okBtn = modal.locator(
						"button.z-messagebox-button, button.z-button, button.btn-primary, button[title='OK'], button")
						.first();
				if (okBtn.count() > 0 && okBtn.isVisible()) {
					okBtn.click(new Locator.ClickOptions().setForce(true).setTimeout(3000));
					log.info("Clicked OK button on error popup.");
				} else {
					// 2. Try 'X' close icon on modal
					Locator crossBtn = modal.locator(
							"div[class*='z-window-close'], a[class*='z-window-close'], div[class*='z-window-icon'], [aria-label='Close'], i[class*='times'], i[class*='close']")
							.first();
					if (crossBtn.count() > 0 && crossBtn.isVisible()) {
						crossBtn.click(new Locator.ClickOptions().setForce(true).setTimeout(3000));
						log.info("Clicked 'X' on error popup.");
					} else {
						page.keyboard().press("Escape");
					}
				}
				page.waitForTimeout(1000);
				WaitUtils.waitForLoadingIndicatorToDisappear(page);
			} else {
				log.info("No modal error popup displayed to cross.");
			}

			// Ensure any lingering modal mask is removed
			Locator mask = page.locator("div.z-modal-mask, div.z-window-modal-mask");
			if (mask.count() > 0 && mask.first().isVisible()) {
				page.evaluate("() => { " +
						"let btns = document.querySelectorAll('.z-window-highlighted button, .z-window-modal button, .z-messagebox button, .z-window-close, [class*=\"z-messagebox-btn\"]'); "
						+
						"for (let b of btns) { try { b.click(); } catch(e){} } " +
						"}");
				page.waitForTimeout(1000);
			}
		} catch (Exception e) {
			log.warn("Error crossing popup: {}", e.getMessage());
		}
	}

	/**
	 * Crosses (closes) the error popup if it is displayed on screen.
	 */
	public void crossErrorPopupIfDisplayed() {
		crossErrorPopup();
	}

	/**
	 * Clicks Revise SOP button and confirms the process dialog.
	 */
	public void clickReviseSOP() {
		log.info("Clicking Revise SOP button...");
		reviseButton.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		reviseButton.click();

		Locator modalDialog = page.locator(
				"//div[contains(@class,'z-window-modal') or contains(@class,'z-window-highlighted') or contains(@class,'z-window')][.//div[contains(@class,'z-window-header') and contains(.,'Revise SOP')] or contains(.,'Do you want to start the Process?') or contains(.,'Revise SOP')]");
		try {
			modalDialog.first()
					.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(15000));
			log.info("Revise SOP modal dialog appeared.");

			Locator confirmOkBtn = page.locator(
					"//div[contains(@class,'z-window') and (contains(.,'Revise SOP') or contains(.,'Do you want to start the Process?'))]//button[contains(.,'OK') or contains(@class,'z-messagebox-button')] | "
							+
							"//div[contains(@class,'z-window-modal') or contains(@class,'z-window-highlighted')]//button[contains(.,'OK') or contains(@class,'z-messagebox-button')]");
			confirmOkBtn.first()
					.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
			confirmOkBtn.first().click();
			log.info("Confirmed Revise SOP process dialog (clicked OK).");
			page.waitForTimeout(3000);
			WaitUtils.waitForLoadingIndicatorToDisappear(page);
		} catch (Exception e) {
			log.warn("Could not confirm Revise SOP process dialog: {}", e.getMessage());
		}
		log.info("Revise SOP process initiated.");
	}

	/**
	 * Clicks on the Revised SOP reference link displayed in the message dialog.
	 */
	public void openRevisedSOPLink() {
		log.info("Waiting for Revise SOP completion dialog / link...");
		try {
			Locator link = page.locator(
					"//a[normalize-space(text())='Revised SOP' or contains(text(),'Revised SOP')] | " +
							"//div[contains(.,'Process completed successfully')]//a");
			link.first().waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(25000));
			if (link.count() > 0 && link.first().isVisible()) {
				log.info("Found Revised SOP link: [{}]. Clicking it...", link.first().innerText());
				link.first().click();
				page.waitForTimeout(3000);
				WaitUtils.waitForLoadingIndicatorToDisappear(page);
				log.info("Opened Revised SOP link.");
			}

			// Dismiss any remaining process window if still visible
			Locator checkBtn = page.locator(
					"//div[contains(@class,'z-window') and contains(.,'Process completed successfully')]//button | " +
							"//div[contains(@class,'z-window') and contains(.,'Revise SOP')]//button | " +
							"//div[contains(@class,'z-window') and contains(.,'Process completed successfully')]//div[contains(@class,'z-window-close')] | "
							+
							"//div[contains(@class,'z-window') and contains(.,'Revise SOP')]//div[contains(@class,'z-window-close')]");
			for (Locator btn : checkBtn.all()) {
				try {
					if (btn.isVisible()) {
						log.info("Closing completion dialog button/icon...");
						btn.click();
						page.waitForTimeout(1000);
						WaitUtils.waitForLoadingIndicatorToDisappear(page);
						break;
					}
				} catch (Exception ignored) {
				}
			}
		} catch (Exception e) {
			log.info("Completion dialog / link handling: {}", e.getMessage());
		}
	}

	/**
	 * Clicks 'Revise Format' button on Annexure tab.
	 */
	public void reviseAnnexureFormat() {
		page.waitForTimeout(2000);
		log.info("Clicking Revise Format button on Annexure tab...");
		Locator btn = getVisibleInput(annexureReviseFormatBtn);
		if (btn.isVisible()) {
			btn.click();
			page.waitForTimeout(1000);
			Locator confirmOk = page.locator("//div[contains(@class,'z-messagebox')]//button[contains(.,'OK')]");
			Locator visibleConfirm = getVisibleInput(confirmOk);
			if (visibleConfirm.isVisible()) {
				visibleConfirm.click();
				page.waitForTimeout(2000);
			}
		}
	}

	/**
	 * Clicks 'Revise Format' button on SOP Formats tab.
	 */
	public void reviseSOPFormat() {
		page.waitForTimeout(2000);
		log.info("Clicking Revise Format button on SOP Formats tab...");
		Locator btn = getVisibleInput(formatReviseFormatBtn);
		if (btn.isVisible()) {
			btn.click();
			page.waitForTimeout(1000);
			Locator confirmOk = page.locator("//div[contains(@class,'z-messagebox')]//button[contains(.,'OK')]");
			Locator visibleConfirm = getVisibleInput(confirmOk);
			if (visibleConfirm.isVisible()) {
				visibleConfirm.click();
				page.waitForTimeout(2000);
			}
		}
	}

	/**
	 * Navigates to the Previous Version of the SOP record.
	 */
	public void openPreviousVersion() {
		log.info("Opening Previous Version of SOP...");
		try {
			Locator prevLink = page.locator(
					"//a[normalize-space(text())='Previous Version'] | " +
							"//span[@instancename='PHX_SOP0PHX_PrevSOP_ID']//a | " +
							"//div[contains(@class,'z-row') and .//span[text()='Previous Version']]//a | " +
							"(//li[contains(@class,'z-tab') and .//span[contains(text(),'SOP Master')]])[1]");
			if (prevLink.count() > 0 && prevLink.first().isVisible()) {
				prevLink.first().click();
				page.waitForTimeout(2000);
				WaitUtils.waitForLoadingIndicatorToDisappear(page);
				log.info("Navigated to Previous Version.");
			}
		} catch (Exception e) {
			log.warn("Could not navigate to previous version via link: {}", e.getMessage());
		}
	}

	public void switchToSubTab(String tabName) {
		log.info("Switching to subtab via double click: [{}]", tabName);

		// 1. Check if already on this subtab in breadcrumb
		try {
			Locator breadcrumb = page.locator("//div[@instancename='breadcrumb']");
			Locator visibleBreadcrumb = getVisibleInput(breadcrumb);
			if (visibleBreadcrumb.isVisible()) {
				String bText = visibleBreadcrumb.innerText();
				if (bText != null && bText.contains(tabName)) {
					log.info("Already on subtab [{}].", tabName);
					return;
				}
			}
		} catch (Exception ignored) {
		}

		// 2. If currently zoomed into a different subtab, return to SOP Master header
		// first
		try {
			Locator headerBreadcrumb = page
					.locator("//div[@instancename='breadcrumb']//a[normalize-space(text())='SOP Master']");
			Locator visibleHeaderBreadcrumb = getVisibleInput(headerBreadcrumb);
			if (visibleHeaderBreadcrumb.isVisible()) {
				log.info("Currently on a different subtab view, returning to SOP Master header...");
				visibleHeaderBreadcrumb.click();
				page.waitForTimeout(1000);
				WaitUtils.waitForLoadingIndicatorToDisappear(page);
			}
		} catch (Exception ignored) {
		}

		// 3. Locate the subtab on the detail pane tab row
		Locator subtab = page.locator(
				"//div[contains(@class,'adwindow-detailpane')]//span[normalize-space(text())='" + tabName + "'] | " +
						"//li[contains(@class,'z-tab')]//span[normalize-space(text())='" + tabName + "'] | " +
						"//ul[@role='tablist']//span[normalize-space(text())='" + tabName + "']");

		Locator visibleSubtab = getVisibleInput(subtab);
		visibleSubtab.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		log.info("Double-clicking subtab [{}]...", tabName);
		visibleSubtab.dblclick();
		page.waitForTimeout(1500);
		WaitUtils.waitForLoadingIndicatorToDisappear(page);

		// Verify if zoomed in; if not yet zoomed, attempt dblclick on parent li
		try {
			Locator breadcrumb = page.locator("//div[@instancename='breadcrumb']");
			Locator visibleBreadcrumb = getVisibleInput(breadcrumb);
			if (visibleBreadcrumb.isVisible() && !visibleBreadcrumb.innerText().contains(tabName)) {
				Locator tabLi = visibleSubtab.locator("xpath=./ancestor-or-self::li[contains(@class,'z-tab')]");
				if (tabLi.count() > 0 && tabLi.first().isVisible()) {
					log.info("Retrying double click on subtab li...");
					tabLi.first().dblclick();
					page.waitForTimeout(1500);
					WaitUtils.waitForLoadingIndicatorToDisappear(page);
				}
			}
		} catch (Exception ignored) {
		}

		try {
			page.waitForTimeout(2000);
			if (page.locator("(//div[@class='z-frozen'])[1]").isVisible()) {
				page.locator("//a[@title='Grid Toggle    Alt+T']").click();
				page.waitForTimeout(2000);
				WaitUtils.waitForLoadingIndicatorToDisappear(page);
			}
		} catch (Exception e) {
		}

		log.info("Subtab [{}] double-clicked successfully.", tabName);
	}

	public void goToAbbreviationsTab() {
		switchToSubTab("Abbreviations");
	}

	public void goToAnnexureTab() {
		switchToSubTab("Annexure");
	}

	public void goToSOPFormatTab() {
		switchToSubTab("SOP Formats");
	}

	public void goToDistributionTab() {
		switchToSubTab("Distribution List");
	}

	public void goToRevisionHistoryTab() {
		switchToSubTab("Revision History");
	}

	public void navigateBackOnSOPHeader() {
		log.info("Navigating back to SOP Master Header...");
		Locator crumb = page.locator(
				"//div[@instancename='breadcrumb']//a[normalize-space(text())='SOP Master'] | " +
						"//div[@instancename='breadcrumb']//a[contains(text(),'SOP Master')]");
		if (crumb.count() > 0 && crumb.first().isVisible()) {
			crumb.first().click();
			page.waitForTimeout(1000);
			WaitUtils.waitForLoadingIndicatorToDisappear(page);
			log.info("Returned to SOP Master Header.");
		} else {
			log.info("Already on SOP Master Header.");
		}
	}

	public void ensureOnSOPMasterHeader(String sopDocNo) {
		log.info("Ensuring on SOP Master header for docNo: [{}]...", sopDocNo);

		// 1. If Report tab is still present, close it
		Locator reportTab = page
				.locator("//li[contains(@class,'z-tab') and (contains(.,'Report') or contains(.,'Report: SOP'))]");
		if (reportTab.count() > 0 && reportTab.first().isVisible()) {
			log.info("Report tab detected, attempting to close it...");
			Locator closeBtn = page.locator(
					"//li[contains(@class,'z-tab') and (contains(.,'Report') or contains(.,'Report: SOP'))]//a[contains(@class,'z-tab-remove') or contains(@class,'z-tab-close')] | "
							+
							"//li[contains(@class,'z-tab') and (contains(.,'Report') or contains(.,'Report: SOP'))]//i[contains(@class,'z-icon-times') or contains(@class,'z-tab-icon') or @aria-label='Close'] | "
							+
							"//li[contains(@class,'z-tab-selected') and contains(.,'Report')]//a[contains(@class,'z-tab-remove') or contains(@class,'z-tab-close')] | "
							+
							"//li[contains(@class,'z-tab-selected') and contains(.,'Report')]//i[contains(@class,'z-icon-times') or contains(@class,'z-tab-icon')]");
			if (closeBtn.count() > 0 && closeBtn.first().isVisible()) {
				try {
					closeBtn.first().click(new Locator.ClickOptions().setForce(true));
				} catch (Exception ignored) {
				}
				page.waitForTimeout(1000);
				WaitUtils.waitForLoadingIndicatorToDisappear(page);
			}
			try {
				page.evaluate("() => { " +
						"let tab = Array.from(document.querySelectorAll('li.z-tab')).find(el => el.textContent.includes('Report')); "
						+
						"if (tab) { " +
						"  let btn = tab.querySelector('.z-tab-remove, .z-tab-close, [aria-label=\"Close\"], [class*=\"remove\"], [class*=\"close\"], [class*=\"icon-times\"]'); "
						+
						"  if (btn) btn.click(); " +
						"  if (window.zk) { " +
						"    let w = zk.Widget.$(tab); " +
						"    if (w) { try { w.close(); } catch(e) {} try { zAu.send(new zk.Event(w, 'onClose')); } catch(e) {} } "
						+
						"  } " +
						"} " +
						"}");
				page.waitForTimeout(1000);
				WaitUtils.waitForLoadingIndicatorToDisappear(page);
			} catch (Exception ignored) {
			}
		}

		// 2. Click on SOP Master window tab if visible and not selected
		Locator sopTab = page.locator("//li[contains(@class,'z-tab') and contains(.,'SOP Master')]");
		if (sopTab.count() > 0 && sopTab.first().isVisible()) {
			log.info("Clicking on SOP Master tab to focus...");
			sopTab.first().click();
			page.waitForTimeout(1000);
			WaitUtils.waitForLoadingIndicatorToDisappear(page);
		} else {
			// If not active, reopen via menu search
			if (docActionButton.count() == 0 || !docActionButton.first().isVisible()) {
				log.info("SOP Master not active, reopening via menu search and looking up record [{}]...", sopDocNo);
				menuSearchInput
						.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
				menuSearchInput.fill("SOP Master");
				page.waitForTimeout(2000);
				Locator menuItem = page.locator(
						"//div[contains(@class,'z-bandbox-popup')]//tr[contains(.,'SOP Master')] | //div[contains(@class,'z-bandbox-popup')]//span[text()='SOP Master']");
				if (menuItem.count() > 0 && menuItem.first().isVisible()) {
					menuItem.first().click();
				} else {
					menuSearchInput.press("Enter");
				}
				page.waitForTimeout(2000);
				WaitUtils.waitForLoadingIndicatorToDisappear(page);
				if (sopDocNo != null && !sopDocNo.isEmpty()) {
					Toolbar tb = new Toolbar(page);
					tb.searchDocNo(sopDocNo);
				}
			}
		}

		// 3. Ensure we are on Header and in Form View
		navigateBackOnSOPHeader();
		ensureHeaderInFormView(sopDocNo);
		log.info("SOP Master Header is confirmed active and ready.");
	}

	public void navigateToSOPrecord(String sopNODoc) {
		log.info("Executing SOP Master debug flow: Searching SOP Master in global search and opening record " + sopNODoc
				+ "...");
		page.waitForTimeout(1000);

		// If SOP Master is already open on this document, no need to re-search
		Locator headerDocNoInp = page.locator("//input[@instancename='PHX_SOP0DocumentNo']");
		Locator visibleDocNo = getVisibleInput(headerDocNoInp);
		if (visibleDocNo.isVisible() && visibleDocNo.inputValue().trim().equals(sopNODoc)) {
			log.info("SOP Master is already open with record [{}] loaded.", sopNODoc);
			return;
		}

		Locator searchBox = page.locator(
				"//span[@class='global-search-box z-bandbox']//input | //input[contains(@class,'z-bandbox-input')]");
		searchBox.first()
				.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		searchBox.first().fill("SOP Master");
		page.waitForTimeout(3000);

		Locator sopMasterOption = page.locator(
				"//div[contains(@class,'global-search-tabpanel')]//tr[contains(@class,'z-listitem') and contains(.,'SOP Master')] | //span[contains(@class,'highlight') and text()='SOP Master']")
				.first();
		try {
			if (sopMasterOption.isVisible()) {
				sopMasterOption.click();
			} else {
				searchBox.first().press("Enter");
			}
		} catch (Exception e) {
			searchBox.first().press("Enter");
		}
		page.waitForTimeout(2000);
		WaitUtils.waitForLoadingIndicatorToDisappear(page);

		Locator docNoLookup = page.locator("//input[@instancename='DocumentNo']").first();
		try {
			docNoLookup.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(5000));
			docNoLookup.fill(sopNODoc);
			page.waitForTimeout(500);

			Locator okBtn = page.locator("//button[@title='OK' and not(contains(@style,'display: none'))]").first();
			okBtn.click();
			page.waitForTimeout(2000);
			WaitUtils.waitForLoadingIndicatorToDisappear(page);
		} catch (Exception e) {
			log.info("Lookup record popup not displayed or SOP Master directly opened: {}", e.getMessage());
		}
		log.info("Record " + sopNODoc + " loaded in SOP Master.");
	}

	/**
	 * Ensures the SOP Master header is in Form View (not Grid View).
	 */
	public void ensureHeaderInFormView(String sopDocNo) {
		log.info("Ensuring SOP Master header is in Form View for docNo: [{}]...", sopDocNo);

		// Check if Form View is already active
		Locator headerDocNoInp = page.locator("//input[@instancename='PHX_SOP0DocumentNo']");
		boolean isFormVisible = false;
		try {
			isFormVisible = headerDocNoInp.count() > 0 && headerDocNoInp.first().isVisible();
		} catch (Exception ignored) {
		}

		if (!isFormVisible) {
			log.info("Header Form View is not currently visible. Checking for Grid View...");

			// Cancel any unsaved/edit row with Ignore Changes (Alt+Z)
			try {
				Locator undoBtn = page.locator(
						"//div[contains(@class,'adwindow-toolbar')]//a[contains(@title,'Ignore Changes') or contains(@title,'Alt+Z')] | (//a[contains(@title,'Ignore Changes')])[1]");
				if (undoBtn.count() > 0 && undoBtn.first().isVisible()) {
					log.info("Cancelling any unsaved grid row with Ignore Changes (Alt+Z)...");
					undoBtn.first().click();
					page.waitForTimeout(1000);
					WaitUtils.waitForLoadingIndicatorToDisappear(page);
				}
			} catch (Exception ignored) {
			}

			// Try double-clicking grid row matching docNo or description
			if (sopDocNo != null && !sopDocNo.isEmpty()) {
				Locator targetRow = page.locator("//div[not(contains(@class,'adwindow-detailpane'))]//tr[contains(.,'"
						+ sopDocNo + "') or contains(.,'ANNUAL BUILDING MAINTENANCE')]");
				if (targetRow.count() > 0 && targetRow.first().isVisible()) {
					log.info("Double-clicking grid row for [{}] to enter Form View...", sopDocNo);
					targetRow.first().dblclick();
					page.waitForTimeout(1500);
					WaitUtils.waitForLoadingIndicatorToDisappear(page);
				}
			}

			// If still not in form view, click Grid Toggle (Alt+T) on main toolbar
			try {
				isFormVisible = headerDocNoInp.count() > 0 && headerDocNoInp.first().isVisible();
				if (!isFormVisible) {
					log.info("Clicking Grid Toggle (Alt+T) on main toolbar...");
					Locator gridToggle = page.locator(
							"//div[contains(@class,'adwindow-toolbar')]//a[contains(@title,'Grid Toggle') or contains(@title,'Alt+T')] | //a[@title='Grid Toggle    Alt+T']");
					if (gridToggle.count() > 0 && gridToggle.first().isVisible()) {
						gridToggle.first().click();
					} else {
						page.keyboard().press("Alt+T");
					}
					page.waitForTimeout(1500);
					WaitUtils.waitForLoadingIndicatorToDisappear(page);
				}
			} catch (Exception ignored) {
			}
		}

		docActionButton.first()
				.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(15000));
		log.info("SOP Master Header is confirmed in Form View and ready.");
	}

	public void createNewSubrecord() {
		log.info("Creating new subrecord...");
		// Use detail pane New button or Shift+Alt+N (NEVER Alt+N which is for header!)
		Locator detailNewBtn = page.locator(
				"//div[contains(@class,'adwindow-detailpane-toolbar')]//a[contains(@title,'New') or contains(@title,'Alt+N') or contains(@title,'Save and Create New')] | "
						+
						"//div[contains(@class,'z-tabpanel') and not(contains(@style,'display: none'))]//a[contains(@title,'Alt+N')] | "
						+
						"//div[contains(@class,'adwindow-detailpane')]//a[contains(@title,'Alt+N')]");
		Locator newBtn = getVisibleInput(detailNewBtn);
		if (newBtn.isVisible()) {
			newBtn.click();
			log.info("Clicked detail pane New button (Alt+N).");
		} else {
			log.info("Pressing Shift+Alt+N for new subrecord...");
			page.keyboard().press("Alt+N");
		}
		page.waitForTimeout(1000);
		WaitUtils.waitForLoadingIndicatorToDisappear(page);
	}

	public void saveSubrecord() {
		log.info("Saving subrecord...");
		// Use detail pane Save button or Shift+Alt+S (NEVER Alt+S which is for header!)
		Locator detailSaveBtn = page.locator(
				"//div[contains(@class,'adwindow-detailpane-toolbar')]//a[contains(@title,'Save Changes') or contains(@title,'Alt+S')] | "
						+
						"//div[contains(@class,'z-tabpanel') and not(contains(@style,'display: none'))]//a[contains(@title,'Alt+S')] | "
						+
						"//div[contains(@class,'adwindow-detailpane')]//a[contains(@title,'Alt+S')]");
		Locator saveBtn = getVisibleInput(detailSaveBtn);
		if (saveBtn.isVisible() && !saveBtn.getAttribute("class").contains("disableFilter")) {
			saveBtn.click();
			log.info("Clicked detail pane Save Changes button (Alt+S).");
		} else {
			log.info("Pressing Alt+S to save subrecord...");
			page.keyboard().press("Alt+S");
		}
		page.waitForTimeout(1000);
		WaitUtils.waitForLoadingIndicatorToDisappear(page);
		log.info("Subrecord saved.");
	}

	public void toggleSubtabView() {
		log.info("Toggling subtab Grid/Form view...");
		Locator detailToggleBtn = page.locator(
				"//div[contains(@class,'adwindow-detailpane-toolbar')]//a[contains(@title,'Grid Toggle') or contains(@title,'Shift+Alt+T')] | "
						+
						"//div[contains(@class,'z-tabpanel') and not(contains(@style,'display: none'))]//a[contains(@title,'Grid Toggle') or contains(@title,'Shift+Alt+T')] | "
						+
						"//div[contains(@class,'adwindow-detailpane')]//a[contains(@title,'Shift+Alt+T')]");
		Locator toggleBtn = getVisibleInput(detailToggleBtn);
		if (toggleBtn.isVisible()) {
			toggleBtn.click();
			log.info("Clicked detail pane Grid Toggle button (Alt+T).");
		} else {
			log.info("Pressing Alt+T for subtab grid toggle...");
			page.keyboard().press("Alt+T");
		}
		page.waitForTimeout(1000);
		WaitUtils.waitForLoadingIndicatorToDisappear(page);
	}

	public void saveRecord() {
		Locator saveBtn = getVisibleInput(saveButton);
		if (saveBtn.isVisible()) {
			saveBtn.click();
		} else {
			saveButton.first()
					.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
			saveButton.first().click();
		}
		page.waitForTimeout(1000);
		WaitUtils.waitForLoadingIndicatorToDisappear(page);
	}

	public void clickPreview() {
		log.info("Clicking Preview toolbar button...");
		try {
			Locator sopTab = page.locator("//li[contains(@class,'z-tab') and contains(.,'SOP Master')]");
			Locator visibleSopTab = getVisibleInput(sopTab);
			if (visibleSopTab.isVisible() && !visibleSopTab.getAttribute("class").contains("z-tab-selected")) {
				visibleSopTab.click();
				page.waitForTimeout(500);
			}
		} catch (Exception ignored) {
		}

		Locator prevBtn = getVisibleInput(previewButton);
		if (prevBtn.isVisible()) {
			prevBtn.click();
		} else {
			previewButton.first()
					.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
			previewButton.first().click();
		}
		page.waitForTimeout(4000);
		WaitUtils.waitForLoadingIndicatorToDisappear(page);
	}

	public String getDocumentNo() {
		documentNoInput.first()
				.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		return documentNoInput.first().inputValue().trim();
	}

	public String getSOPStatus() {
		return sopStatusInput.first().inputValue().trim();
	}

	private void softAssertTrue(boolean condition, String message) {
		CustomSoftAssert sa = BaseTest.getSoftAssert();
		if (sa != null) {
			sa.assertTrue(condition, message);
		} else {
			Assert.assertTrue(condition, message);
		}
	}

	private void softAssertEquals(Object actual, Object expected, String message) {
		CustomSoftAssert sa = BaseTest.getSoftAssert();
		if (sa != null) {
			sa.assertEquals(actual, expected, message);
		} else {
			Assert.assertEquals(actual, expected, message);
		}
	}

	public void verifySOPStatus(String expectedStatus) {
		Locator statusInp = getVisibleInput(sopStatusInput);
		statusInp.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		long start = System.currentTimeMillis();
		String actual = "";
		while (System.currentTimeMillis() - start < 15000) {
			actual = statusInp.inputValue().trim();
			if (actual.equalsIgnoreCase(expectedStatus)) {
				break;
			}
			page.waitForTimeout(500);
			statusInp = getVisibleInput(sopStatusInput);
		}
		softAssertEquals(actual.toLowerCase(), expectedStatus.toLowerCase(),
				"SOP Status mismatch! Expected: [" + expectedStatus + "], Found: [" + actual + "]");
		if (actual.equalsIgnoreCase(expectedStatus)) {
			log.info("✅ SOP Status verified as: [{}]", actual);
		} else {
			log.error("❌ SOP Status mismatch! Expected: [{}], Found: [{}]", expectedStatus, actual);
		}
	}

	public void verifyRevisionNo(String expectedRev) {
		Locator revInp = getVisibleInput(revisionNoInput);
		revInp.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		long start = System.currentTimeMillis();
		String actual = "";
		while (System.currentTimeMillis() - start < 15000) {
			actual = revInp.inputValue().trim();
			if (actual.equalsIgnoreCase(expectedRev)) {
				break;
			}
			page.waitForTimeout(500);
			revInp = getVisibleInput(revisionNoInput);
		}
		Assert.assertEquals(actual, expectedRev,
				"Revision No mismatch! Expected: [" + expectedRev + "], Found: [" + actual + "]");
		log.info("✅ Revision No verified as: [{}]", actual);
	}

	public String getRevisionNo() {
		Locator revInp = getVisibleInput(revisionNoInput);
		try {
			if (revInp.isVisible()) {
				return revInp.inputValue().trim();
			}
		} catch (Exception ignored) {
		}
		return "";
	}

	/**
	 * Verifies that Next Review Date equals Effective Date + Review After (Month).
	 * Dynamically reads Review After (Month) and Next Review Date directly from UI fields.
	 *
	 * @param effectiveDateStr The effective date string (e.g. "dd/MM/yyyy" or
	 *                         "yyyy-MM-dd")
	 */
	public void verifyNextReviewDate(String effectiveDateStr) {
		log.info("Verifying Next Review Date = Effective Date [{}] + Review After (Month)...", effectiveDateStr);
		page.waitForTimeout(1000);
		WaitUtils.waitForLoadingIndicatorToDisappear(page);

		// Read Review After (Month) dynamically from UI
		Locator reviewAfterLoc = page.locator(
				"//div[@instancename='PHX_SOP0PHX_ReviewAfter']//input | " +
				"//span[@instancename='PHX_SOP0PHX_ReviewAfter']//input | " +
				"//input[contains(@instancename,'PHX_ReviewAfter')]");
		Locator visibleReviewAfter = getVisibleInput(reviewAfterLoc);

		String monthsVal = "";
		if (visibleReviewAfter.isVisible()) {
			monthsVal = visibleReviewAfter.inputValue().trim();
		}
		if (monthsVal.isEmpty()) {
			monthsVal = (String) page.evaluate("() => {\n" +
					"    let el = document.querySelector(\"[instancename*='PHX_ReviewAfter'] input, [instancename*='ReviewAfter'] input\");\n" +
					"    if (el && el.value) return el.value.trim();\n" +
					"    let spans = Array.from(document.querySelectorAll('span.z-label, label, div.z-label'));\n" +
					"    let revSpan = spans.find(s => s.innerText && s.innerText.trim().includes('Review After'));\n" +
					"    if (revSpan) {\n" +
					"        let row = revSpan.closest('.z-row') || revSpan.closest('tr') || revSpan.parentElement.parentElement;\n" +
					"        if (row) {\n" +
					"            let numInp = row.querySelector('input.z-decimalbox, input.z-doublebox, input');\n" +
					"            if (numInp && numInp.value) return numInp.value.trim();\n" +
					"        }\n" +
					"    }\n" +
					"    return '';\n" +
					"}");
		}

		Assert.assertTrue(monthsVal != null && !monthsVal.isEmpty(),
				"Review After (Month) field is empty on UI, cannot calculate expected Next Review Date!");
		int months = (int) Double.parseDouble(monthsVal);
		log.info("Dynamically read Review After (Month) from UI: [{}] (parsed as {} months)", monthsVal, months);

		// Read actual Next Review Date from UI
		Locator nextReviewLoc = page.locator(
				"//span[@instancename='PHX_SOP0PHX_ReviewDate']//input | " +
				"//input[contains(@instancename,'PHX_ReviewDate')]");
		Locator visibleNextReview = getVisibleInput(nextReviewLoc);

		String actualNextReview = "";
		if (visibleNextReview.isVisible()) {
			actualNextReview = visibleNextReview.inputValue().trim();
		}
		if (actualNextReview.isEmpty()) {
			long start = System.currentTimeMillis();
			while (System.currentTimeMillis() - start < 5000) {
				actualNextReview = (String) page.evaluate("() => {\n" +
						"    let el = document.querySelector(\"[instancename*='PHX_ReviewDate'] input:not(.z-timebox-input), [instancename*='DateTo'] input:not(.z-timebox-input)\");\n" +
						"    if (el && el.value) return el.value.trim();\n" +
						"    let spans = Array.from(document.querySelectorAll('span.z-label, label, div.z-label'));\n" +
						"    let nextReviewSpan = spans.find(s => s.innerText && s.innerText.trim().includes('Next Review Date'));\n" +
						"    if (nextReviewSpan) {\n" +
						"        let row = nextReviewSpan.closest('.z-row') || nextReviewSpan.closest('tr') || nextReviewSpan.parentElement.parentElement;\n" +
						"        if (row) {\n" +
						"            let dateInp = row.querySelector('.z-datebox input:not(.z-timebox-input), input.z-datebox-input');\n" +
						"            if (dateInp && dateInp.value) return dateInp.value.trim();\n" +
						"        }\n" +
						"    }\n" +
						"    return '';\n" +
						"}");
				if (actualNextReview != null && !actualNextReview.isEmpty()) {
					break;
				}
				page.waitForTimeout(500);
			}
		}

		log.info("Actual Next Review Date from UI: [{}]", actualNextReview);
		Assert.assertTrue(actualNextReview != null && !actualNextReview.isEmpty(),
				"Next Review Date is not populated on UI after Effective action!");

		try {
			java.time.format.DateTimeFormatter dtfIn = effectiveDateStr.contains("/")
					? java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")
					: java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd");
			java.time.LocalDate effDate = java.time.LocalDate.parse(effectiveDateStr, dtfIn);
			java.time.LocalDate expectedDate = effDate.plusMonths(months);

			java.time.format.DateTimeFormatter dtfOut = actualNextReview.contains("/")
					? java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")
					: java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd");
			String expectedNextReview = expectedDate.format(dtfOut);

			log.info("Calculated expected Next Review Date: [{}]. Actual: [{}]", expectedNextReview,
					actualNextReview);
			Assert.assertEquals(actualNextReview, expectedNextReview,
					"Next Review Date mismatch! Effective Date: [" + effectiveDateStr + "] + [" + months
							+ "] Month(s) should be [" + expectedNextReview + "], but found [" + actualNextReview
							+ "]");
			log.info("✅ Next Review Date verified successfully as: [{}]", actualNextReview);
		} catch (Exception e) {
			log.error("Failed to verify Next Review Date: {}", e.getMessage());
			throw new RuntimeException(e);
		}
	}

	public void dismissLingeringReviseModal() {
		try {
			Locator modal = page.locator(
					"//div[contains(@class,'z-window') and (contains(.,'Process completed successfully') or contains(.,'Revise SOP'))]");
			for (Locator m : modal.all()) {
				try {
					if (m.isVisible()) {
						log.info("Dismissing lingering Revise SOP modal...");
						Locator btn = m.locator("button, div.z-window-close, [class*='close']").first();
						if (btn.count() > 0 && btn.isVisible()) {
							btn.click();
							page.waitForTimeout(1000);
						}
					}
				} catch (Exception ignored) {
				}
			}
		} catch (Exception ignored) {
		}
	}

	/**
	 * Switches to the Old SOP record tab (Revision 0 window).
	 */
	public void switchToOldSOPRecordTab() {
		log.info("Switching to Old SOP record tab (Revision 0)...");
		dismissLingeringReviseModal();

		Locator sopTabs = page.locator("//li[contains(@class,'z-tab') and contains(.,'SOP Master')]");
		int count = sopTabs.count();
		log.info("Found [{}] SOP Master workspace tabs.", count);
		if (count > 0) {
			Locator tabText = sopTabs.first().locator(".z-tab-text, a, span").first();
			if (tabText.count() > 0 && tabText.isVisible()) {
				tabText.click();
			} else {
				sopTabs.first().click();
			}
			page.waitForTimeout(2000);
			WaitUtils.waitForLoadingIndicatorToDisappear(page);
			log.info("Switched to Old SOP record tab.");
		}
	}

	/**
	 * Switches to the New SOP record tab (Revision 1 window).
	 */
	public void switchToNewSOPRecordTab() {
		log.info("Switching to New SOP record tab (Revision 1)...");
		dismissLingeringReviseModal();

		Locator sopTabs = page.locator("//li[contains(@class,'z-tab') and contains(.,'SOP Master')]");
		int count = sopTabs.count();
		log.info("Found [{}] SOP Master workspace tabs.", count);
		if (count > 1) {
			Locator newTab = sopTabs.nth(count - 1);
			Locator tabText = newTab.locator(".z-tab-text, a, span").first();
			if (tabText.count() > 0 && tabText.isVisible()) {
				tabText.click();
			} else {
				newTab.click();
			}
			page.waitForTimeout(2000);
			WaitUtils.waitForLoadingIndicatorToDisappear(page);
			log.info("Switched to New SOP record tab.");
		} else if (count == 1) {
			Locator tabText = sopTabs.first().locator(".z-tab-text, a, span").first();
			if (tabText.count() > 0 && tabText.isVisible()) {
				tabText.click();
			} else {
				sopTabs.first().click();
			}
			page.waitForTimeout(2000);
			WaitUtils.waitForLoadingIndicatorToDisappear(page);
		}
	}

	/**
	 * Checks whether the Approved checkbox is checked.
	 */
	public boolean isApprovedChecked() {
		Locator chk = getVisibleInput(page.locator("//span[@instancename='PHX_SOP0IsApproved']//input"));
		Locator span = getVisibleInput(page.locator("//span[@instancename='PHX_SOP0IsApproved']"));
		long start = System.currentTimeMillis();
		while (System.currentTimeMillis() - start < 10000) {
			try {
				if (chk.isVisible() && chk.isChecked()) {
					return true;
				}
				String cls = span.getAttribute("class");
				if (cls != null && (cls.contains("z-checkbox-on")
						|| (!cls.contains("z-checkbox-off") && !cls.contains("off")))) {
					return true;
				}
			} catch (Exception ignored) {
			}
			page.waitForTimeout(500);
			chk = getVisibleInput(page.locator("//span[@instancename='PHX_SOP0IsApproved']//input"));
			span = getVisibleInput(page.locator("//span[@instancename='PHX_SOP0IsApproved']"));
		}
		try {
			return chk.isChecked();
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Verifies that the Approved checkbox matches the expected state.
	 *
	 * @param expected true if Approved checkbox should be checked, false otherwise.
	 */
	public void verifyApprovedCheckbox(boolean expected) {
		log.info("Verifying Approved checkbox is [{}]...", expected);
		boolean actual = isApprovedChecked();
		if (actual != expected) {
			page.waitForTimeout(1000);
			actual = isApprovedChecked();
		}
		softAssertEquals(actual, expected,
				"Approved checkbox state mismatch! Expected: [" + expected + "], Found: [" + actual + "]");
		if (actual == expected) {
			log.info("✅ Approved checkbox verified successfully as: [{}]", actual);
		} else {
			log.error("❌ Approved checkbox mismatch! Expected: [{}], Found: [{}]", expected, actual);
		}
	}

	public void verifyApprovedCheckbox() {
		verifyApprovedCheckbox(true);
	}

	/**
	 * Checks whether the Processed checkbox is checked.
	 */
	public boolean isProcessedChecked() {
		Locator chk = getVisibleInput(page.locator("//span[@instancename='PHX_SOP0Processed']//input"));
		Locator span = getVisibleInput(page.locator("//span[@instancename='PHX_SOP0Processed']"));
		long start = System.currentTimeMillis();
		while (System.currentTimeMillis() - start < 10000) {
			try {
				if (chk.isVisible() && chk.isChecked()) {
					return true;
				}
				String cls = span.getAttribute("class");
				if (cls != null && (cls.contains("z-checkbox-on")
						|| (!cls.contains("z-checkbox-off") && !cls.contains("off")))) {
					return true;
				}
			} catch (Exception ignored) {
			}
			page.waitForTimeout(500);
			chk = getVisibleInput(page.locator("//span[@instancename='PHX_SOP0Processed']//input"));
			span = getVisibleInput(page.locator("//span[@instancename='PHX_SOP0Processed']"));
		}
		try {
			return chk.isChecked();
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Verifies that the Processed checkbox matches the expected state.
	 *
	 * @param expected true if Processed checkbox should be checked, false
	 *                 otherwise.
	 */
	public void verifyProcessedCheckbox(boolean expected) {
		log.info("Verifying Processed checkbox is [{}]...", expected);
		boolean actual = isProcessedChecked();
		if (actual != expected) {
			page.waitForTimeout(1000);
			actual = isProcessedChecked();
		}
		softAssertEquals(actual, expected,
				"Processed checkbox state mismatch! Expected: [" + expected + "], Found: [" + actual + "]");
		if (actual == expected) {
			log.info("✅ Processed checkbox verified successfully as: [{}]", actual);
		} else {
			log.error("❌ Processed checkbox mismatch! Expected: [{}], Found: [{}]", expected, actual);
		}
	}

	public void verifyProcessedCheckbox() {
		verifyProcessedCheckbox(true);
	}

	private boolean isCheckboxChecked(Locator chkLocator, Locator spanLocator) {
		try {
			Locator chk = getVisibleInput(chkLocator);
			if (chk.isVisible()) {
				try {
					if (chk.isChecked()) {
						return true;
					}
				} catch (Exception ignored) {
				}
				String checkedAttr = chk.getAttribute("checked");
				if (checkedAttr != null && !"false".equalsIgnoreCase(checkedAttr)) {
					return true;
				}
			}
		} catch (Exception ignored) {
		}

		try {
			Locator span = getVisibleInput(spanLocator);
			if (span.isVisible()) {
				String cls = span.getAttribute("class");
				if (cls != null && (cls.contains("z-checkbox-on") || cls.contains("z-checkbox-checked"))) {
					return true;
				}
				String aria = span.getAttribute("aria-checked");
				if ("true".equalsIgnoreCase(aria)) {
					return true;
				}
			}
		} catch (Exception ignored) {
		}

		return false;
	}

	private boolean pollCheckboxState(Locator chkLocator, Locator spanLocator, boolean expected, long timeoutMs) {
		long start = System.currentTimeMillis();
		while (System.currentTimeMillis() - start < timeoutMs) {
			boolean state = isCheckboxChecked(chkLocator, spanLocator);
			if (state == expected) {
				return state;
			}
			page.waitForTimeout(400);
		}
		return isCheckboxChecked(chkLocator, spanLocator);
	}

	/**
	 * Checks whether the Active checkbox on SOP Master header is checked.
	 */
	public boolean isActiveChecked() {
		Locator chkLoc = page.locator(
				"//span[@instancename='PHX_SOP0IsActive']//input | //span[contains(@class,'z-checkbox')][.//label[normalize-space(text())='Active']]//input");
		Locator spanLoc = page.locator(
				"//span[@instancename='PHX_SOP0IsActive'] | //span[contains(@class,'z-checkbox')][.//label[normalize-space(text())='Active']]");
		return isCheckboxChecked(chkLoc, spanLoc);
	}

	/**
	 * Verifies that the Active checkbox matches the expected state.
	 *
	 * @param expected true if Active checkbox should be checked, false otherwise.
	 */
	public void verifyActiveCheckbox(boolean expected) {
		log.info("Verifying Active checkbox is [{}]...", expected);
		Locator chkLoc = page.locator(
				"//span[@instancename='PHX_SOP0IsActive']//input | //span[contains(@class,'z-checkbox')][.//label[normalize-space(text())='Active']]//input");
		Locator spanLoc = page.locator(
				"//span[@instancename='PHX_SOP0IsActive'] | //span[contains(@class,'z-checkbox')][.//label[normalize-space(text())='Active']]");
		boolean actual = pollCheckboxState(chkLoc, spanLoc, expected, 5000);
		softAssertEquals(actual, expected,
				"Active checkbox state mismatch! Expected: [" + expected + "], Found: [" + actual + "]");
		if (actual == expected) {
			log.info("✅ Active checkbox verified successfully as: [{}]", actual);
		} else {
			log.error("❌ Active checkbox mismatch! Expected: [{}], Found: [{}]", expected, actual);
		}
	}

	public void verifyActiveCheckbox() {
		verifyActiveCheckbox(true);
	}

	/**
	 * Checks whether the Revised checkbox in Annexure subtab is checked.
	 */
	public boolean isAnnexureRevisedChecked() {
		Locator chkLoc = page.locator(
				"//span[@instancename='PHX_SOPAnnexure0isRevised']//input | "
						+ "//span[@instancename='PHX_SOPAnnexure0IsRevised']//input | "
						+ "//span[translate(@instancename, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz')='phx_sopannexure0isrevised']//input | "
						+ "//span[contains(@class,'z-checkbox')][.//label[normalize-space(text())='Revised']]//input");
		Locator spanLoc = page.locator(
				"//span[@instancename='PHX_SOPAnnexure0isRevised'] | "
						+ "//span[@instancename='PHX_SOPAnnexure0IsRevised'] | "
						+ "//span[translate(@instancename, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz')='phx_sopannexure0isrevised'] | "
						+ "//span[contains(@class,'z-checkbox')][.//label[normalize-space(text())='Revised']]");
		return isCheckboxChecked(chkLoc, spanLoc);
	}

	/**
	 * Verifies that the Revised checkbox in Annexure subtab matches expected state.
	 *
	 * @param expected true if Revised checkbox should be checked, false otherwise.
	 */
	public void verifyAnnexureRevisedCheckbox(boolean expected) {
		log.info("Verifying Annexure Revised checkbox is [{}]...", expected);
		Locator chkLoc = page.locator(
				"//span[@instancename='PHX_SOPAnnexure0isRevised']//input | "
						+ "//span[@instancename='PHX_SOPAnnexure0IsRevised']//input | "
						+ "//span[translate(@instancename, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz')='phx_sopannexure0isrevised']//input | "
						+ "//span[contains(@class,'z-checkbox')][.//label[normalize-space(text())='Revised']]//input");
		Locator spanLoc = page.locator(
				"//span[@instancename='PHX_SOPAnnexure0isRevised'] | "
						+ "//span[@instancename='PHX_SOPAnnexure0IsRevised'] | "
						+ "//span[translate(@instancename, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz')='phx_sopannexure0isrevised'] | "
						+ "//span[contains(@class,'z-checkbox')][.//label[normalize-space(text())='Revised']]");
		boolean actual = pollCheckboxState(chkLoc, spanLoc, expected, 10000);
		softAssertEquals(actual, expected,
				"Annexure Revised checkbox state mismatch! Expected: [" + expected + "], Found: [" + actual + "]");
		if (actual == expected) {
			log.info("✅ Annexure Revised checkbox verified successfully as: [{}]", actual);
		} else {
			log.error("❌ Annexure Revised checkbox mismatch! Expected: [{}], Found: [{}]", expected, actual);
		}
	}

	public void verifyAnnexureRevisedCheckbox() {
		verifyAnnexureRevisedCheckbox(true);
	}

	/**
	 * Checks whether the Revised checkbox in SOP Formats subtab is checked.
	 */
	public boolean isFormatRevisedChecked() {
		Locator chkLoc = page.locator(
				"//span[@instancename='PHX_SOPFormats0isRevised']//input | "
						+ "//span[@instancename='PHX_SOPFormats0IsRevised']//input | "
						+ "//span[translate(@instancename, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz')='phx_sopformats0isrevised']//input | "
						+ "//span[contains(@class,'z-checkbox')][.//label[normalize-space(text())='Revised']]//input");
		Locator spanLoc = page.locator(
				"//span[@instancename='PHX_SOPFormats0isRevised'] | "
						+ "//span[@instancename='PHX_SOPFormats0IsRevised'] | "
						+ "//span[translate(@instancename, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz')='phx_sopformats0isrevised'] | "
						+ "//span[contains(@class,'z-checkbox')][.//label[normalize-space(text())='Revised']]");
		return isCheckboxChecked(chkLoc, spanLoc);
	}

	/**
	 * Verifies that the Revised checkbox in SOP Formats subtab matches expected state.
	 *
	 * @param expected true if Revised checkbox should be checked, false otherwise.
	 */
	public void verifyFormatRevisedCheckbox(boolean expected) {
		log.info("Verifying SOP Format Revised checkbox is [{}]...", expected);
		Locator chkLoc = page.locator(
				"//span[@instancename='PHX_SOPFormats0isRevised']//input | "
						+ "//span[@instancename='PHX_SOPFormats0IsRevised']//input | "
						+ "//span[translate(@instancename, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz')='phx_sopformats0isrevised']//input | "
						+ "//span[contains(@class,'z-checkbox')][.//label[normalize-space(text())='Revised']]//input");
		Locator spanLoc = page.locator(
				"//span[@instancename='PHX_SOPFormats0isRevised'] | "
						+ "//span[@instancename='PHX_SOPFormats0IsRevised'] | "
						+ "//span[translate(@instancename, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz')='phx_sopformats0isrevised'] | "
						+ "//span[contains(@class,'z-checkbox')][.//label[normalize-space(text())='Revised']]");
		boolean actual = pollCheckboxState(chkLoc, spanLoc, expected, 5000);
		softAssertEquals(actual, expected,
				"SOP Format Revised checkbox state mismatch! Expected: [" + expected + "], Found: [" + actual + "]");
		if (actual == expected) {
			log.info("✅ SOP Format Revised checkbox verified successfully as: [{}]", actual);
		} else {
			log.error("❌ SOP Format Revised checkbox mismatch! Expected: [{}], Found: [{}]", expected, actual);
		}
	}

	public void verifyFormatRevisedCheckbox() {
		verifyFormatRevisedCheckbox(true);
	}

	/**
	 * From SOP header, zooms into the Previous Version field (right-click -> Zoom
	 * or zoom button)
	 * to navigate to the Previous SOP record.
	 */
	public void zoomToPreviousVersion() {
		log.info("Zooming into Previous Version field from SOP Master header...");
		page.waitForTimeout(1000);
		WaitUtils.waitForLoadingIndicatorToDisappear(page);
		navigateBackOnSOPHeader();

		Locator prevField = page.locator(
				"//span[@instancename='PHX_SOP0PHX_PrevSOP_ID']//input | " +
						"//span[@instancename='PHX_SOP0PHX_PrevSOP_ID'] | " +
						"//div[@instancename='PHX_SOP0PHX_PrevSOP_ID']//input | " +
						"//div[contains(@class,'z-row') and .//span[contains(text(),'Previous')]]//input");

		Locator visiblePrev = getVisibleInput(prevField);
		try {
			visiblePrev.scrollIntoViewIfNeeded();
		} catch (Exception ignored) {
		}

		Locator zoomBtn = page.locator(
				"//span[@instancename='PHX_SOP0PHX_PrevSOP_ID']//button[@title='Zoom' or contains(@class,'zoom')] | " +
						"//span[@instancename='PHX_SOP0PHX_PrevSOP_ID']//a[contains(@class,'zoom')]");

		if (zoomBtn.count() > 0 && zoomBtn.first().isVisible()) {
			log.info("Clicking zoom button next to Previous Version field...");
			zoomBtn.first().click();
		} else {
			log.info("Right-clicking Previous Version field to trigger Zoom context menu...");
			try {
				visiblePrev.click(new Locator.ClickOptions().setButton(MouseButton.RIGHT).setForce(true));
			} catch (Exception e) {
				prevField.first().click(new Locator.ClickOptions().setButton(MouseButton.RIGHT).setForce(true));
			}
			page.waitForTimeout(1000);

			Locator zoomMenuItem = page.locator(".z-menupopup:visible").getByText("Zoom");
			if (zoomMenuItem.count() == 0 || !zoomMenuItem.first().isVisible()) {
				zoomMenuItem = page.locator(
						"//div[contains(@id,'PHX_PrevSOP_ID-popup') or contains(@class,'z-menupopup')]//span[normalize-space(text())='Zoom'] | "
								+
								"//div[contains(@class,'z-menupopup') and not(contains(@style,'display: none')) and not(contains(@style,'visibility: hidden'))]//span[normalize-space(text())='Zoom'] | "
								+
								"//div[contains(@class,'z-menupopup')]//span[normalize-space(text())='Zoom']");
			}
			if (zoomMenuItem.count() > 0 && zoomMenuItem.first().isVisible()) {
				zoomMenuItem.first().click();
				log.info("Clicked 'Zoom' menu item.");
			} else {
				log.warn(
						"Zoom menu item not found after right-click, attempting fallback click on link or old SOP tab...");
				try {
					Locator prevLink = page.locator("//span[@instancename='PHX_SOP0PHX_PrevSOP_ID']//a");
					if (prevLink.count() > 0 && prevLink.first().isVisible()) {
						prevLink.first().click();
					} else {
						switchToOldSOPRecordTab();
					}
				} catch (Exception ex) {
					switchToOldSOPRecordTab();
				}
			}
		}

		page.waitForTimeout(3000);
		WaitUtils.waitForLoadingIndicatorToDisappear(page);
		log.info("Zoomed to Previous Version record successfully.");
	}

	public void clickReviseSOPAnnexure() {
		// TODO Auto-generated method stub
		Locator ReviseAnnexureBtton = page.locator("//button[@instancename='PHX_SOPAnnexure0Revise']");
		// log.info("Clicking Revise SOP button...");
		ReviseAnnexureBtton
				.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		ReviseAnnexureBtton.click();

		Locator modalDialog = page.locator(
				"//div[contains(@class,'z-window-modal') or contains(@class,'z-window-highlighted') or contains(@class,'z-window')][.//div[contains(@class,'z-window-header') and contains(.,'Revise SOP')] or contains(.,'Do you want to start the Process?') or contains(.,'Revise SOP')]");
		try {
			modalDialog.first()
					.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(15000));
			log.info("Revise SOP modal dialog appeared.");

			Locator confirmOkBtn = page.locator(
					"//div[contains(@class,'z-window') and (contains(.,'Revise SOP') or contains(.,'Do you want to start the Process?'))]//button[contains(.,'OK') or contains(@class,'z-messagebox-button')] | "
							+
							"//div[contains(@class,'z-window-modal') or contains(@class,'z-window-highlighted')]//button[contains(.,'OK') or contains(@class,'z-messagebox-button')]");
			confirmOkBtn.first()
					.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
			confirmOkBtn.first().click();
			log.info("Confirmed Revise SOP process dialog (clicked OK).");
			page.waitForTimeout(3000);
			WaitUtils.waitForLoadingIndicatorToDisappear(page);
		} catch (Exception e) {
			log.warn("Could not confirm Revise SOP process dialog: {}", e.getMessage());
		}
		log.info("Revise SOP Annexure process initiated.");

	}

	public void clickReviseSOPFormat() {
		// TODO Auto-generated method stub

		// TODO Auto-generated method stub
		Locator ReviseFormatBtton = page.locator("//button[@instancename='PHX_SOPFormats0Revise']");
		log.info("Clicking Revise SOP button...");
		ReviseFormatBtton
				.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(15000));
		ReviseFormatBtton.click();

		Locator modalDialog = page.locator(
				"//div[contains(@class,'z-window-modal') or contains(@class,'z-window-highlighted') or contains(@class,'z-window')][.//div[contains(@class,'z-window-header') and contains(.,'Revise SOP')] or contains(.,'Do you want to start the Process?') or contains(.,'Revise SOP')]");
		try {
			modalDialog.first()
					.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(15000));
			log.info("Revise SOP modal dialog appeared.");

			Locator confirmOkBtn = page.locator(
					"//div[contains(@class,'z-window') and (contains(.,'Revise SOP') or contains(.,'Do you want to start the Process?'))]//button[contains(.,'OK') or contains(@class,'z-messagebox-button')] | "
							+
							"//div[contains(@class,'z-window-modal') or contains(@class,'z-window-highlighted')]//button[contains(.,'OK') or contains(@class,'z-messagebox-button')]");
			confirmOkBtn.first()
					.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
			confirmOkBtn.first().click();
			log.info("Confirmed Revise SOP process dialog (clicked OK).");
			page.waitForTimeout(3000);
			WaitUtils.waitForLoadingIndicatorToDisappear(page);
		} catch (Exception e) {
			log.warn("Could not confirm Revise SOP process dialog: {}", e.getMessage());
		}
		log.info("Revise SOP format process initiated.");

	}

	public void closeFirstSOPTab() {
		// TODO Auto-generated method stub
		page.waitForTimeout(500);
		page.locator("(//li[contains(@title,'SOP Master:')]//i[@aria-label='Close'])[1]").click();
		page.waitForTimeout(1000);
	}

	private String pollInputValue(Locator locator, String expectedValue, long timeoutMs) {
		long start = System.currentTimeMillis();
		String val = "";
		while (System.currentTimeMillis() - start < timeoutMs) {
			try {
				Locator vis = getVisibleInput(locator);
				if (vis.isVisible()) {
					val = vis.inputValue().trim();
					if (expectedValue != null && !expectedValue.isEmpty()) {
						if (val.equalsIgnoreCase(expectedValue) || val.contains(expectedValue)) {
							return val;
						}
					} else if (!val.isEmpty()) {
						return val;
					}
				}
			} catch (Exception ignored) {
			}
			page.waitForTimeout(300);
		}
		try {
			Locator vis = getVisibleInput(locator);
			if (vis.isVisible()) {
				val = vis.inputValue().trim();
			}
		} catch (Exception ignored) {
		}
		return val;
	}

	/**
	 * Verifies Reference Fields on Annexure subtab:
	 * - Previous Version equals expectedAnnexureName
	 * - Base Version equals expectedAnnexureName
	 * - Original SOP Version equals expectedSopDocNo
	 *
	 * @param expectedAnnexureName Expected annexure name (e.g. "ANNEXURE-I")
	 * @param expectedSopDocNo     Expected SOP document number (e.g. "SOP/PROD/0001")
	 */
	public void verifyRefrenceFieldsOnAnnexure(String expectedAnnexureName, String expectedSopDocNo) {
		log.info(
				"Verifying Reference Fields on Annexure... Expected: Previous Version = [{}], Base Version = [{}], Original SOP Version = [{}]",
				expectedAnnexureName, expectedAnnexureName, expectedSopDocNo);
		page.waitForTimeout(1000);
		WaitUtils.waitForLoadingIndicatorToDisappear(page);

		// 1. Verify Previous Version
		String actualPrevVer = pollInputValue(annexurePreviousVersion, expectedAnnexureName, 5000);
		if (actualPrevVer.isEmpty()) {
			actualPrevVer = (String) page.evaluate("() => {\n" +
					"    let el = document.querySelector(\"[instancename*='PHX_PrevVersion_ID'] input\");\n" +
					"    return el ? el.value.trim() : '';\n" +
					"}");
		}
		log.info("Annexure Previous Version from UI: [{}]", actualPrevVer);
		softAssertEquals(actualPrevVer, expectedAnnexureName,
				"Annexure Previous Version mismatch! Expected: [" + expectedAnnexureName + "], Found: [" + actualPrevVer + "]");
		if (actualPrevVer.equalsIgnoreCase(expectedAnnexureName)) {
			log.info("✅ Annexure Previous Version verified successfully as: [{}]", actualPrevVer);
		} else {
			log.error("❌ Annexure Previous Version mismatch! Expected: [{}], Found: [{}]", expectedAnnexureName, actualPrevVer);
		}

		// 2. Verify Base Version
		String actualBaseVer = pollInputValue(annexureBaseVersion, expectedAnnexureName, 5000);
		if (actualBaseVer.isEmpty()) {
			actualBaseVer = (String) page.evaluate("() => {\n" +
					"    let el = document.querySelector(\"[instancename*='PHX_Base_ID'] input\");\n" +
					"    return el ? el.value.trim() : '';\n" +
					"}");
		}
		log.info("Annexure Base Version from UI: [{}]", actualBaseVer);
		softAssertEquals(actualBaseVer, expectedAnnexureName,
				"Annexure Base Version mismatch! Expected: [" + expectedAnnexureName + "], Found: [" + actualBaseVer + "]");
		if (actualBaseVer.equalsIgnoreCase(expectedAnnexureName)) {
			log.info("✅ Annexure Base Version verified successfully as: [{}]", actualBaseVer);
		} else {
			log.error("❌ Annexure Base Version mismatch! Expected: [{}], Found: [{}]", expectedAnnexureName, actualBaseVer);
		}

		// 3. Verify Original SOP Version
		String actualOrgSopVer = pollInputValue(annexureOriginalSOPVersion, expectedSopDocNo, 5000);
		if (actualOrgSopVer.isEmpty()) {
			actualOrgSopVer = (String) page.evaluate("() => {\n" +
					"    let el = document.querySelector(\"[instancename*='PHX_OrgSOPVer_ID'] input\");\n" +
					"    return el ? el.value.trim() : '';\n" +
					"}");
		}
		log.info("Annexure Original SOP Version from UI: [{}]", actualOrgSopVer);
		softAssertEquals(actualOrgSopVer, expectedSopDocNo,
				"Annexure Original SOP Version mismatch! Expected: [" + expectedSopDocNo + "], Found: [" + actualOrgSopVer + "]");
		if (actualOrgSopVer.equalsIgnoreCase(expectedSopDocNo)) {
			log.info("✅ Annexure Original SOP Version verified successfully as: [{}]", actualOrgSopVer);
		} else {
			log.error("❌ Annexure Original SOP Version mismatch! Expected: [{}], Found: [{}]", expectedSopDocNo, actualOrgSopVer);
		}
	}

	public void verifyRefrenceFieldsOnAnnexure() {
		String docNo = getActiveSOPDocNo();
		if (docNo == null || docNo.isEmpty()) {
			try {
				docNo = getDocumentNo();
			} catch (Exception ignored) {
			}
		}
		String annexName = getActiveAnnexureName();
		if (annexName == null || annexName.isEmpty()) {
			try {
				Locator nameInp = getVisibleInput(annexureNameInput);
				if (nameInp.isVisible()) {
					annexName = nameInp.inputValue().trim();
				}
			} catch (Exception ignored) {
			}
		}
		verifyRefrenceFieldsOnAnnexure(annexName, docNo);
	}

	/**
	 * Verifies Reference Fields on SOP Formats subtab:
	 * - Previous Version equals expectedFormatName
	 * - Base Version equals expectedFormatName
	 * - Original SOP Version equals expectedSopDocNo
	 *
	 * @param expectedFormatName Expected SOP format name (e.g. "PAINT RECORD")
	 * @param expectedSopDocNo   Expected SOP document number (e.g. "SOP/PROD/0001")
	 */
	public void verifyRefrenceFieldsOnFormat(String expectedFormatName, String expectedSopDocNo) {
		log.info(
				"Verifying Reference Fields on SOP Formats... Expected: Previous Version = [{}], Base Version = [{}], Original SOP Version = [{}]",
				expectedFormatName, expectedFormatName, expectedSopDocNo);
		page.waitForTimeout(1000);
		WaitUtils.waitForLoadingIndicatorToDisappear(page);

		// 1. Verify Previous Version
		String actualPrevVer = pollInputValue(formatPreviousVersion, expectedFormatName, 5000);
		if (actualPrevVer.isEmpty()) {
			actualPrevVer = (String) page.evaluate("() => {\n" +
					"    let el = document.querySelector(\"[instancename*='PHX_PrevVersion_ID'] input\");\n" +
					"    return el ? el.value.trim() : '';\n" +
					"}");
		}
		log.info("Format Previous Version from UI: [{}]", actualPrevVer);
		softAssertEquals(actualPrevVer, expectedFormatName,
				"Format Previous Version mismatch! Expected: [" + expectedFormatName + "], Found: [" + actualPrevVer + "]");
		if (actualPrevVer.equalsIgnoreCase(expectedFormatName)) {
			log.info("✅ Format Previous Version verified successfully as: [{}]", actualPrevVer);
		} else {
			log.error("❌ Format Previous Version mismatch! Expected: [{}], Found: [{}]", expectedFormatName, actualPrevVer);
		}

		// 2. Verify Base Version
		String actualBaseVer = pollInputValue(formatBaseVersion, expectedFormatName, 5000);
		if (actualBaseVer.isEmpty()) {
			actualBaseVer = (String) page.evaluate("() => {\n" +
					"    let el = document.querySelector(\"[instancename*='PHX_Base_ID'] input\");\n" +
					"    return el ? el.value.trim() : '';\n" +
					"}");
		}
		log.info("Format Base Version from UI: [{}]", actualBaseVer);
		softAssertEquals(actualBaseVer, expectedFormatName,
				"Format Base Version mismatch! Expected: [" + expectedFormatName + "], Found: [" + actualBaseVer + "]");
		if (actualBaseVer.equalsIgnoreCase(expectedFormatName)) {
			log.info("✅ Format Base Version verified successfully as: [{}]", actualBaseVer);
		} else {
			log.error("❌ Format Base Version mismatch! Expected: [{}], Found: [{}]", expectedFormatName, actualBaseVer);
		}

		// 3. Verify Original SOP Version
		String actualOrgSopVer = pollInputValue(formatOriginalSOPVersion, expectedSopDocNo, 5000);
		if (actualOrgSopVer.isEmpty()) {
			actualOrgSopVer = (String) page.evaluate("() => {\n" +
					"    let el = document.querySelector(\"[instancename*='PHX_OrgSOPVer_ID'] input\");\n" +
					"    return el ? el.value.trim() : '';\n" +
					"}");
		}
		log.info("Format Original SOP Version from UI: [{}]", actualOrgSopVer);
		softAssertEquals(actualOrgSopVer, expectedSopDocNo,
				"Format Original SOP Version mismatch! Expected: [" + expectedSopDocNo + "], Found: [" + actualOrgSopVer + "]");
		if (actualOrgSopVer.equalsIgnoreCase(expectedSopDocNo)) {
			log.info("✅ Format Original SOP Version verified successfully as: [{}]", actualOrgSopVer);
		} else {
			log.error("❌ Format Original SOP Version mismatch! Expected: [{}], Found: [{}]", expectedSopDocNo, actualOrgSopVer);
		}
	}

	public void verifyRefrenceFieldsOnFormat() {
		String docNo = getActiveSOPDocNo();
		if (docNo == null || docNo.isEmpty()) {
			try {
				docNo = getDocumentNo();
			} catch (Exception ignored) {
			}
		}
		String fmtName = getActiveFormatName();
		if (fmtName == null || fmtName.isEmpty()) {
			try {
				Locator nameInp = getVisibleInput(formatNameInput);
				if (nameInp.isVisible()) {
					fmtName = nameInp.inputValue().trim();
				}
			} catch (Exception ignored) {
			}
		}
		verifyRefrenceFieldsOnFormat(fmtName, docNo);
	}
}
