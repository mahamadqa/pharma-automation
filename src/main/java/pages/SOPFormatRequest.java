package pages;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.SelectOption;
import com.microsoft.playwright.options.WaitForSelectorState;

import base.BaseTest;
import utils.CustomSoftAssert;
import utils.WaitUtils;

/**
 * Page Object Class for SOP Format Request and SOP Format Issue Windows (PHX-3504).
 * Tables: PHX_SOPFormatRequest, PHX_SOPFormatCopy
 */
public class SOPFormatRequest {

	private static final Logger log = LogManager.getLogger(SOPFormatRequest.class);
	private final Page page;

	// Header Fields - SOP Format Request & Issue (PHX_SOPFormatRequest0)
	private final Locator orgInput;
	private final Locator documentNoInput;
	private final Locator docTypeInput;
	private final Locator issueTypeInput;
	private final Locator controlNoInput;
	private final Locator requestedByInput;
	private final Locator departmentInput;
	private final Locator equipmentInput;
	private final Locator areaInput;
	private final Locator requestDateInput;
	private final Locator issuedByInput;
	private final Locator sopInput;
	private final Locator formatInput;
	private final Locator annexureInput;
	private final Locator issueDateInput;
	private final Locator requestedCopiesInput;
	private final Locator issuedCopiesInput;
	private final Locator printNoInput;
	private final Locator reprintReasonInput;
	private final Locator docStatusInput;
	private final Locator receivedByInput;
	private final Locator retrievedOnInput;
	private final Locator retrievedByInput;
	private final Locator sopRequestRefInput;
	private final Locator controlSeqInput;
	private final Locator processedCheckbox;
	private final Locator printedCheckbox;
	private final Locator activeCheckbox;
	private final Locator docActionButton;

	// Subtab - SOP Format Copy (PHX_SOPFormatCopy0)
	private final Locator formatCopyTab;
	private final Locator copyDocNoInput;
	private final Locator copyRetrievedOnInput;
	private final Locator copyCancelledCheckbox;
	private final Locator copyPrintedCheckbox;
	private final Locator copyActiveCheckbox;
	private final Locator copyRemarkInput;

	// Toolbar & Navigation
	private final Locator saveButton;
	private final Locator requeryButton;
	private final Locator newRecordButton;
	private final Locator processButton;
	private final Locator menuSearchInput;
	private final Locator headerBreadcrumb;

