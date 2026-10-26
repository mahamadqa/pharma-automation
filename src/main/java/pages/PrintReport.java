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
 * Page object representing the Report: SOP preview window,
 * controlled copy stamps, supersede copy stamps, and print dialog interactions.
 */
public class PrintReport {

	private static final Logger log = LogManager.getLogger(PrintReport.class);
	private final Page page;

	private final Locator reportTab;
	private final Locator closeReportTabButton;
	private final Locator printButton;

	public PrintReport(Page page) {
		this.page = page;
		this.reportTab = page.locator("//li[contains(@class,'z-tab')]//span[contains(text(),'Report: SOP') or contains(text(),'Report : SOP')]");
		this.closeReportTabButton = page.locator(
				"//li[contains(@class,'z-tab') and .//span[contains(text(),'Report')]]//i[contains(@class,'z-tab-icon') or contains(@class,'z-icon-times') or @aria-label='Close'] | " +
				"//li[contains(@class,'z-tab')]//span[contains(text(),'Report: SOP') or contains(text(),'Report : SOP')]/following-sibling::i[contains(@class,'z-tab-icon')] | " +
				"//div[@class='z-tab-button']//i[@aria-label='Close']");
		this.printButton = page.locator("//a[@title='Print    Alt+P'] | //button[@title='Print'] | //div[contains(@class,'z-tabpanel') and not(contains(@style,'display: none'))]//a[@title='Print    Alt+P']");
	}

	/**
	 * Verifies that the Report: SOP window tab has opened and the report iframe is rendered.
	 */
	public void verifyReportOpened() {
		log.info("Verifying Report: SOP window is opened...");
		try {
			reportTab.first().waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(15000));
		} catch (Exception e) {
			log.warn("Report tab wait timed out: {}", e.getMessage());
		}

