package com.hv.pages.base;

import com.codeborne.selenide.SelenideElement;
import com.hv.pages.DataSources.DataSourceWizardPage;
import com.hv.pages.PAZ.AnalyserReportPage;
import com.hv.pages.PDD.DashboardPage;
import com.hv.pages.PIR.PIRPage;
import org.openqa.selenium.support.FindBy;
import org.apache.log4j.Logger;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.page;

/**
 * Created by pshynin on 11/10/2017.
 */
public class MenuPage extends BasePage {

    private static final Logger LOGGER = Logger.getLogger(MenuPage.class);


    @FindBy(id = "filemenu")
    protected SelenideElement fileMenu;

    //File Menu buttons
    @FindBy(id = "openMenuItem")
    protected SelenideElement fileOpen;

    @FindBy(id = "newmenu")
    protected SelenideElement fileNew;

    @FindBy(id = "manageDatasourceItem")
    protected SelenideElement fileManageDS;

    @FindBy(id = "recentmenu")
    protected SelenideElement fileRecent;

    @FindBy(id = "favoritesmenu")
    protected SelenideElement fileFavourites;

    @FindBy(id = "saveMenuItem")
    protected SelenideElement fileSave;

    @FindBy(id = "saveAsMenuItem")
    protected SelenideElement fileSaveAs;

    @FindBy(id = "logoutMenuItem")
    protected SelenideElement fileLogout;
    //_________________________________

    //File->NEW buttons
    @FindBy(id = "new-analyzer")
    protected SelenideElement newPAZ;

    @FindBy(id = "iadhoc")
    protected SelenideElement newPIR;

    @FindBy(id = "dashboardMenuItem")
    protected SelenideElement newPDD;

    @FindBy(id = "newDatasourceItem")
    protected SelenideElement newDataSource;
    //__________________________________

    @FindBy(id = "filemenu")
    protected SelenideElement viewMenu;

    @FindBy(id = "filemenu")
    protected SelenideElement toolsMenu;

    @FindBy(id = "filemenu")
    protected SelenideElement helpMenu;



    public enum MENU {
        FILE, VIEW, TOOLS, HELP
    }

    public enum MENUFILENEW {
        PAZ, PIR, PDD, DS
    }

    public enum MENUFILE {
        NEW, OPEN, MANAGEDATASOURCES, RECENT, FAVORITES, SAVE, SAVEAS, LOGOUT
    }

    public void clickMenu(MENU menu) {
        LOGGER.info("Clicking on " + menu + " menu");
        switch (menu) {
            case FILE:
                fileMenu.click();
                break;
            case VIEW:
                viewMenu.click();
                break;
            case TOOLS:
                toolsMenu.click();
                break;
            case HELP:
                helpMenu.click();
                break;
        }
    }


    public void clickMenuFile(MENUFILE menuFile) {
        LOGGER.info("Clicking on " + menuFile + " File menu");
        switch (menuFile) {
            case NEW:
                fileNew.click();
                break;
            case OPEN:
                fileOpen.click();
                break;
            case MANAGEDATASOURCES:
                fileManageDS.click();
                break;
            case RECENT:
                fileRecent.click();
                break;
            case FAVORITES:
                fileFavourites.click();
                break;
            case SAVE:
                fileSave.click();
                break;
            case SAVEAS:
                fileSaveAs.click();
                break;
            case LOGOUT:
                fileLogout.click();
                break;
        }
    }

    public void clickMenuFileNew(MENUFILENEW menuFileNew) {
        LOGGER.info("Clicking on " + menuFileNew + " menu");
        switch (menuFileNew) {
            case PAZ:
                newPAZ.click();
                break;
            case PIR:
                newPIR.click();
                break;
            case PDD:
                newPDD.click();
                break;
            case DS:
                newDataSource.click();
                break;
        }
    }

    public Object createNew(FilePage.FILETYPE filetype) {
        clickMenu(MENU.FILE);
        clickMenuFile(MENUFILE.NEW);
        if (filetype.equals(FilePage.FILETYPE.XANALYZER)) {
            clickMenuFileNew(MENUFILENEW.PAZ);
            loading();
            LOGGER.info("Creating AnalyzerService report");
            return page(AnalyserReportPage.class);
        } else if (filetype.equals(FilePage.FILETYPE.PRPTI)) {
            clickMenuFileNew(MENUFILENEW.PIR);
            LOGGER.info("Creating Interactive report");
            return page(PIRPage.class);
        } else if (filetype.equals(FilePage.FILETYPE.XDASH)) {
            clickMenuFileNew(MENUFILENEW.PDD);
            LOGGER.info("Creating Dashboard");
            return page(DashboardPage.class);
        }
        return null;
    }

    public DataSourceWizardPage createNewDatasource() {
        clickMenu(MENU.FILE);
        clickMenuFile(MENUFILE.NEW);
        clickMenuFileNew(MENUFILENEW.DS);
        loading();
        return page(DataSourceWizardPage.class);
    }


}