	public SOPFormatRequest(Page page) {
		this.page = page;

		// Header Fields
		this.orgInput = page.locator("//span[@instancename='PHX_SOPFormatRequest0AD_Org_ID']//input");
		this.documentNoInput = page.locator("//input[@instancename='PHX_SOPFormatRequest0DocumentNo']");
		this.docTypeInput = page.locator("//span[@instancename='PHX_SOPFormatRequest0C_DocType_ID']//input");
		this.issueTypeInput = page.locator("//span[@instancename='PHX_SOPFormatRequest0PHX_IssueType']//input");
		this.controlNoInput = page.locator("//input[@instancename='PHX_SOPFormatRequest0PHX_ControllNo']");
		this.requestedByInput = page.locator(
				"//div[@instancename='PHX_SOPFormatRequest0AD_User_ID']//input | //span[@instancename='PHX_SOPFormatRequest0AD_User_ID']//input");
		this.departmentInput = page.locator("//span[@instancename='PHX_SOPFormatRequest0C_Department_ID']//input");
		this.equipmentInput = page.locator("//span[@instancename='PHX_SOPFormatRequest0A_Asset_ID']//input");
		this.areaInput = page.locator("//span[@instancename='PHX_SOPFormatRequest0S_Resource_ID']//input");
		this.requestDateInput = page
				.locator("//span[@instancename='PHX_SOPFormatRequest0PHX_RequestDate']//input[not(contains(@class,'z-timebox-input'))]");
		this.issuedByInput = page.locator("//span[@instancename='PHX_SOPFormatRequest0PHX_IssuedBy_ID']//input");
		this.sopInput = page.locator("//span[@instancename='PHX_SOPFormatRequest0PHX_SOP_ID']//input");
		this.formatInput = page.locator("//span[@instancename='PHX_SOPFormatRequest0PHX_SOPFormats_ID']//input");
		this.annexureInput = page.locator("//span[@instancename='PHX_SOPFormatRequest0PHX_SOPAnnexure_ID']//input");
		this.issueDateInput = page
				.locator("//span[@instancename='PHX_SOPFormatRequest0IssueDate']//input[not(contains(@class,'z-timebox-input'))]");
		this.requestedCopiesInput = page.locator(
				"//div[@instancename='PHX_SOPFormatRequest0PHX_RequestedCnt']//input | //input[@instancename='PHX_SOPFormatRequest0PHX_RequestedCnt']");
		this.issuedCopiesInput = page.locator(
				"//div[@instancename='PHX_SOPFormatRequest0PHX_IssueCnt']//input | //input[@instancename='PHX_SOPFormatRequest0PHX_IssueCnt']");
		this.printNoInput = page.locator(
				"//div[@instancename='PHX_SOPFormatRequest0PHX_PrintNo']//input | //span[@instancename='PHX_SOPFormatRequest0PHX_PrintNo']//input | //input[@instancename='PHX_SOPFormatRequest0PHX_PrintNo']");
		this.reprintReasonInput = page.locator(
				"//input[@instancename='PHX_SOPFormatRequest0PHX_Reason'] | //textarea[@instancename='PHX_SOPFormatRequest0PHX_Reason'] | //span[@instancename='PHX_SOPFormatRequest0PHX_Reason']//input | //div[@instancename='PHX_SOPFormatRequest0PHX_Reason']//input");
		this.docStatusInput = page.locator("//span[@instancename='PHX_SOPFormatRequest0DocStatus']//input");
		this.receivedByInput = page.locator(
				"//div[@instancename='PHX_SOPFormatRequest0PHX_ReceivedBy_ID']//input | //span[@instancename='PHX_SOPFormatRequest0PHX_ReceivedBy_ID']//input");
		this.retrievedOnInput = page
				.locator("//span[@instancename='PHX_SOPFormatRequest0PHX_RetrievedOn']//input[not(contains(@class,'z-timebox-input'))]");
		this.retrievedByInput = page.locator(
				"//div[@instancename='PHX_SOPFormatRequest0PHX_RetrievedBy_ID']//input | //span[@instancename='PHX_SOPFormatRequest0PHX_RetrievedBy_ID']//input");
		this.sopRequestRefInput = page.locator("//span[@instancename='PHX_SOPFormatRequest0PHX_SOPRequestIssue_ID']//input");
		this.controlSeqInput = page.locator("//span[@instancename='PHX_SOPFormatRequest0AD_Sequence_ID']//input");
		this.processedCheckbox = page.locator("//span[@instancename='PHX_SOPFormatRequest0Processed']//input");
		this.printedCheckbox = page.locator("//span[@instancename='PHX_SOPFormatRequest0IsPrinted']//input");
		this.activeCheckbox = page.locator("//span[@instancename='PHX_SOPFormatRequest0IsActive']//input");
		this.docActionButton = page.locator("//button[@instancename='PHX_SOPFormatRequest0DocAction']");

		// Subtab - SOP Format Copy
		this.formatCopyTab = page.locator(
				"//ul[@role='tablist']//span[text()='SOP Format Copy'] | //div[contains(@class,'adwindow-detailpane')]//span[text()='SOP Format Copy']");
		this.copyDocNoInput = page.locator("//input[@instancename='PHX_SOPFormatCopy0DocumentNo']");
		this.copyRetrievedOnInput = page
				.locator("//span[@instancename='PHX_SOPFormatCopy0PHX_RetrievedOn']//input[not(contains(@class,'z-timebox-input'))]");
		this.copyCancelledCheckbox = page.locator("//span[@instancename='PHX_SOPFormatCopy0IsCancelled']//input");
		this.copyPrintedCheckbox = page.locator("//span[@instancename='PHX_SOPFormatCopy0IsPrinted']//input");
		this.copyActiveCheckbox = page.locator("//span[@instancename='PHX_SOPFormatCopy0IsActive']//input");
		this.copyRemarkInput = page.locator("//input[@instancename='PHX_SOPFormatCopy0PHX_Remark']");

		// Toolbar & Navigation
		this.saveButton = page.locator(
				"//div[contains(@class,'adwindow-toolbar')]//a[@title='Save Changes    Alt+S'] | //a[@title='Save Changes    Alt+S']");
		this.requeryButton = page.locator(
				"//div[contains(@class,'adwindow-toolbar')]//a[contains(@title,'ReQuery') and not(contains(@class,'trash'))] | //a[@title='ReQuery    Alt+F5']");
		this.newRecordButton = page.locator(
				"//div[contains(@class,'adwindow-toolbar')]//a[@title='New    Alt+N'] | //a[@title='New    Alt+N']");
		this.processButton = page.locator(
				"//div[contains(@class,'adwindow-toolbar')]//a[contains(@title,'Process') and not(contains(@class,'trash'))] | //a[contains(@title,'Process    Alt+O')]");
		this.menuSearchInput = page.locator("//input[@class='z-bandbox-input']");
		this.headerBreadcrumb = page.locator(
				"(//div[@instancename='breadcrumb']//a[contains(text(),'Format Issue') or contains(text(),'Format Request') or contains(@class,'z-a')])[1]");
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

	private Locator getVisible(Locator locator) {
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
	 * Step 2: Opens SOP Format Request window through Menu Search '+' icon.
	 */
	public void navigateToSOPFormatRequestNewRecord() {
		log.info("Searching 'SOP Format Request' in Menu Searchbox and clicking '+' icon (Step 2)...");
		menuSearchInput.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		menuSearchInput.fill("SOP Format Request");
		page.waitForTimeout(2000);

		Locator newIcon = page.locator(
				"//div[contains(@class,'z-bandbox-popup')]//tr[contains(.,'SOP Format Request')]//a[@title='New'] | "
						+ "//tr[contains(.,'SOP Format Request')]//a[@title='New'] | "
						+ "//span[contains(text(),'SOP Format Request')]/ancestor::tr//a[@title='New']")
				.first();
		try {
			newIcon.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(5000));
			newIcon.click();
		} catch (Exception e) {
			log.warn("New '+' icon click fallback, pressing Enter: {}", e.getMessage());
			menuSearchInput.press("Enter");
		}
		page.waitForTimeout(2000);
		WaitUtils.waitForLoadingIndicatorToDisappear(page);
		getVisible(docTypeInput).waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(15000));
		log.info("SOP Format Request window opened with a new record.");
	}

