package com.hv.test;

import com.codeborne.selenide.testng.GlobalTextReport;
import com.codeborne.selenide.testng.ScreenShooter;
import com.google.common.base.Strings;
import com.hv.pages.Utils.EmailSender;
import com.hv.pages.base.LoginPage;
import org.apache.log4j.Logger;
import org.testng.ITestContext;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Listeners;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Scanner;

/**
 * Created by pshynin on 11/16/2017.
 */
@Listeners({GlobalTextReport.class, ScreenShooter.class})
public class BaseTest {
    private static final Logger LOGGER = Logger.getLogger(LoginPage.class);
    protected LoginPage loginPage;

    public Map<String, String> getTestData() {
        return this.testData;
    }

    protected Map<String, String> testData;

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

    @AfterSuite
    protected synchronized void sendEmailIfTestFailed(final ITestContext testContext) {
//        if (testContext.getFailedTests().size() != 0) {
            System.out.println(testContext.getCurrentXmlTest().getSuite().getName());

            EmailSender.sendEmailTo(testContext, "siarhei.hanush@hitachivantara.com");
//        }
    }




}