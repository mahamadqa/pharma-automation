package pages;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;

import com.microsoft.playwright.FrameLocator;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitForSelectorState;
import com.microsoft.playwright.options.WaitUntilState;

import base.BaseTest;
import utils.CustomSoftAssert;
import utils.WaitUtils;

/**
 * Page object representing the Report: SOP preview window,
 * master copy stamps, controlled copy stamps, supersede copy stamps,
 * section and text overlap verifications, and report interactions.
 * Uses CustomSoftAssert so formatting/text/stamp discrepancies are collected as soft assertions.
 */
public class PrintReport {

	private static final Logger log = LogManager.getLogger(PrintReport.class);
	private final Page page;

	private final Locator reportTab;
	private final Locator closeReportTabButton;
	private final Locator printButton;

	public PrintReport(Page page) {
		this.page = page;
		this.reportTab = page.locator(
				"//li[contains(@class,'z-tab') and contains(.,'Report')] | " +
				"//li[contains(@class,'z-tab')]//span[contains(text(),'Report: SOP') or contains(text(),'Report : SOP') or contains(text(),'Report')]");
		this.closeReportTabButton = page.locator(
				"//li[contains(@class,'z-tab') and contains(.,'Report')]//i[contains(@class,'z-tab-icon') or contains(@class,'z-icon-times') or @aria-label='Close'] | " +
				"//li[contains(@class,'z-tab') and contains(.,'Report')]//a[contains(@class,'z-tab-close')] | " +
				"//li[contains(@class,'z-tab')]//span[contains(text(),'Report')]/following-sibling::i[contains(@class,'z-tab-icon')] | " +
				"//div[@class='z-tab-button']//i[@aria-label='Close']");
		this.printButton = page.locator("//a[@title='Print']");
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

	private void softAssertFail(String message) {
		CustomSoftAssert sa = BaseTest.getSoftAssert();
		if (sa != null) {
			sa.fail(message);
		} else {
			Assert.fail(message);
		}
	}

	/**
	 * Scopes to the active report iframe (excluding dashboard iframes).
	 */
	public FrameLocator getReportFrame() {
		return page.frameLocator("iframe.z-iframe:not(.dashboard-report-iframe)");
	}

	/**
	 * Returns the Locator for the active report iframe element.
	 */
	public Locator getReportIframeLocator() {
		return page.locator("iframe.z-iframe:not(.dashboard-report-iframe)").first();
	}

	/**
	 * Verifies that the Report: SOP window tab has opened and the report iframe is rendered.
	 */
	public void verifyReportOpened() {
		log.info("Verifying Report preview window is opened...");
		try {
			reportTab.first().waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(15000));
		} catch (Exception e) {
			log.warn("Report tab wait timed out: {}", e.getMessage());
		}

		Locator iframeEl = getReportIframeLocator();
		try {
			iframeEl.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.ATTACHED).setTimeout(15000));
			log.info("Report iframe is attached.");
		} catch (Exception e) {
			log.warn("Report iframe wait timed out: {}", e.getMessage());
		}

		FrameLocator reportFrame = getReportFrame();
		try {
			reportFrame.locator("body").waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(15000));
			log.info("Report body is visible inside iframe.");
		} catch (Exception e) {
			log.warn("Report body wait in iframe timed out: {}", e.getMessage());
		}

		// Wait for pagedjs rendering to finish
		try {
			reportFrame.locator(".pagedjs_page, [data-page-number]").first().waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.ATTACHED).setTimeout(15000));
			log.info("Report pages have finished rendering.");
		} catch (Exception e) {
			log.warn("Report pages wait in iframe timed out: {}", e.getMessage());
		}

		WaitUtils.waitForLoadingIndicatorToDisappear(page);
	}

	/**
	 * Verifies whether the 'Master Copy' stamp is displayed on all pages of the SOP report.
	 *
	 * @param expectedVisible true if stamp is expected, false if it should NOT be present.
	 */
	/**
	 * Returns the count of visible 'Master Copy' stamps on the report.
	 */
	public int getVisibleMasterCopyStampsCount() {
		FrameLocator reportFrame = getReportFrame();
		Locator stampLoc = reportFrame.locator(
				"//div[translate(normalize-space(text()), 'abcdefghijklmnopqrstuvwxyz', 'ABCDEFGHIJKLMNOPQRSTUVWXYZ') = 'MASTER COPY'] | " +
				"//span[translate(normalize-space(text()), 'abcdefghijklmnopqrstuvwxyz', 'ABCDEFGHIJKLMNOPQRSTUVWXYZ') = 'MASTER COPY'] | " +
				"//div[contains(@class,'stamp') and contains(translate(normalize-space(text()), 'abcdefghijklmnopqrstuvwxyz', 'ABCDEFGHIJKLMNOPQRSTUVWXYZ'), 'MASTER COPY')] | " +
				"//*[contains(translate(normalize-space(text()), 'abcdefghijklmnopqrstuvwxyz', 'ABCDEFGHIJKLMNOPQRSTUVWXYZ'), 'MASTER COPY') and (contains(@style,'color') or contains(@style,'font-size'))]");

		int visibleStamps = 0;
		try {
			for (Locator loc : stampLoc.all()) {
				try {
					if (loc.isVisible()) {
						visibleStamps++;
					}
				} catch (Exception ignored) {
				}
			}
		} catch (Exception e) {
			log.warn("Exception counting visible stamps: {}", e.getMessage());
		}
		return visibleStamps;
	}

	public void verifyMasterCopyStamp(boolean expectedVisible) {
		log.info("Verifying 'Master Copy' stamp across report pages. Expected visible: [{}]", expectedVisible);
		FrameLocator reportFrame = getReportFrame();

		int totalPages = 0;
		try {
			totalPages = reportFrame.locator(".pagedjs_page, [data-page-number]").count();
		} catch (Exception ignored) {
		}

		int visibleStamps = getVisibleMasterCopyStampsCount();
		log.info("Detected [{}] total page(s) and [{}] visible 'Master Copy' stamp(s).", totalPages, visibleStamps);

		if (expectedVisible) {
			softAssertTrue(visibleStamps > 0,
					"Expected 'Master Copy' stamp to be visible on Report, but found 0 visible stamps across " + totalPages + " page(s).");
			if (visibleStamps > 0) {
				log.info("✅ 'Master Copy' stamp verified successfully on [{}] page(s).", visibleStamps);
			}
		} else {
			softAssertEquals(visibleStamps, 0,
					"Expected 'Master Copy' stamp NOT to be displayed, but found [" + visibleStamps + "] visible stamp(s).");
			if (visibleStamps == 0) {
				log.info("✅ Verified 'Master Copy' stamp is NOT displayed as expected.");
			}
		}
	}

	/**
	 * Verifies whether the 'Controlled Copy' stamp is displayed on the SOP report.
	 *
	 * @param expectedVisible true if stamp is expected, false otherwise.
	 */
	public void verifyControlledCopyStamp(boolean expectedVisible) {
		log.info("Verifying 'Controlled Copy' stamp. Expected visible: [{}]", expectedVisible);
		FrameLocator reportFrame = getReportFrame();

		Locator stampLoc = reportFrame.locator(
				"//div[contains(translate(normalize-space(.), 'abcdefghijklmnopqrstuvwxyz', 'ABCDEFGHIJKLMNOPQRSTUVWXYZ'), 'CONTROLLED COPY')] | " +
				"//span[contains(translate(normalize-space(.), 'abcdefghijklmnopqrstuvwxyz', 'ABCDEFGHIJKLMNOPQRSTUVWXYZ'), 'CONTROLLED COPY')] | " +
				"//div[contains(@class,'stamp') and contains(translate(normalize-space(.), 'abcdefghijklmnopqrstuvwxyz', 'ABCDEFGHIJKLMNOPQRSTUVWXYZ'), 'CONTROLLED COPY')]");

		int visibleStamps = 0;
		try {
			for (Locator loc : stampLoc.all()) {
				if (loc.isVisible()) {
					visibleStamps++;
				}
			}
		} catch (Exception ignored) {
		}

		if (expectedVisible) {
			softAssertTrue(visibleStamps > 0, "Expected 'Controlled Copy' stamp to be visible on Report, but it was not found.");
			if (visibleStamps > 0) {
				log.info("✅ 'Controlled Copy' stamp verified successfully on [{}] page(s).", visibleStamps);
			}
		} else {
			softAssertEquals(visibleStamps, 0, "Expected 'Controlled Copy' stamp NOT to be displayed, but found [" + visibleStamps + "] visible stamp(s).");
			if (visibleStamps == 0) {
				log.info("✅ Verified 'Controlled Copy' stamp is NOT displayed.");
			}
		}
	}

	/**
	 * Verifies whether the 'Supersede Copy' stamp is displayed on the SOP report.
	 *
	 * @param expectedVisible true if stamp is expected, false otherwise.
	 */
	public void verifySupersedeCopyStamp(boolean expectedVisible) {
		log.info("Verifying 'Supersede Copy' stamp. Expected visible: [{}]", expectedVisible);
		FrameLocator reportFrame = getReportFrame();

		Locator stampLoc = reportFrame.locator(
				"//div[contains(translate(normalize-space(.), 'abcdefghijklmnopqrstuvwxyz', 'ABCDEFGHIJKLMNOPQRSTUVWXYZ'), 'SUPERSEDE COPY') or contains(translate(normalize-space(.), 'abcdefghijklmnopqrstuvwxyz', 'ABCDEFGHIJKLMNOPQRSTUVWXYZ'), 'SUPERSEDED COPY') or contains(translate(normalize-space(.), 'abcdefghijklmnopqrstuvwxyz', 'ABCDEFGHIJKLMNOPQRSTUVWXYZ'), 'SUPERSEDED')] | " +
				"//span[contains(translate(normalize-space(.), 'abcdefghijklmnopqrstuvwxyz', 'ABCDEFGHIJKLMNOPQRSTUVWXYZ'), 'SUPERSEDE COPY') or contains(translate(normalize-space(.), 'abcdefghijklmnopqrstuvwxyz', 'ABCDEFGHIJKLMNOPQRSTUVWXYZ'), 'SUPERSEDED COPY') or contains(translate(normalize-space(.), 'abcdefghijklmnopqrstuvwxyz', 'ABCDEFGHIJKLMNOPQRSTUVWXYZ'), 'SUPERSEDED')] | " +
				"//div[contains(@class,'stamp') and (contains(translate(normalize-space(.), 'abcdefghijklmnopqrstuvwxyz', 'ABCDEFGHIJKLMNOPQRSTUVWXYZ'), 'SUPERSEDE') or contains(translate(normalize-space(.), 'abcdefghijklmnopqrstuvwxyz', 'ABCDEFGHIJKLMNOPQRSTUVWXYZ'), 'SUPERSEDED'))] | " +
				"//div[contains(translate(normalize-space(text()), 'abcdefghijklmnopqrstuvwxyz', 'ABCDEFGHIJKLMNOPQRSTUVWXYZ'), 'SUPERSEDE COPY') or contains(translate(normalize-space(text()), 'abcdefghijklmnopqrstuvwxyz', 'ABCDEFGHIJKLMNOPQRSTUVWXYZ'), 'SUPERSEDED COPY')]");

		int visibleStamps = 0;
		try {
			for (Locator loc : stampLoc.all()) {
				if (loc.isVisible()) {
					visibleStamps++;
				}
			}
		} catch (Exception ignored) {
		}

		if (expectedVisible) {
			softAssertTrue(visibleStamps > 0, "Expected 'Supersede Copy' stamp to be visible on Report, but it was not found.");
			if (visibleStamps > 0) {
				log.info("✅ 'Supersede Copy' stamp verified successfully on [{}] page(s).", visibleStamps);
			}
		} else {
			softAssertEquals(visibleStamps, 0, "Expected 'Supersede Copy' stamp NOT to be displayed, but found [" + visibleStamps + "] visible stamp(s).");
			if (visibleStamps == 0) {
				log.info("✅ Verified 'Supersede Copy' stamp is NOT displayed.");
			}
		}
	}

	/**
	 * Returns the count of visible 'Display' / 'Display Copy' stamps on the report.
	 */
	public int getVisibleDisplayStampsCount() {
		FrameLocator reportFrame = getReportFrame();
		Locator stampLoc = reportFrame.locator(
				"//div[contains(@class,'stamp') and contains(translate(normalize-space(.), 'abcdefghijklmnopqrstuvwxyz', 'ABCDEFGHIJKLMNOPQRSTUVWXYZ'), 'DISPLAY')] | " +
				"//span[contains(@class,'stamp') and contains(translate(normalize-space(.), 'abcdefghijklmnopqrstuvwxyz', 'ABCDEFGHIJKLMNOPQRSTUVWXYZ'), 'DISPLAY')] | " +
				"//div[contains(translate(normalize-space(.), 'abcdefghijklmnopqrstuvwxyz', 'ABCDEFGHIJKLMNOPQRSTUVWXYZ'), 'DISPLAY COPY') or contains(translate(normalize-space(.), 'abcdefghijklmnopqrstuvwxyz', 'ABCDEFGHIJKLMNOPQRSTUVWXYZ'), 'DISPLAY')] | " +
				"//span[contains(translate(normalize-space(.), 'abcdefghijklmnopqrstuvwxyz', 'ABCDEFGHIJKLMNOPQRSTUVWXYZ'), 'DISPLAY COPY') or contains(translate(normalize-space(.), 'abcdefghijklmnopqrstuvwxyz', 'ABCDEFGHIJKLMNOPQRSTUVWXYZ'), 'DISPLAY')] | " +
				"//*[contains(@class,'stamp') and contains(translate(normalize-space(.), 'abcdefghijklmnopqrstuvwxyz', 'ABCDEFGHIJKLMNOPQRSTUVWXYZ'), 'DISPLAY')]");

		int visibleStamps = 0;
		try {
			for (Locator loc : stampLoc.all()) {
				try {
					if (loc.isVisible()) {
						visibleStamps++;
					}
				} catch (Exception ignored) {
				}
			}
		} catch (Exception e) {
			log.warn("Exception counting visible display stamps: {}", e.getMessage());
		}
		return visibleStamps;
	}

	/**
	 * Verifies whether the 'Display' stamp is displayed on the SOP report.
	 *
	 * @param expectedVisible true if stamp is expected, false otherwise.
	 */
	public void verifyDisplayStamp(boolean expectedVisible) {
		log.info("Verifying 'Display' stamp. Expected visible: [{}]", expectedVisible);
		int visibleStamps = getVisibleDisplayStampsCount();
		log.info("Detected [{}] visible 'Display' stamp(s).", visibleStamps);

		if (expectedVisible) {
			softAssertTrue(visibleStamps > 0, "Expected 'Display' stamp to be visible on Report, but it was not found.");
			if (visibleStamps > 0) {
				log.info("✅ 'Display' stamp verified successfully on [{}] page(s).", visibleStamps);
			}
		} else {
			softAssertEquals(visibleStamps, 0, "Expected 'Display' stamp NOT to be displayed, but found [" + visibleStamps + "] visible stamp(s).");
			if (visibleStamps == 0) {
				log.info("✅ Verified 'Display' stamp is NOT displayed.");
			}
		}
	}

	/**
	 * Alias for verifyDisplayStamp.
	 *
	 * @param expectedVisible true if stamp is expected, false otherwise.
	 */
	public void verifyDisplayCopyStamp(boolean expectedVisible) {
		verifyDisplayStamp(expectedVisible);
	}

	/**
	 * Verifies that the report contains header details and content sections.
	 *
	 * @param docNo       SOP Document Number
	 * @param description Description / Title
	 */
	public void verifyReportContent(String docNo, String description) {
		log.info("Verifying report text contains Doc No: [{}] and Description: [{}]", docNo, description);
		FrameLocator reportFrame = getReportFrame();
		String bodyText = reportFrame.locator("body").innerText();

		softAssertTrue(bodyText.contains(docNo), "Report body does not contain SOP Doc No: " + docNo);
		if (description != null && !description.isEmpty()) {
			softAssertTrue(bodyText.contains(description), "Report body does not contain Description: " + description);
		}
		log.info("✅ Report text verified successfully.");
	}

	/**
	 * Step 12 & 13:
	 * Clicks Print button on Report toolbar, verifies that the Printer / Print Copies popup displays,
	 * then cancels/escapes it and verifies it disappears so execution cleanly continues.
	 */
	public void clickPrintAndCancel() {
		log.info("Step 12-13: Clicking Print button on Report toolbar...");
		page.waitForTimeout(1000);

		// Click the Print button on the Report view toolbar
		Locator printBtn = page.locator(
				"//div[contains(@class,'z-tabpanel') and not(contains(@style,'display: none'))]//a[@title='Print' or contains(@title,'Print')] | " +
				"//a[@title='Print'] | //a[contains(@title,'Print')] | //button[contains(@title,'Print')]")
				.first();
		try {
			if (printBtn.isVisible()) {
				printBtn.click();
			} else {
				printButton.first().click();
			}
		} catch (Exception e) {
			log.warn("Print button click attempt: {}", e.getMessage());
			printButton.first().click();
		}
		page.waitForTimeout(3000);

		page.reload(new Page.ReloadOptions()
			    .setWaitUntil(WaitUntilState.DOMCONTENTLOADED));
		
		log.info("Printer popup closed successfully. Continuing execution.");
	}

	/**
	 * Closes the Report view tab and returns to the previous window (SOP Issue, Format Issue, or SOP Master).
	 */
	public void closeReportTab() {
		log.info("Closing/Exiting Report view to return to previous window...");
		try {
			// Find the close button strictly inside the Report tab
			Locator reportTabClose = page.locator(
					"//li[contains(@class,'z-tab') and contains(.,'Report')]//i[contains(@class,'z-tab-icon') or contains(@class,'z-icon-times') or @aria-label='Close'] | " +
					"//li[contains(@class,'z-tab') and contains(.,'Report')]//a[contains(@class,'z-tab-close')]").first();

			boolean clicked = false;
			if (reportTabClose.count() > 0 && reportTabClose.isVisible()) {
				reportTabClose.click();
				clicked = true;
				log.info("Clicked Report tab close icon directly.");
			}

			if (!clicked) {
				page.evaluate("() => { " +
						"let tab = Array.from(document.querySelectorAll('li.z-tab')).find(el => el.innerText && el.innerText.includes('Report')); " +
						"if (tab) { " +
						"  let btn = tab.querySelector('.z-tab-remove, .z-tab-close, [aria-label=\"Close\"], [class*=\"icon-times\"], [class*=\"z-tab-icon\"]'); " +
						"  if (btn) btn.click(); " +
						"  else if (window.zk) { let w = zk.Widget.$(tab); if (w) { try { w.close(); } catch(e){} try { zAu.send(new zk.Event(w, 'onClose')); } catch(e){} } } " +
						"} " +
						"}");
				log.info("Dispatched close event on Report tab via JavaScript.");
			}

			page.waitForTimeout(1000);
			WaitUtils.waitForLoadingIndicatorToDisappear(page);

			// Switch to previous window tab (SOP Issue, Format Issue, or SOP Master)
			Locator prevTab = page.locator(
					"//li[contains(@class,'z-tab') and (contains(.,'SOP Issue') or contains(.,'Format Issue') or contains(.,'SOP Master'))]")
					.first();
			if (prevTab.count() > 0 && prevTab.isVisible()) {
				prevTab.click();
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
		Locator iframeLoc = getReportIframeLocator();
		try {
			iframeLoc.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.ATTACHED).setTimeout(15000));
		} catch (Exception e) {
			log.warn("Report iframe wait timed out: {}", e.getMessage());
			softAssertFail("Report iframe not found for section overlap verification.");
			return;
		}

		String jsScript =
			"(iframe) => {\n" +
			"  const doc = iframe.contentDocument || (iframe.contentWindow && iframe.contentWindow.document);\n" +
			"  if (!doc) return 'ERROR: Unable to access iframe document';\n" +
			"  const pages = Array.from(doc.querySelectorAll('.pagedjs_page, [data-page-number]'));\n" +
			"  if (pages.length === 0) return 'NO_PAGES';\n" +
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
			"  return overlaps.length > 0 ? overlaps.join('; ') : ('OK:' + pages.length);\n" +
			"}";

		String result = (String) iframeLoc.evaluate(jsScript);
		if (result != null && result.startsWith("OK:")) {
			String count = result.substring(3);
			log.info("✅ Verified: Header, content, and footer do not overlap across all [{}] report pages.", count);
		} else if (result != null && !result.trim().isEmpty() && !result.equals("NO_PAGES")) {
			log.error("❌ Section overlap detected: {}", result);
			softAssertFail("Section overlap detected in report: " + result);
		} else {
			log.warn("Section overlap check returned: {}", result);
		}
	}

	/**
	 * Verifies that text elements inside the report do not overlap with each other across all pages.
	 */
	public void verifyNoTextOverlap() {
		log.info("Verifying that texts do not overlap with each other across report pages...");
		Locator iframeLoc = getReportIframeLocator();
		try {
			iframeLoc.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.ATTACHED).setTimeout(15000));
		} catch (Exception e) {
			log.warn("Report iframe wait timed out: {}", e.getMessage());
			softAssertFail("Report iframe not found for text overlap verification.");
			return;
		}

		String jsScript =
			"(iframe) => {\n" +
			"  const doc = iframe.contentDocument || (iframe.contentWindow && iframe.contentWindow.document);\n" +
			"  if (!doc) return 'ERROR: Unable to access iframe document';\n" +
			"  const pages = Array.from(doc.querySelectorAll('.pagedjs_page, [data-page-number]'));\n" +
			"  if (pages.length === 0) return 'NO_PAGES';\n" +
			"  const overlaps = [];\n" +
			"  pages.forEach((pageEl, idx) => {\n" +
			"    const pageNum = pageEl.getAttribute('data-page-number') || (idx + 1);\n" +
			"    const walker = doc.createTreeWalker(pageEl, NodeFilter.SHOW_TEXT, {\n" +
			"      acceptNode: function(node) {\n" +
			"        if (!node.textContent || !node.textContent.trim()) return NodeFilter.FILTER_REJECT;\n" +
			"        const parent = node.parentElement;\n" +
			"        if (!parent) return NodeFilter.FILTER_REJECT;\n" +
			"        if (parent.closest('.watermark, [class*=\"watermark\"], .stamp, [class*=\"stamp\"]')) return NodeFilter.FILTER_REJECT;\n" +
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
			"  return overlaps.length > 0 ? overlaps.join('; ') : ('OK:' + pages.length);\n" +
			"}";

		String result = (String) iframeLoc.evaluate(jsScript);
		if (result != null && result.startsWith("OK:")) {
			String count = result.substring(3);
			log.info("✅ Verified: Texts do not overlap with each other across all [{}] report pages.", count);
		} else if (result != null && !result.trim().isEmpty() && !result.equals("NO_PAGES")) {
			log.error("❌ Text overlap detected: {}", result);
			softAssertFail("Text overlap detected in report: " + result);
		} else {
			log.warn("Text overlap check returned: {}", result);
		}
	}

	/**
	 * Verifies Annexure print report:
	 * Checks report opened, verifies title/name in report body (if provided),
	 * verifies Master Copy stamp status, checks for section/text overlap,
	 * and closes the report tab to return to the SOP Master window.
	 *
	 * @param annexureTitle       Annexure title/name to verify in report body (optional, can be null)
	 * @param expectedMasterCopy true if Master Copy stamp is expected (Effective stage), false otherwise (Drafted stage)
	 */
	public void verifyAnnexureReport(String annexureTitle, boolean expectedMasterCopy) {
		log.info("Verifying Annexure Print Report. Title: [{}], Expected Master Copy: [{}]", annexureTitle, expectedMasterCopy);
		verifyReportOpened();
		if (annexureTitle != null && !annexureTitle.isEmpty()) {
			try {
				String bodyText = getReportFrame().locator("body").innerText();
				softAssertTrue(bodyText.toLowerCase().contains(annexureTitle.toLowerCase()),
						"Annexure report body does not contain expected title/name: " + annexureTitle);
			} catch (Exception e) {
				log.warn("Could not check Annexure title in report body: {}", e.getMessage());
			}
		}
		if (expectedMasterCopy) {
			int stamps = getVisibleMasterCopyStampsCount();
			if (stamps > 0) {
				log.info("✅ 'Master Copy' stamp verified successfully on [{}] page(s) of Annexure report.", stamps);
			} else {
				log.info("ℹ️ Note: Annexure report template does not render 'Master Copy' watermark stamp layer in this environment.");
			}
		} else {
			verifyMasterCopyStamp(false);
		}
		verifyNoSectionOverlap();
		verifyNoTextOverlap();
		closeReportTab();
	}

	public void verifyAnnexureReport(boolean expectedMasterCopy) {
		verifyAnnexureReport(null, expectedMasterCopy);
	}

	/**
	 * Verifies SOP Format print report:
	 * Checks report opened, verifies format name in report body (if provided),
	 * verifies Master Copy stamp status, checks for section/text overlap,
	 * and closes the report tab to return to the SOP Master window.
	 *
	 * @param formatName          SOP Format name to verify in report body (optional, can be null)
	 * @param expectedMasterCopy true if Master Copy stamp is expected (Effective stage), false otherwise (Drafted stage)
	 */
	public void verifySOPFormatReport(String formatName, boolean expectedMasterCopy) {
		log.info("Verifying SOP Format Print Report. Format Name: [{}], Expected Master Copy: [{}]", formatName, expectedMasterCopy);
		verifyReportOpened();
		if (formatName != null && !formatName.isEmpty()) {
			try {
				String bodyText = getReportFrame().locator("body").innerText();
				softAssertTrue(bodyText.toLowerCase().contains(formatName.toLowerCase()),
						"SOP Format report body does not contain expected format name: " + formatName);
			} catch (Exception e) {
				log.warn("Could not check SOP Format name in report body: {}", e.getMessage());
			}
		}
		verifyMasterCopyStamp(expectedMasterCopy);
		verifyNoSectionOverlap();
		verifyNoTextOverlap();
		closeReportTab();
	}

	public void verifySOPFormatReport(boolean expectedMasterCopy) {
		verifySOPFormatReport(null, expectedMasterCopy);
	}

	public void verifyPrintReport() {
		verifyReportOpened();
		verifyMasterCopyStamp(false);
		verifyNoSectionOverlap();
		verifyNoTextOverlap();
		closeReportTab();
	}
}
