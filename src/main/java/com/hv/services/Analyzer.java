package com.hv.services;

import com.hv.pages.AnalyserReportPage;
import com.hv.pages.HomePage;
import org.apache.log4j.Logger;

/**
 * Created by shanush on 12/21/2017.
 */
public class Analyzer {
    private static final Logger LOGGER = Logger.getLogger(Analyzer.class);

    private AnalyserReportPage pazReportPage;

    public Analyzer(AnalyserReportPage pazReportPage) {
        this.pazReportPage = pazReportPage;
    }


    public void selectDataSourcePAZandOpen(String dataSource) {
        pazReportPage.switchToReportFrame();
        pazReportPage.clickPAZDataSource(dataSource);
        pazReportPage.clickOkPAZDataSource();
        pazReportPage.loading();
        pazReportPage.switchToDefault();
    }

    public void isPazDefaultOpened() {
        LOGGER.info("Verifying, that default PAZ report opened.");
        pazReportPage.switchToDefault();
        pazReportPage.isPazReportTabPresent();
        pazReportPage.switchToReportFrame();
        pazReportPage.isPazContentPaneExist();
        pazReportPage.isPazLayoutPanelExist();
        pazReportPage.isPazReportContainerExist();
        pazReportPage.isPazReportButtonsExist();
    }

    public void addFieldsToReport() {
    }
}
