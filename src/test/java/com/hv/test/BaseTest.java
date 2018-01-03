package com.hv.test;

import com.codeborne.selenide.testng.ScreenShooter;
import com.codeborne.selenide.testng.TextReport;
import com.google.common.base.Strings;
import com.hv.pages.Utils.DataParser;
import com.hv.pages.base.LoginPage;
import org.apache.log4j.Logger;
import org.testng.ITestContext;
import org.testng.annotations.*;

import java.util.Enumeration;
import java.util.Map;
import java.util.ResourceBundle;

import static com.codeborne.selenide.Configuration.baseUrl;
import static com.codeborne.selenide.Configuration.browser;
import static com.codeborne.selenide.Selenide.open;

/**
 * Created by pshynin on 11/16/2017.
 */
@Listeners({TextReport.class, ScreenShooter.class})
public class BaseTest {
    private static final Logger LOGGER = Logger.getLogger(LoginPage.class);
    protected LoginPage loginPage;

    public Map<String, String> getTestData() {
        return this.testData;
    }

    private Map<String,String> testData;

    @BeforeSuite
    @Parameters({"dataFilePath"})
    protected void init(String dataFilePath,final ITestContext testContext) {
        //parsing data for TestName to be used in test class.
        testData = DataParser.getTestData(dataFilePath,testContext.getName());

        ResourceBundle rb = ResourceBundle.getBundle("local");
        Enumeration<String> keys = rb.getKeys();

        while (keys.hasMoreElements()) {
            String key = keys.nextElement();
            String value = rb.getString(key);
            System.setProperty(key, (Strings.isNullOrEmpty(System.getProperty(key)) ? value : System.getProperty(key)));
        }
    }

    @BeforeClass
    protected void openPentaho() {
        LOGGER.info("Opening " + baseUrl + " in " + browser + " browser");
        loginPage = open(baseUrl, LoginPage.class);

    }


    @AfterSuite
    protected void sendEmailIfTestFailed(final ITestContext testContext){
        if(testContext.getFailedTests().size() != 0){
            LOGGER.info("Sending email");
        }
    }


}