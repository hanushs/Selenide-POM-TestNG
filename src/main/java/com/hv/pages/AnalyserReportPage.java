package com.hv.pages;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.logevents.SelenideLogger;
import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.support.FindBy;
import org.testng.Assert;

import java.util.ArrayList;
import java.util.Iterator;

import static com.codeborne.selenide.Selenide.*;
import static java.lang.String.format;

/**
 * Created by pshynin on 11/15/2017.
 */
public class AnalyserReportPage extends FilePage {
    private static final Logger LOGGER = Logger.getLogger(AnalyserReportPage.class);

    //Select DataSource Window
    @FindBy(xpath = "//*[@id='datasources']/*")
    protected ElementsCollection datasources;

    @FindBy(id = "btnNext")
    protected SelenideElement btnOk;

    //PAZ report default layout elements!
    @FindBy(xpath = "//div[@class='dialog-content']")
    protected SelenideElement pazDatasourceWindow;

    @FindBy(id="fieldListWrapperContentPane")
    protected SelenideElement paz_contentPane;

    @FindBy(id="layoutPanelWrapper")
    protected SelenideElement paz_layoutPanel;

    @FindBy(id="reportContainer")
    protected SelenideElement paz_reportContainer;

    @FindBy(id="reportBtns")
    protected SelenideElement paz_reportButtons;
    //__________________________________________________


    public void clickPAZDataSource(String datasourceName) {
        LOGGER.info("Selecting datasource " + datasourceName + " for Analyzer report");
        SelenideLogger.beginStep("Selecting datasource","Hello");
        for (SelenideElement element : $$(By.xpath("//*[@id='datasources']/*"))) {
            String tempDatasource = element.getAttribute("title");
            LOGGER.info("Checking " + tempDatasource + " to be equal " + datasourceName);
            if (tempDatasource.equals(datasourceName)) {
                LOGGER.info(datasourceName + " is found");
                element.scrollTo();
                element.click();
                break;
            }
        }
    }

    public void clickOkPAZDataSource() {
        LOGGER.info("Pressing OK for PAZ datasource");
        btnOk.click();
        pazDatasourceWindow.shouldNotBe(Condition.visible);
    }

    public void isPazContentPaneExist() {
        LOGGER.info("Verifying if PAZ report content pane present");
        paz_contentPane.shouldBe(Condition.visible);
    }

    public void isPazLayoutPanelExist() {
        LOGGER.info("Verifying if PAZ report layout panel exist.");
        paz_layoutPanel.shouldBe(Condition.visible);
    }

    public void isPazReportContainerExist() {
        LOGGER.info("Verifying if PAZ report content container exist.");
        paz_reportContainer.shouldBe(Condition.visible);
    }

    public void isPazReportButtonsExist() {
        LOGGER.info("Verifying if PAZ report buttons panel exist.");
        paz_reportButtons.shouldBe(Condition.visible);
    }


}
