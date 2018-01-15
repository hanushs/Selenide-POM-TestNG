package com.hv.test;

import com.codeborne.selenide.testng.ScreenShooter;
import com.codeborne.selenide.testng.TextReport;
import com.hv.listeners.HVTestListener;
import com.hv.pages.base.LoginPage;
import com.hv.utils.DataParser;
import com.hv.utils.EmailSender;
import org.apache.log4j.Logger;
import org.testng.ITestContext;
import org.testng.annotations.*;

import java.util.Map;

/**
 * Created by pshynin on 11/16/2017.
 */
@Listeners({TextReport.class, ScreenShooter.class, HVTestListener.class})
public class BaseTest {
    private static final Logger LOGGER = Logger.getLogger(LoginPage.class);
    protected LoginPage loginPage;
    public Map<String, String> getTestData() {
        return this.testData;
    }
    private Map<String, String> testData;

    @BeforeClass
    @Parameters({"dataFilePath"})
    protected void parseData(String dataFilePath, final ITestContext testContext) {
        //parsing data for TestName to be used in test class.
        testData = DataParser.getTestData(dataFilePath, testContext.getName());
    }


    @AfterClass
    protected void sendEmail(ITestContext context){
        System.out.println(context.getCurrentXmlTest().getSuite().getName());

        EmailSender.sendEmailTo(context, "siarhei.hanush@hitachivantara.com");
    }
}