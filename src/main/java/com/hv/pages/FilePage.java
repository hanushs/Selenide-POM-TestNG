package com.hv.pages;

import static com.codeborne.selenide.Selenide.*;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import org.apache.log4j.Logger;
import org.openqa.selenium.support.FindBy;

/**
 * Created by shanush on 12/21/2017.
 */
public class FilePage extends BasePage {
    private static final Logger LOGGER = Logger.getLogger(FilePage.class);
    private boolean inFrame =false;

    public enum FILETYPE {
        XANALYZER, XDASH, PRPTI
    }

    @FindBy(xpath = "//iframe[contains(@name,'frame_')]")
    protected SelenideElement reportFrame;

    @FindBy(xpath = "//div[@class='pentaho-tabWidgetLabel' and @title='Analysis Report']")
    protected SelenideElement pazReportTab;


    protected void setInFrame(boolean inFrame) {
        this.inFrame = inFrame;
    }

    protected boolean isInFrame() {
        return inFrame;
    }

    public void switchToReportFrame() {
        if (isInFrame())
            LOGGER.info("Already in report frame");
        else {
            switchTo().frame(reportFrame);
            setInFrame(true);
        }
    }


    public void switchToDefault(){
        if(!isInFrame()){
            LOGGER.info("Already in default state");
        }else{
            switchTo().defaultContent();
            setInFrame(false);
        }
    }

    public void isPazReportTabPresent(){
        pazReportTab.should(Condition.exist);
    }


}
