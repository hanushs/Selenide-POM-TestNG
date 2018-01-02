package com.hv.pages.base;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.support.FindBy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.switchTo;

/**
 * Created by shanush on 12/21/2017.
 */
public class FilePage extends BasePage {
    private static final Logger LOGGER = Logger.getLogger(FilePage.class);

    private boolean inReportFrame = false;
    private boolean isSaved = false;

    public enum FILETYPE {
        XANALYZER, XDASH, PRPTI
    }

    @FindBy(xpath = "//iframe[contains(@name,'frame_')]")
    protected SelenideElement reportFrame;


    protected SelenideElement pazReportTab(String reportName) {
        return $(By.xpath("//div[@class='pentaho-tabWidgetLabel' and @title='" + reportName + "']"));
    }


    protected void setInReportFrame(boolean inReportFrame) {
        this.inReportFrame = inReportFrame;
    }

    protected boolean isInReportFrame() {
        return inReportFrame;
    }


    public boolean isSaved() {
        return this.isSaved;
    }

    public void setSaved(boolean saved) {
        this.isSaved = saved;
    }

    //Report options
    @FindBy(id = "cmdOptions")
    protected SelenideElement btnReportOptionsMoreMenu;

    @FindBy(id = "cmdMoreActions")
    protected SelenideElement btnMoreActions;

    @FindBy(id = "moreActionsMenu")
    protected SelenideElement menuMoreActions;

    //this is main button to Save reports
    @FindBy(id = "saveButton_img")
    protected SelenideElement btnSave;

    @FindBy(id = "fileNameTextBox")
    private SelenideElement txtFileName;

    @FindBy(id = "navigationListBox")
    private SelenideElement selectFolder;

    //this is Save button on pop-up Save dialog
    @FindBy(id = "okButton")
    protected SelenideElement btnSaveReport;

    //this is Export button in Export dialog windows
    @FindBy(id="dlgBtnDownload")
    protected SelenideElement btnExport;


    protected SelenideElement folderItem(String folder) {
        return $(By.xpath("//div[contains(@id, 'fileChooser') and @title='" + folder + "']"));
    }

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

    public void saveReport(String reportNameTosave, String filePathToSave) {
        if (isSaved()) {
            btnSaveReport.click();
            loading();
        } else {
            saveAsReport(reportNameTosave, filePathToSave);
        }
    }


    public void saveAsReport(String reportNameTosave, String filePathToSave) {
        switchToDefault();
        btnSave.click();
        txtFileName.val(reportNameTosave);
        selectFilePathToSave(filePathToSave);
        btnSaveReport.click();
        //verification if report saved.
        pazReportTab(reportNameTosave).shouldBe(Condition.visible);
        setSaved(true);
    }

    private void selectFilePathToSave(String filePathToSave) {
        //parsing filePath
        List<String> items = new ArrayList<String>(Arrays.asList(filePathToSave.split("/")));
        if (items.size() > 0) {
            if (items.get(0).isEmpty()) {
                items.remove(0);
            }
        }
        //selecting root folder
        selectFolder.selectOptionContainingText("/");

        //navigating to necessary folder
        for (String folder : items) {
            if (folder.equals("home") || folder.equals("Home")) {
                if (folderItem("Home").isDisplayed()) {
                    folder = "Home";
                } else if (folderItem("home").isDisplayed()) {
                    folder = "home";
                } else {
                    throw new RuntimeException("The \"home\" folder is not present!");
                }
            }
            folderItem(folder).doubleClick();
        }
    }



}
