package pages;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.BoundingBox;
import com.microsoft.playwright.options.WaitForSelectorState;

public class HTMLReportsPreview {
	private static final Logger log = LogManager.getLogger(HTMLReportsPreview.class);
	private Page page;

	private String header1 = "(//div[@class='pagedjs_margin-top'])[1]";
	private String Content1 = "(//div[@class='pagedjs_area'])[1]";
	private String Footer1 = "(//div[@class='pagedjs_margin-bottom'])[1]";

	private String header2 = "(//div[@class='pagedjs_margin-top'])[2]";
	private String Content2 = "(//div[@class='pagedjs_area'])[2]";

	public HTMLReportsPreview(Page page) {
		this.page = page;
	}

	public void checkOverlapIssue() {
		Locator lastPage = page.frameLocator("//iframe[@class='z-iframe']")
				.locator("(//div[@data-page-number])[last()]");
		lastPage.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.ATTACHED));

		int totalPages = Integer.parseInt(lastPage.getAttribute("data-page-number"));

		log.info("Total Pages: " + totalPages);

		for (int i = 1; i <= totalPages; i++) {
			BoundingBox headerBox = page.frameLocator("//iframe[@class='z-iframe']")
					.locator("(//div[@class='pagedjs_margin-top'])[" + i + "]").boundingBox();
			BoundingBox contentBox = page.frameLocator("//iframe[@class='z-iframe']")
					.locator("(//div[@class='pagedjs_area'])[" + i + "]").boundingBox();
			BoundingBox footerBox = page.frameLocator("//iframe[@class='z-iframe']")
					.locator("(//div[@class='pagedjs_margin-bottom'])[" + i + "]").boundingBox();

			if (headerBox == null || contentBox == null || footerBox == null) {
				throw new AssertionError("Unable to get bounding box for Header/Content/Footer");
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

			log.info("====================Page:" + i + "======================");
			log.info("HEADER");
			log.info("Top    : " + headerTop);
			log.info("Bottom : " + headerBottom);

			log.info("CONTENT");
			log.info("Top    : " + contentTop);
			log.info("Bottom : " + contentBottom);

			log.info("FOOTER");
			log.info("Top    : " + footerTop);
			log.info("Bottom : " + footerBottom);

			// Header vs Content
			if (headerBottom > contentTop) {
				double overlap = headerBottom - contentTop;
				log.info("❌ HEADER / CONTENT OVERLAP");
				log.info("Overlap: " + overlap + " px");
				throw new AssertionError("HEADER / CONTENT OVERLAP");

			} else {

				log.info("✅ Header and Content do not overlap");
			}

			// Content vs Footer
			if (contentBottom > footerTop) {
				double overlap = contentBottom - footerTop;
				log.info("❌ CONTENT / FOOTER OVERLAP");
				log.info("Overlap: " + overlap + " px");
				throw new AssertionError("CONTENT / FOOTER OVERLAP");

			} else {

				log.info("✅ Content and Footer do not overlap");
			}
		}

	}

}
