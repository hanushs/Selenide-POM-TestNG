package com.hv.services;

import com.hv.pages.*;
import org.apache.log4j.Logger;

import static com.codeborne.selenide.Selenide.page;
import static com.codeborne.selenide.Selenide.switchTo;
import static com.hv.pages.FilePage.*;

/**
 * Created by shanush on 12/21/2017.
 */
public class Home  {
    private static final Logger LOGGER = Logger.getLogger(HomePage.class);
    protected HomePage homePage;

    public Home(HomePage homePage) {
        this.homePage = homePage;
    }

    public Object createNew(FILETYPE filetype) {
        homePage.clickMenu(HomePage.MENU.FILE);
        homePage.clickMenuFile(HomePage.MENUFILE.NEW);
        if (filetype.equals(FILETYPE.XANALYZER)) {
            homePage.clickMenuFileNew(HomePage.MENUFILENEW.PAZ);
            homePage.loading();
            LOGGER.info("Creating Analyzer report");
            return page(AnalyserReportPage.class);
        } else if (filetype.equals(FILETYPE.PRPTI)) {
            homePage.clickMenuFileNew(HomePage.MENUFILENEW.PIR);
            LOGGER.info("Creating Interactive report");
            return page(InteractiveReportPage.class);
        } else if (filetype.equals(FILETYPE.XDASH)) {
            homePage.clickMenuFileNew(HomePage.MENUFILENEW.PDD);
            LOGGER.info("Creating Dashboard");
            return page(DashboardPage.class);
        }
        return null;
    }

}



