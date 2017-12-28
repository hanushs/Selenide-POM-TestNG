package com.hv.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import org.apache.log4j.Logger;
import org.openqa.selenium.support.FindBy;

import static com.codeborne.selenide.Selenide.switchTo;

/**
 * Created by shanush on 12/21/2017.
 */
public class FilePage extends BasePage {
    private static final Logger LOGGER = Logger.getLogger(FilePage.class);

    private boolean inReportFrame = false;

    public enum FILETYPE {
        XANALYZER, XDASH, PRPTI
    }

    @FindBy(xpath = "//iframe[contains(@name,'frame_')]")
    protected SelenideElement reportFrame;

    @FindBy(xpath = "//div[@class='pentaho-tabWidgetLabel' and @title='Analysis Report']")
    protected SelenideElement pazReportTab;


    protected void setInReportFrame(boolean inReportFrame) {
        this.inReportFrame = inReportFrame;
    }

    protected boolean isInReportFrame() {
        return inReportFrame;
    }

    //Report options
    @FindBy(id = "cmdOptions")
    protected SelenideElement btnReportOptionsMoreMenu;

    @FindBy(id="cmdMoreActions")
    protected SelenideElement btnMoreActions;

    @FindBy( id = "moreActionsMenu" )
    protected SelenideElement menuMoreActions;

    public void switchToReportFrame() {
        if (isInReportFrame()) {
            LOGGER.info("Already in report frame");
        } else {
            switchTo().frame(reportFrame);
            setInReportFrame(true);
        }
    }


    public void switchToDefault() {
        if (!isInReportFrame()) {
            LOGGER.info("Already in default state");
        } else {
            switchTo().defaultContent();
            setInReportFrame(false);
        }
    }

    public void isPazReportTabPresent() {
        pazReportTab.should(Condition.exist);
    }

}
