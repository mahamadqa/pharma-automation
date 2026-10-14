package baseModules;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.Test;

import base.BaseTest;
import utils.ConfigReader;
import utils.DataManager;

public class GRNFlow extends BaseTest {

	private static final Logger log = LogManager.getLogger(GRNFlow.class);

	@Test(groups = { "basic" }, description = "Goods Receipt Note Flow: Login → Role Selection → Search GRN & Click New Record → Fill Header → Add Receipt Line → Save & Verify")
	public void verificationOfGoodsReceiptNoteFlow() {

		// Step 1: Login with credentials
		log.info("========== Starting Goods Receipt Note Flow ==========");
		loginPage.login(ConfigReader.get("Superuser"), ConfigReader.get("Password"));

		// Step 2: Select Client and Role
		clientAndRole.selectClientAndRole(ConfigReader.get("client"), ConfigReader.get("SuperuserRole"));

		// Step 3: In Menubar search 'Goods receipt Note' and click + button to create new record
		goodsReceiptNote.navigateToGRNWindow();

		// Step 4: Fill GRN Header fields using test data profile
		log.info("Filling Goods Receipt Note Header fields using [{}] test data profile...", DataManager.getRegion());
		goodsReceiptNote.selectOrg();
		goodsReceiptNote.attachPO(purchaseOrder);
		goodsReceiptNote.selectDocType();
		goodsReceiptNote.fillDescription();
		goodsReceiptNote.selectBPartner();
		goodsReceiptNote.selectWarehouse();
		toolbar.saveRecord();

		String grnDocNo = goodsReceiptNote.getGRNDocNo();
		log.info("Goods Receipt Note created with Doc No: [{}]", grnDocNo);

		// Validations on Header
		goodsReceiptNote.verifyDocStatus("Drafted");

		// Step 5: Navigate to Receipt Line tab and enter line details
		goodsReceiptNote.clickOnReceiptLineTab();
		goodsReceiptNote.enterProduct();
		goodsReceiptNote.enterQtyEntered();
		toolbar.saveRecord();

		// Step 6: Navigate back to GRN Header
		goodsReceiptNote.navigateBackOnGRNHeader();
		goodsReceiptNote.verifyDocStatus("Drafted");

		log.info("========== Goods Receipt Note Flow Completed Successfully ==========");
	}
}
