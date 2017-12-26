package com.hv.utils;

import com.codeborne.selenide.testng.ScreenShooter;
import com.codeborne.selenide.testng.TextReport;
import com.google.common.base.Strings;
import com.hv.pages.LoginPage;
import org.apache.log4j.Logger;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Listeners;
import org.testng.annotations.Parameters;

import java.util.Enumeration;
import java.util.ResourceBundle;

import static com.codeborne.selenide.Configuration.baseUrl;
import static com.codeborne.selenide.Configuration.browser;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.WebDriverRunner.addListener;

/**
 * Created by pshynin on 11/16/2017.
 */
@Listeners({TextReport.class, ScreenShooter.class})
public class BaseTest {
    private static final Logger LOGGER = Logger.getLogger(LoginPage.class);
    protected LoginPage loginPage;

    @BeforeSuite
    protected void init() {
        addListener(new Highlighter());

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

}