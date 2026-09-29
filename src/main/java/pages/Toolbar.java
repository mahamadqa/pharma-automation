package pages;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.microsoft.playwright.Page;

public class Toolbar {

	private static final Logger log = LogManager.getLogger(Toolbar.class);
	private Page page;

	private String createNewButton = "//a[@title='New    Alt+N']";
	private String saveButton = "//a[@title='Save Changes    Alt+S']";
	private String gridView = "//a[@title='Grid Toggle    Alt+T']";
	private String previewButton = "//a[@title='Preview']";
	private String searchButton = "//a[@title='Lookup Record    Alt+F']";

	public Toolbar(Page page) {
		this.page = page;
	}

	public void createNewRecord() {
		page.click(createNewButton);
		page.waitForTimeout(1000);
	}

	public void saveRecord() {
		page.click(saveButton);
		page.waitForTimeout(1000);
	}

	public void clickOnGrid() {
		page.click(gridView);
		page.waitForTimeout(1000);
	}

	public void clickOnPreviewButton() {
		log.info("clickOnPreviewButton");
		page.click(previewButton);
		page.waitForTimeout(3000);
	}

	public void searchDocNo(String DocNO) {
		log.info("Search Document No");
		page.click(searchButton);
		page.waitForTimeout(1000);
		page.fill("//input[@instancename='DocumentNo']", DocNO);
		page.click("(//button[@title='OK'])[1]");
	}

}