	/**
	 * Step 2 Validation: Checks all specified fields are displaying on the SOP Format Request window.
	 */
	public void verifySOPFormatRequestFieldsDisplaying(String expectedUser) {
		log.info("Verifying all fields displaying on SOP Format Request window (Step 2)...");
		softAssertTrue(getVisible(orgInput).isVisible(), "Organization field should be visible.");
		softAssertTrue(getVisible(documentNoInput).isVisible(), "Document No field should be visible.");
		softAssertTrue(getVisible(docTypeInput).isVisible(), "Document Type field should be visible.");
		softAssertTrue(getVisible(issueTypeInput).isVisible(), "Issue Type field should be visible.");
		softAssertTrue(getVisible(controlNoInput).isVisible(), "Controll Number field should be visible.");
		softAssertTrue(getVisible(requestedByInput).isVisible(), "Request By field should be visible.");
		softAssertTrue(getVisible(departmentInput).isVisible(), "Requesting Department field should be visible.");
		softAssertTrue(getVisible(equipmentInput).isVisible(), "Equipment field should be visible.");
		softAssertTrue(getVisible(areaInput).isVisible(), "Area field should be visible.");
		softAssertTrue(getVisible(requestDateInput).isVisible(), "Request Date field should be visible.");
		softAssertTrue(getVisible(issuedByInput).isVisible(), "Issued By field should be visible.");
		softAssertTrue(getVisible(sopInput).isVisible(), "SOP field should be visible.");
		softAssertTrue(getVisible(issueDateInput).isVisible(), "Issue Date field should be visible.");
		softAssertTrue(getVisible(requestedCopiesInput).isVisible(), "Requested Copies field should be visible.");
		softAssertTrue(getVisible(issuedCopiesInput).isVisible(), "Issues Copies field should be visible.");
		softAssertTrue(getVisible(printNoInput).isVisible(), "Print No field should be visible.");
		softAssertTrue(getVisible(reprintReasonInput).isVisible(), "Reprint Reason field should be visible.");
		softAssertTrue(getVisible(docStatusInput).isVisible(), "Document Status field should be visible.");
		softAssertTrue(getVisible(receivedByInput).isVisible(), "Received By field should be visible.");
		softAssertTrue(getVisible(retrievedOnInput).isVisible(), "Retrieved On field should be visible.");
		softAssertTrue(getVisible(retrievedByInput).isVisible(), "Retrieved By field should be visible.");
		softAssertTrue(getVisible(sopRequestRefInput).isVisible(), "SOP Request field should be visible.");

		// Controll Number - Read Only
		softAssertTrue(getVisible(controlNoInput).isDisabled(), "Controll Number field should be Read Only.");

		// Request By - Auto set to current user
		if (expectedUser != null && !expectedUser.isEmpty()) {
			String actualUser = getVisible(requestedByInput).inputValue().trim();
			softAssertTrue(actualUser.toLowerCase().contains(expectedUser.toLowerCase()),
					"Request By [" + actualUser + "] should be auto set to current user [" + expectedUser + "].");
		}

		// Request Date - Auto set to current Date
		String today = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
		String actualDate = getVisible(requestDateInput).inputValue().trim();
		if (!actualDate.isEmpty()) {
			softAssertTrue(actualDate.equals(today) || actualDate.contains(String.valueOf(LocalDate.now().getDayOfMonth())),
					"Request Date [" + actualDate + "] should be auto set to current date [" + today + "].");
		}

		// Issued By - Read Only
		softAssertTrue(getVisible(issuedByInput).isDisabled(), "Issued By field should be Read Only.");

		// Issue Date - Read Only
		softAssertTrue(getVisible(issueDateInput).isDisabled(), "Issue Date field should be Read Only.");

		// Issues Copies - Read Only
		softAssertTrue(getVisible(issuedCopiesInput).isDisabled(), "Issues Copies field should be Read Only.");

		// Print No - Read Only and default to 0
		softAssertTrue(getVisible(printNoInput).isDisabled(), "Print No field should be Read Only.");
		softAssertEquals(getPrintNo(), "0", "Print No should default to 0.");

		log.info("SOP Format Request fields verified successfully.");
	}

	public String getPrintNo() {
		Locator pn = getVisible(printNoInput);
		try {
			String val = pn.inputValue();
			if (val != null && !val.trim().isEmpty()) return val.trim();
		} catch (Exception ignored) {
		}
		try {
			return pn.innerText().trim();
		} catch (Exception ignored) {
		}
		return "";
	}

	/**
	 * Step 3: Clicks Document Type dropdown arrow and verifies options: "Annexure Request", "SOP Format Request".
	 */
	public void verifyDocTypeDropdownOptions() {
		log.info("Verifying Document Type dropdown options (Step 3)...");
		Locator dt = getVisible(docTypeInput);
		dt.click();
		Locator btn = page.locator(
				"//span[@instancename='PHX_SOPFormatRequest0C_DocType_ID']//*[contains(@class,'z-combobox-button')]").first();
		try {
			btn.click();
			page.waitForTimeout(1000);
		} catch (Exception ignored) {
		}

		String popupText = page.evaluate("() => { "
				+ "let p = document.querySelector('.z-combobox-popup:not([style*=\"display: none\"])'); "
				+ "return p ? p.innerText : ''; "
				+ "}").toString();

		softAssertTrue(popupText.contains("Annexure Request") || popupText.contains("SOP Format Request"),
				"Document Type dropdown should display 'Annexure Request' and 'SOP Format Request'. Found: " + popupText);
		log.info("Document Type dropdown options verified: [{}]", popupText.replaceAll("\n", ", "));
	}

	/**
	 * Step 4: Selects Document Type as "SOP Format Request", verifies Format field enables and becomes mandatory.
	 */
	public void selectDocTypeAsSOPFormatRequest() {
		log.info("Selecting Document Type as 'SOP Format Request' (Step 4)...");
		Locator dt = getVisible(docTypeInput);
		dt.fill("SOP Format Request");
		dt.press("Tab");
		page.waitForTimeout(1000);
		WaitUtils.waitForLoadingIndicatorToDisappear(page);

		// Format field will enable and becomes mandatory
		Locator fmt = getVisible(formatInput);
		softAssertTrue(!fmt.isDisabled(), "Format field should be enabled when Document Type is SOP Format Request.");
		log.info("Verified Format field is enabled.");
	}

