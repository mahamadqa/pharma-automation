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
 * Page Object Class for SOP Request and SOP Issue Windows (PHX-3503).
 * Table: PHX_SOPRequestIssue
 */
public class SOPRequest {

	private static final Logger log = LogManager.getLogger(SOPRequest.class);
	private final Page page;

	// Locators - Header Fields
	private final Locator orgInput;
	private final Locator documentNoInput;
	private final Locator docTypeInput;
	private final Locator sopInput;
	private final Locator issueTypeInput;
	private final Locator requestDateInput;
	private final Locator requestedByInput;
	private final Locator departmentInput;
	private final Locator issueDateInput;
	private final Locator issuedByInput;
	private final Locator remarkInput;
	private final Locator receivedByInput;
	private final Locator retrievedOnInput;
	private final Locator retrievedByInput;
	private final Locator controlSeqInput;
	private final Locator controlNoInput;
	private final Locator printNoInput;
	private final Locator printedCheckbox;
	private final Locator processedCheckbox;
	private final Locator activeCheckbox;
	private final Locator docStatusInput;
	private final Locator docActionButton;
	private final Locator rePrintReason;
	
	// Upper Toolbar
	private final Locator saveButton;
	private final Locator requeryButton;
	private final Locator processButton;
	private final Locator menuSearchInput;

