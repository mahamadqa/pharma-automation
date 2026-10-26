package pages;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;

import com.microsoft.playwright.FrameLocator;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitForSelectorState;

import utils.WaitUtils;

/**
 * Page object representing the SOP Master window, subtabs (Abbreviations, Annexure,
 * SOP Formats, Distribution List, Revision History), Content sections with HTML Editor,
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
	private final Locator reviseButton;
	private final Locator docActionButton;
	private final Locator effectiveDateInput;

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

	// SOP Formats Tab Fields
	private final Locator formatDocTypeInput;
	private final Locator formatNameInput;
	private final Locator formatPrintPaperInput;
	private final Locator formatSupersedesNoInput;
	private final Locator formatContentField;
	private final Locator formatReviseFormatBtn;

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

	// Active SOP Document Number
	private static final ThreadLocal<String> activeSOPDocNo = ThreadLocal.withInitial(() -> "");

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
				"(//div[@instancename='PHX_SOP0Revision']//input[contains(@class,'z-decimalbox')])[1] | (//div[@instancename='PHX_SOP0Revision']//input)[1] | //input[@instancename='PHX_SOP0Revision']");
		this.descriptionInput = page.locator("//input[@instancename='PHX_SOP0Description']");
		this.departmentInput = page.locator("//span[@instancename='PHX_SOP0C_Department_ID']//input");
		this.respDeptTextbox = page.locator(
				"//div[@instancename='PHX_SOP0PHX_RespDept_ID']//input[contains(@class,'multi-select-textbox')]");
		this.masterCopyOwnerInput = page.locator("//span[@instancename='PHX_SOP0PHX_MasterCopyOwner_ID']//input");
		this.supersedesNoInput = page.locator("//input[@instancename='PHX_SOP0PHX_SupersedesNo']");
		this.prevSOPInput = page.locator("//span[@instancename='PHX_SOP0PHX_PrevSOP_ID']//input");
		this.sopStatusInput = page.locator("//span[@instancename='PHX_SOP0PHX_SOPStatus']//input");
		this.docStatusInput = page.locator("//span[@instancename='PHX_SOP0DocStatus']//input");
		this.processedCheckbox = page.locator("//span[@instancename='PHX_SOP0Processed']//input");
		this.approvedCheckbox = page.locator("//span[@instancename='PHX_SOP0IsApproved']//input");
		this.reviseButton = page.locator("//button[@instancename='PHX_SOP0Revise']");
		this.docActionButton = page.locator("//button[@instancename='PHX_SOP0DocAction']");
		this.effectiveDateInput = page.locator("//span[@instancename='PHX_SOP0DateFrom']//input[not(contains(@class,'z-timebox-input'))]");

		// Content Sections
		this.objectiveField = page.locator("//div[@instancename='PHX_SOP0PHX_Objective']");
		this.scopeField = page.locator("//div[@instancename='PHX_SOP0PHX_Scope']");
		this.responsibilityField = page.locator("//div[@instancename='PHX_SOP0PHX_Responsibility']");
		this.procedureField = page.locator("//div[@instancename='PHX_SOP0PHX_Procedure']");

		// Subtabs
		this.abbreviationsTab = page.locator("//ul[@role='tablist']//span[text()='Abbreviations'] | //div[contains(@class,'adwindow-detailpane')]//span[text()='Abbreviations']");
		this.annexureTab = page.locator("//ul[@role='tablist']//span[text()='Annexure'] | //div[contains(@class,'adwindow-detailpane')]//span[text()='Annexure']");
		this.formatTab = page.locator("//ul[@role='tablist']//span[text()='SOP Formats'] | //div[contains(@class,'adwindow-detailpane')]//span[text()='SOP Formats']");
		this.distributionTab = page.locator("//ul[@role='tablist']//span[text()='Distribution List'] | //div[contains(@class,'adwindow-detailpane')]//span[text()='Distribution List']");
		this.revisionHistoryTab = page.locator("//ul[@role='tablist']//span[text()='Revision History'] | //div[contains(@class,'adwindow-detailpane')]//span[text()='Revision History']");

		// Abbreviations Fields
		this.abbrevShortFormInput = page.locator("//input[@instancename='PHX_SOPAbbreviation0PHX_ShortForm']");
		this.abbrevFullFormInput = page.locator("//input[@instancename='PHX_SOPAbbreviation0PHX_FullForm']");

		// Annexure Fields
		this.annexureNameInput = page.locator("//input[@instancename='PHX_SOPAnnexure0Annexure']");
		this.annexureNoInput = page.locator("//input[@instancename='PHX_SOPAnnexure0AnnexureNo']");
		this.annexureTitleInput = page.locator("//input[@instancename='PHX_SOPAnnexure0Title']");
		this.annexurePrintPaperInput = page.locator("//span[@instancename='PHX_SOPAnnexure0AD_PrintPaper_ID']//input");
		this.annexureSupersedesNoInput = page.locator("//input[@instancename='PHX_SOPAnnexure0PHX_SupersedesNo']");
		this.annexureContentField = page.locator("//div[@instancename='PHX_SOPAnnexure0ContentText'] | //div[contains(@instancename,'Content')]");
		this.annexureReviseFormatBtn = page.locator("//button[contains(.,'Revise Format')] | //button[@instancename='PHX_SOPAnnexure0Revise']");

		// SOP Formats Fields
		this.formatDocTypeInput = page.locator("//span[@instancename='PHX_SOPFormats0C_DocType_ID']//input");
		this.formatNameInput = page.locator("//input[@instancename='PHX_SOPFormats0Name']");
		this.formatPrintPaperInput = page.locator("//span[@instancename='PHX_SOPFormats0AD_PrintPaper_ID']//input");
		this.formatSupersedesNoInput = page.locator("//input[@instancename='PHX_SOPFormats0PHX_SupersedesNo']");
		this.formatContentField = page.locator("//div[@instancename='PHX_SOPFormats0Format'] | //div[contains(@instancename,'Format')]");
		this.formatReviseFormatBtn = page.locator("//button[contains(.,'Revise Format')] | //button[@instancename='PHX_SOPFormats0Revise']");

		// Distribution List Fields
		this.distDepartmentInput = page.locator("//span[@instancename='PHX_SOPDistribution0C_Department_ID']//input | //div[contains(@class,'z-row')]//span[contains(@instancename,'Department')]//input");
		this.distCopiesInput = page.locator(
				"//div[@instancename='PHX_SOPDistribution0Copies']//input | " +
				"//input[@instancename='PHX_SOPDistribution0Copies'] | " +
				"//div[contains(@class,'z-row') or contains(@class,'z-grid-body')]//input[contains(@class,'z-decimalbox') or contains(@class,'z-intbox') or contains(@class,'editor-input')]");

		// Revision History Fields
		this.historyRevisionInput = page.locator(
				"//div[@instancename='PHX_SOPHistory0Revision']//input | " +
				"//input[@instancename='PHX_SOPHistory0Revision'] | " +
				"//div[contains(@class,'z-row') or contains(@class,'z-grid-body')]//input[contains(@class,'z-decimalbox') or contains(@class,'z-intbox') or contains(@class,'editor-input')]");
		this.historyReasonInput = page.locator(
				"//input[@instancename='PHX_SOPHistory0Reason'] | //textarea[contains(@instancename,'Reason')]");
		this.historyCCNoInput = page.locator(
				"//input[contains(@instancename,'ChangeControl')] | //input[contains(@instancename,'Controll')]");

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
				"//div[contains(@class,'adwindow-toolbar')]//a[@title='Save Changes    Alt+S'] | (//a[@title='Save Changes    Alt+S'])[1]");
		this.previewButton = page.locator("//a[@title='Preview']");
	}

	public static String getActiveSOPDocNo() {
		return activeSOPDocNo.get();
	}

	public static void setActiveSOPDocNo(String docNo) {
		activeSOPDocNo.set(docNo);
	}

	/**
	 * Navigates to SOP Master window by searching in Menu Search Box and clicking '+' icon.
	 */
	public void navigateToSOPMasterWindow() {
		log.info("Searching 'SOP Master' in menu searchbox and clicking '+' icon...");
		menuSearchInput.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		menuSearchInput.fill("SOP Master");
		page.waitForTimeout(3000);

		try {
			sopMasterNewIcon.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(5000));
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
	 * Fills mandatory and standard details on SOP Master Header and saves the record.
	 */
	public void fillHeaderAndSave(String docType, String description, String department, String respDepts, String masterCopyOwner) {
		log.info("Filling SOP Master Header. DocType: [{}], Desc: [{}], Dept: [{}]", docType, description, department);

		docTypeInput.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		docTypeInput.fill(docType);
		docTypeInput.press("Tab");
		page.waitForTimeout(400);

		descriptionInput.fill(description);
		descriptionInput.press("Tab");
		page.waitForTimeout(400);

		departmentInput.fill(department);
		departmentInput.press("Tab");
		page.waitForTimeout(400);

		// Select Responsible Departments via multi-select popup
		if (respDepts != null && !respDepts.isEmpty()) {
			selectResponsibleDepartments(respDepts);
		}

		if (masterCopyOwner != null && !masterCopyOwner.isEmpty()) {
			masterCopyOwnerInput.fill(masterCopyOwner);
			masterCopyOwnerInput.press("Tab");
			page.waitForTimeout(400);
		}

		saveRecord();

		String generatedDocNo = getDocumentNo();
		setActiveSOPDocNo(generatedDocNo);
		log.info("SOP Master Header saved. Generated Document No: [{}]", generatedDocNo);
	}

	/**
	 * Handles multi-select checkbox selection for Responsible Departments.
	 */
	private void selectResponsibleDepartments(String commaSeparatedDepts) {
		try {
			// Open multi-select popup safely via ZK widget or click
			page.evaluate("() => { const p = document.querySelector(\"div[instancename='PHX_SOP0PHX_RespDept_ID'] .multi-select-popup\"); if (p && window.zk) zk.Widget.$(p).open(); }");
			page.waitForTimeout(500);

			String[] depts = commaSeparatedDepts.split(",");
			for (String dept : depts) {
				String d = dept.trim();
				Locator checkboxLabel = page.locator(
						"//div[contains(@class,'multi-select-popup')]//label[contains(@class,'z-checkbox-content') and text()='" + d + "']");
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
	 * Fills a rich-text content section (Objective, Scope, Responsibility, Procedure)
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
		FrameLocator editorFrame = page.frameLocator("//div[contains(@class,'text-editor-dialog')]//iframe[contains(@class,'cke_wysiwyg_frame')]").last();
		try {
			editorFrame.locator("body").waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
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

		getVisibleInput(targetInput).waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
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
		//shortInp.press("Tab");

		Locator fullInp = getVisibleInput(abbrevFullFormInput);
		fullInp.fill(fullForm);
		//fullInp.press("Tab");

		saveSubrecord();
		page.waitForTimeout(2000);
		log.info("Abbreviation [{}] saved successfully.", shortForm);
	}

	/**
	 * Navigates to Annexure tab, fills details, content and saves.
	 */
	public void addAnnexure(String annexure, String title, String printPaper, String supersedesNo, String content) {
		log.info("Adding Annexure: [{}], Title: [{}]", annexure, title);
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

			FrameLocator editorFrame = page.frameLocator("//div[contains(@class,'text-editor-dialog')]//iframe[contains(@class,'cke_wysiwyg_frame')]").last();
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

			FrameLocator editorFrame = page.frameLocator("//div[contains(@class,'text-editor-dialog')]//iframe[contains(@class,'cke_wysiwyg_frame')]").last();
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
						"let inp = document.querySelector(\"div[instancename='PHX_SOPDistribution0Copies'] input\") || " +
						"document.querySelector(\"input[instancename='PHX_SOPDistribution0Copies']\") || " +
						"document.querySelector(\".z-decimalbox.editor-input\"); " +
						"if (inp && inp.value !== val) { inp.focus(); inp.value = val; inp.dispatchEvent(new Event('input', {bubbles:true})); inp.dispatchEvent(new Event('change', {bubbles:true})); } " +
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
		Locator approvalGroup = page.locator("//div[contains(@class,'z-groupbox') and .//span[normalize-space(text())='Approval']]");
		if (approvalGroup.count() > 0) {
			String cls = approvalGroup.first().getAttribute("class");
			if (cls != null && (cls.contains("collapsed") || cls.contains("z-groupbox-collapsed"))) {
				log.info("Approval section is collapsed, expanding it...");
				page.locator("//span[normalize-space(text())='Approval'] | //div[contains(@class,'z-groupbox')]//div[contains(@class,'z-caption')]").first().click();
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
				signBtn = preparedBySignBtn;
				break;
			case "checkedby":
			case "checked":
				signBtn = checkedBySignBtn;
				break;
			case "reviewedby":
			case "reviewed":
				signBtn = reviewedBySignBtn;
				break;
			case "approver":
			case "approved":
				signBtn = approverSignBtn;
				break;
			default:
				throw new IllegalArgumentException("Unknown signature type: " + signType);
		}

		signBtn.first().waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		signBtn.first().click();
		page.waitForTimeout(1000);

		Locator signModal = page.locator("//div[contains(@class,'z-window') and (contains(.,'Digital-Signature') or contains(.,'Digital Signature')) and not(contains(@style,'display: none')) and not(contains(@style,'display:none'))]");
		signModal.first().waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));

		Locator deptInput = signModal.locator("//span[text()='Department']//parent::div//span//input");
		deptInput.first().waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
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

		// Cross error popup if displayed & ReQuery
//		crossErrorPopupIfDisplayed();
//		dismissReQueryModalIfPresent();
	}

	/**
	 * Checks and dismisses any modal messagebox such as "Current record was changed by another user, please ReQuery".
	 */
	public void dismissReQueryModalIfPresent() {
		try {
			Locator modal = page.locator("//div[contains(@class,'z-messagebox') or (contains(@class,'z-window-modal') and (contains(.,'changed by another user') or contains(.,'please ReQuery') or contains(.,'ReQuery')))]");
			if (modal.count() > 0 && modal.first().isVisible()) {
				String txt = modal.first().innerText();
				log.info("Detected modal: [{}]", txt.trim());
				Locator crossBtn = modal.first().locator("div.z-window-close, div.z-window-icon-close, [aria-label='Close'], div.z-window-header-close, button.z-window-close").first();
				if (crossBtn.count() > 0 && crossBtn.first().isVisible()) {
					log.info("Crossing popup via 'X' button...");
					crossBtn.first().click(new Locator.ClickOptions().setForce(true).setTimeout(3000));
				} else {
					Locator okBtn = modal.first().locator("button.btn-primary, button.z-messagebox-btn, button[title='OK']").first();
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
	 * Performs Complete Document Action and assigns to the specified user in Approver Window.
	 */
	public void completeDocActionAndAssignTo(String assignToUser) {
		log.info("Executing Document Action 'Complete' and assigning to: [{}]", assignToUser);
		crossErrorPopupIfDisplayed();
		dismissReQueryModalIfPresent();

		docActionButton.first().waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(15000));
		docActionButton.first().click();
		WaitUtils.waitForLoadingIndicatorToDisappear(page);

		Locator docActionDialog = page.locator("//div[@instancename='documentAction']");
		try {
			docActionDialog.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(6000));
			Locator actionSelect = docActionDialog.locator("select");
			if (actionSelect.count() > 0 && actionSelect.first().isVisible()) {
				try {
					actionSelect.first().selectOption(new com.microsoft.playwright.options.SelectOption().setLabel("Complete"));
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
		Locator approverModal = page.locator("div.z-window:not(.z-window-embedded)").filter(new Locator.FilterOptions().setHasText("Assign To")).first();
		approverModal.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(25000));

		Locator assignInput = approverModal.locator("tr:has-text('Assign To') input, span[instancename*='AD_User_ID'] input, input:not([type='hidden'])").first();
		assignInput.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		assignInput.click();
		assignInput.fill(assignToUser);
		assignInput.press("Tab");
		page.waitForTimeout(1000);

		// Handle Lookup window if it opened
		Locator lookupWindow = page.locator("div.z-window:not(.z-window-embedded)").filter(new Locator.FilterOptions().setHasText("User"));
		if (lookupWindow.count() > 0 && lookupWindow.first().isVisible()) {
			Locator reqBtn = lookupWindow.locator("button[title*='ReQuery'], button:has-text('ReQuery')");
			if (reqBtn.count() > 0 && reqBtn.first().isVisible()) {
				reqBtn.first().click();
				page.waitForTimeout(1000);
			}
			Locator userRow = lookupWindow.locator("tr:has-text('" + assignToUser + "'), span:has-text('" + assignToUser + "')");
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

		Locator okBtn = approverModal.locator("button.btn-primary, button:has(.z-icon-check), button[title='OK']").first();
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
		Locator alertMsg = page.locator("//div[contains(@class,'z-messagebox') or contains(@class,'popup-dialog') or contains(@class,'z-window-modal')]");
		if (alertMsg.count() > 0 && alertMsg.first().isVisible()) {
			String txt = alertMsg.first().innerText();
			log.info("Detected dialog before Document Action: [{}]", txt.trim());
			Locator okBtn = alertMsg.locator("//button[contains(.,'OK') or contains(@class,'z-messagebox-btn')] | //button[@title='OK']");
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

		docActionButton.first().waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		docActionButton.first().click();
		page.waitForTimeout(1000);
		WaitUtils.waitForLoadingIndicatorToDisappear(page);

		Locator answerSelect = page.locator("//div[contains(@class,'doc-action-dialog')]//select | //div[@instancename='documentAction']//select");
		if (answerSelect.count() > 0 && answerSelect.first().isVisible()) {
			try {
				answerSelect.first().selectOption(answer);
			} catch (Exception e) {
				try {
					answerSelect.first().selectOption(new com.microsoft.playwright.options.SelectOption().setLabel(answer));
				} catch (Exception ignored) {
				}
			}
		}

		Locator docActionOkBtn = page.locator("//div[contains(@class,'doc-action-dialog')]//button[@title='OK'] | //div[@instancename='documentAction']//button[@title='OK']");
		docActionOkBtn.first().waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		docActionOkBtn.first().click();
		page.waitForTimeout(2000);

		if (assignToUser != null && !assignToUser.isEmpty()) {
			WaitUtils.waitForLoadingIndicatorToDisappear(page);
			page.waitForTimeout(1000);
			Locator approverModal = page.locator("div.z-window:not(.z-window-embedded)").filter(new Locator.FilterOptions().setHasText("Assign To")).first();
			try {
				approverModal.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(20000));
			} catch (Exception e) {
				log.warn("Approver modal not found or already closed: {}", e.getMessage());
			}

			if (approverModal.count() > 0 && approverModal.first().isVisible()) {
				Locator assignInput = approverModal.locator("tr:has-text('Assign To') input, span[instancename*='AD_User_ID'] input, input:not([type='hidden'])").first();
				assignInput.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
				assignInput.click();
				assignInput.fill(assignToUser);
				assignInput.press("Tab");
				page.waitForTimeout(1000);

				// Handle Lookup window if opened
				Locator lookupWindow = page.locator("div.z-window:not(.z-window-embedded)").filter(new Locator.FilterOptions().setHasText("User"));
				if (lookupWindow.count() > 0 && lookupWindow.first().isVisible()) {
					Locator nameInp = lookupWindow.locator("input[instancename*='Name'], input.z-textbox").first();
					nameInp.fill(assignToUser);
					nameInp.press("Enter");
					page.waitForTimeout(500);
					Locator lookupOk = lookupWindow.locator("button.btn-primary, button[title='OK']").first();
					lookupOk.click();
					page.waitForTimeout(500);
				}

				Locator okBtn = approverModal.locator("button.btn-primary, button:has(.z-icon-check), button[title='OK']").first();
				okBtn.click();
				page.waitForTimeout(2000);
			}
		}
		WaitUtils.waitForLoadingIndicatorToDisappear(page);
		log.info("Document action with Answer [{}] completed.", answer);
	}

	/**
	 * Performs Document Action with Effective Date input (for QAManager approval step).
	 */
	public void approveWithEffectiveDate(String date) {
		log.info("Approving Document Action with Effective Date: [{}]", date);
		dismissReQueryModalIfPresent();
		docActionButton.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		docActionButton.click();
		page.waitForTimeout(1000);
		dismissReQueryModalIfPresent();

		Locator dateInput = page.locator("//div[contains(@class,'doc-action-dialog')]//input[contains(@class,'z-datebox-input')] | //div[@instancename='documentAction']//input[contains(@class,'z-datebox-input')]");
		if (dateInput.count() > 0 && dateInput.first().isVisible()) {
			dateInput.first().fill(date);
			dateInput.first().press("Tab");
		}

		Locator okBtn = page.locator("//div[contains(@class,'doc-action-dialog')]//button[@title='OK'] | //div[@instancename='documentAction']//button[@title='OK']");
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
		docActionButton.first().waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		docActionButton.first().click();
		page.waitForTimeout(1000);
		WaitUtils.waitForLoadingIndicatorToDisappear(page);
		dismissReQueryModalIfPresent();

		Locator okBtn = page.locator("//div[contains(@class,'doc-action-dialog')]//button[@title='OK'] | //div[@instancename='documentAction']//button[@title='OK']");
		if (okBtn.count() > 0 && okBtn.first().isVisible()) {
			okBtn.first().click();
			page.waitForTimeout(1000);
		}
	}

	/**
	 * Verifies an error popup dialog message or inline status error and dismisses it if dialog.
	 */
	public void verifyErrorMessageAndClose(String expectedText) {
		log.info("Verifying error message containing: [{}]...", expectedText);
		page.waitForTimeout(1000);

		String expLower = expectedText.toLowerCase();
		long startTime = System.currentTimeMillis();
		boolean found = false;
		String actualMessage = "";

		Locator msgBox = page.locator("//div[contains(@class,'z-messagebox') or contains(@class,'popup-dialog') or contains(@class,'z-window-modal') or contains(@class,'z-window-highlighted')]");
		Locator inlineStatus = page.locator(
				"//div[contains(@class,'status-line')] | " +
				"//div[contains(@class,'adwindow-north')]//*[contains(@class,'z-label')] | " +
				"//div[contains(@class,'adwindow')]//*[contains(@style,'color: red') or contains(@style,'color:red') or contains(@class,'error')] | " +
				"//*[contains(text(),'pending') or contains(text(),'Assigned To') or contains(text(),'not uploaded') or contains(text(),'Error')]");

		while (System.currentTimeMillis() - startTime < 10000) {
			// 1. Check modal popup dialog
			try {
				if (msgBox.count() > 0 && msgBox.first().isVisible()) {
					actualMessage = msgBox.first().innerText().trim();
					found = true;
					// Cross the error popup via 'X' button
					Locator crossBtn = msgBox.locator("div.z-window-close, div.z-window-icon-close, [aria-label='Close'], div.z-window-header-close, button.z-window-close").first();
					if (crossBtn.count() > 0 && crossBtn.first().isVisible()) {
						log.info("Crossing error popup via 'X' button...");
						crossBtn.first().click();
					} else {
						Locator okBtn = msgBox.locator("button.btn-primary, button.z-messagebox-btn, button[title='OK']").first();
						if (okBtn.count() > 0 && okBtn.first().isVisible()) {
							okBtn.first().click();
						} else {
							page.keyboard().press("Escape");
						}
					}
					page.waitForTimeout(500);
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
							if (!txt.isEmpty() && (txt.toLowerCase().contains("pending") || txt.toLowerCase().contains("assigned to") || txt.toLowerCase().contains("error") || txt.toLowerCase().contains("not uploaded"))) {
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

		Assert.assertTrue(found, "No error message or modal popup was displayed within 10s for expected: [" + expectedText + "]");

		boolean textMatches = actualMessage.toLowerCase().contains(expLower)
				|| (expLower.contains("prepared by") && actualMessage.toLowerCase().contains("prepared by") && actualMessage.toLowerCase().contains("pending"))
				|| (expLower.contains("assigned to") && actualMessage.toLowerCase().contains("assigned to"))
				|| (expLower.contains("document not uploaded") && actualMessage.toLowerCase().contains("document not uploaded"));

		Assert.assertTrue(textMatches,
				"Error message text mismatch. Expected to contain: [" + expectedText + "], but got: [" + actualMessage + "]");
		log.info("✅ Error message verified successfully: [{}]", actualMessage);
	}

	/**
	 * Crosses (closes) any active error popup by clicking the 'X' (cross) icon.
	 */
	public void crossErrorPopup() {
		log.info("Crossing error popup by clicking 'X' icon...");
		try {
			Locator modal = page.locator("//div[contains(@class,'z-messagebox') or (contains(@class,'z-window-modal') and not(contains(@class,'doc-action-dialog')) and not(contains(@instancename,'documentAction')) and not(contains(.,'Approver')))]");
			if (modal.count() > 0 && modal.first().isVisible()) {
				Locator crossBtn = modal.first().locator("div.z-window-close, div.z-window-icon-close, [aria-label='Close'], div.z-window-header-close, button.z-window-close").first();
				if (crossBtn.count() > 0 && crossBtn.first().isVisible()) {
					crossBtn.first().click(new Locator.ClickOptions().setForce(true).setTimeout(3000));
					log.info("Clicked 'X' on error popup.");
				} else {
					Locator btn = modal.first().locator("button.btn-primary, button.z-messagebox-btn, button[title='OK']").first();
					if (btn.isVisible()) {
						btn.click(new Locator.ClickOptions().setForce(true).setTimeout(3000));
					} else {
						page.keyboard().press("Escape");
					}
				}
				page.waitForTimeout(1000);
				WaitUtils.waitForLoadingIndicatorToDisappear(page);
			} else {
				log.info("No modal error popup displayed to cross.");
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
		page.waitForTimeout(1000);

		Locator confirmOkBtn = page.locator("//div[contains(@class,'z-messagebox')]//button[contains(.,'OK')]");
		if (confirmOkBtn.count() > 0 && confirmOkBtn.first().isVisible()) {
			confirmOkBtn.first().click();
			page.waitForTimeout(3000);
			WaitUtils.waitForLoadingIndicatorToDisappear(page);
		}
		log.info("Revise SOP process initiated.");
	}

	/**
	 * Clicks on the Revised SOP reference link displayed in the message dialog.
	 */
	public void openRevisedSOPLink() {
		log.info("Clicking on Revised SOP link...");
		Locator link = page.locator("//div[contains(@class,'z-messagebox')]//a | //a[contains(text(),'SOP') or contains(@class,'z-a')]");
		if (link.count() > 0 && link.first().isVisible()) {
			link.first().click();
			page.waitForTimeout(3000);
			WaitUtils.waitForLoadingIndicatorToDisappear(page);
			log.info("Opened Revised SOP link.");
		}
	}

	/**
	 * Clicks 'Revise Format' button on Annexure tab.
	 */
	public void reviseAnnexureFormat() {
		log.info("Clicking Revise Format button on Annexure tab...");
		if (annexureReviseFormatBtn.count() > 0 && annexureReviseFormatBtn.first().isVisible()) {
			annexureReviseFormatBtn.first().click();
			page.waitForTimeout(1000);
			Locator confirmOk = page.locator("//div[contains(@class,'z-messagebox')]//button[contains(.,'OK')]");
			if (confirmOk.count() > 0 && confirmOk.first().isVisible()) {
				confirmOk.first().click();
				page.waitForTimeout(2000);
			}
		}
	}

	/**
	 * Clicks 'Revise Format' button on SOP Formats tab.
	 */
	public void reviseSOPFormat() {
		log.info("Clicking Revise Format button on SOP Formats tab...");
		if (formatReviseFormatBtn.count() > 0 && formatReviseFormatBtn.first().isVisible()) {
			formatReviseFormatBtn.first().click();
			page.waitForTimeout(1000);
			Locator confirmOk = page.locator("//div[contains(@class,'z-messagebox')]//button[contains(.,'OK')]");
			if (confirmOk.count() > 0 && confirmOk.first().isVisible()) {
				confirmOk.first().click();
				page.waitForTimeout(2000);
			}
		}
	}

	public void switchToSubTab(String tabName) {
		log.info("Switching to subtab via double click: [{}]", tabName);

		// 1. Check if already on this subtab in breadcrumb
		try {
			Locator breadcrumb = page.locator("//div[@instancename='breadcrumb']");
			if (breadcrumb.count() > 0 && breadcrumb.first().isVisible()) {
				String bText = breadcrumb.first().innerText();
				if (bText != null && bText.contains(tabName)) {
					log.info("Already on subtab [{}].", tabName);
					return;
				}
			}
		} catch (Exception ignored) {
		}

		// 2. If currently zoomed into a different subtab, return to SOP Master header first
		try {
			Locator headerBreadcrumb = page.locator("//div[@instancename='breadcrumb']//a[normalize-space(text())='SOP Master']");
			if (headerBreadcrumb.count() > 0 && headerBreadcrumb.first().isVisible()) {
				log.info("Currently on a different subtab view, returning to SOP Master header...");
				headerBreadcrumb.first().click();
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

		subtab.first().waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		log.info("Double-clicking subtab [{}]...", tabName);
		subtab.first().dblclick();
		page.waitForTimeout(1500);
		WaitUtils.waitForLoadingIndicatorToDisappear(page);

		// Verify if zoomed in; if not yet zoomed, attempt dblclick on parent li
		try {
			Locator breadcrumb = page.locator("//div[@instancename='breadcrumb']");
			if (breadcrumb.count() > 0 && !breadcrumb.first().innerText().contains(tabName)) {
				Locator tabLi = subtab.first().locator("xpath=./ancestor-or-self::li[contains(@class,'z-tab')]");
				if (tabLi.count() > 0 && tabLi.first().isVisible()) {
					log.info("Retrying double click on subtab li...");
					tabLi.first().dblclick();
					page.waitForTimeout(1500);
					WaitUtils.waitForLoadingIndicatorToDisappear(page);
				}
			}
		} catch (Exception ignored) {
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
		Locator reportTab = page.locator("//li[contains(@class,'z-tab') and (contains(.,'Report') or contains(.,'Report: SOP'))]");
		if (reportTab.count() > 0 && reportTab.first().isVisible()) {
			log.info("Report tab detected, attempting to close it...");
			Locator closeBtn = page.locator(
					"//li[contains(@class,'z-tab') and (contains(.,'Report') or contains(.,'Report: SOP'))]//a[contains(@class,'z-tab-remove') or contains(@class,'z-tab-close')] | " +
					"//li[contains(@class,'z-tab') and (contains(.,'Report') or contains(.,'Report: SOP'))]//i[contains(@class,'z-icon-times') or contains(@class,'z-tab-icon') or @aria-label='Close'] | " +
					"//li[contains(@class,'z-tab-selected') and contains(.,'Report')]//a[contains(@class,'z-tab-remove') or contains(@class,'z-tab-close')] | " +
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
						"let tab = Array.from(document.querySelectorAll('li.z-tab')).find(el => el.textContent.includes('Report')); " +
						"if (tab) { " +
						"  let btn = tab.querySelector('.z-tab-remove, .z-tab-close, [aria-label=\"Close\"], [class*=\"remove\"], [class*=\"close\"], [class*=\"icon-times\"]'); " +
						"  if (btn) btn.click(); " +
						"  if (window.zk) { " +
						"    let w = zk.Widget.$(tab); " +
						"    if (w) { try { w.close(); } catch(e) {} try { zAu.send(new zk.Event(w, 'onClose')); } catch(e) {} } " +
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
				menuSearchInput.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
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
				Locator undoBtn = page.locator("//div[contains(@class,'adwindow-toolbar')]//a[contains(@title,'Ignore Changes') or contains(@title,'Alt+Z')] | (//a[contains(@title,'Ignore Changes')])[1]");
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
				Locator targetRow = page.locator("//div[not(contains(@class,'adwindow-detailpane'))]//tr[contains(.,'" + sopDocNo + "') or contains(.,'ANNUAL BUILDING MAINTENANCE')]");
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
					Locator gridToggle = page.locator("//div[contains(@class,'adwindow-toolbar')]//a[contains(@title,'Grid Toggle') or contains(@title,'Alt+T')] | //a[@title='Grid Toggle    Alt+T']");
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

		docActionButton.first().waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(15000));
		log.info("SOP Master Header is confirmed in Form View and ready.");
	}

	public void createNewSubrecord() {
		log.info("Creating new subrecord...");
		// Use detail pane New button or Shift+Alt+N (NEVER Alt+N which is for header!)
		Locator detailNewBtn = page.locator(
				"//div[contains(@class,'adwindow-detailpane-toolbar')]//a[contains(@title,'New') or contains(@title,'Alt+N') or contains(@title,'Save and Create New')] | " +
				"//div[contains(@class,'z-tabpanel') and not(contains(@style,'display: none'))]//a[contains(@title,'Alt+N')] | " +
				"//div[contains(@class,'adwindow-detailpane')]//a[contains(@title,'Alt+N')]");
		if (detailNewBtn.count() > 0 && detailNewBtn.first().isVisible()) {
			detailNewBtn.first().click();
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
				"//div[contains(@class,'adwindow-detailpane-toolbar')]//a[contains(@title,'Save Changes') or contains(@title,'Alt+S')] | " +
				"//div[contains(@class,'z-tabpanel') and not(contains(@style,'display: none'))]//a[contains(@title,'Alt+S')] | " +
				"//div[contains(@class,'adwindow-detailpane')]//a[contains(@title,'Alt+S')]");
		if (detailSaveBtn.count() > 0 && detailSaveBtn.first().isVisible() && !detailSaveBtn.first().getAttribute("class").contains("disableFilter")) {
			detailSaveBtn.first().click();
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
				"//div[contains(@class,'adwindow-detailpane-toolbar')]//a[contains(@title,'Grid Toggle') or contains(@title,'Shift+Alt+T')] | " +
				"//div[contains(@class,'z-tabpanel') and not(contains(@style,'display: none'))]//a[contains(@title,'Grid Toggle') or contains(@title,'Shift+Alt+T')] | " +
				"//div[contains(@class,'adwindow-detailpane')]//a[contains(@title,'Shift+Alt+T')]");
		if (detailToggleBtn.count() > 0 && detailToggleBtn.first().isVisible()) {
			detailToggleBtn.first().click();
			log.info("Clicked detail pane Grid Toggle button (Alt+T).");
		} else {
			log.info("Pressing Alt+T for subtab grid toggle...");
			page.keyboard().press("Alt+T");
		}
		page.waitForTimeout(1000);
		WaitUtils.waitForLoadingIndicatorToDisappear(page);
	}

	public void saveRecord() {
		saveButton.first().waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		saveButton.first().click();
		page.waitForTimeout(1000);
		WaitUtils.waitForLoadingIndicatorToDisappear(page);
	}

	public void clickPreview() {
		log.info("Clicking Preview toolbar button...");
		previewButton.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		previewButton.click();
		page.waitForTimeout(4000);
		WaitUtils.waitForLoadingIndicatorToDisappear(page);
	}

	public String getDocumentNo() {
		documentNoInput.first().waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		return documentNoInput.first().inputValue().trim();
	}

	public String getSOPStatus() {
		return sopStatusInput.first().inputValue().trim();
	}

	public void verifySOPStatus(String expectedStatus) {
		sopStatusInput.first().waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		long start = System.currentTimeMillis();
		String actual = "";
		while (System.currentTimeMillis() - start < 10000) {
			actual = sopStatusInput.first().inputValue().trim();
			if (actual.equalsIgnoreCase(expectedStatus)) {
				break;
			}
			page.waitForTimeout(500);
		}
		Assert.assertEquals(actual, expectedStatus, "SOP Status mismatch! Expected: [" + expectedStatus + "], Found: [" + actual + "]");
		log.info("✅ SOP Status verified as: [{}]", actual);
	}

	public void verifyRevisionNo(String expectedRev) {
		revisionNoInput.first().waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		String actual = revisionNoInput.first().inputValue().trim();
		Assert.assertEquals(actual, expectedRev, "Revision No mismatch! Expected: [" + expectedRev + "], Found: [" + actual + "]");
		log.info("✅ Revision No verified as: [{}]", actual);
	}
}
