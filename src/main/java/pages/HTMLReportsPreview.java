package pages;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;

import com.microsoft.playwright.FrameLocator;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.BoundingBox;
import com.microsoft.playwright.options.WaitForSelectorState;

public class HTMLReportsPreview {

	private static final Logger log = LogManager.getLogger(HTMLReportsPreview.class);
	private final Page page;

	protected Dashboard dashboard;

	public HTMLReportsPreview(Page page) {
		this(page, null);
	}

	public HTMLReportsPreview(Page page, Dashboard dashboard) {
		this.page = page;
		this.dashboard = dashboard;
	}

	/**
	 * Verifies that header, content, and footer do not overlap across all pages of
	 * the HTML report preview.
	 */
	public void checkOverlapIssue() {
		log.info("Checking for header/content/footer overlap issues in HTML Report Preview...");

		FrameLocator reportFrame = page.frameLocator("//iframe[@class='z-iframe']");
		Locator lastPage = reportFrame.locator("(//div[@data-page-number])[last()]");
		lastPage.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.ATTACHED).setTimeout(15000));

		String pageNumAttr = lastPage.getAttribute("data-page-number");
		Assert.assertNotNull(pageNumAttr, "Failed to retrieve 'data-page-number' from report preview.");

		int totalPages = Integer.parseInt(pageNumAttr.trim());
		log.info("Total pages detected in report: [{}]", totalPages);

		for (int i = 1; i <= totalPages; i++) {
			Locator headerLoc = reportFrame.locator("(//div[@class='pagedjs_margin-top'])[" + i + "]");
			Locator contentLoc = reportFrame.locator("(//div[@class='pagedjs_area'])[" + i + "]");
			Locator footerLoc = reportFrame.locator("(//div[@class='pagedjs_margin-bottom'])[" + i + "]");

			headerLoc.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.ATTACHED).setTimeout(5000));
			contentLoc.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.ATTACHED).setTimeout(5000));
			footerLoc.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.ATTACHED).setTimeout(5000));

			BoundingBox headerBox = headerLoc.boundingBox();
			BoundingBox contentBox = contentLoc.boundingBox();
			BoundingBox footerBox = footerLoc.boundingBox();

			if (headerBox == null || contentBox == null || footerBox == null) {
				log.error("Unable to calculate bounding box for Page [{}]", i);
				Assert.fail("Unable to get bounding box for Header/Content/Footer on Page " + i);
			}

			// Header coordinates
			double headerTop = headerBox.y;
			double headerBottom = headerBox.y + headerBox.height;

			// Content coordinates
			double contentTop = contentBox.y;
			double contentBottom = contentBox.y + contentBox.height;

			// Footer coordinates
			double footerTop = footerBox.y;
			double footerBottom = footerBox.y + footerBox.height;

			// Check Header vs Content overlap
			if (headerBottom > contentTop) {
				double overlap = headerBottom - contentTop;
				log.error("❌ HEADER / CONTENT OVERLAP detected on Page [{}] by {} px", i, overlap);
				Assert.fail("HEADER / CONTENT OVERLAP on Page " + i + " by " + overlap + " px");
			}

			// Check Content vs Footer overlap
			if (contentBottom > footerTop) {
				double overlap = contentBottom - footerTop;
				log.error("❌ CONTENT / FOOTER OVERLAP detected on Page [{}] by {} px", i, overlap);
				Assert.fail("CONTENT / FOOTER OVERLAP on Page " + i + " by " + overlap + " px");
			}
		}

		log.info("✅ HTML Report Preview overlap verification passed for all [{}] pages.", totalPages);
	}

	/**
	 * Verifies that text elements inside the HTML report preview do not overlap with each other.
	 */
	public void checkTextOverlapIssue() {
		log.info("Checking for text overlap issues in HTML Report Preview...");

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
			log.error("❌ Text overlap detected in HTML report preview: {}", result);
			Assert.fail("Text overlap detected in HTML report preview: " + result);
		}
		log.info("✅ HTML Report Preview text overlap verification passed for all pages.");
	}
}
