package com.hv.pages;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.support.FindBy;

import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.sleep;

/**
 * Created by shanush on 12/22/2017.
 */
public class BasePage {
    private static final Logger LOGGER = Logger.getLogger(BasePage.class);

    @FindBy(css = ".pentaho-busy-indicator-message")
    public ElementsCollection busyIndicator;


    @FindBy(css = ".pentaho-busy-indicator-spinner")
    public ElementsCollection busyIndicatorSpinner;

    @FindBy(css = ".pageLoadingSpinner")
    public ElementsCollection loadingIndicatorSpinner;

    @FindBy(id = "pageLoadingSpinner")
    public ElementsCollection loadingIndicatorSpinnerId;

    public ElementsCollection getBusyIndicator() {
        return busyIndicator;
    }

    public boolean loading() {
        boolean stillLoading;
        int i = 0;
        int numberOfIterations = 20;

        LOGGER.info("Checking for loading indicator's presence...");
        do {
            stillLoading =
                    (!$$(".pentaho-busy-indicator-message").isEmpty()
                            || !$$(".pentaho-busy-indicator-spinner").isEmpty()
                            || !$$(".pageLoadingSpinner").isEmpty()
                            || !$$(By.id("pageLoadingSpinner")).isEmpty());
            LOGGER.info(busyIndicator.isEmpty());
            LOGGER.info($$(".pentaho-busy-indicator-message").isEmpty());
            if (stillLoading) {
                if (i == numberOfIterations) {
                    throw new RuntimeException(
                            "Busy indicator is still present after long wait. Possibly there is some performance issue!");
                }
                LOGGER.info("Busy indicator is present. Waiting for operation finalization...");
                sleep(2000);
            } else {
                LOGGER.info("No longer waiting for loading indicator to dissapear...");
            }
        } while (stillLoading && ++i <= numberOfIterations);

        return stillLoading;
    }
}
