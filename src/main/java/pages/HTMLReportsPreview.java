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

			log.info("-------------------- Page: [{}] --------------------", i);
			log.info("HEADER  -> Top: {}, Bottom: {}", headerTop, headerBottom);
			log.info("CONTENT -> Top: {}, Bottom: {}", contentTop, contentBottom);
			log.info("FOOTER  -> Top: {}, Bottom: {}", footerTop, footerBottom);

			// Check Header vs Content overlap
			if (headerBottom > contentTop) {
				double overlap = headerBottom - contentTop;
				log.error("❌ HEADER / CONTENT OVERLAP detected on Page [{}] by {} px", i, overlap);
				Assert.fail("HEADER / CONTENT OVERLAP on Page " + i + " by " + overlap + " px");
			} else {
				log.info("✅ Page [{}]: Header and Content do not overlap.", i);
			}

			// Check Content vs Footer overlap
			if (contentBottom > footerTop) {
				double overlap = contentBottom - footerTop;
				log.error("❌ CONTENT / FOOTER OVERLAP detected on Page [{}] by {} px", i, overlap);
				Assert.fail("CONTENT / FOOTER OVERLAP on Page " + i + " by " + overlap + " px");
			} else {
				log.info("✅ Page [{}]: Content and Footer do not overlap.", i);
			}
		}

		log.info("HTML Report Preview overlap verification passed for all [{}] pages.", totalPages);
	}
}