	public SOPRequest(Page page) {
		this.page = page;

		// Header inputs
		this.orgInput = page.locator("//span[@instancename='PHX_SOPRequestIssue0AD_Org_ID']//input");
		this.rePrintReason = page.locator(
				"//input[@instancename='PHX_SOPRequestIssue0PHX_Reason'] | //textarea[@instancename='PHX_SOPRequestIssue0PHX_Reason'] | //span[@instancename='PHX_SOPRequestIssue0PHX_Reason']//input | //div[@instancename='PHX_SOPRequestIssue0PHX_Reason']//input");
		this.documentNoInput = page.locator("//input[@instancename='PHX_SOPRequestIssue0DocumentNo']");
		this.docTypeInput = page.locator("//span[@instancename='PHX_SOPRequestIssue0C_DocType_ID']//input");
		this.sopInput = page.locator("//span[@instancename='PHX_SOPRequestIssue0PHX_SOP_ID']//input");
		this.issueTypeInput = page.locator("//span[@instancename='PHX_SOPRequestIssue0PHX_IssueType']//input");
		this.requestDateInput = page
				.locator("//span[@instancename='PHX_SOPRequestIssue0PHX_RequestDate']//input[not(contains(@class,'z-timebox-input'))]");
		this.requestedByInput = page.locator(
				"//div[@instancename='PHX_SOPRequestIssue0AD_User_ID']//input | //span[@instancename='PHX_SOPRequestIssue0AD_User_ID']//input");
		this.departmentInput = page.locator("//span[@instancename='PHX_SOPRequestIssue0C_Department_ID']//input");
		this.issueDateInput = page
				.locator("//span[@instancename='PHX_SOPRequestIssue0IssueDate']//input[not(contains(@class,'z-timebox-input'))]");
		this.issuedByInput = page.locator("//span[@instancename='PHX_SOPRequestIssue0PHX_IssuedBy_ID']//input");
		this.remarkInput = page.locator(
				"//input[@instancename='PHX_SOPRequestIssue0PHX_Remark'] | //textarea[@instancename='PHX_SOPRequestIssue0PHX_Remark'] | //span[@instancename='PHX_SOPRequestIssue0PHX_Remark']//input | //div[@instancename='PHX_SOPRequestIssue0PHX_Remark']//input");
		this.receivedByInput = page.locator(
				"//div[@instancename='PHX_SOPRequestIssue0PHX_ReceivedBy_ID']//input | //span[@instancename='PHX_SOPRequestIssue0PHX_ReceivedBy_ID']//input");
		this.retrievedOnInput = page
				.locator("//span[@instancename='PHX_SOPRequestIssue0PHX_RetrievedOn']//input[not(contains(@class,'z-timebox-input'))]");
		this.retrievedByInput = page.locator(
				"//div[@instancename='PHX_SOPRequestIssue0PHX_RetrievedBy_ID']//input | //span[@instancename='PHX_SOPRequestIssue0PHX_RetrievedBy_ID']//input");

		this.controlSeqInput = page.locator("//span[@instancename='PHX_SOPRequestIssue0AD_Sequence_ID']//input");
		this.controlNoInput = page.locator("//input[@instancename='PHX_SOPRequestIssue0PHX_ControllNo']");
		this.printNoInput = page.locator(
				"//div[@instancename='PHX_SOPRequestIssue0PHX_PrintNo']//input | //span[@instancename='PHX_SOPRequestIssue0PHX_PrintNo']//input | //input[@instancename='PHX_SOPRequestIssue0PHX_PrintNo']");
		this.printedCheckbox = page.locator("//span[@instancename='PHX_SOPRequestIssue0IsPrinted']//input");
		this.processedCheckbox = page.locator("//span[@instancename='PHX_SOPRequestIssue0Processed']//input");
		this.activeCheckbox = page.locator("//span[@instancename='PHX_SOPRequestIssue0IsActive']//input");
		this.docStatusInput = page.locator("//span[@instancename='PHX_SOPRequestIssue0DocStatus']//input");
		this.docActionButton = page.locator("//button[@instancename='PHX_SOPRequestIssue0DocAction']");

		// Toolbar Locators
		this.saveButton = page.locator(
				"//div[contains(@class,'adwindow-toolbar')]//a[@title='Save Changes    Alt+S'] | //a[@title='Save Changes    Alt+S']");
		this.requeryButton = page.locator(
				"//div[contains(@class,'adwindow-toolbar')]//a[contains(@title,'ReQuery') and not(contains(@class,'trash'))] | //a[@title='ReQuery    Alt+F5']");
		this.processButton = page.locator(
				"//div[contains(@class,'adwindow-toolbar')]//a[contains(@title,'Process') and not(contains(@class,'trash'))] | //a[contains(@title,'Process    Alt+O')]");
		this.menuSearchInput = page.locator("//input[@class='z-bandbox-input']");
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
	 * Step 2: Opens SOP Request window through Menu Search '+' icon.
	 */
	public void navigateToSOPRequestNewRecord() {
		log.info("Searching 'SOP Request' in Menu Searchbox and clicking '+' icon...");
		menuSearchInput.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		menuSearchInput.fill("SOP Request");
		page.waitForTimeout(2500);

		Locator newIcon = page.locator(
				"//div[contains(@class,'z-bandbox-popup')]//tr[contains(.,'SOP Request')]//a[@title='New'] | "
						+ "//tr[contains(.,'SOP Request')]//a[@title='New'] | "
						+ "//span[contains(text(),'SOP Request')]/ancestor::tr//a[@title='New']")
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
		log.info("SOP Request window opened with a new record.");
	}

	/**
	 * Step 2 Validation: Checks all specified fields are displaying on the SOP Request window.
	 */
	public void verifySOPRequestFieldsDisplaying(String expectedUser) {
		log.info("Verifying all fields displaying on SOP Request window (Step 2)...");
		softAssertTrue(getVisible(orgInput).isVisible(), "Organization field should be visible.");
		softAssertTrue(getVisible(documentNoInput).isVisible(), "Document No field should be visible.");
		softAssertTrue(getVisible(docTypeInput).isVisible(), "Document Type field should be visible.");
		softAssertTrue(getVisible(sopInput).isVisible(), "SOP field should be visible.");
		softAssertTrue(getVisible(issueTypeInput).isVisible(), "Issue Type field should be visible.");
		softAssertTrue(getVisible(requestDateInput).isVisible(), "Request Date field should be visible.");
		softAssertTrue(getVisible(requestedByInput).isVisible(), "Requested By field should be visible.");
		softAssertTrue(getVisible(departmentInput).isVisible(), "Department field should be visible.");
		softAssertTrue(getVisible(issueDateInput).isVisible(), "Issue Date field should be visible.");
		softAssertTrue(getVisible(issuedByInput).isVisible(), "Issued By field should be visible.");
		softAssertTrue(getVisible(remarkInput).isVisible(), "Reason/Remark field should be visible.");
		softAssertTrue(getVisible(receivedByInput).isVisible(), "Received By field should be visible.");
		softAssertTrue(getVisible(retrievedOnInput).isVisible(), "Retrieved On field should be visible.");

		// Verify Request Date is auto set to current date
		String today = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
		String actualReqDate = getVisible(requestDateInput).inputValue().trim();
		if (!actualReqDate.isEmpty()) {
			softAssertTrue(actualReqDate.equals(today) || actualReqDate.contains(String.valueOf(LocalDate.now().getDayOfMonth())),
					"Request Date [" + actualReqDate + "] should be auto set to current date [" + today + "].");
		}

		// Verify Requested By is auto set to current user
		if (expectedUser != null && !expectedUser.isEmpty()) {
			String actualUser = getVisible(requestedByInput).inputValue().trim();
			softAssertTrue(actualUser.toLowerCase().contains(expectedUser.toLowerCase()),
					"Requested By [" + actualUser + "] should be auto set to current user [" + expectedUser + "].");
		}
		log.info("SOP Request header fields verified successfully.");
	}

	/**
	 * Step 3: Enters Document Type, SOP, Department, Issue Type and Saves with default remark.
	 */
	public void fillSOPRequestAndSave(String docType, String sopSelection, String department, String issueType) {
		fillSOPRequestAndSave(docType, sopSelection, department, issueType, "Playwright Automation SOP Request");
	}

	/**
	 * Step 3: Enters Document Type, SOP, Department, Issue Type, Remark and Saves.
	 */
	public void fillSOPRequestAndSave(String docType, String sopSelection, String department, String issueType,
			String remark) {
		log.info("Entering details on SOP Request: DocType=[{}], SOP=[{}], Dept=[{}], IssueType=[{}], Remark=[{}]", docType,
				sopSelection, department, issueType, remark);

		Locator dt = getVisible(docTypeInput);
		dt.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		dt.fill(docType);
		dt.press("Tab");
		page.waitForTimeout(500);

		Locator sop = getVisible(sopInput);
		sop.click();
		if (sopSelection != null && !sopSelection.isEmpty()) {
			sop.fill(sopSelection);
			page.waitForTimeout(500);
			Locator matchingItem = page.locator(
					".z-combobox-popup:not([style*='display: none']) .z-comboitem:has-text('" + sopSelection + "')")
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
			// Pick first available approved SOP from dropdown
			Locator sopBtn = page.locator(
					"//span[@instancename='PHX_SOPRequestIssue0PHX_SOP_ID']//*[contains(@class,'z-combobox-button')]").first();
			sopBtn.click();
			page.waitForTimeout(1000);
			Locator firstItem = page.locator(".z-combobox-popup:not([style*='display: none']) .z-comboitem").first();
			firstItem.click();
		}
		page.waitForTimeout(500);

		if (issueType != null && !issueType.isEmpty()) {
			Locator it = getVisible(issueTypeInput);
			it.fill(issueType);
			it.press("Tab");
			page.waitForTimeout(500);
		}

		Locator dept = getVisible(departmentInput);
		dept.fill(department);
		dept.press("Tab");
		page.waitForTimeout(500);

		if (remark != null && !remark.isEmpty()) {
			Locator rem = getVisible(remarkInput);
			rem.fill(remark);
			rem.press("Tab");
			page.waitForTimeout(500);
			log.info("Entered SOP Request Remark: [{}]", remark);
		}

		// Save record
		log.info("Saving SOP Request record...");
		Locator sv = getVisible(saveButton);
		sv.click();
		page.waitForTimeout(2000);
		WaitUtils.waitForLoadingIndicatorToDisappear(page);

		// Verify Document No auto-generated
		String docNo = getDocumentNo();
		softAssertTrue(docNo != null && !docNo.trim().isEmpty(), "Document No should be auto generated.");
		log.info("Generated SOP Request Document No: [{}]", docNo);

		// Verify Document Status is Drafted
		String status = getDocumentStatus();
		softAssertEquals(status.toLowerCase(), "drafted", "Document Status should be 'Drafted' after initial save.");
		log.info("Document Status verified as: [{}]", status);
	}

	/**
	 * Returns current Document No.
	 */
	public String getDocumentNo() {
		Locator docNo = getVisible(documentNoInput);
		return docNo.inputValue().trim();
	}

	/**
	 * Returns current Document Status.
	 */
	public String getDocumentStatus() {
		Locator st = getVisible(docStatusInput);
		return st.inputValue().trim();
	}

	/**
	 * Step 4 & 5: Clicks Document Action -> selects "Prepare" -> clicks OK -> Status becomes "In Progress".
	 */
	public void prepareDocumentAction() {
		log.info("Executing Document Action: Prepare...");
		Locator docAction = getVisible(docActionButton);
		docAction.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		docAction.click();
		page.waitForTimeout(1000);
		WaitUtils.waitForLoadingIndicatorToDisappear(page);

		Locator dialog = page.locator("//div[@instancename='documentAction']");
		dialog.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(8000));

		Locator dropdown = dialog.locator("select");
		dropdown.selectOption(new SelectOption().setLabel("Prepare"));
		page.waitForTimeout(500);

		dialog.locator("//button[@title='OK']").first().click();
		page.waitForTimeout(2500);
		WaitUtils.waitForBigLoadingIndicatorToDisappear(page);
		WaitUtils.waitForLoadingIndicatorToDisappear(page);

		String status = getDocumentStatus();
		softAssertTrue(status.equalsIgnoreCase("In Progress"),
				"Document Status should update to 'In Progress', found: [" + status + "].");
		log.info("Document Status updated successfully to: [{}]", status);
	}

