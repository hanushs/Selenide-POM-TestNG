package com.hv.test;

import com.codeborne.selenide.testng.ScreenShooter;
import com.codeborne.selenide.testng.TextReport;
import com.google.common.base.Strings;
import com.hv.pages.Utils.DataParser;
import com.hv.pages.base.LoginPage;
import org.apache.log4j.Logger;
import org.testng.ITestContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Listeners;
import org.testng.annotations.Parameters;

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
    protected void init() {

        ResourceBundle rb = ResourceBundle.getBundle("local");
        Enumeration<String> keys = rb.getKeys();

        while (keys.hasMoreElements()) {
            String key = keys.nextElement();
            String value = rb.getString(key);
            System.setProperty(key, (Strings.isNullOrEmpty(System.getProperty(key)) ? value : System.getProperty(key)));
        }
    }

    @BeforeClass
    @Parameters({"dataFilePath"})
    protected void openPentaho(String dataFilePath,final ITestContext testContext) {
        LOGGER.info("Opening " + baseUrl + " in " + browser + " browser");
        loginPage = open(baseUrl, LoginPage.class);
        testData = DataParser.getTestData(dataFilePath,testContext.getName());
    }




}