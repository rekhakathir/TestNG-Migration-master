package src.test.java.tests;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.testng.annotations.Test;

import src.main.java.Pageclasses.RegistrationPage;
import src.main.java.Pageclasses.SauceLab;
import src.main.java.baseClasses.BaseSeleniumTest;
import src.main.java.logger.Log;
import src.main.java.util.ApplicationProperties;
import src.main.java.util.DataUtil;
import src.main.java.util.ExcelDataProvider;
import src.main.java.util.ExcelReader;
import src.main.java.util.XConstants;
import java.util.Hashtable;

public class LoginTest extends BaseSeleniumTest {
	
   private static String siteAutomationUrl= ApplicationProperties.APPLICATION_BASE_URL.getStringVal();
   //Logger log=Logger.getLogger(SimplifiedFlow.class);

@Test(dataProviderClass= ExcelDataProvider.class,dataProvider="masterDP")
public void  LoginTest1(Hashtable<String,String>data) throws Exception
{
	BasicConfigurator.configure();
    ExcelReader excel=new ExcelReader(XConstants.SUITE1_XL_PATH);
    DataUtil.checkExecution("master", "LoginTest", data.get("Runmode"), excel);
  
    
	
	  SauceLab SL = new SauceLab(this.getDriver());
	  SL.openBrowser(siteAutomationUrl); 
	  SauceLab.execute();
	
	System.out.println("Run");
	  
	  Log.LOGGER.info("Picking up Username and Password and landing on Home page");
	  Log.LOGGER.info("ending the test case");
	    
}

@Test
public void LoginTestDemo()throws Exception{
	System.out.println("Running Successfully");
}

}
    /*RegistrationPage Reg =new RegistrationPage(this.getDriver());
    Reg.openBrowser(siteAutomationUrl);
    Reg.execute(data.get("Fname_registration"),data.get("Lname_registration"),data.get("phno_registration"),data.get("email_registration"),data.get("addr_registration"),data.get("city_registration"),data.get("state_registration"),data.get("pincode_registration"),
    		data.get("countyr_registration"),data.get("username"),data.get("password"),data.get("COnfirm_password"));
    
    
    */