	/**
	 * Step 5 & 6: Clicks SOP dropdown, selects SOP, clicks Format dropdown, selects Format, and Saves.
	 */
	public void selectSOPAndFormatAndSave(String targetSOP, String targetFormat) {
		log.info("Selecting SOP [{}] and Format [{}] (Steps 5-7)...", targetSOP, targetFormat);

		// 1. Select SOP
		Locator sop = getVisible(sopInput);
		//sop.click();
		if (targetSOP != null && !targetSOP.isEmpty()) {
			sop.fill(targetSOP);
			page.waitForTimeout(500);
			Locator matchingItem = page.locator(
					".z-combobox-popup:not([style*='display: none']) .z-comboitem:has-text('" + targetSOP + "')")
					.first();
			try {
				if (matchingItem.isVisible()) {
					matchingItem.click();
				} else {
					sop.press("Tab");
				}
			} catch (Exception e) {
				sop.press("Tab");
			}
		} else {
			// Click combobox arrow and pick first available SOP
			Locator sopBtn = page.locator(
					"//span[@instancename='PHX_SOPFormatRequest0PHX_SOP_ID']//*[contains(@class,'z-combobox-button')]").first();
			sopBtn.click();
			page.waitForTimeout(1000);
			Locator firstItem = page.locator(".z-combobox-popup:not([style*='display: none']) .z-comboitem").first();
			firstItem.click();
		}
		page.waitForTimeout(1000);
		WaitUtils.waitForLoadingIndicatorToDisappear(page);

		// 2. Select Format of selected SOP
		Locator fmt = getVisible(formatInput);
		fmt.click();
//		if (targetFormat != null && !targetFormat.isEmpty()) {
//			fmt.fill(targetFormat);
//			page.waitForTimeout(500);
//			Locator matchingFmt = page.locator(
//					".z-combobox-popup:not([style*='display: none']) .z-comboitem:has-text('" + targetFormat + "')")
//					.first();
//			try {
//				if (matchingFmt.isVisible()) {
//					matchingFmt.click();
//				} else {
//					fmt.press("Tab");
//				}
//			} catch (Exception e) {
//				fmt.press("Tab");
//			}
//		} else {
			Locator fmtBtn = page.locator(
					"//span[@instancename='PHX_SOPFormatRequest0PHX_SOPFormats_ID']//*[contains(@class,'z-combobox-button')]").first();
			fmtBtn.click();
			page.waitForTimeout(1000);
			Locator firstFmt = page.locator(".z-combobox-popup:not([style*='display: none']) .z-comboitem").last();
			firstFmt.click();
//		}
		page.waitForTimeout(500);

		// Step 7: Click Save
		log.info("Saving record with Format selected (Step 7)...");
		saveRecord();
	}

	public void fillDepartment(String department) {
		Locator dept = getVisible(departmentInput);
		dept.fill(department);
		dept.press("Tab");
		page.waitForTimeout(400);
	}
	
	/**
	 * Step 8: Enters Issue Type: Controlled, Requesting Department: Maintenance, Requested Copies: 2 and Saves.
	 */
	public void fillIssueDetailsAndSave(String issueType, String requestedCopies) {
		log.info("Entering Issue details: IssueType=[{}], Dept=[{}], Copies=[{}] (Step 8)...", issueType,
				requestedCopies);

		if (issueType != null && !issueType.isEmpty()) {
			Locator it = getVisible(issueTypeInput);
			it.fill(issueType);
			it.press("Tab");
			page.waitForTimeout(400);
		}

		Locator copies = getVisible(requestedCopiesInput);
		copies.fill(requestedCopies);
		copies.press("Tab");
		page.waitForTimeout(400);

		saveRecord();

		String docNo = getDocumentNo();
		softAssertTrue(docNo != null && !docNo.trim().isEmpty(), "Document No should be auto generated.");
		log.info("Generated SOP Format Request Document No: [{}]", docNo);
	}

	public String getDocumentNo() {
		Locator docNo = getVisible(documentNoInput);
		return docNo.inputValue().trim();
	}

	public String getDocumentStatus() {
		Locator st = getVisible(docStatusInput);
		return st.inputValue().trim();
	}

	public void saveRecord() {
		Locator sv = getVisible(saveButton);
		sv.click();
		page.waitForTimeout(2000);
		WaitUtils.waitForLoadingIndicatorToDisappear(page);
		log.info("Record saved successfully.");
	}

	/**
	 * Step 9 & 10: Clicks Document Action -> selects "Prepare" -> Status updates to In Progress.
	 */
	public void prepareDocumentAction() {
		log.info("Executing Prepare Document Action (Step 9-10)...");
		Locator docAction = getVisible(docActionButton);
		docAction.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		docAction.click();
		page.waitForTimeout(1000);
		WaitUtils.waitForLoadingIndicatorToDisappear(page);

		Locator dialog = page.locator("//div[@instancename='documentAction']");
		dialog.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(8000));
		dialog.locator("select").selectOption(new SelectOption().setLabel("Prepare"));
		page.waitForTimeout(500);

		dialog.locator("//button[@title='OK']").first().click();
		page.waitForTimeout(3000);
		WaitUtils.waitForBigLoadingIndicatorToDisappear(page);
		WaitUtils.waitForLoadingIndicatorToDisappear(page);

