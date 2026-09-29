package baseModules;

import org.testng.annotations.Test;

import base.BaseTest;
import utils.ConfigReader;

public class POFlow extends BaseTest{

	@Test(groups = {"basic", "purchase_order", "regression"})
	public void verificationOFPurchaseOrderFlow() {
		
		loginPage.login(ConfigReader.get("Superuser"), ConfigReader.get("Password"));     
        clientAndRole.selectClientAndRole(ConfigReader.get("client"), ConfigReader.get("SuperuserRole")); 
		purchaseOrder.navigateToPOWindow();
		toolbar.createNewRecord();
		
		// Methods fetch India vs Abroad data dynamically based on -Dregion
		purchaseOrder.selectOrg();
		purchaseOrder.selecTtargetDocType();
		purchaseOrder.selectBPartner();
		purchaseOrder.selectSalesRep();
		toolbar.saveRecord();
		
		purchaseOrder.clickOnPOLineTab();
		purchaseOrder.enterProdct();
		toolbar.saveRecord();
		
		purchaseOrder.navigateBackOnPoHeader();
		dashboard.pauseScript();
	}
	
}
