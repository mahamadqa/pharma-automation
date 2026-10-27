package pages;

import java.io.File;
import java.nio.file.Paths;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitForSelectorState;

import utils.FileUtils;
import utils.WaitUtils;

/**
 * Page object handling the Document Explorer dialog and DMS Content uploading
 * (e.g. Training Completion Report upload for SOP Master).
 */
public class DocumentExplorer {

	private static final Logger log = LogManager.getLogger(DocumentExplorer.class);
	private final Page page;

	private final Locator docExplorerToolbarBtn;
	private final Locator docExplorerDialog;
	private final Locator uploadContentBtn;
	private final Locator uploadContentDialog;
	private final Locator fileInput;
	private final Locator dmsContentTypeInput;
	private final Locator uploadOkBtn;
	private final Locator uploadCloseBtn;
	private final Locator docExplorerCloseBtn;

	public DocumentExplorer(Page page) {
		this.page = page;
		this.docExplorerToolbarBtn = page.locator("//a[@title='Document Explorer']").first();
		this.docExplorerDialog = page.locator("//div[contains(@class,'z-window') and contains(.,'Document Explorer')]").first();
		this.uploadContentBtn = page.locator("//div[contains(@class,'z-window') and contains(.,'Document Explorer')]//button[@title='Upload Content']").first();
		this.uploadContentDialog = page.locator("//div[contains(@class,'z-window') and contains(.,'Upload Content')]").first();
		this.fileInput = page.locator("//div[contains(@class,'z-window') and contains(.,'Upload Content')]//input[@type='file']").first();
		this.dmsContentTypeInput = page.locator("//div[contains(@class,'z-window') and contains(.,'Upload Content')]//input[contains(@class,'z-combobox-input')]").first();
		this.uploadOkBtn = page.locator("//div[contains(@class,'z-window') and contains(.,'Upload Content')]//button[@title='OK']").first();
		this.uploadCloseBtn = page.locator("//div[contains(@class,'z-window') and contains(.,'Upload Content')]//button[contains(@class,'z-window-close')]").first();
		this.docExplorerCloseBtn = page.locator("//div[contains(@class,'z-window') and contains(.,'Document Explorer')]//button[contains(@class,'z-window-close')] | //div[contains(@class,'z-window') and contains(.,'Document Explorer')]//i[contains(@class,'z-icon-times')]").first();
	}

	/**
	 * Opens Document Explorer from the upper toolbar.
	 */
	public void openDocumentExplorer() {
		log.info("Opening Document Explorer from toolbar...");
		// Clear any unexpected modal popup or mask if present before clicking
		Locator mask = page.locator("div.z-modal-mask, div.z-window-modal-mask");
		if (mask.count() > 0 && mask.first().isVisible()) {
			log.warn("Modal mask detected before opening Document Explorer. Dismissing modal...");
			Locator modalBtn = page.locator("//div[contains(@class,'z-messagebox') or contains(@class,'z-window-highlighted') or contains(@class,'z-window-modal')]//button | //div[contains(@class,'z-window-modal') or contains(@class,'z-window-highlighted')]//div[contains(@class,'z-window-close')]").first();
			if (modalBtn.count() > 0 && modalBtn.isVisible()) {
				modalBtn.click(new Locator.ClickOptions().setForce(true));
				page.waitForTimeout(1000);
			}
		}
		docExplorerToolbarBtn.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		docExplorerToolbarBtn.click();
		page.waitForTimeout(1000);
		docExplorerDialog.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(15000));
		WaitUtils.waitForLoadingIndicatorToDisappear(page);
		log.info("Document Explorer dialog opened successfully.");
	}

	/**
	 * Dynamically resolves the DMS file path by delegating to FileUtils.
	 *
	 * @param fileName Name or relative path of the DMS file (e.g. "Training_Completion_Report.pdf")
	 * @return Absolute path of the resolved DMS file
	 */
	public static String getDynamicDmsFilePath(String fileName) {
		return utils.FileUtils.getDynamicDmsFilePath(fileName);
	}

	/**
	 * Uploads a document with the specified DMS Content Type into Document Explorer.
	 *
	 * @param relativeOrAbsolutePath File path or file name to upload (e.g. "Training_Completion_Report.pdf")
	 * @param contentType            DMS Content Type (e.g. "Training Completion Report")
	 */
	public void uploadContent(String relativeOrAbsolutePath, String contentType) {
		log.info("Initiating Content Upload. File: [{}], Content Type: [{}]", relativeOrAbsolutePath, contentType);

		uploadContentBtn.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
		uploadContentBtn.click();
		page.waitForTimeout(1000);

		uploadContentDialog.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));

		// Resolve path dynamically using FileUtils
		String absPath = FileUtils.getDynamicDmsFilePath(relativeOrAbsolutePath);

		// Attach file to <input type="file">
		fileInput.setInputFiles(Paths.get(absPath));
		page.waitForTimeout(1000);
		log.info("Selected file: [{}]", absPath);

		// Fill DMS Content Type
		if (contentType != null && !contentType.isEmpty()) {
			dmsContentTypeInput.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(5000));
			dmsContentTypeInput.fill(contentType);
			dmsContentTypeInput.press("Tab");
			page.waitForTimeout(500);
			log.info("Selected DMS Content Type: [{}]", contentType);
		}

		// Click OK on upload dialog
		uploadOkBtn.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(5000));
		uploadOkBtn.click();
		page.waitForTimeout(2000);
		WaitUtils.waitForLoadingIndicatorToDisappear(page);
		log.info("Content uploaded successfully.");
	}

	/**
	 * Closes the Document Explorer dialog.
	 */
	public void closeDocumentExplorer() {
		log.info("Closing Document Explorer dialog...");
		try {
			if (docExplorerCloseBtn.count() > 0 && docExplorerCloseBtn.first().isVisible()) {
				docExplorerCloseBtn.first().click();
			} else {
				page.keyboard().press("Escape");
			}
			page.waitForTimeout(1000);
			WaitUtils.waitForLoadingIndicatorToDisappear(page);
			log.info("Document Explorer dialog closed.");
		} catch (Exception e) {
			log.warn("Exception closing Document Explorer: {}", e.getMessage());
		}
	}
}