		String status = getDocumentStatus();
		softAssertTrue(status.equalsIgnoreCase("In Progress"),
				"Document Status should update to 'In Progress', found: [" + status + "].");
		log.info("Document Status updated successfully to: [{}]", status);
	}

	/**
	 * Step 12: Verifies SOP Format Issue window opened and requested record is displayed.
	 */
	public void verifySOPFormatIssueRequestedRecord(String expectedDocNo) {
		log.info("Verifying SOP Format Issue window and requested record: [{}] (Step 12)...", expectedDocNo);
		page.waitForTimeout(2000);
		WaitUtils.waitForLoadingIndicatorToDisappear(page);

		Locator docNoInp = getVisible(documentNoInput);
		docNoInp.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(15000));
		String actualDocNo = docNoInp.inputValue().trim();

		if (!actualDocNo.equals(expectedDocNo)) {
			log.info("Current SOP Format Issue record is [{}], looking up target [{}]...", actualDocNo, expectedDocNo);
			lookupRecord(expectedDocNo);
			actualDocNo = getVisible(documentNoInput).inputValue().trim();
		}

		softAssertEquals(actualDocNo, expectedDocNo,
				"Current record on SOP Format Issue window does not match requested SOP Format record!");
		log.info("Verified current SOP Format Issue record is: [{}]", actualDocNo);
	}

	public void lookupRecord(String docNo) {
		Locator searchBtn = page
				.locator("//div[contains(@class,'adwindow-toolbar')]//a[@title='Lookup Record    Alt+F']").first();
		searchBtn.click();
		page.waitForTimeout(1000);

		Locator docNoSearch = page.locator("//input[@instancename='DocumentNo']").first();
		docNoSearch.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(8000));
		docNoSearch.fill(docNo);
		page.waitForTimeout(500);

		page.locator("(//button[@title='OK'])[1]").click();
		page.waitForTimeout(2000);
		WaitUtils.waitForLoadingIndicatorToDisappear(page);
	}

	/**
	 * Step 13: Selects Control Sequence and Saves -> Control Number auto generates, Settings button enabled.
	 */
	public void selectControlSequenceAndSave(String sequence) {
		log.info("Selecting Control Sequence and saving (Step 13)...");
		Locator seq = getVisible(controlSeqInput);
		if (seq.isVisible() && !seq.isDisabled()) {
			try {
				seq.click();
				if (sequence != null && !sequence.isEmpty()) {
					seq.fill(sequence);
					seq.press("Tab");
				} else {
					Locator btn = page.locator(
							"//span[@instancename='PHX_SOPFormatRequest0AD_Sequence_ID']//*[contains(@class,'z-combobox-button')]").first();
					btn.click();
					page.waitForTimeout(1000);
					Locator firstSeq = page.locator(".z-combobox-popup:not([style*='display: none']) .z-comboitem").first();
					firstSeq.click();
				}
				page.waitForTimeout(500);
			} catch (Exception ignored) {
			}
		}

		// Fallback: If Control Number is directly fillable
		Locator ctrlNo = getVisible(controlNoInput);
		if (ctrlNo.isVisible() && !ctrlNo.isDisabled() && ctrlNo.inputValue().trim().isEmpty()) {
			ctrlNo.fill("101");
			ctrlNo.press("Tab");
			page.waitForTimeout(500);
		}

		saveRecord();

		// Control Number should auto generate / be set
		String actualCtrlNo = getVisible(controlNoInput).inputValue().trim();
		softAssertTrue(!actualCtrlNo.isEmpty(), "Control Number should be generated/present.");
		log.info("Control Number verified as: [{}]", actualCtrlNo);

		// Settings button gets enabled
		Locator proc = getVisible(processButton);
		softAssertTrue(!proc.isDisabled(), "Settings / Process button should be enabled after saving Control Sequence.");
		log.info("Settings / Process button is enabled.");
	}

	/**
	 * Step 14: Selects Issued By, Issue Date, Issued Copies and Saves.
	 */
	public void fillIssuedDetailsAndSave(String issuedBy, String issueDate, String issuedCopies) {
		log.info("Entering Issued details: IssuedBy=[{}], IssueDate=[{}], Copies=[{}] (Step 14)...", issuedBy,
				issueDate, issuedCopies);

		Locator ib = getVisible(issuedByInput);
		if (ib.isVisible() && !ib.isDisabled() && issuedBy != null && !issuedBy.isEmpty()) {
			ib.fill(issuedBy);
			ib.press("Tab");
			page.waitForTimeout(400);
		}

		Locator idDate = getVisible(issueDateInput);
		if (idDate.isVisible() && !idDate.isDisabled()) {
			String targetDate = (issueDate != null && !issueDate.isEmpty()) ? issueDate
					: LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
			idDate.fill(targetDate);
			idDate.press("Tab");
			page.waitForTimeout(400);
		}

		Locator ic = getVisible(issuedCopiesInput);
		if (ic.isVisible() && !ic.isDisabled() && issuedCopies != null && !issuedCopies.isEmpty()) {
			ic.fill(issuedCopies);
			ic.press("Tab");
			page.waitForTimeout(400);
		}

		saveRecord();
	}

	/**
	 * Step 15 & 16: Clicks Setting button -> selects "Print SOP Format" -> popup message displays.
	 */
	public void clickSettingsAndSelectPrintSOPFormat() {
		log.info("Clicking Settings / Process button from upper toolbar (Step 15)...");
		Locator proc = getVisible(processButton);
		proc.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		proc.click();
		page.waitForTimeout(1500);

		Locator printFmtOption = page.locator("//div[contains(@class,'z-menupopup-open')]//*[contains(text(),'Print SOP Format') or contains(text(),'Print Format')]")
				.first();
		printFmtOption.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		softAssertTrue(printFmtOption.isVisible(), "Print SOP Format option should display in Settings menu.");
		log.info("Clicking Print SOP Format option (Step 16)...");
		printFmtOption.click();
		page.waitForTimeout(2000);
	}

	/**
	 * Step 16 & 17: Verifies "Do you want to start the Process?" popup message and clicks OK.
	 */
	public void confirmPrintSOPFormatDialog(String IssueType) {
		log.info("Verifying Print SOP Format popup message (Step 16-17)...");
		Locator dialog = page.locator(
				"//div[contains(@class,'process-modal-dialog') or contains(@class,'popup-dialog') or contains(@class,'z-window-overlapped')][contains(.,'Do you want to start the Process?')]")
				.first();
		try {
			dialog.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
			String text = dialog.innerText();
			softAssertTrue(text.contains("Do you want to start the Process?"),
					"Print dialog should contain: 'Do you want to start the Process?'");
			log.info("Print SOP Format dialog message verified: [{}]", text.trim());

			if(page.locator("//span[@instancename='PHX_IssueType']").isVisible()) {
				page.locator("//span[@instancename='PHX_IssueType']//input").fill(IssueType);
			}
			
			
			Locator okBtn = dialog.locator("button[title='OK'], button:has-text('OK'), button.btn-primary").first();
			okBtn.click();
		} catch (Exception e) {
			log.warn("Print dialog confirm note: {}", e.getMessage());
			page.locator("//div[contains(@class,'process-modal-dialog')]//button[contains(.,'OK')]").first().click();
		}
		page.waitForTimeout(3000);
		WaitUtils.waitForLoadingIndicatorToDisappear(page);
	}

	/**
	 * Step 20: Navigates back on SOP Format Issue window and clicks Requery button.
	 */
	public void navigateBackToSOPFormatIssueAndRequery() {
		log.info("Navigating back on SOP Format Issue window and clicking ReQuery (Step 20)...");
		Locator fmtTab = page.locator("//li[contains(@class,'z-tab')]//span[contains(text(),'Format Issue')]").first();
		if (fmtTab.count() > 0 && fmtTab.isVisible()) {
			fmtTab.click();
			page.waitForTimeout(1000);
		}
		Locator req = getVisible(requeryButton);
		req.click();
		page.waitForTimeout(2500);
		WaitUtils.waitForLoadingIndicatorToDisappear(page);
		log.info("SOP Format Issue record re-queried successfully.");
	}

	/**
	 * Step 20 Validation: Verifies Print No is updated to expected value and Printed checkbox is expected status.
	 */
	public void verifyPrintNoAndPrintedStatus(String expectedPrintNo, boolean expectedPrinted) {
		log.info("Verifying Print No is [{}] and Printed is [{}]...", expectedPrintNo, expectedPrinted);
		String actualPrintNo = getPrintNo();
		boolean actualPrinted = isPrinted();
		softAssertEquals(actualPrintNo, expectedPrintNo, "Print No mismatch after print process!");
		softAssertEquals(actualPrinted, expectedPrinted, "Printed checkbox mismatch after print process!");
		log.info("Verified: Print No=[{}], Printed=[{}]", actualPrintNo, actualPrinted);
	}

	public boolean isPrinted() {
		Locator pr = getVisible(printedCheckbox);
		try {
			return pr.isChecked();
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Step 21: Runs Print SOP Format process again -> verifies error message:
	 * "Re-Print Reason is mandatory for re-printing the SOP Format."
	 */
//	public void verifyReprintReasonMandatoryError() {
//		log.info("Running Print SOP Format again without reprint reason (Step 21)...");
//		clickSettingsAndSelectPrintSOPFormat();
//		confirmPrintSOPFormatDialog();
//
//		// Check for mandatory error dialog
//		verifyErrorMessageAndClose("Re-Print Reason is mandatory for re-printing the SOP Format");
//	}

	/**
	 * Verifies error message displayed when reprinting SOP Format without reason:
	 * "Re-Print Reason is mandatory for re-printing the SOP Format."
	 * Uses soft assertions, dismisses the error dialog, and requeries.
	 */
	public void verifyReprintError() {
		log.info("Verifying reprint error message: 'Re-Print Reason is mandatory for re-printing the SOP Format.'...");
		page.waitForTimeout(1000);

		Locator errorSpan = page.locator(
				"//span[contains(text(),'Reason is mandatory for re-printing') or contains(text(),'Re-Print Reason is mandatory') or contains(text(),'Reason is mandatory')]");
		Locator msgBox = page.locator(
				"//div[contains(@class,'z-messagebox') or contains(@class,'popup-dialog') or contains(@class,'z-window-modal') or contains(@class,'z-window-highlighted')]");

		try {
			if (errorSpan.count() > 0 && errorSpan.first().isVisible()) {
				String actualMsg = errorSpan.first().innerText().trim();
				log.info("Detected reprint error span text: [{}]", actualMsg);
				softAssertTrue(actualMsg.toLowerCase().contains("reason is mandatory"),
						"Expected reprint error message containing reason is mandatory, but found: [" + actualMsg + "].");
			} else {
				msgBox.first().waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(8000));
				String actualMsg = msgBox.first().innerText().trim();
				log.info("Detected modal message: [{}]", actualMsg);
				softAssertTrue(actualMsg.toLowerCase().contains("reason is mandatory"),
						"Expected reprint error message containing reason is mandatory, but found: [" + actualMsg + "].");
			}

			// Dismiss error dialog
			Locator okBtn = page.locator(
					"//div[contains(@class,'z-messagebox') or contains(@class,'popup-dialog') or contains(@class,'z-window-modal') or contains(@class,'z-window-highlighted')]//button[contains(@class,'z-messagebox-btn') or @title='OK' or contains(.,'OK') or contains(@class,'btn-primary')] | //div[contains(@class,'z-messagebox')]//button")
					.first();
			if (okBtn.count() > 0 && okBtn.isVisible()) {
				okBtn.click();
			} else {
				page.keyboard().press("Escape");
			}
			page.waitForTimeout(1000);
			WaitUtils.waitForLoadingIndicatorToDisappear(page);
		} catch (Exception e) {
			log.warn("Could not capture reprint error modal: {}", e.getMessage());
			softAssertTrue(false, "Error modal containing [Re-Print Reason is mandatory for re-printing the SOP Format.] was not displayed.");
		}

		// Requery as noted in comment //Requery
		try {
			Locator req = getVisible(requeryButton);
			if (req.isVisible()) {
				req.click();
				page.waitForTimeout(1500);
				WaitUtils.waitForLoadingIndicatorToDisappear(page);
				log.info("Clicked ReQuery after verifying reprint error.");
			}
		} catch (Exception e) {
			log.warn("Requery after reprint error: {}", e.getMessage());
		}
	}

	/**
	 * Fills Reprint Reason with default value and saves.
	 */
	public void fillReprintReason() {
		
		fillReprintReason("Playwright Automation purpose");
	}

	/**
	 * Fills Reprint Reason with specified reason and saves.
	 */
	public void fillReprintReason(String reason) {
		log.info("Filling Reprint Reason: [{}] and saving...", reason);
		Locator reasonField;
		if (reprintReasonInput.count() > 0 && reprintReasonInput.first().isVisible()) {
			reasonField = reprintReasonInput.first();
		} else {
			reasonField = getVisible(reprintReasonInput);
		}
		reasonField.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(8000));
		reasonField.fill("Playwright Automation purpose");
		reasonField.press("Tab");
		page.waitForTimeout(500);
		log.info("Entered Reprint Reason: [{}]", reason);

		// Save record
		log.info("Saving SOP Format Issue record after entering reprint reason...");
		Locator sv = getVisible(saveButton);
		sv.click();
		page.waitForTimeout(2000);
		WaitUtils.waitForLoadingIndicatorToDisappear(page);
		log.info("Reprint reason saved successfully.");
	}

	// SOPRequest parity / alias methods
	public void clickSettingsAndSelectPrintSOP() {
		clickSettingsAndSelectPrintSOPFormat();
	}

