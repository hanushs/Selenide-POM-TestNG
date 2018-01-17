package com.hv.listeners;

import com.google.common.base.Strings;
import com.hv.utils.EmailSender;
import org.apache.log4j.Logger;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.util.Enumeration;
import java.util.ResourceBundle;


/**
 * Created by shanush on 1/12/2018.
 */
public class HVTestListener implements ITestListener {
    private static final Logger LOGGER = Logger.getLogger(HVTestListener.class);

    public void onStart(ITestContext testContext) {
        ResourceBundle rb = ResourceBundle.getBundle("local");
        Enumeration<String> keys = rb.getKeys();

        while (keys.hasMoreElements()) {
            String key = keys.nextElement();
            String value = rb.getString(key);
            System.setProperty(key, (Strings.isNullOrEmpty(System.getProperty(key)) ? value : System.getProperty(key)));
        }
    }

    public void onTestStart(ITestResult testResult) {
        LOGGER.info(testResult.getName() + " is STARTED");
    }

    public void onTestSuccess(ITestResult testResult) {
        LOGGER.info(testResult.getName() + " is PASSED");
    }

    public void onTestFailure(ITestResult testResult) {
        LOGGER.info(testResult.getName() + " is FAILED");

    }

    public void onTestSkipped(ITestResult result) {
        // TODO Auto-generated method stub

    }

    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
        // TODO Auto-generated method stub

    }

    public void onFinish(ITestContext context) {
    }
}
