package loginTest;

import org.testng.annotations.Test;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import base.BaseTest;
import utils.ConfigReader;

public class LoginWithEveryRole extends BaseTest{


	//private static final Logger log = LogManager.getLogger(LogsListener.class);
	@DataProvider(name = "RolesData")
	public Object[][] getData() {
	    return new Object[][] {
	        {"Accounting"},
	        {"Admin"},
	        {"QA"},
//	        {"QA Manager"},
//	        {"HR"},
//	        {"QC"},
//	        {"QC Manager"},
//	        {"User"},
//	        {"Engineer"},
//	        {"Sales"}
	    };
	}
	
	@Test(dataProvider = "RolesData" , singleThreaded = true)
    public void verifyLogin(String role) {	
        loginPage.login(ConfigReader.get("Superuser"), ConfigReader.get("Password"));     
        clientAndRole.selectClientAndRole(ConfigReader.get("client"), role);   
        //dashboard.verifyLoginDetails();
        //logOut.clickLogout();
       // page.waitForTimeout(1000);
    }

}