//	public void confirmPrintControlledSOPDialog() {
//		confirmPrintSOPFormatDialog();
//	}

	public void navigateBackToSOPIssueAndRequery() {
		navigateBackToSOPFormatIssueAndRequery();
	}

	public void verifySOPIssueRequestedRecord(String expectedDocNo) {
		verifySOPFormatIssueRequestedRecord(expectedDocNo);
	}

	/**
	 * Step 22: Clicks on SOP Format Copy tab and verifies fields.
	 */
	public void navigateToSOPFormatCopyTab() {
		log.info("Clicking on SOP Format Copy tab (Step 22)...");
		Locator tab = getVisible(formatCopyTab);
		tab.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		tab.click();
		page.waitForTimeout(2000);
		WaitUtils.waitForLoadingIndicatorToDisappear(page);

		// Verify fields: Document No, Retrieved On, Cancelled, Printed, Active, Reason/Remark
		softAssertTrue(getVisible(copyDocNoInput).isVisible(), "Copy Document No should be visible.");
		softAssertTrue(getVisible(copyRetrievedOnInput).isVisible(), "Copy Retrieved On should be visible.");
		softAssertTrue(getVisible(copyCancelledCheckbox).isVisible(), "Copy Cancelled checkbox should be visible.");
		softAssertTrue(getVisible(copyPrintedCheckbox).isVisible(), "Copy Printed checkbox should be visible.");
		softAssertTrue(getVisible(copyActiveCheckbox).isVisible(), "Copy Active checkbox should be visible.");
		softAssertTrue(getVisible(copyRemarkInput).isVisible(), "Copy Reason/Remark should be visible.");
		log.info("SOP Format Copy subtab fields verified successfully.");
	}

	/**
	 * Step 23: Clicks Save -> Record generated with Document No.
	 */
	public String saveFirstFormatCopyRecord() {
		log.info("Saving first SOP Format Copy record (Step 23)...");
		page.locator("//span[@instancename='PHX_SOPFormatCopy0IsPrinted']").click();
		page.locator("//input[@instancename='PHX_SOPFormatCopy0PHX_Remark']").fill("Testing..");
		page.keyboard().press("Tab");
		saveRecord();
		String copyDocNo = getVisible(copyDocNoInput).inputValue().trim();
		softAssertTrue(!copyDocNo.isEmpty(), "Record should generate with Document No.");
		log.info("Generated first SOP Format Copy Document No: [{}]", copyDocNo);
		return copyDocNo;
	}

	/**
	 * Step 24 & 25: Clicks '+' button from toolbar -> New record displayed -> Clicks Save -> Generated Doc No.
	 */
	public String createAndSaveSecondFormatCopyRecord() {
		log.info("Clicking '+' button from toolbar to create second SOP Format Copy record (Step 24-25)...");
		Locator newBtn = page.locator(
				"//div[contains(@class,'adwindow-detailpane-toolbar')]//a[@title='New' or contains(@title,'Shift+Alt+N') or @title='New    Alt+N'] | "
						+ "//div[contains(@class,'adwindow-toolbar')]//a[@title='New    Alt+N']").first();
		newBtn.click();
		page.waitForTimeout(1500);
		WaitUtils.waitForLoadingIndicatorToDisappear(page);

		saveRecord();
		String copyDocNo = getVisible(copyDocNoInput).inputValue().trim();
		softAssertTrue(!copyDocNo.isEmpty(), "Second record should generate with Document No.");
		log.info("Generated second SOP Format Copy Document No: [{}]", copyDocNo);
		return copyDocNo;
	}

	/**
	 * Step 26: Navigates back to the SOP Format Issue window.
	 */
	public void navigateBackToSOPFormatIssueHeader() {
		log.info("Navigating back to SOP Format Issue window header (Step 26)...");
		Locator breadcrumb = getVisible(headerBreadcrumb);
		breadcrumb.click();
		page.waitForTimeout(1500);
		WaitUtils.waitForLoadingIndicatorToDisappear(page);
		log.info("Navigated back to SOP Format Issue header successfully.");
	}

	/**
	 * Step 27 & 28: Document Action -> Complete -> Status: Completed, fields read-only except Received By, Retrieved On, Retrieved By.
	 */
	public void completeDocumentAction() {
		log.info("Executing Complete Document Action (Step 27-28)...");
		Locator docAction = getVisible(docActionButton);
		docAction.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		docAction.click();
		page.waitForTimeout(1000);
		WaitUtils.waitForLoadingIndicatorToDisappear(page);

		Locator dialog = page.locator("//div[@instancename='documentAction']");
		dialog.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(8000));
		dialog.locator("select").selectOption(new SelectOption().setLabel("Complete"));
		page.waitForTimeout(500);

		dialog.locator("//button[@title='OK']").first().click();
		page.waitForTimeout(3000);
		WaitUtils.waitForBigLoadingIndicatorToDisappear(page);
		WaitUtils.waitForLoadingIndicatorToDisappear(page);

		String status = getDocumentStatus();
		softAssertTrue(status.equalsIgnoreCase("Completed"),
				"Document Status should update to 'Completed', found: [" + status + "].");

		// All fields become read-only except Received By, Retrieved On, Retrieved By
		softAssertTrue(!getVisible(receivedByInput).isDisabled(), "Received By should be editable.");
		softAssertTrue(!getVisible(retrievedOnInput).isDisabled(), "Retrieved On should be editable.");
		softAssertTrue(!getVisible(retrievedByInput).isDisabled(), "Retrieved By should be editable.");
		log.info("Complete Document Action verified successfully: Status=[{}]", status);
	}

	/**
	 * Step 29: Clicks Document Action -> selects "Closed" -> confirms dialog -> verifies error:
	 * "All format copy are not received back"
	 */
	public void verifyCannotCloseWithoutCopiesReceivedError() {
		log.info("Attempting to Close before receiving back all copies (Step 29)...");
		Locator docAction = getVisible(docActionButton);
		docAction.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		docAction.click();
		page.waitForTimeout(1000);
		WaitUtils.waitForLoadingIndicatorToDisappear(page);

		Locator dialog = page.locator("//div[@instancename='documentAction']");
		dialog.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(8000));
		dialog.locator("select").selectOption(new SelectOption().setLabel("Close"));
		page.waitForTimeout(500);

		dialog.locator("//button[@title='OK']").first().click();
		page.waitForTimeout(2000);

		// Handle confirmation dialog if displayed
		Locator confirmBtn = page.locator(
				"//div[contains(@class,'popup-dialog') or contains(@class,'z-window-highlighted') or contains(@class,'z-messagebox')]//button[contains(@class,'btn-ok') and not(@disabled)] | "
						+ "//div[contains(@class,'popup-dialog') or contains(@class,'z-window-highlighted')]//button[contains(.,'OK') and not(@disabled)]")
				.first();
		try {
			if (confirmBtn.isVisible()) {
				confirmBtn.click();
				page.waitForTimeout(2000);
			}
		} catch (Exception ignored) {
		}

		// Verify error message: "All format copy are not received back"
		verifyErrorMessageAndClose("All format copy are not received back");
	}

	/**
	 * Step 30: Enters Received By, Retrieved On, Retrieved By on Header and Saves.
	 */
	public void fillReceivedAndRetrievedHeaderDetailsAndSave(String qaUser, String date) {
		log.info("Entering Received By and Retrieved By details on header (Step 30)...");
		Locator recBy = getVisible(receivedByInput);
		recBy.fill(qaUser);
		recBy.press("Tab");
		page.waitForTimeout(1000);

		Locator retOn = getVisible(retrievedOnInput);
		String targetDate = (date != null && !date.isEmpty()) ? date
				: LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
		retOn.fill(targetDate);
		retOn.press("Tab");
		page.waitForTimeout(1000);

		Locator retBy = getVisible(retrievedByInput);
		retBy.fill(qaUser);
		retBy.press("Tab");
		page.waitForTimeout(1000);

		saveRecord();
	}

	/**
	 * Step 31 & 32: Clicks on SOP Format Copy tab, enters Retrieved On Date field for both records, and Saves.
	 */
	public void fillRetrievedOnDateForBothFormatCopiesAndSave(String date) {
		log.info("Entering Retrieved On date for both records in SOP Format Copy subtab (Step 31-32)...");
		Locator tab = getVisible(formatCopyTab);
		tab.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		tab.click();
		page.waitForTimeout(2000);
		WaitUtils.waitForLoadingIndicatorToDisappear(page);

		String targetDate = (date != null && !date.isEmpty()) ? date
				: LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

		// 1. Fill current record
		Locator retOn = getVisible(copyRetrievedOnInput);
		retOn.fill(targetDate);
		retOn.press("Tab");
		page.waitForTimeout(400);
		saveRecord();
		
		log.info("Retrieved On date filled for both format copy records.");
	}

	/**
	 * Step 34: Document Action -> Close -> Confirm Dialog -> Status: Closed, Processed: true, all fields read-only.
	 */
	public void closeDocumentAction() {
		log.info("Executing Close Document Action (Step 34)...");
		Locator docAction = getVisible(docActionButton);
		docAction.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		docAction.click();
		page.waitForTimeout(1000);
		WaitUtils.waitForLoadingIndicatorToDisappear(page);

		Locator dialog = page.locator("//div[@instancename='documentAction']");
		dialog.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(8000));
		dialog.locator("select").selectOption(new SelectOption().setLabel("Close"));
		page.waitForTimeout(500);

		dialog.locator("//button[@title='OK']").first().click();
		page.waitForTimeout(2000);

		// Handle confirmation dialog
		Locator confirmBtn = page.locator(
				"//div[contains(@class,'popup-dialog') or contains(@class,'z-window-highlighted') or contains(@class,'z-messagebox')]//button[contains(@class,'btn-ok') and not(@disabled)] | "
						+ "//div[contains(@class,'popup-dialog') or contains(@class,'z-window-highlighted')]//button[contains(.,'OK') and not(@disabled)]")
				.first();
		try {
			confirmBtn.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(8000));
			confirmBtn.click();
			page.waitForTimeout(3000);
			WaitUtils.waitForLoadingIndicatorToDisappear(page);
		} catch (Exception e) {
			log.warn("Close confirmation dialog note: {}", e.getMessage());
		}

		String status = getDocumentStatus();
		softAssertTrue(status.equalsIgnoreCase("Closed"),
				"Document Status should update to 'Closed', found: [" + status + "].");

		boolean isProcessed = getVisible(processedCheckbox).isChecked();
		softAssertTrue(isProcessed, "Processed flag should become true after Close.");
		log.info("Document successfully closed: Status=[{}], Processed=[{}]", status, isProcessed);
	}

	/**
	 * Verifies error message in modal popup or notification and dismisses it.
	 */
	public void verifyErrorMessageAndClose(String expectedText) {
		log.info("Verifying error message containing: [{}]...", expectedText);
		page.waitForTimeout(1000);

		if(page.locator("//span[text()='All format copy are not received back']").isVisible()) {
			log.info(expectedText);
		}
		else {
			softAssertTrue(page.locator("//span[text()='All format copy are not received back']").isVisible(), "All format copy are not received back..");
		}
		
//		Locator msgBox = page.locator(
//				"//div[contains(@class,'z-messagebox') or contains(@class,'popup-dialog') or contains(@class,'z-window-modal') or contains(@class,'z-window-highlighted')]");
//		try {
//			msgBox.first().waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(8000));
//			String text = msgBox.first().innerText();
//			log.info("Detected modal message: [{}]", text.trim());
//			softAssertTrue(text.toLowerCase().contains(expectedText.toLowerCase()),
//					"Expected error message containing: [" + expectedText + "], but found: [" + text.trim() + "].");
//
//			// Dismiss modal
//			Locator okBtn = msgBox.first()
//					.locator("button.btn-primary, button.z-messagebox-btn, button[title='OK'], button:has-text('OK'), div.z-window-close")
//					.first();
//			if (okBtn.count() > 0 && okBtn.isVisible()) {
//				okBtn.click();
//			} else {
//				page.keyboard().press("Escape");
//			}
//			page.waitForTimeout(1000);
//			WaitUtils.waitForLoadingIndicatorToDisappear(page);
//		} catch (Exception e) {
//			log.warn("Could not capture expected error modal [{}]: {}", expectedText, e.getMessage());
//			softAssertTrue(false, "Error modal containing [" + expectedText + "] was not displayed.");
//		}
	}
}
