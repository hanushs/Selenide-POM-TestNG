package com.hv.pages.base;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import com.fasterxml.jackson.databind.deser.Deserializers;
import com.hv.pages.DataSources.ManageDataSourcesPage;
import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.support.FindBy;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.page;
import static com.codeborne.selenide.Selenide.switchTo;

/**
 * Created by shanush on 1/8/2018.
 */
public class HomePage extends BasePage {
    private static final Logger LOGGER = Logger.getLogger(HomePage.class);
    private boolean isInHomeFrame = false;

    @FindBy(xpath = "//button[contains(., 'Manage Data Sources')]")
    protected SelenideElement btnManageDS;

    @FindBy(id = "home.perspective")
    protected SelenideElement homeFrame;


    public void switchToHomeFrame() {
        if (!isInHomeFrame()) {
            switchTo().frame(homeFrame);
            setInHomeFrame(true);
        }
    }

    public boolean isInHomeFrame() {
        return this.isInHomeFrame;
    }

    public void setInHomeFrame(boolean inHomeFrame) {
        this.isInHomeFrame = inHomeFrame;
    }


    public void switchToDefault() {
        if (!isInHomeFrame()) {
            LOGGER.info("Already in default state");
        } else {
            switchTo().defaultContent();
            setInHomeFrame(false);
        }
    }

    public ManageDataSourcesPage openManageDatasource() {
        switchToHomeFrame();
        btnManageDS.click();
        switchToDefault();
        return page(ManageDataSourcesPage.class);
    }

}
