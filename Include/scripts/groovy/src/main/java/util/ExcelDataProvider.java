package src.main.java.util;
import java.lang.reflect.Method;
        import src.main.java.util.ConfigurationManager;
        import org.testng.annotations.DataProvider;

public class ExcelDataProvider {
    @DataProvider(name="masterDP")
    public static Object[][] getDataSuite1(Method m){
        ExcelReader excel = new ExcelReader(XConstants.SUITE1_XL_PATH);
        String testcase = m.getName();
        return DataUtil.getData(testcase, excel ,ConfigurationManager.getKeyStringVal("env", "mock1"));
    }
}


