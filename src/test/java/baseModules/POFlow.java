package baseModules;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.Test;

import base.BaseTest;
import utils.ConfigReader;
import utils.DataManager;

public class POFlow extends BaseTest {

	private static final Logger log = LogManager.getLogger(POFlow.class);

	@Test(groups = { "basic", "purchase_order", "regression" },
		  description = "End-to-end Purchase Order creation flow: Login → Select Client/Role → Create PO Header → Add PO Line → Save")
	public void verificationOFPurchaseOrderFlow() {

		// Step 1: Login with users credentials
		log.info("========== Starting Purchase Order Flow ==========");
		loginPage.login(ConfigReader.get("Superuser"), ConfigReader.get("Password"));

		// Step 2: Select Client and Role
		clientAndRole.selectClientAndRole(ConfigReader.get("client"), ConfigReader.get("SuperuserRole"));

		// Step 3: Navigate to Purchase Order window and create a new record
		purchaseOrder.navigateToPOWindow();
		//toolbar.createNewRecord();

		// Step 4: Fill PO Header fields (Localization vs Non-Localization data based on -Dregion)
		log.info("Filling PO Header fields using [{}] test data profile...", DataManager.getRegion());
		purchaseOrder.selectOrg();
		purchaseOrder.selecTtargetDocType();
		purchaseOrder.fillDescription();
		purchaseOrder.selectBPartner();
		purchaseOrder.selectSalesRep();
		toolbar.saveRecord();
		log.info("Purchase Order created with Doc No : " +purchaseOrder.getPODocNo());
		
		//Validations
		purchaseOrder.verifyDateFields();
		purchaseOrder.verifyDocStatus("Drafted");
		purchaseOrder.verifyBPLocationfields();
		purchaseOrder.verifyCurrency();
		purchaseOrder.verifyAmtFieldsBeforeInvLines();
		
		
		// Step 5: Navigate to PO Line tab and enter product
		purchaseOrder.clickOnPOLineTab();
		purchaseOrder.enterProdct();
		toolbar.saveRecord();

		// Step 6: Navigate back to PO Header & complete doc action
		purchaseOrder.navigateBackOnPoHeader();
		//
		toolbar.performDocAction("Prepare");
		purchaseOrder.verifyDocStatus("In Progress");
		
		toolbar.performDocAction("Complete");
		purchaseOrder.verifyDocStatus("Completed");
		
		log.info("========== Purchase Order Flow Completed Successfully ==========");
		//INR, 
		
	}
}