	/**
	 * Step 7: Verifies SOP Issue window opened and requested record is displayed.
	 */
	public void verifySOPIssueRequestedRecord(String expectedDocNo) {
		log.info("Verifying SOP Issue window and requested record: [{}]...", expectedDocNo);
		page.waitForTimeout(2000);
		WaitUtils.waitForLoadingIndicatorToDisappear(page);

		Locator docNoInp = getVisible(documentNoInput);
		docNoInp.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(15000));
		String actualDocNo = docNoInp.inputValue().trim();

		// If current row is not the expected one, lookup or navigate to it
		if (!actualDocNo.equals(expectedDocNo)) {
			log.info("Current SOP Issue record is [{}], looking up target record [{}]...", actualDocNo, expectedDocNo);
			lookupRecord(expectedDocNo);
			actualDocNo = getVisible(documentNoInput).inputValue().trim();
		}

		softAssertEquals(actualDocNo, expectedDocNo,
				"Current record on SOP Issue window does not match requested SOP record!");
		log.info("Verified current SOP Issue record is: [{}]", actualDocNo);
	}

	/**
	 * Searches and opens record by Document No in the current window.
	 */
	public void lookupRecord(String docNo) {
		log.info("Looking up record with Document No: [{}]", docNo);
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
	 * Step 8: Enters Control Sequence / Control Number, Issue Date, Issued By and Saves.
	 * Checks: Print No = 0, Control Sequence = read only, Printed = false.
	 */
	public void fillSOPIssueDetailsAndSave(String controlSequence, String issueDate, String issuedBy) {
		log.info("Filling SOP Issue details: ControlSeq/No=[{}], IssueDate=[{}], IssuedBy=[{}]", controlSequence,
				issueDate, issuedBy);

		// 1. Control Sequence or Control Number
		Locator ctrlNo = getVisible(controlNoInput);
		if (controlSequence != null && !controlSequence.isEmpty()) {
			// If a sequence combobox option is provided, try selecting it
			Locator seq = getVisible(controlSeqInput);
			if (seq.isVisible() && !seq.isDisabled()) {
				try {
					seq.click();
					seq.fill(controlSequence);
					seq.press("Tab");
					page.waitForTimeout(500);
				} catch (Exception ignored) {
				}
			}
			// If Control Number field is enabled and empty, set numeric control number
			if (ctrlNo.isVisible() && !ctrlNo.isDisabled()) {
				String val = ctrlNo.inputValue().trim();
				if (val.isEmpty()) {
					// Numeric sequence fallback for Control Number
					String numVal = controlSequence.replaceAll("[^0-9]", "");
					if (numVal.isEmpty()) numVal = "101";
					ctrlNo.fill(numVal);
					ctrlNo.press("Tab");
					page.waitForTimeout(500);
				}
			}
		} else {
			if (ctrlNo.isVisible() && !ctrlNo.isDisabled() && ctrlNo.inputValue().trim().isEmpty()) {
				ctrlNo.fill("101");
				ctrlNo.press("Tab");
				page.waitForTimeout(500);
			}
		}

		// 2. Issue Date
		Locator idDate = getVisible(issueDateInput);
		if (idDate.isVisible() && !idDate.isDisabled()) {
			String targetDate = (issueDate != null && !issueDate.isEmpty()) ? issueDate
					: LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
			idDate.fill(targetDate);
			idDate.press("Tab");
			page.waitForTimeout(500);
		}

		// 3. Issued By
		Locator ib = getVisible(issuedByInput);
		if (ib.isVisible() && !ib.isDisabled() && issuedBy != null && !issuedBy.isEmpty()) {
			ib.fill(issuedBy);
			ib.press("Tab");
			page.waitForTimeout(500);
		}

		// Save record
		log.info("Saving SOP Issue record...");
		Locator sv = getVisible(saveButton);
		sv.click();
		page.waitForTimeout(2000);
		WaitUtils.waitForLoadingIndicatorToDisappear(page);

		// Validate Step 8 values:
		// Print No : 0
		String printNo = getPrintNo();
		softAssertEquals(printNo, "0", "Print No should be 0 before printing.");

		// Printed : False
		boolean isPrinted = isPrinted();
		softAssertTrue(!isPrinted, "Printed checkbox should be False before printing.");

		// Control Sequence : Read only
		Locator seqInp = getVisible(controlSeqInput);
		boolean seqDisabled = seqInp.isDisabled() || seqInp.getAttribute("readonly") != null;
		softAssertTrue(seqDisabled, "Control Sequence should become read-only after saving Control Number.");
		log.info("Verified Step 8: Print No=[{}], Printed=[{}], Control Sequence read-only=[{}]", printNo, isPrinted,
				seqDisabled);
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

	public boolean isPrinted() {
		Locator pr = getVisible(printedCheckbox);
		try {
			return pr.isChecked();
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Step 9 & 10: Clicks Settings (Process) button -> selects "Print SOP" -> dialog displayed.
	 */
	public void clickSettingsAndSelectPrintSOP() {
		log.info("Clicking Settings / Process button from upper toolbar (Step 9)...");
		Locator proc = getVisible(processButton);
		proc.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		//softAssertTrue(!proc.isDisabled(), "Settings / Process button should be enabled after setting Control Number.");
		proc.click();
		page.waitForTimeout(2000);

		// Step 9 & 10: Print SOP option should display
		Locator printSOPOption = page.locator(
				"//div[contains(@class,'z-menupopup-open')]//span[text()='Print SOP']")
				.first();
		softAssertTrue(printSOPOption.isVisible(), "Print SOP option should display in Settings menu.");
		log.info("Clicking Print SOP option (Step 10)...");
		printSOPOption.click();
		page.waitForTimeout(2000);
		WaitUtils.waitForLoadingIndicatorToDisappear(page);
	}

	/**
	 * Step 10 & 11: Verifies "Do you want to start the Process?" dialog and clicks OK.
	 */
	public void confirmPrintControlledSOPDialog() {
		log.info("Verifying Print Controlled SOP dialogue popup message...");
		Locator dialog = page.locator(
				"//div[contains(@class,'process-modal-dialog') or contains(@class,'popup-dialog') or contains(@class,'z-window-overlapped')][contains(.,'Do you want to start the Process?')]")
				.first();
		try {
			dialog.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
			String text = dialog.innerText();
			softAssertTrue(text.contains("Do you want to start the Process?"),
					"Print dialog should contain message: 'Do you want to start the Process?'");
			log.info("Dialog popup message verified: [{}]", text.trim());

			Locator okBtn = dialog.locator("button[title='OK'], button:has-text('OK'), button.btn-primary").first();
			okBtn.click();
			log.info("Clicked OK on Print Controlled SOP dialogue popup.");
		} catch (Exception e) {
			log.warn("Dialog confirmation note: {}", e.getMessage());
			// Fallback click on OK button in any active dialog
			page.locator("//div[contains(@class,'process-modal-dialog')]//button[contains(.,'OK')]").first().click();
		}
		page.waitForTimeout(3000);
		WaitUtils.waitForLoadingIndicatorToDisappear(page);
	}

	/**
	 * Step 14: Navigates back on SOP Issue window and requeries.
	 */
	public void navigateBackToSOPIssueAndRequery() {
		log.info("Navigating back on SOP Issue window and clicking ReQuery button (Step 14)...");
		Locator sopIssueTab = page.locator("//li[contains(@class,'z-tab')]//span[contains(text(),'SOP Issue')]").first();
		if (sopIssueTab.count() > 0 && sopIssueTab.isVisible()) {
			sopIssueTab.click();
			page.waitForTimeout(1000);
		}
		Locator req = getVisible(requeryButton);
		req.click();
		page.waitForTimeout(2500);
		WaitUtils.waitForLoadingIndicatorToDisappear(page);
		log.info("SOP Issue record re-queried successfully.");
	}

	/**
	 * Step 14 Validation: Verifies Print No is updated to 1 and Printed checkbox is true.
	 */
	public void verifyPrintNoAndPrintedStatus(String expectedPrintNo, boolean expectedPrinted) {
		log.info("Verifying Print No is [{}] and Printed is [{}]...", expectedPrintNo, expectedPrinted);
		String actualPrintNo = getPrintNo();
		boolean actualPrinted = isPrinted();
		softAssertEquals(actualPrintNo, expectedPrintNo, "Print No mismatch after print process!");
		softAssertEquals(actualPrinted, expectedPrinted, "Printed checkbox mismatch after print process!");
		log.info("Verified: Print No=[{}], Printed=[{}]", actualPrintNo, actualPrinted);
	}

	/**
	 * Step 15 & 16: Document Action -> Complete -> Status: Completed, Processed: true, fields read-only.
	 */
	public void completeDocumentAction() {
		log.info("Executing Complete Document Action (Step 15-16)...");
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

		boolean isProcessed = getVisible(processedCheckbox).isChecked();
		softAssertTrue(isProcessed, "Processed flag should become true after Complete.");

		// Verify fields become read-only except Received By, Retrieved On, Retrieved By
		softAssertTrue(getVisible(controlNoInput).isDisabled(), "Control Number should be read-only.");
		softAssertTrue(getVisible(issueDateInput).isDisabled(), "Issue Date should be read-only.");
		softAssertTrue(getVisible(issuedByInput).isDisabled(), "Issued By should be read-only.");

		log.info("Complete Document Action verified successfully: Status=[{}], Processed=[{}]", status, isProcessed);
	}

	/**
	 * Step 17 & 18: Document Action -> Close -> Confirm Dialog -> Status: Closed, Retrieved On & Retrieved By auto-set.
	 */
	public void closeDocumentAction(String expectedRetrievedByUser) {
		log.info("Executing Close Document Action (Step 17-18)...");
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

		// Handle "Do you want to Close this Document?" confirmation popup
		log.info("Confirming 'Do you want to Close this Document?' popup...");
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

		// Verify Document Status updates to Closed
		String status = getDocumentStatus();
		softAssertTrue(status.equalsIgnoreCase("Closed"),
				"Document Status should update to 'Closed', found: [" + status + "].");

		// Verify Retrieved On auto set to current date
		String today = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
		String actualRetrievedOn = getVisible(retrievedOnInput).inputValue().trim();
		softAssertTrue(!actualRetrievedOn.isEmpty(), "Retrieved On date should be auto set on Close.");
		if (!actualRetrievedOn.isEmpty()) {
			softAssertTrue(
					actualRetrievedOn.equals(today)
							|| actualRetrievedOn.contains(String.valueOf(LocalDate.now().getDayOfMonth())),
					"Retrieved On [" + actualRetrievedOn + "] should be auto set to current date [" + today + "].");
		}

		// Verify Retrieved By auto set to current user
		if (expectedRetrievedByUser != null && !expectedRetrievedByUser.isEmpty()) {
			String actualRetrievedBy = getVisible(retrievedByInput).inputValue().trim();
			softAssertTrue(actualRetrievedBy.toLowerCase().contains(expectedRetrievedByUser.toLowerCase()),
					"Retrieved By [" + actualRetrievedBy + "] should be auto set to current user ["
							+ expectedRetrievedByUser + "].");
		}
		log.info("Document Closed successfully. Status=[{}], RetrievedOn=[{}], RetrievedBy=[{}]", status,
				actualRetrievedOn, getVisible(retrievedByInput).inputValue().trim());
	}

	/**
	 * Verifies error message displayed when reprinting without reason:
	 * "Reason is mandatory for re-printing the SOP."
	 * Uses soft assertions, dismisses the error dialog, and requeries.
	 */
	public void verifyReprintError() {
		log.info("Verifying reprint error message: 'Reason is mandatory for re-printing the SOP.'...");
		page.waitForTimeout(1000);

		Locator errorSpan = page.locator(
				"//span[text()='Reason is mandatory for re-printing the SOP.'] | //span[contains(text(),'Reason is mandatory for re-printing the SOP')]");
		Locator msgBox = page.locator(
				"//div[contains(@class,'z-messagebox') or contains(@class,'popup-dialog') or contains(@class,'z-window-modal') or contains(@class,'z-window-highlighted')]");

		try {
			if (errorSpan.count() > 0 && errorSpan.first().isVisible()) {
				String actualMsg = errorSpan.first().innerText().trim();
				log.info("Detected reprint error span text: [{}]", actualMsg);
				softAssertTrue(actualMsg.contains("Reason is mandatory for re-printing the SOP"),
						"Expected reprint error message containing: [Reason is mandatory for re-printing the SOP.], but found: ["
								+ actualMsg + "].");
			} else {
				msgBox.first().waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(8000));
				String actualMsg = msgBox.first().innerText().trim();
				log.info("Detected modal message: [{}]", actualMsg);
				softAssertTrue(actualMsg.contains("Reason is mandatory for re-printing the SOP"),
						"Expected reprint error message containing: [Reason is mandatory for re-printing the SOP.], but found: ["
								+ actualMsg + "].");
			}

			// Dismiss error dialog
			Locator okBtn = page.locator(
					"//div[contains(@class,'z-messagebox') or contains(@class,'popup-dialog') or contains(@class,'z-window-modal') or contains(@class,'z-window-highlighted')]//button[contains(@class,'z-messagebox-btn') or @title='OK' or contains(.,'OK')] | //div[contains(@class,'z-messagebox')]//button")
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
			softAssertTrue(false, "Error modal containing [Reason is mandatory for re-printing the SOP.] was not displayed.");
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
	 * Fills Reprint Reason with default value ("Playwright Automation purpose") and saves.
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
		if (rePrintReason.count() > 0 && rePrintReason.first().isVisible()) {
			reasonField = rePrintReason.first();
		} else {
			reasonField = getVisible(rePrintReason);
		}
		reasonField.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(8000));
		reasonField.fill(reason);
		reasonField.press("Tab");
		page.waitForTimeout(500);
		log.info("Entered Reprint Reason: [{}]", reason);

		// Save record
		log.info("Saving SOP Issue record after entering reprint reason...");
		Locator sv = getVisible(saveButton);
		sv.click();
		page.waitForTimeout(2000);
		WaitUtils.waitForLoadingIndicatorToDisappear(page);
		log.info("Reprint reason saved successfully.");
	}
}