		FrameLocator reportFrame = page.frameLocator("//iframe[@class='z-iframe']");
		try {
			reportFrame.locator("body").waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(15000));
			log.info("Report body is visible inside iframe.");
		} catch (Exception e) {
			log.warn("Report body wait in iframe timed out: {}", e.getMessage());
		}
		WaitUtils.waitForLoadingIndicatorToDisappear(page);
	}

	/**
	 * Verifies whether the 'Master Copy' stamp is displayed on the SOP report.
	 *
	 * @param expectedVisible true if stamp is expected, false if it should NOT be present.
	 */
	public void verifyMasterCopyStamp(boolean expectedVisible) {
		log.info("Verifying 'Master Copy' stamp. Expected visible: [{}]", expectedVisible);
		FrameLocator reportFrame = page.frameLocator("//iframe[@class='z-iframe']");

		Locator stampLoc = reportFrame.locator("//div[contains(@class,'stamp') and contains(text(),'Master Copy')] | //span[contains(text(),'Master Copy')] | //*[contains(text(),'Master Copy')]");
		boolean isDisplayed = false;
		try {
			isDisplayed = stampLoc.count() > 0 && stampLoc.first().isVisible();
		} catch (Exception ignored) {
		}

		if (expectedVisible) {
			Assert.assertTrue(isDisplayed, "Expected 'Master Copy' stamp to be visible on Report, but it was not found.");
			log.info("✅ 'Master Copy' stamp verified successfully.");
		} else {
			Assert.assertFalse(isDisplayed, "Expected 'Master Copy' stamp NOT to be displayed, but it was visible.");
			log.info("✅ Verified 'Master Copy' stamp is NOT displayed as expected.");
		}
	}

	/**
	 * Verifies whether the 'Controlled Copy' stamp is displayed on the SOP report.
	 *
	 * @param expectedVisible true if stamp is expected, false otherwise.
	 */
	public void verifyControlledCopyStamp(boolean expectedVisible) {
		log.info("Verifying 'Controlled Copy' stamp. Expected visible: [{}]", expectedVisible);
		FrameLocator reportFrame = page.frameLocator("//iframe[@class='z-iframe']");

		Locator stampLoc = reportFrame.locator("//div[contains(@class,'stamp') and contains(text(),'Controlled Copy')] | //span[contains(text(),'Controlled Copy')] | //*[contains(text(),'Controlled Copy')]");
		boolean isDisplayed = false;
		try {
			isDisplayed = stampLoc.count() > 0 && stampLoc.first().isVisible();
		} catch (Exception ignored) {
		}

		if (expectedVisible) {
			Assert.assertTrue(isDisplayed, "Expected 'Controlled Copy' stamp to be visible on Report, but it was not found.");
			log.info("✅ 'Controlled Copy' stamp verified successfully.");
		} else {
			Assert.assertFalse(isDisplayed, "Expected 'Controlled Copy' stamp NOT to be displayed, but it was visible.");
			log.info("✅ Verified 'Controlled Copy' stamp is NOT displayed.");
		}
	}

	/**
	 * Verifies whether the 'Supersede Copy' stamp is displayed on the SOP report.
	 *
	 * @param expectedVisible true if stamp is expected, false otherwise.
	 */
	public void verifySupersedeCopyStamp(boolean expectedVisible) {
		log.info("Verifying 'Supersede Copy' stamp. Expected visible: [{}]", expectedVisible);
		FrameLocator reportFrame = page.frameLocator("//iframe[@class='z-iframe']");

		Locator stampLoc = reportFrame.locator("//div[contains(@class,'stamp') and (contains(text(),'Supersede') or contains(text(),'Superseded'))] | //*[contains(text(),'Supersede Copy')]");
		boolean isDisplayed = false;
		try {
			isDisplayed = stampLoc.count() > 0 && stampLoc.first().isVisible();
		} catch (Exception ignored) {
		}

		if (expectedVisible) {
			Assert.assertTrue(isDisplayed, "Expected 'Supersede Copy' stamp to be visible on Report, but it was not found.");
			log.info("✅ 'Supersede Copy' stamp verified successfully.");
		} else {
			Assert.assertFalse(isDisplayed, "Expected 'Supersede Copy' stamp NOT to be displayed, but it was visible.");
			log.info("✅ Verified 'Supersede Copy' stamp is NOT displayed.");
		}
	}

	/**
	 * Verifies that the report contains header details and content sections.
	 *
	 * @param docNo       SOP Document Number
	 * @param description Description / Title
	 */
	public void verifyReportContent(String docNo, String description) {
		log.info("Verifying report text contains Doc No: [{}] and Description: [{}]", docNo, description);
		FrameLocator reportFrame = page.frameLocator("//iframe[@class='z-iframe']");
		String bodyText = reportFrame.locator("body").innerText();

		Assert.assertTrue(bodyText.contains(docNo), "Report body does not contain SOP Doc No: " + docNo);
		if (description != null && !description.isEmpty()) {
			Assert.assertTrue(bodyText.contains(description), "Report body does not contain Description: " + description);
		}
		log.info("✅ Report text verified successfully.");
	}

	/**
	 * Simulates clicking the Print button and dismissing/cancelling the print dialog.
	 */
	public void clickPrintAndCancel() {
		log.info("Clicking Print button on Report toolbar...");
		if (printButton.count() > 0 && printButton.first().isVisible()) {
			printButton.first().click();
			page.waitForTimeout(1000);
		}
		// Cancel any modal printer popup if displayed
		Locator cancelBtn = page.locator("//div[contains(@class,'z-window-highlighted') or contains(@class,'z-window-modal')]//button[contains(.,'Cancel')]");
		if (cancelBtn.count() > 0 && cancelBtn.first().isVisible()) {
			cancelBtn.first().click();
			page.waitForTimeout(500);
			log.info("Cancelled printer popup dialog.");
		}
	}

	/**
	 * Closes the Report: SOP tab and returns to the previous tab.
	 */
	public void closeReportTab() {
		log.info("Closing/Exiting Report: SOP view to return to SOP Master window...");
		try {
			// 1. Try close button on Report tab
			Locator closeIcons = page.locator(
					"//li[@title='Report: SOP']//i[@aria-label='Close']");

			boolean clicked = false;
			for (Locator loc : closeIcons.all()) {
				try {
					if (loc.isVisible()) {
						loc.click(new Locator.ClickOptions().setForce(true));
						clicked = true;
						log.info("Clicked report tab close icon successfully.");
						break;
					}
				} catch (Exception ignored) {
				}
			}

			page.waitForTimeout(1000);
			WaitUtils.waitForLoadingIndicatorToDisappear(page);

			// 2. If report tab is still present, dispatch ZK onClose event
			try {
				page.evaluate("() => { " +
						"let tab = Array.from(document.querySelectorAll('li.z-tab')).find(el => el.innerText.includes('Report')); " +
						"if (tab) { " +
						"  let btn = tab.querySelector('.z-tab-remove, .z-tab-close, [aria-label=\"Close\"], [class*=\"icon-times\"], [class*=\"z-tab-icon\"]'); " +
						"  if (btn) btn.click(); " +
						"  else if (window.zk) { let w = zk.Widget.$(tab); if (w) { try { w.close(); } catch(e){} try { zAu.send(new zk.Event(w, 'onClose')); } catch(e){} } } " +
						"} " +
						"}");
				page.waitForTimeout(1000);
				WaitUtils.waitForLoadingIndicatorToDisappear(page);
			} catch (Exception ignored) {
			}

			// 3. Switch to SOP Master window tab if it exists and is not selected
			Locator sopTab = page.locator("//li[contains(@class,'z-tab') and contains(.,'SOP Master')]");
			if (sopTab.count() > 0 && sopTab.first().isVisible()) {
				sopTab.first().click();
				page.waitForTimeout(1000);
				WaitUtils.waitForLoadingIndicatorToDisappear(page);
			}
		} catch (Exception e) {
			log.warn("Exception closing report tab: {}", e.getMessage());
		}
	}
	
	/**
	 * Verifies that header, content, and footer do not overlap across all pages of the report.
	 */
	public void verifyNoSectionOverlap() {
		log.info("Verifying that header, content, and footer do not overlap across report pages...");
		Locator iframeLoc = page.locator("//iframe[@class='z-iframe']").first();
		try {
			iframeLoc.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.ATTACHED).setTimeout(15000));
		} catch (Exception e) {
			log.warn("Report iframe wait timed out: {}", e.getMessage());
			Assert.fail("Report iframe not found for section overlap verification.");
		}

		String jsScript =
			"(iframe) => {\n" +
			"  const doc = iframe.contentDocument || (iframe.contentWindow && iframe.contentWindow.document);\n" +
			"  if (!doc) return 'ERROR: Unable to access iframe document';\n" +
			"  const pages = Array.from(doc.querySelectorAll('.pagedjs_page, [data-page-number]'));\n" +
			"  if (pages.length === 0) return '';\n" +
			"  const overlaps = [];\n" +
			"  pages.forEach((pageEl, idx) => {\n" +
			"    const pageNum = pageEl.getAttribute('data-page-number') || (idx + 1);\n" +
			"    const header = pageEl.querySelector('.pagedjs_margin-top');\n" +
			"    const content = pageEl.querySelector('.pagedjs_area');\n" +
			"    const footer = pageEl.querySelector('.pagedjs_margin-bottom');\n" +
			"    const hBox = header ? header.getBoundingClientRect() : null;\n" +
			"    const cBox = content ? content.getBoundingClientRect() : null;\n" +
			"    const fBox = footer ? footer.getBoundingClientRect() : null;\n" +
			"    if (hBox && cBox && hBox.bottom > (cBox.top + 1)) {\n" +
			"      overlaps.push('Page ' + pageNum + ': Header/Content overlap by ' + (hBox.bottom - cBox.top).toFixed(2) + 'px');\n" +
			"    }\n" +
			"    if (cBox && fBox && cBox.bottom > (fBox.top + 1)) {\n" +
			"      overlaps.push('Page ' + pageNum + ': Content/Footer overlap by ' + (cBox.bottom - fBox.top).toFixed(2) + 'px');\n" +
			"    }\n" +
			"  });\n" +
			"  return overlaps.join('; ');\n" +
			"}";

		String result = (String) iframeLoc.evaluate(jsScript);
		if (result != null && !result.trim().isEmpty()) {
			log.error("❌ Section overlap detected: {}", result);
			Assert.fail("Section overlap detected in report: " + result);
		}
		log.info("✅ Verified: Header, content, and footer do not overlap across report pages.");
	}

	/**
	 * Verifies that text elements inside the report do not overlap with each other.
	 */
	public void verifyNoTextOverlap() {
		log.info("Verifying that texts do not overlap with each other across report pages...");
		Locator iframeLoc = page.locator("//iframe[@class='z-iframe']").first();
		try {
			iframeLoc.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.ATTACHED).setTimeout(15000));
		} catch (Exception e) {
			log.warn("Report iframe wait timed out: {}", e.getMessage());
			Assert.fail("Report iframe not found for text overlap verification.");
		}

		String jsScript =
			"(iframe) => {\n" +
			"  const doc = iframe.contentDocument || (iframe.contentWindow && iframe.contentWindow.document);\n" +
			"  if (!doc) return 'ERROR: Unable to access iframe document';\n" +
			"  const pages = Array.from(doc.querySelectorAll('.pagedjs_page, [data-page-number]'));\n" +
			"  if (pages.length === 0) return '';\n" +
			"  const overlaps = [];\n" +
			"  pages.forEach((pageEl, idx) => {\n" +
			"    const pageNum = pageEl.getAttribute('data-page-number') || (idx + 1);\n" +
			"    const walker = doc.createTreeWalker(pageEl, NodeFilter.SHOW_TEXT, {\n" +
			"      acceptNode: function(node) {\n" +
			"        if (!node.textContent || !node.textContent.trim()) return NodeFilter.FILTER_REJECT;\n" +
			"        const parent = node.parentElement;\n" +
			"        if (!parent) return NodeFilter.FILTER_REJECT;\n" +
			"        const style = window.getComputedStyle(parent);\n" +
			"        if (style.display === 'none' || style.visibility === 'hidden' || style.opacity === '0') return NodeFilter.FILTER_REJECT;\n" +
			"        return NodeFilter.FILTER_ACCEPT;\n" +
			"      }\n" +
			"    });\n" +
			"    const textItems = [];\n" +
			"    let textNode;\n" +
			"    while ((textNode = walker.nextNode())) {\n" +
			"      const range = doc.createRange();\n" +
			"      range.selectNodeContents(textNode);\n" +
			"      const rects = Array.from(range.getClientRects());\n" +
			"      for (const r of rects) {\n" +
			"        if (r.width > 1 && r.height > 1) {\n" +
			"          textItems.push({\n" +
			"            text: textNode.textContent.trim(),\n" +
			"            rect: { top: r.top, bottom: r.bottom, left: r.left, right: r.right },\n" +
			"            node: textNode\n" +
			"          });\n" +
			"        }\n" +
			"      }\n" +
			"    }\n" +
			"    for (let i = 0; i < textItems.length; i++) {\n" +
			"      for (let j = i + 1; j < textItems.length; j++) {\n" +
			"        const a = textItems[i];\n" +
			"        const b = textItems[j];\n" +
			"        if (a.node === b.node) continue;\n" +
			"        const xOverlap = Math.max(0, Math.min(a.rect.right, b.rect.right) - Math.max(a.rect.left, b.rect.left));\n" +
			"        const yOverlap = Math.max(0, Math.min(a.rect.bottom, b.rect.bottom) - Math.max(a.rect.top, b.rect.top));\n" +
			"        if (xOverlap > 3 && yOverlap > 3) {\n" +
			"          const snippetA = a.text.length > 25 ? a.text.substring(0, 25) + '...' : a.text;\n" +
			"          const snippetB = b.text.length > 25 ? b.text.substring(0, 25) + '...' : b.text;\n" +
			"          overlaps.push('Page ' + pageNum + ': Text [\"' + snippetA + '\"] overlaps with [\"' + snippetB + '\"] (' + xOverlap.toFixed(1) + 'x' + yOverlap.toFixed(1) + 'px)');\n" +
			"        }\n" +
			"      }\n" +
			"    }\n" +
			"  });\n" +
			"  return overlaps.join('; ');\n" +
			"}";

		String result = (String) iframeLoc.evaluate(jsScript);
		if (result != null && !result.trim().isEmpty()) {
			log.error("❌ Text overlap detected: {}", result);
			Assert.fail("Text overlap detected in report: " + result);
		}
		log.info("✅ Verified: Texts do not overlap with each other across report pages.");
	}

	public void verifyPrintReport() {
		verifyReportOpened();
		verifyMasterCopyStamp(false);
		verifyNoSectionOverlap();
		verifyNoTextOverlap();
		closeReportTab();
	}
}
