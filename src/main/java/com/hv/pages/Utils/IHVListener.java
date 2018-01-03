package com.hv.pages.Utils;

import org.apache.log4j.Logger;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

/**
 * Created by shanush on 1/3/2018.
 */
public class IHVListener implements ITestListener {
    private static final Logger LOGGER = Logger.getLogger(IHVListener.class);
    @Override
    public void onTestStart(ITestResult iTestResult) {
        LOGGER.info("TEST " + iTestResult.getMethod().getMethodName() + " STARTED");
    }

    @Override
    public void onTestSuccess(ITestResult iTestResult) {

    }

    @Override
    public void onTestFailure(ITestResult iTestResult) {
        LOGGER.info("TEST " + iTestResult.getMethod().getMethodName() + " FAILED");

    }

    @Override
    public void onTestSkipped(ITestResult iTestResult) {
        LOGGER.info("TEST " + iTestResult.getMethod().getMethodName() + " SKIPPED");
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult iTestResult) {

    }

    @Override
    public void onStart(ITestContext iTestContext) {

    }

    @Override
    public void onFinish(ITestContext iTestContext) {

    }
}
